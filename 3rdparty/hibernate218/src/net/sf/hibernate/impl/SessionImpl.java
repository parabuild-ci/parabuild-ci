//$Id: SessionImpl.java,v 1.89 2005/01/29 23:01:47 oneovthafew Exp $
package net.sf.hibernate.impl;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.SystemException;
import javax.transaction.TransactionManager;

import net.sf.hibernate.AssertionFailure;
import net.sf.hibernate.CallbackException;
import net.sf.hibernate.Criteria;
import net.sf.hibernate.FlushMode;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Interceptor;
import net.sf.hibernate.JDBCException;
import net.sf.hibernate.Lifecycle;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.NonUniqueObjectException;
import net.sf.hibernate.ObjectDeletedException;
import net.sf.hibernate.ObjectNotFoundException;
import net.sf.hibernate.PersistentObjectException;
import net.sf.hibernate.PropertyValueException;
import net.sf.hibernate.Query;
import net.sf.hibernate.QueryException;
import net.sf.hibernate.ReplicationMode;
import net.sf.hibernate.ScrollableResults;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.StaleObjectStateException;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.TransactionException;
import net.sf.hibernate.TransientObjectException;
import net.sf.hibernate.UnresolvableObjectException;
import net.sf.hibernate.Validatable;
import net.sf.hibernate.WrongClassException;
import net.sf.hibernate.exception.JDBCExceptionHelper;
import net.sf.hibernate.cache.CacheException;
import net.sf.hibernate.cache.CacheConcurrencyStrategy.SoftLock;
import net.sf.hibernate.collection.ArrayHolder;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.collection.PersistentCollection;
import net.sf.hibernate.engine.Batcher;
import net.sf.hibernate.engine.CacheSynchronization;
import net.sf.hibernate.engine.Cascades;
import net.sf.hibernate.engine.CollectionSnapshot;
import net.sf.hibernate.engine.Key;
import net.sf.hibernate.engine.QueryParameters;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.engine.Versioning;
import net.sf.hibernate.hql.FilterTranslator;
import net.sf.hibernate.hql.QueryTranslator;
import net.sf.hibernate.id.IdentifierGenerationException;
import net.sf.hibernate.id.IdentifierGeneratorFactory;
import net.sf.hibernate.loader.CriteriaLoader;
import net.sf.hibernate.loader.SQLLoader;
import net.sf.hibernate.persister.ClassPersister;
import net.sf.hibernate.persister.OuterJoinLoadable;
import net.sf.hibernate.persister.SQLLoadable;
import net.sf.hibernate.persister.UniqueKeyLoadable;
import net.sf.hibernate.proxy.HibernateProxy;
import net.sf.hibernate.proxy.HibernateProxyHelper;
import net.sf.hibernate.proxy.LazyInitializer;
import net.sf.hibernate.type.AbstractComponentType;
import net.sf.hibernate.type.PersistentCollectionType;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.type.TypeFactory;
import net.sf.hibernate.util.ArrayHelper;
import net.sf.hibernate.util.EmptyIterator;
import net.sf.hibernate.util.IdentityMap;
import net.sf.hibernate.util.JoinedIterator;
import net.sf.hibernate.util.ReflectHelper;
import net.sf.hibernate.util.StringHelper;

import org.apache.commons.collections.SequencedHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Concrete implementation of a Session, and also the central, organizing component
 * of Hibernate's internal implementation. As such, this class exposes two interfaces;
 * Session itself, to the application, and SessionImplementor, to other components
 * of Hibernate.
 *
 * This is where all the hard work goes on.
 *
 * NOT THREADSAFE
 *
 * @author Gavin King
 */
public final class SessionImpl implements SessionImplementor {

	private static final Log log = LogFactory.getLog(SessionImpl.class);

	private transient SessionFactoryImpl factory;

	private final boolean autoClose;
	private final long timestamp;
	private boolean isCurrentTransaction; //a bit dodgy...
	private boolean closed = false;
	private FlushMode flushMode = FlushMode.AUTO;

	private final Map entitiesByKey;  //key=Key, value=Object
	private final Map proxiesByKey;  //key=Key, value=HibernateProxy
	private transient Map entityEntries;  //key=Object, value=Entry
	private transient Map arrayHolders; //key=array, value=ArrayHolder
	private transient Map collectionEntries; //key=PersistentCollection, value=CollectionEntry
	private final Map collectionsByKey; //key=CollectionKey, value=PersistentCollection

	private HashSet nullifiables = new HashSet(); //set of Keys of deleted objects

	private final HashSet nonExists;

	private Interceptor interceptor;

	private transient Connection connection;
	private transient boolean connect;

	// We keep scheduled insertions, deletions and updates in collections
	// and actually execute them as part of the flush() process. Actually,
	// not every flush() ends in execution of the scheduled actions. Auto-
	// flushes initiated by a query execution might be "shortcircuited".

	// Object insertions and deletions have list semantics because they
	// must happen in the right order so as to respect referential integrity
	private ArrayList insertions;
	private ArrayList deletions;
	// updates are kept in a Map because successive flushes might need to add
	// extra, new changes for an object that is already scheduled for update.
	// Note: we *could* treat updates the same way we treat collection actions
	// (discarding them at the end of a "shortcircuited" auto-flush) and then
	// we would keep them in a list
	private ArrayList updates;
	// Actually the semantics of the next three are really "Bag"
	// Note that, unlike objects, collection insertions, updates,
	// deletions are not really remembered between flushes. We
	// just re-use the same Lists for convenience.
	private ArrayList collectionCreations;
	private ArrayList collectionUpdates;
	private ArrayList collectionRemovals;

	private transient ArrayList executions;

	// The collections we are currently loading
	private transient Map loadingCollections;
	private transient List nonlazyCollections;
	// A set of entity keys that we predict might be needed for
	// loading soon
	private transient Map batchLoadableEntityKeys; //actually, a Set
	private static final Object MARKER = new Object();

	private transient int dontFlushFromFind = 0;
	//private transient boolean reentrantCallback = false;
	private transient int cascading = 0;
	private transient int loadCounter = 0;
	private transient boolean flushing = false;

	private transient Batcher batcher;

	/**
	 * Represents the status of an entity with respect to
	 * this session. These statuses are for internal
	 * book-keeping only and are not intended to represent
	 * any notion that is visible to the _application_.
	 */
	static final class Status implements Serializable {
		private String name;
		Status(String name) {
			this.name=name;
		}
		public String toString() {
			return name;
		}

		private Object readResolve() throws ObjectStreamException {
			if ( name.equals(LOADED.name) ) return LOADED;
			if ( name.equals(DELETED.name) ) return DELETED;
			if ( name.equals(GONE.name) ) return GONE;
			if ( name.equals(LOADING.name) ) return LOADING;
			throw new InvalidObjectException("invalid Status");
		}

	}
	private static final Status LOADED = new Status("LOADED");
	private static final Status DELETED = new Status("DELETED");
	private static final Status GONE = new Status("GONE");
	private static final Status LOADING = new Status("LOADING");
	private static final Status SAVING = new Status("SAVING");

	interface Executable {
		public void beforeExecutions() throws HibernateException;
		public void execute() throws HibernateException;
		public boolean hasAfterTransactionCompletion();
		public void afterTransactionCompletion(boolean success) throws HibernateException;
		public Serializable[] getPropertySpaces();
	}

	/**
	 * We need an entry to tell us all about the current state
	 * of an object with respect to its persistent state
	 */
	static final class EntityEntry implements Serializable {

		LockMode lockMode;
		Status status;
		Serializable id;
		Object[] loadedState;
		Object[] deletedState;
		boolean existsInDatabase;
		Object version;
		transient ClassPersister persister; // for convenience to save some lookups
		String className;
		boolean isBeingReplicated;

		EntityEntry(
			Status status,
			Object[] loadedState,
			Serializable id,
			Object version,
			LockMode lockMode,
			boolean existsInDatabase,
			ClassPersister persister,
			boolean disableVersionIncrement
		) {
			this.status = status;
			this.loadedState = loadedState;
			this.id = id;
			this.existsInDatabase = existsInDatabase;
			this.version = version;
			this.lockMode = lockMode;
			this.isBeingReplicated = disableVersionIncrement;
			this.persister = persister;
			if (persister!=null) className = persister.getClassName();
		}

	}

	/**
	 * We need an entry to tell us all about the current state
	 * of a collection with respect to its persistent state
	 */
	public static final class CollectionEntry implements CollectionSnapshot, Serializable {
		// collections detect changes made via
		// their public interface and mark
		// themselves as dirty
		boolean dirty;
		// during flush, we navigate the object graph to
		// collections and decide what to do with them
		transient boolean reached;
		transient boolean processed;
		transient boolean doupdate;
		transient boolean doremove;
		transient boolean dorecreate;
		// if we instantiate a collection during the flush() process,
		// we must ignore it for the rest of the flush()
		transient boolean ignore;
		// collections might be lazily initialized
		boolean initialized;
		// "current" means the reference that was found
		// during flush() and "loaded" means the reference
		// that is consistent with the current database
		// state
		transient CollectionPersister currentPersister;
		transient Serializable currentKey;
		transient CollectionPersister loadedPersister;
		Serializable loadedKey;
		// session-start/post-flush persistent state
		Serializable snapshot;
		// allow the snapshot to be serialized
		private String role;

		public CollectionEntry() {
			this.dirty = false; //a newly wrapped collection is NOT dirty (or we get unnecessary version updates)
			this.initialized = true;
			// new collections that get found + wrapped
			// during flush shouldn't be ignored
			this.ignore = false;
		}

		CollectionEntry(CollectionPersister loadedPersister, Serializable loadedID) {
			// detached collection wrappers that get found + reattached
			// during flush shouldn't be ignored
			this(loadedPersister, loadedID, false);
		}

		CollectionEntry(CollectionPersister loadedPersister, Serializable loadedID, boolean ignore) {
			this.dirty = false;
			this.initialized = false;
			this.loadedKey = loadedID;
			setLoadedPersister(loadedPersister);
			this.ignore = ignore;
		}

		CollectionEntry(CollectionSnapshot cs, SessionFactoryImplementor factory)
		throws MappingException {
			this.dirty = cs.getDirty();
			this.snapshot = cs.getSnapshot();
			this.loadedKey = cs.getKey();
			this.initialized = true;
			// detached collections that get found + reattached
			// during flush shouldn't be ignored
			this.ignore = false;
			setLoadedPersister(
				factory.getCollectionPersister( cs.getRole() )
			);
		}

		private boolean isDirty(PersistentCollection coll) throws HibernateException {
			if ( dirty || (
				!coll.isDirectlyAccessible() && !loadedPersister.getElementType().isMutable()
			) ) {
				return dirty;
			}
			else {
				return !coll.equalsSnapshot( loadedPersister.getElementType() );
			}
		}

		void preFlush(PersistentCollection collection) throws HibernateException {

			// if the collection is initialized and it was previously persistent
			// initialize the dirty flag
			dirty = ( initialized && loadedPersister!=null && isDirty(collection) ) ||
				( !initialized && dirty ); //only need this so collection with queued adds will be removed from JCS cache

			if ( log.isDebugEnabled() && dirty && loadedPersister!=null ) log.debug(
				"Collection dirty: " + MessageHelper.infoString(loadedPersister, loadedKey)
			);

			doupdate = false;
			doremove = false;
			dorecreate = false;
			reached = false;
			processed = false;
		}

		void postInitialize(PersistentCollection collection)
		throws HibernateException {
			initialized = true;
			snapshot = collection.getSnapshot(loadedPersister);
		}

		// called after a *successful* flush
		boolean postFlush(PersistentCollection collection)
		throws HibernateException {

			if (ignore) {
				ignore = false;
			}
			else {
				if (!processed) throw new AssertionFailure("collection was not processed by flush()");
				loadedKey = currentKey;
				setLoadedPersister(currentPersister);
				dirty = false;
				collection.postFlush();
				// re-snapshot, if required
				if ( initialized && ( doremove || dorecreate || doupdate ) ) {
					initSnapshot(collection, loadedPersister);
				}
			}
			
			return loadedPersister==null;

		}

		public void initSnapshot(PersistentCollection collection, CollectionPersister persister)
		throws HibernateException {
			snapshot = collection.getSnapshot(persister);
		}

		public boolean getDirty() {
			return dirty;
		}

		public Serializable getKey() {
			return loadedKey;
		}

		public String getRole() {
			return role;
		}

		public Serializable getSnapshot() {
			return snapshot;
		}

		public boolean snapshotIsEmpty() {
			//TODO: implementation here is non-extensible ...
			//should use polymorphism
			return initialized && snapshot!=null && (
				( snapshot instanceof Collection && ( (Collection) snapshot ).size()==0 ) || // if snapshot is a collection
				( snapshot instanceof Map && ( (Map) snapshot ).size()==0 ) || // if snapshot is a map
				( snapshot.getClass().isArray() && Array.getLength(snapshot)==0 )// if snapshot is an array
			);
		}

		public void setDirty() {
			dirty = true;
		}

		void setLoadedPersister(CollectionPersister persister) {
			loadedPersister=persister;
			role = (persister==null) ? null : persister.getRole();
		}

		/*public boolean isInitialized() {
			return initialized;
		}*/

		public boolean isNew() {
			return initialized && snapshot==null; //TODO: is this a correct implementation?
		}
		public boolean wasDereferenced() {
			return loadedKey==null;
		}

	}

	static final class CollectionKey implements Serializable {
		private String role;
		private Serializable key;
		CollectionKey(String role, Serializable key) {
			this.role=role;
			this.key=key;
		}
		CollectionKey(CollectionPersister persister, Serializable key) {
			this.role=persister.getRole();
			this.key=key;
		}
		public boolean equals(Object other) {
			CollectionKey that = (CollectionKey) other;
			return that.role.equals(role) && that.key.equals(key);
		}
		public int hashCode() {
			int result = 17;
			result = 37 * result + role.hashCode();
			result = 37 * result + key.hashCode();
			return result;
		}
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {

		log.trace("deserializing session");

		interceptor = (Interceptor) ois.readObject();
		factory = (SessionFactoryImpl) ois.readObject();

		ois.defaultReadObject();

		entityEntries = IdentityMap.deserialize( ois.readObject() );
		collectionEntries = IdentityMap.deserialize( ois.readObject() );
		arrayHolders = IdentityMap.deserialize( ois.readObject() );
		initTransientState();

		// we need to reconnect all proxies and collections to this session
		// the association is transient because serialization is used for
		// different things.

		Iterator iter = collectionEntries.entrySet().iterator();
		while ( iter.hasNext() ) {
			try {
				Map.Entry e = (Map.Entry) iter.next();
				( (PersistentCollection) e.getKey() ).setCurrentSession(this);
				CollectionEntry ce = (CollectionEntry) e.getValue();
				if ( ce.getRole()!=null ) ce.setLoadedPersister(
					factory.getCollectionPersister( ce.getRole() )
				);
			}
			catch (HibernateException he) {
				throw new InvalidObjectException( he.getMessage() );
			}
		}
		iter = proxiesByKey.values().iterator();
		while ( iter.hasNext() ) {
			Object proxy = iter.next();
			if ( proxy instanceof HibernateProxy ) {
				HibernateProxyHelper.getLazyInitializer( (HibernateProxy) proxy ).setSession(this);
			}
			else {
				iter.remove(); //the proxy was pruned during the serialization process
			}
		}

		iter = entityEntries.entrySet().iterator();
		while ( iter.hasNext() ) {
			EntityEntry e = (EntityEntry) ( (Map.Entry) iter.next() ).getValue();
			try {
				e.persister = factory.getPersister(e.className);
			}
			catch (MappingException me) {
				throw new InvalidObjectException( me.getMessage() );
			}
		}
	}

	private void writeObject(ObjectOutputStream oos) throws IOException {
		if ( isConnected() ) throw new IllegalStateException("Cannot serialize a Session while connected");

		log.trace("serializing session");

		oos.writeObject(interceptor);
		oos.writeObject(factory);

		oos.defaultWriteObject();

		oos.writeObject( IdentityMap.serialize(entityEntries) );
		oos.writeObject( IdentityMap.serialize(collectionEntries) );
		oos.writeObject( IdentityMap.serialize(arrayHolders) );
	}


	SessionImpl(Connection connection, SessionFactoryImpl factory, boolean autoclose, long timestamp, Interceptor interceptor) {

		this.connection = connection;
		connect = connection==null;
		this.interceptor = interceptor;

		this.autoClose = autoclose;
		this.timestamp = timestamp;

		this.factory = factory;

		entitiesByKey = new HashMap(50);
		proxiesByKey = new HashMap(10);
		nonExists = new HashSet(10);
		entityEntries = IdentityMap.instantiateSequenced(50);
		collectionEntries = IdentityMap.instantiateSequenced(30);
		collectionsByKey = new HashMap(30);
		arrayHolders = IdentityMap.instantiate(10);

		insertions = new ArrayList( 20 );
		deletions = new ArrayList( 20 );
		updates = new ArrayList( 20 );
		collectionCreations = new ArrayList( 20 );
		collectionRemovals = new ArrayList( 20 );
		collectionUpdates = new ArrayList( 20 );

		initTransientState();

		log.debug("opened session");

	}

