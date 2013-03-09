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

import com.eternal.xcf.core.XCFStrings;
import com.eternal.xcf.request.processor.XCFProcessingInstruction;
import com.eternal.xcf.request.processor.instructions.INSTRUCTION_Base;
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
public final class SAXEL_Property extends SAXEL_Composite {
	public static final String XCF_TAG = XCFProcessingInstruction.VALIDATE;
	
	public SAXEL_Property() {
		super(XCF_TAG);
	}

	/**
	 * Make sure the container is a parameter processor
	 * Create the validator and add it to the parameter processor
	 */
	public void startInterpreting(String uri, String name, String rawName, Attributes attributes) {
		// grab the property name
		String propertyName = attributes.getValue(XCFStrings.NAME);
		
		// it's required
		if (propertyName == null) {
			handleError("<property ...> must have a name attribute: e.g., <property name=\"db-info\">");
			return;
		}
		
		// get the instruction container
		INSTRUCTION_Base container = null;
		try {
			container = (INSTRUCTION_Base)getHandler().peekValue(XCFProcessingInstruction.XCF_TAG);
			if (container == null) {
				handleError("<property name="+ propertyName + "> failed because there was no container.");
				return;
			}
		} catch (ClassCastException e) {
			handleError("<property name="+propertyName + "> failed because the container was not an INSTRUCTION_Base.");
			return;
		}	

		// set its property to an empty string
		container.setProperty(propertyName, new StringBuffer());
		
		// save the property name
		getHandler().put(getTag(), propertyName);
	}
	
	/**
	 * Assumes the characters represent the literal value of the expression.
	 * Creation date: (9/6/2001 5:57:05 PM)
	 * @param ch char[]
	 * @param offset int
	 * @param length int
	 */
	public final void handleCharacters(char[] ch, int offset, int length) {
		StringBuffer contents = getContents();
		if (contents == null) return;
		
		if (length != 0) {
			contents.append(ch, offset, length);
		}
	}
	
	final StringBuffer getContents() {
		// get the instruction container
		INSTRUCTION_Base container = null;
		try {
			container = (INSTRUCTION_Base)getHandler().peekValue(XCFProcessingInstruction.XCF_TAG);
			if (container == null) {
				return null;
			}
		} catch (ClassCastException e) {
			return null;
		}	
		
		String propertyName = (String)getHandler().get(getTag());
		if (propertyName == null) return null;
		
		StringBuffer contents = (StringBuffer)container.getProperty(propertyName);

		return contents;
	}
	
	/**
	 * Puts the literal onto the context.  Uses it's tag as the key.
	 * Creation date: (9/6/2001 5:47:32 PM)
	 * @param uri java.lang.String
	 * @param name java.lang.String
	 * @param rawName java.lang.String
	 * @param attributes org.xml.sax.Attributes
	 */
	public final void endInterpreting(String uri, String name, String qName) {
		StringBuffer contents = getContents();
		String propertyName = (String)getHandler().consume(getTag());
		if (contents != null) {
			INSTRUCTION_Base container = (INSTRUCTION_Base)getHandler().peekValue(XCFProcessingInstruction.XCF_TAG);
			container.setProperty(propertyName, contents.toString().trim());
		}
	}
}
