//$Id: SequenceHiLoGenerator.java,v 1.13 2004/06/04 01:27:40 steveebersole Exp $
package net.sf.hibernate.id;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.PropertiesHelper;

/**
 * <b>seqhilo</b><br>
 * <br>
 * An <tt>IdentifierGenerator</tt> that combines a hi/lo algorithm with an underlying
 * oracle-style sequence that generates hi values. The user may specify a
 * maximum lo value to determine how often new hi values are fetched.<br>
 * <br>
 * If sequences are not available, <tt>TableHiLoGenerator</tt> might be an
 * alternative.<br>
 * <br>
 * Mapping parameters supported: sequence, max_lo, parameters.
 *
 * @see TableHiLoGenerator
 * @author Gavin King
 */
public class SequenceHiLoGenerator extends SequenceGenerator {
	
	public static final String MAX_LO = "max_lo";
	
	private static final Log log = LogFactory.getLog(SequenceHiLoGenerator.class);
	
	private int maxLo;
	private int lo;
	private long hi;
	private Class returnClass;
	
	public void configure(Type type, Properties params, Dialect d) throws MappingException {
		super.configure(type, params, d);
		maxLo = PropertiesHelper.getInt(MAX_LO, params, 9);
		lo = maxLo + 1; // so we "clock over" on the first invocation
		returnClass = type.getReturnedClass();
	}
	
	public synchronized Serializable generate(SessionImplementor session, Object obj) throws SQLException, HibernateException {
		
		if ( lo>maxLo ) {
			long hival = ( (Number) super.generate(session, obj) ).longValue();
			lo = 1;
			hi = hival * ( maxLo+1 );
			if (log.isDebugEnabled())
				log.debug("new hi value: " + hival);
		}
		
		return IdentifierGeneratorFactory.createNumber( hi + lo++, returnClass );
	}
	
}






