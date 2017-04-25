//$Id: Configuration.java,v 1.42 2005/01/29 03:03:18 oneovthafew Exp $
package net.sf.hibernate.cfg;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import net.sf.hibernate.util.ArrayHelper;
import net.sf.hibernate.util.ReflectHelper;
import net.sf.hibernate.util.StringHelper;
import net.sf.hibernate.util.XMLHelper;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Interceptor;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.tool.hbm2ddl.DatabaseMetadata;
import net.sf.hibernate.tool.hbm2ddl.TableMetadata;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.id.IdentifierGenerator;
import net.sf.hibernate.id.PersistentIdentifierGenerator;
import net.sf.hibernate.impl.SessionFactoryImpl;
import net.sf.hibernate.mapping.Collection;
import net.sf.hibernate.mapping.ForeignKey;
import net.sf.hibernate.mapping.Index;
import net.sf.hibernate.mapping.PersistentClass;
import net.sf.hibernate.mapping.Property;
import net.sf.hibernate.mapping.RootClass;
import net.sf.hibernate.mapping.Table;
import net.sf.hibernate.mapping.SimpleValue;
import net.sf.hibernate.mapping.IdentifierCollection;
import net.sf.hibernate.mapping.UniqueKey;
import net.sf.hibernate.cache.Cache;
import net.sf.hibernate.cache.CacheConcurrencyStrategy;
import net.sf.hibernate.cache.CacheException;
import net.sf.hibernate.cache.CacheFactory;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.Mapping;

/**
 * An instance of <tt>Configuration</tt> allows the application
 * to specify properties and mapping documents to be used when
 * creating a <tt>SessionFactory</tt>. Usually an application will create
 * a single <tt>Configuration</tt>, build a single instance of
 * <tt>SessionFactory</tt> and then instantiate <tt>Session</tt>s in
 * threads servicing client requests. The <tt>Configuration</tt> is meant
 * only as an initialization-time object. <tt>SessionFactory</tt>s are
 * immutable and do not retain any association back to the
 * <tt>Configuration</tt>.<br>
 * <br>
 * A new <tt>Configuration</tt> will use the properties specified in
 * <tt>hibernate.properties</tt> by default.
 *
 * @see net.sf.hibernate.SessionFactory
 * @author Gavin King
 */
public class Configuration {

	private Map classes = new HashMap();
	private Map imports = new HashMap();
	private Map collections = new HashMap();
	private Map tables = new HashMap();
	private Map namedQueries = new HashMap();
	private Map namedSqlQueries = new HashMap();
	private List secondPasses = new ArrayList();
	private List propertyReferences = new ArrayList();
	private Interceptor interceptor = EMPTY_INTERCEPTOR;
	private Properties properties = Environment.getProperties();
	private Map caches = new HashMap();
	
	private NamingStrategy namingStrategy = DefaultNamingStrategy.INSTANCE; 

	private static Log log = LogFactory.getLog(Configuration.class);

	protected void reset() {
		classes = new HashMap();
		collections = new HashMap();
		tables = new HashMap();
		namedQueries = new HashMap();
		namedSqlQueries = new HashMap();
		secondPasses = new ArrayList();
		interceptor = EMPTY_INTERCEPTOR;
		properties = Environment.getProperties();
	}

	private Mapping mapping = new Mapping() {
		/**
		 * Returns the identifier type of a mapped class
		 */
		public Type getIdentifierType(Class persistentClass) throws MappingException {
			return ( (PersistentClass) classes.get(persistentClass) ).getIdentifier().getType();
		}
		public String getIdentifierPropertyName(Class persistentClass) throws MappingException {
			return ( (PersistentClass) classes.get(persistentClass) ).getIdentifierProperty().getName();
		}
		public Type getPropertyType(Class persistentClass, String propertyName) throws MappingException {
			return ( (PersistentClass) classes.get(persistentClass) ).getProperty(propertyName).getType();
		}
	};
	

	public Configuration() {
		reset();
	}

	/**
	 * Iterate the class mappings
	 */
	public Iterator getClassMappings() {
		return classes.values().iterator();
	}

	/**
	 * Iterate the collection mappings
	 */
	public Iterator getCollectionMappings() {
		return collections.values().iterator();
	}

	/**
	 * Iterate the table mappings
	 */
	private Iterator getTableMappings() {
		return tables.values().iterator();
	}

	/**
	 * Get the mapping for a particular class
	 */
	public PersistentClass getClassMapping(Class persistentClass) {
		return (PersistentClass) classes.get(persistentClass);
	}

	/**
	 * Get the mapping for a particular collection role
	 * @param role a collection role
	 * @return Collection
	 */
	public Collection getCollectionMapping(String role) {
		return (Collection) collections.get(role);
	}

