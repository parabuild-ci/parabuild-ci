// $Id: ManagedConnectionImpl.java,v 1.10 2004/08/02 19:20:25 turin42 Exp $
package net.sf.hibernate.jca;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.sql.Connection;

import javax.resource.NotSupportedException;
import javax.resource.ResourceException;
import javax.resource.spi.ConnectionEvent;
import javax.resource.spi.ConnectionEventListener;
import javax.resource.spi.ConnectionRequestInfo;
import javax.resource.spi.LocalTransaction;
import javax.resource.spi.ManagedConnection;
import javax.resource.spi.ManagedConnectionFactory;
import javax.resource.spi.ManagedConnectionMetaData;
import javax.security.auth.Subject;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;
import net.sf.hibernate.engine.SessionImplementor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of JCA Managed Connection.
 */
public class ManagedConnectionImpl implements ManagedConnection, XAResource {
	
	/*
	 * @todo JCA 1.0, 5.10.1
	 * A resource adapter is required to provide support for basic error
	 * logging and tracing by implementing the following methods:
	 *   - ManagedConnectionFactory.set/getLogWriter
	 *   - ManagedConnection.set/getLogWriter
	 */
	private static final Log log =
	LogFactory.getLog(ManagedConnectionImpl.class);
	
	// the factory that created this managed connection
	private ManagedConnectionFactoryImpl mcf;
	
	// as per JCA 1.0 spec 5.6 when this is not null then logging has been
	// enabled by the application server and we log to this writer
	private PrintWriter logWriter;
	
	// registered listeners from the application server
	private final Collection listeners = new ArrayList();
	
	/**
	 * A set (actually a linked list) of JCASessionImpl handles associated with
	 * this managed connection. First element of the list is the currect active
	 * handle.
	 *
	 * <p>It is necessary to synchronize access to this list becase
	 * <code>dissociateConnection</code> can be called asynchronously
	 * from a context of a different transaction.</p>
	 */
	private final LinkedList handles = new LinkedList();

	// Hibernate session
	private SessionImplementor session;
	
	ManagedConnectionImpl(final ManagedConnectionFactoryImpl mcf) {
		log.trace("Constructor called");
		
		this.mcf = mcf;
	}
	
	/**
	 * Creates and returns a new Hibernate Session.
	 *
	 * Will throw a ResourceException if the session factory
	 * fails to create a session
	 */
	public Object getConnection(final Subject subject,
	                            final ConnectionRequestInfo
	        connectionRequestInfo) throws ResourceException {
		
		log.trace(
			"getConnection called with subject["
			+ subject
			+ " ] connection request info["
			+ connectionRequestInfo
			+ "]");

		// make sure we have a session
		initializeSession();

		final JCASessionImpl active = new JCASessionImpl(this);

		// add the session handle to the collection of handles
		// on this connection
		synchronized (handles) {
			handles.addFirst(active);
		}

		if (log.isDebugEnabled()) {
			log.debug("Added session handle - "
			        + active
			        + " to handles now at size "
			        + handles.size());
		}

		return active;
	}
			
	/**
	 * Creates a session from the session factory
	 */
	private void initializeSession() throws ResourceException {
		// single threaded, no need to synchronize
		if (session == null) {
			try {
				session = (SessionImplementor)
						mcf.getSessionFactory().openSession();
			} catch (HibernateException e) {
				ResourceException re = new ResourceException("Failed to open Hibernate session");
				re.setLinkedException(e);
				throw re;
			}
		}
	}
			
	/**
	 * Returns new ResourceException.
	 */
	private static ResourceException newResourceException(
		final String message,
		final Exception e
	) {
		final ResourceException re = new ResourceException(message);
		re.setLinkedException(e);
		return re;
	}

	/**
	 * Closes the connection to the database
	 *
	 * @throws ResourceException cannot close connection (with the linked exception)
	 */
	public void destroy() throws ResourceException {
		if ( log.isTraceEnabled() ) {
			log.trace("destroy called on " + this);
		}
	}

	/**
	 * Cleans up connection handles so they can't be used again
	 * But the physical connection is kept open
	 */
	public void cleanup() throws ResourceException {
		if ( log.isTraceEnabled() ) {
			log.trace("cleanup called on " + this);
		}

		// JCA 1.0, 5.5.4
		// The cleanup is required to invalidate all connection
		// handles created using this ManagedConnection instance.
		synchronized (handles) {
			for (Iterator i = handles.iterator(); i.hasNext();) {
				JCASessionImpl lc = (JCASessionImpl) i.next();
				lc.setManagedConnection(null);
			}
			handles.clear();
		}

		ResourceException re = null;

		try {
			//if session is closed before transaction end it is null here
			if( session != null) session.close();
		}
		catch (HibernateException e) {
			final String message =
			"Exception closing Hibernate session " + session;
			re = newResourceException(message, e);
		}
		finally {
			session = null;
		}

		if (re != null) {
			throw re;
		}
	}
			
	/**
	 * Associates the session handle with this managed connection
	 */
	public void associateConnection(final Object object)
	        throws ResourceException {
		if (!(object instanceof JCASessionImpl)) {
			throw new ResourceException("Wrong kind of connection handle to associate " + object);
		}
		final JCASessionImpl handle = (JCASessionImpl) object;
		if (handle.getManagedConnextion() != this) {
			handle.getManagedConnextion().dissociateConnection(handle);
			handle.setManagedConnection(this);
			synchronized (handles) {
				handles.addFirst(handle);
			}
		}
	}
				
