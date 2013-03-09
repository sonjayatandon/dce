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
package com.eternal.xcf.request.processor;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFRequest;

/**
 * This is the interface implemented by classes used to process requests.  There several type of processing instructions.
 * The most common types are:
 *   Commands (implemented by INSTRUCTION_Command) -- these objects are the primary handlers for requests
 *   Validators (implemented by INSTRUCTION_Validator and XCFValidator) -- these objects are used to validate
 *   	request parameters.
 *   Setters (implemented by INSTRUCTION_Setter and XCFSetter) -- these objects are used to transform request
 *   	parameter values to native objects in the context.
 *   Parameter Processor (implemented by INSTRUCTION_ParameterProcessor) -- these objects act as containers for
 *      validator and setter objects.  There is typically one of these for each request parameter processed.
 *   Composite (implemented by INSTRUCTION_Composite) -- these objects act as containers for other processing instructions.
 *   	There is typcially one of these object bound to a module operation.  This object will typically contain one or more
 *   	child insructions.  
 *   
 * @author Sonjaya Tandon
 *
 */
public interface XCFProcessingInstruction {
	public static final String XCF_TAG = "container";
	public static final String INSTRUCTION = "instruction";
	public static final String COMMAND = "command";
	public static final String VALIDATOR = "validator";
	public static final String VALIDATE = "validate";
	
	/**
	 * Gets the processing instructions name
	 * @return
	 */
	public String getName();

	/**
	 * Sets this instructions name
	 * @param instructionName
	 */
	public void setName(String instructionName);

	/**
	 * This is used to set properties, such as range or address.
	 * @param propertyName
	 * @param propertyValue
	 */
	public void setProperty(String propertyName, Object propertyValue);
	
	/**
	 * Optionally used to initialize the instruction object instance
	 * @param facade
	 */
	public void init(XCFFacade facade) throws XCFException;
	
	/**
	 * Returns the object associated with the propertyName.  If this instance does not contain
	 * the property, then returns the getProperty of the container (if a container exists).
	 * @param propertyName
	 * @return
	 */
	public Object getProperty(String propertyName);

	/**
	 * Sets this instruction's container
	 * @param container
	 */
	public void setContainer(XCFProcessingInstruction container);
	
	/**
	 * Gets this instructions container.
	 * @return
	 */
	public XCFProcessingInstruction getContainer();
	
	/**
	 * Process an instruction using the request as input
	 * @param request
	 * @return true if processing should continue, false if it should stop
	 * @throws XCFException
	 */
	boolean process(XCFRequest request) throws XCFException;

}