	/**
	 * Read mappings from a particular XML file
	 * @param xmlFile a path to a file
	 */
	public Configuration addFile(String xmlFile) throws MappingException {
		log.info("Mapping file: " + xmlFile);
		try {
			List errors = new ArrayList();
			org.dom4j.Document doc= XMLHelper.createSAXReader(xmlFile, errors).read( new File(xmlFile) );
			if ( errors.size()!=0 ) throw new MappingException( "invalid mapping", (Throwable) errors.get(0) );
			add(doc);
			return this;
		}
		catch (Exception e) {
			log.error("Could not configure datastore from file: " + xmlFile, e);
			throw new MappingException(e);
		}
	}

	/**
	 * Read mappings from a particular XML file
	 * @param xmlFile a path to a file
	 */
	public Configuration addFile(File xmlFile) throws MappingException {
		log.info( "Mapping file: " + xmlFile.getPath() );
		try {
			addInputStream( new FileInputStream(xmlFile) );
		}
		catch (Exception e) {
			log.error("Could not configure datastore from file: " + xmlFile.getPath(), e);
			throw new MappingException(e);
		}
		return this;
	}

	/**
	 * Read mappings from a <tt>String</tt>
	 * @param xml an XML string
	 */
	public Configuration addXML(String xml) throws MappingException {
		if ( log.isDebugEnabled() ) log.debug("Mapping XML:\n" + xml);
		try {
			List errors = new ArrayList();
			org.dom4j.Document doc = XMLHelper.createSAXReader("XML String", errors).read( new StringReader(xml) );
			if ( errors.size()!=0 ) throw new MappingException( "invalid mapping", (Throwable) errors.get(0) );
			add(doc);
		}
		catch (Exception e) {
			log.error("Could not configure datastore from XML", e);
			throw new MappingException(e);
		}
		return this;
	}

	/**
	 * Read mappings from a <tt>URL</tt>
	 * @param url
	 */
	public Configuration addURL(URL url) throws MappingException {
		if ( log.isDebugEnabled() ) log.debug("Mapping URL:\n" + url);
		try {
			addInputStream( url.openStream() );
		}
		catch (Exception e) {
			log.error("Could not configure datastore from URL", e);
			throw new MappingException(e);
		}
		return this;
	}

	/**
	 * Read mappings from a DOM <tt>Document</tt>
	 * @param doc a DOM document
	 */
	public Configuration addDocument(Document doc) throws MappingException {
		if ( log.isDebugEnabled() ) log.debug("Mapping XML:\n" + doc);
		try {
			add( XMLHelper.createDOMReader().read(doc) );
		}
		catch (Exception e) {
			log.error("Could not configure datastore from XML document", e);
			throw new MappingException(e);
		}
		return this;
	}

	protected void add(org.dom4j.Document doc) throws Exception {
		try {
			Binder.bindRoot( doc, createMappings() );
		}
		catch (MappingException me) {
			log.error("Could not compile the mapping document", me);
			throw me;
		}
	}

	/**
	 * Create a new <tt>Mappings</tt> to add class and collection
	 * mappings to.
	 */
	public Mappings createMappings() {
		return new Mappings(
			classes, 
			collections, 
			tables, 
			namedQueries, 
			namedSqlQueries, 
			imports, 
			caches, 
			secondPasses, 
			propertyReferences,
			namingStrategy
		);
	}

	/**
	 * Read mappings from an <tt>InputStream</tt>
	 * @param xmlInputStream an <tt>InputStream</tt> containing XML
	 */
	public Configuration addInputStream(InputStream xmlInputStream) throws MappingException {
		try {
			List errors = new ArrayList();
			org.dom4j.Document doc = XMLHelper.createSAXReader("XML InputStream", errors).read( new InputSource(xmlInputStream) );
			if ( errors.size()!=0 ) throw new MappingException( "invalid mapping", (Throwable) errors.get(0) );
			add(doc);
			return this;
		}
		catch (MappingException me) {
			throw me;
		}
		catch (Exception e) {
			log.error("Could not configure datastore from input stream", e);
			throw new MappingException(e);
		}
		finally {
			try{
				xmlInputStream.close();
			} 
			catch (IOException ioe){
				log.error("could not close input stream", ioe);	
			}
		}
	}

	/**
	 * Read mappings from an application resource
	 * @param path a resource
	 * @param classLoader a <tt>ClassLoader</tt> to use
	 */
	public Configuration addResource(String path, ClassLoader classLoader) throws MappingException {
		log.info("Mapping resource: " + path);
		InputStream rsrc = classLoader.getResourceAsStream(path);
		if (rsrc==null) throw new MappingException("Resource: " + path + " not found");
		try {
			return addInputStream(rsrc);
		} 
		catch (MappingException me) {
			throw new MappingException("Error reading resource: " + path, me);
		}
	}
  
