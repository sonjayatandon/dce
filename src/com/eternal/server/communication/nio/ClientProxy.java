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

import com.eternal.communication.nio.IConnection;
import com.eternal.communication.nio.INIOConsumer;
import com.eternal.server.clientinfo.Client;
import com.eternal.server.clientinfo.IClientStateListener;
import com.eternal.server.clientinfo.SERVICE_ClientManager;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFRequest;

/**
 */
public abstract class ClientProxy implements INIOConsumer, IClientStateListener
{
	protected static final String PARAM_CLIENT_ID = "t2";
	protected static final String PARAM_CLIENT_AUTH = "t1";

        private ClientProxyTable proxyTable;
        private String name ="";
//        private boolean signedOn = false;
//        private String[] targets;
        private int remainingTime;
        private int idleTime;
        IConnection conn;
        String clientId = null;
        XCFFacade facade;
        SERVICE_ClientManager cmgr;

        protected ClientProxy() {

        }

        /**
         * construct a user, link it to its connection and put it in the perUser table
         */
        protected ClientProxy(XCFFacade facade, IConnection c, ClientProxyTable p, int idle)
        {
                conn = c;
                conn.attach(this);
                name = conn.getName();
                proxyTable = p;
                idleTime = idle;
                remainingTime = idleTime;
                proxyTable.add(name,this);
//                targets = new String[0];
                this.facade = facade;
                cmgr = (SERVICE_ClientManager)facade.getService(SERVICE_ClientManager.XCF_TAG);
        }

        /**
         * process state changes
         */
        public void stateChange(int state)
        {
                if(state == IConnection.CLOSED)close();
        }

        public void oneSec(){
        	// do not timeout bound connections! SERVICE_ClientManager will take care of that
        	if (clientId != null) return;

        	// ok, connectionn is unbound, count it down and
        	// close it if it is timed out
        	if(idleTime != 0 && 1 > --remainingTime)
        		close();
        }

        public void send(XCFRequest req){
        	conn.send(req);
        }

        /**
         * receive string messages and distiguish between control and data
         */
        public abstract void receive(ArrayList messages);
        /**
         * delete from perUser and close our connection
         */
        protected void close()
        {
                proxyTable.delete(name);
                conn.close();
        }

	/* (non-Javadoc)
	 * @see com.eternal.server.clientinfo.IClientStateListener#handleClientClose()
	 */
	public void handleClientClose(Client client) {
		client.removeClientStateListener(this);
		client.setClientProxy(null);
		clientId = null;
		close();
	}
		/**
		 * @return Returns the clientId.
		 */
		public String getClientId() {
			return clientId;
		}
		/**
		 * @return Returns the name.
		 */
		public String getName() {
			return name;
		}
		/**
		 * @return Returns the remainingTime.
		 */
		public int getRemainingTime() {
			return remainingTime;
		}
}