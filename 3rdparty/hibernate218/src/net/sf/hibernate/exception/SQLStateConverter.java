// $Id: SQLStateConverter.java,v 1.2 2004/12/09 16:01:17 steveebersole Exp $
package net.sf.hibernate.exception;

import net.sf.hibernate.JDBCException;

import java.sql.SQLException;
import java.util.Set;
import java.util.HashSet;

/**
 * A SQLExceptionConverter implementation which performs converion based on
 * the underlying SQLState.  Interpretation of a SQL error based on SQLState
 * is not nearly as accurate as using the ErrorCode (which is, however, vendor-
 * specific).  Use of a ErrorCcode-based converter should be preferred approach
 * for converting/interpreting SQLExceptions.
 *
 * @author Steve Ebersole
 */
public class SQLStateConverter implements SQLExceptionConverter {

	private ViolatedConstraintNameExtracter extracter;

	private static final Set SQL_GRAMMAR_CATEGORIES = new HashSet();
	private static final Set INTEGRITY_VIOLATION_CATEGORIES = new HashSet();
	private static final Set CONNECTION_CATEGORIES = new HashSet();

	static {
		SQL_GRAMMAR_CATEGORIES.add("07");
		SQL_GRAMMAR_CATEGORIES.add("37");
		SQL_GRAMMAR_CATEGORIES.add("42");
		SQL_GRAMMAR_CATEGORIES.add("65");
		SQL_GRAMMAR_CATEGORIES.add("S0");

		INTEGRITY_VIOLATION_CATEGORIES.add("23");
		INTEGRITY_VIOLATION_CATEGORIES.add("27");
		INTEGRITY_VIOLATION_CATEGORIES.add("44");

		CONNECTION_CATEGORIES.add("08");
	}

	public SQLStateConverter(ViolatedConstraintNameExtracter extracter) {
		this.extracter = extracter;
	}

	/**
	 * Convert the given SQLException into Hibernate's JDBCException hierarchy.
	 *
	 * @param sqlException The SQLException to be converted.
	 * @param message An optional error message.
	 * @return The resulting JDBCException.
	 */
	public JDBCException convert(SQLException sqlException, String message) {

		String sqlStateClassCode = JDBCExceptionHelper.extractSqlStateClassCode(sqlException);

		if (sqlStateClassCode != null) {
            if ( SQL_GRAMMAR_CATEGORIES.contains(sqlStateClassCode) ) {
	            return new SQLGrammarException(message, sqlException);
            }
			else if ( INTEGRITY_VIOLATION_CATEGORIES.contains(sqlStateClassCode) ) {
				String constraintName = extracter.extractConstraintName(sqlException);
				return new ConstraintViolationException(message, sqlException, constraintName);
			}
			else if ( CONNECTION_CATEGORIES.contains(sqlStateClassCode) ) {
				return new JDBCConnectionException(message, sqlException);
			}
		}

		return handledNonSpecificException(sqlException, message);
	}

	/**
	 * Handle an exception not converted to a specific type based on the SQLState.
	 *
	 * @param sqlException The exception to be handled.
	 * @param message An optional message
	 * @return The converted exception; should <b>never</b> be null.
	 */
	protected JDBCException handledNonSpecificException(SQLException sqlException, String message) {
		return new GenericJDBCException(message, sqlException);
	}
}
