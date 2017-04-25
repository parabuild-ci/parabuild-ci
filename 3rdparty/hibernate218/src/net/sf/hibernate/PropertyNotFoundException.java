//$Id: PropertyNotFoundException.java,v 1.7 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

/**
 * Indicates that an expected getter or setter method could not be
 * found on a class.
 * 
 * @author Gavin King
 */
public class PropertyNotFoundException extends MappingException {
	
	public PropertyNotFoundException(String s) {
		super(s);
	}
	
}






