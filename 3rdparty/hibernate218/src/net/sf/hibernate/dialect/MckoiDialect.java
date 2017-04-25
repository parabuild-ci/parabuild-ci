//$Id: MckoiDialect.java,v 1.15 2004/08/08 08:23:16 oneovthafew Exp $
// Contributed by Gabe Hicks
package net.sf.hibernate.dialect;

import java.sql.Types;

import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.sql.CaseFragment;
import net.sf.hibernate.sql.MckoiCaseFragment;

/**
 * An SQL dialect compatible with McKoi SQL
 * @author Doug Currie
 */

public class MckoiDialect extends Dialect {
	public MckoiDialect() {
		super();
		registerColumnType( Types.BIT, "bit" );
		registerColumnType( Types.BIGINT, "bigint" );
		registerColumnType( Types.SMALLINT, "smallint" );
		registerColumnType( Types.TINYINT, "tinyint" );
		registerColumnType( Types.INTEGER, "integer" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.VARCHAR, "varchar($l)" );
		registerColumnType( Types.FLOAT, "float" );
		registerColumnType( Types.DOUBLE, "double" );
		registerColumnType( Types.DATE, "date" );
		registerColumnType( Types.TIME, "time" );
		registerColumnType( Types.TIMESTAMP, "timestamp" );
		registerColumnType( Types.VARBINARY, "varbinary" );
		registerColumnType( Types.NUMERIC, "numeric" );
		registerColumnType( Types.BLOB, "blob" );
		registerColumnType( Types.CLOB, "clob" );

		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, NO_BATCH);
	}

	public String getAddColumnString() {
		return "add column";
	}
	public String getSequenceNextValString(String sequenceName) {
		return  "select uniquekey('" + sequenceName + "')";
	}
	public String getCreateSequenceString(String sequenceName) {
		return "create table " + sequenceName + "(id numeric)";
	}
	public String getDropSequenceString(String sequenceName) {
		return "drop table " + sequenceName;
	}
	public boolean supportsForUpdate() {
		return false;
	}
	public boolean supportsSequences() {
		return true;
	}

	public CaseFragment createCaseFragment() {
		return new MckoiCaseFragment();
	}

}






