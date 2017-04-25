//$Id: QueryJoinFragment.java,v 1.14 2004/06/04 01:28:51 steveebersole Exp $
package net.sf.hibernate.sql;

import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.util.StringHelper;

/**
 * A join that appears in a translated HQL query
 * @author Gavin King
 */
public class QueryJoinFragment extends JoinFragment {
	
	private StringBuffer afterFrom = new StringBuffer();
	private StringBuffer afterWhere = new StringBuffer();
	private Dialect dialect;
	private boolean useThetaStyleInnerJoins;
	
	public QueryJoinFragment(Dialect dialect, boolean useThetaStyleInnerJoins) {
		this.dialect = dialect;
		this.useThetaStyleInnerJoins = useThetaStyleInnerJoins;
	}
	
	public void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, int joinType) {
		addJoin(tableName, alias, alias, fkColumns, pkColumns, joinType);
	}
	
	private void addJoin(String tableName, String alias, String concreteAlias, String[] fkColumns, String[] pkColumns, int joinType) {
		if (!useThetaStyleInnerJoins || joinType!=INNER_JOIN) {
			JoinFragment jf = dialect.createOuterJoinFragment();
			jf.addJoin(tableName, alias, fkColumns, pkColumns, joinType);
			addFragment(jf);
		}
		else {
			addCrossJoin(tableName, alias);
			addCondition(concreteAlias, fkColumns, pkColumns);
		}
	}
	
	public String toFromFragmentString() {
		return afterFrom.toString();
	}
	
	public String toWhereFragmentString() {
		return afterWhere.toString();
	}
	
	public void addJoins(String fromFragment, String whereFragment) {
		afterFrom.append(fromFragment);
		afterWhere.append(whereFragment);
	}

	public JoinFragment copy() {
		QueryJoinFragment copy = new QueryJoinFragment(dialect, useThetaStyleInnerJoins);
		copy.afterFrom = new StringBuffer( afterFrom.toString() );
		copy.afterWhere = new StringBuffer( afterWhere.toString() );
		return copy;
	}

	public void addCondition(String alias, String[] columns, String condition) {
		for ( int i=0; i<columns.length; i++ ) {
			afterWhere.append(" and ")
				.append(alias)
				.append(StringHelper.DOT)
				.append( columns[i] )
				.append(condition);
		}
	}


	public void addCrossJoin(String tableName, String alias) {
		afterFrom.append(StringHelper.COMMA_SPACE)
			.append(tableName)
			.append(' ')
			.append(alias);
	}

	public void addCondition(String alias, String[] fkColumns, String[] pkColumns) {
		for ( int j=0; j<fkColumns.length; j++) {
			afterWhere.append(" and ")
				.append( fkColumns[j] )
				.append('=')
				.append(alias)
				.append(StringHelper.DOT)
				.append( pkColumns[j] );
		}
	}

	public void addCondition(String condition) {
		if ( 
			//the condition is not already there
			afterFrom.toString().indexOf( condition.trim() ) < 0 && 
			afterWhere.toString().indexOf( condition.trim() ) < 0 
		) {
			if ( !condition.startsWith(" and ") ) afterWhere.append(" and ");
			afterWhere.append(condition);
		}
	}

	public void addFromFragmentString(String fromFragmentString) {
		afterFrom.append(fromFragmentString);
	}

}






