//$Id: TransientObjectException.java,v 1.6 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

/**
 * Throw when the user passes a transient instance to a <tt>Session</tt>
 * method that expects a persistent instance.
 * 
 * @author Gavin King
 */

public class TransientObjectException extends HibernateException {
	
	public TransientObjectException(String s) {
		super(s);
	}
	
}






