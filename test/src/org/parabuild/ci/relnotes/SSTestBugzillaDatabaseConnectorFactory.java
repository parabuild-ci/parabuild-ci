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
package org.parabuild.ci.relnotes;

import org.apache.cactus.*;
import org.apache.commons.logging.*;

import junit.framework.*;

/**
 * Tests BugzillaDatabaseConnectorFactory on the server side.
 */
public final class SSTestBugzillaDatabaseConnectorFactory extends ServletTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestBugzillaDatabaseConnectorFactory.class); // NOPMD


  public SSTestBugzillaDatabaseConnectorFactory(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_makeInstance() throws Exception {
    final BugzillaDatabaseConnector bugzillaDatabaseConnector = BugzillaDatabaseConnectorFactory.makeInstance("localhost", 3307, "test_bugs", "test_bugs", "test_bugs");
    assertTrue(bugzillaDatabaseConnector instanceof Bugzilla216DatabaseConnector);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestBugzillaDatabaseConnectorFactory.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
  }
}
