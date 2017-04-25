//$Id: HibernateService.java,v 1.15 2004/08/08 07:55:18 oneovthafew Exp $
package net.sf.hibernate.jmx;

import java.util.Properties;

import javax.naming.InitialContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.util.NamingHelper;
import net.sf.hibernate.util.PropertiesHelper;


/**
 * Implementation of <tt>HibernateServiceMBean</tt>. Creates a
 * <tt>SessionFactory</tt> and binds it to the specified JNDI name.<br>
 * <br>
 * All mapping documents are loaded as resources by the MBean.
 * @see HibernateServiceMBean
 * @see net.sf.hibernate.SessionFactory
 * @author John Urberg
 */
public class HibernateService implements HibernateServiceMBean {
	
	private static final Log log = LogFactory.getLog(HibernateServiceMBean.class);
	
	private String mapResources;
	private String boundName;
	private Properties properties = new Properties();
	
	/**
	 * The name of the current bean
	 * @return String
	 */
	public String getName() {
		return getProperty(Environment.SESSION_FACTORY_NAME);
	}
	
	public String getMapResources() {
		return mapResources;
	}
	
	public void setMapResources(String mapResources) {
		if (mapResources==null) {
			this.mapResources=null;
		}
		else {
			this.mapResources = mapResources.trim();
		}
	}
	
	public void addMapResource(String mapResource) {
		if ( mapResources==null || mapResources.length()==0 ) {
			mapResources = mapResource.trim();
		}
		else {
			mapResources += ", " + mapResource.trim();
		}
	}
	
	protected void setProperty(String propName, boolean value) {
		setProperty( propName, value ? "true" : "false" ); //Boolean.toString() only in JDK1.4
	}
	
	protected void setProperty(String propName, Integer value) {
		setProperty( propName, value==null ? null : value.toString() ); //Boolean.toString() only in JDK1.4
	}
	
	public String getDatasource() {
		return getProperty(Environment.DATASOURCE);
	}
	
	public void setDatasource(String datasource) {
		setProperty(Environment.DATASOURCE, datasource);
	}
	
	public String getDialect() {
		return getProperty(Environment.DIALECT);
	}
	
	public void setDialect(String dialect) {
		setProperty(Environment.DIALECT, dialect);
	}
	
	public String getJndiName() {
		return getProperty(Environment.SESSION_FACTORY_NAME);
	}
	
	public void setJndiName(String jndiName) {
		setProperty(Environment.SESSION_FACTORY_NAME, jndiName);
	}
	
	public boolean getShowSql() {
		return getBooleanProperty(Environment.SHOW_SQL, false);
	}
	
	public void setShowSql(boolean showSql) {
		setProperty(Environment.SHOW_SQL, showSql);
	}
	
	public String getUserName() {
		return getProperty(Environment.USER);
	}
	
	public void setUserName(String userName) {
		setProperty(Environment.USER, userName);
	}
	
	public String getPassword() {
		return getProperty(Environment.PASS);
	}
	
	public void setPassword(String password) {
		setProperty(Environment.PASS, password);
	}
	
	private static String[] parseResourceList(String resourceList) {
		return PropertiesHelper.toStringArray(resourceList, " ,\n\t\r\f");
	}
	
	public void start() throws HibernateException {
		boundName = getJndiName();
		try {
			buildSessionFactory();
		}
		catch (HibernateException he) {
			log.info( "Could not build SessionFactory using the MBean classpath - will try again using client classpath: " + he.getMessage() );
			log.debug("Error was", he);
			new SessionFactoryStub(this);
		}
	}
	
	public void stop() {
		log.info("stopping service");
		try {
			InitialContext context = NamingHelper.getInitialContext( getProperties() );
			( (SessionFactory) context.lookup(boundName) ).close();
			//context.unbind(boundName);
		}
		catch (Exception e) {
			log.warn("exception while stopping service", e);
		}
	}
	
	private Configuration getConfiguration() throws HibernateException {
		Configuration cfg = new Configuration().addProperties( getProperties() );
		String[] mappingFiles = parseResourceList( getMapResources() );
		for ( int i=0; i<mappingFiles.length; i++ ) {
			cfg.addResource( mappingFiles[i], Thread.currentThread().getContextClassLoader() );
		}
		return cfg;
	}

	SessionFactory buildSessionFactory() throws HibernateException {
		log.info( "starting service at JNDI name: " + boundName );
		log.info( "service properties: " + properties );
		return getConfiguration().buildSessionFactory();
	}

	public String getTransactionStrategy() {
		return getProperty(Environment.TRANSACTION_STRATEGY);
	}
	
	public String getUserTransactionName() {
		return getProperty(Environment.USER_TRANSACTION);
	}
	public void setTransactionStrategy(String txnStrategy) {
		setProperty(Environment.TRANSACTION_STRATEGY, txnStrategy);
	}
	
	public void setUserTransactionName(String utName) {
		setProperty(Environment.USER_TRANSACTION, utName);
	}
	
