//$Id: UniqueKeyLoadable.java,v 1.6 2004/06/04 05:43:47 steveebersole Exp $
package net.sf.hibernate.persister;

import java.io.Serializable;
import java.sql.SQLException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;

/**
 * @author Gavin King
 */
public interface UniqueKeyLoadable extends Loadable {
	/**
	 * Load an instance of the persistent class, by a unique key other
	 * than the primary key.
	 */
	public Object loadByUniqueKey(String propertyName, Serializable uniqueKey, SessionImplementor session) throws HibernateException, SQLException;
	/**
	 * Get the columns of the unique key.
	 */
	public String[] getUniqueKeyColumnNames(String propertyName);

}
