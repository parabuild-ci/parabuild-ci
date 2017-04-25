//$Id: AbstractCollectionPersister.java,v 1.10 2004/11/11 20:42:28 steveebersole Exp $
package net.sf.hibernate.collection;

import net.sf.hibernate.metadata.CollectionMetadata;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Iterator;

import net.sf.hibernate.AssertionFailure;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.JDBCException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.QueryException;
import net.sf.hibernate.exception.SQLExceptionConverter;
import net.sf.hibernate.exception.JDBCExceptionHelper;
import net.sf.hibernate.cache.CacheConcurrencyStrategy;
import net.sf.hibernate.cache.CacheException;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.id.IdentifierGenerator;
import net.sf.hibernate.impl.MessageHelper;
import net.sf.hibernate.loader.CollectionInitializer;
import net.sf.hibernate.mapping.Collection;
import net.sf.hibernate.mapping.Column;
import net.sf.hibernate.mapping.IdentifierCollection;
import net.sf.hibernate.mapping.IndexedCollection;
import net.sf.hibernate.mapping.Table;
import net.sf.hibernate.persister.ClassPersister;
import net.sf.hibernate.persister.Loadable;
import net.sf.hibernate.persister.PropertyMapping;
import net.sf.hibernate.sql.Alias;
import net.sf.hibernate.sql.SelectFragment;
import net.sf.hibernate.sql.Template;
import net.sf.hibernate.type.AbstractComponentType;
import net.sf.hibernate.type.EntityType;
import net.sf.hibernate.type.PersistentCollectionType;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.StringHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Base implementation of the <tt>QueryableCollection</tt> interface.
 * @see BasicCollectionPersister
 * @see OneToManyPersister
 * @author Gavin King
 */
public abstract class AbstractCollectionPersister implements CollectionMetadata, QueryableCollection {
	// TODO: encapsulate the protected instance variables!
	//private final String sqlSelectString;
	private final String sqlDeleteString;
	private final String sqlInsertRowString;
	private final String sqlUpdateRowString;
	private final String sqlDeleteRowString;
	private final String sqlOrderByString;
	protected final String sqlWhereString;
	private final String sqlOrderByStringTemplate;
	private final String sqlWhereStringTemplate;
	private final boolean hasOrder;
	protected final boolean hasWhere;
	private final boolean hasOrphanDelete;
	//private final boolean isSet;
	private final Type keyType;
	private final Type indexType;
	protected final Type elementType;
	protected final String[] keyColumnNames;
	protected final String[] indexColumnNames;
	protected final String[] elementColumnNames;
	//private final String[] unquotedIndexColumnNames;
	//private final String[] unquotedElementColumnNames;
	//private final String[] unquotedKeyColumnNames;
	protected final String[] rowSelectColumnNames;
	protected final String[] indexColumnAliases;
	protected final String[] elementColumnAliases;
	protected final String[] keyColumnAliases;
	private final Type rowSelectType;
	private final boolean primitiveArray;
	private final boolean array;
	//protected final boolean isOneToMany;
	protected final String qualifiedTableName;
	protected final boolean hasIndex;
	private final boolean isLazy;
	private final boolean isInverse;
	protected final int batchSize;
	private final Class elementClass;
	private final CacheConcurrencyStrategy cache;
	private final PersistentCollectionType collectionType;
	private final int enableJoinedFetch;
	private final Class ownerClass;
	//private final boolean isSorted;
	private final IdentifierGenerator identifierGenerator;
	private final String unquotedIdentifierColumnName;
	private final Type identifierType;
	protected final boolean hasIdentifier;
	protected final String identifierColumnName;
	private final String identifierColumnAlias;
	private final Dialect dialect;
	private final SQLExceptionConverter sqlExceptionConverter;
	private final PropertyMapping elementPropertyMapping;
	protected final ClassPersister elementPersister;
	
	private final CollectionInitializer initializer;
	
	private final String role;
	//private final SessionFactoryImplementor factory;
	
	private static final Log log = LogFactory.getLog(BasicCollectionPersister.class);
	
