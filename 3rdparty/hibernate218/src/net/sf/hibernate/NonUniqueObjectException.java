//$Id: NonUniqueObjectException.java,v 1.5 2004/06/04 05:43:44 steveebersole Exp $
package net.sf.hibernate;

import java.io.Serializable;

/**
 * This exception is thrown when an operation would
 * break session-scoped identity. This occurs if the
 * user tries to associate two different instances of
 * the same Java class with a particular identifier,
 * in the scope of a single <tt>Session</tt>.
 * 
 * @author Gavin King
 */
public class NonUniqueObjectException extends HibernateException {
	private final Serializable identifier;
	private final Class clazz;
	
	public NonUniqueObjectException(String message, Serializable id, Class clazz) {
		super(message);
		this.clazz = clazz;
		this.identifier = id;
	}

	public NonUniqueObjectException(Serializable id, Class clazz) {
		this("a different object with the same identifier value was already associated with the session", id, clazz);
	}

	public Serializable getIdentifier() {
		return identifier;
	}
	
	public String getMessage() {
		return super.getMessage() + ": " + identifier + ", of class: " + clazz.getName();
	}
	
	public Class getPersistentClass() {
		return clazz;
	}
	
}
