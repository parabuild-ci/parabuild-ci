//$Id: QueryTranslator.java,v 1.50 2005/01/10 03:10:23 oneovthafew Exp $
package net.sf.hibernate.hql;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.hibernate.AssertionFailure;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.QueryException;
import net.sf.hibernate.ScrollableResults;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.collection.QueryableCollection;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.QueryParameters;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.engine.TypedValue;
import net.sf.hibernate.impl.IteratorImpl;
import net.sf.hibernate.impl.ScrollableResultsImpl;
import net.sf.hibernate.loader.Loader;
import net.sf.hibernate.persister.Loadable;
import net.sf.hibernate.persister.PropertyMapping;
import net.sf.hibernate.persister.Queryable;
import net.sf.hibernate.sql.ForUpdateFragment;
import net.sf.hibernate.sql.JoinFragment;
import net.sf.hibernate.sql.QueryJoinFragment;
import net.sf.hibernate.sql.QuerySelect;
import net.sf.hibernate.type.EntityType;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.ArrayHelper;
import net.sf.hibernate.util.ReflectHelper;
import net.sf.hibernate.util.StringHelper;

import org.apache.commons.collections.SequencedHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * An instance of <tt>QueryTranslator</tt> translates a Hibernate
 * query string to SQL.
 */
public class QueryTranslator extends Loader {

	private final String queryString;

	private final Map typeMap = new SequencedHashMap();
	private final Map collections = new SequencedHashMap();
	private List returnedTypes = new ArrayList();
	private final List fromTypes = new ArrayList();
	private final List scalarTypes = new ArrayList();
	private final Map namedParameters = new HashMap();
	private final Map aliasNames = new HashMap();
	private final Map oneToOneOwnerNames = new HashMap();
	private final Set crossJoins = new HashSet();
	private final Map decoratedPropertyMappings = new HashMap();

	private final List scalarSelectTokens = new ArrayList();
	private final List whereTokens = new ArrayList();
	private final List havingTokens = new ArrayList();
	private final Map joins = new SequencedHashMap();
	private final List orderByTokens = new ArrayList();
	private final List groupByTokens = new ArrayList();
	private final Set querySpaces = new HashSet();
	private final Set entitiesToFetch = new HashSet();

	private Queryable[] persisters;
	private int[] owners;
	private String[] names;
	private boolean[] includeInSelect;
	private int selectLength;
	private Type[] returnTypes;
	private Type[] actualReturnTypes;
	private String[][] scalarColumnNames;
	private SessionFactoryImplementor factory;
	private Map tokenReplacements;
	private int nameCount=0;
	private int parameterCount=0;
	private boolean distinct=false;
	private boolean compiled;
	private String sqlString;
	private Class holderClass;
	private Constructor holderConstructor;
	private boolean hasScalars;
	private boolean shallowQuery;
	private QueryTranslator superQuery;
	private QueryableCollection collectionPersister;

	private int collectionOwnerColumn = -1;
	private String collectionOwnerName;
	private String fetchName;

	private String[] suffixes;

	private static final Log log = LogFactory.getLog(QueryTranslator.class);

	/**
	 * Construct a query translator
	 */
	public QueryTranslator(String queryString) {
		this.queryString = queryString;
	}

	/**
	 * Compile a subquery
	 */
	void compile(QueryTranslator superquery) throws QueryException, MappingException {
		this.factory = superquery.factory;
		this.tokenReplacements = superquery.tokenReplacements;
		this.superQuery = superquery;
		this.shallowQuery = true;
		compile();
	}


	/**
	 * Compile a "normal" query. This method may be called multiple
	 * times. Subsequent invocations are no-ops.
	 */
	public synchronized void compile(SessionFactoryImplementor factory, Map replacements, boolean scalar)
	throws QueryException, MappingException {
		if (!compiled) {
			this.factory = factory;
			this.tokenReplacements = replacements;
			this.shallowQuery = scalar;
			compile();
		}
	}

	/**
	 * Compile the query (generate the SQL).
	 */
	private void compile() throws QueryException, MappingException {

		log.trace("compiling query");
		try {
			ParserHelper.parse(
				new PreprocessingParser(tokenReplacements),
				queryString,
				ParserHelper.HQL_SEPARATORS,
				this
			);
			renderSQL();
		}
		catch (QueryException qe) {
			qe.setQueryString(queryString);
			throw qe;
		}
		catch (MappingException me) {
			throw me;
		}
		catch (Exception e) {
			log.debug("unexpected query compilation problem", e);
			QueryException qe = new QueryException("Incorrect query syntax", e);
			qe.setQueryString(queryString);
			throw qe;
		}

		postInstantiate();

		compiled=true;

	}

