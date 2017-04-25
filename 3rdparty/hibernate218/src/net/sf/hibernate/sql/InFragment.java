//$Id: InFragment.java,v 1.9 2004/06/04 05:43:48 steveebersole Exp $
package net.sf.hibernate.sql;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.sf.hibernate.util.StringHelper;

/**
 * An SQL IN expression.
 * <br>
 * <code>... in(...)</code>
 * <br>
 * @author Gavin King
 */
public class InFragment {
	
	public static final Object NULL = new Object();
	public static final Object NOT_NULL = new Object();
	
	private String columnName;
	private Set values = new HashSet();
	
	/**
	 * @param value, an SQL literal, NULL, or NOT_NULL
	 */
	public InFragment addValue(Object value) {
		values.add(value);
		return this;
	}
	
	public InFragment setColumn(String columnName) {
		this.columnName = columnName;
		return this;
	}
	
	public InFragment setColumn(String alias, String columnName) {
		this.columnName = StringHelper.qualify(alias, columnName);
		return setColumn(this.columnName);
	}
	
	public String toFragmentString() {
		StringBuffer buf = new StringBuffer( values.size() * 5 );
		buf.append(columnName);
		//following doesn't handle (null, not null) but unnecessary
		//since this would mean all rows
		if ( values.size()>1 ) {
			boolean allowNull = false;
			buf.append(" in (");
			Iterator iter = values.iterator();
			while ( iter.hasNext() ) {
				Object value = iter.next();
				if ( NULL==value ) {
					allowNull = true;
				}
				else if ( NOT_NULL==value ) {
					throw new IllegalArgumentException("not null makes no sense for in expression");
				}
				else {
					buf.append(value);
					buf.append(StringHelper.COMMA_SPACE);
				}
			}
			buf.setLength( buf.length()-2 );
			buf.append(StringHelper.CLOSE_PAREN);
			if (allowNull) buf
				.insert(0, " is null or ")
				.insert(0, columnName)
				.insert(0, StringHelper.OPEN_PAREN)
				.append(StringHelper.CLOSE_PAREN);
		}
		else {
			Object value = values.iterator().next();
			if ( NULL==value ) {
				buf.append(" is null");
			}
			else if ( NOT_NULL==value ) {
				buf.append(" is not null");
			}
			else {
				buf.append("=").append(value);
			}
		}
		return buf.toString();
	}
}
