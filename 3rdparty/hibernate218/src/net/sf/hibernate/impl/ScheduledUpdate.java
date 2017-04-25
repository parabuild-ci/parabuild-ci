//$Id: ScheduledUpdate.java,v 1.12 2004/09/04 03:03:09 oneovthafew Exp $
package net.sf.hibernate.impl;

import java.io.Serializable;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.cache.CacheException;
import net.sf.hibernate.cache.CacheConcurrencyStrategy.SoftLock;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.persister.ClassPersister;

final class ScheduledUpdate extends ScheduledEntityAction implements SessionImpl.Executable, Serializable {

	private final Object[] fields;
	private final Object[] oldFields;
	private final Object lastVersion;
	private final Object nextVersion;
	private final int[] dirtyFields;
	private final Object[] updatedState;
	private CacheEntry cacheEntry;
	private SoftLock lock;

	public ScheduledUpdate(
		Serializable id,
		Object[] fields,
		int[] dirtyProperties,
		Object[] oldFields,
		Object lastVersion,
		Object nextVersion,
		Object instance,
		Object[] updatedState,
		ClassPersister persister,
		SessionImplementor session
	) throws HibernateException {
		super(session, id, instance, persister);
		this.fields = fields;
		this.oldFields = oldFields;
		this.lastVersion = lastVersion;
		this.nextVersion = nextVersion;
		this.dirtyFields = dirtyProperties;
		this.updatedState = updatedState;
	}

	public void execute() throws HibernateException {
		Serializable id = getId();
		ClassPersister persister = getPersister();
		SessionImplementor session = getSession();
		Object instance = getInstance();

		if ( persister.hasCache() ) lock = persister.getCache().lock(id, lastVersion);

		persister.update(id, fields, dirtyFields, oldFields, lastVersion, instance, session);
		session.postUpdate(instance, updatedState, nextVersion);

		if ( persister.hasCache() ) {
			if ( persister.isCacheInvalidationRequired() ) {
				persister.getCache().evict(id);
			}
			else {
				//TODO: inefficient if that cache is just going to ignore the updated state!
				cacheEntry = new CacheEntry( instance, persister, getSession() );
				persister.getCache().update(id, cacheEntry);
			}
		}
	}

	public void afterTransactionCompletion(boolean success) throws CacheException {
		ClassPersister persister = getPersister();
		if ( persister.hasCache() ) {
			if ( success && !persister.isCacheInvalidationRequired() ) {
				persister.getCache().afterUpdate( getId(), cacheEntry, nextVersion, lock );
			}
			else {
				persister.getCache().release( getId(), lock );
			}
		}
	}

}







