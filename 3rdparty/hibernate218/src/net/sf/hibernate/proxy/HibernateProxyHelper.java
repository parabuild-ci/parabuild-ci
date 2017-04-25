//$Id: HibernateProxyHelper.java,v 1.11 2004/06/04 01:28:51 steveebersole Exp $
package net.sf.hibernate.proxy;

import java.io.Serializable;

import net.sf.cglib.proxy.Factory;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.persister.ClassPersister;

/**
 * Utility methods for working with proxies.
 * @author Gavin King
 */
public final class HibernateProxyHelper {
	
	/**
	 * Get the LazyInitializer for a proxy instance
	 */
	public static LazyInitializer getLazyInitializer(HibernateProxy proxy) {
		return (LazyInitializer)( (Factory) proxy ).getCallback(0);
	}
	
	/** 
	 * Get the class of an instance or the underlying class 
	 * of a proxy (without initializing the proxy!).
	 */
	public static Class getClass(Object object) {
		if (object instanceof HibernateProxy) {
			HibernateProxy proxy = (HibernateProxy) object;
			LazyInitializer li = getLazyInitializer(proxy);
			return li.getPersistentClass();
		}
		else {
			return object.getClass();
		}
	}
	/**
	 * Get the identifier value of an instance or proxy
	 */
	public static Serializable getIdentifier(Object object, ClassPersister persister) 
	throws HibernateException {
		if (object instanceof HibernateProxy) {
			HibernateProxy proxy = (HibernateProxy) object;
			LazyInitializer li = getLazyInitializer(proxy);
			return li.getIdentifier();
		}
		else {
			return persister.getIdentifier(object);
		}
	}
	
	private HibernateProxyHelper() {
		//cant instantiate
	}
	
}






