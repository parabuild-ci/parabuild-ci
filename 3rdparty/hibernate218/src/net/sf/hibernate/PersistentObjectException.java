//$Id: PersistentObjectException.java,v 1.7 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

/**
 * Throw when the user passes a persistent instance to a <tt>Session</tt>
 * method that expects a transient instance.
 * 
 * @author Gavin King
 */
public class PersistentObjectException extends HibernateException {
	
	public PersistentObjectException(String s) {
		super(s);
	}
	
}






