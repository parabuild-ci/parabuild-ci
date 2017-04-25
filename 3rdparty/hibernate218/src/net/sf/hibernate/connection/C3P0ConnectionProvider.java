//$Id: C3P0ConnectionProvider.java,v 1.11 2004/06/04 01:27:38 steveebersole Exp $
package net.sf.hibernate.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mchange.v2.c3p0.PoolConfig;
import com.mchange.v2.c3p0.DataSources;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.util.PropertiesHelper;

/**
 * A connection provider that uses a C3P0 connection pool. Hibernate will use this by
 * default if the <tt>hibernate.c3p0.*</tt> properties are set.
 * @see ConnectionProvider
 * @author various people
 */
public class C3P0ConnectionProvider implements ConnectionProvider {
	
	private DataSource ds;
	private Integer isolation;
	
	private static final Log log = LogFactory.getLog(C3P0ConnectionProvider.class);
	
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
		
		log.info("C3P0 using driver: " + jdbcDriverClass + " at URL: " + jdbcUrl);
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
			
			int minPoolSize = PropertiesHelper.getInt(Environment.C3P0_MIN_SIZE, props, 1);
			int maxPoolSize = PropertiesHelper.getInt(Environment.C3P0_MAX_SIZE, props, 100);
			int maxIdleTime = PropertiesHelper.getInt(Environment.C3P0_TIMEOUT, props, 0);
			int maxStatements = PropertiesHelper.getInt(Environment.C3P0_MAX_STATEMENTS, props, 0);
			int acquireIncrement = PropertiesHelper.getInt(Environment.C3P0_ACQUIRE_INCREMENT, props, 1);
			int idleTestPeriod = PropertiesHelper.getInt(Environment.C3P0_IDLE_TEST_PERIOD, props, 0);
			boolean validateConnection = PropertiesHelper.getBoolean(Environment.C3P0_VALIDATE_CONNECTION, props);
			
			PoolConfig pcfg = new PoolConfig();
			pcfg.setInitialPoolSize(minPoolSize);
			pcfg.setMinPoolSize(minPoolSize);
			pcfg.setMaxPoolSize(maxPoolSize);
			pcfg.setAcquireIncrement(acquireIncrement);
			pcfg.setMaxIdleTime(maxIdleTime);
			pcfg.setMaxStatements(maxStatements);
			pcfg.setTestConnectionOnCheckout(validateConnection);
			pcfg.setIdleConnectionTestPeriod(idleTestPeriod);
			
			/*DataSource unpooled = DataSources.unpooledDataSource(
				jdbcUrl, props.getProperty(Environment.USER), props.getProperty(Environment.PASS)
			);*/
			DataSource unpooled = DataSources.unpooledDataSource(jdbcUrl, connectionProps);
			ds = DataSources.pooledDataSource(unpooled, pcfg);
			
		}
		catch (Exception e) {
			log.fatal("could not instantiate C3P0 connection pool", e);
			throw new HibernateException("Could not instantiate C3P0 connection pool", e);
		}
		
		String i = props.getProperty(Environment.ISOLATION);
		if (i==null) {
			isolation=null;
		}
		else {
			isolation = new Integer(i);
			log.info( "JDBC isolation level: " + Environment.isolationLevelToString( isolation.intValue() ) );
		}
		
	}
	
	public void close() {
		try {
			DataSources.destroy(ds);
		}
		catch (SQLException sqle) {
			log.warn("could not destroy C3P0 connection pool", sqle);
		}
	}

}







