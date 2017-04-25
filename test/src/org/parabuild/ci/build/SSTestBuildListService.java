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

import junit.framework.TestSuite;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.services.BuildListService;
import org.parabuild.ci.services.BuildService;
import org.parabuild.ci.services.Service;
import org.parabuild.ci.services.ServiceManager;

import java.util.Collection;
import java.util.Iterator;

/**
 */
public class SSTestBuildListService extends ServersideTestCase {

  private ServiceManager serviceManager = null;
  private BuildListService buildListService = null;


  public SSTestBuildListService(final String s) {
    super(s);
  }


  public void test_getBuilds() {
    final Collection builds = buildListService.getBuilds();
    assertNotNull(builds);
    assertTrue(!builds.isEmpty());
    for (Iterator iter = builds.iterator(); iter.hasNext();) {
      final BuildService buildService = (BuildService) iter.next();
      assertNotNull(buildService.getBuildState());
    }
  }


  public void test_getParallelBuildState() {
    final BuildService buildService = buildListService.getBuild(TestHelper.TEST_DEPENDENT_PARALLEL_BUILD_ID_1);
    final BuildState buildState = buildService.getBuildState();
    assertTrue(buildState.isParallel());
  }


  public void test_getCurrentBuildsStatusesReturnsSortedList() {
    final Collection builds = buildListService.getCurrentBuildStatuses();
    assertNotNull(builds);
    assertTrue(!builds.isEmpty());
    String currentBuildName = null;
    String previousBuildName = null;
    for (Iterator iter = builds.iterator(); iter.hasNext();) {
      final BuildState state = (BuildState) iter.next();
      currentBuildName = state.getBuildName();
      if (previousBuildName != null) {
        assertTrue(currentBuildName.compareToIgnoreCase(previousBuildName) > 0);
      }
      previousBuildName = currentBuildName;
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestBuildListService.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    serviceManager = ServiceManager.getInstance();
    assertNotNull(serviceManager);
    buildListService = serviceManager.getBuildListService();
    assertNotNull(buildListService);
    assertEquals(buildListService.getServiceStatus(), Service.SERVICE_STATUS_STARTED);
  }
}