	public String getTransactionManagerLookupStrategy() {
		return getProperty(Environment.TRANSACTION_MANAGER_STRATEGY);
	}
	
	public void setTransactionManagerLookupStrategy(String lkpStrategy) {
		setProperty(Environment.TRANSACTION_MANAGER_STRATEGY, lkpStrategy);
	}
	
	protected Properties getProperties() {
		return properties;
	}
	public String getPropertyList() {
		return getProperties().toString();
	}
	
	public String getProperty(String property) {
		return properties.getProperty(property);
	}

	public void setProperty(String property, String value) {
		properties.setProperty(property, value);
	}

	private void setBooleanProperty(String property, boolean value) {
		setProperty(property, value ? "true" : "false");
	}
	
	private void setProperty(String property, Object value) {
		if (value==null) {
			setProperty( property, (String) null );
		}
		else {
			setProperty( property, value.toString() );
		}
	}
	
	private boolean getBooleanProperty(String property, boolean defaultVal) {
		String strVal = getProperty(property);
		return strVal==null ?
			defaultVal :
			Boolean.valueOf(strVal).booleanValue();
	}

	private Boolean getBooleanProperty(String property) {
		String strVal = getProperty(property);
		return strVal==null ?
			null :
			Boolean.valueOf(strVal);
	}

	private Integer getIntegerProperty(String property) {
		String strVal = getProperty(property);
		return strVal==null ?
			null :
			new Integer(strVal);
	}

	public void dropSchema() throws HibernateException {
		new SchemaExport( getConfiguration() ).drop(false, true);
	}

	public void createSchema() throws HibernateException {
		new SchemaExport( getConfiguration() ).create(false, true);
	}

	public boolean getBatchUpdateVersionedEnabled() {
		return getBooleanProperty(Environment.BATCH_VERSIONED_DATA, false);
	}

	public String getCacheProvider() {
		return getProperty(Environment.CACHE_PROVIDER);
	}

	public String getCacheRegionPrefix() {
		return getProperty(Environment.CACHE_REGION_PREFIX);
	}

	public String getDefaultSchema() {
		return getProperty(Environment.DEFAULT_SCHEMA);
	}

	public Boolean getGetGeneratedKeysEnabled() {
		return getBooleanProperty(Environment.USE_GET_GENERATED_KEYS);
	}

	public Integer getJdbcBatchSize() {
		return getIntegerProperty(Environment.STATEMENT_BATCH_SIZE);
	}

	public Integer getJdbcFetchSize() {
		return getIntegerProperty(Environment.STATEMENT_FETCH_SIZE);
	}

	public Integer getMaximumFetchDepth() {
		return getIntegerProperty(Environment.MAX_FETCH_DEPTH);
	}

	public boolean getMinimalPutsEnabled() {
		return getBooleanProperty(Environment.USE_MINIMAL_PUTS, false);
	}

	public boolean getQueryCacheEnabled() {
		return getBooleanProperty(Environment.USE_QUERY_CACHE, false);
	}

	public String getQuerySubstitutions() {
		return getProperty(Environment.QUERY_SUBSTITUTIONS);
	}

	public Boolean getScrollableResultSetsEnabled() {
		return getBooleanProperty(Environment.USE_SCROLLABLE_RESULTSET);
	}

	public void setBatchUpdateVersioned(boolean enabled) {
		setBooleanProperty(Environment.BATCH_VERSIONED_DATA, enabled);
	}

	public void setCacheProvider(String providerClassName) {
		setProperty(Environment.CACHE_PROVIDER, providerClassName);
	}

	public void setCacheRegionPrefix(String prefix) {
		setProperty(Environment.CACHE_REGION_PREFIX, prefix);
	}

	public void setDefaultSchema(String schema) {
		setProperty(Environment.DEFAULT_SCHEMA, schema);
	}

	public void setGetGeneratedKeysEnabled(Boolean enabled) {
		setProperty(Environment.USE_GET_GENERATED_KEYS, enabled);
	}

	public void setJdbcBatchSize(Integer batchSize) {
		setProperty(Environment.STATEMENT_BATCH_SIZE, batchSize);
	}

	public void setJdbcFetchSize(Integer fetchSize) {
		setProperty(Environment.STATEMENT_BATCH_SIZE, fetchSize);
	}

	public void setMaximumFetchDepth(Integer fetchDepth) {
		setProperty(Environment.MAX_FETCH_DEPTH, fetchDepth);
	}

	public void setMinimalPutsEnabled(boolean enabled) {
		setBooleanProperty(Environment.USE_MINIMAL_PUTS, enabled);
	}

	public void setQueryCacheEnabled(boolean enabled) {
		setBooleanProperty(Environment.USE_QUERY_CACHE, enabled);
	}

	public void setQuerySubstitutions(String querySubstitutions) {
		setProperty(Environment.QUERY_SUBSTITUTIONS, querySubstitutions);
	}

	public void setScrollableResultSetsEnabled(Boolean enabled) {
		setProperty(Environment.USE_SCROLLABLE_RESULTSET, enabled);
	}

}







