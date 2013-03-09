package com.tandon.dce.combat;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFRequest;
import com.eternal.xcf.core.response.UTIL_Helper;

public class CMD_SetResult extends CMD_BaseCommand {
	public static final String XCF_TAG = "set-result";

	@Override
	public void execute(XCFRequest request) throws XCFException {
		String result = getString(request, "result", XCF_TAG);
	
		UTIL_Helper.setResult(request, result);
	}

}
