package com.eternal.communication.nio;

import com.eternal.xcf.core.XCFRequest;


/**
 * Created on Dec 31, 2004
 *
 * Copyright © 2004 Eternal Adventures, Inc.
 * Licensed under the Open Software License version 1.1
 * (http://www.opensource.org/licenses/osl.php)
 * 
 * The NIO classes are derived from a set of excellent posts by pkwooster:
 * http://forum.java.sun.com/thread.jspa?forumID=11&threadID=531770
 * 
 * @author Sonjaya Tandon
 */
public interface IConnection
{
        static public final int CLOSED=0;
        static public final int OPENING=1;
        static public final int OPENED=2;
        static public final int CLOSING=3;
        
        public void attach(INIOConsumer cu);
        public void send(XCFRequest req);
        public void close();
        public int getState();
        public void setName(String name);
        public String getName();
        public boolean wasDisconnected();
        public void doConnect();
}
