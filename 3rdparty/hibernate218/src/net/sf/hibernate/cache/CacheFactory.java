//$Id: CacheFactory.java,v 1.6 2004/06/04 05:43:44 steveebersole Exp $
package net.sf.hibernate.cache;

import net.sf.hibernate.MappingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

/**
 * @author Gavin King
 */
public final class CacheFactory {
	
	private static final Log log = LogFactory.getLog(CacheFactory.class);
	
	private CacheFactory() {}
	
	public static final String READ_ONLY = "read-only";
	public static final String READ_WRITE = "read-write";
	public static final String NONSTRICT_READ_WRITE = "nonstrict-read-write";
	public static final String TRANSACTIONAL = "transactional";

	public static CacheConcurrencyStrategy createCache(Element node, String name, boolean mutable) 
	throws MappingException {
		return createCache( node.attributeValue("usage"), name, mutable );
	}
	
	private static CacheConcurrencyStrategy createCache(String usage, String name, boolean mutable) 
	throws MappingException {
		
		if ( log.isDebugEnabled() ) log.debug("cache for: " + name + " usage strategy: " + usage);
		
		final CacheConcurrencyStrategy ccs;
		if ( usage.equals(READ_ONLY) ) {
			if (mutable) log.warn( "read-only cache configured for mutable: " + name );
			ccs = new ReadOnlyCache();
		}
		else if ( usage.equals(READ_WRITE) ) {
			ccs = new ReadWriteCache();
		}
		else if ( usage.equals(NONSTRICT_READ_WRITE) ) {
			ccs = new NonstrictReadWriteCache();
		}
		else if ( usage.equals(TRANSACTIONAL) ) {
			ccs = new TransactionalCache();
		}
		else {
			throw new MappingException("cache usage attribute should be read-write, read-only, nonstrict-read-write or transactional");
		}
		return ccs;
	}
		
}
