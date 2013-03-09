package com.tandon.dce.combat;

import com.eternal.xcf.core.XCFContext;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFLogger;
import com.eternal.xcf.core.XCFRequest;
import com.tandon.dce.cards.MODEL_Card;

/**
 * ABILITY:  recover-card
 *
 * PROPERTY SETTINGS:
 *   character:  the context variables that identifies the character whose discarded cards are being recovered
 *   deck: the deck whose cards are being recovered
 *   amount:  the number of cards to recover
 *   target-keyword(optional):  keywork used to match card cards to recover
 *   exclude-keyword(optional):  keywork used to exclude cards from being recovered
 *
 * Recovers the first [amount] cards from the top [character]'s [deck] discard that matches [target-keyword] but
 * does NOT match [exclude-keyword].  Places the recovered cards on the bottom of [deck] deck.
 *
 * @author sonjayatandon
 *
 */
/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
public class CMD_RecoverCard extends CMD_BaseCommand {
	public static final String XCF_TAG = "discard";

	@Override
	public void execute(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		XCFLogger logger = context.getFacade().getLogManager().getLogger("combat");
		String keyword = (String)getProperty("target-keyword");
		if (keyword == null) keyword = "*";
		String exclude = (String)getProperty("exclude-keyword");

		String characterName = getString(request, "character", XCF_TAG);
		String deckName = getString(request, "deck", XCF_TAG);
		int amount = getInt(request, "amount", XCF_TAG);

		MODEL_CombatCharacter character = getCharacter(request, characterName, XCF_TAG);
		ICombatLog mlog = UTIL_Helper.getMatchLog(request);
		ICombatFormulas f = UTIL_Helper.getFormulas(request);
		for (int i = 0; i < amount; i++) {
			MODEL_Card recoveredCard = character.recover(deckName, keyword, exclude);
			mlog.logRecover(request, character, deckName, recoveredCard);
			if (recoveredCard == null) {
				logger.logMessage(context, XCFLogger.LogTypes.INFO, "There are no cards to recover for " + deckName.toUpperCase());
				mlog.logEvent(character.getID(), 0, 18, new int[]{f.getDeckID(deckName)});
			} else {
				character.addCard(deckName, recoveredCard);
				logger.logMessage(context, XCFLogger.LogTypes.INFO, character.getName() + " added " + recoveredCard.getName().toUpperCase() + " to " + deckName.toUpperCase());
				mlog.logEvent(character.getID(), 14, 6, new int[]{recoveredCard.getID(), character.getID(), f.getDeckID(deckName)});
			}
		}
	}
}