	public Object loadSingleRow(
	        final ResultSet resultSet,
	        final SessionImplementor session,
			final QueryParameters queryParameters,
			boolean returnProxies) throws SQLException, HibernateException {
		return super.loadSingleRow(resultSet, session, queryParameters, returnProxies);
	}

	/**
	 * Persisters for the return values of a <tt>find()</tt> style query.
	 * @return an array of <tt>ClassPersister</tt>s.
	 */
	protected Loadable[] getPersisters() {
		return persisters;
	}

	/**
	 * Types of the return values of an <tt>iterate()</tt> style query.
	 * @return an array of <tt>Type</tt>s.
	 */
	public Type[] getReturnTypes() {
		return actualReturnTypes;
	}

	private String[][] getScalarColumnNames() {
		return scalarColumnNames;
	}

	private static void logQuery(String hql, String sql) {
		if ( log.isDebugEnabled() ) {
			log.debug("HQL: " + hql);
			log.debug("SQL: " + sql);
		}
	}

	void setAliasName(String alias, String name) {
		aliasNames.put(alias, name);
	}
	String getAliasName(String alias) {
		String name = (String) aliasNames.get(alias);
		if (name==null) {
			if (superQuery!=null) {
				name = superQuery.getAliasName(alias);
			}
			else {
				name = alias;
			}
		}
		return name;
	}

	String unalias(String path) {
		String alias = StringHelper.root(path);
		String name = getAliasName(alias);
		if (name!=null) {
			return name + path.substring( alias.length() );
		}
		else {
			return path;
		}
	}

	void addEntityToFetch(String name, String oneToOneOwnerName) {
		addEntityToFetch(name);
		if (oneToOneOwnerName!=null) oneToOneOwnerNames.put(name, oneToOneOwnerName);
	}

	void addEntityToFetch(String name) {
		entitiesToFetch.add(name);
	}

	public String getSQLString() {
		return sqlString;
	}

	private int nextCount() {
		return (superQuery==null) ? nameCount++ : superQuery.nameCount++;
	}

	String createNameFor(Class type) {
		return generateAlias( type.getName(), nextCount()  );
	}

	String createNameForCollection(String role) {
		return generateAlias( role, nextCount() );
	}

	Class getType(String name) {
		Class type = (Class) typeMap.get(name);
		if ( type==null && superQuery!=null ) type = superQuery.getType(name);
		return type;
	}

	String getRole(String name) {
		String role = (String) collections.get(name);
		if ( role==null && superQuery!=null ) role = superQuery.getRole(name);
		return role;
	}

	boolean isName(String name) {
		return aliasNames.containsKey(name) ||
			typeMap.containsKey(name) ||
			collections.containsKey(name) || (
				superQuery!=null && superQuery.isName(name)
			);
	}

	PropertyMapping getPropertyMapping(String name) throws QueryException {
		PropertyMapping decorator = getDecoratedPropertyMapping(name);
		if (decorator!=null) return decorator;

		Class type = getType(name);
		if (type==null) {
			String role = getRole(name);
			if (role==null) {
				throw new QueryException("alias not found: " + name);
			}
			return getCollectionPersister(role); //.getElementPropertyMapping();
		}
		else {
			Queryable persister = getPersister(type);
			if (persister==null) throw new QueryException( "persistent class not found: " + type.getName() );
			return persister;
		}
	}

	PropertyMapping getDecoratedPropertyMapping(String name) {
		return (PropertyMapping) decoratedPropertyMappings.get(name);
	}
	void decoratePropertyMapping(String name, PropertyMapping mapping) {
		decoratedPropertyMappings.put(name, mapping);
	}

	Queryable getPersisterForName(String name) throws QueryException {
		Class type = getType(name);
		Queryable persister = getPersister(type);
		if (persister==null) throw new QueryException( "persistent class not found: " + type.getName() );
		return persister;
	}

	Queryable getPersisterUsingImports(String className) {
		try {
			return (Queryable) factory.getPersister( factory.getImportedClassName(className) );
		}
		catch (MappingException me) {
			return null;
		}
	}

