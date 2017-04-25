//$Id: Setter.java,v 1.6 2004/06/04 05:43:48 steveebersole Exp $
package net.sf.hibernate.property;

import java.lang.reflect.Method;

import net.sf.hibernate.HibernateException;

/**
 * Sets values to a particular property
 * @author Gavin King
 */
public interface Setter {
	/**
	 * Set the property value from the given instance
	 */
	public void set(Object target, Object value) throws HibernateException;
	/**
	 * Optional operation (return null)
	 */
	public String getMethodName();
	/**
	 * Optional operation (return null)
	 */
	public Method getMethod();
}
