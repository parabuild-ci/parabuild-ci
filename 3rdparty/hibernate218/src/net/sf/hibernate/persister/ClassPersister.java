//$Id: ClassPersister.java,v 1.15 2004/06/04 01:28:50 steveebersole Exp $
package net.sf.hibernate.persister;

import java.io.Serializable;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.type.VersionType;
import net.sf.hibernate.cache.CacheConcurrencyStrategy;
import net.sf.hibernate.engine.Cascades;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.id.IdentifierGenerator;
import net.sf.hibernate.metadata.ClassMetadata;

/**
 * Concrete <tt>ClassPersister</tt>s implement mapping and persistence 
 * logic for a particular persistent class.
 * <br><br>
 * Implementors must be threadsafe (preferrably immutable) and must 
 * provide a constructor of type
 * <tt>(net.sf.hibernate.map.PersistentClass, net.sf.hibernate.impl.SessionFactoryImplementor)</tt>.
 *
 * @see EntityPersister
 * @author Gavin King
 */
public interface ClassPersister {
	
	/**
	 * The property name of the "special" identifier property in HQL
	 */
	public static final String ENTITY_ID = "id";
	
	/**
	 * Finish the initialization of this object, once all <tt>ClassPersisters</tt> 
	 * have been instantiated.
	 *
	 * Called only once, before any other method.
	 */
	public void postInstantiate() throws MappingException;
	
	/**
	 * Returns an object that identifies the space in which identifiers 
	 * of this class hierarchy are unique.
	 *
	 * A table name, a JNDI URL, etc.
	 */
	public Serializable getIdentifierSpace();
	
	/**
	 * Returns an array of objects that identify spaces in which properties 
	 * of this class instance are persisted.
	 */
	public Serializable[] getPropertySpaces();
	
	/**
	 * The persistent class
	 */
	public Class getMappedClass();
	
	/**
	 * The classname of the persistent class (used only for messages)
	 */
	public String getClassName();
	
	/**
	 * Does the class implement the <tt>Lifecycle</tt> interface.
	 */
	public boolean implementsLifecycle();
	
	/**
	 * Does the class implement the <tt>Validatable</tt> interface.
	 */
	public boolean implementsValidatable();
	
	/**
	 * Does this class support dynamic proxies.
	 */
	public boolean hasProxy();
	/**
	 * Get the proxy interface that instances of <em>this</em> concrete 
	 * class will be cast to (optional operation).
	 */
	public Class getConcreteProxyClass();
	/**
	 * Create a new proxy instance
	 */
	public Object createProxy(Serializable id, SessionImplementor session) throws HibernateException;
	
	/**
	 * Do instances of this class contain collections.
	 */
	public boolean hasCollections();
	
	/**
	 * Does this class declare any cascading save/update/deletes.
	 */
	public boolean hasCascades();
	
	/**
	 * Are instances of this class mutable.
	 */
	public boolean isMutable();
	
	/**
	 * Is the identifier assigned before the insert by an <tt>IDGenerator</tt>. Or
	 * is it returned by the <tt>insert()</tt> method? This determines which form
	 * of <tt>insert()</tt> will be called.
	 */
	public boolean isIdentifierAssignedByInsert();
	
	/**
	 * Is this a new transient instance?
	 */
	public boolean isUnsaved(Object object) throws HibernateException;
	
	/**
	 * Set the given values to the mapped properties of the given object
	 */
	public void setPropertyValues(Object object, Object[] values) throws HibernateException;
	
	/**
	 * Return the values of the mapped properties of the object
	 */
	public Object[] getPropertyValues(Object object) throws HibernateException;
	
	/**
	 * Set the value of a particular property
	 */
	public void setPropertyValue(Object object, int i, Object value) throws HibernateException;
	
	/**
	 * Get the value of a particular property
	 */
	public Object getPropertyValue(Object object, int i) throws HibernateException;
	/**
	 * Get the value of a particular property
	 */
	public Object getPropertyValue(Object object, String propertyName) throws HibernateException;
	/**
	 * Get the type of a particular property
	 */
	public Type getPropertyType(String propertyName) throws MappingException;	
	
