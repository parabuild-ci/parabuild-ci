//$Id: EqExpression.java,v 1.7 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.expression;

/**
 * @author Gavin King
 */
public class EqExpression extends SimpleExpression {
		
	public EqExpression(
		String propertyName,
		Object value,
		boolean ignoreCase) {
		super(propertyName, value, ignoreCase);
	}

	EqExpression(String propertyName, Object value) {
		super(propertyName, value);
	}

	/**
	 * @see net.sf.hibernate.expression.SimpleExpression#getOp()
	 */
	String getOp() {
		return "=";
	}

}
