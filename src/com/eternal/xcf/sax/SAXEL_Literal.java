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
 * Utility class used to interpret xml expressions of the form:
 * <br>&lt;<b>tag</b>&gt;<i>literal</i>&lt;<b>/tag</b>&gt;
 * <br>where <b>tag</b> is the value passed in to the contructor.  For example, to create
 * an interpreter element that will parse the following expression:
 * <br>&lt;name&gt;<i>literal</i>&lt;/name&gt;
 * <br>create an instance of SAXEL_Literal as follows:
 * <br> new SAXEL_Literal("name");
 * <br>
 * <br>SAXEL_Literal's endElement will put a the string value of the literal
 * on the context using it's tag as the key.
 * <br>To get the value, override the interpreterDone of the interpreter element
 * that pushed the SAXEL_Literal onto the interpreter stack and have it consume the tag
 * off the context.
 * Creation date: (9/6/2001 5:29:29 PM)
 * @author: Sonjaya Tandon
 */
public final class SAXEL_Literal extends SAX_Element {
	private StringBuffer contents = new StringBuffer();
	/**
	 * SAXEL_Literal default constructor
	 * @param tag java.lang.String
	 * @param parser com.dataskill.standon.xml.message.XmlMessageParser
	 */
	public SAXEL_Literal(String tag) {
		super(tag);
	}
	
	/**
	 * Assumes the characters represent the literal value of the expression.
	 * Creation date: (9/6/2001 5:57:05 PM)
	 * @param ch char[]
	 * @param offset int
	 * @param length int
	 */
	public final void handleCharacters(char[] ch, int offset, int length) {
		if (length != 0) {
			contents.append(ch, offset, length);
		}
	}
	
	/**
	 * Puts the literal onto the context.  Uses it's tag as the key.
	 * Creation date: (9/6/2001 5:47:32 PM)
	 * @param uri java.lang.String
	 * @param name java.lang.String
	 * @param rawName java.lang.String
	 * @param attributes org.xml.sax.Attributes
	 */
	public final void endInterpreting(String uri, String name, String qName) {
		if (contents != null) {
			handler.put(tag, contents.toString().trim());
			contents.setLength(0);
		}
	}
	
	/**
	 * Generates an error.  Literals aren't supposed to interpret any additional tags.
	 * Creation date: (9/6/2001 5:47:32 PM)
	 * @param uri java.lang.String
	 * @param name java.lang.String
	 * @param rawName java.lang.String
	 * @param attributes org.xml.sax.Attributes
	 */
	public final void handleStartChildElement(String uri, String name, String rawName, Attributes attributes) {
		handleUnexptectedTag(name);
	}
}
