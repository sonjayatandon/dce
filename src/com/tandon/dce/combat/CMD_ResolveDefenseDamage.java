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
 * ABILITY:  resolve-defense-damage
 *
 * PROPERTY SETTINGS:
 *   defense-damage-type: the type of damage to apply to the defender
 *
 * CONTEXT VARIABLES USED:
 *   attacker:  the character whose is attacking
 *   defender:  the character whose is defending
 *   full-damage: amount of damange to apply if [damage-type] is full-damage
 *   parry-damage: amount of damage to apply if [damage-type] is parry-damage
 *   block-damage: amount of damage to apply if [damage-type] is block-damage
 *   fatigue:  base fatigue to be applied to defender
 *   active-card: the card currently being played
 *
 * [attacker] does fatigue and damage to [defender].
 *
 * @author sonjayatandon
 *
 */
public class CMD_ResolveDefenseDamage extends CMD_BaseCommand {
	public static final String XCF_TAG = "resolve-defense-damage";

	@Override
	public void execute(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		MODEL_CombatCharacter attacker = getAttacker(request, XCF_TAG);
		XCFLogger logger = context.getFacade().getLogManager().getLogger("combat");
		MODEL_CombatCharacter defender = getDefender(request, XCF_TAG);
		MODEL_Card damageSource = UTIL_Helper.getDamageSource(request);

		String damageType = getString(request, "defense-damage-type", XCF_TAG);
		ICombatFormulas formulas = UTIL_Helper.getFormulas(request);
		int damage = getIntFromContext(request, damageType, 0, XCF_TAG);
		int fatigue = getIntFromContext(request, "fatigue", XCF_TAG);
		int[] damageToApply = formulas.getDamage(attacker, defender, damage, fatigue);
		int damageAmount = damageToApply[0];
		int fatigueDamage = damageToApply[1];

		ICombatLog mlog = UTIL_Helper.getMatchLog(request);
		damageAmount = defender.takeDamage(request, damageAmount, fatigueDamage);
		logger.logMessage(context, XCFLogger.LogTypes.INFO, defender.getName() + " takes " + damageAmount + " damage and " + fatigueDamage + " fatigue.");
		mlog.logEvent(defender.getID(), 7, 11, new int[]{defender.getID(), damageAmount, fatigueDamage});
		UTIL_Helper.setDamageBuffer(request, getCard(request, XCF_TAG));
		UTIL_Helper.setDamageSource(request, damageSource);
		mlog.logDamage(request, defender, damageType, damageAmount, fatigueDamage);
		UTIL_Helper.setDamageBuffer(request, null);
	}

}
