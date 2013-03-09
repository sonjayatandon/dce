/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.account;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFStrings;
import com.tandon.dce.avatar.MODEL_Avatar;
import com.tandon.dce.common.db.IResultSetVisitor;
import com.tandon.dce.common.db.SERVICE_ConnectionPool;

public class MODEL_User implements IResultSetVisitor {
	XCFFacade facade;
	Integer userID;
	MODEL_Avatar defaultAvatar;
	Integer defaultAvatarID = 0;
	String username;
	String network;
	int tokens = 0;;
	boolean isAdmin = false;
	String reference = XCFStrings.EMPTY;

	public MODEL_User(XCFFacade facade) {
		this.facade = facade;
	}

	public MODEL_User(XCFFacade facade, String network, String identity) {
		this.facade = facade;
		this.username = identity;
		this.network = network;
	}

	public MODEL_User(XCFFacade facade, ResultSet rs) throws SQLException {
		this.facade = facade;
		this.userID = rs.getInt(1);
		this.username = rs.getString(2);
		this.network = rs.getString(3);
		this.tokens = rs.getInt(4);
		this.defaultAvatarID = rs.getInt(5);
	}

	public void setPassword(String password) {

	}

	public Integer getID() {
		return userID;
	}

	public MODEL_Avatar getDefaultAvatar() throws XCFException {
		if (defaultAvatar == null && defaultAvatarID != 0) {
			defaultAvatar = new MODEL_Avatar(facade);
			defaultAvatar.loadInstance(this, defaultAvatarID);
		}
		return defaultAvatar;
	}

	public boolean hasDefaultAvatar() {
		return (defaultAvatar != null || defaultAvatarID != 0);
	}

	public String getUserName() {
		return username;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public Integer getNumTokens() {
		return tokens;
	}

	public String getReference() {
		return reference;
	}

	public boolean connect(boolean required) throws XCFException {
		return loadInstance(required);
	}

	static final String SELECT_BY_AVATAR = "select userID, username, tokens, adminUser, reference from Users where defaultAvatarID=?";
	@SuppressWarnings("unchecked")
	public void loadAvatarsUser(MODEL_Avatar avatar) throws XCFException {
		ArrayList params = new ArrayList();
		params.add(avatar.getID());
		LoadByAvatarVisitor v = new LoadByAvatarVisitor();
		defaultAvatarID = avatar.getID();
		SERVICE_ConnectionPool.getDBM(facade).select(SELECT_BY_AVATAR, params, v);
	}

	class LoadByAvatarVisitor implements IResultSetVisitor {

		public void load(ResultSet rs) throws SQLException, XCFException {
			userID = rs.getInt(1);
			username = rs.getString(2);
			tokens = rs.getInt(3);
			int adminUser = rs.getInt(4);
			isAdmin = (adminUser > 0);
			reference = rs.getString(5);
		}

	}

	//////////////////////////////////////////////////
	// database marshalling code

	static final String TABLE = "Users";
	static final String INSERT_COLUMNS = "network,username";
	static final String INSERT_VALUES="?,?";

	@SuppressWarnings("unchecked")
	public int newInstance() throws XCFException {
		ArrayList params = new ArrayList();
		params.add(network);
		params.add(username);

		if (network.length() > 25 ) throw new XCFException("network name must be 25 characters or less");
		if (username.length() > 255 ) throw new XCFException("Users name must be 255 characters or less");

		userID = SERVICE_ConnectionPool.getDBM(facade).insertAutoIncrement(TABLE, INSERT_COLUMNS, INSERT_VALUES, params);

		if (userID == -1) throw new XCFException("Unable to add " + username + " to the database." );

		return userID;
	}

	static final String UPDATE_LAST_LOGIN = "update Users set modified=now() where userID=?";
	@SuppressWarnings("unchecked")
	public void updateLastLogin() {
		ArrayList params = new ArrayList();
		params.add(userID);

		SERVICE_ConnectionPool.getDBM(facade).update(UPDATE_LAST_LOGIN, params);
	}

	static final String SAVE_REF_CODE = "update Users set reference=? where userID=?";
	@SuppressWarnings("unchecked")
	public void saveRefCode(String refCode) {
		ArrayList params = new ArrayList();
		params.add(refCode);
		params.add(userID);

		SERVICE_ConnectionPool.getDBM(facade).update(SAVE_REF_CODE, params);
	}

	static final String SELECT_INSTANCE = "select userID, defaultAvatarID, tokens, adminUser, reference from Users where username=?";
	int loadCount = 0;
	int avatarID = 0;
	@SuppressWarnings("unchecked")
	public boolean loadInstance(boolean required) throws XCFException {
		loadCount = 0;
		ArrayList params = new ArrayList();
		params.add(username);
		SERVICE_ConnectionPool.getDBM(facade).select(SELECT_INSTANCE, params, this);

		if (loadCount > 1) throw new XCFException ("There were multiple users in the database for username=" + username);
		if (required && loadCount == 0) throw new XCFException ("There was no user in the database for username=" + username);
		if (avatarID != 0) {
			defaultAvatar = new MODEL_Avatar(facade);
			defaultAvatar.loadInstance(this, avatarID);
		}
		return loadCount == 1;
	}
	public void load(ResultSet rs) throws SQLException, XCFException {
		userID = rs.getInt(1);
		avatarID = rs.getInt(2);
		tokens = rs.getInt(3);
		int adminUser = rs.getInt(4);
		isAdmin = (adminUser > 0);
		reference = rs.getString(5);
		loadCount++;
	}

	static final String UPDATE_AVATAR_FIELDS = "defaultAvatarID=?";
	static final String UPDATE_AVATAR_WHERE = "userID=?";

	@SuppressWarnings("unchecked")
	public void setDefaultAvatar(MODEL_Avatar avatar) {
		defaultAvatar = avatar;

		ArrayList params = new ArrayList();
		params.add(avatar.getID());
		params.add(userID);

		SERVICE_ConnectionPool.getDBM(facade).update(TABLE, UPDATE_AVATAR_FIELDS, UPDATE_AVATAR_WHERE, params);
	}

	@SuppressWarnings("unchecked")
	public void clearDefaultAvatar() {
		defaultAvatar = null;

		ArrayList params = new ArrayList();
		params.add(0);
		params.add(userID);

		SERVICE_ConnectionPool.getDBM(facade).update(TABLE, UPDATE_AVATAR_FIELDS, UPDATE_AVATAR_WHERE, params);
	}

	static final String UPDATE_TOKENS_FIELDS = "tokens=?";
	static final String UPDATE_TOKENS_WHERE = "userID=?";

	@SuppressWarnings("unchecked")
	public void setNumTokens(Integer tokens) {
		this.tokens = tokens;

		ArrayList params = new ArrayList();
		params.add(tokens);
		params.add(userID);

		SERVICE_ConnectionPool.getDBM(facade).update(TABLE, UPDATE_TOKENS_FIELDS, UPDATE_TOKENS_WHERE, params);
	}

	public void deleteDefaultAvatar() {
		defaultAvatar.delete();
		clearDefaultAvatar();
	}

	static final String DELETE_WHERE = "userID=?";
	@SuppressWarnings("unchecked")
	public void delete() {
		if (defaultAvatar != null) {
			defaultAvatar.delete();
			defaultAvatar = null;
		}

		ArrayList params = new ArrayList();
		params.add(userID);
		SERVICE_ConnectionPool.getDBM(facade).delete(TABLE, DELETE_WHERE, params);

		userID = null;
	}

}

