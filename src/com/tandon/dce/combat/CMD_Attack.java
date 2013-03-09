/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.combat;

import com.eternal.xcf.core.XCFContext;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFLogger;
import com.eternal.xcf.core.XCFRequest;

/**
 * ABILITY:  attack
 *
 * PROPERTY SETTINGS:
 *   attacker:  the context variables that identifies the character whose is attacking
 *   defender:  the context variables that identifies the character whose is defending
 *   target-keyword(optional):  keywork used to match card from attack deck
 *   exclude-keyword(optional):  keywork used to identify cards that can't be drawn
 *
 * [attacker] draws the first card matching [target-keyword] (and that doesn't match [exclude-keyword]
 * from the attack deck and plays against [defender].  If [target-keyword] is not supplied, the '*' keyword
 * is used (which matches to any keyword).
 * @author sonjayatandon
 *
 */
public class CMD_Attack extends CMD_BaseCommand {
	public static final String XCF_TAG = "attack";

	@Override
	public void execute(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		XCFLogger logger = context.getFacade().getLogManager().getLogger("combat");
		MODEL_CombatCharacter defender = getDefender(request, XCF_TAG);
		MODEL_CombatCharacter attacker = getAttacker(request, XCF_TAG);
		String attackerName = getString(request, "attacker", XCF_TAG);
		String defenderName =  getString(request, "defender", XCF_TAG);
		String targetKeyword = (String)getProperty("target-keyword");
		String excludeKeyword = (String)getProperty("exclude-keyword");
		if (targetKeyword == null) targetKeyword = "*";

		boolean disabled = attacker.decrementDisabled("attack");

		if (!disabled) {
			MODEL_CombatCharacter newAttacker = getCharacter(request, attackerName, XCF_TAG);
			MODEL_CombatCharacter newDefender = getCharacter(request, defenderName, XCF_TAG);
			setCharacter(request, "attacker", newAttacker);
			setCharacter(request, "defender", newDefender);
			newAttacker.play(request, "attack", targetKeyword, excludeKeyword);
			setCharacter(request, "attacker", attacker);
			setCharacter(request, "defender", defender);
		} else {
			ICombatLog mlog = UTIL_Helper.getMatchLog(request);
			mlog.logEvent(attacker.getID(), 0, 8, new int[]{attacker.getID()});
			logger.logMessage(context, XCFLogger.LogTypes.INFO, attacker.getName() + "'s attack is DISABLED.");
		}
	}

}
