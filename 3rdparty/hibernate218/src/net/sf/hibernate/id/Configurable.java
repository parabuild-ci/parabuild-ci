//$Id: Configurable.java,v 1.8 2004/06/04 05:43:46 steveebersole Exp $
package net.sf.hibernate.id;

import java.util.Properties;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.type.Type;

/**
 * An <tt>IdentifierGenerator</tt> that supports "configuration".
 * 
 * @see IdentifierGenerator
 * @author Gavin King
 */
public interface Configurable {

	/**
	 * Configure this instance, given the value of parameters
	 * specified by the user as <tt>&lt;param&gt;</tt> elements.
	 * This method is called just once, following instantiation.
	 * 
	 * @param params param values, keyed by parameter name
	 */
	public void configure(Type type, Properties params, Dialect d) throws MappingException;

}
