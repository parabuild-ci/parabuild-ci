//$Id: ConnectionProvider.java,v 1.10 2004/06/04 01:27:38 steveebersole Exp $
package net.sf.hibernate.connection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import net.sf.hibernate.HibernateException;

/**
 * A strategy for obtaining JDBC connections.
 * <br><br>
 * Implementors might also implement connection pooling.<br>
 * <br>
 * The <tt>ConnectionProvider</tt> interface is not intended to be
 * exposed to the application. Instead it is used internally by
 * Hibernate to obtain connections.<br>
 * <br>
 * Implementors should provide a public default constructor.
 *
 * @see ConnectionProviderFactory
 * @author Gavin King
 */
public interface ConnectionProvider {
	/**
	 * Initialize the connection provider from given properties.
	 * @param props <tt>SessionFactory</tt> properties
	 */
	public void configure(Properties props) throws HibernateException;
	/**
	 * Grab a connection
	 * @return a JDBC connection
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException;
	/**
	 * Dispose of a used connection.
	 * @param conn a JDBC connection
	 * @throws SQLException
	 */
	public void closeConnection(Connection conn) throws SQLException;
	
	/**
	 * Release all resources held by this provider. JavaDoc requires a second sentence.
	 * @throws HibernateException
	 */
	public void close() throws HibernateException;
}







