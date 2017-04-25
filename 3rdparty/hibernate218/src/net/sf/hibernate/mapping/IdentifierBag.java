//$Id: IdentifierBag.java,v 1.5 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.mapping;

import net.sf.hibernate.type.PersistentCollectionType;
import net.sf.hibernate.type.TypeFactory;

/**
 * An <tt>IdentifierBag</tt> has a primary key consisting of 
 * just the identifier column
 */
public class IdentifierBag extends IdentifierCollection {
	
	public IdentifierBag(PersistentClass owner) {
		super(owner);
	}

	public PersistentCollectionType getCollectionType() {
		return TypeFactory.idbag( getRole() );
	}
	
}







