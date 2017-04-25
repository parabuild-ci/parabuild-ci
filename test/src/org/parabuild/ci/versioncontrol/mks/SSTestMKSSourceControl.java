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
package org.parabuild.ci.versioncontrol.mks;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.versioncontrol.AbstractSourceControlTest;

/**
 */
public class SSTestMKSSourceControl extends AbstractSourceControlTest {

  private static final Log log = LogFactory.getLog(SSTestMKSSourceControl.class);
  private static final int TEST_CHANGE_LIST_ID = 27;
  private static final String TEST_NEWLY_CREATED_FILE = "sourceline/alwaysvalid/src/file_that_shouldnt_exist_when_syncing_to_the_first_change_list.txt";

  private MKSSourceControl mks = null;
  private static final String NEVER_EXISTED_USER = "never_existed_user";


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSince() throws Exception {
    // test that we get changes
    final int lastChangeListID = mks.getChangesSince(TEST_CHANGE_LIST_ID);
    assertTrue(lastChangeListID != TEST_CHANGE_LIST_ID);

    // temp dir is empty
    IoUtils.emptyDir(TestHelper.JAVA_TEMP_DIR);
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);

    // no new changes ?
    final int newChangeListID = mks.getChangesSince(lastChangeListID);

    final ChangeList lastChangeList = cm.getChangeList(lastChangeListID);
    if (log.isDebugEnabled()) log.debug("lastChangeList = " + lastChangeList);
    final ChangeList newChangeList = cm.getChangeList(newChangeListID);
    if (log.isDebugEnabled()) log.debug("newChangeList = " + newChangeList);

    assertEquals(lastChangeListID, newChangeListID);

