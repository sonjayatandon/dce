/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.combat;

import com.eternal.xcf.core.XCFContext;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFLogger;
import com.eternal.xcf.core.XCFRequest;
import com.tandon.dce.cards.MODEL_Card;

/**
 * ABILITY:  recover-max
 *
 * PROPERTY SETTINGS:
 *   character:  the character whose discarded cards are being recovered
 *   deck: the deck whose cards are being recovered
 *   amount:  the number of cards to recover
 *   card-attribute: the attribute being used when searching for max
 *   target-keyword(optional):  keywork used to match card cards to recover
 *   exclude-keyword(optional):  keywork used to exclude cards from being recovered
 *
 * Recovers the [amount] cards with the highest [card-attribute] from the
 * top of [character]'s [deck] discard that matches [target-keyword] but
 * does NOT match [exclude-keyword].  Places the recovered cards on the bottom of [deck] deck.
 *
 * @author sonjayatandon
 *
 */
public class CMD_RecoverMax extends CMD_BaseCommand {
	public static final String XCF_TAG = "recover-max";

	@Override
	public void execute(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		XCFLogger logger = context.getFacade().getLogManager().getLogger("combat");
		String cardAttribute = getString(request, "card-attribute", XCF_TAG);
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
			MODEL_Card recoveredCard = character.recoverMax(deckName, cardAttribute, keyword, exclude);
			mlog.logRecover(request, character, deckName, recoveredCard);
			if (recoveredCard == null) {
				logger.logMessage(context, XCFLogger.LogTypes.INFO, character.getName() + " isn't able to recover a matching card in " + deckName.toUpperCase());
				mlog.logEvent(character.getID(), 0, 19, new int[]{character.getID(), f.getDeckID(deckName)});
			} else {
				character.addCard(deckName, recoveredCard);
				logger.logMessage(context, XCFLogger.LogTypes.INFO, character.getName() + " recovered " + recoveredCard.getName().toUpperCase() + " for " + deckName.toUpperCase());
				mlog.logEvent(character.getID(), 14, 20, new int[]{character.getID(), recoveredCard.getID(), f.getDeckID(deckName)});
			}
		}
	}

}
