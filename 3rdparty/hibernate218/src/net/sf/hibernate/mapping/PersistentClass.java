//$Id: PersistentClass.java,v 1.20 2004/08/07 05:47:40 oneovthafew Exp $
package net.sf.hibernate.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.SequencedHashMap;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.cache.CacheConcurrencyStrategy;
import net.sf.hibernate.engine.Mapping;
import net.sf.hibernate.sql.Alias;
import net.sf.hibernate.util.JoinedIterator;
import net.sf.hibernate.util.StringHelper;

/**
 * Mapping for an entity class.
 * @author Gavin King
 */
public abstract class PersistentClass {
	
	private static final Alias PK_ALIAS = new Alias(15, "PK");
	
	public static final String NULL_DISCRIMINATOR_MAPPING = "null";
	public static final String NOT_NULL_DISCRIMINATOR_MAPPING = "not null";

	private Class mappedClass;
	private String discriminatorValue;
	private Map properties = new SequencedHashMap();
	private Table table;
	private Class proxyInterface;
	private final ArrayList subclasses = new ArrayList();
	private final ArrayList subclassProperties = new ArrayList();
	private final ArrayList subclassTables = new ArrayList();
	private boolean dynamicInsert;
	private boolean dynamicUpdate;
	private int batchSize=1;
	private boolean selectBeforeUpdate;
	private int optimisticLockMode;
	private java.util.Map metaAttributes;

	public boolean useDynamicInsert() {
		return dynamicInsert;
	}
	
	public boolean useDynamicUpdate() {
		return dynamicUpdate;
	}
	
	public void setDynamicInsert(boolean dynamicInsert) {
		this.dynamicInsert = dynamicInsert;
	}
	
	public void setDynamicUpdate(boolean dynamicUpdate) {
		this.dynamicUpdate = dynamicUpdate;
	}

	public String getDiscriminatorValue() {
		return discriminatorValue;
	}
	
	public void addSubclass(Subclass subclass) throws MappingException {
		// inheritance cycle detection (paranoid check)
		PersistentClass superclass = getSuperclass();
		while (superclass!=null) {
			if( subclass.getName().equals( superclass.getName() ) ) {
				throw new MappingException( 
					"Circular inheritance mapping detected: " + 
					subclass.getName() + 
					" will have it self as superclass when extending " + 
					getName() 
				);
			}
			superclass = superclass.getSuperclass();
		}
		subclasses.add(subclass);
	}
	
	public boolean hasSubclasses() {
		return subclasses.size() > 0;
	}
	
	public int getSubclassSpan() {
		int n = subclasses.size();
		Iterator iter = subclasses.iterator();
		while ( iter.hasNext() ) {
			n += ( (Subclass) iter.next() ).getSubclassSpan();
		}
		return n;
	}
	/**
	 * Iterate over subclasses in a special 'order', most derived subclasses
	 * first.
	 */
	public Iterator getSubclassIterator() {
		Iterator[] iters = new Iterator[ subclasses.size() + 1 ];
		Iterator iter = subclasses.iterator();
		int i=0;
		while ( iter.hasNext() ) {
			iters[i++] = ( (Subclass) iter.next() ).getSubclassIterator();
		}
		iters[i] = subclasses.iterator();
		return new JoinedIterator(iters);
	}
	
	public Iterator getDirectSubclasses() {
		return subclasses.iterator();
	}
	/**
	 * Add a new property definition
	 * @param prop
	 * @throws MappingException if the property was already defined
	 */
	public void addNewProperty(Property prop) throws MappingException {
		Object old = properties.put( prop.getName(), prop );
		if (old!=null) {
			throw new MappingException(
				"duplicate mapping for property: " + 
				getName() + 
				StringHelper.DOT + 
				prop.getName()
			);
		}
	}
	/**
	 * Change the property definition or add a new property
	 * definition
	 * @param prop
	 */
	public void addProperty(Property prop) {
		properties.put( prop.getName(), prop );
	}
	public void setTable(Table table) {
		this.table=table;
	}
	public Iterator getPropertyIterator() {
		return properties.values().iterator();
	}
	public Table getTable() {
		return table;
	}
	
	public Class getMappedClass() {
		return mappedClass;
	}
	
	public String getName() {
		return mappedClass.getName();
	}
	
