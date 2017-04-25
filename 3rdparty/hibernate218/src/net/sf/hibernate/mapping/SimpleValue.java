//$Id: SimpleValue.java,v 1.5 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.mapping;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.MappingException;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.Mapping;
import net.sf.hibernate.id.IdentifierGenerator;
import net.sf.hibernate.id.IdentifierGeneratorFactory;
import net.sf.hibernate.loader.OuterJoinLoader;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.ReflectHelper;

/**
 * Any value that maps to columns.
 * @author Gavin King
 */
public class SimpleValue implements Value {
	
	private final List columns = new ArrayList();
	private Type type;
	private Properties identifierGeneratorProperties;
	private String identifierGeneratorStrategy = "assigned";
	private String nullValue;
	private Table table;
	private Formula formula;
	private String foreignKeyName;
	private boolean unique;
	
	public void addColumn(Column column) {
		if ( !columns.contains(column) ) columns.add(column);
	}
	public int getColumnSpan() {
		return columns.size();
	}
	public Iterator getColumnIterator() {
		return columns.iterator();
	}
	public List getConstraintColumns() {
		return columns;
	}
	
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
		Iterator iter = getColumnIterator();
		int count=0;
		while ( iter.hasNext() ) {
			Column col = (Column) iter.next();
			col.setType(type);
			col.setTypeIndex(count++);
		}
	}
	public void setTable(Table table) {
		this.table = table;
	}
	
	public SimpleValue(Table table) {
		this.table = table;
	}
	
	public SimpleValue() {}
	
	public void createForeignKey() {
	}
	
	public void createForeignKeyOfClass(Class persistentClass) {
		table.createForeignKey( getForeignKeyName(), getConstraintColumns(), persistentClass );
	}
	
	private IdentifierGenerator uniqueIdentifierGenerator;
	
	public IdentifierGenerator createIdentifierGenerator(Dialect dialect) throws MappingException {
		if (uniqueIdentifierGenerator==null) {
			uniqueIdentifierGenerator = IdentifierGeneratorFactory.create(
				identifierGeneratorStrategy, type, identifierGeneratorProperties, dialect
			);
		}
		return uniqueIdentifierGenerator;
	}
	
	public int getOuterJoinFetchSetting() { 
		return OuterJoinLoader.LAZY; 
	}
	
	public Properties getIdentifierGeneratorProperties() {
		return identifierGeneratorProperties;
	}
	
	public String getNullValue() {
		return nullValue;
	}
	
	public Table getTable() {
		return table;
	}

	/**
	 * Returns the identifierGeneratorStrategy.
	 * @return String
	 */
	public String getIdentifierGeneratorStrategy() {
		return identifierGeneratorStrategy;
	}

	/**
	 * Sets the identifierGeneratorProperties.
	 * @param identifierGeneratorProperties The identifierGeneratorProperties to set
	 */
	public void setIdentifierGeneratorProperties(Properties identifierGeneratorProperties) {
		this.identifierGeneratorProperties = identifierGeneratorProperties;
	}

	/**
	 * Sets the identifierGeneratorStrategy.
	 * @param identifierGeneratorStrategy The identifierGeneratorStrategy to set
	 */
	public void setIdentifierGeneratorStrategy(String identifierGeneratorStrategy) {
		this.identifierGeneratorStrategy = identifierGeneratorStrategy;
	}

	/**
	 * Sets the nullValue.
	 * @param nullValue The nullValue to set
	 */
	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
	}
	
	public void setFormula(Formula formula) {
		this.formula = formula;
	}
	public Formula getFormula() {
		return formula;
	}

	public String getForeignKeyName() {
		return foreignKeyName;
	}

	public void setForeignKeyName(String string) {
		foreignKeyName = string;
	}
	
	public boolean isUnique() {
		return unique;
	}
	
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	
	public boolean isNullable() {
		boolean nullable = true;
		Iterator iter = getColumnIterator();
		while ( iter.hasNext() ) {
			if ( !( (Column) iter.next() ).isNullable() ) nullable = false;
		}
		return nullable;
	} 

	public void setTypeByReflection(Class propertyClass, String propertyName) throws MappingException {
		try {
			if ( type==null ) {
				type = ReflectHelper.reflectedPropertyType(propertyClass, propertyName);
				Iterator iter = getColumnIterator();
				int count=0;
				while ( iter.hasNext() ) {
					Column col = (Column) iter.next();
					col.setType(type);
					col.setTypeIndex(count++);
				}
			}
		}
		catch (HibernateException he) {
			throw new MappingException( "Problem trying to set property type by reflection", he );
		}
	}
	
	public boolean isSimpleValue() {
		return true;
	}
	
	public boolean isValid(Mapping mapping) throws MappingException {
		return getColumnSpan()==getType().getColumnSpan(mapping);
	}

}






