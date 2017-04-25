//$Id: OuterJoinLoader.java,v 1.31 2004/08/13 14:36:30 oneovthafew Exp $
package net.sf.hibernate.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.hibernate.LockMode;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.collection.QueryableCollection;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.persister.ClassPersister;
import net.sf.hibernate.persister.Joinable;
import net.sf.hibernate.persister.Loadable;
import net.sf.hibernate.persister.OuterJoinLoadable;
import net.sf.hibernate.sql.ConditionFragment;
import net.sf.hibernate.sql.DisjunctionFragment;
import net.sf.hibernate.sql.JoinFragment;
import net.sf.hibernate.type.AbstractComponentType;
import net.sf.hibernate.type.AssociationType;
import net.sf.hibernate.type.EntityType;
import net.sf.hibernate.type.OneToOneType;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.ArrayHelper;
import net.sf.hibernate.util.StringHelper;

/**
 * Implements logic for walking a tree of associated classes.
 *
 * Generates an SQL select string containing all properties of those classes.
 * Tables are joined using an ANSI-style left outer join.
 *
 * @author Gavin King, Jon Lipsky
 */
public abstract class OuterJoinLoader extends Loader {
	
	public static final int EAGER = 1;
	public static final int AUTO = 0;
	public static final int LAZY = -1;
	
	protected Loadable[] classPersisters;
	protected LockMode[] lockModeArray;
	protected int[] owners;
	protected String sql;
	protected String[] suffixes;
	private Dialect dialect;
	
	public OuterJoinLoader(Dialect dialect) {
		this.dialect=dialect;
	}
	
	/**
	 * Override on subclasses to enable or suppress joining of some associations,
	 * especially in the case of dynamic fetch settings
	 */
	protected boolean isJoinedFetchEnabled(Type type, boolean mappingDefault, String path, String table, String[] foreignKeyColumns) {
		return type.isEntityType() && mappingDefault;
	}
	
	protected int getJoinType(AssociationType type, int config, String path, String table, String[] foreignKeyColumns, SessionFactoryImplementor factory) 
	throws MappingException {
		boolean mappingDefault = isJoinedFetchEnabledByDefault(config, type, factory);
		return isJoinedFetchEnabled(type, mappingDefault, path, table, foreignKeyColumns) ?
			JoinFragment.LEFT_OUTER_JOIN :
			-1;
	}
	
	public static final class OuterJoinableAssociation {
		Joinable joinable;
		String[] foreignKeyColumns; // belong to other persister
		String subalias;
		String[] primaryKeyColumns;
		String tableName;
		int owner; // the position of the persister we came from in the list
		int joinType;
		//public String foreignKeyalias;
		boolean isOneToOne;
	}
	
	/**
	 * For an entity class, return a list of associations to be fetched by outerjoin
	 */
	protected final List walkTree(OuterJoinLoadable persister, String alias, SessionFactoryImplementor factory) throws MappingException {
		List associations = new ArrayList();
		walkClassTree(persister, alias, associations, new HashSet(), StringHelper.EMPTY_STRING, 0, factory);
		return associations;
	}
	
	/**
	 * For a collection role, return a list of associations to be fetched by outerjoin
	 */
	protected final List walkCollectionTree(QueryableCollection persister, String alias, SessionFactoryImplementor factory) 
	throws MappingException {
		return walkCollectionTree(persister, alias, new ArrayList(), new HashSet(), StringHelper.EMPTY_STRING, 0, factory);
		//TODO: when this is the entry point, we should use an INNER_JOIN for fetching the many-to-many elements!
	}
	
	/**
	 * For a collection role, return a list of associations to be fetched by outerjoin
	 */
	private final List walkCollectionTree(QueryableCollection persister, String alias, List associations, Set visitedPersisters, String path, int currentDepth, SessionFactoryImplementor factory) 
	throws MappingException {
		
		if ( persister.isOneToMany() ) {
			walkClassTree( 
				(OuterJoinLoadable) persister.getElementPersister(), 
				alias, 
				associations, 
				visitedPersisters, 
				path, 
				currentDepth, 
				factory
			);
		} 
		else {
			Type type = persister.getElementType();
			if ( type.isAssociationType() ) {
				// a many-to-many
				AssociationType etype = (AssociationType) type;
				int joinType = getJoinType( 
					etype, 
					persister.enableJoinedFetch(), 
					path, 
					persister.getTableName(), 
					persister.getElementColumnNames(), 
					factory
				);
				if (joinType>=0) {
					String[] columns = StringHelper.qualify( alias, persister.getElementColumnNames() );
					walkAssociationTree(
						etype, 
						columns, 
						persister, 
						alias, 
						associations,
						visitedPersisters,
						path,
						currentDepth,
						joinType,
						factory
					);
				}
			}
			else if ( type.isComponentType() ) {
				walkCompositeElementTree( 
					(AbstractComponentType) type, 
					persister.getElementColumnNames(), 
					persister, 
					alias, 
					associations, 
					new HashSet(), 
					path,
					currentDepth, 
					factory
				);
			}
		}
					
		return associations;
	}
	
