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
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;

import java.io.IOException;
import java.util.Map;

/**
 * Tests ClearCaseSourceControl
 */
public final class SSTestClearCaseSourceControl extends AbstractSourceControlTest {

  private static final Log log = LogFactory.getLog(SSTestClearCaseSourceControl.class);
  private static final int TEST_CLEARCASE_BUILD_ID = 19;
  private static final int TEST_CHANGE_LIST_ID = 1;

  private ClearCaseSourceControl clearCase = null;


  public SSTestClearCaseSourceControl(final String s) {
    super(s);
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSince() throws Exception {
    // check set up
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    assertEquals(0, cm.getPendingChangeLists(TEST_CLEARCASE_BUILD_ID).size());

    // run first time
    final int lastChangeListID = clearCase.getChangesSince(TEST_CHANGE_LIST_ID);
    assertTrue(lastChangeListID != TEST_CHANGE_LIST_ID);
    assertEquals(5, cm.getPendingChangeLists(TEST_CLEARCASE_BUILD_ID).size());

    // run second time
    final int newChangeListID = clearCase.getChangesSince(lastChangeListID);
    assertEquals(lastChangeListID, newChangeListID);
  }


  public void test_checkoutLatest() throws Exception {
    clearCase.cleanupLocalCopy();
    TestHelper.emptyCheckoutDir(agent);
    checkoutLatestAndAssert(); // first run
    checkoutLatestAndAssert(); // second run - nothing should happen
  }


  /**
   * Helper method
   */
  private void checkoutLatestAndAssert() throws BuildException, CommandStoppedException, IOException, AgentFailureException {
    clearCase.checkoutLatest();
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertExists(agent, "test_parabuild_vob/test/sourceline/alwaysvalid/src");
  }


  public void test_checkOutLatestBranch() throws Exception {
    //TODO: implement
  }


  public void test_syncToChangeListBranch() throws Exception {
    // TODO: implement
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceInBranch() throws Exception {
    //TODO: implement
  }


  public void test_checkOutLatestCatchesConfigUpdates() throws Exception {
    clearCase.checkoutLatest();
    final String oldRelativeBuildDir = TestHelper.assertCurrentBuildPathExists(clearCase, agent);

    // alter view config spec to use diferent path
    final SourceControlSetting viewSpec = cm.getSourceControlSetting(TEST_CLEARCASE_BUILD_ID, VersionControlSystem.CLEARCASE_VIEW_CONFIG_SPEC);
    viewSpec.setPropertyValue("element * CHECKEDOUT\nelement * /main/LATEST\nload \\test_parabuild_vob\\test\\second_sourceline");
    cm.saveObject(viewSpec);
    final SourceControlSetting buildDir = cm.getSourceControlSetting(TEST_CLEARCASE_BUILD_ID, VersionControlSystem.CLEARCASE_RELATIVE_BUILD_DIR);
    buildDir.setPropertyValue("test_parabuild_vob\\test\\second_sourceline");
    cm.saveObject(buildDir);

    // call sync
    clearCase.reloadConfiguration();
    clearCase.checkoutLatest();

    TestHelper.assertOldBuildPathGoneAndNewAppeared(clearCase, oldRelativeBuildDir, agent);
  }


  public void test_checkOutMultilineRepositoryPath() throws Exception {
    // do nothing - multiline depot paths are naturally handled by cc specs.
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source
   * line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessUnexistingSourceLine() throws Exception {
    // alter view config spec to use never existing path
    final SourceControlSetting setting = cm.getSourceControlSetting(TEST_CLEARCASE_BUILD_ID, VersionControlSystem.CLEARCASE_VIEW_CONFIG_SPEC);
    setting.setPropertyValue("element * CHECKEDOUT\nelement * /main/LATEST\nload \\test_parabuild_vob\\test_never_existed");
    cm.saveObject(setting);

    // reload config
    clearCase.reloadConfiguration();

    // checkout
    try {
      clearCase.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // ignore
    }
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source
   * line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessInavalidUser() throws Exception {
    // do nothing - ClearCase uses OS security.
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListInitsEmptyCheckoutDir() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);
//    clearCase.setLocalCopyInitialized(true);

    log.info("get latest");
    final int changeListID = clearCase.getChangesSince(ChangeList.UNSAVED_ID);
    log.info("get change list " + changeListID);
    final ChangeList newChangeList = cm.getChangeList(changeListID);
    log.debug("lastChangeList = " + newChangeList);

    // sync to latest
    clearCase.syncToChangeList(changeListID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeList() throws Exception {
    // we expect that there is change list in the test/config/dataset.xml
    // REVIEWME: simeshev@parabuilci.org -> instead add a change list to dataset -  when CC test env stabilized.
    final int lastChangeID = clearCase.getChangesSince(ChangeList.UNSAVED_ID);
    clearCase.syncToChangeList(lastChangeID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertDirIsNotEmpty(agent, clearCase.getRelativeBuildDir());
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListPicksUpConfigChanges() throws Exception {
    //TODO: implement
  }


  /**
   * Tests that getting change list from source line that doesn't
   * contain any changes but dirs
   *
   * @throws Exception
   */
  public void test_getChangesSinceDoesntFailOnBlankSourceline() throws Exception {
    final int changeListID = clearCase.getChangesSince(ChangeList.UNSAVED_ID); // old spec path
    final SourceControlSetting setting = cm.getSourceControlSetting(TEST_CLEARCASE_BUILD_ID, VersionControlSystem.CLEARCASE_VIEW_CONFIG_SPEC);
    setting.setPropertyValue("element * CHECKEDOUT\nelement * /main/LATEST\nload \\test_parabuild_vob\\test\\empty");
    cm.saveObject(setting);
    clearCase.reloadConfiguration();
    final int newChangeListID = clearCase.getChangesSince(changeListID);
    final ChangeList newChangeList = cm.getChangeList(newChangeListID);
// NOTE: simeshev@parabuildci.org ->
//    assertTrue(newChangeListID != changeListID);
//    assertTrue(newChangeList.getDescription().startsWith("Added always empty folder"));
  }


  /**
   * Tests can not access passworded source line with wrong
   * password
   */
  public void test_canNotAccessWithWrongPassword() throws Exception {
    // do nothing - no passwords in ClearCase
  }


  public void test_getUsersMap() throws Exception {
    final Map usersMap = clearCase.getUsersMap();
    assertNotNull(usersMap);
  }


  public void test_getRelativeBuildDir() {
    final String relativeBuildDir = clearCase.getRelativeBuildDir();
    assertNotNull("Relative build dir", relativeBuildDir);
    assertEquals("test_parabuild_vob\\test\\sourceline", relativeBuildDir);
  }


  public int getTestBuildID() {
    return TEST_CLEARCASE_BUILD_ID;
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceHandlesFirstRun() throws Exception {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    assertEquals(0, cm.getPendingChangeLists(TEST_CLEARCASE_BUILD_ID).size());

    final int changeListID = clearCase.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);

    assertEquals(5, cm.getPendingChangeLists(TEST_CLEARCASE_BUILD_ID).size());
  }


  /**
   * Tests label method.
   */
  public void test_label() throws BuildException, CommandStoppedException, AgentFailureException {
    clearCase.syncToChangeList(clearCase.getChangesSince(ChangeList.UNSAVED_ID));
    clearCase.label("test_label_" + Long.toString(System.currentTimeMillis()));
    // REVIEWME: simeshev@parabuilci.org -> add removing label
  }


  protected void setUp() throws Exception {
    super.setUp();
    // empty temp dir
    IoUtils.emptyDir(TestHelper.JAVA_TEMP_DIR);
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);
    //
    final BuildConfig buildConfig = cm.getBuildConfiguration(TEST_CLEARCASE_BUILD_ID);
    assertNotNull(buildConfig);
    this.clearCase = new ClearCaseSourceControl(buildConfig);
    this.clearCase.setAgentHost(agentHost);
    // set in super's steUp
    assertEquals(this.clearCase.initialNumberOfChangeLists(), SystemProperty.UNLIMITED_INITIAL_NUMBER_OF_CHANGELISTS);
  }


  protected void tearDown() throws Exception {
    super.tearDown();
    // test fix #516 - that we dont' leave temp files.
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceWithStorageLocation() throws Exception {
    // alter storage location
    final SourceControlSetting setting = new SourceControlSetting(TEST_CLEARCASE_BUILD_ID, VersionControlSystem.CLEARCASE_VIEW_STORAGE_LOCATION, "Views");
    cm.saveObject(setting);

    clearCase.reloadConfiguration();

    // test that we get changes
    final int lastChangeListID = clearCase.getChangesSince(TEST_CHANGE_LIST_ID);
    assertTrue(lastChangeListID != TEST_CHANGE_LIST_ID);
    final int newChangeListID = clearCase.getChangesSince(lastChangeListID);
    assertEquals(lastChangeListID, newChangeListID);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestClearCaseSourceControl.class,
            new String[]{
                    "test_getChangesSinceWithStorageLocation",
                    "test_getChangesSinceDoesntFailOnBlankSourceline",
                    "test_checkoutLatest",
                    "test_label",
                    "test_getChangesSince",
            });
  }
}
