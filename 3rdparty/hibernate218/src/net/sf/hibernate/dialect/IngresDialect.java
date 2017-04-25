//$Id: IngresDialect.java,v 1.9 2004/08/07 00:10:55 oneovthafew Exp $
package net.sf.hibernate.dialect;

import java.sql.Types;


/**
 * An Ingres SQL dialect
 * @author Ian Booth
 */
public class IngresDialect extends Dialect {

	public IngresDialect() {
		super();
		registerColumnType( Types.BIT, "byte" );
		registerColumnType( Types.BIGINT, "integer4" );
		registerColumnType( Types.SMALLINT, "integer2" );
		registerColumnType( Types.TINYINT, "integer1" );
		registerColumnType( Types.INTEGER, "integer4" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.VARCHAR, "varchar($l)" );
		registerColumnType( Types.FLOAT, "float" );
		registerColumnType( Types.DOUBLE, "double precision" );
		registerColumnType( Types.DATE, "date" );
		registerColumnType( Types.TIME, "date" );
		registerColumnType( Types.TIMESTAMP, "date" );
		registerColumnType( Types.VARBINARY, "varbinary($l)" );
		registerColumnType( Types.NUMERIC, "numeric(19, $l)" );
		registerColumnType( Types.BLOB, "long varchar" );
		registerColumnType( Types.CLOB, "long varchar" );
		registerColumnType( Types.VARBINARY, "long varchar" );
	}

	public String getAddColumnString() {
		return "add column";
	}

 	/**
	 * Do we need to drop constraints before dropping tables in this dialect?
	 * @return boolean
	 */
	public boolean dropConstraints() {
		return false;
	}

	public String getLowercaseFunction() {
		return "lowercase";
	}

}