    /**
     * Read mappings from an application resource trying different classloaders.
     * This method will try to load the resource first from the thread context
     * classloader and then from the classloader that loaded Hibernate.
     */
    public Configuration addResource(String path) throws MappingException {
        log.info("Mapping resource: " + path);
        InputStream rsrc = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
        if (rsrc==null) rsrc = Environment.class.getClassLoader().getResourceAsStream(path);
        if (rsrc==null) throw new MappingException("Resource: " + path + " not found");
        try {
            return addInputStream(rsrc);
        }
        catch (MappingException me) {
            throw new MappingException("Error reading resource: " + path, me);
        }
    }    

	/**
	 * Read a mapping from an application resource, using a convention.
	 * The class <tt>foo.bar.Foo</tt> is mapped by the file <tt>foo/bar/Foo.hbm.xml</tt>.
	 * @param persistentClass the mapped class
	 */
	public Configuration addClass(Class persistentClass) throws MappingException {
		String fileName = persistentClass.getName().replace(StringHelper.DOT,'/') + ".hbm.xml";
		log.info("Mapping resource: " + fileName);
		InputStream rsrc = persistentClass.getClassLoader().getResourceAsStream(fileName);
		if (rsrc==null) throw new MappingException("Resource: " + fileName + " not found");
		try {
			return addInputStream(rsrc);
		} 
		catch (MappingException me) {
			throw new MappingException("Error reading resource: " + fileName, me);
		}
	}

	/**
	 * Read all mappings from a jar file
	 * @param resource an application resource (a jar file)
	 * @deprecated use <tt>addJar(java.io.File)</tt>
	 */
	public Configuration addJar(String resource) throws MappingException {
		return addJar( new File( 
			Thread.currentThread().getContextClassLoader().getResource(resource).getFile() 
		) );
	}
	/**
	 * Read all mappings from a jar file
	 * @param jar a jar file
	 */
	public Configuration addJar(File jar) throws MappingException {

		log.info( "Searching for mapping documents in jar: " + jar.getName() );

		final JarFile jarFile;
		try {
			jarFile = new JarFile(jar);
		}
		catch (IOException ioe) {
			log.error("Could not configure datastore from jar: " + jar.getName(), ioe);
			throw new MappingException("Could not configure datastore from jar: " + jar.getName(), ioe);
		}

		Enumeration entries = jarFile.entries();
		while( entries.hasMoreElements() ) {

			ZipEntry ze = (ZipEntry) entries.nextElement();

			if( ze.getName().endsWith(".hbm.xml") ) {
				log.info( "Found mapping documents in jar: " + ze.getName() );
				try {
					addInputStream( jarFile.getInputStream(ze) );
				}
				catch (MappingException me) {
					throw me;
				}
				catch (Exception e) {
					log.error("Could not configure datastore from jar", e);
					throw new MappingException(e);
				}
			}
		}

		return this;

	}
	
	/**
	 * Read all mapping documents from a directory tree. Assume that any
	 * file named <tt>*.hbm.xml</tt> is a mapping document.
	 * @param dir a directory
	 */
	public Configuration addDirectory(File dir) throws MappingException {
		File[] files = dir.listFiles();
		for ( int i=0; i<files.length; i++ ) {
			if ( files[i].isDirectory() ) {
				addDirectory( files[i] );
			}
			else if ( files[i].getName().endsWith(".hbm.xml") ){
				addFile( files[i] );
			}
		}
		return this;
	}

	private Iterator iterateGenerators(Dialect dialect) throws MappingException {
		HashMap generators = new HashMap();
		Iterator iter = classes.values().iterator();
		while ( iter.hasNext() ) {
			IdentifierGenerator ig = ( (PersistentClass) iter.next() ).getIdentifier().createIdentifierGenerator(dialect);
			if ( ig instanceof PersistentIdentifierGenerator ) generators.put(
				( (PersistentIdentifierGenerator) ig ).generatorKey(), ig
			);
		}
		iter = collections.values().iterator();
		while (iter.hasNext()) {
			Collection collection = (Collection) iter.next();
			if (collection instanceof IdentifierCollection) {
				IdentifierGenerator ig = ( (IdentifierCollection) collection ).getIdentifier().createIdentifierGenerator(dialect);
				if ( ig instanceof PersistentIdentifierGenerator ) generators.put(
					( (PersistentIdentifierGenerator) ig ).generatorKey(), ig
				);
			}
		}

		return generators.values().iterator();
	}

