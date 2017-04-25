//$Id: Key.java,v 1.11 2004/06/04 01:27:38 steveebersole Exp $
package net.sf.hibernate.engine;

import java.io.Serializable;

import net.sf.hibernate.AssertionFailure;
import net.sf.hibernate.persister.ClassPersister;

/**
 * A globally unique identifier of an instance.
 *
 * Consisting of the user-visible identifier and the identifier space (eg. tablename).
 *
 * @author Gavin King
 */
public final class Key implements Serializable {
	private final Serializable identifier;
	private final Serializable identifierSpace;
	private final Class clazz;
	private final boolean isBatchLoadable;
	
	private Key(Serializable id, Serializable identifierSpace, Class clazz, boolean batchLoadable) {
		if (id==null) throw new AssertionFailure("null identifier");
		this.identifier=id;
		this.identifierSpace = identifierSpace;
		this.clazz = clazz;
		this.isBatchLoadable = batchLoadable;
	}
	
	/**
	 * Construct a unique identifier for an entity class instance
	 */
	public Key(Serializable id, ClassPersister p) {
		this( id, p.getIdentifierSpace(), p.getMappedClass(), p.isBatchLoadable() );
	}
	
	/**
	 * Get the user-visible identifier
	 */
	public Serializable getIdentifier() {
		return identifier;
	}
	
	public Class getMappedClass() {
		return clazz;
	}
	
	public boolean isBatchLoadable() {
		return isBatchLoadable;
	}
	
	public boolean equals(Object other) {
		Key otherKey = (Key) other;
		return otherKey.identifierSpace.equals(this.identifierSpace) && otherKey.identifier.equals(this.identifier);
	}
	
	public int hashCode() { 
		int result = 17;
		result = 37 * result + identifierSpace.hashCode();
		result = 37 * result + identifier.hashCode();
		return result;
	}
	
	public String toString() {
		return identifier.toString();
	}
}






