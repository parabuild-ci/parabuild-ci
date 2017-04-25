//$Id: JCASessionFactoryImpl.java,v 1.10 2004/11/11 20:42:32 steveebersole Exp $
package net.sf.hibernate.jca;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.resource.Referenceable;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;

import net.sf.hibernate.Databinder;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Interceptor;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.exception.SQLExceptionConverter;
import net.sf.hibernate.metadata.ClassMetadata;
import net.sf.hibernate.metadata.CollectionMetadata;

public final class JCASessionFactoryImpl
implements SessionFactory, Referenceable {
	
	/*
	 * @todo JCA 1.0, 5.10.1
	 * A resource adapter is required to provide support for basic error
	 * logging and tracing by implementing the following methods:
	 *   - ManagedConnectionFactory.set/getLogWriter
	 *   - ManagedConnection.set/getLogWriter
	 */
	
	private final ManagedConnectionFactoryImpl managedFactory;
	private final ConnectionManager cxManager;
	private Reference reference;
	
	public JCASessionFactoryImpl(
		final ManagedConnectionFactoryImpl managedFactory,
		final ConnectionManager cxManager
	) throws HibernateException {
		
		this.managedFactory = managedFactory;
		this.cxManager = cxManager;
	}
	
	public void close() throws HibernateException {
		// don't want crazy clients to destroy the factory
		// JCA 1.5 will have adapter lifecycle management
		throw new UnsupportedOperationException();
	}
	
	public Map getAllClassMetadata() throws HibernateException {
		return getSessionFactory().getAllClassMetadata();
	}
	
	public Map getAllCollectionMetadata() throws HibernateException {
		return getSessionFactory().getAllCollectionMetadata();
	}
	
	public ClassMetadata getClassMetadata(Class persistentClass)
	throws HibernateException {
		return getSessionFactory().getClassMetadata(persistentClass);
	}
	
	public CollectionMetadata getCollectionMetadata(String roleName)
	throws HibernateException {
		return getSessionFactory().getCollectionMetadata(roleName);
	}
	
	public Databinder openDatabinder() throws HibernateException {
		return getSessionFactory().openDatabinder();
	}
	
	public Session openSession() throws HibernateException {
		Session result = null;
		try {
			// JCA 1.0, 5.5.1
			// resource adapter implementation is not required to support the
			// mechanism for passing resource adapter-specific connection
			// request information. It can choose to pass null for
			// ConnectionRequestInfo in the allocateConnection invocation.
			result = (Session)
			cxManager.allocateConnection(managedFactory, null);
		}
		catch (ResourceException re) {
			throw new HibernateException(re);
		}
		return result;
	}
	
	public Session openSession(final Connection connection, final Interceptor interceptor) {
		return getSessionFactory().openSession(connection, interceptor);
	}
		
	public Session openSession(Connection connection) {
		return getSessionFactory().openSession(connection);
	}
	
	public Session openSession(Interceptor interceptor)
	throws HibernateException {
		return getSessionFactory().openSession(interceptor);
	}
	
	public Reference getReference() throws NamingException {
		return reference;
	}
	
	public void setReference(Reference ref) {
		reference = ref;
	}
	
	private SessionFactory getSessionFactory() {
		return managedFactory.getSessionFactory();
	}
	public void evict(Class persistentClass, Serializable id)
	throws HibernateException {
		getSessionFactory().evict(persistentClass, id);
	}
	
	public void evict(Class persistentClass) throws HibernateException {
		getSessionFactory().evict(persistentClass);
	}
	
	public void evictCollection(String roleName, Serializable id)
	throws HibernateException {
		getSessionFactory().evictCollection(roleName, id);
	}
	
	public void evictCollection(String roleName) throws HibernateException {
		getSessionFactory().evictCollection(roleName);
	}
		
	public void evictQueries() throws HibernateException {
		getSessionFactory().evictQueries();
	}

	public void evictQueries(String cacheRegion) throws HibernateException {
		getSessionFactory().evictQueries(cacheRegion);
	}

	/**
	 * Retrieves the SQLExceptionConverter in effect for this SessionFactory.
	 *
	 * @return The SQLExceptionConverter for this SessionFactory.
	 */
	public SQLExceptionConverter getSQLExceptionConverter() {
		return getSessionFactory().getSQLExceptionConverter();
	}

}
	
