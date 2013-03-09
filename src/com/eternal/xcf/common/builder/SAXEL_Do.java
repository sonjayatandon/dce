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
import com.eternal.xcf.request.processor.XCFProcessingInstruction;
import com.eternal.xcf.request.processor.instructions.INSTRUCTION_Composite;
import com.eternal.xcf.sax.SAXEL_Composite;

/**
 * This SAXEL will lookup and create an processing instruction and add it to the composite
 * instruction in the handler.
 * 
 * This SAXEL assumes an INSTRUCTION_Composite exists in handler under the tag XCFProcessingInstruction.XCF_TAG
 * The instruction object created must be a decendant of INSTRUCTION_Composite.
 * 
 * The syntax is of the form:
 * <do instruction="[instruction-type]" name="[instruction-name]">
 * 
 * This will result in the instruction being created with the flyweight factory as follows:
 *  instruction = flyweightFactory.getInstance("instruction", instruction-type);
 *  instruction.setName(instruction-name)
 *  
 * However, if instruction-type = "command", this will result in the following:
 *  instruction = flyweightFactory.getInstance("command", instruction-name);
 * 
 * In other words, in most cases, intruction-type is used to bind to the actually class name of the instruction.
 * In the case where instruction-type is command, instruction-name is instead used to find the class name.
 * 
 * @author sonjaya
 *
 */
public class SAXEL_Do extends SAXEL_Composite {
	public static final String XCF_TAG = "do";
	
	public SAXEL_Do() {
		super(XCF_TAG);
	}

	/**
	 * Starts interpreting the 'do' tag
	 * Creates a new instruction
	 * Adds that instruction to the container on the handler
	 * Pushes that instruction as the new container
	 */
	public void startInterpreting(String uri, String name, String rawName, Attributes attributes) {
		XCFFacade facade = getHandler().getFacade();
		SERVICE_FlyweightFactory flyweightFactory = (SERVICE_FlyweightFactory)facade.getService(SERVICE_FlyweightFactory.XCF_TAG);
		String instructionType = attributes.getValue(XCFProcessingInstruction.INSTRUCTION);
		String instructionName = attributes.getValue(XCFStrings.NAME);
		
		// if instructionType is not provided, default to a composite instruction
		if (instructionType == null) {
			handleError("<do ...> must have an instruction attribute: e.g., <do instruction=\"command\" name=\"login\">");
			return;
		}
		
		// instructionName is required
		if (instructionName == null) {
			handleError("<do ...> must have a name attribute: e.g., <do instruction=\"command\" name=\"login\">");
			return;
		}
		
		// get the instruction container
		INSTRUCTION_Composite container = null;
		try {
			container = (INSTRUCTION_Composite)getHandler().peekValue(XCFProcessingInstruction.XCF_TAG);
			if (container == null) {
				handleError("<do instruction="+instructionType + " name=" + instructionName + "> failed because there was no container.");
				return;
			}
		} catch (ClassCastException e) {
			handleError("<do instruction="+instructionType + " name=" + instructionName + "> failed because the container was not an INSTRUCTION_Composite.");
			return;
		}
		
		try {
			// create the instruction using the flyweight factory.  Normally we use instructionType as the flyweightTag and "instruction"
			// as the flyweight type.  Check if it is a command, where we use "command" as the flyweightType and instructionName as the
			// flyweight tag.
			String flyweightType = XCFProcessingInstruction.INSTRUCTION;
			boolean isSingleton = false;
			if (instructionType.equals(XCFProcessingInstruction.COMMAND)) {
				flyweightType = XCFProcessingInstruction.COMMAND;
				instructionType = instructionName;
			}
			XCFProcessingInstruction instruction = (XCFProcessingInstruction)flyweightFactory.getInstance(flyweightType, instructionType, isSingleton);
			instruction.setName(instructionName);

			// make this object the new container and add it to the old one
			getHandler().pushValue(XCFProcessingInstruction.XCF_TAG, instruction);
			container.addInstruction(instruction);	
			instruction.init(facade);
			facade.logDebug("| ---   BEGIN " + instruction.getName() + "(" + instruction.getClass().getName() +")");
		} catch (XCFException e) {
			handleException(e);
		}
	}

	/**
	 * Finishes up interpreting by popping the instructions from the handler
	 */
	public void endInterpreting(String uri, String name, String qName) {
		XCFFacade facade = getHandler().getFacade();
		XCFProcessingInstruction instruction = (XCFProcessingInstruction)getHandler().popValue(XCFProcessingInstruction.XCF_TAG);
		if (instruction == null) return;
		
		facade.logDebug("| ---   END " + instruction.getName() + "(" + instruction.getClass().getName() +")");
	}
}
