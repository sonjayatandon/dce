/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce;

import java.util.ArrayList;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFLogger.LogTypes;
import com.eternal.xcf.core.loggers.LOGGER_Console;
import com.eternal.xcf.node.SERVICE_NodeContextManager;
import com.tandon.dce.avatar.SERVICE_AvatarPool;
import com.tandon.dce.cards.SERVICE_CardManager;
import com.tandon.dce.characterclass.MODEL_Class;
import com.tandon.dce.characterclass.SERVICE_StyleManager;

public class DCEInterface {
	protected XCFFacade facade = new XCFFacade();
	protected SERVICE_NodeContextManager cmgr = new SERVICE_NodeContextManager();
	protected SERVICE_StyleManager styleManager = new SERVICE_StyleManager();
	protected SERVICE_AvatarPool avatarPool = new SERVICE_AvatarPool();
	protected SERVICE_CardManager cardManager = new SERVICE_CardManager();

	public DCEInterface() {
		try {
			facade.putService("context-manager", cmgr);
			facade.putService(SERVICE_CardManager.XCF_TAG, cardManager);
			facade.putService(SERVICE_StyleManager.XCF_TAG, styleManager);

			facade.getLogManager().setLogger(LogTypes.DEBUG, new LOGGER_Console());
		} catch (XCFException e) {
			e.printStackTrace();
		}
	}

	// ///////////////////////////////////////
	// Style API
	public ArrayList<MODEL_Class> getStyles() throws XCFException {
		return styleManager.getStyles();
	}

	public MODEL_Class getStyle(Integer styleID) throws XCFException {
		return styleManager.getStyle(styleID);
	}

	/*
	public MODEL_Avatar loadAvatarAndBuilds(Integer avatarID) {
		MODEL_Avatar avatar = null;
		avatar = new MODEL_Avatar(facade);

		try {
			avatar.loadInstance(avatarID);
			avatar.loadBuilds();
		} catch (XCFException e) {
			facade.logError(e);
		}

		return avatar;
	}
	*/
}