	/**
	 * Is this an association that we cannot possibly load by outer
	 * join, no matter what the mapping or subclass specifies?
	 */
	private boolean isJoinedFetchAlwaysDisabled(OuterJoinLoadable persister, AssociationType etype, int propertyNumber) {
		//NOTE: workaround for problem with 1-to-1 defined on a subclass "accidently" picking up an object
		//TODO: really, this should use the "key" columns of the subclass table, then we don't need this check!
		//      (I *think*)
		return etype.isEntityType() && 
			( (EntityType) etype ).isOneToOne() &&
			persister.isDefinedOnSubclass(propertyNumber);
	}
	
	private final void walkAssociationTree(
		final AssociationType associationType,  
		final OuterJoinLoadable persister, 
		final int propertyNumber,
		final String alias, 
		final List associations, 
		final Set visitedPersisters, 
		final String path,
		final int currentDepth,
		final SessionFactoryImplementor factory) 
		throws MappingException {
		
		String[] aliasedForeignKeyColumns = getAliasedForeignKeyColumns( 
			persister, alias, associationType, persister.toColumns(alias, propertyNumber) 
		);
		String[] foreignKeyColumns = getForeignKeyColumns( 
			persister, associationType, persister.getSubclassPropertyColumnNames(propertyNumber)
		);
		if ( isJoinedFetchAlwaysDisabled(persister, associationType, propertyNumber) ) return;
			
		String subpath = subPath( path, persister.getSubclassPropertyName(propertyNumber) );
		int joinType = getJoinType( 
			associationType, 
			persister.enableJoinedFetch(propertyNumber), 
			subpath, 
			persister.getSubclassPropertyTableName(propertyNumber), 
			foreignKeyColumns, 
			factory 
		);
		if (joinType >= 0 ) walkAssociationTree(
			associationType, 
			aliasedForeignKeyColumns, 
			persister, 
			alias, 
			associations, 
			visitedPersisters, 
			subpath, 
			currentDepth, 
			joinType,
			factory
		);

	}
	
	/**
	 * For an entity class, add to a list of associations to be fetched by outerjoin
	 */
	private final void walkClassTree(
		final OuterJoinLoadable persister, 
		final String alias, 
		final List associations, 
		final Set visitedPersisters, 
		final String path,
		final int currentDepth,
		final SessionFactoryImplementor factory) throws MappingException {
		
		int n = persister.countSubclassProperties();
		for ( int i=0; i<n; i++ ) {
			Type type = persister.getSubclassPropertyType(i);
			if ( type.isAssociationType() ) {
				walkAssociationTree( 
					(AssociationType) type, 
					persister, 
					i,
					alias, 
					associations, 
					visitedPersisters,
					path,
					currentDepth,
					factory
				);
			}
			else if ( type.isComponentType() ) {
				walkComponentTree( 
					(AbstractComponentType) type, 
					i, 
					persister.getSubclassPropertyColumnNames(i), 
					persister.toColumns(alias, i), 
					persister, 
					alias, 
					associations, 
					visitedPersisters, 
					subPath( path, persister.getSubclassPropertyName(i) ),
					currentDepth,
					factory
				);
			}
		}
	}
	
