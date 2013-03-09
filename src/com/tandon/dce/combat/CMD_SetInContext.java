package com.tandon.dce.combat;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;

public class CMD_SetInContext extends CMD_BaseCommand {
	public static final String XCF_TAG = "set-in-context";

	@Override
	public void execute(XCFRequest request) throws XCFException {
		String name = getString( request, "name", XCF_TAG);
		String value = getString(request, "value", XCF_TAG);
	
		request.getContext().putValue(name, value);
	}

}
