/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.combat;

import com.eternal.xcf.core.XCFContext;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFLogger;
import com.eternal.xcf.core.XCFRequest;

/**
 * ABILITY:  set-next-attacker
 *
 * PROPERTY SETTINGS:
 *   attacker:  the context variables that identifies the character whose should be the next attacker
 *   defender:  the context variables that identifies the character whose should be the next defender
 *
 * CONTEXT VARIABLES USED:
 *   next-attacker:  the character whose is attacking
 *   next-defender:  the character whose is defending
 *
 * Sets the attacker for the next attack turn to [attacker]
 * Sets the defender for the next attack turn to [defender]
 *
 * @author sonjayatandon
 *
 */
public class CMD_SetNextAttacker extends CMD_BaseCommand {
	public static final String XCF_TAG = "set-next-attacker";

	@Override
	public void execute(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		XCFLogger logger = context.getFacade().getLogManager().getLogger("combat");
		String attackerName = getString(request, "attacker", XCF_TAG);
		String defenderName =  getString(request, "defender", XCF_TAG);

		MODEL_CombatCharacter nextAttacker = getCharacter(request, attackerName, XCF_TAG);
		MODEL_CombatCharacter nextDefender = getCharacter(request, defenderName, XCF_TAG);
		setCharacter(request, "next-attacker", nextAttacker);
		setCharacter(request, "next-defender", nextDefender);
		ICombatLog mlog = com.tandon.dce.combat.UTIL_Helper.getMatchLog(request);
		logger.logMessage(context, XCFLogger.LogTypes.INFO, nextAttacker.getName() + " now has initiative.");
		mlog.logEvent(nextAttacker.getID(), 16, 22, new int[]{nextAttacker.getID()});

		mlog.logInitiative(request, nextAttacker);
	}
}
