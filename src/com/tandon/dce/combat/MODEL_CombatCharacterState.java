package com.tandon.dce.combat;

import java.util.HashMap;

import com.eternal.xcf.core.XCFException;
import com.tandon.dce.cards.MODEL_Card;

public class MODEL_CombatCharacterState {
	////////////////////////////////////////////////////
	// CHARACTER STATE
	//
	//   characterID
	//   [deckID, [cardid,]*]*
	//   [deckID, cardId, override count]*
	//   [attributeID, value]* (including damageTaken, ko, hp)
	//   [disabled deckID, value]*
	ICombatFormulas formulas;
	Integer characterID;
	HashMap<Integer, Integer[]> decks = new HashMap<Integer, Integer[]>();
	HashMap<Integer, Integer> attributeValues = new HashMap<Integer, Integer>();
	HashMap<Integer, Integer> disabledDecks = new HashMap<Integer, Integer>();
	HashMap<Integer, Integer[]> overrideDecks = new HashMap<Integer, Integer[]>();

	public MODEL_CombatCharacterState(ICombatFormulas formulas, Integer characterID) {
		this.formulas = formulas;
		this.characterID = characterID;
	}

	static final String COMMA = ",";
	static final String EMPTY = "";
	static final String START_OBJECT="{";
	static final String END_OBJECT="}";
	static final String START_ARRAY="[";
	static final String END_ARRAY="]";
	static final String COLON = ":";

	static final String DISABLE = "disable:";
	static final String ID = "id:";
	static final String ATTRIBUTES="attributes:";
	static final String OVERRIDE="override:";
	static final String AMOUNT="amount:";
	static final String CARDS="cards:";

	//
	// syntax
	// id: [id],
	// attributes: [...],
	// [deckname]: {disable: [num], override: {id: [num], amount: [num] }, cards: [...] }
	public String getJSON() {
		StringBuffer b = new StringBuffer();

		b.append(START_OBJECT);

		// id: [id],
		b.append(ID);
		b.append(characterID);
		b.append(COMMA);
		// attributes: [...],
		b.append(ATTRIBUTES);
		b.append(START_ARRAY);
		String sep = EMPTY;
		int maxAttribute = formulas.getMaxPropertyID();
		for (int i=0; i<maxAttribute; i++) {
			b.append(sep);
			Integer value = attributeValues.get(i);
			b.append(value==null?0:value);
			sep = COMMA;
		}
		b.append(END_ARRAY);
		b.append(COMMA);

		// [deckname]: {disable: [num], override: {id: [num], amount: [num] }, cards: [...] }
		sep = EMPTY;
		for (Integer deckId: decks.keySet()) {
			b.append(sep);
			getJSONForDeck(b, deckId);
			sep = COMMA;
		}

		b.append(END_OBJECT);
		return b.toString();
	}

	// [deckname]: {disable: [num], override: {id: [num], amount: [num] }, cards: [...] }
	void getJSONForDeck(StringBuffer b, Integer deckId) {
		b.append(formulas.getDeckName(deckId));
		b.append(COLON);
		b.append(START_OBJECT);

		// disable: [num]
		b.append(DISABLE);
		Integer num = disabledDecks.get(deckId);
		b.append(num==null?0:num);
		b.append(COMMA);

		// override: {id: [num], amount: [num] }
		Integer[] overrideInfo = overrideDecks.get(deckId);
		b.append(OVERRIDE);
		b.append(START_OBJECT);
		b.append(ID);
		b.append(overrideInfo==null?0:overrideInfo[0]);
		b.append(COMMA);
		b.append(AMOUNT);
		b.append(overrideInfo==null?0:overrideInfo[1]);
		b.append(END_OBJECT);
		b.append(COMMA);

		// cards: [...]
		b.append(CARDS);
		b.append(START_ARRAY);
		String sep = EMPTY;
		Integer[] cards = decks.get(deckId);
		for (Integer cardId: cards) {
			b.append(sep);
			b.append(cardId);
			sep = COMMA;
		}
		b.append(END_ARRAY);

		b.append(END_OBJECT);
	}

	public Integer getID() {
		return characterID;
	}

	public void setCharacterID(Integer characterID) {
		this.characterID = characterID;
	}

	public void setDeck(Integer deckID, Integer[] cards) {
		decks.put(deckID, cards);
	}

