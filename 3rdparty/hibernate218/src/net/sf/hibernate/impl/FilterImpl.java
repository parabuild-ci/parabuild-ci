//$Id: FilterImpl.java,v 1.14 2004/06/04 01:27:40 steveebersole Exp $
package net.sf.hibernate.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.ScrollableResults;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.type.Type;

/**
 * implementation of the <tt>Query</tt> interface for collection filters
 * @author Gavin King
 */
public class FilterImpl extends QueryImpl {
	
	private Object collection;
	
	public FilterImpl(String queryString, Object collection, SessionImplementor session) {
		super(queryString, session);
		this.collection = collection;
	}
	
	
	/**
	 * @see net.sf.hibernate.Query#iterate()
	 */
	public Iterator iterate() throws HibernateException {
		verifyParameters();
		Map namedParams = getNamedParams();
		return getSession().iterateFilter( collection, bindParameterLists(namedParams), getQueryParameters(namedParams) );
	}
	
	/**
	 * @see net.sf.hibernate.Query#list()
	 */
	public List list() throws HibernateException {
		verifyParameters();
		Map namedParams = getNamedParams();
		return getSession().filter( collection, bindParameterLists(namedParams), getQueryParameters(namedParams) );
	}
	
	/**
	 * @see net.sf.hibernate.Query#scroll()
	 */
	public ScrollableResults scroll() throws HibernateException {
		throw new UnsupportedOperationException("Can't scroll filters");
	}
	
	public Type[] typeArray() {
		List typeList = getTypes();
		int size = typeList.size();
		Type[] result = new Type[size+1];
		for (int i=0; i<size; i++) result[i+1] = (Type) typeList.get(i);
		return result;
	}
	
	public Object[] valueArray() {
		List valueList = getValues();
		int size = valueList.size();
		Object[] result = new Object[size+1];
		for (int i=0; i<size; i++) result[i+1] = valueList.get(i);
		return result;
	}
	
}






