//$Id: List.java,v 1.11 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.mapping;

import net.sf.hibernate.type.PersistentCollectionType;
import net.sf.hibernate.type.TypeFactory;

/**
 * A list mapping has a primary key consisting of
 * the key columns + index column.
 * @author Gavin King
 */
public class List extends IndexedCollection {
	
	public List(PersistentClass owner) {
		super(owner);
	}

	public PersistentCollectionType getCollectionType() {
		return TypeFactory.list( getRole() );
	}
	
}







