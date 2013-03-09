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

public interface XCFContext {
		
	/**
	 * Puts a value in the dimension at the address specified.  This method will overrite
	 * any value already in the dimension.
	 * @param address identifies a dimension
	 * @param value an object to store in the address
	 */
	public void putValue(String address, Object value) throws XCFException;

	/**
	 * Returns the value stored in the dimension referenced by address.
	 * Creation date: (10/8/2001 2:27:11 PM)
	 * @return a value stored in a dimension.
	 * @param address java.lang.String an address of a dimension.
	 */
	public Object getValue(String address) throws XCFException;
	
	/**
	 * Gets the system facade.  The facade provides a decoupled interface
	 * to the rest of the system.
	 * @return
	 * @throws XCFException
	 */
	public XCFFacade getFacade() throws XCFException;
	
	/**
	 * Setter for the facade.  This should be set when the context object is created.
	 * @param facade
	 */
	public void setFacade(XCFFacade facade);

}