	/**
	 * Generate DDL for dropping tables
	 * @see net.sf.hibernate.tool.hbm2ddl.SchemaExport
	 */
	public String[] generateDropSchemaScript(Dialect dialect) throws HibernateException {

		secondPassCompile();

		ArrayList script = new ArrayList(50);

		if ( dialect.dropConstraints() ) {
			Iterator iter = getTableMappings();
			while ( iter.hasNext() ) {
				Table table = (Table) iter.next();
				Iterator subIter = table.getForeignKeyIterator();
				while ( subIter.hasNext() ) {
					ForeignKey fk = (ForeignKey) subIter.next();
					script.add( fk.sqlDropString(dialect, properties.getProperty(Environment.DEFAULT_SCHEMA) ) );
				}
			}
		}


		Iterator iter = getTableMappings();
		while ( iter.hasNext() ) {

			Table table = (Table) iter.next();

			/*Iterator subIter = table.getIndexIterator();
			while ( subIter.hasNext() ) {
				Index index = (Index) subIter.next();
				if ( !index.isForeignKey() || !dialect.hasImplicitIndexForForeignKey() ) {
					script.add( index.sqlDropString(dialect) );
				}
			}*/

			script.add( table.sqlDropString(dialect, properties.getProperty(Environment.DEFAULT_SCHEMA) ) );

		}

		iter = iterateGenerators(dialect);
		while ( iter.hasNext() ) {
			String dropString = ( (PersistentIdentifierGenerator) iter.next() ).sqlDropString(dialect);
			if (dropString!=null) script.add(dropString);
		}

		return ArrayHelper.toStringArray(script);
	}

	/**
	 * Generate DDL for creating tables
	 * @see net.sf.hibernate.tool.hbm2ddl.SchemaExport
	 */
	public String[] generateSchemaCreationScript(Dialect dialect) throws HibernateException {
		secondPassCompile();

		ArrayList script = new ArrayList(50);

		Iterator iter = getTableMappings();
		while ( iter.hasNext() ) {
			Table table = (Table) iter.next();
			script.add( table.sqlCreateString(dialect, mapping, properties.getProperty(Environment.DEFAULT_SCHEMA) ) );
		}

		iter = getTableMappings();
		while ( iter.hasNext() ) {
			Table table = (Table) iter.next();

			if( !dialect.supportsUniqueConstraintInCreateAlterTable() ) {
			    Iterator subIter = table.getUniqueKeyIterator();
			    while ( subIter.hasNext() ) {
			        UniqueKey uk = (UniqueKey) subIter.next();
			        script.add( uk.sqlCreateString(dialect, mapping, properties.getProperty(Environment.DEFAULT_SCHEMA) ) );
			    }
			}
            
			Iterator subIter = table.getIndexIterator();
			while ( subIter.hasNext() ) {
				Index index = (Index) subIter.next();
				script.add( index.sqlCreateString(dialect, mapping, properties.getProperty(Environment.DEFAULT_SCHEMA) ) );
			}
			
			if ( dialect.hasAlterTable() ) {
				subIter = table.getForeignKeyIterator();
				while ( subIter.hasNext() ) {
					ForeignKey fk = (ForeignKey) subIter.next();
					script.add( fk.sqlCreateString(dialect, mapping, properties.getProperty(Environment.DEFAULT_SCHEMA) ) );
				}
			}
		}

		iter = iterateGenerators(dialect);
		while ( iter.hasNext() ) {
			String[] lines = ( (PersistentIdentifierGenerator) iter.next() ).sqlCreateStrings(dialect);
			for ( int i=0; i<lines.length; i++ ) script.add( lines[i] );
		}

		return ArrayHelper.toStringArray(script);
	}

	/**
	 * Generate DDL for altering tables
	 * @see net.sf.hibernate.tool.hbm2ddl.SchemaUpdate
	 */
	public String[] generateSchemaUpdateScript(Dialect dialect, DatabaseMetadata databaseMetadata) throws HibernateException {
		secondPassCompile();

		ArrayList script = new ArrayList(50);

		Iterator iter = getTableMappings();
		while ( iter.hasNext() ) {
			Table table = (Table) iter.next();
			TableMetadata tableInfo = databaseMetadata.getTableMetadata( table.getName(), table.getSchema(), null );
			if (tableInfo==null) {
				script.add( table.sqlCreateString(dialect, mapping, properties.getProperty(Environment.DEFAULT_SCHEMA) ) );
			}
			else {
				Iterator subiter = table.sqlAlterStrings(dialect, mapping, tableInfo, properties.getProperty(Environment.DEFAULT_SCHEMA) );
				while ( subiter.hasNext() ) script.add( subiter.next() );
			}
		}

		iter = getTableMappings();
		while ( iter.hasNext() ) {

			Table table = (Table) iter.next();
			TableMetadata tableInfo = databaseMetadata.getTableMetadata( table.getName(), table.getSchema(), null );
			Iterator subIter;

			if ( dialect.hasAlterTable() ) {
				subIter = table.getForeignKeyIterator();
				while ( subIter.hasNext() ) {
					ForeignKey fk = (ForeignKey) subIter.next();
					boolean create = tableInfo==null || (
						tableInfo.getForeignKeyMetadata( fk.getName() )==null && (
							//Icky workaround for MySQL bug:
							!(dialect instanceof net.sf.hibernate.dialect.MySQLDialect) ||
							tableInfo.getIndexMetadata( fk.getName() )==null
						)
					);
					if (create) script.add( fk.sqlCreateString(dialect, mapping, properties.getProperty(Environment.DEFAULT_SCHEMA) ) );
				}
			}
			
			/*//broken, 'cos we don't generate these with names in SchemaExport
			subIter = table.getIndexIterator();
			while ( subIter.hasNext() ) {
				Index index = (Index) subIter.next();
				if ( !index.isForeignKey() || !dialect.hasImplicitIndexForForeignKey() ) {
					if ( tableInfo==null || tableInfo.getIndexMetadata( index.getName() ) == null ) {
						script.add( index.sqlCreateString(dialect, mapping) );
					}
				}
			}
			//broken, 'cos we don't generate these with names in SchemaExport
			subIter = table.getUniqueKeyIterator();
			while ( subIter.hasNext() ) {
				UniqueKey uk = (UniqueKey) subIter.next();
				if ( tableInfo==null || tableInfo.getIndexMetadata( uk.getName() ) == null ) {
					script.add( uk.sqlCreateString(dialect, mapping) );
				}
			}*/
		}

		iter = iterateGenerators(dialect);
		while ( iter.hasNext() ) {
			PersistentIdentifierGenerator generator = (PersistentIdentifierGenerator) iter.next();
			Object key = generator.generatorKey();
			if ( !databaseMetadata.isSequence(key) && !databaseMetadata.isTable(key) ) {
				String[] lines = generator.sqlCreateStrings(dialect);
				for (int i = 0; i < lines.length; i++) script.add( lines[i] );
			}
		}

		return ArrayHelper.toStringArray(script);
	}
	
