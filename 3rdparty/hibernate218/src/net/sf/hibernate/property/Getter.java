//$Id: Getter.java,v 1.6 2004/06/04 05:43:48 steveebersole Exp $
package net.sf.hibernate.property;

import java.lang.reflect.Method;

import net.sf.hibernate.HibernateException;

/**
 * Gets values of a particular property
 * @author Gavin King
 */
public interface Getter {
	/**
	 * Get the property value from the given instance
	 */
	public Object get(Object target) throws HibernateException;
	/**
	 * Get the declared Java type
	 */
	public Class getReturnType();
	/**
	 * Optional operation (return null)
	 */
	public String getMethodName();
	/**
	 * Optional operation (return null)
	 */
	public Method getMethod();
}
