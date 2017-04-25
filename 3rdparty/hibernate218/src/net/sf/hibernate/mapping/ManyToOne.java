//$Id: ManyToOne.java,v 1.9 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.mapping;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.util.ReflectHelper;
import net.sf.hibernate.type.EntityType;
import net.sf.hibernate.type.TypeFactory;

/**
 * A many-to-one association mapping
 * @author Gavin King
 */
public class ManyToOne extends ToOne {
	
	public ManyToOne(Table table) throws MappingException {
		super(table);
	}
	
	public void setTypeByReflection(Class propertyClass, String propertyName) throws MappingException {
		try {
			if (getType()==null) setType( TypeFactory.manyToOne(
				ReflectHelper.reflectedPropertyClass(propertyClass, propertyName),
				referencedPropertyName
			) );
		}
		catch (HibernateException he) {
			throw new MappingException( "Problem trying to set association type by reflection", he );
		}
	}
	
	public void createForeignKey() {
		// TODO: handle the case of a foreign key to something other than the pk
		if (referencedPropertyName==null) {
			createForeignKeyOfClass( ( (EntityType) getType() ).getAssociatedClass() );
		}
	}
	
}







