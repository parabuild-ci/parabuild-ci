//$Id: SQLServerDialect.java,v 1.9 2004/08/06 09:50:42 oneovthafew Exp $
package net.sf.hibernate.dialect;

/**
 * A dialect for Microsoft SQL Server 2000
 * @author Gavin King
 */
public class SQLServerDialect extends SybaseDialect {

	public String getLimitString(String querySelect, boolean hasOffset, int limit) {
		if (hasOffset) throw new UnsupportedOperationException("sql server has no offset");
		return new StringBuffer( querySelect.length()+8 )
			.append(querySelect)
			.insert( getAfterSelectInsertPoint(querySelect), " top " + limit )
			.toString();
	}

	/**
	 * Use <tt>insert table(...) values(...) select SCOPE_IDENTITY()</tt>
	 * 
	 * @author <a href="mailto:jkristian@docent.com">John Kristian</a>
	 */
	public String appendIdentitySelectToInsert(String insertSQL) {
		return insertSQL + " select scope_identity()";
	}

	public boolean supportsLimit() {
		return true;
	}

	public boolean useMaxForLimit() {
		return true;
	}

	public boolean supportsLimitOffset() {
		return false;
	}

	public boolean supportsVariableLimit() {
		return false;
	}

	private static int getAfterSelectInsertPoint(String sql) {
		return sql.startsWith("select distinct") ? 15 : 6;
	}

	public char closeQuote() {
		return ']';
	}

	public char openQuote() {
		return '[';
	}

}
