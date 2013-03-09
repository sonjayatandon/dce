/*
 * Copyright © 2004 Eternal Adventures, Inc.
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
 * Renders a specific response for a response type.
 * For example, there may be an response id called sec-dsp-home.
 * If the server supported three different clients (cell, browser, and 2D)
 * then there would be three XCFResponse objects defined for sec-dsp-home --
 * one for each client.
 * @author Sonjaya Tandon
 */
public interface XCFResponse {
	public static final String XCF_TAG = "response";

	public static String XCF_RESPONSE_TYPE = "request.xcf-client-type";
	public static String XCF_RESPONSE_ID = "request.xcf-response-id";
	
	public static String SUCCESS = "success";
	public static String FAILURE = "failure";
	
	
	/**
	 * Returns the response name.  
	 * @return
	 */
	String getName();

	/**
	 * Sets the response's name
	 */
	void setName(String responseId);
	
	/**
	 * Returns the property value associated with propertyName
	 * @param propertyName name of property
	 */
	Object getProperty(String propertyName);
	
	/**
	 * Sets a property value for the output.  For example, a typical property
	 * is the device the render uses to render the output (e.g. name
	 * of jsp file).
	 * @param propertyName the property name
	 * @param value the value stored in the property
	 */
	void setProperty(String propertyName, Object value);
	
	/**
	 * Generates the specifed response.  It is expected that the 
	 * nativeRequestInfo array contains the device used to render
	 * the output.
	 * @param req the request whose results need to be rendered
	 * @param nativeRequestInfo information from the native request which may be
	 * needed to render the output.
	 */
	public void render(XCFRequest req, Object[] nativeRequestInfo) throws XCFException;
}