	public AbstractCollectionPersister(Collection collection, Configuration cfg, SessionFactoryImplementor factory) 
		throws MappingException, CacheException {
		
		dialect = factory.getDialect();
		sqlExceptionConverter = factory.getSQLExceptionConverter();
		collectionType = collection.getCollectionType();
		role = collection.getRole();
		ownerClass = collection.getOwnerClass();
		Alias alias = new Alias("__");
		
		sqlOrderByString = collection.getOrderBy();
		hasOrder = sqlOrderByString!=null;
		sqlOrderByStringTemplate = hasOrder ? Template.renderOrderByStringTemplate(sqlOrderByString, dialect) : null;
		sqlWhereString = collection.getWhere();
		hasWhere = sqlWhereString!=null;
		sqlWhereStringTemplate = hasWhere ? Template.renderWhereStringTemplate(sqlWhereString, dialect) : null;
		
		hasOrphanDelete = collection.hasOrphanDelete();
		
		batchSize = collection.getBatchSize();
		
		cache=collection.getCache();
		
		keyType = collection.getKey().getType();
		Iterator iter = collection.getKey().getColumnIterator();
		int keySpan = collection.getKey().getColumnSpan();
		keyColumnNames = new String[keySpan];
		String[] keyAliases = new String[keySpan];
		int k=0;
		while ( iter.hasNext() ) {
			Column col = ( (Column) iter.next() );
			keyColumnNames[k] = col.getQuotedName(dialect);
			keyAliases[k] = col.getAlias();
			k++;
		}
		keyColumnAliases = alias.toAliasStrings(keyAliases);
		//unquotedKeyColumnNames = StringHelper.unQuote(keyColumnAliases);
		java.util.Set distinctColumns = new HashSet();
		checkColumnDuplication( distinctColumns, collection.getKey().getColumnIterator() );
		
		//isSet = collection.isSet();
		//isSorted = collection.isSorted();
		primitiveArray = collection.isPrimitiveArray();
		array = collection.isArray();
		
		int elementSpan = collection.getElement().getColumnSpan();
		iter = collection.getElement().getColumnIterator();
		Table table = collection.getCollectionTable();
		enableJoinedFetch = collection.getElement().getOuterJoinFetchSetting();
		elementType = collection.getElement().getType();
		
		if ( !collection.isOneToMany() ) checkColumnDuplication( distinctColumns, collection.getElement().getColumnIterator() );
		
		if ( elementType.isEntityType() ) {
			elementPersister = factory.getPersister( ( (EntityType) elementType ).getAssociatedClass() );
		}
		else {
			elementPersister = null;
		}

		qualifiedTableName = table.getQualifiedName( dialect, factory.getDefaultSchema() );
		String[] aliases = new String[elementSpan];
		elementColumnNames = new String[elementSpan];
		int j=0;
		while ( iter.hasNext() ) {
			Column col = (Column) iter.next();
			elementColumnNames[j] = col.getQuotedName(dialect);
			aliases[j] = col.getAlias();
			j++;
		}
		
		elementColumnAliases = alias.toAliasStrings(aliases);
		//unquotedElementColumnNames = StringHelper.unQuote(elementColumnAliases);
		
		Type selectColumns;
		String[] selectType;
		if ( hasIndex = collection.isIndexed() ) {
			IndexedCollection indexedCollection = (IndexedCollection) collection;
			indexType = indexedCollection.getIndex().getType();
			int indexSpan = indexedCollection.getIndex().getColumnSpan();
			iter = indexedCollection.getIndex().getColumnIterator();
			indexColumnNames = new String[indexSpan];
			String[] indexAliases = new String[indexSpan];
			int i=0;
			while ( iter.hasNext() ) {
				Column indexCol = (Column) iter.next();
				indexAliases[i] = indexCol.getAlias();
				indexColumnNames[i] = indexCol.getQuotedName(dialect);
				i++;
			}
			selectType = indexColumnNames;
			selectColumns = indexType;
			indexColumnAliases = alias.toAliasStrings(indexAliases);
			//unquotedIndexColumnNames = StringHelper.unQuote(indexColumnAliases);
			checkColumnDuplication( distinctColumns, indexedCollection.getIndex().getColumnIterator() );
		}
		else {
			indexType = null;
			indexColumnNames = null;
			indexColumnAliases = null;
			selectType = elementColumnNames;
			selectColumns = elementType;
		}
		
		if ( hasIdentifier = collection.isIdentified() ) {
			if ( collection.isOneToMany() ) throw new MappingException("one-to-many collections with identifiers are not supported");
			IdentifierCollection idColl = (IdentifierCollection) collection;
			identifierType = idColl.getIdentifier().getType();
			iter = idColl.getIdentifier().getColumnIterator();
			Column col = (Column) iter.next();
			identifierColumnName = col.getQuotedName(dialect);
			selectType = new String[] { identifierColumnName };
			selectColumns = identifierType;
			identifierColumnAlias = alias.toAliasString( col.getAlias() );
			//unquotedIdentifierColumnName = StringHelper.unQuote(identifierColumnAlias);
			unquotedIdentifierColumnName = identifierColumnAlias;
			identifierGenerator = idColl.getIdentifier().createIdentifierGenerator( factory.getDialect() );
			checkColumnDuplication( distinctColumns, idColl.getIdentifier().getColumnIterator() );
		}
		else {
			identifierType = null;
			identifierColumnName = null;
			identifierColumnAlias = null;
			unquotedIdentifierColumnName = null;
			identifierGenerator = null;
		}
		
		rowSelectColumnNames = selectType;
		rowSelectType = selectColumns;
		
		//sqlSelectString = sqlSelectString();
		sqlDeleteString = generateDeleteString();
		//sqlSelectRowString = sqlSelectRowString();
		sqlInsertRowString = generateInsertRowString();
		sqlUpdateRowString = generateUpdateRowString();
		sqlDeleteRowString = generateDeleteRowString();
		isLazy = collection.isLazy();
		
		isInverse = collection.isInverse();
		
		if ( collection.isArray() ) {
			elementClass = ( (net.sf.hibernate.mapping.Array) collection ).getElementClass();
		}
		else {
			// for non-arrays, we don't need to know the element class
			elementClass = null; //elementType.returnedClass();
		}
		
		initializer = createCollectionInitializer(factory);
		
		if ( elementType.isComponentType() ) {
			elementPropertyMapping = new CompositeElementPropertyMapping( 
				elementColumnNames, (AbstractComponentType) elementType, factory 
			);
		}
		else if ( !elementType.isEntityType() ) {
			elementPropertyMapping = new ElementPropertyMapping(elementColumnNames, elementType);
		}
		else {
			ClassPersister persister = factory.getPersister( ( (EntityType) elementType ).getAssociatedClass() );
			if ( persister instanceof PropertyMapping ) { //not all classpersisters implement PropertyMapping!
				elementPropertyMapping = (PropertyMapping) persister;
			}
			else {
				elementPropertyMapping = new ElementPropertyMapping(elementColumnNames, elementType);
			} 
		}
		
	}
	
