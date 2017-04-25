//$Id: DisjunctionFragment.java,v 1.6 2004/06/04 05:43:48 steveebersole Exp $
package net.sf.hibernate.sql;

/**
 * A disjunctive string of conditions
 * @author Gavin King
 */
public class DisjunctionFragment {

	private StringBuffer buffer = new StringBuffer();
	
	public DisjunctionFragment addCondition(ConditionFragment fragment) {
		if ( buffer.length()>0 ) buffer.append(" or ");
		buffer.append("(")
			.append( fragment.toFragmentString() )
			.append(")");
		return this;
	}
	
	public String toFragmentString() {
		return buffer.toString();
	}
}
