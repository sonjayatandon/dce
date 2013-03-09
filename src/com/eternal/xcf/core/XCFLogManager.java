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

import java.util.HashMap;
import java.util.Iterator;

import com.eternal.xcf.core.loggers.LOGGER_Console;
import com.eternal.xcf.core.loggers.LOGGER_Null;

/**
 * Log manager class that provides access to all facade loggers.  You will not typically call this class directly
 * Instead, you will usually use the helper methods in XCFFacade.
 * Creation date: (11/21/2001 10:53:27 AM)
 */
public final class XCFLogManager {
	private HashMap<String,XCFLogger> registeredLoggers = new HashMap<String,XCFLogger>();
	private HashMap<XCFLogger.LogTypes, XCFLogger> activeLoggers = new HashMap<XCFLogger.LogTypes, XCFLogger>();

	/**
	 * LogManager constructor comment.
	 */
	public XCFLogManager() {
		super();
		
		// register the two default loggers: none and console
		try {
			registerLogger(XCFLogger.LOG_NONE, LOGGER_Null.s());
			registerLogger(XCFLogger.LOG_TO_CONSOLE, LOGGER_Console.s());
		} catch (XCFException e) {/*these won't fail because they have empty open methods*/}
		
		// default ERROR and INFO to log to the console, turn off the others
		activeLoggers.put(XCFLogger.LogTypes.ERROR, LOGGER_Console.s());
		activeLoggers.put(XCFLogger.LogTypes.FATAL, LOGGER_Console.s());
		activeLoggers.put(XCFLogger.LogTypes.INFO, LOGGER_Console.s());
		activeLoggers.put(XCFLogger.LogTypes.REPORT, LOGGER_Null.s());
		activeLoggers.put(XCFLogger.LogTypes.TRACE, LOGGER_Null.s());
		activeLoggers.put(XCFLogger.LogTypes.DEBUG, LOGGER_Null.s());
		activeLoggers.put(XCFLogger.LogTypes.WARN, LOGGER_Null.s());
	}
	
	/**
	 * Registers a named logger to the manager.  All loggers must be registered prior to being used.
	 * @param loggerName
	 * @param logger
	 * @throws XCFException
	 */
	public void registerLogger(String loggerName, XCFLogger logger) throws XCFException {
		logger.open();
		registeredLoggers.put(loggerName, logger);
	}
	
	/**
	 * Returns the logger currently bound to a logging type.  Should never return null.
	 * Creation date: (11/21/2001 2:04:20 PM)
	 * @return XCFLogger
	 * @param loggerName java.lang.String
	 */
	public XCFLogger getLogger(XCFLogger.LogTypes logType) {
		return activeLoggers.get(logType);
	}
	
	/**
	 * Returns a logger registered using the name passed in.  Throws an exception if the logger doesn't exist.
	 * Creation date: (11/21/2001 2:04:20 PM)
	 * @param loggerName java.lang.String
	 * @return XCFLogger
	 * @throws XCFException if loggerName not registered.
	 */
	public XCFLogger getLogger(String loggerName) throws XCFException {
		XCFLogger logger = registeredLoggers.get(loggerName);
		if (logger == null) throw new XCFException(loggerName + " is not a registered logger.");
		return registeredLoggers.get(loggerName);
	}
	
	/**
	 * Binds a logger to a log type
	 * Creation date: (11/21/2001 11:55:31 AM)
	 * @param logType XCFLogger.LogTypes
	 * @param log XCFLogger
	 */
	public void setLogger(XCFLogger.LogTypes logType, XCFLogger log) {
		if (log != null) {
			activeLoggers.put(logType, log);
		}
	}
	
	/**
	 * Binds the logger referenced by loggerName to the logType.
	 * @param logType
	 * @param loggerName
	 * @throws XCFException
	 */
	public void setLogger(XCFLogger.LogTypes logType, String loggerName) throws XCFException {
		XCFLogger logger = getLogger(loggerName);
		setLogger(logType, logger);
	}
	
	/**
	 * Returns true if logType is enabled for logging
	 * @param logType
	 * @return
	 */
	public boolean isLoggerEnabled(XCFLogger.LogTypes logType) {
		XCFLogger logger = activeLoggers.get(logType);
		return logger != LOGGER_Null.s();
	}
	
	/**
	 * Logs the exception.
	 * Creation date: (11/21/2001 11:53:03 AM)
	 * @param logType int
	 * @param message java.lang.String
	 */
	public void log(XCFContext context, XCFLogger.LogTypes logType, Exception e) {
		activeLoggers.get(logType).logException(context, logType, e);
	}
	
	/**
	 * Logs the message.
	 * Creation date: (11/21/2001 11:53:03 AM)
	 * @param logType int
	 * @param message java.lang.String
	 */
	public void log(XCFContext context, XCFLogger.LogTypes logType, String message) {
		activeLoggers.get(logType).logMessage(context, logType, message);
	}
	
	/**
	 * Closes all registered loggers.
	 *
	 */
	public void stop() {
		Iterator<XCFLogger> iter =  registeredLoggers.values().iterator();
		while (iter.hasNext()) {
			XCFLogger logger = iter.next();
			try {
				logger.close();
			} catch  (XCFException e) {
				System.out.println("Unable to close: " + logger);
				e.printStackTrace();
			}
		}
	}
}
