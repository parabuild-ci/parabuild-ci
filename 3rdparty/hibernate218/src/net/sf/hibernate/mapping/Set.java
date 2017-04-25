//$Id: Set.java,v 1.10 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.mapping;

import java.util.Iterator;

import net.sf.hibernate.type.PersistentCollectionType;
import net.sf.hibernate.type.TypeFactory;

/**
 * A set with no nullable element columns. It will have a primary key 
 * consisting of all table columns (ie. key columns + element columns).
 * @author Gavin King
 */
public class Set extends Collection {
	
	/**
	 * Constructor for Set.
	 * @param owner
	 */
	public Set(PersistentClass owner) {
		super(owner);
	}

	public boolean isSet() {
		return true;
	}
	
	public PersistentCollectionType getCollectionType() {
		return isSorted() ? 
			TypeFactory.sortedSet( getRole(), getComparator() ) : 
			TypeFactory.set( getRole() );
	}
	
	void createPrimaryKey() {
		if ( !isOneToMany() ) {
			PrimaryKey pk = new PrimaryKey();
			pk.addColumns( getKey().getColumnIterator() );
			boolean nullable = false;
			Iterator iter = getElement().getColumnIterator();
			while ( iter.hasNext() ) {
				Column col = (Column) iter.next();
				//col.setNullable(false);
				if ( col.isNullable() ) nullable=true;
				pk.addColumn(col);
			}
			// Some databases (Postgres) will tolerate nullable
			// column in a primary key others (DB2) won't
			if (!nullable) getCollectionTable().setPrimaryKey(pk);
		}
		else {
			//create an index on the key columns??
		}
	}
	
}







