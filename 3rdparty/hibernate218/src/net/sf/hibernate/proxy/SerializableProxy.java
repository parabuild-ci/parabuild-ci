//$Id: SerializableProxy.java,v 1.13 2004/06/04 01:28:51 steveebersole Exp $
package net.sf.hibernate.proxy;

import java.io.Serializable;
import java.lang.reflect.Method;

import net.sf.hibernate.LazyInitializationException;
import net.sf.hibernate.impl.MessageHelper;

import org.apache.commons.logging.LogFactory;

/**
 * Serializable placeholder for <tt>CGLIB</tt> proxies
 */
public final class SerializableProxy implements Serializable {
	
	private Class persistentClass;
	private Class[] interfaces;
	private Serializable id;
	private Class getIdentifierMethodClass;
	private Class setIdentifierMethodClass;
	private String getIdentifierMethodName;
	private String setIdentifierMethodName;
	private Class[] setIdentifierMethodParams;
	
	public SerializableProxy() {}
	
	public SerializableProxy(Class persistentClass, Class[] interfaces, Serializable id, Method getIdentifierMethod, Method setIdentifierMethod) {
		this.persistentClass = persistentClass;
		this.interfaces = interfaces;
		this.id = id;
		if (getIdentifierMethod!=null) {
			getIdentifierMethodClass = getIdentifierMethod.getDeclaringClass();
			getIdentifierMethodName = getIdentifierMethod.getName();
		}
		if (setIdentifierMethod!=null) {
			setIdentifierMethodClass = setIdentifierMethod.getDeclaringClass();
			setIdentifierMethodName = setIdentifierMethod.getName();
			setIdentifierMethodParams = setIdentifierMethod.getParameterTypes();
		}
	}

	private Object readResolve() {
		try {
			 Object proxy = CGLIBLazyInitializer.getProxy(
				persistentClass,
				interfaces,
				(getIdentifierMethodName==null) ? 
					null : 
					getIdentifierMethodClass.getDeclaredMethod(getIdentifierMethodName, null),
				(setIdentifierMethodName==null) ? 
					null : 
					setIdentifierMethodClass.getDeclaredMethod(setIdentifierMethodName, setIdentifierMethodParams),
				id,
				null
			);
			//LazyInitializer li = HibernateProxyHelper.getLazyInitializer( (HibernateProxy) proxy );
			//li.setTarget(target);
			//li.setSnapshot(snapshot);
			return proxy;
		}
		catch (Exception e) {
			LogFactory.getLog(CGLIBLazyInitializer.class).error("Exception deserializing proxy", e);
			throw new LazyInitializationException(
				"could not deserialize a proxy: " + 
				MessageHelper.infoString(persistentClass, id), 
				e
			);
		}
	}

}






