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
 * Root interperter for an xml syntax.
 * 
 * Creation date: (11/25/2002)
 * @author: standon
 */
public final class SAXEL_Root extends SAX_Element {
	
	/**
	 * INTR_Root.
	 */
	public SAXEL_Root() {
		super("");
	
	}
	
	/**
	 * This is the first interpreter called by the handler.  Bootstrap the whole process
	 * by pushing the intial interpret (a SAXEL_Composite) onto the interpreter stack
	 */
	public void handleStartChildElement(String uri, String name, String rawName, Attributes attributes) {
		if (name.equals(getTag())) {
			getHandler().push(getInitialInterpreter());
		} else {
			handleUnexptectedTag(name);
		}
	}
	
	/**
	 * Create the initial interpreter
	 * @return
	 */
	SAX_Element getInitialInterpreter() {
		SAXEL_Composite server = new SAXEL_Composite(getTag());
		server.setHandler(getHandler());
		return server;
	}

}
