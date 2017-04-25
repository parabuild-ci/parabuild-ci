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
package org.parabuild.ci.build.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * This composite calls process() on each handler in the list of
 * handlers.
 *
 * @see ResultHandler#process
 */
public final class CompositeResultHandler implements ResultHandler {

  private final List handlers = new ArrayList(5); // holds result handlers to iterate


  /**
   * Finds logs, moves them to archive and adjusts database.
   */
  public void process() {
    for (final Iterator iter = handlers.iterator(); iter.hasNext(); ) {
      final ResultHandler handler = (ResultHandler) iter.next();
      handler.process();
    }
  }


  public void setPinResult(final boolean pinResult) {
    for (final Iterator iter = handlers.iterator(); iter.hasNext(); ) {
      final ResultHandler handler = (ResultHandler) iter.next();
      handler.setPinResult(pinResult);
    }
  }


  /**
   * Adds a handler to an internal list of handlers.
   *
   * @param handler to add.
   */
  public void addHandler(final ResultHandler handler) {
    handlers.add(handler);
  }


  /**
   * Returns unmodifiable copy of handlers.
   */
  public List getHandlers() {
    return Collections.unmodifiableList(handlers);
  }


  public String toString() {
    return "CompositeResultHandler{" +
            "handlers=" + handlers +
            '}';
  }
}
