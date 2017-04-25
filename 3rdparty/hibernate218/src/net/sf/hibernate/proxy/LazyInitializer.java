//$Id: LazyInitializer.java,v 1.14 2004/06/04 01:28:51 steveebersole Exp $
package net.sf.hibernate.proxy;

import java.io.Serializable;
import java.lang.reflect.Method;

import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LazyInitializationException;
import net.sf.hibernate.engine.Key;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.impl.MessageHelper;
import net.sf.hibernate.util.ReflectHelper;

/**
 * @author Gavin King
 */
public abstract class LazyInitializer {
	
	protected static final Object INVOKE_IMPLEMENTATION = new Object();
	
	protected Object target = null;
	protected Serializable id;
	protected SessionImplementor session;
	protected Class persistentClass;
	protected Method getIdentifierMethod;
	protected Method setIdentifierMethod;
	protected boolean overridesEquals;
	private Object replacement;
	
	protected LazyInitializer(Class persistentClass, Serializable id, Method getIdentifierMethod, Method setIdentifierMethod, SessionImplementor session) {
		this.id = id;
		this.session = session;
		this.persistentClass = persistentClass;
		this.getIdentifierMethod = getIdentifierMethod;
		this.setIdentifierMethod = setIdentifierMethod;
		overridesEquals = ReflectHelper.overridesEquals(persistentClass);
	}
	
	public void initialize() throws HibernateException {
		if (target==null) {
			if ( session==null ) {
				throw new HibernateException("Could not initialize proxy - no Session");
			}
			else if ( !session.isOpen() ) {
				throw new HibernateException("Could not initialize proxy - the owning Session was closed");
			}
			else if ( !session.isConnected() ) {
				throw new HibernateException("Could not initialize proxy - the owning Session is disconnected");
			}
			else {
				target = session.immediateLoad(persistentClass, id);
			}
		}
	}
	
	private void initializeWrapExceptions() {
		try {
			initialize();
		}
		catch (Exception e) {
			LogFactory.getLog(LazyInitializer.class).error("Exception initializing proxy", e);
			throw new LazyInitializationException(
				"Exception initializing proxy: " +
				MessageHelper.infoString(persistentClass, id),
				e
			);
		}
	}
	
	protected abstract Object serializableProxy();
	
	protected final Object invoke(Method method, Object[] args, Object proxy) throws Throwable {
		
		String methodName = method.getName();
		int params = method.getParameterTypes().length;
		
		if ( params==0 ) {
			
			if ( "writeReplace".equals(methodName) ) {
				
				if (target==null && session!=null ) target = session.getEntity(
					new Key( id, session.getFactory().getPersister(persistentClass) )
				);
				if (target==null) {
					if (replacement==null) replacement = serializableProxy();
					return replacement;
				}
				else {
					return target;
				}
				/*if (replacement==null) replacement = serializableProxy();
				replacement.setSnapshot(snapshot);
				replacement.setTarget( (Serializable) target );
				return replacement;*/
				
			}
			else if ( !overridesEquals && "hashCode".equals(methodName) ) {
				return new Integer( System.identityHashCode(proxy) );
			}
			else if ( method.equals(getIdentifierMethod) ) {
				return id;
			}
			else if ( "finalize".equals( method.getName() ) ) {
				return null;
			}
			
		}
		else if ( params==1 ) {
			
			if ( !overridesEquals && "equals".equals(methodName) ) {
				return new Boolean( args[0]==proxy );
			}
			else if ( method.equals(setIdentifierMethod) ) {
				initialize();
				id = (Serializable) args[0];
				return INVOKE_IMPLEMENTATION;
			}
			
		}
		
		// otherwise:
		return INVOKE_IMPLEMENTATION;
		
	}
	
	public final Serializable getIdentifier() {
		return id;
	}
	
	public final void setIdentifier(Serializable id) {
		this.id = id;
	}
	
	public final Class getPersistentClass() {
		return persistentClass;
	}
	
	public final boolean isUninitialized() {
		return target == null;
	}
	
	public final SessionImplementor getSession() {
		return session;
	}
	
	public final void setSession(SessionImplementor s) {
		if (s!=session) {
			if ( session!=null && session.isOpen() ) {
				//TODO: perhaps this should be some other RuntimeException...
				throw new LazyInitializationException("Illegally attempted to associate a proxy with two open Sessions");
			}
			else {
				session = s;
			}
		}
	}

	/**
	 * Return the underlying persistent object, initializing if necessary
	 */
	public final Object getImplementation() {
		initializeWrapExceptions();
		return target;
	}
	
	/**
	 * Return the underlying persistent object in the given <tt>Session</tt>, or null
	 */
	public final Object getImplementation(SessionImplementor s) throws HibernateException {
		return s.getEntity( new Key(
			getIdentifier(),
			s.getFactory().getPersister( getPersistentClass() )
		) );
	}

}






