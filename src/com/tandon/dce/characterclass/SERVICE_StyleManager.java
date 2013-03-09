/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.characterclass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFService;
import com.tandon.dce.cards.MODEL_Card;
import com.tandon.dce.cards.SERVICE_CardManager;

public class SERVICE_StyleManager implements XCFService {
	XCFFacade facade;
	ArrayList<MODEL_Class> sortedStyles = new ArrayList<MODEL_Class>();
	HashMap<Integer, MODEL_Class> styles = new HashMap<Integer, MODEL_Class>();
	HashMap<String, ArrayList<MODEL_Card>> cardsByStyle = new HashMap<String, ArrayList<MODEL_Card>>();

	public static String XCF_TAG = "style-manager";

	public String getName() {
		return null;
	}

	public void setFacade(XCFFacade facade) {
		this.facade = facade;
	}

	public void setName(String name) {
	}

	public void start() throws XCFException {
	}

	public void stop() throws XCFException {
	}

	public void addStyle(Integer id, MODEL_Class style) {
		sortedStyles.add(style);
		styles.put(id, style);
	}

	/**
	 * Returns the template that contains the defensive and offensive deckIds.
	 * @param styleID
	 * @return
	 * @throws XCFException
	 */
	public MODEL_Class getStyle(Integer styleID) throws XCFException {
		MODEL_Class style = styles.get(styleID);
		if (style == null) throw new XCFException (styleID + " is not a registered style.");
		return style;
	}

	public ArrayList<MODEL_Class> getStyles() {
		return sortedStyles;
	}

	public void catagorize() {
		SERVICE_CardManager cardManager = (SERVICE_CardManager)facade.getService(SERVICE_CardManager.XCF_TAG);
		for (MODEL_Card card: cardManager.getCards()) {
			String styles = card.getProperty("styles");
			if (styles != null) {
				StringTokenizer tokenizer = new StringTokenizer(styles, ",");
				while (tokenizer.hasMoreTokens()) {
					String styleName = tokenizer.nextToken().trim();
					storeIn(styleName, card);
				}
			}
		}
	}

	private void storeIn(String style, MODEL_Card card) {
		ArrayList<MODEL_Card> cards = cardsByStyle.get(style);
		if (cards == null) {
			cards = new ArrayList<MODEL_Card>();
			cardsByStyle.put(style, cards);
		}
		cards.add(card);
	}

	public ArrayList<MODEL_Card> getStyleCards(MODEL_Class style) {
		ArrayList<MODEL_Card> cards = new ArrayList<MODEL_Card>();

		ArrayList<MODEL_Card> cardsForAllStyles = cardsByStyle.get("*");
		ArrayList<MODEL_Card> cardsForStyle = cardsByStyle.get(style.getName());

		if (cardsForAllStyles != null) cards.addAll(cardsForAllStyles);
		if (cardsForStyle != null) cards.addAll(cardsForStyle);

		return cards;
	}

	public boolean styleHasCard(MODEL_Class style, MODEL_Card card) {
		ArrayList<MODEL_Card> cardsForAllStyles = cardsByStyle.get("*");
		ArrayList<MODEL_Card> cardsForStyle = cardsByStyle.get(style.getName());

		if (cardsForAllStyles.contains(card)) return true;
		if (cardsForStyle.contains(card)) return true;

		return false;
	}

}
