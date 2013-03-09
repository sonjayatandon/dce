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
 * ABILITY:  defendable-hit
 *
 * PROPERTY SETTINGS:
 *   damage:  the damage done on a successul hit
 *   fatigue:  fatigue damage done during the attack
 *   parry-damage:  amount of damage done if a parry style defense used
 *   block-damage:  amount of damage done if a block style defense used
 *   deck: deck used by defender to defend attack
 *   target-keyword (optional): keyword that defender's card must match.
 *   exclude-keyword(optional):  keywork used to identify cards that can't be drawn
 *   required-to-defend (optional): the number of cards required to defend (only first card is played)
 *   ally-index (optional): if specified, targets the ally of the defender as specified by ally index 
 *
 * CONTEXT VARIABLES USED:
 *   attacker:  the character whose is attacking
 *   defender:  the character whose is defending
 *   active-card: the card currently being played
 *   defender-card: the card used to defend the attack
 *
 * [defender] attempts to draw the first [required-to-defend] card(s) from [defender]'s [deck] deck
 * that matches the [target-keyword] (and doesn't match [exclude-keyword] to counter defendable-hit.
 * If [defender] is able to draw a card, the [defender] plays that card.  Result is set to 'defended'.
 *    (If multiple cards were draw for defense, only the first is played)
 * If [defender] in unable to draw a card, the attacker deals damage and fatigue to the [defender].
 * Result is set to 'hit'
 *
 *
 * @author sonjayatandon
 *
 */
public class CMD_DefendableHit extends CMD_BaseCommand {
	public static final String XCF_TAG = "defendable-hit";

	@Override
	public void execute(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		XCFLogger logger = context.getFacade().getLogManager().getLogger("combat");
		MODEL_Card card = getCard(request, XCF_TAG);
		MODEL_CombatCharacter defender = getDefender(request, XCF_TAG);
		MODEL_CombatCharacter attacker = getAttacker(request, XCF_TAG);
		
		String deckName = getString(request, "deck", XCF_TAG);

		String targetKeyword = (String)getProperty("target-keyword");
		String excludeKeyword = (String)getProperty("exclude-keyword");
		if (targetKeyword == null) targetKeyword = "*";

		int requiredToDefend = getInt(request, "required-to-defend", 1, XCF_TAG);
		int damage = getInt(request, "damage", XCF_TAG);
		int fatigue = getInt(request, "fatigue", XCF_TAG);
		Integer parryDamage = getInt(request, "parry-damage", XCF_TAG);
		Integer blockDamage = getInt(request, "block-damage", XCF_TAG);
		
		int allyIndex = getInt(request, "ally-index", -1, XCF_TAG);
		if (allyIndex >= 0) {
			defender = defender.getAlly(allyIndex);
		}

		ICombatLog mlog = UTIL_Helper.getMatchLog(request);
		boolean disabled = defender.decrementDisabled(deckName);
		if (disabled) {
			logger.logMessage(context, XCFLogger.LogTypes.INFO, defender.getName() + "'s defense is DISABLED.");
			mlog.logEvent(defender.getID(), 0, 9, new int[]{defender.getID()});
		}

		com.eternal.xcf.core.response.UTIL_Helper.setResult(request, "none");
		if (!disabled && defender.getDeckSize(deckName, targetKeyword, excludeKeyword) >= requiredToDefend) {
			UTIL_Helper.setDamageSource(request, card);
			UTIL_Helper.setDamageDealer(request, attacker);
			logger.logMessage(context, XCFLogger.LogTypes.INFO, defender.getName() + " defends against " + card.getName().toUpperCase());
			mlog.logEvent(defender.getID(), 6, 10, new int[]{defender.getID(), card.getID()});
			context.putValue("parry-damage", parryDamage);
			context.putValue("block-damage", blockDamage);
			context.putValue("fatigue", fatigue);
			context.putValue("full-damage", damage);
			defender.play(request, deckName, targetKeyword, excludeKeyword);
			requiredToDefend--;
			// now discared the other required defense cards
			for (int i=0; i < requiredToDefend; i++) {
				discardCard(request, attacker, defender, deckName, targetKeyword, excludeKeyword);
			}
			context.putValue("fatigue", 0);
			
			if (com.eternal.xcf.core.response.UTIL_Helper.getResult(request).equals("none")) {
				com.eternal.xcf.core.response.UTIL_Helper.setResult(request, "defended");
			}
		} else {
			UTIL_Helper.setDamageSource(request, card);
			UTIL_Helper.setDamageDealer(request, attacker);
			ICombatFormulas formulas = UTIL_Helper.getFormulas(request);
			int[] damageToApply = formulas.getDamage(attacker, defender, damage, fatigue);
			int damageAmount = damageToApply[0];
			int fatigueDamage = damageToApply[1];
			damageAmount = defender.takeDamage(request, damageAmount, fatigueDamage);
			logger.logMessage(context, XCFLogger.LogTypes.INFO, defender.getName() + " takes " + damageAmount + " damage and " + fatigueDamage + " fatigue.");
			mlog.logEvent(defender.getID(), 7, 11, new int[]{defender.getID(), damageAmount, fatigueDamage});

			// set to reset this in case take damage triggered a chain of events
			UTIL_Helper.setDamageSource(request, card);
			UTIL_Helper.setDamageDealer(request, attacker);
			mlog.logHit(request);
			mlog.logDamage(request, defender, "full-damage", damageAmount, fatigueDamage);
			com.eternal.xcf.core.response.UTIL_Helper.setResult(request, "hit");
		}
	}
}
