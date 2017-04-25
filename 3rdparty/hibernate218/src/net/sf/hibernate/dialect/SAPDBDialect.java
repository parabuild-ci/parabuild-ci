//$Id: SAPDBDialect.java,v 1.18 2004/08/08 08:23:16 oneovthafew Exp $
// contributed by Brad Clow
package net.sf.hibernate.dialect;

import java.sql.Types;

import net.sf.hibernate.cfg.Environment;
import net.sf.hibernate.sql.CaseFragment;
import net.sf.hibernate.sql.DecodeCaseFragment;
import net.sf.hibernate.sql.OracleJoinFragment;
import net.sf.hibernate.sql.JoinFragment;
import net.sf.hibernate.util.StringHelper;

/**
 * An SQL dialect compatible with SAP DB.
 * @author Brad Clow
 */
public class SAPDBDialect extends Dialect {

	public SAPDBDialect() {
		super();
		registerColumnType( Types.BIT, "boolean" );
		registerColumnType( Types.BIGINT, "fixed(19,0)" );
		registerColumnType( Types.SMALLINT, "smallint" );
		registerColumnType( Types.TINYINT, "fixed(3,0)" );
		registerColumnType( Types.INTEGER, "int" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.VARCHAR, "varchar($l)" );
		registerColumnType( Types.FLOAT, "float" );
		registerColumnType( Types.DOUBLE, "double precision" );
		registerColumnType( Types.DATE, "date" );
		registerColumnType( Types.TIME, "time" );
		registerColumnType( Types.TIMESTAMP, "timestamp" );
		registerColumnType( Types.VARBINARY, "long byte" );
		registerColumnType( Types.NUMERIC, "fixed(19,$l)" );
		registerColumnType( Types.CLOB, "long varchar" );
		registerColumnType( Types.BLOB, "long byte" );

		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, DEFAULT_BATCH_SIZE);

	}

	public boolean supportsForUpdate() {
		return true;
	}

	public boolean dropConstraints() {
		return false;
	}

	public String getAddColumnString() {
		return "add";
	}

	public String getAddForeignKeyConstraintString(String constraintName, String[] foreignKey, String referencedTable, String[] primaryKey) {
		return new StringBuffer(30)
			.append(" foreign key ")
			.append(constraintName)
			.append(" (")
			.append( StringHelper.join(StringHelper.COMMA_SPACE, foreignKey) )
			.append(") references ")
			.append(referencedTable)
			.toString();
	}

	public String getAddPrimaryKeyConstraintString(String constraintName) {
		return " primary key ";
	}

	public String getNullColumnString() {
		return " null";
	}

	public String getSequenceNextValString(String sequenceName) {
		return  "select " + sequenceName + ".nextval from dual";
	}

	public String getCreateSequenceString(String sequenceName) {
		return "create sequence " + sequenceName;
	}

	public String getDropSequenceString(String sequenceName) {
		return "drop sequence " + sequenceName;
	}

	public String getQuerySequencesString() {
		return "select SEQUENCE_NAME from DOMAIN.SEQUENCES";
	}

	public JoinFragment createOuterJoinFragment() {
		return new OracleJoinFragment();
	}


	public boolean supportsSequences() {
		return true;
	}

	public CaseFragment createCaseFragment() {
		return new DecodeCaseFragment();
	}

}





