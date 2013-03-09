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

import java.util.Iterator;

import com.eternal.communication.IEncoder;
import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;

/**
 * Encodes an XCFRequest to a plain text format of the form
 *   ##[mod]::[operation]::[param1]::[value1]::[param2]::[value2]:: ... ::[paramN]::[valueN]##
 * For example account.login(username=ichiir, password=secret) would encode as:
 *   ##account::login::username::ichiir::password::secret##
 */
public class ENCODER_Plain implements IEncoder {
	public static final String PART_SEPARATOR = "::";
	public static final String REQUEST_SEPARATOR = "##";
	
	public static final char PART_CHAR = ':';
	public static final char REQUEST_CHAR = '#';
	
	public static final String PART_REPLACE = "~P";
	public static final String REQUEST_REPLACE = "~M";
	
	public static final String SPECIAL_CHARS = "~";
	public static final char ESCAPE = '~';
	
	public Object encode(XCFRequest req) {
		StringBuffer buf = new StringBuffer();
		buf.append(REQUEST_SEPARATOR);
		buf.append(req.getModule());
		buf.append(PART_SEPARATOR);
		buf.append(req.getOperation());
		Iterator<String> paramNames = req.getParameterNames();
		if (paramNames != null) {
			while (paramNames.hasNext()) {
				String name = paramNames.next();
				try {
					String value = req.getParameter(name);
					buf.append(PART_SEPARATOR);
					buf.append(name);
					buf.append(PART_SEPARATOR);
					
					// first escape out % and ~
					value = escape(value);
					
					// now replace #==>%P and :==>%M
					buf.append(replace(value));
				} catch (XCFException e) {
					// we only get here if parameter doesn't exist
					// so there is nothing to do as it is ok to skip
				}
			}
		}
		return buf.toString();
	}
	
	String escape(String src) {
		StringBuffer estr = new StringBuffer();
		int size = src.length();
		for (int i = 0; i < size; i++) {
			char c = src.charAt(i);
			if (SPECIAL_CHARS.indexOf(c) >= 0) {
				// escape out the character
				estr.append(ESCAPE);
				estr.append(c);
			} else {
				estr.append(c);
			}
		}
		
		return estr.toString();
	}
	
	String replace(String src) {
		StringBuffer estr = new StringBuffer();
		int size = src.length();
		for (int i = 0; i < size; i++) {
			char c = src.charAt(i);
			if (c == PART_CHAR) {
				estr.append(PART_REPLACE);
			} else if (c == REQUEST_CHAR) {
				estr.append(REQUEST_REPLACE);
			} else {
				estr.append(c);
			}
		}
		
		return estr.toString();
	}
}
