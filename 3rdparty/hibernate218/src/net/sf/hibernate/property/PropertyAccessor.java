//$Id: PropertyAccessor.java,v 1.6 2004/06/04 05:43:48 steveebersole Exp $
package net.sf.hibernate.property;

import net.sf.hibernate.PropertyNotFoundException;

/**
 * Abstracts the notion of a "property". Defines a strategy for accessing the
 * value of an attribute.
 * @author Gavin King
 */
public interface PropertyAccessor {
	/**
	 * Create a "getter" for the named attribute
	 */
	public Getter getGetter(Class theClass, String propertyName) throws PropertyNotFoundException;
	/**
	 * Create a "setter" for the named attribute
	 */
	public Setter getSetter(Class theClass, String propertyName) throws PropertyNotFoundException;
}
