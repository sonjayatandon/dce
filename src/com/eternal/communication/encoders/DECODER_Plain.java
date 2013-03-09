/*
 * Created on Dec 31, 2004
 * 
 * Copyright © 2004 Eternal Adventures, Inc.
 * Licensed under the Open Software License version 1.1
 * (http://www.opensource.org/licenses/osl.php)
 * 
 * @author standon
 */
package com.eternal.communication.encoders;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import com.eternal.communication.IDecoder;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.core.XCFRequestFactory;

/**
 * Decodes a message into an ArrayList of XCFRequests
 * The message syntax is:
 *   MESSAGE == (##)*REQUEST(#*[REQUEST])*(##)*
 *   REQUEST == [mod]::[operation](::[parameter name]::[parameter value])*
 * For example account.login(username=ichiir, password=secret) would encode as:
 *   ##account::login::username::ichiir::password::secret##
 */
public class DECODER_Plain implements IDecoder {
	public static final String PART_SEPARATOR = "::";
	public static final String REQUEST_SEPARATOR = "##";
	
	public static final char PART_CHAR = ':';
	public static final char REQUEST_CHAR = '#';
	
	public static final char PART_REPLACE = 'P';
	public static final char REQUEST_REPLACE = 'M';
	
	public static final String SPECIAL_CHARS = "~";
	public static final char ESCAPE = '~';
	
	private XCFRequestFactory reqFactory;
	
	/**
	 * Converts a plain formated message to an xcf request
	 * The message is expected to be of the form:
	 *   module::operation[::paramName::paramValue]*
	 * For example:
	 *   account::login::ichiir::secret
	 * @param response
	 * @return
	 * @throws XCFException
	 */
	public XCFRequest messageToRequest(String message) throws XCFException {
		StringTokenizer tokenizer = new StringTokenizer(message, PART_SEPARATOR);
		XCFRequest req = reqFactory.newRequestInstance();
		try {
			String module = tokenizer.nextToken().trim();
			String operation = tokenizer.nextToken().trim();
			req.setModule(module);
			req.setOperation(operation);
			while (tokenizer.hasMoreElements()) {
				String paramName = tokenizer.nextToken().trim();
				String paramValue = tokenizer.nextToken().trim();
				
				// unescape parameter value
				paramValue = unescape(paramValue);
					
				req.setParameter(paramName, paramValue);
			}
			return req;
		} catch (NoSuchElementException e) {
			throw new XCFException("DECODER_Plain:: malformed resonse!:: || " + message);
		}
	}
	
	private String unescape(String src) {
		StringBuffer estr = new StringBuffer();
		int size = src.length();
		for (int i = 0; i < size; i++) {
			char c = src.charAt(i);
			if (c == ESCAPE) {
				// skip past the escape char
				i++;
				c = src.charAt(i);
				if (c == PART_REPLACE) {
					estr.append(PART_CHAR);
				} else if (c == REQUEST_REPLACE) {
					estr.append(REQUEST_CHAR);
				} else {
					estr.append(c);
				}
			} else {
				estr.append(c);
			}
		}
		
		return estr.toString();
	}
	
	/**
	 * Converts a plain formatted message list to an array list of xcf requests.
	 * The message list is expected to be of the form:
	 *   (##)*REQUEST(##*[REQUEST])*(##)*
	 * For example:
	 *   core::load::module::account##account::login::username::ichiir::password::secret
	 * @param messageList
	 * @return
	 * @throws XCFException
	 */
	public ArrayList<XCFRequest> decode(Object messageList) throws XCFException {
		String smlist = (String)messageList;
		StringTokenizer tokenizer = new StringTokenizer(smlist, REQUEST_SEPARATOR);
		ArrayList<XCFRequest> requestList = new ArrayList<XCFRequest>();
		while (tokenizer.hasMoreElements()) {
			String message = tokenizer.nextToken();
			if (message.trim().length() > 0) { 
				XCFRequest req = messageToRequest(message);
				requestList.add(req);
			}
		}
		return requestList;		
	}
	
	/**
	 * @param reqFactory The reqFactory to set.
	 */
	public void setReqFactory(XCFRequestFactory reqFactory) {
		this.reqFactory = reqFactory;
	}
}
