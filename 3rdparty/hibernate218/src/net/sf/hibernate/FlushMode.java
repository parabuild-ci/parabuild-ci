//$Id: FlushMode.java,v 1.9 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a flushing strategy. The flush process synchronizes
 * database state with session state by detecting state changes
 * and executing SQL statements.
 *
 * @see Session#setFlushMode(FlushMode)
 * @author Gavin King
 */
public final class FlushMode implements Serializable {
	private final int level;
	private final String name;
	private static final Map INSTANCES = new HashMap();
	
	private FlushMode(int level, String name) {
		this.level=level;
		this.name=name;
	}
	public String toString() {
		return name;
	}
	/**
	 * The <tt>Session</tt> is never flushed unless <tt>flush()</tt>
	 * is explicitly called by the application. This mode is very
	 * efficient for read only transactions.
	 */
	public static final FlushMode NEVER = new FlushMode(0, "NEVER");
	/**
	 * The <tt>Session</tt> is flushed when <tt>Transaction.commit()</tt>
	 * is called.
	 */
	public static final FlushMode COMMIT = new FlushMode(5, "COMMIT");
	/**
	 * The <tt>Session</tt> is sometimes flushed before query execution
	 * in order to ensure that queries never return stale state. This
	 * is the default flush mode.
	 */
	public static final FlushMode AUTO = new FlushMode(10, "AUTO");
	
	static {
		INSTANCES.put( new Integer(NEVER.level), NEVER );
		INSTANCES.put( new Integer(AUTO.level), AUTO );
		INSTANCES.put( new Integer(COMMIT.level), COMMIT );
	}
	
	private Object readResolve() {
		return INSTANCES.get( new Integer(level) );
	}
	
}






