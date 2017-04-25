//$Id: DBCPConnectionProvider.java,v 1.11 2004/07/13 14:26:59 turin42 Exp $
package net.sf.hibernate.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.util.PropertiesHelper;

import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.pool.KeyedObjectPoolFactory;
import org.apache.commons.pool.ObjectPool;
import org.apache.commons.pool.impl.GenericKeyedObjectPoolFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * A connection provider that uses an Apache commons DBCP connection pool. Hibernate will
 * use this by default if the <tt>hibernate.dbcp.*</tt> properties are set.
 * @see ConnectionProvider
 * @author various people
 */
public class DBCPConnectionProvider implements ConnectionProvider {
	
	private Integer isolation;
	private DataSource ds;
	private KeyedObjectPoolFactory statementPool;
	private ObjectPool connectionPool;
	
	private static final Log log = LogFactory.getLog(DBCPConnectionProvider.class);
	
	public Connection getConnection() throws SQLException {
		final Connection c = ds.getConnection();
		if (isolation!=null) c.setTransactionIsolation( isolation.intValue() );
		if ( c.getAutoCommit() ) c.setAutoCommit(false);
		return c;
	}
	
	public void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}
	
	public void configure(Properties props) throws HibernateException {
		String jdbcDriverClass = props.getProperty(Environment.DRIVER);
		String jdbcUrl = props.getProperty(Environment.URL);
		Properties connectionProps = ConnectionProviderFactory.getConnectionProperties(props);
		
		log.info("DBCP using driver: " + jdbcDriverClass + " at URL: " + jdbcUrl);
		log.info("Connection properties: " + connectionProps);
		
		if (jdbcDriverClass==null) {
			log.warn("No JDBC Driver class was specified by property " + Environment.DRIVER);
		}
		else {
			try {
				Class.forName(jdbcDriverClass);
			}
			catch (ClassNotFoundException cnfe) {
				String msg = "JDBC Driver class not found: " + jdbcDriverClass;
				log.fatal(msg);
				throw new HibernateException(msg);
			}
		}
		
		try {

			// We'll need a ObjectPool that serves as the
			// actual pool of connections.
			connectionPool = new GenericObjectPool(
				null,
				PropertiesHelper.getInt(Environment.DBCP_MAXACTIVE, props, 8),
				PropertiesHelper.getByte(Environment.DBCP_WHENEXHAUSTED, props, (byte) 1),
				PropertiesHelper.getLong(Environment.DBCP_MAXWAIT, props, -1),
				PropertiesHelper.getInt(Environment.DBCP_MAXIDLE, props, 8),
				PropertiesHelper.getBoolean( Environment.DBCP_VALIDATION_ONBORROW, props ),
				PropertiesHelper.getBoolean( Environment.DBCP_VALIDATION_ONRETURN, props )
			);

			//check whether we use prepare statement caching or not
			if (props.getProperty(Environment.DBCP_PS_MAXACTIVE)==null) {
				log.info("DBCP prepared statement pooling disabled");
				statementPool = null;
			} else {
				// We'll need a KeyedObjectPoolFactory that serves as the
				// actual pool of prepared statements.
				log.info("DBCP prepared statement pooling enabled");
				statementPool = new GenericKeyedObjectPoolFactory(
					null,
					PropertiesHelper.getInt(Environment.DBCP_PS_MAXACTIVE, props, 8),
					PropertiesHelper.getByte(Environment.DBCP_PS_WHENEXHAUSTED, props, (byte) 1),
					PropertiesHelper.getLong(Environment.DBCP_PS_MAXWAIT, props, -1),
					PropertiesHelper.getInt(Environment.DBCP_PS_MAXIDLE, props, 8)
				);
			}

			// Next, we'll create a ConnectionFactory that the
			// pool will use to create Connections.
			// We'll use the DriverManagerConnectionFactory.
			ConnectionFactory connectionFactory = new DriverManagerConnectionFactory(jdbcUrl, connectionProps);

			// Now we'll create the PoolableConnectionFactory, which wraps
			// the "real" Connections created by the ConnectionFactory with
			// the classes that implement the pooling functionality.
			String validationQuery = PropertiesHelper.getString(Environment.DBCP_VALIDATION_QUERY, props, "SELECT 1");
			new PoolableConnectionFactory(connectionFactory, connectionPool, statementPool, validationQuery, false, false);

			// Finally, we create the PoolingDriver itself,
			// passing in the object pool we created.
			ds = new PoolingDataSource(connectionPool);


		}
		catch (Exception e) {
			log.fatal("could not instantiate DBCP connection pool", e);
			throw new HibernateException( "Could not instantiate DBCP connection pool", e );
		}
		
		String i = props.getProperty(Environment.ISOLATION);
		if (i==null) {
			isolation=null;
		}
		else {
			isolation = new Integer(i);
			log.info("JDBC isolation level: " + Environment.isolationLevelToString( isolation.intValue() ) );
		}
		
	}
	
	/**
	 * @see net.sf.hibernate.connection.ConnectionProvider#close()
	 */
	public void close() throws HibernateException {
		try {
			connectionPool.close();
		}
		catch (Exception e) {
			throw new HibernateException("could not close DBCP pool", e);
		}
	}

}







