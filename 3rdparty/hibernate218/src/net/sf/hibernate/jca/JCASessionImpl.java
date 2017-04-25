// $Id: JCASessionImpl.java,v 1.15 2004/08/02 19:20:25 turin42 Exp $
package net.sf.hibernate.jca;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.FlushMode;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.Query;
import net.sf.hibernate.ReplicationMode;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Transaction;
import net.sf.hibernate.type.Type;

/**
 * A logical session handle, all real work is deligated to underlying physical
 * session represented by ManagedConnectionImpl instance.
 */
public class JCASessionImpl implements Session {
	
	/*
	 * JCA 1.0 5.5.1
	 * A connection implementation class implements its methods in a
	 * resource adapter implementation-specific way. It must use
	 * javax.resource.spi.ManagedConnection instance as its
	 * underlying physical connection.
	 */
	private ManagedConnectionImpl mc;
	
	public JCASessionImpl(final ManagedConnectionImpl mc) {
		this.mc = mc;
	}
	
	private Session getSession() {
		return mc.getSession(this);
	}
	
	ManagedConnectionImpl getManagedConnextion() {
		return mc;
	}
	
	void setManagedConnection(ManagedConnectionImpl mc) {
		this.mc = mc;
	}
	
	//
	// Session implementation goes below.
	//
	
	public void flush() throws HibernateException {
		getSession().flush();
	}
	
	public void setFlushMode(final FlushMode flushMode) {
		getSession().setFlushMode(flushMode);
	}
	
	public FlushMode getFlushMode() {
		return getSession().getFlushMode();
	}
	
	public Connection connection() throws HibernateException {
		return getSession().connection();
	}
	
	public Connection disconnect() throws HibernateException {
		throw new UnsupportedOperationException();
	}
	
	public void reconnect() throws HibernateException {
		throw new UnsupportedOperationException();
	}
	
	public void reconnect(final Connection connection)
	throws HibernateException {
		throw new UnsupportedOperationException();
	}
	
	public Connection close() throws HibernateException {
		mc.closeHandle(this);
		return null; // never let clients mess with database connections
	}
	
	public boolean isOpen() {
		try {
			return getSession().isOpen();
		}
		catch (IllegalStateException expected) {
			return false;
		}
	}
	
	/**
	 * @see net.sf.hibernate.Session#isConnected()
	 */
	public boolean isConnected() {
		return getSession().isConnected();
	}
	
	/**
	 * @see net.sf.hibernate.Session#getIdentifier(Object)
	 */
	public Serializable getIdentifier(final Object object)
	throws HibernateException {
		return getSession().getIdentifier(object);
	}
	
	/**
	 * @see net.sf.hibernate.Session#load(Class, Serializable, LockMode)
	 */
	public Object load(final Class theClass, final Serializable id,
	final LockMode lockMode) throws HibernateException {
		return getSession().load(theClass, id, lockMode);
	}
	
	/**
	 * @see net.sf.hibernate.Session#load(Class, Serializable)
	 */
	public Object load(final Class theClass, final Serializable id)
	throws HibernateException {
		return getSession().load(theClass, id);
	}
	
	/**
	 * @see net.sf.hibernate.Session#load(Object, Serializable)
	 */
	public void load(final Object object, final Serializable id)
	throws HibernateException {
		getSession().load(object, id);
	}
	
	/**
	 * @see net.sf.hibernate.Session#save(Object)
	 */
	public Serializable save(final Object object) throws HibernateException {
		return getSession().save(object);
	}
	
	/**
	 * @see net.sf.hibernate.Session#save(Object, Serializable)
	 */
	public void save(final Object object, final Serializable id)
	throws HibernateException {
		getSession().save(object, id);
	}
	
	/**
	 * @see net.sf.hibernate.Session#saveOrUpdate(Object)
	 */
	public void saveOrUpdate(final Object object) throws HibernateException {
		getSession().saveOrUpdate(object);
	}
	
	/**
	 * @see net.sf.hibernate.Session#update(Object)
	 */
	public void update(final Object object) throws HibernateException {
		getSession().update(object);
	}
	
	/**
	 * @see net.sf.hibernate.Session#update(Object, Serializable)
	 */
	public void update(final Object object, final Serializable id)
	throws HibernateException {
		getSession().update(object, id);
	}
	
