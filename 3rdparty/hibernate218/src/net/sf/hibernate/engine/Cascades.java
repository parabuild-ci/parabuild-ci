//$Id: Cascades.java,v 1.21 2004/06/04 01:27:38 steveebersole Exp $
package net.sf.hibernate.engine;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.LockMode;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.ReplicationMode;
import net.sf.hibernate.collection.CollectionPersister;
import net.sf.hibernate.collection.PersistentCollection;
import net.sf.hibernate.persister.ClassPersister;
import net.sf.hibernate.type.AbstractComponentType;
import net.sf.hibernate.type.AssociationType;
import net.sf.hibernate.type.PersistentCollectionType;
import net.sf.hibernate.type.Type;

/**
 * Implements cascaded save / delete / update / lock / evict / replicate
 * 
 * @see net.sf.hibernate.type.AssociationType
 * @author Gavin King
 */
public final class Cascades {
	
	private Cascades() {}
	
	private static final Log log = LogFactory.getLog(Cascades.class);
	
	// The available cascade actions:
	
	/**
	 * A session action that may be cascaded from parent entity to its children
	 */
	public abstract static class CascadingAction {
		protected CascadingAction() {}
		/**
		 * cascade the action to the child object
		 */
		abstract void cascade(SessionImplementor session, Object child, Object anything) throws HibernateException;
		/**
		 * Should this action be cascaded to the given (possibly uninitialized) collection?
		 */
		abstract Iterator getCascadableChildrenIterator(PersistentCollectionType collectionType, Object collection);
		/**
		 * Do we need to handle orphan delete for this action?
		 */
		abstract boolean deleteOrphans();
	}
	
	/**
	 * @see net.sf.hibernate.Session#delete(Object)
	 */
	public static final CascadingAction ACTION_DELETE = new CascadingAction() {
		void cascade(SessionImplementor session, Object child, Object anything) throws HibernateException {
			log.trace("cascading to delete()");
			if ( session.isSaved(child) ) session.delete(child);
		}
		Iterator getCascadableChildrenIterator(PersistentCollectionType collectionType, Object collection) {
			// delete does cascade to uninitialized collections
			return getAllElementsIterator(collectionType, collection);
		}
		boolean deleteOrphans() {
			// orphans should be deleted during delete
			return true;
		}
	};
	
	/**
	 * @see net.sf.hibernate.Session#lock(Object, LockMode)
	 */
	public static final CascadingAction ACTION_LOCK = new CascadingAction() {
		void cascade(SessionImplementor session, Object child, Object anything) throws HibernateException {
			log.trace("cascading to lock()");
			session.lock( child, (LockMode) anything );
		}
		Iterator getCascadableChildrenIterator(PersistentCollectionType collectionType, Object collection) {
			// lock doesn't cascade to uninitialized collections
			return getLoadedElementsIterator(collectionType, collection);
		}
		boolean deleteOrphans() {
			//TODO: should orphans really be deleted during lock???
			return false;
		}
	};
	
	/**
	 * @see net.sf.hibernate.Session#evict(Object)
	 */
	public static final CascadingAction ACTION_EVICT = new CascadingAction() {
		void cascade(SessionImplementor session, Object child, Object anything) throws HibernateException {
			log.trace("cascading to evict()");
			session.evict(child);
		}
		Iterator getCascadableChildrenIterator(PersistentCollectionType collectionType, Object collection) {
			// evicts don't cascade to uninitialized collections
			return getLoadedElementsIterator(collectionType, collection);
		}
		boolean deleteOrphans() {
			return false;
		}
	};
	
