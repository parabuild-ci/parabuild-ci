package net.sf.hibernate.dialect;

import java.sql.Types;

import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.Hibernate;

import net.sf.hibernate.sql.OracleJoinFragment;
import net.sf.hibernate.sql.JoinFragment;

/**
 * A SQL dialect for TimesTen 5.1.
 * 
 * Known limitations:
 * joined-subclass support because of no CASE support in TimesTen
 * No support for subqueries that includes aggregation
 *  - size() in HQL not supported
 *  - user queries that does subqueries with aggregation
 * No CLOB/BLOB support 
 *  
 * @author Sherry Listgarten and Max Andersen
 */
public class TimesTenDialect extends Dialect {
	
	public TimesTenDialect() {
		super();
		registerColumnType( Types.BIT, "TINYINT" );
		registerColumnType( Types.BIGINT, "BIGINT" );
		registerColumnType( Types.SMALLINT, "SMALLINT" );
		registerColumnType( Types.TINYINT, "TINYINT" );
		registerColumnType( Types.INTEGER, "INTEGER" );
		registerColumnType( Types.CHAR, "CHAR(1)" );
		registerColumnType( Types.VARCHAR, "VARCHAR($l)" );
		registerColumnType( Types.FLOAT, "FLOAT" );
		registerColumnType( Types.DOUBLE, "DOUBLE" );
		registerColumnType( Types.DATE, "DATE" );
		registerColumnType( Types.TIME, "TIME" );
		registerColumnType( Types.TIMESTAMP, "TIMESTAMP" );
		registerColumnType( Types.VARBINARY, "VARBINARY($l)" );
		registerColumnType( Types.NUMERIC, "DECIMAL(19, $l)" );
		// TimesTen has no BLOB/CLOB support, but these types may be suitable 
		// for some applications. The length is limited to 4 million bytes.
        registerColumnType( Types.BLOB, "VARBINARY(4000000)" ); 
        registerColumnType( Types.CLOB, "VARCHAR(4000000)" );
	
		getDefaultProperties().setProperty(Environment.USE_STREAMS_FOR_BINARY, "true");
		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);
		registerFunction( "lower", new StandardSQLFunction() );
		registerFunction( "upper", new StandardSQLFunction() );
		registerFunction( "rtrim", new StandardSQLFunction() );
		registerFunction( "concat", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction( "mod", new StandardSQLFunction() );
		registerFunction( "to_char", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction( "to_date", new StandardSQLFunction(Hibernate.TIMESTAMP) );
		registerFunction( "sysdate", new NoArgSQLFunction(Hibernate.DATE, false) );
		registerFunction( "getdate", new NoArgSQLFunction(Hibernate.DATE, false) );
		registerFunction( "nvl", new StandardSQLFunction() );

	}
	
	public boolean dropConstraints() {
            return true;
	}
	
	public boolean qualifyIndexName() {
            return false;
	}

	public boolean supportsUnique() {
		return false;
	}
    
    public boolean supportsUniqueConstraintInCreateAlterTable() {
        return false;
    }

	public String getAddColumnString() {
            return "add";
	}

	public boolean supportsSequences() {
		return true;
	}

	public String getSequenceNextValString(String sequenceName) {
		return "select first 1 " + sequenceName + ".nextval from sys.tables";
	}
	public String getCreateSequenceString(String sequenceName) {
		return "create sequence " + sequenceName;
	}
	public String getDropSequenceString(String sequenceName) {
		return "drop sequence " + sequenceName;
	}
	public String getQuerySequencesString() {
		return "select NAME from sys.sequences";
	}

	public JoinFragment createOuterJoinFragment() {
		return new OracleJoinFragment();
	}

	public boolean supportsForUpdateNowait() {
		return false;
	}
	
	public boolean supportsCheck() {
		return false;
	}

}







