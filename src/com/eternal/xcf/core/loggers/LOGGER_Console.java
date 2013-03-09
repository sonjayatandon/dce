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
 * This logger simply logs to Sytem.out
 * Creation date: (11/26/2001 1:40:54 PM)
 */
public class LOGGER_Console implements XCFLogger {
	
	private static LOGGER_Console _singleton = new LOGGER_Console();

	/**
	 * Return the singleton instance of the logger
	 * @return
	 */
	public static LOGGER_Console s() {return _singleton;}
	
	/**
	 * Constructs a console logger.
	 */
	public LOGGER_Console() {
		super();
	}
	
	/**
	 * Closes the logger.  In this cases, there is nothing for us to do.
	 */
	public void close() throws XCFException {
	}

	/**
	 * Opens the logger.  In this case, there is nothing for us to do.
	 */
	public void open() throws XCFException {
	}

	/**
	 * Log the exception
	 * @param context concrete loggers can use this to extract additional info.  NOTE: context may be null
	 * @param logType 
	 * @param e
	 */
	public void logException(XCFContext context, LogTypes logType, Exception e) {
		e.printStackTrace();
	}
	
	/**
	 * Log the message.  Prepend the message with the logtype and thread id.
	 * @param context
	 * @param logType
	 * @param message
	 */
	public void logMessage(XCFContext context, LogTypes logType, String message) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(logType);
	    buffer.append(LOG_PAD);
	    buffer.append(getThreadId());
	    buffer.append(LOG_PAD);
	   	buffer.append(message);
	   	System.out.println(buffer.toString());
	}

	/**
	 * Get the thread name and return.
	 * @return
	 */
	protected String getThreadId()
	{
		String threadName = Thread.currentThread().getName();
	
	    if (threadName == null || threadName.length() == 0)
	    {
	        threadName = "THREAD_UNKNOWN";
	    }
	    
		return threadName;	
	}
	
	/**
	 * Return the console's name for the toString
	 * Creation date: (11/26/2001 1:45:47 PM)
	 * @return java.lang.String
	 */
	public String toString() {
		return LOG_TO_CONSOLE;
	}
	
}
