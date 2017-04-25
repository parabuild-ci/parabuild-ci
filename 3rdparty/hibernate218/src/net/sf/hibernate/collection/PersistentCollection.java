//$Id: PersistentCollection.java,v 1.24 2004/06/04 01:27:37 steveebersole Exp $
package net.sf.hibernate.collection;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import net.sf.hibernate.AssertionFailure;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LazyInitializationException;
import net.sf.hibernate.engine.CollectionSnapshot;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.EmptyIterator;

import org.apache.commons.logging.LogFactory;

/**
 * Persistent collections are treated as value objects by Hibernate.
 * ie. they have no independent existence beyond the object holding
 * a reference to them. Unlike instances of entity classes, they are
 * automatically deleted when unreferenced and automatically become
 * persistent when held by a persistent object. Collections can be
 * passed between different objects (change "roles") and this might
 * cause their elements to move from one database table to another.<br>
 * <br>
 * Hibernate "wraps" a java collection in an instance of
 * PersistentCollection. This mechanism is designed to support
 * tracking of changes to the collection's persistent state and
 * lazy instantiation of collection elements. The downside is that
 * only certain abstract collection types are supported and any
 * extra semantics are lost<br>
 * <br>
 * Applications should <em>never</em> use classes in this package 
 * directly, unless extending the "framework" here.<br>
 * <br>
 * Changes to <em>structure</em> of the collection are recorded by the
 * collection calling back to the session. Changes to mutable
 * elements (ie. composite elements) are discovered by cloning their
 * state when the collection is initialized and comparing at flush
 * time.
 * 
 * @author Gavin King
 */

public abstract class PersistentCollection implements Serializable {

	private transient SessionImplementor session;
	private boolean initialized;
	private transient List additions;	
	private CollectionSnapshot collectionSnapshot;
	private transient boolean directlyAccessible;
	private transient boolean initializing;
	
	//Careful: these methods do not initialize the collection.
	/**
	 * Is the initialized collection empty?
	 */
	public abstract boolean empty();
	/**
	 * Called by any read-only method of the collection interface
	 */
	public final void read() {
		initialize(false);
	}
	/**
	 * Is the collection currently connected to an open session?
	 */
	private final boolean isConnectedToSession() {
		return session!=null && session.isOpen();
	}
	
