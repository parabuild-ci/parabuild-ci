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

import java.util.Iterator;
import java.util.List;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SystemProperty;

/**
 * Tests PVCSSourceControl
 */
public class SSTestPVCSSourceControl extends AbstractSourceControlTest {

  private static final Log log = LogFactory.getLog(SSTestPVCSSourceControl.class);

  private static final String STRING_SOURCE_LINE_ONE = "test_project/sourceline/alwaysvalid";
  private static final String STRING_SOURCE_LINE_TWO = "test_project/second_sourceline/src";

  private PVCSSourceControl pvcs;
  private static final int TEST_CHANGE_LIST_ID = 23;
  private static final String TEST_BRANCH = "test_branch_1";


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSince() throws Exception {
    // test that we get changes
    final int lastChangeListID = pvcs.getChangesSince(TEST_CHANGE_LIST_ID);
    assertTrue(lastChangeListID != TEST_CHANGE_LIST_ID);

    // test fix #516 - that we dont' leave temp files.

    // temp dir is empty
    IoUtils.emptyDir(TestHelper.JAVA_TEMP_DIR);
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);

    // no new changes ?
    final int newChangeListID = pvcs.getChangesSince(lastChangeListID);

    final ChangeList lastChangeList = cm.getChangeList(lastChangeListID);
    if (log.isDebugEnabled()) log.debug("lastChangeList = " + lastChangeList);
    final ChangeList newChangeList = cm.getChangeList(newChangeListID);
    if (log.isDebugEnabled()) log.debug("newChangeList = " + newChangeList);

    assertEquals(lastChangeListID, newChangeListID);

    // check if no descriptions contain "Branches:" in the beginning
    final List pendingChangeLists = cm.getPendingChangeLists(getTestBuildID());
    for (final Iterator i = pendingChangeLists.iterator(); i.hasNext();) {
      final ChangeList changeList = (ChangeList) i.next();
      assertTrue(!changeList.getDescription().startsWith("Branches:"));
    }

