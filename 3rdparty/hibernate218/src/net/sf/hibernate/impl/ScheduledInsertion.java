//$Id: ScheduledInsertion.java,v 1.10 2004/09/04 03:03:09 oneovthafew Exp $
package net.sf.hibernate.impl;

import java.io.Serializable;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.persister.ClassPersister;

final class ScheduledInsertion extends ScheduledEntityAction implements SessionImpl.Executable, Serializable {
	private final Object[] state;
	private CacheEntry cacheEntry;
	private final Object version;

	public ScheduledInsertion(
			Serializable id, 
			Object[] state, 
			Object instance, 
			Object version,
			ClassPersister persister, 
			SessionImplementor session)
	throws HibernateException {
		super(session, id, instance, persister);
		this.state = state;
		this.version = version;
	}

	public void execute() throws HibernateException {
		ClassPersister persister = getPersister();
		SessionImplementor session = getSession();
		Object object = getInstance();
		Serializable id = getId();

		// Don't need to lock the cache here, since if someone
		// else inserted the same pk first, the insert would fail

		persister.insert(id, state, object, session);
		session.postInsert(object);

		if ( persister.hasCache() && !persister.isCacheInvalidationRequired() ) {
			cacheEntry = new CacheEntry(object, persister, session);
			persister.getCache().insert(id, cacheEntry);
		}
	}

	//Make 100% certain that this is called before any subsequent ScheduledUpdate.afterTransactionCompletion()!!
	public void afterTransactionCompletion(boolean success) throws HibernateException {
		ClassPersister persister = getPersister();
		if ( success && persister.hasCache() && !persister.isCacheInvalidationRequired() ) {
			persister.getCache().afterInsert( getId(), cacheEntry, version );
		}
	}

}







