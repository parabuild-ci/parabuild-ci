//$Id: AbstractEntityPersister.java,v 1.51 2005/01/10 03:10:25 oneovthafew Exp $
package net.sf.hibernate.persister;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.beans.BulkBean;
import net.sf.cglib.reflect.FastClass;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.InstantiationException;
import net.sf.hibernate.JDBCException;
import net.sf.hibernate.Lifecycle;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.PropertyAccessException;
import net.sf.hibernate.PropertyNotFoundException;
import net.sf.hibernate.StaleObjectStateException;
import net.sf.hibernate.Validatable;
import net.sf.hibernate.cache.CacheConcurrencyStrategy;
import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.Cascades;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.engine.Versioning;
import net.sf.hibernate.exception.JDBCExceptionHelper;
import net.sf.hibernate.exception.SQLExceptionConverter;
import net.sf.hibernate.id.IdentifierGenerationException;
import net.sf.hibernate.id.IdentifierGenerator;
import net.sf.hibernate.id.IdentifierGeneratorFactory;
import net.sf.hibernate.id.IdentityGenerator;
import net.sf.hibernate.impl.MessageHelper;
import net.sf.hibernate.loader.BatchingEntityLoader;
import net.sf.hibernate.loader.EntityLoader;
import net.sf.hibernate.loader.Loader;
import net.sf.hibernate.loader.UniqueEntityLoader;
import net.sf.hibernate.mapping.Column;
import net.sf.hibernate.mapping.Component;
import net.sf.hibernate.mapping.PersistentClass;
import net.sf.hibernate.mapping.Property;
import net.sf.hibernate.mapping.Subclass;
import net.sf.hibernate.mapping.Value;
import net.sf.hibernate.metadata.ClassMetadata;
import net.sf.hibernate.property.Getter;
import net.sf.hibernate.property.Setter;
import net.sf.hibernate.proxy.CGLIBProxyFactory;
import net.sf.hibernate.proxy.HibernateProxy;
import net.sf.hibernate.proxy.ProxyFactory;
import net.sf.hibernate.sql.Alias;
import net.sf.hibernate.sql.SelectFragment;
import net.sf.hibernate.sql.SimpleSelect;
import net.sf.hibernate.sql.Template;
import net.sf.hibernate.type.AbstractComponentType;
import net.sf.hibernate.type.EntityType;
import net.sf.hibernate.type.IdentifierType;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.type.TypeFactory;
import net.sf.hibernate.type.VersionType;
import net.sf.hibernate.util.ReflectHelper;
import net.sf.hibernate.util.StringHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Superclass for built-in mapping strategies. Implements functionality
 * common to both mapping strategies.<br>
 * <br>
 * May be considered an immutable view of the mapping object.<br>
 * 
 * @author Gavin King
 */
