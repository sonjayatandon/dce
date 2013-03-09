/*
 * Created on Jan 1, 2005
 * 
 * Copyright © 2005 Eternal Adventures, Inc.
 * Licensed under the Open Software License version 1.1
 * (http://www.opensource.org/licenses/osl.php)
 * 
 * @author standon
 */
package com.eternal.communication;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;


/**
 */
public interface IEncoder {
	/**
	 * Encodes an XCFRequest into an object that will be written to the 
	 * network channel.
	 * @param req
	 * @return
	 */
	Object encode(XCFRequest req) throws XCFException;
}
