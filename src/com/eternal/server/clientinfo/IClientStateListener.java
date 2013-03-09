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

/**
 */
public interface IClientStateListener {
	void handleClientClose(Client client);
}
