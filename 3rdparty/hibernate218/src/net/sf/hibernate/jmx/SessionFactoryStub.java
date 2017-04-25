//$Id: SessionFactoryStub.java,v 1.15 2004/11/11 20:42:33 steveebersole Exp $
package net.sf.hibernate.jmx;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.AssertionFailure;
import net.sf.hibernate.Databinder;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Interceptor;
import net.sf.hibernate.LazyInitializationException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.exception.SQLExceptionConverter;
import net.sf.hibernate.id.IdentifierGenerator;
import net.sf.hibernate.id.UUIDHexGenerator;
import net.sf.hibernate.impl.SessionFactoryObjectFactory;
import net.sf.hibernate.metadata.ClassMetadata;
import net.sf.hibernate.metadata.CollectionMetadata;

/**
 * A flyweight for <tt>SessionFactory</tt>. If the MBean itself does not
 * have classpath to the persistent classes, then a stub will be registered
 * with JNDI and the actual <tt>SessionFactoryImpl</tt> built upon first
 * access.
 * @author Gavin King
 */
public class SessionFactoryStub implements SessionFactory {
	
	private static final Log log = LogFactory.getLog(SessionFactoryStub.class);
	
	private static final IdentifierGenerator UUID_GENERATOR = new UUIDHexGenerator();
	
	private transient SessionFactory impl;
	private transient HibernateService service;
	private String uuid;
	private String name;
	
	SessionFactoryStub(HibernateService service) {
		this.service = service;
		this.name = service.getJndiName();
		try {
			uuid = (String) UUID_GENERATOR.generate(null, null);
		}
		catch (Exception e) {
			throw new AssertionFailure("Could not generate UUID");
		}
		
		SessionFactoryObjectFactory.addInstance( uuid, name, this, service.getProperties() );
	}
	
	public Session openSession() throws HibernateException {
		return getImpl().openSession();
	}
	public Session openSession(Connection conn) {
		return getImpl().openSession(conn);
	}
	public Databinder openDatabinder() throws HibernateException {
		return getImpl().openDatabinder();
	}
	
	private synchronized SessionFactory getImpl() {
		if (impl==null) {
			try {
				impl = service.buildSessionFactory();
			}
			catch (Exception e) {
				throw new LazyInitializationException(e);
			}
		}
		return impl;
	}
	
	//readResolveObject
	private Object readResolve() throws ObjectStreamException {
		// look for the instance by uuid
		Object result = SessionFactoryObjectFactory.getInstance(uuid);
		if (result==null) {
			// in case we were deserialized in a different JVM, look for an instance with the same name
			// (alternatively we could do an actual JNDI lookup here....)
			result = SessionFactoryObjectFactory.getNamedInstance(name);
			if (result==null) {
				throw new InvalidObjectException("Could not find a stub SessionFactory named: " + name);
			}
			else {
				log.debug("resolved stub SessionFactory by name");
			}
		}
		else {
			log.debug("resolved stub SessionFactory by uid");
		}
		return result;
	}
	
	/**
	 * @see javax.naming.Referenceable#getReference()
	 */
	public Reference getReference() throws NamingException {
		return new Reference(
			SessionFactoryStub.class.getName(),
			new StringRefAddr("uuid", uuid),
			SessionFactoryObjectFactory.class.getName(),
			null
		);
	}
	
	public ClassMetadata getClassMetadata(Class persistentClass) throws HibernateException {
		return getImpl().getClassMetadata(persistentClass);
	}
	
	public CollectionMetadata getCollectionMetadata(String roleName) throws HibernateException {
		return getImpl().getCollectionMetadata(roleName);
	}
	
	public Session openSession(Connection connection, Interceptor interceptor) {
		return getImpl().openSession(connection, interceptor);
	}
	
	public Session openSession(Interceptor interceptor) throws HibernateException {
		return getImpl().openSession(interceptor);
	}
	
	
	public Map getAllClassMetadata() throws HibernateException {
		return getImpl().getAllClassMetadata();
	}
	
	public Map getAllCollectionMetadata() throws HibernateException {
		return getImpl().getAllCollectionMetadata();
	}
	
	public void close() throws HibernateException {
	}

	public void evict(Class persistentClass, Serializable id)
		throws HibernateException {
		getImpl().evict(persistentClass, id);
	}

	public void evict(Class persistentClass) throws HibernateException {
		getImpl().evict(persistentClass);
	}

	public void evictCollection(String roleName, Serializable id)
		throws HibernateException {
		getImpl().evictCollection(roleName, id);
	}

	public void evictCollection(String roleName) throws HibernateException {
		getImpl().evictCollection(roleName);
	}

	public void evictQueries() throws HibernateException {
		getImpl().evictQueries();
	}

	public void evictQueries(String cacheRegion) throws HibernateException {
		getImpl().evictQueries(cacheRegion);
	}

	/**
	 * Retrieves the SQLExceptionConverter in effect for this SessionFactory.
	 *
	 * @return The SQLExceptionConverter for this SessionFactory.
	 */
	public SQLExceptionConverter getSQLExceptionConverter() {
		return getImpl().getSQLExceptionConverter();
	}

}





