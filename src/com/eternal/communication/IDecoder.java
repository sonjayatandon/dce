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

import java.util.ArrayList;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequestFactory;


/**
 */
public interface IDecoder {
	/**
	 * Decodes the messages in messageList into an array list of XCFRequests
	 * Uses the request factory to create the request
	 * @param messageList
	 * @return
	 * @throws XCFException
	 */
	public ArrayList decode(Object messageList) throws XCFException;

	/**
	 * Sets the request factored used to create requests
	 * during the decoding process.
	 * @param reqFactory
	 */
	public void setReqFactory(XCFRequestFactory reqFactory);
}
