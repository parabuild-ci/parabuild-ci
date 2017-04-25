//$Id: Junction.java,v 1.7 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.expression;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionFactoryImplementor;
import net.sf.hibernate.engine.TypedValue;
import net.sf.hibernate.util.StringHelper;

/**
 * A sequence of a logical expressions combined by some
 * associative logical operator
 * 
 * @author Gavin King
 */
public abstract class Junction extends AbstractCriterion {
	
	private List criteria = new ArrayList();
	
	public Junction add(Criterion criterion) {
		criteria.add(criterion);
		return this;
	}
	
	abstract String getOp();

	public TypedValue[] getTypedValues(
		SessionFactoryImplementor sessionFactory,
		Class persistentClass, Map aliasClasses)
		throws HibernateException {
		ArrayList typedValues = new ArrayList();
		Iterator iter = criteria.iterator();
		while ( iter.hasNext() ) {
			TypedValue[] subvalues = ( (Criterion) iter.next() ).getTypedValues(sessionFactory, persistentClass, aliasClasses);
			for ( int i=0; i<subvalues.length; i++ ) {
				typedValues.add( subvalues[i] );
			}
		}
		return (TypedValue[]) typedValues.toArray( new TypedValue[ typedValues.size() ] );
	}

	public String toSqlString(
		SessionFactoryImplementor sessionFactory,
		Class persistentClass,
		String alias, 
		Map aliasClasses)
		throws HibernateException {
		
		if ( criteria.size()==0 ) return "1=1";
		
		StringBuffer buffer = new StringBuffer()
			.append('(');
		Iterator iter = criteria.iterator();
		while ( iter.hasNext() ) {
			buffer.append( ( (Criterion) iter.next() ).toSqlString(sessionFactory, persistentClass, alias, aliasClasses) );
			if ( iter.hasNext() ) buffer.append( getOp() );
		}
		return buffer.append(')').toString();
	}

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return '(' + StringHelper.join( getOp(), criteria.iterator() ) + ')';
	}

}
