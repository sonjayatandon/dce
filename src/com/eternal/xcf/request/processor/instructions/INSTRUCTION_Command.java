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
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.core.response.UTIL_Helper;
import com.eternal.xcf.core.response.XCFResponse;

/**
 * This is a base utility class for by command objects.  Processing instructions which simply want to 'execute'
 * and never interupt processing should extend this class.
 * 
 * This class is also provided as a bridge to XCF5 to ease code migration.
 * 
 * @author sonjaya
 *
 */
public abstract class INSTRUCTION_Command extends INSTRUCTION_Composite {
	
	public INSTRUCTION_Command() {
		super("");
	}

	/**
	 * Child classes implment this method to provide request handling logic.
	 * @param request
	 * @throws XCFException
	 */
	public abstract void execute(XCFRequest request) throws XCFException;
	
	/**
	 * Process the request by calling the execute method.  Never interupt
	 * processing by returning true.
	 */
	public final boolean process(XCFRequest request) throws XCFException {
		XCFFacade facade = request.getContext().getFacade();
		facade.logDebug("Executing: " + getClass().getName());
		if (super.process(request)) {
			execute(request);
			if (UTIL_Helper.getResult(request).equals(XCFResponse.FAILURE)) return false;
			return true;
		} else {
			return false;
		}
	}

}
