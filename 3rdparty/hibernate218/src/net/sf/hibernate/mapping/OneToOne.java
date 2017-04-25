//$Id: OneToOne.java,v 1.9 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.mapping;

import java.util.ArrayList;
import java.util.Iterator;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.util.ReflectHelper;
import net.sf.hibernate.type.EntityType;
import net.sf.hibernate.type.ForeignKeyDirection;
import net.sf.hibernate.type.TypeFactory;

/**
 * A one-to-one association mapping
 * @author Gavin King
 */
public class OneToOne extends ToOne {
	
	private boolean constrained;
	private ForeignKeyDirection foreignKeyType;
	private SimpleValue identifier;
	
	public OneToOne(Table table, SimpleValue identifier) throws MappingException {
		super(table);
		this.identifier = identifier;
	}
	
	public void setTypeByReflection(Class propertyClass, String propertyName) throws MappingException {
		try {
			if (getType()==null) setType( TypeFactory.oneToOne(
				ReflectHelper.reflectedPropertyClass(propertyClass, propertyName), 
				foreignKeyType, 
				referencedPropertyName
			) );
		}
		catch (HibernateException he) {
			throw new MappingException( "Problem trying to set association type by reflection", he );
		}
	}
	public void createForeignKey() {
		if ( constrained && referencedPropertyName==null) {
			//TODO: handle the case of a foreign key to something other than the pk
			createForeignKeyOfClass( ( (EntityType) getType() ).getAssociatedClass() );
		}
	}
	
	public java.util.List getConstraintColumns() {
		ArrayList list = new ArrayList();
		Iterator iter = identifier.getColumnIterator();
		while ( iter.hasNext() ) list.add( iter.next() );
		return list;
	}
	/**
	 * Returns the constrained.
	 * @return boolean
	 */
	public boolean isConstrained() {
		return constrained;
	}

	/**
	 * Returns the foreignKeyType.
	 * @return AssociationType.ForeignKeyType
	 */
	public ForeignKeyDirection getForeignKeyType() {
		return foreignKeyType;
	}

	/**
	 * Returns the identifier.
	 * @return Value
	 */
	public Value getIdentifier() {
		return identifier;
	}

	/**
	 * Sets the constrained.
	 * @param constrained The constrained to set
	 */
	public void setConstrained(boolean constrained) {
		this.constrained = constrained;
	}

	/**
	 * Sets the foreignKeyType.
	 * @param foreignKeyType The foreignKeyType to set
	 */
	public void setForeignKeyType(
		ForeignKeyDirection foreignKeyType) {
		this.foreignKeyType = foreignKeyType;
	}

	/**
	 * Sets the identifier.
	 * @param identifier The identifier to set
	 */
	public void setIdentifier(SimpleValue identifier) {
		this.identifier = identifier;
	}

	public boolean isNullable() {
		return !constrained;
	}

}







