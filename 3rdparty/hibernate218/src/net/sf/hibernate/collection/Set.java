//$Id: Set.java,v 1.26 2004/06/04 01:27:37 steveebersole Exp $
package net.sf.hibernate.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.HashSet;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.odmg.DSet;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.util.LinkedHashCollectionHelper;
import net.sf.hibernate.type.Type;


/**
 * A persistent wrapper for a <tt>java.util.Set</tt>. The underlying
 * collection is a <tt>HashSet</tt>.
 * 
 * @see java.util.HashSet
 * @author Gavin King
 */
public class Set extends ODMGCollection implements java.util.Set, DSet {
	
	java.util.Set set;
	private transient java.util.List tempList;
	
	protected Serializable snapshot(CollectionPersister persister) throws HibernateException {
		//if (set==null) return new Set(session);
		HashMap clonedSet = new HashMap( set.size() );
		Iterator iter = set.iterator();
		while ( iter.hasNext() ) {
			Object copied = persister.getElementType().deepCopy( iter.next() );
			clonedSet.put(copied, copied);
		}
		return clonedSet;
	}
	
	public Collection getOrphans(Serializable snapshot) throws HibernateException {
		java.util.Map sn = (java.util.Map) snapshot;
		return PersistentCollection.getOrphans( sn.keySet(), set, getSession() );
	}
	
	public boolean equalsSnapshot(Type elementType) throws HibernateException {
		java.util.Map sn = (java.util.Map) getSnapshot();
		if ( sn.size()!=set.size() ) {
			return false;
		}
		else {
			Iterator iter = set.iterator();
			while ( iter.hasNext() ) {
				Object test = iter.next();
				Object oldValue = sn.get(test);
				if ( oldValue==null || elementType.isDirty( oldValue, test, getSession() ) ) return false;
			}
			return true;
		}
	}
	
	public Set(SessionImplementor session) {
		super(session);
	}
	
	public Set() {} //needed for SOAP libraries, etc
	
