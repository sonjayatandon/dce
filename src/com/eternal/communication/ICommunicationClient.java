/*
 * Created on Dec 31, 2004
 *
 *
 * Copyright © 2004 Eternal Adventures, Inc.
 * Licensed under the Open Software License version 1.1
 * (http://www.opensource.org/licenses/osl.php)
 * 
 * @author Sonjaya Tandon
 */
package com.eternal.communication;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.core.XCFService;


/**
 * Implemented by services used to encapsulate communication to a server.
 * 
 * The two concrete implementations provided are:
 *   SERVICE_HTTPClient for blocking request/response communction
 *   SERVICE_Nio for non-blocking asynch communication
 */
public interface ICommunicationClient extends XCFService {
	
	void connect();
	void send(XCFRequest req) throws XCFException;
	void disconnect();
	public boolean connectionOpen() throws XCFException;
}
