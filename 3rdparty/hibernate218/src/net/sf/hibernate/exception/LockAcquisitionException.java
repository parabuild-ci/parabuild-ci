// $Id: LockAcquisitionException.java,v 1.1 2004/11/11 20:46:15 steveebersole Exp $
package net.sf.hibernate.exception;

import net.sf.hibernate.JDBCException;

import java.sql.SQLException;

/**
 * Implementation of LockAcquisitionException.
 *
 * @author Steve Ebersole
 */
public class LockAcquisitionException extends JDBCException {
	/**
	 * Constructor for JDBCException.
	 *
	 * @param root The underlying exception.
	 */
	public LockAcquisitionException(SQLException root) {
		super(root);
	}

	/**
	 * Constructor for JDBCException.
	 *
	 * @param message Optional message.
	 * @param root    The underlying exception.
	 */
	public LockAcquisitionException(String message, SQLException root) {
		super(message, root);
	}
}
