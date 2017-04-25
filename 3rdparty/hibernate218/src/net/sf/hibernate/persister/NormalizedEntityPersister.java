//$Id: NormalizedEntityPersister.java,v 1.42 2004/12/24 03:06:25 oneovthafew Exp $
package net.sf.hibernate.persister;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import net.sf.hibernate.AssertionFailure;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.QueryException;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.engine.Versioning;
import net.sf.hibernate.impl.MessageHelper;
import net.sf.hibernate.loader.UniqueEntityLoader;
import net.sf.hibernate.mapping.Column;
import net.sf.hibernate.mapping.PersistentClass;
import net.sf.hibernate.mapping.Property;
import net.sf.hibernate.mapping.Subclass;
import net.sf.hibernate.mapping.Table;
import net.sf.hibernate.sql.CaseFragment;
import net.sf.hibernate.sql.Delete;
import net.sf.hibernate.sql.Insert;
import net.sf.hibernate.sql.JoinFragment;
import net.sf.hibernate.sql.SelectFragment;
import net.sf.hibernate.sql.SimpleSelect;
import net.sf.hibernate.sql.Update;
import net.sf.hibernate.type.AssociationType;
import net.sf.hibernate.type.DiscriminatorType;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.ArrayHelper;
import net.sf.hibernate.util.GetGeneratedKeysHelper;
import net.sf.hibernate.util.StringHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * A <tt>ClassPersister</tt> implementing the normalized "table-per-subclass" mapping strategy.
 * 
 * @author Gavin King
 */
public class NormalizedEntityPersister extends AbstractEntityPersister {
	
	private final SessionFactoryImplementor factory;
	
	// the class hierarchy structure
	private final String qualifiedTableName;
	private final String[] tableNames;
	private final String[] naturalOrderTableNames;
	private final String[][] tableKeyColumns;
	private final String[][] naturalOrderTableKeyColumns;
	private final boolean hasFormulaProperties;
	
	private final Class[] subclassClosure;
	private final String[] subclassTableNameClosure;
	private final String[][] subclassTableKeyColumns;
	private final boolean[] isClassOrSuperclassTable;
	
	// SQL strings
	private final String[] sqlDeleteStrings;
	private final String[] sqlInsertStrings;
	private final String[] sqlIdentityInsertStrings;
	private final String[] sqlUpdateStrings;
	
	// properties of this class, including inherited properties
	private final int[] propertyColumnSpans;
	private final int[] propertyTables;
	private final int[] naturalOrderPropertyTables;
	private final boolean[] propertyHasColumns;
	private final String[][] propertyColumnNames;
	private final String[][] propertyColumnAliases;
	private final String[] propertyFormulaTemplates;
	
	// the closure of all properties in the entire hierarchy including
	// subclasses and superclasses of this class
	private final String[][] subclassPropertyColumnNameClosure;
	private final int[] subclassPropertyTableNumberClosure;
	private final Type[] subclassPropertyTypeClosure;
	private final String[] subclassPropertyNameClosure;
	private final int[] subclassPropertyEnableJoinedFetch;
	private final boolean[] propertyDefinedOnSubclass;
	
	private final HashMap tableNumberByPropertyPath = new HashMap();
	
	// the closure of all columns used by the entire hierarchy including
	// subclasses and superclasses of this class
	private final int[] subclassColumnTableNumberClosure;
	private final String[] subclassColumnClosure;
	private final String[] subclassColumnClosureAliases;
	private final int[] subclassFormulaTableNumberClosure;
	private final String[] subclassFormulaTemplateClosure;
	private final String[] subclassFormulaAliasClosure;
	
	// subclass discrimination works by assigning particular
	// values to certain combinations of null primary key
	// values in the outer join using an SQL CASE
	private final HashMap subclassesByDiscriminatorValue = new HashMap();
	private final String[] discriminatorValues;
	private final String[] notNullColumns;
	private final int[] tableNumbers;
	
	private final DiscriminatorType discriminatorType;
	private final String discriminatorSQLString;
	private final String discriminatorColumnName;	
	private final String sqlConcreteSelectString;
	private final String sqlVersionSelectString;
	
	private UniqueEntityLoader loader;
	
	private static final Log log = LogFactory.getLog(NormalizedEntityPersister.class);
	
	public void postInstantiate() throws MappingException {
		
		initPropertyPaths(factory);
				
		loader = createEntityLoader(factory);
		
		createUniqueKeyLoaders(factory);
		
	}
	
	
	public boolean isDefinedOnSubclass(int i) {
		return propertyDefinedOnSubclass[i];
	}
	
	public String getDiscriminatorColumnName() {
		return discriminatorColumnName;
	}
	
