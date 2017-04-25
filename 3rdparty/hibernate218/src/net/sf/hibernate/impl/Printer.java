//$Id: Printer.java,v 1.6 2004/06/04 05:43:47 steveebersole Exp $
package net.sf.hibernate.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.TypedValue;
import net.sf.hibernate.metadata.ClassMetadata;
import net.sf.hibernate.type.Type;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Renders entities to a nicely readable string.
 * @author Gavin King
 */
public final class Printer {
	
	private SessionFactoryImplementor factory;
	private static final Log log = LogFactory.getLog(Printer.class);
	
	/**
	 * @param entity an actual entity object, not a proxy!
	 */
	public String toString(Object entity) throws HibernateException {
		
		ClassMetadata cm = factory.getClassMetadata( entity.getClass() );
		if ( cm==null ) return entity.getClass().getName();
		
		Map result = new HashMap();
		
		if ( cm.hasIdentifierProperty() ) {
			result.put( 
				cm.getIdentifierPropertyName(), 
				cm.getIdentifierType().toString( cm.getIdentifier(entity), factory ) 
			);
		}
		
		Type[] types = cm.getPropertyTypes();
		String[] names = cm.getPropertyNames();
		Object[] values = cm.getPropertyValues(entity);
		for ( int i=0; i<types.length; i++ ) {
			result.put( names[i], types[i].toString( values[i], factory ) );
		}
		return cm.getMappedClass().getName() + result.toString();
	}
	
	public String toString(Type[] types, Object[] values) throws HibernateException {
		List list = new ArrayList( types.length * 5 );
		for ( int i=0; i<types.length; i++ ) {
			if ( types[i]!=null )list.add( types[i].toString( values[i], factory ) );
		}
		return list.toString();
	}
	
	public String toString(Map namedTypedValues) throws HibernateException {
		Map result = new HashMap();
		Iterator iter = namedTypedValues.entrySet().iterator();
		while ( iter.hasNext() ) {
			Map.Entry me = (Map.Entry) iter.next();
			TypedValue tv = (TypedValue) me.getValue();
			result.put( me.getKey(), tv.getType().toString( tv.getValue(), factory ) );
		}
		return result.toString();
	}
	
	public void toString(Iterator iter) throws HibernateException {
		if ( !log.isDebugEnabled() || !iter.hasNext() ) return;
		log.debug("listing entities:");
		int i=0;
		while ( iter.hasNext() ) {
			if (i++>20) {
				log.debug("more......");
				break;
			}
			log.debug( toString( iter.next() ) );
		}
	}

	public Printer(SessionFactoryImplementor factory) {
		this.factory = factory;
	}

}
