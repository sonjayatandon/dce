/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.combat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.eternal.xcf.core.XCFContext;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFLogger;
import com.eternal.xcf.core.XCFRequest;
import com.tandon.dce.cards.MODEL_Card;
import com.tandon.dce.cards.SERVICE_CardManager;

public class MODEL_CombatCharacter  {
	public static final String XCF_TAG = "character";
	XCFFacade facade;
	SERVICE_CardManager cardMgr;
	XCFLogger logger;
	String name;
	XCFContext context = null;
	IPlayer player;

	int damageTaken = 0;


	boolean ko = false;
	int hp;

	Integer avatarID;


	HashMap<String, Integer> attributes = new HashMap<String, Integer>();
	HashMap<String, MODEL_CombatDeck> decks = new HashMap<String, MODEL_CombatDeck>();
	HashMap<String, Integer> deckLimits = new HashMap<String, Integer>();
	HashMap<String, Integer> deckCosts = new HashMap<String, Integer>();
	ArrayList<DeckEntry> deckBuilder = new ArrayList<DeckEntry>();
	HashMap<String, MODEL_Card> defaultDraw = new HashMap<String, MODEL_Card>();

	public MODEL_CombatCharacter(XCFFacade facade, Integer id, String name) throws XCFException {
		this.facade = facade;
		this.avatarID = id;
		logger = facade.getLogManager().getLogger("combat");
		cardMgr = (SERVICE_CardManager)facade.getService(SERVICE_CardManager.XCF_TAG);
		this.name = name;
	}

	public MODEL_CombatCharacter copy() throws XCFException {
		MODEL_CombatCharacter c = new MODEL_CombatCharacter(facade, avatarID, name);
		c.damageTaken = damageTaken;
		c.ko = ko;
		c.hp = hp;
		c.attributes = attributes;

		return c;
	}

	public String getName() {
		return name;
	}

	public void setContext(XCFContext context) {
		this.context = context;
	}

	public void setDeck(String deckName, MODEL_CombatDeck deck) {
		decks.put(deckName, deck);
	}

	public void setAttribute(String name, Integer value) {
		attributes.put(name, value);
	}

	public void setHP(int hp) {
		this.hp = hp;
	}

	public boolean isKnockedOut() {
		return ko;
	}

	public HashMap<String, MODEL_CombatDeck> getDecks() {return decks;}

	public Integer getAttribute(String name) throws XCFException {
		Integer value = attributes.get(name);
		if (value == null) throw new XCFException(getName() + " does not have the " + name + " attribute.");
		return value;
	}

	public Integer getAttribute(String name, int defaultValue) throws XCFException {
		Integer value = attributes.get(name);
		if (value == null) return defaultValue;
		return value;
	}

	public Set<String> getAttributeNames() {
		return attributes.keySet();
	}

	public void setDeckLimit(String deckName, Integer limit) {
		deckLimits.put(deckName, limit);
	}

	public Integer getDeckLimit(String deckName) {
		Integer limit = deckLimits.get(deckName);
		if (limit == null) return Integer.MAX_VALUE;
		return limit;
	}

	public Integer getDeckStrength(String deckName) {
		MODEL_CombatDeck deck = decks.get(deckName);
		if (deck == null) return 0;

		return deck.getCost();
	}

	public void shuffle() {
		for (MODEL_CombatDeck deck: decks.values()) {
			deck.shuffle();
		}
	}

	public void reset(boolean debug) throws XCFException {
		decks = new HashMap<String, MODEL_CombatDeck>();
		ArrayList<DeckEntry> entries = new ArrayList<DeckEntry>();
		for (DeckEntry entry: deckBuilder) {
			entries.add(entry);
		}
		deckBuilder =  new ArrayList<DeckEntry>();
		for (DeckEntry entry: entries) {
			addCard(entry.deckName, entry.cardId, false);
		}

		if (!debug) shuffle();
		damageTaken = 0;
		setAttribute("fatigue", 0);
	}


	public MODEL_Card getCard(Integer cardId) throws XCFException {
		MODEL_Card card = cardMgr.getCard(cardId);
		if (card == null) throw new XCFException (cardId + " is not a valid card id.");
		return card;
	}

	public void addCard(String deckName, Integer cardId) throws XCFException {
		addCard(deckName, cardId, true);
	}

