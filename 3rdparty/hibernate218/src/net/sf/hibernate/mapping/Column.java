//$Id: Column.java,v 1.18 2004/09/02 10:23:28 oneovthafew Exp $
package net.sf.hibernate.mapping;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.Mapping;
import net.sf.hibernate.sql.Alias;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.StringHelper;

/**
 * A column of a relational database table
 * @author Gavin King
 */
public class Column {
	
	private static final int DEFAULT_PROPERTY_LENGTH = 255;
	
	private int length=DEFAULT_PROPERTY_LENGTH;
	private Type type;
	private int typeIndex = 0;
	private String name;
	private boolean nullable=true;
	private boolean unique=false;
	private String sqlType;
	private boolean quoted=false;
	int uniqueInteger;
	private String checkConstraint;
	
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		char firstChar = name.charAt(0);
		if ( 
			firstChar=='`' |
			Dialect.QUOTE.indexOf(firstChar) > -1 //TODO: deprecated, remove eventually 
		) {
			quoted=true;
			this.name=name.substring( 1, name.length()-1 );
		}
		else {
			this.name = name;
		}
	}
	public String getQuotedName(Dialect d) {
		return quoted ? 
			d.openQuote() + name + d.closeQuote() :
			name;
	}
	
	public String getAlias() {
		char first = name.charAt(0);
		if (
				quoted ||                             // a quoted identifier 
				first==StringHelper.SINGLE_QUOTE ||   // a string literal
				Character.isDigit(first)              // a numeric literal
		) {
			return "y" + Integer.toString(uniqueInteger) + StringHelper.UNDERSCORE;
		}
		else if ( name.length() < 11 ) {
			return name;
		}
		else {
			return new Alias(10, Integer.toString(uniqueInteger) + StringHelper.UNDERSCORE).toAliasString(name);
		}
	}
	
	public boolean isNullable() {
		return nullable;
	}
	
	public void setNullable(boolean nullable) {
		this.nullable=nullable;
	}
	
	public int getTypeIndex() {
		return typeIndex;
	}
	public void setTypeIndex(int typeIndex) {
		this.typeIndex = typeIndex;
	}
	
	private int getAutoSqlType(Mapping mapping) throws MappingException {
		try {
			return getType().sqlTypes(mapping)[ getTypeIndex() ];
		}
		catch (Exception e) {
			throw new MappingException(
				"Could not determine type for column " + 
				name + 
				" of type " + 
				type.getClass().getName() + 
				": " + 
				e.getClass().getName(),
				e
			);
		}
	}
	
	public boolean isUnique() {
		return unique;
	}
	
	
	public String getSqlType(Dialect dialect, Mapping mapping) throws HibernateException {
		return sqlType==null ? 
			dialect.getTypeName( getAutoSqlType(mapping), getLength() ) : 
			sqlType;
	}
	
	public boolean equals(Object object) {
		return object instanceof Column && equals( (Column) object );
	}
	
	public boolean equals(Column column) {
		if (null == column) return false;
		if (this == column) return true;
		
		return name.equals(column.name);
	}
	
	//used also for generation of FK names!
	public int hashCode() {
		return name.hashCode();
	}
	/**
	 * Returns the sqlType.
	 * @return String
	 */
	public String getSqlType() {
		return sqlType;
	}

	/**
	 * Sets the sqlType.
	 * @param sqlType The sqlType to set
	 */
	public void setSqlType(String sqlType) {
		this.sqlType = sqlType;
	}

	/**
	 * Sets the unique.
	 * @param unique The unique to set
	 */
	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	public boolean isQuoted() {
		return quoted;
	}

	public String toString() {
		return getClass().getName() + " for " + name + ":" + sqlType;
	}
	/**
	 * @return Returns the checkConstraint.
	 */
	public String getCheckConstraint() {
		return checkConstraint;
	}

	/**
	 * @param checkConstraint The checkConstraint to set.
	 */
	public void setCheckConstraint(String checkConstraint) {
		this.checkConstraint = checkConstraint;
	}
	
	public boolean hasCheckConstraint() {
		return checkConstraint!=null;
	}

}





