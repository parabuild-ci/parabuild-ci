// $Id: SQLExceptionConverter.java,v 1.2 2004/11/19 01:14:20 steveebersole Exp $
package net.sf.hibernate.exception;

import net.sf.hibernate.JDBCException;

import java.sql.SQLException;

/**
 * Defines a contract for implementations that know how to convert SQLExceptions
 * into Hibernate's JDBCException hierarchy.  Inspired by Spring's
 * SQLExceptionTranslator.
 * <p/>
 * Implementations <b>must</b> have a constructor which takes a
 * {@link ViolatedConstraintNameExtracter} parameter.
 * <p/>
 * Implementations may implement {@link Configurable} if they need to perform
 * configuration steps prior to first use.
 *
 * @see SQLExceptionConverterFactory
 *
 * @author Steve Ebersole
 */
public interface SQLExceptionConverter {
	/**
	 * Convert the given SQLException into Hibernate's JDBCException hierarchy.
	 *
	 * @see ConstraintViolationException, JDBCConnectionException, SQLGrammarException, LockAcquisitionException
	 *
	 * @param sqlException The SQLException to be converted.
	 * @param message An optional error message.
	 * @return The resulting JDBCException.
	 */
	public JDBCException convert(SQLException sqlException, String message);
}
