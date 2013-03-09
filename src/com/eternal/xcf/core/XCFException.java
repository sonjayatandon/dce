/*
 * Copyright 2003 Dataskill, Inc.
 *
 *  Licensed under the Eclipse License, Version 1.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * 
 * @author Sonjaya Tandon
 */
package com.eternal.xcf.core;

/**
 * Base exception class for the xcf system
 * 
 * Creation date: (9/13/2001 10:36:59 AM)
 * @author: Sonjaya Tandon
 */
public class XCFException extends Exception {
	private static final long serialVersionUID = 5674131086115249254L;
	
	/**
	 * Constsruct an XCFException using the message passed in.
	 * @param s java.lang.String
	 */
	public XCFException(String s) {
		super(s);
	}

	/**
	 * Constsruct an XCFException using the message passed in. Retain the cause.
	 * 
	 * @param original
	 * @param msg
	 */
	public XCFException(Exception original, String msg) {
		super(msg);
		super.initCause(original);
	}
	
}
