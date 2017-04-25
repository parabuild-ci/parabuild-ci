//$Id: EqPropertyExpression.java,v 1.5 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.expression;

/**
 * @author Gavin King
 */
public class EqPropertyExpression extends PropertyExpression {

	public EqPropertyExpression(String propertyName, String otherPropertyName) {
		super(propertyName, otherPropertyName);
	}

	String getOp() {
		return "=";
	}

}
