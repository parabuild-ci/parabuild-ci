//$Id: OnLockVisitor.java,v 1.6 2004/06/04 05:43:47 steveebersole Exp $
package net.sf.hibernate.impl;

import java.io.Serializable;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.collection.PersistentCollection;
import net.sf.hibernate.engine.CollectionSnapshot;
import net.sf.hibernate.type.PersistentCollectionType;

/**
 * When a transient entity is passed to lock(), we must inspect all its collections and
 * 1. associate any uninitialized PersistentCollections with this session
 * 2. associate any initialized PersistentCollections with this session, using the
 *    existing snapshot
 * 3. throw an exception for each "new" collection
 * 
 * @author Gavin King
 */
class OnLockVisitor extends ReattachVisitor {

	public OnLockVisitor(
		SessionImpl session,
		Serializable key) {
		super(session, key);
	}

	Object processCollection(Object collection, PersistentCollectionType type)
		throws HibernateException {
		
		SessionImpl session = getSession();
		CollectionPersister persister = session.getCollectionPersister( type.getRole() );
		
		if (collection==null) {
			//do nothing
		}
		else if ( collection instanceof PersistentCollection ) {
			PersistentCollection coll = (PersistentCollection) collection;

			if ( coll.setCurrentSession(session) ) {

				CollectionSnapshot snapshot = coll.getCollectionSnapshot();
				if ( SessionImpl.isOwnerUnchanged( snapshot, persister, getKey() ) ) {
					// a "detached" collection that originally belonged to the same entity
					if ( snapshot.getDirty() ) {
						throw new HibernateException("reassociated object has dirty collection"); 
					}
					session.reattachCollection(coll, snapshot);
				}
				else {
					// a "detached" collection that belonged to a different entity
					throw new HibernateException("reassociated object has dirty collection reference"); 
				}

			}
			else {
				// a collection loaded in the current session
				// can not possibly be the collection belonging
				// to the entity passed to update()
				throw new HibernateException("reassociated object has dirty collection reference"); 
			}

		}
		else {
			// brand new collection
			//TODO: or an array!! we can't lock objects with arrays now??
			throw new HibernateException("reassociated object has dirty collection reference"); 
		}

		return null;

	}

}
