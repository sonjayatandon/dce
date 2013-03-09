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

import java.util.HashMap;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.request.processor.XCFProcessingInstruction;

/**
 * This is a base utility class for request processing instructions.  Though it is not required to 
 * extend this class, most classes should as it provides an implementation for basic parameter processing functionality.
 * 
 * @author sonjaya
 *
 */
public abstract class INSTRUCTION_Base implements XCFProcessingInstruction {
	private String name;
	private XCFProcessingInstruction container = null;
	private HashMap<String, Object> properties = new HashMap<String, Object>();

	/**
	 * This constructor is used by processing instructions, like commands, that do not
	 * require a name.
	 *
	 */
	public INSTRUCTION_Base() {
	}
	
	/**
	 * This constructor is used by those processing instructions, such as parameter processors, that do 
	 * require a name.
	 * 
	 * @param name
	 */
	protected INSTRUCTION_Base(String name) {
		this.name = name;
	}

	/**
	 * Sets this instruction's container
	 * @param container
	 */	
	public XCFProcessingInstruction getContainer() {
		return container;
	}

	/**
	 * Gets this instructions container.
	 * @return
	 */
	public void setContainer(XCFProcessingInstruction container) {
		this.container = container;
	}

	/**
	 * Returns the instructions name
	 */
	public final String getName() {
		return name;
	}
	
	/**
	 * Sets this instructions name
	 */
	public final void setName(String name) {
		this.name = name;
	}

	/**
	 * Returns the object associated with the propertyName.  If this instance does not contain
	 * the property, then returns the getProperty of the container (if a container exists).
	 * @param propertyName
	 * @return
	 */
	public Object getProperty(String propertyName) {
		Object propertyValue = properties.get(propertyName);
		if (propertyValue == null && container != null) {
			propertyValue = container.getProperty(propertyName);
		}
		return propertyValue;
	}

	/**
	 * This is used to set properties, such as range or address.
	 * @param propertyName
	 * @param propertyValue
	 */
	public void setProperty(String propertyName, Object propertyValue) {
		properties.put(propertyName, propertyValue);
	}
	
	/**
	 * Since this method is optional, there is a default null implementation
	 */
	public void init(XCFFacade facade) throws XCFException {
		
	}

}
