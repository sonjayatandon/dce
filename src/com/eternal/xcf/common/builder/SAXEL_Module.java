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
package com.eternal.xcf.common.builder;

import org.xml.sax.Attributes;

import com.eternal.xcf.common.request.processor.MODULE_Instructions;
import com.eternal.xcf.common.service.SERVICE_FlyweightFactory;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFModule;
import com.eternal.xcf.core.XCFStrings;
import com.eternal.xcf.sax.SAXEL_Composite;

/**
 * This SAXEL creats a MODULE_Instruction and adding it to the facade.  It will put that module
 * onto the handler so that child SAXELs can configure it.
 * 
 * The syntax is of the form:
 *  <module name="[module-name]">
 *  
 * @author sonjaya
 *
 */
public class SAXEL_Module extends SAXEL_Composite {
	public static String XCF_TAG = XCFStrings.MODULE;
	
	public SAXEL_Module() {
		super(XCF_TAG);
	}

	/**
	 * Creates a new flyweight scope 
	 * Creates the module
	 * Puts the module on the handler
	 */
	public void startInterpreting(String uri, String name, String rawName, Attributes attributes) {
		XCFFacade facade = getHandler().getFacade();
		SERVICE_FlyweightFactory flyweightFactory = (SERVICE_FlyweightFactory)facade.getService(SERVICE_FlyweightFactory.XCF_TAG);

		// grab the module name
		String moduleName = attributes.getValue(XCFStrings.NAME);
		
		// it's required, so make sure we have it
		if (moduleName == null) {
			handleError("<module ...> must have a name attribute: e.g., <module name=\"account\">");
			return;
		}
		
		// push the scope so that child tags of this module can have their own search space/conventions
		flyweightFactory.pushScope();
		
		// create the module
		facade.logDebug("===================" + " BEGIN MODULE " + moduleName + "===================");
		XCFModule module = new MODULE_Instructions();
		module.setName(moduleName);
		
		// put it on the handler
		getHandler().put(XCF_TAG, module);
	}

	/**
	 * Finish interpreting by adding the newly configured module to the facade and poping the
	 * flyweight scope we pushed earlier.
	 */
	public void endInterpreting(String uri, String name, String qName) {
		XCFFacade facade = getHandler().getFacade();
		SERVICE_FlyweightFactory flyweightFactory = (SERVICE_FlyweightFactory)facade.getService(SERVICE_FlyweightFactory.XCF_TAG);
		XCFModule module = (XCFModule)getHandler().consume(XCF_TAG);
		if (module == null) return;
		
		// add the module to the facade
		facade.putModule(module.getName(), module);
		
		// pop the flyweight scope
		flyweightFactory.popScope();
		facade.logDebug("===================" + "END MODULE " + module.getName() + "===================");
	}
}
