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

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.core.response.UTIL_Helper;

/**
 * This instruction sets the response id in the request context.
 * The instruction was configured such that it's name is the
 * desired response id.
 * @author sonjaya
 *
 */
public final class INSTRUCTION_SelectResponse extends INSTRUCTION_Base {

	/**
	 * Process the request by simply setting the response id to 
	 * this instructions name
	 */
	public boolean process(XCFRequest request) throws XCFException {
		UTIL_Helper.setResponse(request, getName());
		return true;
	}
}
