//$Id: QueryableCollection.java,v 1.6 2004/06/04 05:43:45 steveebersole Exp $
package net.sf.hibernate.collection;

import net.sf.hibernate.persister.ClassPersister;
import net.sf.hibernate.persister.Joinable;
import net.sf.hibernate.persister.PropertyMapping;

/**
 * A collection role that may be queried or loaded by outer join.
 * @author Gavin King
 */
public interface QueryableCollection extends PropertyMapping, Joinable, CollectionPersister {
	/**
	 * Generate a list of collection index and element columns
	 */
	public abstract String selectFragment(String alias);
	/**
	 * Get the names of the collection index columnsm if
	 * this is an indexed collection (optional operation)
	 */
	public abstract String[] getIndexColumnNames();
	/**
	 * Get the names of the collection element columns (or the primary 
	 * key columns in the case of a one-to-many association)
	 */
	public abstract String[] getElementColumnNames();
	/**
	 * Get the names of the collection key columns
	 */
	public abstract String[] getKeyColumnNames();
	/**
	 * Get the extra where clause filter SQL
	 */
	public abstract String getSQLWhereString(String alias);
	/**
	 * Get the order by SQL
	 */
	public abstract String getSQLOrderByString(String alias);
	/**
	 * Does this collection role have a where clause filter?
	 */
	public abstract boolean hasWhere();
	/**
	 * Get the persister of the element class, if this is a
	 * collection of entities (optional operation).  Note that
	 * for a one-to-many association, the returned persister
	 * must be <tt>OuterJoinLoadable</tt>.
	 */
	public abstract ClassPersister getElementPersister();
	/**
	 * Should we load this collection role by outerjoining?
	 */
	public abstract int enableJoinedFetch();

}
