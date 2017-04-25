//$Id: Binder.java,v 1.38 2004/11/30 03:37:57 oneovthafew Exp $
package net.sf.hibernate.cfg;

import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.cache.CacheConcurrencyStrategy;
import net.sf.hibernate.cache.CacheFactory;
import net.sf.hibernate.engine.Cascades;
import net.sf.hibernate.engine.Versioning;
import net.sf.hibernate.id.PersistentIdentifierGenerator;
import net.sf.hibernate.loader.OuterJoinLoader;
import net.sf.hibernate.mapping.Any;
import net.sf.hibernate.mapping.Array;
import net.sf.hibernate.mapping.Bag;
import net.sf.hibernate.mapping.Collection;
import net.sf.hibernate.mapping.Column;
import net.sf.hibernate.mapping.Component;
import net.sf.hibernate.mapping.Fetchable;
import net.sf.hibernate.mapping.Formula;
import net.sf.hibernate.mapping.IdentifierBag;
import net.sf.hibernate.mapping.IdentifierCollection;
import net.sf.hibernate.mapping.IndexedCollection;
import net.sf.hibernate.mapping.List;
import net.sf.hibernate.mapping.ManyToOne;
import net.sf.hibernate.mapping.Map;
import net.sf.hibernate.mapping.MetaAttribute;
import net.sf.hibernate.mapping.NamedSQLQuery;
import net.sf.hibernate.mapping.OneToMany;
import net.sf.hibernate.mapping.OneToOne;
import net.sf.hibernate.mapping.PersistentClass;
import net.sf.hibernate.mapping.PrimitiveArray;
import net.sf.hibernate.mapping.Property;
import net.sf.hibernate.mapping.RootClass;
import net.sf.hibernate.mapping.Set;
import net.sf.hibernate.mapping.SimpleValue;
import net.sf.hibernate.mapping.Subclass;
import net.sf.hibernate.mapping.Table;
import net.sf.hibernate.mapping.ToOne;
import net.sf.hibernate.mapping.Value;
import net.sf.hibernate.persister.EntityPersister;
import net.sf.hibernate.persister.NormalizedEntityPersister;
import net.sf.hibernate.property.Getter;
import net.sf.hibernate.property.Setter;
import net.sf.hibernate.type.ComponentType;
import net.sf.hibernate.type.DiscriminatorType;
import net.sf.hibernate.type.DynamicComponentType;
import net.sf.hibernate.type.EntityType;
import net.sf.hibernate.type.ForeignKeyDirection;
import net.sf.hibernate.type.MetaType;
import net.sf.hibernate.type.PrimitiveType;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.type.TypeFactory;
import net.sf.hibernate.util.ReflectHelper;
import net.sf.hibernate.util.StringHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;

final class Binder {

	private Binder() {}

	private static final Log log = LogFactory.getLog(Binder.class);

	public static void bindClass(Element node, PersistentClass model, Mappings mapping)
	throws MappingException {

		//CLASS
		String className = getClassName( node.attribute("name"), mapping );
		try {
			model.setMappedClass( ReflectHelper.classForName(className) );
		}
		catch ( ClassNotFoundException cnfe ) {
			throw new MappingException( "persistent class [" + className + "] not found", cnfe );
		}
		
		//PROXY INTERFACE
		Attribute proxyNode = node.attribute("proxy");
		Attribute lazyNode = node.attribute("lazy");
		boolean lazyTrue = lazyNode!=null && "true".equals( lazyNode.getValue() );
		if ( proxyNode!=null && ( lazyNode==null || lazyTrue ) ) {
			try {
				model.setProxyInterface( ReflectHelper.classForName( getClassName(proxyNode, mapping) ) );
			}
			catch (ClassNotFoundException cnfe) {
				throw new MappingException(cnfe);
			}
		}
		if ( proxyNode==null && lazyTrue ) {
			model.setProxyInterface( model.getMappedClass() );
		}
		
		//DISCRIMINATOR
		Attribute discriminatorNode = node.attribute("discriminator-value");
		model.setDiscriminatorValue( (discriminatorNode==null) ?
			model.getName() :
			discriminatorNode.getValue()
		);
		
		//DYNAMIC UPDATE
		Attribute dynamicNode = node.attribute("dynamic-update");
		model.setDynamicUpdate( (dynamicNode==null) ?
			false :
			"true".equals( dynamicNode.getValue() )
		);
		
		//DYNAMIC UPDATE
		Attribute insertNode = node.attribute("dynamic-insert");
		model.setDynamicInsert( (insertNode==null) ?
			false :
			"true".equals( insertNode.getValue() )
		);
		
		//IMPORT
		if ( mapping.isAutoImport() ) {
			mapping.addImport( className, StringHelper.unqualify(className) );
		}
		
		//BATCH SIZE
		Attribute batchNode = node.attribute("batch-size");
		if (batchNode!=null) model.setBatchSize( Integer.parseInt( batchNode.getValue() ) );
		
		//SELECT BEFORE UPDATE
		Attribute sbuNode = node.attribute("select-before-update");
		if (sbuNode!=null) model.setSelectBeforeUpdate( "true".equals( sbuNode.getValue() ) );
		
		//OPTIMISTIC LOCK MODE
		Attribute olNode = node.attribute("optimistic-lock");
		model.setOptimisticLockMode( getOptimisticLockMode(olNode) );
		
		model.setMetaAttributes( getMetas(node) );

		//PERSISTER
		Attribute persisterNode = node.attribute("persister");
		if (persisterNode==null) {
			//persister = EntityPersister.class;
		}
		else {
			try {
				model.setClassPersisterClass( ReflectHelper.classForName( persisterNode.getValue() ) );
			}
			catch (ClassNotFoundException cnfe) {
				throw new MappingException( "Could not find persister class: " + persisterNode.getValue() );
			}
		}

	}

	public static void bindSubclass(Element node, Subclass model, Mappings mappings) throws MappingException {

		bindClass(node, model, mappings);

		if ( model.getClassPersisterClass()==null ) {
			model.getRootClass().setClassPersisterClass(EntityPersister.class);
		}

		model.setTable( model.getSuperclass().getTable() );

		log.info("Mapping subclass: " + model.getName() + " -> " + model.getTable().getName() );

		// properties
		propertiesFromXML(node, model, mappings);
	}
	
	private static String getClassTableName(PersistentClass model, Element node, Mappings mappings) {
		Attribute tableNameNode = node.attribute("table");
		if (tableNameNode==null) {
			return mappings.getNamingStrategy().classToTableName( model.getName() );
		}
		else {
			return mappings.getNamingStrategy().tableName( tableNameNode.getValue() );
		}
	}

