package com.eternal.xcf.common.builder;

import org.xml.sax.Attributes;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFStrings;
import com.eternal.xcf.core.response.SERVICE_ResponseManager;
import com.eternal.xcf.core.response.XCFResponse;
import com.eternal.xcf.sax.SAXEL_Composite;

/**
 * This SAXEL begins the definition of a family of response objects.  All
 * response renderers defined under this tag will be bound to the same
 * response type and use the same concrete respones rendering class.
 * 
 * The renders full id will be [response-name].[render-name]
 * The builder will use the flyweight factory to look up the renderClass.
 * 
 * The syntax is of the form:
 *  <response name="[response-name]" responseType="[responseType]" renderClass="[class identifier]">
 *	   <renderer name="[renderer-name]">
 *	     <[property-name>[property-value]</[property-name]>
 *	     ...
 *	   </renderer>
 *  </response>
 * @author sonjaya
 *
 */
public class SAXEL_Response extends SAXEL_Composite {
	public static final String XCF_TAG = XCFResponse.XCF_TAG;
	
	static final String RESPONSE_TYPE = "responseType";
	static final String RENDER_CLASS = "renderClass";
	static final String RESPONSE_NAME = "responseName";
	
	public SAXEL_Response() {
		super(XCF_TAG);
	}

	@Override
	public void startInterpreting(String uri, String name, String rawName, Attributes attributes) {
		XCFFacade facade = getHandler().getFacade();
		
		SERVICE_ResponseManager responseManager =  (SERVICE_ResponseManager)facade.getService(SERVICE_ResponseManager.XCF_TAG);
		if (responseManager == null) {
			responseManager = new SERVICE_ResponseManager();
			try {
				facade.putService(SERVICE_ResponseManager.XCF_TAG, responseManager);
			} catch (XCFException e) {
				handleException(e);
				return;
			}
		}
		
		String responseName = attributes.getValue(XCFStrings.NAME);
		String clientType = attributes.getValue(RESPONSE_TYPE);
		String renderClass = attributes.getValue(RENDER_CLASS);
		
		if (responseName == null) {
			handleError("<response ..> must have a name attribute: e.g., <response name=\"account\" clientType=\"html\" renderClass=\"JSPRenderer\" >");
			return;
		}
		
		if (clientType == null) {
			handleError("<response ..> must have a clientType attribute: e.g., <response name=\"account\" clientType=\"html\" renderClass=\"JSPRenderer\" >");
			return;
		}
		
		if (renderClass == null) {
			handleError("<response ..> must have a renderClass attribute: e.g., <response name=\"account\" clientType=\"html\" renderClass=\"JSPRenderer\" >");
			return;
		}
		
		getHandler().put(RESPONSE_NAME, responseName);
		getHandler().put(RENDER_CLASS, renderClass);
		getHandler().put(RESPONSE_TYPE, clientType);
	}

	@Override
	public void endInterpreting(String uri, String name, String qName) {
		getHandler().consume(RESPONSE_NAME);
		getHandler().consume(RENDER_CLASS);
		getHandler().consume(RESPONSE_TYPE);
	}
	
	
	
}