	public abstract boolean isMutable();
	public abstract boolean hasIdentifierProperty();
	public abstract Property getIdentifierProperty();
	public abstract SimpleValue getIdentifier();
	public abstract Property getVersion();
	public abstract Value getDiscriminator();
	public abstract boolean isInherited();
	public abstract boolean isPolymorphic();
	public abstract boolean isVersioned();
	public abstract CacheConcurrencyStrategy getCache();
	public abstract PersistentClass getSuperclass();
	public abstract boolean isExplicitPolymorphism();
	
	public abstract Iterator getPropertyClosureIterator();
	public abstract Iterator getTableClosureIterator();
	
	protected void addSubclassProperty(Property p) {
		subclassProperties.add(p);
	}
	protected void addSubclassTable(Table subclassTable) {
		subclassTables.add(subclassTable);
	}
	public Iterator getSubclassPropertyClosureIterator() {
		return new JoinedIterator( getPropertyClosureIterator(), subclassProperties.iterator() );
	}
	public Iterator getSubclassTableClosureIterator() {
		return new JoinedIterator( getTableClosureIterator(), subclassTables.iterator() );
	}
	public Class getProxyInterface() {
		return proxyInterface;
	}
	
	public abstract boolean hasEmbeddedIdentifier();
	public abstract Class getClassPersisterClass();
	public abstract void setClassPersisterClass(Class classPersisterClass);
	public abstract Table getRootTable();
	public abstract RootClass getRootClass();
	public abstract SimpleValue getKey();

	/**
	 * Sets the discriminatorValue.
	 * @param discriminatorValue The discriminatorValue to set
	 */
	public void setDiscriminatorValue(String discriminatorValue) {
		this.discriminatorValue = discriminatorValue;
	}

	/**
	 * Sets the persistentClass.
	 * @param persistentClass The persistentClass to set
	 */
	public void setMappedClass(Class persistentClass) {
		this.mappedClass = persistentClass;
	}

	/**
	 * Sets the proxyInterface.
	 * @param proxyInterface The proxyInterface to set
	 */
	public void setProxyInterface(Class proxyInterface) {
		this.proxyInterface = proxyInterface;
	}
	
	public boolean isForceDiscriminator() {
		return false;
	}
	
	public void createPrimaryKey() {
		//Primary key constraint
		PrimaryKey pk = new PrimaryKey();
		pk.setTable(table);
		pk.setName( PK_ALIAS.toAliasString( table.getName() ) );
		table.setPrimaryKey(pk);
		
		pk.addColumns( getKey().getColumnIterator() );
	}
	
	public abstract String getWhere();

	public int getBatchSize() {
		return batchSize;
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public boolean hasSelectBeforeUpdate() {
		return selectBeforeUpdate;
	}

	public void setSelectBeforeUpdate(boolean selectBeforeUpdate) {
		this.selectBeforeUpdate = selectBeforeUpdate;
	}
	
	public Property getProperty(String propertyName) throws MappingException {
		Iterator iter = getPropertyClosureIterator();
		while ( iter.hasNext() ) {
			Property prop = (Property) iter.next();
			if ( prop.getName().equals(propertyName) ) return prop;
		}
		throw new MappingException("property not found: " + propertyName);
	}

	public int getOptimisticLockMode() {
		return optimisticLockMode;
	}

	public void setOptimisticLockMode(int optimisticLockMode) {
		this.optimisticLockMode = optimisticLockMode;
	}
	
	public void validate(Mapping mapping) throws MappingException {
		Iterator iter = getPropertyIterator();
		while ( iter.hasNext() ) {
			Property prop = (Property) iter.next();
			if ( !prop.isValid(mapping) ) {
				throw new MappingException( 
					"property mapping has wrong number of columns: " + 
					StringHelper.qualify( getMappedClass().getName(), prop.getName() ) +
					" type: " + 
					prop.getType().getName()
				);
			}
		}
	}
	
	public boolean isDiscriminatorValueNotNull() {
		return NOT_NULL_DISCRIMINATOR_MAPPING.equals( getDiscriminatorValue() );
	}
	public boolean isDiscriminatorValueNull() {
		return NULL_DISCRIMINATOR_MAPPING.equals( getDiscriminatorValue() );
	}

	public java.util.Map getMetaAttributes() {
		return metaAttributes;
	}

	public void setMetaAttributes(java.util.Map metas) {
		this.metaAttributes = metas;
	}

	public MetaAttribute getMetaAttribute(String name) {
		return (MetaAttribute) metaAttributes.get(name);
	}
	
	public String toString() {
		return getClass() + " for " + getMappedClass();
	}
	
	public abstract boolean isJoinedSubclass();

	public abstract boolean isDiscriminatorInsertable();
}