	public static void bindJoinedSubclass(Element node, Subclass model, Mappings mappings) throws MappingException {

		bindClass(node, model, mappings);

		// joined subclasses
		if ( model.getClassPersisterClass()==null ) {
			model.getRootClass().setClassPersisterClass(NormalizedEntityPersister.class);
		}
		
		// table + schema names
		Attribute schemaNode = node.attribute("schema");
		String schema = schemaNode==null ? mappings.getSchemaName() : schemaNode.getValue();
		Table mytable = mappings.addTable( schema, getClassTableName(model, node, mappings) );
		model.setTable(mytable);

		log.info("Mapping joined-subclass: " + model.getName() + " -> " + model.getTable().getName() );

		Element keyNode = node.element("key");
		SimpleValue key = new SimpleValue(mytable);
		model.setKey(key);
		bindSimpleValue(keyNode, key, false, model.getName(), mappings);

		model.getKey().setType( model.getIdentifier().getType() );
		model.createPrimaryKey();
		model.createForeignKey();

		//CHECK
		Attribute chNode = node.attribute("check");
		if (chNode!=null) mytable.addCheckConstraint( chNode.getValue() );
		
		// properties
		propertiesFromXML(node, model, mappings);
	}

	public static void bindRootClass(Element node, RootClass model, Mappings mappings) throws MappingException {

		bindClass(node, model, mappings);

		//TABLENAME
		Attribute schemaNode = node.attribute("schema");
		String schema = schemaNode==null ? mappings.getSchemaName() : schemaNode.getValue();
		Table table = mappings.addTable( schema, getClassTableName(model, node, mappings) );
		model.setTable(table);

		log.info("Mapping class: " + model.getName() + " -> " + model.getTable().getName() );

		//MUTABLE
		Attribute mutableNode = node.attribute("mutable");
		model.setMutable( (mutableNode==null) || mutableNode.getValue().equals("true") );

		//WHERE
		Attribute whereNode = node.attribute("where");
		if (whereNode!=null) model.setWhere( whereNode.getValue() );
		
		//CHECK
		Attribute chNode = node.attribute("check");
		if (chNode!=null) table.addCheckConstraint( chNode.getValue() );
		
		//POLYMORPHISM
		Attribute polyNode = node.attribute("polymorphism");
		model.setExplicitPolymorphism( (polyNode!=null) && polyNode.getValue().equals("explicit") );

		Iterator subnodes = node.elementIterator();
		while ( subnodes.hasNext() ) {

			Element subnode = (Element) subnodes.next();
			String name = subnode.getName();
			String propertyName = subnode.attributeValue("name");

			if ( "id".equals(name) ) {
				SimpleValue id = new SimpleValue(table);
				model.setIdentifier(id);
				if (propertyName==null) {
					bindSimpleValue(subnode, id, false, RootClass.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings);
					if (id.getType()==null) throw new MappingException(
						"must specify an identifier type: " + model.getMappedClass().getName()
					);
					model.setIdentifierProperty(null);
				}
				else {
					bindSimpleValue(subnode, id, false, propertyName, mappings);
					id.setTypeByReflection( model.getMappedClass(), propertyName );
					Property prop = new Property();
					prop.setValue(id);
					bindProperty(subnode, prop, mappings);
					model.setIdentifierProperty(prop);
				}
				if ( id.getType().getReturnedClass().isArray() ) throw new MappingException(
					"illegal use of an array as an identifier (arrays don't reimplement equals)"
				);
				makeIdentifier(subnode, id, mappings);
			}
			else if ( "composite-id".equals(name) ) {
				Component id = new Component(model);
				model.setIdentifier(id);
				if (propertyName==null) {
					bindComponent(subnode, id, null, model.getName(), "id", false, mappings);
					model.setEmbeddedIdentifier( id.isEmbedded() );
					model.setIdentifierProperty(null);
				}
				else {
					Class reflectedClass = ReflectHelper.reflectedPropertyClass( model.getMappedClass(), propertyName );
					bindComponent(
						subnode,
						id,
						reflectedClass,
						model.getName(), 
						propertyName,
						false,
						mappings
					);
					Property prop = new Property();
					prop.setValue(id);
					bindProperty(subnode, prop, mappings);
					model.setIdentifierProperty(prop);
				}
				makeIdentifier(subnode, id, mappings);
				
				Class idClass = id.getComponentClass();
				if ( !ReflectHelper.overridesEquals(idClass) ) {
					throw new MappingException(
						"composite-id class must override equals(): " + 
						id.getComponentClass().getName()
					);
				}
				if ( !ReflectHelper.overridesHashCode(idClass) ) {
					throw new MappingException(
						"composite-id class must override hashCode(): " + 
						id.getComponentClass().getName()
					);
				}
				if ( !Serializable.class.isAssignableFrom(idClass) ) {
					throw new MappingException(
						"composite-id class must implement Serializable: " +
						id.getComponentClass().getName()
					);
				}
			}
			else if ( "version".equals(name) || "timestamp".equals(name) ) {
				//VERSION
				SimpleValue val = new SimpleValue(table);
				bindSimpleValue(subnode, val, false, propertyName, mappings);
				if ( val.getType()==null ) val.setType( "version".equals(name) ? Hibernate.INTEGER : Hibernate.TIMESTAMP );
				Property prop = new Property();
				prop.setValue(val);
				bindProperty(subnode, prop, mappings);
				makeVersion(subnode, val);
				model.setVersion(prop);
				model.addNewProperty(prop);
			}
			else if ( "discriminator".equals(name) ) {
				//DISCRIMINATOR
				SimpleValue discrim = new SimpleValue(table);
				model.setDiscriminator(discrim);
				bindSimpleValue(subnode, discrim, false, RootClass.DEFAULT_DISCRIMINATOR_COLUMN_NAME, mappings);
				if ( discrim.getType()==null ) {
					discrim.setType(Hibernate.STRING);
					( (Column) discrim.getColumnIterator().next() ).setType(Hibernate.STRING);
				}
				model.setPolymorphic(true);
				if ( "true".equals( subnode.attributeValue("force") ) ) model.setForceDiscriminator(true);
				if ( "false".equals( subnode.attributeValue("insert") ) ) model.setDiscriminatorInsertable(false);
			}
			else if ( "jcs-cache".equals(name) || "cache".equals(name) ) {
				String className = model.getMappedClass().getName();
				CacheConcurrencyStrategy cache = CacheFactory.createCache( 
					subnode, className, model.isMutable() 
				);
				mappings.addCache(className, cache);
				model.setCache(cache);
			}

		}

		//Primary key constraint
		model.createPrimaryKey();

		propertiesFromXML(node, model, mappings);

	}

