//$Id: CompositeElementPropertyMapping.java,v 1.6 2004/06/04 05:43:45 steveebersole Exp $
package net.sf.hibernate.collection;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.persister.AbstractPropertyMapping;
import net.sf.hibernate.type.AbstractComponentType;
import net.sf.hibernate.type.Type;

/**
 * @author Gavin King
 */
public class CompositeElementPropertyMapping extends AbstractPropertyMapping {
	
	private final AbstractComponentType compositeType;
	public CompositeElementPropertyMapping(String[] elementColumns, AbstractComponentType compositeType, SessionFactoryImplementor factory) 
	throws MappingException {
		
		this.compositeType = compositeType;
		
		initComponentPropertyPaths(null, compositeType, elementColumns, factory);

	}
	
	public Type getType() {
		return compositeType;
	}

	protected String getClassName() {
		return compositeType.getName();
	}

}
