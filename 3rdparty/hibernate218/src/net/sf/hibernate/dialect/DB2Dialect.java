//$Id: DB2Dialect.java,v 1.15 2004/08/13 06:39:27 oneovthafew Exp $
package net.sf.hibernate.dialect;

import java.sql.Types;

import net.sf.hibernate.Hibernate;
import net.sf.hibernate.cfg.Environment;

/**
 * An SQL dialect for DB2.
 * @author Gavin King
 */
public class DB2Dialect extends Dialect {

	public DB2Dialect() {
		super();
		registerColumnType( Types.BIT, "smallint" );
		registerColumnType( Types.BIGINT, "bigint" );
		registerColumnType( Types.SMALLINT, "smallint" );
		registerColumnType( Types.TINYINT, "smallint" );
		registerColumnType( Types.INTEGER, "integer" );
		registerColumnType( Types.CHAR, "char(1)" );
		registerColumnType( Types.VARCHAR, "varchar($l)" );
		registerColumnType( Types.FLOAT, "float" );
		registerColumnType( Types.DOUBLE, "double" );
		registerColumnType( Types.DATE, "date" );
		registerColumnType( Types.TIME, "time" );
		registerColumnType( Types.TIMESTAMP, "timestamp" );
		registerColumnType( Types.VARBINARY, "varchar($l) for bit data" );
		registerColumnType( Types.NUMERIC, "numeric(19, $l)" );
		registerColumnType( Types.BLOB, "blob($l)" );
		registerColumnType( Types.CLOB, "clob($l)" );

		registerFunction("abs", new StandardSQLFunction() );
		registerFunction("absval", new StandardSQLFunction() );
		registerFunction("sign", new StandardSQLFunction(Hibernate.INTEGER) );

		registerFunction("ceiling", new StandardSQLFunction() );
		registerFunction("ceil", new StandardSQLFunction() );
		registerFunction("floor", new StandardSQLFunction() );
		registerFunction("round", new StandardSQLFunction() );

		registerFunction("acos", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("asin", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("atan", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("cos", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("cot", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("degrees", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("exp", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("float", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("hex", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("ln", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("log", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("log10", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("radians", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("rand", new NoArgSQLFunction(Hibernate.DOUBLE));
		registerFunction("sin", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("soundex", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("sqrt", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("stddev", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("tan", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("variance", new StandardSQLFunction(Hibernate.DOUBLE) );

		registerFunction("julian_day", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("microsecond", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("midnight_seconds", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("minute", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("month", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("monthname", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("quarter", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("hour", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("second", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("date", new StandardSQLFunction(Hibernate.DATE) );
		registerFunction("day", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("dayname", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("dayofweek", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("dayofweek_iso", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("dayofyear", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("days", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction("time", new StandardSQLFunction(Hibernate.TIME) );
		registerFunction("timestamp", new StandardSQLFunction(Hibernate.TIMESTAMP) );
		registerFunction("timestamp_iso", new StandardSQLFunction(Hibernate.TIMESTAMP) );
		registerFunction("week", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("week_iso", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("year", new StandardSQLFunction(Hibernate.INTEGER) );

		registerFunction("double", new StandardSQLFunction(Hibernate.DOUBLE) );
		registerFunction("varchar", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("real", new StandardSQLFunction(Hibernate.FLOAT) );
		registerFunction("bigint", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction("char", new StandardSQLFunction(Hibernate.CHARACTER) );
		registerFunction("integer", new StandardSQLFunction(Hibernate.INTEGER) );
		registerFunction("smallint", new StandardSQLFunction(Hibernate.SHORT) );

		registerFunction("digits", new StandardSQLFunction(Hibernate.STRING) );
		registerFunction("chr", new StandardSQLFunction(Hibernate.CHARACTER) );
		registerFunction("upper", new StandardSQLFunction() );
		registerFunction("ucase", new StandardSQLFunction() );
		registerFunction("lcase", new StandardSQLFunction() );
		registerFunction("lower", new StandardSQLFunction() );
		registerFunction("length", new StandardSQLFunction(Hibernate.LONG) );
		registerFunction("ltrim", new StandardSQLFunction() );

		getDefaultProperties().setProperty(Environment.STATEMENT_BATCH_SIZE, NO_BATCH);
	}

	public String getAddColumnString() {
		return "add column";
	}
	public boolean dropConstraints() {
		return false;
	}
	public boolean supportsIdentityColumns() {
		return true;
	}
	public String getIdentitySelectString() {
		return "values identity_val_local()";
	}
	public String getIdentityColumnString() {
		return "not null generated by default as identity";
	}
	public String getIdentityInsertString() {
		return "default";
	}

	public String getSequenceNextValString(String sequenceName) {
		return "values nextval for " + sequenceName;
	}
	public String getCreateSequenceString(String sequenceName) {
		return "create sequence " + sequenceName;
	}
	public String getDropSequenceString(String sequenceName) {
		return "drop sequence " + sequenceName + " restrict";
	}

	public boolean supportsSequences() {
		return true;
	}

	public boolean supportsLimit() {
		return true;
	}

	/*public String getLimitString(String sql, boolean hasOffset) {
		StringBuffer rownumber = new StringBuffer(50)
			.append(" rownumber() over(");
		int orderByIndex = sql.toLowerCase().indexOf("order by");
		if (orderByIndex>0) rownumber.append( sql.substring(orderByIndex) );
		rownumber.append(") as row_,");
		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 )
			.append("select * from ( ")
			.append(sql)
			.insert( getAfterSelectInsertPoint(sql), rownumber.toString() )
			.append(" ) as temp_ where row_ ");
		if (hasOffset) {
			pagingSelect.append("between ?+1 and ?");
		}
		else {
			pagingSelect.append("<= ?");
		}
		return pagingSelect.toString();
	}*/

	/**
	 * Render the <tt>rownumber() over ( .... ) as rownumber_,</tt> 
	 * bit, that goes in the select list
	 */
	private String getRowNumber(String sql) {
		StringBuffer rownumber = new StringBuffer(50)
			.append("rownumber() over(");

		int orderByIndex = sql.toLowerCase().indexOf("order by");
		
		if ( orderByIndex>0 && !hasDistinct(sql) ) {
			rownumber.append( sql.substring(orderByIndex) );
		}
			 
		rownumber.append(") as rownumber_,");
		
		return rownumber.toString();
	}

	public String getLimitString(String sql, boolean hasOffset) {
		
		int startOfSelect = sql.indexOf("select");
		
		StringBuffer pagingSelect = new StringBuffer( sql.length()+100 )
					.append( sql.substring(0, startOfSelect) ) //add the comment
					.append("select * from ( select ") //nest the main query in an outer select
					.append( getRowNumber(sql) ); //add the rownnumber bit into the outer query select list
		
		if ( hasDistinct(sql) ) {
			pagingSelect.append(" row_.* from ( ") //add another (inner) nested select
				.append( sql.substring(startOfSelect) ) //add the main query
				.append(" ) as row_"); //close off the inner nested select
		}
		else {
			pagingSelect.append( sql.substring( startOfSelect + 6 ) ); //add the main query
		}
				
		pagingSelect.append(" ) as temp_ where rownumber_ ");
		
		//add the restriction to the outer select
		if (hasOffset) {
			pagingSelect.append("between ?+1 and ?");
		}
		else {
			pagingSelect.append("<= ?");
		}
		
		return pagingSelect.toString();
	}

	private static boolean hasDistinct(String sql) {
		return sql.indexOf("select distinct")>=0;
	}
	
	public boolean useMaxForLimit() {
		return true;
	}

}