	Queryable getPersister(Class clazz) throws QueryException {
		try {
			return (Queryable) factory.getPersister(clazz);
		}
		catch (Exception e) {
			throw new QueryException( "persistent class not found: " + clazz.getName() );
		}
	}

	QueryableCollection getCollectionPersister(String role) throws QueryException {
		try {
			return (QueryableCollection) factory.getCollectionPersister(role);
		}
		catch (ClassCastException cce) {
			throw new QueryException( "collection role is not queryable: " + role );
		}
		catch (Exception e) {
			throw new QueryException( "collection role not found: " + role );
		}
	}

	void addType(String name, Class type) {
		typeMap.put(name, type);
	}

	void addCollection(String name, String role) {
		collections.put(name, role);
	}

	void addFrom(String name, Class type, JoinFragment join) {
		addType(name, type);
		addFrom(name, join);
	}

	void addFromCollection(String name, String collectionRole, JoinFragment join) {
		//register collection role
		addCollection(name, collectionRole);
		addJoin(name, join);
	}

	void addFrom(String name, JoinFragment join) {
		fromTypes.add(name);
		addJoin(name, join);
	}

	void addFromClass(String name, Queryable classPersister) {
		JoinFragment ojf = createJoinFragment(false);
		ojf.addCrossJoin( classPersister.getTableName(), name );
		crossJoins.add(name);
		addFrom(name, classPersister.getMappedClass(), ojf);
	}

	void addSelectClass(String name) {
		returnedTypes.add(name);
	}

	void addSelectScalar(Type type) {
		scalarTypes.add(type);
	}

	void appendWhereToken(String token) {
		whereTokens.add(token);
	}

	void appendHavingToken(String token) {
		havingTokens.add(token);
	}

	void appendOrderByToken(String token) {
		orderByTokens.add(token);
	}

	void appendGroupByToken(String token) {
		groupByTokens.add(token);
	}

	void appendScalarSelectToken(String token) {
		scalarSelectTokens.add(token);
	}

	void appendScalarSelectTokens(String[] tokens) {
		scalarSelectTokens.add(tokens);
	}

	void addJoin(String name, JoinFragment newjoin) {
		JoinFragment oldjoin = (JoinFragment) joins.get(name);
		if (oldjoin==null) {
			joins.put(name, newjoin);
		}
		else {
			oldjoin.addCondition( newjoin.toWhereFragmentString() );
			if ( oldjoin.toFromFragmentString().indexOf( newjoin.toFromFragmentString().trim() ) < 0 ) {
				throw new AssertionFailure("bug in query parser: " + queryString);
				//TODO: what about the toFromFragmentString() ????
			}
		}
	}

	void addNamedParameter(String name) {
		if (superQuery!=null) superQuery.addNamedParameter(name);
		Integer loc = new Integer(parameterCount++);
		Object o = namedParameters.get(name);
		if (o==null) {
			namedParameters.put(name, loc);
		}
		else if (o instanceof Integer) {
			ArrayList list = new ArrayList(4);
			list.add(o);
			list.add(loc);
			namedParameters.put(name, list);
		}
		else {
			( (ArrayList) o ).add(loc);
		}
	}

	protected int[] getNamedParameterLocs(String name) throws QueryException {
		Object o = namedParameters.get(name);
		if (o==null) {
			QueryException qe = new QueryException("Named parameter does not appear in Query: " + name);
			qe.setQueryString(queryString);
			throw qe;
		}
		if (o instanceof Integer) {
			return new int[] { ( (Integer) o ).intValue() };
		}
		else {
			return ArrayHelper.toIntArray( (ArrayList) o );
		}
	}

	private static String scalarName(int x, int y) {
		return new StringBuffer()
			.append('x')
			.append(x)
			.append(StringHelper.UNDERSCORE)
			.append(y)
			.append(StringHelper.UNDERSCORE)
			.toString();
	}

