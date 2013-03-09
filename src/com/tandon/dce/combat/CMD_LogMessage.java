package com.tandon.dce.combat;

import java.util.ArrayList;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFLogger;
import com.eternal.xcf.core.XCFRequest;

public class CMD_LogMessage extends CMD_BaseCommand {
	public static final String XCF_TAG = "log-message";

	@SuppressWarnings("unchecked")
	@Override
	public void execute(XCFRequest request) throws XCFException {
		ArrayList<String> textLog = (ArrayList<String>)request.getContext().getValue("text-log");
		XCFLogger logger = request.getContext().getFacade().getLogManager().getLogger("combat");
		
		if (textLog != null)
		{
			String message = getString(request, "message", XCF_TAG);
			
			if (message.indexOf("[") != -1)
			{
				int lastSearch = 0;
				// There exists at least 1 key.
				int tokenStartIndex = message.indexOf('[', lastSearch);
				while (tokenStartIndex != -1) {
					String token = message.substring(tokenStartIndex+1,message.indexOf("]", tokenStartIndex));
					lastSearch += token.length();
					Object value = 0;
					if (isFormula(token)) {
						value = calcFormula(request, token, XCF_TAG);
					} else if (isArray(token)) {
						value =  arrayLookup(request, token, XCF_TAG);
					} else if (isVar(token)) {
						value =  varLookup(request, token, XCF_TAG);
					}
					message = message. replace("["+token+"]", value.toString());
					tokenStartIndex = message.indexOf('[', lastSearch);
				}
			}
			
			textLog.add(message);
			logger.logMessage(request.getContext(), XCFLogger.LogTypes.INFO, message);
		}
	}

}
