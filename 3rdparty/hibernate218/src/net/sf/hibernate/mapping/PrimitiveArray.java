//$Id: PrimitiveArray.java,v 1.10 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.mapping;

/**
 * A primitive array has a primary key consisting 
 * of the key columns + index column.
 */
public class PrimitiveArray extends Array {

	public PrimitiveArray(PersistentClass owner) {
		super(owner);
	}

	public boolean isPrimitiveArray() {
		return true;
	}
	
}







