/*
 * Created on Jan 1, 2005
 *
 * Copyright © 2005 Eternal Adventures, Inc.
 * Licensed under the Open Software License version 1.1
 * (http://www.opensource.org/licenses/osl.php)
 * 
 * The NIO classes are derived from a set of excellent posts by pkwooster:
 * http://forum.java.sun.com/thread.jspa?forumID=11&threadID=531770
 * 
 * @author Sonjaya Tandon
 */
package com.eternal.server.communication.nio;

import java.util.ArrayList;
import java.util.Iterator;

import com.eternal.communication.nio.IConnection;
import com.eternal.server.clientinfo.Client;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFLogger;
import com.eternal.xcf.core.XCFRequest;

/**
 */
public class PROXY_PersitedContext extends ClientProxy implements IClientProxyFactory
{
	public static final PROXY_PersitedContext FACTORY = new PROXY_PersitedContext();

	PROXY_PersitedContext() {
		
	}
	
	PROXY_PersitedContext(XCFFacade facade, IConnection c, ClientProxyTable p, int idle) {
		super(facade, c, p, idle);
	}
	
	public ClientProxy createClientProxy(XCFFacade facade, IConnection conn, ClientProxyTable proxyTable, int idleTime) {
		return new PROXY_PersitedContext(facade, conn, proxyTable, idleTime);
	}

        
        /**
         * receive string messages and distiguish between control and data
         */
        public void receive(ArrayList messages) {
        	// lookup the client id to get the context
        	// assoicated to the client
        	
        	Iterator iter = messages.iterator();
        	while (iter.hasNext()) {
        		XCFRequest req = (XCFRequest)iter.next();
        		// set the context on the request
        		try {
                	String clientId = req.getParameter(PARAM_CLIENT_ID);
                	String authorizationKey = req.getParameter(PARAM_CLIENT_AUTH);
                	if ((clientId == null) || (authorizationKey == null)) {
                		// hmm, this is a bogus message, close the connection
                		if (this.clientId == null) {
                			// the connection is unbound, so just close it
                			close();
                		} else {
                			// we have a bound connection -- could be we are being hacked.
                			// release the client  (this send a timeout, unbind it
                			// and close the connection) 
                			cmgr.releaseClient(this.clientId); 
                		}
                		return;
                	}
                	
                	Client client = cmgr.getClient(clientId); 
                	if (client == null) {
                		// we must have timed out the client prior to 
                		// binding, close this connection
                		close();
                		return;
                	}
                	
                	synchronized (client) {
                    	req.setContext(client.context);
                    	
                    	if (!authorizationKey.equals(client.authorizationKey) ) {
                    		// it is the correct client id, but wrong authorization info
                    		// release the client the client
                    		cmgr.releaseClient(clientId);
                    		return;
                    	}
                    	
                    	if (this.clientId == null) {
                    		// this is the first message from the client, 
                    		// bind this proxy to the client
                    		// add self as listener to client close
                    		this.clientId = clientId;
                    		client.setClientProxy(this);
                    	}
                    	
                    	// now process the request
            			facade.process(req);
                	}
        		} catch (XCFException e) {
        			facade.getLogManager().log(null, XCFLogger.LogTypes.ERROR, e);
        		} catch (Exception  ex) {
        			facade.getLogManager().log(null, XCFLogger.LogTypes.ERROR, ex);
        		} catch (Throwable t) {
        			facade.getLogManager().log(null, XCFLogger.LogTypes.ERROR, t.getLocalizedMessage());
        		}
        	}
        }

}