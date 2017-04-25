//$Id: UUIDStringGenerator.java,v 1.10 2004/08/12 07:18:53 oneovthafew Exp $
package net.sf.hibernate.id;

import java.io.Serializable;
import java.util.Properties;

import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.BytesHelper;
import net.sf.hibernate.util.PropertiesHelper;
import net.sf.hibernate.util.StringHelper;

/**
 * <b>uuid.string</b><br>
 * <br>
 * A <tt>UUIDGenerator</tt> that returns a string of length 16,
 * This string will NOT consist of only alphanumeric
 * characters. Use this only if you don't mind unreadable
 * identifiers.<br>
 * <br>
 * This implementation is known to be incompatible with
 * Postgres.
 *
 * @see UUIDHexGenerator
 * @author Gavin King
 */

public class UUIDStringGenerator extends UUIDGenerator implements Configurable {
	
	private String sep = "";
	
	public Serializable generate(SessionImplementor cache, Object obj) {
		return new StringBuffer(20)
		.append( toString( getIP() ) ).append(sep)
		.append( toString( getJVM() ) ).append(sep)
		.append( toString( getHiTime() ) ).append(sep)
		.append( toString( getLoTime() ) ).append(sep)
		.append( toString( getCount() ) )
		.toString();
	}
	
	public static void main( String[] args ) throws Exception {
		IdentifierGenerator gen = new UUIDStringGenerator();
		for ( int i=0; i<5; i++) {
			String id = (String) gen.generate(null, null);
			System.out.println( id + ": " +  id.length() );
		}
	}
	
	private static String toString(int value) {
		return new String ( BytesHelper.toBytes(value) );
	}
	
	private static String toString(short value) {
		return new String ( BytesHelper.toBytes(value) );
	}

	public void configure(Type type, Properties params, Dialect d) {
		sep = PropertiesHelper.getString("separator", params, StringHelper.EMPTY_STRING);
	}
}






