//$Id: Loader.java,v 1.50 2005/01/10 03:10:25 oneovthafew Exp $
package net.sf.hibernate.loader;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.hibernate.AssertionFailure;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.QueryException;
import net.sf.hibernate.StaleObjectStateException;
import net.sf.hibernate.WrongClassException;
import net.sf.hibernate.cache.QueryCache;
import net.sf.hibernate.cache.QueryKey;
import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.collection.PersistentCollection;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.Key;
import net.sf.hibernate.engine.QueryParameters;
import net.sf.hibernate.engine.RowSelection;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.impl.MessageHelper;
import net.sf.hibernate.impl.ResultSetWrapper;
import net.sf.hibernate.impl.ColumnNameCache;
import net.sf.hibernate.persister.Loadable;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.JDBCExceptionReporter;
import net.sf.hibernate.util.StringHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import net.sf.hibernate.ScrollMode;

/**
 * Abstract superclass of object loading (and querying) strategies. This class implements 
 * useful common functionality that concrete loaders delegate to. It is not intended that this
 * functionality would be directly accessed by client code. (Hence, all methods of this class
 * are declared <tt>protected</tt> or <tt>private</tt>.) This class relies heavily upon the
 * <tt>Loadable</tt> interface, which is the contract between this class and 
 * <tt>ClassPersister</tt>s that may be loaded by it.<br>
 * <br>
 * The present implementation is able to load any number of columns of entities and at most 
 * one collection role per query.
 * 
 * @see net.sf.hibernate.persister.Loadable
 * @author Gavin King
 */
public abstract class Loader {

	private static final Log log = LogFactory.getLog( Loader.class );

	private ColumnNameCache columnNameCache;

	/**
	 * The SQL query string to be called; implemented by all subclasses
	 */
	protected abstract String getSQLString();
	/**
	 * An array of persisters of entity classes contained in each row of results;
	 * implemented by all subclasses
	 */
	protected abstract Loadable[] getPersisters();
	/**
	 * The suffix identifies a particular column of results in the SQL <tt>ResultSet</tt>;
	 * implemented by all subclasses
	 */
	protected abstract String[] getSuffixes();
	/**
	 * An array of indexes of the entity that owns a one-to-one association
	 * to the entity at the given index (-1 if there is no "owner")
	 */
	protected abstract int[] getOwners();
	/**
	 * An (optional) persister for a collection to be initialized; only collection loaders
	 * return a non-null value
	 */
	protected abstract CollectionPersister getCollectionPersister();
	/**
	 * Get the index of the entity that owns the collection, or -1
	 * if there is no owner in the query results (ie. in the case of a 
	 * collection initializer) or no collection.
	 */
	protected int getCollectionOwner() {
		return -1;
	}
	/**
	 * What lock mode does this load entities with?
	 * @param lockModes a collection of lock modes specified dynamically via the Query interface
	 */
	protected abstract LockMode[] getLockModes(Map lockModes);
	/**
	 * Append <tt>FOR UPDATE OF</tt> clause, if necessary. This
	 * empty superclass implementation merely returns its first
	 * argument.
	 */
	protected String applyLocks(String sql, Map lockModes, Dialect dialect) throws HibernateException {
		return sql;
	}
	/**
	 * Does this query return objects that might be already cached
	 * by the session, whose lock mode may need upgrading
	 */
	protected boolean upgradeLocks() {
		return false;
	}
	/**
	 * Return false is this loader is a batch entity loader
	 */
	protected boolean isSingleRowLoader() {
		return false;
	}

	/**
	 * Execute an SQL query and attempt to instantiate instances of the class mapped by the given
	 * persister from each row of the <tt>ResultSet</tt>. If an object is supplied, will attempt to
	 * initialize that object. If a collection is supplied, attempt to initialize that collection.
	 */
	private List doQueryAndInitializeNonLazyCollections(
		final SessionImplementor session,
		final QueryParameters queryParameters,
		final Object optionalObject,
		final Serializable optionalId,
		final Serializable[] optionalCollectionKeys,
		final boolean returnProxies
	) throws SQLException, HibernateException {

		session.beforeLoad();
		List result;
		try {
			result = doQuery(session, queryParameters, optionalObject, optionalId, optionalCollectionKeys, returnProxies);
		}
		finally {
			session.afterLoad();
		}
		session.initializeNonLazyCollections();
		return result;
	}

