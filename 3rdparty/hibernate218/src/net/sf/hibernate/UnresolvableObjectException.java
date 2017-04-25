//$Id: UnresolvableObjectException.java,v 1.4 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

import java.io.Serializable;

/**
 * Thrown when Hibernate could not resolve an object by id, especially when
 * loading an association.
 * 
 * @author Gavin King
 */
public class UnresolvableObjectException extends HibernateException {
	
	private final Serializable identifier;
	private final Class clazz;
	
	public UnresolvableObjectException(Serializable identifier, Class clazz) {
		this("No row with the given identifier exists", identifier, clazz);
	}
	UnresolvableObjectException(String message, Serializable identifier, Class clazz) {
		super(message);
		this.identifier = identifier;
		this.clazz = clazz;
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
	public static void throwIfNull(Object o, Serializable id, Class clazz) throws UnresolvableObjectException {
		if (o==null) {
			throw new UnresolvableObjectException(id, clazz);
		}
	}
	
}







