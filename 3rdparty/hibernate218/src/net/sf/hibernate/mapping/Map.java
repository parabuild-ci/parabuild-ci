//$Id: Map.java,v 1.13 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.mapping;

import net.sf.hibernate.type.PersistentCollectionType;
import net.sf.hibernate.type.TypeFactory;

/**
 * A map has a primary key consisting of
 * the key columns + index columns.
 */
public class Map extends IndexedCollection {
	
	public Map(PersistentClass owner) {
		super(owner);
	}

	public PersistentCollectionType getCollectionType() {
		return isSorted() ? 
			TypeFactory.sortedMap( getRole(), getComparator() ) : 
			TypeFactory.map( getRole() );
	}
	

	public void createAllKeys() {
		super.createAllKeys();
		if ( !isInverse() ) getIndex().createForeignKey();
	}

}







