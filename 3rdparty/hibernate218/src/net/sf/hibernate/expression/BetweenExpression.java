//$Id: BetweenExpression.java,v 1.8 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.expression;

import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.TypedValue;
import net.sf.hibernate.util.StringHelper;

/**
 * Constrains a property to between two values
 * @author Gavin King
 */
public class BetweenExpression extends AbstractCriterion {

	private final String propertyName;
	private final Object lo;
	private final Object hi;
	
	BetweenExpression(String propertyName, Object lo, Object hi) {
		this.propertyName = propertyName;
		this.lo = lo;
		this.hi = hi;
	}

	public String toSqlString(SessionFactoryImplementor sessionFactory, Class persistentClass, String alias, Map aliasClasses) throws HibernateException {
		return StringHelper.join(
			" and ", 
			StringHelper.suffix( getColumns(sessionFactory, persistentClass, propertyName, alias, aliasClasses), " between ? and ?" )
		);
		
		//TODO: get SQL rendering out of this package!
	}
	
	public TypedValue[] getTypedValues(SessionFactoryImplementor sessionFactory, Class persistentClass, Map aliasClasses) throws HibernateException {
		return new TypedValue[] { 
			getTypedValue(sessionFactory, persistentClass, propertyName, lo, aliasClasses),
			getTypedValue(sessionFactory, persistentClass, propertyName, hi, aliasClasses)
		};
	}

	public String toString() {
		return propertyName + " between " + lo + " and " + hi;
	}
	
}
