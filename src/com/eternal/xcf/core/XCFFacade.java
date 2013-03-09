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


/**
 * XCFFacade<br>
 * Implements the system facade.  The facade contains all the services and modules.  
 * It triggers all the startup and shutdown functionality.  
 * It routes requests to the appropriate modules for processing.  
 * Module or service functionality may use the facade in processing requests or services. 
 * <br>
 * The facade also contains helper methods for logging.
 * 
 */
public final class XCFFacade {
	/**
	 * XCF_SERVER_TAG: Use this tag as the key any time you are going to
	 * put the object in a hash.
	 */
	public static String XCF_TAG = "xcf-server";

	private boolean configurationValid = true;
	private HashMap<String, XCFModule> modules = new HashMap<String, XCFModule>();
	private HashMap<String, XCFService> services = new HashMap<String, XCFService>();
	private XCFLogManager logManager;

	/**
	 * XCFServer Constructor
	 * For now, it creates the two requiered services: LogManager and DataElementManager.
	 * This should be modified to support the service pattern.
	 */	
	public XCFFacade() {
		setLogManager(new XCFLogManager());
	}	
	
	/**
	 * Gets the logManager
	 * @return Returns a LogManager
	 */
	public XCFLogManager getLogManager() {
		return logManager;
	}
	
	/**
	 * Sets the logManager
	 * @param logManager The logManager to set
	 */
	public void setLogManager(XCFLogManager logManager) {
		this.logManager = logManager;
	}

	/**
	 * Gets the configurationValid.  If true, assume that the facade was constructed successfully
	 * @return Returns true if the facade was contructed successfully
	 */
	public boolean getConfigurationValid() {
		return configurationValid;
	}
	
	/**
	 * Sets the configurationValid.  Call this after constructing the facade.
	 * @param configurationValid The configurationValid to set
	 */
	public void setConfigurationValid(boolean configurationValid) {
		this.configurationValid = configurationValid;
	}

	/**
	 * Adds a module to the facade..<br>
	 * 
	 * @param String path the string name of the module.  
	 * This name is used by server clients to identify the module.
	 * @param XCFModule module the module to add
	 */
	public void putModule(String path, XCFModule module) {
		module.setFacade(this);
		modules.put(path, module);
	}
	
	/**
	 * Gets a module from the facade. Modules are used to handle requests.
	 * 
	 * @param String path the string name of the module.  
	 * This name is used by facade clients to identify the module.
	 * @return XCFModule the module bound to the path.
	 */
	public XCFModule getModule(String path) {
		return (XCFModule)modules.get(path);
	}

	/**
	 * Gets the modules
	 * @return Returns a HashMap
	 */
	public HashMap getModules() {
		return modules;
	}
	
	/**
	 * Adds a service the the facade.<br>  Services are accessed by module handlers
	 * and other services and model functionality whose state is available across the lifecycle of the facade.
	 * 
	 * @param String serviceName the string name of the service.
	 * @param XCFService service the service to add.
	 */
	public void putService(String serviceName, XCFService service) throws XCFException {
		service.setFacade(this); 
		services.put(serviceName, service);
		service.start();
		logInfo("STARTED SERVICE " + serviceName + " USING " + service.getClass().getName());
	}
	
	/**
	 * Gets a service from the facade.<br>
	 * 
	 * @param String serviceName the string name of the service.
	 * @return XCFService the service bound to the name.
	 */
	public XCFService getService(String serviceName)  {
		return services.get(serviceName);
	}
	
	/**
	 * Shuts down all the server modules and services.
	 */
	public void shutdown() throws XCFException {
		boolean errors = false;
		StringBuffer msgs = null;
		
		logManager.stop();
		// should we unload the modules?
		
		// stop all the services
		Iterator iter = services.values().iterator();
		while (iter.hasNext()) {
			XCFService service = (XCFService)iter.next();
			
			try {
				service.stop();
			} catch (XCFException e) {
				if (msgs == null) {
					msgs = new StringBuffer();
				}
				msgs.append(e.getMessage());
				msgs.append("\n");
				errors = true;
			}
		}
		
		if (errors) {
			throw new XCFException(msgs.toString());
		}
	}
	
	/**
	 * Processes the XCF request.<br>
	 * 
	 * @param XCFRequest req the request to process.
	 */
	public void process(XCFRequest req) throws XCFException {
		if (req.getModule() == null) {
			throw new XCFException("request's module not set.");
		}
	  	XCFModule module = getModule(req.getModule());
	
	   	if (module == null)
	   	{
	   	   throw new XCFException("module '" + req.getModule() + "' not found");
	  	}
	  	
	  	module.process(req);  	
	}
	
	////////////////////////////////////////
	// HELPER METHODS FOR LOGGING
	
	/**
	 * Log an error message
	 */
	public void logError(String msg) {
		logManager.log(null, XCFLogger.LogTypes.ERROR, msg);
	}
	
	/**
	 * Log an error exception
	 * @param e
	 */
	public void logError(Exception e) {
		logManager.log(null, XCFLogger.LogTypes.ERROR, e);
	}
	
	
	/**
	 * Log an info message
	 * @param msg
	 */
	public void logInfo(String msg) {
		logManager.log(null, XCFLogger.LogTypes.INFO, msg);
	}

	/**
	 * Log a debug message
	 * @param msg
	 */
	public void logDebug(String msg) {
		logManager.log(null, XCFLogger.LogTypes.DEBUG, msg);
	}
	
	/**
	 * Log a debug exception
	 * @param e
	 */
	public void logDebug(Exception e) {
		logManager.log(null, XCFLogger.LogTypes.DEBUG, e);
	}
	
}

