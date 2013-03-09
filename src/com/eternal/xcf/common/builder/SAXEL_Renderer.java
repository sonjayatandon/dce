/*
 * Copyright 2007 Eternal Adventures, Inc.
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

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFStrings;
import com.eternal.xcf.core.response.SERVICE_ResponseManager;
import com.eternal.xcf.core.response.XCFResponse;
import com.eternal.xcf.sax.SAXEL_Composite;
import com.eternal.xcf.sax.SAXEL_Literal;

/**
 * This SAXEL instantiates a response renderer and sets is property values.
 * 
 * This SAXEL assumes it is a child tag of <response>.
 * When this SAXEL is done interpreting it will register the newly configured 
 * reponse renderer with the response manager.
 * 
 * The renders full id will be [response-name].[render-name]
 * The builder will use the flyweight factory to look up the renderClass.
 * 
 * The syntax is of the form:
 *  <renderer name="[renderer-name]">
 *    <[property-name>[property-value]</[property-name]>
 *    ...
 *  </renderer>
 * @author sonjaya
 *
 */
public class SAXEL_Renderer extends SAXEL_Composite {
	public static String XCF_TAG = "renderer";
	
	public SAXEL_Renderer() {
		super(XCF_TAG);
	}

	/**
	 * Instantiates the response renderer and puts it on the handler context.
	 */
	@Override
	public void startInterpreting(String uri, String name, String rawName, Attributes attributes) {
		XCFFacade facade = getHandler().getFacade();
		String responseName = (String)getHandler().get(SAXEL_Response.RESPONSE_NAME);
		String rendererName = attributes.getValue(XCFStrings.NAME);
		
		if (responseName == null) return;
		
		if (rendererName == null) {
			handleError("<renderer ..> must have a name attribute: e.g., <renderer name=\"home\" >");
			return;
		}

		// we can assume the parent interpreter put the render class in the handler context 
		String renderClass = (String)getHandler().get(SAXEL_Response.RENDER_CLASS);
		
		try {
			// use the flyweight factory to get a new instance of that class.
			// put that instance on the handler context
			XCFResponse response = (XCFResponse)flyweightFactory.getNewInstance(XCFResponse.XCF_TAG, renderClass);
			response.setName(responseName + "." + rendererName);
			getHandler().put(XCFResponse.XCF_TAG, response);
			facade.logDebug("|----" + " BEGIN RENDERER " + responseName + "." + rendererName + "----------");
		} catch (XCFException e1) {
			handleException(e1);
			return;
		} catch (ClassCastException e2) {
			handleException(e2);
			return;
		}
	}

	/**
	 * Pushes a literal on the handler stack to grab the property value.
	 */
	@Override
	public void handleStartChildElement(String uri, String name, String rawName, Attributes attributes) {
		SAXEL_Literal propertySaxel = new SAXEL_Literal(name);
		propertySaxel.setHandler(getHandler());
		getHandler().push(propertySaxel);
	}

	/**
	 * Grabs the property value from the handler context and sets
	 * the property in the response renderer.
	 */
	@Override
	public void handleEndChildElement(String interpreterTag) {
		XCFFacade facade = getHandler().getFacade();
		XCFResponse response = (XCFResponse)getHandler().get(XCFResponse.XCF_TAG);
		String propertyValue = (String)handler.consume(interpreterTag);
		
		if (response == null) return;
		response.setProperty(interpreterTag, propertyValue);
		facade.logDebug("|---- " + interpreterTag + " = " + propertyValue);
	}

	/**
	 * Registers the response renderer with the response manager.
	 */
	@Override
	public void endInterpreting(String uri, String name, String qName) {
		XCFFacade facade = getHandler().getFacade();
		SERVICE_ResponseManager responseManager =  (SERVICE_ResponseManager)facade.getService(SERVICE_ResponseManager.XCF_TAG);
		XCFResponse response = (XCFResponse)getHandler().consume(XCFResponse.XCF_TAG);
		String clientType = (String)getHandler().get(SAXEL_Response.RESPONSE_TYPE);
		
		if (response == null) return;
		
		try {
			responseManager.registerResponse(clientType, response);
			facade.logDebug("|----" + " END RENDERER " + response.getName() + "----------");
		} catch (XCFException e) {
			handleException(e);
		}
	}
	
	
}