	public String getDiscriminatorAlias() {
		return getDiscriminatorColumnName(); // is always "clazz_", so just use columnname
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
	
	public String getSubclassPropertyTableName(int i) {
		return subclassTableNameClosure[ subclassPropertyTableNumberClosure[i] ];
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
		return discriminatorSQLString;
	}
	
	
	public Class getSubclassForDiscriminatorValue(Object value) {
		return (Class) subclassesByDiscriminatorValue.get(value);
	}
	
	public int enableJoinedFetch(int i) {
		return subclassPropertyEnableJoinedFetch[i];
	}
	
	public Serializable[] getPropertySpaces() {
		return tableNames; // don't need subclass tables, because they can't appear in conditions
	}
	
	//Access cached SQL
	
	/**
	 * The queries that delete rows by id (and version)
	 */
	protected final String[] getSQLDeleteStrings() {
		return sqlDeleteStrings;
	}
	
	/**
	 * The queries that insert rows with a given id
	 */
	protected final String[] getSQLInsertStrings() {
		return sqlInsertStrings;
	}
	
	/**
	 * The queries that insert rows, letting the database generate an id
	 */
	protected final String[] getSQLIdentityInsertStrings() {
		return sqlIdentityInsertStrings;
	}
	
	/**
	 * The queries that update rows by id (and version)
	 */
	protected final String[] getSQLUpdateStrings() {
		return sqlUpdateStrings;
	}
	
	protected final String getVersionSelectString() {
		return sqlVersionSelectString;
	}
	
	// Generate all the SQL
	
	/**
	 * Generate the SQL that deletes rows by id (and version)
	 */
	protected String[] generateDeleteStrings() {
		String[] result = new String[ naturalOrderTableNames.length ];
		for ( int i=0; i<naturalOrderTableNames.length; i++ ) {
			Delete delete = new Delete()
				.setTableName( naturalOrderTableNames[i] )
				.setPrimaryKeyColumnNames( naturalOrderTableKeyColumns[i] );
			if (i==0) delete.setVersionColumnName( getVersionColumnName() );
			result[i] = delete.toStatementString();
		}
		return result;
	}
	
	/**
	 * Generate the SQL that inserts rows
	 */
	protected String[] generateInsertStrings(boolean identityInsert, boolean[] includeProperty) {
		
		String[] result = new String[naturalOrderTableNames.length];
		for ( int j=0; j<naturalOrderTableNames.length; j++ ) {
			
			Insert insert = new Insert( getDialect() )
				.setTableName( naturalOrderTableNames[j] );
				
			for (int i=0; i<getPropertyTypes().length; i++) {
				if ( includeProperty[i] && naturalOrderPropertyTables[i]==j ) {
					insert.addColumns( propertyColumnNames[i] );
				}
			}
			
			if (identityInsert && j==0) {
				insert.addIdentityColumn( naturalOrderTableKeyColumns[j][0] );
			}
			else {
				insert.addColumns( naturalOrderTableKeyColumns[j] );
			}
			
			result[j] = insert.toStatementString();
			
		}
		return result;
	}
	
	/**
	 * Generate the SQL that updates rows by id (and version)
	 */
	protected String[] generateUpdateStrings(boolean[] includeProperty) {
		String[] result = new String[ naturalOrderTableNames.length ];
		for ( int j=0; j<naturalOrderTableNames.length; j++ ) {
			Update update = new Update()
				.setTableName( naturalOrderTableNames[j] )
				.setPrimaryKeyColumnNames( naturalOrderTableKeyColumns[j] );
				
			if (j==0) update.setVersionColumnName( getVersionColumnName() );
			
			boolean hasColumns = false;
			for (int i=0; i<propertyColumnNames.length; i++) {
				if ( includeProperty[i] && naturalOrderPropertyTables[i]==j ) {
					update.addColumns( propertyColumnNames[i] );
					hasColumns = hasColumns || propertyColumnNames[i].length > 0;
				}
			}
			
			result[j] = hasColumns ? update.toStatementString() : null;
		}
		return result;
	}
	
	/**
	 * Generate the SQL that pessimistic locks a row by id (and version)
	 */
	protected String generateLockString() {
		//TODO: code duplication here - see EP
		SimpleSelect select = new SimpleSelect()
			.setTableName(qualifiedTableName)
			.addColumn( super.getIdentifierColumnNames()[0] )
			.addCondition( super.getIdentifierColumnNames(), "=?" );
		if ( isVersioned() ) {
			select.addWhereToken("and")
				.addCondition( getVersionColumnName(), "=?" );
		}
		return select.toStatementString();
	}

	/**
	 * Generate the SQL that selects a row by id, excluding subclasses
	 */
	protected String getConcreteSelectString() {
		return sqlConcreteSelectString;
	}
	
	private static final String CONCRETE_ALIAS = "x";
	
	protected String generateConcreteSelectString() {
		String select = "select " + 
			StringHelper.join( 
				StringHelper.COMMA_SPACE, 
				StringHelper.qualify( CONCRETE_ALIAS, getIdentifierColumnNames() ) 
			) +
			concretePropertySelectFragment( CONCRETE_ALIAS, getPropertyUpdateability() ) + 
			" from " +
			fromTableFragment(CONCRETE_ALIAS) + 
			fromJoinFragment(CONCRETE_ALIAS, true, false) +
			" where " +
			whereJoinFragment(CONCRETE_ALIAS, true, false) +
			StringHelper.join(
				"=? and ", 
				StringHelper.qualify( CONCRETE_ALIAS, getIdentifierColumnNames() ) 
			) +
			"=?";
		if ( isVersioned() ) {
			select += 
				" and " + 
				getVersionColumnName() + 
				"=?";
		} 
		return select;
	}
	
	/**
	 * Marshall the fields of a persistent instance to a prepared statement
	 */
	protected int dehydrate(Serializable id, Object[] fields, boolean[] includeProperty, PreparedStatement[] statements, SessionImplementor session) throws SQLException, HibernateException {
		
		if ( log.isTraceEnabled() ) log.trace( "Dehydrating entity: " + MessageHelper.infoString(this, id) );
		
		int versionParam = 0;
		
		for ( int i=0; i<tableNames.length; i++ ) {
			int index = dehydrate(id, fields, includeProperty, i, statements[i], session);
			if (i==0) versionParam = index;
		}
		
		return versionParam;
		
	}
	
	private int dehydrate(Serializable id, Object[] fields, boolean[] includeProperty, int table, PreparedStatement statement, SessionImplementor session) throws SQLException, HibernateException {
		
		if (statement==null) return -1;
		
		int index = 1;
		
		for (int j=0; j<getHydrateSpan(); j++) {
			
			if ( includeProperty[j] && naturalOrderPropertyTables[j]==table ) {
				getPropertyTypes()[j].nullSafeSet( statement, fields[j], index, session );
				index += propertyColumnSpans[j];
			}
			
		}
		
		if ( id!=null ) {
			getIdentifierType().nullSafeSet( statement, id, index, session );
			index+=getIdentifierColumnNames().length;
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
		
		if ( log.isTraceEnabled() ) log.trace( "Materializing entity: " + MessageHelper.infoString(this, id) );
		
		try {
		
			Object result = loader.load(session, id, optionalObject);
		
			if (result!=null) lock(id, getVersion(result), result, lockMode, session);
		
			return result;
			
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not load by id: " +  MessageHelper.infoString(this, id) );
		}
	}

	public Serializable insert(Object[] fields, Object object, SessionImplementor session) throws HibernateException {
		if ( useDynamicInsert() ) {
			boolean[] notNull = getNotNullInsertableColumns(fields);
			return insert(fields, notNull, generateInsertStrings(true, notNull), object, session);
		}
		else {
			return insert(fields, getPropertyInsertability(), getSQLIdentityInsertStrings(), object, session);
		}
	}
	
	public void insert(Serializable id, Object[] fields, Object object, SessionImplementor session) throws HibernateException {
		if ( useDynamicInsert() ) {
			boolean[] notNull = getNotNullInsertableColumns(fields);
			insert(id, fields, notNull, generateInsertStrings(false, notNull), object, session);
		}
		else {
			insert(id, fields, getPropertyInsertability(), getSQLInsertStrings(), object, session);
		}
	}
	
	/**
	 * Persist an object
	 */
	public void insert(Serializable id, Object[] fields, boolean[] notNull, String[] sql, Object object, SessionImplementor session) throws HibernateException {
		
		if ( log.isTraceEnabled() ) {
			log.trace( "Inserting entity: " + MessageHelper.infoString(this, id) );
			if ( isVersioned() ) log.trace( "Version: " + Versioning.getVersion(fields, this) );
		}
		
		try {
			// Render the SQL query
			final PreparedStatement[] statements = new PreparedStatement[ tableNames.length ];
			try {
				
				for ( int i=0; i<tableNames.length; i++ ) {
					statements[i] = session.getBatcher().prepareStatement( sql[i] );
				}
				
				// Write the values of fields onto the prepared statement - we MUST use the state at the time the
				// insert was issued (cos of foreign key constraints). Not necessarily the object's current state
				
				dehydrate(id, fields, notNull, statements, session);
				
				for ( int i=0; i<tableNames.length; i++ ) statements[i].executeUpdate();
				
			}
			finally {
				for ( int i=0; i<tableNames.length; i++ ) {
					if ( statements[i]!=null ) session.getBatcher().closeStatement( statements[i] );
				}
			}
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not insert: " +  MessageHelper.infoString(this, id) );
		}
		
	}
	
	/**
	 * Persist an object, using a natively generated identifier
	 */
	public Serializable insert(Object[] fields, boolean[] notNull, String[] sql, Object object, SessionImplementor session) throws HibernateException {
		
		if ( log.isTraceEnabled() ) {
			log.trace("Inserting entity: " + getClassName() + " (native id)");
			if ( isVersioned() ) log.trace( "Version: " + Versioning.getVersion(fields, this) );
		}
		
		boolean useGetGeneratedKeys = session.getFactory().isGetGeneratedKeysEnabled();
		Serializable id = null;
		
		try {
			
			//TODO: refactor all this stuff up to AbstractEntityPersister:
			String insertSelectSQL = useGetGeneratedKeys ? 
				null : 
				getDialect().appendIdentitySelectToInsert( sql[0] );
			if (insertSelectSQL != null) {
				
				//use one statement to insert the row and get the generated id
				PreparedStatement insertSelect = session.getBatcher().prepareStatement(insertSelectSQL);
				try {
					dehydrate(null, fields, notNull, 0, insertSelect, session);
					if ( !insertSelect.execute() ) {
						while ( !insertSelect.getMoreResults() );
					}
					id = getGeneratedIdentity( object, session, insertSelect.getResultSet() );
				}
				finally {
					session.getBatcher().closeStatement(insertSelect);
				}
				
			} 
			else {
		  
				//do the insert
				PreparedStatement statement = session.getBatcher().prepareStatement( sql[0], useGetGeneratedKeys );
				try {
					dehydrate(null, fields, notNull, 0, statement, session);
					statement.executeUpdate();
	
					if (useGetGeneratedKeys) {
						id = getGeneratedIdentity( object, session, GetGeneratedKeysHelper.getGeneratedKey(statement) );
					}	
				}
				finally {
					session.getBatcher().closeStatement(statement);
				}
				
				if (!useGetGeneratedKeys) {
					// fetch the generated id in a separate query
					PreparedStatement idselect = session.getBatcher().prepareStatement( sqlIdentitySelect() );
					try {
						id = getGeneratedIdentity( object, session, idselect.executeQuery() );
					}
					finally {
						session.getBatcher().closeStatement(idselect);
					}
				}
			}
			
			
			for ( int i=1; i<naturalOrderTableNames.length; i++ )  {
				
				PreparedStatement statement = session.getBatcher().prepareStatement( sql[i] );
				
				try {
					dehydrate(id, fields, notNull, i, statement, session);
					statement.executeUpdate();
				}
				finally {
					session.getBatcher().closeStatement(statement);
				}
				
			}
			
			return id;

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
			//if (versioned) log.trace( "Version: " + version );
		}
		
		try {
			final PreparedStatement[] statements = new PreparedStatement[naturalOrderTableNames.length];
			try {
				
				for ( int i=0; i<naturalOrderTableNames.length; i++ ) {
					statements[i] = session.getBatcher().prepareStatement( getSQLDeleteStrings()[i] );
				}
				
				if ( isVersioned() ) getVersionType().nullSafeSet( statements[0], version, getIdentifierColumnNames().length + 1, session );
				
				for ( int i=naturalOrderTableNames.length-1; i>=0; i-- ) {
					
					// Do the key. The key is immutable so we can use the _current_ object state - not necessarily
					// the state at the time the delete was issued
					
					getIdentifierType().nullSafeSet( statements[i], id, 1, session );
					
					check( statements[i].executeUpdate(), id );
					
				}
				
			}
			finally {
				for ( int i=0; i<naturalOrderTableNames.length; i++ )  {
					if ( statements[i]!=null ) session.getBatcher().closeStatement( statements[i] );
				}
			}
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not delete: " +  MessageHelper.infoString(this, id) );
		}
	}
	
	/**
	 * Decide which tables need to be updated
	 */
	private boolean[] getTableUpdateNeeded(final int[] dirtyFields) {
		
		if (dirtyFields==null) {
			return propertyHasColumns; // for objects that came in via update()
		}
		else {
			boolean[] tableUpdateNeeded = new boolean[naturalOrderTableNames.length];
			for ( int i=0; i<dirtyFields.length; i++ ) {
				int table = naturalOrderPropertyTables[ dirtyFields[i] ];
				tableUpdateNeeded[table] = tableUpdateNeeded[table] || 
					propertyColumnSpans[ dirtyFields[i] ]>0;
			}
			if ( isVersioned() ) tableUpdateNeeded[0] = true;
			return tableUpdateNeeded;
		}
	}
	
	/**
	 * Update an object
	 */
	public void update(Serializable id, Object[] fields, int[] dirtyFields, Object[] oldFields, Object oldVersion, Object object, SessionImplementor session) throws HibernateException {

		final boolean[] tableUpdateNeeded = getTableUpdateNeeded(dirtyFields);
		
		final String[] updateStrings;
		final boolean[] propsToUpdate;
		if ( useDynamicUpdate() && dirtyFields!=null ) {
			// decide which columns we really need to update
			propsToUpdate = getPropertiesToUpdate(dirtyFields);
			updateStrings = generateUpdateStrings(propsToUpdate);
		}
		else {
			// just update them all
			propsToUpdate = getPropertyUpdateability();
			updateStrings = getSQLUpdateStrings();
		}
		
		update(id, fields, propsToUpdate, tableUpdateNeeded, oldVersion, object, updateStrings, session);
	}
	
	protected void update(Serializable id, Object[] fields, boolean[] includeProperty, boolean[] includeTable, Object oldVersion, Object object, String[] sql, SessionImplementor session) throws HibernateException {
		
		if ( log.isTraceEnabled() ) {
			log.trace( "Updating entity: " + MessageHelper.infoString(this, id) );
			if ( isVersioned() ) log.trace( "Existing version: " + oldVersion + " -> New version: " + fields[ getVersionProperty() ] );
		}
		
		int tables = naturalOrderTableNames.length;
		
		try {
			final PreparedStatement[] statements = new PreparedStatement[tables];
			try {
				
				for ( int i=0; i<tables; i++ ) {
					if ( includeTable[i] ) statements[i] = session.getBatcher().prepareStatement( sql[i] );
				}
				
				int versionParam = dehydrate(id, fields, includeProperty, statements, session);
				
				if ( isVersioned() ) getVersionType().nullSafeSet( statements[0], oldVersion, versionParam, session );
				
				for ( int i=0; i<tables; i++ ) {
					if ( includeTable[i] ) check( statements[i].executeUpdate(), id );
				}
				
			}
			finally {
				for ( int i=0; i<tables; i++ )  {
					if ( statements[i]!=null ) session.getBatcher().closeStatement( statements[i] );
				}
			}
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not update: " +  MessageHelper.infoString(this, id) );
		}
		
	}
	
	//INITIALIZATION:
	
	public NormalizedEntityPersister(PersistentClass model, SessionFactoryImplementor factory) throws HibernateException {
		
		super(model, factory);
		
		// CLASS + TABLE
		
		this.factory = factory;
		Table table = model.getRootTable();
		qualifiedTableName = table.getQualifiedName( getDialect(), factory.getDefaultSchema() );
		
		// DISCRIMINATOR
		
		final Object discriminatorValue;
		if ( model.isPolymorphic() ) {
			discriminatorColumnName = "clazz_";
			try {
				discriminatorType = (DiscriminatorType) Hibernate.INTEGER;
				discriminatorValue = new Integer(0);
				discriminatorSQLString = "0";
			}
			catch (Exception e) {
				throw new MappingException("Could not format discriminator value to SQL string", e );
			}
		}
		else {
			discriminatorColumnName = null;
			discriminatorType = null;
			discriminatorValue = null;
			discriminatorSQLString = null;
		}
		
		if ( optimisticLockMode()!=Versioning.OPTIMISTIC_LOCK_VERSION ) throw new MappingException("optimistic-lock attribute not supported for joined-subclass mappings: " + getClassName() );
		
		//MULTITABLES
		
		ArrayList tables = new ArrayList();
		ArrayList keyColumns = new ArrayList();
		tables.add(qualifiedTableName);
		keyColumns.add( super.getIdentifierColumnNames() );
		
		int idColumnSpan = super.getIdentifierColumnNames().length;
		Iterator titer = model.getTableClosureIterator();
		while ( titer.hasNext() ) {
			Table tab = (Table) titer.next();
			String tabname = tab.getQualifiedName( getDialect(), factory.getDefaultSchema() );
			if ( !tabname.equals(qualifiedTableName) ) {
				tables.add(tabname);
				String[] key = new String[idColumnSpan];
				Iterator kiter = tab.getPrimaryKey().getColumnIterator();
				for ( int k=0; k<idColumnSpan; k++ ) key[k] = ( (Column) kiter.next() ).getQuotedName( getDialect() );
				keyColumns.add(key);
			}
		}
		naturalOrderTableNames = (String[]) tables.toArray(ArrayHelper.EMPTY_STRING_ARRAY);
		naturalOrderTableKeyColumns = (String[][]) keyColumns.toArray( new String[0][] );
		
		ArrayList subtables = new ArrayList();
		keyColumns = new ArrayList();
		subtables.add(qualifiedTableName);
		keyColumns.add( super.getIdentifierColumnNames() );
		titer = model.getSubclassTableClosureIterator();
		while ( titer.hasNext() ) {
			Table tab = (Table) titer.next();
			String tabname = tab.getQualifiedName( getDialect(), factory.getDefaultSchema() );
			if ( !tabname.equals(qualifiedTableName) ) {
				subtables.add(tabname);
				String[] key = new String[idColumnSpan];
				Iterator kiter = tab.getPrimaryKey().getColumnIterator();
				for ( int k=0; k<idColumnSpan; k++ ) key[k] = ( (Column) kiter.next() ).getQuotedName( getDialect() );
				keyColumns.add(key);
			}
		}
		subclassTableNameClosure = (String[]) subtables.toArray(ArrayHelper.EMPTY_STRING_ARRAY);
		subclassTableKeyColumns = (String[][]) keyColumns.toArray( new String[0][] );
		isClassOrSuperclassTable = new boolean[ subclassTableNameClosure.length ];
		for ( int j=0; j<subclassTableNameClosure.length; j++ ) {
			isClassOrSuperclassTable[j] = tables.contains( subclassTableNameClosure[j] );
		}
		
		int len = naturalOrderTableNames.length;
		tableNames = reverse(naturalOrderTableNames);
		tableKeyColumns = reverse(naturalOrderTableKeyColumns);
		reverse(subclassTableNameClosure, len);
		reverse(subclassTableKeyColumns, len);
		
		// PROPERTIES
		
		int hydrateSpan = getHydrateSpan();
		propertyTables = new int[hydrateSpan];
		naturalOrderPropertyTables = new int[hydrateSpan];
		propertyColumnNames = new String[hydrateSpan][];
		propertyColumnAliases = new String[hydrateSpan][];
		propertyColumnSpans = new int[hydrateSpan];
		propertyFormulaTemplates = new String[hydrateSpan];
		
		HashSet thisClassProperties = new HashSet();
		
		Iterator iter = model.getPropertyClosureIterator();
		int i=0;
		boolean foundFormula = false;
		while( iter.hasNext() ) {
			Property prop = (Property) iter.next();
			thisClassProperties.add(prop);
			Table tab = prop.getValue().getTable();
			String tabname = tab.getQualifiedName( getDialect(), factory.getDefaultSchema() );
			propertyTables[i] = getTableId(tabname, tableNames);
			naturalOrderPropertyTables[i] = getTableId(tabname, naturalOrderTableNames);

			if ( prop.isFormula() ) {
				propertyColumnAliases[i] = new String[] { prop.getFormula().getAlias() };
				propertyColumnSpans[i] = 1;
				propertyFormulaTemplates[i] = prop.getFormula().getTemplate( getDialect() );
				foundFormula = true;
			}
			else {
				propertyColumnSpans[i] = prop.getColumnSpan();
							
				String[] propCols = new String[ propertyColumnSpans[i] ];
				String[] propAliases = new String[ propertyColumnSpans[i] ];
				Iterator colIter = prop.getColumnIterator();
				int j=0;
				while ( colIter.hasNext() ) {
					Column col = (Column) colIter.next();
					String colname = col.getQuotedName( getDialect() );
					propCols[j] = colname;
					propAliases[j] = col.getAlias() + tab.getUniqueInteger() + StringHelper.UNDERSCORE;
					j++;
				}
				propertyColumnNames[i] = propCols;
				propertyColumnAliases[i] = propAliases;
			}
			
			i++;
		}
		
		hasFormulaProperties = foundFormula;
		
		//check distinctness of columns for this specific subclass only
		HashSet distinctColumns = new HashSet();
		checkColumnDuplication( distinctColumns, model.getKey().getColumnIterator() );
		iter = model.getPropertyIterator();
		while ( iter.hasNext() ) {
			Property prop = (Property) iter.next();
			if ( prop.isUpdateable() || prop.isInsertable() ) {
				checkColumnDuplication( distinctColumns, prop.getColumnIterator() );
			}	
		}
		
		// subclass closure properties
		
		ArrayList columns = new ArrayList();
		ArrayList aliases = new ArrayList();
		ArrayList formulaAliases = new ArrayList();
		ArrayList formulaTemplates = new ArrayList();
		ArrayList types = new ArrayList();
		ArrayList names = new ArrayList();
		ArrayList propColumns = new ArrayList();
		ArrayList coltables = new ArrayList();
		ArrayList formtables = new ArrayList();
		ArrayList joinedFetchesList = new ArrayList();
		ArrayList propTables = new ArrayList();
		ArrayList definedBySubclass = new ArrayList();
		
		iter = model.getSubclassPropertyClosureIterator();
		while ( iter.hasNext() ) {
			Property prop = (Property) iter.next();
			names.add( prop.getName() );
			definedBySubclass.add( new Boolean( !thisClassProperties.contains(prop) ) );
			Table tab = prop.getValue().getTable();
			String tabname = tab.getQualifiedName( getDialect(), factory.getDefaultSchema() );
			Integer tabnum = new Integer( getTableId(tabname, subclassTableNameClosure) );
			propTables.add(tabnum);
			types.add( prop.getType() );

			if ( prop.isFormula() ) {
				formulaTemplates.add( prop.getFormula().getTemplate( getDialect() ) );
				propColumns.add(ArrayHelper.EMPTY_STRING_ARRAY);
				formulaAliases.add( prop.getFormula().getAlias() );
				formtables.add(tabnum);
			}
			else {
				Iterator colIter = prop.getColumnIterator();
				String[] cols = new String[ prop.getColumnSpan() ];
				int l=0;
				while ( colIter.hasNext() ) {
					Column col = (Column) colIter.next();
					columns.add( col.getQuotedName( getDialect() ) );
					coltables.add(tabnum);
					cols[l++]=col.getQuotedName( getDialect() );
					aliases.add( col.getAlias() + tab.getUniqueInteger() + StringHelper.UNDERSCORE );
				}
				propColumns.add(cols);
			}
			joinedFetchesList.add( new Integer(
				prop.getValue().getOuterJoinFetchSetting()
			) );
		}
		subclassColumnClosure = (String[]) columns.toArray(ArrayHelper.EMPTY_STRING_ARRAY);
		subclassColumnClosureAliases = (String[]) aliases.toArray(ArrayHelper.EMPTY_STRING_ARRAY);
		subclassColumnTableNumberClosure = ArrayHelper.toIntArray(coltables);
		subclassPropertyTypeClosure = (Type[]) types.toArray(ArrayHelper.EMPTY_TYPE_ARRAY);
		subclassPropertyNameClosure = (String[]) names.toArray(ArrayHelper.EMPTY_STRING_ARRAY);
		subclassPropertyTableNumberClosure = ArrayHelper.toIntArray(propTables);
		subclassFormulaAliasClosure = (String[]) formulaAliases.toArray(ArrayHelper.EMPTY_STRING_ARRAY);
		subclassFormulaTemplateClosure = (String[]) formulaTemplates.toArray(ArrayHelper.EMPTY_STRING_ARRAY);
		subclassFormulaTableNumberClosure = ArrayHelper.toIntArray(formtables);
		subclassPropertyColumnNameClosure = (String[][]) propColumns.toArray( new String[ propColumns.size() ][] );
		
		subclassPropertyEnableJoinedFetch = new int[ joinedFetchesList.size() ];
		iter = joinedFetchesList.iterator();
		int j=0;
		while ( iter.hasNext() ) subclassPropertyEnableJoinedFetch[j++] = ( (Integer) iter.next() ).intValue();
		propertyDefinedOnSubclass = new boolean[ definedBySubclass.size() ];
		iter = definedBySubclass.iterator();
		j=0;
		while ( iter.hasNext() ) propertyDefinedOnSubclass[j++] = ( (Boolean) iter.next() ).booleanValue();
		
		sqlDeleteStrings = generateDeleteStrings();
		sqlInsertStrings = generateInsertStrings( false, getPropertyInsertability() );
		sqlIdentityInsertStrings = isIdentifierAssignedByInsert() ? 
			generateInsertStrings( true, getPropertyInsertability() ) : 
			null;
		sqlUpdateStrings = generateUpdateStrings( getPropertyUpdateability() );
				
		sqlVersionSelectString = generateSelectVersionString();
		sqlConcreteSelectString = generateConcreteSelectString();
		
		Class mappedClass = model.getMappedClass();
		
		// SUBCLASSES
		
		int subclassSpan = model.getSubclassSpan() + 1;
		subclassClosure = new Class[subclassSpan];
		subclassClosure[subclassSpan-1] = mappedClass;
		if ( model.isPolymorphic() ) {
			subclassesByDiscriminatorValue.put(discriminatorValue, mappedClass);
			discriminatorValues = new String[subclassSpan];
			discriminatorValues[subclassSpan-1] = discriminatorSQLString;
			tableNumbers = new int[subclassSpan];
			int id = getTableId(
				model.getTable().getQualifiedName( getDialect(), factory.getDefaultSchema() ),
				subclassTableNameClosure
			);
			tableNumbers[subclassSpan-1] = id;
			notNullColumns = new String[subclassSpan];
			notNullColumns[subclassSpan-1] =  subclassTableKeyColumns[id][0]; //( (Column) model.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
		}
		else {
			discriminatorValues = null;
			tableNumbers = null;
			notNullColumns = null;
		}
		
		iter = model.getSubclassIterator();
		int k=0;
		while ( iter.hasNext() ) {
			Subclass sc = (Subclass) iter.next();
			subclassClosure[k] = sc.getMappedClass();
			try {
				if ( model.isPolymorphic() ) {
					Object disc = new Integer(k+1);
					subclassesByDiscriminatorValue.put( disc, sc.getMappedClass() );
					discriminatorValues[k] = disc.toString();
					int id = getTableId(
						sc.getTable().getQualifiedName( getDialect(), factory.getDefaultSchema() ),
						subclassTableNameClosure
					);
					tableNumbers[k] = id;
					notNullColumns[k] = subclassTableKeyColumns[id][0]; //( (Column) sc.getTable().getPrimaryKey().getColumnIterator().next() ).getName();
				}
			}
			catch (Exception e) {
				throw new MappingException("Error parsing discriminator value", e );
			}
			k++;
		}
		
		propertyHasColumns = new boolean[sqlUpdateStrings.length];
		for ( int m=0; m<sqlUpdateStrings.length; m++ ) {
			propertyHasColumns[m] = sqlUpdateStrings[m]!=null;
		}
		
		initLockers();
		
		initSubclassPropertyAliasesMap(model);
		
	}
	
	private static final void reverse(Object[] objects, int len) {
		Object[] temp = new Object[len];
		for (int i=0; i<len; i++) {
			temp[i] = objects[len-i-1];
		}
		for (int i=0; i<len; i++) {
			objects[i] = temp[i];
		}
	}

	private static final String[] reverse(String[] objects) {
		int len = objects.length;
		String[] temp = new String[len];
		for (int i=0; i<len; i++) {
			temp[i] = objects[len-i-1];
		}
		return temp;
	}

	private static final String[][] reverse(String[][] objects) {
		int len = objects.length;
		String[][] temp = new String[len][];
		for (int i=0; i<len; i++) {
			temp[i] = objects[len-i-1];
		}
		return temp;
	}
	
	protected int getPropertyTableNumber(String propertyName) {
		String[] propertyNames = getPropertyNames();
		for ( int i=0; i<propertyNames.length; i++ ) {
			if ( propertyName.equals( propertyNames[i] ) ) return propertyTables[i];
		}
		return 0;
	}
	
	protected void handlePath(String path, Type type) {
		if ( 
			type.isAssociationType() &&
			( (AssociationType) type).usePrimaryKeyAsForeignKey()
		) {
			tableNumberByPropertyPath.put( path, new Integer(0) );
		}
		else {
			String propertyName = StringHelper.root(path);
			tableNumberByPropertyPath.put( path, new Integer( getPropertyTableNumber(propertyName) ) );
		}
	}	
	
	public String fromTableFragment(String alias) {
		return subclassTableNameClosure[0] + ' ' + alias;
	}
	
	public String getTableName() {
		return subclassTableNameClosure[0];
	}
	
	private JoinFragment outerjoin(String name, boolean innerJoin, boolean includeSubclasses) {
		JoinFragment outerjoin = factory.getDialect().createOuterJoinFragment();
		for ( int i=1; i<subclassTableNameClosure.length; i++ ) {
			if (includeSubclasses || isClassOrSuperclassTable[i]) {
				outerjoin.addJoin(
					subclassTableNameClosure[i],
					alias(name, i),
					StringHelper.qualify( name, getIdentifierColumnNames() ),
					subclassTableKeyColumns[i],
					innerJoin && isClassOrSuperclassTable[i] ? 
						JoinFragment.INNER_JOIN : 
						JoinFragment.LEFT_OUTER_JOIN
				);
			}
		}
		return outerjoin;
	}
	
	private static int getTableId(String tableName, String[] tables) {
		for ( int tab=0; tab<tables.length; tab++ ) {
			if ( tableName.equals( tables[tab] ) ) return tab;
		}
		throw new AssertionFailure("table not found");
	}
	
	public String[] toColumns(String alias, String property) throws QueryException {
		
		if ( ENTITY_CLASS.equals(property) ) {
			// This doesn't actually seem to work but it *might*
			// work on some dbs. Also it doesn't work if there
			// are multiple columns of results because it
			// is not accounting for the suffix:
			// return new String[] { getDiscriminatorColumnName() };
			
			return new String[] { discriminatorFragment(alias).toFragmentString() };
		}
		
		int tab = ( (Integer) tableNumberByPropertyPath.get(property) ).intValue();
		
		return super.toColumns( alias(alias, tab), property );
	}
	
	public String[] toColumns(String alias, int i) {
		int tab = subclassPropertyTableNumberClosure[i];
		return StringHelper.qualify( 
			alias(alias, tab), 
			subclassPropertyColumnNameClosure[i]
		);
	}
	
	private String concretePropertySelectFragment(String alias, boolean[] includeProperty) {
		int propertyCount = getPropertyNames().length;
		SelectFragment frag = new SelectFragment();
		for ( int i=0; i<propertyCount; i++ ) {
			if ( includeProperty[i] ) { //ie. updateable, not a formula
				frag.addColumns( 
					alias( alias, propertyTables[i] ), 
					propertyColumnNames[i], 
					propertyColumnAliases[i] 
				);
				//don't need to handle formulas 'cos they aren't updateable!
			}
		}
		return frag.toFragmentString();
	}
	
	public String propertySelectFragment(String alias, String suffix) {
		
		SelectFragment frag = new SelectFragment()
			.setSuffix(suffix)
			.setUsedAliases( getIdentifierAliases() );
		for ( int i=0; i<subclassColumnClosure.length; i++ ) {
			String subalias = alias( alias, subclassColumnTableNumberClosure[i] );
			frag.addColumn( subalias, subclassColumnClosure[i], subclassColumnClosureAliases[i] );
		}
		for ( int i=0; i<subclassFormulaTemplateClosure.length; i++ ) {
			String subalias = alias( alias, subclassFormulaTableNumberClosure[i] );
			frag.addFormula( subalias, subclassFormulaTemplateClosure[i], subclassFormulaAliasClosure[i] );
		}

		if ( hasSubclasses() ) {
			return ", " + 
				discriminatorFragment(alias)
					.setReturnColumnName( getDiscriminatorAlias(), suffix )
					.toFragmentString() + 
				frag.toFragmentString();
		}
		else {
			return frag.toFragmentString();
		}
	}

	private CaseFragment discriminatorFragment(String alias) {
		CaseFragment cases = getDialect().createCaseFragment();
		
		for ( int i=0; i< discriminatorValues.length; i++ ) {
			cases.addWhenColumnNotNull(
				alias( alias, tableNumbers[i] ),
				notNullColumns[i],
				discriminatorValues[i]
			);
		}
		
		return cases;
	}
	
	private static String alias(String name, int tableNumber) {
		if (tableNumber==0) return name;
		return name + StringHelper.UNDERSCORE + tableNumber + StringHelper.UNDERSCORE;
	}
	
	public String fromJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
		return outerjoin(alias, innerJoin, includeSubclasses).toFromFragmentString();
	}

	public String whereJoinFragment(String alias, boolean innerJoin, boolean includeSubclasses) {
		return outerjoin(alias, innerJoin, includeSubclasses).toWhereFragmentString();
	}

	public String queryWhereFragment(String alias, boolean innerJoin, boolean includeSubclasses) throws MappingException {
		String result = whereJoinFragment(alias, innerJoin, includeSubclasses);
		String rootAlias = alias( alias, naturalOrderTableNames.length-1 ); //urgh, ugly!
		if ( hasWhere() ) result += " and " + getSQLWhereString(rootAlias);
		return result;
	}
	
	public String[] getIdentifierColumnNames() {
		return tableKeyColumns[0];
	}

	protected String[] getActualPropertyColumnNames(int i) {
		return propertyColumnNames[i];
	}
	
	protected String getFormulaTemplate(int i) {
		return propertyFormulaTemplates[i];
	}
	
	public boolean isCacheInvalidationRequired() {
		return hasFormulaProperties || ( !isVersioned() && isInherited() );
	}
	
	protected String getVersionedTableName() {
		return qualifiedTableName;
	}

}








