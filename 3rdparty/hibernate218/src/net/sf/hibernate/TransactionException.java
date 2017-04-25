//$Id: TransactionException.java,v 1.7 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

/**
 * Indicates that a transaction could not be begun, committed
 * or rolled back.
 * 
 * @see Transaction
 * @author Anton van Straaten
 */

public class TransactionException extends HibernateException {
	
	public TransactionException(String message, Exception root) {
		super(message,root);
	}
	
	public TransactionException(String message) {
		super(message);
	}
	
}