public abstract class AbstractEntityPersister 
	extends AbstractPropertyMapping 
	implements OuterJoinLoadable, Queryable, ClassMetadata, UniqueKeyLoadable, SQLLoadable {
	
	private static final Log log = LogFactory.getLog(AbstractEntityPersister.class);
	
	public static final String ENTITY_CLASS = "class";
	
	private final Dialect dialect;
	private final SQLExceptionConverter sqlExceptionConverter;

	// The class itself
	private final Class mappedClass;
	private final boolean polymorphic;
	private final boolean explicitPolymorphism;
	private final boolean inherited;
	private final boolean hasSubclasses;
	private final boolean versioned;
	private final boolean abstractClass;
	private final boolean implementsLifecycle;
	private final boolean implementsValidatable;
	private final boolean hasCollections;
	private final boolean hasCascades;
	private final boolean mutable;
	private final boolean selectBeforeUpdate;
	private final Class superclass;
	private final boolean dynamicUpdate;
	private final boolean dynamicInsert;
	private final int optimisticLockMode;
	private final String className;
	private final int batchSize;	
	private final Type entityType;
	private final Constructor constructor;
	private final BulkBean optimizer;
	private final FastClass fastClass;
	private final String rootClassName;

	// The optional SQL string defined in the where attribute
	private final String sqlWhereString;
	private final String sqlWhereStringTemplate;
	
	// proxies (if the proxies are interfaces, we use an array of 
	// interfaces of all subclasses)
	private final Class concreteProxyClass;
	private final boolean hasProxy;
	private final ProxyFactory proxyFactory;
	
	// The SQL string used to retrieve a primary key generated
	// by the SQL INSERT
	private final boolean useIdentityColumn;
	private final String identitySelectString;
	
	// the identifier property
	private final boolean hasEmbeddedIdentifier;
	private final String identifierPropertyName;
	private final String[] identifierColumnNames;
	private final String[] identifierAliases;
	private final Cascades.IdentifierValue unsavedIdentifierValue;
	private final Type identifierType;
	private final Setter identifierSetter;
	private final Getter identifierGetter;
	private final IdentifierGenerator identifierGenerator;
	
	// version property
	//private final String versionPropertyName;
	private final String versionColumnName;
	private final VersionType versionType;
	private final Getter versionGetter;
	private final int versionProperty;
	private final Cascades.VersionValue unsavedVersionValue;
	private boolean jdbcBatchVersionedData;
	
	// other properties (for this concrete class only, not the 
	// subclass closure)
	private final int hydrateSpan;
	private final String[] propertyNames;
	private final Type[] propertyTypes;
	private final boolean[] propertyUpdateability;
	private final boolean[] propertyInsertability;
	private final boolean[] propertyNullability;
	private final Getter[] getters;
	private final Setter[] setters;
	private final Cascades.CascadeStyle[] cascadeStyles;
	
	private final Map gettersByPropertyName = new HashMap();
	private final Map settersByPropertyName = new HashMap();
	private final Map typesByPropertyName = new HashMap();

	// the cache
	private final CacheConcurrencyStrategy cache;
	
	private final Map uniqueKeyLoaders = new HashMap();
	private final Map uniqueKeyColumns = new HashMap();
	
	private final Map subclassPropertyAliases = new HashMap();

	private final Map lockers = new HashMap();

	private String getLockString(LockMode lockMode) {
		return (String) lockers.get(lockMode);	
	}
	
	public final Class getMappedClass() {
		return mappedClass;
	}
	
	public final String getClassName() {
		return className;
	}
	
	public Serializable getIdentifierSpace() {
		return rootClassName;
	}
	
	public String identifierSelectFragment(String name, String suffix) {
		return new SelectFragment()
			.setSuffix(suffix)
			.addColumns( name, getIdentifierColumnNames(), getIdentifierAliases() )
			.toFragmentString()
			.substring(2); //strip leading ", "
	}
	
	public Cascades.CascadeStyle[] getPropertyCascadeStyles() {
		return cascadeStyles;
	}
	
	/**
	 * Set the given values to the mapped properties of the given object
	 */
	public void setPropertyValues(Object object, Object[] values) throws HibernateException {
		try{
			if (optimizer!=null) {
				optimizer.setPropertyValues(object, values);
				return;
			}  
		}
		catch (Throwable t) {
			throw new PropertyAccessException(
				t, 
				ReflectHelper.PROPERTY_SET_EXCEPTION, 
				true, 
				mappedClass,
				ReflectHelper.getPropertyName(t, optimizer)
			);
		}
		
		for (int j=0; j<getHydrateSpan(); j++) getSetters()[j].set(object, values[j]);
	}
	
	/**
	 * Return the values of the mapped properties of the object
	 */
	public Object[] getPropertyValues(Object object) throws HibernateException {
		try{
			if (optimizer!=null) {
				return optimizer.getPropertyValues(object);
			}
		}
		catch (Throwable t) {
			throw new PropertyAccessException(
				t, 
				ReflectHelper.PROPERTY_GET_EXCEPTION, 
				false, 
				mappedClass,
				ReflectHelper.getPropertyName(t, optimizer)
			);
		}
		
		int span = getHydrateSpan();
		Object[] result = new Object[span];
		for (int j=0; j<span; j++) result[j] = getGetters()[j].get(object);
		return result;
	}
	
	/**
	 * Get the value of the numbered property
	 */
	public Object getPropertyValue(Object object, int i) throws HibernateException {
		return getGetters()[i].get(object);
	}
	
	/**
	 * Set the value of the numbered property
	 */
	public void setPropertyValue(Object object, int i, Object value) throws HibernateException {
		getSetters()[i].set(object, value);
	}
	
	/**
	 * Determine if the given field values are dirty
	 */
	public int[] findDirty(Object[] x, Object[] y, Object object, SessionImplementor session) throws HibernateException {
		int[] props = TypeFactory.findDirty(propertyTypes, x, y, propertyUpdateability, session);
		if ( props==null) {
			return null;
		}
		else {
			if ( log.isTraceEnabled() ) {
				for ( int i=0; i<props.length; i++ ) {
					log.trace( StringHelper.qualify( className, propertyNames[ props[i] ] ) + " is dirty" );
				}
			}
			return props;
		}
	}
	
	/**
	 * Determine if the given field values are dirty
	 */
	public int[] findModified(Object[] old, Object[] current, Object object, SessionImplementor session) throws HibernateException {
		int[] props = TypeFactory.findModified(propertyTypes, old, current, propertyUpdateability, session);
		if ( props==null) {
			return null;
		}
		else {
			if ( log.isTraceEnabled() ) {
				for ( int i=0; i<props.length; i++ ) {
					log.trace( StringHelper.qualify( className, propertyNames[ props[i] ] ) + " is dirty" );
				}
			}
			return props;
		}
	}
	
	public Serializable getIdentifier(Object object) throws HibernateException {
		final Object id;
		if (hasEmbeddedIdentifier) {
			id = object;
		}
		else {
			if (identifierGetter==null) throw new HibernateException( "The class has no identifier property: " + className );
			id = identifierGetter.get(object);
		}
		try {
			return (Serializable) id;
		}
		catch (ClassCastException cce) {
			throw new ClassCastException( "Identifier classes must be serializable: " + cce.getMessage() );
		}
	}
	
	public Object getVersion(Object object) throws HibernateException {
		if ( !versioned ) return null;
		return versionGetter.get(object);
	}
	
	public void setIdentifier(Object object, Serializable id) throws HibernateException {
		if (hasEmbeddedIdentifier) {
			if (object!=id) {
				AbstractComponentType copier = (AbstractComponentType) identifierType;
				copier.setPropertyValues( object, copier.getPropertyValues(id) );
			}
		}
		else if (identifierSetter!=null) {
			identifierSetter.set(object, id);
		}
	}
	
	/**
	 * Return a new instance initialized with the given identifier
	 */
	public Object instantiate(Serializable id) throws HibernateException {
		if ( hasEmbeddedIdentifier && id.getClass()==mappedClass ) {
			return id;
		}
		else {
			if (abstractClass) throw new HibernateException("Cannot instantiate abstract class or interface: " + className);
			final Object result;
			if (optimizer != null) { 
				try {
					result = fastClass.newInstance();
				}
				catch (Throwable t) {
					throw new InstantiationException("Could not instantiate entity with CGLIB: ", mappedClass, t);
				}
			}
			else {
				try {
					result = constructor.newInstance(null);
				}
				catch (Exception e) {
					throw new InstantiationException("Could not instantiate entity: ", mappedClass, e);
				}
			}
			setIdentifier(result, id);
			return result;
		}
	}
	
	// Getters and Setters
	
	protected Setter[] getSetters() {
		return setters;
	}
	
	protected Getter[] getGetters() {
		return getters;
	}
	
	public Type[] getPropertyTypes() {
		return propertyTypes;
	}
	
	public Type getIdentifierType() {
		return identifierType;
	}
	
	public String[] getIdentifierColumnNames() {
		return identifierColumnNames;
	}
	
	protected String[] getIdentifierAliases() {
		return identifierAliases;
	}
	
	public boolean isPolymorphic() {
		return polymorphic;
	}
	
	public boolean isInherited() {
		return inherited;
	}
	
	public boolean hasCascades() {
		return hasCascades;
	}
	
	public CacheConcurrencyStrategy getCache() {
		return cache;
	}
	
	public boolean hasIdentifierProperty() {
		return identifierGetter!=null;
	}
	
	public VersionType getVersionType() {
		return versionType;
	}
	public int getVersionProperty() {
		return versionProperty;
	}
	
	public boolean isVersioned() {
		return versioned;
	}

	public boolean isBatchable() {
		return jdbcBatchVersionedData || !isVersioned();
	}

	public boolean isIdentifierAssignedByInsert() {
		return useIdentityColumn;
	}
	
	public boolean isUnsaved(Object object) throws HibernateException {
		final Serializable id;
		if ( hasIdentifierPropertyOrEmbeddedCompositeIdentifier() ) {
			id = getIdentifier(object);
		}
		else {
			id = null;
		}
		// we always assume a transient instance with a null
		// identifier or no identifier property is unsaved!
		if (id==null) return true;
		
		if ( isVersioned() ) {
			// let this take precedence if defined, since it works for
			// assigned identifiers
			Boolean result = unsavedVersionValue.isUnsaved( getVersion(object) );
			if (result!=null) return result.booleanValue(); 
		}
		
		return unsavedIdentifierValue.isUnsaved(id);

	}
	
	public String[] getPropertyNames() {
		return propertyNames;
	}
	
	public String getIdentifierPropertyName() {
		return identifierPropertyName;
	}
	
	public String getVersionColumnName() {
		return versionColumnName;
	}
	
	public boolean implementsLifecycle() {
		return implementsLifecycle;
	}
	
	public boolean implementsValidatable() {
		return implementsValidatable;
	}
	
	public boolean hasCollections() {
		return hasCollections;
	}
	
	public boolean isMutable() {
		return mutable;
	}
	
	public boolean hasCache() {
		return cache!=null;
	}
	
	public boolean hasSubclasses() {
		return hasSubclasses;
	}
	
	public boolean hasProxy() {
		return hasProxy;
	}
	
	/**
	 * The query that returns the generated identifier for an identity column
	 */
	protected final String sqlIdentitySelect() {
		return identitySelectString;
	}
	
	public IdentifierGenerator getIdentifierGenerator() throws HibernateException {
		return identifierGenerator;
	}
	
	protected void check(int rows, Serializable id) throws HibernateException {
		if (rows<1) {
			throw new StaleObjectStateException( getMappedClass(), id );
		}
		else if (rows>1) {
			throw new HibernateException( "Duplicate identifier in table for "  + getClassName() + ": " + id );
		}
	}
	
	protected abstract String[] getActualPropertyColumnNames(int i);
	protected abstract String getFormulaTemplate(int i);
	
	protected void initPropertyPaths(SessionFactoryImplementor factory) throws MappingException {
		
		for ( int i=0; i<propertyNames.length; i++ ) {
			initPropertyPaths( propertyNames[i], propertyTypes[i], getActualPropertyColumnNames(i), getFormulaTemplate(i), factory );
		}
		
		String idProp = getIdentifierPropertyName();
		if (idProp!=null) initPropertyPaths( idProp, getIdentifierType(), getIdentifierColumnNames(), factory );
		if ( hasEmbeddedIdentifier() ) initPropertyPaths( null, getIdentifierType(), getIdentifierColumnNames(), factory );
		initPropertyPaths( ENTITY_ID, getIdentifierType(), getIdentifierColumnNames(), factory );
		
		if ( isPolymorphic() ) {
			addPropertyPath( 
				ENTITY_CLASS, 
				getDiscriminatorType(), 
				new String[] { getDiscriminatorColumnName() } 
			);
		}
	}
	
	protected AbstractEntityPersister(PersistentClass model, SessionFactoryImplementor factory) throws HibernateException {
		
		this.dialect = factory.getDialect();
		this.sqlExceptionConverter = factory.getSQLExceptionConverter();

		// CLASS
		
		className = model.getMappedClass().getName();
		rootClassName = model.getRootClass().getName();
		mappedClass = model.getMappedClass();
		
		mutable = model.isMutable();
		selectBeforeUpdate = model.hasSelectBeforeUpdate();
		dynamicUpdate = model.useDynamicUpdate();
		dynamicInsert = model.useDynamicInsert();
		sqlWhereString = model.getWhere();
		sqlWhereStringTemplate = sqlWhereString==null ? 
			null : 
			Template.renderWhereStringTemplate(sqlWhereString, dialect);
		
		polymorphic = model.isPolymorphic();
		explicitPolymorphism = model.isExplicitPolymorphism();
		inherited = model.isInherited();
		superclass = inherited ? model.getSuperclass().getMappedClass() : null;
		hasSubclasses = model.hasSubclasses();
		
		batchSize = model.getBatchSize();
		
		constructor = ReflectHelper.getDefaultConstructor(mappedClass);
		abstractClass = ReflectHelper.isAbstractClass(mappedClass);
		
		entityType = Hibernate.entity(mappedClass);
		
		optimisticLockMode = model.getOptimisticLockMode();
		if (optimisticLockMode > Versioning.OPTIMISTIC_LOCK_VERSION && !dynamicUpdate) {
			throw new MappingException("optimistic-lock setting requires dynamic-update=\"true\": " + className);
		}
				
		// IDENTIFIER
		
		hasEmbeddedIdentifier = model.hasEmbeddedIdentifier();
		Value idValue = model.getIdentifier();
		identifierType = idValue.getType();
		//PropertyAccessor pa = null;
		if ( model.hasIdentifierProperty() ) {
			Property idProperty = model.getIdentifierProperty();
			identifierPropertyName = idProperty.getName();
			identifierSetter = idProperty.getSetter(mappedClass);
			identifierGetter = idProperty.getGetter(mappedClass);
		}
		else {
			identifierPropertyName = null;
			identifierGetter = null;
			identifierSetter = null;
		}
		
		Class prox = model.getProxyInterface();
		Method proxyGetIdentifierMethod = null;
		Method proxySetIdentifierMethod = null;
		if ( model.hasIdentifierProperty() && prox!=null ) {
			Property idProperty = model.getIdentifierProperty();
			try {
				proxyGetIdentifierMethod = idProperty.getGetter(prox).getMethod();
			}
			catch (PropertyNotFoundException pnfe) {}
			
			
			try {
				proxySetIdentifierMethod = idProperty.getSetter(prox).getMethod();
			}
			catch (PropertyNotFoundException pnfe) {}
			
		}
		
		// HYDRATE SPAN
		
		int m=0;
		Iterator iter = model.getPropertyClosureIterator();
		while ( iter.hasNext() ) {
			m++; iter.next();
		}
		hydrateSpan=m;
		
		
		// IDENTIFIER
		
		int idColumnSpan = model.getIdentifier().getColumnSpan();
		identifierColumnNames = new String[idColumnSpan];
		identifierAliases = new String[idColumnSpan];
		
		iter = idValue.getColumnIterator();
		int i=0;
		while ( iter.hasNext() ) {
			Column col = (Column) iter.next();
			identifierColumnNames[i] = col.getQuotedName(dialect);
			identifierAliases[i] = col.getAlias(); // getAlias() handles quotes
			i++;
		}
				
		// GENERATOR
		
		identifierGenerator = model.getIdentifier().createIdentifierGenerator(dialect);
		useIdentityColumn = identifierGenerator instanceof IdentityGenerator;
		identitySelectString = useIdentityColumn ? dialect.getIdentitySelectString() : null;
		
		
		// UNSAVED-VALUE:
		
		String unsavedValue = model.getIdentifier().getNullValue();
		if ( unsavedValue==null || "null".equals(unsavedValue) ) {
			unsavedIdentifierValue=Cascades.SAVE_NULL;
		}
		else if ( "none".equals(unsavedValue) ) {
			unsavedIdentifierValue=Cascades.SAVE_NONE;
		}
		else if ( "any".equals(unsavedValue) ) {
			unsavedIdentifierValue=Cascades.SAVE_ANY;
		}
		else {
			Type idType = model.getIdentifier().getType();
			try {
				unsavedIdentifierValue = new Cascades.IdentifierValue( 
					( (IdentifierType) idType ).stringToObject(unsavedValue) 
				);
			}
			catch (ClassCastException cce) {
				throw new MappingException("Bad identifier type: " + idType.getClass().getName() );
			}
			catch (Exception e) {
				throw new MappingException("Could not parse identifier unsaved-value: " + unsavedValue);
			}
		}
		
		// VERSION:
		
		if ( model.isVersioned() ) {
			versionColumnName = ( (Column) model.getVersion().getColumnIterator().next() ).getQuotedName(dialect);
		}
		else {
			versionColumnName = null;
		}
		
		if ( model.isVersioned() ) {
			//versionPropertyName = model.getVersion().getName();
			versioned = true;
			versionGetter = model.getVersion().getGetter(mappedClass);
			versionType = (VersionType) model.getVersion().getType();
		}
		else {
			//versionPropertyName = null;
			versioned = false;
			versionType = null;
			versionGetter = null;
		}

		jdbcBatchVersionedData = factory.isJdbcBatchVersionedData();

		// VERSION UNSAVED-VALUE:
		String versionUnsavedValue = null;
		if ( model.isVersioned() ) {
			versionUnsavedValue = model.getVersion().getNullValue();
		}
		
		if ( versionUnsavedValue==null || "undefined".equals(versionUnsavedValue) ) {
			unsavedVersionValue = Cascades.VERSION_UNDEFINED;
		} 
		else if ( "null".equals(versionUnsavedValue) ) {
			unsavedVersionValue=Cascades.VERSION_SAVE_NULL;
		} 
		else if ( "negative".equals(versionUnsavedValue) ) {
			unsavedVersionValue=Cascades.VERSION_NEGATIVE;
			/*
			 * used to be none and any strategies but this is kind of a non sense for version
			 * especially for none since an 'update where' would be generated
			 * Lot's of hack to support none ?
			 */
		}
		else {
			// this should not happend since the DTD prevent it.
			throw new MappingException("Could not parse version unsaved-value: " + versionUnsavedValue);
		}
		
		// PROPERTIES
		
		propertyTypes = new Type[hydrateSpan];
		propertyNames = new String[hydrateSpan];
		propertyUpdateability = new boolean[hydrateSpan];
		propertyInsertability = new boolean[hydrateSpan];
		propertyNullability = new boolean[hydrateSpan];
		getters = new Getter[hydrateSpan];
		setters = new Setter[hydrateSpan];
		cascadeStyles = new Cascades.CascadeStyle[hydrateSpan];
		String[] setterNames = new String[hydrateSpan];
		String[] getterNames = new String[hydrateSpan];
		Class[] types = new Class[hydrateSpan];
		
		iter = model.getPropertyClosureIterator();
		i=0;
		int tempVersionProperty=-66;
		boolean foundCascade = false;
		boolean foundCustomAccessor = false;
		while( iter.hasNext() ) {
			Property prop = (Property) iter.next();
			if ( prop==model.getVersion() ) tempVersionProperty = i;
			propertyNames[i] = prop.getName();
			if ( !( prop.isBasicPropertyAccessor() ) ) foundCustomAccessor=true;
			getters[i] = prop.getGetter(mappedClass);
			setters[i] = prop.getSetter(mappedClass);
			getterNames[i]= getters[i].getMethodName();
			setterNames[i]= setters[i].getMethodName();
			types[i] = getters[i].getReturnType();
			propertyTypes[i] = prop.getType();
			propertyUpdateability[i] = prop.isUpdateable();
			propertyInsertability[i] = prop.isInsertable();
			propertyNullability[i] = prop.isNullable();
			
			gettersByPropertyName.put( propertyNames[i], getters[i] );
			settersByPropertyName.put( propertyNames[i], setters[i] );
			typesByPropertyName.put( propertyNames[i], propertyTypes[i] );
			
			cascadeStyles[i] = prop.getCascadeStyle();
			if ( cascadeStyles[i]!=Cascades.STYLE_NONE ) foundCascade = true;
			
			i++;
		}
		
		fastClass = ReflectHelper.getFastClass(mappedClass);
		optimizer = !foundCustomAccessor && Environment.useReflectionOptimizer() ?
			ReflectHelper.getBulkBean(mappedClass, getterNames, setterNames, types, fastClass) :
			null;
		
		hasCascades = foundCascade;
		versionProperty = tempVersionProperty;
		
		// CALLBACK INTERFACES
		implementsLifecycle = Lifecycle.class.isAssignableFrom(mappedClass);
		implementsValidatable = Validatable.class.isAssignableFrom(mappedClass);
		
		cache = model.getCache();
		
		hasCollections = initHasCollections();
		
		
		// PROXIES
		concreteProxyClass = model.getProxyInterface();
		hasProxy = concreteProxyClass!=null;
		if (hasProxy) {
			HashSet proxyInterfaces = new HashSet();
			proxyInterfaces.add(HibernateProxy.class);
			if ( !mappedClass.equals(concreteProxyClass) ) {
				if ( !concreteProxyClass.isInterface() ) throw new MappingException( 
					"proxy must be either an interface, or the class itself: " + 
					mappedClass.getName() 
				);
				proxyInterfaces.add(concreteProxyClass);
			}
			if ( mappedClass.isInterface() ) proxyInterfaces.add(mappedClass);
			
			
			if (hasProxy) {
				iter = model.getSubclassIterator();
				while ( iter.hasNext() ) {
					Subclass subclass = (Subclass) iter.next();
					Class subclassProxy = subclass.getProxyInterface();
					if (subclassProxy==null) throw new MappingException( 
						"All subclasses must also have proxies: " + 
						mappedClass.getName() 
					);
					if ( !subclass.getMappedClass().equals(subclassProxy) ) proxyInterfaces.add(subclassProxy);
				}
			}
			
			if (hasProxy) {
				proxyFactory = createProxyFactory();
				proxyFactory.postInstantiate(mappedClass, proxyInterfaces, proxyGetIdentifierMethod, proxySetIdentifierMethod);
			}
			else {
				proxyFactory = null;
			}
		
		}
		else {
			proxyFactory = null;
		}

	}
	
	/**
	 * Create a new ProxyFactory. Returns a CGLIBProxyFactory by default,
	 * may be overridden by a subclass.
	 */
	protected ProxyFactory createProxyFactory(){
		return new CGLIBProxyFactory();
	}
	
	/**
     * Must be called by subclasses, at the end of their constructors
     */
	protected void initSubclassPropertyAliasesMap(PersistentClass model) throws MappingException {

		// ALIASES
		internalInitSubclassPropertyAliasesMap(null, model.getSubclassPropertyClosureIterator());
		
		// aliases for identifier
		if ( hasIdentifierProperty() ) {
			subclassPropertyAliases.put( getIdentifierPropertyName(), getIdentifierAliases() );
			subclassPropertyAliases.put( ENTITY_ID, getIdentifierAliases() );
		}

		if ( hasEmbeddedIdentifier() ) {
			// Fetch embedded identifiers propertynames from the "virtual" identifier component
			AbstractComponentType componentId = ( AbstractComponentType ) getIdentifierType();
			String[] idPropertyNames = componentId.getPropertyNames();
			String[] idAliases = getIdentifierAliases();

			for ( int i = 0; i < idPropertyNames.length; i++ ) {
				subclassPropertyAliases.put( idPropertyNames[i], new String[]{idAliases[i]} );
			}
		}

		if ( isPolymorphic() ) {
			subclassPropertyAliases.put( ENTITY_CLASS,
					new String[]{getDiscriminatorAlias()} );
		}

	}

	private void internalInitSubclassPropertyAliasesMap(String path, Iterator iter) {
	    while ( iter.hasNext() ) {

			Property prop = ( Property ) iter.next();
			String propname = path==null?prop.getName():path + "." + prop.getName();
			if(prop.isComposite()) {
			    Component component = (Component) prop.getValue();
			    Iterator compProps = component.getPropertyIterator();
			    internalInitSubclassPropertyAliasesMap(propname, compProps);
			} else {			  
			    String[] aliases = new String[prop.getColumnSpan()];
			    String[] cols = new String[prop.getColumnSpan()];
			    Iterator colIter = prop.getColumnIterator();
			    int l = 0;
			    while ( colIter.hasNext() ) {
			        Column thing = ( Column ) colIter.next();
			        aliases[l] = thing.getAlias();
			        cols[l] = thing.getQuotedName(getDialect());
			        l++;
			    }
			    
                //	used for loading entities by a unique key:
				if ( prop.getValue().isUnique() ) {
					uniqueKeyColumns.put( propname, cols );
				}
			    
			    subclassPropertyAliases.put( propname, aliases );
			}
		}

    }
	
	protected void initLockers() {
		String lockString = generateLockString();
		lockers.put(LockMode.READ, lockString);
		String lockExclusiveString = getDialect().supportsForUpdate() ? 
			lockString + " for update" : 
			lockString;
		lockers.put(LockMode.UPGRADE, lockExclusiveString);
		String lockExclusiveNowaitString = getDialect().supportsForUpdateNowait() ? 
			lockString + " for update nowait" : 
			lockExclusiveString;
		lockers.put(LockMode.UPGRADE_NOWAIT, lockExclusiveNowaitString);
	}
	
	protected abstract String generateLockString();

	private boolean initHasCollections() {
		return initHasCollections(propertyTypes);
	}
	
	private boolean initHasCollections(Type[] types) {
		for ( int i=0; i<types.length; i++ ) {
			if ( types[i].isPersistentCollectionType() ) {
				return true;
			}
			else if ( types[i].isComponentType() ) {
				if ( initHasCollections(
					( (AbstractComponentType) types[i] ).getSubtypes()
				) ) return true;
			}
		}
		return false;
	}
	
	public ClassMetadata getClassMetadata() {
		return this;
	}
	
	public Class getConcreteProxyClass() {
		return concreteProxyClass;
	}
		
	public Class getMappedSuperclass() {
		return superclass;
	}
	
	public boolean isExplicitPolymorphism() {
		return explicitPolymorphism;
	}
	
	public boolean[] getPropertyUpdateability() {
		return propertyUpdateability;
	}
	
	public boolean[] getPropertyNullability() {
		return propertyNullability;
	}

	protected boolean useDynamicUpdate() {
		return dynamicUpdate;
	}

	protected boolean useDynamicInsert() {
		return dynamicInsert;
	}

	public boolean[] getPropertyInsertability() {
		return propertyInsertability;
	}
	
	public Object getPropertyValue(Object object, String propertyName)
		throws HibernateException {
		
		Getter getter = (Getter) gettersByPropertyName.get(propertyName);
		if (getter==null) throw new HibernateException("unmapped property: " + propertyName);
		return getter.get(object);
	}

	public void setPropertyValue(Object object, String propertyName, Object value)
		throws HibernateException {
		
		Setter setter = (Setter) settersByPropertyName.get(propertyName);
		if (setter==null) throw new HibernateException("unmapped property: " + propertyName);
		setter.set(object, value);
	}
	
	protected boolean hasEmbeddedIdentifier() {
		return hasEmbeddedIdentifier;
	}

	public boolean[] getNotNullInsertableColumns(Object[] fields) {
		boolean[] notNull = new boolean[fields.length];
		boolean[] insertable = getPropertyInsertability();
		for ( int i=0; i<fields.length; i++ ) notNull[i] = insertable[i] && fields[i]!=null;
		return notNull;
	}

	protected Dialect getDialect() {
		return dialect;
	}
	protected String getSQLWhereString(String alias) {
		return StringHelper.replace(sqlWhereStringTemplate, Template.TEMPLATE, alias);
	}
	protected boolean hasWhere() {
		return sqlWhereString!=null;
	}

	public boolean hasIdentifierPropertyOrEmbeddedCompositeIdentifier() {
		return hasIdentifierProperty() || hasEmbeddedIdentifier;
	}

	protected void checkColumnDuplication(Set distinctColumns, Iterator columns) throws MappingException {
		while ( columns.hasNext() ) {
			Column col = (Column) columns.next();
			if ( !distinctColumns.add( col.getName() ) ) throw new MappingException(
				"Repeated column in mapping for class " +
				className +
				" should be mapped with insert=\"false\" update=\"false\": " + 
				col.getName()
			);
		}
	}
	
	protected UniqueEntityLoader createEntityLoader(SessionFactoryImplementor factory) throws MappingException {
		Loader nonBatchLoader = new EntityLoader(this, 1, factory);
		if (batchSize>1) {
			Loader batchLoader = new EntityLoader(this, batchSize, factory);
			int smallBatchSize = (int) Math.round( Math.sqrt(batchSize) );
			Loader smallBatchLoader = new EntityLoader(this, smallBatchSize, factory);
			return new BatchingEntityLoader(this, batchSize, batchLoader, smallBatchSize, smallBatchLoader, nonBatchLoader);
		}
		else {
			return (UniqueEntityLoader) nonBatchLoader;
		}
	}
	
	protected void createUniqueKeyLoaders(SessionFactoryImplementor factory) throws MappingException {
		//TODO: does not handle components, or properties of a joined subclass
		for ( int i=0; i<propertyNames.length; i++ ) {
			String[] columns = (String[]) uniqueKeyColumns.get( propertyNames[i] );
			if (columns!=null) {
				Type uniqueKeyType = propertyTypes[i];
				if ( uniqueKeyType.isEntityType() ) {
					Class clazz = ( (EntityType) uniqueKeyType ).getAssociatedClass();
					uniqueKeyType = factory.getPersister(clazz).getIdentifierType();
				}
				uniqueKeyLoaders.put( 
					propertyNames[i], 
					new EntityLoader(this, columns, uniqueKeyType, 1, factory) 
				);
			}
		}
	}
	
	public Type getType() {
		return entityType;
	}

	protected int getHydrateSpan() {
		return hydrateSpan;
	}

	public boolean isBatchLoadable() {
		return batchSize>1;
	}

	public String[] getSubclassPropertyColumnAliases(String propertyName, String suffix) {
		String rawAliases[] = (String[]) subclassPropertyAliases.get(propertyName);
		 
		if(rawAliases==null) return null;
		
		String result[] = new String[rawAliases.length];
		for ( int i=0; i<rawAliases.length; i++ ) {
			result[i] = new Alias(suffix).toUnquotedAliasString( rawAliases[i] );
		}
		return result;
	}

	public String[] getJoinKeyColumnNames() {
		return getIdentifierColumnNames();
	}

	public String getName() {
		return getClassName();
	}

	public String selectFragment(String alias, String suffix) {
		return identifierSelectFragment(alias, suffix) + propertySelectFragment(alias, suffix);
	}

	public String[] getIdentifierAliases(String suffix) {
		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
		// was toUnqotedAliasStrings( getIdentiferColumnNames() ) before - now tried
		// to remove that unqoting and missing aliases..
		return new Alias(suffix).toAliasStrings( getIdentifierAliases() );
	}

	public String[] getPropertyAliases(String suffix, int i) {
		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
		return new Alias(suffix).toUnquotedAliasStrings( getPropertyColumnNames(i) );
	}

	public String getDiscriminatorAlias(String suffix) {	
		// NOTE: this assumes something about how propertySelectFragment is implemented by the subclass!
		// was toUnqotedAliasStrings( getdiscriminatorColumnName() ) before - now tried
		// to remove that unqoting and missing aliases..		
		return hasSubclasses() ?
			new Alias(suffix).toAliasString( getDiscriminatorAlias() ) :
			null;
	}

	protected abstract String getDiscriminatorAlias();

	public Object loadByUniqueKey(String propertyName, Serializable uniqueKey, SessionImplementor session)
	throws HibernateException, SQLException {	
		return ( (EntityLoader) uniqueKeyLoaders.get(propertyName) ).loadByUniqueKey(session, uniqueKey);
	}

	public String[] getUniqueKeyColumnNames(String propertyName) {
		return (String[]) uniqueKeyColumns.get(propertyName);
	}

	public boolean isCollection() {
		return false;
	}

	public boolean consumesAlias() {
		return true;
	}

	public Type getPropertyType(String propertyName) throws MappingException {
		Type propertyType = (Type) typesByPropertyName.get(propertyName);
		if (propertyType==null) throw new MappingException("property does not exist: " + propertyName);
		return propertyType;
	}

	protected boolean hasSelectBeforeUpdate() {
		return selectBeforeUpdate;
	}
	
	protected abstract String getVersionSelectString();

	/**
	 * Retrieve the version number
	 */
	public Object getCurrentVersion(Serializable id, SessionImplementor session) throws HibernateException {
		
		if ( log.isTraceEnabled() ) {
			log.trace( "Getting version: " +  MessageHelper.infoString(this, id) );
		}
		
		try {
			
			PreparedStatement st = session.getBatcher().prepareStatement( getVersionSelectString() );
			try {
				getIdentifierType().nullSafeSet(st, id, 1, session);
				
				ResultSet rs = st.executeQuery();
				try {
					if ( !rs.next() ) return null;
					if ( !isVersioned() ) return this;
					return getVersionType().nullSafeGet(rs, getVersionColumnName(), session, null);
				}
				finally {
					rs.close();
				}
			}
			finally {
				session.getBatcher().closeStatement(st);
			}
			
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not retrieve version: " + MessageHelper.infoString(this, id) );
		}
		
	}
	
	/**
	 * Do a version check
	 */
	public void lock(Serializable id, Object version, Object object, LockMode lockMode, SessionImplementor session) throws HibernateException {
		
		if ( lockMode!=LockMode.NONE ) {
			
			if ( log.isTraceEnabled() ) {
				log.trace( "Locking entity: " +  MessageHelper.infoString(this, id) );
				if ( isVersioned() ) log.trace("Version: " + version);
			}
			
			try {
				
				PreparedStatement st = session.getBatcher().prepareStatement( getLockString(lockMode) );
				try {
					getIdentifierType().nullSafeSet(st, id, 1, session);
					if ( isVersioned() ) {
						getVersionType().nullSafeSet(st, version, getIdentifierColumnNames().length+1, session);
					}
					
					ResultSet rs = st.executeQuery();
					try {
						if ( !rs.next() ) throw new StaleObjectStateException( getMappedClass(), id );
					}
					finally {
						rs.close();
					}
				}
				finally {
					session.getBatcher().closeStatement(st);
				}
				
			}
			catch (SQLException sqle) {
				throw convert( sqle, "could not lock: " + MessageHelper.infoString(this, id) );
			}
			
		}
		
	}

	/**
	 * Get the generated identifier when using identity columns
	 */
	protected Serializable getGeneratedIdentity(Object object, SessionImplementor session, ResultSet rs)
	throws SQLException, HibernateException, IdentifierGenerationException {
		final Serializable id;
		try {
			if ( !rs.next() ) throw new HibernateException("The database returned no natively generated identity value");
			id = IdentifierGeneratorFactory.get( rs, getIdentifierType(), session, object );
		}
		finally {
			rs.close();
		}
		if ( log.isDebugEnabled() ) log.debug("Natively generated identity: " + id);
		return id;
	}
	
	public Object[] getCurrentPersistentState(Serializable id, Object version, SessionImplementor session) 
	throws HibernateException {
		
		if ( !hasSelectBeforeUpdate() ) return null;
		
		if ( log.isTraceEnabled() ) log.trace( "Getting current persistent state for: " + MessageHelper.infoString(this, id) );

		Type[] types = getPropertyTypes();
		Object[] values = new Object[ types.length ];
		boolean[] includeProperty = getPropertyUpdateability();
		try {
			PreparedStatement ps = session.getBatcher().prepareQueryStatement( getConcreteSelectString(), false, null );
			ResultSet rs = null;
			try {
				getIdentifierType().nullSafeSet(ps, id, 1, session);
				if ( isVersioned() ) getVersionType().nullSafeSet( ps, version, getIdentifierColumnNames().length+1, session );
				rs = session.getBatcher().getResultSet(ps);
				if ( !rs.next() ) throw new StaleObjectStateException( getMappedClass(), id );
				for (int i=0; i<types.length; i++) {
					if ( includeProperty[i] ) {
						values[i] = types[i].hydrate( rs, getPropertyAliases(StringHelper.EMPTY_STRING, i), session, null ); //null owner ok??
					}
				}
			}
			finally {
				session.getBatcher().closeQueryStatement(ps, rs);				
			}
		}
		catch (SQLException sqle) {
			throw convert( sqle, "error retrieving current persistent state" );
		}
		
		return values;
	}
	
	protected abstract String getVersionedTableName();
	
	/**
	 * Generate the SQL that selects the version number by id
	 */
	protected String generateSelectVersionString() {
		SimpleSelect select = new SimpleSelect()
			.setTableName( getVersionedTableName() );
		if ( isVersioned() ) {
			select.addColumn(versionColumnName);
		}
		else {
			select.addColumns(identifierColumnNames);
		}
		return select.addCondition(identifierColumnNames, "=?" ).toStatementString();
	}
	
	protected abstract String getConcreteSelectString();
	
	protected final int optimisticLockMode() {
		return optimisticLockMode;
	}

	public boolean isManyToMany() {
		return false;
	}

	public Object createProxy(Serializable id, SessionImplementor session) 
	throws HibernateException {
		return proxyFactory.getProxy(id, session);
	}

	/**
	 * Transform the array of property indexes to an array of booleans
	 */
	protected final boolean[] getPropertiesToUpdate(final int[] dirtyProperties) {
		final boolean[] propsToUpdate = new boolean[ getHydrateSpan() ];
		for (int j=0; j<dirtyProperties.length; j++) {
			propsToUpdate[ dirtyProperties[j] ] = true;
		}
		if ( isVersioned() ) propsToUpdate[ getVersionProperty() ] = true;
		return propsToUpdate;
	}

	public String toString() {
		return StringHelper.root( getClass().getName() ) + '(' + className + ')';
	}
	
	/**
	 * Get the column names for the numbered property of <em>this</em> class
	 */
	protected abstract String[] getPropertyColumnNames(int i);

	public final String selectFragment(String alias, String suffix, boolean includeCollectionColumns) {
		return selectFragment(alias, suffix);
	}

	protected JDBCException convert( SQLException sqlException, String message ) {
		return JDBCExceptionHelper.convert( sqlExceptionConverter, sqlException, message );
	}
}
