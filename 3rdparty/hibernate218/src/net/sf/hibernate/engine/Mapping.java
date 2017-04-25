//$Id: Mapping.java,v 1.9 2004/06/04 01:27:38 steveebersole Exp $
package net.sf.hibernate.engine;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.type.Type;

/**
 * Defines operations common to "compiled" mappings (ie. <tt>SessionFactory</tt>)
 * and "uncompiled" mappings (ie. <tt>Configuration</tt>) that are used by
 * implementors of <tt>Type</tt>.
 * 
 * @see net.sf.hibernate.type.Type
 * @see net.sf.hibernate.impl.SessionFactoryImpl
 * @see net.sf.hibernate.cfg.Configuration
 * @author Gavin King
 */
public interface Mapping {
	public Type getIdentifierType(Class persistentClass) throws MappingException;
	public String getIdentifierPropertyName(Class persistentClass) throws MappingException;
	public Type getPropertyType(Class persistentClass, String propertyName) throws MappingException;
}







