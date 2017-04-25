//$Id: Criterion.java,v 1.6 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.expression;

import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.TypedValue;

/**
 * An object-oriented representation of a query criterion that may be used as a constraint
 * in a <tt>Criteria</tt> query.
 * Built-in criterion types are provided by the <tt>Expression</tt> factory class.
 * This interface might be implemented by application classes but, more commonly, application 
 * criterion types would extend <tt>AbstractCriterion</tt>.
 * 
 * @see net.sf.hibernate.expression.AbstractCriterion
 * @see net.sf.hibernate.expression.Expression
 * @author Gavin King
 */
public interface Criterion {

	/**
	 * Render the SQL fragment
	 * 
	 * @param sessionFactory
	 * @param persistentClass
	 * @param alias
	 * @return String
	 * @throws HibernateException
	 */
	public String toSqlString(SessionFactoryImplementor sessionFactory, Class persistentClass, String alias, Map aliasClasses) throws HibernateException;
	/**
	 * Return typed values for all parameters in the rendered SQL fragment
	 * 
	 * @param sessionFactory
	 * @param persistentClass
	 * @return TypedValue[]
	 * @throws HibernateException
	 */
	public TypedValue[] getTypedValues(SessionFactoryImplementor sessionFactory, Class persistentClass, Map aliasClasses) throws HibernateException;

}
