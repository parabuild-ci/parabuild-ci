//$Id: HibernateIterator.java,v 1.7 2004/11/11 20:42:30 steveebersole Exp $
package net.sf.hibernate.engine;

import net.sf.hibernate.JDBCException;

import java.util.Iterator;

/**
 * An iterator that may be "closed"
 * @see net.sf.hibernate.Hibernate#close(java.util.Iterator)
 * @author Gavin King
 */
public interface HibernateIterator extends Iterator {
	public void close() throws JDBCException;
}
