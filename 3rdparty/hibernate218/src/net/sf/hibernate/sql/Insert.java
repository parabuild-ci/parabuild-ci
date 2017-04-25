//$Id: Insert.java,v 1.6 2004/06/04 05:43:48 steveebersole Exp $
package net.sf.hibernate.sql;

import java.util.Iterator;
import java.util.Map;

import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.type.LiteralType;
import net.sf.hibernate.util.StringHelper;

import org.apache.commons.collections.SequencedHashMap;

/**
 * An SQL <tt>INSERT</tt> statement
 * 
 * @author Gavin King
 */
public class Insert {
	
	public Insert(Dialect dialect) {
		this.dialect = dialect;
	}
	
	private Dialect dialect;
	private String tableName;
	
	private Map columns = new SequencedHashMap();
	
	public Insert addColumn(String columnName) {
		return addColumn(columnName, "?");
	}
	
	public Insert addColumns(String[] columnNames) {
		for ( int i=0; i<columnNames.length; i++ ) {
			addColumn( columnNames[i] );
		}
		return this;
	}

	public Insert addColumn(String columnName, String value) {
		columns.put(columnName, value);
		return this;
	}
	
	public Insert addColumn(String columnName, Object value, LiteralType type) throws Exception {
		return addColumn( columnName, type.objectToSQLString(value) );
	}
	
	public Insert addIdentityColumn(String columnName) {
		String value = dialect.getIdentityInsertString();
		if (value!=null) addColumn(columnName, value);
		return this;
	}
	
	public Insert setTableName(String tableName) {
		this.tableName = tableName;
		return this;
	}
	
	public String toStatementString() {
		StringBuffer buf = new StringBuffer( columns.size()*15 + tableName.length() + 10 );
		buf.append("insert into ")
			.append(tableName);
		if ( columns.size()==0 ) {
			buf.append(' ').append( dialect.getNoColumnsInsertString() );
		}
		else {
			buf.append(" (");
			Iterator iter = columns.keySet().iterator();
			while ( iter.hasNext() ) {
				buf.append( iter.next() );
				if ( iter.hasNext() ) buf.append(StringHelper.COMMA_SPACE);
			}
			buf.append(") values (");
			iter = columns.values().iterator();
			while ( iter.hasNext() ) {
				buf.append( iter.next() );
				if ( iter.hasNext() ) buf.append(StringHelper.COMMA_SPACE);
			}
			buf.append(')');
		}
		return buf.toString(); 
	}
}
