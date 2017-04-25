//$Id: UserType.java,v 1.11 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This interface should be implemented by user-defined "types".
 * A "type" class is <em>not</em> the actual property type - it
 * is a class that knows how to serialize instances of another 
 * class to and from JDBC.<br>
 * <br>
 * This interface 
 * <ul>
 * <li>abstracts user code from future changes to the <tt>Type</tt>
 * interface,</li>
 * <li>simplifies the implementation of custom types and</li>
 * <li>hides certain "internal" interfaces from user code.</li>
 * </ul>
 * <br>
 * Implementors must be immutable and must declare a public
 * default constructor.<br>
 * <br>
 * The actual class mapped by a <tt>UserType</tt> may be just
 * about anything. However, if it is to be cacheable by JCS, it 
 * must be serializable.<br>
 * <br>
 * <tt>CompositeUserType</tt> provides an extended version of
 * this interface that is useful for more complex cases.<br>
 * <br>
 * Alternatively, custom types could implement <tt>Type</tt>
 * directly or extend one of the abstract classes in
 * <tt>net.sf.hibernate.type</tt>. This approach risks future
 * incompatible changes to classes or interfaces in that
 * package.
 *
 * @see CompositeUserType for more complex cases
 * @see net.sf.hibernate.type.Type
 * @author Gavin King
 */
public interface UserType {
	
	/**
	 * Return the SQL type codes for the columns mapped by this type. The
	 * codes are defined on <tt>java.sql.Types</tt>.
	 * @see java.sql.Types
	 * @return int[] the typecodes
	 */
	public int[] sqlTypes();
	
	/**
	 * The class returned by <tt>nullSafeGet()</tt>.
	 *
	 * @return Class
	 */
	public Class returnedClass();
	
	/**
	 * Compare two instances of the class mapped by this type for persistence "equality".
	 * Equality of the persistent state.
	 *
	 * @param x
	 * @param y
	 * @return boolean
	 */
	public boolean equals(Object x, Object y) throws HibernateException;
	
	/**
	 * Retrieve an instance of the mapped class from a JDBC resultset. Implementors
	 * should handle possibility of null values.
	 *
	 * @param rs a JDBC result set
	 * @param names the column names
	 * @param owner the containing entity
	 * @return Object
	 * @throws HibernateException
	 * @throws SQLException
	 */
	public Object nullSafeGet(ResultSet rs, String[] names, Object owner) throws HibernateException, SQLException;
	
	/**
	 * Write an instance of the mapped class to a prepared statement. Implementors
	 * should handle possibility of null values. A multi-column type should be written
	 * to parameters starting from <tt>index</tt>.
	 *
	 * @param st a JDBC prepared statement
	 * @param value the object to write
	 * @param index statement parameter index
	 * @throws HibernateException
	 * @throws SQLException
	 */
	public void nullSafeSet(PreparedStatement st, Object value, int index) throws HibernateException, SQLException;
	
	/**
	 * Return a deep copy of the persistent state, stopping at entities and at
	 * collections.
	 *
	 * @return Object a copy
	 */
	public Object deepCopy(Object value) throws HibernateException;
	
	/**
	 * Are objects of this type mutable?
	 *
	 * @return boolean
	 */
	public boolean isMutable();
}






