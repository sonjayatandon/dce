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
package com.eternal.xcf.core.loggers;

import com.eternal.xcf.core.XCFContext;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFLogger;

/**
 * An empty logger used to disable logging types.
 * Creation date: (11/21/2001 11:49:42 AM)
 */
public final class LOGGER_Null implements XCFLogger {
	private static LOGGER_Null _singleton = new LOGGER_Null();
	/**
	 * Return the singleton instance of the logger
	 * @return
	 */
	public static LOGGER_Null s() {return _singleton;}
	
	/**
	 * NullLogger constructor comment.
	 */
	public LOGGER_Null() {
		super();
	}
	
	/**
	 * No-ops the logException call
	 * Creation date: (11/21/2001 2:00:01 PM)
	 * @param message java.lang.String
	 */
	public void logException(XCFContext context, LogTypes logType, java.lang.Exception e) {}
	
	/**
	 * No-ops the logMessage call
	 * Creation date: (11/21/2001 11:49:42 AM)
	 * @param message java.lang.String
	 */
	public void logMessage(XCFContext context, LogTypes logType, String message) {}
	
	/**
	 * Nothing needed
	 */
	public void close() throws XCFException {
	}

	
	/**
	 * Nothng needed
	 */
	public void open() throws XCFException {
	}

	/**
	 * Return the console's name for the toString
	 * Creation date: (11/21/2001 3:06:58 PM)
	 * @return java.lang.String
	 */
	public String toString() {
		return LOG_NONE;
	}
}
