/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.combat;

import java.util.ArrayList;
import java.util.Random;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.tandon.dce.cards.MODEL_Card;
import com.tandon.dce.cards.SERVICE_CardManager;

public class MODEL_CombatDeck {
	XCFFacade facade;
	SERVICE_CardManager cardMgr;
	ArrayList<MODEL_Card> cards = new ArrayList<MODEL_Card>();
	String name;

	int cost = 0;
	MODEL_Card override = null;
	int numOverrides = 0;

	public MODEL_CombatDeck(XCFFacade facade, String name) throws XCFException {
		this.facade = facade;
		cardMgr = (SERVICE_CardManager)facade.getService(SERVICE_CardManager.XCF_TAG);
		this.name = name;
	}

	public int getCost() {
		return cost;
	}

	public String getName() {
		return name;
	}

	public MODEL_Card add(Integer cardId, int limit) throws XCFException {
		MODEL_Card card = cardMgr.getCard(cardId);
		if (card == null) throw new XCFException(cardId + " is not a registed card.");

		if ((cost + card.getCost()) > limit) {
			throw new XCFException("You can't add " + card.getName() + ".  You are out of action points.");
		}

		cost += card.getCost();
		add(card);
		return card;
	}

	public MODEL_Card setOverride(int cardId, int amount) throws XCFException {
		MODEL_Card card = cardMgr.getCard(cardId);
		if (card == null) throw new XCFException(cardId + " is not a registed card.");

		override = card;
		numOverrides = amount;

		return card;
	}

	public Integer[] getOverrideState() {
		if (numOverrides <= 0) return null;

		Integer[] overrideState = new Integer[2];
		overrideState[0] = override.getID();
		overrideState[1] = numOverrides;

		return overrideState;
	}

	public void add(MODEL_Card card) throws XCFException {
		cards.add(card);
	}

	public void push(MODEL_Card card) {
		cards.add(0, card);

	}

	public MODEL_Card drawCard() {
		if (cards.size() > 0) {
			MODEL_Card card = cards.remove(0);
			return card;
		}

		return null;
	}

	public MODEL_Card drawOverride() {
		if (numOverrides > 0) {
			numOverrides--;
			return override;
		}

		override = null;
		return null;
	}

	public MODEL_Card drawCard(String matchKeywords, String excludeKeywords) {
		MODEL_Card drawnCard = null;

		for (MODEL_Card card: cards) {
			if (card.matches(matchKeywords)) {
				if (excludeKeywords == null || !card.matches(excludeKeywords)) {
					drawnCard = card;
					break;
				}
			}
		}

		if (drawnCard == null) return null;
		cards.remove(drawnCard);

		return drawnCard;
	}

	public MODEL_Card drawMaxCard(String propertyName, String matchKeywords, String excludeKeywords) throws XCFException {
		MODEL_Card drawnCard = null;
		int max = -1;

		for (MODEL_Card card: cards) {
			if (card.matches(matchKeywords)) {
				if (excludeKeywords == null || !card.matches(excludeKeywords)) {
					int amount = 0;
					String sCardAttributeValue = card.getProperty(propertyName);
					try {
						amount = Integer.parseInt(sCardAttributeValue);
					} catch (NumberFormatException e) {
						throw new XCFException(propertyName +" not and integer for " + card.getName());
					}

					if (amount > max) {
						drawnCard = card;
						max = amount;
					}
				}
			}
		}

		if (drawnCard == null) return null;
		cards.remove(drawnCard);

		return drawnCard;
	}

	public MODEL_Card drawCardFromBottom(String matchKeywords, String excludeKeywords) {
		MODEL_Card drawnCard = null;

		int i = 0;
		for (i=cards.size()-1; i>=0; i--) {
			MODEL_Card card = cards.get(i);
			if (card.matches(matchKeywords)) {
				if (excludeKeywords == null || !card.matches(excludeKeywords)) {
					drawnCard = card;
					break;
				}
			}
		}

		if (drawnCard == null) return null;
		cards.remove(i);

		return drawnCard;
	}

	public int size() {
		return cards.size();
	}

	public int size(String matchKeywords, String excludeKeywords) {
		if ("*".equals(matchKeywords) && excludeKeywords == null) return cards.size();
		int size = 0;
		for (MODEL_Card card: cards) {
			if (card.matches(matchKeywords)) {
				if (excludeKeywords == null || !card.matches(excludeKeywords)) {
					size++;
				}
			}
		}
		return size;
	}

	public void shuffle() {
		numOverrides = 0;
		override = null;
		shuffle(cards);
	}

	public void shuffle(ArrayList<MODEL_Card> cards) {

		// get a random number generator
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());

		int size = cards.size();

		// for each card in the deck, starting with the first card, randomly
		// pick another card and swap the current card with the randomly chosen on
		// So, if there are 10 cards, then, we start with card 0, and randomly select one of card 1-9 and swap
		// then we go to card 1, and randomly select one of card 2-9, etc until we get to the next to last card.

		for (int i = 0; i < (size-1); i++) {
			int range = size-i;
			int swapIndex = i + rand.nextInt(range);

			if (i != swapIndex) {
				MODEL_Card card1 = cards.get(i);
				MODEL_Card card2 = cards.get(swapIndex);

				cards.set(i, card2);
				cards.set(swapIndex, card1);
			}
		}
	}

	public ArrayList<MODEL_Card> getCards() {
		return cards;
	}

}
