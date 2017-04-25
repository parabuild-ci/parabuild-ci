//$Id: HibernateServiceMBean.java,v 1.12 2004/08/08 08:55:49 oneovthafew Exp $
package net.sf.hibernate.jmx;

import net.sf.hibernate.HibernateException;

/**
 * Hibernate JMX Management API
 * @see HibernateService
 * @author John Urberg
 */
public interface HibernateServiceMBean {
	
	/**
	 * The Hibernate mapping files (might be overridden by subclasses
	 * that want to specify the mapping files by some other mechanism)
	 * @return String
	 */
	public String getMapResources();
	/**
	 * Specify the Hibernate mapping files
	 * @param mappingFiles
	 */
	public void setMapResources(String mappingFiles);
	/**
	 * Add a mapping file
	 * @param mapResource
	 */
	public void addMapResource(String mapResource);
	
	/**
	 * Set a property
	 * @param property the property name
	 * @param value the property value
	 */
	public void setProperty(String property, String value);
	
	/**
	 * Get a property
	 * @param property the property name
	 * @return the property value
	 */
	public String getProperty(String property);
	
	/**
	 * Display the properties
	 * @return a list of property names and values
	 */
	public String getPropertyList();
	
	/**
	 * The JNDI name of the datasource to use in this <tt>SessionFactory</tt>
	 * @return String
	 */
	public String getDatasource();
	/**
	 * Set the JNDI name of the datasource to use in this <tt>SessionFactory</tt>
	 * @param datasource
	 */
	public void setDatasource(String datasource);
	
	/**
	 * Log into the database with this name
	 * @return String
	 */
	public String getUserName();
	/**
	 * Log into the database with this name
	 * @param userName
	 */
	public void setUserName(String userName);
	
	/**
	 * Log into the database with this password
	 * @return String
	 */
	public String getPassword();
	/**
	 * Log into the database with this password
	 * @param password
	 */
	public void setPassword(String password);
	
	/**
	 * The JNDI name of the dialect class to use in this <tt>SessionFactory</tt>
	 * @return String
	 */
	public String getDialect();
	/**
	 * The name of the dialect class to use in this <tt>SessionFactory</tt>
	 * @param dialect fully qualified class name of <tt>Dialect</tt> subclass
	 * @see net.sf.hibernate.dialect.Dialect
	 */
	public void setDialect(String dialect);
	
	/**
	 * The JNDI name to bind to the <tt>SessionFactory</tt>
	 * @return String
	 */
	public String getJndiName();
	/**
	 * The JNDI name to bind to the <tt>SessionFactory</tt>
	 * @param jndiName
	 */
	public void setJndiName(String jndiName);
	
	/**
	 * The fully qualified class name of the Hibernate <tt>TransactionFactory</tt> implementation
	 * @return the class name
	 * @see net.sf.hibernate.transaction.TransactionFactory
	 */
	public String getTransactionStrategy();
	
	/**
	 * Set the fully qualified class name of the Hibernate <tt>TransactionFactory</tt> implementation
	 * @param txnStrategy the class name
	 * @see net.sf.hibernate.transaction.TransactionFactory
	 */
	public void setTransactionStrategy(String txnStrategy);
	
	/**
	 * The JNDI name of the JTA UserTransaction object (used only be <tt>JTATransaction</tt>).
	 * @return the JNDI name
	 * @see net.sf.hibernate.transaction.JTATransaction
	 */
	public String getUserTransactionName();
	/**
	 * Set the JNDI name of the JTA UserTransaction object (used only by <tt>JTATransaction</tt>).
	 * @param utName the JNDI name
	 * @see net.sf.hibernate.transaction.JTATransaction
	 */
	public void setUserTransactionName(String utName);
	
