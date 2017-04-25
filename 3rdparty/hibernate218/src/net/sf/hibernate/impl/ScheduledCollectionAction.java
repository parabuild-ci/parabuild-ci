//$Id: ScheduledCollectionAction.java,v 1.13 2004/09/04 01:17:48 oneovthafew Exp $
package net.sf.hibernate.impl;

import java.io.Serializable;
import java.io.ObjectInputStream;
import java.io.IOException;

import net.sf.hibernate.cache.CacheException;
import net.sf.hibernate.cache.CacheConcurrencyStrategy.SoftLock;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.impl.SessionImpl.Executable;
import net.sf.hibernate.MappingException;

abstract class ScheduledCollectionAction implements Executable, Serializable {

	private transient CollectionPersister persister;
	private Serializable id;
	private SessionImplementor session;
	private SoftLock lock;
	private String collectionRole;

	public ScheduledCollectionAction(CollectionPersister persister, Serializable id, SessionImplementor session)
	throws CacheException {
		this.persister = persister;
		this.session = session;
		this.id = id;
		this.collectionRole = persister.getRole();
	}

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		ois.defaultReadObject();
		try {
			persister = session.getFactory().getCollectionPersister(collectionRole);
		}
		catch(MappingException e) {
			throw new IOException("Unable to resolve collection persister on deserialization");
		}
	}

	public void afterTransactionCompletion(boolean success) throws CacheException {
		if ( persister.hasCache() ) persister.getCache().release(id, lock);
	}

	public boolean hasAfterTransactionCompletion() {
		return persister.hasCache();
	}

	public Serializable[] getPropertySpaces() {
		return new Serializable[] { persister.getCollectionSpace() };
	}

	protected final CollectionPersister getPersister() {
		return persister;
	}

	protected final Serializable getId() {
		return id;
	}

	protected final SessionImplementor getSession() {
		return session;
	}

	public final void beforeExecutions() throws CacheException {
		// we need to obtain the lock before any actions are
		// executed, since this may be an inverse="true"
		// bidirectional association and it is one of the
		// earlier entity actions which actually updates
		// the database (this action is resposible for
		// second-level cache invalidation only)
		if ( persister.hasCache() ) lock = persister.getCache().lock(id, null); //collections don't have version numbers :-(
	}

	protected final void evict() throws CacheException {
		if ( persister.hasCache() ) persister.getCache().evict(id);
	}

}






