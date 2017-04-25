//$Id: SessionFactoryImplementor.java,v 1.17 2004/11/26 05:20:01 steveebersole Exp $
package net.sf.hibernate.engine;

import javax.transaction.TransactionManager;


import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.persister.ClassPersister;
import net.sf.hibernate.cache.QueryCache;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.connection.ConnectionProvider;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.type.Type;

/**
 * Defines the internal contract between the <tt>SessionFactory</tt> and other parts of
 * Hibernate such as implementors of <tt>Type</tt>.
 * 
 * @see net.sf.hibernate.SessionFactory
 * @see net.sf.hibernate.impl.SessionFactoryImpl
 * @author Gavin King
 */
public interface SessionFactoryImplementor extends Mapping, SessionFactory {
	
	/**
	 * Get the persister for a class
	 */
	public ClassPersister getPersister(Class clazz) throws MappingException;
	/**
	 * Get the persister for the named class
	 */
	public ClassPersister getPersister(String className) throws MappingException;
	/**
	 * Get the persister object for a collection role
	 */
	public CollectionPersister getCollectionPersister(String role) throws MappingException;

	/**
	 * Is outerjoin fetching enabled?
	 */
	public boolean isOuterJoinedFetchEnabled();
	/**
	 * Are scrollable <tt>ResultSet</tt>s supported?
	 */
	public boolean isScrollableResultSetsEnabled();
	/**
	 * Is <tt>PreparedStatement.getGeneratedKeys</tt> supported?
	 */
	public boolean isGetGeneratedKeysEnabled();
	/**
	 * Get the database schema specified in <tt>hibernate.default_schema</tt>
	 */
	public String getDefaultSchema();
	/**
	 * Get the SQL <tt>Dialect</tt>
	 */
	public Dialect getDialect();
	
	/**
	 * Get the return types of a query
	 */
	public Type[] getReturnTypes(String queryString) throws HibernateException;
	
	/**
	 * Get the connection provider
	 */
	public ConnectionProvider getConnectionProvider();
	/**
	 * Get the names of all persistent classes that implement/extend the given interface/class
	 */
	public String[] getImplementors(Class clazz);
	/**
	 * Get a class name, using query language imports
	 */
	public String getImportedClassName(String name);
	
	/**
	 * Get the JDBC batch size
	 */
	public int getJdbcBatchSize();
	
	/**
	 * Get the JDBC fetch size
	 */
	public Integer getJdbcFetchSize();
	
	/**
	 * Get the maxmimum depth of outer join fetching
	 */
	public Integer getMaximumFetchDepth();
	
	/**
	 * Get the JTA transaction manager
	 */
	public TransactionManager getTransactionManager();
	
	/**
	 * Are we logging SQL to the console?
	 */
	public boolean isShowSqlEnabled();
	
	/**
	 * Get the default query cache
	 */
	public QueryCache getQueryCache();
	/**
	 * Get a particular named query cache, or the default cache
	 * @param regionName the name of the cache region, or null for the default query cache
	 * @return the existing cache, or a newly created cache if none by that region name
	 */
	public QueryCache getQueryCache(String regionName) throws HibernateException;
	/**
	 * If query caching enabled?
	 */
	public boolean isQueryCacheEnabled();

	/**
	 * Should versioned data be included in jdbc batches?
	 *
	 * @return true if versioned data should be batched.
	 */
	boolean isJdbcBatchVersionedData();

	/**
	 * Should Hibernate wrap result sets in order to speed up column name lookups?
	 *
	 * @return true if result sets should get wrapped; false otherwise.
	 */
	boolean isWrapResultSetsEnabled();
}






