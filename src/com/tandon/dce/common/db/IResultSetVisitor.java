/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.common.db;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.eternal.xcf.core.XCFException;

public interface IResultSetVisitor {
	void load(ResultSet rs) throws SQLException, XCFException;
}