	protected Object loadSingleRow(
			final ResultSet resultSet,
			final SessionImplementor session,
			final QueryParameters queryParameters,
			boolean returnProxies) 
	throws SQLException, HibernateException {
		final int cols = getPersisters().length;
		final List hydratedObjects = cols==0 ? null : new ArrayList();
		final Object result = getRowFromResultSet(
				resultSet,
				session,
				queryParameters,
				hydratedObjects,
				null,
				null,
				new Key[cols],
				returnProxies
		);
		initializeEntitiesAndCollections(hydratedObjects, resultSet, session);
		session.initializeNonLazyCollections();
		return result;

	}

	private Object getRowFromResultSet(
			final ResultSet resultSet,
			final SessionImplementor session,
			final QueryParameters queryParameters,
			final List hydratedObjects,
			final Object optionalObject,
			final Serializable optionalId,
			final Key[] keys,
			boolean returnProxies) 
	throws SQLException, HibernateException {

		final Loadable[] persisters = getPersisters();
		final int cols = persisters.length;
		final CollectionPersister collectionPersister = getCollectionPersister();
		final int collectionOwner = getCollectionOwner();
		final String[] suffixes = getSuffixes();
		final LockMode[] lockModeArray = getLockModes( queryParameters.getLockModes() );
		final int[] owners = getOwners();

		//this is a CollectionInitializer and we are loading up a single collection
		final boolean hasCollections = collectionPersister!=null;
		//this is a query and we are loading multiple instances of the same collection role
		final boolean hasCollectionOwners = hasCollections && collectionOwner>=0;

		final Key optionalObjectKey;

		if (optionalObject!=null) {
			optionalObjectKey = new Key( optionalId, session.getPersister(optionalObject) );
		}
		else {
			optionalObjectKey = null;
		}

		for ( int i=0; i<cols; i++ ) {
			keys[i] = getKeyFromResultSet(
					i,
					persisters[i],
					(i==cols-1) ?  optionalId : null,
					resultSet,
					session
			);
			//TODO: the i==cols-1 bit depends upon subclass implementation (very bad)
		}

		if (owners!=null) registerNonExists(keys, owners, persisters, session);

		// this call is side-effecty
		Object[] row = getRow(
				resultSet,
				persisters,
				suffixes,
				keys,
				optionalObject,
				optionalObjectKey,
				lockModeArray,
				hydratedObjects,
				session
		);

		if (returnProxies) {
			// now get an existing proxy for each row element (if there is one)
			for ( int i=0; i<cols; i++ ) row[i] = session.proxyFor( persisters[i], keys[i], row[i] );
		}

		if (hasCollections) {
			Object owner = hasCollectionOwners ? row[collectionOwner] : null; //if null, owner will be retrieved from session
			Serializable key = owner!=null
					? keys[collectionOwner].getIdentifier()
					: null;
			readCollectionElement(owner, key, resultSet, session);
		}

		return getResultColumnOrRow(row, resultSet, session);

	}

	private List doQuery(
		final SessionImplementor session,
		final QueryParameters queryParameters,
		final Object optionalObject,
		final Serializable optionalId,
		final Serializable[] optionalCollectionKeys,
		boolean returnProxies
	) throws SQLException, HibernateException {

		returnProxies = returnProxies && Environment.jvmSupportsProxies();
		
		RowSelection selection = queryParameters.getRowSelection();
		int maxRows = hasMaxRows(selection) ?
			selection.getMaxRows().intValue() :
			Integer.MAX_VALUE;

		final Loadable[] persisters = getPersisters();
		final int cols = persisters.length;

		final ArrayList hydratedObjects = cols > 0 ? new ArrayList() : null;
		final List results = new ArrayList(); //new net.sf.hibernate.collections.List(this);

		final PreparedStatement st = prepareQueryStatement(
			applyLocks( getSQLString(), queryParameters.getLockModes(), session.getFactory().getDialect() ),
			queryParameters, false, session
		);
		final ResultSet rs = getResultSet(st, selection, session);

		try {

			if (optionalCollectionKeys!=null) handleEmptyCollections(optionalCollectionKeys, rs, session);

			final Key[] keys = new Key[cols]; //we can reuse it each time

			if ( log.isTraceEnabled() ) log.trace("processing result set");

			int count;
			for ( count=0; count<maxRows && rs.next(); count++ ) {
				Object result = getRowFromResultSet(
						rs,
						session,
						queryParameters,
						hydratedObjects,
						optionalObject,
						optionalId,
						keys,
						returnProxies
				);
				results.add(result);
			}

			if ( log.isTraceEnabled() ) log.trace("done processing result set (" + count + " rows)");

		}
		catch (SQLException sqle) {
			JDBCExceptionReporter.logExceptions(sqle);
			throw sqle;
		}
		finally {
			session.getBatcher().closeQueryStatement(st, rs);
		}

		initializeEntitiesAndCollections(hydratedObjects, rs, session);

		return results; //getResultList(results);

	}
	