	public static void bindColumns(
		final Element node, 
		final SimpleValue model, 
		final boolean isNullable, 
		final boolean autoColumn, 
		final String propertyPath, 
		final Mappings mappings) {
		
		//COLUMN(S)
		Attribute columnAttribute = node.attribute("column");
		if ( columnAttribute==null) {
			Iterator iter = node.elementIterator("column");
			int count=0;
			Table table = model.getTable();
			while( iter.hasNext() ) {
				Element columnElement = (Element) iter.next();
				Column col = new Column();
				col.setType( model.getType() );
				col.setTypeIndex(count++);
				bindColumn(columnElement, col, isNullable);
				col.setName( mappings.getNamingStrategy().columnName( columnElement.attributeValue("name") ) );
				if (table!=null) table.addColumn(col); //table=null -> an association - fill it in later
				model.addColumn(col);
				//column index
				Attribute indexNode = columnElement.attribute("index");
				if ( indexNode!=null && table!=null ) {
					table.getIndex( indexNode.getValue() ).addColumn(col);
				}
				//column group index (although can server as a seperate column index)
				Attribute parentElementIndexAttr = node.attribute("index");
				if ( parentElementIndexAttr!=null && table!=null) {
					table.getIndex( parentElementIndexAttr.getValue() ).addColumn(col);
				}
				Attribute uniqueNode = columnElement.attribute("unique-key");
				if ( uniqueNode!=null && table!=null ) {
					table.getUniqueKey( uniqueNode.getValue() ).addColumn(col);
				}
			}
		}
		else {
			Column col = new Column();
			col.setType( model.getType() );
			bindColumn(node, col, isNullable);
			col.setName( mappings.getNamingStrategy().columnName( columnAttribute.getValue() ) );
			Table table = model.getTable();
			if (table!=null) table.addColumn(col); //table=null -> an association - fill it in later
			model.addColumn(col);
			//column group index (although can server as a seperate column index)
			Attribute indexAttr = node.attribute("index");
			if ( indexAttr!=null && table!=null) {
				table.getIndex( indexAttr.getValue() ).addColumn(col);
			}
		}

		if ( autoColumn && model.getColumnSpan()==0 ) {
			Column col = new Column();
			col.setType( model.getType() );
			bindColumn(node, col, isNullable);
			col.setName( mappings.getNamingStrategy().propertyToColumnName(propertyPath) );
			model.getTable().addColumn(col);
			model.addColumn(col);
		}

	}


	//automatically makes a column with the default name if none is specifed by XML
	public static void bindSimpleValue(Element node, SimpleValue model, boolean isNullable, String path, Mappings mappings) 
	throws MappingException {
		model.setType( getTypeFromXML(node) );

		Attribute formulaNode = node.attribute("formula");
		if (formulaNode!=null) {
			Formula f = new Formula();
			f.setFormula( formulaNode.getText() );
			model.setFormula(f);
		}
		else {
			bindColumns(node, model, isNullable, true, path, mappings);
		}

		Attribute fkNode = node.attribute("foreign-key");
		if (fkNode!=null) model.setForeignKeyName( fkNode.getValue() );
	}

	public static void bindProperty(Element node, Property model, Mappings mappings) throws MappingException {

		model.setName( node.attributeValue("name") );
		Type type = model.getValue().getType();
		if (type==null) throw new MappingException(
			"Could not determine a property type for: " + model.getName()
		);
		Attribute accessNode = node.attribute("access");
		if (accessNode!=null) {
			model.setPropertyAccessorName( accessNode.getValue() );
		} 
		else {
			model.setPropertyAccessorName( mappings.getDefaultAccess() );
		}
		Attribute cascadeNode = node.attribute("cascade");
		model.setCascade( (cascadeNode==null) ?
			mappings.getDefaultCascade() :
			cascadeNode.getValue()
		);
		Attribute updateNode = node.attribute("update");
		model.setUpdateable( (updateNode==null) ?
			true :
			"true".equals( updateNode.getValue() )
		);
		Attribute insertNode = node.attribute("insert");
		model.setInsertable( (insertNode==null) ?
			true :
			"true".equals( insertNode.getValue() )
		);
		
		if ( log.isDebugEnabled() ) {
			String msg = "Mapped property: " + model.getName();
			String columns = columns( model.getValue() );
			if ( columns.length() > 0  ) msg += " -> " + columns;
			if ( model.getType()!=null ) msg += ", type: " + model.getType().getName();
			log.debug(msg);
		}
		
		model.setMetaAttributes( getMetas(node) );

	}

	private static String columns(Value val) {
		StringBuffer columns = new StringBuffer();
		Iterator iter = val.getColumnIterator();
		while ( iter.hasNext() ) {
			columns.append( ( (Column) iter.next() ).getName() );
			if ( iter.hasNext() ) columns.append(", ");
		}
		return columns.toString();
	}

	/**
	 * Called for all collections
	 */
	public static void bindCollection(Element node, Collection model, String className, String path, Mappings mappings) throws MappingException {

		//ROLENAME
		model.setRole( StringHelper.qualify(className, path) );

		Attribute inverseNode = node.attribute("inverse");
		if ( inverseNode!=null) model.setInverse( StringHelper.booleanValue( inverseNode.getValue() ) );

		Attribute orderNode = node.attribute("order-by");
		if ( orderNode!=null) {
			if ( Environment.jvmSupportsLinkedHashCollections() || ( model instanceof Bag ) ) {
				model.setOrderBy( orderNode.getValue() );
			}
			else {
				log.warn("Attribute \"order-by\" ignored in JDK1.3 or less");
			}
		}
		Attribute whereNode = node.attribute("where");
		if (whereNode!=null) {
			model.setWhere( whereNode.getValue() );
		}
		Attribute batchNode = node.attribute("batch-size");
		if (batchNode!=null) {
			model.setBatchSize( Integer.parseInt( batchNode.getValue() ) );
		}

		//PERSISTER
		Attribute persisterNode = node.attribute("persister");
		if (persisterNode==null) {
			//persister = CollectionPersisterImpl.class;
		} 
		else {
			try {
				model.setCollectionPersisterClass( ReflectHelper.classForName( persisterNode.getValue() ) );
			}
			catch (ClassNotFoundException cnfe) {
				throw new MappingException( "Could not find collection persister class: " + persisterNode.getValue() );
			}
		}

		initOuterJoinFetchSetting(node, model);

		Element oneToManyNode = node.element("one-to-many");
		if (oneToManyNode!=null) {
			OneToMany oneToMany = new OneToMany( model.getOwner() );
			model.setElement(oneToMany);
			bindOneToMany(oneToManyNode, oneToMany, mappings);
			//we have to set up the table later!! yuck
		}
		else {
			//TABLE
			Attribute tableNode = node.attribute("table");
			String tableName;
			if (tableNode!=null) {
				tableName = mappings.getNamingStrategy()
					.tableName( tableNode.getValue() );
			}
			else {
				tableName = mappings.getNamingStrategy()
					.propertyToTableName(className, path);
			}
			Attribute schemaNode = node.attribute("schema");
			String schema = schemaNode==null ? mappings.getSchemaName() : schemaNode.getValue();
			model.setCollectionTable( mappings.addTable(schema, tableName) );

			log.info("Mapping collection: " + model.getRole() + " -> " + model.getCollectionTable().getName() );
		}

		//LAZINESS
		Attribute lazyNode = node.attribute("lazy");
		if (lazyNode!=null) {
			model.setLazy( StringHelper.booleanValue( lazyNode.getValue() ) );
		}

		//SORT
		Attribute sortedAtt = node.attribute("sort");
		// unsorted, natural, comparator.class.name
		if ( sortedAtt==null || sortedAtt.getValue().equals("unsorted") ) {
			model.setSorted(false);
		}
		else {
			model.setSorted(true);
			String comparatorClassName = sortedAtt.getValue();
			if ( !comparatorClassName.equals("natural") ) {
				try {
					model.setComparator( (Comparator) ReflectHelper.classForName(comparatorClassName).newInstance() );
				}
				catch (Exception e) {
					throw new MappingException( "Could not instantiate comparator class: " + comparatorClassName );
				}
			}
		}

		//ORPHAN DELETE (used for programmer error detection)
		Attribute cascadeAtt = node.attribute("cascade");
		if ( cascadeAtt!=null && cascadeAtt.getValue().equals("all-delete-orphan") ) model.setOrphanDelete(true);

		//set up second pass
		if (model instanceof List) {
			mappings.addSecondPass( new ListSecondPass(node, mappings, (List) model) );
		}
		else if (model instanceof Map) {
			mappings.addSecondPass( new MapSecondPass(node, mappings, (Map) model) );
		}
		else if (model instanceof IdentifierCollection) {
			mappings.addSecondPass( new IdentifierCollectionSecondPass(node, mappings, (IdentifierCollection) model) );
		}
		else {
			mappings.addSecondPass( new CollectionSecondPass(node, mappings, model) );
		}
	}

