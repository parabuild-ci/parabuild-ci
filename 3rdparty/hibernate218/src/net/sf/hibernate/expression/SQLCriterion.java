//$Id: SQLCriterion.java,v 1.5 2004/06/04 01:27:39 steveebersole Exp $
package net.sf.hibernate.expression;

import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.TypedValue;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.StringHelper;

/**
 * A SQL fragment. The string {alias} will be replaced by the
 * alias of the root entity.
 */
public class SQLCriterion extends AbstractCriterion {
	
	private final String sql;
	private final TypedValue[] typedValues;

	public String toSqlString(
		SessionFactoryImplementor sessionFactory,
		Class persistentClass,
		String alias, 
		Map aliasClasses)
		throws HibernateException {
		return StringHelper.replace(sql, "{alias}", alias);
	}

	public TypedValue[] getTypedValues(
		SessionFactoryImplementor sessionFactory,
		Class persistentClass, Map aliasClasses)
		throws HibernateException {
		return typedValues;
	}

	public String toString() {
		return sql;
	}
	
	SQLCriterion(String sql, Object[] values, Type[] types) {
		this.sql = sql;
		typedValues = new TypedValue[values.length];
		for ( int i=0; i<typedValues.length; i++ ) {
			typedValues[i] = new TypedValue( types[i], values[i] );
		}
	}

}