	/**
	 * @see net.sf.hibernate.Session#saveOrUpdate(Object)
	 */
	public static final CascadingAction ACTION_SAVE_UPDATE = new CascadingAction() {
		void cascade(SessionImplementor session, Object child, Object anything) throws HibernateException {
			log.trace("cascading to saveOrUpdate()");
			session.saveOrUpdate(child);
		}
		Iterator getCascadableChildrenIterator(PersistentCollectionType collectionType, Object collection) {
			// saves / updates don't cascade to uninitialized collections
			return getLoadedElementsIterator(collectionType, collection);
		}
		boolean deleteOrphans() {
			// orphans should be deleted during save/update
			return true;
		}
	};
	
	/**
	 * @see net.sf.hibernate.Session#copy(Object)
	 */
	public static final CascadingAction ACTION_COPY = new CascadingAction() {
		void cascade(SessionImplementor session, Object child, Object anything) throws HibernateException {
			log.trace("cascading to copy()");
			session.copy( child, (java.util.Map) anything );
		}
		Iterator getCascadableChildrenIterator(PersistentCollectionType collectionType, Object collection) {
			// saves / updates don't cascade to uninitialized collections
			return getLoadedElementsIterator(collectionType, collection);
		}
		boolean deleteOrphans() {
			// orphans should not be deleted during copy??
			return false;
		}
	};
	
	public static final CascadingAction ACTION_REPLICATE = new CascadingAction() {
		void cascade(SessionImplementor session, Object child, Object anything) throws HibernateException {
			log.trace("cascading to replicate()");
			session.replicate( child, (ReplicationMode) anything );
		}
		Iterator getCascadableChildrenIterator(PersistentCollectionType collectionType, Object collection) {
			// replicate does cascade to uninitialized collections
			return getLoadedElementsIterator(collectionType, collection);
		}
		boolean deleteOrphans() {
			return false; //I suppose?
		}
	};
	
	private static boolean collectionIsInitialized(Object collection) {
		return !(collection instanceof PersistentCollection) || ( (PersistentCollection) collection ).wasInitialized();
	}
	
	// The types of children to cascade to:
	
	/**
	 * A cascade point that occurs just after the insertion of the parent entity and
	 * just before deletion
	 */
	public static final int CASCADE_AFTER_INSERT_BEFORE_DELETE = 1;
	/**
	 * A cascade point that occurs just before the insertion of the parent entity and
	 * just after deletion
	 */
	public static final int CASCADE_BEFORE_INSERT_AFTER_DELETE = 2;
	/**
	 * A cascade point that occurs just after the insertion of the parent entity and
	 * just before deletion, inside a collection
	 */
	public static final int CASCADE_AFTER_INSERT_BEFORE_DELETE_VIA_COLLECTION = 3;
	/**
	 * A cascade point that occurs just after update of the parent entity
	 */
	public static final int CASCADE_ON_UPDATE = 0;
	/**
	 * A cascade point that occurs just after eviction of the parent entity from the
	 * session cache
	 */
	public static final int CASCADE_ON_EVICT = 0;
	/**
	 * A cascade point that occurs just after locking a transient parent entity into the
	 * session cache
	 */
	public static final int CASCADE_ON_LOCK = 0;
	/**
	 * A cascade point that occurs just after copying from a transient parent entity into 
	 * the object in the session cache
	 */
	public static final int CASCADE_ON_COPY = 0;
	
	// The allowable cascade styles for a property:
	
