//$Id: Property.java,v 1.18 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.mapping;

import java.util.Iterator;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.PropertyNotFoundException;
import net.sf.hibernate.engine.Cascades;
import net.sf.hibernate.engine.Mapping;
import net.sf.hibernate.property.Getter;
import net.sf.hibernate.property.PropertyAccessor;
import net.sf.hibernate.property.PropertyAccessorFactory;
import net.sf.hibernate.property.Setter;
import net.sf.hibernate.type.AbstractComponentType;
import net.sf.hibernate.type.Type;

/**
 * Mapping for a property of a Java class (entity
 * or component)
 * @author Gavin King
 */
public class Property {
	
	private String name;
	private Value value;
	private String cascade;
	private boolean updateable = true;
	private boolean insertable = true;
	private String propertyAccessorName;
	private java.util.Map metaAttributes;
	
	public Type getType() {
		return value.getType();
	}
	public int getColumnSpan() {
		return value.getColumnSpan();
	}
	public Iterator getColumnIterator() {
		return value.getColumnIterator();
	}
	public String getName() {
		return name;
	}
	public boolean isUpdateable() {
		return updateable && !isFormula();
	}
	
	public boolean isComposite() {
		return value instanceof Component;
	}
	
	public Value getValue() {
		return value;
	}
	
	public Cascades.CascadeStyle getCascadeStyle() throws MappingException {
		Type type = value.getType();
		if ( type.isComponentType() && !type.isObjectType() ) {
			AbstractComponentType actype = (AbstractComponentType) type;
			int length = actype.getSubtypes().length;
			for ( int i=0; i<length; i++ ) {
				if ( actype.cascade(i)!=Cascades.STYLE_NONE ) return Cascades.STYLE_ALL;
			}
			return Cascades.STYLE_NONE;
		}
		else {
			if ( cascade==null || cascade.equals("none") ) {
				return Cascades.STYLE_NONE;
			}
			else if ( cascade.equals("all") ) {
				return Cascades.STYLE_ALL;
			}
			else if ( cascade.equals("all-delete-orphan") ) {
				return Cascades.STYLE_ALL_DELETE_ORPHAN;
			}
			else if ( cascade.equals("save-update") ) {
				return Cascades.STYLE_SAVE_UPDATE;
			}
			/*else if ( cascade.equals("save") ) {
				return Cascades.STYLE_SAVE;
			}*/
			else if ( cascade.equals("delete") ) {
				return Cascades.STYLE_ONLY_DELETE;
			}
			else if ( cascade.equals("delete-orphan") ) {
				return Cascades.STYLE_DELETE_ORPHAN;
			}
			else {
				throw new MappingException("Unsupported cascade style: " + cascade);
			}
		}
	}
	
	
	/**
	 * Returns the cascade.
	 * @return String
	 */
	public String getCascade() {
		return cascade;
	}

	/**
	 * Sets the cascade.
	 * @param cascade The cascade to set
	 */
	public void setCascade(String cascade) {
		this.cascade = cascade;
	}

	/**
	 * Sets the mutable.
	 * @param mutable The mutable to set
	 */
	public void setUpdateable(boolean mutable) {
		this.updateable = mutable;
	}

	/**
	 * Sets the name.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the value.
	 * @param value The value to set
	 */
	public void setValue(Value value) {
		this.value = value;
	}

	/**
	 * Returns the insertable.
	 * @return boolean
	 */
	public boolean isInsertable() {
		return insertable && !isFormula();
	}

	/**
	 * Sets the insertable.
	 * @param insertable The insertable to set
	 */
	public void setInsertable(boolean insertable) {
		this.insertable = insertable;
	}
	
	public Formula getFormula() {
		return value.getFormula();
	}
	
	public boolean isFormula() {
		return getFormula()!=null;
	}

	public String getPropertyAccessorName() {
		return propertyAccessorName;
	}

	public void setPropertyAccessorName(String string) {
		propertyAccessorName = string;
	}
	
	/**
	 * Approximate!
	 */
	public boolean isNullable() {
		return value==null || value.isNullable();
	}
	
	public Getter getGetter(Class clazz) throws PropertyNotFoundException, MappingException {
		return getPropertyAccessor().getGetter(clazz, name);
	}
	public Setter getSetter(Class clazz) throws PropertyNotFoundException, MappingException {
		return getPropertyAccessor().getSetter(clazz, name);
	}
	protected PropertyAccessor getPropertyAccessor() throws MappingException {
		return PropertyAccessorFactory.getPropertyAccessor( getPropertyAccessorName() );
	}
	public boolean isBasicPropertyAccessor() {
		return propertyAccessorName==null || "property".equals(propertyAccessorName);
	}
	public java.util.Map getMetaAttributes() {
		return metaAttributes;
	}
	public MetaAttribute getMetaAttribute(String name) {
		return (MetaAttribute) metaAttributes.get(name);
	}

	public void setMetaAttributes(java.util.Map metas) {
		this.metaAttributes = metas;
	}
	
	public boolean isValid(Mapping mapping) throws MappingException {
		return isFormula() ? 
			getColumnSpan()==0 :
			getValue().isValid(mapping);
	}
	
	public String getNullValue() {
		if (value instanceof SimpleValue) {
			return ( (SimpleValue) value ).getNullValue();
		}
		else {
			return null;
		}
	}

}







