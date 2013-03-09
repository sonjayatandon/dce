/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.combat;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;
import com.tandon.dce.cards.MODEL_Card;

public interface ICombatLog {

	void logPlayCard(XCFRequest request, MODEL_CombatCharacter cardOwner, String deckName, MODEL_Card card) throws XCFException;

	void logDiscard(XCFRequest request, MODEL_CombatCharacter target, String deckName, MODEL_Card zappedCard) throws XCFException;

	void logDamage(XCFRequest request, MODEL_CombatCharacter defender, String string, int damageAmount, int fractionalDamage) throws XCFException;

	void logDisable(XCFRequest request, MODEL_CombatCharacter character, String deckName, int numDraws) throws XCFException;

	void logOverride(XCFRequest request, MODEL_CombatCharacter character, String deckName, int cardId, int amount) throws XCFException;

	void logRecover(XCFRequest request, MODEL_CombatCharacter character, String deckName, MODEL_Card recoveredCard) throws XCFException;

	void logInitiative(XCFRequest request, MODEL_CombatCharacter nextAttacker) throws XCFException;

	void logHit(XCFRequest request) throws XCFException;

	void logAddCard(XCFRequest request, MODEL_CombatCharacter character, String deckName, MODEL_Card card, int amount);

//	int getDeckID(String deckName);

	void logEvent(int avatarID, int eventID, int messageID, int[] params);

//	int getPropertyID(String counterName);

}
