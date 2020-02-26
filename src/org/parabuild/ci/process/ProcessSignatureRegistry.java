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
package org.parabuild.ci.process;

import java.util.*;

import org.parabuild.ci.util.*;

/**
 * This class is a registry for process signatures.
 */
public final class ProcessSignatureRegistry {

  private final Map signatureMap = new HashMap(11);


  /**
   * Registers signature
   */
  public synchronized void register(final String signature) {
    if (StringUtils.isBlank(signature)) return;
    signatureMap.put(signature, Boolean.TRUE);
  }


  /**
   * Registers list of signatures.
   */
  public synchronized void register(final List signatures) {
    for (final Iterator i = signatures.iterator(); i.hasNext();) {
      register((String)i.next());
    }
  }


  /**
   * Unregisters signature
   */
  public synchronized void unregister(final String signature) {
    if (StringUtils.isBlank(signature)) return;
    signatureMap.remove(signature);
  }


  /**
   * Returs String List of signatures.
   */
  public synchronized List signtatures() {
    return new ArrayList(signatureMap.keySet());
  }


  /**
   * Unregisters signatures
   */
  public synchronized void unregister(final List signatures) {
    for (final Iterator i = signatures.iterator(); i.hasNext();) {
      unregister((String)i.next());
    }
  }


  public String toString() {
    return "ProcessSignatureRegistry{" +
      "signatureMap=" + signatureMap +
      '}';
  }
}
