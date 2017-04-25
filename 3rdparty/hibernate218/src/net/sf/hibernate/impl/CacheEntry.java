//$Id: CacheEntry.java,v 1.11 2004/07/20 14:27:18 oneovthafew Exp $
package net.sf.hibernate.impl;

import java.io.Serializable;

import net.sf.hibernate.AssertionFailure;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Interceptor;
import net.sf.hibernate.Lifecycle;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.persister.ClassPersister;
import net.sf.hibernate.type.Type;

/**
 * A cached instance of a persistent class
 * 
 * @author Gavin King
 */
public final class CacheEntry implements Serializable {
	
	Object[] state;
	Class subclass;
	
	public Class getSubclass() {
		return subclass;
	}
	
	public CacheEntry(Object object, ClassPersister persister, SessionImplementor session) throws HibernateException {
		state = disassemble(object, persister, session);
		subclass = object.getClass();
	}
	
	
	private static Object[] disassemble(Object object, ClassPersister persister, SessionImplementor session) throws HibernateException {
		Object[] values = persister.getPropertyValues(object);
		Type[] propertyTypes = persister.getPropertyTypes();
		for ( int i=0; i<values.length; i++ ) {
			values[i] = propertyTypes[i].disassemble(values[i], session);
		}
		return values;
	}
	
	
	public Object[] assemble(Object instance, Serializable id, ClassPersister persister, Interceptor interceptor, SessionImplementor session) throws HibernateException {
		
		if ( subclass!=persister.getMappedClass() ) throw new AssertionFailure("Tried to assemble a different subclass instance");
		
		return assemble(state, instance, id, persister, interceptor, session);
		
	}
	
	private static Object[] assemble(Object[] values, Object result, Serializable id, ClassPersister persister, Interceptor interceptor, SessionImplementor session) throws HibernateException {
		Type[] propertyTypes = persister.getPropertyTypes();
		Object[] assembledProps = new Object[propertyTypes.length];
		for ( int i=0; i<values.length; i++ ) {
			assembledProps[i] = propertyTypes[i].assemble( (Serializable) values[i], session, result );
		}
		
		//persister.setIdentifier(result, id); //before calling interceptor, for consistency with normal load

		interceptor.onLoad(result, id, assembledProps, persister.getPropertyNames(), propertyTypes);
		
		persister.setPropertyValues(result, assembledProps);
		
		if ( persister.implementsLifecycle() ) {
			//log.debug("calling onLoad() for cached object");
			( (Lifecycle) result ).onLoad(session, id);
		}
		
		return assembledProps;
	}
	
	
}