	/**
	 * Called by any writer method of the collection interface
	 */
	protected final void write() {
		initialize(true);
		collectionSnapshot.setDirty();
	}
	/**
	 * Is this collection in a state that would allow us to 
	 * "queue" additions?
	 */
	private boolean isQueueAdditionEnabled() {
		return !initialized && 
			isConnectedToSession() && 
			session.isInverseCollection(this);
	}
	/**
	 * Queue an addition
	 */
	protected final boolean queueAdd(Object element) {
		if ( isQueueAdditionEnabled() ) {
			if (additions==null) additions = new ArrayList(10);
			additions.add(element);
			collectionSnapshot.setDirty(); //needed so that we remove this collection from the JCS cache
			return true;
		}
		else {
			return false;
		}
	}
	/**
	 * Queue additions
	 */
	protected final boolean queueAddAll(Collection coll) {
		if ( isQueueAdditionEnabled() ) {
			if (additions==null) additions = new ArrayList(20);
			additions.addAll(coll);
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * After reading all existing elements from the database,
	 * add the queued elements to the underlying collection.
	 */
	public void delayedAddAll(Collection coll) {
		throw new AssertionFailure("Collection does not support delayed initialization");
	}
	
	/**
	 * After flushing, clear any "queued" additions, since the
	 * database state is now synchronized with the memory state.
	 */
	public void postFlush() {
		if (additions!=null) additions=null;
	}

	/**
	 * Not called by Hibernate, but used by non-JDK serialization,
	 * eg. SOAP libraries.
	 */
	public PersistentCollection() {} 
	
	protected PersistentCollection(SessionImplementor session) {
		this.session = session;
	}

	/**
	 * return the user-visible collection (or array) instance
	 */
	public Object getValue() {
		return this;
	}
	
	/**
	 * Called just before reading any rows from the JDBC result set
	 */
	public void beginRead() { 
		// override on some subclasses
		initializing = true;
	}
	
	/**
	 * Called after reading all rows from the JDBC result set
	 */
	public boolean endRead() { 
		//override on some subclasses
		
		setInitialized();
		//do this bit after setting initialized to true or it will recurse
		if (additions!=null) {
			delayedAddAll(additions);
			additions=null;
			return false;
		}
		else {
			return true;
		}
	}
	
	/**
	 * Initialize the collection, if possible, wrapping any exceptions
	 * in a runtime exception
	 * @param writing currently obsolete
	 * @throws LazyInitializationException if we cannot initialize
	 */
	protected final void initialize(boolean writing) {
		if (!initialized) {
			if (initializing) throw new LazyInitializationException("cannot access loading collection");
			if ( isConnectedToSession() )  {
				if ( session.isConnected() ) {
					try {
						session.initializeCollection(this, writing);
					}
					catch (Exception e) {
						LogFactory.getLog(PersistentCollection.class).error(
							"Failed to lazily initialize a collection", e
						);
						throw new LazyInitializationException("Failed to lazily initialize a collection", e);
					}
				}
				else {
					throw new LazyInitializationException("Failed to lazily initialize a collection - session is disconnected");
				}
			}
			else {
				throw new LazyInitializationException("Failed to lazily initialize a collection - no session or session was closed");
			}
		}
	}

	protected final void setInitialized() {
		this.initializing = false;
		this.initialized = true;
	}

	protected final void setDirectlyAccessible(boolean directlyAccessible) {
		this.directlyAccessible = directlyAccessible;
	}
	
	/**
	 * Could the application possibly have a direct reference to
	 * the underlying collection implementation?
	 */
	public boolean isDirectlyAccessible() {
		return directlyAccessible;
	}
	
	/**
	 * Disassociate this collection from the given session.
	 * @return true if this was currently associated with the given session
	 */
	public final boolean unsetSession(SessionImplementor currentSession) {
		if (currentSession==this.session) {
			this.session=null;
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Associate the collection with the given session.
	 * @return false if the collection was already associated with the session
	 * @throws HibernateException if the collection was already associated
	 * with another open session
	 */
	public final boolean setCurrentSession(SessionImplementor session) throws HibernateException {
		if (session==this.session) {
			return false;
		}
		else {
			if ( isConnectedToSession() ) {
				throw new HibernateException("Illegal attempt to associate a collection with two open sessions");
			}
			else {
				this.session = session;
				return true;
			}
		}
	}
	
	/**
	 * Read the state of the collection from a disassembled cached value 
	 */
	public abstract void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner) 
	throws HibernateException;
	
	/**
	 * Iterate all collection entries, during update of the database
	 */
	public abstract Iterator entries();
	
	/**
	 * Read a row from the JDBC result set
	 */
	public abstract Object readFrom(ResultSet rs, CollectionPersister role, Object owner) 
	throws HibernateException, SQLException;
	
	/**
	 * Write a row to the JDBC prepared statement
	 */
	public abstract void writeTo(PreparedStatement st, CollectionPersister role, Object entry, int i, boolean writeOrder) 
	throws HibernateException, SQLException;
	
	/**
	 * Get the index of the given collection entry
	 */
	public abstract Object getIndex(Object entry, int i);

	/**
	 * Called before any elements are read into the collection,
	 * allowing appropriate initializations to occur.
	 */
	public abstract void beforeInitialize(CollectionPersister persister);
	
	/**
	 * Does the current state exactly match the snapshot?
	 */
	public abstract boolean equalsSnapshot(Type elementType) throws HibernateException;
	/**
	 * Return a new snapshot of the current state
	 */
	protected abstract Serializable snapshot(CollectionPersister persister) throws HibernateException;
	
	/**
	 * Disassemble the collection, ready for the cache
	 */
	public abstract Serializable disassemble(CollectionPersister persister) throws HibernateException;
	
	/**
	 * Do we need to completely recreate this collection when it changes?
	 */
	public boolean needsRecreate(CollectionPersister persister) {
		return false;
	}
	/**
	 * Return a new snapshot of the current state of the collection,
	 * or null if no persister is passed
	 */
	public final Serializable getSnapshot(CollectionPersister persister) throws HibernateException {
		return (persister==null) ? null : snapshot(persister);
	}
	
	/**
	 * To be called internally by the session, forcing
	 * immediate initialization.
	 */
	public final void forceInitialization() throws HibernateException {
		if (initializing) throw new AssertionFailure("force initialize loading collection");
		if (session==null) throw new HibernateException("collection is not associated with any session");
		if ( !session.isConnected() ) throw new HibernateException("disconnected session");
		if (!initialized) session.initializeCollection(this, false);
	}
	
	/**
	 * Does an element exist at this entry in the collection?
	 */
	public abstract boolean entryExists(Object entry, int i);  //note that i parameter is now unused (delete it?)
	/**
	 * Do we need to insert this element?
	 */
	public abstract boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException;
	/**
	 * Do we need to update this element?
	 */
	public abstract boolean needsUpdating(Object entry, int i, Type elemType) throws HibernateException;
	/**
	 * Get all the elements that need deleting
	 */
	public abstract Iterator getDeletes(Type elemType) throws HibernateException;
	
	/**
	 * Is this the wrapper for the given underlying collection instance?
	 */
	public abstract boolean isWrapper(Object collection);
	
	/**
	 * Get the current snapshot from the session
	 */
	protected final Serializable getSnapshot() {
		return session.getSnapshot(this);
	}
	
	/**
	 * Is this instance initialized?
	 */
	public final boolean wasInitialized() {
		return initialized;
	}
	
	/**
	 * Does this instance have any "queued" additions?
	 */
	public final boolean hasQueuedAdditions() {
		return additions!=null;
	}
	/**
	 * Iterate the "queued" additions
	 */
	public final Iterator queuedAdditionIterator() {
		return hasQueuedAdditions() ? 
			additions.iterator() :
			EmptyIterator.INSTANCE;
	}
	
	/**
	 * Returns the collectionSnapshot.
	 * @return CollectionSnapshot
	 */
	public CollectionSnapshot getCollectionSnapshot() {
		return collectionSnapshot;
	}

	/**
	 * Sets the collectionSnapshot.
	 * @param collectionSnapshot The collectionSnapshot to set
	 */
	public void setCollectionSnapshot(CollectionSnapshot collectionSnapshot) {
		this.collectionSnapshot = collectionSnapshot;
	}
	
	/**
	 * Called before inserting rows, to ensure that any surrogate keys
	 * are fully generated
	 */
	public void preInsert(CollectionPersister persister) throws HibernateException {}
	/**
	 * Called after inserting a row, to fetch the natively generated id
	 */
	public void afterRowInsert(CollectionPersister persister, Object entry, int i) throws HibernateException {}
	/**
	 * get all "orphaned" elements
	 */
	public abstract Collection getOrphans(Serializable snapshot) throws HibernateException;

	/**
	 * Get the current session
	 */
	protected final SessionImplementor getSession() {
		return session;
	}

	final class IteratorProxy implements Iterator {
		private final Iterator iter;
		IteratorProxy(Iterator iter) {
			this.iter=iter;
		}
		public boolean hasNext() {
			return iter.hasNext();
		}
		
		public Object next() {
			return iter.next();
		}
		
		public void remove() {
			write();
			iter.remove();
		}
		
	}
	
	final class ListIteratorProxy implements ListIterator {
		private final ListIterator iter;
		ListIteratorProxy(ListIterator iter) {
			this.iter = iter;
		}
		public void add(Object o) {
			write();
			iter.add(o);
		}
		
		public boolean hasNext() {
			return iter.hasNext();
		}
		
		public boolean hasPrevious() {
			return iter.hasPrevious();
		}
		
		public Object next() {
			return iter.next();
		}
		
		public int nextIndex() {
			return iter.nextIndex();
		}
		
		public Object previous() {
			return iter.previous();
		}
		
		public int previousIndex() {
			return iter.previousIndex();
		}
		
		public void remove() {
			write();
			iter.remove();
		}
		
		public void set(Object o) {
			write();
			iter.set(o);
		}
		
	}
	
	class SetProxy implements java.util.Set {
		
		final Collection set;
		
		SetProxy(Collection set) {
			this.set=set;
		}
		public boolean add(Object o) {
			write();
			return set.add(o);
		}
		
		public boolean addAll(Collection c) {
			write();
			return set.addAll(c);
		}
		
		public void clear() {
			write();
			set.clear();
		}
		
		public boolean contains(Object o) {
			return set.contains(o);
		}
		
		public boolean containsAll(Collection c) {
			return set.containsAll(c);
		}
		
		public boolean isEmpty() {
			return set.isEmpty();
		}
		
		public Iterator iterator() {
			return new IteratorProxy( set.iterator() );
		}
		
		public boolean remove(Object o) {
			write();
			return set.remove(o);
		}
		
		public boolean removeAll(Collection c) {
			write();
			return set.removeAll(c);
		}
		
		public boolean retainAll(Collection c) {
			write();
			return set.retainAll(c);
		}
		
		public int size() {
			return set.size();
		}
		
		public Object[] toArray() {
			return set.toArray();
		}
		
		public Object[] toArray(Object[] array) {
			return set.toArray(array);
		}
		
	}
	
	final class ListProxy implements java.util.List {
		
		private final java.util.List list;
		
		ListProxy(java.util.List list) {
			this.list = list;
		}
		
		public void add(int index, Object value) {
			write();
			list.add(index, value);
		}
		
		/**
		 * @see java.util.Collection#add(Object)
		 */
		public boolean add(Object o) {
			write();
			return list.add(o);
		}
		
		/**
		 * @see java.util.Collection#addAll(Collection)
		 */
		public boolean addAll(Collection c) {
			write();
			return list.addAll(c);
		}
		
		/**
		 * @see java.util.List#addAll(int, Collection)
		 */
		public boolean addAll(int i, Collection c) {
			write();
			return list.addAll(i, c);
		}
		
		/**
		 * @see java.util.Collection#clear()
		 */
		public void clear() {
			write();
			list.clear();
		}
		
		/**
		 * @see java.util.Collection#contains(Object)
		 */
		public boolean contains(Object o) {
			return list.contains(o);
		}
		
		/**
		 * @see java.util.Collection#containsAll(Collection)
		 */
		public boolean containsAll(Collection c) {
			return list.containsAll(c);
		}
		
		/**
		 * @see java.util.List#get(int)
		 */
		public Object get(int i) {
			return list.get(i);
		}
		
		/**
		 * @see java.util.List#indexOf(Object)
		 */
		public int indexOf(Object o) {
			return list.indexOf(o);
		}
		
		/**
		 * @see java.util.Collection#isEmpty()
		 */
		public boolean isEmpty() {
			return list.isEmpty();
		}
		
		/**
		 * @see java.util.Collection#iterator()
		 */
		public Iterator iterator() {
			return new IteratorProxy( list.iterator() );
		}
		
		/**
		 * @see java.util.List#lastIndexOf(Object)
		 */
		public int lastIndexOf(Object o) {
			return list.lastIndexOf(o);
		}
		
		/**
		 * @see java.util.List#listIterator()
		 */
		public ListIterator listIterator() {
			return new ListIteratorProxy( list.listIterator() );
		}
		
		/**
		 * @see java.util.List#listIterator(int)
		 */
		public ListIterator listIterator(int i) {
			return new ListIteratorProxy( list.listIterator(i) );
		}
		
		/**
		 * @see java.util.List#remove(int)
		 */
		public Object remove(int i) {
			write();
			return list.remove(i);
		}
		
		/**
		 * @see java.util.Collection#remove(Object)
		 */
		public boolean remove(Object o) {
			write();
			return list.remove(o);
		}
		
		/**
		 * @see java.util.Collection#removeAll(Collection)
		 */
		public boolean removeAll(Collection c) {
			write();
			return list.removeAll(c);
		}
		
		/**
		 * @see java.util.Collection#retainAll(Collection)
		 */
		public boolean retainAll(Collection c) {
			write();
			return list.retainAll(c);
		}
		
		/**
		 * @see java.util.List#set(int, Object)
		 */
		public Object set(int i, Object o) {
			write();
			return list.set(i, o);
		}
		
		/**
		 * @see java.util.Collection#size()
		 */
		public int size() {
			return list.size();
		}
		
		/**
		 * @see java.util.List#subList(int, int)
		 */
		public List subList(int i, int j) {
			return list.subList(i, j);
		}
		
		/**
		 * @see java.util.Collection#toArray()
		 */
		public Object[] toArray() {
			return list.toArray();
		}
		
		/**
		 * @see java.util.Collection#toArray(Object[])
		 */
		public Object[] toArray(Object[] array) {
			return list.toArray(array);
		}
		
	}
	
	
	protected static Collection getOrphans(Collection oldElements, Collection currentElements, SessionImplementor session) 
	throws HibernateException {
		
		// short-circuit(s)
		if ( currentElements.size()==0 ) return oldElements; // no new elements, the old list contains only Orphans
		if ( oldElements.size()==0) return oldElements; // no old elements, so no Orphans neither
		
		// create the collection holding the Orphans
		Collection res = new ArrayList();
		
		// collect EntityIdentifier(s) of the *current* elements - add them into a HashSet for fast access
		java.util.Set currentIds = new HashSet();
		for ( Iterator it=currentElements.iterator(); it.hasNext(); ) {
			Object current = it.next();
			if ( current!=null && session.isSaved(current) ) {
				currentIds.add( session.getEntityIdentifierIfNotUnsaved(current) );
			}
		}
		
		// iterate over the *old* list
		for ( Iterator it=oldElements.iterator(); it.hasNext(); ) {
			Object old = it.next();
			Object id = session.getEntityIdentifierIfNotUnsaved(old);
			if ( !currentIds.contains(id) ) res.add(old);
		}
		
		return res;
	}
		
	static void identityRemove(Collection list, Object object, SessionImplementor session) 
	throws HibernateException {
		
		if ( object!=null && session.isSaved(object) ) {
		
			Serializable idOfCurrent = session.getEntityIdentifierIfNotUnsaved(object);
			Iterator iter = list.iterator();
			while ( iter.hasNext() ) {
				Serializable idOfOld = session.getEntityIdentifierIfNotUnsaved( iter.next() );
				if ( idOfCurrent.equals(idOfOld) ) {
					iter.remove();
					break;
				}
			}
			
		}
	}
}






