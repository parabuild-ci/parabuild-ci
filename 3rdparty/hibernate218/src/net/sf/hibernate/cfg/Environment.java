//$Id: Environment.java,v 1.31 2005/01/29 23:48:58 oneovthafew Exp $
package net.sf.hibernate.cfg;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.util.PropertiesHelper;


/**
 * Provides access to configuration info passed in <tt>Properties</tt> objects.
 * <br><br>
 * Hibernate has two property scopes:
 * <ul>
 * <li><b>Factory-level</b> properties may be passed to the <tt>SessionFactory</tt> when it
 * instantiated. Each instance might have different property values. If no
 * properties are specified, the factory calls <tt>Environment.getProperties()</tt>.
 * <li><b>System-level</b> properties are shared by all factory instances and are always
 * determined by the <tt>Environment</tt> properties.
 * </ul>
 * The only system-level properties are
 * <ul>
 * <li><tt>hibernate.jdbc.use_streams_for_binary</tt>
 * <li><tt>hibernate.cglib.use_reflection_optimizer</tt>
 * </ul>
 * <tt>Environment</tt> properties are populated by calling <tt>System.getProperties()</tt>
 * and then from a resource named <tt>/hibernate.properties</tt> if it exists. System
 * properties override properties specified in <tt>hibernate.properties</tt>.<br>
 * <br>
 * The <tt>SessionFactory</tt> is controlled by the following properties.
 * Properties may be either be <tt>System</tt> properties, properties
 * defined in a resource named <tt>/hibernate.properties</tt> or an instance of 
 * <tt>java.util.Properties</tt> passed to
 * <tt>Configuration.buildSessionFactory()</tt><br>
 * <br>
 * <table>
 * <tr><td><b>property</b></td><td><b>meaning</b></td></tr>
 * <tr>
 *   <td><tt>hibernate.dialect</tt></td>
 *   <td>classname of <tt>net.sf.hibernate.dialect.Dialect</tt> subclass</td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.cache.provider_class</tt></td>
 *   <td>classname of <tt>net.sf.hibernate.cache.CacheProvider</tt>
 *   subclass (if not specified JCS is used)</td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.connection.provider_class</tt></td>
 *   <td>classname of <tt>net.sf.hibernate.connection.ConnectionProvider</tt>
 *   subclass (if not specified hueristics are used)</td>
 * </tr>
 * <tr><td><tt>hibernate.connection.username</tt></td><td>database username</td></tr>
 * <tr><td><tt>hibernate.connection.password</tt></td><td>database password</td></tr>
 * <tr>
 *   <td><tt>hibernate.connection.url</tt></td>
 *   <td>JDBC URL (when using <tt>java.sql.DriverManager</tt>)</td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.connection.driver_class</tt></td>
 *   <td>classname of JDBC driver</td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.connection.isolation</tt></td>
 *   <td>JDBC transaction isolation level (only when using
 *     <tt>java.sql.DriverManager</tt>)
 *   </td>
 * </tr>
 *   <td><tt>hibernate.connection.pool_size</tt></td>
 *   <td>the maximum size of the connection pool (only when using
 *     <tt>java.sql.DriverManager</tt>)
 *   </td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.connection.datasource</tt></td>
 *   <td>databasource JNDI name (when using <tt>javax.sql.Datasource</tt>)</td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.jndi.url</tt></td><td>JNDI <tt>InitialContext</tt> URL</td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.jndi.class</tt></td><td>JNDI <tt>InitialContext</tt> classname</td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.max_fetch_depth</tt></td>
 *   <td>maximum depth of outer join fetching</td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.jdbc.batch_size</tt></td>
 *   <td>enable use of JDBC2 batch API for drivers which support it</td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.jdbc.fetch_size</tt></td>
 *   <td>set the JDBC fetch size</td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.jdbc.use_scrollable_resultset</tt></td>
 *   <td>enable use of JDBC2 scrollable resultsets (you only need this specify
 *   this property when using user supplied connections)</td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.jdbc.use_getGeneratedKeys</tt></td>
 *   <td>enable use of JDBC3 PreparedStatement.getGeneratedKeys() to retrieve 
 *   natively generated keys after insert. Requires JDBC3+ driver and JRE1.4+</td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.hbm2ddl.auto</tt></td>
 *   <td>enable auto DDL export</td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.default_schema</tt></td>
 *   <td>use given schema name for unqualified tables (always optional)</td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.session_factory_name</tt></td>
 *   <td>If set, the factory attempts to bind this name to itself in the
 *   JNDI context. This name is also used to support cross JVM <tt>
 *   Session</tt> (de)serialization.</td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.transaction.manager_lookup_class</tt></td>
 *   <td>classname of <tt>net.sf.hibernate.transaction.TransactionManagerLookup</tt>
 *   implementor</td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.transaction.factory_class</tt></td>
 *   <td>the factory to use for instantiating <tt>Transaction</tt>s.
 *   (Defaults to <tt>JDBCTransactionFactory</tt>.)</td>
 * </tr>
 * <tr>
 *   <td><tt>hibernate.query.substitutions</tt></td><td>query language token substitutions</td>
 * </tr>
 * </table>
 *
 * @see net.sf.hibernate.SessionFactory
 * @author Gavin King
 */
