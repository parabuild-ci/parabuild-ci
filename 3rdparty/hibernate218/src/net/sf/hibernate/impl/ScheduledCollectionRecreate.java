//$Id: ScheduledCollectionRecreate.java,v 1.10 2004/06/04 01:27:41 steveebersole Exp $
package net.sf.hibernate.impl;

import java.io.Serializable;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.cache.CacheException;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.collection.PersistentCollection;
import net.sf.hibernate.engine.SessionImplementor;

final class ScheduledCollectionRecreate  extends ScheduledCollectionAction implements SessionImpl.Executable, Serializable {

	private final PersistentCollection collection;

	public ScheduledCollectionRecreate(PersistentCollection collection, CollectionPersister persister, Serializable id, SessionImplementor session)
	throws CacheException {
		super(persister, id, session);
		this.collection = collection;
	}

	public void execute() throws HibernateException {
		getPersister().recreate( collection, getId(), getSession() );
		evict();
	}

}







