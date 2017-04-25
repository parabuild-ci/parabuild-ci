//$Id: OracleJoinFragment.java,v 1.6 2004/06/04 01:28:51 steveebersole Exp $
package net.sf.hibernate.sql;

import net.sf.hibernate.util.StringHelper;

/**
 * An Oracle-style (theta) join
 * @author Jon Lipsky, Gavin King
 */
public class OracleJoinFragment extends JoinFragment {
	
	private StringBuffer afterFrom = new StringBuffer();
	private StringBuffer afterWhere = new StringBuffer();
	
	public void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, int joinType) {
		
		addCrossJoin(tableName, alias);
		
		for ( int j=0; j<fkColumns.length; j++) {
			afterWhere.append(" and ")
				.append( fkColumns[j] );
			if (joinType==RIGHT_OUTER_JOIN || joinType==FULL_JOIN) afterWhere.append("(+)");
			afterWhere.append('=')
				.append(alias)
				.append(StringHelper.DOT)
				.append( pkColumns[j] );
			if (joinType==LEFT_OUTER_JOIN || joinType==FULL_JOIN) afterWhere.append("(+)");
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
		OracleJoinFragment copy = new OracleJoinFragment();
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

	public void addCondition(
		String alias,
		String[] fkColumns,
		String[] pkColumns) {
			
			throw new UnsupportedOperationException();
			
	}

	public void addCondition(String condition) {
		afterWhere.append(condition);
	}

	public void addFromFragmentString(String fromFragmentString) {
		afterFrom.append(fromFragmentString);
	}

}