	private void validate() throws MappingException {
		Iterator iter = classes.values().iterator();
		while ( iter.hasNext() ) ( (PersistentClass) iter.next() ).validate(mapping);
		iter = collections.values().iterator();
		while ( iter.hasNext() ) ( (Collection) iter.next() ).validate(mapping);
	}

	// This method may be called many times!!
	private void secondPassCompile() throws MappingException {

		log.info("processing one-to-many association mappings");

		Iterator iter = secondPasses.iterator();
		while ( iter.hasNext() ) {
			Binder.SecondPass sp = (Binder.SecondPass) iter.next();
			sp.doSecondPass(classes);
			iter.remove();
		}
		
		log.info("processing one-to-one association property references");
		
		iter = propertyReferences.iterator();
		while ( iter.hasNext() ) {
			Mappings.UniquePropertyReference upr = (Mappings.UniquePropertyReference) iter.next();
			PersistentClass clazz = getClassMapping(upr.referencedClass);
			if (clazz==null) throw new MappingException( "property-ref to unmapped class: " + upr.referencedClass.getName() );
			boolean found = false;
			Iterator propIter = clazz.getPropertyIterator();
			while ( propIter.hasNext() ) {
				Property prop = (Property) propIter.next();
				if ( upr.propertyName.equals( prop.getName() ) ) {
					( (SimpleValue) prop.getValue() ).setUnique(true);
					found = true;
					break;
				}
			}
			if (!found) throw new MappingException( 
				"property-ref not found: " + upr.propertyName + 
				" in class: " + upr.referencedClass.getName() 
			);
		}
		
		//TODO: Somehow add the newly created foreign keys to the internal collection

		log.info("processing foreign key constraints");
		
		iter = getTableMappings();
		Set done = new HashSet();
		while ( iter.hasNext() ) {
			secondPassCompileForeignKeys( (Table) iter.next(), done );
		}
		
	}
	
	private void secondPassCompileForeignKeys(Table table, Set done) throws MappingException {

		Iterator iter = table.getForeignKeyIterator();
		while ( iter.hasNext() ) {
			ForeignKey fk = (ForeignKey) iter.next();
			if ( !done.contains(fk) ) {
				done.add(fk);
				if ( log.isDebugEnabled() ) log.debug(
					"resolving reference to class: " + fk.getReferencedClass().getName()
				);
				PersistentClass referencedClass = (PersistentClass) classes.get( fk.getReferencedClass() );
				if (referencedClass == null) throw new MappingException(
						"An association from the table " +
						fk.getTable().getName() +
						" refers to an unmapped class: " +
						fk.getReferencedClass().getName()
				);
				if ( referencedClass.isJoinedSubclass() ) {
					secondPassCompileForeignKeys( referencedClass.getSuperclass().getTable(), done );
				}
				fk.setReferencedTable( referencedClass.getTable() );
			}
		}
	}

	/**
	 * Get the named queries
	 */
	public Map getNamedQueries() {
		return namedQueries;
	}

	private static final Interceptor EMPTY_INTERCEPTOR = new EmptyInterceptor();

	static final class EmptyInterceptor implements Interceptor, Serializable {
		/**
		 * @see net.sf.hibernate.Interceptor#onDelete(Object, Serializable id, Object[], String[], Type[])
		 */
		public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
		}

