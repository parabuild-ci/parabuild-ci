//$Id: ObjectNotFoundException.java,v 1.7 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

import java.io.Serializable;

/**
 * Thrown when <tt>Session.load()</tt> fails to select a row with
 * the given primary key (identifier value). This exception might not
 * be thrown when <tt>load()</tt> is called, even if there was no
 * row on the database, because <tt>load()</tt> returns a proxy if
 * possible. Applications should use <tt>Session.get()</tt> to test if 
 * a row exists in the database.
 * 
 * @author Gavin King
 */
public class ObjectNotFoundException extends UnresolvableObjectException {
	
	public ObjectNotFoundException(Serializable identifier, Class clazz) {
		super(identifier, clazz);
	}

	public static void throwIfNull(Object o, Serializable id, Class clazz) 
	throws ObjectNotFoundException {
		if (o==null) throw new ObjectNotFoundException(id, clazz);
	}
	
}







