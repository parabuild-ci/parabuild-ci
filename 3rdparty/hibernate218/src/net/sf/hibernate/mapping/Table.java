//$Id: Table.java,v 1.22 2005/01/29 03:03:19 oneovthafew Exp $
package net.sf.hibernate.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import net.sf.hibernate.engine.Mapping;
import net.sf.hibernate.id.IdentityGenerator;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.dialect.HSQLDialect;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.tool.hbm2ddl.ColumnMetadata;
import net.sf.hibernate.tool.hbm2ddl.TableMetadata;
import net.sf.hibernate.util.StringHelper;

import org.apache.commons.collections.SequencedHashMap;

/**
 * A relational table
 * @author Gavin King
 */
public class Table implements RelationalModel {
	
	private String name;
	private String schema;
	private Map columns = new SequencedHashMap();
	private SimpleValue idValue;
	private PrimaryKey primaryKey;
	private Map indexes = new HashMap();
	private Map foreignKeys = new HashMap();
	private Map uniqueKeys = new HashMap();
	private final int uniqueInteger;
	private boolean quoted;
	private static int tableCounter=0;
	private List checkConstraints = new ArrayList();
	
	public Table() {
		uniqueInteger = tableCounter++;
	}
	
	private String getQualifiedName(Dialect dialect) {
		String quotedName = getQuotedName(dialect);
		return schema==null ? quotedName : schema + dialect.getSchemaSeparator() + quotedName;
	}
	
	public String getQualifiedName(Dialect dialect, String defaultQualifier) {
		String quotedName = getQuotedName(dialect);
		return schema==null ? 
			( defaultQualifier==null ? quotedName : defaultQualifier + dialect.getSchemaSeparator() + quotedName ) : 
			getQualifiedName(dialect);
	}
	
	public String getName() {
		return name;
	}
	public String getQuotedName(Dialect dialect) {
		return quoted ?
			dialect.openQuote() + name + dialect.closeQuote() :
			name;
	}
	public void setName(String name) {
		if ( name.charAt(0)=='`' ) {
			quoted=true;
			this.name=name.substring( 1, name.length()-1 );
		}
		else {
			this.name = name;
		}
	}
	
	public Column getColumn(int n) {
		Iterator iter = columns.values().iterator();
		for (int i=0; i<n-1; i++) iter.next();
		return (Column) iter.next();
	}
	public void addColumn(Column column) {
		Column old = (Column) columns.get( column.getName() );
		if ( old==null ) {
			columns.put( column.getName(), column );
			column.uniqueInteger = columns.size();
		}
		else {
			column.uniqueInteger = old.uniqueInteger;
		}
	}
	public int getColumnSpan() {
		return columns.size();
	}
	public Iterator getColumnIterator() {
		return columns.values().iterator();
	}
	public Iterator getIndexIterator() {
		return indexes.values().iterator();
	}
	public Iterator getForeignKeyIterator() {
		return foreignKeys.values().iterator();
	}
	public Iterator getUniqueKeyIterator() {
		return uniqueKeys.values().iterator();
	}
	
	public Iterator sqlAlterStrings(Dialect dialect, Mapping p, TableMetadata tableInfo, String defaultSchema) throws HibernateException {
		
		StringBuffer root = new StringBuffer("alter table ")
			.append( getQualifiedName(dialect, defaultSchema) )
			.append(' ')
			.append( dialect.getAddColumnString() );
		
		Iterator iter=getColumnIterator();
		List results = new ArrayList();
		while ( iter.hasNext() ) {
			Column col = (Column) iter.next();
			
			ColumnMetadata columnInfo=tableInfo.getColumnMetadata( col.getName() );
			
			if (columnInfo==null) {
				// the column doesnt exist at all.
				StringBuffer alter = new StringBuffer( root.toString() )
					.append(' ')
					.append( col.getQuotedName(dialect) )
					.append(' ')
					.append( col.getSqlType(dialect, p) );
				if ( col.isUnique() && dialect.supportsUnique() ) {
					alter.append(" unique");
				}
				if ( col.hasCheckConstraint() ) {
					alter.append(" check(")
						.append( col.getCheckConstraint() )
						.append(")");
				}
				results.add( alter.toString() );
			}
			
		}
		
		return results.iterator();
	}
	
