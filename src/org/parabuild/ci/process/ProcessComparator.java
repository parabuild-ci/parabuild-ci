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

import java.io.Serializable;
import java.util.*;

/**
 * Implementation of Comparator interface to compare
 * <code>Process</code> objects.
 *
 */
public final class ProcessComparator implements Comparator, Serializable {

  private static final long serialVersionUID = -380334459107228390L;

  private final int order;
  
  public ProcessComparator(final byte order) {
    this.order = order;
  }
  
  public int compare(final Object o1, final Object o2) {
    final OSProcess p1 = (OSProcess) o1;
    final OSProcess p2 = (OSProcess) o2;
    switch (order) {
      case ProcessManager.SORT_BY_PID:
        return p1.getPID() - p2.getPID();
      case ProcessManager.SORT_BY_PPID:
        return p1.getPPID() - p2.getPPID();
      default:
        final String name1 = p1.getName();
        final String name2 = p2.getName();
        if (name1 == null && name2 == null)
          return 0;
        if (name1 == null)
          return -1;
        if (name2 == null)
          return 1;
        return name1.compareTo(name2);
    }
  }


  public String toString() {
    return "ProcessComparator{" +
      "order=" + order +
      '}';
  }
}