	public Batcher getBatcher() {
		return batcher;
	}

	public SessionFactoryImplementor getFactory() {
		return factory;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public Connection close() throws HibernateException {

		log.trace("closing session");

		try {
			if (connection==null) {
				connect = false;
				return null;
			}
			else {
				return disconnect();
			}
		}
		finally {
			cleanup();
		}
	}

	public void afterTransactionCompletion(boolean success) {

		log.trace("transaction completion");

		isCurrentTransaction = false;

		// Downgrade locks
		Iterator iter = entityEntries.values().iterator();
		while ( iter.hasNext() ) {
			( (EntityEntry) iter.next() ).lockMode = LockMode.NONE;
		}

		// Release cache softlocks
		int size = executions.size();
		final boolean invalidateQueryCache = factory.isQueryCacheEnabled();
		for ( int i=0; i<size; i++ ) {
			try {
				Executable exec = (Executable) executions.get(i);
				try {
					exec.afterTransactionCompletion(success);
				}
				finally {
					if (invalidateQueryCache) factory.getUpdateTimestampsCache().invalidate( exec.getPropertySpaces() );
				}
			}
			catch (CacheException ce) {
				log.error("could not release a cache lock", ce);
				// continue loop
			}
			catch (Exception e) {
				throw new AssertionFailure("Exception releasing cache locks", e);
			}
		}
		executions.clear();

	}

	private void initTransientState() {
		executions = new ArrayList(50);
		batchLoadableEntityKeys = new SequencedHashMap(30);
		loadingCollections = new HashMap();
		nonlazyCollections = new ArrayList(20);

		batcher = factory.isJdbcBatchUpdateEnabled() ?
			(Batcher) new BatchingBatcher(this) :
			(Batcher) new NonBatchingBatcher(this);
	}

	private void cleanup() {
		closed = true;
		entitiesByKey.clear();
		proxiesByKey.clear();
		entityEntries.clear();
		arrayHolders.clear();
		collectionEntries.clear();
		nullifiables.clear();
		batchLoadableEntityKeys.clear();
		collectionsByKey.clear();
		nonExists.clear();

		/*insertions.clear();
		deletions.clear();
		updates.clear();
		collectionCreations.clear();
		collectionRemovals.clear();
		collectionUpdates.clear();*/

	}

	public LockMode getCurrentLockMode(Object object) throws HibernateException {
		if (object==null) throw new NullPointerException("null object passed to getCurrentLockMode()");
		if ( object instanceof HibernateProxy ) {
			object = HibernateProxyHelper.getLazyInitializer( (HibernateProxy) object ).getImplementation(this);
			if (object==null) return LockMode.NONE;
		}
		EntityEntry e = getEntry(object);
		if (e==null) throw new TransientObjectException("Given object not associated with the session");
		if ( e.status!=LOADED ) throw new ObjectDeletedException( "The given object was deleted", e.id, object.getClass() );
		return e.lockMode;
	}

	public LockMode getLockMode(Object object) {
		return getEntry(object).lockMode;
	}

	private void addEntity(Key key, Object object) {
		entitiesByKey.put(key, object);
		if ( key.isBatchLoadable() ) batchLoadableEntityKeys.remove(key);
	}
	public Object getEntity(Key key) {
		return entitiesByKey.get(key);
	}
	private Object removeEntity(Key key) {
		return entitiesByKey.remove(key);
	}

	public void setLockMode(Object entity, LockMode lockMode) {
		getEntry(entity).lockMode = lockMode;
	}

	private EntityEntry addEntry(
		Object object,
		Status status,
		Object[] loadedState,
		Serializable id,
		Object version,
		LockMode lockMode,
		boolean existsInDatabase,
		ClassPersister persister,
		boolean disableVersionIncrement
	) {
		EntityEntry e = new EntityEntry(status, loadedState, id, version, lockMode, existsInDatabase, persister, disableVersionIncrement);
		entityEntries.put(object, e);
		return e;
	}

	private EntityEntry getEntry(Object object) {
		return (EntityEntry) entityEntries.get(object);
	}

	private EntityEntry removeEntry(Object object) {
		return (EntityEntry) entityEntries.remove(object);
	}

	private boolean isEntryFor(Object object) {
		return entityEntries.containsKey(object);
	}

	private CollectionEntry getCollectionEntry(PersistentCollection coll) {
		return (CollectionEntry) collectionEntries.get(coll);
	}

	public boolean isOpen() { return !closed; }

	/**
	 * Save a transient object.
	 * An id is generated, assigned to the given object and returned.
	 */
	public Serializable save(Object obj) throws HibernateException {

		if (obj==null) throw new NullPointerException("attempted to save null");

		Object object = unproxy(obj); //throws exception if uninitialized

		EntityEntry e = getEntry(object);
		if ( e!=null ) {
			if ( e.status==DELETED ) {
				forceFlush(e);
			}
			else {
				log.trace( "object already associated with session" );
				return e.id;
			}
		}

		Serializable id = saveWithGeneratedIdentifier(object, Cascades.ACTION_SAVE_UPDATE, null); //id might be generated by SQL insert
		reassociateProxy(obj, id); //TODO: move into saveWithGeneratedIdentifier()?
		return id;

	}

	private void forceFlush(EntityEntry e) throws HibernateException {
		if ( log.isDebugEnabled() ) {
			log.debug(
				"flushing to force deletion of re-saved object: " +
				MessageHelper.infoString(e.persister, e.id)
			);
		}
		if (cascading>0) {
			throw new ObjectDeletedException(
				"deleted object would be re-saved by cascade (remove deleted object from associations)",
				e.id,
				e.persister.getMappedClass()
			);
		}
		flush();
	}

	private Serializable saveWithGeneratedIdentifier(Object object, Cascades.CascadingAction action, Object anything)
	throws HibernateException {
		ClassPersister persister = getPersister(object);
		try {
			Serializable id = persister.getIdentifierGenerator()
				.generate(this, object);

			if (id==null) {
				throw new IdentifierGenerationException("null id generated for: " + object.getClass());
			}
			else if (id==IdentifierGeneratorFactory.SHORT_CIRCUIT_INDICATOR) {
				return getIdentifier(object); //yick!
			}
			else if (id==IdentifierGeneratorFactory.IDENTITY_COLUMN_INDICATOR) {
				return doSave(object, null, persister, true, action, anything);
			}
			else {
				if ( log.isDebugEnabled() ) log.debug("generated identifier: " + id);
				return doSave(object, id, persister, false, action, anything);
			}
		}
		catch (SQLException sqle) {
			throw convert( sqle, "Could not save object" );
		}
	}

	/**
	 * Save a transient object with a manually assigned ID.
	 */
	public void save(Object obj, Serializable id) throws HibernateException {

		if (obj==null) throw new NullPointerException("attempted to insert null");
		if (id==null) throw new NullPointerException("null identifier passed to insert()");

		Object object = unproxy(obj); //throws exception if uninitialized!

		EntityEntry e = getEntry(object);
		if ( e!=null ) {
			if ( e.status==DELETED ) {
				forceFlush(e);
			}
			else {
				if ( !id.equals(e.id) ) throw new PersistentObjectException(
					"object passed to save() was already persistent: " +
					MessageHelper.infoString(e.persister, id)
				);
				log.trace( "object already associated with session" );
			}
		}

		doSave(object, id, getPersister(object), false, Cascades.ACTION_SAVE_UPDATE, null);

		reassociateProxy(obj, id);
	}

	private Serializable doSave(
		final Object object,
		final Serializable id,
		final ClassPersister persister,
		final boolean useIdentityColumn,
		final Cascades.CascadingAction cascadeAction,
		final Object anything)
	throws HibernateException {

		if ( log.isTraceEnabled() ) log.trace( "saving " + MessageHelper.infoString(persister, id) );

		final Key key;
		if (useIdentityColumn) {
			// if the id is generated by the database, we assign the key later
			key = null;
		}
		else {
			key = new Key(id, persister);
			Object old = getEntity(key);
			if (old!= null) {
				EntityEntry e = getEntry(old);
				if (e.status==DELETED) {
					forceFlush(e);
				}
				else {
					throw new NonUniqueObjectException( id, persister.getMappedClass() );
				}
			}
			persister.setIdentifier(object, id);
		}

		// Sub-insertions should occur before containing insertion so
		// Try to do the callback now
		if ( persister.implementsLifecycle() ) {
			log.debug("calling onSave()");
			if ( ( (Lifecycle) object ).onSave(this) ) {
				log.debug("insertion vetoed by onSave()");
				return id;
			}
		}

		return doSave(object, key, persister, false, useIdentityColumn, cascadeAction, anything);

	}

	private Serializable doSave(
		final Object object,
		Key key,
		final ClassPersister persister,
		final boolean replicate,
		final boolean useIdentityColumn,
		final Cascades.CascadingAction cascadeAction,
		final Object anything)
	throws HibernateException {

		if ( persister.implementsValidatable() ) ( (Validatable) object ).validate();

		Serializable id;
		if (useIdentityColumn) {
			id = null;
			executeInserts();
		}
		else {
			id = key.getIdentifier();
		}

		// Put a placeholder in entries, so we don't recurse back and try to save() the
		// same object again. QUESTION: should this be done before onSave() is called?
		// likewise, should it be done before onUpdate()?
		addEntry(object, SAVING, null, id, null, LockMode.WRITE, useIdentityColumn, persister, false); //okay if id is null here

		// cascade-save to many-to-one BEFORE the parent is saved
		cascading++;
		try {
			Cascades.cascade(this, persister, object, cascadeAction, Cascades.CASCADE_BEFORE_INSERT_AFTER_DELETE, anything);
		}
		finally {
			cascading--;
		}

		Object[] values = persister.getPropertyValues(object);
		Type[] types = persister.getPropertyTypes();

		boolean substitute = false;
		if (!replicate) {

			substitute = interceptor.onSave( object, id, values, persister.getPropertyNames(), types );

			//keep the existing version number in the case of replicate!
			if ( persister.isVersioned() ) {
				substitute = Versioning.seedVersion(
					values, persister.getVersionProperty(), persister.getVersionType()
				) || substitute;
			}
		}

		if ( persister.hasCollections() ) {
			//TODO: make OnReplicateVisitor extend WrapVisitor
			if (replicate) {
				OnReplicateVisitor visitor = new OnReplicateVisitor(this, id);
				visitor.processValues(values, types);
			}
			WrapVisitor visitor = new WrapVisitor(this);
			// substitutes into values by side-effect
			visitor.processValues(values, types);
			substitute = substitute || visitor.isSubstitutionRequired();
		}

		if (substitute) persister.setPropertyValues(object, values);

		TypeFactory.deepCopy(values, types, persister.getPropertyUpdateability(), values);
		nullifyTransientReferences(values, types, useIdentityColumn, object);
		checkNullability(values, persister, false);

		if (useIdentityColumn) {
			ScheduledIdentityInsertion insert = new ScheduledIdentityInsertion(values, object, persister, this);
			execute(insert);
			id = insert.getGeneratedId();
			persister.setIdentifier(object, id);
			key = new Key(id, persister);
			checkUniqueness(key, object);
		}

		Object version = Versioning.getVersion(values, persister);
		addEntity(key, object);
		addEntry(object, LOADED, values, id, version, LockMode.WRITE, useIdentityColumn, persister, replicate);
		nonExists.remove(key);

		if (!useIdentityColumn) {
			insertions.add( new ScheduledInsertion(id, values, object, version, persister, this) );
		}

		// cascade-save to collections AFTER the collection owner was saved
		cascading++;
		try {
			Cascades.cascade(this, persister, object, cascadeAction, Cascades.CASCADE_AFTER_INSERT_BEFORE_DELETE, anything);
		}
		finally {
			cascading--;
		}

		return id;

	}

	boolean reassociateIfUninitializedProxy(Object value) throws MappingException {
		if ( !Hibernate.isInitialized(value) ) {
			HibernateProxy proxy = (HibernateProxy) value;
			LazyInitializer li = HibernateProxyHelper.getLazyInitializer(proxy);
			reassociateProxy(li, proxy);
			return true;
		}
		else {
			return false;
		}
	}

	private void reassociateProxy(Object value, Serializable id) throws MappingException {
		if (value instanceof HibernateProxy) {
			HibernateProxy proxy = (HibernateProxy) value;
			LazyInitializer li = HibernateProxyHelper.getLazyInitializer(proxy);
			li.setIdentifier(id);
			reassociateProxy(li, proxy);
		}
	}

	private Object unproxy(Object maybeProxy) throws HibernateException {
		if ( maybeProxy instanceof HibernateProxy ) {
			HibernateProxy proxy = (HibernateProxy) maybeProxy;
			LazyInitializer li = HibernateProxyHelper.getLazyInitializer(proxy);
			if ( li.isUninitialized() ) throw new PersistentObjectException(
				"object was an uninitialized proxy for: " + li.getPersistentClass().getName()
			);
			return li.getImplementation(); //unwrap the object
		}
		else {
			return maybeProxy;
		}
	}

	private Object unproxyAndReassociate(Object maybeProxy) throws HibernateException {
		if ( maybeProxy instanceof HibernateProxy ) {
			HibernateProxy proxy = (HibernateProxy) maybeProxy;
			LazyInitializer li = HibernateProxyHelper.getLazyInitializer(proxy);
			reassociateProxy(li, proxy);
			return li.getImplementation(); //initialize + unwrap the object
		}
		else {
			return maybeProxy;
		}
	}

	/**
	 * associate a proxy that was instantiated by another session with this session
	 */
	private void reassociateProxy(LazyInitializer li, HibernateProxy proxy) throws MappingException {
		if ( li.getSession()!=this ) {
			ClassPersister persister = getClassPersister( li.getPersistentClass() );
			Key key = new Key( li.getIdentifier(), persister );
			if ( !proxiesByKey.containsKey(key) ) proxiesByKey.put(key, proxy); // any earlier proxy takes precedence
			HibernateProxyHelper.getLazyInitializer( proxy ).setSession(this);
		}
	}

	private void nullifyTransientReferences(Object[] values, Type[] types, boolean earlyInsert, Object self) throws HibernateException {
		for ( int i=0; i<types.length; i++ ) {
			values[i] = nullifyTransientReferences( values[i], types[i], earlyInsert, self );
		}
	}

	/**
	 * Return null if the argument is an "unsaved" entity (ie. one with no existing database row),
	 * or the input argument otherwise. This is how Hibernate avoids foreign key constraint
	 * violations.
	 */
	private Object nullifyTransientReferences(Object value, Type type, boolean earlyInsert, Object self) throws HibernateException {
		if ( value==null ) {
			return null;
		}
		else if ( type.isEntityType() || type.isObjectType() ) {
			return ( isUnsaved(value, earlyInsert, self) ) ? null : value;
		}
		else if ( type.isComponentType() ) {
			AbstractComponentType actype = (AbstractComponentType) type;
			Object[] subvalues = actype.getPropertyValues(value, this);
			Type[] subtypes = actype.getSubtypes();
			boolean substitute=false;
			for ( int i=0; i<subvalues.length; i++ ) {
				Object replacement = nullifyTransientReferences( subvalues[i], subtypes[i], earlyInsert, self );
				if ( replacement!=subvalues[i] ) {
					substitute=true;
					subvalues[i]=replacement;
				}
			}
			if (substitute) actype.setPropertyValues(value, subvalues);
			return value;
		}
		else {
			return value;
		}
	}

	/**
	 * determine if the object already exists in the database, using a
	 * "best guess"
	 */
	private boolean isUnsaved(Object object, boolean earlyInsert, Object self) throws HibernateException {

		if ( object instanceof HibernateProxy ) {
			// if its an uninitialized proxy it can't be transient
			LazyInitializer li = HibernateProxyHelper.getLazyInitializer( (HibernateProxy) object );
			if ( li.getImplementation(this)==null ) {
				return false;
				// ie. we never have to null out a reference to
				// an uninitialized proxy
			}
			else {
				//unwrap it
				object = li.getImplementation();
			}
		}

		// if it was a reference to self, don't need to nullify
		// unless we are using native id generation, in which
		// case we definitely need to nullify
		if (object==self) return earlyInsert;

		// See if the entity is already bound to this session, if not look at the
		// entity identifier and assume that the entity is persistent if the
		// id is not "unsaved" (that is, we rely on foreign keys to keep
		// database integrity)

		EntityEntry e = getEntry(object);
		if (e==null) {
			return getPersister(object).isUnsaved(object);
			/*if( persister.hasIdentifierPropertyOrEmbeddedCompositeIdentifier() ) {
				Serializable id = persister.getIdentifier(object);
				if (id!=null) {
					// see if theres another object that *is* associated with the session for that id
					e = getEntry( getEntity( new Key(id, persister) ) );

					if (e==null) { // look at the id value
						return persister.isUnsaved(object);
					}
					// else use the other object's entry....
				}
				else { // null id, so have to assume transient (because thats safer)
					return true;
				}
			}
			else { // can't determine the id, so assume transient (because thats safer)
				return true;
			}*/
		}

		return e.status==SAVING || (
			earlyInsert ?
			!e.existsInDatabase :
			nullifiables.contains( new Key (e.id, e.persister) )
		);

	}

	/**
	 * Delete a persistent object
	 */
	public void delete(Object object) throws HibernateException {

		if (object==null) throw new NullPointerException("attempted to delete null");

		object = unproxyAndReassociate(object);

		EntityEntry entry = getEntry(object);
		final ClassPersister persister;
		if (entry==null) {
			log.trace("deleting a transient instance");

			persister = getPersister(object);
			Serializable id = persister.getIdentifier(object);

			if (id==null) throw new TransientObjectException("the transient instance passed to delete() had a null identifier");

			Object old = getEntity( new Key(id, persister) );

			if (old!=null) throw new NonUniqueObjectException( id, persister.getMappedClass() );

			new OnUpdateVisitor(this, id).process(object, persister);

			addEntity( new Key(id, persister), object );
			entry = addEntry(
				object, LOADED,
				persister.getPropertyValues(object),
				id,
				persister.getVersion(object),
				LockMode.NONE,
				true,
				persister,
				false
			);
			// not worth worrying about the proxy
		}
		else {
			log.trace("deleting a persistent instance");

			if ( entry.status==DELETED || entry.status==GONE ) {
				log.trace("object was already deleted");
				return;
			}
			persister = entry.persister;
		}

		if ( !persister.isMutable() ) throw new HibernateException(
			"attempted to delete an object of immutable class: " +
			MessageHelper.infoString(persister)
		);

		doDelete(object, entry, persister);

	}

	private void doDelete(Object object, EntityEntry entry, ClassPersister persister) throws HibernateException {

		if ( log.isTraceEnabled() ) log.trace( "deleting " + MessageHelper.infoString(persister, entry.id) );

		Type[] types = persister.getPropertyTypes();

		Object version = entry.version;//getCurrentVersion();

		final Object[] loadedState;
		if ( entry.loadedState==null ) { //ie. the object came in from update()
			loadedState = persister.getPropertyValues(object);
		}
		else {
			loadedState = entry.loadedState;
		}
		entry.deletedState = new Object[types.length];
		TypeFactory.deepCopy(loadedState, types, persister.getPropertyUpdateability(), entry.deletedState);

		interceptor.onDelete(object, entry.id, entry.deletedState, persister.getPropertyNames(), types);

		entry.status = DELETED; //before cascade and Lifecycle callback, so we are circular-reference-safe
		Key key = new Key(entry.id, persister);

		List deletionsByOnDelete = null;
		HashSet nullifiablesAfterOnDelete = null;

		// do Lifecycle callback before cascades, since this can veto
		if ( persister.implementsLifecycle() ) {
			HashSet oldNullifiables = (HashSet) nullifiables.clone();
			ArrayList oldDeletions = (ArrayList) deletions.clone();
			nullifiables.add(key); //the deletion of the parent is actually executed BEFORE any deletion from onDelete()
			try {
				log.debug("calling onDelete()");
				if ( ( (Lifecycle) object ).onDelete(this) ) {
					//rollback deletion
					entry.status = LOADED;
					entry.deletedState = null;
					nullifiables=oldNullifiables;
					log.debug("deletion vetoed by onDelete()");
					return; //don't let it cascade
				}
			}
			catch (CallbackException ce) {
				//rollback deletion
				entry.status = LOADED;
				entry.deletedState = null;
				nullifiables=oldNullifiables;
				throw ce;
			}
			//note, the following assumes that onDelete() didn't cause the session
			//to be flushed! TODO: add a better check that it doesn't
			if ( oldDeletions.size() > deletions.size() ) throw new HibernateException("session was flushed during onDelete()");
			deletionsByOnDelete = deletions.subList( oldDeletions.size(), deletions.size() );
			deletions = oldDeletions;
			nullifiablesAfterOnDelete = nullifiables;
			nullifiables = oldNullifiables;
		}

		cascading++;
		try {
			// cascade-delete to collections BEFORE the collection owner is deleted
			Cascades.cascade(this, persister, object, Cascades.ACTION_DELETE, Cascades.CASCADE_AFTER_INSERT_BEFORE_DELETE);
		}
		finally {
			cascading--;
		}

		nullifyTransientReferences(entry.deletedState, types, false, object);
		checkNullability(entry.deletedState, persister, true);
		nullifiables.add(key);

		ScheduledDeletion delete = new ScheduledDeletion(entry.id, version, object, persister, this);
		deletions.add(delete); // Ensures that containing deletions happen before sub-deletions

		if ( persister.implementsLifecycle() ) {
			// after nullify, because we don't want to nullify references to subdeletions
			nullifiables.addAll(nullifiablesAfterOnDelete);
			// after deletions.add(), to respect foreign key constraints
			deletions.addAll(deletionsByOnDelete);
		}

		cascading++;
		try {
			// cascade-delete to many-to-one AFTER the parent was deleted
			Cascades.cascade(this, persister, object, Cascades.ACTION_DELETE, Cascades.CASCADE_BEFORE_INSERT_AFTER_DELETE);
		}
		finally {
			cascading--;
		}

	}

	private static void checkNullability(Object[] values, ClassPersister persister, boolean isUpdate)
	throws PropertyValueException {
		boolean[] nullability = persister.getPropertyNullability();
		boolean[] checkability = isUpdate ?
			persister.getPropertyUpdateability() :
			persister.getPropertyInsertability();
		for ( int i=0; i<values.length; i++ ) {
			if ( !nullability[i] && checkability[i] && values[i]==null ) {
				throw new PropertyValueException(
					"not-null property references a null or transient value: ",
					persister.getMappedClass(),
					persister.getPropertyNames()[i]
				);
			}
		}
	}

	void removeCollection(CollectionPersister role, Serializable id) throws HibernateException {
		if ( log.isTraceEnabled() ) log.trace( "collection dereferenced while transient " + MessageHelper.infoString(role, id) );
		/*if ( role.hasOrphanDelete() ) {
			throw new HibernateException(
				"You may not dereference a collection with cascade=\"all-delete-orphan\": " +
				MessageHelper.infoString(role, id)
			);
		}*/
		collectionRemovals.add( new ScheduledCollectionRemove(role, id, false, this) );
	}

	static boolean isCollectionSnapshotValid(CollectionSnapshot snapshot) {
		return snapshot!=null &&
			snapshot.getRole()!=null &&
			snapshot.getKey()!=null;
	}

	static boolean isOwnerUnchanged(CollectionSnapshot snapshot, CollectionPersister persister, Serializable id) {
		return isCollectionSnapshotValid(snapshot) &&
			persister.getRole().equals( snapshot.getRole() ) &&
			id.equals( snapshot.getKey() );
	}

	/**
	 * Reattach a detached (disassociated) initialized or uninitialized
	 * collection wrapper
	 */
	void reattachCollection(PersistentCollection collection, CollectionSnapshot snapshot)
	throws HibernateException {
		if ( collection.wasInitialized() ) {
			addInitializedDetachedCollection(collection, snapshot);
		}
		else {
			if ( !isCollectionSnapshotValid(snapshot) ) {
				throw new HibernateException("could not reassociate uninitialized transient collection");
			}
			addUninitializedDetachedCollection(
				collection,
				getCollectionPersister( snapshot.getRole() ),
				snapshot.getKey()
			);
		}
	}

	public void update(Object obj) throws HibernateException {

		if (obj==null) throw new NullPointerException("attempted to update null");

		if ( reassociateIfUninitializedProxy(obj) ) return;

		Object object = unproxyAndReassociate(obj);

		ClassPersister persister = getPersister(object);

		if ( isEntryFor(object) ) {
			log.trace("object already associated with session");
			// do nothing
		}
		else {
			// the object is transient
			Serializable id = persister.getIdentifier(object);

			if (id==null) {
				// assume this is a newly instantiated transient object
				throw new HibernateException(
					"The given object has a null identifier property " + MessageHelper.infoString(persister)
				);
			}
			else {
				doUpdate(object, id, persister);
			}

		}

	}

	public void saveOrUpdate(Object obj) throws HibernateException {
		if (obj==null) throw new NullPointerException("attempted to update null");

		if ( reassociateIfUninitializedProxy(obj) ) return;

		Object object = unproxyAndReassociate(obj); //a proxy is always "update", never "save"

		EntityEntry e = getEntry(object);
		if (e!=null && e.status!=DELETED) {
			// do nothing for persistent instances
			log.trace("saveOrUpdate() persistent instance");
		}
		else if (e!=null) { //ie. e.status==DELETED
			log.trace("saveOrUpdate() deleted instance");
			save(obj);
		}
		else {

			// the object is transient
			Boolean isUnsaved = interceptor.isUnsaved(object);
			ClassPersister persister = getPersister(object);
			if (isUnsaved==null) {
				// use unsaved-value

				if ( persister.isUnsaved(object) ) {
					log.trace("saveOrUpdate() unsaved instance");
					save(obj);
				}
				else {
					Serializable id = persister.getIdentifier(object);
					if ( log.isTraceEnabled() ) log.trace("saveOrUpdate() previously saved instance with id: " + id);
					doUpdate(object, id, persister);
				}

			}
			else {
				if ( isUnsaved.booleanValue() ) {
					log.trace("saveOrUpdate() unsaved instance");
					save(obj);
				}
				else {
					log.trace("saveOrUpdate() previously saved instance");
					doUpdate( object, persister.getIdentifier(object), persister );
				}
			}

		}

	}

	public void update(Object obj, Serializable id) throws HibernateException {
		if (id==null) throw new NullPointerException("null is not a valid identifier");
		if (obj==null) throw new NullPointerException("attempted to update null");

		if ( obj instanceof HibernateProxy ) {
			HibernateProxyHelper.getLazyInitializer( (HibernateProxy) obj ).setIdentifier(id);
		}

		if ( reassociateIfUninitializedProxy(obj) ) return;

		Object object = unproxyAndReassociate(obj);

		EntityEntry e = getEntry(object);
		if (e==null) {
			ClassPersister persister = getPersister(object);
			persister.setIdentifier(object, id);
			doUpdate(object, id, persister);
		}
		else {
			if ( !e.id.equals(id) ) throw new PersistentObjectException(
				"The instance passed to update() was already persistent: " +
				MessageHelper.infoString(e.persister, id)
			);
		}
	}

	private void doUpdateMutable(Object object, Serializable id, ClassPersister persister) throws HibernateException {

		if ( log.isTraceEnabled() ) log.trace( "updating " + MessageHelper.infoString(persister, id) );

		Key key = new Key(id, persister);
		checkUniqueness(key, object);

		if ( persister.implementsLifecycle() ) {
			log.debug("calling onUpdate()");
			if ( ( (Lifecycle) object ).onUpdate(this) ) {  // do callback
				log.debug("update vetoed by onUpdate()");
				reassociate(object, id, persister);
				return;
			}
		}

		// this is a transient object with existing persistent state not loaded by the session

		new OnUpdateVisitor(this, id).process(object, persister);

		addEntity(key, object);
		addEntry(object, LOADED, null, id, persister.getVersion(object), LockMode.NONE, true, persister, false);

	}

	private void doUpdate(Object object, Serializable id, ClassPersister persister) throws HibernateException {

		if ( !persister.isMutable() ) {
			log.trace("immutable instance passed to doUpdate(), locking");
			reassociate(object, id, persister);
		}
		else {
			doUpdateMutable(object, id, persister);
		}

		cascading++;
		try {
			Cascades.cascade(this, persister, object, Cascades.ACTION_SAVE_UPDATE, Cascades.CASCADE_ON_UPDATE);  // do cascade
		}
		finally {
			cascading--;
		}

	}

	/**
	 * Used only by replicate
	 */
	private void doReplicate(Object object, Serializable id, Object version, ReplicationMode replicationMode, ClassPersister persister)
	throws HibernateException {

		if ( log.isTraceEnabled() ) log.trace( "replicating changes to " + MessageHelper.infoString(persister, id) );

		new OnReplicateVisitor(this, id).process(object, persister);
		Key key = new Key(id, persister);
		addEntity(key, object);
		addEntry(object, LOADED, null, id, version, LockMode.NONE, true, persister, true);

		cascading++;
		try {
			Cascades.cascade(this, persister, object, Cascades.ACTION_REPLICATE, Cascades.CASCADE_ON_UPDATE, replicationMode);  // do cascade
		}
		finally {
			cascading--;
		}

	}

	private static final Object[] NO_ARGS = ArrayHelper.EMPTY_STRING_ARRAY;
	private static final Type[] NO_TYPES = ArrayHelper.EMPTY_TYPE_ARRAY;

	/**
	 * Retrieve a list of persistent objects using a hibernate query
	 */
	public List find(String query) throws HibernateException {
		return find(query, NO_ARGS, NO_TYPES);
	}

	public List find(String query, Object value, Type type) throws HibernateException {
		return find( query, new Object[] { value }, new Type[] { type } );
	}

	public List find(String query, Object[] values, Type[] types) throws HibernateException {
		return find(query, new QueryParameters(types, values) );
	}

	public List find(String query, QueryParameters queryParameters) throws HibernateException {

		if ( log.isTraceEnabled() ) {
			log.trace( "find: " + query );
			queryParameters.traceParameters(factory);
		}

		queryParameters.validateParameters();

		QueryTranslator[] q = getQueries(query, false);

		List results = Collections.EMPTY_LIST;

		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called

		//execute the queries and return all result lists as a single list
		try {
			for ( int i=0; i<q.length; i++ ) {
				List currentResults;
				try {
					currentResults = q[i].list(this, queryParameters);
				}
				catch (SQLException sqle) {
					throw convert( sqle, "Could not execute query" );
				}
				currentResults.addAll(results);
				results = currentResults;
			}
		}
		finally {
			dontFlushFromFind--;
		}
		return results;
	}

	private QueryTranslator[] getQueries(String query, boolean scalar) throws HibernateException {

		// take the union of the query spaces (ie. the queried tables)
		QueryTranslator[] q = factory.getQuery(query, scalar);
		HashSet qs = new HashSet();
		for ( int i=0; i<q.length; i++ ) {
			qs.addAll( q[i].getQuerySpaces() );
		}

		autoFlushIfRequired(qs);

		return q;
	}

	public Iterator iterate(String query) throws HibernateException {
		return iterate(query, NO_ARGS, NO_TYPES);
	}

	public Iterator iterate(String query, Object value, Type type) throws HibernateException {
		return iterate( query, new Object[] { value }, new Type[] { type } );
	}

	public Iterator iterate(String query, Object[] values, Type[] types) throws HibernateException {
		return iterate( query, new QueryParameters(types, values) );
	}

	public Iterator iterate(String query, QueryParameters queryParameters) throws HibernateException {

		if ( log.isTraceEnabled() ) {
			log.trace( "iterate: " + query );
			queryParameters.traceParameters(factory);
		}

		QueryTranslator[] q = getQueries(query, true);

		if (q.length==0) return EmptyIterator.INSTANCE;

		Iterator result = null;
		Iterator[] results = null;
		boolean many = q.length>1;
		if (many) results = new Iterator[q.length];

		dontFlushFromFind++; //stops flush being called multiple times if this method is recursively called

		try {

			//execute the queries and return all results as a single iterator
			for ( int i=0; i<q.length; i++ ) {

				try {
					result = q[i].iterate(queryParameters, this);
				}
				catch (SQLException sqle) {
					throw convert( sqle, "Could not execute query" );
				}
				if ( many ) {
					results[i] = result;
				}

			}

			return many ? new JoinedIterator(results) : result;

		}
		finally {
			dontFlushFromFind--;
		}
	}

	public ScrollableResults scroll(String query, QueryParameters queryParameters) throws HibernateException {

		if ( log.isTraceEnabled() ) {
			log.trace( "scroll: " + query );
			queryParameters.traceParameters(factory);
		}
// This true/false bit is the "shallow" stuff that controls whether
// entity's are loaded, or just their ids.
//		QueryTranslator[] q = factory.getQuery(query, true);
		QueryTranslator[] q = factory.getQuery(query, false);
		if (q.length!=1) throw new QueryException("implicit polymorphism not supported for scroll() queries");
		autoFlushIfRequired( q[0].getQuerySpaces() );

		dontFlushFromFind++; //stops flush being called multiple times if this method is recursively called
		try {
			return q[0].scroll(queryParameters, this);
		}
		catch (SQLException sqle) {
			throw convert( sqle, "Could not execute query" );
		}
		finally {
			dontFlushFromFind--;
		}
	}

	public int delete(String query) throws HibernateException {
		return delete(query, NO_ARGS, NO_TYPES);
	}

	public int delete(String query, Object value, Type type) throws HibernateException {
		return delete( query, new Object[] { value }, new Type[] { type } );
	}

	public int delete(String query, Object[] values, Type[] types) throws HibernateException {

		if ( log.isTraceEnabled() ) {
			log.trace( "delete: " + query );
			if (values.length!=0)  log.trace( "parameters: " + StringHelper.toString(values) );
		}

		List list = find(query, values, types);
		int size = list.size();
		for ( int i=0; i<size; i++ ) delete( list.get(i) );
		return size;
	}

	private void checkUniqueness(Key key, Object object) throws HibernateException {
		Object entity = getEntity(key);
		if (entity==object) throw new AssertionFailure("object already associated in doSave()");
		if (entity!=null) throw new NonUniqueObjectException( key.getIdentifier(), key.getMappedClass() );
	}

	private EntityEntry reassociate(Object object, Serializable id, ClassPersister persister) throws HibernateException {
		if ( log.isTraceEnabled() ) log.trace( "reassociating transient instance: " + MessageHelper.infoString(persister, id) );
		Key key = new Key(id, persister);
		checkUniqueness(key, object);
		addEntity(key, object);
		Object[] values = persister.getPropertyValues(object);
		TypeFactory.deepCopy(values, persister.getPropertyTypes(), persister.getPropertyUpdateability(), values);
		Object version = Versioning.getVersion(values, persister);
		EntityEntry newEntry = addEntry(object, LOADED, values, id, version, LockMode.NONE, true, persister, false);
		new OnLockVisitor(this, id).process(object, persister);
		return newEntry;
	}

	public void lock(Object object, LockMode lockMode) throws HibernateException {

		if (object==null) throw new NullPointerException("attempted to lock null");

		if (lockMode==LockMode.WRITE) throw new HibernateException("Invalid lock mode for lock()");

		object = unproxyAndReassociate(object);
		//TODO: if object was an uninitialized proxy, this is inefficient,
		//      resulting in two SQL selects

		EntityEntry entry = getEntry(object);
		if (entry==null) {
			final ClassPersister persister = getPersister(object);
			final Serializable id = persister.getIdentifier(object);
			if ( !isSaved(object) ) throw new TransientObjectException("cannot lock an unsaved transient instance: " + MessageHelper.infoString(persister) );

			entry = reassociate(object, id, persister);

			cascading++;
			try {
				Cascades.cascade(this, persister, object, Cascades.ACTION_LOCK, Cascades.CASCADE_ON_LOCK, lockMode);
			}
			finally {
				cascading--;
			}
		}

		upgradeLock(object, entry, lockMode);

	}

	private void upgradeLock(Object object, EntityEntry entry, LockMode lockMode) throws HibernateException {
		if ( lockMode.greaterThan(entry.lockMode) ) {

			if (entry.status!=LOADED) {
				throw new ObjectDeletedException( "attempted to lock a deleted instance", entry.id, object.getClass() );
			}

			final ClassPersister persister = entry.persister;

			if ( log.isTraceEnabled() ) log.trace( "locking " + MessageHelper.infoString(persister, entry.id) + " in mode: " + lockMode );

			SoftLock lock=null;
			if ( persister.hasCache() ) lock = persister.getCache().lock(entry.id, entry.version);
			try {
				persister.lock(entry.id, entry.version, object, lockMode, this);
				entry.lockMode = lockMode;
			}
			finally {
				// the database now holds a lock + the object is flushed from the cache,
				// so release the soft lock
				if ( persister.hasCache() ) persister.getCache().release(entry.id, lock);
			}

		}
	}

	public Query createFilter(Object collection, String queryString) {
		return new FilterImpl(queryString, collection, this);
	}

	public Query createQuery(String queryString) {
		return new QueryImpl(queryString, this);
	}

	public Query getNamedQuery(String queryName) throws MappingException {

		String queryString = factory.getNamedQuery(queryName);

		if(queryString!=null) return createQuery(queryString);

		SessionFactoryImpl.InternalNamedSQLQuery nq = factory.getNamedSQLQuery(queryName);
		if (nq==null) throw new MappingException("Named query not known: " + queryName);
		return createSQLQuery(
			nq.getQueryString(),
			nq.getReturnAliases(),
			nq.getReturnClasses(),
			nq.getQuerySpaces()
		);

	}

	public Object instantiate(Class clazz, Serializable id) throws HibernateException {
		return instantiate( factory.getPersister(clazz), id );
	}

	/**
	 * give the interceptor an opportunity to override the default instantiation
	 */
	public Object instantiate(ClassPersister persister, Serializable id) throws HibernateException {
		Object result = interceptor.instantiate( persister.getMappedClass(), id );
		if (result==null) result = persister.instantiate(id);
		return result;
	}

	public void setFlushMode(FlushMode flushMode) {
		this.flushMode = flushMode;
	}
	public FlushMode getFlushMode() {
		return flushMode;
	}

	/**
	 * detect in-memory changes, determine if the changes are to tables
	 * named in the query and, if so, complete execution the flush
	 */
	private boolean autoFlushIfRequired(Set querySpaces) throws HibernateException {

		if ( flushMode==FlushMode.AUTO && dontFlushFromFind==0 ) {

			int oldSize = collectionRemovals.size();

			flushEverything();

			if ( areTablesToBeUpdated(querySpaces) ) {

				log.trace("Need to execute flush");

				execute();
				postFlush();
				// note: execute() clears all collectionXxxxtion collections
				return true;
			}
			else {

				log.trace("Dont need to execute flush");

				// sort of don't like this: we re-use the same collections each flush
				// even though their state is not kept between flushes. However, its
				// nice for performance since the collection sizes will be "nearly"
				// what we need them to be next time.
				collectionCreations.clear();
				collectionUpdates.clear();
				updates.clear();
				// collection deletions are a special case since update() can add
				// deletions of collections not loaded by the session.
				for ( int i=collectionRemovals.size()-1; i>=oldSize; i-- ) {
					collectionRemovals.remove(i);
				}
			}

		}

		return false;

	}

	/**
	 * If the existing proxy is insufficiently "narrow" (derived), instantiate a new proxy
	 * and overwrite the registration of the old one. This breaks == and occurs only for
	 * "class" proxies rather than "interface" proxies.
	 */
	public Object narrowProxy(Object proxy, ClassPersister persister, Key key, Object object) throws HibernateException {

		if ( !persister.getConcreteProxyClass().isAssignableFrom( proxy.getClass() ) ) {

			if ( log.isWarnEnabled() ) log.warn(
				"Narrowing proxy to " + persister.getConcreteProxyClass() + " - this operation breaks =="
			);

			if (object!=null) {
				proxiesByKey.remove(key);
				return object; //return the proxied object
			}
			else {
				proxy = persister.createProxy( key.getIdentifier(), this );
				proxiesByKey.put(key, proxy); //overwrite old proxy
				return proxy;
			}

		}
		else {
			return proxy;
		}
	}

	/**
	 * Grab the existing proxy for an instance, if
	 * one exists. (otherwise return the instance)
	 */
	public Object proxyFor(ClassPersister persister, Key key, Object impl) throws HibernateException {
		if ( !persister.hasProxy() ) return impl;
		Object proxy = proxiesByKey.get(key);
		if (proxy!=null) {
			return narrowProxy(proxy, persister, key, impl);
		}
		else {
			return impl;
		}
	}

	public Object proxyFor(Object impl) throws HibernateException {
		EntityEntry e = getEntry(impl);
		//can't use e.persister since it is null after addUninitializedEntity (when this method is called)
		ClassPersister p = getPersister(impl);
		return proxyFor( p, new Key(e.id, p), impl);
	}

	/**
	 * Create a "temporary" entry for a newly instantiated entity. The entity is uninitialized,
	 * but we need the mapping from id to instance in order to guarantee uniqueness.
	 */
	public void addUninitializedEntity(Key key, Object object, LockMode lockMode) {
		addEntity(key, object);
		addEntry( object, LOADING, null, key.getIdentifier(), null, lockMode, true, null /*getPersister(object)*/, false ); //temporary
	}

	/**
	 * Add the "hydrated state" (an array) of an uninitialized entity to the session. We don't try
	 * to resolve any associations yet, because there might be other entities waiting to be
	 * read from the JDBC result set we are currently processing
	 */
	public void postHydrate(ClassPersister persister, Serializable id, Object[] values, Object object, LockMode lockMode) throws HibernateException {
		//persister.setIdentifier(object, id);
		Object version = Versioning.getVersion(values, persister);
		addEntry(object, LOADING, values, id, version, lockMode, true, persister, false);

		if ( log.isTraceEnabled() && version!=null ) log.trace("Version: " + version);
	}

	public void load(Object object, Serializable id) throws HibernateException {
		if (id==null) throw new NullPointerException("null is not a valid identifier");
		doLoadByObject(object, id, LockMode.NONE);
	}

	public Object load(Class clazz, Serializable id) throws HibernateException {
		if (id==null) throw new NullPointerException("null is not a valid identifier");
		Object result = doLoadByClass(clazz, id, true, true);
		ObjectNotFoundException.throwIfNull(result, id, clazz);
		return result;
	}

	public Object get(Class clazz, Serializable id) throws HibernateException {
		if (id==null) throw new NullPointerException("null is not a valid identifier");
		return doLoadByClass(clazz, id, true, false);
	}

	/**
	 * Load the data for the object with the specified id into a newly created object.
	 * Do NOT return a proxy.
	 */
	public Object immediateLoad(Class clazz, Serializable id) throws HibernateException {
		Object result = doLoad(clazz, id, null, LockMode.NONE, false);
		ObjectNotFoundException.throwIfNull(result, id, clazz); //should it be UnresolvableObject?
		return result;
	}

	/**
	 * Return the object with the specified id or null if no row with that id exists. Do not defer the load
	 * or return a new proxy (but do return an existing proxy). Do not check if the object was deleted.
	 */
	public Object internalLoadOneToOne(Class clazz, Serializable id) throws HibernateException {
		return doLoadByClass(clazz, id, false, false);
	}

	/**
	 * Return the object with the specified id or throw exception if no row with that id exists. Defer the load,
	 * return a new proxy or return an existing proxy if possible. Do not check if the object was deleted.
	 */
	public Object internalLoad(Class clazz, Serializable id) throws HibernateException {
		Object result = doLoadByClass(clazz, id, false, true);
		UnresolvableObjectException.throwIfNull(result, id, clazz);
		return result;
	}

	/**
	 * Load the data for the object with the specified id into the supplied
	 * instance. A new key will be assigned to the object. If there is an
	 * existing uninitialized proxy, this will break identity equals as far
	 * as the application is concerned.
	 */
	private void doLoadByObject(Object object, Serializable id, LockMode lockMode) throws HibernateException {

		Class clazz = object.getClass();
		if ( getEntry(object)!=null ) throw new PersistentObjectException(
			"attempted to load into an instance that was already associated with the Session: " +
			MessageHelper.infoString(clazz, id)
		);
		Object result = doLoad(clazz, id, object, lockMode, true);
		ObjectNotFoundException.throwIfNull(result, id, clazz);
		if (result!=object) throw new NonUniqueObjectException(id, clazz);

	}

	/**
	 * Load the data for the object with the specified id into a newly created
	 * object. A new key will be assigned to the object. If the class supports
	 * lazy initialization, return a proxy instead, leaving the real work for
	 * later. This should return an existing proxy where appropriate.
	 */
	private Object doLoadByClass(Class clazz, Serializable id, boolean checkDeleted, boolean allowProxyCreation)
	throws HibernateException {

		if ( log.isTraceEnabled() ) log.trace( "loading " + MessageHelper.infoString(clazz, id) );

		ClassPersister persister = getClassPersister(clazz);
		if ( !persister.hasProxy() ) {
			// this class has no proxies (so do a shortcut)
			return doLoad(clazz, id, null, LockMode.NONE, checkDeleted);
		}
		else {

			Key key = new Key(id, persister);
			Object proxy;
			if ( getEntity(key)!=null ) {
				// return existing object or initialized proxy (unless deleted)
				return proxyFor(
					persister,
					key,
					doLoad(clazz, id, null, LockMode.NONE, checkDeleted) // just to do the deleted check
				);
			}
			else if ( ( proxy = proxiesByKey.get(key) )!=null ) {
				// return existing uninitialized proxy
				return narrowProxy(proxy, persister, key, null);
			}
			else if (allowProxyCreation) {
				// return new uninitialized proxy
				proxy = persister.createProxy(id, this);
				if ( persister.isBatchLoadable() ) batchLoadableEntityKeys.put(key, MARKER);
				proxiesByKey.put(key, proxy);
				return proxy;
			}
			else {
				// return a newly loaded object
				return doLoad(clazz, id, null, LockMode.NONE, checkDeleted);
			}
		}

	}

	/**
	 * Load the data for the object with the specified id into a newly created object
	 * using "for update", if supported. A new key will be assigned to the object.
	 * This method always hits the db, and does not create proxies. It should return
	 * an existing proxy where appropriate.
	 */
	private Object doLoad(Class clazz, Serializable id, LockMode lockMode, boolean allowNull) throws HibernateException {

		if (id==null) throw new NullPointerException("null is not a valid identifier");
		if ( log.isTraceEnabled() ) log.trace( "loading " + MessageHelper.infoString(clazz, id)  + " in lock mode: " + lockMode );

		ClassPersister persister = getClassPersister(clazz);
		SoftLock lock = null;
		if ( persister.hasCache() ) lock = persister.getCache().lock(id, null); //increments the lock
		
		Object result;
		try {
			result = doLoad(clazz, id, null, lockMode, true);
		}
		finally {
			// the database now holds a lock + the object is flushed from the cache,
			// so release the soft lock
			if ( persister.hasCache() ) persister.getCache().release(id, lock);
		}

		if (!allowNull) ObjectNotFoundException.throwIfNull( result, id, persister.getMappedClass() );

		//return existing proxy (if one exists)
		return proxyFor(persister, new Key(id, persister), result);

	}

	public Object load(Class clazz, Serializable id, LockMode lockMode) throws HibernateException {
		if (lockMode==LockMode.WRITE) throw new HibernateException("Invalid lock mode for load()");
		if (lockMode==LockMode.NONE) return load(clazz, id); //we don't necessarily need to hit the db in this case
		return doLoad(clazz, id, lockMode, false);

	}

	public Object get(Class clazz, Serializable id, LockMode lockMode) throws HibernateException {
		if (lockMode==LockMode.WRITE) throw new HibernateException("Invalid lock mode for get()");
		if (lockMode==LockMode.NONE) return get(clazz, id); //we don't necessarily need to hit the db in this case
		return doLoad(clazz, id, lockMode, true);
	}

	/**
	 * Actually do all the hard work of loading up an object.
	 * 1. see if its already loaded
	 * 2. see if its cached
	 * 3. actually go to the database
	 */
	private Object doLoad(
		final Class clazz,
		final Serializable id,
		final Object optionalObject,
		final LockMode lockMode,
		final boolean checkDeleted
	) throws HibernateException {
		//DONT need to flush before a load by id, because ids are constant

		if ( log.isTraceEnabled() ) log.trace( "attempting to resolve " + MessageHelper.infoString(clazz, id) );

		final ClassPersister persister = getClassPersister(clazz);
		final Key key = new Key(id, persister);

		if (optionalObject!=null) persister.setIdentifier(optionalObject, id);

		// LOOK FOR LOADED OBJECT
		Object old = getEntity(key);
		if (old!=null) {  //if this object was already loaded
			EntityEntry oldEntry = getEntry(old);
			Status status = oldEntry.status;
			if ( checkDeleted && ( status==DELETED || status==GONE ) ) {
				throw new ObjectDeletedException("The object with that id was deleted", id, clazz);
			}
			upgradeLock(old, oldEntry, lockMode);
			if ( log.isTraceEnabled() ) log.trace( "resolved object in session cache " + MessageHelper.infoString(persister, id) );
			return old;
		}

		else {
			// check to see if we know already that it does not exist:
			if ( nonExists.contains(key) ) {
				log.trace("entity does not exist");
				return null;
			}

			// LOOK IN CACHE
			CacheEntry entry = persister.hasCache() && lockMode.lessThan(LockMode.READ) ?
				(CacheEntry) persister.getCache().get( id, getTimestamp() ) :
				null;
			if (entry!=null) {
				return assembleCacheEntry(entry, id, persister, optionalObject);
			}
			else {
				// GO TO DATABASE
				if ( log.isTraceEnabled() ) log.trace( "object not resolved in any cache " + MessageHelper.infoString(persister, id) );
				final Object result = persister.load(id, optionalObject, lockMode, this);
				if (result==null) addNonExist(key); //remember it doesn't exist, in case of next time
				return result;
			}

		}

	}

	private Object assembleCacheEntry(CacheEntry entry, Serializable id, ClassPersister persister, Object optionalObject)
	throws HibernateException {
		if ( log.isTraceEnabled() ) log.trace( "resolved object in second-level cache " + MessageHelper.infoString(persister, id) );
		ClassPersister subclassPersister = getClassPersister( entry.getSubclass() );
		Object result = optionalObject==null ? instantiate(subclassPersister, id) : optionalObject;
		addEntry(result, LOADING, null, id, null, LockMode.NONE, true, subclassPersister, false); //make it circular-reference safe
		addEntity( new Key(id, persister), result );
		Type[] types = subclassPersister.getPropertyTypes();
		Object[] values = entry.assemble(result, id, subclassPersister, interceptor, this); // intializes result by side-effect
		TypeFactory.deepCopy(values, types, subclassPersister.getPropertyUpdateability(), values);
		Object version = Versioning.getVersion(values, subclassPersister);
		if ( log.isTraceEnabled() ) log.trace("Cached Version: " + version);
		addEntry(result, LOADED, values, id, version, LockMode.NONE, true, subclassPersister, false);
		initializeNonLazyCollections();
		// upgrade the lock if necessary:
		//lock(result, lockMode);
		return result;
	}

	public void refresh(Object object) throws HibernateException {
		refresh(object, LockMode.READ);
	}

	public void refresh(Object obj, LockMode lockMode) throws HibernateException {

		if (obj==null) throw new NullPointerException("attempted to refresh null");

		if ( reassociateIfUninitializedProxy(obj) ) return;

		Object object = unproxyAndReassociate(obj);
		EntityEntry e = removeEntry(object);

		ClassPersister persister;
		Serializable id;
		if (e==null) {
			persister = getPersister(object);
			id = persister.getIdentifier(object);

			if ( log.isTraceEnabled() ) log.trace( "refreshing transient " + MessageHelper.infoString(persister, id) );
			if ( getEntry( new Key(id, persister) )!=null ) throw new PersistentObjectException(
				"attempted to refresh transient instance when persistent instance was already associated with the Session: " +
				MessageHelper.infoString(persister, id)
			);
		}
		else {
			if ( log.isTraceEnabled() ) log.trace( "refreshing " + MessageHelper.infoString(e.persister, e.id) );
			if ( !e.existsInDatabase ) throw new HibernateException("this instance does not yet exist as a row in the database");

			persister = e.persister;
			id = e.id;
			Key key = new Key(id, persister);
			removeEntity(key);
			if ( persister.hasCollections() ) new EvictVisitor(this).process(object, persister);
		}

		if ( persister.hasCache() ) persister.getCache().remove(id);
		evictCachedCollections(persister, id);
		Object result = persister.load(id, object, lockMode, this);
		UnresolvableObjectException.throwIfNull( result, id, persister.getMappedClass() );

	}

	/**
	 * After processing a JDBC result set, we "resolve" all the associations
	 * between the entities which were instantiated and had their state
	 * "hydrated" into an array
	 */
	public void initializeEntity(Object object) throws HibernateException {

		EntityEntry e = getEntry(object);
		if (e==null) throw new AssertionFailure("possible non-threadsafe access to the session");
		ClassPersister persister = e.persister;
		Serializable id = e.id;
		Object[] hydratedState = e.loadedState;
		Type[] types = persister.getPropertyTypes();

		if ( log.isDebugEnabled() ) log.debug( "resolving associations for " + MessageHelper.infoString(persister, id) );

		for ( int i=0; i<hydratedState.length; i++ ) {
			hydratedState[i] = types[i].resolveIdentifier( hydratedState[i], this, object );
		}

		interceptor.onLoad( object, id, hydratedState, persister.getPropertyNames(), types ); //after resolving identifiers!

		persister.setPropertyValues(object, hydratedState);

		if ( persister.hasCache() ) {
			log.debug( "adding entity to second-level cache " + MessageHelper.infoString(persister, id) );
			persister.getCache().put( 
					id, 
					new CacheEntry(object, persister, this), 
					getTimestamp(), 
					Versioning.getVersion(hydratedState, persister), 
					persister.isVersioned() ? 
							persister.getVersionType().getComparator() : null
			);
		}

		if ( persister.implementsLifecycle() ) {
			log.debug("calling onLoad()");
			( (Lifecycle) object ).onLoad(this, id);
		}

		TypeFactory.deepCopy(hydratedState, persister.getPropertyTypes(), persister.getPropertyUpdateability(), hydratedState); //after setting values to object

		e.status = LOADED;

		if ( log.isDebugEnabled() ) log.debug( "done materializing entity " + MessageHelper.infoString(persister, id) );

	}

	public Transaction beginTransaction() throws HibernateException {
		Transaction tx = factory.getTransactionFactory().beginTransaction(this);
		isCurrentTransaction = true;
		return tx;
	}

	public void flush() throws HibernateException {
		if (cascading>0) throw new HibernateException("Flush during cascade is dangerous");

		flushEverything();
		execute();
		postFlush();
	}

	private void flushEverything() throws HibernateException {

		log.trace("flushing session");

		interceptor.preFlush( entitiesByKey.values().iterator() );

		preFlushEntities();
		// we could move this inside if we wanted to
		// tolerate collection initializations during
		// collection dirty checking:
		preFlushCollections();
		// now, any collections that are initialized
		// inside this block do not get updated - they
		// are ignored until the next flush
		flushing = true;
		try {
			flushEntities();
			flushCollections();
		}
		finally {
			flushing = false;
		}

		//some statistics

		if ( log.isDebugEnabled() ) {
			log.debug( "Flushed: " +
			insertions.size() + " insertions, " +
			updates.size() + " updates, " +
			deletions.size() + " deletions to " +
			entityEntries.size() + " objects");
			log.debug( "Flushed: " +
			collectionCreations.size() + " (re)creations, " +
			collectionUpdates.size() + " updates, " +
			collectionRemovals.size() + " removals to " +
			collectionEntries.size() + " collections");

			new Printer(factory).toString( entitiesByKey.values().iterator() );
		}
	}

	private boolean areTablesToBeUpdated(Set tables) {
		return areTablesToUpdated(updates, tables) ||
		areTablesToUpdated(insertions, tables) ||
		areTablesToUpdated(deletions, tables) ||
		areTablesToUpdated(collectionUpdates, tables) ||
		areTablesToUpdated(collectionCreations, tables) ||
		areTablesToUpdated(collectionRemovals, tables);
	}

	private static boolean areTablesToUpdated(List executables, Set set) {
		int size = executables.size();
		for ( int j=0; j<size; j++ ) {
			Serializable[] spaces = ( (Executable) executables.get(j) ).getPropertySpaces();
			for ( int i=0; i<spaces.length; i++ ) {
				if ( set.contains( spaces[i] ) ) {
					if ( log.isDebugEnabled() ) log.debug( "changes must be flushed to space: " + spaces[i] );
					return true;
				}
			}
		}
		return false;
	}

	private void executeInserts() throws HibernateException {
		log.trace("executing insertions");
		executeAll(insertions);
	}

	public boolean isDirty() throws HibernateException {
		log.debug("checking session dirtiness");
		if ( insertions.size() > 0 || deletions.size() > 0 ) {
			log.debug("session dirty (scheduled updates and insertions)");
			return true;
		}
		else {
			int oldSize = collectionRemovals.size();
			try {
				flushEverything();
				boolean result = updates.size() > 0 ||
					insertions.size() > 0 ||
					deletions.size() > 0 ||
					collectionUpdates.size() > 0 ||
					collectionRemovals.size() > 0 ||
					collectionCreations.size() > 0;
				log.debug( result ? "session dirty" : "session not dirty" );
				return result;
			}
			finally {
				collectionCreations.clear();
				collectionUpdates.clear();
				updates.clear();
				// collection deletions are a special case since update() can add
				// deletions of collections not loaded by the session.
				for ( int i=collectionRemovals.size()-1; i>=oldSize; i-- ) {
					collectionRemovals.remove(i);
				}
			}
		}
	}

	/**
	 * Execute all SQL and second-level cache updates, in a
	 * special order so that foreign-key constraints cannot
	 * be violated:
	 * <ol>
	 * <li> Inserts, in the order they were performed
	 * <li> Updates
	 * <li> Deletion of collection elements
	 * <li> Insertion of collection elements
	 * <li> Deletes, in the order they were performed
	 * </ol>
	 */
	private void execute() throws HibernateException {

		log.trace("executing flush");

		try {
			// we need to lock the collection caches before
			// executing entity inserts/updates in order to
			// account for bidi associations
			beforeExecutionsAll(collectionRemovals);
			beforeExecutionsAll(collectionUpdates);
			beforeExecutionsAll(collectionCreations);

			// now actually execute SQL and update the
			// second-level cache
			executeAll(insertions);
			executeAll(updates);
			executeAll(collectionRemovals);
			executeAll(collectionUpdates);
			executeAll(collectionCreations);
			executeAll(deletions);
		}
		catch (HibernateException he) {
			log.error("Could not synchronize database state with session");
			throw he;
		}
	}

	public void postInsert(Object obj) {
		EntityEntry entry = getEntry(obj);
		if (entry==null) throw new AssertionFailure("possible nonthreadsafe access to session");
		entry.existsInDatabase = true;
	}

	public void postDelete(Object obj) {
		EntityEntry entry = removeEntry(obj);
		if (entry==null) throw new AssertionFailure("possible nonthreadsafe access to session");
		entry.status = GONE;
		entry.existsInDatabase = false;
		Key key = new Key(entry.id, entry.persister);
		removeEntity(key);
		proxiesByKey.remove(key);
	}

	public void postUpdate(Object obj, Object[] updatedState, Object nextVersion) throws HibernateException {
		EntityEntry entry = getEntry(obj);
		if (entry==null) throw new AssertionFailure("possible nonthreadsafe access to session");
		entry.loadedState = updatedState;
		entry.lockMode = LockMode.WRITE;
		if ( entry.persister.isVersioned() ) {
			entry.version = nextVersion;
			entry.persister.setPropertyValue( obj, entry.persister.getVersionProperty(), nextVersion );
		}
	}

	private void executeAll(List list) throws HibernateException {
		int size = list.size();
		for ( int i=0; i<size; i++ ) {
			execute( (Executable) list.get(i) );
		}
		list.clear();
		if ( batcher!=null ) batcher.executeBatch();
	}
	
	private void execute(Executable executable) throws HibernateException {
		final boolean lockQueryCache = factory.isQueryCacheEnabled();
		if ( executable.hasAfterTransactionCompletion() || lockQueryCache ) {
			executions.add(executable);
		}
		if (lockQueryCache) {
			factory.getUpdateTimestampsCache().preinvalidate( executable.getPropertySpaces() );
		}
		executable.execute();
	}

	private void beforeExecutionsAll(List list) throws HibernateException {
		int size = list.size();
		for ( int i=0; i<size; i++ ) {
			Executable executable = (Executable) list.get(i);
			executable.beforeExecutions();
		}
	}

	/**
	 * 1. detect any dirty entities
	 * 2. schedule any entity updates
	 * 3. search out any reachable collections
	 */
	private void flushEntities() throws HibernateException {

		log.trace("Flushing entities and processing referenced collections");

		// Among other things, updateReachables() will recursively load all
		// collections that are moving roles. This might cause entities to
		// be loaded.

		// So this needs to be safe from concurrent modification problems.
		// It is safe because of how IdentityMap implements entrySet()

		List list = IdentityMap.concurrentEntries(entityEntries);
		int size = list.size();
		for ( int i=0; i<size; i++ ) {

			// Update the status of the object and if necessary, schedule an update

			Map.Entry me = (Map.Entry) list.get(i);
			EntityEntry entry = (EntityEntry) me.getValue();
			Status status = entry.status;

			if (status!=LOADING && status!=GONE) flushEntity( me.getKey(), entry );
		}
	}

	private void flushEntity(Object object, EntityEntry entry) throws HibernateException {
		ClassPersister persister = entry.persister;
		Status status = entry.status;
		checkId(object, persister, entry.id);

		Object[] values;
		if (status==DELETED) {
			//grab its state saved at deletion
			values = entry.deletedState;
		}
		else {
			// grab its current state
			values = persister.getPropertyValues(object);
		}
		Type[] types = persister.getPropertyTypes();

		boolean substitute = false;

		if ( persister.hasCollections() ) {

			// wrap up any new collections directly referenced by the object
			// or its components

			// NOTE: we need to do the wrap here even if its not "dirty",
			// because collections need wrapping but changes to _them_
			// don't dirty the container. Also, for versioned data, we
			// need to wrap before calling searchForDirtyCollections

			WrapVisitor visitor = new WrapVisitor(this);
			// substitutes into values by side-effect
			visitor.processValues(values, types);
			substitute = visitor.isSubstitutionRequired();
		}

		boolean cannotDirtyCheck;
		final boolean interceptorHandledDirtyCheck;
		boolean dirtyCheckDoneBySelect = false;
		Object[] currentPersistentState = null;

		int[] dirtyProperties = interceptor.findDirty(object, entry.id, values, entry.loadedState, persister.getPropertyNames(), types);

		if (dirtyProperties==null) {
			// Interceptor returned null, so do the dirtycheck ourself, if possible
			interceptorHandledDirtyCheck = false;
			cannotDirtyCheck = (entry.loadedState==null); // object loaded by update()
			if (!cannotDirtyCheck) {
				dirtyProperties = persister.findDirty(values, entry.loadedState, object, this);
			}
			else {
				currentPersistentState = persister.getCurrentPersistentState(entry.id, entry.version, this);
				if (currentPersistentState!=null) {
					dirtyProperties = persister.findModified(currentPersistentState, values, object, this);
					cannotDirtyCheck = false;
					dirtyCheckDoneBySelect = true;
				}
			}
		}
		else {
			// the Interceptor handled the dirty checking
			cannotDirtyCheck = false;
			interceptorHandledDirtyCheck = true;
		}

		// compare to cached state (ignoring collections unless versioned)
		if ( isUpdateNecessary(persister, cannotDirtyCheck, status, dirtyProperties, values, types) ) {

			if ( log.isTraceEnabled() ) {
				if (status==DELETED) {
					log.trace("Updating deleted entity: " + MessageHelper.infoString(persister, entry.id) );
				}
				else {
					log.trace("Updating entity: " + MessageHelper.infoString(persister, entry.id) );
				}
			}

			if ( !entry.isBeingReplicated ) {
				//give the Interceptor a chance to modify property values
				final boolean intercepted = interceptor.onFlushDirty(
					object, entry.id, values, entry.loadedState, persister.getPropertyNames(), types
				);
				//now we might need to recalculate the dirtyProperties array
				if (intercepted && !cannotDirtyCheck && !interceptorHandledDirtyCheck) {
					if (dirtyCheckDoneBySelect) {
						dirtyProperties = persister.findModified(currentPersistentState, values, object, this);
					}
					else {
						dirtyProperties = persister.findDirty(values, entry.loadedState, object, this);
					}
				}
				//if the properties were modified by the Interceptor, we need to set them back to the object
				substitute = substitute || intercepted;
			}

			// validate() instances of Validatable
			if ( status==LOADED && persister.implementsValidatable() ) ( (Validatable) object ).validate();

			// increment the version number (if necessary)
			final Object nextVersion = getNextVersion(persister, values, entry);

			// get the updated snapshot by cloning current state
			Object[] updatedState = null;
			if (status==LOADED) {
				updatedState = new Object[values.length];
				TypeFactory.deepCopy(values, types, persister.getPropertyUpdateability(), updatedState);
			}

			// if it was dirtied by a collection only
			if ( !cannotDirtyCheck && dirtyProperties==null ) dirtyProperties = ArrayHelper.EMPTY_INT_ARRAY;

			checkNullability(values, persister, true);

			// schedule the update
			updates.add(
				//new Key(entry.id, persister),
				new ScheduledUpdate(entry.id, values, dirtyProperties, entry.loadedState, entry.version, nextVersion, object, updatedState, persister, this)
				//note that we intentionally did _not_ pass in currentPersistentState!
			);

		}

		if (status==DELETED)  {
			//entry.status = GONE;
		}
		else {

			// now update the object .. has to be outside the main if block above (because of collections)
			if (substitute) persister.setPropertyValues(object, values);

			// Search for collections by reachability, updating their role.
			// We don't want to touch collections reachable from a deleted object
			if ( persister.hasCollections() ) new FlushVisitor(this, object).processValues(values, types);
		}

	}

	private boolean isUpdateNecessary(
		final ClassPersister persister,
		final boolean cannotDirtyCheck,
		final Status status,
		final int[] dirtyProperties,
		final Object[] values,
		final Type[] types
	) throws HibernateException {

		if ( !persister.isMutable() ) return false;
		if ( cannotDirtyCheck ) return true;
		if ( dirtyProperties!=null && dirtyProperties.length!=0 ) return true;
		if (status==LOADED && persister.isVersioned() && persister.hasCollections() ) {
			DirtyCollectionSearchVisitor visitor = new DirtyCollectionSearchVisitor(this);
			visitor.processValues(values, types);
			return visitor.wasDirtyCollectionFound();
		}
		else {
			return false;
		}
	}

	private Object getNextVersion(ClassPersister persister, Object[] values, EntityEntry entry) throws HibernateException {
		if ( persister.isVersioned() ) {
			if (entry.isBeingReplicated) {
				return Versioning.getVersion(values, persister);
			}
			else {
				Object nextVersion = entry.status==DELETED ?
					entry.version :
					Versioning.increment( entry.version, persister.getVersionType() );
				Versioning.setVersion(values, nextVersion, persister);
				return nextVersion;
			}
		}
		else {
			return null;
		}
	}

	private static void checkId(Object object, ClassPersister persister, Serializable id) throws HibernateException {
		// make sure user didn't mangle the id
		if ( persister.hasIdentifierPropertyOrEmbeddedCompositeIdentifier() ) {
			Serializable oid = persister.getIdentifier(object);
			if (id==null) throw new AssertionFailure("null id in entry (don't flush the Session after an exception occurs)");
			if ( !id.equals(oid) ) throw new HibernateException(
				"identifier of an instance of " +
				persister.getClassName() +
				" altered from " +
				id +
				" to " + oid
			);
		}

	}

	/**
	 * process cascade save/update at the start of a flush to discover
	 * any newly referenced entity that must be passed to saveOrUpdate(),
	 * and also apply orphan delete
	 */
	private void preFlushEntities() throws HibernateException {

		List list = IdentityMap.concurrentEntries(entityEntries);
		//safe from concurrent modification because of how entrySet() is implemented on IdentityMap
		int size = list.size();
		for ( int i=0; i<size; i++ ) {

			Map.Entry me = (Map.Entry) list.get(i);
			EntityEntry entry = (EntityEntry) me.getValue();
			Status status = entry.status;

			if ( status!=LOADING && status!=GONE && status!=DELETED ) {
				Object object = me.getKey();
				cascading++;
				try {
					Cascades.cascade(this, entry.persister, object, Cascades.ACTION_SAVE_UPDATE, Cascades.CASCADE_ON_UPDATE);
				}
				finally {
					cascading--;
				}
			}
		}

	}

	// This just does a table lookup, but caches the last result

	private transient Class lastClass;
	private transient ClassPersister lastResultForClass;

	ClassPersister getClassPersister(Class theClass) throws MappingException {
		if (lastClass!=theClass) {
			lastResultForClass = factory.getPersister(theClass);
			lastClass = theClass;
		}
		return lastResultForClass;
	}

	public ClassPersister getPersister(Object object) throws MappingException {
		return getClassPersister( object.getClass() );
	}

	// not for internal use:
	public Serializable getIdentifier(Object object) throws HibernateException {
		if (object instanceof HibernateProxy) {
			LazyInitializer li = HibernateProxyHelper.getLazyInitializer( (HibernateProxy) object );
			if ( li.getSession()!=this ) throw new TransientObjectException("The proxy was not associated with this session");
			return li.getIdentifier();
		}
		else {
			EntityEntry entry = getEntry(object);
			if (entry==null) throw new TransientObjectException("The instance was not associated with this session");
			return entry.id;
		}
	}

	/**
	 * Get the id value for an object that is actually associated with the session. This
	 * is a bit stricter than getEntityIdentifierIfNotUnsaved().
	 */
	public Serializable getEntityIdentifier(Object object) {
		if (object instanceof HibernateProxy) {
			return HibernateProxyHelper.getLazyInitializer( (HibernateProxy) object ).getIdentifier();
		}
		else {
			EntityEntry entry = getEntry(object);
			return (entry!=null) ? entry.id : null;
		}
	}

	public boolean isSaved(Object object) throws HibernateException {
		if (object instanceof HibernateProxy) return true;
		EntityEntry entry = getEntry(object);
		if (entry!=null) return true;
		Boolean isUnsaved = interceptor.isUnsaved(object);
		if (isUnsaved!=null) return !isUnsaved.booleanValue();
		return !getPersister(object).isUnsaved(object);
	}

	/**
	 * Used by OneToOneType and ManyToOneType to determine what id value should be used for an
	 * object that may or may not be associated with the session. This does a "best guess" using
	 * any/all info available to use (not just the EntityEntry).
	 */
	public Serializable getEntityIdentifierIfNotUnsaved(Object object) throws HibernateException {
		if (object == null) {
			return null;
		}
		else {
			if (object instanceof HibernateProxy) {
				return HibernateProxyHelper.getLazyInitializer( (HibernateProxy) object ).getIdentifier();
			}
			else {

				EntityEntry entry = getEntry(object);
				if (entry!=null) {
					return entry.id;
				}
				else {
					Boolean isUnsaved = interceptor.isUnsaved(object);
					if ( isUnsaved!=null && isUnsaved.booleanValue() ) throwTransientObjectException(object);
					ClassPersister persister = getPersister(object);
					if ( persister.isUnsaved(object) ) throwTransientObjectException(object);
					return persister.getIdentifier(object);
				}
			}
		}
	}

	private static void throwTransientObjectException(Object object) throws TransientObjectException {
		throw new TransientObjectException(
			"object references an unsaved transient instance - save the transient instance before flushing: " +
			object.getClass().getName()
		);
	}

	/**
	 * process any unreferenced collections and then inspect all known collections,
	 * scheduling creates/removes/updates
	 */
	private void flushCollections() throws HibernateException {

		log.trace("Processing unreferenced collections");

		List list = IdentityMap.entries(collectionEntries);
		int size = list.size();
		for ( int i=0; i<size; i++ ) {
			Map.Entry me = (Map.Entry) list.get(i);
			CollectionEntry ce = (CollectionEntry) me.getValue();
			if ( !ce.reached && !ce.ignore ) {
				updateUnreachableCollection( (PersistentCollection) me.getKey() );
			}
		}

		// Schedule updates to collections:

		log.trace("Scheduling collection removes/(re)creates/updates");

		list = IdentityMap.entries(collectionEntries);
		size = list.size();
		for ( int i=0; i<size; i++ ) {
			Map.Entry me = (Map.Entry) list.get(i);
			PersistentCollection coll = (PersistentCollection) me.getKey();
			CollectionEntry ce = (CollectionEntry) me.getValue();

			if ( ce.dorecreate ) collectionCreations.add(
				new ScheduledCollectionRecreate(coll, ce.currentPersister, ce.currentKey, this)
			);
			if ( ce.doremove ) collectionRemovals.add(
				new ScheduledCollectionRemove(ce.loadedPersister, ce.loadedKey, ce.snapshotIsEmpty(), this)
			);
			if ( ce.doupdate ) collectionUpdates.add(
				new ScheduledCollectionUpdate(coll, ce.loadedPersister, ce.loadedKey, ce.snapshotIsEmpty(), this)
			);

		}

	}

	/**
	 * 1. Recreate the collection key -> collection map
	 * 2. rebuild the collection entries
	 * 3. call Interceptor.postFlush()
	 */
	private void postFlush() throws HibernateException {

		log.trace("post flush");

		collectionsByKey.clear();

		Iterator iter = collectionEntries.entrySet().iterator();
		while ( iter.hasNext() ) {
			Map.Entry me = (Map.Entry) iter.next();
			CollectionEntry ce = (CollectionEntry) me.getValue();
			PersistentCollection pc = (PersistentCollection) me.getKey();
			if ( ce.postFlush(pc) ) {
				iter.remove();
			}
			else if ( ce.reached ) {
				collectionsByKey.put( new CollectionKey( ce.currentPersister.getRole(), ce.currentKey ), pc );
			}
		}

		interceptor.postFlush( entitiesByKey.values().iterator() );

	}

	/**
	 * Initialize the flags of the CollectionEntry, including the
	 * dirty check.
	 */
	private void preFlushCollections() throws HibernateException {

		// Initialize dirty flags for arrays + collections with composite elements
		// and reset reached, doupdate, etc.

		List list = IdentityMap.entries(collectionEntries);
		int size = list.size();
		for ( int i=0; i<size; i++ ) {
			Map.Entry e = (Map.Entry) list.get(i);
			( (CollectionEntry) e.getValue() ).preFlush( (PersistentCollection) e.getKey() );
		}
	}

	/**
	 * Initialize the role of the collection.
	 *
	 * The CollectionEntry.reached stuff is just to detect any silly users who set up
	 * circular or shared references between/to collections.
	 */
	void updateReachableCollection(PersistentCollection coll, Type type, Object owner) throws HibernateException {

		CollectionEntry ce = getCollectionEntry(coll);

		if (ce==null) {
			// refer to comment in addCollection()
			throw new HibernateException("Found two representations of same collection: " + coll.getCollectionSnapshot().getRole());
		}

		if (ce.reached) {
			// We've been here before
			throw new HibernateException("Found shared references to a collection: " + coll.getCollectionSnapshot().getRole());
		}
		ce.reached = true;

		CollectionPersister persister = getCollectionPersister( ( (PersistentCollectionType) type ).getRole() );
		ce.currentPersister = persister;
		ce.currentKey = getEntityIdentifier(owner); //TODO: better to pass the id in as an argument?

		if ( log.isDebugEnabled() ) {
			log.debug(
				"Collection found: " + MessageHelper.infoString(persister, ce.currentKey) +
				", was: " + MessageHelper.infoString(ce.loadedPersister, ce.loadedKey)
			);
		}

		prepareCollectionForUpdate(coll, ce);

	}

	/**
	 * record the fact that this collection was dereferenced
	 */
	private void updateUnreachableCollection(PersistentCollection coll) throws HibernateException {

		CollectionEntry entry = getCollectionEntry(coll);

		if ( log.isDebugEnabled() && entry.loadedPersister!=null ) log.debug(
			"Collection dereferenced: " + MessageHelper.infoString(entry.loadedPersister, entry.loadedKey)
		);

		// do a check
		if (
			entry.loadedPersister!=null &&
			entry.loadedPersister.hasOrphanDelete()
		) {
			Key key = new Key(
				entry.loadedKey,
				getClassPersister( entry.loadedPersister.getOwnerClass() )
			);
			Object owner = getEntity(key);
			if (owner==null) throw new AssertionFailure("owner not associated with session");
			EntityEntry e = getEntry(owner);
			//only collections belonging to deleted entities are allowed to be dereferenced in the case of orphan delete
			if ( e!=null && e.status!=DELETED && e.status!=GONE ) {
				throw new HibernateException("You may not dereference a collection with cascade=\"all-delete-orphan\"");
			}
		}

		// do the work
		entry.currentPersister=null;
		entry.currentKey=null;
		prepareCollectionForUpdate(coll, entry);

	}

	/**
	 * 1. record the collection role that this collection is referenced by
	 * 2. decide if the collection needs deleting/creating/updating (but
	 *    don't actually schedule the action yet)
	 */
	private void prepareCollectionForUpdate(PersistentCollection coll, CollectionEntry entry) throws HibernateException {

		if (entry.processed) throw new AssertionFailure("collection was processed twice by flush()");
		entry.processed = true;

		if ( entry.loadedPersister!=null || entry.currentPersister!=null ) {                    // it is or was referenced _somewhere_

			if (
				entry.loadedPersister!=entry.currentPersister ||                                // if either its role changed,
				!entry.currentPersister.getKeyType().equals(entry.loadedKey, entry.currentKey)  // or its key changed (for nested collections)
			) {

				// do a check
				if (
					entry.loadedPersister!=null &&
					entry.currentPersister!=null &&
					entry.loadedPersister.hasOrphanDelete()
				) {
					throw new HibernateException("Don't dereferemce a collection with cascade=\"all-delete-orphan\": " + coll.getCollectionSnapshot().getRole());
				}

				// do the work
				if (entry.currentPersister!=null) entry.dorecreate = true;                      // we will need to create new entries

				if ( entry.loadedPersister!=null ) {
					entry.doremove = true;                                                      // we will need to remove ye olde entries
					if (entry.dorecreate) {
						log.trace("Forcing collection initialization");
						coll.forceInitialization();                                                       // force initialize!
					}
				}

			}
			else if (entry.dirty) {                                                             // else if it's elements changed
				entry.doupdate = true;
			}

		}

	}

	/**
	 * ONLY near the end of the flush process, determine if the collection is dirty
	 * by checking its entry
	 */
	boolean collectionIsDirty(PersistentCollection coll) throws HibernateException {
		CollectionEntry entry = getCollectionEntry(coll);
		return entry.initialized && entry.dirty; //( entry.dirty || coll.hasQueuedAdds() );
	}


	private static final class LoadingCollectionEntry {
		final PersistentCollection collection;
		final Serializable id;
		final Object resultSetId;
		LoadingCollectionEntry(PersistentCollection collection, Serializable id, Object resultSetId) {
			this.collection = collection;
			this.id = id;
			this.resultSetId = resultSetId;
		}
	}
	private LoadingCollectionEntry getLoadingCollectionEntry(CollectionKey collectionKey) {
		return (LoadingCollectionEntry) loadingCollections.get(collectionKey);
	}
	private void addLoadingCollectionEntry(CollectionKey collectionKey, PersistentCollection collection, Serializable id, Object resultSetId) {
		loadingCollections.put( collectionKey, new LoadingCollectionEntry(collection, id, resultSetId) );
	}

	public PersistentCollection getLoadingCollection(CollectionPersister persister, Serializable id, Object resultSetId) throws HibernateException {

		CollectionKey ckey = new CollectionKey(persister, id);
		LoadingCollectionEntry lce = getLoadingCollectionEntry(ckey);
		if (lce==null) {
			//look for existing collection
			PersistentCollection pc = getCollection(ckey);
			if (pc!=null) {
				CollectionEntry ce = getCollectionEntry(pc);
				if (ce.initialized) {
					log.trace( "collection already initialized: ignoring");
					return null; //ignore this row of results! Note the early exit
				}
				else {
					//initialize this collection
					log.trace( "uninitialized collection: initializing");
				}
			}
			else {
				Object entity = getCollectionOwner(id, persister);
				if ( entity!=null && getEntry(entity).status!=LOADING ) {
					//important, to account for newly saved entities in query
					log.trace("owning entity already loaded: ignoring");
					return null;
				}
				else {
					//create one
					log.trace( "new collection: instantiating");
					pc = persister.getCollectionType().instantiate(this, persister);
				}
			}
			pc.beforeInitialize(persister);
			pc.beginRead();
			addLoadingCollectionEntry(ckey, pc, id, resultSetId);
			return pc;
		}
		else {
			if (lce.resultSetId==resultSetId) {
				log.trace("reading row");
				return lce.collection;
			}
			else {
				//ignore this row, the collection is in process of being loaded somewhere further "up" the stack
				log.trace("collection is already being initialized: ignoring row");
				return null;
			}
		}
	}

	public void endLoadingCollections(CollectionPersister persister, Object resultSetId) throws HibernateException {

		// scan the loading collections for collections from this result set
		// put them in a new temp collection so that we are safe from concurrent
		// modification when the call to endRead() causes a proxy to be
		// initialized
		List resultSetCollections = null; //TODO: make this the resultSetId?
		Iterator iter = loadingCollections.values().iterator();
		while ( iter.hasNext() ) {
			LoadingCollectionEntry lce = (LoadingCollectionEntry) iter.next();
			if (lce.resultSetId==resultSetId) {
				if (resultSetCollections==null) resultSetCollections = new ArrayList();
				resultSetCollections.add(lce);
				iter.remove();
			}
		}

		endLoadingCollections(persister, resultSetCollections);
	}

	private void endLoadingCollections(CollectionPersister persister, List resultSetCollections)
	throws HibernateException {

		final int count = resultSetCollections==null ? 0 : resultSetCollections.size();

		if ( log.isDebugEnabled() ) log.debug( count + " collections were found in result set" );

		//now finish them
		for ( int i=0; i<count; i++ ) {
			LoadingCollectionEntry lce = (LoadingCollectionEntry) resultSetCollections.get(i);
			boolean noQueuedAdds = lce.collection.endRead(); //warning: can cause a recursive query! (proxy initialization)
			CollectionEntry ce = getCollectionEntry(lce.collection);
			if (ce==null) {
				ce = addInitializedCollection(lce.collection, persister, lce.id);
			}
			else {
				ce.postInitialize(lce.collection);
			}
			if ( noQueuedAdds && persister.hasCache() && !ce.doremove ) {
				if ( log.isDebugEnabled() ) log.debug( "Caching collection: " + MessageHelper.infoString(persister, lce.id) );
				ClassPersister ownerPersister = factory.getPersister( persister.getOwnerClass() );
				Object version;
				Comparator versionComparator;
				if ( ownerPersister.isVersioned() ) {
					version = getEntry( getCollectionOwner(ce) ).version;
					versionComparator = ownerPersister.getVersionType().getComparator();
				}
				else {
					version = null;
					versionComparator = null;
				}
				persister.getCache().put( 
						lce.id, lce.collection.disassemble(persister), getTimestamp(), version, versionComparator
				);
			}

			if ( log.isDebugEnabled() ) log.debug( "collection fully initialized: " + MessageHelper.infoString(persister, lce.id) );
		}

		if ( log.isDebugEnabled() ) log.debug( count + " collections initialized" );
	}

	private PersistentCollection getLoadingCollection(String role, Serializable id) {
		LoadingCollectionEntry lce = getLoadingCollectionEntry( new CollectionKey(role, id) );
		return (lce!=null) ? lce.collection : null;
	}

	public void beforeLoad() {
		loadCounter++;
	}

	public void afterLoad() {
		loadCounter--;
	}

	public void initializeNonLazyCollections() throws HibernateException {
		if (loadCounter==0) {
			log.debug( "initializing non-lazy collections");
			//do this work only at the very highest level of the load
			loadCounter++; //don't let this method be called recursively
			try {
				int size;
				while( ( size=nonlazyCollections.size() ) > 0 ) {
					//note that each iteration of the loop may add new elements
					( (PersistentCollection) nonlazyCollections.remove(size-1) ).forceInitialization();
				}
			}
			finally {
				loadCounter--;
			}
		}
	}

	private void addCollection(PersistentCollection coll, CollectionEntry entry, Serializable key) {
		collectionEntries.put(coll, entry);
		PersistentCollection old = (PersistentCollection) collectionsByKey.put(
			new CollectionKey(entry.loadedPersister, key),
			coll
		);
		if (old!=null) {
			if (old==coll) throw new AssertionFailure("collection added twice");
			// or should it actually throw an exception?
			old.unsetSession(this);
			collectionEntries.remove(old);
			// watch out for a case where old is still referenced
			// somewhere in the object graph! (which is a user error)
		}
	}

	private PersistentCollection getCollection(CollectionKey collectionKey) {
		return (PersistentCollection) collectionsByKey.get(collectionKey);
	}

	/**
	 * add a collection we just loaded up (still needs initializing)
	 */
	private void addUninitializedCollection(PersistentCollection collection, CollectionPersister persister, Serializable id) {
		CollectionEntry ce = new CollectionEntry(persister, id, flushing);
		collection.setCollectionSnapshot(ce);
		addCollection(collection, ce, id);
	}

	/**
	 * add a detached uninitialized collection
	 */
	private void addUninitializedDetachedCollection(PersistentCollection collection, CollectionPersister persister, Serializable id) {
		CollectionEntry ce = new CollectionEntry(persister, id);
		collection.setCollectionSnapshot(ce);
		addCollection(collection, ce, id);
	}

	/**
	 * add a collection we just pulled out of the cache (does not need initializing)
	 */
	private CollectionEntry addInitializedCollection(PersistentCollection collection, CollectionPersister persister, Serializable id)
	throws HibernateException {
		CollectionEntry ce = new CollectionEntry(persister, id, flushing);
		ce.postInitialize(collection);
		collection.setCollectionSnapshot(ce);
		addCollection(collection, ce, id);
		return ce;
	}

	CollectionEntry addCollection(PersistentCollection collection)
	throws HibernateException {
		CollectionEntry ce = new CollectionEntry();
		collectionEntries.put(collection, ce);
		collection.setCollectionSnapshot(ce);
		return ce;
	}

	/**
	 * add a new collection (ie. a newly created one, just instantiated by the
	 * application, with no database state or snapshot)
	 */
	void addNewCollection(PersistentCollection collection, CollectionPersister persister)
	throws HibernateException {
		CollectionEntry ce = addCollection(collection);
		if ( persister.hasOrphanDelete() ) ce.initSnapshot(collection, persister);
	}

	/**
	 * add an (initialized) collection that was created by another session and passed
	 * into update() (ie. one with a snapshot and existing state on the database)
	 */
	private void addInitializedDetachedCollection(PersistentCollection collection, CollectionSnapshot cs)
	throws HibernateException {
		if ( cs.wasDereferenced() ) {
			addCollection(collection);
		}
		else {
			CollectionEntry ce = new CollectionEntry(cs, factory);
			collection.setCollectionSnapshot(ce);
			addCollection( collection, ce, cs.getKey() );
		}
	}

	public ArrayHolder getArrayHolder(Object array) {
		return (ArrayHolder) arrayHolders.get(array);
	}

	/**
	 * associate a holder with an array - called after loading array
	 */
	public void addArrayHolder(ArrayHolder holder) {
		//TODO:refactor + make this method private
		arrayHolders.put( holder.getArray(), holder );
	}

	CollectionPersister getCollectionPersister(String role) throws MappingException {
		return factory.getCollectionPersister(role);
	}

	/*public void dirty(PersistentCollection coll) {
		getCollectionEntry(coll).dirty = true;
	}*/
	public Serializable getSnapshot(PersistentCollection coll) {
		return getCollectionEntry(coll).snapshot;
	}

	public Serializable getLoadedCollectionKey(PersistentCollection coll) {
		return getCollectionEntry(coll).loadedKey;
	}

	public boolean isInverseCollection(PersistentCollection collection) {
		CollectionEntry ce = getCollectionEntry(collection);
		return ce!=null && ce.loadedPersister.isInverse();
	}

	private static final Collection EMPTY = new ArrayList();

	public Collection getOrphans(PersistentCollection coll) throws HibernateException {
		CollectionEntry ce = getCollectionEntry(coll);
		return ce==null || ce.isNew() ? EMPTY : coll.getOrphans( ce.getSnapshot() );
	}

	/**
	 * called by a collection that wants to initialize itself
	 */
	public void initializeCollection(PersistentCollection collection, boolean writing) throws HibernateException {
		CollectionEntry ce = getCollectionEntry(collection);
		if (ce==null) throw new HibernateException("collection was evicted");
		if ( !ce.initialized ) {
			if ( log.isTraceEnabled() ) log.trace( "initializing collection " + MessageHelper.infoString(ce.loadedPersister, ce.loadedKey) );
			log.trace("checking second-level cache");
			boolean foundInCache = initializeCollectionFromCache(ce.loadedKey, getCollectionOwner(ce), ce.loadedPersister, collection);
			if (foundInCache) {
				log.trace("collection initialized from cache");
			}
			else {
				log.trace("collection not cached");
				ce.loadedPersister.initialize(ce.loadedKey, this);
				log.trace("collection initialized");
			}
		}
	}

	private Object getCollectionOwner(CollectionEntry ce) throws MappingException {
		return getCollectionOwner(ce.loadedKey, ce.loadedPersister);
	}

	public Object getCollectionOwner(Serializable key, CollectionPersister collectionPersister) throws MappingException {
		//TODO:give collection persister a reference to the owning class persister
		return getEntity( new Key(key, factory.getPersister( collectionPersister.getOwnerClass() ) ) );
	}

	public Connection connection() throws HibernateException {
		if (connection==null) {
			if (connect) {
				connect();
			}
			else if ( isOpen() ) {
				throw new HibernateException("Session is currently disconnected");
			}
			else {
				throw new HibernateException("Session is closed");
			}
		}
		return connection;
	}

	private boolean isJTATransactionActive(javax.transaction.Transaction tx) throws SystemException {
		return tx != null && (
			tx.getStatus() == javax.transaction.Status.STATUS_ACTIVE ||
			tx.getStatus() == javax.transaction.Status.STATUS_MARKED_ROLLBACK
		);
	}

	private void connect() throws HibernateException {
		if (!isCurrentTransaction) {
			//if there is no current transaction callback registered
			//when we obtain the connection, try to register one now
			//note that this is not going to handle the case of
			//multiple-transactions-per-connection when the user is
			//manipulating transactions (need to use Hibernate txn)
			TransactionManager tm = factory.getTransactionManager();
			if (tm!=null) {
				try {
					javax.transaction.Transaction tx = tm.getTransaction();
					if ( isJTATransactionActive(tx) ) {
						tx.registerSynchronization( new CacheSynchronization(this) );
						isCurrentTransaction = true;
					}
				}
				catch (Exception e) {
					throw new TransactionException("could not register synchronization with JTA TransactionManager", e);
				}
			}
		}
		connection = batcher.openConnection();
		connect = false;
	}

	public boolean isConnected() {
		return connection!=null || connect;
	}

	public Connection disconnect() throws HibernateException {

		log.debug("disconnecting session");

		try {

			if (connect) {
				connect = false;
				return null;
			}
			else {

				if (connection==null) throw new HibernateException("Session already disconnected");

				batcher.closeStatements();
				Connection c = connection;
				connection=null;
				if (autoClose) {
					batcher.closeConnection(c);
					return null;
				}
				else {
					return c;
				}

			}

		}
		finally {
			if ( !isCurrentTransaction ) {
				afterTransactionCompletion(false); //false because we don't know the outcome of the transaction
			}
		}
	}

	public void reconnect() throws HibernateException {
		if ( isConnected() ) throw new HibernateException("Session already connected");

		log.debug("reconnecting session");

		connect = true;
		//connection = factory.openConnection();
	}

	public void reconnect(Connection conn) throws HibernateException {
		if ( isConnected() ) throw new HibernateException("Session already connected");
		this.connection=conn;
	}

	/**
	 * Just in case user forgot to commit()/cancel() or close()
	 */
	protected void finalize() throws Throwable {

		log.debug("running Session.finalize()");

		if (isCurrentTransaction) log.warn("afterTransactionCompletion() was never called");

		if (connection!=null) { //ie it was never disconnected

			//afterTransactionCompletion(false);

			if ( connection.isClosed() ) {
				log.warn("finalizing unclosed session with closed connection");
			}
			else {
				log.warn("unclosed connection, forgot to call close() on your session?");
				if (autoClose) connection.close();
				//TODO: Should I also call closeStatements() from here?
			}
		}
	}

	public Collection filter(Object collection, String filter) throws HibernateException {
		return filter(collection, filter, new QueryParameters( new Type[1], new Object[1] ) );
	}

	public Collection filter(Object collection, String filter, Object value, Type type) throws HibernateException {
		return filter(collection, filter, new QueryParameters( new Type[] { null, type }, new Object[] { null, value } ) );
	}

	public Collection filter(Object collection, String filter, Object[] values, Type[] types) throws HibernateException {
		Object[] vals = new Object[ values.length + 1 ];
		Type[] typs = new Type[ types.length + 1];
		System.arraycopy(values, 0, vals, 1, values.length);
		System.arraycopy(types, 0, typs, 1, types.length);
		return filter( collection, filter, new QueryParameters(typs, vals) );
	}

	/**
	 * 1. determine the collection role of the given collection (this may require a flush, if the
	 *    collecion is recorded as unreferenced)
	 * 2. obtain a compiled filter query
	 * 3. autoflush if necessary
	 */
	private FilterTranslator getFilterTranslator(Object collection, String filter, QueryParameters parameters, boolean scalar)
	throws HibernateException {

		if (collection==null) throw new NullPointerException("null collection passed to filter");

		if ( log.isTraceEnabled() ) {
			log.trace( "filter: " + filter );
			parameters.traceParameters(factory);
		}

		CollectionEntry entry = getCollectionEntryOrNull(collection);
		final CollectionPersister roleBeforeFlush = (entry==null) ? null : entry.loadedPersister;

		FilterTranslator filterTranslator;
		if ( roleBeforeFlush==null ) {
			// if it was previously unreferenced, we need
			// to flush in order to get its state into the
			// database to query
			flush();
			entry = getCollectionEntryOrNull(collection);
			CollectionPersister roleAfterFlush = (entry==null) ? null : entry.loadedPersister;
			if (roleAfterFlush==null) throw new QueryException("The collection was unreferenced");
			filterTranslator = factory.getFilter(filter, roleAfterFlush.getRole(), scalar);
		}
		else {
			// otherwise, we only need to flush if there are
			// in-memory changes to the queried tables
			filterTranslator = factory.getFilter( filter, roleBeforeFlush.getRole(), scalar );
			if ( autoFlushIfRequired( filterTranslator.getQuerySpaces() ) ) {
				// might need to run a different filter entirely after the flush
				// because the collection role may have changed
				entry = getCollectionEntryOrNull(collection);
				CollectionPersister roleAfterFlush = (entry==null) ? null : entry.loadedPersister;
				if (roleBeforeFlush!=roleAfterFlush)  {
					if (roleAfterFlush==null) throw new QueryException("The collection was dereferenced");
					filterTranslator=factory.getFilter( filter, roleAfterFlush.getRole(), scalar );
				}
			}
		}

		parameters.getPositionalParameterValues()[0] = entry.loadedKey;
		parameters.getPositionalParameterTypes()[0] = entry.loadedPersister.getKeyType();

		return filterTranslator;
	}

	/**
	 * Get the collection entry for a collection passed to filter,
	 * which might be a collection wrapper, an array, or an unwrapped
	 * collection. Return null if there is no entry.
	 */
	private CollectionEntry getCollectionEntryOrNull(Object collection) {

		PersistentCollection coll;
		if (collection instanceof PersistentCollection) {
			coll = (PersistentCollection) collection;
			//if (collection==null) throw new TransientObjectException("Collection was not yet persistent");
		}
		else {
			coll = getArrayHolder(collection);
			if (coll==null) {
				//it might be an unwrapped collection reference!
				//try to find a wrapper (slowish)
				Iterator wrappers = IdentityMap.keyIterator(collectionEntries);
				while ( wrappers.hasNext() ) {
					PersistentCollection pc = (PersistentCollection) wrappers.next();
					if ( pc.isWrapper(collection) ) {
						coll=pc;
						break;
					}
				}
			}
		}

		return (coll==null) ? null : getCollectionEntry(coll);

	}

	public List filter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {

		String[] concreteFilters = QueryTranslator.concreteQueries(filter, factory);
		FilterTranslator[] filters = new FilterTranslator[ concreteFilters.length ];

		for ( int i=0; i<concreteFilters.length; i++ ) {
			filters[i] = getFilterTranslator(collection, concreteFilters[i], queryParameters, false);
		}

		dontFlushFromFind++;   //stops flush being called multiple times if this method is recursively called

		List results = Collections.EMPTY_LIST;
		try {
			for ( int i=0; i<concreteFilters.length; i++ ) {
				List currentResults;
				try {
					currentResults = filters[i].list(this, queryParameters);
				}
				catch (SQLException sqle) {
					throw convert( sqle, "Could not execute query" );
				}
				currentResults.addAll(results);
				results = currentResults;
			}
		}
		finally {
			dontFlushFromFind--;
		}
		return results;

	}

	public Iterator iterateFilter(Object collection, String filter, QueryParameters queryParameters) throws HibernateException {

		String[] concreteFilters = QueryTranslator.concreteQueries(filter, factory);
		FilterTranslator[] filters = new FilterTranslator[ concreteFilters.length ];

		for ( int i=0; i<concreteFilters.length; i++ ) {
			filters[i] = getFilterTranslator(collection, concreteFilters[i], queryParameters, true);
		}

		if (filters.length==0) return Collections.EMPTY_LIST.iterator();

		Iterator result = null;
		Iterator[] results = null;
		boolean many = filters.length>1;
		if (many) results = new Iterator[filters.length];

		//execute the queries and return all results as a single iterator
		for ( int i=0; i<filters.length; i++ ) {

			try {
				result = filters[i].iterate(queryParameters, this);
			}
			catch (SQLException sqle) {
				throw convert( sqle, "Could not execute query" );
			}
			if ( many ) {
				results[i] = result;
			}

		}

		return many ? new JoinedIterator(results) : result;

	}

	public Criteria createCriteria(Class persistentClass) {
		return new CriteriaImpl(persistentClass, this);
	}

	public List find(CriteriaImpl criteria) throws HibernateException {

		String[] implementors = factory.getImplementors( criteria.getCriteriaClass() );
		int size = implementors.length;

		CriteriaLoader[] loaders = new CriteriaLoader[size];
		Set spaces = new HashSet();
		for( int i=0; i <size; i++ ) {

			Class newCriteriaClazz;
			try {
				newCriteriaClazz = ReflectHelper.classForName( implementors[i] );
			}
			catch (ClassNotFoundException cnfe) {
				throw new HibernateException("class not found", cnfe);
			}

			loaders[i] = new CriteriaLoader(
				getOuterJoinLoadable(newCriteriaClazz),
				factory,
				new CriteriaImpl(newCriteriaClazz, criteria)
			);

			spaces.addAll( loaders[i].getQuerySpaces() );

		}

		autoFlushIfRequired(spaces);

		List results = Collections.EMPTY_LIST;
		dontFlushFromFind++;
		try {
			for( int i=0; i<size; i++ ) {
				List currentResults;
				try {
					currentResults = loaders[i].list(this);
				}
				catch (SQLException sqle) {
					throw convert( sqle, "Unable to perform find" );
				}
				currentResults.addAll(results);
				results = currentResults;
			}
		}
		finally {
			dontFlushFromFind--;
		}

		return results;
	}

	private OuterJoinLoadable getOuterJoinLoadable(Class clazz) throws MappingException {
		ClassPersister persister = getClassPersister(clazz);
		if ( !(persister instanceof OuterJoinLoadable) ) {
			throw new MappingException( "class persister is not OuterJoinLoadable: " + clazz.getName() );
		}
		return (OuterJoinLoadable) persister;
	}

	public boolean contains(Object object) {
		if ( object instanceof HibernateProxy ) {
			//do not use proxiesByKey, since not all
			//proxies that point to this session's
			//instances are in that collection!
			LazyInitializer li = HibernateProxyHelper.getLazyInitializer( (HibernateProxy) object );
			if ( li.isUninitialized() ) {
				//if it is an uninitialized proxy, pointing
				//with this session, then when it is accessed,
				//the underlying instance will be "contained"
				return li.getSession()==this;
			}
			else {
				//if it is initialized, see if the underlying
				//instance is contained, since we need to 
				//account for the fact that it might have been
				//evicted
				object = li.getImplementation();
			}
		}
		return isEntryFor(object);
	}

	/**
	 * remove any hard references to the entity that are held by the infrastructure
	 * (references held by application or other persistant instances are okay)
	 */
	public void evict(Object object) throws HibernateException {
		if (object instanceof HibernateProxy) {
			LazyInitializer li = HibernateProxyHelper.getLazyInitializer( (HibernateProxy) object );
			Serializable id = li.getIdentifier();
			ClassPersister persister = getClassPersister( li.getPersistentClass() );
			Key key = new Key(id, persister);
			proxiesByKey.remove(key);
			if ( !li.isUninitialized() ) {
				Object entity = removeEntity(key);
				if (entity!=null) {
					EntityEntry e = removeEntry(entity);
					doEvict(e.persister, entity);
				}
			}
		}
		else {
			EntityEntry e = removeEntry(object);
			if (e!=null) {
				removeEntity( new Key(e.id, e.persister) );
				doEvict(e.persister, object);
			}
		}
	}

	private void doEvict(ClassPersister persister, Object object) throws HibernateException {

		if ( log.isTraceEnabled() ) log.trace( "evicting " + MessageHelper.infoString(persister) );

		//remove all collections for the entity from the session-level cache
		if ( persister.hasCollections() ) new EvictVisitor(this).process(object, persister);

		Cascades.cascade(this, persister, object, Cascades.ACTION_EVICT, Cascades.CASCADE_ON_EVICT);
	}

	void evictCollection(Object value, PersistentCollectionType type) {

		final Object pc;
		if ( type.isArrayType() ) {
			pc = arrayHolders.remove(value);
		}
		else if (value instanceof PersistentCollection) {
			pc = value;
		}
		else {
			return; //EARLY EXIT!
		}

		PersistentCollection collection = (PersistentCollection) pc;
		if ( collection.unsetSession(this) ) evictCollection(collection);
	}

	private void evictCollection(PersistentCollection collection) {
		CollectionEntry ce = (CollectionEntry) collectionEntries.remove(collection);
		if ( log.isDebugEnabled() ) log.debug( "evicting collection: " + MessageHelper.infoString(ce.loadedPersister, ce.loadedKey) );
		if (ce.loadedPersister!=null && ce.loadedKey!=null) {
			//TODO: is this 100% correct?
			collectionsByKey.remove( new CollectionKey( ce.loadedPersister.getRole(), ce.loadedKey ) );
		}
	}

	/**
	 * Evict collections from the factory-level cache
	 */
	private void evictCachedCollections(ClassPersister persister, Serializable id) throws HibernateException {
		evictCachedCollections( persister.getPropertyTypes(), id );
	}

	private void evictCachedCollections(Type[] types, Serializable id) throws HibernateException {
		for ( int i=0; i<types.length; i++ ) {
			if ( types[i].isPersistentCollectionType() ) {
				factory.evictCollection( ( (PersistentCollectionType) types[i] ).getRole(), id );
			}
			else if ( types[i].isComponentType() ) {
				AbstractComponentType actype = (AbstractComponentType) types[i];
				evictCachedCollections( actype.getSubtypes(), id );
			}
		}
	}

	public Object getVersion(Object entity) {
		return getEntry(entity).version;
	}

	public Serializable[] getCollectionBatch(CollectionPersister collectionPersister, Serializable id, int batchSize) {
		Serializable[] keys = new Serializable[batchSize];
		keys[0] = id;
		int i=0;
		Iterator iter = collectionEntries.values().iterator();
		while ( iter.hasNext() ) {
			CollectionEntry ce = (CollectionEntry) iter.next();
			if (
				!ce.initialized &&
				ce.loadedPersister==collectionPersister &&
				!id.equals(ce.loadedKey)
			) {
				keys[++i] = ce.loadedKey;
				if (i==batchSize-1) return keys;
			}
		}
		return keys;
	}

	public Serializable[] getClassBatch(Class clazz, Serializable id, int batchSize) {
		Serializable[] ids = new Serializable[batchSize];
		ids[0] = id;
		int i=0;
		Iterator iter = batchLoadableEntityKeys.keySet().iterator();
		while ( iter.hasNext() ) {
			Key key = (Key) iter.next();
			if (
				key.getMappedClass()==clazz &&
				!id.equals( key.getIdentifier() )
			) {
				ids[++i] = key.getIdentifier();
				if (i==batchSize-1) return ids;
			}
		}
		return ids;
	}

	public void scheduleBatchLoad(Class clazz, Serializable id) throws MappingException {
		ClassPersister persister = getClassPersister(clazz);
		if ( persister.isBatchLoadable() ) {
			batchLoadableEntityKeys.put( new Key(id, persister), MARKER );
		}
	}

	public Query createSQLQuery(String sql, String returnAlias, Class returnClass) {
		return new SQLQueryImpl(sql, new String[] { returnAlias }, new Class[] { returnClass }, this, null);
	}

	public Query createSQLQuery(String sql, String returnAliases[], Class returnClasses[]) {
		return new SQLQueryImpl(sql, returnAliases, returnClasses, this, null);
	}

	public Query createSQLQuery(String sql, String returnAliases[], Class returnClasses[], Collection querySpaces) {
		return new SQLQueryImpl(sql, returnAliases, returnClasses, this, querySpaces);
	}

	// basically just an adapted copy of find(CriteriaImpl)
	public List findBySQL(String sqlQuery, String[] aliases, Class[] classes, QueryParameters queryParameters, Collection querySpaces) throws HibernateException {

		if ( log.isTraceEnabled() ) log.trace( "SQL query: " + sqlQuery );

		SQLLoadable persisters[] = new SQLLoadable[classes.length];
		for (int i = 0; i < classes.length; i++) {
			persisters[i] = getSQLLoadable( classes[i] );
		}

		//TODO: we could cache these!!
		SQLLoader loader = new SQLLoader(aliases, persisters, factory, sqlQuery, querySpaces);

		autoFlushIfRequired( loader.getQuerySpaces() );

		dontFlushFromFind++;
		try {
			return loader.list(this, queryParameters);
		}
		catch (SQLException sqle) {
			throw convert( sqle, "error performing findBySQL" );
		}
		finally {
			dontFlushFromFind--;
		}
	}

	private SQLLoadable getSQLLoadable(Class clazz) throws MappingException {
		ClassPersister cp = getClassPersister(clazz);
		if ( !(cp instanceof SQLLoadable) ) {
			throw new MappingException( "class persister is not SQLLoadable: " + clazz.getName() );
		}
		return (SQLLoadable) cp;
	}

	public void clear() {

		arrayHolders.clear();
		entitiesByKey.clear();
		entityEntries.clear();
		collectionsByKey.clear();
		collectionEntries.clear();
		proxiesByKey.clear();
		batchLoadableEntityKeys.clear();
        nonExists.clear();

		updates.clear();
		insertions.clear();
		deletions.clear();
		collectionCreations.clear();
		collectionRemovals.clear();
		collectionUpdates.clear();
	}

	public Object loadByUniqueKey(Class clazz, String uniqueKeyPropertyName, Serializable id)
	throws HibernateException {
		UniqueKeyLoadable persister = (UniqueKeyLoadable) getFactory().getPersister(clazz);
		try {
			//TODO: implement caching?! proxies?!
			Object result = persister.loadByUniqueKey(uniqueKeyPropertyName, id, this);
			//throwObjectNotFound(result, id, clazz);
			return result==null ? null : proxyFor(result);
		}
		catch (SQLException sqle) {
			throw convert( sqle, "error performing loadByUniqueKey" );
		}
	}


	public void replicate(Object obj, ReplicationMode replicationMode) throws HibernateException {

		if (obj==null) throw new NullPointerException("attempted to replicate null");

		if ( reassociateIfUninitializedProxy(obj) ) return;

		Object object = unproxyAndReassociate(obj);

		if ( isEntryFor(object) ) return;

		ClassPersister persister = getPersister(object);
		if ( persister.isUnsaved(object) ) {
			throw new TransientObjectException("unsaved object passed to replicate()");
			//TODO: generate a new id value for brand new objects
		}
		Serializable id = persister.getIdentifier(object);

		final Object oldVersion;
		if (replicationMode==ReplicationMode.EXCEPTION) {
			//always do an INSERT, and let it fail by constraint violation
			oldVersion = null;
		}
		else {
			//what is the version on the database?
			oldVersion = persister.getCurrentVersion(id, this);
		}

		if (oldVersion!=null) {
			// existing row - do an update if appropriate
			if ( replicationMode.shouldOverwriteCurrentVersion(
				object, oldVersion, persister.getVersion(object), persister.getVersionType()
			) ) {
				//will result in a SQL UPDATE:
				doReplicate(object, id, oldVersion, replicationMode, persister);
			}
			//else do nothing (don't even reassociate object!)
			//TODO: would it be better to do a refresh from db?
		}
		else {
			// no existing row - do an insert
			if ( log.isTraceEnabled() ) log.trace( "replicating " + MessageHelper.infoString(persister, id) );
			final boolean regenerate = persister.isIdentifierAssignedByInsert(); // prefer re-generation of identity!
			doSave(
				object,
				regenerate ? null : new Key(id, persister),
				persister,
				true, //!persister.isUnsaved(object), //TODO: Do an ordinary save in the case of an "unsaved" object
				// TODO: currently ignores interceptor definition of isUnsaved()
				regenerate,
				Cascades.ACTION_REPLICATE, // not quite an ordinary save(), since we cascade back to replicate()
				replicationMode
			);
		}
	}

	public SessionFactory getSessionFactory() {
		return factory;
	}

	/**
	 * instantiate a collection wrapper (called when loading an object)
	 */
	public Object getCollection(String role, Serializable id, Object owner) throws HibernateException {

		// note: there cannot possibly be a collection already registered,
		// because this method is called while first loading the entity
		// that references it

		CollectionPersister persister = factory.getCollectionPersister(role);
		PersistentCollection collection = getLoadingCollection(role, id);
		if (collection!=null) {
			if ( log.isTraceEnabled() ) log.trace( "returning loading collection:" + MessageHelper.infoString(persister, id) );
			return collection.getValue();
		}
		else {
			if ( log.isTraceEnabled() ) log.trace( "creating collection wrapper:" + MessageHelper.infoString(persister, id) );
			collection = persister.getCollectionType().instantiate(this, persister); //TODO: suck into CollectionPersister.instantiate()
			addUninitializedCollection(collection, persister, id);
			if ( persister.isArray() ) {
				initializeCollection(collection, false);
				addArrayHolder( (ArrayHolder) collection );
			}
			else if ( !persister.isLazy() ) {
				nonlazyCollections.add(collection);
			}
			return collection.getValue();
		}

	}

	/**
	 * Try to initialize a collection from the cache
	 */
	private boolean initializeCollectionFromCache(Serializable id, Object owner, CollectionPersister persister, PersistentCollection collection)
	throws HibernateException {

		if ( !persister.hasCache() ) {
			return false;
		}
		else {
			Serializable cached = (Serializable) persister.getCache().get( id, getTimestamp() );
			if (cached==null) {
				return false;
			}
			else {
				collection.initializeFromCache(persister, cached, owner);
				getCollectionEntry(collection).postInitialize(collection);
				//addInitializedCollection(collection, persister, id);
				return true;
			}
		}
	}

	public void cancelQuery() throws HibernateException {
		getBatcher().cancelLastQuery();
	}

	public void addNonExist(Key key) {
		nonExists.add(key);
	}

	public Object saveOrUpdateCopy(Object object) throws HibernateException {
		return doCopy( object, null, IdentityMap.instantiate(10) );
	}

	public Object copy(Object object, Map copiedAlready) throws HibernateException {
		return doCopy(object, null, copiedAlready);
	}

	public Object doCopy(Object object, Serializable id, Map copiedAlready) throws HibernateException {
		if (object==null) return null;

		if (object instanceof HibernateProxy) {
			LazyInitializer li = HibernateProxyHelper.getLazyInitializer( (HibernateProxy) object);
			if ( li.isUninitialized() ) {
				return load( li.getPersistentClass(), li.getIdentifier() );  //EARLY EXIT!
			}
			else {
				object = li.getImplementation();
			}
		}

		if ( copiedAlready.containsKey(object) ) return object; //EARLY EXIT!
		EntityEntry entry = getEntry(object);
		if (entry!=null) {
			if ( id!=null && entry.id.equals(id) ) {
				return object; //EARLY EXIT!
			}
			//else copy from one persistent instance to another!
		}

		Class clazz = object.getClass();
		ClassPersister persister = getClassPersister(clazz);
		
		Object result;
		Object target;
		if ( id==null && persister.isUnsaved(object) ) {
			copiedAlready.put(object, object);
			saveWithGeneratedIdentifier(object, Cascades.ACTION_COPY, copiedAlready);
			result = object; //TODO: handle its proxy (reassociate it, I suppose)
			target = object;
		}
		else {
			if (id==null) id = persister.getIdentifier(object);
			result = get(clazz, id);
			if (result==null) {
				copiedAlready.put(object, object);
				saveWithGeneratedIdentifier(object, Cascades.ACTION_COPY, copiedAlready);
				result = object; //TODO: could it have a proxy??
				target = object;
			}
			else {
				target = unproxy(result);
				copiedAlready.put(object, target);
				if (target==object) {
					return result;
				}
				else if ( Hibernate.getClass(result)!=clazz ) {
					throw new WrongClassException("class of the given object did not match class of persistent copy", id, clazz);
				}
				else if (
					persister.isVersioned() &&
					!persister.getVersionType().equals( persister.getVersion(result), persister.getVersion(object) )
				) {
					throw new StaleObjectStateException(clazz, id);
				}
	
				//cascade first, so that all unsaved objects get saved before we actually copy
				Cascades.cascade(this, persister, object, Cascades.ACTION_COPY, Cascades.CASCADE_ON_COPY, copiedAlready);
			}
			
		}

		//no need to handle the version differently
		Object[] copiedValues = TypeFactory.copy(
			persister.getPropertyValues(object),
			persister.getPropertyValues(target),
			persister.getPropertyTypes(),
			this,
			target, copiedAlready
		);

		persister.setPropertyValues(target, copiedValues);
		return result;
	}

	public Object saveOrUpdateCopy(Object object, Serializable id)
		throws HibernateException {
		return doCopy( object, id, IdentityMap.instantiate(10) );
	}

	protected JDBCException convert( SQLException sqlException, String message ) {
		return JDBCExceptionHelper.convert( getFactory().getSQLExceptionConverter(), sqlException, message );
	}

}