public final class Environment {
	
	public static final String VERSION = "2.1.8";
	
	/**
	 * <tt>ConnectionProvider</tt> implementor to use when obtaining connections
	 */
	public static final String CONNECTION_PROVIDER ="hibernate.connection.provider_class";
	/**
	 * JDBC driver class
	 */
	public static final String DRIVER ="hibernate.connection.driver_class";
	/**
	 * JDBC transaction isolation level
	 */
	public static final String ISOLATION ="hibernate.connection.isolation";
	/**
	 * JDBC URL
	 */
	public static final String URL ="hibernate.connection.url";
	/**
	 * JDBC user
	 */
	public static final String USER ="hibernate.connection.username";
	/**
	 * JDBC password
	 */
	public static final String PASS ="hibernate.connection.password";
	/**
	 * Maximum number of inactive connections for Hibernate's connection pool
	 */
	public static final String POOL_SIZE ="hibernate.connection.pool_size";
	/**
	 * <tt>java.sql.Datasource</tt> JNDI name
	 */
	public static final String DATASOURCE ="hibernate.connection.datasource";
	/**
	 * prefix for arbitrary JDBC connection properties
	 */
	public static final String CONNECTION_PREFIX = "hibernate.connection";
	
	/**
	 * Maximum size for Hibernate's statement cache
	 * 
	 * @deprecated Hibernate no longer has a built-in prepared statement cache
	 */
	public static final String STATEMENT_CACHE_SIZE ="hibernate.statement_cache.size";
	
	/**
	 * JNDI initial context class, <tt>Context.INITIAL_CONTEXT_FACTORY</tt>
	 */
	public static final String JNDI_CLASS ="hibernate.jndi.class";
	/**
	 * JNDI provider URL, <tt>Context.PROVIDER_URL</tt>
	 */
	public static final String JNDI_URL ="hibernate.jndi.url";
	/**
	 * prefix for arbitrary JNDI <tt>InitialContext</tt> properties
	 */
	public static final String JNDI_PREFIX = "hibernate.jndi";
	/**
	 * JNDI name to bind to <tt>SessionFactory</tt>
	 */
	public static final String SESSION_FACTORY_NAME = "hibernate.session_factory_name";
	
	/**
	 * Hibernate SQL <tt>Dialect</tt> class
	 */
	public static final String DIALECT ="hibernate.dialect";
	/**
	 * A default database schema (owner) name to use for unqualified tablenames
	 */
	public static final String DEFAULT_SCHEMA = "hibernate.default_schema";
	