	private void renderSQL() throws QueryException, MappingException {

		final int rtsize;
		if ( returnedTypes.size()==0 && scalarTypes.size()==0 ) {
			//ie no select clause in HQL
			returnedTypes = fromTypes;
			rtsize = returnedTypes.size();
		}
		else {
			rtsize = returnedTypes.size();
			Iterator iter = entitiesToFetch.iterator();
			while ( iter.hasNext() ) {
				returnedTypes.add( iter.next() );
			}
		}
		int size = returnedTypes.size();
		persisters = new Queryable[size];
		names = new String[size];
		owners = new int[size];
		suffixes = new String[size];
		includeInSelect = new boolean[size];
		for ( int i=0; i<size; i++ ) {
			String name = (String) returnedTypes.get(i);
			//if ( !isName(name) ) throw new QueryException("unknown type: " + name);
			persisters[i] = getPersisterForName(name);
			// TODO: cannot use generateSuffixes() - it handles the initial suffix differently.
			suffixes[i] = (size==1) ?
				StringHelper.EMPTY_STRING :
				Integer.toString(i) + StringHelper.UNDERSCORE;
			names[i] = name;
			includeInSelect[i] = !entitiesToFetch.contains(name);
			if ( includeInSelect[i] ) selectLength++;
			if ( name.equals(collectionOwnerName) ) collectionOwnerColumn = i;
			String oneToOneOwner = (String) oneToOneOwnerNames.get(name);
			owners[i] = (oneToOneOwner==null) ? -1 : returnedTypes.indexOf(oneToOneOwner);
		}

		if ( ArrayHelper.isAllNegative(owners) ) owners = null;

		String scalarSelect = renderScalarSelect(); //Must be done here because of side-effect! yuck...

		int scalarSize = scalarTypes.size();
		hasScalars = scalarTypes.size()!=rtsize;

		returnTypes = new Type[scalarSize];
		for ( int i=0; i<scalarSize; i++ ) {
			returnTypes[i] = (Type) scalarTypes.get(i);
		}

		QuerySelect sql = new QuerySelect( factory.getDialect() );
		sql.setDistinct(distinct);

		if ( !shallowQuery ) {
			renderIdentifierSelect(sql);
			renderPropertiesSelect(sql);
		}

		if ( collectionPersister!=null ) {
			sql.addSelectFragmentString( collectionPersister.selectFragment(fetchName) );
		}

		if ( hasScalars || shallowQuery ) sql.addSelectFragmentString(scalarSelect);

		//TODO: for some dialiects it would be appropriate to add the renderOrderByPropertiesSelect() to other select strings
		mergeJoins( sql.getJoinFragment() );

		sql.setWhereTokens( whereTokens.iterator() );

		sql.setGroupByTokens( groupByTokens.iterator() );
		sql.setHavingTokens( havingTokens.iterator() );
		sql.setOrderByTokens( orderByTokens.iterator() );

		if ( collectionPersister!=null && collectionPersister.hasOrdering() ) {
			sql.addOrderBy( collectionPersister.getSQLOrderByString(fetchName) );
		}

		scalarColumnNames = generateColumnNames(returnTypes, factory);

		// initialize the Set of queried identifier spaces (ie. tables)
		Iterator iter = collections.values().iterator();
		while ( iter.hasNext() ) {
			CollectionPersister p = getCollectionPersister( (String) iter.next() );
			addQuerySpace( p.getCollectionSpace() );
		}
		iter = typeMap.keySet().iterator();
		while ( iter.hasNext() ) {
			Queryable p = getPersisterForName( (String) iter.next() );
			Serializable[] spaces = p.getPropertySpaces();
			for ( int i=0; i<spaces.length; i++ ) addQuerySpace( spaces[i] );
		}

		sqlString = sql.toQueryString();

		if (holderClass!=null) holderConstructor = ReflectHelper.getConstructor(holderClass, returnTypes);

		if (hasScalars) {
			actualReturnTypes = returnTypes;
		}
		else {
			actualReturnTypes = new Type[selectLength];
			int j=0;
			for (int i=0; i<persisters.length; i++) {
				if ( includeInSelect[i] ) actualReturnTypes[j++] = Hibernate.entity( persisters[i].getMappedClass() );
			}
		}

	}

	private void renderIdentifierSelect(QuerySelect sql) {
		int size = returnedTypes.size();

		for ( int k=0; k<size; k++ ) {
			String name = (String) returnedTypes.get(k);
			String suffix = size==1 ?
				StringHelper.EMPTY_STRING :
				Integer.toString(k) + StringHelper.UNDERSCORE;
			sql.addSelectFragmentString( persisters[k].identifierSelectFragment(name, suffix) );
		}

	}

