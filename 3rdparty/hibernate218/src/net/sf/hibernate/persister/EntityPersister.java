//$Id: EntityPersister.java,v 1.51 2004/12/24 03:06:25 oneovthafew Exp $
package net.sf.hibernate.persister;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.engine.Versioning;
import net.sf.hibernate.impl.MessageHelper;
import net.sf.hibernate.loader.SimpleEntityLoader;
import net.sf.hibernate.loader.UniqueEntityLoader;
import net.sf.hibernate.mapping.Column;
import net.sf.hibernate.mapping.PersistentClass;
import net.sf.hibernate.mapping.Property;
import net.sf.hibernate.mapping.Subclass;
import net.sf.hibernate.mapping.Table;
import net.sf.hibernate.mapping.Value;
import net.sf.hibernate.sql.Delete;
import net.sf.hibernate.sql.InFragment;
import net.sf.hibernate.sql.Insert;
import net.sf.hibernate.sql.SelectFragment;
import net.sf.hibernate.sql.SimpleSelect;
import net.sf.hibernate.sql.Update;
import net.sf.hibernate.type.DiscriminatorType;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.ArrayHelper;
import net.sf.hibernate.util.GetGeneratedKeysHelper;
import net.sf.hibernate.util.StringHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The default implementation of the <tt>ClassPersister</tt> interface.
 * Implements the "table-per-class-hierarchy" mapping strategy for an entity
 * class.
 *
 * @author Gavin King
 */
public class EntityPersister extends AbstractEntityPersister implements Queryable {
	
	private final SessionFactoryImplementor factory;
	
	// the class hierarchy structure
	private final String qualifiedTableName;
	private final String[] tableNames;
	private final boolean hasUpdateableColumns;
	private final Class[] subclassClosure;
	private final boolean hasFormulaProperties;
	
	// SQL strings
	private final String sqlDeleteString;
	private final String sqlInsertString;
	private final String sqlUpdateString;
	private final String sqlIdentityInsertString;
	private final String sqlConcreteSelectString;
	private final String sqlVersionSelectString;
	
	// properties of this class, including inherited properties
	private final int[] propertyColumnSpans;
	private final boolean[] propertyDefinedOnSubclass;
	private final String[][] propertyColumnNames;
	private final String[][] propertyColumnAliases;
	private final String[] propertyFormulaTemplates;
	
	// the closure of all columns used by the entire hierarchy including
	// subclasses and superclasses of this class
	private final String[] subclassColumnClosure;
	private final String[] subclassColumnAliasClosure;
	private final String[] subclassFormulaTemplateClosure;
	private final String[] subclassFormulaClosure;
	private final String[] subclassFormulaAliasClosure;

	// the closure of all properties in the entire hierarchy including
	// subclasses and superclasses of this class
	private final String[][] subclassPropertyColumnNameClosure;
	private final String[] subclassPropertyNameClosure;
	private final Type[] subclassPropertyTypeClosure;
	private final int[] subclassPropertyEnableJoinedFetch;
	
	// discriminator column
	private final HashMap subclassesByDiscriminatorValue = new HashMap();
	private final boolean forceDiscriminator;	
	private final String discriminatorColumnName;
	private final String discriminatorAlias;
	private final Type discriminatorType;
	private final Object discriminatorSQLValue;
	private final boolean discriminatorInsertable;	
	
	private final Map loaders = new HashMap();
	
	private static final Object NULL_DISCRIMINATOR = new Object();
	private static final Object NOT_NULL_DISCRIMINATOR = new Object();
	
	private static final Log log = LogFactory.getLog(EntityPersister.class);
	
	public void postInstantiate() throws MappingException {
		
		initPropertyPaths(factory);
		
		UniqueEntityLoader loader = createEntityLoader(factory);
		
		loaders.put( LockMode.NONE, loader );
		loaders.put( LockMode.READ, loader );
		
		String selectForUpdate = factory.getDialect().supportsForUpdate() ?
			generateSelectForUpdateString() :
			generateSelectString();
		
		loaders.put(
			LockMode.UPGRADE,
			new SimpleEntityLoader( this, selectForUpdate, LockMode.UPGRADE )
		);
		
		String selectForUpdateNowait = factory.getDialect().supportsForUpdateNowait() ?
		generateSelectForUpdateNowaitString() :
		selectForUpdate;
		
		loaders.put(
			LockMode.UPGRADE_NOWAIT,
			new SimpleEntityLoader( this, selectForUpdateNowait, LockMode.UPGRADE_NOWAIT )
		);
		
		createUniqueKeyLoaders(factory);
		
	}
	
