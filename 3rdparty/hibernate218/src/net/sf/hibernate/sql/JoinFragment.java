//$Id: JoinFragment.java,v 1.7 2004/06/04 05:43:48 steveebersole Exp $
package net.sf.hibernate.sql;

/**
 * An abstract SQL join fragment renderer
 * @author Gavin King
 */
public abstract class JoinFragment {

	public abstract void addJoin(String tableName, String alias, String[] fkColumns, String[] pkColumns, int joinType);
	public abstract void addCrossJoin(String tableName, String alias);
	public abstract void addJoins(String fromFragment, String whereFragment);
	public abstract String toFromFragmentString();
	public abstract String toWhereFragmentString();
	public abstract void addCondition(String alias, String[] columns, String condition);
	public abstract void addCondition(String alias, String[] fkColumns, String[] pkColumns);
	public abstract void addCondition(String condition);
	public abstract void addFromFragmentString(String fromFragmentString);
	
	public abstract JoinFragment copy();
	
	public static final int INNER_JOIN = 0;
	public static final int FULL_JOIN = 4;
	public static final int LEFT_OUTER_JOIN = 1;
	public static final int RIGHT_OUTER_JOIN = 2;
	
	public void addFragment(JoinFragment ojf) {
		addJoins( ojf.toFromFragmentString(), ojf.toWhereFragmentString() );
	}
}
