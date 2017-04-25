//$Id: OnUpdateVisitor.java,v 1.6 2004/06/04 05:43:47 steveebersole Exp $
package net.sf.hibernate.impl;

import java.io.Serializable;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.collection.PersistentCollection;
import net.sf.hibernate.engine.CollectionSnapshot;
import net.sf.hibernate.type.PersistentCollectionType;

/**
 * When an entity is passed to update(), we must inspect all its collections and
 * 1. associate any uninitialized PersistentCollections with this session
 * 2. associate any initialized PersistentCollections with this session, using the
 *    existing snapshot
 * 3. execute a collection removal (SQL DELETE) for each null collection property
 *    or "new" collection
 * 
 * @author Gavin King
 */
class OnUpdateVisitor extends ReattachVisitor {
	
	OnUpdateVisitor(SessionImpl session, Serializable key) {
		super(session, key);
	}

	Object processCollection(Object collection, PersistentCollectionType type)
		throws HibernateException {
		
		SessionImpl session = getSession();
		Serializable key = getKey();
		CollectionPersister persister = session.getCollectionPersister( type.getRole() );
		
		if ( collection!=null && (collection instanceof PersistentCollection) ) {
			PersistentCollection wrapper = (PersistentCollection) collection;

			if ( wrapper.setCurrentSession(session) ) {
				//a "detached" collection!
				CollectionSnapshot snapshot = wrapper.getCollectionSnapshot();
				
				if ( !SessionImpl.isOwnerUnchanged(snapshot, persister, key) ) {
					// if the collection belonged to a different entity,
					// clean up the existing state of the collection
					session.removeCollection(persister, key);
				}
				
				session.reattachCollection(wrapper, snapshot);
			}
			else {
				// a collection loaded in the current session
				// can not possibly be the collection belonging
				// to the entity passed to update()
				session.removeCollection(persister, key);
			}

		}
		else {
			// null or brand new collection
			// this will also (inefficiently) handle arrays, which have
			// no snapshot, so we can't do any better
			session.removeCollection(persister, key);
			//processArrayOrNewCollection(collection, type);
		}

		return null;
	
	}

}
