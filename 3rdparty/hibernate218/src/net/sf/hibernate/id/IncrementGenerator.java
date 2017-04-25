package net.sf.hibernate.id;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.type.Type;

/**
 * <b>increment</b><br>
 * <br>
 * An <tt>IdentifierGenerator</tt> that returns a <tt>long</tt>, constructed by 
 * counting from the maximum primary key value at startup. Not safe for use in a 
 * cluster!<br>
 * <br>
 * Mapping parameters supported, but not usually needed: table, column.
 * 
 * @author Gavin King
 */
public class IncrementGenerator implements IdentifierGenerator, Configurable {
	
	private static final Log log = LogFactory.getLog(IncrementGenerator.class);
	
	private long next;
	private String sql;
	private Class returnClass;
	
	public synchronized Serializable generate(SessionImplementor session, Object object)
		throws SQLException, HibernateException {
			
		if (sql!=null) {
			getNext( session.connection() );
		}
		return IdentifierGeneratorFactory.createNumber(next++, returnClass);
	}

	public void configure(Type type, Properties params, Dialect d)
		throws MappingException {
		
		String table = params.getProperty("table");
		if (table==null) table = params.getProperty(PersistentIdentifierGenerator.TABLE);
		String column = params.getProperty("column");
		if (column==null) column = params.getProperty(PersistentIdentifierGenerator.PK);
		String schema = params.getProperty(PersistentIdentifierGenerator.SCHEMA);
		returnClass = type.getReturnedClass();

		sql = "select max(" + column + ") from " + ( schema==null ? table : schema + '.' + table );
	}
	
	private void getNext(Connection conn) throws SQLException {
		
		log.debug("fetching initial value: " + sql);
		
		PreparedStatement st = conn.prepareStatement(sql);
		ResultSet rs = null;
		try {
			rs = st.executeQuery();
			if ( rs.next() ) {
				next = rs.getLong(1) + 1;
				if ( rs.wasNull() ) next = 1;
			}
			else {
				next = 1;
			}
			sql=null;
			log.debug("first free id: " + next);
		}
		finally {
			if (rs!=null) rs.close();
			st.close();
		}
	}

}
