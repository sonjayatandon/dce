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

import org.xml.sax.Attributes;



/**
 * Base Interpreter class used for SAX parsing.  Create a concrete extension of this class for each
 * XML tag you wish to interpret.  Child classes are reffered to as SAXELs
 *
 * Each concrete extension MUST implement a null constructor.  That constructor should call
 *   super([default tag name])
 *
 * Creation date: (9/6/2001 5:06:03 PM)
 * @author: Sonjaya Tandon
 */
public abstract class SAX_Element {
	protected String tag;
	protected SAX_Handler handler;

	/**
	 * Creates a new SAX element and sets the tag name
	 */
	public SAX_Element(String tag) {
		super();
		this.tag = tag;
	}

	/**
	 * Override this method to create any child interpret SAXELs
	 *
	 */
	public void build() {}

	///////////////////////////////////////////////////////
	// Usefull accessors
	///////////////////////////////////////////////////////
	/**
	 * Sets the SAXELs handler
	 * Creation date: (9/6/2001 6:15:31 PM)
	 * @param newHandler SAX handler
	 */
	public void setHandler(SAX_Handler newHandler) {
		handler = newHandler;
		assert handler != null;
	}

	/**
	 * Returns the SAXELs handler
	 * Creation date: (9/6/2001 6:15:31 PM)
	 * @return SAX_Handler
	 */
	public SAX_Handler getHandler() {
		return handler;
	}

	/**
	 * Gets the tag
	 * @return Returns a String
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Sets the tag
	 * @param tag The tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	///////////////////////////////////////////////////////
	// SAX handler methods
	///////////////////////////////////////////////////////
	/**
	 * This method is invoked when the tag associated with this SAXEL is encountered
	 * by the handler.
	 * @param uri
	 * @param name
	 * @param rawName
	 * @param attributes
	 */
	public void startInterpreting(String uri, String name, String rawName, Attributes attributes) {
	}

	/**
	 * This method is invoked by the handler.  It is called to capture the text in the body of an xml tag.
	 *
	 * Creation date: (9/6/2001 5:24:27 PM)
	 * @param ch char[]
	 * @param offset int
	 * @param length int
	 */
	public void handleCharacters(char[] ch, int offset, int length) {}

	/**
	 * Called when the handler sees a new child tag.  Override if the SAXEL has child tags that need interpreting.
	 * When you see a tag that represents a sub-expression, push the appropriate SAXEL on the stack.
	 *
	 * Creation date: (9/6/2001 5:18:34 PM)
	 * @param uri java.lang.String
	 * @param name java.lang.String
	 * @param rawName java.lang.String
	 * @param attributes org.xml.sax.Attributes
	 */
	public void handleStartChildElement(String uri, String name, String rawName, Attributes attributes) {}

	/**
	 * Called after a child element is done interpreting.
	 *
	 * Creation date: (9/19/2001 12:16:34 PM)
	 * @param interpreterTag the tag name of the child interpreter
	 */
	public void handleEndChildElement(String interpreterTag) {}

	/**
	 * Called when the end tag for this SAXEL is encountered.  After invoking this method, the handler will
	 * pop the SAXEL from the interpreter stack.
	 *
	 * Creation date: (9/6/2001 5:18:34 PM)
	 * @param uri java.lang.String
	 * @param name java.lang.String
	 * @param qName java.lang.String
	 */
	public void endInterpreting(String uri, String name, String qName) {}

	///////////////////////////////////////////////////////
	// Error handling methods
	///////////////////////////////////////////////////////
	public final String getRequiredString(Attributes attributes, String paramName, int errorCount)  {
		String value = attributes.getValue(paramName);
		if (value == null) {
			handleError(tag + " MUST HAVE A " + paramName + " attribute.");
			errorCount++;
			return null;
		}

		return value;
	}

	public final Integer getRequiredInt(Attributes attributes, String paramName, int errorCount)  {
		String svalue = attributes.getValue(paramName);
		if (svalue == null) {
			handleError(tag + " MUST HAVE A " + paramName + " attribute.");
			errorCount++;
			return null;
		}

		int value = 0;
		try {
			value = Integer.parseInt(svalue);
		} catch (NumberFormatException e) {
			handleError(tag + " MUST HAVE AN INTEGER " + paramName + " attribute.");
			errorCount++;
			return 0;
		}

		return value;
	}

	public final boolean isAttributeYes(Attributes attributes, String paramName) {
		String value = attributes.getValue(paramName);
		if (value == null) return false;

		if ("yes".equals(value.trim().toLowerCase())) return true;

		return false;
	}

	/**
	 * Handle's an error by logging a message
	 * Creation date: (9/13/2001 11:46:44 AM)
	 * @param tag java.lang.String
	 */
	public final void handleError(String msg) {
		handler.getFacade().logError(msg);
	}

	/**
	 * Handles the exception by logging to the error and debug logger
	 * Creation date: (9/13/2001 11:46:44 AM)
	 * @param tag java.lang.String
	 */
	public final void handleException(Exception e) {
		handler.getFacade().logError(e.getLocalizedMessage());
		handler.getFacade().logDebug(e);
	}

	/**
	 * Log a message indicating that the XML file contains a tag we don't know how to handle
	 * Creation date: (9/13/2001 11:46:44 AM)
	 * @param tag java.lang.String
	 */
	public final void handleUnexptectedTag(String name) {
		handler.getFacade().logError("Unexpected [" + name + "] tag under [" + tag + "] tag");
	}
}
