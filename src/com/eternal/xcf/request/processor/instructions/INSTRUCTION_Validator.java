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

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.request.parameter.XCFValidator;

/**
 * This package level class is used to contain an XCFValidator.  Using this class as a container
 * allows for easier code migration from XCF 5.
 * @author sonjaya
 *
 */
final class INSTRUCTION_Validator extends INSTRUCTION_Base {

	private final XCFValidator validator;
	
	/**
	 * Invoke the super constructor.  Name is the name of the 
	 * request parameter.  The validator is what is used to 
	 * process the parameter.
	 * @param name
	 * @param validator
	 */
	INSTRUCTION_Validator(String name, XCFValidator validator) {
		super(name);
		this.validator = validator;
	}
	
	/**
	 * Process the parameter by invoking the validators validate
	 * method.  Return the results.
	 */
	public boolean process(XCFRequest request) throws XCFException {
		return validator.validate(request, this);
	}

}
