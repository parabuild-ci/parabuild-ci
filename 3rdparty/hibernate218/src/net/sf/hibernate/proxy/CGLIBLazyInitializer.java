//$Id: CGLIBLazyInitializer.java,v 1.13 2004/06/04 01:28:51 steveebersole Exp $
package net.sf.hibernate.proxy;

import java.io.Serializable;
import java.lang.reflect.Method;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;

import org.apache.commons.logging.LogFactory;


/**
 * A <tt>LazyInitializer</tt> implemented using the CGLIB bytecode generation library
 */
public final class CGLIBLazyInitializer extends LazyInitializer implements MethodInterceptor {
	
	private Class[] interfaces;
	private boolean constructed = false;
	
	static HibernateProxy getProxy(
		final Class persistentClass, 
		final Class[] interfaces, 
		final Method getIdentifierMethod, 
		final Method setIdentifierMethod, 
		final Serializable id, 
		final SessionImplementor session) 
	throws HibernateException {
		//note: interfaces is assumed to already contain HibernateProxy.class
		try {
			final CGLIBLazyInitializer instance = new CGLIBLazyInitializer(
				persistentClass, interfaces, id, getIdentifierMethod, setIdentifierMethod, session
			);
			final HibernateProxy proxy = (HibernateProxy) Enhancer.create(
				(interfaces.length==1) ?
					persistentClass :
					null,
				interfaces,
				instance
			);
			instance.constructed = true;
			return proxy;
		}
		catch (Throwable t) {
			LogFactory.getLog(LazyInitializer.class).error("CGLIB Enhancement failed", t);
			throw new HibernateException( "CGLIB Enhancement failed", t );
		}
	}
	
	public static HibernateProxy getProxy(
		final Factory factory, 
		final Class persistentClass, 
		final Class[] interfaces, 
		final Method getIdentifierMethod, 
		final Method setIdentifierMethod, 
		final Serializable id, 
		final SessionImplementor session) 
	throws HibernateException {
		final CGLIBLazyInitializer instance = new CGLIBLazyInitializer(
			persistentClass, interfaces, id, getIdentifierMethod, setIdentifierMethod, session
		);
		final HibernateProxy proxy = (HibernateProxy) factory.newInstance(instance);
		instance.constructed = true;
		return proxy;
	}
	
	public static Factory getProxyFactory(Class persistentClass, Class[] interfaces) throws HibernateException {
		//note: interfaces is assumed to already contain HibernateProxy.class
		try {
			return (Factory) Enhancer.create(
				(interfaces.length==1) ?
					persistentClass :
					null,
				interfaces,
				NULL_METHOD_INTERCEPTOR
			);
		}
		catch (Throwable t) {
			LogFactory.getLog(LazyInitializer.class).error("CGLIB Enhancement failed", t);
			throw new HibernateException( "CGLIB Enhancement failed", t );
		}
	}
	
	private CGLIBLazyInitializer(
		final Class persistentClass, 
		final Class[] interfaces, 
		final Serializable id, 
		final Method getIdentifierMethod, 
		final Method setIdentifierMethod, 
		final SessionImplementor session) {
		super(persistentClass, id, getIdentifierMethod, setIdentifierMethod, session);
		this.interfaces = interfaces;
	}
	
	public Object intercept(
		final Object obj, 
		final Method method, 
		final Object[] args, 
		final MethodProxy proxy) 
	throws Throwable {
		if (constructed) {
			Object result = invoke(method, args, obj);
			if (result==INVOKE_IMPLEMENTATION) {
				return proxy.invoke( getImplementation(), args );
			}
			else {
				return result;
			}
		}
		else {
			//while constructor is running
			return proxy.invokeSuper(obj, args);
		}
	}
	
	protected Object serializableProxy() {
		return new SerializableProxy(persistentClass, interfaces, id, getIdentifierMethod, setIdentifierMethod);
	}
	
	private static final MethodInterceptor NULL_METHOD_INTERCEPTOR = new MethodInterceptor() {
		public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable { 
			return proxy.invokeSuper(obj, args); 
		}
	};
}