	public boolean isDefinedOnSubclass(int i) {
		return propertyDefinedOnSubclass[i];
	}
	
	public String getDiscriminatorColumnName() {
		return discriminatorColumnName;
	}
	
	public String getDiscriminatorAlias() {
		return discriminatorAlias;
	}
	
	public int enableJoinedFetch(int i) {
		return subclassPropertyEnableJoinedFetch[i];
	}
	
	public Type getSubclassPropertyType(int i) {
		return subclassPropertyTypeClosure[i];
	}
	public String getSubclassPropertyName(int i) {
		return subclassPropertyNameClosure[i];
	}
	public int countSubclassProperties() {
		return subclassPropertyTypeClosure.length;
	}
	
	public String getTableName() {
		return qualifiedTableName;
	}
	
	public String[] getSubclassPropertyColumnNames(int i) {
		return subclassPropertyColumnNameClosure[i];
	}
	
	public String[] getPropertyColumnNames(int i) {
		return propertyColumnAliases[i];
	}
	
	public Type getDiscriminatorType() {
		return discriminatorType;
	}
	
	public Object getDiscriminatorSQLValue() {
		return discriminatorSQLValue;
	}
	
	public Class[] getSubclassClosure() {
		return subclassClosure;
	}
	
	public Class getSubclassForDiscriminatorValue(Object value) {
		if (value==null) {
			return (Class) subclassesByDiscriminatorValue.get(NULL_DISCRIMINATOR);
		}
		else {
			Class result = (Class) subclassesByDiscriminatorValue.get(value);
			if (result==null) result = (Class) subclassesByDiscriminatorValue.get(NOT_NULL_DISCRIMINATOR);
			return result;
		}
	}
	
	public Serializable[] getPropertySpaces() {
		return tableNames;
	}
	
	//Access cached SQL
	
	/**
	 * The query that deletes a row by id (and version)
	 */
	protected final String getSQLDeleteString() {
		return sqlDeleteString;
	}
	
	/**
	 * The query that inserts a row with a given id
	 */
	protected final String getSQLInsertString() {
		return sqlInsertString;
	}
	
	/**
	 * The query that inserts a row, letting the database generate an id
	 */
	protected final String getSQLIdentityInsertString() {
		return sqlIdentityInsertString;
	}
	
	/**
	 * The query that updates a row by id (and version)
	 */
	protected final String getSQLUpdateString() {
		return sqlUpdateString;
	}
	
	protected final String getVersionSelectString() {
		return sqlVersionSelectString;
	}
	
	// Generate all the SQL
	
	/**
	 * Generate the SQL that deletes a row by id (and version)
	 */
	protected String generateDeleteString() {
		return new Delete()
			.setTableName( getTableName() )
			.setPrimaryKeyColumnNames( getIdentifierColumnNames() )
			.setVersionColumnName( getVersionColumnName() )
			.toStatementString();
	}
	
	/**
	 * Generate the SQL that inserts a row
	 */
	protected String generateInsertString(boolean identityInsert, boolean[] includeProperty) {
		Insert insert = new Insert( getDialect() )
			.setTableName( getTableName() );
		for (int i=0; i<getHydrateSpan(); i++) {
			if ( includeProperty[i] ) insert.addColumns( propertyColumnNames[i] );
		}
		if (discriminatorInsertable) {
			insert.addColumn( getDiscriminatorColumnName(), discriminatorSQLValue.toString() );
		}
		if (!identityInsert) {
			insert.addColumns( getIdentifierColumnNames() );
		}
		else {
			insert.addIdentityColumn( getIdentifierColumnNames()[0] );
		}
		return insert.toStatementString();
	}
	
	/**
	 * Generate the SQL that selects a row by id using <tt>FOR UPDATE</tt>
	 */
	protected String generateSelectForUpdateString() {
		return generateSelectString() + " for update";
	}
	
	/**
	 * Generate the SQL that selects a row by id using <tt>FOR UPDATE</tt>
	 */
	protected String generateSelectForUpdateNowaitString() {
		return generateSelectString() + " for update nowait";
	}
	