	public static void bindManyToOne(Element node, ManyToOne model, String path, boolean isNullable, Mappings mappings) 
	throws MappingException {
		bindColumns(node, model, isNullable, true, path, mappings);
		initOuterJoinFetchSetting(node, model);

		Attribute ukName = node.attribute("property-ref");
		if (ukName!=null) model.setReferencedPropertyName( ukName.getValue() );

		Attribute classNode = node.attribute("class");
		if (classNode!=null) {
			try {
				model.setType( TypeFactory.manyToOne( 
					ReflectHelper.classForName( getClassName(classNode, mappings) ), 
					model.getReferencedPropertyName()
				) );
			}
			catch (Exception e) {
				throw new MappingException( "Could not find class: " + classNode.getValue() );
			}
		}

		Attribute fkNode = node.attribute("foreign-key");
		if (fkNode!=null) model.setForeignKeyName( node.attributeValue("foreign-key") );
	}

	public static void bindAny(Element node, Any model, boolean isNullable, Mappings mappings) throws MappingException {
		model.setIdentifierType( getTypeFromXML(node) );
		Attribute metaAttribute = node.attribute("meta-type");
		if (metaAttribute!=null) {
			Type metaType = TypeFactory.heuristicType( metaAttribute.getValue() );
			if ( metaType==null ) throw new MappingException("could not interpret meta-type");
			model.setMetaType(metaType);
			
			Iterator iter = node.elementIterator("meta-value");
			HashMap values = new HashMap();
			while ( iter.hasNext() ) {
				Element metaValue = (Element) iter.next();
				try {
					Object value = ( (DiscriminatorType) model.getMetaType() ).fromString( metaValue.attributeValue("value") );
					Class clazz = ReflectHelper.classForName( getClassName( metaValue.attribute("class"), mappings ) );
					values.put(value, clazz);
				}
				catch (ClassCastException cce) {
					throw new MappingException( "meta-type was not a DiscriminatorType: " + metaType.getName() );
				}
				catch (HibernateException he) {
					throw new MappingException("could not interpret meta-value", he);
				}
				catch (ClassNotFoundException cnfe) {
					throw new MappingException("meta-value class not found", cnfe);
				}
			}
			if ( values.size()>0 ) model.setMetaType( new MetaType( values, model.getMetaType() ) );
		}
		
		bindColumns(node, model, isNullable, false, null, mappings);
	}

	public static void bindOneToOne(Element node, OneToOne model, boolean isNullable, Mappings mappings) throws MappingException {
		//bindColumns(node, model, isNullable, false, null, mappings);
		initOuterJoinFetchSetting(node, model);

		Attribute constrNode = node.attribute("constrained");
		boolean constrained = constrNode!=null && constrNode.getValue().equals("true");
		model.setConstrained(constrained);

		model.setForeignKeyType(
			constrained ?
			ForeignKeyDirection.FOREIGN_KEY_FROM_PARENT :
			ForeignKeyDirection.FOREIGN_KEY_TO_PARENT
		);

		Attribute fkNode = node.attribute("foreign-key");
		if (fkNode!=null) model.setForeignKeyName( fkNode.getValue() );
		
		Attribute ukName = node.attribute("property-ref");
		if (ukName!=null) model.setReferencedPropertyName( ukName.getValue() );
		
		Attribute classNode = node.attribute("class");
		if (classNode!=null) {
			try {
				model.setType( TypeFactory.oneToOne(
					ReflectHelper.classForName( getClassName(classNode, mappings) ),
					model.getForeignKeyType(),
					model.getReferencedPropertyName()
				) );
			}
			catch (Exception e) {
				throw new MappingException( "Could not find class: " + classNode.getValue() );
			}
		}

	}

	public static void bindOneToMany(Element node, OneToMany model, Mappings mappings) throws MappingException {
		try {
			model.setType( (EntityType) Hibernate.entity(
				ReflectHelper.classForName( getClassName( node.attribute("class"), mappings ) )
			) );
		}
		catch (ClassNotFoundException cnfe) {
			throw new MappingException("Associated class not found", cnfe);
		}
	}

	public static void bindColumn(Element node, Column model, boolean isNullable) {
		Attribute lengthNode = node.attribute("length");
		if (lengthNode!=null) model.setLength( Integer.parseInt( lengthNode.getValue() ) );
		Attribute nullNode = node.attribute("not-null");
		model.setNullable( (nullNode!=null) ?
			!StringHelper.booleanValue( nullNode.getValue() ) :
			isNullable
		);
		Attribute unqNode = node.attribute("unique");
		model.setUnique( unqNode!=null && StringHelper.booleanValue( unqNode.getValue() ) );
		model.setCheckConstraint( node.attributeValue("check") );
		//Attribute qtNode = node.attribute("quote");
		//model.setQuoted( qtNode!=null && StringHelper.booleanValue( qtNode.getValue() ) );
		Attribute typeNode = node.attribute("sql-type");
		model.setSqlType( (typeNode==null) ? null : typeNode.getValue() );
	}

	/**
	 * Called for arrays and primitive arrays
	 */
	public static void bindArray(Element node, Array model, String prefix, String path, Mappings mappings) throws MappingException {

		bindCollection(node, model, prefix, path, mappings);

		Attribute att = node.attribute("element-class");

		if ( att!=null ) {
			try {
				model.setElementClass( ReflectHelper.classForName( getClassName(att, mappings) ) );
			}
			catch (ClassNotFoundException cnfe) {
				throw new MappingException(cnfe);
			}
		}
		else {

			Iterator iter = node.elementIterator();
			while ( iter.hasNext() ) {
				Element subnode = (Element) iter.next();
				String name = subnode.getName();

				if ( "element".equals(name) ) {
					Type type = getTypeFromXML(subnode);
					model.setElementClass( model.isPrimitiveArray() ?
						( (PrimitiveType) type ).getPrimitiveClass() :
						type.getReturnedClass()
					);
				}
				else if (
					"one-to-many".equals(name) ||
					"many-to-many".equals(name) ||
					"composite-element".equals(name)
				) {
					try {
						model.setElementClass(
							ReflectHelper.classForName( getClassName( subnode.attribute("class"), mappings ) )
						);
					}
					catch (ClassNotFoundException cnfe) {
						throw new MappingException(cnfe);
					}
				}

			}
		}

	}

