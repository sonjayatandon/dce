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
package com.eternal.xcf.common.builder;

import org.xml.sax.Attributes;

import com.eternal.xcf.common.service.SERVICE_FlyweightFactory;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFStrings;
import com.eternal.xcf.request.parameter.XCFValidator;
import com.eternal.xcf.request.processor.XCFProcessingInstruction;
import com.eternal.xcf.request.processor.instructions.INSTRUCTION_Composite;
import com.eternal.xcf.request.processor.instructions.INSTRUCTION_ProcessParameter;
import com.eternal.xcf.sax.SAXEL_Composite;

/**
 * This SAXEL uses the flyweight factory to create a validator and adds that validator
 * to the INSTRUCTION_Processor on the handler.
 * 
 * This SAXEL assumes it is a child tag of a parameter processing definition.
 * 
 * The syntax is of the form:
 *  <validate with="[validator-name]">
 * 
 * The flyweight factory will use [validator-name] as the flyweight tag.
 *  
 * @author sonjaya
 *
 */
public class SAXEL_Validate extends SAXEL_Composite {
	public static final String XCF_TAG = XCFProcessingInstruction.VALIDATE;
	
	public SAXEL_Validate() {
		super(XCF_TAG);
	}

	/**
	 * Make sure the container is a parameter processor
	 * Create the validator and add it to the parameter processor
	 */
	public void startInterpreting(String uri, String name, String rawName, Attributes attributes) {
		XCFFacade facade = getHandler().getFacade();
		SERVICE_FlyweightFactory flyweightFactory = (SERVICE_FlyweightFactory)facade.getService(SERVICE_FlyweightFactory.XCF_TAG);
		
		// grab the validator name
		String validatorName = attributes.getValue(XCFStrings.WITH);
		
		// it's required
		if (validatorName == null) {
			handleError("<validate ...> must have a with attribute: e.g., <validate with=\"required\">");
			return;
		}
		
		// get the instruction container
		INSTRUCTION_Composite container = null;
		try {
			container = (INSTRUCTION_Composite)getHandler().peekValue(XCFProcessingInstruction.XCF_TAG);
			if (container == null) {
				handleError("<validate with="+ validatorName + "> failed because there was no container.");
				return;
			}
		} catch (ClassCastException e) {
			handleError("<validate with="+validatorName + "> failed because the container was not an INSTRUCTION_ProcessParameter.");
			return;
		}
		
		// make sure the container is an INSTRUCTION_ProcessParameter
		if (!(container instanceof INSTRUCTION_ProcessParameter)) {
			handleError("<validate with="+validatorName + "> failed because the container was not an INSTRUCTION_ProcessParameter.");
			return;
		}		
		
		INSTRUCTION_ProcessParameter pp = (INSTRUCTION_ProcessParameter)container;

		// get the validator and add it to the parameter processor
		try {
			XCFValidator validator = (XCFValidator)flyweightFactory.getInstance(XCFProcessingInstruction.VALIDATOR, validatorName);
			pp.addValidator(validator);
			facade.logDebug("|      ADDED validator: " + validatorName + "(" + validator.getClass().getName() + ")");
		} catch (XCFException e) {
			handleException(e);
		}
	}	
}