	/*private String renderOrderByPropertiesSelect() {
		StringBuffer buf = new StringBuffer(10);

		//add the columns we are ordering by to the select ID select clause
		Iterator iter = orderByTokens.iterator();
		while ( iter.hasNext() ) {
			String token = (String) iter.next();
			if ( token.lastIndexOf(".") > 0 ) {
				//ie. it is of form "foo.bar", not of form "asc" or "desc"
				buf.append(StringHelper.COMMA_SPACE).append(token);
			}
		}

		return buf.toString();
	}*/

	private void renderPropertiesSelect(QuerySelect sql) {
		int size = returnedTypes.size();
		for ( int k=0; k<size; k++ ) {
			String suffix = (size==1) ?
				StringHelper.EMPTY_STRING :
				Integer.toString(k) + StringHelper.UNDERSCORE;
			String name = (String) returnedTypes.get(k) ;
			sql.addSelectFragmentString( persisters[k].propertySelectFragment(name, suffix) );
		}
	}

	/**
	 * WARNING: side-effecty
	 */
	private String renderScalarSelect() {

		boolean isSubselect = superQuery!=null;

		StringBuffer buf = new StringBuffer(20);

		if ( scalarTypes.size()==0 ) {
			//ie. no select clause
			int size = returnedTypes.size();
			for ( int k=0; k<size; k++ ) {

				scalarTypes.add( Hibernate.entity(
					persisters[k].getMappedClass()
				) );

				String[] idColumnNames = persisters[k].getIdentifierColumnNames();
				for (int i=0; i<idColumnNames.length; i++) {
					buf.append( returnedTypes.get(k) ).append(StringHelper.DOT).append( idColumnNames[i] );
					if (!isSubselect) buf.append(" as ").append( scalarName(k, i) );
					if (i!=idColumnNames.length-1 || k!=size-1 ) buf.append(StringHelper.COMMA_SPACE);
				}

			}

		}
		else {
			//there _was_ a select clause
			Iterator iter = scalarSelectTokens.iterator();
			int c=0;
			boolean nolast=false; //real hacky...
			int parenCount = 0; // used to count the nesting of parentheses
			while ( iter.hasNext() ) {
				Object next = iter.next();
				if (next instanceof String) {
					String token = (String) next;

					if (StringHelper.OPEN_PAREN.equals(token)) {
						parenCount++;
					}
					else if (StringHelper.CLOSE_PAREN.equals(token)) {
						parenCount--;
					}

					String lc = token.toLowerCase();
					if ( lc.equals(StringHelper.COMMA_SPACE) ) {
						if (nolast) {
							nolast=false;
						}
						else {
							if (!isSubselect && parenCount == 0) {
								buf.append(" as ").append( scalarName(c++, 0) );
							}
						}
					}
					buf.append(token);
					if ( lc.equals("distinct") || lc.equals("all") ) {
						buf.append(' ');
					}
				}
				else {
					nolast=true;
					String[] tokens = (String[]) next;
					for ( int i=0; i<tokens.length; i++ ) {
						buf.append(tokens[i]);
						if (!isSubselect) buf.append(" as ").append( scalarName(c, i) );
						if (i!=tokens.length-1) buf.append(StringHelper.COMMA_SPACE);
					}
					c++;
				}
			}
			if (!isSubselect && !nolast) buf.append(" as ").append( scalarName(c++, 0) );

		}

		return buf.toString();
	}

	private void mergeJoins(JoinFragment ojf) throws MappingException, QueryException {

		Iterator iter = joins.entrySet().iterator();
		while ( iter.hasNext() ) {
			Map.Entry me = (Map.Entry) iter.next();
			String name = (String) me.getKey();
			JoinFragment join = (JoinFragment) me.getValue();

			if ( typeMap.containsKey(name) ) {

				Queryable p = getPersisterForName(name);
				boolean includeSubclasses = returnedTypes.contains(name) && !isShallowQuery();

				boolean isCrossJoin = crossJoins.contains(name);
				ojf.addFragment(join);
				ojf.addJoins(
					p.fromJoinFragment(name, isCrossJoin, includeSubclasses),
					p.queryWhereFragment(name, isCrossJoin, includeSubclasses)
				);

			}
			else if ( collections.containsKey(name) ) {
				ojf.addFragment(join);
			}
			else {
				//name from a super query (a bit inelegant that it shows up here)
			}

		}

	}

