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
package com.eternal.xcf.sax;

import java.util.HashMap;
import java.util.StringTokenizer;

import org.xml.sax.Attributes;

import com.eternal.xcf.common.service.SERVICE_FlyweightFactory;
import com.eternal.xcf.core.XCFException;

/**
 * Acts as the base class for any SAXEL used to interpret composite expressions.
 * Composite expressions are represented by XML tags which will have child tags.
 * 
 * You will typically override build, startInterpreting, and endInterpreting.
 * If you override startChildElement OR endChildElement make sure to call the super method.
 * 
 * Creation date: (9/6/2001 5:29:29 PM)
 * 
 * @author: Sonjaya Tandon
 */
public class SAXEL_Composite extends SAX_Element {
	private static String EL_ADD_TAG = "add-tag";
	private static String EL_INCLUDE_PACKAGES = "include-packages";
	private static String EL_ADD_CONVENTION = "add-convention";
	
	private static SAXEL_Literal ADD_TAG = new SAXEL_Literal(EL_ADD_TAG);
	private static SAXEL_Literal INCLUDE_PACKAGES_TAG = new SAXEL_Literal(EL_INCLUDE_PACKAGES);
	private static SAXEL_Literal ADD_CONVENTION_TAG = new SAXEL_Literal(EL_ADD_CONVENTION);
	
	protected HashMap<String, SAX_Element> children = new HashMap<String, SAX_Element>();
	protected SERVICE_FlyweightFactory flyweightFactory;
	
	///////////////////////////////////////////////////////
	// Creational methods
	///////////////////////////////////////////////////////
	/**
	 * Default constsructor
	 * @param tag java.lang.String
	 * @param parser com.dataskill.standon.xml.message.XmlMessageParser
	 */
	public SAXEL_Composite(String tag) {
		super(tag);
	}
	
	/**
	 * Hooks onto the setHandler method to grab the flyweightFactory
	 */
	public final void setHandler(SAX_Handler handler) {
		super.setHandler(handler);
		assert handler.getFacade() != null;
		flyweightFactory = (SERVICE_FlyweightFactory)handler.getFacade().getService(SERVICE_FlyweightFactory.XCF_TAG);
		assert flyweightFactory != null;
	}
	
	/**
	 * Associates an xml tag with the SAXEL that will interpret it
	 * @param tag
	 * @param el
	 */
	public final void addTag(String tag, SAX_Element el) {
		el.setTag(tag);
		el.setHandler(getHandler());
		children.put(tag, el);
	    el.build();
	}
	
	///////////////////////////////////////////////////////
	// Accessor methods
	///////////////////////////////////////////////////////
	/**
	 * Returns the children.
	 * @return HashMap
	 */
	public final HashMap getChildren() {
		return children;
	}

