//$Id: TypedValue.java,v 1.9 2004/06/04 01:27:39 steveebersole Exp $
package net.sf.hibernate.engine;

import java.io.Serializable;

import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.EqualsHelper;

/**
 * An ordered pair of a value and its Hibernate type.
 * @see net.sf.hibernate.type.Type
 * @author Gavin King
 */
public final class TypedValue implements Serializable {
	private Type type;
	private Object value;
	
	public TypedValue(Type t, Object o) {
		setType(t); value=o;
	}
	
	public void setValue(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
	
	public void setType(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
	
	public String toString() {
		return value.toString();
	}
	
	public int hashCode() {
		int result = 17;
		result = 37 * result + type.hashCode();
		result = 37 * result + ( value==null ? 0 : value.hashCode() );
		return result;
	}
	
	public boolean equals(Object other) {
		if ( !(other instanceof TypedValue) ) return false;
		TypedValue that = (TypedValue) other;
		return that.type.equals(type)
			&& EqualsHelper.equals(that.value, value);
	}
	
}





