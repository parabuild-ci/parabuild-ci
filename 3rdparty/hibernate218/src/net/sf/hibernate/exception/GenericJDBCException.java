// $Id: GenericJDBCException.java,v 1.1 2004/11/11 20:46:15 steveebersole Exp $
package net.sf.hibernate.exception;

import net.sf.hibernate.JDBCException;

import java.sql.SQLException;

/**
 * Generic, non-specific JDBCException.
 *
 * @author Steve Ebersole
 */
public class GenericJDBCException extends JDBCException {
	/**
	 * Constructor for GenericJDBCException.
	 *
	 * @param root The underlying exception.
	 */
	public GenericJDBCException(SQLException root) {
		super(root);
	}

	/**
	 * Constructor for GenericJDBCException.
	 *
	 * @param message Optional message.
	 * @param root The underlying exception.
	 */
	public GenericJDBCException(String message, SQLException root) {
		super(message, root);
	}
}
