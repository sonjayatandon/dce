/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.avatar;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.tandon.dce.common.db.SERVICE_ConnectionPool;

public class MODEL_AvatarAttribute {
	XCFFacade facade;
	int maxValue;
	int currentValue;
	String attribute;
	MODEL_Avatar avatar;
	int attributeID;

	public MODEL_AvatarAttribute(XCFFacade facade) {
		this.facade = facade;
	}

	public MODEL_AvatarAttribute(XCFFacade facade, MODEL_Avatar avatar, ResultSet rs) throws SQLException {
		this.facade = facade;
		this.avatar = avatar;
		attributeID = rs.getInt(1);
		attribute = rs.getString(2);
		maxValue = rs.getInt(3);
		currentValue = rs.getInt(4);
	}

	static final String TABLE = "AvatarAttributes";
	static final String INSERT_COLUMNS = "avatarID, attrName, attrMaxValue, attrCurValue";
	static final String INSERT_VALUES="?,?,?,?";
	@SuppressWarnings("unchecked")

	public int newInstance(MODEL_Avatar avatar, String attrName, Integer attrMaxValue, Integer attrCurValue) throws XCFException {
		ArrayList params = new ArrayList();
		params.add(avatar.getID());
		params.add(attrName);
		params.add(attrMaxValue);
		params.add(attrCurValue);

		this.avatar = avatar;
		this.attribute = attrName;
		this.maxValue = attrMaxValue;
		this.currentValue = attrCurValue;
		attributeID = SERVICE_ConnectionPool.getDBM(facade).insertAutoIncrement(TABLE, INSERT_COLUMNS, INSERT_VALUES, params);
		if (attributeID == -1) throw new XCFException("Unable to add " + attrName + " to the database for avatar " + avatar.getName() );

		return attributeID;
	}

	static final String UPDATE_FIELDS = "attrMaxValue=?, attrCurValue=?";
	static final String UPDATE_WHERE = "avatarID=? and attrName=?";
	@SuppressWarnings("unchecked")
	public void update(Integer maxValue, Integer curValue) {
		ArrayList params = new ArrayList();
		this.maxValue = maxValue;
		this.currentValue = curValue;
		params.add(maxValue);
		params.add(currentValue);
		params.add(avatar.getID());
		params.add(attribute);

		SERVICE_ConnectionPool.getDBM(facade).update(TABLE, UPDATE_FIELDS, UPDATE_WHERE, params);
	}

	@SuppressWarnings("unchecked")
	public void increment(Integer maxAmount, Integer curAmount) {
		ArrayList params = new ArrayList();
		this.maxValue = maxValue+maxAmount;
		this.currentValue = currentValue+curAmount;
		String incrementSQL = "attrMaxValue = attrMaxValue+" + maxAmount + ", attrCurValue=attrCurValue+"+curAmount;
		params.add(avatar.getID());
		params.add(attribute);

		SERVICE_ConnectionPool.getDBM(facade).update(TABLE, incrementSQL, UPDATE_WHERE, params);
	}


	public int getCurrentValue() {
		return currentValue;
	}

	public void setCurrentValue(int currentValue) {
		this.currentValue = currentValue;
	}

	public int getMaxValue() {
		return maxValue;
	}

	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

}
