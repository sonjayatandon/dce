package com.tandon.dce.combat;

import com.eternal.xcf.core.XCFException;

public interface ICombatFormulas {
	public static String XCF_TAG = "dce-combat-formulas";

	int getHP(MODEL_CombatCharacter character) throws XCFException;

	int[] getDamage(MODEL_CombatCharacter attacker, MODEL_CombatCharacter defender, int damage, int fatigue) throws XCFException;

	int resovleFatigue(MODEL_CombatCharacter character, int fatigueDamage) throws XCFException;

	int getDeckID(String deckName);

	int getPropertyID(String propertyName);

	int getMaxPropertyID();

	String getDeckName(Integer deckId);

	String getPropertyName(Integer propertyId);
}
