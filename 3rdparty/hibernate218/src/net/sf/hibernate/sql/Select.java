//$Id: Select.java,v 1.9 2004/06/04 05:43:48 steveebersole Exp $
package net.sf.hibernate.sql;

/**
 * A simple SQL <tt>SELECT</tt> statement
 * @author Gavin King
 */
public class Select {
	
	private String selectClause; 
	private String fromClause;
	private String outerJoinsAfterFrom; 
	private String whereClause;
	private String outerJoinsAfterWhere;
	private String orderByClause;

	/**
	 * Construct an SQL <tt>SELECT</tt> statement from the given clauses
	 */
	public String toStatementString() {
		StringBuffer buf = new StringBuffer(
			selectClause.length() + 
			fromClause.length() + 
			outerJoinsAfterFrom.length() + 
			whereClause.length() + 
			outerJoinsAfterWhere.length() + 
			20
		);
		buf.append("select ").append(selectClause)
			.append(" from ").append(fromClause)
			.append(outerJoinsAfterFrom)
			.append(" where ").append(whereClause)
			.append(outerJoinsAfterWhere);
		if (orderByClause!=null && orderByClause.trim().length() > 0 ) {
			buf.append(" order by ")
			.append(orderByClause);
		}
		return buf.toString();
	} 

	/**
	 * Sets the fromClause.
	 * @param fromClause The fromClause to set
	 */
	public Select setFromClause(String fromClause) {
		this.fromClause = fromClause;
		return this;
	}

	public Select setFromClause(String tableName, String alias) {
		this.fromClause = tableName + ' ' + alias;
		return this;
	}

	/**
	 * Sets the orderByClause.
	 * @param orderByClause The orderByClause to set
	 */
	public Select setOrderByClause(String orderByClause) {
		this.orderByClause = orderByClause;
		return this;
	}

	/**
	 * Sets the outerJoins.
	 * @param outerJoinsAfterFrom The outerJoinsAfterFrom to set
	 * @param outerJoinsAfterWhere The outerJoinsAfterWhere to set
	 */
	public Select setOuterJoins(String outerJoinsAfterFrom, String outerJoinsAfterWhere) {
		this.outerJoinsAfterFrom = outerJoinsAfterFrom;
		this.outerJoinsAfterWhere = outerJoinsAfterWhere;
		return this;
	}


	/**
	 * Sets the selectClause.
	 * @param selectClause The selectClause to set
	 */
	public Select setSelectClause(String selectClause) {
		this.selectClause = selectClause;
		return this;
	}

	/**
	 * Sets the whereClause.
	 * @param whereClause The whereClause to set
	 */
	public Select setWhereClause(String whereClause) {
		this.whereClause = whereClause;
		return this;
	}

}
