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
 * ABILITY:  move-card
 *
 * PROPERTY SETTINGS:
 *   source-character:  the charcter whose card is being moved
 *   source-deck:  the deck we are drawing from
 *   target-character: the character who is getting the card
 *   target-deck: the deck we are moving the card to
 *   amount(optional): number of cards to move, defaults to 1
 *   draw-from (optional): [top|bottom] defaults to top.  Specifies if the card is being drawn from top or bottom
 *   add-to (optional):  [top|bottom] defaults to bottom.  Specifies if the card is being placed on the top or bottom of the deck
 *   target-keyword(optional):  used to match cards to draw from
 *   exclude-keyword(optional):  used  to exclude cards from being drawn
 *
 * moves [amount] number of cards from [source-character]'s [source-deck] to [target-character]'s [target-deck]
 *
 * @author sonjayatandon
 *
 */
public class CMD_MoveCard extends CMD_BaseCommand {
	public static final String XCF_TAG = "move-card";

	@Override
	public void execute(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		XCFLogger logger = context.getFacade().getLogManager().getLogger("combat");
		String sourceCharacterName = getString(request, "source-character", XCF_TAG);
		String sourceDeck = getString(request, "source-deck", XCF_TAG);
		MODEL_CombatCharacter sourceCharacter = getCharacter(request, sourceCharacterName, XCF_TAG);

		String targetCharacterName =  getString(request, "target-character", XCF_TAG);
		String targetDeck =  getString(request, "target-deck", XCF_TAG);
		MODEL_CombatCharacter targetCharacter = getCharacter(request, targetCharacterName, XCF_TAG);

		int amount = getInt(request, "amount", 1, XCF_TAG);

		String drawFrom = getString(request, "draw-from", "top", XCF_TAG);
		boolean drawFromTop = drawFrom.equals("top");

		String addTo = getString(request, "add-to", "bottom", XCF_TAG);
		boolean addToBottom = addTo.equals("bottom");

		String targetKeyword = getString(request, "target-keyword", "*", XCF_TAG);
		String excludeKeyword = (String)getProperty("exclude-keyword");

		for (int i=0; i < amount; i++) {
			MODEL_Card card = drawFromTop?sourceCharacter.draw(request, sourceDeck, targetKeyword, excludeKeyword):
				sourceCharacter.drawFromBottom(request, sourceDeck, targetKeyword, excludeKeyword);
			if (card == null) {
				logger.logMessage(context, XCFLogger.LogTypes.INFO, sourceCharacter.getName() + "'s " + sourceDeck + " does not having any matching cards.");
				break;
			}
			if (addToBottom) {
				targetCharacter.addCard(targetDeck, card);
			} else {
				targetCharacter.pushCard(targetDeck, card);
			}
			ICombatLog mlog = UTIL_Helper.getMatchLog(request);
			ICombatFormulas f = UTIL_Helper.getFormulas(request);
			logger.logMessage(context, XCFLogger.LogTypes.INFO, "Drawing " + card.getName().toUpperCase() + " from " + sourceCharacter.getName() + "'s " + sourceDeck.toUpperCase());
			mlog.logEvent(sourceCharacter.getID(), 12, 16, new int[]{card.getID(), sourceCharacter.getID(),f.getDeckID(sourceDeck)});
			logger.logMessage(context, XCFLogger.LogTypes.INFO, "Adding " + card.getName().toUpperCase() + " to " + targetCharacter.getName() + "'s " + targetDeck.toUpperCase());
			mlog.logEvent(targetCharacter.getID(), 13, 17, new int[]{card.getID(), targetCharacter.getID(), f.getDeckID(targetDeck)});
		}

	}

}
