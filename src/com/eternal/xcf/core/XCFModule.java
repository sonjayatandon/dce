/*
 * Copyright 2003 Dataskill, Inc.
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
package com.eternal.xcf.core;


/**
 * Abstract interface for request handlers.  The function of the module is to 
 * handle requests sent to the facade.  The facade will map the request to the appropriate module
 * and that module, in turn, will forward the request the the appropriate object or method intended to handle
 * that request.<br>
 * System events and end user functionality will typically map to requests.
 * @author sonjaya
 *
 */
public interface XCFModule {
	public String XCF_TAG = "xcf-module";
	
	/**
	 * Sets the facade that this module is part of
	 * @param server the module's server
	 */
	void setFacade(XCFFacade facade);
	
	/**
	 * Sets the name
	 * @param name The name to set
	 */
	void setName(String name);
	
	/**
	 * Returns the modules name
	 * @return
	 */
	String getName();

	/**
	 * Gets the defaultOperation
	 * @return Returns a String
	 */
	String getDefaultOperation();
	
	/**
	 * Sets the defaultOperation
	 * @param defaultOperation The defaultOperation to set
	 */
	void setDefaultOperation(String defaultOperation);

	/**
	 * Execute the request by using the information in req
	 * to lookup the appropriate request handler.
	 * @param req
	 * @throws XCFException
	 */
	void process(XCFRequest req) throws XCFException;
}