	/**
	 * Sets the children.
	 * @param children The children to set
	 */
	public final void setChildren(HashMap<String, SAX_Element> children) {
		this.children = children;
	}
	///////////////////////////////////////////////////////
	// SAX handler methods
	///////////////////////////////////////////////////////
	/**
	 * Finds the SAXEL associated with the tag, pushes it onto the interpreter stack and 
	 * calls startInterpreting for that SAXEL
	 * 
	 * There are two reserved tags:
	 *   add-tag: in this case, a literal is pushed onto the stack to grab the class name in the tag body
	 *            that SAXEL class is then assocated to the tag name
	 *   include-packages: in this case a literal is pushed onto the stack to grab the package list
	 *            those packages are then added to the flyweight factory.
	 *            
	 * If there is no SAXEL associated with the tag, attempt to find one using the flyweight factory
	 * If the flyweight factory returns a SAXEL, associate it witht the tag, push it on the stack
	 * and startInterpreting.  If not, nothing more can be done, tag is unexpected.
	 * 
	 * Creation date: (9/6/2001 5:47:32 PM)
	 * @param uri java.lang.String
	 * @param name java.lang.String
	 * @param rawName java.lang.String
	 * @param attributes org.xml.sax.Attributes
	 */
	public void handleStartChildElement(String uri, String name, String rawName, Attributes attributes) {
		// handle the reserved tags first
		if (name.equals(EL_ADD_TAG)) {
			// tag is "add-tag" -- name attribute is the tag we are adding
			// body of the tag is the class name to bind to that tag
			String tagName = attributes.getValue("name");
			
			// push a literal to grab the class name
			handler.put("tag-name", tagName);
			ADD_TAG.setHandler(getHandler());
			handler.push(ADD_TAG);		
			return;
		} else if (name.equals(EL_INCLUDE_PACKAGES)) {
			// tag is include packages, push a literal to grab the package list
			INCLUDE_PACKAGES_TAG.setHandler(getHandler());
			handler.push(INCLUDE_PACKAGES_TAG);		
			return;			
		} else if (name.equals(EL_ADD_CONVENTION)) {
			// tag is add convention, push a literal to grab the conventions
			ADD_CONVENTION_TAG.setHandler(getHandler());
			handler.push(ADD_CONVENTION_TAG);
			return;
		} else {
			// check to see if we have a SAXEL for this tag
			SAX_Element el = (SAX_Element)children.get(name);
			
			if (el == null) {
				// we don't, use the flyweightFactory to get one
				try {
					el = (SAX_Element)flyweightFactory.getInstance("saxel", name);
					if (el != null) {
						addTag(name, el);
					}
				} catch (XCFException e) {
					handleException(e);
				}
			}
			
			// if we have a SAXEL for the tag, push it and start interpreting
			if (el != null) {
				el.setHandler(handler);
				handler.push(el);
				el.startInterpreting(uri, name, rawName, attributes);
			} else {
				handleUnexptectedTag(name);
			}
		}
	}
	
	/**
	 * Invoked whenever a child tag completes interpreting.
	 * 
	 * Handles the post-processing for the two reserved tags: add-tag and include-packages
	 */
	public void handleEndChildElement(String interpreterTag) {
		
		if (interpreterTag.equals(EL_ADD_TAG)) {
			// grab the tag name and tag SAXEL class name off the handler
			// instantiate the class and bind it to the tag
			String tagName = (String)handler.consume("tag-name");
			String tagClass = (String)handler.consume(EL_ADD_TAG);
			
			try {
				Class intrClass = Class.forName(tagClass);
				SAX_Element el = (SAX_Element)intrClass.newInstance();
				addTag(tagName, el);
			} catch (Exception e) {
				handleException(e);
			}
		} else if (interpreterTag.equals(EL_INCLUDE_PACKAGES)) {
			// grab the include list off the handler and add each 
			// package in the list to the flyweight factory's search path
			String includeList = (String)handler.consume(EL_INCLUDE_PACKAGES);
			StringTokenizer iterator = new StringTokenizer(includeList);
			while (iterator.hasMoreTokens()) {
				String includePackage = iterator.nextToken();
				flyweightFactory.addPackage(includePackage);
			}
		} else if (interpreterTag.equals(EL_ADD_CONVENTION)) {
			// grab the conventions off the handler and add each one
			// to the flyweight factory
			String conventions = (String)handler.consume(EL_ADD_CONVENTION);
			StringTokenizer iterator = new StringTokenizer(conventions, "\n");
			while (iterator.hasMoreTokens()) {
				String convention = iterator.nextToken().trim();
				
				if (convention.length() == 0) continue;
				
				int i = convention.indexOf(',');
				if (i < 0 || i >= convention.length()-1) {
					handleError("BAD CONVENTION FORMAT.  Convention must be of the form: type,prefix.  Instead, convention was: " + convention);
					continue;
				}
				String type = convention.substring(0, i).trim();
				String prefix = convention.substring(i+1).trim();
				flyweightFactory.addConvention(type, prefix);
			}
		}
	}
}
