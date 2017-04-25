//$Id: Order.java,v 1.7 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.expression;

import java.util.HashMap;
import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionFactoryImplementor;

/**
 * Represents an order imposed upon a <tt>Criteria</tt> result set
 * @author Gavin King
 */
public class Order {
	
	private boolean ascending;
	private String propertyName;

	/**
	 * Constructor for Order.
	 */
	protected Order(String propertyName, boolean ascending) {
		this.propertyName = propertyName;
		this.ascending = ascending;
	}
	
	/**
	 * Render the SQL fragment
	 * 
	 * @param sessionFactory
	 * @param persistentClass
	 * @param alias
	 * @return String
	 * @throws HibernateException
	 */
	public String toSqlString(SessionFactoryImplementor sessionFactory, Class persistentClass, String alias) throws HibernateException {
		String[] columns = AbstractCriterion.getColumns(sessionFactory, persistentClass, propertyName, alias, EMPTY_MAP);
		if (columns.length!=1) throw new HibernateException("Cannot order by multi-column property: " + propertyName);
		return columns[0] + ( ascending ? " asc" : " desc" );
	}
	
	/**
	 * Ascending order
	 * 
	 * @param propertyName
	 * @return Order
	 */
	public static Order asc(String propertyName) {
		return new Order(propertyName, true);
	}
	
	/**
	 * Descending order
	 * 
	 * @param propertyName
	 * @return Order
	 */
	public static Order desc(String propertyName) {
		return new Order(propertyName, false);
	}
	
	private static final Map EMPTY_MAP = new HashMap();

}
