/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.combat;

import com.eternal.xcf.core.XCFContext;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFLogger;
import com.eternal.xcf.core.XCFRequest;

/**
 * ABILITY:  resolve-defense-damage
 *
 *
 * PROPERTY SETTINGS:
 *   attacker:  the character whose is attacking
 *   defender:  the character whose is defending
 *   damage: amount of damage to apply to defender
 *   fatigue:  base fatigue to be applied to defender
 *
 * CONTEXT VARIABLES USED:
 *   active-card: the card currently being played
 *
 * [attacker] does fatigue and damage to [defender].
 *
 * @author sonjayatandon
 *
 */
public class CMD_DirectDamage extends CMD_BaseCommand {
	public static final String XCF_TAG = "direct-damage";

	@Override
	public void execute(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		String attackerName = getString(request, "attacker", XCF_TAG);
		String defenderName =  getString(request, "defender", XCF_TAG);

		MODEL_CombatCharacter attacker = getCharacter(request, attackerName, XCF_TAG);
		MODEL_CombatCharacter defender = getCharacter(request, defenderName, XCF_TAG);
		XCFLogger logger = context.getFacade().getLogManager().getLogger("combat");

		String damageType = getString(request, "defense-damage-type", XCF_TAG);
		int damage = getInt(request, "damage", XCF_TAG);
		int fatigue = getInt(request, "fatigue", XCF_TAG);
		int damageAmount = damage;
		int fatigueDamage = fatigue;

		ICombatLog mlog = UTIL_Helper.getMatchLog(request);

		MODEL_CombatCharacter oldAttacker = getCharacter(request, "attacker", XCF_TAG);
		MODEL_CombatCharacter oldDefender = getCharacter(request, "defender", XCF_TAG);
		setCharacter(request, "attacker", attacker);
		setCharacter(request, "defender", defender);
		damageAmount = defender.takeDamage(request, damageAmount, fatigueDamage);
		logger.logMessage(context, XCFLogger.LogTypes.INFO, defender.getName() + " takes " + damageAmount + " damage and " + fatigueDamage + " fatigue.");
		mlog.logEvent(defender.getID(), 7, 11, new int[]{defender.getID(), damageAmount, fatigueDamage});
		setCharacter(request, "attacker", oldAttacker);
		setCharacter(request, "defender", oldDefender);

		UTIL_Helper.setDamageSource(request, getCard(request, XCF_TAG));
		UTIL_Helper.setDamageBuffer(request, null);
		mlog.logDamage(request, defender, damageType, damageAmount, fatigueDamage);
		UTIL_Helper.setDamageBuffer(request, null);
		UTIL_Helper.setDamageSource(request, null);
	}

}
