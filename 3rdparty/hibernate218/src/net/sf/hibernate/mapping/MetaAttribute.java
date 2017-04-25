//$Id: MetaAttribute.java,v 1.6 2004/06/04 05:43:47 steveebersole Exp $
package net.sf.hibernate.mapping;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A meta attribute is a named value or values.
 * @author Gavin King
 */
public class MetaAttribute {
	private String name;
	private java.util.List values = new ArrayList();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public java.util.List getValues() {
		return Collections.unmodifiableList(values);
	}

	public void addValue(String value) {
		values.add(value);
	}
	
	public String getValue() {
		if ( values.size()!=1 ) throw new IllegalStateException("no unique value");
		return (String) values.get(0);
	}
	
	public boolean isMultiValued() {
		return values.size()>1;
	}

}
