/*
 * Copyright 2006 Eternal Adventures, Inc.
 *
 *  Licensed under the Eclipse License, Version 1.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 * @author Sonjaya Tandon
 */
package com.tandon.dce.combat.builder;

import org.xml.sax.Attributes;

import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFStrings;
import com.eternal.xcf.request.processor.XCFProcessingInstruction;
import com.eternal.xcf.request.processor.instructions.INSTRUCTION_Operation;
import com.eternal.xcf.sax.SAXEL_Composite;
import com.tandon.dce.cards.MODEL_Card;

/**
 * This SAXEL creates an INSTRUCTION_Composite and adds it to the module defined
 * in the parent tag as an operation handler
 * 
 * This SAXEL assumes it is a child tag of <module>.
 * This SAXEL will push the instruction it creates onto the handler as an instruction container.
 * 
 * The syntax is of the form:
 *  <operation name="[operation-name]">
 * @author sonjaya
 *
 */
public class SAXEL_Action extends SAXEL_Composite {
	public static final String XCF_TAG = XCFStrings.OPERATION;
	
	public SAXEL_Action() {
		super(XCF_TAG);
	}

	/**
	 * Creates an INSTRUCTION_Composite and adds it to the handler as an instruction container
	 */
	public void startInterpreting(String uri, String name, String rawName, Attributes attributes) {
		XCFFacade facade = getHandler().getFacade();

		// make sure we have a card
		MODEL_Card card = (MODEL_Card)getHandler().get(MODEL_Card.XCF_TAG); 
		if (card == null) return;
		
		// create the composite instruction and push it as the instruction container
		// for any child tags
		INSTRUCTION_Operation operation = new INSTRUCTION_Operation(XCFStrings.EMPTY);
		getHandler().pushValue(XCFProcessingInstruction.XCF_TAG, operation);
		
		facade.logDebug("|----" + " BEGIN ACTION FOR " + card.getName() + "----------");
	}

	/**
	 * Finish interpreting by grabbing the composite instruction off the handler
	 * and adding it to the module as an operation handler.
	 */
	public void endInterpreting(String uri, String name, String qName) {
		XCFFacade facade = getHandler().getFacade();
		MODEL_Card card = (MODEL_Card)getHandler().get(MODEL_Card.XCF_TAG); 
		if (card == null) return;
		INSTRUCTION_Operation operation = (INSTRUCTION_Operation)getHandler().popValue(XCFProcessingInstruction.XCF_TAG);
		if (operation == null) return;
		
		card.setAction(operation);
		facade.logDebug("|----" + " END ACTION FOR  " + card.getName() + "----------");
	}
}
