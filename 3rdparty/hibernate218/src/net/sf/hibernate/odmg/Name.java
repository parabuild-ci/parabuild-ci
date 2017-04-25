//$Id: Name.java,v 1.7 2004/06/04 06:50:29 steveebersole Exp $
package net.sf.hibernate.odmg;

import java.io.Serializable;

/** An ODMG name that may be bound to a persistent object.
 */
public class Name {
	private String name;
	private Class persistentClass;
	private Serializable id;

	public Name(String name, Class persistentClass, Serializable id) {
		this.name = name;
		this.persistentClass = persistentClass;
		this.id = id;
	}

	public Name()
	{
	}

	/**
	 * Returns the name. JavaDoc requires a second sentence.
	 * @return String
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the persistentClass. JavaDoc requires a second sentence.
	 * @return Class
	 */
	public Class getPersistentClass() {
		return persistentClass;
	}

	/**
	 * Sets the name. JavaDoc requires a second sentence.
	 * @param name The name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the persistentClass. JavaDoc requires a second sentence.
	 * @param persistentClass The persistentClass to set
	 */
	public void setPersistentClass(Class persistentClass) {
		this.persistentClass = persistentClass;
	}

	/**
	 * Returns the id. JavaDoc requires a second sentence.
	 * @return Serializable
	 */
	public Serializable getId() {
		return id;
	}

	/**
	 * Sets the id. JavaDoc requires a second sentence.
	 * @param id The id to set
	 */
	public void setId(Serializable id) {
		this.id = id;
	}
}

