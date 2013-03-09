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
package com.eternal.xcf.common.request.parameter;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.request.parameter.XCFValidator;
import com.eternal.xcf.request.processor.XCFProcessingInstruction;

/**
 * This validator checks for the presense of the request parameter.  It returns true if the parameter exists
 * and false otherwise.
 * @author sonjaya
 *
 */
public class VALIDATOR_Required implements XCFValidator {

	public boolean validate(XCFRequest req,	XCFProcessingInstruction parameterSpecification)
			throws XCFException {
		boolean exists = req.parameterExists(parameterSpecification.getName());
		
		if (!exists) {
			// TODO req.addNotice(-- message -- ) We will add this in a later lesson
		}
		return exists;
	}

}
