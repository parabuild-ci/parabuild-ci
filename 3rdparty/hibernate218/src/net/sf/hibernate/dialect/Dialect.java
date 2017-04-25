//$Id: Dialect.java,v 1.29 2004/12/28 13:25:22 maxcsaucdk Exp $
package net.sf.hibernate.dialect;

import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.sql.Types;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.sql.ANSICaseFragment;
import net.sf.hibernate.sql.ANSIJoinFragment;
import net.sf.hibernate.sql.CaseFragment;
import net.sf.hibernate.sql.JoinFragment;
import net.sf.hibernate.util.ReflectHelper;
import net.sf.hibernate.util.StringHelper;
import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.engine.Mapping;
import net.sf.hibernate.Hibernate;
import net.sf.hibernate.QueryException;
import net.sf.hibernate.exception.SQLExceptionConverter;
import net.sf.hibernate.exception.SQLStateConverter;
import net.sf.hibernate.exception.ViolatedConstraintNameExtracter;

/**
 * Represents a dialect of SQL implemented by a particular RDBMS.
 * Subclasses implement Hibernate compatibility with different systems.<br>
 * <br>
 * Subclasses should provide a public default constructor that <tt>register()</tt>
 * a set of type mappings and default Hibernate properties.<br>
 * <br>
 * Subclasses should be immutable.
 * @author Gavin King, David Channon
 */
public abstract class Dialect {

	static final String DEFAULT_BATCH_SIZE = "15";
	static final String NO_BATCH = "0";

	private static final Map STANDARD_AGGREGATE_FUNCTIONS = new HashMap();
	
	static {
		STANDARD_AGGREGATE_FUNCTIONS.put("count", new SQLFunction() {
			public Type getReturnType(Type columnType, Mapping mapping) {
				return Hibernate.INTEGER;
			}
			public boolean hasArguments() { return true; }
			public boolean hasParenthesesIfNoArguments() { return true; }
		});
		
		STANDARD_AGGREGATE_FUNCTIONS.put("avg", new SQLFunction() {
			public Type getReturnType(Type columnType, Mapping mapping) throws QueryException {
				int[] sqlTypes;
				try {
					sqlTypes = columnType.sqlTypes(mapping);
				}
				catch (MappingException me) {
					throw new QueryException(me);
				}
				if (sqlTypes.length!=1) throw new QueryException("multi-column type in avg()");
				int sqlType = sqlTypes[0];
				if (sqlType==Types.INTEGER || sqlType==Types.BIGINT || sqlType==Types.TINYINT) {
					return Hibernate.FLOAT;
				}
				else {
					return columnType;
				}	
			}
			public boolean hasArguments() { return true; }
			public boolean hasParenthesesIfNoArguments() { return true; }
		});
		
		STANDARD_AGGREGATE_FUNCTIONS.put( "max", new StandardSQLFunction() );
		STANDARD_AGGREGATE_FUNCTIONS.put( "min", new StandardSQLFunction() );
		STANDARD_AGGREGATE_FUNCTIONS.put( "sum", new StandardSQLFunction() );
	}
	
	private static final Log log = LogFactory.getLog(Dialect.class);
	
	protected Dialect() {
		log.info( "Using dialect: " + this );
		sqlFunctions.putAll(STANDARD_AGGREGATE_FUNCTIONS);
	}
	
	public String toString() {
		return getClass().getName();
	}
	
	private final TypeNames typeNames = new TypeNames("$l");
	private final Properties properties = new Properties();
	private final Map sqlFunctions = new HashMap();
	
	/**
	 * Characters used for quoting SQL identifiers
	 */
	public static final String QUOTE="`\"[";
	public static final String CLOSED_QUOTE="`\"]";
	
	
	/**
	 * Get the name of the database type associated with the given
	 * <tt>java.sql.Types</tt> typecode.
	 * @param code <tt>java.sql.Types</tt> typecode
	 * @return the database type name
	 * @throws HibernateException
	 */
	public String getTypeName(int code) throws HibernateException {
		String result = typeNames.get(code);
		if (result == null)
		throw new HibernateException("No default type mapping for (java.sql.Types) " + code);
		return result;
	}
	
