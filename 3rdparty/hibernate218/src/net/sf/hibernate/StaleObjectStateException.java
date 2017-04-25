//$Id: StaleObjectStateException.java,v 1.8 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

import java.io.Serializable;

import org.apache.commons.logging.LogFactory;

/**
 * Thrown when a version number or timestamp check failed, indicating that the
 * <tt>Session</tt> contained stale data (when using long transactions
 * with versioning). Also occurs if we try delete or update a row that does
 * not exist.<br>
 * <br>
 * Note that this exception often indicates that the user failed to specify the
 * correct <tt>unsaved-value</tt> strategy for a class!
 * 
 * @author Gavin King
 */
public class StaleObjectStateException extends HibernateException {
	private final Class persistentClass;
	private final Serializable identifier;
	
	public StaleObjectStateException(Class persistentClass, Serializable identifier) {
		super("Row was updated or deleted by another transaction (or unsaved-value mapping was incorrect)");
		this.persistentClass = persistentClass;
		this.identifier = identifier;
		LogFactory.getLog(StaleObjectStateException.class).warn("An operation failed due to stale data", this);
	}
	
	public Class getPersistentClass() {
		return persistentClass;
	}
	
	public Serializable getIdentifier() {
		return identifier;
	}
	
	public String getMessage() {
		return super.getMessage() + " for " + persistentClass.getName() + " instance with identifier: " + identifier;
	}
	
}