	public void initialize(Serializable key, SessionImplementor session) throws HibernateException {
		try {
			initializer.initialize(key, session);
		}
		catch (SQLException sqle) {
			throw convert( sqle, "could not initialize collection: " + MessageHelper.infoString(this, key) );
		}
	}
	
	protected abstract CollectionInitializer createCollectionInitializer(SessionFactoryImplementor factory) throws MappingException;
	
	public CacheConcurrencyStrategy getCache() {
		return cache;
	}
	
	public boolean hasCache() {
		return cache!=null;
	}
	
	public PersistentCollectionType getCollectionType() {
		return collectionType;
	}
	
	public String getSQLWhereString(String alias) {
		return StringHelper.replace(sqlWhereStringTemplate, Template.TEMPLATE, alias);
	}
	
	public String getSQLOrderByString(String alias) {
		return StringHelper.replace(sqlOrderByStringTemplate, Template.TEMPLATE, alias);
	}
	
	public int enableJoinedFetch() {
		return enableJoinedFetch;
	}
	
	public boolean hasOrdering() {
		return hasOrder;
	}
	
	public boolean hasWhere() {
		return hasWhere; 
	}
	
	protected String getSQLDeleteString() {
		return sqlDeleteString;
	}
	
	protected String getSQLInsertRowString() {
		return sqlInsertRowString;
	}
	
	protected String getSQLUpdateRowString() {
		return sqlUpdateRowString;
	}
	