	/**
	 * Get the name of the database type associated with the given
	 * <tt>java.sql.Types</tt> typecode.
	 * @param code <tt>java.sql.Types</tt> typecode
	 * @param length the length of the column
	 * @return the database type name
	 * @throws HibernateException
	 */
	public String getTypeName(int code, int length) throws HibernateException {
		String result = typeNames.get(code, length);
		if (result == null)
		throw new HibernateException("No type mapping for (java.sql.Types) " + code + " of length " + length);
		return result;
	}
	
	protected void registerFunction(String name, SQLFunction function) {
		sqlFunctions.put(name, function);
	}
	
	/**
	 * Subclasses register a typename for the given type code and maximum
	 * column length. <tt>$l</tt> in the type name with be replaced by the
	 * column length (if appropriate).
	 * @param code <tt>java.sql.Types</tt> typecode
	 * @param capacity maximum length of database type
	 * @param name the database type name
	 */
	protected void registerColumnType(int code, int capacity, String name) {
		typeNames.put(code, capacity, name);
	}
	
	/**
	 * Subclasses register a typename for the given type code. <tt>$l</tt> in
	 * the type name with be replaced by the column length (if appropriate).
	 * @param code <tt>java.sql.Types</tt> typecode
	 * @param name the database type name
	 */
	protected void registerColumnType(int code, String name) {
		typeNames.put(code, name);
	}
	
	/**
	 * Does this dialect support the <tt>ALTER TABLE</tt> syntax?
	 * @return boolean
	 */
	public boolean hasAlterTable() {
		return true;
	}
	
	/**
	 * Do we need to drop constraints before dropping tables in this dialect?
	 * @return boolean
	 */
	public boolean dropConstraints() {
		return true;
	}
	
	/**
	 * Do we need to qualify index names with the schema name?
	 * @return boolean
	 */
	public boolean qualifyIndexName() {
		return true;
	}
	
	/**
	 * Does this dialect support the <tt>FOR UPDATE</tt> syntax?
	 * @return boolean
	 */
	public boolean supportsForUpdate() {
		return true;
	}
	
	/**
	 * Does this dialect support <tt>FOR UPDATE OF</tt>, allowing
	 * particular rows to be locked?
	 */
	public boolean supportsForUpdateOf() {
		return false;
	}
	
	/**
	 * Does this dialect support the Oracle-style <tt>FOR UPDATE NOWAIT</tt> syntax?
	 * @return boolean
	 */
	public boolean supportsForUpdateNowait() {
		return false;
	}
	
	/**
	 * Does this dialect support the <tt>UNIQUE</tt> column syntax?
	 * @return boolean
	 */
	public boolean supportsUnique() {
		return true;
	}
	
    /**
     * Does this dialect support adding Unique constraints via create and alter table ?
     * @return boolean
     */
    public boolean supportsUniqueConstraintInCreateAlterTable() {
        return true;
    }
    
	/**
	 * The syntax used to add a column to a table (optional).
	 */
	public String getAddColumnString() {
		throw new UnsupportedOperationException("No add column syntax supported by Dialect");
	}
	
	public String getDropForeignKeyString() {
		return " drop constraint ";
	}

