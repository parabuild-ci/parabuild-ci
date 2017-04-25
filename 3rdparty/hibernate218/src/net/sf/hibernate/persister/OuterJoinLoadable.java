//$Id: OuterJoinLoadable.java,v 1.6 2004/06/04 05:43:47 steveebersole Exp $
package net.sf.hibernate.persister;

import net.sf.hibernate.type.Type;

/**
 * A <tt>ClassPersister</tt> that may be loaded by outer join using
 * the <tt>OuterJoinLoader</tt> hierarchy and may be an element
 * of a one-to-many association.
 * 
 * @see net.sf.hibernate.loader.OuterJoinLoader
 * @author Gavin King
 */
public interface OuterJoinLoadable extends Loadable, Joinable {

	/**
	 * Get the names of columns used to persist the identifier
	 */
	public String[] getIdentifierColumnNames();

	/**
	 * Get the name of the column used as a discriminator
	 */
	public String getDiscriminatorColumnName();


	/**
	 * How many properties are there, for this class and all subclasses.
	 */
	public int countSubclassProperties();

	/**
	 * May this property be fetched using an SQL outerjoin.
	 */
	public int enableJoinedFetch(int i);

	/**
	 * Is this property defined on a subclass of the mapped class.
	 */
	public boolean isDefinedOnSubclass(int i);

	/**
	 * Get the type of the numbered property of the class or a subclass.
	 */
	public Type getSubclassPropertyType(int i);

	/**
	 * Get the name of the numbered property of the class or a subclass.
	 */
	public String getSubclassPropertyName(int i);

	/**
	 * Return the column names used to persist the numbered property of the 
	 * class or a subclass.
	 */
	public String[] getSubclassPropertyColumnNames(int i);

	/**
	 * Return the table name used to persist the numbered property of the 
	 * class or a subclass.
	 */
	public String getSubclassPropertyTableName(int i);
	/**
	 * Given the number of a property of a subclass, and a table alias, 
	 * return the aliased column names.
	 */
	public String[] toColumns(String name, int i);

	/**
	 * Get the main from table fragment, given a query alias.
	 */
	public String fromTableFragment(String alias);
	
	/**
	 * Generate a list of collection index and element columns
	 */
	public String selectFragment(String alias, String suffix);

}
