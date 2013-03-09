/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.combat;

import com.eternal.xcf.core.XCFContext;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFLogger;
import com.eternal.xcf.core.XCFRequest;

/**
 * ABILITY:  disable
 *
 * PROPERTY SETTINGS:
 *   character:  the context variables that identifies the character whose deck is being disabled
 *   deck: the deck being disabled
 *   draws:  the number of draws the deck is disabled
 *
 * [character]'s [deck] deck will be disabled for the next [draws] draws.
 *
 * @author sonjayatandon
 *
 */
public class CMD_Disable extends CMD_BaseCommand {
	public static final String XCF_TAG = "discard";

	@Override
	public void execute(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		XCFLogger logger = context.getFacade().getLogManager().getLogger("combat");
		String characterName = getString(request, "character", XCF_TAG);
		String deckName = getString(request, "deck", XCF_TAG);
		int numDraws = getInt(request, "draws", XCF_TAG);

		MODEL_CombatCharacter character = getCharacter(request, characterName, XCF_TAG);
		character.disable(deckName, numDraws);
		ICombatLog mlog = UTIL_Helper.getMatchLog(request);
		ICombatFormulas f = UTIL_Helper.getFormulas(request);
		mlog.logDisable(request, character, deckName, numDraws);
		if (deckName.equals("*")) {
			logger.logMessage(context, XCFLogger.LogTypes.INFO, character.getName() + "'s DECKS are disabled.");
			mlog.logEvent(character.getID(), 8, 12, new int[]{character.getID()});
		} else {
			logger.logMessage(context, XCFLogger.LogTypes.INFO, character.getName() + "'s " + deckName.toUpperCase() + " is disabled.");
			mlog.logEvent(character.getID(), 9, 13, new int[]{character.getID(), f.getDeckID(deckName)});
		}
	}

}
