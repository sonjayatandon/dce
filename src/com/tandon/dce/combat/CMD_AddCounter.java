/*
 * Copyright 2009 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.combat;

import com.eternal.xcf.core.XCFContext;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFLogger;
import com.eternal.xcf.core.XCFRequest;

/**
 * ABILITY:  add-counter
 *
 * PROPERTY SETTINGS:
 *   character:  the character whose attribute is being modified
 *   best: replaces character with ally that has the best attribute as identified by this value
 *   counter:  the name of the counter being modified
 *   amount:  the number being added to the counter
 *   min (optional): defaults to 0 -- minimum value counter will be set
 *
 *
 * add-counter will add [amount] to [character]'s [counter].
 * @author sonjayatandon
 *
 */
public class CMD_AddCounter extends CMD_BaseCommand {
	public static final String XCF_TAG = "add-counter";

	@Override
	public void execute(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		XCFLogger logger = context.getFacade().getLogManager().getLogger("combat");
		String characterName = getString(request, "character", XCF_TAG);
		MODEL_CombatCharacter character = getCharacter(request, characterName, XCF_TAG);
		String best = (String)getProperty("best");
		if (best != null) {
			character = character.getBest(best);
		}
		
		String counterName = getString(request, "counter", XCF_TAG);
		Integer counter = character.getAttribute(counterName, 0);
		int min  = getInt(request, "min", 0, XCF_TAG);
		int amount = getInt(request, "amount", XCF_TAG);

		counter = counter + amount;
		if (counter < min) counter = min;

		ICombatLog mlog = UTIL_Helper.getMatchLog(request);
		ICombatFormulas f = UTIL_Helper.getFormulas(request);
		logger.logMessage(context, XCFLogger.LogTypes.INFO, "Set " + character.getName() + "'s " + counterName.toUpperCase() + " TO " + counter );
		mlog.logEvent(character.getID(), 5, 7, new int[]{character.getID(), f.getPropertyID(counterName), counter});
		character.setAttribute(counterName, counter);
	}

}