	/**
	 * Compare two snapshots of the state of an instance to determine 
	 * if the persistent state was modified
	 * @return <tt>null</tt> or the indices of the dirty properties
	 */
	public int[] findDirty(Object[] x, Object[] y, Object owner, SessionImplementor session) throws HibernateException;
	/**
	 * Compare the state of an instance to the current database state
	 * @return <tt>null</tt> or the indices of the dirty properties
	 */
	public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) throws HibernateException;	
	
	/**
	 * Does the class have a property holding the identifier value?
	 */
	public boolean hasIdentifierProperty();
	/**
	 * Do detached instances of this class carry their own identifier value?
	 */
	public boolean hasIdentifierPropertyOrEmbeddedCompositeIdentifier();
	
	/**
	 * Get the identifier of an instance (throw an exception 
	 * if no identifier property)
	 */
	public Serializable getIdentifier(Object object) throws HibernateException;
	
	/**
	 * Set the identifier of an instance (or do nothing if 
	 * no identifier property)
	 */
	public void setIdentifier(Object object, Serializable id) throws HibernateException;
	
	/**
	 * Are instances of this class versioned by a timestamp 
	 * or version number column.
	 */
	public boolean isVersioned();
	
	/**
	 * Get the type of versioning (optional operation)
	 */
	public VersionType getVersionType();
	
	/**
	 * Which property holds the version number (optional operation).
	 */
	public int getVersionProperty();
	
	/**
	 * Get the version number (or timestamp) from the object's 
	 * version property (or return null if not versioned)
	 */
	public Object getVersion(Object object) throws HibernateException;
	
	/**
	 * Create a class instance initialized with the given identifier
	 */
	public Object instantiate(Serializable id) throws HibernateException;
	
	/**
	 * Return the <tt>IdentifierGenerator</tt> for the class
	 */
	public IdentifierGenerator getIdentifierGenerator() throws HibernateException;
	
	/**
	 * Load an instance of the persistent class.
	 */
	public Object load(Serializable id, Object optionalObject, LockMode lockMode, SessionImplementor session) throws HibernateException;
	
	/**
	 * Do a version check (optional operation)
	 */
	public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) throws HibernateException;
	
	/**
	 * Persist an instance
	 */
	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) throws HibernateException;
	
	/**
	 * Persist an instance, using a natively generated identifier (optional operation)
	 */
	public Serializable insert(Object[] fields, Object object, SessionImplementor session) throws HibernateException;
	
	/**
	 * Delete a persistent instance
	 */
	public void delete(Serializable id, Object version, Object object, SessionImplementor session) throws HibernateException;
	
	/**
	 * Update a persistent instance
	 */
	public void update(Serializable id, Object[] fields, int[] dirtyFields, Object[] oldFields, Object oldVersion, Object object, SessionImplementor session) throws HibernateException;
	
	/**
	 * Get the Hibernate types of the class properties
	 */
	public Type[] getPropertyTypes();
	
	/**
	 * Get the names of the class properties - doesn't have to be the names of the
	 * actual Java properties (used for XML generation only)
	 */
	public String[] getPropertyNames();
	
	/**
	 * Get the "updateability" of the properties of this class
	 * (does the property appear in an SQL UPDATE)
	 */
	public boolean[] getPropertyUpdateability();
	
	/**
	 * Get the nullability of the properties of this class
	 */
	public boolean[] getPropertyNullability();
	
	/**
	 * Get the "insertability" of the properties of this class
	 * (does the property appear in an SQL INSERT)
	 */
	public boolean[] getPropertyInsertability();

	/**
	 * Get the cascade styles of the propertes (optional operation)
	 */
	public Cascades.CascadeStyle[] getPropertyCascadeStyles();
	
	/**
	 * Get the identifier type
	 */
	public Type getIdentifierType();
	
	/**
	 * Get the name of the identifier property (or return null) - 
	 * need not return the name of an actual Java property
	 */
	public String getIdentifierPropertyName();
	
	/**
	 * Should we always invalidate the cache instead of
	 * recaching updated state
	 */
	public boolean isCacheInvalidationRequired();
	/**
	 * Does this class have a cache.
	 */
	public boolean hasCache();
	/**
	 * Get the cache (optional operation)
	 */
	public CacheConcurrencyStrategy getCache();
	
	/**
	 * Get the user-visible metadata for the class (optional operation)
	 */
	public ClassMetadata getClassMetadata();
	
	/**
	 * Is batch loading enabled?
	 */
	public boolean isBatchLoadable();
	
	/**
	 * Get the current database state of the object, in a "hydrated" form, without
	 * resolving identifiers
	 * @return null if select-before-update is not enabled or not supported
	 */
	public Object[] getCurrentPersistentState(Serializable id, Object version, SessionImplementor session) throws HibernateException;
	/**
	 * Get the current version of the object, or return null if there is no row for
	 * the given identifier. In the case of unversioned data, return any object
	 * if the row exists.
	 */
	public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException;
}