	/**
	 * For a component, add to a list of associations to be fetched by outerjoin
	 */
	private void walkComponentTree(
		final AbstractComponentType componentType, 
		final int propertyNumber, 
		final String[] cols, 
		final String[] aliasedCols, 
		final OuterJoinLoadable persister, 
		final String alias, 
		final List associations, 
		final Set visitedPersisters, 
		final String path, 
		final int currentDepth,
		final SessionFactoryImplementor factory
	) throws MappingException {
		
		Type[] types = componentType.getSubtypes();
		String[] propertyNames = ( componentType ).getPropertyNames();
		int begin = 0;
		for ( int i=0; i <types.length; i++ ) {
			int length = types[i].getColumnSpan(factory);
			String[] range = ArrayHelper.slice(cols, begin, length);
			String[] aliasedRange = ArrayHelper.slice(aliasedCols, begin, length);
			
			if ( types[i].isAssociationType() ) {
				AssociationType associationType = (AssociationType) types[i];
		
				if ( isJoinedFetchAlwaysDisabled(persister, associationType, propertyNumber) ) return;
				
				String[] aliasedFkColumns = getAliasedForeignKeyColumns(persister, alias, associationType, aliasedRange);
				String[] fkColumns = getForeignKeyColumns(persister, associationType, range);
				String subpath = subPath( path, propertyNames[i] );
				int joinType = getJoinType( 
					associationType, 
					componentType.enableJoinedFetch(i), 
					subpath, 
					persister.getSubclassPropertyTableName(propertyNumber), 
					fkColumns, 
					factory 
				);
				if (joinType>=0) walkAssociationTree(
					associationType, 
					aliasedFkColumns, 
					persister, 
					alias, 
					associations, 
					visitedPersisters, 
					subpath, 
					currentDepth,
					joinType, 
					factory
				);
			}
			else if ( types[i].isComponentType() ) {
				String subpath = subPath( path, propertyNames[i] );
				walkComponentTree( 
					(AbstractComponentType) types[i], 
					propertyNumber, 
					range, 
					aliasedRange, 
					persister, 
					alias, 
					associations, 
					visitedPersisters, 
					subpath, 
					currentDepth,
					factory
				);
			}
			begin+=length;
		}

	}
	
	/**
	 * For a composite element, add to a list of associations to be fetched by outerjoin
	 */
	private void walkCompositeElementTree(
		AbstractComponentType compositeType, 
		String[] cols, 
		QueryableCollection persister, 
		String alias, 
		List associations, 
		Set visitedPersisters, 
		String path,
		int currentDepth,
		SessionFactoryImplementor factory
	) throws MappingException {
		
		Type[] types = compositeType.getSubtypes();
		String[] propertyNames = compositeType.getPropertyNames();
		int begin = 0;
		for ( int i=0; i <types.length; i++ ) {
			int length = types[i].getColumnSpan(factory);
			String[] range = ArrayHelper.slice(cols, begin, length);
			
			if ( types[i].isAssociationType() ) {
				AssociationType associationType = (AssociationType) types[i];
				
				//simple, because we can't have a one-to-one or a collection in a composite-element:
				String[] aliasedForeignKeyColumns = StringHelper.qualify(alias, range);

				String subpath = subPath( path, propertyNames[i] );
				int joinType = getJoinType( 
					associationType, 
					compositeType.enableJoinedFetch(i), 
					subpath, 
					persister.getTableName(), 
					range, 
					factory
				);
				if (joinType>=0) { 
					walkAssociationTree(
						associationType, 
						aliasedForeignKeyColumns, 
						persister, 
						alias, 
						associations, 
						visitedPersisters, 
						subpath, 
						currentDepth,
						joinType,
						factory
					);
				}
			}
			else if ( types[i].isComponentType() ) {
				String subpath = subPath( path, propertyNames[i] );
				walkCompositeElementTree( 
					(AbstractComponentType) types[i], 
					range, 
					persister, 
					alias, 
					associations, 
					visitedPersisters, 
					subpath, 
					currentDepth,
					factory
				);
			}
			begin+=length;
		}

	}
	
	/**
	 * Does the mapping, and Hibernate default semantics, specify that
	 * this association should be fetched by outer joining
	 */
	protected boolean isJoinedFetchEnabledByDefault(int config, AssociationType type, SessionFactoryImplementor factory) throws MappingException {
		if ( !type.isEntityType() && !type.isPersistentCollectionType() ) {
			return false;
		}
		else {
			if (config==EAGER) return true;
			if (config==LAZY) return false;
			if ( !factory.isOuterJoinedFetchEnabled() ) return false;
			if ( type.isEntityType() ) {
				EntityType entityType =(EntityType) type;
				ClassPersister persister = factory.getPersister( entityType.getAssociatedClass() );
				return !persister.hasProxy() || ( 
					entityType.isOneToOne() && 
					( (OneToOneType) entityType ).isNullable() 
				);
			}
			else {
				return false;
			}
		}
	}
	