	private void initializeEntitiesAndCollections(List hydratedObjects, Object resultSetId, SessionImplementor session) throws HibernateException {
		if ( getPersisters().length > 0 ) {
			int hydratedObjectsSize = hydratedObjects.size();
			if ( log.isTraceEnabled() ) log.trace("total objects hydrated: " + hydratedObjectsSize);
			for ( int i=0; i<hydratedObjectsSize; i++ ) session.initializeEntity( hydratedObjects.get(i) );
		}
		final CollectionPersister collectionPersister = getCollectionPersister();
		if ( collectionPersister!=null ) {
			//this is a query and we are loading multiple instances of the same collection role
			session.endLoadingCollections(collectionPersister, resultSetId);
		}
	}

	protected List getResultList(List results) throws QueryException {
		return results;
	}
	
	/**
	 * Get the actual object that is returned in the user-visible result list.
	 * This empty implementation merely returns its first argument. This is
	 * overridden by some subclasses.
	 */
	protected Object getResultColumnOrRow(Object[] row, ResultSet rs, SessionImplementor session)
	throws SQLException, HibernateException {
		return row;
	}
	
	private void registerNonExists(
		final Key[] keys, 
		final int[] owners,
		final Loadable[] persisters,
		final SessionImplementor session) {
		
		for (int i=0; i<keys.length; i++) {
			int owner = owners[i];
			if (owner>-1) {
				Key ownerKey = keys[owner];
				if ( keys[i]==null && ownerKey!=null ) {
					session.addNonExist( new Key( ownerKey.getIdentifier(), persisters[i] ) );
				}
			}
		}
	}
	
	/**
	 * Read one collection element from the current row of the JDBC result set
	 */
	private void readCollectionElement(
		final Object optionalOwner, 
		final Serializable optionalKey, 
		final ResultSet rs, 
		final SessionImplementor session)
	throws HibernateException, SQLException {
		final CollectionPersister collectionPersister = getCollectionPersister();
		final Serializable collectionRowKey = (Serializable) collectionPersister.readKey(rs, session);
		if (collectionRowKey!=null) {
			if ( log.isDebugEnabled() ) log.debug( "found row of collection: " + MessageHelper.infoString(collectionPersister, collectionRowKey) );
			Object owner = optionalOwner;
			if (owner==null) {
				owner = session.getCollectionOwner(collectionRowKey, collectionPersister);
				if (owner==null) {
					//TODO: This is assertion is disabled because there is a bug that means the
					//      original owner of a transient, uninitialized collection is not known 
					//      if the collection is re-referenced by a different object associated 
					//      with the current Session
					//throw new AssertionFailure("bug loading unowned collection");
				}
			}
			PersistentCollection rowCollection = session.getLoadingCollection(collectionPersister, collectionRowKey, rs);
			if (rowCollection!=null) rowCollection.readFrom(rs, collectionPersister, owner);
		}
		else if (optionalKey!=null) {
			if ( log.isDebugEnabled() ) log.debug( "result set contains (possibly empty) collection: " + MessageHelper.infoString(collectionPersister, optionalKey) );
			session.getLoadingCollection(collectionPersister, optionalKey, rs); //handle empty collection
		}
	}
	
	/**
	 * If this is a collection initializer, we need to tell the session that a collection
	 * is being initilized, to account for the possibility of the collection having
	 * no elements (hence no rows in the result set).
	 */
	private void handleEmptyCollections(
		final Serializable[] keys, 
		final Object resultSetId, 
		final SessionImplementor session) 
	throws HibernateException {
	
		CollectionPersister collectionPersister = getCollectionPersister();
		for ( int i=0; i<keys.length; i++ ) {
			//handle empty collections
			if ( log.isDebugEnabled() ) log.debug( "result set contains (possibly empty) collection: " + MessageHelper.infoString(collectionPersister, keys[i]) );
			session.getLoadingCollection(collectionPersister, keys[i], resultSetId);
		}
	}

	/**
	 * Read a row of <tt>Key</tt>s from the <tt>ResultSet</tt> into the given array. 
	 * Warning: this method is side-effecty. 
	 * 
	 * If an <tt>id</tt> is given, don't bother going to the <tt>ResultSet</tt>.
	 */
	private Key getKeyFromResultSet(int i, Loadable persister, Serializable id, ResultSet rs, SessionImplementor session)
	throws HibernateException, SQLException {
		
		Serializable resultId;

		// if we know there is exactly 1 row, we can skip.
		// it would be great if we could _always_ skip this;
		// it is a problem for <key-many-to-one>

		if ( isSingleRowLoader() && id!=null ) {
			resultId = id;
		}
		else {
			Type idType = persister.getIdentifierType();
			resultId = (Serializable) idType.nullSafeGet(rs, suffixedKeyColumns[i], session, null); //problematic for <key-many-to-one>!
			if ( id!=null && resultId!=null && id.equals(resultId) ) resultId = id; //use the id passed in
		}
		
		return resultId==null ?
			null :
			new Key(resultId, persister);
	}

