//$Id: NamedSQLQuery.java,v 1.6 2004/11/07 16:55:41 maxcsaucdk Exp $
package net.sf.hibernate.mapping;

import java.util.ArrayList;
import java.util.List;

import net.sf.hibernate.util.ArrayHelper;

/**
 * Simple value holder for named sql queries.
 * 
 * @author Max Andersen
 */
public class NamedSQLQuery {
	
	private String query;
	private List aliasedClasses;
	private List synchronizedTables;
	private ArrayList aliases;

	public NamedSQLQuery(String query) {
		this.aliases = new ArrayList();
		this.aliasedClasses = new ArrayList();
		this.query = query;
		this.synchronizedTables = new ArrayList();
	}

	public String[] getReturnAliases() {
		return (String[]) aliases.toArray(ArrayHelper.EMPTY_STRING_ARRAY);
	}

	public Class[] getReturnClasses() {
		return (Class[]) aliasedClasses.toArray(ArrayHelper.EMPTY_CLASS_ARRAY);
	}

	public String getQueryString() {
		return query;
	}
	
	public void addSynchronizedTable(String table) {
		synchronizedTables.add(table);
	}
	
	public void addAliasedClass(String alias, Class clazz) {
		aliases.add(alias);
		aliasedClasses.add(clazz);
	}
	
	public List getSynchronizedTables() {
		return synchronizedTables;
	}
}
