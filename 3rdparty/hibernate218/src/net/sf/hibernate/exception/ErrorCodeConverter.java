// $Id: ErrorCodeConverter.java,v 1.1 2004/11/11 20:46:15 steveebersole Exp $
package net.sf.hibernate.exception;

import net.sf.hibernate.JDBCException;

import java.sql.SQLException;

/**
 * A SQLExceptionConverter implementation which performs converion based on
 * the vendor specific ErrorCode.  This is just intended really as just a
 * base class for converters which know the interpretation of vendor-specific
 * codes.
 *
 * @author Steve Ebersole
 */
public class ErrorCodeConverter implements SQLExceptionConverter {

	private ViolatedConstraintNameExtracter extracter;

	public ErrorCodeConverter(ViolatedConstraintNameExtracter extracter) {
		this.extracter = extracter;
	}

	/**
	 * The error codes representing SQL grammar issues.
	 *
	 * @return The SQL grammar error codes.
	 */
	protected int[] getSQLGrammarErrorCodes() {
		return null;
	}

	/**
	 * The error codes representing issues with a connection.
	 *
	 * @return The connection error codes.
	 */
	protected int[] getConnectionErrorCodes() {
		return null;
	}

	/**
	 * The error codes representing various types of database integrity issues.
	 *
	 * @return The integrity violation error codes.
	 */
	protected int[] getIntegrityViolationErrorCodes() {
		return null;
	}

	protected int[] getLockAcquisitionErrorCodes() {
		return null;
	}

	/**
	 * Convert the given SQLException into Hibernate's JDBCException hierarchy.
	 *
	 * @param sqlException The SQLException to be converted.
	 * @param message An optional error message.
	 * @return The resulting JDBCException.
	 */
	public JDBCException convert(SQLException sqlException, String message) {
		int errorCode = JDBCExceptionHelper.extractErrorCode(sqlException);

        if ( isMatch( getConnectionErrorCodes(), errorCode ) ) {
            return new JDBCConnectionException(message, sqlException);
        }
		else if ( isMatch( getSQLGrammarErrorCodes(), errorCode ) ) {
			return new SQLGrammarException(message, sqlException);
		}
		else if ( isMatch( getIntegrityViolationErrorCodes(), errorCode ) ) {
			String constraintName = extracter.extractConstraintName(sqlException);
			return new ConstraintViolationException(message, sqlException, constraintName);
		}
		else if ( isMatch( getLockAcquisitionErrorCodes(), errorCode ) ) {
			return new LockAcquisitionException(message, sqlException);
		}

		return handledNonSpecificException(sqlException, message);
	}

	/**
	 * Handle an exception not converted to a specific type based on the built-in checks.
	 *
	 * @param sqlException The exception to be handled.
	 * @param message An optional message
	 * @return The converted exception; should <b>never</b> be null.
	 */
	protected JDBCException handledNonSpecificException(SQLException sqlException, String message) {
		return new GenericJDBCException(message, sqlException);
	}

	private boolean isMatch(int[] errorCodes, int errorCode) {
		if ( errorCodes != null ) {
			for ( int i = 0, max = errorCodes.length; i < max; i++ ) {
				if ( errorCodes[i] == errorCode ) {
					return true;
				}
			}
		}
		return false;
	}
}