	/**
	 * Check the version of the object in the <tt>ResultSet</tt> against
	 * the object version in the session cache, throwing an exception
	 * if the version numbers are different
	 */
	private void checkVersion(
		final int i, 
		final Loadable persister, 
		final Serializable id, 
		final Object version, 
		final ResultSet rs, 
		final SessionImplementor session)
	throws HibernateException, SQLException {

		if (version!=null) { //null version means the object is in the process of being loaded somewhere else in the ResultSet
			Type versionType = persister.getVersionType();
			Object currentVersion = versionType.nullSafeGet(rs, suffixedVersionColumNames[i], session, null);
			if ( !versionType.equals(version, currentVersion) ) {
				throw new StaleObjectStateException( persister.getMappedClass(), id );
			}
		}
	}

	/**
	 * Resolve any ids for currently loaded objects, duplications within the 
	 * <tt>ResultSet</tt>, etc. Instantiate empty objects to be initialized from the  
	 * <tt>ResultSet</tt>. Return an array of objects (a row of results) and an  
	 * array of booleans (by side-effect) that determine whether the corresponding
	 * object should be initialized.
	 */
	private Object[] getRow(
		final ResultSet rs,
		final Loadable[] persisters,
		final String[] suffixes,
		final Key[] keys,
		final Object optionalObject,
		final Key optionalObjectKey,
		final LockMode[] lockModes,
		final List hydratedObjects,
		final SessionImplementor session) 
	throws HibernateException, SQLException {

		int cols = persisters.length;

		if ( log.isDebugEnabled() ) log.debug( "result row: " + StringHelper.toString(keys) );

		Object[] rowResults = new Object[cols];

		for ( int i=0; i<cols; i++ ) {

			Object object=null;
			Key key = keys[i];

			if ( keys[i]==null ) {
				//do nothing
			}
			else {

				//If the object is already loaded, return the loaded one
				object = session.getEntity(key);
				if (object!=null) {
					//its already loaded so don't need to hydrate it
					instanceAlreadyLoaded(rs, i, persisters[i], suffixes[i], key, object, lockModes[i], session);
				}
				else {
					object = instanceNotYetLoaded(
						rs, i, persisters[i], suffixes[i], key, lockModes[i], optionalObjectKey, optionalObject, hydratedObjects, session
					);
				}

			}

			rowResults[i]=object;

		}

		return rowResults;

	}

	/**
	 * The entity instance is already in the session cache
	 */
	private void instanceAlreadyLoaded(
		final ResultSet rs, 
		final int i, 
		final Loadable persister, 
		final String suffix, 
		final Key key, 
		final Object object, 
		final LockMode lockMode, 
		final SessionImplementor session)
	throws HibernateException, SQLException {

		if ( !persister.getMappedClass().isAssignableFrom( object.getClass() ) )
			throw new WrongClassException( "loaded object was of wrong class", key.getIdentifier(), persister.getMappedClass() );

		if ( LockMode.NONE!=lockMode && upgradeLocks() ) { //no point doing this if NONE was requested

			if (
				persister.isVersioned() &&
				session.getLockMode(object).lessThan(lockMode)
				// we don't need to worry about existing version being uninitialized
				// because this block isn't called by a re-entrant load (re-entrant
				// loads _always_ have lock mode NONE)
			) {
				//we only check the version when _upgrading_ lock modes
				checkVersion(i, persister, key.getIdentifier(), session.getVersion(object), rs, session);
				//we need to upgrade the lock mode to the mode requested
				session.setLockMode(object, lockMode);
			}

		}

	}
	
