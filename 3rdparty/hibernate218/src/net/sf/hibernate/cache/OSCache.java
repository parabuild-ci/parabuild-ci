//$Id: OSCache.java,v 1.6 2004/06/04 05:43:45 steveebersole Exp $
package net.sf.hibernate.cache;

import net.sf.hibernate.util.StringHelper;

import com.opensymphony.oscache.base.NeedsRefreshException;
import com.opensymphony.oscache.general.GeneralCacheAdministrator;

/**
 * @author <a href="mailto:m.bogaert@intrasoft.be">Mathias Bogaert</a>
 */
public class OSCache implements Cache {
	/** 
	 * The OSCache 2.0 cache administrator. 
	 */
	private GeneralCacheAdministrator cache = new GeneralCacheAdministrator();

	private final int refreshPeriod;
	private final String cron;
	private final String regionName;
	
	private String toString(Object key) {
		return String.valueOf(key) + StringHelper.DOT + regionName;
	}

	public OSCache(int refreshPeriod, String cron, String region) {
		this.refreshPeriod = refreshPeriod;
		this.cron = cron;
		this.regionName = region;
	}

	public void setCacheCapacity(int cacheCapacity) {
		cache.setCacheCapacity(cacheCapacity);
	}

	public Object get(Object key) throws CacheException {
		try {
			return cache.getFromCache( toString(key), refreshPeriod, cron );
		}
		catch (NeedsRefreshException e) {
			cache.cancelUpdate( toString(key) );
			return null;
		}
	}

	public void put(Object key, Object value) throws CacheException {
		cache.putInCache( toString(key), value );
	}

	public void remove(Object key) throws CacheException {
		cache.flushEntry( toString(key) );
	}

	public void clear() throws CacheException {
		cache.flushAll();
	}

	public void destroy() throws CacheException {
		cache.destroy();
	}

	public void lock(Object key) throws CacheException {
		// local cache, so we use synchronization
	}

	public void unlock(Object key) throws CacheException {
		// local cache, so we use synchronization
	}

	public long nextTimestamp() {
		return Timestamper.next();
	}

	public int getTimeout() {
		return Timestamper.ONE_MS * 60000; //ie. 60 seconds
	}

}