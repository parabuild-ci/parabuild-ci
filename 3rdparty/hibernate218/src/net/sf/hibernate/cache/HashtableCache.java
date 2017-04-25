//$Id: HashtableCache.java,v 1.6 2004/06/04 05:43:45 steveebersole Exp $
package net.sf.hibernate.cache;

import java.util.Hashtable;
import java.util.Map;

/**
 * A lightweight implementation of the <tt>Cache</tt> interface
 * @author Gavin King
 */
public class HashtableCache implements Cache {
	
	private Map hashtable = new Hashtable();

	public Object get(Object key) throws CacheException {
		return hashtable.get(key);
	}

	public void put(Object key, Object value) throws CacheException {
		hashtable.put(key, value);
	}

	public void remove(Object key) throws CacheException {
		hashtable.remove(key);
	}

	public void clear() throws CacheException {
		hashtable.clear();
	}

	public void destroy() throws CacheException {

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
