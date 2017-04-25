//$Id: OQLQuery.java,v 1.8 2004/06/04 06:50:29 steveebersole Exp $
package net.sf.hibernate.odmg;

import org.odmg.ODMGRuntimeException;
import org.odmg.QueryException;
import org.odmg.QueryInvalidException;
import org.odmg.QueryParameterCountInvalidException;
import org.odmg.QueryParameterTypeInvalidException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;

/** Experimental implementation of the ODMG <tt>OQLQuery</tt> interface. The
 * supported query language is actually the Hibernate query language and the
 * <tt>execute()</tt> method  returns results in the same format as
 * <tt>Session.find()</tt>.<br> <br> Warning: this implementation will change
 * significantly as  ODMG support matures!
 */
public class OQLQuery implements org.odmg.OQLQuery {
	private Transaction tx;
	private Query query;
	private int param=0;

	/**
	 * Instantiate an <tt>OQLQuery</tt> for the current transaction.
	 */
	public OQLQuery(Database db) {
		this.tx = db.currentTransaction();
	}

	/**
	 * Instantiate an <tt>OQLQuery</tt> for the given transaction.
	 */
	public OQLQuery(Transaction tx) {
		this.tx = tx;
	}

	/**
	 * Instantiate an <tt>OQLQuery</tt> for the current transaction.
	 */
	public OQLQuery() {
		this.tx = (Transaction) Implementation.getInstance().currentTransaction();
	}

	/**
	 * Get the underlying Hibernate <tt>Query</tt>.
	 */
	public Query getQuery() {
		return query;
	}

	/**
	 * Set the Hibernate query string. Scalar return values are not supported.
	 * @see org.odmg.OQLQuery#create(String)
	 */
	public void create(String queryString) throws QueryInvalidException {
		//TODO: the right exception
		try {
			this.query = tx.getSession().createQuery(queryString);
		}
		catch (HibernateException he) {
			throw new ODMGRuntimeException( he.getMessage() );
		}
	}

	/**
	 * Bind a value to the next enumerated parameter. JavaDoc requires a second sentence.
	 *
	 * @see org.odmg.OQLQuery#bind(Object)
	 */
	public void bind(Object parameter) throws QueryParameterCountInvalidException, QueryParameterTypeInvalidException {
		//TODO: the right exception
		try {
			query.setParameter(param++, parameter);
		}
		catch (HibernateException he) {
			throw new ODMGRuntimeException( he.getMessage() );
		}
	}

	/**
	 * Get the query results as a collection. JavaDoc requires a second sentence.
	 * @see org.odmg.OQLQuery#execute()
	 */
	public Object execute() throws QueryException {
		//TODO: how are results meant to be returned in ODMG?
		try {
			return query.list();
		}
		catch (HibernateException he)
		{
			throw new QueryException( he.getMessage() );
		}
	}
}