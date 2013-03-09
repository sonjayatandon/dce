/*
 * Created on Jan 1, 2005
 *
 * Copyright © 2005 Eternal Adventures, Inc.
 * Licensed under the Open Software License version 1.1
 * (http://www.opensource.org/licenses/osl.php)
 * 
 * @author standon
 */
package com.eternal.xcf.core;

/**
 */
public interface XCFRequestFactory {
	XCFRequest newRequestInstance();
	XCFRequest newRequestInstance(String module, String operation);
}
