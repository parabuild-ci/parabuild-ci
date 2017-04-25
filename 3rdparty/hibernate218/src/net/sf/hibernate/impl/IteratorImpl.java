//$Id: IteratorImpl.java,v 1.10 2004/11/11 20:42:31 steveebersole Exp $
package net.sf.hibernate.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.lang.reflect.Constructor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LazyInitializationException;
import net.sf.hibernate.QueryException;
import net.sf.hibernate.JDBCException;
import net.sf.hibernate.exception.JDBCExceptionHelper;
import net.sf.hibernate.util.ReflectHelper;
import net.sf.hibernate.engine.HibernateIterator;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.type.Type;

/**
 * An implementation of <tt>java.util.Iterator</tt> that is
 * returned by <tt>iterate()</tt> query execution methods.
 * @author Gavin King
 */
public final class IteratorImpl implements HibernateIterator {

	private static final Log log = LogFactory.getLog(IteratorImpl.class);

	private ResultSet rs;
	private final SessionImplementor sess;
	private final Type[] types;
	private final boolean single;
	private Object currentResult;
	private boolean hasNext;
	private final String[][] names;
	private PreparedStatement ps;
	private Object nextResult;
	private Constructor holderConstructor;

	public IteratorImpl(
	        ResultSet rs,
	        PreparedStatement ps,
	        SessionImplementor sess,
	        Type[] types,
	        String[][] columnNames,
	        Class holderClass)
	throws HibernateException, SQLException {

		this.rs=rs;
		this.ps=ps;
		this.sess = sess;
		this.types = types;
		this.names = columnNames;

		if (holderClass != null) {
			holderConstructor = ReflectHelper.getConstructor(holderClass, types);
		}

		single = types.length==1;

		postNext();
	}

	public void close() throws JDBCException {
		if (ps!=null) {
			try {
				log.debug("closing iterator");
				nextResult = null;
				sess.getBatcher().closeQueryStatement(ps, rs);
				ps = null;
				rs = null;
				hasNext = false;
			}
			catch (SQLException e) {
				log.info( "Unable to close iterator", e );
				throw JDBCExceptionHelper.convert( sess.getFactory().getSQLExceptionConverter(), e, "Unable to close iterator" );
			}
		}
	}

	private void postNext() throws HibernateException, SQLException {
		this.hasNext = rs.next();
		if (!hasNext) {
			log.debug("exhausted results");
			close();
		}
		else {
			log.debug("retrieving next results");
			if (single) {
				nextResult = types[0].nullSafeGet( rs, names[0], sess, null );
			}
			else {
				Object[] nextResults = new Object[types.length];
				for (int i=0; i<types.length; i++) {
					nextResults[i] = types[i].nullSafeGet( rs, names[i], sess, null );
				}
				nextResult = nextResults;
			}

			if (holderConstructor != null) {
				try {
					if (nextResult == null || !nextResult.getClass().isArray()) {
						nextResult = holderConstructor.newInstance( new Object[] {nextResult} );
					}
					else {
						nextResult = holderConstructor.newInstance( (Object[]) nextResult );
					}
				}
				catch(Exception e) {
					throw new QueryException("Could not instantiate: " + holderConstructor.getDeclaringClass(), e);
				}
			}
		}
	}

	public boolean hasNext() {
		return hasNext;
	}

	public Object next() {
		if ( !hasNext ) throw new NoSuchElementException("No more results");
		try {
			currentResult = nextResult;
			postNext();
			log.debug("returning current results");
			return currentResult;
		}
		catch (Exception sqle) {
			log.error("could not get next result", sqle);
			throw new LazyInitializationException(sqle);
		}
	}

	public void remove() {
		if (!single) throw new UnsupportedOperationException("Not a single column hibernate query result set");
		if (currentResult==null) throw new IllegalStateException("Called Iterator.remove() before next()");
		try {
			sess.delete(currentResult);
		}
		catch (Exception sqle) {
			log.error("could not remove", sqle);
			throw new LazyInitializationException(sqle);
		}
	}

}







