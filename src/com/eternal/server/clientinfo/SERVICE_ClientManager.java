/*
 * Created on Oct 19, 2004
 *
 * Copyright © 2004 Eternal Adventures, Inc.
 * The designs and code represented by this file are a 
 * Trade Secret of Eternal Adventures and may not be used
 * without explicit permission from Eternal Adventures.
 * 
 * @author Sonjaya Tandon
 */
package com.eternal.server.clientinfo;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;

import com.eternal.xcf.common.REQUEST_Simple;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.core.XCFService;
import com.eternal.xcf.node.INodeManager;
import com.eternal.xcf.node.SERVICE_NodeContextManager;


/**
 * This service tracks all the currently connected clients.  It also generates new client ids.
 * 
 * @author standon
 *
 */
public class SERVICE_ClientManager implements XCFService {
	public static final String XCF_TAG = "client-manager";
	public static final String XCF_CLIENT_ID = "id";
	private int nextClientId = 1400; // TODO replace TEMPORARY SETTING with stored value
	private Hashtable<String, Client> clients = new Hashtable<String, Client>();
	Hashtable<String, Client> clientsByUserName = new Hashtable<String, Client>();
	private SERVICE_NodeContextManager contextMgr = null;
	Timer idleTimer;
	
	private long lastTimeoutCheck = Long.MIN_VALUE;
	
	private XCFFacade facade;
	
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.service.XCFService#start()
	 */
	public void start() throws XCFException {
		// TODO load next client id from the database
		idleTimer = new Timer();
		
		// set timer to milliseconds*seconds*minutes
		lastTimeoutCheck = System.currentTimeMillis();
		
		// set the number of minutes for the timeout
		idleTimer.scheduleAtFixedRate(new TimerTask(){public void run(){timeOutCheck();}},0,1000*60*15);
		
		contextMgr = (SERVICE_NodeContextManager)facade.getService(SERVICE_NodeContextManager.XCF_TAG);
	}
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.service.XCFService#stop()
	 */
	public void stop() throws XCFException {
	}
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.service.XCFService#getName()
	 */
	public String getName() {
		return null;
	}
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.service.XCFService#setName(java.lang.String)
	 */
	public void setName(String arg0) {
	}
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.server.XCFComponent#setServer(com.dataskill.xcf.server.XCFServer)
	 */
	public void setFacade(XCFFacade facade) {
		this.facade = facade;
	}
	
	public synchronized Enumeration getClientList() {
		Enumeration clientList = clients.elements();
		
		return clientList;
	}
	
	public synchronized int createClientId() {
		// TODO store next client id in the database
		int clientId = nextClientId;
		nextClientId++;
		return clientId;
	}
	
	public synchronized Client getClient(String clientId) throws XCFException {
		Client client;
		client = (Client)clients.get(clientId);
		if (client == null) {
			client = new Client();
			client.clientId = clientId;
			clients.put(clientId, client);
			
			// create a context for the client
			client.context = (INodeManager)contextMgr.getNewContext();
			client.context.putValue(XCF_CLIENT_ID, clientId);
			
		}
		client.lastAccessed = System.currentTimeMillis();
		return client;
	}
	
	public synchronized Client lookupClient(String clientId) {
		Client client;
		client = (Client)clients.get(clientId);
		return client;
	}
	
	public void releaseClient(String clientId) throws XCFException {
		Client client = null;
		
		synchronized(this) {
			client = (Client)clients.get(clientId);
			clients.remove(clientId);
			if (client.userName != null) {
				clientsByUserName.remove(client.userName);
			}
		}
		
		// if the client had a proxy, send a timeout message
		synchronized (client) {
			if (client.clientProxy != null) {
				XCFRequest req = new REQUEST_Simple("core","time-out");
				req.setContext(client.context);
				client.send(req);
			}
			
			client.close();
		}
	}
	
	public void releaseClient(String clientId, boolean sendTimeout) throws XCFException {
		Client client = null;
		
		synchronized(this) {
			client = (Client)clients.get(clientId);
			clients.remove(clientId);
			if (client.userName != null) {
				clientsByUserName.remove(client.userName);
			}
		}
		
		// if the client had a proxy, send a timeout message
		synchronized (client) {
			if ((client.clientProxy != null) && (sendTimeout)) {
				XCFRequest req = new REQUEST_Simple("core","time-out");
				req.setContext(client.context);
				client.send(req);
			}
		
			client.close();
		}
	}
	
	public synchronized void bindClientToUserName(String clientId, String userName) {
		Client client;
		client = (Client)clients.get(clientId);
		if (client != null) {
			synchronized(clientsByUserName) {
				client.userName = userName;
				clientsByUserName.put(userName, client);
			}
		}
	}
	
	public Client getClientByUserName(String userName) {
		Client client;
		synchronized(clientsByUserName) {
			client = (Client)clientsByUserName.get(userName);
		}
		return client;
	}
	
	public synchronized void timeOutCheck() {
		Enumeration keys = clients.keys();
		while (keys.hasMoreElements()) {
			String clientId = (String)keys.nextElement();
			Client client = (Client)clients.get(clientId);
			if (client.lastAccessed < lastTimeoutCheck) {
				try {
					releaseClient(clientId);
				} catch (XCFException e) {
					
				}
			}
		}
		lastTimeoutCheck = System.currentTimeMillis();
	}
	
	public void sendToUser(String username, XCFRequest req) throws XCFException {
		Client client = getClientByUserName(username);
		client.send(req);
	}
	
	public void sendToClient(String clientId, XCFRequest req) throws XCFException {
		Client client = lookupClient(clientId);
		if (client != null) {
			client.send(req);
		}
	}
	
	public void send(XCFRequest req) throws XCFException {
		String clientId = (String)req.getContext().getValue(XCF_CLIENT_ID);
		Client client = lookupClient(clientId);
		client.send(req);
	}
}