	public static void bindComponent(Element node, Component model, Class reflectedClass, String className, String path, boolean isNullable, Mappings mappings) throws MappingException {

		Attribute classNode = node.attribute("class");
		if ( "dynamic-component".equals( node.getName() ) ) {
			model.setEmbedded(false);
			model.setDynamic(true);
		}
		else if (classNode!=null) {
			try {
				model.setComponentClass( ReflectHelper.classForName( getClassName(classNode, mappings) ) );
			}
			catch (ClassNotFoundException cnfe) {
				throw new MappingException( "component class not found", cnfe );
			}
			model.setEmbedded(false);
		}
		else if (reflectedClass!=null) {
			model.setComponentClass(reflectedClass);
			model.setEmbedded(false);
		}
		else {
			// an "embedded" component (ids only)
			model.setComponentClass(
				model.getOwner().getMappedClass()
			);
			model.setEmbedded (true);
		}
		
		Iterator iter = node.elementIterator();
		while ( iter.hasNext() ) {

			Element subnode = (Element) iter.next();
			String name = subnode.getName();
			String propertyName = subnode.attributeValue("name");
			String subpath = propertyName==null ? 
				null : 
				StringHelper.qualify(path, propertyName);

			CollectionType collectType = CollectionType.collectionTypeFromString(name);
			Value value = null;
			if (collectType!=null) {
				Collection collection = collectType.create(subnode, className, subpath, model.getOwner(), mappings);
				mappings.addCollection(collection);
				value = collection;
			}
			else if ( "many-to-one".equals(name) || "key-many-to-one".equals(name) ) {
				value = new ManyToOne( model.getTable() );
				bindManyToOne(subnode, (ManyToOne) value, subpath, isNullable, mappings);
			}
			else if ( "one-to-one".equals(name) ) {
				value = new OneToOne( model.getTable(), model.getOwner().getIdentifier() );
				bindOneToOne(subnode, (OneToOne) value, isNullable, mappings);
			}
			else if ( "any".equals(name) ) {
				value = new Any( model.getTable() );
				bindAny(subnode, (Any) value, isNullable, mappings);
			}
			else if ( "property".equals(name) || "key-property".equals(name) ) {
				value = new SimpleValue( model.getTable() );
				bindSimpleValue(subnode, (SimpleValue) value, isNullable, subpath, mappings);
			}
			else if (
				"component".equals(name) ||
				"dynamic-component".equals(name) ||
				"nested-composite-element".equals(name)
			) {
				Class subreflectedClass = (model.getComponentClass()==null) ?
					null :
					ReflectHelper.reflectedPropertyClass( model.getComponentClass(), propertyName );
				value = ( model.getOwner()!=null ) ?
					new Component( model.getOwner() ) :  // a class component
					new Component( model.getTable() );   // a composite element
				bindComponent(subnode, (Component) value, subreflectedClass, className, subpath, isNullable, mappings);
			}
			else if ( "parent".equals(name) ) {
				model.setParentProperty(propertyName);
			}
			
			if ( value!=null) model.addProperty( createProperty(value, propertyName, model.getComponentClass(), subnode, mappings) );
		}

		int span = model.getPropertySpan();
		String[] names = new String[span];
		Type[] types = new Type[span];
		Cascades.CascadeStyle[] cascade = new Cascades.CascadeStyle[span];
		int[] joinedFetch = new int[span];
		iter = model.getPropertyIterator();
		int i=0;
		while ( iter.hasNext() ) {
			Property prop = (Property) iter.next();
			if ( prop.isFormula() ) {
				throw new MappingException( "properties of components may not be formulas: " + prop.getName() );
			}
			if ( !prop.isInsertable() || !prop.isUpdateable() ) {
				throw new MappingException( "insert=\"false\", update=\"false\" not supported for properties of components: " + prop.getName() );
			}
			names[i] = prop.getName();
			types[i] = prop.getType();
			cascade[i] = prop.getCascadeStyle();
			joinedFetch[i] = prop.getValue().getOuterJoinFetchSetting();
			i++;
		}
		final Type componentType;
		if ( model.isDynamic() ) {
			componentType = new DynamicComponentType(names, types, joinedFetch, cascade);
		}
		else {
			Getter[] getters = new Getter[span];
			Setter[] setters = new Setter[span];
			iter = model.getPropertyIterator();
			boolean foundCustomAccessor=false;
			i=0;
			while ( iter.hasNext() ) {
				Property prop = (Property) iter.next();
				setters[i] = prop.getSetter( model.getComponentClass() );
				getters[i] = prop.getGetter( model.getComponentClass() );
				if ( !prop.isBasicPropertyAccessor() ) foundCustomAccessor = true;
				i++;
			}
			componentType = new ComponentType(
				model.getComponentClass(),
				names,
				getters,
				setters,
				foundCustomAccessor,
				types,
				joinedFetch,
				cascade,
				model.getParentProperty()
			);
		}
		model.setType(componentType);
	}

	private static Type getTypeFromXML(Element node) throws MappingException {
		Type type;
		Attribute typeNode = node.attribute("type");
		if (typeNode==null) typeNode = node.attribute("id-type"); //for an any
		if (typeNode==null) {
			return null; //we will have to use reflection
		}
		else {
			type = TypeFactory.heuristicType( typeNode.getValue() );
			if (type==null) throw new MappingException( "Could not interpret type: " + typeNode.getValue() );
		}
		return type;
	}

	private static void initOuterJoinFetchSetting(Element node, Fetchable model) {
		Attribute jfNode = node.attribute("outer-join");
		if ( jfNode==null ) {
			model.setOuterJoinFetchSetting(OuterJoinLoader.AUTO);
		}
		else {
			String eoj = jfNode.getValue();
			if ( "auto".equals(eoj) ) {
				model.setOuterJoinFetchSetting(OuterJoinLoader.AUTO);
			}
			else {
				model.setOuterJoinFetchSetting(
					"true".equals(eoj) ? OuterJoinLoader.EAGER : OuterJoinLoader.LAZY
				);
			}
		}
	}

	private static void makeIdentifier(Element node, SimpleValue model, Mappings mappings)
	        throws MappingException {
		//GENERATOR

		Element subnode = node.element("generator");
		if ( subnode!=null ) {
			model.setIdentifierGeneratorStrategy( subnode.attributeValue("class") );

			Properties params = new Properties();

			if ( mappings.getSchemaName()!=null ) {
				params.setProperty( PersistentIdentifierGenerator.SCHEMA, mappings.getSchemaName() );
			}

			params.setProperty( PersistentIdentifierGenerator.TABLE, model.getTable().getName() );

			params.setProperty(
				PersistentIdentifierGenerator.PK,
				( (Column) model.getColumnIterator().next() ).getName()
			);

			Iterator iter = subnode.elementIterator("param");
			while( iter.hasNext() ) {
				Element childNode = (Element) iter.next();
				params.setProperty(
					childNode.attributeValue("name"),
					childNode.getText()
				);
			}

			model.setIdentifierGeneratorProperties(params);
		}

		model.getTable().setIdentifierValue(model);

		// ID UNSAVED-VALUE
		Attribute nullValueNode = node.attribute("unsaved-value");
		if (nullValueNode!=null) {
			model.setNullValue( nullValueNode.getValue() );
		}
		else {
			model.setNullValue("null");
		}
	}

