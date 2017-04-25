// $Id: JDBCConnectionException.java,v 1.1 2004/11/11 20:46:15 steveebersole Exp $
package net.sf.hibernate.exception;

import net.sf.hibernate.JDBCException;

import java.sql.SQLException;

/**
 * Implementation of JDBCConnectionException.
 *
 * @author Steve Ebersole
 */
public class JDBCConnectionException extends JDBCException {
	/**
	 * Constructor for JDBCException.
	 *
	 * @param root The underlying exception.
	 */
	public JDBCConnectionException(SQLException root) {
		super(root);
	}

	/**
	 * Constructor for JDBCException.
	 *
	 * @param message Optional message.
	 * @param root The underlying exception.
	 */
	public JDBCConnectionException(String message, SQLException root) {
		super(message, root);
	}
}
