//$Id: HSQLDialect.java,v 1.22 2004/11/11 20:42:29 steveebersole Exp $
// Contributed by Phillip Baird

package net.sf.hibernate.dialect;

import java.sql.Types;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.exception.SQLExceptionConverter;
import net.sf.hibernate.exception.ErrorCodeConverter;
import net.sf.hibernate.exception.ViolatedConstraintNameExtracter;
import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.sql.CaseFragment;
import net.sf.hibernate.sql.HSQLCaseFragment;

/**
 * An SQL dialect compatible with HSQLDB (Hypersonic SQL).
 * @author Christoph Sturm
 */
public class HSQLDialect extends Dialect {

	public HSQLDialect() {
		super();
		registerColumnType( Types.BIGINT, "bigint" );
		registerColumnType( Types.BINARY, "binary" );
		registerColumnType( Types.BIT, "bit" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.DATE, "date" );
		registerColumnType( Types.DECIMAL, "decimal" );
		registerColumnType( Types.DOUBLE, "double" );
		registerColumnType( Types.FLOAT, "float" );
		registerColumnType( Types.INTEGER, "integer" );
		registerColumnType( Types.LONGVARBINARY, "longvarbinary" );
		registerColumnType( Types.LONGVARCHAR, "longvarchar" );
		registerColumnType( Types.SMALLINT, "smallint" );
		registerColumnType( Types.TINYINT, "tinyint" );
		registerColumnType( Types.TIME, "time" );
		registerColumnType( Types.TIMESTAMP, "timestamp" );
		registerColumnType( Types.VARCHAR, "varchar($l)" );
		registerColumnType( Types.VARBINARY, "varbinary($l)" );
		registerColumnType( Types.NUMERIC, "numeric" );
		//HSQL has no Blob/Clob support .... but just put these here for now!
		registerColumnType( Types.BLOB, "longvarbinary" );
		registerColumnType( Types.CLOB, "longvarchar" );

		registerFunction( "ascii", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction( "char", new StandardSQLFunction(Hibernate.CHARACTER) );
		registerFunction( "length", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction( "lower", new StandardSQLFunction() );
		registerFunction( "upper", new StandardSQLFunction() );
		registerFunction( "lcase", new StandardSQLFunction() );
		registerFunction( "ucase", new StandardSQLFunction() );
		registerFunction( "soundex", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction( "ltrim", new StandardSQLFunction() );
		registerFunction( "rtrim", new StandardSQLFunction() );
		registerFunction( "reverse", new StandardSQLFunction() );
		registerFunction( "space", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction( "rawtohex", new StandardSQLFunction() );
		registerFunction( "hextoraw", new StandardSQLFunction() );

		registerFunction( "user", new NoArgSQLFunction(Hibernate.STRING) );
		registerFunction( "database", new NoArgSQLFunction(Hibernate.STRING) );

		registerFunction( "curdate", new NoArgSQLFunction(Hibernate.DATE) );
		registerFunction( "now", new NoArgSQLFunction(Hibernate.TIMESTAMP) );
		registerFunction( "curtime", new NoArgSQLFunction(Hibernate.TIME) );
		registerFunction( "day", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction( "dayofweek", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction( "dayofyear", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction( "dayofmonth", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction( "month", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction( "year", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction( "week", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction( "quater", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction( "hour", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction( "minute", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction( "second", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction( "dayname", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction( "monthname", new StandardSQLFunction(Hibernate.STRING) );

		registerFunction( "abs", new StandardSQLFunction() );
		registerFunction( "sign", new StandardSQLFunction(Hibernate.INTEGER) );

		registerFunction( "acos", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "asin", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "atan", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "cos", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "cot", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "exp", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "log", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "log10", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "sin", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "sqrt", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "tan", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "pi", new NoArgSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "rand", new StandardSQLFunction(Hibernate.FLOAT) );

		registerFunction("radians", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("degrees", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("roundmagic", new StandardSQLFunction() );

		registerFunction( "ceiling", new StandardSQLFunction() );
		registerFunction( "floor", new StandardSQLFunction() );

		// Multi-param dialect functions...
		registerFunction( "mod", new StandardSQLFunction(Hibernate.INTEGER) );

		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);
	}

	public String getAddColumnString() {
		return "add column";
	}

	public boolean supportsIdentityColumns() {
		return true;
	}
	public String getIdentityColumnString() {
		return "generated by default as identity (start with 1)";
	}
	public String getIdentitySelectString() {
		return "call identity()";
	}
	public String getIdentityInsertString() {
		return "null";
	}

	public boolean supportsForUpdate() {
		return false;
	}

	/**
	 * Not supported in 1.7.1 (1.7.2 only)
	 */
	public boolean supportsUnique() {
		return false;
	}

	public boolean supportsLimit() {
		return true;
	}

	public String getLimitString(String sql, boolean hasOffset) {
		return new StringBuffer( sql.length()+10 )
			.append(sql)
			.insert(6, hasOffset ? " limit ? ?" : " top ?")
			.toString();
	}

	public CaseFragment createCaseFragment() {
		return new HSQLCaseFragment();
	}

	public boolean bindLimitParametersFirst() {
		return true;
	}

	public boolean supportsIfExistsAfterTableName() {
		return true;
	}

	public boolean supportsCheck() {
		return false;
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
		private int[] sqlGrammarCodes = new int[] { -22, -28 };
		private int[] integrityViolationCodes = new int[] { -9, -177 };

		public ExceptionConverter(ViolatedConstraintNameExtracter extracter) {
			super(extracter);
		}

		protected int[] getSQLGrammarErrorCodes() {
			return sqlGrammarCodes;
		}

		protected int[] getIntegrityViolationErrorCodes() {
			return integrityViolationCodes;
		}
	}

}