	void addCard(String deckName, Integer cardId, boolean log) throws XCFException {
		deckBuilder.add(new DeckEntry(deckName, cardId));

		int limit = getDeckLimit(deckName);
		MODEL_CombatDeck deck = decks.get(deckName);
		if (deck == null) {
			deck = new MODEL_CombatDeck(facade, deckName);
			setDeck(deckName, deck);
		}

		MODEL_Card card = deck.add(cardId, limit);
		if (log) logger.logMessage(context, XCFLogger.LogTypes.INFO, "Adding " + card.getName().toUpperCase() + " to " + deckName.toUpperCase());
	}


	public void addCard(String deckName, MODEL_Card card) throws XCFException {
		MODEL_CombatDeck deck = decks.get(deckName);
		if (deck == null) {
			deck = new MODEL_CombatDeck(facade, deckName);
			setDeck(deckName, deck);
		}

		deck.add(card);
	}

	public void pushCard(String deckName, MODEL_Card card) throws XCFException {
		MODEL_CombatDeck deck = decks.get(deckName);
		if (deck == null) {
			deck = new MODEL_CombatDeck(facade, deckName);
			setDeck(deckName, deck);
		}

		deck.push(card);
	}

	public void setOverride(String deckName, int cardId, int amount) throws XCFException {
		MODEL_CombatDeck deck = decks.get(deckName);
		if (deck != null) {
			MODEL_Card card = deck.setOverride(cardId, amount);
			ICombatLog mlog = (ICombatLog)context.getValue("match");
			ICombatFormulas f = (ICombatFormulas)context.getValue(ICombatFormulas.XCF_TAG);
			logger.logMessage(context, XCFLogger.LogTypes.INFO, "Overriding " + deckName.toUpperCase() + " with "+ card.getName().toUpperCase());
			mlog.logEvent(avatarID, 2, 4, new int[] {f.getDeckID(deckName), card.getID()});
		} else {
			throw new XCFException(deckName + " does not exist.");
		}
	}

	public MODEL_Card play(XCFRequest request, String deckName, String matchKeywords, String excludeKeywords) throws XCFException {
		ICombatLog mlog = UTIL_Helper.getMatchLog(request);
		ICombatFormulas f = UTIL_Helper.getFormulas(request);

		if (decrementDisabled(deckName)) {
			mlog.logEvent(avatarID, 0, 1, new int[] {avatarID, f.getDeckID(deckName)});
			logger.logMessage(context, XCFLogger.LogTypes.INFO, getName() + " " + deckName.toUpperCase() + " is DISABLED");
			return null;
		}
		MODEL_Card card = null;
		MODEL_CombatDeck deck = decks.get(deckName);
		if (deck != null) {
			card = deck.drawOverride();
		}

		if (card == null && deck != null) card = deck.drawCard(matchKeywords, excludeKeywords);

		if (card == null) {
			card = defaultDraw.get(deckName);
		}
		if (card == null && !deckName.equals("damage")) {
			mlog.logEvent(avatarID, 0, 1, new int[] {avatarID, f.getDeckID(deckName)});
			logger.logMessage(context, XCFLogger.LogTypes.INFO, getName() + " exhausted " + deckName.toUpperCase());
			return null;
		}

		MODEL_Card oldCard = UTIL_Helper.setActiveCard(request, card);
		MODEL_CombatCharacter oldActive = UTIL_Helper.setActivePlayer(request, this);
		logger.logMessage(context, XCFLogger.LogTypes.INFO, getName() + " Playing " + card.getName().toUpperCase() + " from " + deckName.toUpperCase());
		mlog.logEvent(avatarID, 1, 2, new int[]{avatarID, card.getID(), f.getDeckID(deckName)});
		mlog.logPlayCard(request, this, deckName, card);
		card.execute(request);
		discard(deckName, card);
		UTIL_Helper.setActiveCard(request, oldCard);
		UTIL_Helper.setActivePlayer(request, oldActive);

		return card;
	}

