//$Id: DatabaseMetadata.java,v 1.8 2005/01/16 17:30:36 oneovthafew Exp $
package net.sf.hibernate.tool.hbm2ddl;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.util.StringHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * JDBC database metadata
 * @author Christoph Sturm, Teodor Danciu
 */
public class DatabaseMetadata {
	
	private static final Log log = LogFactory.getLog(DatabaseMetadata.class);
	
	private final Map tables = new HashMap();
	private final Set sequences = new HashSet();

	private DatabaseMetaData meta;

	public DatabaseMetadata(Connection connection, Dialect dialect) throws SQLException {
		meta = connection.getMetaData();
		initSequences(connection, dialect);
	}
	
	private static final String[] TYPES = {"TABLE"};

	public TableMetadata getTableMetadata(String name, String schema, String catalog) throws HibernateException {

		TableMetadata table = (TableMetadata) tables.get(name);
		if (table!=null) {
			return table;
		}
		else {
			
			try {
				ResultSet rs = null;
				try {
					
					if ( meta.storesUpperCaseIdentifiers() ) {
						rs = meta.getTables( 
								StringHelper.toUpperCase(catalog), 
								StringHelper.toUpperCase(schema), 
								StringHelper.toUpperCase(name), 
								TYPES 
						);
					}
					else if ( meta.storesLowerCaseIdentifiers() ) {
						rs = meta.getTables( 
								StringHelper.toLowerCase(catalog), 
								StringHelper.toLowerCase(schema), 
								StringHelper.toLowerCase(name), 
								TYPES 
						);
					}
					else {
						rs = meta.getTables(catalog, schema, name, TYPES);
					}
					
					while ( rs.next() ) {
						String tableName = rs.getString("TABLE_NAME");
						if ( name.equalsIgnoreCase(tableName) ) {
							table = new TableMetadata(rs, meta);
							tables.put(name, table);
							return table;
						}
					}
					
					log.info("table not found: " + name);
					return null;

				}
				finally {
					if (rs!=null) rs.close();
				}
			}
			catch(SQLException e) {
				throw new HibernateException(e);
			}
		}

	}
	
	private void initSequences(Connection connection, Dialect dialect) throws SQLException {
		String sql = dialect.getQuerySequencesString();

		if (sql!=null) {

			Statement statement = null;
			ResultSet rs = null;
			try {
				statement = connection.createStatement();
				rs = statement.executeQuery(sql);
	
				while ( rs.next() ) sequences.add( rs.getString(1).toUpperCase() );
			}
			finally {
				if (rs!=null) rs.close();
				if (statement!=null) statement.close();
			}
			
		}
	}

	public boolean isSequence(Object key) {
		return key instanceof String && sequences.contains( ( (String) key ).toUpperCase() );
	}

	public boolean isTable(Object key) throws HibernateException {
		return key instanceof String && ( getTableMetadata( (String) key, null, null ) != null );
	}
	
	public String toString() {
		return "DatabaseMetadata" + tables.toString();
	}
}





