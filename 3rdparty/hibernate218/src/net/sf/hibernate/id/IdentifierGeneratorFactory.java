//$Id: IdentifierGeneratorFactory.java,v 1.12 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.id;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Properties;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.ReflectHelper;

/**
 * Factory and helper methods for <tt>IdentifierGenerator</tt> framework.
 * 
 * @author Gavin King
 */
public final class IdentifierGeneratorFactory {
	
	//private static final Log log = LogFactory.getLog(IdentifierGeneratorFactory.class);
	
	// unhappy about this being public ... is there a better way?
	public static Serializable get(ResultSet rs, Type type, SessionImplementor session, Object owner) throws SQLException, IdentifierGenerationException {
		Class clazz = type.getReturnedClass();
		if ( clazz==Long.class ) {
			return new Long( rs.getLong(1) );
		}
		else if ( clazz==Integer.class ) {
			return new Integer( rs.getInt(1) );
		}
		else if ( clazz==Short.class ) {
			return new Short( rs.getShort(1) );
		}
		else {
			throw new IdentifierGenerationException("this id generator generates long, integer, short");
		}
	}

	private static final HashMap GENERATORS = new HashMap();

	public static final String SHORT_CIRCUIT_INDICATOR = new String();
	public static final String IDENTITY_COLUMN_INDICATOR = new String();
	
	static {
		GENERATORS.put("uuid.hex", UUIDHexGenerator.class);
		GENERATORS.put("uuid.string", UUIDStringGenerator.class);
		GENERATORS.put("hilo", TableHiLoGenerator.class);
		GENERATORS.put("assigned", Assigned.class);
		GENERATORS.put("identity", IdentityGenerator.class);
		GENERATORS.put("sequence", SequenceGenerator.class);
		GENERATORS.put("seqhilo", SequenceHiLoGenerator.class);
		GENERATORS.put("vm", IncrementGenerator.class); //vm is deprecated
		GENERATORS.put("increment", IncrementGenerator.class);
		GENERATORS.put("foreign", ForeignGenerator.class);
	}
	
	public static IdentifierGenerator create(String strategy, Type type, Properties params, Dialect dialect) throws MappingException {
		try {
			//if ( "vm".equals(strategy) ) log.info("'vm' strategy is deprecated; use 'increment'");
			
			Class clazz = (Class) GENERATORS.get(strategy);
			if ( "native".equals(strategy) ) {
				if ( dialect.supportsIdentityColumns() ) {
					clazz = IdentityGenerator.class;
				}
				else if ( dialect.supportsSequences() ) {
					clazz = SequenceGenerator.class;
				}
				else {
					clazz = TableHiLoGenerator.class;
				}
			}
			if (clazz==null) clazz = ReflectHelper.classForName(strategy);
			IdentifierGenerator idgen = (IdentifierGenerator) clazz.newInstance();
			if (idgen instanceof Configurable) ( (Configurable) idgen).configure(type, params, dialect);
			return idgen;
		}
		catch (Exception e) {
			throw new MappingException("could not instantiate id generator", e);
		}
	}
	
	static Number createNumber(long value, Class clazz) throws IdentifierGenerationException {
		if ( clazz==Long.class ) {
			return new Long(value);
		}
		else if ( clazz==Integer.class ) {
			return new Integer( (int) value );
		}
		else if ( clazz==Short.class ) {
			return new Short( (short) value );
		}
		else {
			throw new IdentifierGenerationException("this id generator generates long, integer, short");
		}
	}
	
	private IdentifierGeneratorFactory() {} //cannot be instantiated
	
}
