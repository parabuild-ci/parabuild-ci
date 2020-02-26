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

import java.io.File;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.versioncontrol.mks.SSTestMKSSourceControl;

/**
 * Test for FileSystemSourceControl
 */
public class SSTestFileSystemSourceControl extends AbstractSourceControlTest {

  private static final Log log = LogFactory.getLog(SSTestMKSSourceControl.class);
  private static final int TEST_CHANGE_LIST_ID = 28;

  private FileSystemSourceControl fsvcs = null;


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSince() throws Exception {
    // test that we get changes
    final int lastChangeListID = fsvcs.getChangesSince(TEST_CHANGE_LIST_ID);
    assertTrue(lastChangeListID != TEST_CHANGE_LIST_ID);

    // temp dir is empty
    IoUtils.emptyDir(TestHelper.JAVA_TEMP_DIR);
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);

    // no new changes ?
    final int newChangeListID = fsvcs.getChangesSince(lastChangeListID);

    final ChangeList lastChangeList = cm.getChangeList(lastChangeListID);
//    if (log.isDebugEnabled()) log.debug("lastChangeList = " + lastChangeList);
    final ChangeList newChangeList = cm.getChangeList(newChangeListID);
//    if (log.isDebugEnabled()) log.debug("newChangeList = " + newChangeList);

    assertEquals(lastChangeListID, newChangeListID);

    // temp dir is empty
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);
  }


  public void test_checkoutLatest() throws Exception {
    // Not supported for FileSystemSourceControl
  }


  public void test_checkOutLatestBranch() throws Exception {
    // Not supported for FileSystemSourceControl
  }


  public void test_syncToChangeListBranch() throws Exception {
    // Not supported for FileSystemSourceControl
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceInBranch() throws Exception {
    // Not supported for FileSystemSourceControl
  }


  public void test_checkOutLatestCatchesConfigUpdates() throws Exception {
    // Not supported for FileSystemSourceControl
  }


  public void test_checkOutMultilineRepositoryPath() throws Exception {

    // alter path
    final String canonicalPath = new File(TestHelper.getTestDataDir(), "nunit_xml_logs").getCanonicalPath();
    final String url = "http://www.parabuildci.org";
    TestHelper.setSourceControlProperty(
            getTestBuildID(),
            SourceControlSetting.FILESYSTEM_VCS_PATH,
            canonicalPath + '\n' + url);

    // sync w/reload - make sure nothing happens
    fsvcs.reloadConfiguration();
    fsvcs.syncToChangeList(fsvcs.getChangesSince(-1));
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessUnexistingSourceLine() throws Exception {
    // Not supported for FileSystemSourceControl
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessInavalidUser() throws Exception {
    // Not supported for FileSystemSourceControl
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListInitsEmptyCheckoutDir() throws Exception {
    // Not supported for FileSystemSourceControl
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeList() throws Exception {
    // prepare
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);
    final int changeListID = fsvcs.getChangesSince(ChangeList.UNSAVED_ID);
    if (log.isDebugEnabled()) log.debug("changeListID: " + changeListID);
    // first call
    fsvcs.syncToChangeList(changeListID);

    // second call - nothing should happen
    fsvcs.syncToChangeList(changeListID);
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListPicksUpConfigChanges() throws Exception {
    // Not supported for FileSystemSourceControl
  }


  /**
   * Tests that getting change list from source line that doesn't
   * contain any changes but dirs
   *
   * @throws Exception
   */
  public void test_getChangesSinceDoesntFailOnBlankSourceline() throws Exception {
    // Not supported for FileSystemSourceControl
  }


  /**
   * Tests that getting change list from source line that doesn't
   * contain any changes but dirs
   *
   * @throws Exception
   */
  public void test_getChangesSinceFailsOnNonExistingPath() throws Exception {
    TestHelper.setSourceControlProperty(
            getTestBuildID(),
            SourceControlSetting.FILESYSTEM_VCS_PATH,
            "some_never_existed_path");

    // sync w/reload - make sure nothing happens
    fsvcs.reloadConfiguration();

    //
    try {
      fsvcs.getChangesSince(-1);
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
  }


  /**
   * Tests can not access passworded source line with wrong password
   */
  public void test_canNotAccessWithWrongPassword() throws Exception {
    // Not supported for FileSystemSourceControl
  }


  public void test_getUsersMap() throws Exception {
    assertEquals(0, fsvcs.getUsersMap().size());
  }


  public int getTestBuildID() {
    return 25;
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceHandlesFirstRun() throws Exception {
    final int changeListID = fsvcs.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);
  }


  /**
   * Tests label method.
   */
  public void test_label() throws BuildException, CommandStoppedException, AgentFailureException {
    // no exception thrown
    fsvcs.syncToChangeList(fsvcs.getChangesSince(-1));
    fsvcs.label("parabuild_test_" + System.currentTimeMillis());
    assertTrue(errorManager.errorCount() == 0);
  }


  public SSTestFileSystemSourceControl(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.errorManager.clearAllActiveErrors();

    // prepare test file directory
    TestHelper.setSourceControlProperty(getTestBuildID(), SourceControlSetting.FILESYSTEM_VCS_PATH, new File(TestHelper.getTestDataDir(), "nunit_xml_logs").getCanonicalPath());

    final BuildConfig buildConfig = cm.getBuildConfiguration(getTestBuildID());
    this.fsvcs = new FileSystemSourceControl(buildConfig);
    this.fsvcs.setAgentHost(agentHost);

    // set in super's steUp
    assertEquals(SystemProperty.UNLIMITED_INITIAL_NUMBER_OF_CHANGELISTS, this.fsvcs.initialNumberOfChangeLists());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestFileSystemSourceControl.class,
            new String[]{
                    "test_getChangesSince",
                    "test_label",
                    "test_checkoutLatest",
                    "test_syncToChangeList"
            });
  }
}
