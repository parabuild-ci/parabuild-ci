//$Id: Expression.java,v 1.10 2004/06/04 01:27:39 steveebersole Exp $
package net.sf.hibernate.expression;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.ArrayHelper;

/**
 * The <tt>expression</tt> package may be used by applications as a framework for building 
 * new kinds of <tt>Criterion</tt>. However, it is intended that most applications will 
 * simply use the built-in criterion types via the static factory methods of this class.
 * 
 * @see net.sf.hibernate.Criteria
 * @author Gavin King
 */
public final class Expression {
	
	private Expression() {
		//cannot be instantiated
	}
	
	/**
	 * Apply an "equal" constraint to the named property
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression eq(String propertyName, Object value) {
		return new EqExpression(propertyName, value);
	}
	/**
	 * Apply a "like" constraint to the named property
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression like(String propertyName, Object value) {
		return new LikeExpression(propertyName, value);
	}
	/**
	 * Apply a "like" constraint to the named property
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression like(String propertyName, String value, MatchMode matchMode) {
		return new LikeExpression(propertyName, value, matchMode);
	}
	/**
	 * A case-insensitive "like", similar to Postgres <tt>ilike</tt>
	 * operator
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static Criterion ilike(String propertyName, String value, MatchMode matchMode) {
		return new IlikeExpression(propertyName, value, matchMode);
	}
	/**
	 * A case-insensitive "like", similar to Postgres <tt>ilike</tt>
	 * operator
	 * 
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static Criterion ilike(String propertyName, Object value) {
		return new IlikeExpression(propertyName, value);
	}
	/**
	 * Apply a "greater than" constraint to the named property
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression gt(String propertyName, Object value) {
		return new GtExpression(propertyName, value);
	}
	/**
	 * Apply a "less than" constraint to the named property
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression lt(String propertyName, Object value) {
		return new LtExpression(propertyName, value);
	}
	/**
	 * Apply a "less than or equal" constraint to the named property
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression le(String propertyName, Object value) {
		return new LeExpression(propertyName, value);
	}
	/**
	 * Apply a "greater than or equal" constraint to the named property
	 * @param propertyName
	 * @param value
	 * @return Criterion
	 */
	public static SimpleExpression ge(String propertyName, Object value) {
		return new GeExpression(propertyName, value);
	}
	/**
	 * Apply a "between" constraint to the named property
	 * @param propertyName
	 * @param lo value
	 * @param hi value
	 * @return Criterion
	 */
	public static Criterion between(String propertyName, Object lo, Object hi) {
		return new BetweenExpression(propertyName, lo, hi);
	}
	/**
	 * Apply an "in" constraint to the named property
	 * @param propertyName
	 * @param values
	 * @return Criterion
	 */
	public static Criterion in(String propertyName, Object[] values) {
		return new InExpression(propertyName, values);
	}
	/**
	 * Apply an "in" constraint to the named property
	 * @param propertyName
	 * @param values
	 * @return Criterion
	 */
	public static Criterion in(String propertyName, Collection values) {
		return new InExpression( propertyName, values.toArray() );
	}
	/**
	 * Apply an "is null" constraint to the named property
	 * @return Criterion
	 */
	public static Criterion isNull(String propertyName) {
		return new NullExpression(propertyName);
	}
	/**
	 * Apply an "equal" constraint to two properties
	 */
	public static Criterion eqProperty(String propertyName, String otherPropertyName) {
		return new EqPropertyExpression(propertyName, otherPropertyName);
	}
	/**
	 * Apply a "less than" constraint to two properties
	 */
	public static Criterion ltProperty(String propertyName, String otherPropertyName) {
		return new LtPropertyExpression(propertyName, otherPropertyName);
	}
	/**
	 * Apply a "less than or equal" constraint to two properties
	 */
	public static Criterion leProperty(String propertyName, String otherPropertyName) {
		return new LePropertyExpression(propertyName, otherPropertyName);
	}
	/**
	 * Apply an "is not null" constraint to the named property
	 * @return Criterion
	 */
	public static Criterion isNotNull(String propertyName) {
		return new NotNullExpression(propertyName);
	}
	/**
	 * Return the conjuction of two expressions
	 * 
	 * @param lhs
	 * @param rhs
	 * @return Criterion
	 */
	public static Criterion and(Criterion lhs, Criterion rhs) {
		return new AndExpression(lhs, rhs);
	}
	/**
	 * Return the disjuction of two expressions
	 * 
	 * @param lhs
	 * @param rhs
	 * @return Criterion
	 */
	public static Criterion or(Criterion lhs, Criterion rhs) {
		return new OrExpression(lhs, rhs);
	}
	/**
	 * Return the negation of an expression
	 * 
	 * @param expression
	 * @return Criterion
	 */
	public static Criterion not(Criterion expression) {
		return new NotExpression(expression);
	}
	/**
	 * Apply a constraint expressed in SQL, with the given JDBC
	 * parameters. Any occurrences of <tt>{alias}</tt> will be 
	 * replaced by the table alias.
	 * 
	 * @param sql
	 * @param values
	 * @param types
	 * @return Criterion
	 */
	public static Criterion sql(String sql, Object[] values, Type[] types) {
		return new SQLCriterion(sql, values, types);
	}
	/**
	 * Apply a constraint expressed in SQL, with the given JDBC
	 * parameter. Any occurrences of <tt>{alias}</tt> will be replaced 
	 * by the table alias.
	 * 
	 * @param sql
	 * @param value
	 * @param type
	 * @return Criterion
	 */
	public static Criterion sql(String sql, Object value, Type type) {
		return new SQLCriterion(sql, new Object[] { value }, new Type[] { type } );
	}
	/**
	 * Apply a constraint expressed in SQL. Any occurrences of <tt>{alias}</tt>
	 * will be replaced by the table alias.
	 * 
	 * @param sql
	 * @return Criterion
	 */
	public static Criterion sql(String sql) {
		return new SQLCriterion(sql, ArrayHelper.EMPTY_OBJECT_ARRAY, ArrayHelper.EMPTY_TYPE_ARRAY);
	}
	
	/**
	 * Group expressions together in a single conjunction (A and B and C...)
	 * 
	 * @return Conjunction
	 */
	public static Conjunction conjunction() {
		return new Conjunction();
	}
	
	/**
	 * Group expressions together in a single disjunction (A or B or C...)
	 * 
	 * @return Conjunction
	 */
	public static Disjunction disjunction() {
		return new Disjunction();
	}
	
	/**
	 * Apply an "equals" constraint to each property in the
	 * key set of a <tt>Map</tt>
	 * 
	 * @param propertyNameValues a map from property names to values
	 * @return Criterion
	 */
	public static Criterion allEq(Map propertyNameValues) {
		Conjunction conj = conjunction();
		Iterator iter = propertyNameValues.entrySet().iterator();
		while ( iter.hasNext() ) {
			Map.Entry me = (Map.Entry) iter.next();
			conj.add( eq( (String) me.getKey(), me.getValue() ) );
		}
		return conj;
	}
		
}
