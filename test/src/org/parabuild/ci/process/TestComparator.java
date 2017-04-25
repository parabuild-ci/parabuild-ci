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

import java.util.Comparator;

/**
 * Implementation of Comparator interface to compare
 * <code>Process</code> objects.
 *
 */
public class TestComparator implements Comparator {

  private final boolean order;
  
  public TestComparator(final boolean order) {
    this.order = order;
  }
  
  public int compare(final Object o1, final Object o2) {
    final OSProcess p1 = (OSProcess) o1;
    final OSProcess p2 = (OSProcess) o2;
    final String cmd1 = p1.getCommandLine();
    final String cmd2 = p2.getCommandLine();
    if (cmd1.indexOf(SSTestProcessManagerFactory.SLEEP_SIGNATURE) >= 0 &&
        cmd2.indexOf(SSTestProcessManagerFactory.SLEEP_SIGNATURE) >= 0 )
        return 0;
    if (cmd1.indexOf(SSTestProcessManagerFactory.TEST_PRCMANAGER_SH) >= 0 &&
        cmd2.indexOf(SSTestProcessManagerFactory.TEST_PRCMANAGER_SH) >= 0 )
        return 0;
    if (cmd1.indexOf(SSTestProcessManagerFactory.PRCMANAGER_SPAWN_SH) >= 0 &&
        cmd2.indexOf(SSTestProcessManagerFactory.PRCMANAGER_SPAWN_SH) >= 0 )
        return 0;
    if (cmd1.indexOf(SSTestProcessManagerFactory.TEST_PRCMANAGER_SH) >= 0 )
        return order ? 1 : -1;
    if (cmd2.indexOf(SSTestProcessManagerFactory.TEST_PRCMANAGER_SH)>= 0 )
        return order ? -1 : 1;
    if (cmd1.indexOf(SSTestProcessManagerFactory.PRCMANAGER_SPAWN_SH)>= 0 )
        return order ? 1 : -1;
    return order ? -1 : 1;
  }  
}
