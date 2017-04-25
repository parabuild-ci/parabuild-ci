//$Id: UserSuppliedConnectionProvider.java,v 1.9 2004/06/04 01:27:38 steveebersole Exp $
package net.sf.hibernate.connection;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;

/**
 * An implementation of the <literal>ConnectionProvider</literal> interface that
 * simply throws an exception when a connection is requested. This implementation
 * indicates that the user is expected to supply a JDBC connection.
 * @see ConnectionProvider
 * @author Gavin King
 */
public class UserSuppliedConnectionProvider implements ConnectionProvider {
	
	/**
	 * @see net.sf.hibernate.connection.ConnectionProvider#configure(Properties)
	 */
	public void configure(Properties props) throws HibernateException {
		LogFactory.getLog(UserSuppliedConnectionProvider.class).warn("No connection properties specified - the user must supply JDBC connections");
	}
	
	/**
	 * @see net.sf.hibernate.connection.ConnectionProvider#getConnection()
	 */
	public Connection getConnection() throws SQLException {
		throw new UnsupportedOperationException("The user must supply a JDBC connection");
	}
	
	/**
	 * @see net.sf.hibernate.connection.ConnectionProvider#closeConnection(Connection)
	 */
	public void closeConnection(Connection conn) throws SQLException {
		throw new UnsupportedOperationException("The user must supply a JDBC connection");
	}
	
	public void close() {
	}

}






