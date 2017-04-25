//$Id: FetchMode.java,v 1.6 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an association fetching strategy. This is used
 * together with the <tt>Criteria</tt> API to specify runtime 
 * fetching strategies.<br>
 * <br>
 * For HQL queries, use the <tt>FETCH</tt> keyword instead.
 *
 * @see Criteria#setFetchMode(java.lang.String, FetchMode)
 * @author Gavin King
 */
public final class FetchMode implements Serializable {
	private final int level;
	private final String name;
	private static final Map INSTANCES = new HashMap();
	
	private FetchMode(int level, String name) {
		this.level=level;
		this.name=name;
	}
	public String toString() {
		return name;
	}
	/**
	 * Fetch lazily. Equivalent to <tt>outer-join="false"</tt>.
	 */
	public static final FetchMode LAZY = new FetchMode(1, "LAZY");
	/**
	 * Fetch eagerly, using an outer join. Equivalent to 
	 * <tt>outer-join="true"</tt>.
	 */
	public static final FetchMode EAGER = new FetchMode(2, "EAGER");
	/**
	 * Default to the setting configured in the mapping file.
	 */
	public static final FetchMode DEFAULT = new FetchMode(0, "DEFAULT");
	
	static {
		INSTANCES.put( new Integer(LAZY.level), LAZY );
		INSTANCES.put( new Integer(EAGER.level), EAGER );
		INSTANCES.put( new Integer(DEFAULT.level), DEFAULT );
	}
	
	private Object readResolve() {
		return INSTANCES.get( new Integer(level) );
	}
	
}