	/**
	 * A style of cascade that can be specified by the mapping for an association.
	 * The style is specified by the <tt>cascade</tt> attribute in the mapping file.
	 */
	public abstract static class CascadeStyle implements Serializable {
		protected CascadeStyle() {}
		/**
		 * Should the given action be cascaded?
		 */
		abstract boolean doCascade(CascadingAction action);
		/**
		 * Should we cascade to this particular child?
		 */
		/*boolean doCascade(SessionImplementor session, Object child) 
		throws HibernateException { 
			return true; 
		};*/
		boolean hasOrphanDelete() {
			return false;
		}
	}
	/**
	 * save / delete / update / evict / lock / replicate + delete orphans
	 */
	public static final CascadeStyle STYLE_ALL_DELETE_ORPHAN = new CascadeStyle() {
		boolean doCascade(CascadingAction action) {
			return true;
		}
		boolean hasOrphanDelete() {
			return true;
		}
	};
	/**
	 * save / delete / update / evict / lock / replicate
	 */
	public static final CascadeStyle STYLE_ALL = new CascadeStyle() {
		boolean doCascade(CascadingAction action) {
			return true;
		}
	};
	/**
	 * save / update / lock / replicate
	 */
	public static final CascadeStyle STYLE_SAVE_UPDATE = new CascadeStyle() {
		boolean doCascade(CascadingAction action) {
			return action==ACTION_SAVE_UPDATE || action==ACTION_LOCK || action==ACTION_REPLICATE || action==ACTION_COPY;
		}
	};
	/**
	 * save
	 */
	/*public static final CascadeStyle STYLE_SAVE = new CascadeStyle() {
		boolean doCascade(CascadingAction action) {
			return action==ACTION_SAVE_UPDATE;
		}
		boolean doCascade(SessionImplementor session, Object child) throws HibernateException { 
			return !session.isSaved(child);
		};
	};*/
	/**
	 * delete
	 */
	public static final CascadeStyle STYLE_ONLY_DELETE = new CascadeStyle() {
		boolean doCascade(CascadingAction action) {
			return action==ACTION_DELETE;
		}
	};
	/**
	 * delete + delete orphans
	 */
	public static final CascadeStyle STYLE_DELETE_ORPHAN = new CascadeStyle() {
		boolean doCascade(CascadingAction action) {
			return action==ACTION_DELETE;
		}
		boolean hasOrphanDelete() {
			return true;
		}
	};
	/**
	 * no cascades
	 */
	public static final CascadeStyle STYLE_NONE = new CascadeStyle() {
		boolean doCascade(CascadingAction action) {
			return action==ACTION_REPLICATE;
		}
	};
	
	// The allowable unsaved-value settings:
	
	/**
	 * A strategy for determining if an identifier value is an identifier of
	 * a new transient instance or a previously persistent transient instance.
	 * The strategy is determined by the <tt>unsaved-value</tt> attribute in
	 * the mapping file.
	 */
	public static class IdentifierValue {
		private final Object value;
		protected IdentifierValue() {
			this.value = null;
		}
		/**
		 * Assume the transient instance is newly instantiated if
		 * its identifier is null or equal to <tt>value</tt>
		 */
		public IdentifierValue(Object value) {
			this.value = value;
		}
		/**
		 * Does the given identifier belong to a new instance?
		 */
		public boolean isUnsaved(Serializable id) {
			if ( log.isTraceEnabled() ) log.trace("id unsaved-value: " + value);
			return id==null || value.equals(id);
		}
	}
	
	/**
	 * Always assume the transient instance is newly instantiated
	 */
	public static final IdentifierValue SAVE_ANY = new IdentifierValue() {
		public final boolean isUnsaved(Serializable id) {
			log.trace("id unsaved-value strategy ANY");
			return true;
		}
	};
	/**
	 * Never assume the transient instance is newly instantiated
	 */
	public static final IdentifierValue SAVE_NONE = new IdentifierValue() {
		public final boolean isUnsaved(Serializable id) {
			log.trace("id unsaved-value strategy NONE");
			return false;
		}
	};
	/**
	 * Assume the transient instance is newly instantiated if the identifier
	 * is null.
	 */
	public static final IdentifierValue SAVE_NULL = new IdentifierValue() {
		public final boolean isUnsaved(Serializable id) {
			log.trace("id unsaved-value strategy NULL");
			return id==null;
		}
	};
	
	/**
	 * A strategy for determining if a version value is an version of
	 * a new transient instance or a previously persistent transient instance.
	 * The strategy is determined by the <tt>unsaved-value</tt> attribute in
	 * the mapping file.
	 */
	public static class VersionValue {
		