	private static final void makeVersion(Element node, SimpleValue model) {
		
		// VERSION UNSAVED-VALUE
		Attribute nullValueNode = node.attribute("unsaved-value");
		if (nullValueNode!=null) {
			model.setNullValue( nullValueNode.getValue() );
		}
		else {
			model.setNullValue("undefined");
		}

	}
	
	protected static void propertiesFromXML(Element node, PersistentClass model, Mappings mappings) throws MappingException {

		Table table = model.getTable();

		Iterator iter = node.elementIterator();
		while( iter.hasNext() ) {
			Element subnode = (Element) iter.next();
			String name = subnode.getName();
			String propertyName = subnode.attributeValue("name");

			CollectionType collectType = CollectionType.collectionTypeFromString(name);
			Value value = null;
			if (collectType!=null) {
				Collection collection = collectType.create(subnode, model.getName(), propertyName, model, mappings);
				mappings.addCollection(collection);
				value = collection;
			}
			else if ( "many-to-one".equals(name) ) {
				value = new ManyToOne(table);
				bindManyToOne(subnode, (ManyToOne) value, propertyName, true, mappings);
			}
			else if ( "any".equals(name) ) {
				value = new Any(table);
				bindAny(subnode, (Any) value, true, mappings);
			}
			else if ( "one-to-one".equals(name) ) {
				OneToOne oneToOne = new OneToOne( table, model.getIdentifier() );
				bindOneToOne(subnode, oneToOne, true, mappings);
				value = oneToOne;
			}
			else if ( "property".equals(name) ) {
				value = new SimpleValue(table);
				bindSimpleValue(subnode, (SimpleValue) value, true, propertyName, mappings);
			}
			else if ( "component".equals(name) || "dynamic-component".equals(name) ) {
				Class reflectedClass = ReflectHelper.reflectedPropertyClass( model.getMappedClass(), propertyName );
				value = new Component(model);
				bindComponent(subnode, (Component) value, reflectedClass, model.getName(), propertyName, true, mappings);
			}
			else if ( "subclass".equals(name) ) {
				handleSubclass(model, mappings, subnode);
			}
			else if ( "joined-subclass".equals(name) ) {
				handleJoinedSubclass(model, mappings, subnode);
			}
			
			if ( value!=null) model.addNewProperty( createProperty(value, propertyName, model.getMappedClass(), subnode, mappings) );
		}
	}

	private static Property createProperty(Value value, String propertyName, Class parentClass, Element subnode, Mappings mappings) 
	throws MappingException {
		
		if ( parentClass!=null && value.isSimpleValue() ) ( (SimpleValue) value ).setTypeByReflection(parentClass, propertyName);
		
		if ( value instanceof ToOne ) { //this is done here 'cos we might only know the type here (ugly!)
			String propertyRef = ( (ToOne) value ).getReferencedPropertyName();
			if (propertyRef!=null) mappings.addUniquePropertyReference( 
				( (EntityType) value.getType() ).getAssociatedClass(),
				propertyRef
			);
		}
		
		value.createForeignKey();
		Property prop = new Property();
		prop.setValue(value);
		bindProperty(subnode, prop, mappings);
		return prop;
	}

	private static void handleJoinedSubclass(PersistentClass model, Mappings mappings, Element subnode) throws MappingException {
		Subclass subclass = new Subclass(model);
		bindJoinedSubclass( subnode, subclass, mappings );
		model.addSubclass(subclass);
		mappings.addClass(subclass);
	}

	private static void handleSubclass(PersistentClass model, Mappings mappings, Element subnode) throws MappingException {
		Subclass subclass = new Subclass(model);
		bindSubclass( subnode, subclass, mappings );
		model.addSubclass(subclass);
		mappings.addClass(subclass);
	}

	/**
	 * Called for Lists, arrays, primitive arrays
	 */
	public static void bindListSecondPass(Element node, List model, java.util.Map classes, Mappings mappings) throws MappingException {

		bindCollectionSecondPass(node, model, classes, mappings);

		Element subnode = node.element("index");
		SimpleValue iv = new SimpleValue( model.getCollectionTable() );
		bindSimpleValue(subnode, iv, model.isOneToMany(), IndexedCollection.DEFAULT_INDEX_COLUMN_NAME, mappings);
		iv.setType(Hibernate.INTEGER);
		model.setIndex(iv);

	}

	public static void bindIdentifierCollectionSecondPass(Element node, IdentifierCollection model, java.util.Map persistentClasses, Mappings mappings) throws MappingException {

		bindCollectionSecondPass(node, model, persistentClasses, mappings);

		Element subnode = node.element("collection-id");
		SimpleValue id = new SimpleValue( model.getCollectionTable() );
		bindSimpleValue(subnode, id, false, IdentifierCollection.DEFAULT_IDENTIFIER_COLUMN_NAME, mappings);
		model.setIdentifier(id);
		makeIdentifier(subnode, id, mappings);

	}

	/**
	 * Called for Maps
	 */
	public static void bindMapSecondPass(Element node, Map model, java.util.Map classes, Mappings mappings) throws MappingException {

		bindCollectionSecondPass(node, model, classes, mappings);

		Iterator iter = node.elementIterator();
		while( iter.hasNext() ) {
			Element subnode = (Element) iter.next();
			String name = subnode.getName();

			if ( "index".equals(name) ) {
				SimpleValue value = new SimpleValue( model.getCollectionTable() );
				bindSimpleValue(subnode, value, model.isOneToMany(), IndexedCollection.DEFAULT_INDEX_COLUMN_NAME, mappings);
				model.setIndex(value);
				if ( model.getIndex().getType()==null ) {
					throw new MappingException( "map index element must specify a type" + model.getRole() );
				}
			}
			else if ( "index-many-to-many".equals(name) ) {
				ManyToOne mto = new ManyToOne( model.getCollectionTable() );
				bindManyToOne( subnode, mto, IndexedCollection.DEFAULT_INDEX_COLUMN_NAME, model.isOneToMany(), mappings );
				model.setIndex(mto);

			}
			else if ( "composite-index".equals(name) ) {
				Component component = new Component( model.getCollectionTable() );
				bindComponent(subnode, component, null, model.getRole(), "index", model.isOneToMany(), mappings);
				model.setIndex(component);
			}
			else if ( "index-many-to-any".equals(name) ) {
				Any any = new Any( model.getCollectionTable() );
				bindAny( subnode, any, model.isOneToMany(), mappings );
				model.setIndex(any);
			}
		}

	}

