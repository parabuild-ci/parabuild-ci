//$Id: ArrayHolder.java,v 1.20 2004/06/04 01:27:37 steveebersole Exp $
package net.sf.hibernate.collection;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.type.Type;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A persistent wrapper for an array. Lazy initialization
 * is NOT supported. Use of Hibernate arrays is not really
 * recommended.
 * 
 * @author Gavin King
 */
public class ArrayHolder extends PersistentCollection {
	private Object array;
	
	private static final Log log = LogFactory.getLog(PersistentCollection.class);
	
	//just to help out during the load (ugly, i know)
	private transient Class elementClass;
	private transient java.util.List tempList;
	
	public ArrayHolder(SessionImplementor session, Object array) {
		super(session);
		this.array = array;
		setInitialized();
	}
	
	protected Serializable snapshot(CollectionPersister persister) throws HibernateException {
		int length = /*(array==null) ? tempList.size() :*/ Array.getLength(array);
		Serializable result = (Serializable) Array.newInstance( persister.getElementClass(), length );
		for ( int i=0; i<length; i++ ) {
			Object elt = /*(array==null) ? tempList.get(i) :*/ Array.get(array, i);
			try {
				Array.set( result, i, persister.getElementType().deepCopy(elt) );
			}
			catch (IllegalArgumentException iae) {
				log.error("Array element type error", iae);
				throw new HibernateException( "Array element type error", iae );
			}
		}
		return result;
	}
	
	public Collection getOrphans(Serializable snapshot) throws HibernateException {
		Object[] sn = (Object[]) snapshot;
		Object[] arr = (Object[]) array;
		ArrayList result = new ArrayList();
		for (int i=0; i<sn.length; i++) result.add( sn[i] );
		for (int i=0; i<sn.length; i++) PersistentCollection.identityRemove( result, arr[i], getSession() );
		return result;
	}
	
	public ArrayHolder(SessionImplementor session, CollectionPersister persister) throws HibernateException {
		super(session);
		elementClass = persister.getElementClass();
	}

	public Object getArray() {
		return array;
	}
	
	public boolean isWrapper(Object collection) {
		return array==collection;
	}

	public boolean equalsSnapshot(Type elementType) throws HibernateException {
		Serializable snapshot = getSnapshot();
		int xlen = Array.getLength(snapshot);
		if ( xlen!= Array.getLength(array) ) return false;
		for ( int i=0; i<xlen; i++) {
			if ( elementType.isDirty( Array.get(snapshot, i), Array.get(array, i), getSession() ) ) return false;
		}
		return true;
	}
	
	/**
	 * @see PersistentCollection#entries
	 */
	public Iterator elements() {
		//if (array==null) return tempList.iterator();
		int length = Array.getLength(array);
		java.util.List list = new ArrayList(length);
		for (int i=0; i<length; i++) {
			list.add( Array.get(array, i) );
		}
		return list.iterator();
	}
	public boolean empty() {
		return false;
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
	public Object readFrom(ResultSet rs, CollectionPersister persister, Object owner)
	throws HibernateException, SQLException {
		
		// relies on the fact that elements are returned sorted by index! (obsolete comment, I think?)
		
		Object element = persister.readElement( rs, owner, getSession() );
		int index = ( (Integer) persister.readIndex( rs, getSession() ) ).intValue();
		for ( int i = tempList.size(); i<=index; i++) {
			tempList.add(i, null);
		}
		tempList.set(index, element);
		return element;
	}
	
	/**
	 * @see PersistentCollection#entries()
	 */
	public Iterator entries() {
		return elements();
	}
	
	public void beginRead() {
		super.beginRead();
		tempList = new ArrayList();
	}
	public boolean endRead() {
		setInitialized();
		array = Array.newInstance( elementClass, tempList.size() );
		for ( int i=0; i<tempList.size(); i++) {
			Array.set(array, i, tempList.get(i) );
		}
		tempList=null;
		return true;
	}
	
	public void beforeInitialize(CollectionPersister persister) {
		//if (tempList==null) throw new UnsupportedOperationException("Can't lazily initialize arrays");
	}
	
	public boolean isDirectlyAccessible() {
		return true;
	}
	
	public void initializeFromCache(CollectionPersister persister, Serializable disassembled, Object owner) 
	throws HibernateException {
		Serializable[] cached = (Serializable[]) disassembled;
		
		array = Array.newInstance( persister.getElementClass(), cached.length );
		
		for ( int i=0; i<cached.length; i++ ) {
			Array.set( array, i, persister.getElementType().assemble(cached[i], getSession(), owner) );
		}
		setInitialized();
	}
	
	public Serializable disassemble(CollectionPersister persister) throws HibernateException {
		int length = Array.getLength(array);
		Serializable[] result = new Serializable[length];
		for ( int i=0; i<length; i++ ) {
			result[i] = persister.getElementType().disassemble( Array.get(array,i), getSession() );
		}
		
		/*int length = tempList.size();
		Serializable[] result = new Serializable[length];
		for ( int i=0; i<length; i++ ) {
			result[i] = persister.getElementType().disassemble( tempList.get(i), session );
		}*/
		
		return result;
		
	}
	
	public Object getValue() {
		return array;
	}
	
	public Iterator getDeletes(Type elemType) throws HibernateException {
		java.util.List deletes = new ArrayList();
		Serializable sn = getSnapshot();
		int snSize = Array.getLength(sn);
		int arraySize = Array.getLength(array);
		int end;
		if ( snSize > arraySize ) {
			for ( int i=arraySize; i<snSize; i++ ) deletes.add( new Integer(i) );
			end = arraySize;
		}
		else {
			end = snSize;
		}
		for ( int i=0; i<end; i++ ) {
			if ( Array.get(array, i)==null && Array.get(sn, i)!=null ) deletes.add( new Integer(i) );
		}
		return deletes.iterator();
	}
	
	public boolean needsInserting(Object entry, int i, Type elemType) throws HibernateException {
		Serializable sn = getSnapshot();
		return Array.get(array, i)!=null && ( i >= Array.getLength(sn) || Array.get(sn, i)==null );
	}
	
	public boolean needsUpdating(Object entry, int i, Type elemType) throws HibernateException {
		Serializable sn = getSnapshot();
		return i<Array.getLength(sn) && 
			Array.get(sn, i)!=null && 
			Array.get(array, i)!=null && 
			elemType.isDirty( Array.get(array, i), Array.get(sn, i), getSession() );
	}
	/**
	 * @see PersistentCollection#getIndex(Object, int)
	 */
	public Object getIndex(Object entry, int i) {
		return new Integer(i);
	}
	
	/**
	 * @see PersistentCollection#entryExists(java.lang.Object, int)
	 */
	public boolean entryExists(Object entry, int i) {
		return entry!=null;
	}

}







