//$Id: TreeCacheProvider.java,v 1.7 2004/12/02 00:45:01 steveebersole Exp $
package net.sf.hibernate.cache;

import org.jboss.cache.PropertyConfigurator;
import net.sf.hibernate.transaction.TransactionManagerLookup;
import net.sf.hibernate.transaction.TransactionManagerLookupFactory;

import javax.transaction.TransactionManager;
import java.util.Properties;

/**
 * Support for JBoss TreeCache
 * @author Gavin King
 */
public class TreeCacheProvider implements CacheProvider {

	private org.jboss.cache.TreeCache cache;

	/**
	 * Construct and configure the Cache representation of a named cache region.
	 *
	 * @param regionName the name of the cache region
	 * @param properties configuration settings
	 * @return The Cache representation of the named cache region.
	 * @throws CacheException Indicates an error building the cache region.
	 */
	public Cache buildCache(String regionName, Properties properties) throws CacheException {
		return new TreeCache(cache, regionName);
	}

	public long nextTimestamp() {
		return System.currentTimeMillis() / 100;
	}

	/**
	 * Prepare the underlying JBossCache TreeCache instance.
	 *
	 * @param properties All current config settings.
	 *
	 * @throws CacheException Indicates a problem preparing cache for use.
	 */
	public void start(Properties properties) throws CacheException {
		try {
			cache = new org.jboss.cache.TreeCache();
			PropertyConfigurator config = new PropertyConfigurator();
			config.configure(cache, "treecache.xml");
			TransactionManagerLookup tml = TransactionManagerLookupFactory.getTransactionManagerLookup(properties);
			if (tml!=null) {
				cache.setTransactionManagerLookup( new TransactionManagerLookupAdaptor(tml, properties) );
			}
			cache.start();
		}
		catch (Exception e) {
			throw new CacheException(e);
		}
	}

	public void stop() {
		if (cache!=null) {
			cache.stop();
			cache.destroy();
			cache=null;
		}
	}

	static final class TransactionManagerLookupAdaptor implements org.jboss.cache.TransactionManagerLookup {
		private final TransactionManagerLookup tml;
		private final Properties props;
		TransactionManagerLookupAdaptor(TransactionManagerLookup tml, Properties props) {
			this.tml=tml;
			this.props=props;
		}
		public TransactionManager getTransactionManager() throws Exception {
			return tml.getTransactionManager(props);
		}
	}

}
