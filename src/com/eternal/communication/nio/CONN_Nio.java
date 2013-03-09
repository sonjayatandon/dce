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

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.LinkedList;

import com.eternal.communication.IDecoder;
import com.eternal.communication.IEncoder;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFRequest;

/**
 * Encapsulates the relationship between a spefic nio connection (identified by the selection key)
 * and the consumer of the connection.
 * The connection notifies the consumer of any state changes in the nio connection.  
 * On OP_READ (incomming messages), the connection will decode the network stream into 
 * an array list of XCFRequests.  It will then send those requests to the consumer for processing. 
 * will pass any read operations
 * For OP_WRITE, the connection will encode the posted XCFRequest for network communication and
 * use the information in selection key to send the request.  
 * On the client side, this consumer is typically SERVICE_NIOClient.  
 * On the client side, there is usually only one CONN_Nio for each server that the client connects to.
 * On the server side, this consumer is typically ClientProxy.
 * On the server side, there is usually a CONN_Nio for each client that has requested a connection.
 * 
 */
public final class CONN_Nio implements IConnection
{
	private SelectionKey sk;
	private INIOConsumer consumer;
	private int state;
	private LinkedList<String> sendQ = new LinkedList<String>();
	
	private CharsetEncoder encoder;
	private CharsetDecoder decoder;
	private ByteBuffer recvBuffer=null;
	private ByteBuffer sendBuffer=null;
	private StringBuffer recvString = new StringBuffer();
	private boolean writeReady = false;
	private String name="";
	
	private XCFFacade facade;
	private IEncoder msgencoder;
	private IDecoder msgdecoder;
	
	/**
	 * construct a NIOConnection from a selection key
	 */
	public CONN_Nio(XCFFacade facade, SelectionKey sk, IEncoder me, IDecoder md) {
		SocketChannel sch = (SocketChannel)sk.channel();
		this.facade = facade;
		this.msgdecoder = md;
		this.msgencoder = me;
		
		if(sch.isConnected())        // connected immediatedly if local on *nix
		{        
			sk.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);
			state = IConnection.OPENED;
		}
		else if(sch.isConnectionPending())
		{
			sk.interestOps(SelectionKey.OP_CONNECT);
			state = IConnection.OPENING;
		}               
		
		// link this to the key
		this.sk = sk;		
		sk.attach(this);  
		
