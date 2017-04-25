//$Id: LockMode.java,v 1.10 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Instances represent a lock mode for a row of a relational
 * database table. It is not intended that users spend much
 * time worrying about locking since Hibernate usually
 * obtains exactly the right lock level automatically.
 * Some "advanced" users may wish to explicitly specify lock
 * levels.
 *
 * @see Session#lock(Object,LockMode)
 * @author Gavin King
 */
public final class LockMode implements Serializable {
	private final int level;
	private final String name;
	private static final Map INSTANCES = new HashMap();
	
	private LockMode(int level, String name) {
		this.level=level;
		this.name=name;
	}
	public String toString() {
		return name;
	}
	/**
	 * Check if this lock mode is more restrictive than the given lock mode.
	 *
	 * @param mode LockMode to check
	 * @return true if this lock mode is more restrictive than given lock mode
	 */
	public boolean greaterThan(LockMode mode) {
		return level > mode.level;
	}
	/**
	 * Check if this lock mode is less restrictive than the given lock mode.
	 *
	 * @param mode LockMode to check
	 * @return true if this lock mode is less restrictive than given lock mode
	 */
	public boolean lessThan(LockMode mode) {
		return level < mode.level;
	}
	/**
	 * No lock required. If an object is requested with this lock
	 * mode, a <tt>READ</tt> lock will be obtained if it is 
	 * necessary to actually read the state from the database,
	 * rather than pull it from a cache.<br>
	 * <br>
	 * This is the "default" lock mode.
	 */
	public static final LockMode NONE = new LockMode(0, "NONE");
	/**
	 * A shared lock. Objects in this lock mode were read from
	 * the database in the current transaction, rather than being
	 * pulled from a cache.
	 */
	public static final LockMode READ = new LockMode(5, "READ");
	/**
	 * An upgrade lock. Objects loaded in this lock mode are
	 * materialized using an SQL <tt>select ... for update</tt>.
	 */
	public static final LockMode UPGRADE = new LockMode(10, "UPGRADE");
	/**
	 * Attempt to obtain an upgrade lock, using an Oracle-style
	 * <tt>select for update nowait</tt>. The semantics of
	 * this lock mode, once obtained, are the same as
	 * <tt>UPGRADE</tt>.
	 */
	public static final LockMode UPGRADE_NOWAIT = new LockMode(10, "UPGRADE_NOWAIT");
	/**
	 * A <tt>WRITE</tt> lock is obtained when an object is updated
	 * or inserted. This is not a valid mode for <tt>load()</tt>
	 * or <tt>lock()</tt>.
	 */
	public static final LockMode WRITE = new LockMode(10, "WRITE");
	
	static {
		INSTANCES.put( NONE.name, NONE );
		INSTANCES.put( READ.name, READ );
		INSTANCES.put( UPGRADE.name, UPGRADE );
		INSTANCES.put( UPGRADE_NOWAIT.name, UPGRADE_NOWAIT );
		INSTANCES.put( WRITE.name, WRITE );
	}
	
	private Object readResolve() {
		return INSTANCES.get(name);
	}
	
}