		/**
		 * @see net.sf.hibernate.Interceptor#onFlushDirty(Object, Serializable, Object[], Object[], String[], Type[])
		 */
		public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) {
			return false;
		}

		/**
		 * @see net.sf.hibernate.Interceptor#onLoad(Object, Serializable, Object[], String[], Type[])
		 */
		public boolean onLoad(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
			return false;
		}

		/**
		 * @see net.sf.hibernate.Interceptor#onSave(Object, Serializable, Object[], String[], Type[])
		 */
		public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) {
			return false;
		}

		/**
		 * @see net.sf.hibernate.Interceptor#postFlush(Iterator)
		 */
		public void postFlush(Iterator entities) {
		}

		/**
		 * @see net.sf.hibernate.Interceptor#preFlush(Iterator)
		 */
		public void preFlush(Iterator entities) {
		}

		/**
		 * @see net.sf.hibernate.Interceptor#isUnsaved(java.lang.Object)
		 */
		public Boolean isUnsaved(Object entity) {
			return null;
		}

		/**
		 * @see net.sf.hibernate.Interceptor#instantiate(java.lang.Class, java.io.Serializable)
		 */
		public Object instantiate(Class clazz, Serializable id) {
			return null;
		}

		/**
		 * @see net.sf.hibernate.Interceptor#findDirty(Object, Serializable, Object[], Object[], String[], Type[])
		 */
		public int[] findDirty(
			Object entity,
			Serializable id,
			Object[] currentState,
			Object[] previousState,
			String[] propertyNames,
			Type[] types) {
			return null;
		}

	}

	/**
	 * Instantiate a new <tt>SessionFactory</tt>, using the properties and
	 * mappings in this configuration. The <tt>SessionFactory</tt> will be
	 * immutable, so changes made to the <tt>Configuration</tt> after
	 * building the <tt>SessionFactory</tt> will not affect it.
	 *
	 * @see net.sf.hibernate.SessionFactory
	 * @return a new factory for <tt>Session</tt>s
	 */
	public SessionFactory buildSessionFactory() throws HibernateException {
		secondPassCompile();
		validate();
		Environment.verifyProperties(properties);
		Properties copy = new Properties();
		copy.putAll(properties);
		Settings settings = buildSettings();
		configureCaches(settings);
		return new SessionFactoryImpl(this, settings);
	}

	/**
	 * Return the configured <tt>Interceptor</tt>
	 */
	public Interceptor getInterceptor() {
		return interceptor;
	}

	/**
	 * Get all properties
	 */
	public Properties getProperties() {
		return properties;
	}

	/**
	 * Configure an <tt>Interceptor</tt>
	 */
	public Configuration setInterceptor(Interceptor interceptor) {
		this.interceptor = interceptor;
		return this;
	}

	/**
	 * Specify a completely new set of properties
	 */
	public Configuration setProperties(Properties properties) {
		this.properties = properties;
		return this;
	}

	/**
	 * Set the given properties
	 */
	public Configuration addProperties(Properties extraProperties) {
		this.properties.putAll(extraProperties);
		return this;
	}

	/**
	 * Set a property
	 */
	public Configuration setProperty(String propertyName, String value) {
		properties.setProperty(propertyName, value);
		return this;
	}

	/**
	 * Get a property
	 */
	public String getProperty(String propertyName) {
		return properties.getProperty(propertyName);
	}

	private void addProperties(Element parent) {
		Iterator iter = parent.elementIterator("property");
		while ( iter.hasNext() ) {
			Element node = (Element) iter.next();
			String name = node.attributeValue("name");
			String value = node.getText().trim();
			log.debug(name + "=" + value);
			properties.setProperty(name, value);
			if ( !name.startsWith("hibernate") ) properties.setProperty("hibernate." + name, value);
		}
		Environment.verifyProperties(properties);
	}

	/**
	 * Get the configuration file as an <tt>InputStream</tt>. Might be overridden
	 * by subclasses to allow the configuration to be located by some arbitrary
	 * mechanism.
	 */
	protected InputStream getConfigurationInputStream(String resource) throws HibernateException {

		log.info("Configuration resource: " + resource);

		InputStream stream = Environment.class.getResourceAsStream(resource);
		if (stream==null) {
			log.warn(resource + " not found");
			throw new HibernateException(resource + " not found");
		}
		return stream;

	}

	/**
	 * Use the mappings and properties specified in an application
	 * resource named <tt>hibernate.cfg.xml</tt>.
	 */
	public Configuration configure() throws HibernateException {
		configure("/hibernate.cfg.xml");
		return this;
	}

	/**
	 * Use the mappings and properties specified in the given application
	 * resource. The format of the resource is defined in
	 * <tt>hibernate-configuration-2.0.dtd</tt>.
	 *
	 * The resource is found via <tt>getConfigurationInputStream(resource)</tt>.
	 */
	public Configuration configure(String resource) throws HibernateException {
		log.info("configuring from resource: " + resource);
		InputStream stream = getConfigurationInputStream(resource);
		return doConfigure(stream, resource);
	}

	/**
	 * Use the mappings and properties specified in the given document.
	 * The format of the document is defined in
	 * <tt>hibernate-configuration-2.0.dtd</tt>.
	 *
	 * @param url URL from which you wish to load the configuration
	 * @return A configuration configured via the file
	 * @throws HibernateException
	 */
	public Configuration configure(URL url) throws HibernateException {
		log.info( "configuring from url: " + url.toString() );
		try {
			return doConfigure( url.openStream(), url.toString() );
		}
		catch (IOException ioe) {
			throw new HibernateException("could not configure from URL: " + url, ioe);
		}
	}

	/**
	 * Use the mappings and properties specified in the given application
	 * file. The format of the file is defined in
	 * <tt>hibernate-configuration-2.0.dtd</tt>.
	 *
	 * @param configFile <tt>File</tt> from which you wish to load the configuration
	 * @return A configuration configured via the file
	 * @throws HibernateException
	 */
	public Configuration configure(File configFile) throws HibernateException {
		log.info( "configuring from file: " + configFile.getName() );
		try {
			return doConfigure( new FileInputStream(configFile), configFile.toString() );
		}
		catch (FileNotFoundException fnfe) {
			throw new HibernateException("could not find file: " + configFile, fnfe);
		}
	}

	/**
	 * Use the mappings and properties specified in the given application
	 * resource. The format of the resource is defined in
	 * <tt>hibernate-configuration-2.0.dtd</tt>.
	 *
	 * @param stream Inputstream to be read from
	 * @param resourceName The name to use in warning/error messages
	 * @return A configuration configured via the stream
	 * @throws HibernateException
	 */
	protected Configuration doConfigure(InputStream stream, String resourceName) throws HibernateException {

		org.dom4j.Document doc;
		try {
			List errors = new ArrayList();
			doc = XMLHelper.createSAXReader(resourceName, errors).read( new InputSource(stream) );
			if ( errors.size()!=0 ) throw new MappingException( "invalid configuration", (Throwable) errors.get(0) );
		}
		catch (Exception e) {
			log.error("problem parsing configuration" + resourceName, e);
			throw new HibernateException("problem parsing configuration" + resourceName, e);
		}
		finally {
			try{
				stream.close();
			} 
			catch (IOException ioe){
				log.error("could not close stream on: " + resourceName, ioe);	
			}
		}
		
		return doConfigure(doc);

	}

	/**
	 * Use the mappings and properties specified in the given XML document.
	 * The format of the file is defined in
	 * <tt>hibernate-configuration-2.0.dtd</tt>.
	 *
	 * @param document an XML document from which you wish to load the configuration
	 * @return A configuration configured via the <tt>Document</tt>
	 * @throws HibernateException if there is problem in accessing the file.
	 */
	public Configuration configure(Document document) throws HibernateException {
		log.info("configuring from XML document");
		org.dom4j.Document doc;
		try {
			doc = XMLHelper.createDOMReader().read(document);
		}
		catch (Exception e) {
			log.error("problem parsing document", e);
			throw new HibernateException("problem parsing document", e);
		}

		return doConfigure(doc);
	}

	protected Configuration doConfigure(org.dom4j.Document doc) throws HibernateException {

		Element sfNode = doc.getRootElement().element("session-factory");
		String name = sfNode.attributeValue("name");
		if (name!=null) properties.setProperty(Environment.SESSION_FACTORY_NAME, name);
		addProperties(sfNode);

		Iterator elements = sfNode.elementIterator();
		while ( elements.hasNext() ) {
			Element mapElement = (Element) elements.next();
			String elemname = mapElement.getName();
			if ( "mapping".equals(elemname) ) {
				Attribute rsrc = mapElement.attribute("resource");
				Attribute file = mapElement.attribute("file");
				Attribute jar = mapElement.attribute("jar");
				if (rsrc!=null) {
					log.debug(name + "<-" + rsrc);
					addResource( rsrc.getValue() );
				}
				else if ( jar!=null ) {
					log.debug(name + "<-" + jar);
					addJar( jar.getValue() );
				}
				else {
					if (file==null) throw new MappingException("<mapping> element in configuration specifies no attributes");
					log.debug(name + "<-" + file);
					addFile( file.getValue() );
				}
			}
			else if ( "jcs-class-cache".equals(elemname) || "class-cache".equals(elemname) ) {
				String className = mapElement.attributeValue("class");
				final Class clazz;
				try {
					clazz = ReflectHelper.classForName(className);
				}
				catch (ClassNotFoundException cnfe) {
					throw new MappingException("Could not find class: " + className, cnfe);
				}
				Attribute regionNode = mapElement.attribute("region");
				final String region = (regionNode==null) ? className : regionNode.getValue();
				CacheConcurrencyStrategy cache = CacheFactory.createCache(
					mapElement, region, getRootClassMapping(clazz).isMutable()
				);
				setCacheConcurrencyStrategy(clazz, cache, region);
			}
			else if ( "jcs-collection-cache".equals(elemname) || "collection-cache".equals(elemname) ) {
				String role = mapElement.attributeValue("collection");
				Collection collection = getCollectionMapping(role);
				Attribute regionNode = mapElement.attribute("region");
				final String region = (regionNode==null) ? role : regionNode.getValue();
				CacheConcurrencyStrategy cache = CacheFactory.createCache( 
					mapElement, region, collection.getOwner().isMutable()
				);
				setCacheConcurrencyStrategy(role, cache, region);
			}
		}

		log.info("Configured SessionFactory: " + name);
		log.debug("properties: " + properties);

		return this;

	}
	
	RootClass getRootClassMapping(Class clazz) throws MappingException {
		try {
			return (RootClass) getClassMapping(clazz);
		}
		catch (ClassCastException cce) {
			throw new MappingException("You may only specify a cache for root <class> mappings");
		}
	}
	
	/**
	 * Set up a cache for an entity class
	 * @param clazz
	 * @param concurrencyStrategy
	 * @return Configuration
	 * @throws MappingException
	 */
	public Configuration setCacheConcurrencyStrategy(Class clazz, CacheConcurrencyStrategy concurrencyStrategy) 
	throws MappingException {
		setCacheConcurrencyStrategy( clazz, concurrencyStrategy, clazz.getName() );
		return this;
	}
	
	void setCacheConcurrencyStrategy(Class clazz, CacheConcurrencyStrategy concurrencyStrategy, String region) 
	throws MappingException {
		RootClass rootClass = getRootClassMapping(clazz);
		rootClass.setCache(concurrencyStrategy);
		caches.put( rootClass.getMappedClass().getName(), concurrencyStrategy );
	}
	
	/**
	 * Set up a cache for a collection role
	 * @param collectionRole
	 * @param concurrencyStrategy
	 * @return Configuration
	 * @throws MappingException
	 */
	public Configuration setCacheConcurrencyStrategy(String collectionRole, CacheConcurrencyStrategy concurrencyStrategy) 
	throws MappingException {
		setCacheConcurrencyStrategy(collectionRole, concurrencyStrategy, collectionRole);
		return this;
	}
	
	void setCacheConcurrencyStrategy(String collectionRole, CacheConcurrencyStrategy concurrencyStrategy, String region) 
	throws MappingException {
		Collection collection = getCollectionMapping(collectionRole);
		collection.setCache(concurrencyStrategy);
		Object old = caches.put( collection.getRole(), concurrencyStrategy );
		if (old!=null) throw new MappingException("duplicate cache region");
	}
	
	protected void configureCaches(Settings settings) throws HibernateException {
		
		//TODO: this is actually broken, I guess, since changing the
		//      cache provider property and rebuilding the SessionFactory
		//      will affect existing SessionFactory!

		log.info("instantiating and configuring caches");

        // needed here because caches are built directly below.  This is fixed in H3.
		settings.getCacheProvider().start( properties );

		String prefix = properties.getProperty(Environment.CACHE_REGION_PREFIX);
		
		Iterator iter = caches.entrySet().iterator();
		while ( iter.hasNext() ) {
			Map.Entry me = (Map.Entry) iter.next();
			String name = (String) me.getKey();
			if (prefix != null)
			    name = prefix + "." + name;
			CacheConcurrencyStrategy strat = (CacheConcurrencyStrategy) me.getValue();
			if ( log.isDebugEnabled() ) log.debug("instantiating cache " + name);
			Cache cache;
			try {
				cache = settings.getCacheProvider().buildCache(name, properties);
			}
			catch (CacheException e) {
				throw new HibernateException( "Could not instantiate Cache", e );
			}
			strat.setCache(cache);
			strat.setMinimalPuts( settings.isMinimalPutsEnabled() );
		}
		
		caches.clear();
	}

	/**
	 * Get the query language imports
	 *
	 * @return a mapping from "import" names to fully qualified class names
	 */
	public Map getImports() {
		return imports;
	}

	/**
	 * Create an object-oriented view of the configuration properties
	 */
	protected Settings buildSettings() throws HibernateException {
		return SettingsFactory.buildSettings(properties);
	}

	public Map getNamedSQLQueries() {
		return namedSqlQueries;
	}

	/**
	 * @return the NamingStrategy.
	 */
	public NamingStrategy getNamingStrategy() {
		return namingStrategy;
	}

	/**
	 * Set a custom naming strategy
	 * 
	 * @param namingStrategy the NamingStrategy to set
	 */
	public Configuration setNamingStrategy(NamingStrategy namingStrategy) {
		this.namingStrategy = namingStrategy;
		return this;
	}

}






