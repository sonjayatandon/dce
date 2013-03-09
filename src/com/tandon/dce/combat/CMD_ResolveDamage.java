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
 * ABILITY:  resolve-damage
 *
 * PROPERTY SETTINGS:
 *   damage-dealer:  the context variables that identifies the character whose is dealing the damage
 *   character:  the context variables that identifies the character receiving the damage
 *   damage-type: the type of damage dealt
 *   damage:  the amount of damage dealt
 *
 * CONTEXT VARIABLES USED:
 *   active-card: the card currently being played
 *
 * [damage-dealder] does [damage] damage of type [damage-type] to [character]
 *
 * @author sonjayatandon
 *
 */
public class CMD_ResolveDamage extends CMD_BaseCommand {
	public static final String XCF_TAG = "resolve-defense-damage";

	@Override
	public void execute(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		XCFLogger logger = context.getFacade().getLogManager().getLogger("combat");

		MODEL_Card damageSource = UTIL_Helper.getDamageSource(request);

		String damageDealer = getString(request, "damage-dealer", XCF_TAG);
		String characterName = getString(request, "character", XCF_TAG);
		String damageType = getString(request, "damage-type", XCF_TAG);
		int damage = getInt(request, "damage", XCF_TAG);
		int fatigue = getInt(request, "fatigue", 0, XCF_TAG);
		MODEL_CombatCharacter character = getCharacter(request, characterName, XCF_TAG);

		UTIL_Helper.setDamageDealer(request, getCharacter(request, damageDealer, XCF_TAG));
		UTIL_Helper.setDamageSource(request, getCard(request, XCF_TAG));

		ICombatLog mlog = UTIL_Helper.getMatchLog(request);
		damage = character.takeDamage(request, damage, 0);
		logger.logMessage(context, XCFLogger.LogTypes.INFO, character.getName() + " takes " + damage + " damage.");
		mlog.logEvent(character.getID(), 15, 21, new int[]{character.getID(), damage});
		UTIL_Helper.setDamageSource(request, damageSource);
		mlog.logDamage(request, character, damageType, damage, fatigue);
	}

}