	public void beforeInitialize(CollectionPersister persister) {
		this.set = persister.hasOrdering() ? 
			LinkedHashCollectionHelper.createLinkedHashSet() : 
			new HashSet();
	}
	public Set(SessionImplementor session, java.util.Set set) {
		super(session);
		// Sets can be just a view of a part of another collection.
		// do we need to copy it to be sure it won't be changing
		// underneath us?
		// ie. this.set.addAll(set);
		this.set = set;
		setInitialized();
		setDirectlyAccessible(true);
	}

	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner) 
	throws HibernateException {
		beforeInitialize(persister);
		Serializable[] array = (Serializable[]) disassembled;
		for (int i=0; i<array.length; i++ ) set.add(
			persister.getElementType().assemble( array[i], getSession(), owner )
		);
		setInitialized();
	}

	public boolean empty() {
		return set.isEmpty();
	}
	
	/**
	 * @see java.util.Set#size()
	 */
	public int size() {
		read();
		return set.size();
	}
	
	/**
	 * @see java.util.Set#isEmpty()
	 */
	public boolean isEmpty() {
		read();
		return set.isEmpty();
	}
	
	/**
	 * @see java.util.Set#contains(Object)
	 */
	public boolean contains(Object o) {
		read();
		return set.contains(o);
	}
	
	/**
	 * @see java.util.Set#iterator()
	 */
	public Iterator iterator() {
		read();
		return new IteratorProxy( set.iterator() );
	}
	
	/**
	 * @see java.util.Set#toArray()
	 */
	public Object[] toArray() {
		read();
		return set.toArray();
	}
	
	/**
	 * @see java.util.Set#toArray(Object[])
	 */
	public Object[] toArray(Object[] array) {
		read();
		return set.toArray(array);
	}
	
	/**
	 * @see java.util.Set#add(Object)
	 */
	public boolean add(Object value) {
		write();
		return set.add(value);
	}
	
	/**
	 * @see java.util.Set#remove(Object)
	 */
	public boolean remove(Object value) {
		write();
		return set.remove(value);
	}
	
	/**
	 * @see java.util.Set#containsAll(Collection)
	 */
	public boolean containsAll(Collection coll) {
		read();
		return set.containsAll(coll);
	}
	
	/**
	 * @see java.util.Set#addAll(Collection)
	 */
	public boolean addAll(Collection coll) {
		if ( coll.size()> 0 ) {
			write();
			return set.addAll(coll);
		}
		else {
			return false;
		}
	}
	
	/**
	 * @see java.util.Set#retainAll(Collection)
	 */
	public boolean retainAll(Collection coll) {
		write();
		return set.retainAll(coll);
	}
	
	/**
	 * @see java.util.Set#removeAll(Collection)
	 */
	public boolean removeAll(Collection coll) {
		if ( coll.size()>0 ) {
			write();
			return set.removeAll(coll);
		}
		else {
			return false;
		}
	}
	
	/**
	 * @see java.util.Set#clear()
	 */
	public void clear() {
		write();
		set.clear();
	}
	
	public String toString() {
		read();
		return set.toString();
	}
	
	/**
	 * @see PersistentCollection#writeTo(PreparedStatement, CollectionPersister, Object, int, boolean)
	 */
	public void writeTo(PreparedStatement st, CollectionPersister persister, Object entry, int i, boolean writeOrder)
	throws HibernateException, SQLException {
		persister.writeElement( st, entry, writeOrder, getSession() );
	}
	
	/**
	 * @see PersistentCollection#readFrom(ResultSet, CollectionPersister, Object)
	 */
	public Object readFrom(ResultSet rs, CollectionPersister persister, Object owner) throws HibernateException, SQLException {
		Object element = persister.readElement( rs, owner, getSession() );
		tempList.add(element);
		return element;
	}
	
	public void beginRead() {
		super.beginRead();
		tempList = new ArrayList();
	}
	
	public boolean endRead() {
		set.addAll(tempList);
		tempList = null;
		setInitialized();
		return true;
	}
	
	/**
	 * @see PersistentCollection#entries()
	 */
	public Iterator entries() {
		return set.iterator();
	}
	
	public Serializable disassemble(CollectionPersister persister)
	throws HibernateException {
		
		Serializable[] result = new Serializable[ set.size() ];
		Iterator iter = set.iterator();
		int i=0;
		while ( iter.hasNext() ) {
			result[i++] = persister.getElementType().disassemble( iter.next(), getSession() );
		}
		return result;
		
	}	
	
	public Iterator getDeletes(Type elemType) throws HibernateException {
		final java.util.Map sn = (java.util.Map) getSnapshot();
		ArrayList deletes = new ArrayList( sn.size() );
		Iterator iter = sn.keySet().iterator();
		while ( iter.hasNext() ) {
			Object test = iter.next();
			if ( !set.contains(test) ) {
				// the element has been removed from the set
				deletes.add(test);
			}
		}
		iter = set.iterator();
		while ( iter.hasNext() ) {
			Object test = iter.next();
			Object oldValue = sn.get(test);
			if ( oldValue!=null && elemType.isDirty( test, oldValue, getSession() ) ) {
				// the element has changed
				deletes.add(oldValue);
			}
		}
		return deletes.iterator();
	}
	
	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
		final java.util.Map sn = (java.util.Map) getSnapshot();
		Object oldValue = sn.get(entry); 
		// note that it might be better to iterate the snapshot but this is safe,
		// assuming the user implements equals() properly, as required by the Set
		// contract!
		return oldValue==null || elemType.isDirty( oldValue, entry, getSession() );
	}
	
	public boolean needsUpdating(Object entry, int i, Type elemType) {
		return false;
	}
	
	
	public Object getIndex(Object entry, int i) {
		throw new UnsupportedOperationException("Sets don't have indexes");
	}
	
	/**
	 * @see org.odmg.DSet#union(DSet)
	 */
	public DSet union(DSet otherSet) {
		read();
		HashSet newset = new HashSet( this.set.size() );
		newset.addAll(this.set);
		newset.addAll(otherSet);
		return new Set(getSession(), newset);
	}
	
	/**
	 * @see org.odmg.DSet#difference(DSet)
	 */
	public DSet difference(DSet otherSet) {
		//TODO: perhaps not correct semantics - is it supposed to be symmetric?
		read();
		HashSet newset = new HashSet( this.set.size() );
		newset.addAll(this.set);
		newset.removeAll(otherSet);
		return new Set(getSession(), newset);
	}
	
	/**
	 * @see org.odmg.DSet#intersection(DSet)
	 */
	public DSet intersection(DSet otherSet) {
		read();
		HashSet newset = new HashSet( this.set.size() );
		newset.addAll(this.set);
		newset.retainAll(otherSet);
		return new Set(getSession(), newset);
	}
	
	/**
	 * @see org.odmg.DSet#properSubsetOf(DSet)
	 */
	public boolean properSubsetOf(DSet otherSet) {
		read();
		return otherSet.size() > this.set.size() && otherSet.containsAll(this.set);
	}
	
	/**
	 * @see org.odmg.DSet#properSupersetOf(DSet)
	 */
	public boolean properSupersetOf(DSet otherSet) {
		read();
		return this.set.size() > otherSet.size() && this.set.containsAll(otherSet);
	}
	
	/**
	 * @see org.odmg.DSet#subsetOf(DSet)
	 */
	public boolean subsetOf(DSet otherSet) {
		read();
		return otherSet.size() >= this.set.size() && otherSet.containsAll(this.set);
	}
	
	/**
	 * @see org.odmg.DSet#supersetOf(DSet)
	 */
	public boolean supersetOf(DSet otherSet) {
		read();
		return this.set.size() >= otherSet.size() && this.set.containsAll(otherSet);
	}
	
	public boolean equals(Object other) {
		read();
		return set.equals(other);
	}
	
	public int hashCode() {
		read();
		return set.hashCode();
	}
	
	/**
	 * @see PersistentCollection#entryExists(java.lang.Object, int)
	 */
	public boolean entryExists(Object key, int i) {
		return true;
	}

	public boolean isWrapper(Object collection) {
		return set==collection;
	}

}







