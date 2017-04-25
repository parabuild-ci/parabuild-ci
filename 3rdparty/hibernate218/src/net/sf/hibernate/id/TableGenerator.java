//$Id: TableGenerator.java,v 1.10 2004/11/18 11:26:00 turin42 Exp $
package net.sf.hibernate.id;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.PropertiesHelper;
import net.sf.hibernate.util.StringHelper;

/**
 * An <tt>IdentifierGenerator</tt> that uses a database
 * table to store the last generated value. It is not
 * intended that applications use this strategy directly. 
 * However, it may be used to build other (efficient) 
 * strategies. The returned type is <tt>Integer</tt>.<br>
 * <br>
 * The hi value MUST be fetched in a seperate transaction
 * to the <tt>Session</tt> transaction so the generator must 
 * be able to obtain a new connection and commit it. Hence 
 * this implementation may not be used when Hibernate is 
 * fetching connections from an application server datasource 
 * or when the user is supplying connections.<br>
 * <br>
 * The returned value is of type <tt>integer</tt>.<br>
 * <br>
 * Mapping parameters supported: table, column
 * 
 * @see TableHiLoGenerator
 * @author Gavin King
 */
public class TableGenerator implements PersistentIdentifierGenerator, Configurable {
	
	/**
	 * The column parameter
	 */
	public static final String COLUMN = "column";
	/**
	 * The table parameter
	 */
	public static final String TABLE = "table";
	
	private static final Log log = LogFactory.getLog(TableGenerator.class);

	private String tableName;
	private String columnName;
	private String query;
	private String update;

	public void configure(Type type, Properties params, Dialect dialect) {
		
		this.tableName = PropertiesHelper.getString(TABLE, params, "hibernate_unique_key");
		this.columnName = PropertiesHelper.getString(COLUMN, params, "next_hi");
		String schemaName = params.getProperty(SCHEMA);
		if ( schemaName!=null && tableName.indexOf(StringHelper.DOT)<0 ) 
			tableName = schemaName + '.' + tableName;
		
		query = "select " + columnName + " from " + tableName;
		if ( dialect.supportsForUpdate() ) query += " for update";
		update = "update " + tableName + " set " + columnName + " = ? where " + columnName + " = ?";
	}

	public synchronized Serializable generate(SessionImplementor session, Object object)
		throws SQLException, HibernateException {
			
		// This has to be done using a different connection to the
		// containing transaction because the new hi value must
		// remain valid even if the containing transaction rolls
		// back
		Connection conn  = session.getBatcher().openConnection();
		int result;
		int rows;
		try {
			conn.setAutoCommit(false);

			do {
				// The loop ensures atomicity of the
				// select + update even for no transaction
				// or read committed isolation level
				
				PreparedStatement qps = conn.prepareStatement(query);
				try {
					ResultSet rs = qps.executeQuery();
					if ( !rs.next() ) {
						String err = "could not read a hi value - you need to populate the table: " + tableName;
						log.error(err);
						throw new IdentifierGenerationException(err);
					}
					result = rs.getInt(1);
					rs.close();
				}
				catch (SQLException sqle) {
					log.error("could not read a hi value", sqle);
					throw sqle;
				}
				finally {
					qps.close();
				}
				
				PreparedStatement ups = conn.prepareStatement(update);
				try {
					ups.setInt( 1, result + 1 );
					ups.setInt( 2, result );
					rows = ups.executeUpdate();
				}
				catch (SQLException sqle) {
					log.error("could not update hi value in: " + tableName, sqle);
					throw sqle;
				}
				finally {
					ups.close();
				}
			}
			while (rows==0);
			
			conn.commit();
			
			return new Integer(result);
			
		}
		finally {
			session.getBatcher().closeConnection(conn);
		}
	}

	
	public String[] sqlCreateStrings(Dialect dialect) throws HibernateException {
		return new String[] {
			"create table " + tableName + " ( " + columnName + " " + dialect.getTypeName(Types.INTEGER) + " )",
			"insert into " + tableName + " values ( 0 )"
		};
	}
	
	public String sqlDropString(Dialect dialect) {
		//return "drop table " + tableName + dialect.getCascadeConstraintsString();
		StringBuffer sqlDropString = new StringBuffer()
			.append("drop table ");
		if ( dialect.supportsIfExistsBeforeTableName() ) sqlDropString.append("if exists ");
		sqlDropString.append(tableName)
			.append( dialect.getCascadeConstraintsString() );
		if ( dialect.supportsIfExistsAfterTableName() ) sqlDropString.append(" if exists");   
		return sqlDropString.toString();
	}

	public Object generatorKey() {
		return tableName;
	}

}
