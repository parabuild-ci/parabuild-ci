//$Id: HibernateException.java,v 1.8 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

import net.sf.hibernate.exception.NestableException;

/**
 * Any exception that occurs inside the persistence layer
 * or JDBC driver. <tt>SQLException</tt>s are always wrapped
 * by instances of <tt>JDBCException</tt>.
 * 
 * @see JDBCException
 * @author Gavin King
 */

public class HibernateException extends NestableException {
	
	public HibernateException(Throwable root) {
		super(root);
	}
	
	
	public HibernateException(String string, Throwable root) {
		super(string, root);
	}
	
	public HibernateException(String s) {
		super(s);
	}
}






