//$Id: OracleDialect.java,v 1.16 2005/01/28 03:50:24 oneovthafew Exp $
package net.sf.hibernate.dialect;

import net.sf.hibernate.sql.CaseFragment;
import net.sf.hibernate.sql.DecodeCaseFragment;
import net.sf.hibernate.sql.JoinFragment;
import net.sf.hibernate.sql.OracleJoinFragment;

/**
 * An SQL dialect for Oracle, compatible with Oracle 8.
 * @author Gavin King
 */
public class OracleDialect extends Oracle9Dialect {
	
	public JoinFragment createOuterJoinFragment() {
		return new OracleJoinFragment();
	}
	public CaseFragment createCaseFragment() {
		return new DecodeCaseFragment();
	}

	public String getLimitString(String sql, boolean hasOffset) {
		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 );
		if (hasOffset) {
			pagingSelect.append("select * from ( select row_.*, rownum rownum_ from ( ");
		}
		else {
			pagingSelect.append("select * from ( ");
		}
		pagingSelect.append(sql);
		if (hasOffset) {
			pagingSelect.append(" ) row_ ) where rownum_ <= ? and rownum_ > ?");
		}
		else {
			pagingSelect.append(" ) where rownum <= ?");
		}
		return pagingSelect.toString();
	}
	
}








