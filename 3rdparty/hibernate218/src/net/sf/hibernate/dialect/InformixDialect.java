//$Id: InformixDialect.java,v 1.15 2004/12/24 02:58:26 oneovthafew Exp $
package net.sf.hibernate.dialect;

import java.sql.Types;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.util.StringHelper;

/**
 * Informix dialect. This class is required in order to use Hibernate with
 * Informix.<br>
 * <br>
 * Seems to work with Informix Dynamic Server Version 7.31.UD3,
 * Informix JDBC driver version 2.21JC3.
 * @author Steve Molitor
 */
public class InformixDialect extends Dialect {

	/**
	 * Creates new <code>InformixDialect</code> instance. Sets up the JDBC /
	 * Informix type mappings.
	 */
	public InformixDialect() {
		super();

		registerColumnType(Types.BIGINT, "int8");
		registerColumnType(Types.BINARY, "byte");
		registerColumnType(Types.BIT, "smallint"); // Informix doesn't have a bit type
		registerColumnType(Types.CHAR, "char($l)");
		registerColumnType(Types.DATE, "date");
		registerColumnType(Types.DECIMAL, "decimal");
		registerColumnType(Types.DOUBLE, "double");
		registerColumnType(Types.FLOAT, "float");
		registerColumnType(Types.INTEGER, "integer");
		registerColumnType(Types.LONGVARBINARY, "blob"); // or BYTE
		registerColumnType(Types.LONGVARCHAR, "clob"); // or TEXT?
		registerColumnType(Types.NUMERIC, "decimal"); // or MONEY
		registerColumnType(Types.REAL, "smallfloat");
		registerColumnType(Types.SMALLINT, "smallint");
		registerColumnType(Types.TIMESTAMP, "datetime year to fraction(5)");
		registerColumnType(Types.TIME, "datetime hour to second");
		registerColumnType(Types.TINYINT, "smallint");
		registerColumnType(Types.VARBINARY, "byte");
		registerColumnType(Types.VARCHAR, "varchar($l)");
	}

	public String getAddColumnString() {
		return "add";
	}

	public boolean supportsIdentityColumns() {
		return true;
	}

	public String getIdentitySelectString() throws MappingException {
		return "select first 1 dbinfo('sqlca.sqlerrd1') from systables";
	}

	public String getIdentityColumnString() throws MappingException {
		return "serial8 not null";
	}

	public boolean hasDataTypeInIdentityColumn() {
		return false;
	}

	/**
	 * The syntax used to add a foreign key constraint to a table.
	 * Informix constraint name must be at the end.
	 * @return String
	 */
	public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey) {
		return new StringBuffer(30)
		.append(" add constraint ")
		.append(" foreign key (")
		.append( StringHelper.join(StringHelper.COMMA_SPACE, foreignKey) )
		.append(") references ")
		.append(referencedTable)
		.append(" constraint ")
		.append(constraintName)
		.toString();
	}

	/**
	 * The syntax used to add a primary key constraint to a table.
	 * Informix constraint name must be at the end.
	 * @return String
	 */
	public String getAddPrimaryKeyConstraintString(String constraintName) {
		return " add constraint primary key constraint " + constraintName + " ";
	}

	public String getCreateSequenceString(String sequenceName) {
		return "create sequence " + sequenceName;
	}
	public String getDropSequenceString(String sequenceName) {
		return "drop sequence " + sequenceName + " restrict";
	}

	public String getSequenceNextValString(String sequenceName) {
		return "select " + sequenceName + ".nextval from systables where tabid=1";
	}

	public boolean supportsSequences() {
		return true;
	}

	public boolean supportsLimit() {
		return true;
	}

	public boolean useMaxForLimit() {
		return true;
	}

	public boolean supportsLimitOffset() {
		return false;
	}

	public String getLimitString(String querySelect, boolean hasOffset, int limit) {
		if (hasOffset) throw new UnsupportedOperationException("informix has no offset");
		return new StringBuffer( querySelect.length()+8 )
			.append(querySelect)
			.insert( getAfterSelectInsertPoint(querySelect), " first " + limit )
			.toString();
	}

	private static int getAfterSelectInsertPoint(String sql) {
		return sql.startsWith("select distinct") ? 15 : 6;
	}

	public boolean supportsVariableLimit() {
		return false;
	}

}