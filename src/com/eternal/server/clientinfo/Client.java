/*
 * Created on Oct 19, 2004
 *
 * Copyright © 2004 Eternal Adventures, Inc.
 * Licensed under the Open Software License version 1.1
 * (http://www.opensource.org/licenses/osl.php)
 * 
 * @author Sonjaya Tandon
 */
package com.eternal.server.clientinfo;

import java.util.ArrayList;

import com.eternal.server.communication.nio.ClientProxy;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.node.INodeManager;

/**
 * @author standon
 *
 */
public final class Client {
	// The client can be in one of three states:
	//	initial: the client is in this state when it first connects.  It remains in this state
	//			 until the guest logs in.  While the client is in this state, the server will
	//		     only process account and system operations.
	//  anonymous: we set the client to this state when we detect that the client has lost
	//           its network connection and is trying to relog back in.  The client will remain
	//           in this state until the guest logs in.  When the guest logs in, and the guest 
	//           was playing a game, the server will resynchronize the client so that the guest
	//           can resume the game.
	//  authorized: the client is in this state while the guest is logged into the game network.
	public static final int STATE_INITIAL = 0;
	public static final int STATE_ANONYMOUS = 1;
	public static final int STATE_AUTHORIZED = 2;
	
	ArrayList nioConnections;
	
	public String clientId;
	public int state = STATE_INITIAL;
	public INodeManager context = null;
	public String authorizationKey = null;
	public String userName = null;
	long lastAccessed;
	
	ClientProxy clientProxy;
	
	ArrayList<IClientStateListener> stateListeners = new ArrayList<IClientStateListener>();
	
	public void setClientProxy(ClientProxy clientProxy) {
		if (clientProxy != null) {
			addClientStateListener(clientProxy);
		}
		this.clientProxy = clientProxy;
	}
	
	public void addClientStateListener(IClientStateListener l) {
		stateListeners.add(l);
	}
	
	public void removeClientStateListener(IClientStateListener l) {
		stateListeners.remove(l);
	}
	
	public void close() {
		IClientStateListener[] l = new IClientStateListener[stateListeners.size()];
		for (int j = 0; j < l.length; j++) {
			try {
				l[j] = stateListeners.get(j);
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
		
		for (int i = 0; i < l.length; i++) {
			l[i].handleClientClose(this);
		}
		
		stateListeners.clear();
	}
	
	public void send(XCFRequest req) throws XCFException {
		if (clientProxy != null) {
			clientProxy.send(req);
		}
	}
}