	/**
	 * Enable logging of generated SQL to the console
	 */
	public static final String SHOW_SQL ="hibernate.show_sql";
	/**
	 * Enable deep fetching using outerjoins
	 * @deprecated use <tt>hibernate.max_fetch_depth=0</tt>
	 */
	public static final String USE_OUTER_JOIN ="hibernate.use_outer_join";
	/**
	 * Maximum depth of outerjoin fetching
	 */
	public static final String MAX_FETCH_DEPTH = "hibernate.max_fetch_depth";
	/**
	 * Use <tt>java.io</tt> streams to read / write binary data from / to JDBC
	 */
	public static final String USE_STREAMS_FOR_BINARY = "hibernate.jdbc.use_streams_for_binary";
	/**
	 * Use JDBC scrollable <tt>ResultSet</tt>s. This property is only necessary when there is
	 * no <tt>ConnectionProvider</tt>, ie. the user is supplying JDBC connections.
	 */
	public static final String USE_SCROLLABLE_RESULTSET = "hibernate.jdbc.use_scrollable_resultset";
	/**
	 * Tells the JDBC driver to attempt to retrieve row Id with the JDBC 3.0 PreparedStatement.getGeneratedKeys()
	 * method. In general, performance will be better if this property is set to true and the underlying 
	 * JDBC driver supports getGeneratedKeys().
	 */
	public static final String USE_GET_GENERATED_KEYS = "hibernate.jdbc.use_get_generated_keys";
	/**
	 * Gives the JDBC driver a hint as to the number of rows that should be fetched from the database
	 * when more rows are needed. If <tt>0</tt>, JDBC driver default settings will be used.
	 */
	public static final String STATEMENT_FETCH_SIZE = "hibernate.jdbc.fetch_size";
	/**
	 * Maximum JDBC batch size. A nonzero value enables batch updates.
	 */
	public static final String STATEMENT_BATCH_SIZE = "hibernate.jdbc.batch_size";
	/**
	 * Should versioned data be included in batching?
	 */
	public static final String BATCH_VERSIONED_DATA = "hibernate.jdbc.batch_versioned_data";
	/**
	 * An XSLT resource used to generate "custom" XML
	 */
	public static final String OUTPUT_STYLESHEET ="hibernate.xml.output_stylesheet";
	
	/**
	 * Maximum size of C3P0 connection pool
	 */
	public static final String C3P0_MAX_SIZE = "hibernate.c3p0.max_size";
	/**
	 * Minimum size of C3P0 connection pool
	 */
	public static final String C3P0_MIN_SIZE = "hibernate.c3p0.min_size";
	/**
	 * Maximum idle time for C3P0 connection pool
	 */
	public static final String C3P0_TIMEOUT = "hibernate.c3p0.timeout";
	/**
	 * Maximum size of C3P0 statement cache
	 */
	public static final String C3P0_MAX_STATEMENTS = "hibernate.c3p0.max_statements";
	/**
	 * Number of connections acquired when pool is exhausted
	 */
	public static final String C3P0_ACQUIRE_INCREMENT = "hibernate.c3p0.acquire_increment";
	/**
	 * Idle time before a C3P0 pooled connection is validated
	 */
	public static final String C3P0_IDLE_TEST_PERIOD = "hibernate.c3p0.idle_test_period";
	/**
	 * Should we validate the connection on checkout.
	 * @deprecated use <tt>C3P0_IDLE_TEST_PERIOD</tt>
	 */
	public static final String C3P0_VALIDATE_CONNECTION = "hibernate.c3p0.validate";
	
