//$Id: OrExpression.java,v 1.8 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.expression;


/**
 * A logical "or"
 * @author Gavin King
 */
public class OrExpression extends LogicalExpression {
	
	String getOp() {
		return "or";
	}
		
	OrExpression(Criterion lhs, Criterion rhs) {
		super(lhs, rhs);
	}

}
