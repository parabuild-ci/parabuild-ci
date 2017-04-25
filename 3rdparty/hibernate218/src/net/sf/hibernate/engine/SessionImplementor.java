//$Id: SessionImplementor.java,v 1.22 2004/06/04 01:27:39 steveebersole Exp $
package net.sf.hibernate.engine;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.io.Serializable;

import net.sf.hibernate.persister.ClassPersister;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.ScrollableResults;
import net.sf.hibernate.Session;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.collection.PersistentCollection;
import net.sf.hibernate.collection.ArrayHolder;


/**
 * Defines the internal contract between the <tt>Session</tt> and other parts of
 * Hibernate such as implementors of <tt>Type</tt> or <tt>ClassPersister</tt>.
 *
 * @see net.sf.hibernate.Session the interface to the application
 * @see net.sf.hibernate.impl.SessionImpl the actual implementation
 * @author Gavin King
 */
public interface SessionImplementor extends Session {

	/**
	 * Get the pre-flush identifier of the collection
	 */
	public Serializable getLoadedCollectionKey(PersistentCollection collection);
	/**
	 * Get the snapshot of the pre-flush collection state
	 */
	public Serializable getSnapshot(PersistentCollection collection);

	/**
	 * Get the <tt>PersistentCollection</tt> object for an array
	 */
	public ArrayHolder getArrayHolder(Object array);
	/**
	 * Register a <tt>PersistentCollection</tt> object for an array
	 */
	public void addArrayHolder(ArrayHolder holder);
	/**
	 * Set the "shallow dirty" status of the collection. Called when the collection detects that the
	 * client is modifying it
	 */
	//public void dirty(PersistentCollection collection);
	/**
	 * Initialize the collection (if not already initialized)
	 */
	public void initializeCollection(PersistentCollection collection, boolean writing) throws HibernateException;
	/**
	 * Is this the "inverse" end of a bidirectional association?
	 */
	public boolean isInverseCollection(PersistentCollection collection);

	public PersistentCollection getLoadingCollection(CollectionPersister persister, Serializable id, Object resultSetId) throws HibernateException;
	public void endLoadingCollections(CollectionPersister persister, Object resultSetId) throws HibernateException;
	public void afterLoad();
	public void beforeLoad();
	public void initializeNonLazyCollections() throws HibernateException;
	
	public Object getCollection(String role, Serializable id, Object owner) throws HibernateException;

	/**
	 * Load an instance without checking if it was deleted. If it does not exist, throw an exception.
	 * This method may create a new proxy or return an existing proxy.
	 */
	public Object internalLoad(Class persistentClass, Serializable id) throws HibernateException;
	/**
	 * Load an instance without checking if it was deleted. If it does not exist, return <tt>null</tt>.
	 * Do not create a proxy (but do return any existing proxy).
	 */
	public Object internalLoadOneToOne(Class persistentClass, Serializable id) throws HibernateException;
	/**
	 * Load an instance immediately. Do not return a proxy.
	 */
	public Object immediateLoad(Class persistentClass, Serializable id) throws HibernateException;
	/**
	 * Load an instance by a unique key that is not the primary key.
	 */
	public Object loadByUniqueKey(Class persistentClass, String uniqueKeyPropertyName, Serializable id) throws HibernateException;

	/**
	 * System time before the start of the transaction
	 */
	public long getTimestamp();
	/**
	 * Get the creating <tt>SessionFactoryImplementor</tt>
	 */
	public SessionFactoryImplementor getFactory();
	/**
	 * Get the prepared statement <tt>Batcher</tt> for this session
	 */
	public Batcher getBatcher();

	/**
	 * After actually inserting a row, record the fact that the instance exists on the database
	 * (needed for identity-column key generation)
	 */
	public void postInsert(Object object);
	/**
	 * After actually deleting a row, record the fact that the instance no longer exists on the
	 * database (needed for identity-column key generation)
	 */
	public void postDelete(Object object);
	/**
	 * After actually updating a row, record the fact that the database state has been updated
	 */
	public void postUpdate(Object object, Object[] updatedState, Object nextVersion) throws HibernateException;

	/**
	 * Execute a <tt>find()</tt> query
	 */
	public List find(String query, QueryParameters queryParameters) throws HibernateException;
	/**
	 * Execute an <tt>iterate()</tt> query
	 */
	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException;
	/**
	 * Execute a <tt>scroll()</tt> query
	 */
	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException;

