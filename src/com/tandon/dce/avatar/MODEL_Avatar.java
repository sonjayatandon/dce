/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.avatar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.tandon.dce.account.MODEL_User;
import com.tandon.dce.characterclass.MODEL_Class;
import com.tandon.dce.common.db.IResultSetVisitor;
import com.tandon.dce.common.db.SERVICE_ConnectionPool;

public class MODEL_Avatar implements IResultSetVisitor {
	final XCFFacade facade;

	Integer avatarID;
	MODEL_User user;
	int defaultBuildID;
	String avatarName;
	String userName;
	final MODEL_Avatar avatar; // for easy inner class access
	HashMap<String, Object> data = new HashMap<String, Object>();

	HashMap<String, MODEL_AvatarAttribute> attributes = new HashMap<String, MODEL_AvatarAttribute>();
	HashMap<String, MODEL_AvatarProfile> profiles = new HashMap<String, MODEL_AvatarProfile>();

	public MODEL_Avatar(XCFFacade facade) {
		this.facade = facade;
		avatar = this;
	}

	public MODEL_Avatar(XCFFacade facade, ResultSet rs) throws SQLException {
		this.facade = facade;
		this.userName = rs.getString(1);
		this.avatarID = rs.getInt(2);
		this.avatarName = rs.getString(3);
		avatar = this;
	}

	public Integer getID() {
		return avatarID;
	}

	public String getName() {
		return avatarName;
	}

	public MODEL_User getUser() throws XCFException {
		if (user == null) {
			user = new MODEL_User(facade);
			user.loadAvatarsUser(this);
		}
		return user;
	}

	public String getUserName() {
		return userName;
	}

	public void setAvatarName(String avatarName) {
		this.avatarName = avatarName;
	}

	public void putData(String key, Object value) {
		data.put(key, value);
	}

	public Object getData(String key) {
		return data.get(key);
	}

	public Integer getCurrAttributeValue(String attrName) throws XCFException  {
		if ("sk".equals(attrName)) {
			return user.getNumTokens();
		}
		MODEL_AvatarAttribute attr = attributes.get(attrName);

		if (attr==null) throw new XCFException (avatarName + " doesn't have the " + attrName + " attribute. ");

		return attr.getCurrentValue();
	}

	public Integer getCurrAttributeValue(String attrName, Integer defaultMax, Integer defaultCur) throws XCFException {
		MODEL_AvatarAttribute attr = attributes.get(attrName);

		if (attr == null) {
			addAttribute(attrName, defaultMax, defaultCur);
			return defaultCur;
		}

		return attr.getCurrentValue();
	}

	public Set<String> getAttributeNames() {
		return attributes.keySet();
	}

	public Integer getMaxAttributeValue(String attrName) throws XCFException {
		if ("sk".equals(attrName)) {
			return user.getNumTokens();
		}
		MODEL_AvatarAttribute attr = attributes.get(attrName);

		if (attr==null) throw new XCFException (avatarName + " doesn't have the " + attrName + " attribute. ");

		return attr.getMaxValue();
	}

	static final String TABLE = "Avatars";
	static final String INSERT_COLUMNS = "userID, name";
	static final String INSERT_VALUES="?,?";

	@SuppressWarnings("unchecked")
	public int newInstance(MODEL_User user, String avatarName) throws XCFException {
		this.user = user;
		ArrayList params = new ArrayList();
		params.add(user.getID());
		params.add(avatarName);

		if (avatarName.length() > 50 ) throw new XCFException("Avatar name must be 50 characters or less");

		this.avatarName = avatarName;
		avatarID = SERVICE_ConnectionPool.getDBM(facade).insertAutoIncrement(TABLE, INSERT_COLUMNS, INSERT_VALUES, params);

		if (avatarID == -1) throw new XCFException("Unable to add " + avatarName + " to the database." );
		user.setDefaultAvatar(this);

		return avatarID;
	}


	public void loadInstance(MODEL_User user, int avatarID) throws XCFException {
		loadInstance(user, avatarID, true);
		loadAttributes();
	}

	static final String SELECT_INSTANCE = "select name from Avatars where avatarID=?";
	int loadCount = 0;
	@SuppressWarnings("unchecked")
	public boolean loadInstance(MODEL_User user, int avatarID, boolean required) throws XCFException {
		this.user = user;
		loadCount = 0;
		ArrayList params = new ArrayList();
		params.add(avatarID);
		SERVICE_ConnectionPool.getDBM(facade).select(SELECT_INSTANCE, params, this);

		if (loadCount > 1) throw new XCFException ("There were multiple avatars in the database for avatarID=" + avatarID);
		if (required && loadCount == 0) throw new XCFException ("There was no avatar in the database for avatarID=" + avatarID);
		this.avatarID = avatarID;
		return loadCount == 1;
	}

