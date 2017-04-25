//$Id: AbstractCriterion.java,v 1.7 2004/08/20 02:04:43 oneovthafew Exp $
package net.sf.hibernate.expression;

import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.TypedValue;
import net.sf.hibernate.persister.Queryable;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.StringHelper;

/**
 * Base class for <tt>Criterion</tt> implementations
 * @author Gavin King
 */
public abstract class AbstractCriterion implements Criterion {

	/**
	 * For cosmetic purposes only!
	 * 
	 * @see java.lang.Object#toString()
	 */
	public abstract String toString();

	private static Queryable getPropertyMapping(Class persistentClass, SessionFactoryImplementor sessionFactory)
	throws MappingException {
		return (Queryable) sessionFactory.getPersister(persistentClass);
	}
	
	protected static String[] getColumns(SessionFactoryImplementor sessionFactory, Class persistentClass, String property, String alias, Map aliasClasses) throws HibernateException {
		if ( property.indexOf('.')>0 ) {
			String root = StringHelper.root(property);
			Class clazz = (Class) aliasClasses.get(root);
			if (clazz!=null) {
				persistentClass = clazz;
				alias = root;
				property = property.substring( root.length()+1 );
			}
		}
		return getPropertyMapping(persistentClass, sessionFactory).toColumns(alias, property);
	}

	protected static Type getType(
			SessionFactoryImplementor sessionFactory,
			Class persistentClass,
			String property,
			Map aliasClasses)
	throws HibernateException {
		if ( property.indexOf('.')>0 ) {
			String root = StringHelper.root(property);
			Class clazz = (Class) aliasClasses.get(root);
			if (clazz!=null) {
				persistentClass = clazz;
				property = property.substring( root.length()+1 );
			}
		}
		return getPropertyMapping(persistentClass, sessionFactory).toType(property);
		
	}

	/**
	 * Get the a typed value for the given property value.
	 */
	protected static TypedValue getTypedValue(
		SessionFactoryImplementor sessionFactory,
		Class persistentClass,
		String property,
		Object value,
		Map aliasClasses)
	throws HibernateException {
		return new TypedValue( getType(sessionFactory, persistentClass, property, aliasClasses), value );
	}

}
