//$Id: TypeNames.java,v 1.9 2004/06/04 01:27:38 steveebersole Exp $
package net.sf.hibernate.dialect;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Iterator;

import net.sf.hibernate.MappingException;
import net.sf.hibernate.util.StringHelper;

/**
 * This class maps a type to names. Associations
 * may be marked with a capacity. Calling the get()
 * method with a type and actual size n will return
 * the associated name with smallest capacity >= n,
 * if available and an unmarked default type otherwise.
 * Eg, setting
 * <pre>
 *	names.put(type,        "TEXT" );
 *	names.put(type,   255, "VARCHAR($l)" );
 *	names.put(type, 65534, "LONGVARCHAR($l)" );
 * </pre>
 * will give you back the following:
 * <pre>
 *  names.get(type)         // --> "TEXT" (default)
 *  names.get(type,    100) // --> "VARCHAR(100)" (100 is in [0:255])
 *  names.get(type,   1000) // --> "LONGVARCHAR(1000)" (1000 is in [256:65534])
 *  names.get(type, 100000) // --> "TEXT" (default)
 * </pre>
 * On the other hand, simply putting
 * <pre>
 *	names.put(type, "VARCHAR($l)" );
 * </pre>
 * would result in
 * <pre>
 *  names.get(type)        // --> "VARCHAR($l)" (will cause trouble)
 *  names.get(type, 100)   // --> "VARCHAR(100)"
 *  names.get(type, 10000) // --> "VARCHAR(10000)"
 * </pre>
 *
 * @author Christoph Beck
 */
public class TypeNames {
	private String placeholder;
	private HashMap weighted = new HashMap();
	private HashMap defaults = new HashMap();
	
	/**
	 * Constructor.
	 * @param placeholder string to be replaced by actual size/length in type names
	 */
	public TypeNames(String placeholder) {
		super();
		
		this.placeholder = placeholder;
	}
	
	/**
	 * get default type name for specified type
	 * @param typecode the type key
	 * @return the default type name associated with specified key
	 */
	public String get(int typecode) throws MappingException {
		String result = (String) defaults.get( new Integer(typecode) );
		if (result==null) throw new MappingException("No Dialect mapping for JDBC type: " + typecode);
		return result;
	}
	
	/**
	 * get type name for specified type and size
	 * @param typecode the type key
	 * @param size the (maximum) type size/length
	 * @return the associated name with smallest capacity >= size,
	 * if available and the default type name otherwise
	 */
	public String get(int typecode, int size) throws MappingException {
		Map map = (Map) weighted.get( new Integer(typecode) );
		if (map != null && map.size() > 0) {
			// iterate entries ordered by capacity to find first fit
			Iterator entries = map.entrySet().iterator();
			while ( entries.hasNext() ) {
				Map.Entry entry = (Map.Entry)entries.next();
				if ( size <= ( (Integer) entry.getKey() ).intValue() ) {
					return StringHelper.replaceOnce(
						(String) entry.getValue(),
						placeholder,
						Integer.toString(size)
					);
				}
			}
		}
		return StringHelper.replaceOnce( get(typecode), placeholder, Integer.toString(size) );
	}
	
	/**
	 * set a type name for specified type key and capacity
	 * @param typecode the type key
	 */
	public void put(int typecode, int capacity, String value) {
		TreeMap map = (TreeMap)weighted.get( new Integer(typecode) );
		if (map == null) {// add new ordered map
			map = new TreeMap();
			weighted.put( new Integer(typecode), map );
		}
		map.put(new Integer(capacity), value);
	}
	
	/**
	 * set a default type name for specified type key
	 * @param typecode the type key
	 */
	public void put(int typecode, String value) {
		defaults.put( new Integer(typecode), value );
	}
}