	/**
	 * The entity instance is not in the session cache
	 */
	private Object instanceNotYetLoaded(
		final ResultSet rs, 
		final int i, 
		final Loadable persister, 
		final String suffix, 
		final Key key, 
		final LockMode lockMode, 
		final Key optionalObjectKey, 
		final Object optionalObject, 
		final List hydratedObjects, 
		final SessionImplementor session)
	throws HibernateException, SQLException {
		Object object;

		Class instanceClass = getInstanceClass(rs, i, persister, key.getIdentifier(), session);

		if ( optionalObjectKey!=null && key.equals(optionalObjectKey) ) {
			//its the given optional object
			object=optionalObject;
		}
		else {
			// instantiate a new instance
			object = session.instantiate( instanceClass, key.getIdentifier() );
		}

		//need to hydrate it.

		// grab its state from the ResultSet and keep it in the Session
		// (but don't yet initialize the object itself)
		// note that we acquire LockMode.READ even if it was not requested
		LockMode acquiredLockMode = lockMode==LockMode.NONE ? LockMode.READ : lockMode;
		loadFromResultSet(rs, i, object, key, suffix, acquiredLockMode, persister, session);

		//materialize associations (and initialize the object) later
		hydratedObjects.add(object);

		return object;
	}


	/**
	 * Hydrate the state an object from the SQL <tt>ResultSet</tt>, into
	 * an array or "hydrated" values (do not resolve associations yet),
	 * and pass the hydrates state to the session.
	 */
	private void loadFromResultSet(
		final ResultSet rs, 
		final int i, 
		final Object object, 
		final Key key, 
		final String suffix, 
		final LockMode lockMode, 
		final Loadable rootPersister, 
		final SessionImplementor session)
	throws SQLException, HibernateException {

		if ( log.isTraceEnabled() ) log.trace( "Initializing object from ResultSet: " + key );

		// add temp entry so that the next step is circular-reference
		// safe - only needed because some types don't take proper
		// advantage of two-phase-load (esp. components)
		session.addUninitializedEntity(key, object, lockMode);

		// Get the persister for the _subclass_
		Loadable persister = (Loadable) session.getPersister(object);

		//This is not very nice (and quite slow):
		String[][] cols = persister==rootPersister ?
			suffixedPropertyColumns[i] :
			getSuffixedPropertyAliases(persister, suffix);

		Serializable id = key.getIdentifier();
		Object[] values = hydrate(rs, id, object, persister, session, cols);
		session.postHydrate(persister, id, values, object, lockMode);

	}

	/**
	 * Determine the concrete class of an instance in the <tt>ResultSet</tt>
	 */
	private Class getInstanceClass(
		final ResultSet rs, 
		final int i, 
		final Loadable persister, 
		final Serializable id, 
		final SessionImplementor session)
	throws HibernateException, SQLException {

		Class topClass = persister.getMappedClass();

		if ( persister.hasSubclasses() ) {

			// Code to handle subclasses of topClass
			Object discriminatorValue = persister.getDiscriminatorType().nullSafeGet(
				rs, suffixedDiscriminatorColumn[i], session, null
			);

			Class result = persister.getSubclassForDiscriminatorValue(discriminatorValue);

			if (result==null) {
				//woops we got an instance of another class heirarchy branch
				throw new WrongClassException("Discriminator: " + discriminatorValue, id, topClass);
			}

			return result;

		}
		else {
			return topClass;
		}
	}

	/**
	 * Unmarshall the fields of a persistent instance from a result set,
	 * without resolving associations or collections
	 */
	private Object[] hydrate(
		final ResultSet rs, 
		final Serializable id, 
		final Object object, 
		final Loadable persister, 
		final SessionImplementor session, 
		final String[][] suffixedPropertyColumns)
	throws SQLException, HibernateException {

		if ( log.isTraceEnabled() ) log.trace("Hydrating entity: " + persister.getClassName() + '#' + id);

		Type[] types = persister.getPropertyTypes();
		Object[] values = new Object[ types.length ];

		for (int i=0; i<types.length; i++) {
			values[i] = types[i].hydrate(rs, suffixedPropertyColumns[i], session, object);
		}
		return values;
	}

	/**
	 * Advance the cursor to the first required row of the <tt>ResultSet</tt>
	 */
	private void advance(
		final ResultSet rs, 
		final RowSelection selection, 
		final SessionImplementor session) 
	throws SQLException {
		
		int firstRow = getFirstRow(selection);
		if ( firstRow!=0 ) {
			if ( session.getFactory().isScrollableResultSetsEnabled() ) {
				// we can go straight to the first required row
				rs.absolute(firstRow);
			}
			else {
				// we need to step through the rows one row at a time (slow)
				for ( int m=0; m<firstRow; m++ ) rs.next();
			}
		}
	}

	private static boolean hasMaxRows(RowSelection selection) {
		return selection!=null && selection.getMaxRows()!=null;
	}
	
	private static int getFirstRow(RowSelection selection) {
		if ( selection==null || selection.getFirstRow()==null ) {
			return 0;
		}
		else {
			return selection.getFirstRow().intValue();
		}
	}

