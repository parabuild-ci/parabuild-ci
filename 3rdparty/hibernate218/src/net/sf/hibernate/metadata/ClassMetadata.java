//$Id: ClassMetadata.java,v 1.11 2004/08/06 02:56:49 oneovthafew Exp $
package net.sf.hibernate.metadata;

import java.io.Serializable;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.type.Type;

/**
 * Exposes entity class metadata to the application
 *
 * @see net.sf.hibernate.SessionFactory#getClassMetadata(Class)
 * @author Gavin King
 */
public interface ClassMetadata {
	
	/**
	 * The persistent class
	 */
	public Class getMappedClass();
	
	/**
	 * Create a class instance initialized with the given identifier
	 */
	public Object instantiate(Serializable id) throws HibernateException;

	/**
	 * Get the name of the identifier property (or return null)
	 */
	public String getIdentifierPropertyName();
	
	/**
	 * Get the names of the class' persistent properties
	 */
	public String[] getPropertyNames();
	
	/**
	 * Get the identifier Hibernate type
	 */
	public Type getIdentifierType();
	
	/**
	 * Get the Hibernate types of the class properties
	 */
	public Type[] getPropertyTypes();
	
	/**
	 * Get the type of a particular (named) property
	 */
	public Type getPropertyType(String propertyName) throws HibernateException;
	
	/**
	 * Get the value of a particular (named) property
	 */
	public Object getPropertyValue(Object object, String propertyName) throws HibernateException;

	/**
	 * Set the value of a particular (named) property
	 */
	public void setPropertyValue(Object object, String propertyName, Object value) throws HibernateException;

	/**
	 * Return the values of the mapped properties of the object
	 */
	public Object[] getPropertyValues(Object entity) throws HibernateException;
	
	/**
	 * Set the given values to the mapped properties of the given object
	 */
	public void setPropertyValues(Object object, Object[] values) throws HibernateException;
	
	/**
	 * Get the identifier of an instance (throw an exception if no identifier property)
	 */
	public Serializable getIdentifier(Object entity) throws HibernateException;
	
	/**
	 * Set the identifier of an instance (or do nothing if no identifier property)
	 */
	public void setIdentifier(Object object, Serializable id) throws HibernateException;
	
	/**
	 * Does the class implement the <tt>Lifecycle</tt> interface?
	 */
	public boolean implementsLifecycle();
	
	/**
	 * Does the class implement the <tt>Validatable</tt> interface?
	 */
	public boolean implementsValidatable();
	
	/**
	 * Does this class support dynamic proxies?
	 */
	public boolean hasProxy();
	
	/**
	 * Are instances of this class mutable?
	 */
	public boolean isMutable();
	
	/**
	 * Are instances of this class versioned by a timestamp or version number column?
	 */
	public boolean isVersioned();

	/**
	 * Get the version number (or timestamp) from the object's version property 
	 * (or return null if not versioned)
	 */
	public Object getVersion(Object object) throws HibernateException;
	
	/**
	 * Get the index of the version property
	 */
	public int getVersionProperty();
	
	/**
	 * Get the nullability of the class' persistent properties
	 */
	public boolean[] getPropertyNullability();
	
	/**
	 * Does this class have an identifier property?
	 */
	public boolean hasIdentifierProperty();
	
}






