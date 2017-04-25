//$Id: CGLIBProxyFactory.java,v 1.6 2004/06/04 05:43:48 steveebersole Exp $
package net.sf.hibernate.proxy;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Set;

import net.sf.cglib.proxy.Factory;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;

/**
 * @author Gavin King
 */
public class CGLIBProxyFactory implements ProxyFactory {
	
	protected static final Class[] NO_CLASSES = new Class[0];
	
	private Class persistentClass;
	private Class[] interfaces;
	private Method getIdentifierMethod;
	private Method setIdentifierMethod;
	private Factory factory;
	
	public void postInstantiate(
		Class persistentClass,
		Set interfaces,
		Method getIdentifierMethod,
		Method setIdentifierMethod)
		throws HibernateException {
		
		this.persistentClass = persistentClass;
		this.interfaces = (Class[]) interfaces.toArray(NO_CLASSES);
		this.getIdentifierMethod = getIdentifierMethod;
		this.setIdentifierMethod = setIdentifierMethod;
		factory = CGLIBLazyInitializer.getProxyFactory(persistentClass, this.interfaces);

	}

	public HibernateProxy getProxy(Serializable id, SessionImplementor session)
		throws HibernateException {
		
		return CGLIBLazyInitializer.getProxy(
			factory, persistentClass, interfaces, getIdentifierMethod, setIdentifierMethod, id, session
		);
	}

}
