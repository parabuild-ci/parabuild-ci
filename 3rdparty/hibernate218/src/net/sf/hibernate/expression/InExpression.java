//$Id: InExpression.java,v 1.10 2004/08/20 02:04:43 oneovthafew Exp $
package net.sf.hibernate.expression;

import java.util.ArrayList;
import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.TypedValue;
import net.sf.hibernate.type.AbstractComponentType;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.StringHelper;

/**
 * Constrains the property to a specified list of values
 * @author Gavin King
 */
public class InExpression extends AbstractCriterion {

	private final String propertyName;
	private final Object[] values;
	
	InExpression(String propertyName, Object[] values) {
		this.propertyName = propertyName;
		this.values = values;
	}

	public String toSqlString(SessionFactoryImplementor sessionFactory, Class persistentClass, String alias, Map aliasClasses) 
	throws HibernateException {
		String params;
		if ( values.length>0 ) {
			params = StringHelper.repeat( "?, ", values.length-1 );
			params += "?";
		}
		else {
			params = StringHelper.EMPTY_STRING;
		}
		String condition = " in (" + params + ')';
		return StringHelper.join(
			" and ", 
			StringHelper.suffix(
				getColumns(sessionFactory, persistentClass, propertyName, alias, aliasClasses),
				condition
			)
		);
		
		//TODO: get SQL rendering out of this package!
	}
	
	public TypedValue[] getTypedValues(SessionFactoryImplementor sessionFactory, Class persistentClass, Map aliasClasses) 
	throws HibernateException {
		ArrayList list = new ArrayList();
		Type type = getType(sessionFactory, persistentClass, propertyName, aliasClasses);
		if ( type.isComponentType() ) {
			AbstractComponentType actype = (AbstractComponentType) type;
			Type[] types = actype.getSubtypes();
			for ( int i=0; i<types.length; i++ ) {
				for ( int j=0; j<values.length; j++ ) {
					Object subval = values[j]==null ? 
						null : 
						actype.getPropertyValues( values[j] )[i];
					list.add( new TypedValue( types[i], subval ) );
				}
			}
		}
		else {
			for ( int j=0; j<values.length; j++ ) {
				list.add( new TypedValue( type, values[j] ) );
			}
		}
		return (TypedValue[]) list.toArray( new TypedValue[ list.size() ] );
	}

	public String toString() {
		return propertyName + " in (" + StringHelper.toString(values) + ')';
	}
	
}
