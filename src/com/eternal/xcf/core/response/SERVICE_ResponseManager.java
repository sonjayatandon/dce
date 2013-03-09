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
 * @author Sonjaya Tandon
 */
package com.eternal.xcf.core.response;

import java.util.HashMap;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.core.XCFService;

/**
 * The response manager service stores all the response renderers used in the system.
 * @author Sonjaya Tandon
 */
public class SERVICE_ResponseManager implements XCFService {
	public static final String XCF_TAG = "xcf-renderer";
	private XCFFacade facade;
	private HashMap<String, HashMap<String, XCFResponse>> responseRenderersByType = new HashMap<String, HashMap<String,XCFResponse>>();

	public String getName() {
		return XCF_TAG;
	}

	public void setFacade(XCFFacade facade) {
		this.facade = facade;
	}

	public void setName(String name) {
	}

	public void start() throws XCFException {
	}

	public void stop() throws XCFException {
	}
	
	/**
	 * Looks up the response type and response id stored in the request and 
	 * returns the associated response render.  It is assumed that this method
	 * is ONLY called AFTER the request has been processes
	 * @param req a request sent to the server for processing
	 * @return the response render identified by the request
	 */ 
	public XCFResponse getResponse(XCFRequest req) throws XCFException {
		String responseType = (String)req.getContext().getValue(XCFResponse.XCF_RESPONSE_TYPE);
		String responseId = (String)req.getContext().getValue(XCFResponse.XCF_RESPONSE_ID);
		
		if (responseType == null) {
			throw new XCFException("Unable to render response: " + responseId + ".  Response Type never set.");
		}
		
		// get all the response renderes for this response type
		HashMap<String, XCFResponse> responseRenderers = responseRenderersByType.get(responseType); 
		if (responseRenderers == null) {
			throw new XCFException("Unable to render response: " + responseId + ".  No responses registered for: " + responseType);
		}
		
		// look up the response renderer and return it
		XCFResponse response = responseRenderers.get(responseId);
		
		if (response == null) {
			throw new XCFException("Unable to render response: " + responseId + ".  Response not registered for: " + responseType);
		}
		
		return response;
	}
	
	/**
	 * Registers a response into the system and associates it with a response type.
	 * @param responseType identifies the type of response the renderer will render to.
	 * responseType can be things like html, nio, 2D, 3D or cell
	 */
	public void registerResponse(String responseType, XCFResponse response) throws XCFException {
		HashMap<String, XCFResponse> responseRenderers = responseRenderersByType.get(responseType); 
		if (responseRenderers == null) {
			responseRenderers = new HashMap<String, XCFResponse>();
			responseRenderersByType.put(responseType, responseRenderers);
		}
		facade.logDebug("Registering " + response.getName() + " for type " + responseType);
		responseRenderers.put(response.getName(), response);
	}
	
}
