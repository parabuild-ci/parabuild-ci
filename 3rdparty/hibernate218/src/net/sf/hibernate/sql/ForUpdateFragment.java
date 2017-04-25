//$Id: ForUpdateFragment.java,v 1.6 2004/06/04 05:43:48 steveebersole Exp $
package net.sf.hibernate.sql;

import java.util.Iterator;
import java.util.Map;

import net.sf.hibernate.LockMode;
import net.sf.hibernate.QueryException;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.util.StringHelper;

/**
 * @author Gavin King
 */
public class ForUpdateFragment {
	private StringBuffer aliases = new StringBuffer();
	private boolean isNowaitEnabled;
	public ForUpdateFragment addTableAlias(String alias) {
		if ( aliases.length()>0 ) aliases.append(", ");
		aliases.append(alias);
		return this;
	}
	public String toFragmentString(Dialect dialect) {
		if ( aliases.length()==0 ) return StringHelper.EMPTY_STRING;
		boolean nowait = isNowaitEnabled && dialect.supportsForUpdateNowait();
		if ( dialect.supportsForUpdateOf() ) {
			return " for update of " + aliases + ( nowait ? " nowait" : StringHelper.EMPTY_STRING );
		}
		else if ( dialect.supportsForUpdate() ) {
			return " for update" + ( nowait ? " nowait" : StringHelper.EMPTY_STRING );
		}
		else {
			return StringHelper.EMPTY_STRING;
		}
	}
	
	public ForUpdateFragment setNowaitEnabled(boolean nowait) {
		isNowaitEnabled = nowait;
		return this;
	}
	
	public ForUpdateFragment() {}
	
	public ForUpdateFragment(Map lockModes) throws QueryException {
		LockMode upgradeType = null;
		Iterator iter = lockModes.entrySet().iterator();
		while ( iter.hasNext() ) {
			Map.Entry me = (Map.Entry) iter.next();
			LockMode lockMode = (LockMode) me.getValue();
			if ( LockMode.READ.lessThan(lockMode) ) {
				addTableAlias( (String) me.getKey() );
				if ( upgradeType!=null && lockMode!=upgradeType ) throw new QueryException("mixed LockModes");
				upgradeType = lockMode;
			}
		}
			
		if ( upgradeType==LockMode.UPGRADE_NOWAIT ) setNowaitEnabled(true);
	}
}