	public MODEL_Card draw(XCFRequest request, String deckName, String matchKeywords, String excludeKeywords) throws XCFException {
		MODEL_CombatDeck deck = decks.get(deckName);
		if (deck == null) return null;

		MODEL_Card card = deck.drawCard(matchKeywords, excludeKeywords);
		if (card == null) {
			card = defaultDraw.get(deckName);
		}
		
		if (card == null) {
			ICombatLog mlog = UTIL_Helper.getMatchLog(request);
			ICombatFormulas f = UTIL_Helper.getFormulas(request);
			logger.logMessage(context, XCFLogger.LogTypes.INFO, getName() + " isn't able to find a matching card in " + deckName.toUpperCase());
			mlog.logEvent(avatarID, 0, 3, new int[]{avatarID, f.getDeckID(deckName)});
			return null;
		}

		return card;
	}

	public MODEL_Card drawFromBottom(XCFRequest request, String deckName, String matchKeywords, String excludeKeywords) throws XCFException {
		MODEL_CombatDeck deck = decks.get(deckName);
		if (deck == null) return null;

		MODEL_Card card = deck.drawCardFromBottom(matchKeywords, excludeKeywords);
		if (card == null) {
			ICombatLog mlog = UTIL_Helper.getMatchLog(request);
			ICombatFormulas f = UTIL_Helper.getFormulas(request);
			logger.logMessage(context, XCFLogger.LogTypes.INFO, getName() + " isn't able to find a matching card in " + deckName.toUpperCase());
			mlog.logEvent(avatarID, 0, 3, new int[]{avatarID, f.getDeckID(deckName)});
			return null;
		}

		return card;
	}

	public void discard(String deckName, MODEL_Card card) throws XCFException {
		String discardDeck = deckName+"-discard";
		addCard(discardDeck, card);
	}

	public MODEL_Card recoverMax(String deckName, String propertyName, String keyword, String exclude) throws XCFException {
		String discardDeck = deckName+"-discard";
		MODEL_CombatDeck deck = decks.get(discardDeck);
		if (deck == null) return null;

		return deck.drawMaxCard(propertyName, keyword, exclude);
	}

	public MODEL_Card recover(String deckName, String keyword, String exclude) throws XCFException {
		String discardDeck = deckName+"-discard";
		MODEL_CombatDeck deck = decks.get(discardDeck);
		if (deck == null) return null;

		return deck.drawCard(keyword, exclude);
	}

	public int takeDamage(XCFRequest request, int amount, int fatigueDamage) throws XCFException {
		ICombatFormulas formulas = UTIL_Helper.getFormulas(request);
		int fatigueDraw =  formulas.resovleFatigue(this, fatigueDamage);

		MODEL_Card card = null;
		MODEL_CombatDeck damage = decks.get("damage");
		if (damage != null) {
			card = damage.drawCard();
			if (card != null) {
				setAttribute("damage-taken", amount);
				logger.logMessage(context, XCFLogger.LogTypes.INFO, getName() + " Playing " + card.getName() + " from DAMAGE.");
				MODEL_CombatCharacter oldActive = UTIL_Helper.setActivePlayer(request, this);
				card.execute(request);
				UTIL_Helper.setActivePlayer(request, oldActive);
				amount = getAttribute("damage-taken");
			}
		}
		
		for (int i=0; i < amount; i++) {
			takeDamage(request);
		}

		for (int i=0; i < fatigueDraw; i++) {
			takeFatigue(request);
		}
		
		return amount;
	}

	public void takeDamage(XCFRequest request) throws XCFException {
		MODEL_CombatDeck wound = decks.get("wound");
		MODEL_Card card = null;

		if (card == null && wound != null) {
			card = wound.drawCard();
			if (card != null) logger.logMessage(context, XCFLogger.LogTypes.INFO, getName() + " Playing " + card.getName() + " from WOUND.");
		}

		if (card != null) {
			MODEL_Card oldCard = UTIL_Helper.setActiveCard(request, card);
			MODEL_CombatCharacter oldActive = UTIL_Helper.setActivePlayer(request, this);
			card.execute(request);
			UTIL_Helper.setActiveCard(request, oldCard);
			UTIL_Helper.setActivePlayer(request, oldActive);
		} else {
			// logger.logMessage(context, XCFLogger.LogTypes.INFO, getName() + " exhausted wound.");
			damageTaken ++;
		}

		if ((hp - damageTaken) <= 0) {
			ko = true;
		}
	}