	/**
	 * Maximum number of checked out connections for DBCP connection pool
	 */
	public static final String DBCP_MAXACTIVE = "hibernate.dbcp.maxActive";
	/**
	 * Maximum number of idle connections for DBCP connection pool
	 */
	public static final String DBCP_MAXIDLE = "hibernate.dbcp.maxIdle";
	/**
	 * Maximum idle time for connections in DBCP connection pool (ms)
	 */
	public static final String DBCP_MAXWAIT = "hibernate.dbcp.maxWait";
	/**
	 * Action to take in case of an exhausted DBCP connection pool ( 0 = fail, 1 = block, 2= grow)
	 */
	public static final String DBCP_WHENEXHAUSTED = "hibernate.dbcp.whenExhaustedAction";
	/**
	 * Validate connection when borrowing connection from pool (optional, true or false)
	 */
	public static final String DBCP_VALIDATION_ONBORROW = "hibernate.dbcp.testOnBorrow";
	/**
	 * Validate connection when returning connection to pool (optional, true or false)
	 */
	public static final String DBCP_VALIDATION_ONRETURN = "hibernate.dbcp.testOnReturn";
	/**
	 * Query to execute for connection validation (optional, requires either 
	 * <tt>DBCP_VALIDATION_ONBORROW</tt> or <tt>DBCP_VALIDATION_ONRETURN</tt>)
	 */
	public static final String DBCP_VALIDATION_QUERY = "hibernate.dbcp.validationQuery";
	/**
	 * Maximum number of checked out statements for DBCP
	 */
	public static final String DBCP_PS_MAXACTIVE = "hibernate.dbcp.ps.maxActive";
	/**
	 * Maximum number of idle statements for DBCP
	 */
	public static final String DBCP_PS_MAXIDLE = "hibernate.dbcp.ps.maxIdle";
	/**
	 * Maximum idle time for statements in DBCP (ms)
	 */
	public static final String DBCP_PS_MAXWAIT = "hibernate.dbcp.ps.maxWait";
	/**
	 * Action to take in case of an exhausted DBCP statement pool ( 0 = fail, 1 = block, 2= grow)
	 */
	public static final String DBCP_PS_WHENEXHAUSTED = "hibernate.dbcp.ps.whenExhaustedAction";
	
	/**
	 * Proxool/Hibernate property prefix
	 */
	public static final String PROXOOL_PREFIX = "hibernate.proxool";
	/**
	 * Proxool property to configure the Proxool Provider using an XML (<tt>/path/to/file.xml</tt>)
	 */  
	public static final String PROXOOL_XML = "hibernate.proxool.xml";
	/**
	 * Proxool property to configure the Proxool Provider  using a properties file (<tt>/path/to/proxool.properties</tt>)
	 */    
	public static final String PROXOOL_PROPERTIES = "hibernate.proxool.properties";
	/**
	 * Proxool property to configure the Proxool Provider from an already existing pool (<tt>true</tt> / <tt>false</tt>)
	 */    
	public static final String PROXOOL_EXISTING_POOL = "hibernate.proxool.existing_pool";
	/**
	 * Proxool property with the Proxool pool alias to use
	 * (Required for <tt>PROXOOL_EXISTING_POOL</tt>, <tt>PROXOOL_PROPERTIES</tt>, or 
	 * <tt>PROXOOL_XML</tt>)
	 */  
	public static final String PROXOOL_POOL_ALIAS = "hibernate.proxool.pool_alias";  	

	/**
	 * <tt>TransactionFactory</tt> implementor to use for creating <tt>Transaction</tt>s
	 */
	public static final String TRANSACTION_STRATEGY = "hibernate.transaction.factory_class";
	/**
	 * <tt>TransactionManagerLookup</tt> implementor to use for obtaining the <tt>TransactionManager</tt>
	 */
	public static final String TRANSACTION_MANAGER_STRATEGY = "hibernate.transaction.manager_lookup_class";
	/**
	 * JNDI name of JTA <tt>UserTransaction</tt> object
	 */
	public static final String USER_TRANSACTION = "jta.UserTransaction";
	
	/**
	 * The <tt>CacheProvider</tt> implementation class
	 */
	public static final String CACHE_PROVIDER = "hibernate.cache.provider_class";
	/**
	 * Should query caching be truned on?
	 */
	public static final String USE_QUERY_CACHE = "hibernate.cache.use_query_cache";
	/**
	 * The <tt>QueryCacheFactory</tt> implementation class.
	 */
	public static final String QUERY_CACHE_FACTORY = "hibernate.cache.query_cache_factory";
	/**
	 * The <tt>CacheProvider</tt> region name prefix
	 */
	public static final String CACHE_REGION_PREFIX = "hibernate.cache.region_prefix";
	/**
	 * Optimize the cache for mimimal puts instead of minimal gets
	 */
	public static final String USE_MINIMAL_PUTS = "hibernate.cache.use_minimal_puts";

