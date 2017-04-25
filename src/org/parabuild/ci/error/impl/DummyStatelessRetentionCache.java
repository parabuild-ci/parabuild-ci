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

/**
 * Dummy cache
 */
public final class DummyStatelessRetentionCache implements RetentionCache {

  /**
   * Returns all elements.
   */
  public void removeAll() throws IllegalStateException, IOException {
    // do noting
  }


  public Element get(final Serializable key) throws IllegalStateException, CacheException {
    return null;  // never find anything
  }


  public void put(final Element elem) throws IllegalArgumentException, IllegalStateException {
    // do noting
  }
}