	/**
	 * Called for all collections
	 */
	public static void bindCollectionSecondPass(Element node, Collection model, java.util.Map persistentClasses, Mappings mappings) throws MappingException {

		if ( model.isOneToMany() ) {
			OneToMany oneToMany = (OneToMany) model.getElement();
			Class assocClass = oneToMany.getEntityType().getAssociatedClass();
			PersistentClass persistentClass = (PersistentClass) persistentClasses.get(assocClass);
			if (persistentClass==null) throw new MappingException(
				"Association references unmapped class: " + assocClass.getName()
			);
			oneToMany.setAssociatedClass(persistentClass);
			model.setCollectionTable( persistentClass.getTable() );

			log.info("Mapping collection: " + model.getRole() + " -> " + model.getCollectionTable().getName() );
		}

		//CHECK
		Attribute chNode = node.attribute("check");
		if (chNode!=null) {
			model.getCollectionTable().addCheckConstraint( chNode.getValue() );
		}
		
		//contained elements:
		Iterator iter = node.elementIterator();
		while( iter.hasNext() ) {
			Element subnode = (Element) iter.next();
			String name = subnode.getName();

			if ( "key".equals(name) ) {
				SimpleValue key = new SimpleValue( model.getCollectionTable() );
				bindSimpleValue(subnode, key, model.isOneToMany(), Collection.DEFAULT_KEY_COLUMN_NAME, mappings);
				key.setType( model.getOwner().getIdentifier().getType() );
				if ( key.getType().getReturnedClass().isArray() ) throw new MappingException(
					"illegal use of an array as an identifier (arrays don't reimplement equals)"
				);
				model.setKey(key);
			}
			else if ( "element".equals(name) ) {
				SimpleValue elt = new SimpleValue( model.getCollectionTable() );
				model.setElement(elt);
				bindSimpleValue(subnode, elt, true, Collection.DEFAULT_ELEMENT_COLUMN_NAME, mappings);
			}
			else if ( "many-to-many".equals(name) ) {
				ManyToOne element = new ManyToOne( model.getCollectionTable() );
				model.setElement(element);
				bindManyToOne(subnode, element, Collection.DEFAULT_ELEMENT_COLUMN_NAME, false, mappings);
			}
			else if ( "composite-element".equals(name) ) {
				Component element = new Component( model.getCollectionTable() );
				model.setElement(element);
				bindComponent(subnode, element, null, model.getRole(), "element", true, mappings);
			}
			else if ( "many-to-any".equals(name) ) {
				Any element = new Any( model.getCollectionTable() );
				model.setElement(element);
				bindAny(subnode, element, true, mappings);
			}
			else if ( "jcs-cache".equals(name) || "cache".equals(name) ) {
				CacheConcurrencyStrategy cache = CacheFactory.createCache( 
					subnode, model.getRole(), model.getOwner().isMutable()
				);
				mappings.addCache( model.getRole(), cache );
				model.setCache(cache);
			}

		}

	}

	public static void bindRoot(Document doc, Mappings model) throws MappingException {

		Element hmNode = doc.getRootElement();
		Attribute schemaNode = hmNode.attribute("schema");
		model.setSchemaName( (schemaNode==null) ? null : schemaNode.getValue() );
		Attribute dcNode = hmNode.attribute("default-cascade");
		model.setDefaultCascade( (dcNode==null) ? "none" : dcNode.getValue() );
		Attribute daNode = hmNode.attribute("default-access");
		model.setDefaultAccess( (daNode==null) ? "property" : daNode.getValue() );
		
		Attribute aiNode = hmNode.attribute("auto-import");
		model.setAutoImport( (aiNode==null) ? true : "true".equals( aiNode.getValue() ) );
		Attribute packNode = hmNode.attribute("package");
		if (packNode!=null) model.setDefaultPackage( packNode.getValue() );

		Iterator nodes = hmNode.elementIterator("class");
		while ( nodes.hasNext() ) {
			Element n = (Element) nodes.next();
			RootClass rootclass = new RootClass();
			Binder.bindRootClass(n, rootclass, model);
			model.addClass(rootclass);
		}
		
		Iterator subclassnodes = hmNode.elementIterator("subclass");
			while ( subclassnodes.hasNext() ) {
				Element subnode = (Element) subclassnodes.next();
				PersistentClass superModel = getSuperclass(model, subnode);
				handleSubclass(superModel, model, subnode);
			}
		
		Iterator joinedsubclassnodes = hmNode.elementIterator("joined-subclass");
			while ( joinedsubclassnodes.hasNext() ) {
				Element subnode = (Element) joinedsubclassnodes.next();
				PersistentClass superModel = getSuperclass(model, subnode);
				handleJoinedSubclass(superModel, model, subnode);
			}
		
		
		nodes = hmNode.elementIterator("query");
		while ( nodes.hasNext() ) {
			Element n = (Element) nodes.next();
			String qname = n.attributeValue("name");
			String query = n.getText();
			log.debug("Named query: " + qname + " -> " + query);
			model.addQuery(qname, query);
		}

		nodes = hmNode.elementIterator("sql-query");
		while ( nodes.hasNext() ) {
			Element queryElem = (Element) nodes.next();
			
			String queryName = queryElem.attribute("name").getValue();
			NamedSQLQuery namedQuery = new NamedSQLQuery( queryElem.getText() );				
			
			Iterator returns = queryElem.elementIterator("return");
			while ( returns.hasNext() ) {
				Element returnElem = (Element) returns.next();
				String alias = returnElem.attribute("alias").getText();
				Class clazz;
				try {
					clazz = ReflectHelper.classForName( getClassName( returnElem.attribute("class"), model ) );
				}
				catch ( ClassNotFoundException cnfe ) {
					throw new MappingException("class not found for alias: " + alias, cnfe);
				}
				namedQuery.addAliasedClass(alias, clazz);
			}
			
			
			Iterator tables = queryElem.elementIterator("synchronize");
			while ( tables.hasNext() ) {
				namedQuery.addSynchronizedTable( ( (Element) tables.next() ).attributeValue("table") );
			}
			
			log.debug( "Named sql query: " + queryName + " -> " + namedQuery.getQueryString() );
			model.addSQLQuery(queryName, namedQuery);
		}
		
		nodes = hmNode.elementIterator("import");
		while ( nodes.hasNext() ) {
			Element n = (Element) nodes.next();
			String className = getClassName( n.attribute("class"), model );
			Attribute renameNode = n.attribute("rename");
			String rename = (renameNode==null) ? StringHelper.unqualify(className) : renameNode.getValue();
			log.debug("Import: " + rename + " -> " + className);
			model.addImport(className, rename);
		}
	}

	private static PersistentClass getSuperclass(Mappings model, Element subnode) throws MappingException {
		String extendsValue = getClassName( subnode.attribute("extends"), model );
		Class superclass;
		try {
			superclass = ReflectHelper.classForName(extendsValue);
		} 
		catch (ClassNotFoundException e) {
			throw new MappingException("extended class not found: " + extendsValue, e);
		}
		PersistentClass superModel = model.getClass(superclass);
			
		if (superModel==null) {
			throw new MappingException( "Cannot extend unmapped class: " + extendsValue );
		}
		return superModel;
	}

