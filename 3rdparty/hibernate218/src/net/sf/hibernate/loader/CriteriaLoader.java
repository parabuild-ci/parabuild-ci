//$Id: CriteriaLoader.java,v 1.13 2004/07/18 04:48:51 oneovthafew Exp $
package net.sf.hibernate.loader;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.hibernate.Criteria;
import net.sf.hibernate.FetchMode;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.QueryException;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.QueryParameters;
import net.sf.hibernate.engine.RowSelection;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.engine.TypedValue;
import net.sf.hibernate.expression.Order;
import net.sf.hibernate.impl.CriteriaImpl;
import net.sf.hibernate.persister.OuterJoinLoadable;
import net.sf.hibernate.persister.Queryable;
import net.sf.hibernate.sql.ForUpdateFragment;
import net.sf.hibernate.sql.JoinFragment;
import net.sf.hibernate.type.AssociationType;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.ArrayHelper;

//TODO: this class depends directly upon CriteriaImpl, in the impl package ... add a CriteriaImplementor interface
/**
 * A <tt>Loader</tt> for <tt>Criteria</tt> queries. Note that criteria queries are
 * more like multi-object <tt>load()</tt>s than like HQL queries.
 * 
 * @author Gavin King
 */
public class CriteriaLoader extends AbstractEntityLoader {
	
	private CriteriaImpl criteria;
	private Set querySpaces = new HashSet();
	private Type[] resultTypes;

	public CriteriaLoader(OuterJoinLoadable persister, SessionFactoryImplementor factory, CriteriaImpl criteria) throws HibernateException {
		super(persister, factory);
		this.criteria = criteria;
		
		addAllToPropertySpaces( persister.getPropertySpaces() );

		resultTypes = new Type[1];
		resultTypes[0] = Hibernate.entity( persister.getMappedClass() );

		StringBuffer condition = new StringBuffer(30);
		Iterator iter = criteria.iterateExpressionEntries();
		if ( !iter.hasNext() ) condition.append("1=1"); //TODO: fix this ugliness
		while ( iter.hasNext() ) {
			CriteriaImpl.CriterionEntry ee = (CriteriaImpl.CriterionEntry) iter.next();
			String sqlString = ee.getCriterion().toSqlString(
				factory, 
				criteria.getPersistentClass( ee.getAlias() ), 
				ee.getAlias(),
				criteria.getAliasClasses()
			);
			condition.append(sqlString);
			if ( iter.hasNext() ) condition.append(" and ");
		}
		
		StringBuffer orderBy = new StringBuffer(30);
		iter = criteria.iterateOrderings();
		while ( iter.hasNext() ) {
			Order ord = (Order) iter.next();
			orderBy.append( ord.toSqlString( factory, criteria.getCriteriaClass(), getAlias() ) );
			if ( iter.hasNext() ) orderBy.append(", ");
		}
		
		List associations = walkTree(persister, getAlias(), factory);
		initClassPersisters(associations);
		initStatementString( associations, condition.toString(), orderBy.toString(), factory );
		
		postInstantiate();
		
	}
	
	public List list(SessionImplementor session) throws HibernateException, SQLException {
		List values = new ArrayList();
		List types = new ArrayList();
		Iterator iter = criteria.iterateExpressionEntries();
		while ( iter.hasNext() ) {
			CriteriaImpl.CriterionEntry ce = (CriteriaImpl.CriterionEntry) iter.next();
			TypedValue[] tv = ce.getCriterion().getTypedValues( 
				session.getFactory(), 
				criteria.getCriteriaClass( ce.getAlias() ),
				criteria.getAliasClasses()
			);
			for ( int i=0; i<tv.length; i++ ) {
				values.add( tv[i].getValue() );
				types.add( tv[i].getType() );
			}
		}
		Object[] valueArray = values.toArray();
		Type[] typeArray = (Type[]) types.toArray(ArrayHelper.EMPTY_TYPE_ARRAY);
		
		RowSelection selection = new RowSelection();
		selection.setFirstRow( criteria.getFirstResult() );
		selection.setMaxRows( criteria.getMaxResults() );
		selection.setTimeout( criteria.getTimeout() );
		selection.setFetchSize( criteria.getFetchSize() );
		
		QueryParameters qp = new QueryParameters(typeArray, valueArray, criteria.getLockModes(), selection);
		qp.setCacheable( criteria.getCacheable() );
		qp.setCacheRegion( criteria.getCacheRegion() );
		return list(session, qp, querySpaces, resultTypes);
	}

	protected Object getResultColumnOrRow(Object[] row, ResultSet rs, SessionImplementor session)
	throws SQLException, HibernateException {
		return criteria.getResultTransformer().transformTuple( row, getEntityAliases() );
	}

	protected int getJoinType(
		AssociationType type,
		int config,
		String path,
		String table,
		String[] foreignKeyColumns,
		SessionFactoryImplementor factory) 
		throws MappingException {
		
		if ( criteria.isJoin(path) ) {
			return JoinFragment.INNER_JOIN;
		}
		else {
			FetchMode fm = criteria.getFetchMode(path);
			if ( fm==null || fm==FetchMode.DEFAULT ) {
				return super.getJoinType(type, config, path, table, foreignKeyColumns, factory);
			}
			else {
				return (fm==FetchMode.EAGER) ? JoinFragment.LEFT_OUTER_JOIN : -1;
			}
		}
	}

	/**
	 * Use the discriminator, to narrow the select to instances
	 * of the queried subclass.
	 */
	protected String getWhereFragment() throws MappingException {
		return ( (Queryable) getPersister() ).queryWhereFragment( getAlias(), true, true );
	}

	protected String generateTableAlias(String className, int n, String path, boolean isLinkTable) {
		if (!isLinkTable) {
			String userDefinedAlias = criteria.getAlias(path);
			if (userDefinedAlias!=null) {
				return userDefinedAlias;
			}
		}
		return super.generateTableAlias(className, n, path, isLinkTable);
	}

	protected String generateRootAlias(String tableName) {
		return Criteria.ROOT_ALIAS;
	}
	
	public Set getQuerySpaces() {
		return querySpaces;
	}

	protected void addToPropertySpaces(Serializable space) {
		querySpaces.add(space);
	}

	protected String applyLocks(String sqlSelectString, Map lockModes, Dialect dialect) throws QueryException {
		if ( lockModes==null || lockModes.size()==0 ) {
			return sqlSelectString;
		}
		else {
			return sqlSelectString + new ForUpdateFragment(lockModes).toFragmentString(dialect);

		}
	}

	protected LockMode[] getLockModes(Map lockModes) {
		String[] aliases = getEntityAliases();
		final int size = aliases.length;
		LockMode[] lockModesArray = new LockMode[size];
		//LockMode lm = (LockMode) lockModes.get(Criteria.ROOT_ALIAS);
		//lockModesArray[size] = lm==null ? LockMode.NONE : lm;
		for ( int i=0; i<size; i++ ) {
			LockMode lockMode = (LockMode) lockModes.get( aliases[i] );
			lockModesArray[i] = lockMode==null ? LockMode.NONE : lockMode;
		}
		return lockModesArray;
	}

	protected List getResultList(List results) {
		return criteria.getResultTransformer().transformList(results);
	}

}
