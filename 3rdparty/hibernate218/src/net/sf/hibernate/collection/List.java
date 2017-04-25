//$Id: List.java,v 1.21 2004/06/04 01:27:37 steveebersole Exp $
package net.sf.hibernate.collection;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Collection;
import java.util.ListIterator;
import java.util.ArrayList;

import org.odmg.DArray;
import org.odmg.DList;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.type.Type;

/**
 * A persistent wrapper for a <tt>java.util.List</tt>. Underlying
 * collection is an <tt>ArrayList</tt>.
 * 
 * @see java.util.ArrayList
 * @author Gavin King
 */
public class List extends ODMGCollection implements java.util.List, DList, DArray {
	
	private java.util.List list;
	
	protected Serializable snapshot(CollectionPersister persister) throws HibernateException {
		ArrayList clonedList = new ArrayList( list.size() );
		Iterator iter = list.iterator();
		while ( iter.hasNext() ) {
			clonedList.add( persister.getElementType().deepCopy( iter.next() ) );
		}
		return clonedList;
	}
	
	public Collection getOrphans(Serializable snapshot) throws HibernateException {
	    java.util.List sn = (java.util.List) snapshot;
	    return PersistentCollection.getOrphans( sn, list, getSession() );
	}
	
	public boolean equalsSnapshot(Type elementType) throws HibernateException {
		java.util.List sn = (java.util.List) getSnapshot();
		if ( sn.size()!=this.list.size() ) return false;
		Iterator iter = list.iterator();
		Iterator sniter = sn.iterator();
		while ( iter.hasNext() ) {
			if ( elementType.isDirty( iter.next(), sniter.next(), getSession() ) ) return false;
		}
		return true;
	}
	
	public List(SessionImplementor session) {
		super(session);
	}
	
	public List(SessionImplementor session, java.util.List list) {
		super(session);
		this.list = list;
		setInitialized();
		setDirectlyAccessible(true);
	}
	public void beforeInitialize(CollectionPersister persister) {
		this.list = new ArrayList();
	}
	
	public boolean isWrapper(Object collection) {
		return list==collection;
	}
	public List() {} //needed for SOAP libraries, etc

	/**
	 * @see java.util.List#size()
	 */
	public int size() {
		read();
		return list.size();
	}
	
	/**
	 * @see java.util.List#isEmpty()
	 */
	public boolean isEmpty() {
		read();
		return list.isEmpty();
	}
	
	/**
	 * @see java.util.List#contains(Object)
	 */
	public boolean contains(Object object) {
		read();
		return list.contains(object);
	}
	
	/**
	 * @see java.util.List#iterator()
	 */
	public Iterator iterator() {
		read();
		return new IteratorProxy( list.iterator() );
	}
	
	/**
	 * @see java.util.List#toArray()
	 */
	public Object[] toArray() {
		read();
		return list.toArray();
	}
	
	/**
	 * @see java.util.List#toArray(Object[])
	 */
	public Object[] toArray(Object[] array) {
		read();
		return list.toArray(array);
	}
	
	/**
	 * @see java.util.List#add(Object)
	 */
	public boolean add(Object object) {
		if ( !queueAdd(object) ) {
			write();
			return list.add(object);
		}
		else {
			return true;
		}
	}
	
	/**
	 * @see java.util.List#remove(Object)
	 */
	public boolean remove(Object value) {
		write();
		return list.remove(value);
	}
	
	/**
	 * @see java.util.List#containsAll(Collection)
	 */
	public boolean containsAll(Collection coll) {
		read();
		return list.containsAll(coll);
	}
	
	/**
	 * @see java.util.List#addAll(Collection)
	 */
	public boolean addAll(Collection c) {
		if ( c.size()==0 ) return false;
		if ( !queueAddAll(c) ) {
			write();
			return list.addAll(c);
		}
		else {
			return c.size()>0;
		}
	}
	
	public void delayedAddAll(Collection c) {
		list.addAll(c);
	}
	
	/**
	 * @see java.util.List#addAll(int, Collection)
	 */
	public boolean addAll(int index, Collection coll) {
		if ( coll.size()>0 ) {
			write();
			return list.addAll(index,  coll);
		}
		else {
			return false;
		}
	}
	
	/**
	 * @see java.util.List#removeAll(Collection)
	 */
	public boolean removeAll(Collection coll) {
		if ( coll.size()>0 ) {
			write();
			return list.removeAll(coll);
		}
		else {
			return false;
		}
	}
	
	/**
	 * @see java.util.List#retainAll(Collection)
	 */
	public boolean retainAll(Collection coll) {
		write();
		return list.retainAll(coll);
	}
	
	/**
	 * @see java.util.List#clear()
	 */
	public void clear() {
		write();
		list.clear();
	}
	
	/**
	 * @see java.util.List#get(int)
	 */
	public Object get(int index) {
		read();
		return list.get(index);
	}
	
	/**
	 * @see java.util.List#set(int, Object)
	 */
	public Object set(int index, Object value) {
		write();
		return list.set(index, value);
	}
	
	/**
	 * @see java.util.List#add(int, Object)
	 */
	public void add(int index, Object value) {
		write();
		list.add(index, value);
	}
	
