//$Id: TreeCache.java,v 1.8 2004/12/02 00:45:01 steveebersole Exp $
package net.sf.hibernate.cache;

import org.jboss.cache.Fqn;

/**
 * @author Gavin King
 */
public class TreeCache implements Cache {

	private static final String ITEM = "item";

	private org.jboss.cache.TreeCache cache;
	private final String regionName;

	public TreeCache(org.jboss.cache.TreeCache cache, String regionName) {
		this.cache = cache;
		this.regionName = '/' + regionName.replace('.', '/');
	}

	public Object get(Object key) throws CacheException {
		try {
			return cache.get( new Fqn( new Object[] { regionName, key } ), ITEM );
		}
		catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public void put(Object key, Object value) throws CacheException {
		try {
			cache.put( new Fqn( new Object[] { regionName, key } ), ITEM, value );
		}
		catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public void remove(Object key) throws CacheException {
		try {
			cache.remove( new Fqn( new Object[] { regionName, key } ) );
		}
		catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public void clear() throws CacheException {
		try {
			cache.remove( new Fqn(regionName) );
		}
		catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public void destroy() throws CacheException {
		synchronized (TreeCache.class) {
			if (cache!=null) {
				cache.stop();
				cache.destroy();
				cache=null;
			}
		}
	}

	public void lock(Object key) throws CacheException {
		throw new UnsupportedOperationException("TreeCache is a fully transactional cache" + regionName);
	}

	public void unlock(Object key) throws CacheException {
		throw new UnsupportedOperationException("TreeCache is a fully transactional cache: " + regionName);
	}

	public long nextTimestamp() {
		return System.currentTimeMillis() / 100;
	}

	public int getTimeout() {
		return 600; //60 seconds
	}

}
