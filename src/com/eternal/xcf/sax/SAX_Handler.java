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

import java.util.Hashtable;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.eternal.xcf.core.XCFFacade;

/**
 * SAX Handler<br>
 * Acts as the SAX content and error handler for all XCF XML Interpreters.
 * <br><br>
 * <ol type="1">
 * <li><b>Define your XML language as a context free grammar</b>
 * <br>&nbsp;For example, a command language could be defined as:
 * <ul>
 * <li><b>COMMAND</b> ::= &lt;command&gt;<b>NAME(PARAMETER)*</b>&lt;/command&gt;</li>
 * <li><b>PARAMTER</b> ::= &lt;parameter&gt;<b>NAME VALUE</b>&lt;/parameter&gt;</li>
 * <li><b>NAME</b> ::= &lt;name&gt;<i>literal</i>&lt;/name&gt;</li>
 * <li><b>VALUE</b> ::= &lt;value&gt;<i>literal</i>&lt;/value&gt;</li>
 * </ul>
 * <br>An example of a stream in this language would be
 * <br>&lt;command&gt;
 * <br>&nbsp;&nbsp;&lt;name&gt;run-simulation&lt;/name&gt;
 * <br>&nbsp;&nbsp;&lt;parameter&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&lt;name&gt;iterations&lt;/name&gt;
 * <br>&nbsp;&nbsp;&nbsp;&nbsp;&lt;value&gt;1000&lt;/value&gt;
 * <br>&nbsp;&nbsp;&lt;/parameter&gt;
 * <br>&lt;/command&gt;
 * </li>
 * <li><b>For each expression in the language, create a subclass of SAX_Element</i></b>
 * <br>Override <i>startInterpreting</i> to start interpreting the expression
 * <br>Override <i>endInterpreting</i> to finish up interpreting of the expression.
 * </li>
 * </ol> <br>
 * Creation date: (9/6/2001 2:38:23 PM)
 * @author: Sonjaya Tandon
 */
public final class SAX_Handler extends DefaultHandler {
	private Stack<SAX_Element> interpreterStack;

	private Hashtable<Object, Object> context;
	private SAX_Element interpreter;
	private XCFFacade facade;


	/**
	 * Creates an instance of an interpreter handler.
	 * @param start root interpreter for xml language
	 * @param facade
	 * @see SAX_Element
	 */
	public SAX_Handler(SAX_Element start, XCFFacade facade) {
		super();
		context = new Hashtable<Object, Object>();
		interpreterStack = new Stack<SAX_Element>();
		interpreter = start;
		this.facade = facade;
		interpreter.setHandler(this);
		interpreter.build();
	}

	///////////////////////////////////////////////////////
	// Usefull accessors
	///////////////////////////////////////////////////////
	/**
	 * Return the subsystem facade
	 */
	public XCFFacade getFacade() {
		return facade;
	}

	/**
	 * Return the root interpreter
	 * Creation date: (9/13/2001 4:28:01 PM)
	 * @return com.dataskill.xcf.message.InterpreterElement root interpreter
	 */
	public SAX_Element getInterpreter() {
		return interpreter;
	}

	///////////////////////////////////////////////////////
	// Hash table helpers
	///////////////////////////////////////////////////////
	/**
	 * Puts an object onto the context.  Associates the object with the "key"
	 * Creation date: (9/7/2001 10:09:41 AM)
	 * @param key java.lang.Object
	 * @param value java.lang.Object
	 */
	public void put(Object key, Object value) {
		context.put(key, value);
	}

	/**
	 * Returns the data associated with "key".
	 * Creation date: (9/7/2001 10:11:11 AM)
	 * @return java.lang.Object an object associated with the "key"
	 * @param key java.lang.Object a string used to reference an object in the context
	 */
	public Object get(Object key) {
		return context.get(key);
	}

	/**
	 * Returns the data associated with "key".  Removes that data from the context.
	 * Creation date: (9/20/2001 1:45:20 PM)
	 * @param key java.lang.String used to identify an object on the context.
	 */
	public Object consume(String key) {
		Object val = context.get(key);
		if (val != null) {
			context.remove(key);
		}
		return val;
	}