	public final Set getQuerySpaces() {
		return querySpaces;
	}

	/**
	 * Is this query called by scroll() or iterate()?
	 * @return true if it is, false if it is called by find() or list()
	 */
	boolean isShallowQuery() {
		return shallowQuery;
	}

	void addQuerySpace(Serializable table) {
		querySpaces.add(table);
		if (superQuery!=null) superQuery.addQuerySpace(table);
	}

	void setDistinct(boolean distinct) {
		this.distinct = distinct;
	}

	boolean isSubquery() {
		return superQuery!=null;
	}

	/**
	 * Overrides method from Loader
	 */
	protected CollectionPersister getCollectionPersister() {
		return collectionPersister;
	}

	void setCollectionToFetch(String role, String name, String ownerName, String entityName) throws QueryException {
		fetchName = name;
		collectionPersister = getCollectionPersister(role);
		collectionOwnerName = ownerName;
		if ( collectionPersister.getElementType().isEntityType() ) addEntityToFetch(entityName);
	}

	protected String[] getSuffixes() {
		return suffixes;
	}

	/**
	 * Used for collection filters
	 */
	protected void addFromAssociation(final String elementName, final String collectionRole) throws QueryException {
		//q.addCollection(collectionName, collectionRole);
		Type collectionElementType = getCollectionPersister(collectionRole).getElementType();
		if ( !collectionElementType.isEntityType() ) throw new QueryException(
			"collection of values in filter: " + elementName
		);

		QueryableCollection persister = getCollectionPersister(collectionRole);
		String[] keyColumnNames = persister.getKeyColumnNames();
		//if (keyColumnNames.length!=1) throw new QueryException("composite-key collection in filter: " + collectionRole);

		String collectionName;
		JoinFragment join = createJoinFragment(false);
		collectionName = persister.isOneToMany() ?
			elementName :
			createNameForCollection(collectionRole);
		join.addCrossJoin( persister.getTableName(), collectionName );
		if ( !persister.isOneToMany() ) {
			//many-to-many
			addCollection(collectionName, collectionRole);

			Queryable p = (Queryable) persister.getElementPersister();
			String[] idColumnNames =  p.getIdentifierColumnNames();
			String[] eltColumnNames = persister.getElementColumnNames();
			join.addJoin(
				p.getTableName(),
				elementName,
				StringHelper.qualify(collectionName, eltColumnNames),
				idColumnNames,
				JoinFragment.INNER_JOIN
			);
		}
		join.addCondition(collectionName, keyColumnNames, " = ?");
		if ( persister.hasWhere() ) join.addCondition( persister.getSQLWhereString(collectionName) );
		EntityType elemType = (EntityType) collectionElementType;
		addFrom(elementName, elemType.getAssociatedClass(), join);

	}

	private final Map pathAliases = new HashMap();
	private final Map pathJoins = new HashMap();

	String getPathAlias(String path) {
		return (String) pathAliases.get(path);
	}

	JoinFragment getPathJoin(String path) {
		return (JoinFragment) pathJoins.get(path);
	}

	void addPathAliasAndJoin(String path, String alias, JoinFragment join) {
		pathAliases.put(path, alias);
		pathJoins.put( path, join.copy() );
	}

	protected int bindNamedParameters(PreparedStatement ps, Map namedParams, int start, SessionImplementor session)
	throws SQLException, HibernateException {
		if (namedParams!=null) {
			// assumes that types are all of span 1
			Iterator iter = namedParams.entrySet().iterator();
			int result = 0;
			while ( iter.hasNext() ) {
				Map.Entry e = (Map.Entry) iter.next();
				String name = (String) e.getKey();
				TypedValue typedval = (TypedValue) e.getValue();
				int[] locs = getNamedParameterLocs(name);
				for ( int i=0; i<locs.length; i++ ) {
					typedval.getType().nullSafeSet( ps, typedval.getValue(), locs[i] + start, session );
				}
				result += locs.length;
			}
			return result;
		}
		else {
			return 0;
		}
	}

	public List list(SessionImplementor session, QueryParameters queryParameters)
	throws HibernateException, SQLException {
		logQuery(queryString, sqlString);
		return list(session, queryParameters, getQuerySpaces(), actualReturnTypes);
	}

