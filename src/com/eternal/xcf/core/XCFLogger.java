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
 * XCF Logger interface.  This interface provides a higher lever
 * abstraction over the basic logging functionality.  As a result,
 * loggers can be attributed with business functionality, but still kept
 * decoupled from other components.
 * <br>
 * In general, you should not access this interface directly.  The most common way
 * of accessing logging functionality is via the helper methods in XCFFacade
 * 
 * Creation date: (11/21/2001 11:25:58 AM)
 */
public interface XCFLogger {
	enum LogTypes  {TRACE, DEBUG, INFO, WARN, ERROR, FATAL, REPORT};

	static final String LOG_AS_MESSAGE = "message";
	static final String LOG_TO_CONSOLE = "console";
	static final String LOG_NONE = "none";
	static final String LOG_TO_FILE = "file";
	
	String LOG_PAD = ":::";

	/**
	 * Opens the logger
	 * @throws XCFException
	 */
	public void open() throws XCFException;
	
	/**
	 * Closes the logger
	 * @throws XCFException
	 */
	public void close() throws XCFException;
	
	/**
	 * Log the exception
	 * @param context concrete loggers can use this to extract additional info.  NOTE: context may be null
	 * @param logType 
	 * @param e
	 */
	void logException(XCFContext context, LogTypes logType, Exception e);

	/**
	 * Log the message
	 * @param context
	 * @param logType
	 * @param message
	 */
	void logMessage(XCFContext context, LogTypes logType, String message);
}