    // temp dir is empty
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);
  }


  public void test_checkoutLatest() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    mks.checkoutLatest();
    mks.checkoutLatest(); // make sure nothing happens at second run
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertDirIsNotEmpty(agent, "test_project/sourceline/alwaysvalid/src");
    TestHelper.assertNotExists(agent, "test_project/second_sourceline/src");
  }


  public void test_checkOutLatestBranch() throws Exception {
// NOTE: simeshev@parabuildci.org ->
//    TestHelper.setSourceControlProperty(mks.getBuildID(), SourceControlSetting.MKS_DEVELOPMENT_PATH, "test_development_path");
//    mks.reloadConfiguration();
//    mks.checkoutLatest();
//    mks.checkoutLatest(); // make sure nothing happens at second run
//    TestHelper.assertCheckoutDirNotEmpty(getTestBuildID());
//    TestHelper.assertDirIsNotEmpty(agent, "test_project/sourceline/alwaysvalid/src");
//    TestHelper.assertNotExists(agent, "test_project/second_sourceline/src");
  }


  public void test_syncToChangeListBranch() throws Exception {
// NOTE: simeshev@parabuildci.org ->
//    // prepare
//    TestHelper.setSourceControlProperty(mks.getBuildID(), SourceControlSetting.MKS_DEVELOPMENT_PATH, "test_development_path");
//    mks.reloadConfiguration();
//    TestHelper.assertCheckoutDirExistsAndEmpty(getTestBuildID());
//
//    final int changeListID = mks.getChangesSince(ChangeList.UNSAVED_ID);
//    if (log.isDebugEnabled()) log.debug("changeListID: " + changeListID);
//
//    // first call
//    mks.syncToChangeList(changeListID);
//    TestHelper.assertCheckoutDirNotEmpty(getTestBuildID());
//    TestHelper.assertExists(agent, TEST_NEWLY_CREATED_FILE);
//
//    // second call - nothing should happen
//    mks.syncToChangeList(changeListID);
//
//    if (log.isDebugEnabled()) log.debug("======= ======= ======= ======= ======= ======= ======= ");
//    // third - sync to first one
//    mks.syncToChangeList(TEST_CHANGE_LIST_ID);
//    TestHelper.assertNotExists(agent, TEST_NEWLY_CREATED_FILE);
//    if (log.isDebugEnabled()) log.debug("======= ======= ======= ======= ======= ======= ======= ");
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceInBranch() throws Exception {
// NOTE: simeshev@parabuildci.org ->
//    TestHelper.setSourceControlProperty(mks.getBuildID(), SourceControlSetting.MKS_DEVELOPMENT_PATH, "test_development_path");
//    mks.reloadConfiguration();
//
//    // test that we get changes
//    final int lastChangeListID = mks.getChangesSince(TEST_CHANGE_LIST_ID);
//    assertTrue(lastChangeListID != TEST_CHANGE_LIST_ID);
//
//    // temp dir is empty
//    IoUtils.emptyDir(TestHelper.JAVA_TEMP_DIR);
//    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);
//
//    // no new changes ?
//    final int newChangeListID = mks.getChangesSince(lastChangeListID);
//
//    final ChangeList lastChangeList = cm.getChangeList(lastChangeListID);
//    if (log.isDebugEnabled()) log.debug("lastChangeList = " + lastChangeList);
//    final ChangeList newChangeList = cm.getChangeList(newChangeListID);
//    if (log.isDebugEnabled()) log.debug("newChangeList = " + newChangeList);
//
//    assertEquals(lastChangeListID, newChangeListID);
//
//    // temp dir is empty
//    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);
  }


  public void test_checkOutLatestCatchesConfigUpdates() throws Exception {
    // REVIEWME: simeshev@parabuilci.org -> implement
  }


  public void test_checkOutMultilineRepositoryPath() throws Exception {
    // no multiline support in MKS
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessUnexistingSourceLine() throws Exception {
    // alter
    TestHelper.setSourceControlProperty(mks.getBuildID(), SourceControlSetting.MKS_PROJECT, "/opt/mks/integrity_server2005/mksis/projects/never_existed_project.pj");
    mks.reloadConfiguration();

    // test
    try {
      mks.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
      System.out.println("DEBUG: e: " + e);
    }
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessInavalidUser() throws Exception {
    // alter
    TestHelper.setSourceControlProperty(mks.getBuildID(), SourceControlSetting.MKS_USER, NEVER_EXISTED_USER);
    mks.reloadConfiguration();

    // test
    try {
      mks.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
      System.out.println("DEBUG: e: " + e);
    }
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListInitsEmptyCheckoutDir() throws Exception {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeList() throws Exception {
    // prepare
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);
    final int changeListID = mks.getChangesSince(ChangeList.UNSAVED_ID);
    if (log.isDebugEnabled()) log.debug("changeListID: " + changeListID);
    // first call
    mks.syncToChangeList(changeListID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertExists(agent, TEST_NEWLY_CREATED_FILE);

    // second call - nothing should happen
    mks.syncToChangeList(changeListID);

    if (log.isDebugEnabled()) log.debug("======= ======= ======= ======= ======= ======= ======= ");
    // third - sync to first one
    mks.syncToChangeList(TEST_CHANGE_LIST_ID);
    TestHelper.assertNotExists(agent, TEST_NEWLY_CREATED_FILE);
    if (log.isDebugEnabled()) log.debug("======= ======= ======= ======= ======= ======= ======= ");
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListPicksUpConfigChanges() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);

    final int changeListID = mks.getChangesSince(ChangeList.UNSAVED_ID);
    final ChangeList newChangeList = cm.getChangeList(changeListID);
    log.debug("lastChangeList = " + newChangeList);

    // sync to latest
    mks.syncToChangeList(changeListID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  /**
   * Tests that getting change list from source line that doesn't
   * contain any changes but dirs
   *
   * @throws Exception
   */
  public void test_getChangesSinceDoesntFailOnBlankSourceline() throws Exception {
    // REVIEWME: simeshev@parabuilci.org -> implement
  }


  /**
   * Tests can not access passworded source line with wrong password
   */
  public void test_canNotAccessWithWrongPassword() throws Exception {
// REVIEWME: simeshev@parabuilci.org -> doesn't break, byabe because it picks up user/password from local snabox definition
//    // alter
//    TestHelper.setSourceControlProperty(mks.getBuildID(), SourceControlSetting.MKS_PASSWORD, org.parabuild.ci.security.SecurityManager.encryptPassword("wrong_passord"));
//    mks.reloadConfiguration();
//
//    // test
//    try {
//      mks.getChangesSince(TEST_CHANGE_LIST_ID);
//      TestHelper.failNoExceptionThrown();
//    } catch (Exception e) {
//    }
  }


  public void test_getUsersMap() throws Exception {
    // User maps are not supported under MKS
  }


  public int getTestBuildID() {
    return TestHelper.TEST_VALID_MKS_BUILD_ID;  //To change body of implemented methods use File | Settings | File Templates.
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceHandlesFirstRun() throws Exception {
    final int changeListID = mks.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);
  }


  /**
   * Tests label method.
   */
  public void test_label() throws BuildException, CommandStoppedException, AgentFailureException {
    // no exception thrown
    mks.syncToChangeList(mks.getChangesSince(-1));
    mks.label("parabuild_test_" + System.currentTimeMillis());
    assertTrue(errorManager.errorCount() == 0);
  }


  /**
   * Tests removeLabels method.
   */
  public void test_removeLabels() throws BuildException, CommandStoppedException, AgentFailureException {
    // no exception thrown
    mks.syncToChangeList(mks.getChangesSince(-1));
    final String label = "parabuild_test_" + System.currentTimeMillis();
    mks.label(label);
    mks.removeLabels(new String[]{label});
    try {
      mks.removeLabels(new String[]{label}); // should blow up
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
    assertTrue(errorManager.errorCount() == 0);
  }


  public SSTestMKSSourceControl(final String s) {
    super(s);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.errorManager.clearAllActiveErrors();
    final BuildConfig buildConfig = cm.getBuildConfiguration(getTestBuildID());
    this.mks = new MKSSourceControl(buildConfig);
    this.mks.setAgentHost(agentHost);

    // set in super's setUp
    assertEquals(this.mks.initialNumberOfChangeLists(), SystemProperty.UNLIMITED_INITIAL_NUMBER_OF_CHANGELISTS);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestMKSSourceControl.class,
            new String[]{
                    "test_label",
                    "test_checkoutLatest",
                    "test_getChangesSince",
                    "test_syncToChangeList"
            });
  }
}
