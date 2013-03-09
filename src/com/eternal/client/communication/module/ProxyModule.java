/*
 * Created on Dec 31, 2004
 *
 * Copyright © 2004 Eternal Adventures, Inc.
 * Licensed under the Open Software License version 1.1
 * (http://www.opensource.org/licenses/osl.php)
 * 
 * @author Sonjaya Tandon
 */
package com.eternal.client.communication.module;

import com.eternal.communication.ICommunicationClient;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFModule;
import com.eternal.xcf.core.XCFRequest;

/**
 * Forwards requests to a provider on the network.  Uses 
 * an ICommunictionClient to send the request.
 *
 */
public class ProxyModule implements XCFModule {
	private XCFFacade facade;
	private String name;
	private String defaultOperation;
	private ICommunicationClient comm;
	
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.server.XCFComponent#setServer(com.dataskill.xcf.server.XCFServer)
	 */
	public void setFacade(XCFFacade facade) {
		this.facade = facade;
	}
	public XCFFacade getFacade() {
		return facade;
	}
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.module.XCFModule#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.module.XCFModule#getName()
	 */
	public String getName() {
		
		return name;
	}
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.module.XCFModule#getDefaultOperation()
	 */
	public String getDefaultOperation() {
		return defaultOperation;
	}
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.module.XCFModule#setDefaultOperation(java.lang.String)
	 */
	public void setDefaultOperation(String defaultOperation) {
		this.defaultOperation = defaultOperation;
	}
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.module.XCFModule#process(com.dataskill.xcf.server.XCFRequest, java.lang.String)
	 */
	public void process(XCFRequest req) throws XCFException {
		comm.send(req);
	}
	
	/**
	 * @return Returns the comm.
	 */
	public ICommunicationClient getComm() {
		return comm;
	}
	/**
	 * @param comm The comm to set.
	 */
	public void setComm(ICommunicationClient comm) {
		this.comm = comm;
	}
}
