//$Id: AndExpression.java,v 1.8 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.expression;


/**
 * A logical "and"
 * @author Gavin King
 */
public class AndExpression extends LogicalExpression {
	
	String getOp() {
		return "and";
	}
		
	AndExpression(Criterion lhs, Criterion rhs) {
		super(lhs, rhs);
	}

}