	/**
	 * Return the query results as an iterator
	 */
	public Iterator iterate(QueryParameters queryParameters, SessionImplementor session)
	throws HibernateException, SQLException {
		logQuery(queryString, sqlString);

		PreparedStatement st = prepareQueryStatement(
			applyLocks( getSQLString(), queryParameters.getLockModes(), session.getFactory().getDialect() ),
			queryParameters, false, session
		);
		ResultSet rs = getResultSet(st, queryParameters.getRowSelection(), session);
		return new IteratorImpl( rs, st, session, returnTypes, getScalarColumnNames(), holderClass );

	}

	/**
	 * Return the query results, as an instance of <tt>ScrollableResults</tt>
	 */
	public ScrollableResults scroll(QueryParameters queryParameters, SessionImplementor session)
	throws HibernateException, SQLException {
		logQuery(queryString, sqlString);

		PreparedStatement st = prepareQueryStatement(
			applyLocks( getSQLString(), queryParameters.getLockModes(), session.getFactory().getDialect() ),
			queryParameters, true, session
		);
		ResultSet rs = getResultSet(st, queryParameters.getRowSelection(), session);
		return new ScrollableResultsImpl( rs, st, session, this, queryParameters, returnTypes, holderClass );

	}

	/**
	 * Handle Hibernate "implicit" polymorphism, by translating the query string into
	 * several "concrete" queries against mapped classes.
	 */
	public static String[] concreteQueries(String query, SessionFactoryImplementor factory) {

		//scan the query string for class names appearing in the from clause and replace
		//with all persistent implementors of the class/interface, returning multiple
		//query strings (make sure we don't pick up a class in the select clause!)

		//TODO: this is one of the ugliest and most fragile pieces of code in Hibernate....

		String[] tokens = StringHelper.split( ParserHelper.WHITESPACE + "(),", query, true );
		if (tokens.length==0) return new String[] { query }; // just especially for the trivial collection filter
		ArrayList placeholders = new ArrayList();
		ArrayList replacements = new ArrayList();
		StringBuffer templateQuery = new StringBuffer(40);
		int count=0;
		String last = null;
		int nextIndex = 0;
		String next = null;
		templateQuery.append( tokens[0] );
		for ( int i=1; i<tokens.length; i++ ) {

			//update last non-whitespace token, if necessary
			if ( !ParserHelper.isWhitespace( tokens[i-1] ) ) last = tokens[i-1].toLowerCase();

			String token = tokens[i];
			if ( !ParserHelper.isWhitespace(token) || last==null ) {

				//scan for next non-whitespace token
				if (nextIndex<=i) {
					for ( nextIndex=i+1; nextIndex<tokens.length; nextIndex++ ) {
						next = tokens[nextIndex].toLowerCase();
						if ( !ParserHelper.isWhitespace(next) ) break;
					}
				}

				if (
					Character.isJavaIdentifierStart( token.charAt(0) ) && (
						( BEFORE_CLASS_TOKENS.contains(last) && !NOT_AFTER_CLASS_TOKENS.contains(next) ) ||
						"class".equals(last)
					)
				){
					Class clazz= getImportedClass(token, factory);
					if (clazz!=null) {
						String[] implementors = factory.getImplementors(clazz);
						String placeholder = "$clazz" + count++ + "$";
						if ( implementors!=null ) {
							placeholders.add(placeholder);
							replacements.add(implementors);
						}
						token = placeholder; // Note this!!
					}
				}

			}

			templateQuery.append(token);

		}
		String[] results = StringHelper.multiply( templateQuery.toString(), placeholders.iterator(), replacements.iterator() );
		if (results.length==0) log.warn("no persistent classes found for query class: " + query);
		return results;
	}

	private static final Set BEFORE_CLASS_TOKENS = new HashSet();
	private static final Set NOT_AFTER_CLASS_TOKENS = new HashSet();
	static {
		BEFORE_CLASS_TOKENS.add("from");
		//beforeClassTokens.add("new"); DEFINITELY DON'T HAVE THIS!!
		BEFORE_CLASS_TOKENS.add(",");
		NOT_AFTER_CLASS_TOKENS.add("in");
		//notAfterClassTokens.add(",");
		NOT_AFTER_CLASS_TOKENS.add("from");
		NOT_AFTER_CLASS_TOKENS.add(")");
	}

	Class getImportedClass(String name) {
		return getImportedClass(name, factory);
	}

