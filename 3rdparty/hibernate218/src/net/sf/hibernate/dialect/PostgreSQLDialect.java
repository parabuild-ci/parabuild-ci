//$Id: PostgreSQLDialect.java,v 1.22 2004/08/08 08:23:16 oneovthafew Exp $
package net.sf.hibernate.dialect;

import java.sql.Types;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.cfg.Environment;

/**
 * An SQL dialect for Postgres
 * @author Gavin King
 */

public class PostgreSQLDialect extends Dialect {
	public PostgreSQLDialect() {
		super();
		registerColumnType( Types.BIT, "bool" );
		registerColumnType( Types.BIGINT, "int8" );
		registerColumnType( Types.SMALLINT, "int2" );
		registerColumnType( Types.TINYINT, "int2" );
		registerColumnType( Types.INTEGER, "int4" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.VARCHAR, "varchar($l)" );
		registerColumnType( Types.FLOAT, "float4" );
		registerColumnType( Types.DOUBLE, "float8" );
		registerColumnType( Types.DATE, "date" );
		registerColumnType( Types.TIME, "time" );
		registerColumnType( Types.TIMESTAMP, "timestamp" );
		registerColumnType( Types.VARBINARY, "bytea" );
		registerColumnType( Types.CLOB, "text" );
		registerColumnType( Types.BLOB, "bytea" );
		registerColumnType( Types.NUMERIC, "numeric" );

		registerFunction( "abs", new StandardSQLFunction() );
		registerFunction( "sign", new StandardSQLFunction(Hibernate.INTEGER) );

		registerFunction( "acos", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "asin", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "atan", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "cos", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "cot", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "exp", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "ln", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "log", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "sin", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "sqrt", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "cbrt", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "tan", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "radians", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "degrees", new StandardSQLFunction(Hibernate.DOUBLE) );

		registerFunction( "stddev", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction( "variance", new StandardSQLFunction(Hibernate.DOUBLE) );

		registerFunction( "random", new NoArgSQLFunction(Hibernate.DOUBLE) );

		registerFunction( "round", new StandardSQLFunction() );
		registerFunction( "trunc", new StandardSQLFunction() );
		registerFunction( "ceil", new StandardSQLFunction() );
		registerFunction( "floor", new StandardSQLFunction() );

		registerFunction( "chr", new StandardSQLFunction(Hibernate.CHARACTER) );
		registerFunction( "lower", new StandardSQLFunction() );
		registerFunction( "upper", new StandardSQLFunction() );
		registerFunction( "initcap", new StandardSQLFunction() );
		registerFunction( "to_ascii", new StandardSQLFunction() );
		registerFunction( "quote_ident", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction( "quote_literal", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction( "md5", new StandardSQLFunction() );
		registerFunction( "ascii", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction( "length", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction( "char_length", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction( "bit_length", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction( "octet_length", new StandardSQLFunction(Hibernate.LONG) );

		registerFunction( "current_date", new NoArgSQLFunction(Hibernate.DATE, false) );
		registerFunction( "current_time", new NoArgSQLFunction(Hibernate.TIME, false) );
		registerFunction( "current_timestamp", new NoArgSQLFunction(Hibernate.TIMESTAMP, false) );
		registerFunction( "localtime", new NoArgSQLFunction(Hibernate.TIME, false) );
		registerFunction( "localtimestamp", new NoArgSQLFunction(Hibernate.TIMESTAMP, false) );
		registerFunction( "now", new NoArgSQLFunction(Hibernate.TIMESTAMP) );
		registerFunction( "timeofday", new NoArgSQLFunction(Hibernate.STRING) );
		registerFunction( "age", new StandardSQLFunction() );

		registerFunction( "current_user", new NoArgSQLFunction(Hibernate.STRING, false) );
		registerFunction( "session_user", new NoArgSQLFunction(Hibernate.STRING, false) );
		registerFunction( "user", new NoArgSQLFunction(Hibernate.STRING, false) );
		registerFunction( "current_database", new NoArgSQLFunction(Hibernate.STRING, true) );
		registerFunction( "current_schema", new NoArgSQLFunction(Hibernate.STRING, true) );

		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);
	}

	public String getAddColumnString() {
		return "add column";
	}
	public boolean dropConstraints() {
		return true;
	}
	public String getSequenceNextValString(String sequenceName) {
		return "select nextval ('" + sequenceName +"')";
	}
	public String getCreateSequenceString(String sequenceName) {
		return "create sequence " + sequenceName;
	}
	public String getDropSequenceString(String sequenceName) {
		return "drop sequence " + sequenceName;
	}

	public String getCascadeConstraintsString() {
		return "";//" cascade";
	}

	public boolean supportsSequences() {
		return true;
	}

	public boolean supportsLimit() {
		return true;
	}

	public String getLimitString(String sql, boolean hasOffset) {
		return new StringBuffer( sql.length()+20 )
			.append(sql)
			.append(hasOffset ? " limit ? offset ?" : " limit ?")
			.toString();
	}

	public boolean bindLimitParametersInReverseOrder() {
		return true;
	}

	public boolean supportsForUpdateOf() {
		return true;
	}

}







