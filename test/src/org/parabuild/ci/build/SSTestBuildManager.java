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
package org.parabuild.ci.build;

import java.util.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.services.*;

/**
 */
public class SSTestBuildManager extends ServersideTestCase {

  private BuildManager buildManager;


  public SSTestBuildManager(final String s) {
    super(s);
  }


  public void testgetCurrentBuildsStatuses() {
    final Collection statuses = buildManager.getCurrentBuildsStatuses();
    assertTrue("Size of the build statuses list should be bigger than zero",
      !statuses.isEmpty());
    for (Iterator iter = statuses.iterator(); iter.hasNext();) {
      final BuildState buildState = (BuildState)iter.next();
      assertNotNull(buildState);
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestBuildManager.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    buildManager = BuildManager.getInstance();
    assertNotNull(buildManager);
  }
}
