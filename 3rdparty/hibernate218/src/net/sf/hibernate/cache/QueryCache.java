//$Id: QueryCache.java,v 1.6 2004/06/09 19:29:03 steveebersole Exp $
package net.sf.hibernate.cache;

import java.util.List;
import java.util.Set;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.type.Type;

/**
 * Defines the contract for caches capable of storing query results.  These
 * caches should only concern themselves with storing the matching result ids.
 * The transactional semantics are necessarily less strict than the semantics
 * of an item cache.
 * 
 * @author Gavin King
 */
public interface QueryCache {

	public void clear() throws CacheException;
	
	public void put(QueryKey key, Type[] returnTypes, List result, SessionImplementor session) throws HibernateException;

	public List get(QueryKey key, Type[] returnTypes, Set spaces, SessionImplementor session) throws HibernateException;

	public void destroy();

}
