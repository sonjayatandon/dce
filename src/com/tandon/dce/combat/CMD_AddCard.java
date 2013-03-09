/*
 * Copyright 2009 Sonjaya Tandon.  All rights reserved.
 */

package com.tandon.dce.combat;

import com.eternal.xcf.core.XCFContext;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFLogger;
import com.eternal.xcf.core.XCFRequest;
import com.tandon.dce.cards.MODEL_Card;


/**
 * ABILITY:  add-card
 *
 * PROPERTY SETTINGS:
 *   character:  the character whose deck is being modified
 *   deck:  the name of the deck being modified
 *   cardId:  id of the card being added
 *   amount:  number of the cards being added to the deck
 *   ally-index (optional): if specified, targets the ally of the character as specified by ally index 
 *
 *
 * add-card will add [amount] of card [cardId] to the bottom of [character]'s [deck] deck.
 * @author sonjayatandon
 *
 */
public class CMD_AddCard extends CMD_BaseCommand {
	public static final String XCF_TAG = "add-card";

	@Override
	public void execute(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		XCFLogger logger = context.getFacade().getLogManager().getLogger("combat");
		String characterName = getString(request, "character", XCF_TAG);
		String deckName = getString(request, "deck", XCF_TAG);
		int amount = getInt(request, "amount", XCF_TAG);
		int cardId = getInt(request, "cardId", XCF_TAG);

		MODEL_CombatCharacter character = getCharacter(request, characterName, XCF_TAG);
		MODEL_Card card = character.getCard(cardId);
		
		int allyIndex = getInt(request, "ally-index", -1, XCF_TAG);
		if (allyIndex >= 0) {
			character = character.getAlly(allyIndex);
		}

		ICombatLog mlog = UTIL_Helper.getMatchLog(request);
		ICombatFormulas f = UTIL_Helper.getFormulas(request);
		for (int i=0; i < amount; i ++) {
			logger.logMessage(context, XCFLogger.LogTypes.INFO, "Adding " + card.getName().toUpperCase() + " to " + character.getName() + "'s " + deckName.toUpperCase());
			mlog.logEvent(character.getID(), 4, 6, new int[]{card.getID(), character.getID(), f.getDeckID(deckName)});
			character.addCard(deckName, card);
		}

		mlog.logAddCard(request, character, deckName, card, amount);
	}

}
