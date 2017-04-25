//$Id: DecodeCaseFragment.java,v 1.11 2004/06/04 06:50:29 steveebersole Exp $
package net.sf.hibernate.sql;

import java.util.Iterator;
import java.util.Map;

/** An Oracle-style DECODE function. <br> <code>decode(pkvalue, key1, 1, key2, 2, ..., 0)</code> <br>
 * @author Simon Harris
 */
public class DecodeCaseFragment extends CaseFragment {
	public String toFragmentString() {
		StringBuffer buf = new StringBuffer( cases.size() * 15 + 10 );
		Iterator iter = cases.entrySet().iterator();
		while ( iter.hasNext() ) {
			Map.Entry me = (Map.Entry) iter.next();
			if ( "0".equals( me.getValue() ) ) {
				buf.insert( 0, me.getKey() );
			}
			else {
				buf.append(", ")
				        .append( me.getKey() )
				        .append(", ")
				        .append( me.getValue() );
			}
		}
		buf.insert(0, "decode (").append(",0 )");
		if (returnColumnName!=null) {
			buf.append(" as ")
			        .append(returnColumnName);
		}
		return buf.toString();
	}
}