//$Id: Any.java,v 1.8 2004/06/04 05:43:47 steveebersole Exp $
package net.sf.hibernate.mapping;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.type.ObjectType;
import net.sf.hibernate.type.Type;

/**
 * A Hibernate "any" type (ie. polymorphic association to 
 * one-of-several tables).
 * @author Gavin King
 */
public class Any extends SimpleValue {
	
	private Type identifierType;
	private Type metaType = Hibernate.CLASS;

	public Any(Table table) {
		super(table);
	}

	/**
	 * Returns the identifier type.
	 */
	public Type getIdentifierType() {
		return identifierType;
	}

	/**
	 * Sets the identifier type.
	 */
	public void setIdentifierType(Type identifierType) {
		this.identifierType = identifierType;
	}
	
	public Type getType() {
		return new ObjectType(metaType, identifierType);
	}
	
	public void setType(Type type) {
		throw new UnsupportedOperationException("cannot set type of an Any");
	}
	
	public void setTypeByReflection(Class propertyClass, String propertyName) {}

	public Type getMetaType() {
		return metaType;
	}

	public void setMetaType(Type type) {
		metaType = type;
	}

}