	public void setDisabledDecks(Integer[] disabledDeckValues) {
		int deckID=0;
		for (Integer value: disabledDeckValues) {
			disabledDecks.put(deckID, value);
			setProperty(deckID, value);
			deckID++;
		}
	}

	public void setOverrideDeck(Integer deckID, Integer cardID, Integer amount) {
		Integer[] overrideState = new Integer[]{cardID, amount};
		overrideDecks.put(deckID, overrideState);
	}

	public void setProperties(Integer[] values) {
		int propertyID=0;
		for (Integer value: values) {
			setProperty(propertyID, value);
			propertyID++;
		}
	}

	public void setProperty(Integer propertyID, Integer value) {
		attributeValues.put(propertyID, value);
	}

	static Integer ZERO = new Integer(0);
	static Integer ONE = new Integer(1);
	static Integer TWO = new Integer(2);
	public Integer getProperty(Integer propertyID) {
		Integer value = attributeValues.get(propertyID);
		if (value == null) return ZERO;
		return value;
	}

	public void loadFromState(MODEL_CombatCharacter character) throws XCFException {
		// set damageTaken, ko, and hp
		character.setDamageTaken(getProperty(ZERO));
		character.setKO(getProperty(ONE).equals(ZERO)?false:true);
		character.setHP(getProperty(TWO));

		// set all the other properties
		for (Integer propertyId: attributeValues.keySet()) {
			if (propertyId.intValue() > 2) { // skip damageTaken, ko, hp since we already handled
				character.setAttribute(formulas.getPropertyName(propertyId), getProperty(propertyId));
			}
		}

		// load up the decks
		for (Integer deckId:decks.keySet()) {
			String deckName = formulas.getDeckName(deckId);
			Integer[] cards = decks.get(deckId);
			for (Integer cardId: cards) {
				character.addCard(deckName, cardId);
			}
		}

		// set the disabled state
		for (Integer deckId:disabledDecks.keySet()) {
			String deckName = formulas.getDeckName(deckId);
			Integer numDraws = disabledDecks.get(deckId);
			character.disable(deckName, numDraws);
		}

		// set the override state
		for (Integer deckId: overrideDecks.keySet()) {
			String deckName = formulas.getDeckName(deckId);
			Integer[] overrideState = overrideDecks.get(deckId);
			character.getDecks().get(deckName).setOverride(overrideState[0], overrideState[1]);
		}
	}

	public void extractState(MODEL_CombatCharacter character) throws XCFException {
		// get the characterID
		characterID = character.getID();

		// get damageTaken, ko, and hp
		attributeValues.put(ZERO, character.getDamageTaken());
		attributeValues.put(ONE, character.isKnockedOut()?ONE:ZERO);
		attributeValues.put(TWO, character.isKnockedOut()?ZERO:character.getHP()-character.getDamageTaken());

		// get all the other properties
		for (String propertyName: character.getAttributeNames()) {
			attributeValues.put(formulas.getPropertyID(propertyName), character.getAttribute(propertyName));
//			System.out.println("Extracting  property: " + formulas.getPropertyID(propertyName) + ", " + propertyName + "=" + character.getAttribute(propertyName));
		}

		// get all the decks
		HashMap<String, MODEL_CombatDeck> characterDecks = character.getDecks();
		decks.clear();
		decks.put(ZERO, new Integer[0]);
		Integer disabledState = character.getDisabledState("*");
		if (disabledState != null) {
			disabledDecks.put(formulas.getDeckID("*"), disabledState);
		}
		for (String deckName: characterDecks.keySet()) {
			Integer deckId = formulas.getDeckID(deckName);
			MODEL_CombatDeck deck = characterDecks.get(deckName);
			Integer[] deckState = new Integer[deck.size()];
			int i=0;
			for (MODEL_Card card: deck.getCards()) {
				deckState[i]=card.getID();
				i++;
			}
			decks.put(deckId, deckState);

			// get the override state
			Integer[] overrideState = deck.getOverrideState();
			if (overrideState != null) {
				overrideDecks.put(deckId, overrideState);
			}

			// get the disabled state
			disabledState = character.getDisabledState(deckName);
			if (disabledState != null) {
				disabledDecks.put(formulas.getDeckID(deckName), disabledState);
			}
		}
	}
}
