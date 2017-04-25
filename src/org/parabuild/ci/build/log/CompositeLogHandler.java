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
package org.parabuild.ci.build.log;

import java.util.*;

/**
 * This composite calls process() on each handler in the list of
 * handlers.
 *
 * @see LogHandler#process
 */
public final class CompositeLogHandler implements LogHandler {

  private final List handlers = new ArrayList(5); // holds logs to iterate


  /**
   * Finds logs, moves them to archive and adjusts database.
   */
  public void process() {
    for (final Iterator iter = handlers.iterator(); iter.hasNext();) {
      final LogHandler handler = (LogHandler)iter.next();
      handler.process();
    }
  }


  /**
   * Adds a handler to an internal list of handlers.
   *
   * @param handler to add.
   */
  public void addHandler(final LogHandler handler) {
    handlers.add(handler);
  }


  /**
   * Returns unmodifiable copy of handlers.
   */
  public List getHandlers() {
    return Collections.unmodifiableList(handlers);
  }


  public String toString() {
    return "CompositeLogHandler{" +
      "handlers=" + handlers +
      '}';
  }
}
