/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.combat;

import com.eternal.xcf.core.XCFContext;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.node.INodeManager;
import com.tandon.dce.cards.MODEL_Card;

public class UTIL_Helper {
	public static final ICombatFormulas getFormulas(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		ICombatFormulas formulas = (ICombatFormulas)context.getValue(ICombatFormulas.XCF_TAG);
		if (formulas == null) throw new XCFException("No formulas in context.");
		return formulas;
	}

	public static final void setFormulas(XCFRequest request, ICombatFormulas formulas) throws XCFException {
		XCFContext context = request.getContext();
		context.putValue(ICombatFormulas.XCF_TAG, formulas);
	}

	public static final ICombatLog getMatchLog(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		ICombatLog matchLog = (ICombatLog)context.getValue("match");
		if (matchLog == null) throw new XCFException("No combat log in context.");
		return matchLog;
	}

	public static final MODEL_CombatCharacter getActivePlayer(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		MODEL_CombatCharacter character = (MODEL_CombatCharacter)context.getValue("active-character");
		if (character == null) throw new XCFException("No active-character in context.");
		return character;
	}
	public static final MODEL_CombatCharacter setActivePlayer(XCFRequest request, MODEL_CombatCharacter character) throws XCFException {
		XCFContext context = request.getContext();
		if (character == null) return null;
		MODEL_CombatCharacter oldCharacter = (MODEL_CombatCharacter)context.getValue("active-character");
		context.putValue("active-character", character);
		return oldCharacter;
	}

	public static final MODEL_Card getActiveCard(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		MODEL_Card card = (MODEL_Card)context.getValue("active-card");
		if (card == null) throw new XCFException("No active-card in context.");
		return card;
	}

	public static final MODEL_Card setActiveCard(XCFRequest request, MODEL_Card card) throws XCFException {
		XCFContext context = request.getContext();
		if (card == null) return null;
		MODEL_Card pldCard = (MODEL_Card)context.getValue("active-card");
		context.putValue("active-card", card);
		return pldCard;
	}

	public static final MODEL_Card getDamageSource(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		MODEL_Card card = (MODEL_Card)context.getValue("damage-source");
		if (card == null) throw new XCFException("No damage-source in context.");
		return card;
	}
	public static final MODEL_Card getDamageBuffer(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		MODEL_Card card = (MODEL_Card)context.getValue("damage-buffer");
		return card;
	}
	public static final void setDamageBuffer(XCFRequest request, MODEL_Card card) throws XCFException {
		XCFContext context = request.getContext();
		if (card == null) {
			((INodeManager)context).removeDimension("damage-buffer");
		}
		context.putValue("damage-buffer", card);
	}
	public static final MODEL_CombatCharacter getDamageDealer(XCFRequest request) throws XCFException {
		XCFContext context = request.getContext();
		MODEL_CombatCharacter character = (MODEL_CombatCharacter)context.getValue("damage-dealer");
		if (character == null) throw new XCFException("No damage-dealer in context.");
		return character;
	}
	public static final MODEL_CombatCharacter setDamageDealer(XCFRequest request, MODEL_CombatCharacter character) throws XCFException {
		XCFContext context = request.getContext();
		if (character == null) return null;
		MODEL_CombatCharacter oldCharacter = (MODEL_CombatCharacter)context.getValue("damage-dealer");
		context.putValue("damage-dealer", character);
		return oldCharacter;
	}
	public static final void setDamageSource(XCFRequest request, MODEL_Card card) throws XCFException {
		XCFContext context = request.getContext();
		if (card == null) {
			((INodeManager)context).removeDimension("damage-source");
		}
		context.putValue("damage-source", card);
	}



}
