/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.combat;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;

/**
 * ABILITY:  override-deck
 *
 * PROPERTY SETTINGS:
 *   character:  the context variables that identifies the character whose deck is being overriden
 *   deck: the deck that is being overriden
 *   cardId:  id of the card that will be drawn while the deck is overriden
 *   draws:  the number of draws the deck deck is overriden
 *
 * For the next [draws] draws, [character]'s [deck] deck will only draw the card identified by [cardId]
 *
 * This ability is not cummalative.  If the deck is already being overriden, that overide is cleared and the
 * new one is put in place.
 *
 * @author sonjayatandon
 *
 */
public class CMD_OverrideDeck extends CMD_BaseCommand {
	public static final String XCF_TAG = "discard";

	@Override
	public void execute(XCFRequest request) throws XCFException {
		String characterName = getString(request, "character", XCF_TAG);
		String deckName = getString(request, "deck", XCF_TAG);
		int amount = getInt(request, "draws", XCF_TAG);
		int cardId = getInt(request, "cardId", XCF_TAG);

		MODEL_CombatCharacter character = getCharacter(request, characterName, XCF_TAG);

		character.setOverride(deckName, cardId, amount);

		ICombatLog mlog = UTIL_Helper.getMatchLog(request);
		mlog.logOverride(request, character, deckName, cardId, amount);
	}

}
