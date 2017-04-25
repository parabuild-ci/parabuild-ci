//$Id: ScheduledIdentityInsertion.java,v 1.7 2005/01/10 03:10:24 oneovthafew Exp $
package net.sf.hibernate.impl;

import java.io.Serializable;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.persister.ClassPersister;

final class ScheduledIdentityInsertion extends ScheduledEntityAction implements SessionImpl.Executable, Serializable {
	private final Object[] state;
	private Serializable generatedId;

	public ScheduledIdentityInsertion(Object[] state, Object instance, ClassPersister persister, SessionImplementor session)
	throws HibernateException {
		super(session, null, instance, persister);
		this.state = state;
	}

	public void execute() throws HibernateException {
		ClassPersister persister = getPersister();
		SessionImplementor session = getSession();
		Object object = getInstance();

		// Don't need to lock the cache here, since if someone
		// else inserted the same pk first, the insert would fail

		generatedId = persister.insert(state, object, session);

		//TODO: this bit actually has to be called after all cascades!
		/*if ( persister.hasCache() && !persister.isCacheInvalidationRequired() ) {
			cacheEntry = new CacheEntry(object, persister, session);
			persister.getCache().insert(generatedId, cacheEntry);
		}*/
	}

	//Make 100% certain that this is called before any subsequent ScheduledUpdate.afterTransactionCompletion()!!
	public void afterTransactionCompletion(boolean success) throws HibernateException {
		//TODO: reenable
		/*ClassPersister persister = getPersister();
		if ( success && persister.hasCache() && !persister.isCacheInvalidationRequired() ) {
			persister.getCache().afterInsert( getGeneratedId(), cacheEntry );
		}*/
	}

	//TODO: remove
	public boolean hasAfterTransactionCompletion() {
		return false;
	}

	public final Serializable getGeneratedId() {
		return generatedId;
	}

}







