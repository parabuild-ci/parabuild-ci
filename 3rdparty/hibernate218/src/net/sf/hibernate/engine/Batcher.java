//$Id: Batcher.java,v 1.11 2005/01/10 03:10:22 oneovthafew Exp $
package net.sf.hibernate.engine;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import net.sf.hibernate.ScrollMode;

import net.sf.hibernate.HibernateException;

/**
 * Manages <tt>PreparedStatement</tt>s for a session. Abstracts JDBC
 * batching to maintain the illusion that a single logical batch
 * exists for the whole session, even when batching is disabled.
 * Provides transparent <tt>PreparedStatement</tt> caching.
 * 
 * @see java.sql.PreparedStatement
 * @see net.sf.hibernate.impl.SessionImpl
 * @author Gavin King
 */
public interface Batcher {
	/**
	 * Get a prepared statement for use in loading / querying. If not explicitly
	 * released by <tt>closeQueryStatement()</tt>, it will be released when the
	 * session is closed or disconnected.
	 */
	public PreparedStatement prepareQueryStatement(String sql, boolean scrollable, ScrollMode scrollMode) throws SQLException, HibernateException;
	/**
	 * Close a prepared statement opened with <tt>prepareQueryStatement()</tt>
	 */
	public void closeQueryStatement(PreparedStatement ps, ResultSet rs) throws SQLException;
		
	/**
	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
	 * Must be explicitly released by <tt>closeStatement()</tt>
	 */
	public PreparedStatement prepareStatement(String sql, boolean useGetGeneratedKeys) throws SQLException, HibernateException;
	/**
	 * Get a non-batchable prepared statement to use for inserting / deleting / updating.
	 * Must be explicitly released by <tt>closeStatement()</tt>
	 */
	public PreparedStatement prepareStatement(String sql) throws SQLException, HibernateException;
	/**
	 * Close a prepared statement opened using <tt>prepareStatement()</tt>
	 */
	public void closeStatement(PreparedStatement ps) throws SQLException;
	/**
	 * Get a batchable prepared statement to use for inserting / deleting / updating
	 * (might be called many times before a single call to <tt>executeBatch()</tt>).
	 * After setting parameters, call <tt>addToBatch</tt> - do not execute the
	 * statement explicitly.
	 * @see Batcher#addToBatch(int)
	 */
	public PreparedStatement prepareBatchStatement(String sql) throws SQLException, HibernateException;
	/**
	 * Add an insert / delete / update to the current batch (might be called multiple times
	 * for single <tt>prepareBatchStatement()</tt>)
	 */
	public void addToBatch(int expectedRowCount) throws SQLException, HibernateException;
	
	/**
	 * Execute the batch
	 */
	public void executeBatch() throws HibernateException;
	
	/**
	 * Close any query statements that were left lying around
	 */
	public void closeStatements();
	/**
	 * Execute the statement and return the result set
	 */
	public ResultSet getResultSet(PreparedStatement ps) throws SQLException;
	
	/**
	 * Must be called when an exception occurs
	 * @param sqle the (not null) exception that is the reason for aborting
	 */
	public void abortBatch(SQLException sqle);
	
	/**
	 * Obtain a JDBC connection
	 */
	public Connection openConnection() throws HibernateException;
	/**
	 * Dispose of the JDBC connection
	 */
	public void closeConnection(Connection conn) throws HibernateException;
	
	/**
	 * Cancel the current query statement
	 */
	public void cancelLastQuery() throws HibernateException;
	
}