	/**
	 * Assumes "key" is associated with a stack and pushes the value on
	 * that stack.
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public void pushValue(String key, Object value) {
		Object curValue = context.get(key);
		if (curValue == null) {
			curValue = new Stack<Object>();
			context.put(key, curValue);
		} else if (!(curValue instanceof Stack)) {
			curValue = new Stack<Object>();
			context.put(key, curValue);
		}

		Stack<Object> values = (Stack<Object>)curValue;
		values.push(value);
	}

	/**
	 * Assumes "key" is associated with a stack and
	 * returns the top value of that stack (or null
	 * if there is no stack).
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object peekValue(String key) {
		Object curValue = context.get(key);

		if (curValue == null) {
			return null;
		} else if (!(curValue instanceof Stack)) {
			return null;
		}

		Stack<Object> values = (Stack<Object>)curValue;
		return values.peek();
	}

	/**
	 * Assumes "key" is associated with a stack.
	 * Pops that stack and returns that value
	 * or null if there is no stack.
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Object popValue(String key) {
		Object curValue = context.get(key);

		if (curValue == null) {
			return null;
		} else if (!(curValue instanceof Stack)) {
			return null;
		}

		Stack<Object> values = (Stack<Object>)curValue;
		return values.pop();
	}


	///////////////////////////////////////////////////////
	// Stack helper methods
	///////////////////////////////////////////////////////
	/**
	 * Pops an interpreter off the stack.
	 * Creation date: (9/6/2001 5:13:27 PM)
	 * @param element com.dataskill.xcf.message.InterpreterElement
	 */
	public SAX_Element pop() {
		return (SAX_Element)interpreterStack.pop();
	}

	/**
	 * Pushes an interpeter element onto the stack.
	 * Creation date: (9/6/2001 5:13:27 PM)
	 * @param element com.dataskill.xcf.message.InterpreterElement
	 */
	public void push(SAX_Element element) {
		interpreterStack.push(element);
	}

	/**
	 * Returns the SAXEL that is on the top of the stack.
	 * Creation date: (9/6/2001 5:13:27 PM)
	 * @param element com.dataskill.xcf.message.InterpreterElement
	 */
	public SAX_Element tos() {
		return interpreterStack.peek();
	}

	///////////////////////////////////////////////////////
	// SAX Event handlers
	///////////////////////////////////////////////////////
	/**
	 * Called when the parser starts reading the xml stream.
	 * Creation date: (9/6/2001 5:28:35 PM)
	 */
	public void startDocument() {
		push(interpreter);
	}

	/**
	 * Called when the parser sees a new tag.  This call is passed onto the SAXEL
	 * that is on the top of the stack.
	 * Creation date: (9/6/2001 2:47:38 PM)
	 * @param uri java.lang.String
	 * @param localName java.lang.String
	 * @param rawName java.lang.String
	 * @param attributes org.xml.sax.Attributes
	 */
	public void startElement(String uri, String localName, String rawName, Attributes attributes) {
		localName = rawName;
		tos().handleStartChildElement(uri, localName, rawName, attributes);
	}

	/**
	 * This method is invoked to pass the character content data to
	 * the interpreter element that is on the top of the stack.
	 *
	 * Creation date: (9/6/2001 4:50:28 PM)
	 * @param ch char[] the entire character buffer
	 * @param start int the starting position in the buffer
	 * @param length int the ending position in the buffer.
	 */
	public void characters(char[] ch, int start, int length) {
		tos().handleCharacters(ch, start, length);
	}

	/**
	 * Called whenever an end-tag is reached.  This event is passed to the
	 * SAXEL at the top of the stack.
	 * Creation date: (9/6/2001 4:47:12 PM)
	 * @param uri java.lang.String
	 * @param localName java.lang.String
	 * @param qName java.lang.String
	 */
	public void endElement(String uri, String localName, String qName) {
		localName = qName;
		tos().endInterpreting(uri, localName, qName);
		if (localName.equals(tos().tag)) {
			pop();
			tos().handleEndChildElement(localName);
		}
	}

	/**
	 * Called when the end of the XML stream is reached.
	 * Creation date: (9/6/2001 5:28:45 PM)
	 */
	public void endDocument() {
		pop();
	}

}
