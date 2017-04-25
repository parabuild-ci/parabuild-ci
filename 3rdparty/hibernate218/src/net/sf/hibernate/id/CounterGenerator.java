//$Id: CounterGenerator.java,v 1.5 2004/06/04 01:27:40 steveebersole Exp $
package net.sf.hibernate.id;

import java.io.Serializable;

import net.sf.hibernate.engine.SessionImplementor;

/**
 * <b>vm</b><br>
 * <br>
 * An <tt>IdentifierGenerator</tt> that returns a <tt>long</tt>, 
 * constructed from the system time and a counter value. Not safe 
 * for use in a cluster!
 * 
 * @deprecated use <tt>IncrementGenerator</tt> instead
 * @see IncrementGenerator
 * @author Gavin King
 */
public class CounterGenerator implements IdentifierGenerator {
	
	private static short counter = (short) 0;

	protected static short getCount() {
		synchronized(CounterGenerator.class) {
			if (counter<0) counter=0;
			return counter++;
		}
	}
	
	public Serializable generate(SessionImplementor cache, Object obj) {
		return new Long( ( System.currentTimeMillis() << 16 ) + getCount() );
	}
	
	public static void main( String[] args ) throws Exception {
		IdentifierGenerator gen = new CounterGenerator();
		for ( int i=0; i<5; i++) {
			long result = ( (Long) gen.generate(null, null) ).longValue();
			System.out.println( result + " (" + Long.toHexString(result) + ")" );
		}
	}

}






