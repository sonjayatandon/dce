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

import com.eternal.xcf.request.parameter.XCFSetter;
import com.eternal.xcf.request.parameter.XCFValidator;
import com.eternal.xcf.request.processor.XCFProcessingInstruction;

/**
 * This class is a special purpose composite class.  It is intended to be used as
 * a container for instructions that process a single request parameter.  It contains
 * two types of instructions: setters and validators.
 * 
 * It's name is the name of the parameter it is processing.
 * @author sonjaya
 *
 */
public final class INSTRUCTION_ProcessParameter extends INSTRUCTION_Composite {

	public INSTRUCTION_ProcessParameter() {
		super();
	}
	
	/**
	 * Invoke the parent constructor.  Name should be the parameterName of the 
	 * request parameter this instruction processes.
	 * @param name
	 */
	public INSTRUCTION_ProcessParameter(String name) {
		super(name);
	}
	
	/**
	 * Adds a setter child instruction.
	 * @param setter
	 * @return
	 */
	public XCFProcessingInstruction addSetter(XCFSetter setter) {
		XCFProcessingInstruction instruction = new INSTRUCTION_Setter(getName(), setter);
		addInstruction(instruction);
		return instruction;
	}

	/**
	 * Adds a validator child instruction
	 * @param validator
	 * @return
	 */
	public XCFProcessingInstruction addValidator(XCFValidator validator) {
		XCFProcessingInstruction instruction = new INSTRUCTION_Validator(getName(), validator);
		addInstruction(instruction);
		return instruction;
	}
}
