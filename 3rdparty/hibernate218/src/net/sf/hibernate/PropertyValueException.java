//$Id: PropertyValueException.java,v 1.4 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

import net.sf.hibernate.util.StringHelper;

/**
 * Thrown when the (illegal) value of a property can not be persisted. 
 * There are two main causes:
 * <ul>
 * <li>a property declared <tt>not-null="true"</tt> is null
 * <li>an association references an unsaved transient instance
 * </ul>
 * @author Gavin King
 */
public class PropertyValueException extends HibernateException {
	
	private final Class persistentClass;
	private final String propertyName;
	
	public PropertyValueException(String s, Class persistentClass, String propertyName) {
		super(s);
		this.persistentClass = persistentClass;
		this.propertyName = propertyName;
	}
	
	public Class getPersistentClass() {
		return persistentClass;
	}
	
	public String getPropertyName() {
		return propertyName;
	}
	
	public String getMessage() {
		return super.getMessage() +
		StringHelper.qualify( persistentClass.getName(), propertyName );
	}
}






