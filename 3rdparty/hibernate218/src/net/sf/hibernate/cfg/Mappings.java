//$Id: Mappings.java,v 1.11 2004/06/04 01:27:37 steveebersole Exp $
package net.sf.hibernate.cfg;

import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.cache.CacheConcurrencyStrategy;
import net.sf.hibernate.mapping.Collection;
import net.sf.hibernate.mapping.NamedSQLQuery;
import net.sf.hibernate.mapping.PersistentClass;
import net.sf.hibernate.mapping.Table;

/**
 * A collection of mappings from classes and collections to
 * relational database tables. (Represents a single
 * <tt>&lt;hibernate-mapping&gt;</tt> element.)
 * @author Gavin King
 */
public class Mappings {
	
	private static final Log log = LogFactory.getLog(Mappings.class);
	
	private final Map classes;
	private final Map collections;
	private final Map tables;
	private final Map queries;
	private final Map sqlqueries;
	private final List secondPasses;
	private final Map imports;
	private String schemaName;
	private String defaultCascade;
	private String defaultPackage;
	private String defaultAccess;
	private boolean autoImport;
	private final List propertyReferences;
	private final Map caches;
	private final NamingStrategy namingStrategy;

	
	
	Mappings(
		final Map classes, 
		final Map collections, 
		final Map tables, 
		final Map queries, 
		final Map sqlqueries, 
		final Map imports, 
		final Map caches, 
		final List secondPasses, 
		final List propertyReferences,
		final NamingStrategy namingStrategy
	) {
		this.classes = classes;
		this.collections = collections;
		this.queries = queries;
		this.sqlqueries = sqlqueries;
		this.tables = tables;
		this.imports = imports;
		this.secondPasses = secondPasses;
		this.propertyReferences = propertyReferences;
		this.caches = caches;
		this.namingStrategy = namingStrategy;
	}
	
	public void addClass(PersistentClass persistentClass) throws MappingException {
		Object old = classes.put( persistentClass.getMappedClass(), persistentClass );
		if ( old!=null ) log.warn( "duplicate class mapping: " + persistentClass.getMappedClass().getName() );
	}
	public void addCollection(Collection collection) throws MappingException {
		Object old = collections.put( collection.getRole(), collection );
		if ( old!=null ) log.warn( "duplicate collection role: " + collection.getRole() );
	}
	public PersistentClass getClass(Class clazz) {
		return (PersistentClass) classes.get(clazz);
	}
	public Collection getCollection(String role) {
		return (Collection) collections.get(role);
	}
	
	public void addImport(String className, String rename) throws MappingException {
		if ( imports.put(rename, className)!=null ) throw new MappingException("duplicate import: " + rename);
	}
	
	public Table addTable(String schema, String name) {
		
		String key = schema != null ? schema + "." + name : name;
		Table table = (Table) tables.get(key);
		
		if (table == null) {
			table = new Table();
			table.setName(name);
			table.setSchema(schema);
			tables.put(key, table);
		}
		
		return table;
	}
	
	public Table getTable(String schema, String name) {
		String key = schema != null ? schema + "." + name : name;
		return (Table) tables.get(key);
	}
	
	public String getSchemaName() {
		return schemaName;
	}
	
	public String getDefaultCascade() {
		return defaultCascade;
	}
	
	/**
	 * Sets the schemaName.
	 * @param schemaName The schemaName to set
	 */
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * Sets the defaultCascade.
	 * @param defaultCascade The defaultCascade to set
	 */
	public void setDefaultCascade(String defaultCascade) {
		this.defaultCascade = defaultCascade;
	}
	
	/** 
	 * sets the default access strategy
	 * @param the default access strategy.
	 */
	public void setDefaultAccess(String defaultAccess) {
		this.defaultAccess = defaultAccess;
	}
	
	public String getDefaultAccess() {
		return defaultAccess;
	}
	
	public void addQuery(String name, String query) throws MappingException {
		checkQueryExist(name);	
		queries.put(name, query);		
	}		

	public void addSQLQuery(String name, NamedSQLQuery query) throws MappingException {		
		checkQueryExist(name);	
		sqlqueries.put(name, query);
	}

	private void checkQueryExist(String name) throws MappingException {
		if ( sqlqueries.containsKey(name) || queries.containsKey(name) ) {
			throw new MappingException("Duplicate query named: " + name);	
		}
	}
	
	public String getQuery(String name) {
		return (String) queries.get(name);
	}
	
	void addSecondPass(Binder.SecondPass sp) {
		secondPasses.add(sp);
	}

	/**
	 * Returns the autoImport.
	 * @return boolean
	 */
	public boolean isAutoImport() {
		return autoImport;
	}

	/**
	 * Sets the autoImport.
	 * @param autoImport The autoImport to set
	 */
	public void setAutoImport(boolean autoImport) {
		this.autoImport = autoImport;
	}
	
	void addUniquePropertyReference(Class referencedClass, String propertyName) {
		UniquePropertyReference upr = new UniquePropertyReference();
		upr.referencedClass = referencedClass;
		upr.propertyName = propertyName;
		propertyReferences.add(upr);
	}
	
	static final class UniquePropertyReference {
		Class referencedClass;
		String propertyName;
	}
	
	public void addCache(String name, CacheConcurrencyStrategy cache) throws MappingException {
		Object old = caches.put(name, cache);
		if (old!=null) throw new MappingException("duplicate cache region");
	}
		
	/**
	 * @return Returns the defaultPackage.
	 */
	public String getDefaultPackage() {
		return defaultPackage;
	}

	/**
	 * @param defaultPackage The defaultPackage to set.
	 */
	public void setDefaultPackage(String defaultPackage) {
		this.defaultPackage = defaultPackage;
	}
	
	public NamingStrategy getNamingStrategy() {
		return namingStrategy;
	}

}