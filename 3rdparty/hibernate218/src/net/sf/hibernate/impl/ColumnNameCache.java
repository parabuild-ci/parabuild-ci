// $Id: ColumnNameCache.java,v 1.1 2004/12/01 23:41:22 steveebersole Exp $
package net.sf.hibernate.impl;

import java.sql.SQLException;
import java.sql.ResultSetMetaData;

/**
 * Implementation of ColumnNameCache.
 *
 * @author Steve Ebersole
 */
public class ColumnNameCache {

	private static final String HOLDER = "ZERO-POSITION-HOLDER";

	private String[] columnNameToIndexCache;

	public int getIndexForColumnName(String columnName)throws SQLException {
		for ( int i = 1, max = columnNameToIndexCache.length; i < max; i++ ) {
			if ( columnName.equalsIgnoreCase( columnNameToIndexCache[i] ) ) {
				return i;
			}
		}
		throw new SQLException( "Column [" + columnName + "] not found in result set" );
	}

	public static ColumnNameCache construct(ResultSetMetaData metadata) throws SQLException {
		int columnCount = metadata.getColumnCount();
		ColumnNameCache result = new ColumnNameCache();

		result.columnNameToIndexCache = new String[columnCount + 1];
		// the holder bit simply pads the zero-based indexing of the java
		// array to more easily conceptualize with the one-based indexing
		// employed in JDBC
		result.columnNameToIndexCache[0] = HOLDER;

		// perform "eager" resolution of columnNames->indexes from the result set metadata
		for ( int i = 1; i <= columnCount; i++ ) {
			result.columnNameToIndexCache[i] = metadata.getColumnName(i);
		}

		return result;
	}
}
