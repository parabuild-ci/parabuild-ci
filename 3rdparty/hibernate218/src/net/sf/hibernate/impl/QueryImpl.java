//$Id: QueryImpl.java,v 1.24 2005/01/10 03:10:24 oneovthafew Exp $
package net.sf.hibernate.impl;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.ScrollMode;
import net.sf.hibernate.ScrollableResults;
import net.sf.hibernate.engine.QueryParameters;
import net.sf.hibernate.engine.SessionImplementor;

/**
 * default implementation of the <tt>Query</tt> interface,
 * for "ordinary" HQL queries (not collection filters)
 * @see FilterImpl
 * @author Gavin King
 */
public class QueryImpl extends AbstractQueryImpl {
	
	public QueryImpl(String queryString, SessionImplementor session) {
		super(queryString, session);				
	}
	
	public Iterator iterate() throws HibernateException {
		verifyParameters();
		Map namedParams = getNamedParams();
		return getSession().iterate( bindParameterLists(namedParams), getQueryParameters(namedParams) );
	}
	
	public ScrollableResults scroll() throws HibernateException {
		verifyParameters();
		Map namedParams = getNamedParams();
		return getSession().scroll( bindParameterLists(namedParams), getQueryParameters(namedParams) );
	}

	public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException {
		verifyParameters();
		Map namedParams = getNamedParams();
		QueryParameters qp = getQueryParameters(namedParams);
		qp.setScrollMode(scrollMode);
		return getSession().scroll( bindParameterLists(namedParams), qp);
	}
	
	public List list() throws HibernateException {
		verifyParameters();
		Map namedParams = getNamedParams();
		return getSession().find( bindParameterLists(namedParams), getQueryParameters(namedParams) );
	}
	
}