		private final Object value;
		protected VersionValue() {
			this.value = null;
		}
		
		/**
		 * Assume the transient instance is newly instantiated if
		 * its version is null or equal to <tt>value</tt>
		 * @param value value to compare to
		 */
		public VersionValue(Object value) {
			this.value = value;
		}
		/**
		 * Does the given version belong to a new instance?
		 * 
		 * @param version version to check
		 * @return true is unsaved, false is saved, null is undefined
		 */
		public Boolean isUnsaved(Object version) throws MappingException  {
			if ( log.isTraceEnabled() ) log.trace("version unsaved-value: " + value);
			return version==null || value.equals(version) ? Boolean.TRUE : Boolean.FALSE;
		}
	}

	/**
	 * Assume the transient instance is newly instantiated if the version
	 * is null, otherwise assume it is a detached instance.
	 */
	public static final VersionValue VERSION_SAVE_NULL = new VersionValue() {
		public final Boolean isUnsaved(Object version) {
			log.trace("version unsaved-value strategy NULL");
			return version==null ? Boolean.TRUE : Boolean.FALSE;
		}
	};
	
	/**
	 * Assume the transient instance is newly instantiated if the version
	 * is null, otherwise defer to the identifier unsaved-value.
	 */
	public static final VersionValue VERSION_UNDEFINED = new VersionValue() {
		public final Boolean isUnsaved(Object version) {
			log.trace("version unsaved-value strategy UNDEFINED");
			return version==null ? Boolean.TRUE : null;
		}
	};
	
	/**
	 * Assume the transient instance is newly instantiated if the version
	 * is negative, otherwise assume it is a detached instance.
	 */
	public static final VersionValue VERSION_NEGATIVE = new VersionValue() {
		
		public final Boolean isUnsaved(Object version) throws MappingException {
			log.trace("version unsaved-value strategy NEGATIVE");

			if (version instanceof Number) {
				return ( (Number) version ).longValue() < 0l ? Boolean.TRUE : Boolean.FALSE;
			}
			else {
				throw new MappingException("unsaved-value NEGATIVE may only be used with short, int and long types");
			}
		}
	};
	
	/**
	 * Cascade an action to the child or children
	 */
	private static void cascade(
		SessionImplementor session, 
		Object child, 
		Type type, 
		CascadingAction action, 
		CascadeStyle style,
		int cascadeTo,
		Object anything
	) throws HibernateException {
		
		if (child!=null) {
			if ( type.isAssociationType() ) {
				if ( ( (AssociationType) type ).getForeignKeyDirection().cascadeNow(cascadeTo) ) {
					if ( type.isEntityType() || type.isObjectType() ) {
						/*if ( style.doCascade(session, child) )*/ action.cascade(session, child, anything);
					}
					else if ( type.isPersistentCollectionType() ) {
						final int cascadeVia;
						if ( cascadeTo==CASCADE_AFTER_INSERT_BEFORE_DELETE) {
							cascadeVia = CASCADE_AFTER_INSERT_BEFORE_DELETE_VIA_COLLECTION;
						}
						else {
							cascadeVia = cascadeTo;
						}
						PersistentCollectionType pctype = (PersistentCollectionType) type;
						CollectionPersister persister = session.getFactory().getCollectionPersister( pctype.getRole() );
						Type elemType = persister.getElementType();
						
						//cascade to current collection elements
						if ( elemType.isEntityType() || elemType.isObjectType() || elemType.isComponentType() ) {
							cascadeCollection(action, style, pctype, elemType, child, cascadeVia, session, anything);
						}
					}
						
				}
			}
			else if ( type.isComponentType() ) {
				AbstractComponentType componentType = (AbstractComponentType) type;
				Object[] children = componentType.getPropertyValues(child, session);
				Type[] types = componentType.getSubtypes();
				for ( int i=0; i<types.length; i++ ) {
					CascadeStyle componentPropertyStyle = componentType.cascade(i);
					if ( componentPropertyStyle.doCascade(action) ) cascade(
						session, children[i], types[i], action, componentPropertyStyle, cascadeTo, anything
					);
				}
			}
		}
	}
	