	public String sqlCreateString(Dialect dialect, Mapping p, String defaultSchema) throws HibernateException {
		StringBuffer buf = new StringBuffer("create table ")
			.append( getQualifiedName(dialect, defaultSchema) )
			.append(" (");
		
		boolean identityColumn = idValue!=null && idValue.createIdentifierGenerator(dialect) instanceof IdentityGenerator;
		
		// Try to find out the name of the primary key to create it as identity if the IdentityGenerator is used
		String pkname = null;
		if (primaryKey != null && identityColumn ) {
			pkname = ( (Column) primaryKey.getColumnIterator().next() ).getQuotedName(dialect);
		}
		
		Iterator iter = getColumnIterator();
		while ( iter.hasNext() ) {
			Column col = (Column) iter.next();
			
			buf.append( col.getQuotedName(dialect) )
				.append(' ');
			
			if ( identityColumn && col.getQuotedName(dialect).equals(pkname) ) {
				// to support dialects that have their own identity data type
				if ( dialect.hasDataTypeInIdentityColumn() ) {
					buf.append( col.getSqlType(dialect, p) );
				}
				buf.append(' ').append( dialect.getIdentityColumnString() );
			}
			else {
				buf.append( col.getSqlType(dialect, p) );
				if ( col.isNullable() ) {
					buf.append( dialect.getNullColumnString() );
				}
				else {
					buf.append(" not null" );
				}
			}
			
			if ( col.isUnique() ) {
				if ( dialect.supportsUnique() ) {
					buf.append(" unique");
				}
				else {
					UniqueKey uk = getUniqueKey( col.getQuotedName(dialect) + '_' );
					uk.addColumn(col);
				}
			}
			if ( col.hasCheckConstraint() && dialect.supportsCheck() ) {
				buf.append(" check(")
					.append( col.getCheckConstraint() )
					.append(")");
			}
			if ( iter.hasNext() ) buf.append(StringHelper.COMMA_SPACE);
			
		}
		if (primaryKey!=null) {
			if ( dialect instanceof HSQLDialect && identityColumn ) {
				// skip the primary key definition
				// ugly, ugly hack!
			}
			else {
				buf.append(StringHelper.COMMA_SPACE)
					.append( primaryKey.sqlConstraintString(dialect, defaultSchema) );
			}
		}
		
        if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
            Iterator ukiter = getUniqueKeyIterator();
            while ( ukiter.hasNext() ) {
                UniqueKey uk = (UniqueKey) ukiter.next();
                buf.append(StringHelper.COMMA_SPACE)
                	.append( uk.sqlConstraintString(dialect) );
            }
        }
        
		/*Iterator idxiter = getIndexIterator();
		while ( idxiter.hasNext() ) {
			Index idx = (Index) idxiter.next();
			buf.append(',').append( idx.sqlConstraintString(dialect) );
		}*/
		
		if ( dialect.supportsCheck() ) {
			Iterator chiter = checkConstraints.iterator();
			while ( chiter.hasNext() ) {
				buf.append(", check(").append( chiter.next() ).append(StringHelper.CLOSE_PAREN);
			}
		}
		
		buf.append(StringHelper.CLOSE_PAREN);
		
		return buf.toString();
	}
	
	public String sqlDropString(Dialect dialect, String defaultSchema) {
		StringBuffer buf = new StringBuffer("drop table ");
		if ( dialect.supportsIfExistsBeforeTableName() ) buf.append("if exists ");
		buf.append( getQualifiedName(dialect, defaultSchema) )
			.append( dialect.getCascadeConstraintsString() );
		if ( dialect.supportsIfExistsAfterTableName() ) buf.append(" if exists");
		return buf.toString();
	}
	
	public PrimaryKey getPrimaryKey() {
		return primaryKey;
	}
	
	public void setPrimaryKey(PrimaryKey primaryKey) {
		this.primaryKey = primaryKey;
	}
	
	public Index createIndex(String name, List indexColumns) {
		if (name==null) name = "IX" + uniqueColumnString( indexColumns.iterator() );
		Index idx = getIndex(name);
		idx.addColumns( indexColumns.iterator() );
		return idx;
	}
	
	public Index getIndex(String indexName) {
		Index index = (Index)indexes.get(indexName);
		
		if (index == null) {
			index = new Index();
			index.setName(indexName);
			index.setTable(this);
			indexes.put(indexName, index);
		}
		
		return index;
	}
	
	public UniqueKey createUniqueKey(List keyColumns) {
		String keyName = "UK" + uniqueColumnString( keyColumns.iterator() );
		UniqueKey uk = getUniqueKey(keyName);
		uk.addColumns( keyColumns.iterator() );
		return uk;
	}
	
	public UniqueKey getUniqueKey(String keyName) {
		
		UniqueKey uk = (UniqueKey) uniqueKeys.get(keyName);
		
		if (uk == null) {
			uk = new UniqueKey();
			uk.setName(keyName);
			uk.setTable(this);
			uniqueKeys.put(keyName, uk);
		}
		return uk;
	}
	
	public ForeignKey createForeignKey(String keyName, List keyColumns, Class referencedClass) {
		
		if (keyName==null) keyName = "FK" + uniqueColumnString( keyColumns.iterator() );
		ForeignKey fk = (ForeignKey) foreignKeys.get(keyName);
		
		if (fk == null) {
			fk = new ForeignKey();
			fk.setName(keyName);
			fk.setTable(this);
			foreignKeys.put(keyName, fk);
			fk.setReferencedClass(referencedClass);
		}
		else {
			keyName += Integer.toHexString( referencedClass.getName().hashCode() ).toUpperCase();
			if ( fk.getReferencedClass()!=referencedClass ) fk = createForeignKey(keyName, keyColumns, referencedClass);
		}
		fk.addColumns( keyColumns.iterator() );
		return fk;
	}
	
	public String uniqueColumnString(Iterator iterator) {
		int result = 0;
		while ( iterator.hasNext() ) result += iterator.next().hashCode();
		return ( Integer.toHexString( name.hashCode() ) + Integer.toHexString(result) ).toUpperCase();
	}
	
	
	public String getSchema() {
		return schema;
	}
	
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	public int getUniqueInteger() {
		return uniqueInteger;
	}
	
	public void setIdentifierValue(SimpleValue idValue) {
		this.idValue = idValue;
	}
	
	public boolean isQuoted() {
		return quoted;
	}

	public void setQuoted(boolean quoted) {
		this.quoted = quoted;
	}
	
	public void addCheckConstraint(String constraint) {
		checkConstraints.add(constraint);
	}

}








