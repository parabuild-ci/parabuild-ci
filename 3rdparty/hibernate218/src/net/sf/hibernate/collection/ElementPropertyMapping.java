package net.sf.hibernate.collection;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.QueryException;
import net.sf.hibernate.persister.PropertyMapping;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.StringHelper;

/**
 * @author Gavin King
 */
public class ElementPropertyMapping implements PropertyMapping {
	
	private final String[] elementColumns;
	private final Type type;
	
	public ElementPropertyMapping(String[] elementColumns, Type type)
	throws MappingException {
		this.elementColumns = elementColumns;
		this.type = type;
	}

	public Type toType(String propertyName) throws QueryException {
		if (propertyName==null || "id".equals(propertyName) ) {
			return type;
		}
		else {
			throw new QueryException("cannot dereference scalar collection element: " + propertyName);
		}
	}

	public String[] toColumns(String alias, String propertyName) throws QueryException {
		if (propertyName==null || "id".equals(propertyName) ) {
			return StringHelper.qualify(alias, elementColumns);
		}
		else {
			throw new QueryException("cannot dereference scalar collection element: " + propertyName);
		}
	}

	public Type getType() {
		return type;
	}

}