	/**
	 * Cascade an action from the parent object to all its children
	 */
	public static void cascade(
		SessionImplementor session, 
		ClassPersister persister, 
		Object parent, 
		Cascades.CascadingAction action, 
		int cascadeTo) throws HibernateException {
		
		cascade(session, persister, parent, action, cascadeTo, null);
	}
		
	/**
	 * Cascade an action from the parent object to all its children
	 */
	public static void cascade(
		SessionImplementor session, 
		ClassPersister persister, 
		Object parent, 
		Cascades.CascadingAction action, 
		int cascadeTo,
		Object anything) throws HibernateException {
		
		if ( persister.hasCascades() ) { // performance opt
			if ( log.isTraceEnabled() ) log.trace( "processing cascades for: " + persister.getClassName() );
			Type[] types = persister.getPropertyTypes();
			Cascades.CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();
			for ( int i=0; i<types.length; i++) {
				CascadeStyle style = cascadeStyles[i];
				if ( style.doCascade(action) ) {
					cascade( session, persister.getPropertyValue(parent,i), types[i], action, style, cascadeTo, anything );
				}
			}
			if ( log.isTraceEnabled() ) log.trace( "done processing cascades for: " + persister.getClassName() );
		}
	}
	
	/**
	 * Cascade to the collection elements
	 */
	private static void cascadeCollection(
		CascadingAction action, 
		CascadeStyle style,
		PersistentCollectionType collectionType,
		Type elemType, 
		Object child, 
		int cascadeVia,
		SessionImplementor session,
		Object anything
	) throws HibernateException {
		
		if ( log.isTraceEnabled() ) log.trace( "cascading to collection: " + collectionType.getRole() );
		Iterator iter = action.getCascadableChildrenIterator(collectionType, child);
		while ( iter.hasNext() ) cascade( session, iter.next(), elemType, action, style, cascadeVia, anything );
		
		// handle orphaned entities!!
		if ( style.hasOrphanDelete() && action.deleteOrphans() && child instanceof PersistentCollection ) {
			// we can do the cast since orphan-delete does not apply to:
			// 1. newly instantiated collections
			// 2. arrays (we can't track orphans for detached arrays)
			deleteOrphans( (PersistentCollection) child, session );
		}
	}
	
	/**
	 * Delete any entities that were removed from the collection
	 */
	private static void deleteOrphans(PersistentCollection pc, SessionImplementor session) throws HibernateException {
		if ( pc.wasInitialized() ) { //can't be any orphans if it was not initialized!
			Iterator orphanIter = session.getOrphans(pc).iterator();
			while ( orphanIter.hasNext() ) {
				Object orphan = orphanIter.next();
				if (orphan!=null) session.delete(orphan);
			}
		}
	}
	
	/**
	 * Iterate just the elements of the collection that are already there. Don't load
	 * any new elements from the database.
	 */
	private static Iterator getLoadedElementsIterator(PersistentCollectionType collectionType, Object collection) {
		if ( collectionIsInitialized(collection) ) {
			// handles arrays and newly instantiated collections
			return collectionType.getElementsIterator(collection);
		}
		else {
			// does not handle arrays (thats ok, cos they can't be lazy) 
			// or newly instantiated collections, so we can do the cast 
			return ( (PersistentCollection) collection ).queuedAdditionIterator();
		}
	}
	
	/**
	 * Iterate all the collection elements, loading them from the database if necessary.
	 */
	private static Iterator getAllElementsIterator(PersistentCollectionType collectionType, Object collection) {
		return collectionType.getElementsIterator(collection);
	}
	
}







