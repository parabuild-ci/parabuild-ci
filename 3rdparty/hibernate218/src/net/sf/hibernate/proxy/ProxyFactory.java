//$Id: ProxyFactory.java,v 1.6 2004/06/04 05:43:48 steveebersole Exp $
package net.sf.hibernate.proxy;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;

/**
 * @author Gavin King
 */
public interface ProxyFactory {
	
	/**
	 * Called immediately after instantiation
	 */
	public void postInstantiate(
		Class persistentClass, 
		Set interfaces,
		Method getIdentifierMethod, 
		Method setIdentifierMethod
	) throws HibernateException;
	
	/**
	 * Create a new proxy
	 */
	public HibernateProxy getProxy(
		Serializable id, 
		SessionImplementor session
	) throws HibernateException;
	
}
