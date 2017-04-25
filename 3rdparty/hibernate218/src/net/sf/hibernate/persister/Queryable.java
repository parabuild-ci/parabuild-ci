//$Id: Queryable.java,v 1.13 2004/06/04 01:28:50 steveebersole Exp $
package net.sf.hibernate.persister;

import net.sf.hibernate.MappingException;

/**
 * Extends the generic <tt>ClassPersister</tt> contract to add
 * operations required by the Hibernate Query Language
 * 
 * @author Gavin King
 */
public interface Queryable extends Loadable, PropertyMapping, Joinable {
	
	/**
	 * Is this class mapped as a subclass of another class?
	 */
	public boolean isInherited();
	/**
	 * Is this class explicit polymorphism only?
	 */
	public boolean isExplicitPolymorphism();
	/**
	 * Get the class that this class is mapped as a subclass of -
	 * not necessarily the direct superclass
	 */
	public Class getMappedSuperclass();
	/**
	 * Get the discriminator value for this particular concrete subclass,
	 * as a string that may be embedded in a select statement
	 */
	public Object getDiscriminatorSQLValue();	
	/**
	 * Get the where clause fragment, given a query alias
	 */
	public String queryWhereFragment(String alias, boolean innerJoin, boolean includeSubclasses) throws MappingException;

	/**
	 * Given a query alias and an identifying suffix, render the intentifier select fragment.
	 */
	public String identifierSelectFragment(String name, String suffix);	
	/**
	 * Given a query alias and an identifying suffix, render the property select fragment.
	 */
	public String propertySelectFragment(String alias, String suffix);

	/**
	 * Get the names of columns used to persist the identifier
	 */
	public String[] getIdentifierColumnNames();

}






