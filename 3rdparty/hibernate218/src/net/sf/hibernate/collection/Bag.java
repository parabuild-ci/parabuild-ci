//$Id: Bag.java,v 1.21 2004/07/22 09:46:47 epbernard Exp $
package net.sf.hibernate.collection;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.odmg.DBag;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.type.Type;

/**
 * An unordered, unkeyed collection that can contain the same element
 * multiple times. The Java collections API, curiously, has no <tt>Bag</tt>.
 * Most developers seem to use <tt>List</tt>s to represent bag semantics,
 * so Hibernate follows this practice.
 * 
 * @author Gavin King
 */
public class Bag extends ODMGCollection implements DBag, java.util.List {
	
	private java.util.List bag;
	
	public Bag(SessionImplementor session) {
		super(session);
	}
	
	public Bag(SessionImplementor session, java.util.Collection coll) {
		super(session);
		if (coll instanceof java.util.List) {
			bag = (java.util.List) coll;
		}
		else {
			bag = new ArrayList();
			Iterator iter = coll.iterator();
			while ( iter.hasNext() ) {
				bag.add( iter.next() );
			}
		}
		setInitialized();
		setDirectlyAccessible(true);
	}
	
	public Bag() {} //needed for SOAP libraries, etc

	public boolean isWrapper(Object collection) {
		return bag==collection;
	}
	public boolean empty() {
		return bag.isEmpty();
	}
	
	/**
	 * @see PersistentCollection#entries()
	 */
	public Iterator entries() {
		return bag.iterator();
	}
	
	
	/**
	 * @see PersistentCollection#readFrom(ResultSet, CollectionPersister, Object)
	 */
	public Object readFrom(ResultSet rs, CollectionPersister persister, Object owner) throws HibernateException, SQLException {
		// note that if we load this collection from a cartesian product
		// the multiplicity would be broken ... so use an idbag instead
		Object element = persister.readElement( rs, owner, getSession() ) ;
		bag.add(element);
		return element;
	}
	
	/**
	 * @see PersistentCollection#writeTo(PreparedStatement, CollectionPersister, Object, int, boolean)
	 */
	public void writeTo(PreparedStatement st, CollectionPersister persister, Object entry, int i, boolean writeOrder)
	throws HibernateException, SQLException {
		persister.writeElement( st, entry, writeOrder, getSession() );
	}
	
	/**
	 * @see PersistentCollection#beforeInitialize(CollectionPersister persister)
	 */
	public void beforeInitialize(CollectionPersister persister) {
		this.bag = new ArrayList();
	}
	
	/**
	 * @see PersistentCollection#equalsSnapshot(Type elementType)
	 */
	public boolean equalsSnapshot(Type elementType) throws HibernateException {
		java.util.List sn = (java.util.List) getSnapshot();
		if ( sn.size()!=bag.size() ) return false;
		Iterator iter = bag.iterator();
		while ( iter.hasNext() ) {
			Object elt = iter.next();
			if ( countOccurrences(elt, bag, elementType)!=countOccurrences(elt, sn, elementType) ) return false;
		}
		return true;
	}
	
	private int countOccurrences(Object element, java.util.List list, Type elementType) throws HibernateException {
		Iterator iter = list.iterator();
		int result=0;
		while ( iter.hasNext() ) {
			if ( elementType.equals( element, iter.next() ) ) result++;
		}
		return result;
	}
	
	
	/**
	 * @see PersistentCollection#snapshot(CollectionPersister persister)
	 */
	protected Serializable snapshot(CollectionPersister persister)
	throws HibernateException {
		ArrayList clonedList = new ArrayList( bag.size() );
		Iterator iter = bag.iterator();
		while ( iter.hasNext() ) {
			clonedList.add( persister.getElementType().deepCopy( iter.next() ) );
		}
		return clonedList;
	}
	
