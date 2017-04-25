//$Id: Component.java,v 1.11 2004/06/04 01:27:42 steveebersole Exp $
package net.sf.hibernate.mapping;

import java.util.ArrayList;
import java.util.Iterator;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.util.JoinedIterator;

/**
 * The mapping for a component, composite element, composite identifier,
 * etc.
 * @author Gavin King
 */
public class Component extends SimpleValue {
	
	private ArrayList properties = new ArrayList();
	private Class componentClass;
	private boolean embedded;
	private String parentProperty;
	private PersistentClass owner;
	private boolean dynamic;
	
	public int getPropertySpan() {
		return properties.size();
	}
	public Iterator getPropertyIterator() {
		return properties.iterator();
	}
	public void addProperty(Property p) {
		properties.add(p);
	}
	public void addColumn(Column column) {
		throw new UnsupportedOperationException("Cant add a column to a component");
	}
	public int getColumnSpan() {
		int n=0;
		Iterator iter = getPropertyIterator();
		while ( iter.hasNext() ) {
			Property p = (Property) iter.next();
			n+= p.getColumnSpan();
		}
		return n;
	}
	public Iterator getColumnIterator() {
		Iterator[] iters = new Iterator[ getPropertySpan() ];
		Iterator iter = getPropertyIterator();
		int i=0;
		while ( iter.hasNext() ) {
			iters[i++] = ( (Property) iter.next() ).getColumnIterator();
		}
		return new JoinedIterator(iters);
	}
	
	public Component(PersistentClass owner) throws MappingException {
		super( owner.getTable() );
		this.owner = owner;
	}

	public Component(Table table) throws MappingException {
		super(table);
		this.owner = null;
	}

	public void setTypeByReflection(Class propertyClass, String propertyName) throws MappingException {
	}
	
	public boolean isEmbedded() {
		return embedded;
	}
	
	/**
	 * Returns the componentClass.
	 * @return Class
	 */
	public Class getComponentClass() {
		return componentClass;
	}

	/**
	 * Returns the owner.
	 * @return PersistentClass
	 */
	public PersistentClass getOwner() {
		return owner;
	}

	/**
	 * Returns the parentProperty.
	 * @return String
	 */
	public String getParentProperty() {
		return parentProperty;
	}

	/**
	 * Sets the componentClass.
	 * @param componentClass The componentClass to set
	 */
	public void setComponentClass(Class componentClass) {
		this.componentClass = componentClass;
	}

	/**
	 * Sets the embedded.
	 * @param embedded The embedded to set
	 */
	public void setEmbedded(boolean embedded) {
		this.embedded = embedded;
	}

	/**
	 * Sets the owner.
	 * @param owner The owner to set
	 */
	public void setOwner(PersistentClass owner) {
		this.owner = owner;
	}

	/**
	 * Sets the parentProperty.
	 * @param parentProperty The parentProperty to set
	 */
	public void setParentProperty(String parentProperty) {
		this.parentProperty = parentProperty;
	}

	/**
	 * @return Returns the dynamic.
	 */
	public boolean isDynamic() {
		return dynamic;
	}

	/**
	 * @param dynamic The dynamic to set.
	 */
	public void setDynamic(boolean dynamic) {
		this.dynamic = dynamic;
	}

}