	/**
	 * Generate the SQL that selects a row by id
	 */
	protected String generateSelectString() {
		SimpleSelect select = new SimpleSelect()
			.setTableName( getTableName() )
			.addColumns( getIdentifierColumnNames() )
			.addColumns(subclassColumnClosure, subclassColumnAliasClosure)
			.addColumns(subclassFormulaClosure, subclassFormulaAliasClosure);
		if ( hasSubclasses() ) select.addColumn( getDiscriminatorColumnName() , getDiscriminatorAlias() );
		return select.addCondition( getIdentifierColumnNames(), "=?" ).toStatementString();
	}
	
	/**
	 * Generate the SQL that selects a row by id, excluding subclasses
	 */
	protected String generateConcreteSelectString(boolean[] includeProperty) {
		SimpleSelect select = new SimpleSelect()
			.setTableName( getTableName() )
			.addColumns( getIdentifierColumnNames() );
		for ( int i=0; i<getPropertyNames().length; i++ ) {
			if ( includeProperty[i] ) { //ie. not a formula, updateable
				select.addColumns( propertyColumnNames[i], propertyColumnAliases[i] );
			}
			//don't handle formulas, cos they are not updateable
		}
		select.addCondition( getIdentifierColumnNames(), "=?" );
		if ( isVersioned() ) {
			select.addWhereToken("and")
				.addCondition( getVersionColumnName(), "=?" );
		}
		return select.toStatementString();
	}
	
	/**
	 * Generate the SQL that updates a row by id (and version)
	 */
	protected String generateUpdateString(boolean[] includeProperty) {
		return generateUpdate(includeProperty).toStatementString();
	}
	protected String generateUpdateString(boolean[] includeProperty, Object[] oldFields) {
		Update update = generateUpdate(includeProperty);
		if ( optimisticLockMode()>Versioning.OPTIMISTIC_LOCK_VERSION && oldFields!=null ) {
			boolean[] includeInWhere = optimisticLockMode()==Versioning.OPTIMISTIC_LOCK_ALL ?
				getPropertyUpdateability() :
				includeProperty;
			for ( int i=0; i<getHydrateSpan(); i++ ) {
				if ( includeInWhere[i] ) {
					if ( oldFields[i]==null) {
						update.addWhereColumns( propertyColumnNames[i], " is null");
					}
					else {
						update.addWhereColumns( propertyColumnNames[i] );
					}
				} 
			}
		}
			
		return update.toStatementString();
	}
	
	private Update generateUpdate(boolean[] includeProperty) {
		Update update = new Update()
			.setTableName( getTableName() )
			//.addColumns( getColumnNames() )
			.setPrimaryKeyColumnNames( getIdentifierColumnNames() );
		for ( int i=0; i<getHydrateSpan(); i++ ) {
			if ( includeProperty[i] ) update.addColumns( propertyColumnNames[i] );
		}
		if ( optimisticLockMode()==Versioning.OPTIMISTIC_LOCK_VERSION ) {
			update.setVersionColumnName( getVersionColumnName() );
		}
		return update;
	}
	
	/**
	 * Generate the SQL that pessimistic locks a row by id (and version)
	 */
	protected String generateLockString() {
		//TODO: code duplication here - see NEP
		SimpleSelect select = new SimpleSelect()
			.setTableName( getTableName() )
			.addColumn( getIdentifierColumnNames()[0] )
			.addCondition( getIdentifierColumnNames(), "=?" );
		if ( isVersioned() ) {
			select.addWhereToken("and")
				.addCondition( getVersionColumnName(), "=?" );
		}
		return select.toStatementString();

	}
	
	/**
	 * Marshall the fields of a persistent instance to a prepared statement
	 */
	protected int dehydrate(Serializable id, Object[] fields, boolean[] includeProperty, PreparedStatement st, SessionImplementor session) throws SQLException, HibernateException {
		
		if ( log.isTraceEnabled() ) log.trace( "Dehydrating entity: " + MessageHelper.infoString(this, id) );
		
		int index = 1;
		for (int j=0; j<getHydrateSpan(); j++) {
			if ( includeProperty[j] ) {
				getPropertyTypes()[j].nullSafeSet( st, fields[j], index, session );
				index += propertyColumnSpans[j];
			}
		}
		
		if ( id!=null ) {
			getIdentifierType().nullSafeSet( st, id, index, session );
			index += getIdentifierColumnNames().length;
		}
		
		return index;
		
	}
	
