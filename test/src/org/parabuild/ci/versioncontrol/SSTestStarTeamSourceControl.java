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
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SystemProperty;

/**
 */
public final class SSTestStarTeamSourceControl extends AbstractSourceControlTest {

  private static final Log log = LogFactory.getLog(SSTestStarTeamSourceControl.class);
  private static final String STRING_SOURCE_LINE_ONE = "test_project/sourceline/alwaysvalid";
  private static final String STRING_SOURCE_LINE_TWO = "test_project/second_sourceline/src";
  private static final int TEST_CHANGE_LIST_ID = 26;

  private StarTeamSourceControl starTeam = null;


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSince() throws Exception {
    // test that we get changes
    final int lastChangeListID = starTeam.getChangesSince(TEST_CHANGE_LIST_ID);
    assertTrue(lastChangeListID != TEST_CHANGE_LIST_ID);

    // test fix #516 - that we dont' leave temp files.

    // temp dir is empty
    IoUtils.emptyDir(TestHelper.JAVA_TEMP_DIR);
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);

    // no new changes ?
    final int newChangeListID = starTeam.getChangesSince(lastChangeListID);

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
    starTeam.checkoutLatest();
    starTeam.checkoutLatest(); // make sure nothing happens at second run
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertDirIsNotEmpty(agent, "test_project/sourceline/alwaysvalid/src");
    TestHelper.assertNotExists(agent, "test_project/second_sourceline/src");
  }


  public void test_checkOutLatestBranch() throws Exception {
    //REVIEWME: simeshev@parabuilci.org -> not clear how branches look like
  }


  public void test_syncToChangeListBranch() throws Exception {
    //REVIEWME: simeshev@parabuilci.org -> not clear how branches look like
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceInBranch() throws Exception {
    // update property
    TestHelper.setSourceControlProperty(getTestBuildID(), VCSAttribute.STARTEAM_PROJECT_PATH, "test_project/Release 1 release-prep codeline");
    starTeam.reloadConfiguration();

    // test that we get changes
    final int lastChangeListID = starTeam.getChangesSince(TEST_CHANGE_LIST_ID);
    assertTrue(lastChangeListID != TEST_CHANGE_LIST_ID);

    // no new changes ?
    final int newChangeListID = starTeam.getChangesSince(lastChangeListID);

    assertEquals(lastChangeListID, newChangeListID);
  }


  public void test_checkOutLatestCatchesConfigUpdates() throws Exception {
    starTeam.checkoutLatest();
    final String oldRelativeBuildDir = TestHelper.assertCurrentBuildPathExists(starTeam, agent);

    // update property
    TestHelper.setSourceControlProperty(getTestBuildID(), VCSAttribute.STARTEAM_PROJECT_PATH, STRING_SOURCE_LINE_TWO);
    starTeam.reloadConfiguration();

    // call sync
    starTeam.checkoutLatest();

    TestHelper.assertOldBuildPathGoneAndNewAppeared(starTeam, oldRelativeBuildDir, agent);
  }


  public void test_checkOutMultilineRepositoryPath() throws Exception {
    // cleanup build logs dir
    assertTrue(agent.emptyLogDir());
    // alter
    final String multilineSourceLine = STRING_SOURCE_LINE_ONE + '\n' + STRING_SOURCE_LINE_TWO;
    TestHelper.setSourceControlProperty(starTeam.getBuildID(), VCSAttribute.STARTEAM_PROJECT_PATH, multilineSourceLine);

    // sync w/reload
    starTeam.reloadConfiguration();
    starTeam.checkoutLatest();

    // assert
    TestHelper.assertPathsEqual(STRING_SOURCE_LINE_ONE, starTeam.getRelativeBuildDir().substring(1));
    TestHelper.assertDirIsNotEmpty(agent, STRING_SOURCE_LINE_ONE);
    TestHelper.assertDirIsNotEmpty(agent, STRING_SOURCE_LINE_TWO);

    // no logs /passwds left
    assertTrue(agent.logDirIsEmpty());
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessUnexistingSourceLine() throws Exception {
    // alter
    TestHelper.setSourceControlProperty(starTeam.getBuildID(), VCSAttribute.STARTEAM_PROJECT_PATH, "/test/never/existed");
    starTeam.reloadConfiguration();

    // test
    try {
      starTeam.checkoutLatest();
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
    TestHelper.setSourceControlProperty(starTeam.getBuildID(), VCSAttribute.STARTEAM_USER, "never_existed_user");
    starTeam.reloadConfiguration();

    // test
    try {
      starTeam.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListInitsEmptyCheckoutDir() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);

    final int changeListID = starTeam.getChangesSince(ChangeList.UNSAVED_ID);
    final ChangeList newChangeList = cm.getChangeList(changeListID);
    log.debug("lastChangeList = " + newChangeList);

    // sync to latest
    starTeam.syncToChangeList(changeListID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeList() throws Exception {
    // prepare
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);
    final int changeListID = starTeam.getChangesSince(ChangeList.UNSAVED_ID);
    if (log.isDebugEnabled()) log.debug("changeListID: " + changeListID);
    // first call
    starTeam.syncToChangeList(changeListID);
    TestHelper.assertCheckoutDirNotEmpty(agent);

    // second call - nothing should happen
    starTeam.syncToChangeList(changeListID);

    // third call
    // create a file that is not under StarTeam

    final String relativeNonStarTeamFile = starTeam.getRelativeBuildDir() + "/non_starteam_file.txt";
    final String absoluteNonStarTeamFile = agent.getCheckoutDirName() + relativeNonStarTeamFile;
    agent.createFile(absoluteNonStarTeamFile, "This is a file that is not under StarTeam control");
    TestHelper.assertExists(agent, relativeNonStarTeamFile); // cover-ass check
    starTeam.syncToChangeList(changeListID);
    TestHelper.assertNotExists(agent, relativeNonStarTeamFile);
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListPicksUpConfigChanges() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);

    // alter
    TestHelper.setSourceControlProperty(starTeam.getBuildID(), VCSAttribute.STARTEAM_PROJECT_PATH, STRING_SOURCE_LINE_TWO);
    starTeam.reloadConfiguration();
    starTeam.syncToChangeList(starTeam.getChangesSince(-1));
    final String oldRelativeBuildDir = TestHelper.assertCurrentBuildPathExists(starTeam, agent);

    // update property
    TestHelper.setSourceControlProperty(starTeam.getBuildID(), VCSAttribute.STARTEAM_PROJECT_PATH, STRING_SOURCE_LINE_ONE);
    starTeam.reloadConfiguration();

    // we expect that there is change list in the test/config/dataset.xml
    starTeam.syncToChangeList(TEST_CHANGE_LIST_ID);

    // assert
    TestHelper.assertOldBuildPathGoneAndNewAppeared(starTeam, oldRelativeBuildDir, agent);
  }


  /**
   * Tests that getting change list from source line that doesn't
   * contain any changes but dirs
   *
   * @throws Exception
   */
  public void test_getChangesSinceDoesntFailOnBlankSourceline() throws Exception {
    // There is no blank line problem in StarTeam
  }


  /**
   * Tests can not access passworded source line with wrong password
   */
  public void test_canNotAccessWithWrongPassword() throws Exception {
    // alter
    TestHelper.setSourceControlProperty(starTeam.getBuildID(), VCSAttribute.STARTEAM_PASSWORD, org.parabuild.ci.security.SecurityManager.encryptPassword("wrong_passord"));
    starTeam.reloadConfiguration();

    // test
    try {
      starTeam.getChangesSince(-1);
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
  }


  public void test_getUsersMap() throws Exception {
    assertEquals(0, starTeam.getUsersMap().size());
  }


  public int getTestBuildID() {
    return 22;  //To change body of implemented methods use File | Settings | File Templates.
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceHandlesFirstRun() throws Exception {
    final int changeListID = starTeam.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);
  }


  /**
   * Tests label method.
   */
  public void test_label() throws BuildException, CommandStoppedException, AgentFailureException {
    // no exception thrown
    starTeam.syncToChangeList(starTeam.getChangesSince(-1));
    starTeam.label("parabuild_test_" + System.currentTimeMillis());
    assertTrue(errorManager.errorCount() == 0);
  }


  /**
   *
   */
  public void test_getSyncCommandNote() {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.errorManager.clearAllActiveErrors();
    final BuildConfig buildConfig = cm.getBuildConfiguration(getTestBuildID());
    this.starTeam = new StarTeamSourceControl(buildConfig);
    this.starTeam.setAgentHost(agentHost);

    // set in super's setUp
    assertEquals(SystemProperty.UNLIMITED_INITIAL_NUMBER_OF_CHANGELISTS, this.starTeam.initialNumberOfChangeLists());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestStarTeamSourceControl.class,
            new String[]{
                    "test_syncToChangeList",
                    "test_checkOutMultilineRepositoryPath",
                    "test_getChangesSince",
                    "test_getSyncCommandNote",
                    "test_getChangesSinceInBranch",
                    "test_checkOutLatestBranch",
                    "test_label",
                    "test_syncToChangeListPicksUpConfigChanges",
                    "test_checkoutLatest"
            });
  }


  public SSTestStarTeamSourceControl(final String s) {
    super(s);
  }
}
