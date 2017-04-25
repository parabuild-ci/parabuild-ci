//$Id: Conjunction.java,v 1.6 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.expression;

/**
 * @author Gavin King
 */
public class Conjunction extends Junction {

	/**
	 * @see net.sf.hibernate.expression.Junction#getOp()
	 */
	String getOp() {
		return " and ";
	}

}
