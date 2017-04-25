//$Id: Example.java,v 1.5 2004/06/04 01:27:39 steveebersole Exp $
package net.sf.hibernate.expression;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.TypedValue;
import net.sf.hibernate.metadata.ClassMetadata;
import net.sf.hibernate.type.AbstractComponentType;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.StringHelper;

/**
 * Support for query by example.
 * <pre>
 * List results = session.createCriteria(Parent.class)
 *     .add( Example.create(parent).ignoreCase() )
 *     .createCriteria("child")
 *         .add( Example.create( parent.getChild() ) )
 *     .list();
 * </pre>
 * "Examples" may be mixed and matched with "Expressions" in the same <tt>Criteria</tt>.
 * @see net.sf.hibernate.Criteria
 * @author Gavin King
 */
public class Example extends AbstractCriterion {
	
	private final Object entity;
	private final Set excludedProperties = new HashSet();
	private PropertySelector selector;
	private boolean isLikeEnabled;
	private boolean isIgnoreCaseEnabled;
	private MatchMode matchMode;
	
	/**
	 * A strategy for choosing property values for inclusion in the query
	 * criteria
	 */
	public static interface PropertySelector {
		public boolean include(Object propertyValue, String propertyName, Type type);
	}
	
	private static final PropertySelector NOT_NULL = new NotNullPropertySelector();
	private static final PropertySelector ALL = new AllPropertySelector();
	private static final PropertySelector NOT_NULL_OR_ZERO = new NotNullOrZeroPropertySelector();
		
	static final class AllPropertySelector implements PropertySelector {
		public boolean include(Object object, String propertyName, Type type) { 
			return true;
		}
	}
	
	static final class NotNullPropertySelector implements PropertySelector {
		public boolean include(Object object, String propertyName, Type type) { 
			return object!=null;
		}
	}
	
	static final class NotNullOrZeroPropertySelector implements PropertySelector {
		public boolean include(Object object, String propertyName, Type type) { 
			return object!=null && (
				!(object instanceof Number) || ( (Number) object ).longValue()!=0
			);
		}
	}
	
	/**
	 * Set the property selector
	 */
	public Example setPropertySelector(PropertySelector selector) {
		this.selector = selector;
		return this;
	}
	
	/**
	 * Exclude zero-valued properties
	 */
	public Example excludeZeroes() {
		setPropertySelector(NOT_NULL_OR_ZERO);
		return this;
	}
	
	/**
	 * Don't exclude null or zero-valued properties
	 */
	public Example excludeNone() {
		setPropertySelector(ALL);
		return this;
	}
	
	/**
	 * Use the "like" operator for all string-valued properties
	 */
	public Example enableLike(MatchMode matchMode) {
		isLikeEnabled = true;
		this.matchMode = matchMode;
		return this;
	}
	
	/**
	 * Use the "like" operator for all string-valued properties
	 */
	public Example enableLike() {
		return enableLike(MatchMode.EXACT);
	}

	/**
	 * Ignore case for all string-valued properties
	 */
	public Example ignoreCase() {
		isIgnoreCaseEnabled = true;
		return this;
	}
	
	/**
	 * Exclude a particular named property
	 */
	public Example excludeProperty(String name) {
		excludedProperties.add(name);
		return this;
	}
	
	/**
	 * Create a new instance, which includes all non-null properties 
	 * by default
	 * @param entity
	 * @return a new instance of <tt>Example</tt>
	 */
	public static Example create(Object entity) {
		if (entity==null) throw new NullPointerException("null example");
		return new Example(entity, NOT_NULL);
	}

	protected Example(Object entity, PropertySelector selector) {
		this.entity = entity;
		this.selector = selector;
	}

	public String toString() {
		return entity.toString();
	}
	
	private boolean isPropertyIncluded(Object value, String name, Type type) {
		return !excludedProperties.contains(name) &&
			!type.isAssociationType() &&
			selector.include(value, name, type);
	}

	public String toSqlString(
		SessionFactoryImplementor sessionFactory,
		Class persistentClass,
		String alias,
		Map aliasClasses)
		throws HibernateException {
		
		StringBuffer buf = new StringBuffer().append(StringHelper.OPEN_PAREN);
		ClassMetadata meta = sessionFactory.getClassMetadata(persistentClass);
		String[] propertyNames = meta.getPropertyNames();
		Type[] propertyTypes = meta.getPropertyTypes();
		Object[] propertyValues = meta.getPropertyValues(entity);
		for (int i=0; i<propertyNames.length; i++) {
			Object propertyValue = propertyValues[i];
			String propertyName = propertyNames[i];
			
			boolean isPropertyIncluded = i!=meta.getVersionProperty() && 
				isPropertyIncluded( propertyValue, propertyName, propertyTypes[i] );
			if (isPropertyIncluded) {
				if ( propertyTypes[i].isComponentType() ) {
					appendComponentCondition(
						propertyName, 
						propertyValue, 
						(AbstractComponentType) propertyTypes[i], 
						persistentClass,
						alias,
						aliasClasses,
						sessionFactory,
						buf
					);
				}
				else {
					appendPropertyCondition(
						propertyName, 
						propertyValue, 
						persistentClass,
						alias,
						aliasClasses,
						sessionFactory,
						buf
					);
				}
			}
		}
		if ( buf.length()==1 ) buf.append("1=1"); //yuck!
		return buf.append(StringHelper.CLOSE_PAREN).toString();
	}
	
