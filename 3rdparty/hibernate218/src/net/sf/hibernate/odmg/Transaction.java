//$Id: Transaction.java,v 1.9 2004/06/04 06:50:29 steveebersole Exp $
package net.sf.hibernate.odmg;

import org.odmg.LockNotGrantedException;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.Session;

/** Implements the ODMG <tt>Transaction</tt> API.
 */
public class Transaction implements org.odmg.Transaction {
	private final Database database;
	private final Session session;
	private net.sf.hibernate.Transaction tx;

	/**
	 * Instantiate a <tt>Transaction</tt> for the given <tt>Database</tt>.
	 */
	public Transaction(org.odmg.Database database) throws ODMGException {
		this.database = (Database) database;
		try {
			this.session = this.database.getSessionFactory().openSession();
		}
		catch (HibernateException he) {
			throw new ODMGException( he.getMessage() );
		}
		this.database.associateThread(this);
	}

	/**
	 * Instantiate a <tt>Transaction</tt> for a <tt>Database</tt> created
	 * by the current thread.
	 */
	public Transaction() throws ODMGException {
		this( Implementation.getInstance().currentDatabase() );
	}

	/**
	 * Get the underlying Hibernate <tt>Session</tt>. (Very useful!)
	 */
	public Session getSession() {
		return session;
	}

	/**
	 * Associate the current thread with this <tt>Transaction</tt>. Disassociate the
	 * thread from any other <tt>Transaction</tt>.
	 * @see org.odmg.Transaction#join()
	 */
	public void join() {
		//database.disassociateThread();
		database.associateThread(this);
	}

	/**
	 * Disassociate the thread the <tt>Transaction</tt>. JavaDoc requires a second sentence.
	 *
	 * @see org.odmg.Transaction#leave()
	 */
	public void leave() {
		database.disassociateThread();
	}

	/**
	 * Begin the transaction. JavaDoc requires a second sentence.
	 *
	 * @see org.odmg.Transaction#begin()
	 */
	public void begin() {
		try {
			tx = session.beginTransaction();
		}
		catch (HibernateException he) {
			throw new ODMGRuntimeException( he.getMessage() );
		}
	}

	/**
	 * Is the transaction open. Returns true if <tt>begin()</tt> was called but
	 * neither <tt>commit()</tt> nor <tt>abort()</tt> was called.
	 *
	 * @see org.odmg.Transaction#isOpen()
	 */
	public boolean isOpen() {
		return tx!=null;
	}

	/**
	 * Commit the transaction. JavaDoc requires a second sentence.
	 *
	 * @see org.odmg.Transaction#commit()
	 */
	public void commit() {
		database.disassociateThread();
		try {
			if (tx!=null) tx.commit();
		}
		catch (HibernateException he) {
			throw new ODMGRuntimeException( he.getMessage() );
		}
		finally {
			tx=null;
			try {
				session.close();
			}
			catch (HibernateException he) {
				throw new ODMGRuntimeException( he.getMessage() );
			}
		}
	}

	/**
	 * Abort the transaction. JavaDoc requires a second sentence.
	 *
	 * @see org.odmg.Transaction#abort()
	 */
	public void abort() {
		database.disassociateThread();
		try {
			if (tx!=null) tx.rollback();
		}
		catch (HibernateException he) {
			throw new ODMGRuntimeException( he.getMessage() );
		}
		finally {
			tx=null;
			try {
				session.close();
			}
			catch (HibernateException he) {
				throw new ODMGRuntimeException( he.getMessage() );
			}
		}
	}

	/**
	 * Commit the changes, but leave the transaction open. This implementation
	 * does not have quite the same semantics os ODMG (locks are not retained).
	 * So you should only use this with versioned data.
	 *
	 * @see org.odmg.Transaction#checkpoint()
	 */
	public void checkpoint() {
		try {
			tx.commit();
			tx = session.beginTransaction();
		}
		catch (HibernateException he)
		{
			throw new ODMGRuntimeException( he.getMessage() );
		}
	}

	/**
	 * Obtain a lock upon the given object. In the present implementation,
	 * <tt>READ</tt> lock mode is ignored while <tt>UPGRADE</tt> and
	 * <tt>WRITE</tt> lock modes obtain an <tt>UPGRADE</tt> lock for databases
	 * which support <tt>for update</tt>. We should improve this eventually....
	 *
	 * @see org.odmg.Transaction#lock(Object, int)
	 */
	public void lock(Object obj, int lockMode) throws LockNotGrantedException {
		//TODO: check the semantics of this...
		try {
			if ( lockMode==org.odmg.Transaction.READ ) {
				session.lock(obj, LockMode.READ);
			}
			else {
				session.lock(obj, LockMode.UPGRADE);
			}
		}
		catch (HibernateException he) {
			throw new ODMGRuntimeException( he.getMessage() );
		}
	}

	/**
	 * Not implemented. JavaDoc requires a second sentence.
	 *
	 * @see org.odmg.Transaction#tryLock(Object, int)
	 */
	public boolean tryLock(Object obj, int lockMode) {
		throw new UnsupportedOperationException("try using lock()");
	}
}
