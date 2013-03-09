/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.combat;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;

/**
 * ABILITY:  discard
 *
 * PROPERTY SETTINGS:
 *   discarder:  the context variables that identifies the character is discarding
 *   target:  the context variables that identifies the character whose card is being discarded
 *   deck: the deck that is discarded from
 *   target-keyword(optional):  id of the card being added
 *   exclude-keyword(optional):  keywork used to identify cards that can't be discarded
 *
 * [discarded] discards first card from [target]'s deck [deck] that matches [target-keyword] but does not
 * match [exclude-keyword].
 * If [target-keyword] is not supplied, the '*' keyword is used (which matches to any keyword).
 *
 * @author sonjayatandon
 *
 */
public class CMD_Discard extends CMD_BaseCommand {
	public static final String XCF_TAG = "discard";

	@Override
	public void execute(XCFRequest request) throws XCFException {
		String targetKeyword = (String)getProperty("target-keyword");
		String excludeKeyword = (String)getProperty("exclude-keyword");
		if (targetKeyword == null) targetKeyword = "*";

		String discarderCharacter = getString(request, "discarder", XCF_TAG);
		String targetCharacter = getString(request, "target", XCF_TAG);
		String deckName = (String)getProperty("deck");

		MODEL_CombatCharacter discarder = getCharacter(request, discarderCharacter, XCF_TAG);
		MODEL_CombatCharacter target = getCharacter(request, targetCharacter, XCF_TAG);

		MODEL_CombatCharacter active = UTIL_Helper.setActivePlayer(request, discarder);
		discardCard(request, discarder, target, deckName, targetKeyword, excludeKeyword);
		UTIL_Helper.setActivePlayer(request, active);
	}

}
