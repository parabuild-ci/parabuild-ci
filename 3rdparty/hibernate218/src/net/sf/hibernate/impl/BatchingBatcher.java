//$Id: BatchingBatcher.java,v 1.9 2004/06/04 01:27:40 steveebersole Exp $
package net.sf.hibernate.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.util.JDBCExceptionReporter;

/**
 * An implementation of the <tt>Batcher</tt> interface that 
 * actually uses batching
 * @author Gavin King
 */
public class BatchingBatcher extends BatcherImpl {
	
	private int batchSize;
	private int[] expectedRowCounts;
	
	public BatchingBatcher(SessionImplementor session) {
		super(session);
		expectedRowCounts = new int[ getFactory().getJdbcBatchSize() ];
	}
	
	public void addToBatch(int expectedRowCount) throws SQLException, HibernateException {
		
		log.trace("Adding to batch");
		PreparedStatement batchUpdate = getStatement();
		batchUpdate.addBatch();
		expectedRowCounts[ batchSize++ ] = expectedRowCount;
		if ( batchSize==getFactory().getJdbcBatchSize() ) {
			//try {
				doExecuteBatch(batchUpdate);
			/*}
			catch (SQLException sqle) {
				closeStatement(batchUpdate);
				throw sqle;
			}
			catch (HibernateException he) {
				closeStatement(batchUpdate);
				throw he;
			}*/
		}
		
	}
	
	protected void doExecuteBatch(PreparedStatement ps) throws SQLException, HibernateException {
		if ( log.isDebugEnabled() )
		log.debug("Executing batch size: " + batchSize );
		
		try {
			if (batchSize!=0) {
				final int[] results = ps.executeBatch();
				// check return codes
				for ( int i=0; i<batchSize; i++ ) {
					if ( results[i]==-2 ) {
						if ( log.isDebugEnabled() ) log.debug("success of batch update unknown: " + i);
					}
					else if ( results[i]==-3 ) {
						throw new HibernateException("Batch update failed: " + i);
					}
					else {
						if ( expectedRowCounts[i]>=0 && results[i]!=expectedRowCounts[i] ) {
							throw new HibernateException("Batch update row count wrong: " + i);
						}
					}
				}
			}
		}
		catch(SQLException sqle) {
			JDBCExceptionReporter.logExceptions(sqle);
			throw sqle;
		}
		catch(RuntimeException re) {
			log.error("Exception executing batch: ", re);
			throw re;
		}
		finally {
			batchSize=0;
			//ps.clearBatch();
		}
	}
	
	
	
}






