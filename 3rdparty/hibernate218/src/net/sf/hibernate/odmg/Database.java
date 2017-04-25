//$Id: Database.java,v 1.7 2004/06/04 01:28:50 steveebersole Exp $
package net.sf.hibernate.odmg;

import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;
import org.odmg.ObjectNameNotFoundException;
import org.odmg.ObjectNameNotUniqueException;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.ObjectNotFoundException;
import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.impl.SessionFactoryObjectFactory;

/**
 * Implements the ODMG <tt>Database</tt> API. This provides operations for
 * persisting and deleteing objects, binding names to objects and looking
 * up objects by name.<br>
 * <br>
 * Applications may create an instance by calling
 * <tt>Implementation.newDatabase()</tt> or by instantiating this class
 * directly. The application should then call <tt>open()</tt> to associate
 * it with an existing Hibernate <tt>SessionFactory</tt>.<br>
 * <br>
 * Instances maintain an association between <tt>Thread</tt>s and
 * </tt>Transaction</tt>s.
 * @see Implementation
 * @see Transaction
 */
public class Database implements org.odmg.Database {

	private SessionFactory sessionFactory;
	private final ThreadLocal threadTransaction = new ThreadLocal();
	
	/**
	 * Instantiate
	 */
	public Database() {}
	
	/**
	 * Specify the underlying <tt>SessionFactory</tt>. This method is not
	 * exposed by the ODMG API but is needed when no JNDI server is available.
	 */
	public void open(SessionFactory factory) {
		this.sessionFactory=factory;
	}
	
	/**
	 * Get the <tt>Session</tt> underlying the <tt>Transaction</tt> associated
	 * with the current thread.
	 */
	public Session getSession() {
		return currentTransaction().getSession();
	}
	
	/**
	 * Get the <tt>SessionFactory</tt> underlying this <tt>Database</tt>.
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	
	/**
	 * Get the <tt>Transaction</tt> associatedm with the current thread.
	 */
	public Transaction currentTransaction() {
		return (Transaction) threadTransaction.get();
	}
	void associateThread(Transaction trans) {
		threadTransaction.set(trans);
	}
	void disassociateThread() {
		threadTransaction.set(null);
	}
	
	/**
	 * Specify the underlying <tt>SessionFactory</tt>, by passing a JNDI name.
	 * The <tt>accessMode</tt> is ignored by Hibernate.
	 * @see org.odmg.Database#open(String, int)
	 */
	public void open(String name, int accessMode) throws ODMGException {
		try {
			new Configuration().configure();
		}
		catch (HibernateException he) {
			throw new ODMGException( he.getMessage() );
		}
		sessionFactory = (SessionFactory) SessionFactoryObjectFactory.getNamedInstance(name);
		if (sessionFactory==null) throw new ODMGException("No SessionFactory was associated with the given JDNI name");
		/*try {
			sessionFactory = (SessionFactory) NamingHelper.getInitialContext( Environment.getProperties() ).lookup(name);
		}
		catch (NamingException ne) {
			throw new ODMGException( ne.getMessage() );
		}*/
	}
	
	/**
	 * Close the <tt>Database</tt> (but not the underlying <tt>SessionFactory</tt>).
	 * @see org.odmg.Database#close()
	 */
	public void close() throws ODMGException {
		//TODO: Wait for all transactions to complete?
		//if ( !threadTransactions.isEmpty() ) throw new ODMGException("Running transactions");
		sessionFactory = null;
		//TODO: remove it from ThreadLocal storage on Implementation
	}
	
	/**
	 * Bind a name to a persistent object, making the object persistent if necessary.
	 * @see org.odmg.Database#bind(Object, String)
	 */
	public void bind(Object object, String name) throws ObjectNameNotUniqueException {
		try {
			Session s = getSession();
			Name nameObj = new Name( name, object.getClass(), s.save(object) );
			s.save(nameObj);
			//TODO: handle ObjectNameNotUniqueException properly
		}
		catch (HibernateException he) {
			throw new ODMGRuntimeException( he.getMessage() );
		}
	}
	
	/**
	 * Retrieve the persistent object bound to the given name.
	 * @see org.odmg.Database#lookup(String)
	 */
	public Object lookup(String name) throws ObjectNameNotFoundException {
		try {
			Session s = getSession();
			Name nameObj;
			try {
				nameObj = (Name) s.load(Name.class, name);
			}
			catch (ObjectNotFoundException onfe) {
				throw new ObjectNameNotFoundException();
			}
			return s.load( nameObj.getPersistentClass(), nameObj.getId() );
		}
		catch (HibernateException he) {
			throw new ODMGRuntimeException( he.getMessage() );
		}
	}
	
	/**
	 * Unbind the given name.
	 * @see org.odmg.Database#unbind(String)
	 */
	public void unbind(String name) throws ObjectNameNotFoundException {
		try {
			Session s = getSession();
			Name nameObj;
			try {
				nameObj = (Name) s.load(Name.class, name);
			}
			catch (ObjectNotFoundException onfe) {
				throw new ObjectNameNotFoundException();
			}
			s.delete(nameObj);
		}
		catch (HibernateException he) {
			throw new ODMGRuntimeException( he.getMessage() );
		}
	}
	
	/**
	 * Make the given object persistent.
	 * @see org.odmg.Database#makePersistent(Object)
	 */
	public void makePersistent(Object object) {
		try {
			getSession().save(object);
		}
		catch (HibernateException he) {
			throw new ODMGRuntimeException( he.getMessage() );
		}
	}
	/**
	 * Delete the given object from the database.
	 * @see org.odmg.Database#deletePersistent(Object)
	 */
	public void deletePersistent(Object object) {
		try {
			getSession().delete(object);
		}
		catch (HibernateException he) {
			throw new ODMGRuntimeException( he.getMessage() );
		}
	}
	
}






