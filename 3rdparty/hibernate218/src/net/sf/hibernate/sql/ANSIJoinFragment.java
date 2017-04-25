//$Id: ANSIJoinFragment.java,v 1.7 2004/07/15 06:18:45 oneovthafew Exp $
package net.sf.hibernate.sql;

import net.sf.hibernate.AssertionFailure;
import net.sf.hibernate.util.StringHelper;

/**
 * An ANSI-style join
 * 
 * @author Gavin King
 */
public class ANSIJoinFragment extends JoinFragment {
	
	private StringBuffer buffer = new StringBuffer();
	private StringBuffer conditions = new StringBuffer();
	
	public void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, int joinType) {
		
		String joinString;
		switch (joinType) {
			case INNER_JOIN:
				joinString = " inner join ";
				break;
			case LEFT_OUTER_JOIN:
				joinString = " left outer join ";
				break;
			case RIGHT_OUTER_JOIN:
				joinString = " right outer join ";
				break;
			case FULL_JOIN:
				joinString = " full outer join ";
				break;
			default:
				throw new AssertionFailure("undefined join type");
		}
		
		buffer.append(joinString)
			.append(tableName)
			.append(' ')
			.append(alias)
			.append(" on ");


		for ( int j=0; j<fkColumns.length; j++) {
			//if (fkColumns[j].indexOf('.')<1) throw new AssertionFailure("missing alias");
			buffer.append( fkColumns[j] )
				.append('=')
				.append(alias)
				.append(StringHelper.DOT)
				.append( pkColumns[j] );
			if ( j<fkColumns.length-1 ) buffer.append(" and ");
		}
	}

	public String toFromFragmentString() {
		return buffer.toString();
	}

	public String toWhereFragmentString() {
		return conditions.toString();
	}

	public void addJoins(String fromFragment, String whereFragment) {
		buffer.append(fromFragment);
		//where fragment must be empty!
	}

	public JoinFragment copy() {
		ANSIJoinFragment copy = new ANSIJoinFragment();
		copy.buffer = new StringBuffer( buffer.toString() );
		return copy;
	}

	public void addCondition(String alias, String[] columns, String condition) {
		for ( int i=0; i<columns.length; i++ ) {
			conditions.append(" and ")
				.append(alias)
				.append(StringHelper.DOT)
				.append( columns[i] )
				.append(condition);
		}
	}

	public void addCrossJoin(String tableName, String alias) {
		buffer.append(StringHelper.COMMA_SPACE)
			.append(tableName)
			.append(' ')
			.append(alias);		
	}

	public void addCondition(String alias, String[] fkColumns, String[] pkColumns) {
		throw new UnsupportedOperationException();
			
	}

	public void addCondition(String condition) {
			throw new UnsupportedOperationException();	
	}
	
	public void addFromFragmentString(String fromFragmentString) {
		buffer.append(fromFragmentString);
	}

}






