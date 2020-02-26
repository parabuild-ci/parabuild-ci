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
package org.parabuild.ci.versioncontrol;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.versioncontrol.mks.SSTestMKSSourceControl;

import java.io.File;

/**
 * Test for GenericSourceControl
 */
public class SSTestGenericSourceControl extends AbstractSourceControlTest {

  private static final Log log = LogFactory.getLog(SSTestMKSSourceControl.class);
  private static final int TEST_CHANGE_LIST_ID = 29;

  private GenericSourceControl gvcs = null;


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSince() throws Exception {
    // test that we get changes
    final int lastChangeListID = gvcs.getChangesSince(SSTestGenericSourceControl.TEST_CHANGE_LIST_ID);
    assertTrue(lastChangeListID != SSTestGenericSourceControl.TEST_CHANGE_LIST_ID);

    // temp dir is empty
    IoUtils.emptyDir(TestHelper.JAVA_TEMP_DIR);
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);

    // no new changes ?
    // alter the command to show nothing.
    TestHelper.setSourceControlProperty(getTestBuildID(), VersionControlSystem.GENERIC_VCS_GET_CHANGES_COMMAND, "echo");
    gvcs.reloadConfiguration();
    final int newChangeListID = gvcs.getChangesSince(lastChangeListID);

    final ChangeList lastChangeList = cm.getChangeList(lastChangeListID);
//    if (log.isDebugEnabled()) log.debug("lastChangeList = " + lastChangeList);
    final ChangeList newChangeList = cm.getChangeList(newChangeListID);
//    if (log.isDebugEnabled()) log.debug("newChangeList = " + newChangeList);

    assertEquals(lastChangeListID, newChangeListID);

    // temp dir is empty
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);
  }


  public void test_checkoutLatest() throws Exception {
    // Not supported for GenericSourceControl
  }


  public void test_checkOutLatestBranch() throws Exception {
    // Not supported for GenericSourceControl
  }


  public void test_syncToChangeListBranch() throws Exception {
    // Not supported for GenericSourceControl
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceInBranch() throws Exception {
    // Not supported for GenericSourceControl
  }


  public void test_checkOutLatestCatchesConfigUpdates() throws Exception {
    // Not supported for GenericSourceControl
  }


  public void test_checkOutMultilineRepositoryPath() throws Exception {
    // Not supported for GenericSourceControl
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessUnexistingSourceLine() throws Exception {
    // Not supported for GenericSourceControl
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessInavalidUser() throws Exception {
    // Not supported for GenericSourceControl
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListInitsEmptyCheckoutDir() throws Exception {
    // Not supported for GenericSourceControl
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeList() throws Exception {
    // prepare
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);
    final int changeListID = gvcs.getChangesSince(ChangeList.UNSAVED_ID);
    if (SSTestGenericSourceControl.log.isDebugEnabled()) {
      SSTestGenericSourceControl.log.debug("changeListID: " + changeListID);
    }
    // first call
    gvcs.syncToChangeList(changeListID);

    // second call - nothing should happen
    gvcs.syncToChangeList(changeListID);
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListPicksUpConfigChanges() throws Exception {
    // Not supported for GenericSourceControl
  }


  /**
   * Tests that getting change list from source line that doesn't
   * contain any changes but dirs
   *
   * @throws Exception
   */
  public void test_getChangesSinceDoesntFailOnBlankSourceline() throws Exception {
    // Not supported for GenericSourceControl
  }


  /**
   * Tests that getting change list from source line that doesn't
   * contain any changes but dirs
   *
   * @throws Exception
   */
  public void test_getChangesSinceFailsOnNonExistingPath() throws Exception {
    // Not supported for GenericSourceControl
  }


  /**
   * Tests can not access passworded source line with wrong password
   */
  public void test_canNotAccessWithWrongPassword() throws Exception {
    // Not supported for GenericSourceControl
  }


  public void test_getUsersMap() throws Exception {
    assertEquals(0, gvcs.getUsersMap().size());
  }


  public int getTestBuildID() {
    return 26;
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceHandlesFirstRun() throws Exception {
    final int changeListID = gvcs.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);
  }


  /**
   * Tests label method.
   */
  public void test_label() throws BuildException, CommandStoppedException, AgentFailureException {
    // no exception thrown
    gvcs.syncToChangeList(gvcs.getChangesSince(-1));
    gvcs.label("parabuild_test_" + System.currentTimeMillis());
    assertTrue(errorManager.errorCount() == 0);
  }


  public SSTestGenericSourceControl(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.errorManager.clearAllActiveErrors();

    // prepare test file directory
    TestHelper.setSourceControlProperty(getTestBuildID(), VersionControlSystem.FILESYSTEM_VCS_PATH, new File(TestHelper.getTestDataDir(), "nunit_xml_logs").getCanonicalPath());

    final BuildConfig buildConfig = cm.getBuildConfiguration(getTestBuildID());
    this.gvcs = new GenericSourceControl(buildConfig);
    this.gvcs.setAgentHost(agentHost);

    // set in super's setUp
    assertEquals(SystemProperty.UNLIMITED_INITIAL_NUMBER_OF_CHANGELISTS, this.gvcs.initialNumberOfChangeLists());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestGenericSourceControl.class,
            new String[]{
                    "test_getChangesSinceHandlesFirstRun",
                    "test_getChangesSince",
                    "test_label",
                    "test_checkoutLatest",
                    "test_syncToChangeList"
            });
  }
}
