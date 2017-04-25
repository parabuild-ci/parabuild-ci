// $Id: SQLGrammarException.java,v 1.1 2004/11/11 20:46:15 steveebersole Exp $
package net.sf.hibernate.exception;

import net.sf.hibernate.JDBCException;

import java.sql.SQLException;

/**
 * Implementation of JDBCException indicating that the SQL sent to the database
 * server was invalid (syntax error, invalid object references, etc).
 *
 * @author Steve Ebersole
 */
public class SQLGrammarException extends JDBCException {
	/**
	 * Constructor for JDBCException.
	 *
	 * @param root The underlying exception.
	 */
	public SQLGrammarException(SQLException root) {
		super(root);
	}

	/**
	 * Constructor for JDBCException.
	 *
	 * @param message Optional message.
	 * @param root The underlying exception.
	 */
	public SQLGrammarException(String message, SQLException root) {
		super(message, root);
	}
}
