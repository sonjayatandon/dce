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

import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.sax.SAXEL_Composite;
import com.tandon.dce.characterclass.MODEL_Template;
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
public class SAXEL_Template extends SAXEL_Composite {
	public static final String XCF_TAG = "posture";
	
	public SAXEL_Template() {
		super(XCF_TAG);
	}

	/**
	 * Creates an INSTRUCTION_Composite and adds it to the handler as an instruction container
	 */
	public void startInterpreting(String uri, String name, String rawName, Attributes attributes) {
		XCFFacade facade = getHandler().getFacade();

		// make sure we have a card
		MODEL_Class style = (MODEL_Class)getHandler().get("style"); 
		if (style == null) return;
		String postureName = attributes.getValue("name");
		if (postureName == null) {
			handleError("<posture ...> must have a name attribute: e.g., <style id=\"1\" id=\"Hit\">");
			return;
		}
		facade.logDebug("|----" + " BEGIN POSTURE FOR " + style.getName() + "." + postureName + " ----------");

		MODEL_Template posture = new MODEL_Template(facade, postureName);

		// put it on the handler
		getHandler().put(XCF_TAG, posture);	
		style.addTemplate(posture);
		
	}

	/**
	 * Finish interpreting by grabbing the composite instruction off the handler
	 * and adding it to the module as an operation handler.
	 */
	public void endInterpreting(String uri, String name, String qName) {
		XCFFacade facade = getHandler().getFacade();
		MODEL_Class style = (MODEL_Class)getHandler().get("style"); 
		MODEL_Template posture = (MODEL_Template)getHandler().consume(XCF_TAG); 
		if (style == null) return;

		facade.logDebug("|----" + " BEGIN POSTURE FOR " + style.getName() + "." + posture.getName() + " ----------");
	}
}
