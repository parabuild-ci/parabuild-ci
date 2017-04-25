// $Id: QueryCacheFactory.java,v 1.1 2004/06/09 19:29:03 steveebersole Exp $
package net.sf.hibernate.cache;

import net.sf.hibernate.HibernateException;

import java.util.Properties;

/**
 * Defines a factory for query cache instances.  These factories are responsible for
 * creating individual QueryCache instances.
 *
 * @author Steve Ebersole
 */
public interface QueryCacheFactory {

	public QueryCache getQueryCache(
	        String regionName,
	        CacheProvider provider,
	        UpdateTimestampsCache updateTimestampsCache,
	        Properties props) throws HibernateException;

}
