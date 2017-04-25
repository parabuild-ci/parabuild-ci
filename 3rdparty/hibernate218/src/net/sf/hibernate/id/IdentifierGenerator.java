//$Id: IdentifierGenerator.java,v 1.10 2004/06/04 01:27:40 steveebersole Exp $
package net.sf.hibernate.id;

import java.io.Serializable;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;

import java.sql.SQLException;

/**
 * The general contract between a class that generates unique
 * identifiers and the <tt>Session</tt>. It is not intended that 
 * this interface ever be exposed to the application. It <b>is</b>
 * intended that users implement this interface to provide
 * custom identifier generation strategies.<br>
 * <br>
 * Implementors should provide a public default constructor.<br>
 * <br>
 * Implementations that accept configuration parameters should
 * also implement <tt>Configurable</tt>.
 * <br>
 * Implementors <em>must</em> be threadsafe
 *
 * @see PersistentIdentifierGenerator
 * @see Configurable
 * @author Gavin King
 */
public interface IdentifierGenerator {

	/**
	 * Generate a new identifier.
	 * @param session
	 * @param object the entity or toplevel collection for which the id is being generated
	 * @return Serializable a new identifier
	 * @throws SQLException
	 * @throws IdentifierGenerationException
	 */
	public Serializable generate(SessionImplementor session, Object object) throws SQLException, HibernateException;
	
}






