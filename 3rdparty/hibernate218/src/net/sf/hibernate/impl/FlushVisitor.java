//$Id: FlushVisitor.java,v 1.6 2004/06/04 05:43:47 steveebersole Exp $
package net.sf.hibernate.impl;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.collection.PersistentCollection;
import net.sf.hibernate.type.PersistentCollectionType;

/**
 * Process collections reachable from an entity. This 
 * visitor assumes that wrap was already performed for
 * the entity.
 * 
 * @author Gavin King
 */
class FlushVisitor extends AbstractVisitor {
	
	private Object owner;

	Object processCollection(Object collection, PersistentCollectionType type)
	throws HibernateException {
		
		if (collection!=null) {
			SessionImpl session = getSession();
			final PersistentCollection coll;
			if ( type.isArrayType() ) {
				coll = session.getArrayHolder(collection);
			}
			else {
				coll = (PersistentCollection) collection;
			}
			
			session.updateReachableCollection(coll, type, owner);
		}
		
		return null;

	}

	FlushVisitor(SessionImpl session, Object owner) {
		super(session);
		this.owner = owner;
	}

}
