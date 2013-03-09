/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */

package com.tandon.dce.combat;

import java.util.ArrayList;
import java.util.StringTokenizer;

import com.eternal.xcf.core.XCFContext;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFLogger;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.request.processor.instructions.INSTRUCTION_Command;
import com.tandon.dce.cards.MODEL_Card;

public abstract class  CMD_BaseCommand extends INSTRUCTION_Command {

	public MODEL_Card getCard(XCFRequest request, String commandName) throws XCFException {
		MODEL_Card card = UTIL_Helper.getActiveCard(request);
		if (card == null) throw new XCFException("No card in context for " + commandName);
		return card;
	}

	public MODEL_CombatCharacter getAttacker(XCFRequest request, String commandName) throws XCFException {
		XCFContext context = request.getContext();
		MODEL_CombatCharacter attacker = (MODEL_CombatCharacter)context.getValue("attacker");
		MODEL_Card card = UTIL_Helper.getActiveCard(request);
		if (attacker == null) throw new XCFException("No attacker in context for  " + card.getName() + "." + commandName);
		return attacker;
	}

	public MODEL_CombatCharacter getDefender(XCFRequest request, String commandName) throws XCFException {
		XCFContext context = request.getContext();
		MODEL_CombatCharacter attacker = (MODEL_CombatCharacter)context.getValue("defender");
		MODEL_Card card = UTIL_Helper.getActiveCard(request);
		if (attacker == null) throw new XCFException("No attacker in context for  " + card.getName() + "." + commandName);
		return attacker;
	}
	public MODEL_CombatCharacter getCharacter(XCFRequest request, String character, String commandName) throws XCFException {
		XCFContext context = request.getContext();
		MODEL_CombatCharacter attacker = (MODEL_CombatCharacter)context.getValue(character);
		MODEL_Card card = UTIL_Helper.getActiveCard(request);
		if (attacker == null) throw new XCFException("No " + character + " in context for  " + card.getName() + "." + commandName);
		return attacker;
	}
	public void setCharacter(XCFRequest request, String characterName, MODEL_CombatCharacter character) throws XCFException {
		XCFContext context = request.getContext();
		context.putValue(characterName, character);
	}

	public String getString(XCFRequest request, String propertyName, String commandName) throws XCFException {
		MODEL_Card card = UTIL_Helper.getActiveCard(request);
		String property = (String)getProperty(propertyName);
		if (property == null) throw new XCFException (propertyName + " not specified for " + card.getName() + "." + commandName);
		return property;
	}

	public String getString(XCFRequest request, String propertyName, String defaultValue, String commandName) throws XCFException {
		String property = (String)getProperty(propertyName);
		if (property == null) return defaultValue;
		return property;
	}

	public int getInt(XCFRequest request, String propertyName, String commandName) throws XCFException {
		MODEL_Card card = UTIL_Helper.getActiveCard(request);
		String sAmount = (String)getProperty(propertyName);

		if (sAmount == null) throw new XCFException (propertyName +" not specified for " + card.getName() + "." + commandName);
		if (isFormula(sAmount)) {
			return calcFormula(request, sAmount, commandName);
		}

		int amount = 0;
		try {
			amount = Integer.parseInt(sAmount);
		} catch (NumberFormatException e) {
			throw new XCFException("The value " + sAmount + " for " + propertyName +" not and integer for " + card.getName() + "." + commandName);
		}

		return amount;
	}

	public int getInt(XCFRequest request, String propertyName, int defaultValue, String commandName) throws XCFException {
		MODEL_Card card = UTIL_Helper.getActiveCard(request);
		String sAmount = (String)getProperty(propertyName);

		if (sAmount == null) return defaultValue;
		if (isFormula(sAmount)) {
			return calcFormula(request, sAmount, commandName);
		}

		int amount = 0;
		try {
			amount = Integer.parseInt(sAmount);
		} catch (NumberFormatException e) {
			throw new XCFException(propertyName +" not and integer for " + card.getName() + "." + commandName);
		}

		return amount;
	}

	boolean isFormula(String value) {
		value = value.trim().toLowerCase();

		if (value.charAt(0) >= 'a' && value.charAt(0) <= 'z' ) return true;

		return false;
	}

	boolean isArray(String value) {
		
		value = value.trim().toLowerCase();
		
		if (value.length() == 0) return false;
		
		if (value.startsWith("$") && value.indexOf("(") != -1) return true;	
		
		return false;
	}
	
	boolean isVar(String value) {
		
		value = value.trim().toLowerCase();
		
		if (value.length() == 0) return false;
		
		if (value.startsWith("$")) return true;	
		
		return false;
	}
	
	int calcFormula(XCFRequest request, String formula, String commandName) throws XCFException {
		StringTokenizer tokenizer = new StringTokenizer(formula);
		int value = 0;
		while (tokenizer.hasMoreTokens()) {
			value = evaluate(request, tokenizer, formula, commandName);
		}

		return value;
	}

