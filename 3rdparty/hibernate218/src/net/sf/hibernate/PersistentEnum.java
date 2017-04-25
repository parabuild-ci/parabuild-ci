//$Id: PersistentEnum.java,v 1.7 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

/**
 * Implementors of <tt>PersistentEnum</tt> are enumerated types persisted to
 * the database as <tt>SMALLINT</tt>s. As well as implementing <tt>toInt()</tt>,
 * a <tt>PersistentEnum</tt> must also provide a static method with the
 * signature:<br>
 * <br>
 * 		<tt>public static PersistentEnum fromInt(int i)</tt>
 *
 * @author Gavin King
 * @deprecated Support for PersistentEnums will be removed in 2.2
 */
public interface PersistentEnum {
	public int toInt();
}






