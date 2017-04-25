//$Id: Cache.java,v 1.9 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate.cache;

/**
 * Implementors define a caching algorithm. All implementors
 * <b>must</b> be threadsafe.
 */
public interface Cache {
	/**
	 * Get an item from the cache
	 * @param key
	 * @return the cached object or <tt>null</tt>
	 * @throws CacheException
	 */
	public Object get(Object key) throws CacheException;
	/**
	 * Add an item to the cache
	 * @param key
	 * @param value
	 * @throws CacheException
	 */
	public void put(Object key, Object value) throws CacheException;
	/**
	 * Remove an item from the cache
	 */
	public void remove(Object key) throws CacheException;
	/**
	 * Clear the cache
	 */
	public void clear() throws CacheException;
	/**
	 * Clean up
	 */
	public void destroy() throws CacheException;
	/**
	 * If this is a clustered cache, lock the item
	 */
	public void lock(Object key) throws CacheException;
	/**
	 * If this is a clustered cache, unlock the item
	 */
	public void unlock(Object key) throws CacheException;
	/**
	 * Generate a timestamp
	 */
	public long nextTimestamp();
	/**
	 * Get a reasonable "lock timeout"
	 */
	public int getTimeout();

}






