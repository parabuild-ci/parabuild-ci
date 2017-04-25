//$Id: CacheException.java,v 1.6 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate.cache;

import net.sf.hibernate.HibernateException;

/**
 * Something went wrong in the cache
 */
public class CacheException extends HibernateException {
	
	public CacheException(String s) {
		super(s);
	}
	
	public CacheException(Exception e) {
		super(e);
	}
	
}