	public void load(ResultSet rs) throws SQLException {
		avatarName = rs.getString(1);
		loadCount++;
	}

	static final String SELECT_ATTRIBUTES = "select attributeID, attrName, attrMaxValue, attrCurValue from AvatarAttributes where avatarID=?";
	@SuppressWarnings("unchecked")
	public void loadAttributes() throws XCFException {
		ArrayList params = new ArrayList();
		params.add(avatarID);
		SERVICE_ConnectionPool.getDBM(facade).select(SELECT_ATTRIBUTES, params, new AttributeVisitor());
	}

	class AttributeVisitor implements IResultSetVisitor {
		public void load(ResultSet rs) throws SQLException {
			MODEL_AvatarAttribute attribute = new MODEL_AvatarAttribute(facade, avatar, rs);
			attributes.put(attribute.getAttribute(), attribute);
		}
	}

	public void addAttribute(String attrName, Integer attrMaxValue, Integer attrCurValue) throws XCFException {
		if ("sk".equals(attrName)) {
			user.setNumTokens(attrCurValue);
			return;
		}
		MODEL_AvatarAttribute attr = new MODEL_AvatarAttribute(facade);
		attr.newInstance(this, attrName, attrMaxValue, attrCurValue);
		attributes.put(attrName, attr);
	}

	public void updateAttributes(String attrName, Integer maxValue, Integer curValue) throws XCFException {
		if ("sk".equals(attrName)) {
			user.setNumTokens(curValue);
			return;
		}
		MODEL_AvatarAttribute attribute = attributes.get(attrName);
		if (attribute == null) throw new XCFException( attrName + " is not an attribute for " + getName());

		attribute.update(maxValue, curValue);
	}

	public void incrementAttributes(String attrName, Integer maxValue, Integer curValue) throws XCFException {
		if ("sk".equals(attrName)) {
			throw new XCFException("incrementAttributes doesn't support increments to SK");
		}
		MODEL_AvatarAttribute attribute = attributes.get(attrName);
		if (attribute == null) throw new XCFException( attrName + " is not an attribute for " + getName());

		attribute.increment(maxValue, curValue);
	}

	public void addProfile(String profileName, MODEL_Class style, Integer postureID) throws XCFException {
		MODEL_AvatarProfile profile = new MODEL_AvatarProfile(facade);
		profile.newInstance(avatar, profileName, style, postureID);
	}

	static final String SELECT_ONE_PROFILE = "select profileID, name, styleID, postureID from AvatarProfiles where avatarID=? and name=?";
	@SuppressWarnings("unchecked")
	public MODEL_AvatarProfile loadProfile(String profileName) throws XCFException {
		MODEL_AvatarProfile profile = null;
		if (profiles != null) {
			synchronized (profiles) {
				profile = profiles.get(profileName);
				if (profile != null) return profile;
			}
		}
		ArrayList params = new ArrayList();
		params.add(avatarID);
		params.add(profileName);
		SERVICE_ConnectionPool.getDBM(facade).select(SELECT_ONE_PROFILE, params, new ProfileVisitor());

		synchronized(profiles) {
			profile = profiles.get(profileName);
			if (profile == null) {
				throw new XCFException(profileName + " is not a profile for " + getName());
			}
		}
		profile.loadCards();

		return profile;
	}

	public void refreshProfiles() {
		synchronized(profiles) {
			profiles = new HashMap<String, MODEL_AvatarProfile>();
		}
	}

	class ProfileVisitor implements IResultSetVisitor {
		public void load(ResultSet rs) throws SQLException, XCFException {
			MODEL_AvatarProfile profile = new MODEL_AvatarProfile(facade, avatar, rs);
			synchronized(profiles) {
				profiles.put(profile.getName(), profile);
			}
		}
	}

	static final String UPDATE_ITEM_ACCESS_TABLE = "ItemAccess";
	static final String UPDATE_ITEM_ACCESS_FIELDS = "accessLevel = ?";
	static final String UPDATE_ITEM_ACCESS_WHERE = "avatarID=? and itemType=? and itemID=?";
	@SuppressWarnings("unchecked")
	public void acquire(Integer itemType, Integer itemID) throws XCFException {
		// first make sure they can view the item
		view(itemType, itemID);
		ArrayList params = new ArrayList();
		params.add(new Integer(2));
		params.add(avatarID);
		params.add(itemType);
		params.add(itemID);

		// now acquire the item
		SERVICE_ConnectionPool.getDBM(facade).update(UPDATE_ITEM_ACCESS_TABLE, UPDATE_ITEM_ACCESS_FIELDS, UPDATE_ITEM_ACCESS_WHERE, params);
	}