	// Execute the SQL:
	
	/**
	 * Load an instance using either the <tt>forUpdateLoader</tt> or the outer joining <tt>loader</tt>,
	 * depending upon the value of the <tt>lock</tt> parameter
	 */
	public Object load(Serializable id,	Object optionalObject, LockMode lockMode, SessionImplementor session)
	throws HibernateException {
		
		if ( log.isTraceEnabled() ) log.trace( "Materializing entity: " +  MessageHelper.infoString(this, id) );
		
		try {
			return ( (UniqueEntityLoader) loaders.get(lockMode) ).load(session, id, optionalObject);
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not load: " +  MessageHelper.infoString(this, id) );
		}
	}
		
	public Serializable insert(Object[] fields, Object object, SessionImplementor session) throws HibernateException {
		if ( useDynamicInsert() ) {
			boolean[] notNull = getNotNullInsertableColumns(fields);
			return insert(fields, notNull, generateInsertString(true, notNull), object, session);
		}
		else {
			return insert(fields, getPropertyInsertability(), getSQLIdentityInsertString(), object, session);
		}
	}
	
	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) throws HibernateException {
		if ( useDynamicInsert() ) {
			boolean[] notNull = getNotNullInsertableColumns(fields);
			insert(id, fields, notNull, generateInsertString(false, notNull), object, session);
		}
		else {
			insert(id, fields, getPropertyInsertability(), getSQLInsertString(), object, session);
		}
	}
	
	/**
	 * Persist an object
	 */
	public void insert(Serializable id, Object[] fields, boolean[] notNull, String sql, Object object, SessionImplementor session) 
	throws HibernateException {
		
		if ( log.isTraceEnabled() ) {
			log.trace( "Inserting entity: " + MessageHelper.infoString(this, id) );
			if ( isVersioned() ) log.trace( "Version: " + Versioning.getVersion(fields, this) );
		}
		
		try {
			
			// Render the SQL query
			PreparedStatement statement = session.getBatcher().prepareBatchStatement(sql);
			try {
				
				// Write the values of fields onto the prepared statement - we MUST use the state at the time the
				// insert was issued (cos of foreign key constraints). Not necessarily the object's current state
				
				dehydrate(id, fields, notNull, statement, session);
				
				session.getBatcher().addToBatch(1);
				//statement.executeUpdate();
				
			}
			catch (SQLException sqle) {
				session.getBatcher().abortBatch(sqle);
				throw sqle;
			}
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not insert: " +  MessageHelper.infoString(this, id) );
		}
		
	}
	
	/**
	 * Persist an object, using a natively generated identifier
	 */
	public Serializable insert(Object[] fields, boolean[] notNull, String sql, Object object, SessionImplementor session) 
	throws HibernateException {
		
		if ( log.isTraceEnabled() ) {
			log.trace("Inserting entity: " + getClassName() + " (native id)");
			if ( isVersioned() ) log.trace( "Version: " + Versioning.getVersion(fields, this) );
		}
		
		boolean useGetGeneratedKeys = session.getFactory().isGetGeneratedKeysEnabled();
		
		try {
			
			//TODO: refactor all this stuff up to AbstractEntityPersister:
			String insertSelectSQL = useGetGeneratedKeys ? 
				null : 
				getDialect().appendIdentitySelectToInsert(sql);
			if (insertSelectSQL != null) {
				
				//use one statement to insert the row and get the generated id
				PreparedStatement insertSelect = session.getBatcher().prepareStatement(insertSelectSQL);
				try {
					dehydrate(null, fields, notNull, insertSelect, session);
					if ( !insertSelect.execute() ) {
						while ( !insertSelect.getMoreResults() );
					}
					return getGeneratedIdentity( object, session, insertSelect.getResultSet() );
				}
				finally {
					session.getBatcher().closeStatement(insertSelect);
				}
				
			} 
			else {
				
				//do the insert
				PreparedStatement statement = session.getBatcher().prepareStatement(sql, useGetGeneratedKeys);
				try {
					dehydrate(null, fields, notNull, statement, session);
					statement.executeUpdate();
					if (useGetGeneratedKeys) {
						return getGeneratedIdentity( object, session, GetGeneratedKeysHelper.getGeneratedKey(statement) );
					}
				}
				finally {
					session.getBatcher().closeStatement(statement);
				}
				
				//fetch the generated id in a separate query
				PreparedStatement idselect = session.getBatcher().prepareStatement( sqlIdentitySelect() );
				try {
					return getGeneratedIdentity( object, session, idselect.executeQuery() );
				}
				finally {
					session.getBatcher().closeStatement(idselect);
				}
				
			}
			
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not insert: " +  MessageHelper.infoString(this) );

		}
		
	}
	
	/**
	 * Delete an object
	 */
	public void delete(Serializable id, Object version, Object object, SessionImplementor session) throws HibernateException {
		
		if ( log.isTraceEnabled() ) {
			log.trace( "Deleting entity: " + MessageHelper.infoString(this, id) );
			if ( isVersioned() ) log.trace( "Version: " + version );
		}
		
		try {
		
			//Render the SQL query
			final PreparedStatement statement;// = session.getPreparedStatement( sqlDelete() );
			if ( isVersioned() ) {
				statement = session.getBatcher().prepareStatement( getSQLDeleteString() );
			}
			else {
				statement = session.getBatcher().prepareBatchStatement( getSQLDeleteString() );
			}
			
			try {
				
				// Do the key. The key is immutable so we can use the _current_ object state - not necessarily
				// the state at the time the delete was issued
				
				getIdentifierType().nullSafeSet( statement, id, 1, session );
				
				// We should use the _current_ object state (ie. after any updates that occurred during flush)
				
				if ( isVersioned() ) {
					getVersionType().nullSafeSet( statement, version, getIdentifierColumnNames().length + 1, session );
					check( statement.executeUpdate(), id );
				}
				else {
					session.getBatcher().addToBatch(1);
				}
				//check( statement.executeUpdate(), id );
				
			}
			catch (SQLException sqle) {
				if ( !isVersioned() ) {
					session.getBatcher().abortBatch(sqle);
				}
				throw sqle;
			}
			finally {
				if ( isVersioned() ) session.getBatcher().closeStatement(statement);
			}
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not delete: " +  MessageHelper.infoString(this, id) );

		}
	}
	
	/**
	 * Update an object
	 */
	public void update(Serializable id, Object[] fields, int[] dirtyFields, Object[] oldFields, Object oldVersion, Object object, SessionImplementor session) 
	throws HibernateException {
		//note: dirtyFields==null means we had no snapshot, and we couldn't get one using select-before-update
		//      oldFields==null just means we had no snapshot to begin with (we might have used select-before-update to get the dirtyFields)
		final boolean[] propsToUpdate;
		final String updateString;
		if ( useDynamicUpdate() && dirtyFields!=null ) {
			propsToUpdate = getPropertiesToUpdate(dirtyFields);
			updateString = generateUpdateString(propsToUpdate, oldFields);
			//don't need to check property updatability (dirty checking algorithm handles that)
		}
		else {
			propsToUpdate = getPropertyUpdateability();
			updateString = getSQLUpdateString();
		}

		update(id, fields, oldFields, propsToUpdate, oldVersion, object, updateString, session);
	}
	
	protected void update(Serializable id, Object[] fields, Object[] oldFields, boolean[] includeProperty, Object oldVersion, Object object, String sql, SessionImplementor session) 
	throws HibernateException {
		if ( log.isTraceEnabled() ) {
			log.trace( "Updating entity: " + MessageHelper.infoString(this, id) );
			if ( isVersioned() ) log.trace( "Existing version: " + oldVersion + " -> New version: " + fields[ getVersionProperty() ] );
		}
		
		if (!hasUpdateableColumns) return;
		
		try {

			final PreparedStatement statement = isBatchable() ? 
				session.getBatcher().prepareBatchStatement(sql) : 
				session.getBatcher().prepareStatement(sql);

			try {
				
				//Now write the values of fields onto the prepared statement
				int index = dehydrate(id, fields, includeProperty, statement, session);

				// Write any appropriate versioning conditional parameters
				if ( isVersioned() && Versioning.OPTIMISTIC_LOCK_VERSION == optimisticLockMode() ) {
					getVersionType().nullSafeSet( statement, oldVersion, index, session );
				}
				else if ( Versioning.OPTIMISTIC_LOCK_VERSION < optimisticLockMode() && null != oldFields ) {
					boolean[] includeOldField = optimisticLockMode()==Versioning.OPTIMISTIC_LOCK_ALL ?
						getPropertyUpdateability() :
						includeProperty;
					for (int j=0; j<getHydrateSpan(); j++) {
						if ( includeOldField[j] && oldFields[j]!=null ) {
							getPropertyTypes()[j].nullSafeSet( statement, oldFields[j], index, session );
							index += propertyColumnSpans[j];
						}
					}
				}

				if ( isBatchable() ) {
					session.getBatcher().addToBatch(1);
				}
				else {
					check( statement.executeUpdate(), id );
				}

			}
			catch (SQLException sqle) {
				if ( isBatchable() ) {
					session.getBatcher().abortBatch(sqle);
				}
				throw sqle;
			}
			finally {
				if ( !isBatchable() ) {
					session.getBatcher().closeStatement(statement);
				}
			}
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not update: " +  MessageHelper.infoString(this, id) );
		}
		
	}
	
	//INITIALIZATION:
	
	public EntityPersister(PersistentClass model, SessionFactoryImplementor factory) throws HibernateException {
		
		super(model, factory);
		
		// CLASS + TABLE
		
		Class mappedClass = model.getMappedClass();
		this.factory = factory;
		Table table = model.getRootTable();
		qualifiedTableName = table.getQualifiedName( getDialect(), factory.getDefaultSchema() );
		tableNames = new String[] { qualifiedTableName };
		
		//detect mapping errors:
		HashSet distinctColumns = new HashSet();

		// DISCRIMINATOR
		
		final Object discriminatorValue;
		if ( model.isPolymorphic() ) {
			Value d = model.getDiscriminator();
			if (d==null) throw new MappingException("discriminator mapping required for polymorphic persistence");
			forceDiscriminator = model.isForceDiscriminator();
			Column column = ( (Column) d.getColumnIterator().next() );
			discriminatorColumnName = column.getQuotedName( getDialect() );
			discriminatorAlias = column.getAlias();
			discriminatorType = model.getDiscriminator().getType();
			if ( model.isDiscriminatorValueNull() ) {
				discriminatorValue = NULL_DISCRIMINATOR;
				discriminatorSQLValue = InFragment.NULL;
				discriminatorInsertable = false;
			}
			else if ( model.isDiscriminatorValueNotNull() ) {
				discriminatorValue = NOT_NULL_DISCRIMINATOR;
				discriminatorSQLValue = InFragment.NOT_NULL;
				discriminatorInsertable = false;
			}
			else {
				discriminatorInsertable = model.isDiscriminatorInsertable();
				try {
					DiscriminatorType dtype = (DiscriminatorType) discriminatorType;
					discriminatorValue = dtype.stringToObject( model.getDiscriminatorValue() );
					discriminatorSQLValue = dtype.objectToSQLString(discriminatorValue);
				}
				catch (ClassCastException cce) {
					throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
				}
				catch (Exception e) {
					throw new MappingException("Could not format discriminator value to SQL string", e);
				}
				if (discriminatorInsertable) distinctColumns.add(discriminatorColumnName); //don't do this check for the case of null/not null discriminator values
			}
		}
		else {
			forceDiscriminator = false;
			discriminatorInsertable = false;
			discriminatorColumnName = null;
			discriminatorAlias = null;
			discriminatorType = null;
			discriminatorValue = null;
			discriminatorSQLValue = null;
		}
		
		// PROPERTIES

		checkColumnDuplication( distinctColumns, model.getKey().getColumnIterator() );
		
		int hydrateSpan = getHydrateSpan();
		propertyColumnNames = new String[hydrateSpan][];
		propertyColumnAliases = new String[hydrateSpan][];
		propertyColumnSpans = new int[hydrateSpan];
		propertyFormulaTemplates = new String[hydrateSpan];
		HashSet thisClassProperties = new HashSet();
		
		Iterator iter = model.getPropertyClosureIterator();
		int i=0;
		
		boolean foundColumn = false;
		boolean foundFormula = false;
		while( iter.hasNext() ) {
			Property prop = (Property) iter.next();
			thisClassProperties.add(prop);
			
			if ( prop.isFormula() ) {
				propertyColumnAliases[i] = new String[] { prop.getFormula().getAlias() };
				propertyColumnSpans[i] = 1;
				propertyFormulaTemplates[i] = prop.getFormula().getTemplate( getDialect() );
				foundFormula = true;
			}
			else {
				int span = prop.getColumnSpan();
				propertyColumnSpans[i] = span;
				String[] colNames = new String[span];
				String[] colAliases = new String[span];
				Iterator colIter = prop.getColumnIterator();
				int j=0;
				while ( colIter.hasNext() ) {
					Column col = (Column) colIter.next();
					colAliases[j] = col.getAlias();
					colNames[j] = col.getQuotedName( getDialect() );
					j++;
					if ( prop.isUpdateable() ) foundColumn=true;
				}
				propertyColumnNames[i] = colNames;
				propertyColumnAliases[i] = colAliases;
			}
			
			//initPropertyPaths(prop, StringHelper.EMPTY_STRING, factory);
			i++;

			//columns must be unique accross all subclasses
			if ( prop.isUpdateable() || prop.isInsertable() ) {
				checkColumnDuplication( distinctColumns, prop.getColumnIterator() );
			}

		}
		
		hasFormulaProperties = foundFormula;
		
		hasUpdateableColumns = foundColumn;
		
		ArrayList columns = new ArrayList();
		ArrayList aliases = new ArrayList();
		ArrayList formulas = new ArrayList();
		ArrayList formulaAliases = new ArrayList();
		ArrayList formulaTemplates = new ArrayList();
		ArrayList types = new ArrayList();
		ArrayList names = new ArrayList();
		ArrayList propColumns = new ArrayList();
		ArrayList joinedFetchesList = new ArrayList();
		ArrayList definedBySubclass = new ArrayList();
		
		iter = model.getSubclassPropertyClosureIterator();
		while ( iter.hasNext() ) {
			Property prop = (Property) iter.next();
			names.add( prop.getName() );
			definedBySubclass.add( new Boolean( !thisClassProperties.contains(prop) ) );
			types.add( prop.getType() );
			
			if ( prop.isFormula() ) {
				formulas.add( prop.getFormula().getFormula() );
				formulaTemplates.add( prop.getFormula().getTemplate( getDialect() ) );
				propColumns.add(ArrayHelper.EMPTY_STRING_ARRAY);
				formulaAliases.add( prop.getFormula().getAlias() );
			}
			else {
				Iterator colIter = prop.getColumnIterator();
				String[] cols = new String[ prop.getColumnSpan() ];
				int l=0;
				while ( colIter.hasNext() ) {
					Column col = (Column) colIter.next();
					columns.add( col.getQuotedName( getDialect() ) );
					aliases.add( col.getAlias() );
					cols[l++] = col.getQuotedName( getDialect() );
				}
				propColumns.add(cols);
			}
			joinedFetchesList.add( new Integer(
				prop.getValue().getOuterJoinFetchSetting()
			) );			
		}
		subclassColumnClosure = (String[]) columns.toArray(ArrayHelper.EMPTY_STRING_ARRAY);
		subclassFormulaClosure = (String[]) formulas.toArray(ArrayHelper.EMPTY_STRING_ARRAY);
		subclassFormulaTemplateClosure = (String[]) formulaTemplates.toArray(ArrayHelper.EMPTY_STRING_ARRAY);
		subclassPropertyTypeClosure = (Type[]) types.toArray(ArrayHelper.EMPTY_TYPE_ARRAY);
		subclassColumnAliasClosure = (String[]) aliases.toArray(ArrayHelper.EMPTY_STRING_ARRAY);
		subclassFormulaAliasClosure = (String[]) formulaAliases.toArray(ArrayHelper.EMPTY_STRING_ARRAY);
		subclassPropertyNameClosure = (String[]) names.toArray(ArrayHelper.EMPTY_STRING_ARRAY);
		subclassPropertyColumnNameClosure = (String[][]) propColumns.toArray( new String[ propColumns.size() ][] );
		
		subclassPropertyEnableJoinedFetch = new int[ joinedFetchesList.size() ];
		iter = joinedFetchesList.iterator();
		int j=0;
		while ( iter.hasNext() ) subclassPropertyEnableJoinedFetch[j++] = ( (Integer) iter.next() ).intValue();
		propertyDefinedOnSubclass = new boolean[ definedBySubclass.size() ];
		iter = definedBySubclass.iterator();
		j=0;
		while ( iter.hasNext() ) propertyDefinedOnSubclass[j++] = ( (Boolean) iter.next() ).booleanValue();
		
		sqlDeleteString = generateDeleteString();
		sqlInsertString = generateInsertString( false, getPropertyInsertability() );
		sqlIdentityInsertString = isIdentifierAssignedByInsert() ? 
			generateInsertString( true, getPropertyInsertability() ) : 
			null;
		sqlUpdateString = generateUpdateString( getPropertyUpdateability() );
		sqlConcreteSelectString = generateConcreteSelectString( getPropertyUpdateability() );
		sqlVersionSelectString = generateSelectVersionString();
				
		int subclassSpan = model.getSubclassSpan() + 1;
		subclassClosure = new Class[subclassSpan];
		subclassClosure[0] = mappedClass;
		if ( model.isPolymorphic() ) {
			subclassesByDiscriminatorValue.put(discriminatorValue, mappedClass);
		}
		
		// SUBCLASSES
		if ( model.isPolymorphic() ) {
			iter = model.getSubclassIterator();
			int k=1;
			while ( iter.hasNext() ) {
				Subclass sc = (Subclass) iter.next();
				subclassClosure[k++] = sc.getMappedClass();
				if ( sc.isDiscriminatorValueNull() ) {
					subclassesByDiscriminatorValue.put( NULL_DISCRIMINATOR, sc.getMappedClass() );
				}
				else if ( sc.isDiscriminatorValueNotNull() ) {
					subclassesByDiscriminatorValue.put( NOT_NULL_DISCRIMINATOR, sc.getMappedClass() );
				}
				else {
					try {
						DiscriminatorType dtype = (DiscriminatorType) discriminatorType;
						subclassesByDiscriminatorValue.put(
							dtype.stringToObject( sc.getDiscriminatorValue() ),
							sc.getMappedClass()
						);
					}
					catch (ClassCastException cce) {
						throw new MappingException("Illegal discriminator type: " + discriminatorType.getName() );
					}
					catch (Exception e) {
						throw new MappingException("Error parsing discriminator value", e);
					}
				}
			}
		}
		
		initLockers();
		
		initSubclassPropertyAliasesMap(model);
		
	}
	
	public String fromTableFragment(String name) {
		return getTableName() + ' '  + name;
	}

	public String queryWhereFragment(String name, boolean innerJoin, boolean includeSubclasses) throws MappingException {
		
		if ( innerJoin && ( forceDiscriminator || isInherited() ) ) {
			InFragment frag = new InFragment()
				.setColumn( name, getDiscriminatorColumnName() );
			Class[] subclasses = getSubclassClosure();
			for ( int i=0; i<subclasses.length; i++ ) {
				frag.addValue(
					( (Queryable) factory.getPersister( subclasses[i] ) ).getDiscriminatorSQLValue()
				);
			}
			StringBuffer buf = new StringBuffer(50)
				.append(" and ")
				.append( frag.toFragmentString() );
			if ( hasWhere() ) buf
				.append(" and ")
				.append( getSQLWhereString(name) );
			return buf.toString();
		}
		else {
			return hasWhere() ? 
				" and " + getSQLWhereString(name) : 
				StringHelper.EMPTY_STRING;
		}
		
	}
	
	public String[] toColumns(String name, int i) {
		return StringHelper.qualify( name, subclassPropertyColumnNameClosure[i] );
	}
	
	public String getSubclassPropertyTableName(int i) {
		return qualifiedTableName;
	}
	
	public String propertySelectFragment(String name, String suffix) {
		
		SelectFragment frag = new SelectFragment()
			.setSuffix(suffix)
			.setUsedAliases( getIdentifierAliases() );
		if ( hasSubclasses() ) frag.addColumn( name, getDiscriminatorColumnName(),  getDiscriminatorAlias() );
		return frag.addColumns(name, subclassColumnClosure, subclassColumnAliasClosure)
			.addFormulas(name, subclassFormulaTemplateClosure, subclassFormulaAliasClosure)
			.toFragmentString();
	}
	
	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
		return StringHelper.EMPTY_STRING;
	}

	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
		return StringHelper.EMPTY_STRING;
	}

	protected String[] getActualPropertyColumnNames(int i) {
		return propertyColumnNames[i];
	}
	
	protected String getFormulaTemplate(int i) {
		return propertyFormulaTemplates[i];
	}
	
	protected String getConcreteSelectString() {
		return sqlConcreteSelectString;
	}

	public boolean isCacheInvalidationRequired() {
		return hasFormulaProperties || (  !isVersioned() && useDynamicUpdate() );
	}
	
	protected String getVersionedTableName() {
		return qualifiedTableName;
	}

}








