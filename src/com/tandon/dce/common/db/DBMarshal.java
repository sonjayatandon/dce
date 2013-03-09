/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.common.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;

public class DBMarshal {
	XCFFacade facade;

	public DBMarshal(XCFFacade facade) {
		this.facade = facade;
	}

	@SuppressWarnings("unchecked")
	public int insertAutoIncrement(String table, String columns, String values, ArrayList parameters) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		int autoIndex = -1;
		try {
			conn = SERVICE_ConnectionPool.getConnection(facade);
			stmt = conn.prepareStatement("insert into " + table + " (" + columns +", created) values (" + values + ",now())", Statement.RETURN_GENERATED_KEYS);

			if (parameters != null) {
				int i=1;
				for (Object parameter: parameters) {
					stmt.setObject(i, parameter);
					i++;
				}
			}

			stmt.executeUpdate();
			rs = stmt.getGeneratedKeys();

			if (rs.next()) {
				autoIndex = rs.getInt(1);
			}
		} catch (SQLException e) {
			facade.logError(e);
		} finally {
			releaseResultSet(rs);
			releaseStatement(stmt);
			SERVICE_ConnectionPool.releaseConnection(facade, conn);
		}

		return autoIndex;
	}

	@SuppressWarnings("unchecked")
	public void insert(String table, String columns, String values, ArrayList parameters) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = SERVICE_ConnectionPool.getConnection(facade);
			stmt = conn.prepareStatement("insert into " + table + " (" + columns +", created) values (" + values + ",now())", Statement.RETURN_GENERATED_KEYS);

			if (parameters != null) {
				int i=1;
				for (Object parameter: parameters) {
					stmt.setObject(i, parameter);
					i++;
				}
			}

			stmt.executeUpdate();

		} catch (SQLException e) {
			facade.logError(e);
		} finally {
			releaseResultSet(rs);
			releaseStatement(stmt);
			SERVICE_ConnectionPool.releaseConnection(facade, conn);
		}
	}

	@SuppressWarnings("unchecked")
	public void update(String sql, ArrayList parameters) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = SERVICE_ConnectionPool.getConnection(facade);
			stmt = conn.prepareStatement(sql);

			if (parameters != null) {
				int i=1;
				for (Object parameter: parameters) {
					stmt.setObject(i, parameter);
					i++;
				}
			}

			stmt.executeUpdate();

		} catch (SQLException e) {
			facade.logError(e);
		} finally {
			releaseResultSet(rs);
			releaseStatement(stmt);
			SERVICE_ConnectionPool.releaseConnection(facade, conn);
		}
	}

	@SuppressWarnings("unchecked")
	public void update(String table, String fields, String whereClause, ArrayList parameters) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = SERVICE_ConnectionPool.getConnection(facade);
			String sql = "update " + table + " set " + fields + ", modified=now() where " + whereClause;
			stmt = conn.prepareStatement(sql);

			if (parameters != null) {
				int i=1;
				for (Object parameter: parameters) {
					stmt.setObject(i, parameter);
					i++;
				}
			}

			stmt.executeUpdate();

		} catch (SQLException e) {
			facade.logError(e);
		} finally {
			releaseResultSet(rs);
			releaseStatement(stmt);
			SERVICE_ConnectionPool.releaseConnection(facade, conn);
		}
	}

	@SuppressWarnings("unchecked")
	public void delete(String table, String whereClause, ArrayList parameters) {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = SERVICE_ConnectionPool.getConnection(facade);
			String sql = "delete from " + table + "  where " + whereClause;
			stmt = conn.prepareStatement(sql);

			if (parameters != null) {
				int i=1;
				for (Object parameter: parameters) {
					stmt.setObject(i, parameter);
					i++;
				}
			}

			stmt.executeUpdate();

		} catch (SQLException e) {
			facade.logError(e);
		} finally {
			releaseResultSet(rs);
			releaseStatement(stmt);
			SERVICE_ConnectionPool.releaseConnection(facade, conn);
		}
	}
	@SuppressWarnings("unchecked")
	public void select(String sql, ArrayList parameters, IResultSetVisitor visitor) throws XCFException {
		Connection conn = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		try {
			conn = SERVICE_ConnectionPool.getConnection(facade);
			stmt = conn.prepareStatement(sql);

			if (parameters != null) {
				int i=1;
				for (Object parameter: parameters) {
					stmt.setObject(i, parameter);
					i++;
				}
			}

			rs = stmt.executeQuery();
			while (rs.next()) {
				visitor.load(rs);
			}
		} catch (SQLException e) {
			facade.logError(e);
		} finally {
			releaseResultSet(rs);
			releaseStatement(stmt);
			SERVICE_ConnectionPool.releaseConnection(facade, conn);
		}
	}

	private void releaseResultSet(ResultSet rs) {
		if (rs == null) return;
		try {rs.close();} catch (SQLException e) {facade.logError(e);}
	}

	private void releaseStatement(Statement stmt) {
		if (stmt == null) return;
		try {stmt.close();} catch (SQLException e) {facade.logError(e);}
	}


}