	public void takeFatigue(XCFRequest request) throws XCFException {
		MODEL_CombatDeck deck = decks.get("fatigue");
		MODEL_Card card = null;
		if (deck != null) {
			card = deck.drawCard();
		}
		ICombatLog mlog = UTIL_Helper.getMatchLog(request);
		ICombatFormulas f = UTIL_Helper.getFormulas(request);
		if (card != null) {
			MODEL_Card oldCard = UTIL_Helper.setActiveCard(request, card);
			MODEL_CombatCharacter oldActive = UTIL_Helper.setActivePlayer(request, this);
			logger.logMessage(context, XCFLogger.LogTypes.INFO, getName() + " Playing " + card.getName().toUpperCase() + " from FATIGUE");
			mlog.logEvent(avatarID, 1, 2, new int[] {avatarID, card.getID(), f.getDeckID("fatigue")});
			card.execute(request);
			mlog.logPlayCard(request, this, "fatigue", card);
			UTIL_Helper.setActiveCard(request, oldCard);
			UTIL_Helper.setActivePlayer(request, oldActive);
		} else {
			// logger.logMessage(context, XCFLogger.LogTypes.INFO, getName() + " exhausted wound.");
			logger.logMessage(context, XCFLogger.LogTypes.INFO, getName() + " takes 1 damage from fatigue.");
			mlog.logEvent(avatarID, 3, 5, new int[avatarID]);
			damageTaken ++;
		}

		if ((hp - damageTaken) <= 0) {
			ko = true;
		}
	}

	public int getDeckSize(String deckName, String matchKeywords, String excludeKeywords) {
		MODEL_CombatDeck deck = decks.get(deckName);
		if (deck == null) return 0;
		return deck.size(matchKeywords, excludeKeywords);
	}

	public int getDamageTaken() {
		return damageTaken;
	}

	public int heal(int hp) {
		int amount = Math.min(hp, damageTaken);

		damageTaken -= amount;
		return amount;
	}

	class DeckEntry {
		String deckName;
		int cardId;

		DeckEntry(String deckName, int cardId) {
			this.deckName = deckName;
			this.cardId = cardId;
		}
	}

	static final String STAR = "*";
	HashMap<String, Integer> disabledState = new HashMap<String, Integer>();
	public boolean decrementDisabled(String deckName) {
		if (decrementWildcardDisabled()) return true;

		Integer drawCount = disabledState.get(deckName);
		if (drawCount == null) return false;
		if (drawCount == 1) {
			disabledState.remove(deckName);
			return true;
		}
		disabledState.put(deckName, drawCount-1);
		return true;
	}

	public boolean decrementWildcardDisabled() {
		Integer drawCount = disabledState.get(STAR);
		if (drawCount == null) return false;
		if (drawCount == 1) {
			disabledState.remove(STAR);
			return true;
		}
		disabledState.put(STAR, drawCount-1);
		return true;
	}

	public void disable(String deckName, int numDraws) {
		disabledState.put(deckName, numDraws);
	}

	public Integer getDisabledState(String deckName) {
		return disabledState.get(deckName);
	}

	public void startEncounter(boolean debug) throws XCFException {
		ko = false;
		reset(debug);

	}

	public Integer getID() {
		return avatarID;
	}

	public void setDamageTaken(Integer property) {
		damageTaken = property;
	}

	public void setKO(boolean ko) {
		this.ko = ko;
	}

	public int getHP() {return hp;}

	public void setDefaultDraw(String deck, int cardId)throws XCFException {
		defaultDraw.put(deck, getCard(cardId));
	}

	public void setPlayer(IPlayer player) {
		this.player = player;
	}
	
	public IPlayer getPlayer() { return player;}

	public MODEL_CombatCharacter getAlly(int allyIndex) {
		return player.getAlly(this, allyIndex);
	}

	public MODEL_CombatCharacter getBest(String attributeName) throws XCFException {
		return player.getBest(this, attributeName);
	}

	////////////////////////////////////////////////////
	// GET/SET STATE METHODS
	//  state returned as
	//   characterID
	//   [deckname, [cardid,]*]*
	//   [attribute, value]* (including damageTaken, ko, hp)
	//   [disabled deckname, value]*
	//
	//  state is set with
	//   characterID
	//   [deckID, [cardid,]*]*
	//   [attributeID, value]* (including damageTaken, ko, hp)
	//   [disabled deckID, value]*

}