	/**
	 * Execute a filter
	 */
	public List filter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException;
	/**
	 * Iterate a filter
	 */
	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException;
	/**
	 * Get the <tt>ClassPersister</tt> for an object
	 */
	public ClassPersister getPersister(Object object) throws MappingException;

	/**
	 * Add an uninitialized instance of an entity class, as a placeholder to ensure object identity.
	 * Must be called before <tt>postHydrate()</tt>.
	 */
	public void addUninitializedEntity(Key key, Object object, LockMode lockMode);
	/**
	 * Register the "hydrated" state of an entity instance, after the first step of 2-phase loading
	 */
	public void postHydrate(ClassPersister persister, Serializable id, Object[] values, Object object, LockMode lockMode) throws HibernateException;
	/**
	 * Perform the second step of 2-phase load. Fully initialize the entity instance.
	 */
	public void initializeEntity(Object object) throws HibernateException;

	/**
	 * Get the entity instance associated with the given <tt>Key</tt>
	 */
	public Object getEntity(Key key);
	/**
	 * Return the existing proxy associated with the given <tt>Key</tt>, or the
	 * second argument (the entity associated with the key) if no proxy exists.
	 */
	public Object proxyFor(ClassPersister persister, Key key, Object impl) throws HibernateException;
	/**
	 * Return the existing proxy associated with the given <tt>Key</tt>, or the
	 * second argument (the entity associated with the key) if no proxy exists.
	 * (slower than the form above)
	 */
	public Object proxyFor(Object impl) throws HibernateException;

	/**
	 * Notify the session that the transaction completed, so we no longer
	 * own the old locks. (Also we should release cache softlocks.) May
	 * be called multiple times during the transaction completion process.
	 */
	public void afterTransactionCompletion(boolean successful);

	/**
	 * Return the identifier of the persistent object, or null if transient
	 */
	public Serializable getEntityIdentifier(Object obj);

	/**
	 * Return the identifier of the persistent or transient object, or throw
	 * an exception if the instance is "unsaved"
	 */
	public Serializable getEntityIdentifierIfNotUnsaved(Object object) throws HibernateException;

	/**
	 * Was this object already saved to the database?
	 */
	public boolean isSaved(Object object) throws HibernateException;

	/**
	 * Instantiate the entity class, initializing with the given identifier
	 */
	public Object instantiate(Class clazz, Serializable id) throws HibernateException;

	/**
	 * Set the lock mode of the entity to the given lock mode
	 */
	public void setLockMode(Object entity, LockMode lockMode);

	/**
	 * Get the current versioon of the entity
	 */
	public Object getVersion(Object entity);

	/**
	 * Get the lock mode of the entity
	 */
	public LockMode getLockMode(Object object);

	/**
	 * Get the collection orphans (entities which were
	 * removed from the collection)
	 */
	public Collection getOrphans(PersistentCollection coll) throws HibernateException;

	/**
	 * Get a batch of uninitialized collection keys for this role
	 * @param collectionPersister the collection role
	 * @param id a key that must be included
	 * @param batchSize the maximum number of keys to return
	 * @return an array of collection keys, of length batchSize (padded with nulls)
	 */
	public Serializable[] getCollectionBatch(CollectionPersister collectionPersister, Serializable id, int batchSize);
	/**
	 * Get a batch of unloaded identifiers for this class
	 * @param clazz the persistent class
	 * @param id an identifier that must be included
	 * @param batchSize the maximum number of keys to return
	 * @return an array of identifiers, of length batchSize (padded with nulls)
	 */
	public Serializable[] getClassBatch(Class clazz, Serializable id, int batchSize);

	/**
	 * Register the entity as batch loadable, if enabled
	 */
	public void scheduleBatchLoad(Class clazz, Serializable id) throws MappingException;

	/**
	 * Execute an SQL Query
	 */
	public List findBySQL(String sqlQuery, String[] aliases, Class[] classes, QueryParameters queryParameters, Collection querySpaces) throws HibernateException;
	
	public void addNonExist(Key key);
	
	public Object copy(Object object, Map copiedAlready) throws HibernateException;
	
	public Object getCollectionOwner(Serializable key, CollectionPersister collectionPersister) throws MappingException;
}







