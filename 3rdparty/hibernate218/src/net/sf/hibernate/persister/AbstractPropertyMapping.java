//$Id: AbstractPropertyMapping.java,v 1.5 2004/06/04 01:28:50 steveebersole Exp $
package net.sf.hibernate.persister;

import java.util.HashMap;
import java.util.Map;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.QueryException;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.sql.Template;
import net.sf.hibernate.type.AbstractComponentType;
import net.sf.hibernate.type.AssociationType;
import net.sf.hibernate.type.EntityType;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.ArrayHelper;
import net.sf.hibernate.util.StringHelper;

/**
 * Base implementation of a PropertyMapping
 * 
 * @author Gavin King
 */
public abstract class AbstractPropertyMapping implements PropertyMapping {

	private final Map typesByPropertyPath = new HashMap();
	private final Map columnsByPropertyPath = new HashMap();
	private final Map formulaTemplatesByPropertyPath = new HashMap();
	
	public String[] getIdentifierColumnNames() {
		throw new UnsupportedOperationException("one-to-one is not supported here");
	}
	
	protected abstract String getClassName();
	
	public Type toType(String propertyName) throws QueryException {
		Type type = (Type) typesByPropertyPath.get(propertyName);
		if (type==null) {
			throw new QueryException( "could not resolve property: " + propertyName + " of: " + getClassName() );
		} 
		return type;
	}

	public String[] toColumns(String alias, String propertyName)
		throws QueryException {
		
		String[] columns = (String[]) columnsByPropertyPath.get(propertyName);
		if (columns==null) {
			String template = (String) formulaTemplatesByPropertyPath.get(propertyName);
			if (template==null) {
				throw new QueryException( "could not resolve property: " + propertyName + " of: " + getClassName() );
			}
			else {
				return new String[] { StringHelper.replace(template, Template.TEMPLATE, alias) };
			}
		}
		else {
			return StringHelper.qualify(alias, columns);
		}
	}
	
	protected void addPropertyPath(String path, Type type, String[] columns) {
		typesByPropertyPath.put(path, type);
		columnsByPropertyPath.put(path, columns);
		handlePath(path, type);
	}

	protected void addFormulaPropertyPath(String path, Type type, String template) {
		typesByPropertyPath.put(path, type);
		formulaTemplatesByPropertyPath.put(path, template);
		handlePath(path, type);
	}

	protected void initPropertyPaths(String path, Type type, String[] columns, String formulaTemplate, SessionFactoryImplementor factory) 
	throws MappingException {
		
		if ( formulaTemplate!=null ) {
			addFormulaPropertyPath(path, type, formulaTemplate);
		}
		else {
			initPropertyPaths(path, type, columns, factory);
		}
	}
		
	protected void initPropertyPaths(String path, Type type, String[] columns, SessionFactoryImplementor factory) 
	throws MappingException {
			
		if ( columns.length!=type.getColumnSpan(factory) ) {
			throw new MappingException( "broken column mapping for: " + path + " of: " + getClassName() );
		}
				
		if ( 
			type.isAssociationType() &&
			( (AssociationType) type).usePrimaryKeyAsForeignKey()
		) {
			columns = getIdentifierColumnNames();	
		}
		
		if (path!=null) addPropertyPath(path, type, columns);

		if ( type.isComponentType() ) {
			initComponentPropertyPaths( path, (AbstractComponentType) type, columns, factory );
		}
		else if ( type.isEntityType() ) {
			initIdentifierPropertyPaths(path, (EntityType) type, columns, factory);
		}
	}
	
	protected void initIdentifierPropertyPaths(String path, EntityType etype, String[] columns, SessionFactoryImplementor factory) 
	throws MappingException {
		
		Type idtype = etype.getIdentifierOrUniqueKeyType(factory);
		
		if ( !etype.isUniqueKeyReference() ) {
			String idpath1 = extendPath(path, ClassPersister.ENTITY_ID);
			addPropertyPath(idpath1, idtype, columns);
			initPropertyPaths(idpath1, idtype, columns, factory);
		}
		
		String idPropName = etype.getIdentifierOrUniqueKeyPropertyName(factory);
		if (idPropName!=null) {
			String idpath2 = extendPath(path, idPropName);
			addPropertyPath(idpath2, idtype, columns);
			initPropertyPaths(idpath2, idtype, columns, factory);
	  	}
	}
	
	protected void initComponentPropertyPaths(String path, AbstractComponentType type, String[] columns, SessionFactoryImplementor factory) 
	throws MappingException {
		
		Type[] types = type.getSubtypes();
		String[] properties = type.getPropertyNames();
		int begin=0;
		for ( int i=0; i<properties.length; i++ ) {
			String subpath = extendPath( path, properties[i] );
			try {
				int length = types[i].getColumnSpan(factory);
				String[] columnSlice = ArrayHelper.slice(columns, begin, length);
				initPropertyPaths(subpath, types[i], columnSlice, factory);
				begin+=length;
			}
			catch (Exception e) {
				throw new MappingException("bug in initComponentPropertyPaths", e);
			}
		}
	}
	
	private static String extendPath(String path, String property) {
		if (path==null) {
			return property;
		}
		else {
			return StringHelper.qualify(path, property);
		}
	}
	
	protected void handlePath(String path, Type type) {}

}
