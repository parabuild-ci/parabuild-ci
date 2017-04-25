//$Id: SortedSet.java,v 1.15 2004/06/04 01:27:37 steveebersole Exp $
package net.sf.hibernate.collection;

import java.io.Serializable;
import java.util.Iterator;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Comparator;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;


/**
 * A persistent wrapper for a <tt>java.util.SortedSet</tt>. Underlying
 * collection is a <tt>TreeSet</tt>.
 * 
 * @see java.util.TreeSet
 * @author <a href="mailto:doug.currie@alum.mit.edu">e</a>
 */
public class SortedSet extends Set implements java.util.SortedSet {
	
	private Comparator comparator;
	
	public Serializable snapshot(BasicCollectionPersister persister) throws HibernateException {
		//if (set==null) return new Set(session);
		TreeMap clonedSet = new TreeMap(comparator);
		Iterator iter = set.iterator();
		while ( iter.hasNext() ) {
			Object copy = persister.getElementType().deepCopy( iter.next() );
			clonedSet.put(copy, copy);
		}
		return clonedSet;
	}
	
	public void setComparator(Comparator comparator) {
		this.comparator = comparator;
	}
	
	public void beforeInitialize(CollectionPersister persister) {
		this.set = new TreeSet(comparator);
	}
	
	public SortedSet(SessionImplementor session) {
		super(session);
	}
	
	public SortedSet(SessionImplementor session, java.util.SortedSet set) {
		super(session, set);
		comparator = set.comparator();
	}
	
	public SortedSet() {} //needed for SOAP libraries, etc

	/**
	 * @see SortedSet#comparator()
	 */
	public Comparator comparator() {
		return comparator;
	}
	
	/**
	 * @see SortedSet#subSet(Object,Object)
	 */
	public java.util.SortedSet subSet(Object fromElement, Object toElement) {
		read();
		java.util.SortedSet s;
		s = ( (java.util.SortedSet) set ).subSet(fromElement, toElement);
		return new SubSetProxy(s);
	}
	
	/**
	 * @see SortedSet#headSet(Object)
	 */
	public java.util.SortedSet headSet(Object toElement) {
		read();
		java.util.SortedSet s = ( (java.util.SortedSet) set ).headSet(toElement);
		return new SubSetProxy(s);
	}
	
	/**
	 * @see SortedSet#tailSet(Object)
	 */
	public java.util.SortedSet tailSet(Object fromElement) {
		read();
		java.util.SortedSet s = ( (java.util.SortedSet) set ).tailSet(fromElement);
		return new SubSetProxy(s);
	}
	
	/**
	 * @see SortedSet#first()
	 */
	public Object first() {
		read();
		return ( (java.util.SortedSet) set ).first();
	}
	
	/**
	 * @see SortedSet#last()
	 */
	public Object last() {
		read();
		return ( (java.util.SortedSet) set ).last();
	}
	
	/** wrapper for subSets to propagate write to its backing set */
	class SubSetProxy extends SetProxy implements java.util.SortedSet {
		
		SubSetProxy(java.util.SortedSet s) {
			super(s);
		}
		
		public Comparator comparator() {
			return ( (java.util.SortedSet) this.set ).comparator();
		}
		
		public Object first() {
			return ( (java.util.SortedSet) this.set ).first();
		}
		
		public java.util.SortedSet headSet(Object toValue) {
			return new SubSetProxy( ( (java.util.SortedSet) this.set ).headSet(toValue) );
		}
		
		public Object last() {
			return ( (java.util.SortedSet) this.set ).last();
		}
		
		public java.util.SortedSet subSet(Object fromValue, Object toValue) {
			return new SubSetProxy( ( (java.util.SortedSet) this.set ).subSet(fromValue, toValue) );
		}
		
		public java.util.SortedSet tailSet(Object fromValue) {
			return new SubSetProxy( ( (java.util.SortedSet) this.set ).tailSet(fromValue) );
		}
		
	}

	public SortedSet(
		SessionImplementor session,
		CollectionPersister persister,
		Comparator comparator,
		Serializable disassembled, 
		Object owner)
		throws HibernateException {
		
		this(session);
		this.comparator=comparator;
		beforeInitialize(persister);
		Serializable[] array = (Serializable[]) disassembled;
		for (int i=0; i<array.length; i++ ) set.add(
			persister.getElementType().assemble( array[i], session, owner )
		);
		setInitialized();
	}

}







