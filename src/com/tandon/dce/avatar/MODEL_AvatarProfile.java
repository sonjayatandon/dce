/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.avatar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.tandon.dce.cards.MODEL_Card;
import com.tandon.dce.cards.SERVICE_CardManager;
import com.tandon.dce.characterclass.MODEL_Template;
import com.tandon.dce.characterclass.MODEL_Class;
import com.tandon.dce.characterclass.SERVICE_StyleManager;
import com.tandon.dce.combat.MODEL_CombatCharacter;
import com.tandon.dce.combat.MODEL_CombatDeck;
import com.tandon.dce.common.db.IResultSetVisitor;
import com.tandon.dce.common.db.SERVICE_ConnectionPool;

public class MODEL_AvatarProfile {
	final XCFFacade facade;
	final SERVICE_StyleManager styleManager;
	final SERVICE_CardManager cardManager;
	MODEL_Avatar avatar;
	MODEL_Class style;
	Integer postureID;
	Integer avatarProfileID;
	String profileName;

	HashMap<String, Integer> deckCosts = new HashMap<String, Integer>();

	HashMap<Integer, Integer> cardQuantities = new HashMap<Integer, Integer>();
	HashMap<String, ArrayList<MODEL_Card>> customDecks = new HashMap<String, ArrayList<MODEL_Card>>();


	public MODEL_AvatarProfile(XCFFacade facade) {
		this.facade = facade;
		styleManager = (SERVICE_StyleManager)facade.getService(SERVICE_StyleManager.XCF_TAG);
		cardManager = (SERVICE_CardManager)facade.getService(SERVICE_CardManager.XCF_TAG);
	}

	public MODEL_AvatarProfile(XCFFacade facade, MODEL_Avatar avatar, ResultSet rs) throws SQLException, XCFException {
		this.facade = facade;
		this.avatar = avatar;
		styleManager = (SERVICE_StyleManager)facade.getService(SERVICE_StyleManager.XCF_TAG);
		cardManager = (SERVICE_CardManager)facade.getService(SERVICE_CardManager.XCF_TAG);
		avatarProfileID = rs.getInt(1);
		profileName = rs.getString(2);
		int styleID = rs.getInt(3);
		style = styleManager.getStyle(styleID);
		postureID = rs.getInt(4);
		MODEL_Template posture = style.getTemplate(postureID);

		// store the initial deck costs
		for (String deckName: posture.getDeckNames()) {
			addDeckCost(deckName, posture.getDeckCost(deckName));
		}
	}

	public Integer getID() {
		return avatarProfileID;
	}

	public String getName() {
		return profileName;
	}

	public MODEL_Avatar getAvatar() {
		return avatar;
	}

	public MODEL_Template getPosture() {
		return style.getTemplate(postureID);
	}

	public MODEL_Class getStyle() {
		return style;
	}

	public Integer getPostureID() {
		return postureID;
	}

	public Integer getDeckCost(String deckName) {
		Integer cost = deckCosts.get(deckName);
		if (cost == null) return 0;
		return cost;
	}

	private void addDeckCost(String deckName, Integer cost) {
		Integer deckCost = deckCosts.get(deckName);
		if (deckCost == null) {
			deckCosts.put(deckName, cost);
		} else {
			deckCosts.put(deckName, deckCost + cost);
		}
	}

	private ArrayList<MODEL_Card> getDeck(String deckName) {
		ArrayList<MODEL_Card> deck = customDecks.get(deckName);
		if (deck == null) {
			deck = new ArrayList<MODEL_Card>();
			customDecks.put(deckName, deck);
		}
		return deck;
	}

	static final String TABLE = "AvatarProfiles";
	static final String INSERT_COLUMNS = "avatarID, name, styleID, postureID";
	static final String INSERT_VALUES="?,?,?,?";
	@SuppressWarnings("unchecked")
	public int newInstance(MODEL_Avatar avatar, String profileName, MODEL_Class style, Integer postureID) throws XCFException {
		ArrayList params = new ArrayList();
		params.add(avatar.getID());
		params.add(profileName);
		params.add(style.getID());
		params.add(postureID);

		this.avatar = avatar;
		this.style = style;
		this.postureID = postureID;
		avatarProfileID = SERVICE_ConnectionPool.getDBM(facade).insertAutoIncrement(TABLE, INSERT_COLUMNS, INSERT_VALUES, params);
		if (avatarProfileID == -1) throw new XCFException("Unable to add profile " + profileName + " to the database for avatar " + avatar.getName() );

		return avatarProfileID;
	}

	static final String SELECT_PROFILE_CARDS = "select deckName, cardID, qty from AvatarProfileCards where profileID=?";
	@SuppressWarnings("unchecked")
	public void loadCards() throws XCFException {
		ArrayList params = new ArrayList();
		params.add(avatarProfileID);
		SERVICE_ConnectionPool.getDBM(facade).select(SELECT_PROFILE_CARDS, params, new CardVisitor());
	}

	class CardVisitor implements IResultSetVisitor {
		public void load(ResultSet rs) throws SQLException, XCFException {
			String deckName = rs.getString(1);
			int cardID = rs.getInt(2);
			MODEL_Card card = cardManager.getCard(cardID);
			int quantity = rs.getInt(3);
			addCard(card, deckName, quantity);
		}
	}

