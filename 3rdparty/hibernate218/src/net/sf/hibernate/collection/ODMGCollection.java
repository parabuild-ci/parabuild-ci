//$Id: ODMGCollection.java,v 1.12 2004/06/04 01:27:37 steveebersole Exp $
package net.sf.hibernate.collection;

import java.util.Iterator;

import org.odmg.DCollection;
import org.odmg.QueryInvalidException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;

/**
 * All Hibernate collections actually implement the ODMG <tt>DCollection</tt>
 * interface and are castable to that type. However, we don't recommend that
 * the object model be "polluted" by using this interface, unless the
 * application is using the ODMG API.
 * 
 * @author Gavin King
 */
public abstract class ODMGCollection extends PersistentCollection implements DCollection {
	
	public ODMGCollection(SessionImplementor session) {
		super(session);
	}
	
	public ODMGCollection() {} //needed for SOAP libraries, etc

	/**
	 * @see org.odmg.DCollection#existsElement(String)
	 */
	public boolean existsElement(String queryString) throws QueryInvalidException {
		//return select(queryString).hasNext();
		try {
			return ( (Integer) getSession().filter( this, "select count(*) " + queryString ).iterator().next() ).intValue() > 0;
		}
		catch (HibernateException he) {
			throw new QueryInvalidException( he.getMessage() );
		}
	}
	
	/**
	 * @see org.odmg.DCollection#query(String)
	 */
	public DCollection query(String queryString) throws QueryInvalidException {
		// TODO: make this return the right collection subclass for DSet, DBag
		try {
			return new List( getSession(), (java.util.List) getSession().filter(this, queryString) );
		}
		catch (HibernateException he) {
			throw new QueryInvalidException( he.getMessage() );
		}
	}
	
	/**
	 * @see org.odmg.DCollection#select(String)
	 */
	public Iterator select(String queryString) throws QueryInvalidException {
		try {
			return getSession().filter(this, queryString).iterator();
		}
		catch (HibernateException he) {
			throw new QueryInvalidException( he.getMessage() );
		}
	}
	
	/**
	 * @see org.odmg.DCollection#selectElement(String)
	 */
	public Object selectElement(String queryString) throws QueryInvalidException {
		Iterator iter = select(queryString);
		return iter.hasNext() ? iter.next() : null;
	}
	
}






