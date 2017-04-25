//$Id: JDBCException.java,v 1.9 2004/12/24 03:06:23 oneovthafew Exp $
package net.sf.hibernate;

import java.sql.SQLException;

/**
 * Wraps an <tt>SQLException</tt>. Indicates that an exception
 * occurred during a JDBC call.
 * 
 * @see java.sql.SQLException
 * @author Gavin King
 */
public class JDBCException extends HibernateException {

	private static final String DEFAULT_MESSAGE = "SQLException occurred";
	private SQLException sqle;

	/**
	 * Constructor for JDBCException.
	 *
	 * @param root The underlying exception.
	 */
	public JDBCException(SQLException root) {
		this(null, root);
	}

	/**
	 * Constructor for JDBCException.
	 *
	 * @param message Optional message.
	 * @param root The underlying exception.
	 */
	public JDBCException(String message, SQLException root) {
		super( nullCheck(message), root );
		sqle=root;
	}

	private static String nullCheck(String string) {
		return string == null ? DEFAULT_MESSAGE : string;
	}

	/**
	 * Get the SQLState of the underlying <tt>SQLException</tt>.
	 *
	 * @see java.sql.SQLException
	 * @return String
	 */
	public String getSQLState() {
		return sqle.getSQLState();
	}

	/**
	 * Get the <tt>errorCode</tt> of the underlying <tt>SQLException</tt>.
	 *
	 * @see java.sql.SQLException
	 * @return int the error code
	 */
	public int getErrorCode() {
		return sqle.getErrorCode();
	}
	
	/**
	 * Get the underlying <tt>SQLException</tt>.
	 *
	 * @return SQLException
	 */
	public SQLException getSQLException() {
		return sqle;
	}

}
