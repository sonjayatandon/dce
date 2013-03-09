/*
 * Created on Jan 5, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package com.eternal.xcf.common;

import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.core.XCFRequestFactory;
import com.eternal.xcf.core.XCFStrings;


/**
 * @author standon
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
public class REQUESTFACTORY_Simple implements XCFRequestFactory {
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.server.XCFRequestFactory#newRequestInstance()
	 */
	public XCFRequest newRequestInstance() {
		return new REQUEST_Simple(XCFStrings.EMPTY, XCFStrings.EMPTY);
	}
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.server.XCFRequestFactory#newRequestInstance(java.lang.String, java.lang.String)
	 */
	public XCFRequest newRequestInstance(String module, String operation) {
		XCFRequest req = new REQUEST_Simple(module, operation);
		return req;
	}
}
