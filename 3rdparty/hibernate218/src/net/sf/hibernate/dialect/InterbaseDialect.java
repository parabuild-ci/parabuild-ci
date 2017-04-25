package net.sf.hibernate.dialect;

import java.sql.Types;

import net.sf.hibernate.cfg.Environment;

/**
 * An SQL dialect for Interbase.
 * @author Gavin King
 */

public class InterbaseDialect extends Dialect {

	public InterbaseDialect() {
		super();
		registerColumnType( Types.BIT, "smallint" );
		registerColumnType( Types.BIGINT, "numeric(18,0)" );
		registerColumnType( Types.SMALLINT, "smallint" );
		registerColumnType( Types.TINYINT, "smallint" );
		registerColumnType( Types.INTEGER, "integer" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.VARCHAR, "varchar($l)" );
		registerColumnType( Types.FLOAT, "float" );
		registerColumnType( Types.DOUBLE, "double precision" );
		registerColumnType( Types.DATE, "date" );
		registerColumnType( Types.TIME, "time" );
		registerColumnType( Types.TIMESTAMP, "timestamp" );
		registerColumnType( Types.VARBINARY, "blob" );
		registerColumnType( Types.NUMERIC, "numeric(18, $l)" );
		registerColumnType( Types.BLOB, "blob" );
		registerColumnType( Types.CLOB, "blob sub_type 1" );

		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, NO_BATCH);
	}

	public String getAddColumnString() {
		return "add";
	}

	public String getSequenceNextValString(String sequenceName) {
		return "select gen_id( " + sequenceName + ", 1 ) from RDB$DATABASE";
	}

	public String getCreateSequenceString(String sequenceName) {
		return "create generator " + sequenceName;
	}

	public String getDropSequenceString(String sequenceName) {
		return "delete from RDB$GENERATORS where RDB$GENERATOR_NAME = '" + sequenceName.toUpperCase() + "'";
	}

	public boolean supportsForUpdateOf() {
		return true;
	}

	public boolean supportsSequences() {
		return true;
	}

	public boolean supportsLimit() {
		return true;
	}

	public String getLimitString(String sql, boolean hasOffset) {
		return new StringBuffer( sql.length()+15 )
			.append(sql)
			.append(hasOffset ? " rows ? to ?" : " rows ?")
			.toString();
	}

	public boolean bindLimitParametersFirst() {
		return false;
	}

	public boolean bindLimitParametersInReverseOrder() {
		return false;
	}

}