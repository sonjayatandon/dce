/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.avatar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFService;
import com.tandon.dce.common.db.IResultSetVisitor;
import com.tandon.dce.common.db.SERVICE_ConnectionPool;

public class SERVICE_AvatarPool implements XCFService {
	public static final String XCF_TAG = "avatar-pool";

	XCFFacade facade;

	HashMap<String, AvatarPool> pools = new HashMap<String, AvatarPool>();

	public String getName() {
		return null;
	}

	public void setFacade(XCFFacade facade) {
		this.facade = facade;
	}

	public void setName(String name) {
	}

	public void start() throws XCFException {
		for (AvatarPool pool: pools.values()) {
			pool.start();
		}
	}

	public void stop() throws XCFException {
		for (AvatarPool pool: pools.values()) {
			pool.stop();
		}
	}

	public void createPool(String poolName, String sql, int refresh) {
		pools.put(poolName, new AvatarPool(sql, refresh));
	}

	class AvatarPool {
		final String sql;
		final int refreshRate;
		final Timer idleTimer = new Timer();
		ArrayList<MODEL_Avatar> avatars;

		AvatarPool(String sql, int refreshRate) {
			this.sql = sql;
			this.refreshRate = refreshRate;
		}

		public void start() {
			idleTimer.scheduleAtFixedRate(new TimerTask(){public void run(){loadAvatars();}},0,1000*refreshRate);
		}

		public void stop() {
			idleTimer.cancel();
		}

		public void loadAvatars()  {
			AvatarVisitor visitor = new AvatarVisitor();

			try {
			SERVICE_ConnectionPool.getDBM(facade).select(sql, null,visitor);

				for (MODEL_Avatar a: visitor.avatars) {
					a.loadAttributes();
				}
			} catch  (Exception e) {e.printStackTrace();}
			avatars = visitor.avatars;
		}

		synchronized void setAvatars(ArrayList<MODEL_Avatar> l) {
			avatars = l;
		}

		synchronized ArrayList<MODEL_Avatar> getAvatars() {
			return avatars;
		}

	}

	class AvatarVisitor implements IResultSetVisitor {
		ArrayList<MODEL_Avatar> avatars = new ArrayList<MODEL_Avatar>();
		public void load(ResultSet rs) throws SQLException {
			MODEL_Avatar avatar = new MODEL_Avatar(facade, rs);
			avatars.add(avatar);
		}
	}

	/////////////////////////////////////////////////////////////////
	// List access

	public ArrayList<MODEL_Avatar> getPool(String poolName, MODEL_Avatar avatar, boolean includeAvatar) {
		AvatarPool pool = pools.get(poolName);
		if (pool == null) return new ArrayList<MODEL_Avatar>();
		if (includeAvatar) {
			return pool.getAvatars();
		} else {
			ArrayList<MODEL_Avatar> avatars = new ArrayList<MODEL_Avatar>();
			for (MODEL_Avatar cur: pool.getAvatars()) {
				if (!avatar.getID().equals(cur.getID())) avatars.add(cur);
			}
			return avatars;
		}
	}

}