	private void dissociateConnection(final JCASessionImpl handle) {
		synchronized (handles) {
			handles.remove(handle);
		}
	}

	public void addConnectionEventListener(ConnectionEventListener listener) {
		listeners.add(listener);
	}

	public void removeConnectionEventListener(ConnectionEventListener listener) {
		listeners.remove(listener);
	}

	public XAResource getXAResource() throws ResourceException {
		return this;
	}

	public LocalTransaction getLocalTransaction() throws ResourceException {
		// JCA 1.0, section 6.1.10
		// both LocalTransaction and XATransaction resource adapters
		// support local transactions
		// At the same time all transactions will enlist at least Hibernate
		// session and database connection, so support for local transactions
		// is really redundant.
		throw new NotSupportedException("LocalTransaction is not supported!");
	}

	public ManagedConnectionMetaData getMetaData() throws ResourceException {
		return new MetaDataImpl(this);
	}

	public void setLogWriter(PrintWriter out) throws ResourceException {
		this.logWriter = out;
	}

	public PrintWriter getLogWriter() throws ResourceException {
		return logWriter;
	}

	//
	// XAResource implementation
	//

	// @todo
	// Currently, XAResource implementation does nothing. In the future,
	// in order to support JCS read-write cache it will be necessary
	// to implement prepare/commit/rollback protocol.

	// @todo JCA 6.6.2
	// RM must ensure that TM invokes XAResource calls in the legal sequence,
	// and must return XAER_PROTO or other suitable error if the caller TM
	// violates the state tables (as defined in Chapter 6 of the XA
	// specification (refer [4]).
	public void commit(Xid arg0, boolean arg1) throws XAException {}
	public void end(Xid arg0, int arg1) throws XAException {}
	public void forget(Xid arg0) throws XAException {}
	public int getTransactionTimeout() throws XAException { return 0; }
	public boolean isSameRM(XAResource arg0) throws XAException { return false; }
	public int prepare(Xid arg0) throws XAException { return XAResource.XA_RDONLY; }
	public Xid[] recover(int arg0) throws XAException { return new Xid[0]; }
	public void rollback(Xid arg0) throws XAException {}
	public boolean setTransactionTimeout(int arg0) throws XAException { return false; }
	public void start(Xid arg0, int arg1) throws XAException {}
				
	//
	// package private level methods
	//

	ManagedConnectionFactory getManagedConnectionFactory() {
		return mcf;
	}

	void closeHandle(Session handle) {

		// remove the handle from the collection safely
		synchronized (handles) {
			handles.remove(handle);
		}

		// create a connection closed event and send
		ConnectionEvent ce =
		new ConnectionEvent(this, ConnectionEvent.CONNECTION_CLOSED);
		ce.setConnectionHandle(handle);

		sendEvent(ce);
	}
				
	void sendEvent(final ConnectionEvent event) {
		int type = event.getId();

		if (log.isDebugEnabled()) {
			log.debug("Sending connection event: " + type);
		}

		// convert to an array to avoid concurrent modification exceptions
		ConnectionEventListener[] list =
		        (ConnectionEventListener[]) listeners.toArray(new ConnectionEventListener[listeners.size()]);

		for (int i = 0; i < list.length; i++) {
			switch (type) {
				case ConnectionEvent.CONNECTION_CLOSED:
					list[i].connectionClosed(event);
					break;

				case ConnectionEvent.LOCAL_TRANSACTION_STARTED:
					list[i].localTransactionStarted(event);
					break;

				case ConnectionEvent.LOCAL_TRANSACTION_COMMITTED:
					list[i].localTransactionCommitted(event);
					break;

				case ConnectionEvent.LOCAL_TRANSACTION_ROLLEDBACK:
					list[i].localTransactionRolledback(event);
					break;

				case ConnectionEvent.CONNECTION_ERROR_OCCURRED:
					list[i].connectionErrorOccurred(event);
					break;

				default :
					throw new IllegalArgumentException("Illegal eventType: " + type);
			}
		}
	}
						
	Session getSession(JCASessionImpl handle) {
		// JCA 1.0, 5.5.4
		// Ensure that there is at most one connection handle associated
		// actively with a ManagedConnection instance. [skiped] Any operations
		// on the ManagedConnection from any previously created connection
		// handles should result in an application level exception.

		// this might be pretty bad for performance, profile and find a
		// better way if it is really bad
		synchronized (handles) {
			if ( handles.size() > 0 && handles.get(0) == handle) {
				return session;
			}
			else {
				final String message = "Inactive logical session handle called";
				// cannot throw HibernateException because not all Session
				// methods throws it. This is incompatible with the spec!
				throw new IllegalStateException(message);
			}
		}
	}

	Connection getSqlConnection() throws ResourceException {

		initializeSession();
		Connection conn = null;
		try {
			conn = session.connection();
		} catch (HibernateException ex) {
			ResourceException re = new ResourceException("Failed to get connection from session");
			re.setLinkedException(ex);
			throw re;
		}
		return conn;
	}
}