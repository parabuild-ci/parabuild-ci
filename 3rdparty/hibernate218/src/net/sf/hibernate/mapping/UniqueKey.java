//$Id: UniqueKey.java,v 1.13 2005/01/29 03:03:19 oneovthafew Exp $
package net.sf.hibernate.mapping;

import java.util.Iterator;

import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.Mapping;
import net.sf.hibernate.util.StringHelper;

/**
 * A relational unique key constraint
 * @author Gavin King
 */
public class UniqueKey extends Constraint {
	
	public String sqlConstraintString(Dialect dialect) {
		StringBuffer buf = new StringBuffer("unique (");
		Iterator iter = getColumnIterator();
		while ( iter.hasNext() ) {
			buf.append( ( (Column) iter.next() ).getQuotedName(dialect) );
			if ( iter.hasNext() ) buf.append(StringHelper.COMMA_SPACE);
		}
		return buf.append(StringHelper.CLOSE_PAREN).toString();
	}
	
	public String sqlConstraintString(Dialect dialect, String constraintName, String defaultSchema) {
		StringBuffer buf = new StringBuffer(
			dialect.getAddPrimaryKeyConstraintString(constraintName)
		).append('(');
		Iterator iter = getColumnIterator();
		while ( iter.hasNext() ) {
			buf.append( ( (Column) iter.next() ).getQuotedName(dialect) );
			if ( iter.hasNext() ) buf.append(StringHelper.COMMA_SPACE);
		}
		return StringHelper.replace( buf.append(StringHelper.CLOSE_PAREN).toString(), "primary key", "unique" ); //TODO: improve this hack!
	}
    
    public String sqlCreateString(Dialect dialect, Mapping p, String defaultSchema) {
        if ( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
            return super.sqlCreateString(dialect, p, defaultSchema);
        } 
        else {
            return Index.buildSqlCreateIndexString(dialect, getName(), getTable(), getColumnIterator(), true, defaultSchema);
        }
    }
    
    public String sqlDropString(Dialect dialect, String defaultSchema) {
        if( dialect.supportsUniqueConstraintInCreateAlterTable() ) {
            return super.sqlDropString(dialect, defaultSchema);
        } 
        else {
            return Index.buildSqlDropIndexString(dialect, getTable(), getName(), defaultSchema);
        }
    }
}







