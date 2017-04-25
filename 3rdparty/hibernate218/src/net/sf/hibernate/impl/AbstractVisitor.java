//$Id: AbstractVisitor.java,v 1.7 2004/08/14 09:33:32 oneovthafew Exp $
package net.sf.hibernate.impl;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.persister.ClassPersister;
import net.sf.hibernate.type.AbstractComponentType;
import net.sf.hibernate.type.EntityType;
import net.sf.hibernate.type.PersistentCollectionType;
import net.sf.hibernate.type.Type;

/**
 * Abstract superclass of algorithms that walk
 * a tree of property values of an entity, and
 * perform specific functionality for collections,
 * components and associated entities.
 * 
 * @author Gavin King
 */
abstract class AbstractVisitor {
	
	private final SessionImpl session;
	
	AbstractVisitor(SessionImpl session) {
		this.session = session;
	}
	
	/**
	 * Dispatch each property value to processValue().
	 * 
	 * @param values
	 * @param types
	 * @throws HibernateException
	 */
	void processValues(Object[] values, Type[] types) throws HibernateException {
		for ( int i=0; i<types.length; i++ ) {
			processValue( values[i], types[i] );
		}
	}
	
	/**
	 * Visit a component. Dispatch each property 
	 * to processValue().
	 * @param component
	 * @param componentType
	 * @throws HibernateException
	 */
	Object processComponent(Object component, AbstractComponentType componentType) 
	throws HibernateException {
		if (component!=null) {
			processValues( 
				componentType.getPropertyValues(component, session), 
				componentType.getSubtypes() 
			);
		}
		return null;
	}
	
	/**
	 * Visit a property value. Dispatch to the 
	 * correct handler for the property type.
	 * @param value
	 * @param type
	 * @throws HibernateException
	 */
	final Object processValue(Object value, Type type) throws HibernateException {

		if ( type.isPersistentCollectionType() ) {
			//even process null collections
			return processCollection( value, (PersistentCollectionType) type );
		}
		else if ( type.isEntityType() ) {
			return processEntity( value, (EntityType) type );
		}
		else if ( type.isComponentType() ) {
			return processComponent( value, (AbstractComponentType) type );
		}
		else {
			return null;
		}
	}
	
	/**
	 * Walk the tree starting from the given entity. 
	 * 
	 * @param object
	 * @param persister
	 * @throws HibernateException
	 */
	void process(Object object, ClassPersister persister) 
	throws HibernateException {
		processValues( 
			persister.getPropertyValues(object), 
			persister.getPropertyTypes()
		);
	}
	
	/**
	 * Visit a collection. Default superclass 
	 * implementation is a no-op.
	 * @param collection
	 * @param type
	 * @throws HibernateException
	 */
	Object processCollection(Object collection, PersistentCollectionType type)
	throws HibernateException {
		return null;
	}
	
	/**
	 * Visit a many-to-one or one-to-one associated 
	 * entity. Default superclass implementation is 
	 * a no-op.
	 * @param value
	 * @param entityType
	 * @throws HibernateException
	 */
	Object processEntity(Object value, EntityType entityType)
	throws HibernateException {
		return null;
	}
	
	final SessionImpl getSession() {
		return session;
	}
}
