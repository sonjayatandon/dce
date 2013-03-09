/*
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
package com.eternal.communication.nio;

import java.util.ArrayList;

/**
 * describes a consumer of a Connection 
 */
public interface INIOConsumer
{
	public void receive(ArrayList messages);
    public void stateChange(int state);
}