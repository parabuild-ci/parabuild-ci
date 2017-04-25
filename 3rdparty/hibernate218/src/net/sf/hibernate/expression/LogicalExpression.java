//$Id: LogicalExpression.java,v 1.9 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.expression;

import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.TypedValue;

/**
 * Superclass of binary logical expressions
 * @author Gavin King
 */
public abstract class LogicalExpression extends AbstractCriterion {

	private Criterion lhs;
	private Criterion rhs;
	
	LogicalExpression(Criterion lhs, Criterion rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	public TypedValue[] getTypedValues(
		SessionFactoryImplementor sessionFactory,
		Class persistentClass, Map aliasClasses)
		throws HibernateException {
		
		TypedValue[] lhstv = lhs.getTypedValues(sessionFactory, persistentClass, aliasClasses);
		TypedValue[] rhstv = rhs.getTypedValues(sessionFactory, persistentClass, aliasClasses);
		TypedValue[] result = new TypedValue[ lhstv.length + rhstv.length ];
		System.arraycopy(lhstv, 0, result, 0, lhstv.length);
		System.arraycopy(rhstv, 0, result, lhstv.length, rhstv.length);
		return result;
	}

	public String toSqlString(
		SessionFactoryImplementor sessionFactory,
		Class persistentClass,
		String alias, 
		Map aliasClasses)
		throws HibernateException {
		
		return '(' + 
			lhs.toSqlString(sessionFactory, persistentClass, alias, aliasClasses) + 
			' ' + 
			getOp() + 
			' ' + 
			rhs.toSqlString(sessionFactory, persistentClass, alias, aliasClasses) +
			')';
	}
	
	abstract String getOp();
	
	public String toString() {
		return lhs.toString() + ' ' + getOp() + ' ' + rhs.toString();
	}
}
