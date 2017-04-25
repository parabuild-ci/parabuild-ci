//$Id: JCSCache.java,v 1.7 2004/06/04 01:27:36 steveebersole Exp $
package net.sf.hibernate.cache;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.apache.jcs.JCS;

/**
 * Support for Apache Turbine's JCS
 * @author Gavin King
 * @deprecated JCS support will be removed in version 2.1.1
 */
public class JCSCache implements Cache {
	
	private static final Log log = LogFactory.getLog(JCSCache.class);
	
	private JCS region;
	
	public JCSCache(String regionName, java.util.Properties properties) throws CacheException {
		
		try {
			region = JCS.getInstance(regionName);
		}
		catch (org.apache.jcs.access.exception.CacheException e) {
			log.error("could not create JCS region", e);
			throw new CacheException(e);
		}
	}
	
	public Object get(Object key) {
		return region.get(key);
	}
	
	public void put(Object key, Object value) throws CacheException {
		try {
			region.put(key, value);
		}
		catch (org.apache.jcs.access.exception.CacheException e) {
			log.error("could not add to JCS region", e);
			throw new CacheException(e);
		}
	}
	
	public void remove(Object key) throws CacheException {
		try {
			region.remove(key);
		}
		catch (org.apache.jcs.access.exception.CacheException e) {
			log.error("could not remove from JCS region", e);
			throw new CacheException(e);
		}
	}

	public void clear() throws CacheException {
		try {
			region.remove();
		}
		catch (org.apache.jcs.access.exception.CacheException e) {
			log.error("could not remove from JCS region", e);
			throw new CacheException(e);
		}
	}

	public void destroy() throws CacheException {
		try {
			region.destroy();
		}
		catch (org.apache.jcs.access.exception.CacheException e) {
			log.error("could not destroy JCS region", e);
			throw new CacheException(e);
		}
	}

	public long nextTimestamp() {
		return Timestamper.next();
	}

	public void lock(Object key) throws CacheException {
		//local cache, so we use synchronization
	}

	public void unlock(Object key) throws CacheException {
		//local cache, so we use synchronization
	}

	public int getTimeout() {
		return Timestamper.ONE_MS * 60000; //ie. 60 seconds
	}

}