	/**
	 * The syntax used to add a foreign key constraint to a table.
	 * @return String
	 */
	public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey) {
		return new StringBuffer(30)
			.append(" add constraint ")
			.append(constraintName)
			.append(" foreign key (")
			.append( StringHelper.join(StringHelper.COMMA_SPACE, foreignKey) )
			.append(") references ")
			.append(referencedTable)
			.toString();
	}
	
	/**
	 * The syntax used to add a primary key constraint to a table.
	 * @return String
	 */
	public String getAddPrimaryKeyConstraintString(String constraintName) {
		return " add constraint " + constraintName + " primary key ";
	}
	
	/**
	 * The keyword used to specify a nullable column.
	 * @return String
	 */
	public String getNullColumnString() {
		return StringHelper.EMPTY_STRING;
	}
	
	/**
	 * Does this dialect support identity column key generation?
	 * @return boolean
	 */
	public boolean supportsIdentityColumns() {
		return false;
	}
	
	/**
	 * Does this dialect support sequences?
	 * @return boolean
	 */
	public boolean supportsSequences() {
		return false;
	}

	/** 
	 * Generate SQL to get the identifier of an inserted row.
	 * If the returned value is not null, the caller will prepare a statement from it,
	 * set SQL parameters just as it would for insertSQL, and execute it as a query
	 * which is expected to return the identifier of the inserted row.
	 * If the returned value is null, the caller will execute insertSQL as an update
	 * and then execute getIdentitySelectString() as a query.
	 * The default implementation (in this class) returns null.
	 * @param insertSQL a parameterized SQL statement to insert a row into a table.
	 * @return a SQL statement that has the same effect as insertSQL
	 * and also gets the identifier of the inserted row.
	 * Return <code>null</code> if this dialect doesn't support this feature.
	 */
	public String appendIdentitySelectToInsert(String insertSQL) {
		return null;
	}
	
	/**
	 * The syntax that returns the identity value of the last insert, if
	 * identity column key generation is supported.
	 * @throws MappingException if no native key generation
	 */
	public String getIdentitySelectString() throws MappingException {
		throw new MappingException("Dialect does not support identity key generation");
	}
	/**
	 * The keyword used to specify an identity column, if identity 
	 * column key generation is supported.
	 * @throws MappingException if no native key generation
	 */
	public String getIdentityColumnString() throws MappingException {
		throw new MappingException("Dialect does not support identity key generation");
	}
	/**
	 * The keyword used to insert a generated value into an identity column (or null)
	 * @return String
	 */
	public String getIdentityInsertString() {
		return null;
	}
	/**
	 * The keyword used to insert a row without specifying any column values
	 */
	public String getNoColumnsInsertString() {
		return "values ( )";
	}
	/**
	 * The syntax that fetches the next value of a sequence, if sequences are supported.
	 * @param sequenceName the name of the sequence
	 * @return String
	 * @throws MappingException if no sequences
	 */
	public String getSequenceNextValString(String sequenceName) throws MappingException {
		throw new MappingException("Dialect does not support sequences");
	}
	/**
	 * The syntax used to create a sequence, if sequences are supported.
	 * @param sequenceName the name of the sequence
	 * @return String
	 * @throws MappingException if no sequences
	 */
	public String getCreateSequenceString(String sequenceName) throws MappingException {
		throw new MappingException("Dialect does not support sequences");
	}
	/**
	 * The syntax used to drop a sequence, if sequences are supported.
	 * @param sequenceName the name of the sequence
	 * @return String
	 * @throws MappingException if no sequences
	 */
	public String getDropSequenceString(String sequenceName) throws MappingException {
		throw new MappingException("Dialect does not support sequences");
	}
	/**
	 * A query used to find all sequences
	 * @see net.sf.hibernate.tool.hbm2ddl.SchemaUpdate
	 */
	public String getQuerySequencesString() {
		return null;
	}
	
	/**
	 * Get the <tt>Dialect</tt> specified by the current <tt>System</tt> properties.
	 * @return Dialect
	 * @throws HibernateException
	 */
	public static Dialect getDialect() throws HibernateException {
		String dialectName = Environment.getProperties().getProperty(Environment.DIALECT);
		if (dialectName==null) throw new HibernateException("The dialect was not set. Set the property hibernate.dialect.");
		try {
			return (Dialect) ReflectHelper.classForName(dialectName).newInstance();
		}
		catch (ClassNotFoundException cnfe) {
			throw new HibernateException("Dialect class not found: " + dialectName);
		}
		catch (Exception e) {
			throw new HibernateException( "Could not instantiate dialect class", e );
		}
	}
	
	
	/**
	 * Get the <tt>Dialect</tt> specified by the given properties or system properties.
	 * @param props
	 * @return Dialect
	 * @throws HibernateException
	 */
	public static Dialect getDialect(Properties props) throws HibernateException {
		String dialectName = props.getProperty(Environment.DIALECT);
		if (dialectName== null) return getDialect();
		try {
			return (Dialect) ReflectHelper.classForName(dialectName).newInstance();
		}
		catch (ClassNotFoundException cnfe) {
			throw new HibernateException("Dialect class not found: " + dialectName);
		}
		catch (Exception e) {
			throw new HibernateException( "Could not instantiate dialect class", e );
		}
	}
	
	/**
	 * Retrieve a set of default Hibernate properties for this database.
	 * @return a set of Hibernate properties
	 */
	public final Properties getDefaultProperties() {
		return properties;
	}
	
	/**
	 * Completely optional cascading drop clause
	 * @return String
	 */
	public String getCascadeConstraintsString() {
		return StringHelper.EMPTY_STRING;
	}
	
	/**
	 * Create an <tt>OuterJoinGenerator</tt> for this dialect.
	 * @return OuterJoinGenerator
	 */
	public JoinFragment createOuterJoinFragment() {
		return new ANSIJoinFragment();
	}
	
	/**
	 * Create a <tt>CaseFragment</tt> for this dialect.
	 * @return OuterJoinGenerator
	 */
	public CaseFragment createCaseFragment() {
		return new ANSICaseFragment();
	}
	
	/**
	 * The name of the SQL function that transforms a string to 
	 * lowercase
	 * 
	 * @return String
	 */
	public String getLowercaseFunction() {
		return "lower";
	}
	
	/**
	 * Does this <tt>Dialect</tt> have some kind of <tt>LIMIT</tt> syntax?
	 */
	public boolean supportsLimit() {
		return false;
	}
	
	/**
	 * Does this dialect support an offset?
	 */
	public boolean supportsLimitOffset() {
		return supportsLimit();
	}
	
	/**
	 * Add a <tt>LIMIT</tt> clause to the given SQL <tt>SELECT</tt>
	 * @return the modified SQL
	 */
	public String getLimitString(String querySelect, boolean hasOffset) {
		throw new UnsupportedOperationException("paged queries not supported");
	}
	
	public String getLimitString(String querySelect, boolean hasOffset, int limit) {
		return getLimitString(querySelect, hasOffset);
	}
	public boolean supportsVariableLimit() {
		return supportsLimit();
	}
	
	/**
	 * Does the <tt>LIMIT</tt> clause specify arguments in the "reverse" order
	 * limit, offset instead of offset, limit?
	 * @return true if the correct order is limit, offset
	 */
	public boolean bindLimitParametersInReverseOrder() {
		return false;
	}
	
	/**
	 * Does the <tt>LIMIT</tt> clause come at the start of the
	 * <tt>SELECT</tt> statement, rather than at the end?
	 * @return true if limit parameters should come before other parameters
	 */
	public boolean bindLimitParametersFirst() {
		return false;
	}
	
	/**
	 * Does the <tt>LIMIT</tt> clause take a "maximum" row number instead
	 * of a total number of returned rows?
	 */
	public boolean useMaxForLimit() {
		return false;
	}
	
	/**
	 * The opening quote for a quoted identifier
	 */
	public char openQuote() {
		return '"';
	}
	
	/**
	 * The closing quote for a quoted identifier
	 */
	public char closeQuote() {
		return '"';
	}

	/**
	 * SQL functions as defined in general. The results of this
	 * method should be integrated with the specialisation's data.
	 */
	public final Map getFunctions() {
		return sqlFunctions;
	}
	
	public boolean supportsIfExistsBeforeTableName() {
		return false;
	}
	
	public boolean supportsIfExistsAfterTableName() {
		return false;
	}
	
	/**
	 * The separator between the schema/tablespace name and the table name.
	 */
	public char getSchemaSeparator(){
		return StringHelper.DOT;
	}
	
	/**
	 * Does this dialect support check constraints?
	 */
	public boolean supportsCheck() {
		return true;
	}

	/**
	 * Whether this dialect have an Identity clause added to the data type or a 
	 * completely seperate identity data type
	 * @return boolean
	 */
	public boolean hasDataTypeInIdentityColumn() {
		return true;
	}

	/**
	 * Build an instance of the SQLExceptionConverter preferred by this dialect for
	 * converting SQLExceptions into Hibernate's JDBCException hierarchy.  The default
	 * Dialect implementation simply returns a converter based on X/Open SQLState codes.
	 *
	 * It is strongly recommended that specific Dialect implementations override this
	 * method, since interpretation of a SQL error is much more accurate when based on
	 * the ErrorCode rather than the SQLState.  Unfortunately, the ErrorCode is a vendor-
	 * specific approach.
	 *
	 * @return The Dialect's preferred SQLExceptionConverter.
	 */
	public SQLExceptionConverter buildSQLExceptionConverter() {
		// The default SQLExceptionConverter for all dialects is based on SQLState
		// since SQLErrorCode is extremely vendor-specific.  Specific Dialects
		// may override to return whatever is most appropriate for that vendor.
		return new SQLStateConverter( getViolatedConstraintNameExtracter() );
	}

	private static final ViolatedConstraintNameExtracter EXTRACTER = new ViolatedConstraintNameExtracter() {
		public String extractConstraintName(SQLException sqle) {
			return null;
		}
	};

	public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
		return EXTRACTER;
	}

    

}







