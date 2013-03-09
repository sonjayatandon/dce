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
package com.tandon.dce.characterclass.builder;

import org.xml.sax.Attributes;

import com.eternal.xcf.sax.SAXEL_Composite;
import com.tandon.dce.characterclass.MODEL_Class;

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
public class SAXEL_Attribute extends SAXEL_Composite {
	public static final String XCF_TAG = "stats";
	
	public SAXEL_Attribute() {
		super(XCF_TAG);
	}

	/**
	 * Creates an INSTRUCTION_Composite and adds it to the handler as an instruction container
	 */
	public void startInterpreting(String uri, String name, String rawName, Attributes attributes) {
		MODEL_Class style = (MODEL_Class)getHandler().get("style"); 
		if (style == null) return;
		
		String attributeId = attributes.getValue("id");
		String attributeName = attributes.getValue("name");
		String attributeValue = attributes.getValue("value");
		
		if (attributeValue == null) {
			handleError("<attribute ...> must have an id attribute: e.g., <character id=\"1\" id=\"Hit\">");
			return;
		}
		if (attributeName == null) {
			handleError("<attribute ...> must have a name attribute: e.g., <character id=\"1\" id=\"Hit\">");
			return;
		}
		
		int value = 0;
		try {
			value = Integer.parseInt(attributeValue);
		} catch (NumberFormatException e) {
			handleError("<attribute ...> must have an INTEGER id attribute: e.g., <character id=\"1\" id=\"Hit\">");
			return;
		}
		
		style.addAttribute(attributeId, attributeName, value);
	}
}
