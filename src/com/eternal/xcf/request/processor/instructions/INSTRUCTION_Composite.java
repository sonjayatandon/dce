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
package com.eternal.xcf.request.processor.instructions;

import java.util.ArrayList;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.core.response.UTIL_Helper;
import com.eternal.xcf.core.response.XCFResponse;
import com.eternal.xcf.request.processor.XCFProcessingInstruction;

/**
 * This class is used to hold multiple instructions.  Invoking process on an instance of this object will 
 * cause process to be invoked on all the child instructions.
 * @author sonjaya
 *
 */
public class INSTRUCTION_Composite extends INSTRUCTION_Base {
	protected ArrayList<XCFProcessingInstruction> instructions = new ArrayList<XCFProcessingInstruction>();

	public INSTRUCTION_Composite() {
		super();
	}

	public INSTRUCTION_Composite(String name) {
		super(name);
	}
	
	/**
	 * Process this request by processing all the child instructions.
	 * Stop as soon as one returns false (and return false) or return
	 * true if all instructions process.
	 * 
	 * TODO: Add a switch that allows composite to process all instructions
	 * even when one fails.
	 */
	public boolean process(XCFRequest request) throws XCFException {
		XCFFacade facade = request.getContext().getFacade();
		for (XCFProcessingInstruction instruction : instructions) {
			facade.logDebug("Processing: " + instruction.getClass().getName() + "::"+instruction.getName());
			if (instruction.process(request) == false) {
				// only set response to failure if it is currently at success
				// we don't want to overwrite any other failure code.
				if (UTIL_Helper.getResult(request).equals(XCFResponse.SUCCESS)) {
					UTIL_Helper.setResult(request, XCFResponse.FAILURE);
				}
				return false;	
			}
		}
		return true;
	}
	
	/**
	 * Add a child instruction to the composite.
	 * @param instruction
	 */
	public void addInstruction(XCFProcessingInstruction instruction) {
		instruction.setContainer(this);
		instructions.add(instruction);
	}

}
