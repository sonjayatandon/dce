/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.combat;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;

public class CMD_NoOp extends CMD_BaseCommand {
	public static final String XCF_TAG = "no-op";

	@Override
	public void execute(XCFRequest request) throws XCFException {
	}

}
