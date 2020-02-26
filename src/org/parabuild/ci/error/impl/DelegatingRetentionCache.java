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
package org.parabuild.ci.error.impl;

import java.io.*;

import net.sf.ehcache.*;
import org.parabuild.ci.util.*;


/**
 * Delegates cache handling to net.sf.ehcache.Cache
 *
 * @see RetentionCache
 * @see DummyStatelessRetentionCache
 */
public final class DelegatingRetentionCache implements RetentionCache {

  private final Cache delegate;


  public DelegatingRetentionCache(final Cache cache) {
    ArgumentValidator.validateArgumentNotNull(cache, "cache");
    this.delegate = cache;
  }


  /**
   * Returns all elements.
   */
  public void removeAll() throws IllegalStateException, IOException {
    delegate.removeAll();
  }


  public Element get(final Serializable key) throws IllegalStateException, CacheException {
    return delegate.get(key);
  }


  public void put(final Element elem) throws IllegalArgumentException, IllegalStateException {
    delegate.put(elem);
  }


  public String toString() {
    return "DelegatingRetentionCache{" +
      "delegate=" + delegate +
      '}';
  }
}
