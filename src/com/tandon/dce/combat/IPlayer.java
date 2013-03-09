package com.tandon.dce.combat;

import com.eternal.xcf.core.XCFException;

public interface IPlayer {

	IPlayer getOpponent();

	MODEL_CombatCharacter getTarget();

	MODEL_CombatCharacter getAlly(MODEL_CombatCharacter modelCombatCharacter,
			int allyIndex);
	
	MODEL_CombatCharacter getBest(MODEL_CombatCharacter modelCombatCharacter,
			String attributeName) throws XCFException ;

}
