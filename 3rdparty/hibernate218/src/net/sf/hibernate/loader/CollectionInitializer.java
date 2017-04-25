//$Id: CollectionInitializer.java,v 1.9 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.loader;

import java.io.Serializable;
import java.sql.SQLException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;

/**
 * An interface for collection loaders
 * @see CollectionLoader
 * @see OneToManyLoader
 * @author Gavin King
 */
public interface CollectionInitializer {
	/**
	 * Initialize the given collection
	 */
	public void initialize(Serializable id, SessionImplementor session) throws SQLException, HibernateException;
}






