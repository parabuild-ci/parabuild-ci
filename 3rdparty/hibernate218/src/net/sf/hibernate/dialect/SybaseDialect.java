//$Id: SybaseDialect.java,v 1.16 2004/08/11 09:51:06 oneovthafew Exp $
package net.sf.hibernate.dialect;

import java.sql.Types;

import net.sf.hibernate.LockMode;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.cfg.Environment;

/**
 * An SQL dialect compatible with Sybase and MS SQL Server.
 * @author Gavin King
 */

public class SybaseDialect extends Dialect {
	public SybaseDialect() {
		super();
		registerColumnType( Types.BIT, "tinyint" ); //Sybase BIT type does not support null values
		registerColumnType( Types.BIGINT, "numeric(19,0)" );
		registerColumnType( Types.SMALLINT, "smallint" );
		registerColumnType( Types.TINYINT, "tinyint" );
		registerColumnType( Types.INTEGER, "int" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.VARCHAR, "varchar($l)" );
		registerColumnType( Types.FLOAT, "float" );
		registerColumnType( Types.DOUBLE, "double precision" );
		registerColumnType( Types.DATE, "datetime" );
		registerColumnType( Types.TIME, "datetime" );
		registerColumnType( Types.TIMESTAMP, "datetime" );
		registerColumnType( Types.VARBINARY, "varbinary($l)" );
		registerColumnType( Types.NUMERIC, "numeric(19,$l)" );
		registerColumnType( Types.BLOB, "image" );
		registerColumnType( Types.CLOB, "text" );

		registerFunction( "ascii", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction( "char", new StandardSQLFunction(Hibernate.CHARACTER) );
		registerFunction( "len", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction( "lower", new StandardSQLFunction() );
		registerFunction( "upper", new StandardSQLFunction() );
		registerFunction( "str", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction( "ltrim", new StandardSQLFunction() );
		registerFunction( "rtrim", new StandardSQLFunction() );
		registerFunction( "reverse", new StandardSQLFunction() );
		registerFunction( "space", new StandardSQLFunction(Hibernate.STRING) );

		registerFunction( "user", new NoArgSQLFunction(Hibernate.STRING) );

		registerFunction( "getdate", new NoArgSQLFunction(Hibernate.TIMESTAMP) );
		registerFunction( "getutcdate", new NoArgSQLFunction(Hibernate.TIMESTAMP) );
		registerFunction( "day", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction( "month", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction( "year", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction( "datename", new StandardSQLFunction(Hibernate.STRING) );

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
		registerFunction( "square", new StandardSQLFunction() );
		registerFunction( "rand", new StandardSQLFunction(Hibernate.FLOAT) );

		registerFunction("radians", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("degrees", new StandardSQLFunction(Hibernate.DOUBLE) );

		registerFunction( "round", new StandardSQLFunction() );
		registerFunction( "ceiling", new StandardSQLFunction() );
		registerFunction( "floor", new StandardSQLFunction() );

		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, NO_BATCH);
	}

	public String getAddColumnString() {
		return "add";
	}
	public String getNullColumnString() {
		return " null";
	}
	public boolean qualifyIndexName() {
		return false;
	}

	public boolean supportsForUpdate() {
		return false;
	}

	public boolean supportsIdentityColumns() {
		return true;
	}
	public String getIdentitySelectString() {
		return "select @@identity";
	}
	public String getIdentityColumnString() {
		return "identity not null";
	}

	public String getNoColumnsInsertString() {
		return "default values";
	}

	public String appendIdentitySelectToInsert(String insertSQL) {
		return insertSQL + "\nselect @@identity";
	}

	public String appendLockHint(LockMode mode, String tableName) {
		if ( mode.greaterThan(LockMode.READ) ) {
			return tableName + " holdlock";
		}
		else {
			return tableName;
		}
	}
}





