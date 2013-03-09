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
package com.eternal.xcf.core.response;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;

/**
 * A set of static convinience methods used to aid processing of responses
 * @author sonjaya
 *
 */
public class UTIL_Helper {
	static final String ADDR_RESULT = "request.result-code";
	
	/**
	 * Sets a result in the request context
	 * @param request
	 * @param resultCode
	 * @throws XCFException
	 */
	public static void setResult(XCFRequest request, String resultCode) throws XCFException {
		request.getContext().putValue(ADDR_RESULT, resultCode);
	}
	
	/**
	 * Gets a result in the request context.  If there is no result there,
	 * return "success" as the result.
	 * @param request
	 * @return
	 * @throws XCFException
	 */
	public static String getResult(XCFRequest request) throws XCFException {
		String result = (String)request.getContext().getValue(ADDR_RESULT);
		
		if (result == null) {
			// assume if a result wasn't set, things were successful
			return XCFResponse.SUCCESS;
		}
		
		return result;
	}
	
	
	/**
	 * Sets the output in the request context.
	 * @param req
	 * @param outputId
	 * @throws XCFException
	 */
	public static void setResponse(XCFRequest req, String responseId) throws XCFException {
		req.getContext().putValue(XCFResponse.XCF_RESPONSE_ID, responseId);
	}
	
	/**
	 * Sets the current response type.
	 * @param req
	 * @param responseType
	 * @throws XCFException
	 */
	public static void setResponseType(XCFRequest req, String clientType) throws XCFException {
		req.getContext().putValue(XCFResponse.XCF_RESPONSE_TYPE, clientType);
	}

}
