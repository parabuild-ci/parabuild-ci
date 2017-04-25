//$Id: NamingStrategy.java,v 1.6 2004/06/04 05:43:45 steveebersole Exp $
package net.sf.hibernate.cfg;

/**
 * A set of rules for determining the physical column
 * and table names given the information in the mapping
 * document. May be used to implement project-scoped
 * naming standards for database objects.
 * 
 * @see DefaultNamingStrategy
 * @see ImprovedNamingStrategy
 * @author Gavin King
 */
public interface NamingStrategy {
	/**
	 * Return a table name for an entity class
	 * @param className the fully-qualified class name
	 * @return a table name
	 */
	public String classToTableName(String className);
	/**
	 * Return a column name for a property path expression 
	 * @param propertyName a property path
	 * @return a column name
	 */
	public String propertyToColumnName(String propertyName);
	/**
	 * Alter the table name given in the mapping document
	 * @param tableName a table name
	 * @return a table name
	 */
	public String tableName(String tableName);
	/**
	 * Alter the column name given in the mapping document
	 * @param columnName a column name
	 * @return a column name
	 */
	public String columnName(String columnName);
	/**
	 * Return a table name for a collection
	 * @param className the fully-qualified name of the owning entity class
	 * @param propertyName a property path
	 * @return a table name
	 */
	public String propertyToTableName(String className, String propertyName);
}