	public Collection getOrphans(Serializable snapshot) throws HibernateException {
	    java.util.List sn = (java.util.List) snapshot;
	    return PersistentCollection.getOrphans( sn, bag, getSession() );
	}
	
	
	/**
	 * @see PersistentCollection#disassemble(CollectionPersister persister)
	 */
	public Serializable disassemble(CollectionPersister persister)
	throws HibernateException {
		
		int length = bag.size();
		Serializable[] result = new Serializable[length];
		for ( int i=0; i<length; i++ ) {
			result[i] = persister.getElementType().disassemble( bag.get(i), getSession() );
		}
		return result;
	}
	
	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner) 
	throws HibernateException {
		beforeInitialize(persister);
		Serializable[] array = (Serializable[]) disassembled;
		for ( int i=0; i<array.length; i++ ) {
			bag.add( persister.getElementType().assemble( array[i], getSession(), owner ) );
		}
		setInitialized();
	}
	
	public boolean needsRecreate(CollectionPersister persister) {
		return !persister.isOneToMany();
	}
	
	
	// For a one-to-many, a <bag> is not really a bag;
	// it is *really* a set, since it can't contain the
	// same element twice. It could be considered a bug
	// in the mapping dtd that <bag> allows <one-to-many>.
		
	// Anyway, here we implement <set> semantics for a
	// <one-to-many> <bag>!
		
	public Iterator getDeletes(Type elemType) throws HibernateException {
		//if ( !persister.isOneToMany() ) throw new AssertionFailure("Not implemented for Bags");
		ArrayList deletes = new ArrayList();
		java.util.List sn = (java.util.List) getSnapshot();
		Iterator olditer = sn.iterator();
		int i=0;
		while ( olditer.hasNext() ) {
			Object old = olditer.next();
			Iterator newiter = bag.iterator();
			boolean found = false;
			if ( bag.size()>i && elemType.equals( old, bag.get(i++) ) ) {
			//a shortcut if its location didn't change!
				found = true; 
			}
			else {
				//search for it
				//note that this code is incorrect for other than one-to-many
				while ( newiter.hasNext() ) {
					if ( elemType.equals( old, newiter.next() ) ) {
						found = true;
						break;
					}
				}
			}
			if (!found) deletes.add(old);
		}
		return deletes.iterator();
	}
	
	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
		//if ( !persister.isOneToMany() ) throw new AssertionFailure("Not implemented for Bags");
		java.util.List sn = (java.util.List) getSnapshot();
		if ( sn.size()>i && elemType.equals( sn.get(i), entry ) ) {
			//a shortcut if its location didn't change!
			return false;
		}
		else {
			//search for it
			//note that this code is incorrect for other than one-to-many
			Iterator olditer = sn.iterator();
			while ( olditer.hasNext() ) {
				Object old = olditer.next();
				if ( elemType.equals(old, entry) ) return false;
			}
			return true;
		}
	}
	
	public boolean needsUpdating(Object entry, int i, Type elemType) {
		//if ( !persister.isOneToMany() ) throw new AssertionFailure("Not implemented for Bags");
		return false;
	}
	
	/**
	 * @see java.util.Collection#size()
	 */
	public int size() {
		read();
		return bag.size();
	}
	
	/**
	 * @see java.util.Collection#isEmpty()
	 */
	public boolean isEmpty() {
		read();
		return bag.isEmpty();
	}
	
	/**
	 * @see java.util.Collection#contains(Object)
	 */
	public boolean contains(Object o) {
		read();
		return bag.contains(o);
	}
	
	/**
	 * @see java.util.Collection#iterator()
	 */
	public Iterator iterator() {
		read();
		return new IteratorProxy( bag.iterator() );
	}
	
	/**
	 * @see java.util.Collection#toArray()
	 */
	public Object[] toArray() {
		read();
		return bag.toArray();
	}
	
	/**
	 * @see java.util.Collection#toArray(Object[])
	 */
	public Object[] toArray(Object[] a) {
		read();
		return bag.toArray(a);
	}
	
	/**
	 * @see java.util.Collection#add(Object)
	 */
	public boolean add(Object o) {
		if ( !queueAdd(o) ) {
			write();
			return bag.add(o);
		}
		else {
			return true;
		}
	}
	
	/**
	 * @see java.util.Collection#remove(Object)
	 */
	public boolean remove(Object o) {
		write();
		return bag.remove(o);
	}
	
	/**
	 * @see java.util.Collection#containsAll(Collection)
	 */
	public boolean containsAll(Collection c) {
		read();
		return bag.containsAll(c);
	}
	
	/**
	 * @see java.util.Collection#addAll(Collection)
	 */
	public boolean addAll(Collection c) {
		if ( c.size()==0 ) return false;
		if ( !queueAddAll(c) ) {
			write();
			return bag.addAll(c);
		}
		else {
			return c.size()>0;
		}
	}
	
	public void delayedAddAll(Collection c) {
		bag.addAll(c);
	}
	
	/**
	 * @see java.util.Collection#removeAll(Collection)
	 */
	public boolean removeAll(Collection c) {
		if ( c.size()>0 ) {
			write();
			return bag.removeAll(c);
		}
		else {
			return false;
		}
	}
	
	/**
	 * @see java.util.Collection#retainAll(Collection)
	 */
	public boolean retainAll(Collection c) {
		write();
		return bag.retainAll(c);
	}
	
	/**
	 * @see java.util.Collection#clear()
	 */
	public void clear() {
		write();
		bag.clear();
	}
	
	/**
	 * @see PersistentCollection#getIndex(Object, int)
	 */
	public Object getIndex(Object entry, int i) {
		throw new UnsupportedOperationException("Bags don't have indexes");
	}
	
	public DBag difference(DBag otherBag) {
		//TODO: perhaps not correct semantics - is it supposed to be symmetric?
		read();
		ArrayList list = new ArrayList( this.bag.size() );
		list.addAll(bag);
		list.removeAll(otherBag);
		return new Bag(getSession(), list);
	}
	
	public DBag intersection(DBag otherBag) {
		read();
		ArrayList list = new ArrayList( this.bag.size() );
		list.addAll(bag);
		list.retainAll(otherBag);
		return new Bag( getSession(), list );
	}
	
	public int occurrences(Object o) {
		read();
		Iterator iter = bag.iterator();
		int result=0;
		while ( iter.hasNext() ) {
			if ( o.equals( iter.next() ) ) result++;
		}
		return result;
	}
	
	public DBag union(DBag otherBag) {
		read();
		ArrayList list = new ArrayList( this.bag.size() + otherBag.size() );
		list.addAll(otherBag);
		list.addAll(bag);
		return new Bag( getSession(), list );
	}
	
	// List OPERATIONS:
	
	/**
	 * @see java.util.List#add(int, Object)
	 */
	public void add(int i, Object o) {
		write();
		bag.add(i, o);
	}
	
	/**
	 * @see java.util.List#addAll(int, Collection)
	 */
	public boolean addAll(int i, Collection c) {
		if ( c.size()>0 ) {
			write();
			return bag.addAll(i, c);
		}
		else {
			return false;
		}
	}
	
	/**
	 * @see java.util.List#get(int)
	 */
	public Object get(int i) {
		read();
		return bag.get(i);
	}
	
	/**
	 * @see java.util.List#indexOf(Object)
	 */
	public int indexOf(Object o) {
		read();
		return bag.indexOf(o);
	}
	
	/**
	 * @see java.util.List#lastIndexOf(Object)
	 */
	public int lastIndexOf(Object o) {
		read();
		return bag.lastIndexOf(o);
	}
	
	/**
	 * @see java.util.List#listIterator()
	 */
	public ListIterator listIterator() {
		read();
		return new ListIteratorProxy( bag.listIterator() );
	}
	
	/**
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator listIterator(int i) {
		read();
		return new ListIteratorProxy( bag.listIterator(i) );
	}
	
	/**
	 * @see java.util.List#remove(int)
	 */
	public Object remove(int i) {
		write();
		return bag.remove(i);
	}
	
	/**
	 * @see java.util.List#set(int, Object)
	 */
	public Object set(int i, Object o) {
		write();
		return bag.set(i, o);
	}
	
	/**
	 * @see java.util.List#subList(int, int)
	 */
	public List subList(int start, int end) {
		read();
		return new ListProxy( bag.subList(start, end) );
	}
	
	public String toString() {
		read();
		return bag.toString();
	}
	
	/*public boolean equals(Object other) {
		read();
		return bag.equals(other);
	}
	
	public int hashCode(Object other) {
		read();
		return bag.hashCode();
	}*/
	
	/**
	 * @see PersistentCollection#entryExists(java.lang.Object, int)
	 */
	public boolean entryExists(Object entry, int i) {
		return entry!=null;
	}

	/**
	 * Bag does not respect the collection API and do an
	 * JVM instance comparison to do the equals.
	 * The semantic is broken not to have to initialize a
	 * collection for a simple equals() operation.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return super.hashCode();
	}

}






