//$Id: SimpleExpression.java,v 1.9 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.expression;

import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.TypedValue;
import net.sf.hibernate.util.StringHelper;

/**
 * superclass for "simple" comparisons (with SQL binary operators)
 * @author Gavin King
 */
public abstract class SimpleExpression extends AbstractCriterion {

	private final String propertyName;
	private final Object value;
	private boolean ignoreCase;
	
	SimpleExpression(String propertyName, Object value) {
		this.propertyName = propertyName;
		this.value = value;
	}
	
	SimpleExpression(String propertyName, Object value, boolean ignoreCase) {
		this.propertyName = propertyName;
		this.value = value;
		this.ignoreCase = ignoreCase;
	}
	
	public SimpleExpression ignoreCase() {
		ignoreCase = true;
		return this;
	}

	public String toSqlString(SessionFactoryImplementor sessionFactory, Class persistentClass, String alias, Map aliasClasses) 
	throws HibernateException {
		
		String[] columns = getColumns(sessionFactory, persistentClass, propertyName, alias, aliasClasses);
		if (ignoreCase) {
			if ( columns.length!=1 ) throw new HibernateException(
				"case insensitive expression may only be applied to single-column properties: " +
				propertyName
			);
			return new StringBuffer()
				.append( sessionFactory.getDialect().getLowercaseFunction() ) 
				.append(StringHelper.OPEN_PAREN)
				.append( columns[0] )
				.append(StringHelper.CLOSE_PAREN)
				.append( getOp() )
				.append("?")
				.toString();
		}
		else {
			String result = StringHelper.join(
				" and ", 
				StringHelper.suffix( columns, getOp() + "?" )
			);
			if (columns.length>1) result = StringHelper.OPEN_PAREN + result + StringHelper.CLOSE_PAREN;
			return result;
		}
		
		//TODO: get SQL rendering out of this package!
	}
	
	public TypedValue[] getTypedValues(SessionFactoryImplementor sessionFactory, Class persistentClass, Map aliasClasses) 
	throws HibernateException {
		Object icvalue = ignoreCase ? value.toString().toLowerCase() : value;
		return new TypedValue[] { getTypedValue(sessionFactory, persistentClass, propertyName, icvalue, aliasClasses) };
	}

	public String toString() {
		return propertyName + getOp() + value;
	}
	
	abstract String getOp();

}