	private static Class getImportedClass(String name, SessionFactoryImplementor factory) {
		try {
			return ReflectHelper.classForName( factory.getImportedClassName(name) );
		}
		catch (Throwable e) {
			return null;
		}
	}

	private static String[][] generateColumnNames(Type[] types, SessionFactoryImplementor f) throws MappingException {
		String[][] columnNames = new String[types.length][];
		for (int i=0; i<types.length; i++) {
			int span = types[i].getColumnSpan(f);
			columnNames[i] = new String[span];
			for ( int j=0; j<span; j++ ) {
				columnNames[i][j] = scalarName(i, j);
			}
		}
		return columnNames;
	}

	protected Object getResultColumnOrRow(Object[] row, ResultSet rs, SessionImplementor session)
	throws SQLException, HibernateException {

		row = toResultRow(row);
		if (hasScalars) {
			String[][] scalarColumns = getScalarColumnNames();
			int queryCols = returnTypes.length;
			if ( holderClass==null && queryCols==1 ) {
				return returnTypes[0].nullSafeGet( rs, scalarColumns[0], session, null );
			}
			else {
				row = new Object[queryCols];
				for ( int i=0; i<queryCols; i++ )
				row[i] = returnTypes[i].nullSafeGet( rs, scalarColumns[i], session, null );
				return row;
			}
		}
		else if (holderClass==null) {
			return row.length==1 ? row[0] : row;
		}
		else {
			return row;
		}

	}

	protected List getResultList(List results) throws QueryException {
		if (holderClass!=null) {
			for (int i=0; i<results.size(); i++) {
				Object[] row = (Object[]) results.get(i);
				try {
					results.set( i, holderConstructor.newInstance(row) );
				}
				catch (Exception e) {
					throw new QueryException("could not instantiate: " + holderClass, e);
				}
			}
		}
		return results;
	}

	private Object[] toResultRow(Object[] row) {
		if (selectLength==row.length) {
			return row;
		}
		else {
			Object[] result = new Object[selectLength];
			int j=0;
			for (int i=0; i<row.length; i++) {
				if ( includeInSelect[i] ) result[j++] = row[i];
			}
			return result;
		}
	}

	QueryJoinFragment createJoinFragment(boolean useThetaStyleInnerJoins) {
		return new QueryJoinFragment( factory.getDialect(), useThetaStyleInnerJoins );
	}

	void setHolderClass(Class clazz) {
		holderClass = clazz;
	}

	protected LockMode[] getLockModes(Map lockModes) {
		// unfortunately this stuff can't be cached because
		// it is per-invocation, not constant for the
		// QueryTranslator instance
		HashMap nameLockModes = new HashMap();
		if (lockModes!=null) {
			Iterator iter = lockModes.entrySet().iterator();
			while ( iter.hasNext() ) {
				Map.Entry me = (Map.Entry) iter.next();
				nameLockModes.put(
					getAliasName( (String) me.getKey() ),
					me.getValue()
				);
			}
		}
		LockMode[] lockModeArray = new LockMode[names.length];
		for ( int i=0; i<names.length; i++ ) {
			LockMode lm = (LockMode) nameLockModes.get( names[i] );
			if (lm==null) lm = LockMode.NONE;
			lockModeArray[i] = lm;
		}
		return lockModeArray;
	}

	protected String applyLocks(String sql, Map lockModes, Dialect dialect) throws QueryException {
		// can't cache this stuff either (per-invocation)
		if ( lockModes==null || lockModes.size()==0 ) {
			return sql;
		}
		else {
			Map aliasedLockModes = new HashMap();
			Iterator iter = lockModes.entrySet().iterator();
			while ( iter.hasNext() ) {
				Map.Entry me = (Map.Entry) iter.next();
				aliasedLockModes.put( getAliasName( (String) me.getKey() ), me.getValue() );
			}
			return sql + new ForUpdateFragment(aliasedLockModes).toFragmentString(dialect);

		}
	}

	protected boolean upgradeLocks() {
		return true;
	}

	protected int getCollectionOwner() {
		return collectionOwnerColumn;
	}

	protected void setFactory(SessionFactoryImplementor factory) {
		this.factory = factory;
	}

	protected SessionFactoryImplementor getFactory() {
		return factory;
	}

	protected boolean isCompiled() {
		return compiled;
	}

	public String toString() {
		return queryString;
	}

	protected int[] getOwners() {
		return owners;
	}

}