	/**
	 * Use CGLIB <tt>MetaClass</tt> to optimize property access
	 */
	public static final String USE_REFLECTION_OPTIMIZER = "hibernate.cglib.use_reflection_optimizer";
	
	/**
	 * A comma-seperated list of token substitutions to use when translating a Hibernate
	 * query to SQL
	 */
	public static final String QUERY_SUBSTITUTIONS = "hibernate.query.substitutions";
	/**
	 * A comma-seperated list of packages that need not be specified in a query
	 * @deprecated
	 */
	public static final String QUERY_IMPORTS = "hibernate.query.imports";
	/**
	 * Auto export/update schema using hbm2ddl tool. Valid values are <tt>update</tt>, <tt>create</tt>
	 * and <tt>create-drop</tt>.
	 */
	public static final String HBM2DDL_AUTO = "hibernate.hbm2ddl.auto";

	/**
	 * The {@link net.sf.hibernate.exception.SQLExceptionConverter} to use for converting SQLExceptions
	 * to Hibernate's JDBCException hierarchy.  The default is to use the configured
	 * {@link net.sf.hibernate.dialect.Dialect}'s preferred SQLExceptionConverter.
	 */
	public static final String SQL_EXCEPTION_CONVERTER = "hibernate.sql_exception_converter";

	/**
	 * Should Hibernate wrap result sets in order to speed up column name lookups.
	 */
	public static final String WRAP_RESULT_SETS = "hibernate.wrap_result_sets";


	//Obsolete properties:
	
	private static final String OUTPUT_STYLESHEET_OLD ="hibernate.output_stylesheet";
	private static final String CONNECTION_PROVIDER_OLD ="hibernate.connection_provider";
	private static final String DRIVER_OLD ="hibernate.driver";
	private static final String ISOLATION_OLD ="hibernate.isolation";
	private static final String USER_OLD ="hibernate.username";
	private static final String PASS_OLD ="hibernate.password";
	private static final String POOL_SIZE_OLD ="hibernate.pool_size";
	private static final String STATEMENT_CACHE_SIZE_OLD ="hibernate.statement_cache_size";
	private static final String DATASOURCE_OLD ="hibernate.datasource";
	private static final String TRANSACTION_STRATEGY_OLD = "hibernate.transaction_factory";
	private static final String URL_OLD ="hibernate.url";
	private static final String USE_STREAMS_FOR_BINARY_OLD = "hibernate.use_streams_for_binary";
	private static final String STATEMENT_FETCH_SIZE_OLD = "hibernate.statement.fetch_size";
	private static final String USE_SCROLLABLE_RESULTSET_OLD = "hibernate.use_scrollable_resultset";
	/**
	 * Use JDBC2 batch updates
	 * @deprecated
	 */
	public static final String USE_JDBC_BATCH = "hibernate.use_jdbc_batch";
	
	private static final boolean ENABLE_BINARY_STREAMS;
	private static final boolean ENABLE_REFLECTION_OPTIMIZER;
	private static final boolean JVM_SUPPORTS_LINKED_HASH_COLLECTIONS;
	private static final boolean JVM_HAS_TIMESTAMP_BUG;
	private static final boolean JVM_HAS_JDK14_TIMESTAMP;
	private static final boolean JVM_SUPPORTS_GET_GENERATED_KEYS;
	
	private static final Properties GLOBAL_PROPERTIES;
	private static final HashMap ISOLATION_LEVELS = new HashMap();
	private static final Map OBSOLETE_PROPERTIES = new HashMap();
	
	private static final Log log = LogFactory.getLog(Environment.class);
	
	/**
	 * Issues warnings to the user when any obsolete property names are used.
	 */
	public static void verifyProperties(Properties props) {
		Iterator iter = props.keySet().iterator();
		while ( iter.hasNext() ) {
			Object oldProp = iter.next();
			Object newProp = OBSOLETE_PROPERTIES.get(oldProp);
			if ( newProp!=null ) log.warn("Usage of obsolete property: " + oldProp + " no longer supported, use: " + newProp);
		}
	}
	