	/**
	 * Add on association (one-to-one, many-to-one, or a collection) to a list of associations 
	 * to be fetched by outerjoin (if necessary)
	 */
	private void walkAssociationTree(
		AssociationType type, 
		String[] aliasedForeignKeyColumns, 
		Joinable persister, 
		String alias, 
		List associations, 
		Set visitedPersisters, 
		String path,
		int currentDepth,
		int joinType,
		SessionFactoryImplementor factory) 
		throws MappingException {
		
		Joinable joinable = type.getJoinable(factory);
		
		Integer maxFetchDepth = factory.getMaximumFetchDepth();
		//int currentDepth = associations.size();
		
		boolean enable = (joinType==JoinFragment.INNER_JOIN) || (                      // ALWAYS continue in the case of an inner join (indicates "not just a performance opt")
			( maxFetchDepth==null || currentDepth < maxFetchDepth.intValue() ) &&      // not too deep
			!visitedPersisters.contains(joinable) &&                                   // to avoid circularities
			( !joinable.isCollection() || !containsCollectionPersister(associations) ) // to avoid cartesian product problem
		);
		
		if (enable) {
			
			visitedPersisters.add(persister);
			OuterJoinableAssociation assoc = new OuterJoinableAssociation();
			associations.add(assoc);

			String subalias = generateTableAlias( 
				joinable.getName(), 
				associations.size(), 
				path, 
				joinable.isManyToMany() 
			); //after adding to collection!
			
			assoc.joinable = joinable;
			assoc.tableName = joinable.getTableName();
			assoc.primaryKeyColumns = type.getReferencedColumns(factory);
			assoc.foreignKeyColumns = aliasedForeignKeyColumns;
			assoc.subalias = subalias;
			assoc.owner = getPosition(alias, associations);
			assoc.isOneToOne = type.isEntityType() && 
				( (EntityType) type ).isOneToOne() && 
				!( (EntityType) type ).isUniqueKeyReference();
			assoc.joinType = joinType;
						
			if ( 
				assoc.foreignKeyColumns.length!=assoc.primaryKeyColumns.length || 
				assoc.foreignKeyColumns.length==0 
			) {
				throw new MappingException("invalid join columns for association: " + path);
			}
			
			int nextDepth = currentDepth+1;
			if ( !joinable.isCollection() ) {
				if (joinable instanceof OuterJoinLoadable) {
					walkClassTree( 
						(OuterJoinLoadable) joinable, subalias, associations, visitedPersisters, path, nextDepth, factory
					);
				}
			}
			else {
				if (joinable instanceof QueryableCollection) {
					walkCollectionTree( 
						(QueryableCollection) joinable, subalias, associations, visitedPersisters, path, nextDepth, factory
					);
				}
			}

		}
	}
	
	
	protected final String getSQLString() {
		return sql;
	}
	
	protected final Loadable[] getPersisters() {
		return classPersisters;
	}
	
	/**
	 * Generate a select list of columns containing all properties of the entity classes
	 */
	protected final String selectString(List associations, SessionFactoryImplementor factory) 
	throws MappingException {
		
		if ( associations.size()==0 ) {
			return StringHelper.EMPTY_STRING;
		}
		else {
			StringBuffer buf = new StringBuffer( associations.size() * 100 )
				.append(StringHelper.COMMA_SPACE);
			int aliasCount=0;
			for ( int i=0; i<associations.size(); i++ ) {
				OuterJoinableAssociation join = (OuterJoinableAssociation) associations.get(i);
				String selectFragment = join.joinable.selectFragment( 
						join.subalias, 
						getSuffixes()[aliasCount], 
						join.joinType==JoinFragment.LEFT_OUTER_JOIN 
				);
				buf.append(selectFragment);
				if ( join.joinable.consumesAlias() ) aliasCount++;
				if ( 
					i<associations.size()-1 && 
					!selectFragment.trim().equals(StringHelper.EMPTY_STRING) 
				) {
					buf.append(StringHelper.COMMA_SPACE);
				}
			}
			return buf.toString();
		}
	}
	
	protected String[] getSuffixes() {
		return suffixes;
	}
	
	protected String generateTableAlias(
		final String className, 
		final int n, 
		final String path, 
		final boolean isLinkTable
	) {
		return generateAlias(className, n);
	}

	protected String generateRootAlias(final String tableName) {
		return generateAlias(tableName, 0);
	}

	protected CollectionPersister getCollectionPersister() {
		return null;
	}

