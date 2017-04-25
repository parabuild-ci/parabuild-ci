//$Id: PropertyExpression.java,v 1.6 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.expression;

import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.TypedValue;
import net.sf.hibernate.util.StringHelper;

/**
 * superclass for comparisons between two properties (with SQL binary operators)
 * @author Gavin King
 */
public abstract class PropertyExpression extends AbstractCriterion {

	private final String propertyName;
	private final String otherPropertyName;
	
	private static final TypedValue[] NO_TYPED_VALUES = new TypedValue[0]; 
	
	PropertyExpression(String propertyName, String otherPropertyName) {
		this.propertyName = propertyName;
		this.otherPropertyName = otherPropertyName;
	}

	public String toSqlString(SessionFactoryImplementor sessionFactory, Class persistentClass, String alias, Map aliasClasses) throws HibernateException {
		String[] xcols = getColumns(sessionFactory, persistentClass, propertyName, alias, aliasClasses);
		String[] ycols = getColumns(sessionFactory, persistentClass, otherPropertyName, alias, aliasClasses);
		String result = StringHelper.join(
			" and ", 
			StringHelper.add(xcols, getOp(), ycols)
		);
		if (xcols.length>1) result = StringHelper.OPEN_PAREN + result + StringHelper.CLOSE_PAREN;
		return result;
		//TODO: get SQL rendering out of this package!
	}
	
	public TypedValue[] getTypedValues(SessionFactoryImplementor sessionFactory, Class persistentClass, Map aliasClasses) throws HibernateException {
		return NO_TYPED_VALUES;
	}

	public String toString() {
		return propertyName + getOp() + otherPropertyName;
	}
	
	abstract String getOp();

}