	/**
	 * @see java.util.List#remove(int)
	 */
	public Object remove(int index) {
		write();
		return list.remove(index);
	}
	
	/**
	 * @see java.util.List#indexOf(Object)
	 */
	public int indexOf(Object value) {
		read();
		return list.indexOf(value);
	}
	
	/**
	 * @see java.util.List#lastIndexOf(Object)
	 */
	public int lastIndexOf(Object value) {
		read();
		return list.lastIndexOf(value);
	}
	
	/**
	 * @see java.util.List#listIterator()
	 */
	public ListIterator listIterator() {
		read();
		return new ListIteratorProxy( list.listIterator() );
	}
	
	/**
	 * @see java.util.List#listIterator(int)
	 */
	public ListIterator listIterator(int index) {
		read();
		return new ListIteratorProxy( list.listIterator(index) );
	}
	
	/**
	 * @see java.util.List#subList(int, int)
	 */
	public java.util.List subList(int from, int to) {
		read();
		return new ListProxy( list.subList(from, to) );
	}

	public boolean empty() {
		return list.isEmpty();
	}
	
	public String toString() {
		read();
		return list.toString();
	}
	
	/**
	 * @see PersistentCollection#writeTo(PreparedStatement, CollectionPersister, Object, int, boolean)
	 */
	public void writeTo(PreparedStatement st, CollectionPersister persister, Object entry, int i, boolean writeOrder)
	throws HibernateException, SQLException {
		
		persister.writeElement( st, entry, writeOrder, getSession() );
		persister.writeIndex( st, new Integer(i), writeOrder, getSession() );
	}
	
	/**
	 * @see PersistentCollection#readFrom(ResultSet, CollectionPersister, Object)
	 */
	public Object readFrom(ResultSet rs, CollectionPersister persister, Object owner) throws HibernateException, SQLException {
		Object element = persister.readElement( rs, owner, getSession() ) ;
		int index = ( (Integer) persister.readIndex( rs, getSession() ) ).intValue();
		for ( int i = list.size(); i<=index; i++) {
			list.add(i, null);
		}
		list.set(index, element);
		return element;
	}
	
	/**
	 * @see PersistentCollection#entries()
	 */
	public Iterator entries() {
		return list.iterator();
	}
	
	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner) 
	throws HibernateException {
		beforeInitialize(persister);
		Serializable[] array = (Serializable[]) disassembled;
		for ( int i=0; i<array.length; i++ ) {
			list.add( persister.getElementType().assemble( array[i], getSession(), owner ) );
		}
		setInitialized();
	}
	
	public Serializable disassemble(CollectionPersister persister)
	throws HibernateException {
		
		int length = list.size();
		Serializable[] result = new Serializable[length];
		for ( int i=0; i<length; i++ ) {
			result[i] = persister.getElementType().disassemble( list.get(i), getSession() );
		}
		return result;
	}

	
	public Iterator getDeletes(Type elemType) throws HibernateException {
		java.util.List deletes = new ArrayList();
		java.util.List sn = (java.util.List) getSnapshot();
		int end;
		if ( sn.size() > list.size() ) {
			for ( int i=list.size(); i<sn.size(); i++ ) deletes.add( new Integer(i) );
			end = list.size();
		}
		else {
			end = sn.size();
		}
		for ( int i=0; i<end; i++ ) {
			if ( list.get(i)==null && sn.get(i)!=null ) deletes.add( new Integer(i) );
		}
		return deletes.iterator();
	}
	
	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
		final java.util.List sn = (java.util.List) getSnapshot();
		return list.get(i)!=null && ( i >= sn.size() || sn.get(i)==null );
	}
	
	public boolean needsUpdating(Object entry, int i, Type elemType) throws HibernateException {
		final java.util.List sn = (java.util.List) getSnapshot();
		return i<sn.size() && sn.get(i)!=null && list.get(i)!=null && elemType.isDirty( list.get(i), sn.get(i), getSession() );
	}
	
	/**
	 * @see PersistentCollection#getIndex(Object, int)
	 */
	public Object getIndex(Object entry, int i) {
		return new Integer(i);
	}
	
	/**
	 * @see org.odmg.DList#concat(DList)
	 */
	public DList concat(DList arglist) {
		read();
		ArrayList tempList = new ArrayList( arglist.size() + this.list.size() );
		tempList.addAll(this.list);
		tempList.addAll(arglist);
		return new List(getSession(), tempList);
	}
	
	/**
	 * @see org.odmg.DArray#resize(int)
	 */
	public void resize(int length) {
		//TODO: is this correct semantics?
		write();
		ArrayList newlist = new ArrayList(length);
		Iterator iter = list.iterator();
		int i=0;
		while ( iter.hasNext() ) {
			if (++i>length) break;
			newlist.add( iter.next() );
		}
		this.list = newlist;
	}
	
	public boolean equals(Object other) {
		read();
		return list.equals(other);
	}
	
	public int hashCode() {
		read();
		return list.hashCode();
	}
	
	/**
	 * @see net.sf.hibernate.collection.PersistentCollection#entryExists(java.lang.Object, int)
	 */
	public boolean entryExists(Object entry, int i) {
		return entry!=null;
	}

}