	/**
	 * Should we pre-process the SQL string, adding a dialect-specific
	 * LIMIT clause.
	 */
	private static boolean useLimit(final RowSelection selection, final Dialect dialect) {
		return dialect.supportsLimit() && hasMaxRows(selection);
	}
	
	/**
	 * Bind positional parameter values to the <tt>PreparedStatement</tt>
	 * (these are parameters specified by a JDBC-style ?).
	 */
	protected int bindPositionalParameters(
		final PreparedStatement st, 
		final QueryParameters queryParameters, 
		final int start, 
		final SessionImplementor session) 
	throws SQLException, HibernateException {
		
		Object[] values = queryParameters.getPositionalParameterValues();
		Type[] types = queryParameters.getPositionalParameterTypes();
		int span=0;
		for ( int i=0; i<values.length; i++) {
			types[i].nullSafeSet( st, values[i], start + span, session );
			span += types[i].getColumnSpan( session.getFactory() );
		}
		return span;
	}

	/**
	 * Obtain a <tt>PreparedStatement</tt> with all parameters pre-bound.
	 * Bind JDBC-style <tt>?</tt> parameters, named parameters, and
	 * limit parameters.
	 */
	protected final PreparedStatement prepareQueryStatement(
		String sql,
		final QueryParameters queryParameters,
		final boolean scroll,
		final SessionImplementor session)
	throws SQLException, HibernateException {

		Dialect dialect = session.getFactory().getDialect();
		RowSelection selection = queryParameters.getRowSelection();
		boolean useLimit = useLimit(selection, dialect);
		boolean hasFirstRow = getFirstRow(selection)>0;
		boolean useOffset = hasFirstRow && useLimit && dialect.supportsLimitOffset();
		boolean scrollable = session.getFactory().isScrollableResultSetsEnabled() && (
			scroll || //ie. a query called using scroll()
			( hasFirstRow && !useOffset ) //we want to skip some rows at the start
		);
		ScrollMode scrollMode = scroll ? queryParameters.getScrollMode() : ScrollMode.SCROLL_INSENSITIVE;

		if (useLimit) sql = dialect.getLimitString( sql.trim(), useOffset, getMaxOrLimit(selection, dialect) );

		PreparedStatement st = session.getBatcher().prepareQueryStatement(sql, scrollable, scrollMode);

		try {

			int col=1;

			if ( useLimit && dialect.bindLimitParametersFirst() ) {
				col += bindLimitParameters(st, col, selection, session);
			}
			col += bindPositionalParameters(st, queryParameters, col, session);
			col += bindNamedParameters(st, queryParameters.getNamedParameters(), col, session);

			if ( useLimit && !dialect.bindLimitParametersFirst() ) {
				col += bindLimitParameters(st, col, selection, session);
			}

			if (!useLimit) setMaxRows(st, selection);
			if (selection!=null) {
				if ( selection.getTimeout()!=null ) {
					st.setQueryTimeout( selection.getTimeout().intValue() );
				}
				if ( selection.getFetchSize()!=null ) {
					st.setFetchSize( selection.getFetchSize().intValue() );
				}
			}
		}
		catch (SQLException sqle) {
			JDBCExceptionReporter.logExceptions(sqle);
			session.getBatcher().closeQueryStatement(st, null);
			throw sqle;
		}
		catch (HibernateException he) {
			session.getBatcher().closeQueryStatement(st, null);
			throw he;
		}

		return st;
	}
	
	/**
	 * Some dialect-specific LIMIT clauses require the maximium last row number,
	 * others require the maximum returned row count.
	 */
	private static int getMaxOrLimit(final RowSelection selection, final Dialect dialect) {
		int firstRow = getFirstRow(selection);
		int lastRow  = selection.getMaxRows().intValue();
		if ( dialect.useMaxForLimit() ) {
			return lastRow + firstRow;
		}
		else {
			return lastRow;
		}
	}
	
	/**
	 * Bind parameters needed by the dialect-specific LIMIT clause
	 */
	private int bindLimitParameters(
		final PreparedStatement st, 
		final int index, 
		final RowSelection selection, 
		final SessionImplementor session) throws SQLException {
		
		Dialect dialect = session.getFactory().getDialect();
		if ( !dialect.supportsVariableLimit() ) return 0;
		if ( !hasMaxRows(selection) ) throw new AssertionFailure("max results not set");
		int firstRow = getFirstRow(selection);
		int lastRow = getMaxOrLimit(selection, dialect);
		boolean hasFirstRow = firstRow>0 && dialect.supportsLimitOffset();
		boolean reverse = dialect.bindLimitParametersInReverseOrder();
		if (hasFirstRow) st.setInt( index + (reverse ? 1 : 0 ), firstRow );
		st.setInt( index + ( reverse || !hasFirstRow ? 0 : 1 ), lastRow );
		return hasFirstRow?2:1;
	}

