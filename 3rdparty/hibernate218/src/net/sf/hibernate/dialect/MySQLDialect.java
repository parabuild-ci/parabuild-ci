//$Id: MySQLDialect.java,v 1.26 2005/01/24 00:21:41 epbernard Exp $
package net.sf.hibernate.dialect;

import java.sql.Types;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.exception.SQLExceptionConverter;
import net.sf.hibernate.exception.ErrorCodeConverter;
import net.sf.hibernate.exception.ViolatedConstraintNameExtracter;
import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.util.StringHelper;

/**
 * An SQL dialect for MySQL.
 * @author Gavin King
 */
public class MySQLDialect extends Dialect {
	public MySQLDialect() {
		super();
		registerColumnType( Types.BIT, "bit" );
		registerColumnType( Types.BIGINT, "bigint" );
		registerColumnType( Types.SMALLINT, "smallint" );
		registerColumnType( Types.TINYINT, "tinyint" );
		registerColumnType( Types.INTEGER, "integer" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.VARCHAR, "longtext" );
		registerColumnType( Types.VARCHAR, 16777215, "mediumtext" );
		registerColumnType( Types.VARCHAR, 65535, "text" );
		registerColumnType( Types.VARCHAR, 255, "varchar($l)" );
		registerColumnType( Types.FLOAT, "float" );
		registerColumnType( Types.DOUBLE, "double precision" );
		registerColumnType( Types.DATE, "date" );
		registerColumnType( Types.TIME, "time" );
		registerColumnType( Types.TIMESTAMP, "datetime" );
		registerColumnType( Types.VARBINARY, "longblob" );
		registerColumnType( Types.VARBINARY, 16777215, "mediumblob" );
		registerColumnType( Types.VARBINARY, 65535, "blob" );
		registerColumnType( Types.VARBINARY, 255, "tinyblob" );
		registerColumnType( Types.NUMERIC, "numeric(19, $l)" );
		registerColumnType( Types.BLOB, "longblob" );
		registerColumnType( Types.BLOB, 16777215, "mediumblob" );
		registerColumnType( Types.BLOB, 65535, "blob" );
		registerColumnType( Types.CLOB, "longtext" );
		registerColumnType( Types.CLOB, 16777215, "mediumtext" );
		registerColumnType( Types.CLOB, 65535, "text" );

		registerFunction("ascii", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("bin", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("char_length", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction("character_length", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction("lcase", new StandardSQLFunction() );
		registerFunction("lower", new StandardSQLFunction() );
		registerFunction("length", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction("ltrim", new StandardSQLFunction() );
		registerFunction("ord", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("quote", new StandardSQLFunction() );
		registerFunction("reverse", new StandardSQLFunction() );
		registerFunction("rtrim", new StandardSQLFunction() );
		registerFunction("soundex", new StandardSQLFunction() );
		registerFunction("space", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("ucase", new StandardSQLFunction() );
		registerFunction("upper", new StandardSQLFunction() );
		registerFunction("unhex", new StandardSQLFunction(Hibernate.STRING) );

		registerFunction("abs", new StandardSQLFunction() );
		registerFunction("sign", new StandardSQLFunction(Hibernate.INTEGER) );

		registerFunction("acos", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("asin", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("atan", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("cos", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("cot", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("crc32", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction("exp", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("ln", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("log", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("log2", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("log10", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("pi", new NoArgSQLFunction(Hibernate.DOUBLE) );
		registerFunction("rand", new NoArgSQLFunction(Hibernate.DOUBLE) );
		registerFunction("sin", new NoArgSQLFunction(Hibernate.DOUBLE) );
		registerFunction("sqrt", new NoArgSQLFunction(Hibernate.DOUBLE) );
		registerFunction("tan", new NoArgSQLFunction(Hibernate.DOUBLE) );

		registerFunction("radians", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("degrees", new StandardSQLFunction(Hibernate.DOUBLE) );

		registerFunction("ceiling", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("ceil", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("floor", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("round", new StandardSQLFunction(Hibernate.INTEGER) );

		registerFunction("curdate", new NoArgSQLFunction(Hibernate.DATE) );
		registerFunction("current_date", new NoArgSQLFunction(Hibernate.DATE) );
		registerFunction("curtime", new NoArgSQLFunction(Hibernate.TIME) );
		registerFunction("current_time", new NoArgSQLFunction(Hibernate.TIME) );
		registerFunction("current_timestamp", new NoArgSQLFunction(Hibernate.TIMESTAMP) );
		registerFunction("date", new StandardSQLFunction(Hibernate.DATE) );
		registerFunction("day", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("dayofmonth", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("dayname", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("dayofweek", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("dayofyear", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("from_days", new StandardSQLFunction(Hibernate.DATE) );
		registerFunction("from_unixtime", new StandardSQLFunction(Hibernate.TIMESTAMP) );
		registerFunction("hour", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("last_day", new StandardSQLFunction(Hibernate.DATE) );
		registerFunction("localtime", new NoArgSQLFunction(Hibernate.TIMESTAMP));
		registerFunction("localtimestamp", new NoArgSQLFunction(Hibernate.TIMESTAMP) );
		registerFunction("microseconds", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("minute", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("month", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("monthname", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("now", new NoArgSQLFunction(Hibernate.TIMESTAMP) );
		registerFunction("quarter", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("second", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("sec_to_time", new StandardSQLFunction(Hibernate.TIME) );
		registerFunction("sysdate", new NoArgSQLFunction(Hibernate.TIMESTAMP) );
		registerFunction("time", new StandardSQLFunction(Hibernate.TIME) );
		registerFunction("time", new StandardSQLFunction(Hibernate.TIMESTAMP) );
		registerFunction("time_to_sec", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("to_days", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction("unix_timestamp", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction("utc_date", new NoArgSQLFunction(Hibernate.STRING) );
		registerFunction("utc_time", new NoArgSQLFunction(Hibernate.STRING) );
		registerFunction("utc_timestamp", new NoArgSQLFunction(Hibernate.STRING) );
		registerFunction("week", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("weekday", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("weekofyear", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("year", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("yearweek", new StandardSQLFunction(Hibernate.INTEGER) );

		registerFunction("hex", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("oct", new StandardSQLFunction(Hibernate.STRING) );

		registerFunction("octet_length", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction("bit_length", new StandardSQLFunction(Hibernate.LONG) );

		registerFunction("bit_count", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction("encrypt", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("md5", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("sha1", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("sha", new StandardSQLFunction(Hibernate.STRING) );

		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);
		getDefaultProperties().setProperty(Environment.MAX_FETCH_DEPTH, "2");
	}

	public String getAddColumnString() {
		return "add column";
	}
	
	public String getDropForeignKeyString() {
		return " drop foreign key ";
	}

	public boolean dropConstraints() {
		return true;
	}
	
	public boolean qualifyIndexName() {
		return false;
	}

	public boolean supportsIdentityColumns() {
		return true;
	}
	public String getIdentitySelectString() {
		return "select last_insert_id()";
	}

	public String getIdentityColumnString() {
		return "not null auto_increment";
	}

	public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey) {
		String cols = StringHelper.join(StringHelper.COMMA_SPACE, foreignKey);
		return new StringBuffer(30)
			.append(" add index ")
			.append(constraintName)
			.append(" (")
			.append(cols)
			.append("), add constraint ")
			.append(constraintName)
			.append(" foreign key (")
			.append(cols)
			.append(") references ")
			.append(referencedTable)
			.append(" (")
			.append( StringHelper.join(StringHelper.COMMA_SPACE, primaryKey) )
			.append(')')
			.toString();
	}

	public boolean supportsLimit() {
		return true;
	}

	public String getLimitString(String sql, boolean hasOffset) {
		return new StringBuffer( sql.length()+20 )
			.append(sql)
			.append( hasOffset ? " limit ?, ?" : " limit ?")
			.toString();
	}

	public char closeQuote() {
		return '`';
	}

	public char openQuote() {
		return '`';
	}

	public boolean supportsIfExistsBeforeTableName() {
		return true;
	}

	public char getSchemaSeparator() {
	  return StringHelper.UNDERSCORE;
	}

	/**
	 * Build an instance of the SQLExceptionConverter preferred by this dialect for
	 * converting SQLExceptions into Hibernate's JDBCException hierarchy.  The default
	 * Dialect implementation simply returns a converter based on X/Open SQLState codes.
	 * <p/>
	 * It is strongly recommended that specific Dialect implementations override this
	 * method, since interpretation of a SQL error is much more accurate when based on
	 * the ErrorCode rather than the SQLState.  Unfortunately, the ErrorCode is a vendor-
	 * specific approach.
	 *
	 * @return The Dialect's preferred SQLExceptionConverter.
	 */
	public SQLExceptionConverter buildSQLExceptionConverter() {
		return new ExceptionConverter( getViolatedConstraintNameExtracter() );
	}

	private static class ExceptionConverter extends ErrorCodeConverter {
		private int[] sqlGrammarCodes = new int[] { 1054, 1064, 1146 };
		private int[] integrityViolationCodes = new int[] { 1062, 1216 };
		private int[] connectionCodes = new int[] { 1049 };
        private int[] lockAcquisitionErrorCodes = new int[] { 1099, 1100, 1150, 1165, 1192, 1205, 1206, 1207, 1213, 1223 };

		public ExceptionConverter(ViolatedConstraintNameExtracter extracter) {
			super(extracter);
		}

		protected int[] getSQLGrammarErrorCodes() {
			return sqlGrammarCodes;
		}

		protected int[] getIntegrityViolationErrorCodes() {
			return integrityViolationCodes;
		}

		protected int[] getConnectionErrorCodes() {
			return connectionCodes;
		}
        
        protected int[] getLockAcquisitionErrorCodes() {
			return lockAcquisitionErrorCodes;
		}
	}
}
