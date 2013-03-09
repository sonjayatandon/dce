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

import com.eternal.xcf.common.request.processor.MODULE_Instructions;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFStrings;
import com.eternal.xcf.request.processor.XCFProcessingInstruction;
import com.eternal.xcf.request.processor.instructions.INSTRUCTION_Composite;
import com.eternal.xcf.request.processor.instructions.INSTRUCTION_Operation;
import com.eternal.xcf.sax.SAXEL_Composite;

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
public class SAXEL_Operation extends SAXEL_Composite {
	public static final String XCF_TAG = XCFStrings.OPERATION;
	
	public SAXEL_Operation() {
		super(XCF_TAG);
	}

	/**
	 * Creates an INSTRUCTION_Composite and adds it to the handler as an instruction container
	 */
	public void startInterpreting(String uri, String name, String rawName, Attributes attributes) {
		XCFFacade facade = getHandler().getFacade();

		// grab the operation name
		String operationName = attributes.getValue(XCFStrings.NAME);
		
		// it's required
		if (operationName == null) {
			handleError("<operation ...> must have a name attribute: e.g., <operation name=\"login\">");
			return;
		}
		
		// make sure we have a module
		MODULE_Instructions module = (MODULE_Instructions)getHandler().get(XCFStrings.MODULE); 
		if (module == null) return;
		
		// create the composite instruction and push it as the instruction container
		// for any child tags
		INSTRUCTION_Operation operation = new INSTRUCTION_Operation(operationName);
		getHandler().pushValue(XCFProcessingInstruction.XCF_TAG, operation);
		
		facade.logDebug("|----" + " BEGIN OPERATION " + operationName + "----------");
	}

	/**
	 * Finish interpreting by grabbing the composite instruction off the handler
	 * and adding it to the module as an operation handler.
	 */
	public void endInterpreting(String uri, String name, String qName) {
		XCFFacade facade = getHandler().getFacade();
		MODULE_Instructions module = (MODULE_Instructions)getHandler().get(XCFStrings.MODULE); 
		if (module == null) return;
		INSTRUCTION_Composite operation = (INSTRUCTION_Composite)getHandler().popValue(XCFProcessingInstruction.XCF_TAG);
		if (operation == null) return;
		
		module.addOperationHandler(operation.getName(), operation);
		facade.logDebug("|----" + " END OPERATION " + operation.getName() + "----------");
	}
}
