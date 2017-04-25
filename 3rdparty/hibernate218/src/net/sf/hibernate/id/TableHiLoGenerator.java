//$Id: TableHiLoGenerator.java,v 1.9 2004/06/04 01:27:40 steveebersole Exp $
package net.sf.hibernate.id;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Properties;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.PropertiesHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <b>hilo</b><br>
 * <br>
 * An <tt>IdentifierGenerator</tt> that returns a <tt>Long</tt>, constructed using
 * a hi/lo algorithm. The hi value MUST be fetched in a seperate transaction
 * to the <tt>Session</tt> transaction so the generator must be able to obtain
 * a new connection and commit it. Hence this implementation may not
 * be used when Hibernate is fetching connections from an application
 * server datasource or when the user is supplying connections. In these
 * cases a <tt>SequenceHiLoGenerator</tt> would be a better choice (where
 * supported).<br>
 * <br>
 * Mapping parameters supported: table, column, max_lo
 *
 * @see SequenceHiLoGenerator
 * @author Gavin King
 */

public class TableHiLoGenerator extends TableGenerator {
	
	/**
	 * The max_lo parameter
	 */
	public static final String MAX_LO = "max_lo";
	
	private long hi;
	private int lo;
	private int maxLo;
	private Class returnClass;
	
	private static final Log log = LogFactory.getLog(TableHiLoGenerator.class);
	
	public void configure(Type type, Properties params, Dialect d) {
		super.configure(type, params, d);
		maxLo = PropertiesHelper.getInt(MAX_LO, params, Short.MAX_VALUE);
		lo = maxLo + 1; // so we "clock over" on the first invocation
		returnClass = type.getReturnedClass();
	}
	
	public synchronized Serializable generate(SessionImplementor session, Object obj) throws SQLException, HibernateException {
		
		if (lo>maxLo) {
			int hival = ( (Integer) super.generate(session, obj) ).intValue();
			lo = 1;
			hi = hival * (maxLo+1);
			log.debug("new hi value: " + hival);
		}
		
		return IdentifierGeneratorFactory.createNumber( hi + lo++, returnClass );
		
	}
	
}