	@SuppressWarnings("unchecked")
	public void unlock(Integer itemType, Integer itemID) throws XCFException {
		// first make sure they can view the item
		view(itemType, itemID);
		ArrayList params = new ArrayList();
		params.add(new Integer(1));
		params.add(avatarID);
		params.add(itemType);
		params.add(itemID);

		// now acquire the item
		SERVICE_ConnectionPool.getDBM(facade).update(UPDATE_ITEM_ACCESS_TABLE, UPDATE_ITEM_ACCESS_FIELDS, UPDATE_ITEM_ACCESS_WHERE, params);
	}

	static final String SELECT_COUNT_VIEW = "select count(itemAccessID) from ItemAccess where avatarID=? and itemType=? and itemID=?";
	static final String ACCESS_TABLE = "ItemAccess";
	static final String ACCESS_COLUMNS = "avatarID, itemType, itemID, accessLevel";
	static final String ACCESS_PARAMETERS = "?,?,?,?";
	@SuppressWarnings("unchecked")
	public void view(Integer itemType, Integer itemID) throws XCFException {
		ArrayList params = new ArrayList();
		params.add(avatarID);
		params.add(itemType);
		params.add(itemID);
		ItemCounter v = new ItemCounter();
		SERVICE_ConnectionPool.getDBM(facade).select(SELECT_COUNT_VIEW, params, v);

		if (v.count == 0) {
			params.add(0);
			SERVICE_ConnectionPool.getDBM(facade).insert(ACCESS_TABLE, ACCESS_COLUMNS, ACCESS_PARAMETERS, params);
		}

	}

	class ItemCounter implements IResultSetVisitor {
		int count = 0;
		public void load(ResultSet rs) throws SQLException, XCFException {
			count=rs.getInt(1);
		}
	}

	HashMap<Integer, HashMap<Integer, Integer>> accessByType = null;

	static final String SELECT_GET_ITEMS = "select itemType, itemID, accessLevel from ItemAccess where avatarID=?";
	// returns access level by itemType, itemID
	@SuppressWarnings("unchecked")
	public HashMap<Integer, HashMap<Integer, Integer>> getItemAccess() throws XCFException {
		if (accessByType != null) return accessByType;
		accessByType = new HashMap<Integer, HashMap<Integer,Integer>>();
		ArrayList params = new ArrayList();
		params.add(avatarID);
		ItemAccessVisitor v = new ItemAccessVisitor();
		SERVICE_ConnectionPool.getDBM(facade).select(SELECT_GET_ITEMS, params, v);

		return accessByType;
	}

	public void refreshItemAccess() {
		accessByType = null;
	}

	class ItemAccessVisitor implements IResultSetVisitor {
		public void load(ResultSet rs) throws SQLException, XCFException {
			Integer itemType = rs.getInt(1);
			Integer itemID = rs.getInt(2);
			Integer itemAccessLevel = rs.getInt(3);

			HashMap<Integer, Integer> itemAccess = accessByType.get(itemType);
			if (itemAccess == null) {
				itemAccess = new HashMap<Integer, Integer>();
				accessByType.put(itemType, itemAccess);
			}
			itemAccess.put(itemID, itemAccessLevel);
		}
	}

	static final String DELETE_WHERE = "avatarID=?";
	@SuppressWarnings("unchecked")
	public void delete() {
		ArrayList params = new ArrayList();
		params.add(avatarID);

		// DELETE ALL THE ATTRIBUTES
		SERVICE_ConnectionPool.getDBM(facade).delete("AvatarAttributes", DELETE_WHERE, params);

		// DELETE ALL THE PROFILES
		SERVICE_ConnectionPool.getDBM(facade).delete("AvatarProfiles", DELETE_WHERE, params);

		// DELETE ALL THE PROFILE CARDS
		SERVICE_ConnectionPool.getDBM(facade).delete("AvatarProfileCards", DELETE_WHERE, params);

		// DELETE ALL THE ITEM ACCESS
		SERVICE_ConnectionPool.getDBM(facade).delete("ItemAccess", DELETE_WHERE, params);

		// NOW DELETE THE AVATARS
		SERVICE_ConnectionPool.getDBM(facade).delete(TABLE, DELETE_WHERE, params);
	}
}
