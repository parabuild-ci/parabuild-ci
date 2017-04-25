//$Id: Loadable.java,v 1.14 2004/06/04 01:28:50 steveebersole Exp $
package net.sf.hibernate.persister;

import net.sf.hibernate.type.Type;

/**
 * Implemented by a <tt>ClassPersister</tt> that may be loaded
 * using <tt>Loader</tt>.
 *
 * @see net.sf.hibernate.loader.Loader
 * @author Gavin King
 */
public interface Loadable extends ClassPersister {

	/**
	 * Does this persistent class have subclasses?
	 */
	public boolean hasSubclasses();

	/**
	 * Get the discriminator type
	 */
	public Type getDiscriminatorType();

	/**
	 * Get the concrete subclass corresponding to the given discriminator
	 * value
	 */
	public Class getSubclassForDiscriminatorValue(Object value);

	/**
	 * Get the result set aliases used for the identifier columns, given a suffix
	 */
	public String[] getIdentifierAliases(String suffix);
	/**
	 * Get the result set aliases used for the property columns, given a suffix (properties of this class, only).
	 */
	public String[] getPropertyAliases(String suffix, int i);
	/**
	 * Get the result set aliases used for the identifier columns, given a suffix
	 */
	public String getDiscriminatorAlias(String suffix);
	
}