	protected String getSQLDeleteRowString() {
		return sqlDeleteRowString;
	}
	
	public Type getKeyType() {
		return keyType;
	}
	
	public Type getIndexType() {
		return indexType;
	}
	
	public Type getElementType() {
		return elementType;
	}
	
	/**
	 * Return the element class of an array, or null otherwise
	 */
	public Class getElementClass() { //needed by arrays
		return elementClass;
	}
	
	public Object readElement(ResultSet rs, Object owner, SessionImplementor session) throws HibernateException, SQLException {
		Object element = getElementType().nullSafeGet(rs, elementColumnAliases, session, owner);
		return element;
	}
	public Object readIndex(ResultSet rs, SessionImplementor session) throws HibernateException, SQLException {
		Object index = getIndexType().nullSafeGet(rs, indexColumnAliases, session, null);
		if (index==null) throw new HibernateException("null index column for collection: " + role);
		return index;
	}
	
	public Object readIdentifier(ResultSet rs, SessionImplementor session) throws HibernateException, SQLException {
		Object id = getIdentifierType().nullSafeGet(rs, unquotedIdentifierColumnName, session, null);
		if (id==null) throw new HibernateException("null identifier column for collection: " + role);
		return id;
	}
	
	public Object readKey(ResultSet rs, SessionImplementor session) throws HibernateException, SQLException {
		return getKeyType().nullSafeGet(rs, keyColumnAliases, session, null);
	}
	
	public void writeElement(PreparedStatement st, Object elt, boolean writeOrder, SessionImplementor session)
	throws HibernateException, SQLException {
		getElementType().nullSafeSet(
			st, 
			elt, 
			1+( writeOrder?0:keyColumnNames.length+(hasIndex?indexColumnNames.length:0)+(hasIdentifier?1:0) ), 
			session
		);
	}
	
	public void writeIndex(PreparedStatement st, Object idx, boolean writeOrder, SessionImplementor session)
	throws HibernateException, SQLException {
		getIndexType().nullSafeSet(st, idx, 1+keyColumnNames.length + (writeOrder?elementColumnNames.length:0), session);
	}
	
	public void writeIdentifier(PreparedStatement st, Object idx, boolean writeOrder, SessionImplementor session)
	throws HibernateException, SQLException {
		getIdentifierType().nullSafeSet(st, idx, 1+(writeOrder?elementColumnNames.length:keyColumnNames.length), session);
	}
	
	private void writeRowSelect(PreparedStatement st, Object idx, SessionImplementor session)
	throws HibernateException, SQLException {
		rowSelectType.nullSafeSet(st, idx, 1+(hasIdentifier?0:keyColumnNames.length), session);
	}
	
	public void writeKey(PreparedStatement st, Serializable id, boolean writeOrder, SessionImplementor session)
	throws HibernateException, SQLException {
		if (id==null) throw new NullPointerException("null key for collection: " + role);  //an assertion
		getKeyType().nullSafeSet(st, id, 1+(writeOrder?elementColumnNames.length:0), session);
	}
	
	public boolean isPrimitiveArray() {
		return primitiveArray;
	}
	
	public boolean isArray() {
		return array;
	}
	
	/**
	 * Generate a list of collection index, key and element columns
	 */
	public String selectFragment(String alias) {
		SelectFragment frag = new SelectFragment()
			.setSuffix(StringHelper.EMPTY_STRING) //always ignore suffix for collection columns
			.addColumns(alias, keyColumnNames, keyColumnAliases)
			.addColumns(alias, elementColumnNames, elementColumnAliases);
		if (hasIndex) frag.addColumns(alias, indexColumnNames, indexColumnAliases);
		if (hasIdentifier) frag.addColumn(alias, identifierColumnName, identifierColumnAlias);
		return frag.toFragmentString()
			.substring(2); //strip leading ','
	}
	
	/*private String sqlSelectString() {
		//we no longer have Jon Lipsky's patch to allow a Map from id's to objects
		SimpleSelect select = new SimpleSelect()
			.setTableName(qualifiedTableName)
			.addColumns(elementColumnNames);
		if (hasIndex) select.addColumns(indexColumnNames);
		select.addCondition( keyColumnNames, "=?" );
		if (hasWhere) select.addWhereToken( " and " + sqlWhereString );
		if (hasOrder) select.setOrderBy(sqlOrderByString);
		return select.toStatementString();
	}*/
	
