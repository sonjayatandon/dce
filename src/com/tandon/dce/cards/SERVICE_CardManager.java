/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.cards;

import java.util.ArrayList;
import java.util.HashMap;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFService;

public class SERVICE_CardManager implements XCFService {
	XCFFacade facade;
	HashMap<Integer, MODEL_Card> cards = new HashMap<Integer, MODEL_Card>();
	ArrayList<MODEL_Card> cardList = new ArrayList<MODEL_Card>();

	public static String XCF_TAG = "card-manager";

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

	public void addCard(Integer id, MODEL_Card card) {
		MODEL_Card curCard = cards.get(id);
		if (curCard != null) {
			facade.logError(card.getName() + " IS ALREADY REGISTERED.  IGNORING THIS DEFINITION. <================================ ");
			return;
		}
		cards.put(id, card);
		cardList.add(card);
	}

	public void stop() throws XCFException {
		// TODO Auto-generated method stub

	}

	public MODEL_Card getCard(Integer cardId) {
		return cards.get(cardId);
	}

	public ArrayList<MODEL_Card> getCards() {
		return cardList;
	}

}