	static {
		
		log.info("Hibernate " + VERSION);
		
		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_NONE), "NONE" );
		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_READ_UNCOMMITTED), "READ_UNCOMMITTED" );
		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_READ_COMMITTED), "READ_COMMITTED" );
		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_REPEATABLE_READ), "REPEATABLE_READ" );
		ISOLATION_LEVELS.put( new Integer(Connection.TRANSACTION_SERIALIZABLE), "SERIALIZABLE" );
		
		OBSOLETE_PROPERTIES.put(CONNECTION_PROVIDER_OLD, CONNECTION_PROVIDER);
		OBSOLETE_PROPERTIES.put(DRIVER_OLD, DRIVER);
		OBSOLETE_PROPERTIES.put(ISOLATION_OLD, ISOLATION);
		OBSOLETE_PROPERTIES.put(URL_OLD, URL);
		OBSOLETE_PROPERTIES.put(PASS_OLD, PASS);
		OBSOLETE_PROPERTIES.put(USER_OLD, USER);
		OBSOLETE_PROPERTIES.put(POOL_SIZE_OLD, POOL_SIZE);
		OBSOLETE_PROPERTIES.put(STATEMENT_CACHE_SIZE_OLD, STATEMENT_CACHE_SIZE);
		OBSOLETE_PROPERTIES.put(DATASOURCE_OLD, DATASOURCE);
		OBSOLETE_PROPERTIES.put(TRANSACTION_STRATEGY_OLD, TRANSACTION_STRATEGY);
		OBSOLETE_PROPERTIES.put(OUTPUT_STYLESHEET_OLD, OUTPUT_STYLESHEET);
		OBSOLETE_PROPERTIES.put(USE_JDBC_BATCH, STATEMENT_BATCH_SIZE);
		OBSOLETE_PROPERTIES.put(USE_SCROLLABLE_RESULTSET_OLD, USE_SCROLLABLE_RESULTSET);
		OBSOLETE_PROPERTIES.put(USE_STREAMS_FOR_BINARY_OLD, USE_STREAMS_FOR_BINARY);
		OBSOLETE_PROPERTIES.put(STATEMENT_FETCH_SIZE_OLD, STATEMENT_FETCH_SIZE);
		
		GLOBAL_PROPERTIES = new Properties();
		GLOBAL_PROPERTIES.setProperty( USE_REFLECTION_OPTIMIZER, Boolean.TRUE.toString() );
		
		InputStream stream = Environment.class.getResourceAsStream("/hibernate.properties");
		if ( stream==null ) {
			log.info("hibernate.properties not found");
		}
		else {
			try {
				GLOBAL_PROPERTIES.load(stream);
				log.info("loaded properties from resource hibernate.properties: " + GLOBAL_PROPERTIES);
			}
			catch (Exception e) {
				log.error("problem loading properties from hibernate.properties");
			} 
			finally {
				try{
					stream.close();
				} 
				catch (IOException ioe){
					log.error("could not close stream on hibernate.properties", ioe);	
				}
			}
		}
		try {
			GLOBAL_PROPERTIES.putAll( System.getProperties() );
		} catch (SecurityException se) {
			log.warn("could not copy system properties. System properties will be ignored.");
		}
		
		verifyProperties(GLOBAL_PROPERTIES);
		
		ENABLE_BINARY_STREAMS = PropertiesHelper.getBoolean(USE_STREAMS_FOR_BINARY, GLOBAL_PROPERTIES);
		ENABLE_REFLECTION_OPTIMIZER = PropertiesHelper.getBoolean(USE_REFLECTION_OPTIMIZER, GLOBAL_PROPERTIES);
		
		if (ENABLE_BINARY_STREAMS) log.info("using java.io streams to persist binary types");
		if (ENABLE_REFLECTION_OPTIMIZER) log.info("using CGLIB reflection optimizer");

		boolean getGeneratedKeysSupport;
		try {
			Statement.class.getMethod("getGeneratedKeys", null);
			getGeneratedKeysSupport = true;
		}
		catch (NoSuchMethodException nsme) {
			getGeneratedKeysSupport = false;
		}		
		JVM_SUPPORTS_GET_GENERATED_KEYS = getGeneratedKeysSupport;
		if (!JVM_SUPPORTS_GET_GENERATED_KEYS) log.info("JVM does not support Statement.getGeneratedKeys()");
		
		boolean linkedHashSupport;
		try {
			Class.forName("java.util.LinkedHashSet");
			linkedHashSupport = true;
		}
		catch (ClassNotFoundException cnfe) {
			linkedHashSupport = false;
		}
		JVM_SUPPORTS_LINKED_HASH_COLLECTIONS = linkedHashSupport;
		if (!JVM_SUPPORTS_LINKED_HASH_COLLECTIONS) log.info("JVM does not support LinkedHasMap, LinkedHashSet - ordered maps and sets disabled");
		
		JVM_HAS_TIMESTAMP_BUG = new Timestamp(123456789).getTime() != 123456789;
		if (JVM_HAS_TIMESTAMP_BUG) log.info("using workaround for JVM bug in java.sql.Timestamp");
		Timestamp t = new Timestamp(0);
		t.setNanos(5 * 1000000);
		JVM_HAS_JDK14_TIMESTAMP = t.getTime() == 5;
		if (JVM_HAS_JDK14_TIMESTAMP) log.info("using JDK 1.4 java.sql.Timestamp handling");
	}
	
	/**
	 * Does this JVM support dynamic proxies. (Now return true because CGLIB
	 * proxies work on all supported JDK)
	 */
	public static boolean jvmSupportsProxies() {
		return true;//jvmSupportsProxies;
	}
	
	/**
	 * Does this JVM have the IBM JDK 1.3.1. The bug is <tt>new Timestamp(x).getTime()!=x</tt>.
	 */
	public static boolean jvmHasTimestampBug() {
		return JVM_HAS_TIMESTAMP_BUG;
	}
	
	/**
	 * Does this JVM handle <tt>Timestamp</tt> in the JDK 1.4 compliant way? 
	 */
	public static boolean jvmHasJDK14Timestamp() {
		return JVM_HAS_JDK14_TIMESTAMP;
	}

	/**
	 * Does this JVM support <tt>LinkedHashSet</tt>, <tt>LinkedHashMap</tt>.
	 * @see java.util.LinkedHashSet
	 * @see java.util.LinkedHashMap
	 */
	public static boolean jvmSupportsLinkedHashCollections() {
		return JVM_SUPPORTS_LINKED_HASH_COLLECTIONS;
	}
	
	public static boolean jvmSupportsGetGeneratedKeys() {
		return JVM_SUPPORTS_GET_GENERATED_KEYS;
	}
	
	/**
	 * Should we use streams to bind binary types to JDBC IN parameters.
	 * Property <tt>hibernate.jdbc.use_streams_for_binary</tt>.
	 * @see Environment#USE_STREAMS_FOR_BINARY
	 */
	public static boolean useStreamsForBinary() {
		return ENABLE_BINARY_STREAMS;
	}
	
	/**
	 * Should we use CGLIB reflection optimizer.
	 * Property <tt>hibernate.jdbc.use_refection_optimizer</tt>.
	 * @see Environment#USE_REFLECTION_OPTIMIZER
	 */
	public static boolean useReflectionOptimizer() {
		return ENABLE_REFLECTION_OPTIMIZER;
	}
	
	private Environment() { throw new UnsupportedOperationException(); }
	
	/**
	 * Return <tt>System</tt> properties, extended by any properties specified
	 * in <tt>hibernate.properties</tt>.
	 * @return Properties
	 */
	public static Properties getProperties() {
		Properties copy = new Properties();
		copy.putAll(GLOBAL_PROPERTIES);
		return copy;
	}
	
	/**
	 * Get the name of a JDBC transaction isolation level
	 *
	 * @see java.sql.Connection
	 * @param isolation as defined by <tt>java.sql.Connection</tt>
	 * @return a human-readable name
	 */
	public static String isolationLevelToString(int isolation) {
		return (String) ISOLATION_LEVELS.get( new Integer(isolation) );
	}
	
}







