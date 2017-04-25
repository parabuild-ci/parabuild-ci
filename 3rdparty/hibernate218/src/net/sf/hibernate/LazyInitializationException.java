//$Id: LazyInitializationException.java,v 1.7 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

import net.sf.hibernate.exception.NestableRuntimeException;
import org.apache.commons.logging.LogFactory;

/**
 * A problem occurred trying to lazily initialize a collection or
 * proxy (for example the session was closed) or iterate query
 * results.
 * 
 * @see Hibernate#initialize(java.lang.Object)
 * @see Hibernate#isInitialized(java.lang.Object)
 * @author Gavin King
 */

public class LazyInitializationException extends NestableRuntimeException {
	
	public LazyInitializationException(Exception root) {
		super("Hibernate lazy instantiation problem", root);
	}
	
	public LazyInitializationException(String msg) {
		super(msg);
		LogFactory.getLog(LazyInitializationException.class).error(msg, this);
	}
	
	public LazyInitializationException(String msg, Exception root) {
		super(msg, root);
	}
	
	/**
	 * @deprecated use <tt>getCause()</tt>
	 * @see org.apache.commons.lang.exception.NestableRuntimeException#getCause()
	 */
	public Exception getRoot() {
		return (Exception) super.getCause();
	}
	
}