	abstract static class SecondPass {
		Element node;
		Mappings mappings;
		Collection collection;
		SecondPass(Element node, Mappings mappings, Collection collection) {
			this.node = node;
			this.collection = collection;
			this.mappings = mappings;
		}
		final void doSecondPass(java.util.Map persistentClasses) throws MappingException {
			if ( log.isDebugEnabled() ) log.debug("Second pass for collection: " + collection.getRole() );
			
			secondPass(persistentClasses);
			collection.createAllKeys();
			
			if ( log.isDebugEnabled() ) {
				String msg = "Mapped collection key: " + columns( collection.getKey() );
				if ( collection.isIndexed() ) msg+= ", index: " + columns( ( (IndexedCollection) collection ).getIndex() );
				if ( collection.isOneToMany() ) {
					msg += ", one-to-many: " + collection.getElement().getType().getName();
				}
				else {
					msg += ", element: " + columns( collection.getElement() );
					msg += ", type: " + collection.getElement().getType().getName();
				}
				log.debug(msg);
			}
		}
		abstract void secondPass(java.util.Map persistentClasses) throws MappingException;
	}

	static class CollectionSecondPass extends SecondPass {
		CollectionSecondPass(Element node, Mappings mappings, Collection collection) {
			super(node, mappings, collection);
		}
		void secondPass(java.util.Map persistentClasses) throws MappingException {
			Binder.bindCollectionSecondPass( node, collection, persistentClasses, mappings );
		}

	}

	static class IdentifierCollectionSecondPass extends SecondPass {
		IdentifierCollectionSecondPass(Element node, Mappings mappings, IdentifierCollection collection) {
			super(node, mappings, collection);
		}
		void secondPass(java.util.Map persistentClasses) throws MappingException {
			Binder.bindIdentifierCollectionSecondPass( node, (IdentifierCollection) collection, persistentClasses, mappings );
		}

	}

	static class MapSecondPass extends SecondPass {
		MapSecondPass(Element node, Mappings mappings, Map collection) {
			super(node, mappings, collection);
		}
		void secondPass(java.util.Map persistentClasses) throws MappingException {
			Binder.bindMapSecondPass( node, (Map) collection, persistentClasses, mappings );
		}

	}

	static class ListSecondPass extends SecondPass {
		ListSecondPass(Element node, Mappings mappings, List collection) {
			super(node, mappings, collection);
		}
		void secondPass(java.util.Map persistentClasses) throws MappingException {
			Binder.bindListSecondPass( node, (List) collection, persistentClasses, mappings );
		}

	}

	//This inner class implements a case statement....perhaps im being a bit over-clever here
	abstract static class CollectionType {
		private String xmlTag;
		public abstract Collection create(Element node, String className, String path, PersistentClass owner, Mappings mappings) throws MappingException;
		CollectionType(String xmlTag) {
			this.xmlTag = xmlTag;
		}
		public String toString() {
			return xmlTag;
		}
		private static final CollectionType MAP = new CollectionType("map") {
			public Collection create(Element node, String prefix, String path, PersistentClass owner, Mappings mappings) throws MappingException {
				Map map = new Map(owner);
				bindCollection(node, map, prefix, path, mappings);
				return map;
			}
		};
		private static final CollectionType SET = new CollectionType("set") {
			public Collection create(Element node, String prefix, String path, PersistentClass owner, Mappings mappings) throws MappingException {
				Set set = new Set(owner);
				bindCollection(node, set, prefix, path, mappings);
				return set;
			}
		};
		private static final CollectionType LIST = new CollectionType("list") {
			public Collection create(Element node, String prefix, String path, PersistentClass owner, Mappings mappings) throws MappingException {
				List list = new List(owner);
				bindCollection(node, list, prefix, path, mappings);
				return list;
			}
		};
		private static final CollectionType BAG = new CollectionType("bag") {
			public Collection create(Element node, String prefix, String path, PersistentClass owner, Mappings mappings) throws MappingException {
				Bag bag = new Bag(owner);
				bindCollection(node, bag, prefix, path, mappings);
				return bag;
			}
		};
		private static final CollectionType IDBAG = new CollectionType("idbag") {
			public Collection create(Element node, String prefix, String path, PersistentClass owner, Mappings mappings) throws MappingException {
				IdentifierBag bag = new IdentifierBag(owner);
				bindCollection(node, bag, prefix, path, mappings);
				return bag;
			}
		};
		private static final CollectionType ARRAY = new CollectionType("array") {
			public Collection create(Element node, String prefix, String path, PersistentClass owner, Mappings mappings) throws MappingException {
				Array array = new Array(owner);
				bindArray(node, array, prefix, path, mappings);
				return array;
			}
		};
		private static final CollectionType PRIMITIVE_ARRAY = new CollectionType("primitive-array") {
			public Collection create(Element node, String prefix, String path, PersistentClass owner, Mappings mappings) throws MappingException {
				PrimitiveArray array = new PrimitiveArray(owner);
				bindArray(node, array, prefix, path, mappings);
				return array;
			}
		};
		private static final HashMap INSTANCES = new HashMap();
		static {
			INSTANCES.put(MAP.toString(), MAP);
			INSTANCES.put(BAG.toString(), BAG);
			INSTANCES.put(IDBAG.toString(), IDBAG);
			INSTANCES.put(SET.toString(), SET);
			INSTANCES.put(LIST.toString(), LIST);
			INSTANCES.put(ARRAY.toString(), ARRAY);
			INSTANCES.put(PRIMITIVE_ARRAY.toString(), PRIMITIVE_ARRAY);
		}
		public static CollectionType collectionTypeFromString(String xmlTagName) {
			return (CollectionType) INSTANCES.get(xmlTagName);
		}
	}

	private static int getOptimisticLockMode(Attribute olAtt) throws MappingException {
		
		if (olAtt==null) return Versioning.OPTIMISTIC_LOCK_VERSION;
		String olMode = olAtt.getValue();
		if ( olMode==null || "version".equals(olMode) ) {
			return Versioning.OPTIMISTIC_LOCK_VERSION;
		}
		else if ( "dirty".equals(olMode) ) {
			return Versioning.OPTIMISTIC_LOCK_DIRTY;
		}
		else if ( "all".equals(olMode) ) {
			return Versioning.OPTIMISTIC_LOCK_ALL;
		}
		else if ( "none".equals(olMode) ) {
			return Versioning.OPTIMISTIC_LOCK_NONE;
		}
		else {
			throw new MappingException("Unsupported optimistic-lock style: " + olMode);
		}
	}
	
	private static final java.util.Map getMetas(Element node) {
		java.util.Map map = new HashMap();
		Iterator iter = node.elementIterator("meta");
		while ( iter.hasNext() ) {
			Element metaNode = (Element) iter.next();
			String name = metaNode.attributeValue("attribute");
			MetaAttribute meta = (MetaAttribute) map.get(name);
			if (meta==null) {
				meta = new MetaAttribute();
				map.put(name, meta);
			}
			meta.addValue( metaNode.getText() );
		}
		return map;
	}
	
	private static String getClassName(Attribute att, Mappings model) {
		String result = att.getValue();
		if (result==null) return null;
		if ( result.indexOf('.')<0 && model.getDefaultPackage()!=null ) {
			result = model.getDefaultPackage() + StringHelper.DOT + result;
		}
		return result;
	}

}

