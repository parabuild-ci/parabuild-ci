//$Id: CacheConcurrencyStrategy.java,v 1.10 2004/09/04 03:03:09 oneovthafew Exp $
package net.sf.hibernate.cache;

import java.util.Comparator;

import net.sf.hibernate.HibernateException;

/**
 * Implementors manage transactional access to cached data. Transactions
 * pass in a timestamp indicating transaction start time. Two different
 * implementation patterns are provided for. A transaction-aware cache
 * implementation might be wrapped by a "synchronous" concurrency strategy, 
 * where updates to the cache are written to the cache inside the transaction. 
 * A non transaction-aware cache would be wrapped by an "asynchronous"
 * concurrency strategy, where items are merely "soft locked" during the 
 * transaction and then updated during the "after transaction completion"
 * phase. The soft lock is not an actual lock on the database row - 
 * only upon the cached representation of the item.<br>
 * <br>
 * For the client, update lifecycles are: lock->evict->release,
 * lock->update->afterUpdate, insert->afterInsert.<br>
 * <br>
 * Note that, for an asynchronous cache, cache invalidation must be a two 
 * step process (lock->release, or lock-afterUpdate), since this is the only 
 * way to guarantee consistency with the database for a nontransaction cache 
 * implementation. For a synchronous cache, cache invalidation is a single 
 * step process (evict, or update). Hence, this interface defines a three
 * step process, to cater for both models.
 */
public interface CacheConcurrencyStrategy {
	
	/**
	 * Attempt to retrieve an object from the cache.
	 * @param key
	 * @param txTimestamp a timestamp prior to the transaction start time
	 * @return the cached object or <tt>null</tt>
	 * @throws CacheException
	 */
	public Object get(Object key, long txTimestamp) throws CacheException;
	/**
	 * Attempt to cache an object, after loading from the database.
	 * @param key
	 * @param value
	 * @param txTimestamp a timestamp prior to the transaction start time
	 * @param version the version number of the object we are putting
	 * @param versionComparator a Comparator to be used to compare version numbers
	 * @return <tt>true</tt> if the object was successfully cached
	 * @throws CacheException
	 */
	public boolean put(Object key, Object value, long txTimestamp, Object version, Comparator versionComparator) 
	throws CacheException;


	/**
	 * We are going to attempt to update/delete the keyed object. This
	 * method is used by "asynchronous" concurrency strategies. The
	 * returned object must be passed back to release(), to release the 
	 * lock. Concurrency strategies which do not support client-visible
	 * locks may silently return null.
	 * @param key
	 * @param version
	 * @throws CacheException
	 */
	public SoftLock lock(Object key, Object version) throws CacheException;
	
	
	/**
	 * Called after an item has become stale (before the transaction completes).
	 * This method is used by "synchronous" concurrency strategies.
	 */
	public void evict(Object key) throws CacheException;
	/**
	 * Called after an item has been updated (before the transaction completes),
	 * instead of calling evict().
	 * This method is used by "synchronous" concurrency strategies.
	 */
	public void update(Object key, Object value) throws CacheException;
	/**
	 * Called after an item has been inserted (before the transaction completes),
	 * instead of calling evict().
	 * This method is used by "synchronous" concurrency strategies.
	 */
	public void insert(Object key, Object value) throws CacheException;
	
	
	/**
	 * Called when we have finished the attempted update/delete (which may or 
	 * may not have been successful), after transaction completion.
	 * This method is used by "asynchronous" concurrency strategies.
	 * @param key
	 * @throws CacheException
	 */
	public void release(Object key, SoftLock lock) throws CacheException;
	/**
	 * Called after an item has been updated (after the transaction completes),
	 * instead of calling release().
	 * This method is used by "asynchronous" concurrency strategies.
	 */
	public void afterUpdate(Object key, Object value, Object version, SoftLock lock) throws CacheException;
	/**
	 * Called after an item has been inserted (after the transaction completes),
	 * instead of calling release().
	 * This method is used by "asynchronous" concurrency strategies.
	 */
	public void afterInsert(Object key, Object value, Object version) throws CacheException;
	
	
	/**
	 * Evict an item from the cache immediately (without regard for transaction
	 * isolation).
	 * @param key
	 * @throws CacheException
	 */
	public void remove(Object key) throws CacheException;
	/**
	 * Evict all items from the cache immediately.
	 * @throws CacheException
	 */
	public void clear() throws CacheException;
	/**
	 * Clean up all resources.
	 */
	public void destroy();
	/**
	 * Set the underlying cache implementation.
	 * @param cache
	 */
	public void setCache(Cache cache);
	
	/**
	 * Marker interface, denoting a client-visible "soft lock"
	 * on a cached item.
	 * @author Gavin King
	 */
	public static interface SoftLock {}
	
	/**
	 * Enable "minimal puts" mode for this cache
	 */
	public void setMinimalPuts(boolean minimalPuts) throws HibernateException;
}






