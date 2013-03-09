package com.eternal.communication.nio;

import com.eternal.xcf.core.XCFException;

public class EXCEPTION_ConnectionClosed extends XCFException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2493904744651775410L;

	public EXCEPTION_ConnectionClosed(Exception original, String msg) {
		super(original, msg);
	}
	
	public EXCEPTION_ConnectionClosed(String msg) {
		super(msg);
	}

}
