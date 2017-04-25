package net.sf.hibernate.jca;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionManager;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.security.auth.Subject;
import javax.sql.DataSource;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.util.PropertiesHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Factory for creating Factories, connections and matching connections
 *
 * Does not currently support unmanaged environments
 */
public class ManagedConnectionFactoryImpl
implements ManagedConnectionFactory, Serializable {
	
	/*
	 * @todo JCA 1.0, 5.10.1
	 * A resource adapter is required to provide support for basic error
	 * logging and tracing by implementing the following methods:
	 *   - ManagedConnectionFactory.set/getLogWriter
	 * ManagedConnection.set/getLogWriter
	 */
	private static final Log log =
	LogFactory.getLog(ManagedConnectionFactoryImpl.class);
	
	//
	// Resource adaptor configuration properties
	//
	
	/** SQL dialect */
	private String dialect;
	
	/** O/R mappings */
	private String mapResources;
	
	/** JNDI name of a datasource */
	private String datasourceJndi;
	
	/** Hibernate properties */
	private String properties;
	
	//
	//
	//
	
	private PrintWriter out;
	
	private DataSource datasource;
	
	private SessionFactory sessionFactory;
	
	/**
	 * Creates a Hibernate SessionFactory for the container
	 *
	 * @throws ResourceException can't create Session Factory
	 *      (thrown with linked exception)
	 */
	public Object createConnectionFactory(final ConnectionManager cxManager)
	throws ResourceException {
		log("createConnectionFactory with connection manager - " + cxManager);
		
		// initialize datasource and session factory
		initialize();
		
		Object obj = null;
		try {
			obj = new JCASessionFactoryImpl(this, cxManager);
		}
		catch (HibernateException hbe) {
			final String message =
			"Got Hibernate exception when trying to create " +
			"Connection Factory";
			log(message, hbe);
			final ResourceException re = new ResourceException(message);
			re.setLinkedException(hbe);
			throw re;
		}
		return obj;
	}
	
	/**
	 * Initializes datasource (a.k.a JDBC connection factory) and Hibernate
	 * session factory. This method is synchonized, because a container does
	 * not have to serialize createConnectionFactory/createManagedConnection
	 * calls.
	 */
	private synchronized void initialize() throws ResourceException {
		// locate datasource
		if (datasource == null) {
			try {
				Context ctx = new InitialContext();
				try {
					datasource = (DataSource) ctx.lookup(datasourceJndi);
				} 
				finally {
					ctx.close();
				}
			}
			catch (NamingException e) {
				final String message =
				"Cannot locate DataSource " + datasourceJndi;
				log(message, e);
				final ResourceException re = new ResourceException(message);
				re.setLinkedException(e);
				throw re;
			}
		}
		
		// initialize session factory
		if (sessionFactory == null) {
			try {
				final String delim = " ,\n\t\r\f";
				final Properties hibProperties = new Properties();
				// load this.properties that will override global properties
				if (properties != null && properties.length() > 0) {
					try {
						hibProperties.load(new ByteArrayInputStream(properties.getBytes("ISO-8859-1")));
					} catch (IOException e1) {
						throw new HibernateException("Error processing JCA Hibernate properties", e1);
					}
				}
				hibProperties.setProperty(Environment.DIALECT, dialect);
				hibProperties.setProperty(Environment.DATASOURCE, datasourceJndi);
				
				Configuration cfg = new Configuration().addProperties(hibProperties);
				
				// taken from HibernateService, maybe factor out for JCA 1.5
				String[] mappingFiles = PropertiesHelper.toStringArray(mapResources, delim);
				for ( int i=0; i<mappingFiles.length; i++ ) {
					cfg.addResource( mappingFiles[i] );
				}
				sessionFactory = cfg.buildSessionFactory();
			}
			catch (HibernateException e) {
				final String message =
				"Cannot create Hibernate session factory";
				log(message, e);
				final ResourceException re = new ResourceException(message);
				re.setLinkedException(e);
				throw re;
			}
		}
	}
	
	/**
	 * NOT SUPPORTED
	 *
	 * @throws NotSupportedException Unmanaged environments are not currently supported
	 */
	public Object createConnectionFactory() throws ResourceException {
		throw new NotSupportedException("Resource Adapter does not support " +
		"an un-managed environment");
	}
	
	/**
	 * Creates a ManagedConnection
	 */
	public ManagedConnection createManagedConnection(
		final Subject subject, final ConnectionRequestInfo info
	) {
			log("createManagedConnection called");
			
			return new ManagedConnectionImpl(this);
		}
		
		public void setLogWriter(final PrintWriter out) throws ResourceException {
			this.out = out;
		}
		
		public PrintWriter getLogWriter() throws ResourceException {
			return this.out;
		}
		
		public ManagedConnection matchManagedConnections(
			final Set mcs, 
			final Subject subject, 
			final ConnectionRequestInfo cri
		) throws ResourceException {
			
			for (Iterator i = mcs.iterator(); i.hasNext();) {
				Object o = i.next();
				if (o instanceof ManagedConnectionImpl) {
					// all idle connections are identical
					return (ManagedConnectionImpl) o;
				}
			}
			return null;
		}
		
		/**
		 * JCA 1.0, 5.5.3
		 * It is required that the ManagedConnectionFactory implementation class
		 * extend the implementation of the hashCode and equals methods defined
		 * in the java.lang.Object class.
		 *
		 * @return hashcode computed according to recommendations in Effective Java.
		 */
		public int hashCode() {
			int result = 17;
			result = result * 37 + ( (datasourceJndi == null) ? 0 : datasourceJndi.hashCode() );
			result = result * 37 + ( (dialect == null) ? 0 : dialect.hashCode() );
			result = result * 37 + ( (mapResources == null) ? 0 : mapResources.hashCode() );
			result = result * 37 + ( (properties == null) ? 0 : properties.hashCode() );
			return result;
		}
		
		/**
		 * JCA 1.0, 5.5.3
		 * It is required that the ManagedConnectionFactory implementation class
		 * extend the implementation of the hashCode and equals methods defined
		 * in the java.lang.Object class.
		 *
		 * @return if object is equals based on a complete set of configuration
		 *      properties.
		 */
		public boolean equals(final Object object) {
			if (this == object) {
				return true;
			}
			boolean result = false;
			if (object instanceof ManagedConnectionFactoryImpl) {
				final ManagedConnectionFactoryImpl other =
				(ManagedConnectionFactoryImpl) object;
				result = equals(datasourceJndi, other.datasourceJndi) &&
				equals(dialect, other.dialect) &&
				equals(mapResources, other.mapResources) &&
				equals(properties, other.properties);
			}
			return result;
		}
		
		public void setDatasourceJndi(String datasourceJndi) {
			this.datasourceJndi = datasourceJndi;
		}
		
		public String getDatasourceJndi() {
			return datasourceJndi;
		}
		
		public String getDialect() {
			return dialect;
		}
		
		public void setDialect(String dialect) {
			this.dialect = dialect;
		}
		
		public String getMapResources() {
			return mapResources;
		}
		
		public void setMapResources(String mapResources) {
			this.mapResources = mapResources;
		}
		
		public String getHibernateProperties() {
			return properties;
		}
		
		public void setHibernateProperties(final String properties) {
			this.properties = properties;
		}
		
		//
		// Package private methods
		//
		
		DataSource getDatasource() {
			return datasource;
		}
		
		SessionFactory getSessionFactory() {
			return sessionFactory;
		}
		
		//
		// Private methods
		//
		
		private void log(String message) {
			log(message, null);
		}
		
		private void log(String message, Throwable t) {
			// log to provided output if set by app server
			// assumes to log error if an exception is provided
			if (out != null) {
				out.write(message);
				if (t != null)
				t.printStackTrace(out);
			}
			
			if (t != null) {
				log.error(message, t);
			}
			else {
				log.info(message);
			}
		}
		
		/**
		 * Compares two objects assuming that <code>null</code> equals to
		 * <code>null</code>
		 */
		private boolean equals(final Object o1, final Object o2) {
			if (o1 == o2) {
				return true;
			}
			else if (o1 != null) {
				return o1.equals(o2);
			}
			else {
				return o2 == null; // both null => equals
			}
		}
	}
	
