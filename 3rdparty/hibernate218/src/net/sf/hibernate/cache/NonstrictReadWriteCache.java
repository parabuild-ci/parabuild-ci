//$Id: NonstrictReadWriteCache.java,v 1.11 2004/09/04 03:03:09 oneovthafew Exp $
package net.sf.hibernate.cache;

import java.util.Comparator;

import net.sf.hibernate.HibernateException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Caches data that is sometimes updated without ever locking the cache. 
 * If concurrent access to an item is possible, this concurrency strategy 
 * makes no guarantee that the item returned from the cache is the latest 
 * version available in the database. Configure your cache timeout accordingly! 
 * This is an "asynchronous" concurrency strategy.
 * 
 * @see ReadWriteCache for a much stricter algorithm
 * @author Gavin King
 */
public class NonstrictReadWriteCache implements CacheConcurrencyStrategy {
	
	private Cache cache;
	
	private static final Log log = LogFactory.getLog(NonstrictReadWriteCache.class);

	private boolean minimalPuts;
	
	public NonstrictReadWriteCache() {}
	
	public void setCache(Cache cache) {
		this.cache=cache;
	}

	/**
	 * Get the most recent version, if available.
	 */
	public Object get(Object key, long txTimestamp) throws CacheException {
		if ( log.isDebugEnabled() ) log.debug("Cache lookup: " + key);
		
		Object result = cache.get(key);
		if ( result!=null ) {
			log.debug("Cache hit");
		}
		else {
			log.debug("Cache miss");
		}
		return result;
	}
	
	/**
	 * Add an item to the cache.
	 */
	public boolean put(Object key, Object value, long txTimestamp, Object version, Comparator versionComparator)
	throws CacheException {
		if ( minimalPuts && cache.get(key)!=null ) {
			if ( log.isDebugEnabled() ) log.debug("item already cached: " + key);
			return false;
		}
		if ( log.isDebugEnabled() ) log.debug("Caching: " + key);
		cache.put(key, value);
		return true;
		
	}
	
	/**
	 * Do nothing.
	 * @return null, no lock
	 */
	public SoftLock lock(Object key, Object version) throws CacheException {
		return null;
	}
	
	public void remove(Object key) throws CacheException {
		if ( log.isDebugEnabled() ) log.debug("Removing: " + key);
		cache.remove(key);
	}

	public void clear() throws CacheException {
		if ( log.isDebugEnabled() ) log.debug("Clearing");
		cache.clear();
	}

	public void destroy() {
		try {
			cache.destroy();
		}
		catch (Exception e) {
			log.warn("could not destroy cache", e);
		}
	}
	
	/**
	 * Invalidate the item
	 */
	public void evict(Object key) throws CacheException {
		if ( log.isDebugEnabled() ) log.debug("Invalidating: " + key);
		
		cache.remove(key);
	}

	/**
	 * Invalidate the item
	 */
	public void update(Object key, Object value) throws CacheException {
		evict(key);
	}

	/**
	 * Do nothing.
	 */
	public void insert(Object key, Object value) throws CacheException {
	}

	/**
	 * Invalidate the item (again, for safety).
	 */
	public void release(Object key, SoftLock lock) throws CacheException {
		if ( log.isDebugEnabled() ) log.debug("Invalidating (again): " + key);
		
		cache.remove(key);
	}

	/**
	 * Invalidate the item (again, for safety).
	 */
	public void afterUpdate(Object key, Object value, Object version, SoftLock lock) throws CacheException {
		release(key, lock);
	}
	
	/**
	 * Do nothing.
	 */
	public void afterInsert(Object key, Object value, Object version) throws CacheException {
		//if ( log.isDebugEnabled() ) log.debug("Adding new: " + key);
		
		//cache.put(key, value);
	}

	public void setMinimalPuts(boolean minimalPuts) throws HibernateException {
		this.minimalPuts = minimalPuts;
	}
}