	/**
	 * Get the strategy for obtaining the JTA <tt>TransactionManager</tt>
	 * @return the class name
	 * @see net.sf.hibernate.transaction.TransactionManagerLookup
	 */
	public String getTransactionManagerLookupStrategy();
	/**
	 * Set the strategy for obtaining the JTA <tt>TransactionManager</tt>
	 * @param lkpStrategy the class name
	 * @see net.sf.hibernate.transaction.TransactionManagerLookup
	 */
	public void setTransactionManagerLookupStrategy(String lkpStrategy);
	
	/**
	 * Set the max outer join fetch depth
	 */
	public void setMaximumFetchDepth(Integer depth);
	/**
	 * Get the max outer join fetch depth
	 */
	public Integer getMaximumFetchDepth();
	
	/**
	 * Set the JDBC fetch size
	 */
	public void setJdbcFetchSize(Integer depth);
	/**
	 * Get the JDBC fetch size
	 */
	public Integer getJdbcFetchSize();
	/**
	 * Set the JDBC batch size
	 */
	public void setJdbcBatchSize(Integer depth);
	/**
	 * Get the JDBC batch size
	 */
	public Integer getJdbcBatchSize();
	
	/**
	 * Set the cache provider class
	 */
	public void setCacheProvider(String cacheProvider);
	/**
	 * Get the cache provider class
	 */
	public String getCacheProvider();
	
	/**
	 * Set the query substitutions string
	 */
	public void setQuerySubstitutions(String substitutions);
	/**
	 * Get the query substitutions string
	 */
	public String getQuerySubstitutions();
	
	/**
	 * Set the default schema name
	 */
	public void setDefaultSchema(String schema);
	/**
	 * Get the default schema name
	 */
	public String getDefaultSchema();

	/**
	 * Set a prefix for all second-level cache regions.
	 */
	public void setCacheRegionPrefix(String cacheRegionPrefix);

	/**
	 * Get the second-level cache region prefix;
	 */
	public String getCacheRegionPrefix();

	/**
	 * Is SQL logging enabled?
	 */
	public boolean getShowSql();
	/**
	 * Enable logging of SQL to console
	 */
	public void setShowSql(boolean showSql);
	
	/**
	 * Is the query cache enabled?
	 */
	public boolean getQueryCacheEnabled();
	/**
	 * Enable/disable the query cache
	 */
	public void setQueryCacheEnabled(boolean enabled);
	
	/**
	 * Is use of scrollable resultsets enabled?
	 */
	public Boolean getScrollableResultSetsEnabled();
	/**
	 * Enable/disable use of scrollable resultsets
	 */
	public void setScrollableResultSetsEnabled(Boolean enabled);
	/**
	 * Is use of JDBC3 <tt>getGeneratedKeys()</tt> enabled?
	 */
	public Boolean getGetGeneratedKeysEnabled();
	/**
	 * Enable/disable use of JDBC3 <tt>getGeneratedKeys()</tt>
	 */
	public void setGetGeneratedKeysEnabled(Boolean enabled);
	/**
	 * Is second-level cache optimized for minimal puts?
	 */
	public boolean getMinimalPutsEnabled();
	/**
	 * Optimize second-level cache for minimal puts
	 */
	public void setMinimalPutsEnabled(boolean enabled);
	/**
	 * Are JDBC batch updates enabled for versioned entities?
	 */
	public boolean getBatchUpdateVersionedEnabled();
	/**
	 * Enabled/disable JDBC batch updates for versioned entities
	 */
	public void setBatchUpdateVersioned(boolean enabled);

	/**
	 * Create the <tt>SessionFactory</tt> and bind to the jndi name on startup
	 */
	public void start() throws HibernateException;
	/**
	 * Unbind the <tt>SessionFactory</tt> or stub from JNDI
	 */
	public void stop();

	/**
	 * Export create DDL to the database
	 * @throws HibernateException
	 */
	public void createSchema() throws HibernateException;
	/**
	 * Export drop DDL to the database
	 * @throws HibernateException
	 */
	public void dropSchema() throws HibernateException;

}






