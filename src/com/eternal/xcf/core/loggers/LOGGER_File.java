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

import java.io.PrintWriter;
import java.io.IOException;
import java.text.DateFormat;

import java.io.File;
import java.io.FileWriter;

import com.eternal.xcf.core.XCFContext;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFLogger;

/**
 * Logger that will log to a physical file.
 * 
 * Creation date: (11/21/2001 12:02:09 PM)
 */
public class LOGGER_File implements XCFLogger {
	private File logFile = null;
	private DateFormat dateFormatter = DateFormat.getDateTimeInstance();
	private FileWriter writer = null;
	private PrintWriter pwriter = null;

	/**
	 * Constructs a file logger
	 */
	public LOGGER_File() {
		super();
	}
	/**
	 * Return the log file File object
	 * @return java.io.File
	 */
	public java.io.File getLogFile() {
		return logFile;
	}

	/**
	 * Logs the exception to the file.  Prepends message with logtype, timestamp, and thread id.
	 *  @param context unused
	 *  @param logType 
	 *  @param exception
	 */
	public void logException(XCFContext context, LogTypes logType, java.lang.Exception e) {
	
		if (writer == null) {
			LOGGER_Console.s().logException(context, logType, e);
			return;
		}
		
		synchronized (writer) {
				
		    pwriter.flush();
		    pwriter.write("\n");
		    pwriter.write(logType.toString());
	   	    pwriter.write(LOG_PAD);
			pwriter.write(dateFormatter.format(new java.util.Date(System.currentTimeMillis())));
		    pwriter.write(LOG_PAD);
		    pwriter.write(getThreadId());
		    pwriter.write("  ");
	
	    	e.printStackTrace(pwriter);
		    	
			pwriter.flush();
		}
	}
	
	/**
	 * Logs the exception to the file.  Prepends message with logtype, timestamp, and thread id.
	 * Creation date: (11/21/2001 12:02:09 PM)
	 * @param message java.lang.String
	 */
	public void logMessage(XCFContext context, LogTypes logType, String message) {
	
		if (writer == null) {
			LOGGER_Console.s().logMessage(context, logType, message);
			return;
		}
		
		synchronized (writer) {	
		    pwriter.flush();
	
		    pwriter.println();
		    pwriter.write(logType.toString());
		    pwriter.write(LOG_PAD);
			pwriter.write(dateFormatter.format(new java.util.Date(System.currentTimeMillis())));
		    pwriter.write(LOG_PAD);
		    pwriter.write(getThreadId());
		    pwriter.write(LOG_PAD);
	    	pwriter.write(message);
	   	
			pwriter.flush();
		}
	}
		
	/**
	 * Sets the log file name and creates the File object over that file.
	 * This will also create the writer's needed.
	 * Creation date: (11/26/2001 12:25:39 PM)
	 * @param newLogFile java.io.File
	 */
	public void setLogFile(String logFileName) {
		if (logFile != null) return;
		
		logFile = new File(logFileName);
	
	}
	
	/**
	 * Return the thread id
	 * Creation date: (11/21/2001 3:07:44 PM)
	 * @return java.lang.String
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
	 * Opens the resources necessary to log to a file.
	 */
	public void open() throws XCFException {
		if (logFile == null) throw new XCFException("No log file set for LOGGER_File");
		try {
			writer = new FileWriter(logFile);
			pwriter = new PrintWriter(writer);
			logMessage(null, LogTypes.INFO, "Starting Log");	
		} catch (IOException e1) {
			System.out.println("The log file: " + logFile.getAbsolutePath() + " is not a valid file name.");
			e1.printStackTrace();
			logFile = null;
		}
	}

	/**
	 * Closes the file logger resources.
	 */
	public void close() throws XCFException {
		try {
			if (writer != null) writer.flush();
			writer.close();
		} catch (IOException e) {}
		if (pwriter != null) pwriter.close();
	}
	
	/**
	 * Return the name of this logger
	 */
	public String toString() {
		return LOG_TO_FILE;
	}
	
}
