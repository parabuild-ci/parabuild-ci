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
 * Cache interface to use with ErrorManagerImpl.
 */
public interface RetentionCache {

  /**
   * Returns all elements.
   */
  void removeAll() throws IllegalStateException, IOException;


  /**
   * Gets element for the key.
   */
  Element get(Serializable key) throws IllegalStateException, CacheException;


  /**
   * Puts an element to cache.
   */
  void put(Element elem) throws IllegalArgumentException, IllegalStateException;
}
