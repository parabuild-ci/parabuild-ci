//$Id: Databinder.java,v 1.8 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

import java.util.Collection;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;

/**
 * Provides XML Marshalling for classes registered with a <tt>SessionFactory</tt>.
 * Hibernate defines a <i>generic</i> XML format that may be used to
 * represent any class (<tt>hibernate-generic.dtd</tt>). The user configures
 * an XSLT stylesheet for marshalling data from this generic format to an
 * application and / or user readable format. By default, Hibernate will
 * use <tt>hibernate-default.xslt</tt> which maps data to a useful human -
 * readable format.<br>
 * <br>
 * The property <tt>hibernate.xml.output_stylesheet</tt>
 * specifies a user - written stylesheet. Hiberate will attempt to load the
 * stylesheet from the classpath first and if not found, it will attempt to
 * load it as a file.<br>
 * <br>
 * <i>This is an experimental feature.</i><br>
 * <br>
 * It is not intended that implementors be threadsafe.
 * 
 * @see SessionFactory
 * @author Gavin King, Brad Clow
 */
public interface Databinder {
	
	/**
	 * Add an object to the output document.
	 * @param object a transient or persistent instance
	 * @return Databinder
	 */
	public Databinder bind(Object object);
	
	/**
	 * Add a collection of objects to the output document.
	 * @param objects
	 * @return Databinder
	 */
	public Databinder bindAll(Collection objects);
	
	/**
	 * Output the generic XML representation of the bound objects.
	 *
	 * @return String generic XML representation
	 * @throws HibernateException
	 */
	public String toGenericXML() throws HibernateException;
	
	/**
	 * Output the generic XML representation of the bound objects as a DOM tree.
	 *
	 * @return Node generic XML tree
	 * @throws HibernateException
	 */
	public Document toGenericDOM() throws HibernateException;
	
	/**
	 * Output the custom XML representation of the bound objects.
	 *
	 * @return String custom XML representation
	 * @throws HibernateException
	 * @throws TransformerException
	 */
	public String toXML() throws HibernateException, TransformerException;
	
	/**
	 * Output the custom XML representation of the bound objects as a DOM tree.
	 *
	 * @return Node custom XML tree
	 * @throws HibernateException
	 * @throws TransformerException
	 */
	public Document toDOM() throws HibernateException, TransformerException;
	
	/**
	 * Controls lazy initialization.
	 *
	 * Controls whether bound objects (and their associated objects) that are lazily initialized are
	 * explicitly initialized or left as they are.
	 *
	 * @param initializeLazy true to explicitly initialize lazy objects, 
	 *  false to leave them in the state they are in.
	 */
	public void setInitializeLazy(boolean initializeLazy);
}