    // temp dir is empty
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);
  }


  public void test_checkoutLatest() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    pvcs.checkoutLatest();
    pvcs.checkoutLatest(); // make sure nothing happens at second run
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertDirIsNotEmpty(agent, "test_project/sourceline/alwaysvalid/src");
    TestHelper.assertNotExists(agent, "test_project/second_sourceline/src");
  }


  public void test_checkoutLatestCanBeAcessesWithNameAndPassword() throws Exception {
    TestHelper.setSourceControlProperty(pvcs.getBuildID(), VCSAttribute.PVCS_USER, "test_user");
    TestHelper.setSourceControlProperty(pvcs.getBuildID(), VCSAttribute.PVCS_PASSWORD, org.parabuild.ci.security.SecurityManager.encryptPassword("test_password"));
    TestHelper.emptyCheckoutDir(agent);
    pvcs.checkoutLatest();
    pvcs.checkoutLatest(); // make sure nothing happens at second run
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertDirIsNotEmpty(agent, "test_project/sourceline/alwaysvalid/src");
    TestHelper.assertNotExists(agent, "test_project/second_sourceline/src");
  }


  public void test_checkOutLatestBranch() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.setSourceControlProperty(getTestBuildID(), VCSAttribute.PVCS_BRANCH_NAME, TEST_BRANCH);
    pvcs.reloadConfiguration();
    pvcs.checkoutLatest();
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  public void test_syncToChangeListBranch() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);
    TestHelper.setSourceControlProperty(getTestBuildID(), VCSAttribute.PVCS_BRANCH_NAME, TEST_BRANCH);
    pvcs.reloadConfiguration();
    final int changeListID = pvcs.getChangesSince(ChangeList.UNSAVED_ID);
    if (log.isDebugEnabled()) log.debug("changeListID: " + changeListID);
    pvcs.syncToChangeList(changeListID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceInBranch() throws Exception {
    TestHelper.setSourceControlProperty(getTestBuildID(), VCSAttribute.PVCS_BRANCH_NAME, TEST_BRANCH);
    pvcs.reloadConfiguration();
    final int changeListID = pvcs.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID != ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);
    final ChangeList changeList = cm.getChangeList(changeListID);
    if (log.isDebugEnabled()) log.debug("changeList: " + changeList);
    assertEquals(TEST_BRANCH, changeList.getBranch());
  }


  public void test_checkOutLatestCatchesConfigUpdates() throws Exception {
    pvcs.checkoutLatest();
    final String oldRelativeBuildDir = TestHelper.assertCurrentBuildPathExists(pvcs, agent);

    // update property
    TestHelper.setSourceControlProperty(getTestBuildID(), VCSAttribute.PVCS_PROJECT, STRING_SOURCE_LINE_TWO);
    pvcs.reloadConfiguration();

    // call sync
    pvcs.checkoutLatest();

    TestHelper.assertOldBuildPathGoneAndNewAppeared(pvcs, oldRelativeBuildDir, agent);
  }


  public void test_checkOutMultilineRepositoryPath() throws Exception {
    // cleanup build logs dir
    assertTrue(agent.emptyLogDir());
    // alter
    final String multilineSourceLine = STRING_SOURCE_LINE_ONE + '\n' + STRING_SOURCE_LINE_TWO;
    TestHelper.setSourceControlProperty(pvcs.getBuildID(), VCSAttribute.PVCS_PROJECT, multilineSourceLine);

    // sync w/reload
    pvcs.reloadConfiguration();
    pvcs.checkoutLatest();

    // assert
    TestHelper.assertPathsEqual(STRING_SOURCE_LINE_ONE, pvcs.getRelativeBuildDir());
    TestHelper.assertDirIsNotEmpty(agent, STRING_SOURCE_LINE_ONE);
    TestHelper.assertDirIsNotEmpty(agent, STRING_SOURCE_LINE_TWO);

    // no logs /passwds left
    assertTrue(agent.logDirIsEmpty());
  }


  /**
   * Tests that checkoutLatest cannot process unxesising
   * source line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessUnexistingSourceLine() throws Exception {
    // alter
    TestHelper.setSourceControlProperty(pvcs.getBuildID(), VCSAttribute.PVCS_PROJECT, "test/never/existed");
    pvcs.reloadConfiguration();

    // test
    try {
      pvcs.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
      System.out.println("DEBUG: e: " + e);
    }
  }


  /**
   * Tests that checkoutLatest cannot process unxesising
   * source line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessInavalidUser() throws Exception {
// REVIEWME: simeshev@parabuilci.org -> currently it allows accessing the database thow we created test_user
//    // alter
//    TestHelper.setSourceControlProperty(pvcs.getBuildID(), SourceControlSetting.PVCS_USER, "never_existed_user");
//    pvcs.reloadConfiguration();
//
//    // test
//    try {
//      pvcs.checkoutLatest();
//      TestHelper.failNoExceptionThrown();
//    } catch (Exception e) {
//    }
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListInitsEmptyCheckoutDir() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);

    final int changeListID = pvcs.getChangesSince(ChangeList.UNSAVED_ID);
    final ChangeList newChangeList = cm.getChangeList(changeListID);
    log.debug("lastChangeList = " + newChangeList);

    // sync to latest
    pvcs.syncToChangeList(changeListID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeList() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);
    final int changeListID = pvcs.getChangesSince(ChangeList.UNSAVED_ID);
    if (log.isDebugEnabled()) log.debug("changeListID: " + changeListID);
    pvcs.syncToChangeList(changeListID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListPicksUpConfigChanges() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);

    // alter
    TestHelper.setSourceControlProperty(pvcs.getBuildID(), VCSAttribute.PVCS_PROJECT, STRING_SOURCE_LINE_TWO);
    pvcs.reloadConfiguration();
    pvcs.syncToChangeList(pvcs.getChangesSince(-1));
    final String oldRelativeBuildDir = TestHelper.assertCurrentBuildPathExists(pvcs, agent);

    // update property
    TestHelper.setSourceControlProperty(pvcs.getBuildID(), VCSAttribute.PVCS_PROJECT, STRING_SOURCE_LINE_ONE);
    pvcs.reloadConfiguration();

    // we expect that there is change list in the test/config/dataset.xml
    pvcs.syncToChangeList(TEST_CHANGE_LIST_ID);

    // assert
    TestHelper.assertOldBuildPathGoneAndNewAppeared(pvcs, oldRelativeBuildDir, agent);
  }


  /**
   * Tests that getting change list from source line that
   * doesn't contain any changes but dirs
   *
   * @throws Exception
   */
  public void test_getChangesSinceDoesntFailOnBlankSourceline() throws Exception {
    //REVIEWME: not clear what is blank source line in PVCS
  }


  /**
   * Tests can not access passworded source line with wrong
   * password
   */
  public void test_canNotAccessWithWrongPassword() throws Exception {
    // alter
    TestHelper.setSourceControlProperty(pvcs.getBuildID(), VCSAttribute.PVCS_USER, "test_user");
    TestHelper.setSourceControlProperty(pvcs.getBuildID(), VCSAttribute.PVCS_PASSWORD, org.parabuild.ci.security.SecurityManager.encryptPassword("wrong_passord"));
    pvcs.reloadConfiguration();

    // test
    try {
      pvcs.getChangesSince(TEST_CHANGE_LIST_ID);
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
  }


  public void test_getUsersMap() throws Exception {
    assertEquals(0, pvcs.getUsersMap().size());
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceHandlesFirstRun() throws Exception {
    final int changeListID = pvcs.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);
  }


  public final int getTestBuildID() {
    return 21;
  }


  protected void setUp() throws Exception {
    super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
    super.errorManager.clearAllActiveErrors();
    final BuildConfig buildConfig = cm.getBuildConfiguration(getTestBuildID());
    this.pvcs = new PVCSSourceControl(buildConfig);
    this.pvcs.setAgentHost(agentHost);

    // set in super's setUp
    assertEquals(SystemProperty.UNLIMITED_INITIAL_NUMBER_OF_CHANGELISTS, this.pvcs.initialNumberOfChangeLists());
  }


  /**
   * Tests label method.
   */
  public void test_label() throws BuildException, CommandStoppedException, AgentFailureException {
    // no exception thrown
    pvcs.syncToChangeList(pvcs.getChangesSince(-1));
    pvcs.label("parabuild_test_" + System.currentTimeMillis());
    assertTrue(errorManager.errorCount() == 0);
  }


  public void test_getSyncCommandNote() throws AgentFailureException {
    assertEquals("pcli get -o -qe -z -u \"-prD:\\mor2\\dev\\bt\\test\\data\\pvcs\" \"-dJan 30, 2006 11:17:22 PM\"  \"/test_project/sourceline/alwaysvalid\"", pvcs.getSyncCommandNote(TEST_CHANGE_LIST_ID));
  }


  public SSTestPVCSSourceControl(final String s) {
    super(s);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestPVCSSourceControl.class,
            new String[]{
                    "test_getChangesSince",
                    "test_getSyncCommandNote",
                    "test_getChangesSinceInBranch",
                    "test_checkOutLatestBranch",
                    "test_label",
                    "test_syncToChangeListPicksUpConfigChanges",
                    "test_syncToChangeList",
                    "test_getChangesSinceHandlesFirstRun",
                    "test_checkoutLatest"
            });
  }
}
