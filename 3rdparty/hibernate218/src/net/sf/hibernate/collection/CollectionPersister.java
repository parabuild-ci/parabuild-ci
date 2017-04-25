//$Id: CollectionPersister.java,v 1.36 2004/06/04 01:27:37 steveebersole Exp $
package net.sf.hibernate.collection;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.cache.CacheConcurrencyStrategy;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.id.IdentifierGenerator;
import net.sf.hibernate.metadata.CollectionMetadata;
import net.sf.hibernate.type.PersistentCollectionType;
import net.sf.hibernate.type.Type;

/**
 * A strategy for persisting a collection role. Defines a contract between
 * the persistence strategy and the actual persistent collection framework
 * and session. Does not define operations that are required for querying
 * collections, or loading by outer join.<br>
 * <br>
 * Implements persistence of a collection instance while the instance is
 * referenced in a particular role.<br>
 * <br>
 * This class is highly coupled to the <tt>PersistentCollection</tt>
 * hierarchy, since double dispatch is used to load and update collection 
 * elements.<br>
 * <br>
 * May be considered an immutable view of the mapping object
 * 
 * @see QueryableCollection
 * @see PersistentCollection
 * @author Gavin King
 */
public interface CollectionPersister {
	/**
	 * Initialize the given collection with the given key
	 */
	public void initialize(Serializable key, SessionImplementor session) //TODO: add owner argument!!
	throws HibernateException;
	/**
	 * Get the cache
	 */
	public CacheConcurrencyStrategy getCache();
	/**
	 * Is this collection role cacheable
	 */
	public boolean hasCache();
	/**
	 * Get the associated <tt>Type</tt>
	 */
	public PersistentCollectionType getCollectionType();
	/**
	 * Get the "key" type (the type of the foreign key)
	 */
	public Type getKeyType();
	/**
	 * Get the "index" type for a list or map (optional operation)
	 */
	public Type getIndexType();
	/**
	 * Get the "element" type
	 */
	public Type getElementType();
	/**
	 * Return the element class of an array, or null otherwise
	 */
	public Class getElementClass();
	/**
	 * Read the key from a row of the JDBC <tt>ResultSet</tt>
	 */
	public Object readKey(ResultSet rs, SessionImplementor session)
		throws HibernateException, SQLException;
	/**
	 * Read the element from a row of the JDBC <tt>ResultSet</tt>
	 */
	public Object readElement(
		ResultSet rs,
		Object owner,
		SessionImplementor session)
		throws HibernateException, SQLException;
	/**
	 * Read the index from a row of the JDBC <tt>ResultSet</tt>
	 */
	public Object readIndex(ResultSet rs, SessionImplementor session)
		throws HibernateException, SQLException;
	/**
	 * Read the identifier from a row of the JDBC <tt>ResultSet</tt>
	 */
	public Object readIdentifier(
		ResultSet rs,
		SessionImplementor session)
		throws HibernateException, SQLException;
	/**
	 * Write the key to a JDBC <tt>PreparedStatement</tt>
	 */
	public void writeKey(
		PreparedStatement st,
		Serializable key,
		boolean writeOrder,
		SessionImplementor session)
		throws HibernateException, SQLException;
	/**
	 * Write the element to a JDBC <tt>PreparedStatement</tt>
	 */
	public void writeElement(
		PreparedStatement st,
		Object elt,
		boolean writeOrder,
		SessionImplementor session)
		throws HibernateException, SQLException;
	/**
	 * Write the index to a JDBC <tt>PreparedStatement</tt>
	 */
	public void writeIndex(
		PreparedStatement st,
		Object idx,
		boolean writeOrder,
		SessionImplementor session)
		throws HibernateException, SQLException;
	/**
	 * Write the identifier to a JDBC <tt>PreparedStatement</tt>
	 */
	public void writeIdentifier(
		PreparedStatement st,
		Object idx,
		boolean writeOrder,
		SessionImplementor session)
		throws HibernateException, SQLException;
	/**
	 * Is this an array or primitive values?
	 */
	public boolean isPrimitiveArray();
	/**
	 * Is this an array?
	 */
	public boolean isArray();
	/**
	 * Is this a one-to-many association?
	 */
	public boolean isOneToMany();
	/**
	 * Is this an "indexed" collection? (list or map)
	 */
	public boolean hasIndex();
	/**
	 * Is this collection lazyily initialized?
	 */
	public boolean isLazy();
	/**
	 * Is this collection "inverse", so state changes are not
	 * propogated to the database.
	 */
	public boolean isInverse();
	/**
	 * Completely remove the persistent state of the collection
	 */
	public void remove(Serializable id, SessionImplementor session)
		throws HibernateException;
	/**
	 * (Re)create the collection's persistent state
	 */
	public void recreate(
		PersistentCollection collection,
		Serializable key,
		SessionImplementor session)
		throws HibernateException;
	/**
	 * Delete the persistent state of any elements that were removed from
	 * the collection
	 */
	public void deleteRows(
		PersistentCollection collection,
		Serializable key,
		SessionImplementor session)
		throws HibernateException;
	/**
	 * Update the persistent state of any elements that were modified
	 */
	public void updateRows(
		PersistentCollection collection,
		Serializable key,
		SessionImplementor session)
		throws HibernateException;
	/**
	 * Insert the persistent state of any new collection elements
	 */
	public void insertRows(
		PersistentCollection collection,
		Serializable key,
		SessionImplementor session)
		throws HibernateException;
	/**
	 * Get the name of this collection role (the fully qualified class name,
	 * extended by a "property path")
	 */
	public String getRole();
	/**
	 * Get the entity class that "owns" this collection
	 */
	public Class getOwnerClass();
	/**
	 * Get the surrogate key generation strategy (optional operation)
	 */
	public IdentifierGenerator getIdentifierGenerator();
	/**
	 * Get the type of the surrogate key
	 */
	public Type getIdentifierType();
	/**
	 * Does this collection implement "orphan delete"?
	 */
	public boolean hasOrphanDelete();
	/**
	 * Is this an ordered collection? (An ordered collection is
	 * ordered by the initialization operation, not by sorting
	 * that happens in memory, as in the case of a sorted collection.)
	 */
	public boolean hasOrdering();
	/**
	 * Get the "space" that holds the persistent state
	 */
	public Serializable getCollectionSpace();
	
	public CollectionMetadata getCollectionMetadata();
}