		Charset charset = Charset.forName("ISO-8859-1");
		decoder = charset.newDecoder();
		encoder = charset.newEncoder();
		recvBuffer = ByteBuffer.allocate(8196);
	}
	
	/**
	 * attach a connection user to this connection
	 */
	public void attach(INIOConsumer consumer)
	{
		this.consumer = consumer;
	}
	
	/**
	 * process a connect complete selection
	 */
	public void doConnect()
	{
		SocketChannel sc = (SocketChannel)sk.channel();
		try
		{
			sc.finishConnect();
			sk.interestOps(SelectionKey.OP_WRITE);	// select OP_WRITE
			
			state = IConnection.OPENED;
			if(consumer != null)consumer.stateChange(state);
		}
		catch(IOException e)
		{
			// this will happen if we attempt to connect to 
			// something that isn't running yet.
			// only log this in debug -- it is actually expected
			// to get here from time to time in production
			facade.logDebug(e);
			closeComplete();
		}
	}
	
	/**
	 * process a read ready selection
	 */
	public void doRead()
	{
		SocketChannel sc = (SocketChannel)sk.channel();
		if(sc.isOpen())
		{
			int len;
			recvBuffer.clear();
			try{len = sc.read(recvBuffer);}
			catch(IOException e){facade.logError(e); len=-1;} // error look like eof
			
			if(len > 0)
			{
				recvBuffer.flip();
				CharBuffer buf = null;
				try{
					buf = decoder.decode(recvBuffer);
					toConsumer(buf);
				}	// convert bytes to chars
				catch(Exception ce){ce.printStackTrace(); len = -1;}
			}
			if(len < 0)closeComplete();
		}
		else facade.logError("read closed");
	}
	/* 
	 * split up received data and forward it to the user
	 */
	private void toConsumer(CharBuffer buf) throws XCFException
	{
		if(buf != null)
		{
			recvString.append(buf);			// as a string buffer
			int z = recvString.length();
			if(z > 0) {
				ArrayList messages = msgdecoder.decode(recvString.toString());
				consumer.receive(messages);
				recvString = new StringBuffer();
			}
		}
	}
	
	/**
	 * process a write ready selection
	 */
	public void doWrite()
	{
		sk.interestOps(SelectionKey.OP_READ);		// deselect OP_WRITE
		writeReady = true;				// write is ready
		if(sendBuffer != null)write(sendBuffer);	// may have a partial write
		writeQueued();					// write out rest of queue
	}	
	/**
	 * queue up a text string to send and try to send it out
	 */
	public void send(XCFRequest req)
	{
		try {
			String text = (String)msgencoder.encode(req);
			sendQ.add(text);	// first put it on the queue
			writeQueued();		// write all we can from the queue
		} catch (XCFException e) {
			facade.logError(e);
		}
	}
	
	/*
	 * attempt to send all queued data
	 */
	private void writeQueued()
	{
		while(writeReady && sendQ.size() > 0)	// now process the queue
		{
			String msg = (String)sendQ.remove(0);
			write(msg);	// write the string
		}
	}
	
	/* 
	 * convert a text string to a byte buffer and send it
	 */
	private void write(String text)
	{
		try
		{
			ByteBuffer buf = encoder.encode(CharBuffer.wrap(text));
			write(buf);
		}
		catch(Exception e){facade.logError(e);}
	}
	
	/*
	 * write out a byte buffer
	 */
	private void write(ByteBuffer data)
	{
		SocketChannel sc = (SocketChannel)sk.channel();
		if(sc.isOpen())
		{
			if(data.hasRemaining())
			{
				try{/*len = */sc.write(data);}
				catch(IOException e){facade.logError(e); closeComplete();}
			}
			if(data.hasRemaining())			// write would have blocked
			{
				writeReady = false;
				sk.interestOps(SelectionKey.OP_READ | SelectionKey.OP_WRITE);	// select OP_WRITE
				sendBuffer = data;		// save the partial buffer
			}
			else sendBuffer = null;
		} 
	}
	
	public boolean wasDisconnected() {
		if (state != IConnection.OPENED) return true;
		
		SocketChannel sc = (SocketChannel)sk.channel();
		if(sc.isOpen()) {
			return false;
		}
		return true;
	}
	
	/*
	 * close the connection and its socket
	 */
	public void close()
	{	
		if(state != IConnection.CLOSED)
		{
			SocketChannel sc = (SocketChannel)sk.channel();
			if(sc.isOpen())
			{
				if(state == IConnection.OPENED)  // open attempt graceful shutdown
				{
					state = IConnection.CLOSING;
					Socket sock = sc.socket();
					try{sock.shutdownOutput();}
					catch(IOException se)
					{
						facade.logError(se);
					}
					if(consumer != null)consumer.stateChange(state);
				}
				else closeComplete();
			}
		}
	}
	
	// called internally if already closing or closed by partner
	private void closeComplete()
	{
		try
		{
			sk.interestOps(0);
			SocketChannel sc = (SocketChannel)sk.channel();
			if(sc != null && sc.isOpen())sc.close();
			sk.selector().wakeup();
			sk.attach(null);
		}
		catch(IOException ce){
		} catch (Throwable t) {
			// we tried our best to close, we get here
			// if the selection key is already invalid -- 
			// this happens when the other end terminates w/out sending us a close
		}
		state = IConnection.CLOSED;
		if(consumer != null)consumer.stateChange(state);
	}
	
	public String getName(){return name;}
	public void setName(String nm){name = nm;}
	public int getState(){return state;}
	public XCFFacade getFacade(){return facade;}
}