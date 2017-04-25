//$Id: IdentifierGenerationException.java,v 1.8 2004/06/04 01:27:40 steveebersole Exp $
package net.sf.hibernate.id;

import net.sf.hibernate.HibernateException;

/**
 * Thrown by <tt>IdentifierGenerator</tt> implementation class when
 * ID generation fails.
 *
 * @see IdentifierGenerator
 * @author Gavin King
 */

public class IdentifierGenerationException extends HibernateException {
	
	public IdentifierGenerationException(String msg) {
		super(msg);
	}
	
	public IdentifierGenerationException(String msg, Throwable t) {
		super(msg, t);
	}
	
}