	int evaluate(XCFRequest request, StringTokenizer tokenizer, String formula, String commandName) throws XCFException {
		int value = 0;
		String var = tokenizer.nextToken();
		value = getValue(request, var, commandName);

		while (tokenizer.hasMoreTokens()) {
			String operator = tokenizer.nextToken();
			var = tokenizer.nextToken();

			if ("+".equals(operator)) {
				value += getValue(request, var, commandName);
			} else if (("-").equals(operator)) {
				value -= getValue(request, var, commandName);
			} else if (("*").equals(operator)) {
				value *= getValue(request, var, commandName);
			} else if (("/").equals(operator)) {
				value /= getValue(request, var, commandName);
			}
		}

		return value;
	}

	@SuppressWarnings("unchecked")
	Object arrayLookup(XCFRequest request, String arrayString, String commandName)  throws XCFException {
		int startBracket = arrayString.indexOf("(");
		int endBracket = arrayString.indexOf(")");
		
		String arrayVar = arrayString.substring(1, startBracket);
		String indexString= arrayString.substring(startBracket+1, endBracket);
		int index = calcFormula(request, indexString, commandName);
		ArrayList<Object> list = (ArrayList<Object>)request.getContext().getValue(arrayVar); 
		
		return list.get(index);
	}

	Object varLookup(XCFRequest request, String varName, String commandName) throws XCFException 
	{
		Object value = request.getContext().getValue(varName.substring(1));
		if (value == null) return "";
		return value.toString();
	}
	
	int getValue(XCFRequest request, String varName, String commandName) throws XCFException {
		int i = varName.indexOf('.');
		
		if (i == -1) {
			// assume it is a number
			int value = Integer.parseInt(varName);
			return value;
		} else {
			// it is a variable
			String characterName = varName.substring(0, i);

			varName = varName.substring(i+1);

			MODEL_CombatCharacter character =  getCharacter(request, characterName, commandName);

			int value = character.getAttribute(varName);
			return value;
		}
	}

	public boolean propertyExists(String propertyName) {
		return getProperty(propertyName) != null;
	}

	public int getIntFromContext(XCFRequest request, String variableName, String commandName) throws XCFException {
		XCFContext context = request.getContext();
		MODEL_Card card = UTIL_Helper.getActiveCard(request);

		try {
			Integer value = (Integer)context.getValue(variableName);
			if (value != null) return value;
		} catch (ClassCastException e) {
			throw new XCFException(variableName +" not and integer for " + card.getName() + "." + commandName);
		}

		throw new XCFException (variableName +" not specified for " + card.getName() + "." + commandName);
	}

	public int getIntFromContext(XCFRequest request, String variableName, int defaultValue, String commandName) throws XCFException {
		XCFContext context = request.getContext();
		MODEL_Card card = UTIL_Helper.getActiveCard(request);

		try {
			Integer value = (Integer)context.getValue(variableName);
			if (value != null) return value;
		} catch (ClassCastException e) {
			throw new XCFException(variableName +" not and integer for " + card.getName() + "." + commandName);
		}

		return defaultValue;
	}

	public void discardCard(XCFRequest request, MODEL_CombatCharacter discarder, MODEL_CombatCharacter target, String deckName, String targetKeyword, String excludeKeyword) throws XCFException {
		XCFContext context = request.getContext();
		XCFLogger logger = context.getFacade().getLogManager().getLogger("combat");
		ICombatLog mlog = UTIL_Helper.getMatchLog(request);
		ICombatFormulas f = UTIL_Helper.getFormulas(request);
		logger.logMessage(context, XCFLogger.LogTypes.INFO, discarder.getName() + " draws from " + target.getName() + "'s " + deckName.toUpperCase());
		mlog.logEvent(discarder.getID(), 10, 14, new int[]{discarder.getID(), target.getID(), f.getDeckID(deckName)});
		MODEL_Card zappedCard = target.draw(request, deckName, targetKeyword, excludeKeyword);
		if (zappedCard != null) {
			target.discard(deckName, zappedCard);
			mlog.logDiscard(request, target, deckName, zappedCard);
			logger.logMessage(context, XCFLogger.LogTypes.INFO, discarder.getName() + " discards "  + target.getName() + "'s " + zappedCard.getName().toUpperCase());
			mlog.logEvent(discarder.getID(), 11, 15, new int[]{discarder.getID(), target.getID(), zappedCard.getID()});
		}
	}

	public String getTargetKeywords(XCFRequest request, MODEL_CombatCharacter character, String commandName) throws XCFException {
		XCFContext context = request.getContext();
		String excludeKeyword = (String)getProperty("target-keyword");
		String characterExclude = (String)context.getValue(character.getName() + ".exclude-keyword");

		if (excludeKeyword != null && excludeKeyword.trim().length() != 0) excludeKeyword += "#";

		if (excludeKeyword == null) return characterExclude;

		if (characterExclude != null) excludeKeyword += characterExclude;

		return excludeKeyword;
	}

	public String getExcludeKeywords(XCFRequest request, MODEL_CombatCharacter character, String commandName) throws XCFException {
		XCFContext context = request.getContext();
		String excludeKeyword = (String)getProperty("exclude-keyword");
		String characterExclude = (String)context.getValue(character.getName() + ".exclude-keyword");

		if (excludeKeyword != null && excludeKeyword.trim().length() != 0) excludeKeyword += "#";

		if (excludeKeyword == null) return characterExclude;

		if (characterExclude != null) excludeKeyword += characterExclude;

		return excludeKeyword;
	}

}
