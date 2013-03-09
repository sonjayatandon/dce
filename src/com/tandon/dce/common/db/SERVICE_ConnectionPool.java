/*
 * Copyright 2008 Sonjaya Tandon.  All rights reserved.
 */
package com.tandon.dce.common.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;
import java.util.Vector;

import javax.sql.DataSource;

import com.eternal.xcf.core.XCFException;
import com.eternal.xcf.core.XCFFacade;
import com.eternal.xcf.core.XCFService;

public class SERVICE_ConnectionPool implements XCFService {
	public static final String XCF_TAG = "connection-pool";

	public static final Connection getConnection(XCFFacade facade) throws SQLException {
		SERVICE_ConnectionPool pool = (SERVICE_ConnectionPool)facade.getService(XCF_TAG);
		return pool.getConnection();
	}

	public static final void releaseConnection(XCFFacade facade, Connection conn)  {
		if (conn == null) return;
		SERVICE_ConnectionPool pool = (SERVICE_ConnectionPool)facade.getService(XCF_TAG);
		try {pool.release(conn);} catch (SQLException e) {facade.logError(e);}
	}

	public static final DBMarshal getDBM(XCFFacade facade) {
		SERVICE_ConnectionPool pool = (SERVICE_ConnectionPool)facade.getService(XCF_TAG);
		return pool.getDBMarshall();
	}

	XCFFacade facade;

	protected Vector free_cons = new Vector();
	protected Vector used_cons = new Vector();
	protected DataSource data_source = null;
	protected int max_cons;
	protected Hashtable open_time = new Hashtable();
	protected boolean shutdown = false;
	protected static int num_timeouts = 0;

	///////

	static final long max_time_to_stay_open = 1000L * 60L * 12L;
	static final long wait_increment = 40;
	static final long total_wait_time = (1000L * 20L);



	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setFacade(XCFFacade facade) {
		this.facade = facade;

	}

	public void setName(String name) {
		// TODO Auto-generated method stub

	}

	DBMarshal dbm = null;
	public void start() throws XCFException {
		dbm = new DBMarshal(facade);
	}

	public void stop() throws XCFException {
		// TODO Auto-generated method stub

	}

	public DBMarshal getDBMarshall() {
		return dbm;
	}

	public void setDatasource(DataSource data_source, int max_cons) {
		this.data_source = data_source;
		this.max_cons = max_cons;
	}


	@SuppressWarnings("unchecked")
	public Connection getConnection()	throws SQLException  {

		if(shutdown)	throw new SQLException("This webserver is shutting down");

		closeOldConnections();

		Connection con = tryToGetConnection();
		int wait_count = 0;
		while (con == null) {
			try{Thread.sleep(wait_increment);} catch(Exception e) {}
			wait_count++;
			if(wait_count>=(total_wait_time / wait_increment))	{
				notifyConnectionTimeout();
				throw new SQLException("Could not get a new connection: timed out");
			}
			con = tryToGetConnection();
		}
		return(con);
	}

	synchronized void notifyConnectionTimeout() {
		num_timeouts++;
	}

	@SuppressWarnings("unchecked")
	synchronized Connection tryToGetConnection() throws SQLException {
		if(shutdown)	throw new SQLException("This webserver is shutting down");

		Connection con = null;

		if(free_cons.size()>0)	{  //has available free connection

			con = reuseConnection();
		}
		else if(used_cons.size() < max_cons)	{  //can create new connection

			con = createNewConnection();

			used_cons.addElement(con);
		}

		return con;
	}


	@SuppressWarnings("unchecked")
	protected Connection createNewConnection()	throws SQLException {

		Connection con = data_source.getConnection();

		open_time.put(con, new Long(System.currentTimeMillis()));

		return(con);
	}


	@SuppressWarnings("unchecked")
	public void release(Connection con)	 throws SQLException {

		used_cons.removeElement(con);

		free_cons.addElement(con);
	}


	@SuppressWarnings("unchecked")
	protected Connection reuseConnection()	{

		Connection con = (Connection)(free_cons.elementAt(0));

		free_cons.removeElementAt(0);

		used_cons.addElement(con);

		return(con);
	}


	protected synchronized void closeOldConnections()	{

		for(int i=0; i<free_cons.size(); i++)	{

			try	{

				Connection existing_con = (Connection)(free_cons.elementAt(i));

				Long opened = (Long)(open_time.get(existing_con));

				long running_for = System.currentTimeMillis() - opened.longValue();

				if(running_for > max_time_to_stay_open)	{

					existing_con.close();

					free_cons.removeElementAt(i--);

					open_time.remove(existing_con);
				}

			} catch(Exception e)	{

				e.printStackTrace();
			}
		}
	}



	synchronized public void shutdown()	{

		// can't open any more conns

		shutdown = true;

		// give some time for sql in progess to finish

		for(int num_waits = 0; num_waits < 10; num_waits++)	{

			if(used_cons.size()==0)  break;

			try{ Thread.sleep(1000L); } catch(Exception e)	{}
		}

		closeDownAllConns(used_cons);

		// close down all unused conns

		closeDownAllConns(free_cons);
	}



	protected void closeDownAllConns(Vector conns)	{

		for(int i=0; i<conns.size(); i++)	{

			try	{

				Connection con = (Connection)(conns.elementAt(i));

				con.close();

				conns.removeElementAt(i--);

			} catch(Exception e)	{

				e.printStackTrace();
			}
		}
	}






    /**
    * For Ops monitoring
    */

	public int getNumConnsInUse()	{

		return(used_cons.size());
	}

	public int getNumFreeConns()	{

		return(free_cons.size());
	}

}
