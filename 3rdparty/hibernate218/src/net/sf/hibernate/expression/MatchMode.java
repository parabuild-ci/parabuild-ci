//$Id: MatchMode.java,v 1.5 2004/06/04 01:27:39 steveebersole Exp $
package net.sf.hibernate.expression;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents an strategy for matching strings using "like".
 *
 * @see Example#enableLike(MatchMode)
 * @author Gavin King
 */
public abstract class MatchMode implements Serializable {
	private final int intCode;
	private final String name;
	private static final Map INSTANCES = new HashMap();
	
	protected MatchMode(int intCode, String name) {
		this.intCode=intCode;
		this.name=name;
	}
	public String toString() {
		return name;
	}
	
	/**
	 * Match the entire string to the pattern
	 */
	public static final MatchMode EXACT = new MatchMode(0, "EXACT") {
		String toMatchString(String pattern) {
			return pattern;
		}
	};

	/**
	 * Match the start of the string to the pattern
	 */
	public static final MatchMode START = new MatchMode(1, "START") {
		String toMatchString(String pattern) {
			return pattern + '%';
		}
	};
	
	/**
	 * Match the end of the string to the pattern
	 */
	public static final MatchMode END = new MatchMode(2, "END") {
		String toMatchString(String pattern) {
			return '%' + pattern;
		}
	};

	/**
	 * Match the pattern anywhere in the string
	 */
	public static final MatchMode ANYWHERE = new MatchMode(3, "ANYWHERE") {
		String toMatchString(String pattern) {
			return '%' + pattern + '%';
		}
	};
	
	static {
		INSTANCES.put( new Integer(EXACT.intCode), EXACT );
		INSTANCES.put( new Integer(END.intCode), END );
		INSTANCES.put( new Integer(START.intCode), START );
		INSTANCES.put( new Integer(ANYWHERE.intCode), ANYWHERE );
	}
	
	private Object readResolve() {
		return INSTANCES.get( new Integer(intCode) );
	}
	
	/**
	 * convert the pattern, by appending/prepending "%"
	 */
	abstract String toMatchString(String pattern);
	
}





