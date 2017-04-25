//$Id: Versioning.java,v 1.9 2004/06/04 01:27:39 steveebersole Exp $
package net.sf.hibernate.engine;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.persister.ClassPersister;
import net.sf.hibernate.type.VersionType;

/**
 * Utility methods for managing versions and timestamps
 * @author Gavin King
 */
public final class Versioning {
	
	private Versioning() {}
	
	private static final Log log = LogFactory.getLog(Versioning.class);
	
	/**
	 * Increment the given version number
	 */
	public static Object increment(Object version, VersionType versionType) {
		Object next = versionType.next(version);
		if ( log.isTraceEnabled() ) log.trace("Incrementing: " + version + " to " + next);
		return next;
	}
	
	/**
	 * Create an initial version number
	 */
	private static Object seed(VersionType versionType) {
		Object seed = versionType.seed();
		if ( log.isTraceEnabled() ) log.trace("Seeding: " + seed);
		return seed;
	}
	
	/**
	 * Seed the given instance state snapshot with an initial version number
	 */
	public static boolean seedVersion(Object[] fields, int versionProperty, VersionType versionType) {
		Object initialVersion = fields[versionProperty];
		if ( 
			initialVersion==null || 
			// This next bit is to allow for both unsaved-value="negative"
			// and for "older" behavior where version number did not get
			// seeded if it was already set in the object
			// TODO: shift it into unsaved-value strategy
			( (initialVersion instanceof Number) && ( (Number) initialVersion ).longValue()<0 )
		) {
			fields[versionProperty] = seed(versionType);
			return true;
		}
		else {
			if ( log.isTraceEnabled() ) log.trace( "using initial version: " + initialVersion );
			return false;
		}
	}
	
	private static Object getVersion(Object[] fields, int versionProperty) {
		return fields[versionProperty];
	}
	
	private static void setVersion(Object[] fields, Object version, int versionProperty) {
		fields[versionProperty] = version;
	}
	
	/**
	 * Set the version number of the given instance state snapshot
	 */
	public static void setVersion(Object[] fields, Object version, ClassPersister persister) {
		setVersion( fields, version, persister.getVersionProperty()  );
	}
	
	/**
	 * Get the version number of the given instance state snapshot
	 */
	public static Object getVersion(Object[] fields, ClassPersister persister) throws HibernateException {
		return persister.isVersioned() ? getVersion( fields, persister.getVersionProperty() ) : null;
	}
	
	public static final int OPTIMISTIC_LOCK_NONE = -1;
	public static final int OPTIMISTIC_LOCK_ALL = 2;
	public static final int OPTIMISTIC_LOCK_DIRTY = 1;
	public static final int OPTIMISTIC_LOCK_VERSION = 0;
	
}






