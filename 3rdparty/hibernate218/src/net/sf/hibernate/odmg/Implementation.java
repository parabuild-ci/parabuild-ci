//$Id: Implementation.java,v 1.5 2004/06/04 01:28:50 steveebersole Exp $
package net.sf.hibernate.odmg;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.odmg.DArray;
import org.odmg.DBag;
import org.odmg.DList;
import org.odmg.DMap;
import org.odmg.DSet;
import org.odmg.ODMGException;
import org.odmg.ODMGRuntimeException;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.collection.Bag;
import net.sf.hibernate.collection.List;
import net.sf.hibernate.collection.Map;
import net.sf.hibernate.collection.Set;
import net.sf.hibernate.engine.SessionImplementor;
/**
 * Singleton class implementing the ODMG <tt>Implementation</tt> interface.
 * Maintains a reference to a default <tt>Database</tt> and also associations
 * between a <tt>Database</tt> and the creating <tt>Thread</tt>.
 */
public class Implementation implements org.odmg.Implementation {
	
	private Database database;
	private final ThreadLocal threadDatabase = new ThreadLocal();
	private static final Implementation INSTANCE = new Implementation();
	
	/**
	 * Get the singleton instance
	 */
	public static Implementation getInstance() {
		return INSTANCE;
	}
	/**
	 * Instantiate a new <tt>Transaction</tt> associated with the current
	 * thread.
	 * @see org.odmg.Implementation#newTransaction()
	 */
	public org.odmg.Transaction newTransaction() {
		try {
			return new Transaction( currentDatabase() );
		}
		catch (ODMGException ode) {
			throw new ODMGRuntimeException( ode.getMessage() );
		}
	}
	
	/**
	 * Get the <tt>Database</tt> associated with the current thread (the most recent
	 * database created by the current thread or the default database).
	 */
	public Database currentDatabase() {
		Database db = (Database) threadDatabase.get();
		if (db!=null) {
			return db;
		}
		else {
			return database;
		}
	}
	/**
	 * Get the <tt>Transaction</tt> associated with the current thread.
	 * @see org.odmg.Implementation#currentTransaction()
	 */
	public org.odmg.Transaction currentTransaction() {
		return currentDatabase().currentTransaction();
	}
	/**
	 * Create a new <tt>Database</tt> and associate it with the current thread.
	 * The first <tt>Database</tt> created by this method becomes the "default"
	 * database that all threads that have not created databases are associated
	 * with.
	 * @see org.odmg.Implementation#newDatabase()
	 */
	public synchronized org.odmg.Database newDatabase() {
		Database db = new Database();
		if (database==null) database=db;
		threadDatabase.set(db);
		return db;
	}
	/**
	 * Instantiate an <tt>OQLQuery</tt> for the <tt>Database</tt> associated with
	 * the current thread
	 * @see org.odmg.Implementation#newOQLQuery()
	 */
	public org.odmg.OQLQuery newOQLQuery() {
		return new OQLQuery( currentDatabase() );
	}
	/**
	 * @see org.odmg.Implementation#newDList()
	 */
	public DList newDList() {
		return new List( (SessionImplementor) currentDatabase().getSession(), new ArrayList() );
	}
	/**
	 * @see org.odmg.Implementation#newDBag()
	 */
	public DBag newDBag() {
		return new Bag( (SessionImplementor) currentDatabase().getSession(), new ArrayList() );
	}
	/**
	 * @see org.odmg.Implementation#newDSet()
	 */
	public DSet newDSet() {
		return new Set( (SessionImplementor) currentDatabase().getSession(), new HashSet() );
	}
	/**
	 * @see org.odmg.Implementation#newDArray()
	 */
	public DArray newDArray() {
		return new List( (SessionImplementor) currentDatabase().getSession(), new ArrayList() );
	}
	/**
	 * @see org.odmg.Implementation#newDMap()
	 */
	public DMap newDMap() {
		return new Map( (SessionImplementor) currentDatabase().getSession(), new HashMap() );
	}
	/**
	 * Get the stringified identifier of the given object.
	 * @see org.odmg.Implementation#getObjectId(Object)
	 */
	public String getObjectId(Object obj) {
		try {
			return database.getSession().getIdentifier(obj).toString();
		}
		catch (HibernateException he) {
			throw new ODMGRuntimeException( he.getMessage() );
		}
	}
	/**
	 * Get the <tt>Database</tt> associated with the current thread (the most recent
	 * database created by the current thread or the default database).
	 * @see org.odmg.Implementation#getDatabase(Object)
	 */
	public org.odmg.Database getDatabase(Object obj) {
		//TODO: improve this
		return currentDatabase();
	}
	
}






