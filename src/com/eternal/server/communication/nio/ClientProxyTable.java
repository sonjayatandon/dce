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

/**
 */
@SuppressWarnings("serial")
public class ClientProxyTable extends java.util.TreeMap
{
        public synchronized ClientProxy get(String name){return (ClientProxy)super.get(name);}

        @SuppressWarnings("unchecked")
		public synchronized boolean add(String name, ClientProxy user)
        {
                if(containsKey(name))return false;      // don't reuse
                {
                        put(name, user);
                        return true;
                }
        }

        public synchronized void delete(String name){remove(name);}

        public synchronized boolean rename(String oldName, String newName)
        {
                if(oldName.equals(newName))return true;
                ClientProxy u = get(oldName);
                if(containsKey(newName) || u == null)return false;
                else
                {
                        remove(oldName);
                        add(newName,u);
                }
                return true;
        }

        public synchronized ClientProxy[] allClients()
        {
                Object [] ov = this.values().toArray();
                ClientProxy [] uv = new ClientProxy[ov.length];
                for(int i=0; i<ov.length;i++)uv[i]=(ClientProxy)ov[i];
                return uv;
        }
}