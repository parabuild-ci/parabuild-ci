//$Id: IlikeExpression.java,v 1.8 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.expression;

import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.dialect.PostgreSQLDialect;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.TypedValue;

/**
 * A case-insensitive "like"
 * @author Gavin King
 */
public class IlikeExpression extends AbstractCriterion {

	private final String propertyName;
	private final Object value;
	
	IlikeExpression(String propertyName, Object value) {
		this.propertyName = propertyName;
		this.value = value;
	}

	IlikeExpression(String propertyName, String value, MatchMode matchMode) {
		this( propertyName, matchMode.toMatchString(value) );
	}

	public String toSqlString(SessionFactoryImplementor sessionFactory, Class persistentClass, String alias, Map aliasClasses) throws HibernateException {
		Dialect dialect = sessionFactory.getDialect();
		String[] columns = getColumns(sessionFactory, persistentClass, propertyName, alias, aliasClasses);
		if (columns.length!=1) throw new HibernateException("ilike may only be used with single-column properties");
		if ( dialect instanceof PostgreSQLDialect ) {
			return columns[0] + " ilike ?";
		}
		else {
			return dialect.getLowercaseFunction() + '(' + columns[0] + ") like ?";
		}
				
		//TODO: get SQL rendering out of this package!
	}
	
	public TypedValue[] getTypedValues(SessionFactoryImplementor sessionFactory, Class persistentClass, Map aliasClasses) throws HibernateException {
		return new TypedValue[] { getTypedValue( sessionFactory, persistentClass, propertyName, value.toString().toLowerCase(), aliasClasses ) };
	}

	public String toString() {
		return propertyName + " ilike " + value;
	}

}
