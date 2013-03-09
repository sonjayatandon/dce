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
package com.eternal.client.communication.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.eternal.communication.ICommunicationClient;
import com.eternal.communication.encoders.DECODER_Plain;
import com.eternal.communication.encoders.ENCODER_Plain;
import com.eternal.communication.nio.CONN_Nio;
import com.eternal.communication.nio.IConnection;
import com.eternal.communication.nio.INIOConsumer;
import com.eternal.xcf.common.REQUESTFACTORY_Simple;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.core.response.UTIL_Helper;
import com.eternal.xcf.core.response.XCFResponse;

/**
 * Encapsulates client side NIO communication to a single NIO Server
 */
public final class SERVICE_NIOClient implements ICommunicationClient, INIOConsumer {
	
	public static final String XCF_TAG = "nio-client";
	private XCFFacade facade;
	private String name;
	private boolean poll = true;

    static int debugLevel=2;
    private Selector selector;                      // the only selector
    private SocketChannel sch;                      // and one channel
    private int idleTime;
    private String address;
    private int port;
    private IConnection conn;
   	private Timer idleTimer;
    private LinkedList<Runnable> invocations;
     
    // methods required by Client interface
    public void setAddress(String address){this.address = address;}
    public void setPort(int port){this.port = port;}
    public void setIdleTime(int idleTime){this.idleTime=idleTime;}
    public String getAddress(){return address;}
    public int getPort(){return port;}
    public int getIdleTime(){return idleTime;}
    public void connect(){invoke(new Runnable(){public void run(){runConnect();}});}
    public void disconnect(){invoke(new Runnable(){public void run(){conn.close();}});}
    
    public void send(final XCFRequest req){
    	invoke(new Runnable(){public void run(){
    		IConnection conn = getConn();
    		if (conn != null) conn.send(req);
    		}});
    }
    	
    /**
     * Launches the network connection thread -- the runnable is implemented as an inner class 
     * in the method.
     * 
	 * @see com.dataskill.xcf.service.XCFService#start()
     */
	public void start() throws XCFException {
		Runnable r = new Runnable() {
			public void run() {
	            invocations = new LinkedList<Runnable>();
	            idleTimer = new Timer();
	            
	            // The timer will track how long we go without communication on this
	            // channel of communication.
	            idleTimer.scheduleAtFixedRate(new TimerTask(){public void run(){oneSec();}},0,1000);
	            Iterator it;
	            SelectionKey key;

	            try{selector = Selector.open();}
	            catch(IOException ie)
	            {
                    ie.printStackTrace();
                    idleTimer.cancel();
	            }
	            connect();
	            while(poll)
	            {
	            	// run queued requests
                    doInvocations();
                    
                    // now we select any pending io -- this will block until 
                    // something happens (could be client wanting to send OP_WRITE
                    // or the server sending us something OP_READ)
                    try{/* n = */selector.select();}     // select
                    catch(Exception e){
//                    	Functions.fail(e,"select failed");
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
                        if((kro & SelectionKey.OP_CONNECT) == SelectionKey.OP_CONNECT)doConnect(key);
                        it.remove();                    // remove the key
                    }
	            }
			}
		};
		poll = true;
		init();
		
		Thread t = new Thread(r);
		t.start();
	}
	
	protected void init() {
		
	}
	
	/* (non-Javadoc)
	 * @see com.dataskill.xcf.service.XCFService#stop()
	 */
	public void stop() throws XCFException {
		if (poll == false) return; // already has been stopped
		poll = false;
		selector.wakeup();
		close(sch);
		conn = null;
        idleTimer.cancel();
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
	
	public synchronized boolean connectionOpen() throws XCFException {
		if (!poll) {
			start();
			return false;
		}
    	IConnection conn = getConn();
    	if (conn == null || conn.wasDisconnected()) {
    		stop();
    		
    		return false;
    	}
    	return true;
	}
	
    // run a connect request from the select loop
    private void runConnect()
    {
        String name = address+":"+port;
        
        if(conn == null)
        {
            try
            {
                sch = SocketChannel.open();
                sch.configureBlocking(false);
                sch.connect(new InetSocketAddress(address,port));
                SelectionKey sk = sch.register(selector,0);
                ENCODER_Plain me = createEncoder();
                DECODER_Plain md = createDecoder();
                setConn(new CONN_Nio(facade, sk, me, md));
                conn.setName(name);
                sk.attach(conn);                // link it all together
                conn.attach(this);
             }
            catch(Exception e)
            {
                e.printStackTrace();
                close(sch);
            }
        }
    }
    
    public final synchronized void setConn(IConnection conn) {
    	this.conn = conn;
    }
    
    public final synchronized IConnection getConn() {
    	return conn;
    }
    
    protected ENCODER_Plain createEncoder() {
    	return new ENCODER_Plain();
    }
    
    protected DECODER_Plain createDecoder() {
    	DECODER_Plain md = new DECODER_Plain();
    	md.setReqFactory(new REQUESTFACTORY_Simple());
    	return md;
    }
    

    public void close(SocketChannel sch)
    {
        try{sch.close();}catch(IOException e){e.printStackTrace();}
    }
    
   	private void invoke(Runnable d) {
        synchronized(invocations)
        {
            invocations.add(d);		// add it to our request queue
            selector.wakeup();		// break out of the select
        }
   	}
    
   	// run the invocations in our thread, these probably set the interestOps,
	// or register dispatchables
	// but they could do almost anything
	private void doInvocations()
	{
        synchronized(invocations)
        {
            while(invocations.size() > 0)((Runnable)invocations.removeFirst()).run();
        }
	}

    private void doConnect(SelectionKey sk)
    {
//      Functions.dout(2,"doConnect");
//        CONN_Nio conn = (CONN_Nio)sk.attachment();    // get our connection
        conn.doConnect();
    }

    private void doRead(SelectionKey sk)
    {
//      Functions.dout(2,"doRead");
        CONN_Nio conn = (CONN_Nio)sk.attachment();    // get our connection
        conn.doRead();
    }

    private void doWrite(SelectionKey sk)
    {
//      Functions.dout(2,"doWrite");
        CONN_Nio conn = (CONN_Nio)sk.attachment();    // get our connection
        conn.doWrite();
    }

    private void oneSec()
    {
    }
    
	/* (non-Javadoc)
	 * @see pkwnet.msgswitch.ConnectionUser#receive(java.lang.String)
	 */
	public void receive(ArrayList messages) {
		Iterator iter = messages.iterator();
		while (iter.hasNext()) {
			XCFRequest req = (XCFRequest)iter.next();
			try {
				facade.process(req);
			} catch (XCFException e) {
				facade.logError(e);
				try {UTIL_Helper.setResult(req, XCFResponse.FAILURE);} catch (XCFException e1){}
			}
		}
	}
	/* (non-Javadoc)
	 * @see pkwnet.msgswitch.ConnectionUser#stateChange(int)
	 */
	public void stateChange(int state) {
		// record the state change
	}
}
