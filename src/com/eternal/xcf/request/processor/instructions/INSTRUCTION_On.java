/*
 * Copyright 2007 Eternal Adventures, Inc.
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

import java.util.HashMap;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.core.response.UTIL_Helper;
import com.eternal.xcf.request.processor.XCFProcessingInstruction;

/**
 * This instruction checks the "result" value in the request context.
 * It will then execute the instruction associated with that value.
 * By default, if no "result" has been set, then the value will be 
 * "success".
 * @author sonjaya
 *
 */
public class INSTRUCTION_On extends INSTRUCTION_Composite {
	private HashMap<String, XCFProcessingInstruction> instructionsByResult = new HashMap<String, XCFProcessingInstruction>();
	private String result;

	////////////////////////////////////////////////
	// MEHTODS USED DURING BUILDING
	/**
	 * Sets the result currently being configured
	 */
	public void setCurrentResult(String result) {
		this.result = result;
	}

	/**
	 * Overide addInstruction by assoicating the instruction
	 * to the current result being configured.
	 */
	@Override
	public void addInstruction(XCFProcessingInstruction instruction) {
		assert result != null;
		INSTRUCTION_Composite resultInstruction = (INSTRUCTION_Composite)instructionsByResult.get(result);
		if (resultInstruction == null) {
			resultInstruction= new INSTRUCTION_Composite();
			resultInstruction.setContainer(this);
			instructionsByResult.put(result, resultInstruction);
		}
		resultInstruction.addInstruction(instruction);
	}	

	////////////////////////////////////////////////
	// MEHTODS USED INSTRUCTION PROCESSING
	/**
	 * Look up the result and process the instruction associated 
	 * with that result.
	 */
	public boolean process(XCFRequest request) throws XCFException {
		String result = UTIL_Helper.getResult(request);
		XCFProcessingInstruction instruction = instructionsByResult.get(result);
		if (instruction == null) {
			throw new XCFException("There is no processing instruction for result: " + result);
		}
		return instruction.process(request);
	}
	
}
