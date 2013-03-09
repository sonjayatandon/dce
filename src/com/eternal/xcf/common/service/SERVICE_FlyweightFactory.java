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
package com.eternal.xcf.common.service;

import java.util.HashMap;
import java.util.Stack;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFService;

/**
 * Client classes use this class to get a reference to a flyweight instance.  
 * The flyweight factory provides method that takes as input a string description 
 * of an object type.  It then does the work of mapping that type to a concrete class 
 * and determining whether or not a new instance of that class needs to be created.
 * 
 * @author sonjaya
 *
 */
public final class SERVICE_FlyweightFactory implements XCFService {
	public static String XCF_TAG = "flyweight-factory";
	private String name = XCF_TAG;
	
	private XCFFacade facade;
	
	private Stack<Scope> scopeStack = new Stack<Scope>();

	/**
	 * Returns services name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Set the services facade
	 */
	public void setFacade(XCFFacade facade) {
		this.facade = facade;
		
	}

	/**
	 * Set the services name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Start out the service by defining some conventions
	 * and add com.eternal.xcf.common to the search path
	 */
	public void start() throws XCFException {
		pushScope();
		addConvention("validator", ".request.parameter.VALIDATOR_");
		addConvention("setter", ".request.parameter.SETTER_");
		addConvention("saxel", ".builder.SAXEL_");
		addConvention("instruction", ".request.processor.instructions.INSTRUCTION_");
		
		addPackage("com.eternal.xcf");
		addPackage("com.eternal.xcf.common");
	}

	/**
	 * Stop by poping the scope
	 */
	public void stop() throws XCFException {
		popScope();
	}
	
	/**
	 * Returns the instance associated w/the tag and type
	 * This method will first look to see if the instance exists in the current scope
	 * If a flyweight exists, it will return that, otherwise it will instantiate a new one.
	 * It repeats this process for each scope on the stack.
	 *  
	 * @param type identifies the type of the flyweight -- e.g. validator, saxel
	 * @param tag identifies the flyweight in that type
	 * @return the flyweight object
	 * @throws XCFException thrown if it is unable to create a flyweight object
	 */
	public Object getInstance(String type, String tag) throws XCFException {
		return getInstance(type, tag, true);
	}
	
	/**
	 * This is similar to getInstance.  The only difference is that this 
	 * method always returns a new instance of the desired type.
	 * @param type
	 * @param tag
	 * @return
	 * @throws XCFException
	 */
	public Object getNewInstance(String type, String tag) throws XCFException {
		return getInstance(type, tag, false);
	}
	
	/**
	 * This is the actual work horse method for both getInstances.
	 * @param type
	 * @param tag
	 * @param isSingleton
	 * @return
	 * @throws XCFException
	 */
	public Object getInstance(String type, String tag, boolean isSingleton) throws XCFException {
		// get the sub-package and class name prefix for this type
		String prefix = getConvention(type);
		if (prefix == null) throw new XCFException("No convention for type:" + type);
		
		// for each scope started with the top of stack, 
		// find our create the flyweight
		for (int i = scopeStack.size() - 1; i >= 0; i--) {
			Scope scope = scopeStack.elementAt(i);
			
			if (isSingleton) {
				// first see if we have already created the flyweight
				Object flyweight = scope.getFlyweight(type, tag);
				// if flyweight is this object, then it meant we
				// already looked and couldn't find it for this scope
				// no need to look again, so move on to the next scope
				if (flyweight == this) continue;
				// if we got a non-null flyweight that was not this object
				// we have what we need and can return it
				if (flyweight != null) return flyweight;
				
				// ok, we don't have a flyweight yet for this type,tag and we haven't
				// looked yet.  Search through all the packages to see if we can find 
				// the flyweight class
			}
			
			Stack<String> searchPath = scope.searchPath;
			
			for (int j = searchPath.size()-1; j>=0; j--) {
				// create the class name by appending [path][prefix][normalized name]
				String path = searchPath.elementAt(j);
				String name = normalizeName(tag);
				String className = path + prefix + name;
				facade.logDebug("Searching for: " + className);
				
				// try to load it and create an instance
				try {
					Class aClass = Class.forName(className);
					Object anInstance = aClass.newInstance();
					scope.putFlyweight(type, tag, anInstance);
					return anInstance;
				} catch (ClassNotFoundException cnf) {
					facade.logDebug(className + " not in classpath");
				} catch (IllegalAccessException ia) {
					facade.logDebug(className + " does not have a public constructor");
				} catch (InstantiationException ie) {
					facade.logDebug(className + " does not have a null constructor");
				}
				
				// if we got here, we didn't find it in this path, go on to the next one
			}
			
			if (isSingleton) {
				// there is no flyweight for this context, mark it so
				// we don't bother searcing on the next request
				scope.putFlyweight(type, tag, this);
			}
		}
		
		// if we get here, we can't find the flyweight class and that is a problem
		throw new XCFException("Unable to find the class for type: " + type + " and tag: " + tag);
		
	}
	
