//$Id: ValidationFailure.java,v 1.6 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

/**
 * Thrown from <tt>Validatable.validate()</tt> when an invariant
 * was violated. Some applications might subclass this exception
 * in order to provide more information about the violation.
 * 
 * @author Gavin King
 */
public class ValidationFailure extends HibernateException {
	
	public ValidationFailure(String message) {
		super(message);
	}
	
	public ValidationFailure(String message, Exception e) {
		super(message, e);
	}
	
	public ValidationFailure(Exception e) {
		super("A validation failure occurred", e);
	}
	
}