	/**
	 * Use JDBC API to limit the number of rows returned by the SQL query if necessary
	 */
	private void setMaxRows(final PreparedStatement st, final RowSelection selection) throws SQLException {
		if ( hasMaxRows(selection) ) {
			st.setMaxRows( selection.getMaxRows().intValue() + getFirstRow(selection) );
		}
	}

	/**
	 * Fetch a <tt>PreparedStatement</tt>, call <tt>setMaxRows</tt> and then execute it,
	 * advance to the first result and return an SQL <tt>ResultSet</tt>
	 */
	protected final ResultSet getResultSet(
		final PreparedStatement st, 
		final RowSelection selection, 
		final SessionImplementor session)
	throws SQLException, HibernateException {

		ResultSet rs = null;
		try {
			rs = wrapResultSetIfEnabled( session.getBatcher().getResultSet( st ), session );
			Dialect dialect = session.getFactory().getDialect();
			if ( !dialect.supportsLimitOffset() || !useLimit(selection, dialect) ) {
				advance(rs, selection, session);
			}
			return rs;
		}
		catch (SQLException sqle) {
			JDBCExceptionReporter.logExceptions(sqle);
			session.getBatcher().closeQueryStatement(st, rs);
			throw sqle;
		}
	}

	private synchronized ResultSet wrapResultSetIfEnabled(final ResultSet rs, final SessionImplementor session) {
		// synchronized to avoid multi-thread access issues; defined as method synch to avoid
		// potential deadlock issues due to nature of code.
		if ( session.getFactory().isWrapResultSetsEnabled() ) {
			try {
				log.debug("Wrapping result set [" + rs + "]");
				return new ResultSetWrapper( rs, retreiveColumnNameToIndexCache( rs ) );
			}
			catch(SQLException e) {
				log.info("Error wrapping result set; using naked result set", e);
			}
		}

		return rs;
	}

	private ColumnNameCache retreiveColumnNameToIndexCache(ResultSet rs) throws SQLException {
		if ( columnNameCache == null ) {
			log.trace("Building columnName->columnIndex cache");
			columnNameCache = ColumnNameCache.construct( rs.getMetaData() );
		}

		return columnNameCache;
	}

	/**
	 * Bind named parameters to the <tt>PreparedStatement</tt>. This has an empty 
	 * implementation on this superclass and should be implemented by subclasses 
	 * (queries) which allow named parameters.
	 */
	protected int bindNamedParameters(
		PreparedStatement st, 
		Map namedParams, 
		int start, 
		SessionImplementor session)
	throws SQLException, HibernateException { 
		return 0; 
	}

	private List loadEntity(
		final SessionImplementor session,
		final Object[] values,
		final Type[] types,
		final Object optionalObject,
		final Serializable optionalId
	) throws SQLException, HibernateException {
		
		return doQueryAndInitializeNonLazyCollections(
			session, 
			new QueryParameters(types, values), 
			optionalObject, 
			optionalId, 
			null, 
			false
		);
	}

	/**
	 * Called by subclasses that load entities
	 */
	protected final List loadEntity(
		final SessionImplementor session,
		final Serializable id,
		final Type identifierType,
		final Object optionalObject,
		final Serializable optionalIdentifier
	) throws SQLException, HibernateException {
		return loadEntity(
			session, 
			new Object[] {id}, 
			new Type[] {identifierType}, 
			optionalObject, 
			optionalIdentifier
		);
	}

	/**
	 * Called by subclasses that batch load entities
	 */
	protected final List loadEntityBatch(
		final SessionImplementor session,
		final Serializable[] ids,
		final Type idType,
		final Object optionalObject,
		final Serializable optionalID
	) throws SQLException, HibernateException {

		Type[] types = new Type[ids.length];
		Arrays.fill(types, idType);
		return loadEntity(session, ids, types, optionalObject, optionalID);
	}

	/**
	 * Called by subclasses that load collections
	 */
	protected final void loadCollection(
		final SessionImplementor session, 
		final Serializable id, 
		final Type type)
	throws SQLException, HibernateException {

		loadCollection( session, new Serializable[] {id}, new Type[] {type} );
	}

	/**
	 * Called by subclasses that batch initialize collections
	 */
	protected final void loadCollectionBatch(
		final SessionImplementor session, 
		final Serializable[] ids, 
		final Type type)
	throws SQLException, HibernateException {

		Type[] idTypes = new Type[ids.length];
		Arrays.fill(idTypes, type);
		loadCollection(session, ids, idTypes);
	}

