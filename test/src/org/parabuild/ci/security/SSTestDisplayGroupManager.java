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
package org.parabuild.ci.security;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.DisplayGroupManager;
import org.parabuild.ci.object.DisplayGroup;
import org.parabuild.ci.services.ServiceManager;

import java.util.List;

/**
 *
 */
public final class SSTestDisplayGroupManager extends ServersideTestCase {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SSTestDisplayGroupManager.class); // NOPMD

  private static final int TEST_BUILD_ID = 1;
  private static final int TEST_DISPLAY_GROUP_ID = 0;
  private static final String TEST_DISPLAY_GROUP_NAME = "Test display group 1";

  private DisplayGroupManager displayGroupManager = null;


  public void test_getDisplayGroupBuildVOList() {
    final List displayGroupBuildVOList = displayGroupManager.getDisplayGroupBuildVOList(1);
    assertEquals(20, displayGroupBuildVOList.size());
  }


  public void test_bug924_getDisplayGroupBuildVOListDoesNotListDeleted() {
    ConfigurationManager.getInstance().markActiveBuildDeleted(2);
    final List displayGroupBuildVOList = displayGroupManager.getDisplayGroupBuildVOList(1);
    assertEquals(19, displayGroupBuildVOList.size());
  }


  public void test_getAllDisplayGroups() {
    final List displayGroups = displayGroupManager.getAllDisplayGroups();
    assertEquals(2, displayGroups.size());
  }


  public void test_getDisplayGroup() {
    final DisplayGroup displayGroup = displayGroupManager.getDisplayGroup(TEST_DISPLAY_GROUP_ID);
    assertNotNull(displayGroup);
  }


  public void test_getDisplayGroupByName() {
    final DisplayGroup displayGroup = displayGroupManager.getDisplayGroupByName(TEST_DISPLAY_GROUP_NAME);
    assertNotNull(displayGroup);
  }


  public void test_deleteGroup() {
    displayGroupManager.deleteGroup(displayGroupManager.getDisplayGroup(TEST_DISPLAY_GROUP_ID));
    assertNull(displayGroupManager.getDisplayGroup(TEST_DISPLAY_GROUP_ID));
  }


  public void test_deleteBuildFromDisplayGroup() {
    assertNotNull(displayGroupManager.getDisplayGroupBuild(TEST_BUILD_ID, TEST_DISPLAY_GROUP_ID));
    displayGroupManager.deleteBuildFromDisplayGroup(TEST_BUILD_ID, TEST_DISPLAY_GROUP_ID);
    assertNull(displayGroupManager.getDisplayGroupBuild(TEST_BUILD_ID, TEST_DISPLAY_GROUP_ID));
  }


  public void test_filterBuildStatuses() {
    final List currentBuildsStatuses = ServiceManager.getInstance().getBuildListService().getCurrentBuildStatuses();
    final List filteredStatusList = displayGroupManager.filterBuildStatuses(currentBuildsStatuses, TEST_DISPLAY_GROUP_ID, true);
    assertEquals(2, filteredStatusList.size());
    assertTrue(filteredStatusList.get(0) instanceof BuildState);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestDisplayGroupManager.class, new String[]{
    });
  }


  public SSTestDisplayGroupManager(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    // call ServerSideTest setup that initializes db data
    super.setUp();
    displayGroupManager = DisplayGroupManager.getInstance();
  }
}