	static final String UPDATE_POSTURE_FIELDS = "postureID=?";
	static final String UPDATE_WHERE = "profileID=?";
	@SuppressWarnings("unchecked")
	public void setPostureID(Integer postureID) {
		ArrayList params = new ArrayList();
		this.postureID = postureID;
		if (avatarProfileID == null) return;

		params.add(postureID);
		params.add(avatarProfileID);

		SERVICE_ConnectionPool.getDBM(facade).update(TABLE, UPDATE_POSTURE_FIELDS, UPDATE_WHERE, params);
	}

	static final String UPDATE_SYTLE_FIELDS = "styleID=?";
	@SuppressWarnings("unchecked")
	public void setStyle(MODEL_Class style) {
		ArrayList params = new ArrayList();
		this.style = style;
		if (avatarProfileID == null) return;

		params.add(style.getID());
		params.add(avatarProfileID);

		SERVICE_ConnectionPool.getDBM(facade).update(TABLE, UPDATE_SYTLE_FIELDS, UPDATE_WHERE, params);
	}

	static final String DELETE_CARDS = "delete from AvatarProfileCards where profileID=?";
	@SuppressWarnings("unchecked")
	public void clear(Integer defaultPosture) {
		setPostureID(defaultPosture);
		ArrayList params = new ArrayList();
		params.add(avatarProfileID);;

		SERVICE_ConnectionPool.getDBM(facade).update(DELETE_CARDS, params);
		cardQuantities = new HashMap<Integer, Integer>();
		customDecks = new HashMap<String, ArrayList<MODEL_Card>>();
	}

	public void addCard(MODEL_Card card, String deckName, Integer quantity) {
		ArrayList<MODEL_Card> deck = getDeck(deckName);
		Integer cost = card.getCost() * quantity;
		addDeckCost(deckName, cost);
		deck.add(card);
		cardQuantities.put(card.getID(), quantity);
	}

	static final String CARD_TABLE = "AvatarProfileCards";
	static final String INSERT_CARD_COLUMNS = "avatarID, profileID, deckName, cardID, qty";
	static final String INSERT_CARD_VALUES="?,?,?,?,?";
	@SuppressWarnings("unchecked")
	public void saveCard(MODEL_Card card, String deckName, Integer quantity) {
		if (quantity == 0) return;
		addCard(card, deckName, quantity);
		ArrayList params = new ArrayList();
		params.add(avatar.getID());
		params.add(avatarProfileID);
		params.add(deckName);
		params.add(card.getID());
		params.add(quantity);
		SERVICE_ConnectionPool.getDBM(facade).insert(CARD_TABLE, INSERT_CARD_COLUMNS, INSERT_CARD_VALUES, params);
	}

	static final String COPY_CARDS = "insert into AvatarProfileCards (avatarID, profileID, deckName, cardID, qty, created) (select ?, ?, deckName, cardID, qty, created from AvatarProfileCards where profileID=?)";

	@SuppressWarnings("unchecked")
	public void copy(MODEL_AvatarProfile srcProfile) {
		setStyle(srcProfile.getStyle());
		setPostureID(srcProfile.getPostureID());
		ArrayList params = new ArrayList();
		params.add(avatar.getID());
		params.add(avatarProfileID);
		params.add(srcProfile.getID());
		SERVICE_ConnectionPool.getDBM(facade).update(COPY_CARDS, params);

		// TODO load the cards just copied?
	}

	public Integer getQuantityOf(MODEL_Card card) {
		Integer quantity = cardQuantities.get(card.getID());

		if (quantity == null) return 0;

		return quantity;
	}

	////////////////////////////////////////
	// combat support methods

	public MODEL_CombatCharacter getCombatCharacter() throws XCFException {
		MODEL_CombatCharacter combatCharacter = new MODEL_CombatCharacter(facade, avatar.getID(), avatar.getName());
		// copy over the attributes

		for (String attribute: style.getAttributeIDS()) {
			combatCharacter.setAttribute(attribute, avatar.getCurrAttributeValue(attribute));
		}

		fillCombatCharacter(combatCharacter);

		return combatCharacter;
	}

	public void fillCombatCharacter(MODEL_CombatCharacter combatCharacter) throws XCFException {
		// now copy over all the cards
		// first add in the postures decks
		MODEL_Template posture = getPosture();
		for (String deckName: posture.getDeckNames()) {
			MODEL_CombatDeck deck = posture.getDeck(deckName);
			addCards(combatCharacter, deck);
		}

		// now add the other cards in the profile
		for (String deckName: customDecks.keySet()) {
			ArrayList<MODEL_Card> cards = customDecks.get(deckName);
			addCards(combatCharacter, deckName, cards);
		}
	}

	private int addCards(MODEL_CombatCharacter character, MODEL_CombatDeck deck) throws XCFException {
		int deckCost = 0;
		for (MODEL_Card card: deck.getCards()) {
			character.addCard(deck.getName(), card.getID());
			deckCost += card.getCost();
		}
		return deckCost;
	}

	private int addCards(MODEL_CombatCharacter character, String deckName, ArrayList<MODEL_Card> cards) throws XCFException {
		int deckCost = 0;
		for (MODEL_Card card: cards) {
			Integer quantity = cardQuantities.get(card.getID());
			for (int i=0; i<quantity; i++) {
				character.addCard(deckName, card.getID());
				deckCost += (card.getCost() * quantity);
			}

			deckCost += card.getCost();
		}
		return deckCost;
	}
}
