package com.eternal.server.communication.nio;

import com.eternal.communication.nio.IConnection;
import com.eternal.xcf.core.XCFFacade;

public interface IClientProxyFactory {
	ClientProxy createClientProxy(XCFFacade facade, IConnection conn, ClientProxyTable proxyTable, int idleTime);
}
