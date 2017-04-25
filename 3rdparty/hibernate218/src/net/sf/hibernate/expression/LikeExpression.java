//$Id: LikeExpression.java,v 1.7 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.expression;

/**
 * 
 */
public class LikeExpression extends SimpleExpression {
	
	public LikeExpression(
		String propertyName,
		Object value,
		boolean ignoreCase) {
		super(propertyName, value, ignoreCase);
	}

	LikeExpression(String propertyName, Object value) {
		super(propertyName, value);
	}


	LikeExpression(String propertyName, String value, MatchMode matchMode) {
		this( propertyName, matchMode.toMatchString(value) );
	}

	/**
	 * @see net.sf.hibernate.expression.SimpleExpression#getOp()
	 */
	String getOp() {
		return " like ";
	}

}
