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

import java.util.Iterator;

/**
 * Abstract interface used to model requests sent to the facade for processing.  Concrete implementations
 * of this interface are typically technology stack dependant.  For example, an HTTPServletRequest based implementation
 * will adapt the HTTServletRequest interface to XCFRequest.  This allows XCF based classes to access the information in 
 * the HTTPServletRequest without being coupled to the web application technology stack.  Later, if the technology stack 
 * changes, the only code that needs changing is the code that instantiates the concrete request object (typically a 
 * framework level class).  
 * @author sonjaya
 *
 */
public interface XCFRequest 
{
	//allowing in XML for different processes to use different requests
	String XCF_TAG = "xcfrequest";
	
	/////////////////////////////////////////////////////////
	// COMMON METHODS
	//  You will use these ones a lot.
	/**
	 * Get a parameter value.  This method will throw an exception if 
	 * paramName is not in the request.  If paramName is an optional parameter,
	 * you should define a default setter in the parameter processing
	 * for this operation.
	 * @param paramName named used to lookup parameter value
	 * @return parameter value
	 * @exception XCFException thrown if paramName is not in the request
	 */
	String getParameter(String paramName) throws XCFException;

	/**
	 * Set a string parameter value using a string as a key
	 * @param paramName  named used to lookup parameter value
	 * @param paramValue value of the parameter
	 * @return
	 */
	void setParameter(String paramName, String paramValue);

	/**
	 * Returns the requests context
	 * @return
	 */
	XCFContext getContext();
	
	/**
	 * 
	 * @param paramName
	 * @return
	 */
	boolean parameterExists(String paramName);
	
	/////////////////////////////////////////////////////////
	// RARE METHODS
	//  These methods are for more advanced uses.  If you 
	//  find yourself using them often, consider refactoring
	//  as you are likely in danger of breaking cohesion 
	/**
	 * Returns an enumeration of parameter names 
	 */
	Iterator<String> getParameterNames();
	
	/**
	 * Sets the request's context.  This is typically set by the object
	 * that creates the concrete request object
	 * @param context
	 */
	void setContext(XCFContext context);	

	/**
	 * Sets the name of the module that should handle this request.  This is typically set by the object
	 * that creates the concrete request object.
	 * @param module
	 */
	void setModule(String module);
	
	/**
	 * Returns the name of the module that should handle this request.
	 * @return
	 */
	String getModule();
	
	/**
	 * Returns the module operation requested.
	 * @return
	 */
	String getOperation();
	
	/**
	 * Sets the module operation that should be performed. This is typically set by the object
	 * that creates the concrete request object.
	 * @param operation
	 */
	void setOperation(String operation);
	
	/**
	 * Adds a notice message to the request.  A notice message is typically an error message delivered
	 * back to the request invoker.  The error is usually with parameter processing, but could also be
	 * due to issues with the request handling.
	 * @param message
	 */
	void addNotice(String message);
	
	/**
	 * Clears all the notice messages.
	 *
	 */
	void clearNotice();
	
	/**
	 * Returns an iterator over the notice messages.
	 * @return
	 */
	Iterator<String> getNotices();

	/////////////////////////////////////////////////////////
	// DANGEROUS METHODS
	//  These methods are for the most advanced uses.  Using
	//  these methods BREAKS coupling.  The code that uses 
	//  these methods will be tightly coupled to a specific
	//  technology stack.  If the technology stack changes
	//  objects that use these methods will need to be 
	//  re-written or replaced.
	//
	//  So, the question is, why do we even have these methods.  Glad you asked!
	//  To leverage the underlying technology stack, you will need to create adaptive
	//  classes that map the technology stack specific methods to the technology
	//  stack neutral XCF interfaces.  It is likely that these adaptive classes
	//  will need to make use of the methods below to talk to each other.
	/**
	 * Sets the technology stack specific request object.  For example, this may be
	 * the HTTPServletRequest object.
	 * @param req
	 */
	void setNativeRequest(Object req);
	
	/**
	 * Gets the technology stack specific request object.
	 * @return
	 */
	Object getNativeRequest();

	/**
	 * Sets the technology stack specific response object.  For example, this may be
	 * the HTTPServletResponse object
	 * @param resp
	 */
	void setNativeResponse(Object resp);
	
	/**
	 * Gets the technology stack specific response object.
	 * @return
	 */
	Object getNativeResponse();

	/**
	 * Sets the native listener object.  This is the object that listens for requests and sends responses.
	 * For example, this could be the HTTPServlet.
	 * @param listener
	 */
	void setNativeListener(Object listener);
	
	/**
	 * Gets the native listener object.
	 * @return
	 */
	Object getNativeListener();
	
}

