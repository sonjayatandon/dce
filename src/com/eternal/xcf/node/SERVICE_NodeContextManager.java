/*
 * Copyright © 2004 Eternal Adventures, Inc.
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
 * @author standon
 */
package com.eternal.xcf.node;

import java.util.HashMap;

import com.eternal.xcf.core.XCFContext;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFService;


/**
 * @author Owner
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class SERVICE_NodeContextManager implements XCFService {
	
	public static final String XCF_TAG = "context-manager";
	
	private String name;
	private XCFFacade facade;
	
	private HashMap<String, Class> nodeTypes = new HashMap<String, Class>();
	
	public void setFacade(XCFFacade facade) {
		this.facade = facade;
	}
	
	public void start() throws XCFException {
		register(INodeManager.NT_GENERIC, "com.eternal.xcf.node.NODE_GenericElement");
	}
	
	public void stop() throws XCFException {
	}
	
	public void register(String typeName, String typeClass) throws XCFException {
		try {
			Class factoryClass = Class.forName(typeClass);
			nodeTypes.put(typeName, factoryClass);
		} catch (ClassNotFoundException e1) {
			throw new XCFException("Unable to locate factory:" + typeClass + " for type: " + typeName);
		} 
	}
	
	public INodeElement newElement(String type) throws XCFException {
		Class factoryClass = (Class)nodeTypes.get(type);
		
		if (factoryClass == null) {
			throw new XCFException("There is no registered factory for the type: " + type);
		}
		
		try {
			INodeElement element = (INodeElement)factoryClass.newInstance();
			return element;
		} catch (ClassCastException e2) {
			throw new XCFException("The class:" + factoryClass.getName() + " for type: " + type + " is not a factory class");
		} catch (InstantiationException e3) {
			throw new XCFException("The class:" + factoryClass.getName() + " for type: " + type + " does not have a default constructor");
		} catch (IllegalAccessException e4) {
			throw new XCFException("The class:" + factoryClass.getName() + " for type: " + type + " does not have a public constructor");
		} catch (Exception e) {
			throw new XCFException("Unable to create element for type: " + type + " ::: " + e.getMessage());
		}
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public XCFContext getNewContext() throws XCFException {
		INodeManager context;
		context = new CONTEXT_NodeManager();
		context.setContextManager(this);
		context.setFacade(facade);
		return context;
	}
	
}