	public void delete(final Object object) throws HibernateException {
		getSession().delete(object);
	}
	
	public List find(final String query) throws HibernateException {
		return getSession().find(query);
	}
	
	public List find(final String query, final Object value, final Type type)
	throws HibernateException {
		return getSession().find(query, value, type);
	}
	
	public List find(final String query, final Object[] values,
	final Type[] types) throws HibernateException {
		return getSession().find(query, values, types);
	}
	
	public Iterator iterate(final String query) throws HibernateException {
		return getSession().iterate(query);
	}
	
	public Iterator iterate(final String query, final Object value,
	final Type type) throws HibernateException {
		return getSession().iterate(query, value, type);
	}
	
	public Iterator iterate(final String query, final Object[] values,
	final Type[] types) throws HibernateException {
		return getSession().iterate(query, values, types);
	}
	
	public Collection filter(final Object collection, final String filter)
	throws HibernateException {
		return getSession().filter(collection, filter);
	}
	
	public Collection filter(final Object collection, final String filter,
	final Object value, final Type type) throws HibernateException {
		return getSession().filter(collection, filter, value, type);
	}
	
	public Collection filter(final Object collection, final String filter,
	final Object[] values, final Type[] types) throws HibernateException {
		return getSession().filter(collection, filter, values, types);
	}
	
	public int delete(final String query) throws HibernateException {
		return getSession().delete(query);
	}
	
	public int delete(final String query, final Object value, final Type type)
	throws HibernateException {
		return getSession().delete(query, value, type);
	}
	
	public int delete(final String query, final Object[] values,
	final Type[] types) throws HibernateException {
		return getSession().delete(query, values, types);
	}
	
	public void lock(final Object object, final LockMode lockMode)
	throws HibernateException {
		getSession().lock(object, lockMode);
	}
	
	public void refresh(final Object object) throws HibernateException {
		getSession().refresh(object);
	}
	
	public LockMode getCurrentLockMode(final Object object)
	throws HibernateException {
		return getSession().getCurrentLockMode(object);
	}
	
	public Transaction beginTransaction() throws HibernateException {
		return getSession().beginTransaction();
	}
	
	public Query createQuery(String queryString) throws HibernateException {
		return getSession().createQuery(queryString);
	}
	
	public Query createFilter(final Object collection, final String queryString)
	throws HibernateException {
		return getSession().createFilter(collection, queryString);
	}
	
	public Query getNamedQuery(final String queryName)
	throws HibernateException {
		return getSession().getNamedQuery(queryName);
	}
	
	public Criteria createCriteria(final Class persistentClass) {
		return getSession().createCriteria(persistentClass);
	}
	
	public void refresh(final Object object, final LockMode lockMode)
	throws HibernateException {
		getSession().refresh(object, lockMode);
	}
	
	public boolean contains(final Object object) {
		return getSession().contains(object);
	}
	
	public void evict(Object object) throws HibernateException {
		getSession().evict(object);
	}

	public Query createSQLQuery(String string, String returnAlias, Class returnClass) {
		return getSession().createSQLQuery(string, returnAlias, returnClass);
	}

	public Query createSQLQuery(String string, String returnAliases[], Class returnClasses[]) {
		return getSession().createSQLQuery(string, returnAliases, returnClasses);
	}
	
	public void clear() {
		getSession().clear();
	}

	public Object get(Class clazz, Serializable id, LockMode lockMode)
		throws HibernateException {
		return getSession().get(clazz, id, lockMode);
	}

	public Object get(Class clazz, Serializable id) throws HibernateException {
		return getSession().get(clazz, id);
	}

	public void replicate(Object object, ReplicationMode mode) throws HibernateException {
		getSession().replicate(object, mode);
	}

	public SessionFactory getSessionFactory() {
		return getSession().getSessionFactory();
	}	

	public void cancelQuery() throws HibernateException {
		getSession().cancelQuery();
	}

	public Object saveOrUpdateCopy(Object object) throws HibernateException {
		return getSession().saveOrUpdateCopy(object);
	}

	public Object saveOrUpdateCopy(Object object, Serializable id)
		throws HibernateException {
		return getSession().saveOrUpdateCopy(object, id);
	}

	public boolean isDirty() throws HibernateException {
		return getSession().isDirty();
	}

}

