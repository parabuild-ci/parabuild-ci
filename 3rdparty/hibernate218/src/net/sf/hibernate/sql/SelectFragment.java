//$Id: SelectFragment.java,v 1.10 2004/09/02 01:08:02 oneovthafew Exp $
package net.sf.hibernate.sql;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import net.sf.hibernate.util.StringHelper;

/**
 * A fragment of an SQL <tt>SELECT</tt> clause
 * 
 * @author Gavin King
 */
public class SelectFragment {
	private String suffix;
	private List columns = new ArrayList();
	//private List aliases = new ArrayList();
	private List columnAliases = new ArrayList();
	private String[] usedAliases;
	
	public SelectFragment setUsedAliases(String[] aliases) {
		usedAliases = aliases;
		return this;
	}
	

	public SelectFragment setSuffix(String suffix) {
		this.suffix = suffix;
		return this;
	}
	
	public SelectFragment addColumn(String columnName) {
		addColumn(null, columnName);
		return this;
	}

	public SelectFragment addColumns(String[] columnNames) {
		for (int i=0; i<columnNames.length; i++) addColumn( columnNames[i] );
		return this;
	}

	public SelectFragment addColumn(String tableAlias, String columnName) {
		return addColumn(tableAlias, columnName, columnName);
	}

	public SelectFragment addColumn(String tableAlias, String columnName, String columnAlias) {
		columns.add( StringHelper.qualify(tableAlias, columnName) );
		//columns.add(columnName);
		//aliases.add(tableAlias);
		columnAliases.add(columnAlias);
		return this;
	}

	public SelectFragment addColumns(String tableAlias, String[] columnNames) {
		for (int i=0; i<columnNames.length; i++) addColumn( tableAlias, columnNames[i] );
		return this;
	}

	public SelectFragment addColumns(String tableAlias, String[] columnNames, String[] columnAliases) {
		for (int i=0; i<columnNames.length; i++) addColumn( tableAlias, columnNames[i], columnAliases[i] );
		return this;
	}
	
	public SelectFragment addFormulas(String tableAlias, String[] formulas, String[] formulaAliases) {
		for ( int i=0; i<formulas.length; i++ ) addFormula( tableAlias, formulas[i], formulaAliases[i] );
		return this;
	}

	public SelectFragment addFormula(String tableAlias, String formula, String formulaAlias) {
		columns.add( StringHelper.replace(formula, Template.TEMPLATE, tableAlias) );
		columnAliases.add(formulaAlias);
		return this;
	}

	public String toFragmentString() {
		StringBuffer buf = new StringBuffer( columns.size() * 10 );
		Iterator iter = columns.iterator();
		Iterator columnAliasIter = columnAliases.iterator();
		HashSet columnsUnique = new HashSet(); 
		if (usedAliases!=null) columnsUnique.addAll( Arrays.asList(usedAliases) );
		while ( iter.hasNext() ) {
			String column = (String) iter.next();
			String columnAlias = (String) columnAliasIter.next();
			if ( columnsUnique.add(columnAlias) ) {
				buf.append(StringHelper.COMMA_SPACE)
					.append(column)
					.append(" as ")
					.append( new Alias(suffix).toAliasString(columnAlias) );
			}
		}
		return buf.toString();
	}
}