	/**
	 * Looks up the convention for the type
	 * @param type
	 * @return
	 */
	String getConvention(String type) {
		for (int i = scopeStack.size() - 1; i >= 0; i--) {
			Scope scope = scopeStack.elementAt(i);
			String prefix = scope.getConvention(type);
			if (prefix != null) return prefix;
		}
		
		return null;
	}
	
	/**
	 * Convert the tag to a name that follows the conventions.  
	 * Upercase word boundaries and remove -'s and _'s
	 * @param tag
	 * @return the converted name
	 */
	String normalizeName(String tag) {
		StringBuffer buf = new StringBuffer();
		
		boolean newWord = true;
		//  capitalize first letter of each word
		//  remove -'s and _'s
		for (int i = 0; i < tag.length(); i++) {
			char c = tag.charAt(i);
			if (c == '-' || c == '_') {
				newWord=true;
			} else if (newWord) {
				buf.append(Character.toUpperCase(c));
				newWord = false;
			} else {
				buf.append(c);
				newWord = false;
			}
		}
		return buf.toString().trim();
	}
	
	/**
	 * Push a new scope onto the scope stack
	 *
	 */
	public void pushScope() {
		scopeStack.add(new Scope());
		
	}
	
	/**
	 * Pop a scope off of the scope stack
	 *
	 */
	public void popScope() {
		scopeStack.pop();
	}

	/**
	 * Add a sub-package, prefix convention for the type
	 * @param type
	 * @param prefix  this should be of the form .[package(s)].[prefix (in caps)]. e.g. .request.processors.VALIDATOR_
	 */
	public void addConvention(String type, String prefix) {
		scopeStack.peek().addConvention(type, prefix);
		
	}

	/**
	 * Add a package to the search path stack for the current scope.  The search path search starts from last first
	 * @param path
	 */
	public void addPackage(String path) {
		scopeStack.peek().addPath(path);
	}
	
	/**
	 * A scope contains a stack of packages (called the search path)
	 * and a cache of flyweights bound to a type,tag pair
	 * 
	 * @author sonjaya
	 *
	 */
	final class Scope {
		Stack<String> searchPath = new Stack<String>();
		HashMap<String, Object>cache = new HashMap<String, Object>();
		private HashMap<String, String>conventions = new HashMap<String, String>();
		
		/**
		 * Add a sub-package, prefix convention for the type
		 * @param type
		 * @param prefix  this should be of the form .[package(s)].[prefix (in caps)]. e.g. .request.processors.VALIDATOR_
		 */
		public void addConvention(String type, String prefix) {
			conventions.put(type, prefix);
			
		}
		
		/**
		 * Get the sub-package, prefix convention for the type
		 * @param type
		 * @return
		 */
		public String getConvention(String type) {
			return conventions.get(type);
		}

		/**
		 * Add a path to search path stack
		 * @param path
		 */
		void addPath(String path) {
			searchPath.add(path);
		}
		
		/**
		 * Lookup the flyweight object bound to type,tag return null 
		 * if it doesn't exist
		 * @param type
		 * @param tag
		 * @return
		 */
		Object getFlyweight(String type, String tag) {
			return cache.get(type + "." + tag);
		}
		
		/**
		 * Bind a flyweight object to a type,tag
		 * @param type
		 * @param tag
		 * @param flyweight
		 */
		void putFlyweight(String type, String tag, Object flyweight) {
			cache.put(type + "." + tag, flyweight);
		}
	}
}
