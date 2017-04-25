//$Id: PropertyMapping.java,v 1.6 2004/06/04 05:43:47 steveebersole Exp $
package net.sf.hibernate.persister;

import net.sf.hibernate.QueryException;
import net.sf.hibernate.type.Type;

/**
 * Abstraction of all mappings that define properties:
 * entities, collection elements.
 * 
 * @author Gavin King
 */
public interface PropertyMapping {
	// TODO: It would be really, really nice to use this to also model components!
	/**
	 * Given a component path expression, get the type of the property
	 */
	public Type toType(String propertyName) throws QueryException;
	/**
	 * Given a query alias and a property path, return the qualified
	 * column name
	 */
	public String[] toColumns(String alias, String propertyName) throws QueryException;
	/**
	 * Get the type of the thing containing the properties
	 */
	public Type getType();
}
