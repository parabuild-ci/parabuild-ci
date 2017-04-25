//$Id: UniqueEntityLoader.java,v 1.7 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.loader;

import java.io.Serializable;
import java.sql.SQLException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;

/**
 * Loads entities for a <tt>ClassPersister</tt>
 * @author Gavin King
 */
public interface UniqueEntityLoader {
	/**
	 * Load an entity instance. If <tt>optionalObject</tt> is supplied,
	 * load the entity state into the given (uninitialized) object.
	 */
	public Object load(SessionImplementor session, Serializable id, Object optionalObject) throws HibernateException, SQLException;
}






