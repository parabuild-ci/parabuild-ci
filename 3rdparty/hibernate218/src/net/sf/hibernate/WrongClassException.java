//$Id: WrongClassException.java,v 1.7 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

import java.io.Serializable;

/**
 * Thrown when <tt>Session.load()</tt> selects a row with
 * the given primary key (identifier value) but the row's
 * discriminator value specifies a subclass that is not
 * assignable to the class requested by the user.
 * 
 * @author Gavin King
 */
public class WrongClassException extends HibernateException {
	
	private final Serializable identifier;
	private final Class clazz;
	
	public WrongClassException(String msg, Serializable identifier, Class clazz) {
		super(msg);
		this.identifier = identifier;
		this.clazz = clazz;
	}
	public Serializable getIdentifier() {
		return identifier;
	}
	
	public String getMessage() {
		return "Object with id: " + identifier + " was not of the specified subclass: " + clazz.getName() + " (" + super.getMessage() + ")" ;
	}
	
	public Class getPersistentClass() {
		return clazz;
	}
	
}







