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
import java.io.IOException;
import java.util.Map;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;

import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.SystemProperty;


/**
 * Tests CVSSourceControl
 */
public class SSTestCVSSourceControl extends AbstractSourceControlTest {

  private static final Log log = LogFactory.getLog(SSTestCVSSourceControl.class);

  private static final String STRING_SOURCE_LINE_ONE = "test/sourceline/alwaysvalid";
  private static final String STRING_SOURCE_LINE_TWO = "test/second_sourceline/src";
  private static final String TEST_BRANCH_NAME = "test_branch";

  private CVSSourceControl cvs = null;


  private static File testDirWithSpaces() {
    return new File(TestHelper.getTestTempDir(), "test dir with spaces");
  }


  public SSTestCVSSourceControl(final String s) {
    super(s);
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSince() throws Exception {
    // test that we get changes
//    log.debug("getting changes starting change list id 1");
    final int lastChangeListID = cvs.getChangesSince(1);

//    log.debug("lastChangeListID: " + lastChangeListID);
    assertTrue(lastChangeListID != 1);
//    final ChangeList lastChangeList = ConfigurationManager.getInstance().getChangeList(lastChangeListID);
//    if (log.isDebugEnabled()) log.debug("lastChangeList: " + lastChangeList);
//    final List lastPendingChangeLists = ConfigurationManager.getInstance().getPendingChangeLists(getTestBuildID());
//    if (log.isDebugEnabled()) log.debug("lastPendingChangeLists: " + lastPendingChangeLists);

    // test fix #516 - that we dont' leave temp files.

    // temp dir is empty
    IoUtils.emptyDir(TestHelper.JAVA_TEMP_DIR);
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);
//    log.debug("getting changes starting change list id " + lastChangeListID);
    final int newChangeListID = cvs.getChangesSince(lastChangeListID);
//    if (log.isDebugEnabled()) log.debug("newChangeListID: " + newChangeListID);
//    final ChangeList newChangeList = ConfigurationManager.getInstance().getChangeList(newChangeListID);
//    if (log.isDebugEnabled()) log.debug("newChangeList: " + newChangeList);
//    final List newPendingChangeLists = ConfigurationManager.getInstance().getPendingChangeLists(getTestBuildID());
//    if (log.isDebugEnabled()) log.debug("newPendingChangeLists: " + newPendingChangeLists);

    // no new changes
    assertEquals(lastChangeListID, newChangeListID);

    // temp dir is empty
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);
  }


  public void test_getChangesSinceHandlesFirstRun() throws Exception {
    final int changeListID = cvs.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);
  }


  public void test_getChangesSinceCanUseHistory() throws Exception {
    // NOTE: simeshev@parabuildci.org -> hangs on non-english locales
    // alter pre-check setting
    SourceControlSetting precheck = cm.getSourceControlSetting(cvs.getBuildID(), SourceControlSetting.CVS_CHANGE_PRECHECK);
    if (precheck == null) {
      precheck = new SourceControlSetting();
      precheck.setBuildID(TestHelper.TEST_CVS_VALID_BUILD_ID);
      precheck.setPropertyName(SourceControlSetting.CVS_CHANGE_PRECHECK);
    }
    precheck.setPropertyValue(SourceControlSetting.OPTION_CHECKED);
    cm.saveObject(precheck);
    cvs.reloadConfiguration();

    // test
    final int lastChangeListID = cvs.getChangesSince(1);
    assertTrue(lastChangeListID != 1); // test that we get changes
    final int newChangeListID = cvs.getChangesSince(lastChangeListID);
    assertEquals(lastChangeListID, newChangeListID); // no new changes
  }


  /**
   * Tests label method.
   */
  public void test_label() {
    // TODO: implement
  }


  /**
   * Tests getting change list with checkin window enabled.
   *
   * @throws Exception
   */
  public void test_getChangesSinceWithCheckinWindowOn() throws Exception {

    final SourceControlSetting changeWindow = cm.getSourceControlSetting(cvs.getBuildID(), SourceControlSetting.CVS_CHANGE_WINDOW);
    changeWindow.setPropertyValue("10"); // 10 secs
    cm.saveObject(changeWindow);

    int changeListID = cvs.getChangesSince(1);
    assertTrue(changeListID != 1);
    changeListID = cvs.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_bug251_returnsTheSameIDAfterFirstTime() throws Exception {
    final int changeListID = cvs.getChangesSince(ChangeList.UNSAVED_ID);
    if (log.isDebugEnabled()) log.debug("changeListID: " + changeListID);
    assertTrue(changeListID != ChangeList.UNSAVED_ID);
    final int changesSince = cvs.getChangesSince(changeListID);
    if (log.isDebugEnabled()) log.debug("changesSince: " + changesSince);
    assertEquals(changeListID, changesSince);
  }


  public void test_checkoutLatest() throws Exception {
    assertTrue("Build logs home dir is not empty", agent.emptyLogDir());
    cvs.checkoutLatest();
    assertTrue("Build logs home dir is not empty", agent.emptyLogDir());
  }


  public void test_checkOutLatestBranch() throws Exception {
    final String branchedFile = STRING_SOURCE_LINE_ONE + "/src/readme_in_test_branch.txt";
    final String fileInBranch = STRING_SOURCE_LINE_ONE + "/src/readme.txt";

    // sync  normally
    cvs.checkoutLatest();

    // make sure there is no branch
    TestHelper.assertNotExists(agent, branchedFile);
    TestHelper.assertExists(agent, fileInBranch);

    // alter branch setting
    addBranchSettingToCVSConfiguration();
    cvs.reloadConfiguration();

    // re-sync and assert
    cvs.checkoutLatest();
    TestHelper.assertExists(agent, branchedFile);
    TestHelper.assertExists(agent, fileInBranch);
  }


  public void test_getChangesSinceInHEAD() throws Exception {
    final String fileInBranch = STRING_SOURCE_LINE_ONE + "/src/readme.txt";

    // alter branch setting
    TestHelper.setSourceControlProperty(cvs.getBuildID(), SourceControlSetting.CVS_BRANCH_NAME, "HEAD");
    cvs.reloadConfiguration();

    // re-sync and assert
    assertTrue(!(cvs.getChangesSince(ChangeList.UNSAVED_ID) == ChangeList.UNSAVED_ID));
    TestHelper.assertExists(agent, fileInBranch);
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
    // alter config to be branched
    addBranchSettingToCVSConfiguration();
    cvs.reloadConfiguration();

    // get changes
    final int changeListID = cvs.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID != ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);
    final ChangeList changeList = cm.getChangeList(changeListID);

    // make sure branch name is set
    assertEquals(TEST_BRANCH_NAME, changeList.getBranch());
  }


  private void addBranchSettingToCVSConfiguration() {
    final SourceControlSetting branch = new SourceControlSetting();
    branch.setBuildID(cvs.getBuildID());
    branch.setPropertyName(SourceControlSetting.CVS_BRANCH_NAME);
    branch.setPropertyValue(TEST_BRANCH_NAME);
    cm.saveObject(branch);
  }


  public void test_checkOutMultilineRepositoryPath() throws Exception {
    // cleanup build logs dir
    assertTrue(agent.emptyLogDir());

    // cleanup temp password dir
    assertTrue(agent.emptyPasswordDir());

    // alter
    final String multilineSourceLine = STRING_SOURCE_LINE_ONE + '\n' + STRING_SOURCE_LINE_TWO;
    TestHelper.setSourceControlProperty(cvs.getBuildID(), SourceControlSetting.CVS_REPOSITORY_PATH, multilineSourceLine);

    // sync w/reload
    cvs.reloadConfiguration();
    cvs.checkoutLatest();

    // assert
    TestHelper.assertPathsEqual(STRING_SOURCE_LINE_ONE, cvs.getRelativeBuildDir());
    TestHelper.assertDirIsNotEmpty(agent, STRING_SOURCE_LINE_ONE);
    TestHelper.assertDirIsNotEmpty(agent, STRING_SOURCE_LINE_TWO);

    // no logs /passwds left
    assertTrue(agent.logDirIsEmpty());
    assertTrue(agent.passwordDirIsEmpty());
  }


  public void test_picksRelativeBuildDir() throws Exception {
    // alter
    final String multilineSourceLine = STRING_SOURCE_LINE_ONE + '\n' + STRING_SOURCE_LINE_TWO;
    TestHelper.setSourceControlProperty(cvs.getBuildID(), SourceControlSetting.CVS_REPOSITORY_PATH, multilineSourceLine);

    // sync w/reload
    cvs.reloadConfiguration();
    cvs.checkoutLatest();
    TestHelper.assertPathsEqual(STRING_SOURCE_LINE_ONE, cvs.getRelativeBuildDir());

    // change to advanced mode
    TestHelper.setSourceControlProperty(cvs.getBuildID(), SourceControlSetting.CVS_CUSTOM_RELATIVE_BUILD_DIR, STRING_SOURCE_LINE_TWO);
    cvs.reloadConfiguration();
    assertEquals("Relative build dir should change", STRING_SOURCE_LINE_TWO, cvs.getRelativeBuildDir());
  }


  private void alterCVSPathToSecondSourceLine() {
    final SourceControlSetting path = cm.getSourceControlSetting(cvs.getBuildID(), SourceControlSetting.CVS_REPOSITORY_PATH);
    path.setPropertyValue(STRING_SOURCE_LINE_TWO);
    cm.saveObject(path);
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source
   * line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessUnexistingSourceLine() throws Exception {
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_PATH_TO_CLIENT, "cvs");
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_ROOT, TestHelper.CVS_VALID_ROOT);
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_REPOSITORY_PATH, TestHelper.CVS_INVALID_SOURCE_LINE_PATH);
    cvs.reloadConfiguration();

    boolean thrown = false;
    try {
      cvs.checkoutLatest();
    } catch (BuildException e) {
      thrown = true;
    }
    assertTrue("Expected BuildException to be thrown, but it was not", thrown);
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source
   * line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessInavalidUser() throws Exception {

    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_PATH_TO_CLIENT, "cvs");
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_ROOT, TestHelper.CVS_ROOT_WITH_WRONG_USER);
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_REPOSITORY_PATH, TestHelper.CVS_VALID_SOURCE_LINE_PATH);

    cvs.reloadConfiguration();

    boolean thrown = false;
    try {
      cvs.checkoutLatest();
    } catch (BuildException e) {
      thrown = true;
    }
    assertTrue("Expected BuildException to be thrown, but it was not", thrown);
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source
   * line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessUnknownHost() throws Exception {

    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_PATH_TO_CLIENT, "cvs");
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_ROOT, TestHelper.CVS_ROOT_WITH_UNKNOWN_HOST);
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_REPOSITORY_PATH, TestHelper.CVS_VALID_SOURCE_LINE_PATH);

    cvs.reloadConfiguration();

    boolean thrown = false;
    try {
      cvs.checkoutLatest();
    } catch (BuildException e) {
      thrown = true;
    }
    assertTrue("Expected BuildException to be thrown, but it was not", thrown);
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source
   * line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessUnknownPort() throws Exception {

    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_PATH_TO_CLIENT, "cvs");
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_ROOT, TestHelper.CVS_ROOT_WITH_UNKNOWN_PORT);
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_REPOSITORY_PATH, TestHelper.CVS_VALID_SOURCE_LINE_PATH);

    cvs.reloadConfiguration();

    boolean thrown = false;
    try {
      cvs.checkoutLatest();
    } catch (BuildException e) {
      thrown = true;
    }
    assertTrue("Expected BuildException to be thrown, but it was not", thrown);
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListInitsEmptyCheckoutDir() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);
//    cvs.setLocalCopyInitialized(true);
    cvs.syncToChangeList(1);
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeList() throws Exception {
    // we expect that there is change list in the test/config/dataset.xml
    cvs.syncToChangeList(1);
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertDirIsNotEmpty(agent, cvs.getRelativeBuildDir());
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListPicksUpConfigChanges() throws Exception {
    // we expect that there is change list in the test/config/dataset.xml
    cvs.syncToChangeList(9);
    final String oldRelativeBuildDir = TestHelper.assertCurrentBuildPathExists(cvs, agent);

    // update property
    alterCVSPathToSecondSourceLine();

    // reload
    cvs.reloadConfiguration();
    cvs.syncToChangeList(9);

    // assert
    TestHelper.assertOldBuildPathGoneAndNewAppeared(cvs, oldRelativeBuildDir, agent);
  }


  /**
   * Tests that getting change list from source line that doesn't
   * contain any changes but dirs
   *
   * @throws Exception
   */
  public void test_getChangesSinceDoesntFailOnBlankSourceline() throws Exception {
    final CVSSourceControl cvsForEmptySourceLine = makeCVSSourceControl(TestHelper.TEST_CVS_EMPTY_BUILD_ID);
    cvsForEmptySourceLine.setAgentHost(agentHost);
    final int changeListID = cvsForEmptySourceLine.getChangesSince(1);
    assertEquals(1, changeListID);
  }


  /**
   * Tests can access passworded source line
   */
  public void test_passwordAccess() throws Exception {

    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_PATH_TO_CLIENT, "cvs");
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_ROOT, TestHelper.CVS_VALID_PASSWORD_ROOT);
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_PASSWORD, TestHelper.CVS_VALID_PASSWORD);
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_REPOSITORY_PATH, TestHelper.CVS_VALID_SOURCE_LINE_PATH);

    cvs.reloadConfiguration();
    cvs.checkoutLatest();
  }


  /**
   * Tests can not access passworded source line with wrong
   * password
   */
  public void test_canNotAccessWithWrongPassword() throws Exception {

    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_PATH_TO_CLIENT, "cvs");
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_ROOT, TestHelper.CVS_VALID_PASSWORD_ROOT);
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_PASSWORD, TestHelper.CVS_INVALID_PASSWORD);
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_REPOSITORY_PATH, TestHelper.CVS_VALID_SOURCE_LINE_PATH);


    cvs.reloadConfiguration();
    try {
      cvs.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  /**
   * Tests can access passworded source line via CVSNT
   */
  public void test_bug892_passwordAccessCVSNT() throws Exception {
    if (!agent.isWindows()) return;

    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_PATH_TO_CLIENT, makeCVSNTExePath());
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_ROOT, TestHelper.CVS_VALID_PASSWORD_ROOT);
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_PASSWORD, TestHelper.CVS_VALID_PASSWORD);
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_REPOSITORY_PATH, TestHelper.CVS_VALID_SOURCE_LINE_PATH);

    cvs.reloadConfiguration();
    cvs.checkoutLatest();
  }


  /**
   * Tests can not access passworded source line with wrong
   * password
   */
  public void test_bug892_canNotAccessWithWrongPasswordCVSNT() throws Exception {
    if (!agent.isWindows()) return;

    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_PATH_TO_CLIENT, makeCVSNTExePath());
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_ROOT, TestHelper.CVS_VALID_PASSWORD_ROOT);
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_PASSWORD, TestHelper.CVS_INVALID_PASSWORD);
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_REPOSITORY_PATH, TestHelper.CVS_VALID_SOURCE_LINE_PATH);


    cvs.reloadConfiguration();
    try {
      cvs.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  /**
   */
  public void test_bug897_doesNotHangOnNoPasswordCVSNT() throws Exception {
    if (!agent.isWindows()) return;
    // first let CVSNT "log in" w/wrong password
    test_bug892_canNotAccessWithWrongPasswordCVSNT();

    TestHelper.setSourceControlProperty(getTestBuildID(), SourceControlSetting.CVS_ROOT, TestHelper.CVS_VALID_PASSWORD_ROOT);
    TestHelper.setSourceControlProperty(getTestBuildID(), SourceControlSetting.CVS_PATH_TO_CLIENT, makeCVSNTExePath());
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    cm.deleteObject(cm.getSourceControlSetting(getTestBuildID(), SourceControlSetting.CVS_PASSWORD));
    cvs.reloadConfiguration();

    try {
      cvs.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  public void test_getUsersMap() throws Exception {
    final Map resut = cvs.getUsersMap();
    assertNotNull(resut);
    assertTrue(!resut.isEmpty());
    assertEquals(3, resut.size());
    assertTrue(agent.relativeTempPathExists(CVSSourceControl.CVS_CVSROOT_USERS));

    // check if there was a error created (for a misformed e-mail)
    assertTrue(errorManager.errorCount() > 0);
  }


  public int getTestBuildID() {
    return TestHelper.TEST_CVS_VALID_BUILD_ID;
  }


  /**
   * Thats that source line existance for CVS is checked
   * including existance of system ./CVS.
   */
  public void test_issue315_missngCVSSystemFilesDetected() throws Exception {
    // sync
    cvs.syncToChangeList(1);

    // delete a file (CVS/Entries)
    final String relativeBuildDir = TestHelper.assertCurrentBuildPathExists(cvs, agent);
    final String cvsEntries = relativeBuildDir + File.separator + "CVS" + File.separator + "Entries";
    if (log.isDebugEnabled()) log.debug("cvsEntries = " + cvsEntries);
    TestHelper.assertExists(cvsEntries, agent);

    agent.deleteFileUnderCheckoutDir(cvsEntries);
    TestHelper.assertNotExists(agent, cvsEntries);

    // sync again
    cvs.syncToChangeList(1);
    TestHelper.assertExists(agent, cvsEntries);
  }


  public void test_getSyncCommandNote() {
    assertNotNull(cvs.getSyncCommandNote(1));
  }


  public void test_isBuildDirInitialized() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);
    assertEquals("Build dir should be uninitialized", false, cvs.isBuildDirInitialized());
    cvs.syncToChangeList(1);
    TestHelper.assertCheckoutDirNotEmpty(agent);
    assertEquals("Build dir should be initialized", true, cvs.isBuildDirInitialized());
  }


  public void test_checkOutLatestCatchesConfigUpdates() throws Exception {
    cvs.checkoutLatest();
    final String oldRelativeBuildDir = TestHelper.assertCurrentBuildPathExists(cvs, agent);

    // update property
    alterCVSPathToSecondSourceLine();

    // call sync
    cvs.reloadConfiguration();
    cvs.checkoutLatest();

    TestHelper.assertOldBuildPathGoneAndNewAppeared(cvs, oldRelativeBuildDir, agent);
  }


  /**
   * Tests that can checkout latest of both path to SS.ESE
   * and project path have spaces.
   */
  public void test_checkoutLatestWithSpacesInPathToCVSAndProject() throws Exception {
    if (!agent.isWindows()) return;
    switchToSVSNTAndProjectWithSpaces();
    cvs.reloadConfiguration();
    try {
      cvs.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // REVIEWME: simeshev@parabuilci.org -> currently CVSNT throws an exception that pserver protocol is not available
      // assertTrue(e.toString().indexOf("test/sourceline/alwaysvalid/spaced name is not an existing filename or project") >=0);
    }
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceithSpacesInPathToCVSAndProject() throws Exception {
    if (!agent.isWindows()) return;
    //noinspection UNUSED_SYMBOL
    int newChangeListID = -1;
    try {
      final int lastChangeListID = cvs.getChangesSince(1);
      switchToSVSNTAndProjectWithSpaces();
      cvs.reloadConfiguration();
      assertTrue(lastChangeListID != 1);
      newChangeListID = cvs.getChangesSince(lastChangeListID);
    } catch (BuildException e) {
      // REVIEWME: simeshev@parabuilci.org -> currently CVSNT throws an exception that pserver protocol is not available
      // assertTrue(e.toString().indexOf("test/sourceline/alwaysvalid/spaced name is not an existing filename or project") >=0);
//      assertEquals(lastChangeListID, newChangeListID);
    }
  }


  /**
   */
  public void test_bug897_doesNotHangOnNoPassword() throws Exception {
    TestHelper.setSourceControlProperty(getTestBuildID(), SourceControlSetting.CVS_ROOT, TestHelper.CVS_VALID_PASSWORD_ROOT);
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    cm.deleteObject(cm.getSourceControlSetting(getTestBuildID(), SourceControlSetting.CVS_PASSWORD));
    cvs.reloadConfiguration();

    try {
      cvs.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  private void switchToSVSNTAndProjectWithSpaces() throws IOException {
    IoUtils.copyDirectory(new File(System.getProperty("test.cvsnt.home")), testDirWithSpaces());
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_PATH_TO_CLIENT, testDirWithSpaces().getCanonicalPath() + '\\' + "cvs.exe");
    TestHelper.setSourceControlProperty(TestHelper.TEST_CVS_VALID_BUILD_ID, SourceControlSetting.CVS_REPOSITORY_PATH, STRING_SOURCE_LINE_ONE + "/spaced name");
  }


  private CVSSourceControl makeCVSSourceControl(final int buildID) throws Exception {
    final BuildConfig buildConfig = cm.getBuildConfiguration(buildID);
    assertNotNull(buildConfig);
    return new CVSSourceControl(buildConfig);
  }


  private static String makeCVSNTExePath() {
    return System.getProperty("test.cvsnt.home") + "\\cvs.exe";
  }


  protected void setUp() throws Exception {
    super.setUp();
    this.cvs = makeCVSSourceControl(getTestBuildID());
    this.cvs.setAgentHost(agentHost);
    assertEquals(SystemProperty.UNLIMITED_INITIAL_NUMBER_OF_CHANGELISTS, this.cvs.initialNumberOfChangeLists());
    IoUtils.deleteFileHard(testDirWithSpaces());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestCVSSourceControl.class,
            new String[]{
                    "test_syncToChangeList",
                    "test_bug897_doesNotHangOnNoPasswordCVSNT",
                    "test_bug897_doesNotHangOnNoPassword",
                    "test_bug892_passwordAccessCVSNT",
                    "test_bug892_canNotAccessWithWrongPasswordCVSNT",
                    "test_picksRelativeBuildDir",
                    "test_getChangesSinceithSpacesInPathToCVSAndProject",
                    "test_checkoutLatestWithSpacesInPathToCVSAndProject",
                    "test_getChangesSince",
                    "test_getChangesSinceCanUseHistory",
                    "test_getUsersMap",
                    "test_checkoutLatest",
                    "test_bug251_returnsTheSameIDAfterFirstTime",
                    "test_checkOutMultilineRepositoryPath"
            });
  }
}