	private static final Object[] TYPED_VALUES = new TypedValue[0];

	public TypedValue[] getTypedValues(
		SessionFactoryImplementor sessionFactory,
		Class persistentClass,
		Map aliasClasses)
		throws HibernateException {

		ClassMetadata meta = sessionFactory.getClassMetadata(persistentClass);
		String[] propertyNames = meta.getPropertyNames();
		Type[] propertyTypes = meta.getPropertyTypes();
		Object[] values = meta.getPropertyValues(entity);
		List list = new ArrayList();
		for (int i=0; i<propertyNames.length; i++) {
			Object value = values[i];
			Type type = propertyTypes[i];
			String name = propertyNames[i];
			
			boolean isPropertyIncluded = i!=meta.getVersionProperty() && 
				isPropertyIncluded(value, name, type);
			
			if (isPropertyIncluded) {
				if ( propertyTypes[i].isComponentType() ) {
					addComponentTypedValues(name, value, (AbstractComponentType) type, list);
				}
				else {
					addPropertyTypedValue(value, type, list);
				}
			}
		}
		return (TypedValue[]) list.toArray(TYPED_VALUES);
	}
	
	protected void addPropertyTypedValue(Object value, Type type, List list) {
		if ( value!=null ) {
			if ( value instanceof String ) {
				String string = (String) value;
				if (isIgnoreCaseEnabled) string = string.toLowerCase();
				if (isLikeEnabled) string = matchMode.toMatchString(string);
				value = string;
			}
			list.add( new TypedValue(type, value) );
		}
	}
	
	protected void addComponentTypedValues(String path, Object component, AbstractComponentType type, List list) 
		throws HibernateException {
		
		if (component!=null) {
			String[] propertyNames = type.getPropertyNames();
			Type[] subtypes = type.getSubtypes();
			Object[] values = type.getPropertyValues(component);
			for (int i=0; i<propertyNames.length; i++) {
				Object value = values[i];
				Type subtype = subtypes[i];
				String subpath = StringHelper.qualify( path, propertyNames[i] );
				if ( isPropertyIncluded(value, subpath, subtype) ) {
					if ( subtype.isComponentType() ) {
						addComponentTypedValues(subpath, value, (AbstractComponentType) subtype, list);
					} 
					else {
						addPropertyTypedValue(value, subtype, list);
					}
				}
			}
		}
	}
	
	protected void appendPropertyCondition(
		String propertyName, 
		Object propertyValue, 
		Class persistentClass,
		String alias,
		Map aliasClasses,
		SessionFactoryImplementor sessionFactory,
		StringBuffer buf) 
	throws HibernateException {
		
		if ( buf.length()>1 ) buf.append(" and ");
		Criterion crit;
		if ( propertyValue!=null ) {
			boolean isString = propertyValue instanceof String;
			crit = ( isLikeEnabled && isString ) ?
				(Criterion) new LikeExpression( propertyName, propertyValue, isIgnoreCaseEnabled ) :
				(Criterion) new EqExpression( propertyName, propertyValue, isIgnoreCaseEnabled && isString );
					
		}
		else {
			crit = new NullExpression(propertyName);
		}
		buf.append( crit.toSqlString(sessionFactory, persistentClass, alias, aliasClasses) );
	}
	
	protected void appendComponentCondition(
		String path, 
		Object component, 
		AbstractComponentType type, 
		Class persistentClass,
		String alias,
		Map aliasClasses,
		SessionFactoryImplementor sessionFactory,
		StringBuffer buf) 
	throws HibernateException {
		
		if (component!=null) {
			String[] propertyNames = type.getPropertyNames();
			Object[] values = type.getPropertyValues(component);
			Type[] subtypes = type.getSubtypes();
			for (int i=0; i<propertyNames.length; i++) {
				String subpath = StringHelper.qualify( path, propertyNames[i] );
				Object value = values[i];
				if ( isPropertyIncluded( value, subpath, subtypes[i] ) ) {
					Type subtype = subtypes[i];
					if ( subtype.isComponentType() ) {
						appendComponentCondition(
							subpath,
							value,
							(AbstractComponentType) subtype,
							persistentClass,
							alias,
							aliasClasses,
							sessionFactory,
							buf
						);
					} 
					else {
						appendPropertyCondition( 
							subpath,
							value, 
							persistentClass,
							alias,
							aliasClasses,
							sessionFactory,
							buf
						);
					}
				}
			}
		}
	}
}
