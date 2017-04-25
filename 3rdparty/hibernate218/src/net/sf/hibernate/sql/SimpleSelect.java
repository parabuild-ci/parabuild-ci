//$Id: SimpleSelect.java,v 1.8 2004/06/04 05:43:48 steveebersole Exp $
package net.sf.hibernate.sql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import java.util.Set;
import net.sf.hibernate.util.StringHelper;

/**
 * An SQL <tt>SELECT</tt> statement with no table joins
 * 
 * @author Gavin King
 */
public class SimpleSelect {
	
	//private static final Alias DEFAULT_ALIAS = new Alias(10, null);
	
	private String tableName;
	private String orderBy;
	
	private List columns = new ArrayList();
	private Map aliases = new HashMap();
	private List whereTokens = new ArrayList();
	
	public SimpleSelect addColumns(String[] columnNames, String[] columnAliases) {
		for ( int i=0; i<columnNames.length; i++ ) {
			addColumn( columnNames[i], columnAliases[i] );
		}
		return this;
	}
	
	public SimpleSelect addColumns(String[] columnNames) {
		for ( int i=0; i<columnNames.length; i++ ) {
			addColumn( columnNames[i] );
		}
		return this;
	}
	public SimpleSelect addColumn(String columnName) {
		columns.add(columnName);
		//aliases.put( columnName, DEFAULT_ALIAS.toAliasString(columnName) );
		return this;
	}
	
	public SimpleSelect addColumn(String columnName, String alias) {
		columns.add(columnName);
		aliases.put(columnName, alias);
		return this;
	}
	
	public SimpleSelect setTableName(String tableName) {
		this.tableName = tableName;
		return this;
	}
	
	public SimpleSelect addWhereToken(String token) {
		whereTokens.add(token);
		return this;
	}
	
	public SimpleSelect addCondition(String lhs, String op, String rhs) {
		whereTokens.add( lhs + ' ' + op + ' ' + rhs );
		return this;
	}
	
	public SimpleSelect addCondition(String lhs, String condition) {
		whereTokens.add( lhs + ' ' + condition );
		return this;
	}
	
	public SimpleSelect addCondition(String[] lhs, String op, String[] rhs) {
		for ( int i=0; i<lhs.length; i++ ) {
			addCondition( lhs[i], op, rhs[i] );
			if ( i!=lhs.length-1) whereTokens.add("and");
		}
		return this;
	}
	
	public SimpleSelect addCondition(String[] lhs, String condition) {
		for ( int i=0; i<lhs.length; i++ ) {
			addCondition( lhs[i], condition );
			if ( i!=lhs.length-1) whereTokens.add("and");
		}
		return this;
	}
	
	public String toStatementString() {
		StringBuffer buf = new StringBuffer( columns.size()*10 + tableName.length() + whereTokens.size() * 10 + 10 );
		buf.append("select ");
		Set uniqueColumns = new HashSet();
		Iterator iter = columns.iterator();
		boolean appendComma = false;
		while ( iter.hasNext() ) {
			String col = (String) iter.next();
			String alias = (String) aliases.get(col);
			if ( uniqueColumns.add(alias==null ? col : alias) ) {
				if (appendComma) buf.append(StringHelper.COMMA_SPACE);
				buf.append(col);
				if ( alias!=null && !alias.equals(col) ) {
					buf.append(" as ")
						.append(alias);
				}
				appendComma = true;
			}
		}
		buf.append(" from ")
			.append(tableName);
		if ( whereTokens.size() > 0 ) {
			buf.append(" where ")
				.append( toWhereClause() );
		}
		if (orderBy!=null) buf.append(orderBy);
		return buf.toString(); 
	}
	
	public String toWhereClause() {
		StringBuffer buf = new StringBuffer( whereTokens.size() * 5 );
		Iterator iter = whereTokens.iterator();
		while ( iter.hasNext() ) {
			buf.append( iter.next() );
			if ( iter.hasNext() ) buf.append(' ');
		}
		return buf.toString();
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

}
