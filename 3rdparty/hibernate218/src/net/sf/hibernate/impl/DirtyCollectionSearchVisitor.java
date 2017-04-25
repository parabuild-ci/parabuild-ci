//$Id: DirtyCollectionSearchVisitor.java,v 1.6 2004/06/04 05:43:47 steveebersole Exp $
package net.sf.hibernate.impl;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.collection.PersistentCollection;
import net.sf.hibernate.type.PersistentCollectionType;

/**
 * Do we have a dirty collection here?
 * 1. if it is a new application-instantiated collection, return true (does not occur anymore!)
 * 2. if it is a component, recurse
 * 3. if it is a wrappered collection, ask the collection entry
 * 
 * @author Gavin King
 */
class DirtyCollectionSearchVisitor extends AbstractVisitor {
	
	private boolean dirty = false;

	DirtyCollectionSearchVisitor(SessionImpl session) {
		super(session);
	}
	
	boolean wasDirtyCollectionFound() {
		return dirty;
	}

	Object processCollection(Object collection, PersistentCollectionType type)
	throws HibernateException {
		
		if (collection!=null) {
			
			SessionImpl session = getSession();
			
			final PersistentCollection coll;
			if ( type.isArrayType() ) {
				 coll = session.getArrayHolder(collection);
				// if no array holder we found an unwrappered array (this can't occur,
				// because we now always call wrap() before getting to here)
				// return (ah==null) ? true : searchForDirtyCollections(ah, type);
			}
			else {
				// if not wrappered yet, its dirty (this can't occur, because
				// we now always call wrap() before getting to here)
				// return ( ! (obj instanceof PersistentCollection) ) ?
				//true : searchForDirtyCollections( (PersistentCollection) obj, type );
				coll = (PersistentCollection) collection;
			}
			
			if ( session.collectionIsDirty(coll) ) {
				dirty=true; 
				return null; //NOTE: EARLY EXIT!
			}
		}
		
		return null;
	}

}
