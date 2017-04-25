// $Id: StandardQueryCacheFactory.java,v 1.2 2004/06/09 20:33:35 steveebersole Exp $
package net.sf.hibernate.cache;

import net.sf.hibernate.HibernateException;

import java.util.Properties;

/**
 * Standard Hibernate implementation of the QueryCacheFactory interface.  Returns
 * instances of {@link StandardQueryCache}.
 */
public class StandardQueryCacheFactory implements QueryCacheFactory {

	public QueryCache getQueryCache(
	        String regionName,
	        CacheProvider provider,
	        UpdateTimestampsCache updateTimestampsCache,
	        Properties props) throws HibernateException {
		return new StandardQueryCache(provider, props, updateTimestampsCache, regionName);
	}

}
