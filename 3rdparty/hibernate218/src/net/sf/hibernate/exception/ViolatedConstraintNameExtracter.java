// $Id: ViolatedConstraintNameExtracter.java,v 1.1 2004/11/11 20:46:15 steveebersole Exp $
package net.sf.hibernate.exception;

import java.sql.SQLException;

/**
 * Defines a contract for implementations that can extract the name of a violated
 * constraint from a SQLException that is the result of that constraint violation.
 *
 * @author Steve Ebersole
 */
public interface ViolatedConstraintNameExtracter {
	/**
	 * Extract the name of the violated constraint from the given SQLException.
	 *
	 * @param sqle The exception that was the result of the constraint violation.
	 * @return The extracted constraint name.
	 */
	public String extractConstraintName(SQLException sqle);
}