	/**
	 * Called by subclasses that initialize collections
	 */
	private void loadCollection(
		final SessionImplementor session, 
		final Serializable[] ids, 
		final Type[] types)
	throws SQLException, HibernateException {
		doQueryAndInitializeNonLazyCollections(
			session, new QueryParameters(types, ids), null, null, ids, true
		);
	}

	/**
	 * Return the query results, using the query cache, called
	 * by subclasses that implement cacheable queries
	 */
	protected List list(
		final SessionImplementor session, 
		final QueryParameters queryParameters,
		final Set querySpaces,
		final Type[] resultTypes)
	throws SQLException, HibernateException {
		
		final SessionFactoryImplementor factory = session.getFactory();
		
		final boolean cacheable = factory.isQueryCacheEnabled() && queryParameters.isCacheable();
		
		if (cacheable) {
			QueryCache queryCache = factory.getQueryCache( queryParameters.getCacheRegion() );
			QueryKey key = new QueryKey( getSQLString(), queryParameters );
			List result = null;
			if ( !queryParameters.isForceCacheRefresh() ) {
				result = queryCache.get(key, resultTypes, querySpaces, session);
			}
			if (result==null) {
				result = doList(session, queryParameters);
				queryCache.put(key, resultTypes, result, session);
			}
			return getResultList(result);
		}
		else {
			return getResultList( doList(session, queryParameters) );
		}
	}
	
	/**
	 * Actually execute a query, ignoring the query cache
	 */
	protected final List doList(final SessionImplementor session, final QueryParameters queryParameters)
	throws SQLException, HibernateException {
		return doQueryAndInitializeNonLazyCollections(
			session, queryParameters, null, null, null, true
		);
	}

	private String[][] suffixedKeyColumns;
	private String[][] suffixedVersionColumNames;
	private String[][][] suffixedPropertyColumns;
	private String[] suffixedDiscriminatorColumn;
	
	protected static final String[] NO_SUFFIX = { StringHelper.EMPTY_STRING };

	/**
	 * Calculate and cache select-clause suffixes. Must be
	 * called by subclasses after instantiation.
	 */
	protected void postInstantiate() {
		Loadable[] persisters = getPersisters();
		String[] suffixes = getSuffixes();
		suffixedKeyColumns = new String[persisters.length][];
		suffixedPropertyColumns = new String[persisters.length][][];
		suffixedVersionColumNames = new String[persisters.length][];
		suffixedDiscriminatorColumn = new String[persisters.length];
		for ( int i=0; i<persisters.length; i++ ) {
			suffixedKeyColumns[i] = persisters[i].getIdentifierAliases( suffixes[i] );
			suffixedPropertyColumns[i] = getSuffixedPropertyAliases( persisters[i], suffixes[i] );
			suffixedDiscriminatorColumn[i] = persisters[i].getDiscriminatorAlias( suffixes[i] );
			if ( persisters[i].isVersioned() ) {
				suffixedVersionColumNames[i] = suffixedPropertyColumns[i][ persisters[i].getVersionProperty() ];
			}
		}
	}

	private static String[][] getSuffixedPropertyAliases(Loadable persister, String suffix) {
		int size = persister.getPropertyNames().length;
		String[][] suffixedPropertyAliases = new String[size][];
		for ( int j=0; j<size; j++ ) {
			suffixedPropertyAliases[j] = persister.getPropertyAliases( suffix, j );
		}
		return suffixedPropertyAliases;
	}

	/**
	 * Utility method that generate 0_, 1_ suffixes. Subclasses don't
	 * necessarily need to use this algorithm, but it is intended that
	 * they will in most cases.
	 */
	protected static String[] generateSuffixes(int length) {

		if (length==0) return NO_SUFFIX;
		
		String[] suffixes = new String[length];
		for ( int i=0; i<length; i++ ) {
			suffixes[i] = Integer.toString(i) + StringHelper.UNDERSCORE;
		}
		return suffixes;
	}
	
	/**
	 * Generate a nice alias for the given class name or collection role
	 * name and unique integer. Subclasses do <em>not</em> have to use
	 * aliases of this form.
	 * @return an alias of the form <tt>foo1_</tt>
	 */
	protected static String generateAlias(String description, int unique) {
		return StringHelper.truncate( StringHelper.unqualify(description), 10 )
			.toLowerCase()
			.replace('$','_') + 
			Integer.toString(unique) +
			StringHelper.UNDERSCORE;
	}
	
}