	public String[] getIndexColumnNames() {
		return indexColumnNames;
	}
	
	public String[] getElementColumnNames() {
		return elementColumnNames;
	}
	
	public String[] getKeyColumnNames() {
		return keyColumnNames;
	}
	
	public boolean hasIndex() {
		return hasIndex;
	}
	
	public boolean isLazy() { return isLazy; }
	
	public boolean isInverse() {
		return isInverse;
	}
	
	public String getTableName() {
		return qualifiedTableName;
	}
	
	public void remove(Serializable id, SessionImplementor session) throws HibernateException {
		
		if ( !isInverse ) {
			
			if ( log.isDebugEnabled() ) log.debug( "Deleting collection: " + MessageHelper.infoString(this, id) );
			
			// Remove all the old entries
			
			try {
				PreparedStatement st = session.getBatcher().prepareBatchStatement( getSQLDeleteString() );
				
				try {
					writeKey(st, id, false, session);
					session.getBatcher().addToBatch(-1);
				}
				catch (SQLException sqle) {
					session.getBatcher().abortBatch(sqle);
					throw sqle;
				}
				
				if ( log.isDebugEnabled() ) log.debug("done deleting collection");
			}
			catch (SQLException sqle) {
				throw convert( sqle, "could not delete collection: " + MessageHelper.infoString(this, id) );
			}
			
		}
		
	}
	
	public void recreate(PersistentCollection collection, Serializable id, SessionImplementor session)
	throws HibernateException {
		
		if (!isInverse) {
			
			if ( log.isDebugEnabled() ) log.debug( "Inserting collection: " + MessageHelper.infoString(this, id) );
			
			try {
				//create all the new entries
				Iterator entries = collection.entries();
				if ( entries.hasNext() ) {
					try {
						collection.preInsert(this);
						int i=0;
						int count=0;
						while ( entries.hasNext() ) {
							Object entry = entries.next();
							if ( collection.entryExists(entry, i) ) {
								PreparedStatement st = session.getBatcher().prepareBatchStatement( getSQLInsertRowString() );
								writeKey(st, id, false, session);
								collection.writeTo(st, this, entry, i, false);
								session.getBatcher().addToBatch(1);
								collection.afterRowInsert(this, entry, i);
								count++;
							}
							i++;
						}
						if ( log.isDebugEnabled() ) log.debug("done inserting collection: " + count + " rows inserted");
					}
					catch (SQLException sqle) {
						session.getBatcher().abortBatch(sqle);
						throw sqle;
					}
					
				}
				else {
					if ( log.isDebugEnabled() ) log.debug("collection was empty");
				}
			}
			catch (SQLException sqle) {
				throw convert( sqle, "could not insert collection: " + MessageHelper.infoString(this, id) );
			}
		}
	}
	
	public void deleteRows(PersistentCollection collection, Serializable id, SessionImplementor session)
	throws HibernateException {
		
		if (!isInverse) {
			
			if ( log.isDebugEnabled() ) log.debug( "Deleting rows of collection: " + MessageHelper.infoString(this, id) );
			
			try {
				//delete all the deleted entries
				Iterator entries = collection.getDeletes(elementType);
				if ( entries.hasNext() ) {
					int count=0;
					PreparedStatement st = session.getBatcher().prepareBatchStatement( getSQLDeleteRowString() );
					
					try {
						while ( entries.hasNext() ) {
							if (!hasIdentifier) writeKey(st, id, false, session );
							writeRowSelect( st, entries.next(), session );
							session.getBatcher().addToBatch(-1);
							count++;
						}
					}
					catch (SQLException sqle) {
						session.getBatcher().abortBatch(sqle);
						throw sqle;
					}
					
					if ( log.isDebugEnabled() ) log.debug("done deleting collection rows: " + count + " deleted");
				}
				else {
					if ( log.isDebugEnabled() ) log.debug("no rows to delete");
				}
			}
			catch (SQLException sqle) {
				throw convert( sqle, "could not delete collection rows: " + MessageHelper.infoString(this, id) );
			}
		}
	}
	
