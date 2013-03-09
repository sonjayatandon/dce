/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.characterclass;

import java.util.HashMap;
import java.util.Set;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.tandon.dce.cards.MODEL_Card;
import com.tandon.dce.cards.SERVICE_CardManager;
import com.tandon.dce.combat.MODEL_CombatDeck;

public class MODEL_Template {
	final XCFFacade facade;
	final String name;
	final SERVICE_CardManager cardMgr;
	HashMap<String, MODEL_CombatDeck> decks = new HashMap<String, MODEL_CombatDeck>();

	public MODEL_Template(XCFFacade facade, String name) {
		this.facade = facade;
		this.name = name;
		cardMgr = (SERVICE_CardManager)facade.getService(SERVICE_CardManager.XCF_TAG);
	}

	public MODEL_Card addCard(String deckName, int cardId) throws XCFException {
		MODEL_CombatDeck deck = decks.get(deckName);
		if (deck == null) {
			deck = new MODEL_CombatDeck(facade, deckName);
			decks.put(deckName, deck);
		}

		MODEL_Card card = deck.add(cardId, Integer.MAX_VALUE);
		return card;
	}

	public final String getName() {
		return name;
	}

	public Set<String> getDeckNames() {
		return decks.keySet();
	}

	public Integer getDeckCost(String deckName) {
		MODEL_CombatDeck deck = decks.get(deckName);
		if (deck == null) return 0;
		return deck.getCost();
	}

	public MODEL_CombatDeck getDeck(String deckName) {
		return decks.get(deckName);
	}

}