	/**
	 * Generate a sequence of <tt>LEFT OUTER JOIN</tt> clauses for the given associations.
	 */
	protected final JoinFragment mergeOuterJoins(List associations) {
		JoinFragment outerjoin = dialect.createOuterJoinFragment();
		Iterator iter = associations.iterator();
		while ( iter.hasNext() ) {
			OuterJoinLoader.OuterJoinableAssociation oj = (OuterJoinLoader.OuterJoinableAssociation) iter.next();
			outerjoin.addJoin(
				oj.tableName,
				oj.subalias,
				oj.foreignKeyColumns,
				oj.primaryKeyColumns,
				oj.joinType
			);
			outerjoin.addJoins(
				oj.joinable.fromJoinFragment(oj.subalias, false, true),
				oj.joinable.whereJoinFragment(oj.subalias, false, true)
			);
		}
		return outerjoin;
	}
	
	/**
	 * Count the number of instances of Joinable which are actually
	 * also instances of Loadable, or are one-to-many associations
	 */
	protected static final int countClassPersisters(List associations) {
		int result = 0;
		Iterator iter = associations.iterator();
		while ( iter.hasNext() ) {
			OuterJoinLoader.OuterJoinableAssociation oj = (OuterJoinLoader.OuterJoinableAssociation) iter.next();
			if ( oj.joinable.consumesAlias() ) result++;
		}
		return result;
	}
	
	protected static boolean containsCollectionPersister(List associations) {
		Iterator iter = associations.iterator();
		while ( iter.hasNext() ) {
			OuterJoinLoader.OuterJoinableAssociation oj = (OuterJoinLoader.OuterJoinableAssociation) iter.next();
			if ( oj.joinable.isCollection() ) return true;
		}
		return false;
	}
	
	protected LockMode[] getLockModes(Map lockModes) {
		return lockModeArray;
	}
	
	protected LockMode[] createLockModeArray(int length, LockMode lockMode) {
		LockMode[] array = new LockMode[length];
		Arrays.fill(array, lockMode);
		return array;
	}
	
	private static String subPath(String path, String property) {
		if ( path==null || path.length()==0) {
			return property;
		}
		else {
			return StringHelper.qualify(path, property);
		}
	}
	
	/**
	 * Render the where condition for a (batch) load by identifier / collection key
	 */
	protected static StringBuffer whereString(String alias, String[] columnNames, int batchSize) {
		ConditionFragment byId = new ConditionFragment()
			.setTableAlias(alias)
			.setCondition( columnNames, "?" );
		
		StringBuffer whereString = new StringBuffer();
		if (batchSize==1) {
			whereString.append( byId.toFragmentString() );
		}
		else {
			whereString.append(StringHelper.OPEN_PAREN); //TODO: unnecessary for databases with ANSI-style joins
			DisjunctionFragment df = new DisjunctionFragment();
			for ( int i=0; i<batchSize; i++ ) {
				df.addCondition(byId);
			}
			whereString.append( df.toFragmentString() );
			whereString.append(StringHelper.CLOSE_PAREN); //TODO: unnecessary for databases with ANSI-style joins
		}
		return whereString;			
	}
	
	/**
	 * Get the position of the join with the given alias in the
	 * list of joins, or -1 if not found
	 */
	private static int getPosition(String alias, List associations) {
		int result=0;
		for ( int i=0; i<associations.size(); i++ ) {
			OuterJoinableAssociation oj = (OuterJoinableAssociation) associations.get(i);
			if ( oj.joinable.consumesAlias() ) {
				if ( oj.subalias.equals(alias) ) return result;
				result++;
			}
		}
		return -1;
	}

	private static String[] getAliasedForeignKeyColumns(
		final OuterJoinLoadable persister, 
		final String alias, 
		final AssociationType associationType, 
		final String[] aliasedPropertyColumns
	) {
		if ( associationType.usePrimaryKeyAsForeignKey() ) {
			// a one-to-one association, or collection
			return StringHelper.qualify( alias, persister.getIdentifierColumnNames() );
		}
		else {
			// a many-to-one association
			return aliasedPropertyColumns;
		}
	}
	
	private static String[] getForeignKeyColumns(OuterJoinLoadable persister, AssociationType associationType, String[] propertyColumns) {
		if ( associationType.usePrimaryKeyAsForeignKey() ) {
			return persister.getIdentifierColumnNames();
		}
		else {
			return propertyColumns;
		}
	}
		
	protected int[] getOwners() {
		return owners;
	}

	protected int toOwner(OuterJoinableAssociation oj, int joins, boolean dontIgnore) {
		if (dontIgnore) {
			return (oj.owner==-1) ? joins : oj.owner; //TODO: UGLY AS SIN!
		}
		else {
			return -1;
		}
	}

}
