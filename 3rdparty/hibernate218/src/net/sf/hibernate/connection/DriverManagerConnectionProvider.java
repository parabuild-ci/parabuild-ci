//$Id: DriverManagerConnectionProvider.java,v 1.12 2004/06/04 01:27:38 steveebersole Exp $
package net.sf.hibernate.connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.util.JDBCExceptionReporter;
import net.sf.hibernate.util.PropertiesHelper;
import net.sf.hibernate.util.ReflectHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A connection provider that uses <tt>java.sql.DriverManager</tt>. This provider
 * also implements a very rudimentary connection pool.
 * @see ConnectionProvider
 * @author Gavin King
 */
public class DriverManagerConnectionProvider implements ConnectionProvider {
	
	private String url;
	private Properties connectionProps;
	private Integer isolation;
	private final ArrayList pool = new ArrayList();
	private int poolSize;
	private int checkedOut = 0;
	
	private static final Log log = LogFactory.getLog(DriverManagerConnectionProvider.class);
	
	public void configure(Properties props) throws HibernateException {
		
		String driverClass = props.getProperty(Environment.DRIVER);
		
		poolSize = PropertiesHelper.getInt(Environment.POOL_SIZE, props, 20); //default pool size 20
		log.info("Using Hibernate built-in connection pool (not for production use!)");
		log.info("Hibernate connection pool size: " + poolSize);
		
		isolation = PropertiesHelper.getInteger(Environment.ISOLATION, props);
		if (isolation!=null)
		log.info( "JDBC isolation level: " + Environment.isolationLevelToString( isolation.intValue() ) );
		
		if (driverClass==null) {
			log.warn("no JDBC Driver class was specified by property " + Environment.DRIVER);
		}
		else {
			try {
				// trying via forName() first to be as close to DriverManager's semantics
				Class.forName(driverClass);								
			}
			catch (ClassNotFoundException cnfe) {
				try {
					ReflectHelper.classForName(driverClass);
				} catch (ClassNotFoundException e) {
				String msg = "JDBC Driver class not found: " + driverClass;
				log.fatal(msg);
				throw new HibernateException(msg);
				}
			}
		}
		
		url = props.getProperty(Environment.URL);
		if (url==null) {
			String msg = "JDBC URL was not specified by property " + Environment.URL;
			log.fatal(msg);
			throw new HibernateException(msg);
		}
		
		connectionProps = ConnectionProviderFactory.getConnectionProperties(props);
		
		log.info( "using driver: " + driverClass + " at URL: " + url );
		log.info("connection properties: " + connectionProps);
		
	}
	
	public Connection getConnection() throws SQLException {
		
		if ( log.isTraceEnabled() ) log.trace( "total checked-out connections: " + checkedOut );
		
		synchronized (pool) {
			if ( !pool.isEmpty() ) {
				int last = pool.size() - 1;
				if ( log.isTraceEnabled() ) {
					log.trace("using pooled JDBC connection, pool size: " + last);
					checkedOut++;
				}
				Connection pooled = (Connection) pool.remove(last);
				if (isolation!=null) pooled.setTransactionIsolation( isolation.intValue() );
				if ( pooled.getAutoCommit() ) pooled.setAutoCommit(false);
				return pooled;
			}
		}
		
		log.debug("opening new JDBC connection");
		Connection conn = DriverManager.getConnection(url, connectionProps);
		if (isolation!=null) conn.setTransactionIsolation( isolation.intValue() );
		if ( conn.getAutoCommit() ) conn.setAutoCommit(false);

		if ( log.isDebugEnabled() ) {
			log.debug( "created connection to: " + url + ", Isolation Level: " + conn.getTransactionIsolation() );
		}
		if ( log.isTraceEnabled() ) checkedOut++;
		
		return conn;
	}
	
	public void closeConnection(Connection conn) throws SQLException {
		
		if ( log.isDebugEnabled() ) checkedOut--;
		
		synchronized (pool) {
			int currentSize = pool.size();
			if ( currentSize < poolSize ) {
				if ( log.isTraceEnabled() ) log.trace("returning connection to pool, pool size: " + (currentSize + 1) );
				pool.add(conn);
				return;
			}
		}
		
		log.debug("closing JDBC connection");
		
		try {
			conn.close();
		}
		catch (SQLException sqle) {
			JDBCExceptionReporter.logExceptions(sqle);
			throw sqle;
		}
	}
	
	protected void finalize() {
		close();
	}
	
	public void close() {
		
		log.info("cleaning up connection pool: " + url);
		
		Iterator iter = pool.iterator();
		while ( iter.hasNext() ) {
			try {
				( (Connection) iter.next() ).close();
			}
			catch (SQLException sqle) {
				log.warn("problem closing pooled connection", sqle);
			}
		}
		pool.clear();
		
	}

}







