//$Id: ObjectDeletedException.java,v 1.7 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

import java.io.Serializable;

/**
 * Thrown when the user tries to do something illegal with a deleted 
 * object.
 * 
 * @author Gavin King
 */
public class ObjectDeletedException extends UnresolvableObjectException {
	
	public ObjectDeletedException(String message, Serializable identifier, Class clazz) {
		super(message, identifier, clazz);
	}
	
}







