// $Id: ConstraintViolationException.java,v 1.1 2004/11/11 20:46:15 steveebersole Exp $
package net.sf.hibernate.exception;

import net.sf.hibernate.JDBCException;

import java.sql.SQLException;

/**
 * Implementation of JDBCException indicating that the requested DML operation
 * resulted in a violation of a defined integrity constraint.
 *
 * @author Steve Ebersole
 */
public class ConstraintViolationException extends JDBCException {

	private String constraintName;

	/**
	 * Constructor for JDBCException.
	 *
	 * @param root The underlying exception.
	 */
	public ConstraintViolationException(SQLException root, String constraintName) {
		super(root);
		this.constraintName = constraintName;
	}

	/**
	 * Constructor for JDBCException.
	 *
	 * @param message Optional message.
	 * @param root The underlying exception.
	 */
	public ConstraintViolationException(String message, SQLException root, String constraintName) {
		super(message, root);
		this.constraintName = constraintName;
	}

	/**
	 * Returns the name of the violated constraint, if known.
	 *
	 * @return The name of the violated constraint, or null if not known.
	 */
	public String getConstraintName() {
		return constraintName;
	}
}
