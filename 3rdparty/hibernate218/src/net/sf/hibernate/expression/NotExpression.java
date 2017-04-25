//$Id: NotExpression.java,v 1.8 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.expression;

import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.dialect.MySQLDialect;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.TypedValue;
import net.sf.hibernate.util.StringHelper;

/**
 * Negates another criterion
 * @author Gavin King
 */
public class NotExpression extends AbstractCriterion {
	
	private Criterion criterion;
	
	NotExpression(Criterion criterion) {
		this.criterion = criterion;
	}

	public String toSqlString(
		SessionFactoryImplementor sessionFactory,
		Class persistentClass,
		String alias, 
		Map aliasClasses)
		throws HibernateException {
		if ( sessionFactory.getDialect() instanceof MySQLDialect ) {
			return "not (" + criterion.toSqlString(sessionFactory, persistentClass, alias, aliasClasses) + StringHelper.CLOSE_PAREN;
		}
		else {
			return "not " + criterion.toSqlString(sessionFactory, persistentClass, alias, aliasClasses);
		}
	}

	public TypedValue[] getTypedValues(
		SessionFactoryImplementor sessionFactory,
		Class persistentClass, Map aliasClasses)
		throws HibernateException {
		return criterion.getTypedValues(sessionFactory, persistentClass, aliasClasses);
	}

	public String toString() {
		return "not " + criterion.toString();
	}

}
