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
 * Abstract interface for shared singleton objects.  An XCFService is typically an object used by
 * multiple request handlers (and sometimes even other XCFService objects).  The singleton instance is typically
 * created when the facade is built; it may contain mulitple threads; and it is typically terminiated when 
 * the facade is shut down.  An object that provides abstracted access to database connections is an example.
 * <br />
 * Handlers typicallly access services in request handlers as follows:
 * <br />&nbsp;&nbsp;[ConcreteServiceType]service = [ConcreteServiceType]request.getContext().getFacade().getService([ConcreteServiceType].XCF_TAG);
 * @author sonjaya
 *
 */
public interface XCFService {
	
	/**
	 * Starts the service object
	 * @throws XCFException
	 */
	void start() throws XCFException;
	
	/**
	 * Stops the service object
	 * @throws XCFException
	 */
	void stop() throws XCFException;
	
	/**
	 * Returns the service object's name
	 * @return
	 */
	String getName();
	
	/**
	 * Sets the service object's name.  This method is typically called by the object that instantiated the service.
	 * @param name
	 */
	void setName(String name);
	
	/**
	 * Sets the facade that this service is part of
	 * @param facade the service's facade
	 */
	void setFacade(XCFFacade facade);
}

