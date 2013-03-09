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

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.eternal.communication.encoders.DECODER_Plain;
import com.eternal.communication.encoders.ENCODER_Plain;
import com.eternal.communication.nio.CONN_Nio;
import com.eternal.communication.nio.IConnection;
import com.eternal.xcf.common.REQUESTFACTORY_Simple;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFLogger;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.core.XCFService;

/**
 * Encapsulates service side NIO communication to mulitple NIO Clients.
 */
public final class SERVICE_NIOServer implements XCFService {
	public static final String XCF_TAG = "nio-server";
	private XCFFacade facade;
	private String name;

	private Timer idleTimer;
	private ServerSocket ss;			// the listening socket
	private ServerSocketChannel sschan; 		// the listening channel
	private Selector selector;			// the only selector
	private int bufsz = 8192;
	private int idleTime = 6;			// # of 5 minute increments to wait
	private int port = 5050;
	
	private ClientProxyTable proxyTable = new ClientProxyTable();
	private IClientProxyFactory clientProxyFactory;
	
	private ENCODER_Plain me = new ENCODER_Plain();
	private DECODER_Plain md = new DECODER_Plain();
	
	private boolean poll = true;
	
	private Thread pollingThread;
	
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.service.XCFService#start()
	 */
	public void start() throws XCFException {
		Runnable r = new Runnable() {
			public void run() {
				idleTimer = new Timer();
				idleTimer.scheduleAtFixedRate(new TimerTask(){public void run(){oneSec();}},0,1000*300);
		 
//				int n=0;
				Iterator it;
				SelectionKey key;
//				Object att;
//				int io;

				try {
				try
				{
					sschan = ServerSocketChannel.open();
					sschan.configureBlocking(false);
					ss = sschan.socket();
					ss.bind(new InetSocketAddress(port));
					selector = Selector.open();
					sschan.register(selector, SelectionKey.OP_ACCEPT);
				}
				catch(IOException ie)
				{
					ie.printStackTrace();
					idleTimer.cancel();
					return;
				}
		 
				while(getPoll())
				{
					// now we select any pending io
					try{/* n = */ selector.select();}	// select
					catch(Exception e){
					}
		 
					// process any selected keys
					Set selectedKeys = selector.selectedKeys();
					it = selectedKeys.iterator();
					while(it.hasNext())
					{
						key = (SelectionKey)it.next();
						int kro = key.readyOps();
						if((kro & SelectionKey.OP_READ) == SelectionKey.OP_READ)doRead(key);
						if((kro & SelectionKey.OP_WRITE) == SelectionKey.OP_WRITE)doWrite(key);
						if((kro & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT)doAccept(key);
						it.remove();			// remove the key
					}
				}
				} finally {
					try {
						if (selector != null && selector.isOpen()) {
							selector.close();
						}
					} catch (IOException e) {
						
					}
					try
					{
						if(ss != null && !ss.isClosed()) {
							ss.close();
						}
					}
					catch(IOException ie)
					{
					}
					try {
					if (sschan != null && sschan.isOpen()) {
						sschan.close();
					}
					} catch (IOException ie) {
					}
				}
			}
		};
		
		// initialize the decoder
		md.setReqFactory(new REQUESTFACTORY_Simple());
		
		// launch the thread that will check for NIO events
		pollingThread = new Thread(r);
		pollingThread.start();
	}
	
	public void waitUntilDone() {
		try {
			pollingThread.join();
		} catch (InterruptedException e) {
			facade.logError(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.service.XCFService#stop()
	 */
	public void stop() throws XCFException {
		stopPoll();
		pollingThread.interrupt(); // this will trigger the finally block in the polling thread
	}
	
	synchronized boolean getPoll() {
		return poll;
	}
	
	synchronized void stopPoll() {
		poll = false;
	}
	
	
	
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.service.XCFService#getName()
	 */
	public String getName() {
		return name;
	}
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.service.XCFService#setName(java.lang.String)
	 */
	public void setName(String name) {
		this.name = name;
	}
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.server.XCFComponent#setServer(com.dataskill.xcf.server.XCFServer)
	 */
	public void setFacade(XCFFacade facade) {
		this.facade = facade;
	}
	
	public void setClientProxyFactory(IClientProxyFactory clientProxyFactory) {
		this.clientProxyFactory = clientProxyFactory;
	}

	public void send(String clientId, XCFRequest req) {
		ClientProxy cp = proxyTable.get(clientId);
		
		if (cp != null) {
			cp.send(req);
		}
	}
	
	public void broadcast(XCFRequest req) {
    	ClientProxy[] cp = proxyTable.allClients();
        for(int i=0; i<cp.length; i++) {
        	cp[i].send(req);
        }
	}
	
	/* (non-Javadoc)
	 * A client wants to open a connection to this NIO Server.
	 * Open a network socket to that client, create a selection key for the client
	 * and register that key in nio channel.
	 * Create a ClientProxy and NIO_Connection and link them.
	 * Attach to NIO_Connection to the client's selection key. 
	 */
	private void doAccept(SelectionKey sk)
	{
		ServerSocketChannel sc = (ServerSocketChannel)sk.channel();
		SocketChannel usc = null;
		ByteBuffer data;
		try
		{
			usc = sc.accept();
			usc.configureBlocking(false);
			Socket sock = usc.socket();
			String nm = sock.getInetAddress()+":"+sock.getPort();
			sock.setKeepAlive(true);
			data = ByteBuffer.allocate(bufsz);
			data.position(data.limit()); // looks like write complete
 			SelectionKey dsk = usc.register(selector, SelectionKey.OP_READ|SelectionKey.OP_WRITE,null);
			IConnection conn = new CONN_Nio(facade, dsk, me, md);	// contains socket i/o code
			conn.setName(nm);
			clientProxyFactory.createClientProxy(facade, conn, proxyTable, idleTime);
			dsk.attach(conn);				// link it to the key so we can find it
 		}
		catch(IOException re){
			facade.getLogManager().log(null, XCFLogger.LogTypes.ERROR, re);
		} catch (Exception ex) {
			facade.getLogManager().log(null, XCFLogger.LogTypes.ERROR, ex);
		}
	}
 
	/* (non-Javadoc)
	 * A client has sent us a message, get the CONN_Nio associated to that client
	 * and process the message.
	 */
	private void doRead(SelectionKey sk)
	{
		CONN_Nio conn = (CONN_Nio)sk.attachment();	// get our connection
		conn.doRead();
	}
 
	/* (non-Javadoc)
	 * We have been requested to send a message to a specific client.
	 * Get the CONN_Nio associated with that client and use it to 
	 * send the message.
	 */
	private void doWrite(SelectionKey sk)
	{
		CONN_Nio conn = (CONN_Nio)sk.attachment();	// get our connection
		conn.doWrite();
	}
	
    private void oneSec()
    {
    	// clear out all unbound clients older than 5 minutes   	
    	ClientProxy[] cp = proxyTable.allClients();
        for(int i=0; i<cp.length; i++) {
        	cp[i].oneSec();
        }
    }
    
    public ClientProxy[] getClientProxyList() {
    	ClientProxy[] cp = proxyTable.allClients();
    	return cp;
    }
	
}
