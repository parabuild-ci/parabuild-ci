//$Id: Criteria.java,v 1.7 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

import java.util.List;

import net.sf.hibernate.expression.Criterion;
import net.sf.hibernate.expression.Order;
import net.sf.hibernate.transform.AliasToEntityMapResultTransformer;
import net.sf.hibernate.transform.DistinctRootEntityResultTransformer;
import net.sf.hibernate.transform.ResultTransformer;
import net.sf.hibernate.transform.RootEntityResultTransformer;

/**
 * <tt>Criteria</tt> is a simplified API for retrieving entities
 * by composing <tt>Criterion</tt> objects. This is a very
 * convenient approach for functionality like "search" screens
 * where there is a variable number of conditions to be placed
 * upon the result set.<br>
 * <br>
 * The <tt>Session</tt> is a factory for <tt>Criteria</tt>.
 * <tt>Criterion</tt> instances are usually obtained via
 * the factory methods on <tt>Expression</tt>. eg.
 * <pre>
 * List cats = session.createCriteria(Cat.class)
 *     .add( Expression.like("name", "Iz%") )
 *     .add( Expression.gt( "weight", new Float(minWeight) ) )
 *     .addOrder( Order.asc("age") )
 *     .list();
 * </pre>
 * You may navigate associations using <tt>createAlias()</tt> or
 * <tt>createCriteria()</tt>.
 * <pre>
 * List cats = session.createCriteria(Cat.class)
 *     .createCriteria("kittens")
 *         .add( Expression.like("name", "Iz%") )
 *     .list();
 * </pre>
 * Hibernate's query language is much more general and should be used 
 * for non-simple cases.<br>
 * <br>
 * <i>This is an experimental API</i>
 * 
 * @see Session#createCriteria(java.lang.Class)
 * @see net.sf.hibernate.expression.Expression
 * @see net.sf.hibernate.expression.Criterion
 * @see net.sf.hibernate.expression.Order
 * @author Gavin King
 */
public interface Criteria {
	
	/**
	 * Each row of results is a <tt>Map</tt> from alias to entity instance
	 */
	public static final ResultTransformer ALIAS_TO_ENTITY_MAP = new AliasToEntityMapResultTransformer();
	/**
	 * Each row of results is an instance of the root entity
	 */
	public static final ResultTransformer ROOT_ENTITY = new RootEntityResultTransformer();
	/**
	 * Each row of results is a distinct instance of the root entity
	 */
	public static final ResultTransformer DISTINCT_ROOT_ENTITY = new DistinctRootEntityResultTransformer();

	/**
	 * The alias that refers to the "root" entity of the criteria query.
	 */
	public static final String ROOT_ALIAS = "this";

	/**
	 * Set a limit upon the number of objects to be 
	 * retrieved.
	 * 
	 * @param maxResults the maximum number of results
	 * @return Criteria
	 */
	public Criteria setMaxResults(int maxResults);
	/**
	 * Set the first result to be retrieved.
	 * 
	 * @param firstResult the first result, numbered from <tt>0</tt>
	 * @return Criteria
	 */
	public Criteria setFirstResult(int firstResult);
	/**
	 * Set a fetch size for the underlying JDBC query.
	 * @param fetchSize the fetch size
	 */
	public Criteria setFetchSize(int fetchSize);	
	/**
	 * Set a timeout for the underlying JDBC query.
	 * 
	 * @param timeout
	 * @return Criteria
	 */
	public Criteria setTimeout(int timeout);
	/**
	 * Add a <tt>Criterion</tt> to constrain the results to be 
	 * retrieved.
	 * 
	 * @param criterion
	 * @return Criteria
	 */
	public Criteria add(Criterion criterion);
	
	/**
	 * Add an <tt>Order</tt> to the result set.
	 * 
	 * @param order
	 * @return Criteria
	 */
	public Criteria addOrder(Order order);
	
	/**
	 * Get the results.
	 * 
	 * @return List
	 * @throws HibernateException
	 */
	public List list() throws HibernateException;
	
	/**
	 * Convenience method to return a single instance that matches
	 * the query, or null if the query returns no results.
	 * 
	 * @return the single result or <tt>null</tt>
	 * @throws HibernateException if there is more than one matching result
	 */
	public Object uniqueResult() throws HibernateException;
	
	/**
	 * Specify an association fetching strategy for a
	 * one-to-many, many-to-one or one-to-one association, or
	 * for a collection of values.
	 * 
	 * @param associationPath a dot seperated property path
	 * @param mode the fetch mode
	 * @return the Criteria object for method chaining
	 */
	public Criteria setFetchMode(String associationPath, FetchMode mode) throws HibernateException;
	/**
	 * Join an association, assigning an alias to the joined entity
	 */
	public Criteria createAlias(String associationPath, String alias) throws HibernateException;
	
	/**
	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity
	 */
	public Criteria createCriteria(String associationPath) throws HibernateException;
	
	/**
	 * Create a new <tt>Criteria</tt>, "rooted" at the associated entity,
	 * assigning the given alias
	 */
	public Criteria createCriteria(String associationPath, String alias) throws HibernateException;
	
	/**
	 * Get the persistent class that this <tt>Criteria</tt> applies to
	 */
	public Class getCriteriaClass();
	/**
	 * Get the persistent class that the alias refers to
	 */
	public Class getCriteriaClass(String alias);
	
	/**
	 * Return each row of results as a <tt>Map</tt> from alias
	 * to an instance of the aliased entity
	 * @see Criteria#returnRootEntities()
	 * @deprecated use <tt>setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP)</tt>
	 */
	public Criteria returnMaps();
	/**
	 * Return each row of results as a single instance of the
	 * "root" entity (this is the default mode)
	 * @see Criteria#returnMaps()
	 * @deprecated use <tt>setResultTransformer(Criteria.ROOT_ENTITY)</tt>
	 */
	public Criteria returnRootEntities();
	/**
	 * Set a strategy for handling the query results. This determines the
	 * "shape" of the query result set.
	 * @see Criteria.ROOT_ENTITY
	 * @see Criteria.DISTINCT_ROOT_ENTITY
	 * @see Criteria.ALIAS_TO_ENTITY_MAP
	 * @param resultProcessor
	 */
	public Criteria setResultTransformer(ResultTransformer resultTransformer);
	
	/**
	 * Set the lock mode of the current entity
	 * @param lockMode the lock mode
	 */
	public Criteria setLockMode(LockMode lockMode);
	/**
	 * Set the lock mode of the aliased entity
	 * @param alias an alias
	 * @param lockMode the lock mode
	 */
	public Criteria setLockMode(String alias, LockMode lockMode);
	
	/**
	 * Enable caching of this query result set
	 */
	public Criteria setCacheable(boolean cacheable);
	
	/**
	 * Set the name of the cache region.
	 *
	 * @param cacheRegion the name of a query cache region, or <tt>null</tt>
	 * for the default query cache
	 */
	public Criteria setCacheRegion(String cacheRegion);
	
}