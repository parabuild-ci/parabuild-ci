//$Id: NullExpression.java,v 1.8 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.expression;

import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.TypedValue;
import net.sf.hibernate.util.StringHelper;

/**
 * Constrains a property to be null
 * @author Gavin King
 */
public class NullExpression extends AbstractCriterion {

	private final String propertyName;
	
	private static final TypedValue[] NO_VALUES = new TypedValue[0];
	
	NullExpression(String propertyName) {
		this.propertyName = propertyName;
	}

	public String toSqlString(SessionFactoryImplementor sessionFactory, Class persistentClass, String alias, Map aliasClasses) throws HibernateException {
		String[] columns = getColumns(sessionFactory, persistentClass, propertyName, alias, aliasClasses);
		String result = StringHelper.join(
			" and ", 
			StringHelper.suffix( columns, " is null" )
		);
		if (columns.length>1) result = StringHelper.OPEN_PAREN + result + StringHelper.CLOSE_PAREN;
		return result;
		
		//TODO: get SQL rendering out of this package!
	}
	
	public TypedValue[] getTypedValues(SessionFactoryImplementor sessionFactory, Class persistentClass, Map aliasClasses) throws HibernateException {
		return NO_VALUES;
	}

	public String toString() {
		return propertyName + " is null";
	}
	
}
