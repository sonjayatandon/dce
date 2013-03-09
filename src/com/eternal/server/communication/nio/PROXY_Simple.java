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
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFLogger;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.node.SERVICE_NodeContextManager;

/**
 */
public class PROXY_Simple extends ClientProxy implements IClientProxyFactory
{
	SERVICE_NodeContextManager ctxMgr;
	
	public static final PROXY_Simple FACTORY = new PROXY_Simple();

	PROXY_Simple() {
		
	}
	
	PROXY_Simple(XCFFacade facade, IConnection c, ClientProxyTable p, int idle) {
		super(facade, c, p, idle);
		ctxMgr = (SERVICE_NodeContextManager)facade.getService(SERVICE_NodeContextManager.XCF_TAG);
	}
	
	public ClientProxy createClientProxy(XCFFacade facade, IConnection conn, ClientProxyTable proxyTable, int idleTime) {
		return new PROXY_Simple(facade, conn, proxyTable, idleTime);
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
         		try {
               		req.setContext(ctxMgr.getNewContext());
            		facade.process(req);
        		} catch (XCFException e) {
        			facade.logError(e);
        		} catch (Exception  ex) {
        			facade.logError( ex);
        		} catch (Throwable t) {
        			facade.getLogManager().log(null, XCFLogger.LogTypes.ERROR, t.getLocalizedMessage());
        		}
        	}
        }

}