	public void insertRows(PersistentCollection collection, Serializable id, SessionImplementor session)
	throws HibernateException {
		
		if (!isInverse) {
			
			if ( log.isDebugEnabled() ) log.debug( "Inserting rows of collection: " + MessageHelper.infoString(this, id) );
			
			try {
				//insert all the new entries
				Iterator entries = collection.entries();
				try {
					collection.preInsert(this);
					int i=0;
					int count=0;
					while ( entries.hasNext() ) {
						Object entry = entries.next();
						PreparedStatement st = null;
						if ( collection.needsInserting(entry, i, elementType) ) {
							if (st==null) st = session.getBatcher().prepareBatchStatement( getSQLInsertRowString() );
							writeKey(st, id, false, session);
							collection.writeTo( st, this, entry, i, false );
							session.getBatcher().addToBatch(1);
							collection.afterRowInsert(this, entry, i);
							count++;
						}
						i++;
					}
					if ( log.isDebugEnabled() ) log.debug("done inserting rows: " + count + " inserted");
				}
				catch (SQLException sqle) {
					session.getBatcher().abortBatch(sqle);
					throw sqle;
				}
			}
			catch (SQLException sqle) {
				throw convert( sqle, "could not insert collection rows: " + MessageHelper.infoString(this, id) );
			}
			
		}
	}
	
	
	public String getRole() {
		return role;
	}
	
	public Class getOwnerClass() {
		return ownerClass;
	}
	
	public IdentifierGenerator getIdentifierGenerator() {
		return identifierGenerator;
	}
	
	public Type getIdentifierType() {
		return identifierType;
	}
	
	public boolean hasOrphanDelete() {
		return hasOrphanDelete;
	}
	
	private void checkColumnDuplication(java.util.Set distinctColumns, Iterator columns) throws MappingException {
		while ( columns.hasNext() ) {
			Column col = (Column) columns.next();
			if ( !distinctColumns.add( col.getName() ) ) throw new MappingException(
				"Repeated column in mapping for collection: " +
				role +
				" column: " + 
				col.getName()
			);
		}
	}

	public Type toType(String propertyName) throws QueryException {
		if ( "index".equals(propertyName) ) return indexType;
		return elementPropertyMapping.toType(propertyName);
	}

	public String[] toColumns(String alias, String propertyName)
		throws QueryException {
		
		if ( "index".equals(propertyName) ) {
			if ( isManyToMany() ) throw new QueryException("index() function not supported for many-to-many association");
			return StringHelper.qualify(alias, indexColumnNames);
		}
		return elementPropertyMapping.toColumns(alias, propertyName);
	}

	public Type getType() {
		return elementPropertyMapping.getType(); //==elementType ??
	}

	public String[] getJoinKeyColumnNames() {
		return getKeyColumnNames();
	}

	public String getName() {
		return getRole();
	}

	public ClassPersister getElementPersister() {
		if (elementPersister==null) throw new AssertionFailure("not an association");
		return (Loadable) elementPersister;
	}

	public boolean isCollection() {
		return true;
	}

	public Serializable getCollectionSpace() {
		return getTableName();
	}

	protected abstract String generateDeleteString();
	protected abstract String generateDeleteRowString();
	protected abstract String generateUpdateRowString();
	protected abstract String generateInsertRowString();

	public void updateRows(PersistentCollection collection, Serializable id, SessionImplementor session) throws HibernateException {
		
		if (!isInverse) {
			
			if ( log.isDebugEnabled() ) log.debug("Updating rows of collection: " + role + "#" + id);
			
			//update all the modified entries
			int count = doUpdateRows(id, collection, session);
			
			if ( log.isDebugEnabled() ) log.debug("done updating rows: " + count + " updated");
		}
	}
	
	protected abstract int doUpdateRows(Serializable key, PersistentCollection collection, SessionImplementor session) throws HibernateException;

	public CollectionMetadata getCollectionMetadata() {
		return this;
	}

	protected JDBCException convert( SQLException sqlException, String message ) {
		return JDBCExceptionHelper.convert( sqlExceptionConverter, sqlException, message );
	}

	public String toString() {
		return StringHelper.root( getClass().getName() ) + '(' + role + ')';
	}

}
