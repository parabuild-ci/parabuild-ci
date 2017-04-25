/*
 * Parabuild CI licenses this file to You under the LGPL 2.1
 * (the "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      https://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.parabuild.ci.common;

import java.io.*;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;

/**
 */
public final class CacheUtils {

  public static void removeHard(final String cacheName) {
    try {
      CacheManager.getInstance().removeCache(cacheName);
    } catch (Exception e) {
      IoUtils.ignoreExpectedException(e);
    }
  }


  public static Cache createCache(final CacheManager cacheManager, final Cache cache) throws CacheException {
    cacheManager.addCache(cache);
    return cacheManager.getCache(cache.getName());
  }


  private CacheUtils() {
  }


  public static void resetAllCaches() throws CacheException, IOException {
    final CacheManager cacheManager = CacheManager.getInstance();
    final String[] cacheNames = cacheManager.getCacheNames();
    for (int i = 0; i < cacheNames.length; i++) {
      final String cacheName = cacheNames[i];
      if (cacheName.equals("retention_cache")) continue;
      //if (log.isDebugEnabled()) log.debug("cache hits before setup: " + cacheName + "/" + cache.getHitCount());
      cacheManager.getCache(cacheName).removeAll();
    }
  }
}
