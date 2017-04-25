//$Id: PropertyAccessorFactory.java,v 1.6 2004/06/04 05:43:48 steveebersole Exp $
package net.sf.hibernate.property;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.util.ReflectHelper;

/**
 * @author Gavin King
 */
public final class PropertyAccessorFactory {
	
	private static final PropertyAccessor BASIC_PROPERTY_ACCESSOR = new BasicPropertyAccessor();
	private static final PropertyAccessor DIRECT_PROPERTY_ACCESSOR = new DirectPropertyAccessor();
	
	public static PropertyAccessor getPropertyAccessor(String type) throws MappingException {
		
		if ( type==null || "property".equals(type) ) return BASIC_PROPERTY_ACCESSOR;
		if ( "field".equals(type) ) return DIRECT_PROPERTY_ACCESSOR;
		
		Class accessorClass;
		try {
			accessorClass = ReflectHelper.classForName(type);
		}
		catch (ClassNotFoundException cnfe) {
			throw new MappingException("could not find PropertyAccessor class: " + type, cnfe);
		}
		try {
			return (PropertyAccessor) accessorClass.newInstance();
		}
		catch (Exception e) {
			throw new MappingException("could not instantiate PropertyAccessor class: " + type, e);
		}
			
	}
	
	private PropertyAccessorFactory() {}
}
