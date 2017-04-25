//$Id: RowSelection.java,v 1.9 2004/06/04 01:27:39 steveebersole Exp $
package net.sf.hibernate.engine;

/**
 * Represents a selection of rows in a JDBC <tt>ResultSet</tt>
 * @author Gavin King
 */
public final class RowSelection {
	private Integer firstRow;
	private Integer maxRows;
	private Integer timeout;
	private Integer fetchSize;
	
	public void setFirstRow(Integer firstRow) {
		this.firstRow = firstRow;
	}
	
	public Integer getFirstRow() {
		return firstRow;
	}
	
	public void setMaxRows(Integer maxRows) {
		this.maxRows = maxRows;
	}
	
	public Integer getMaxRows() {
		return maxRows;
	}
	
	public void setTimeout(Integer timeout) {
		this.timeout = timeout;
	}
	
	public Integer getTimeout() {
		return timeout;
	}

	public Integer getFetchSize() {
		return fetchSize;
	}

	public void setFetchSize(Integer fetchSize) {
		this.fetchSize = fetchSize;
	}

}





