//$Id: ScheduledCollectionRemove.java,v 1.11 2004/06/04 01:27:41 steveebersole Exp $
package net.sf.hibernate.impl;

import java.io.Serializable;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.cache.CacheException;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.engine.SessionImplementor;

final class ScheduledCollectionRemove extends ScheduledCollectionAction implements SessionImpl.Executable, Serializable {

	private boolean emptySnapshot;

	public ScheduledCollectionRemove(CollectionPersister persister, Serializable id, boolean emptySnapshot, SessionImplementor session)
	throws CacheException {
		super(persister, id, session);
		this.emptySnapshot = emptySnapshot;
	}

	public void execute() throws HibernateException {
		if (!emptySnapshot) getPersister().remove( getId(), getSession() );
		evict();
	}


}







