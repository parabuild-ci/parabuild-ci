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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.build.BuildLabelNameGenerator;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.remote.internal.LocalAgentEnvironment;


/**
 * Tests VSSSourceControl
 */
public final class SSTestVSSSourceControl extends AbstractSourceControlTest {

  private static final Log log = LogFactory.getLog(SSTestVSSSourceControl.class);

  //private static final String TEST_BRANCH_NAME = "test_branch";
  private static final String TEST_INVALID_PROJECT_PATH = "never_existed";
  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final String TEST_EMPTY_PROJECT_PATH = "test/sourceline/alwaysempty";
  private static final String TEST_INVALID_USER = "unexistant_user";
  private static final String TEST_SOURCE_LINE_ONE = "test/sourceline/alwaysvalid";
  private static final String TEST_SOURCE_LINE_TWO = "test/second_sourceline/src";
  private static final String TEST_VALID_DATABASE_PATH = "..\\..\\..\\..\\..\\..\\..\\..\\test_temp\\test_vss_db";
  private static final String TEST_VALID_EXE_PATH = "SS.EXE";
  private static final String TEST_VALID_USER = "test";
  public static final String STR_VSS_ROOT = "$/";


  private VSSSourceControl vss = null;
  public static final int TEST_CHANGELIST_ID = 11;


  public SSTestVSSSourceControl(final String s) {
    super(s);
  }


  public void test_checkoutLatest() throws Exception {
    assertTrue("Build logs home dir is not empty", agent.emptyLogDir());
    vss.checkoutLatest();
    TestHelper.assertCheckoutDirNotEmpty(agent);
    assertTrue("Build logs home dir is not empty", agent.emptyLogDir());
  }


  /**
   * Tests that can checkout latest of both path to SS.ESE
   * and project path have spaces.
   */
  public void test_checkoutLatestWithRootProject() throws Exception {
    TestHelper.setSourceControlProperty(TestHelper.TEST_VSS_VALID_BUILD_ID, SourceControlSetting.VSS_PROJECT_PATH, STR_VSS_ROOT);
    TestHelper.setSourceControlProperty(TestHelper.TEST_VSS_VALID_BUILD_ID, SourceControlSetting.VSS_USER, "test");
    TestHelper.setSourceControlProperty(TestHelper.TEST_VSS_VALID_BUILD_ID, SourceControlSetting.VSS_PASSWORD, org.parabuild.ci.security.SecurityManager.encryptPassword("test_password"));
    vss.reloadConfiguration();
    vss.checkoutLatest();
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  /**
   * Tests that can checkout latest of both path to SS.ESE
   * and project path have spaces.
   */
  public void test_832_checkoutLatest() throws Exception {
    assertTrue("Build logs home dir is not empty", agent.emptyLogDir());
    IoUtils.copyDirectory(new File(TestHelper.getTestDataDir(), "vss_bin"), testDirWithSpaces());
    TestHelper.setSourceControlProperty(TestHelper.TEST_VSS_VALID_BUILD_ID, SourceControlSetting.VSS_EXE_PATH, testDirWithSpaces().getCanonicalPath() + '\\' + "SS.EXE");
    vss.reloadConfiguration();
    vss.checkoutLatest();
    TestHelper.assertCheckoutDirNotEmpty(agent);
    assertTrue("Build logs home dir is not empty", agent.emptyLogDir());
  }


  /**
   * Tests that can checkout latest of both path to SS.ESE
   * and project path have spaces.
   */
  public void test_832_checkoutLatestBothWithSpaces() throws Exception {
    IoUtils.copyDirectory(new File(TestHelper.getTestDataDir(), "vss_bin"), testDirWithSpaces());
    TestHelper.setSourceControlProperty(TestHelper.TEST_VSS_VALID_BUILD_ID, SourceControlSetting.VSS_EXE_PATH, testDirWithSpaces().getCanonicalPath() + '\\' + "SS.EXE");
    TestHelper.setSourceControlProperty(TestHelper.TEST_VSS_VALID_BUILD_ID, SourceControlSetting.VSS_PROJECT_PATH, TEST_SOURCE_LINE_ONE + "/spaced name");
    vss.reloadConfiguration();
    try {
      vss.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      assertTrue(e.toString().indexOf("test/sourceline/alwaysvalid/spaced name is not an existing filename or project") >= 0);
    }
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSince() throws Exception {
    // 1. make sure changes are detected
    final int changeListID = vss.getChangesSince(TEST_CHANGELIST_ID);
    assertTrue(changeListID != TEST_CHANGELIST_ID);

    // 2. make there are no changes detected after the latest
    assertEquals(changeListID, vss.getChangesSince(changeListID));
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceHandlesFirstRun() throws Exception {
    final int changeListID = vss.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceHandlesRootPath() throws Exception {
    TestHelper.setSourceControlProperty(TestHelper.TEST_VSS_VALID_BUILD_ID, SourceControlSetting.VSS_PROJECT_PATH, STR_VSS_ROOT);
    vss.reloadConfiguration();
    final int changeListID = vss.getChangesSince(TEST_CHANGELIST_ID);
    assertTrue(changeListID != TEST_CHANGELIST_ID);
  }


  /**
   * Tests getting change list with checkin window enabled.
   *
   * @throws Exception
   */
  public void test_getChangesSinceWithCheckinWindowOn() throws Exception {
    final SourceControlSetting changeWindow = cm.getSourceControlSetting(vss.getBuildID(),
            SourceControlSetting.VSS_CHANGE_WINDOW);
    changeWindow.setPropertyValue("10"); // 10 secs
    cm.saveObject(changeWindow);

    int changeListID = vss.getChangesSince(TEST_CHANGELIST_ID);
    assertTrue(changeListID != TEST_CHANGELIST_ID);
    changeListID = vss.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);
  }


  public void test_checkOutLatestBranch() throws Exception {
// REVIEWME: simeshev@parabuilci.org -> uncomment when implemented or delete if branches don't require special handling
//    String branchedFile = TEST_SOURCE_LINE_ONE + "/src/readme_in_test_branch.txt";
//    String fileInBranch = TEST_SOURCE_LINE_ONE + "/src/readme.txt";
//
//    // sync  normally
//    vss.checkoutLatest();
//
//    // make sure there is no branch
//    TestHelper.assertNotExists(agent, branchedFile);
//    TestHelper.assertExists(agent, fileInBranch);
//
//    // alter branch setting
//    SourceControlSetting branch = new SourceControlSetting();
//    branch.setBuildID(vss.getBuildID());
//    branch.setPropertyName(SourceControlSetting.VSS_BRANCH_NAME);
//    branch.setPropertyValue(TEST_BRANCH_NAME);
//    cm.saveObject(branch);
//    vss.reloadConfiguration();
//
//    // re-sync and assert
//    vss.checkoutLatest();
//    TestHelper.assertExists(agent, branchedFile);
//    TestHelper.assertExists(agent, fileInBranch);
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
// REVIEWME: simeshev@parabuilci.org -> implemented or leave blank if branches don't require special handling
  }


  public void test_checkOutLatestCatchesConfigUpdates() throws Exception {
    vss.checkoutLatest();
    final String oldRelativeBuildDir = TestHelper.assertCurrentBuildPathExists(vss, agent);

    // update property
    alterVSSPathToSecondSourceLine();

    // call sync
    vss.reloadConfiguration();
    vss.checkoutLatest();

    TestHelper.assertOldBuildPathGoneAndNewAppeared(vss, oldRelativeBuildDir, agent);
  }


  public void test_checkOutLatestCanProcessRootPaths() throws Exception {
    // REVIEWME: simeshev@parabuilci.org -> fails possible because test doesn't have access to root
    //agent.emptyCheckoutDir();
    //alterVSSPath(STR_VSS_ROOT);
    //vss.reloadConfiguration();
    //vss.checkoutLatest();
    //assertTrue(agent.fileRelativeToCheckoutDirExists(TEST_SOURCE_LINE_ONE));
    //assertTrue(agent.fileRelativeToCheckoutDirExists(TEST_SOURCE_LINE_TWO));
  }


  public void test_checkoutDirIsCleanable() throws Exception {
    vss.checkoutLatest();
    assertTrue(!agent.checkoutDirIsEmpty());

    agent.emptyCheckoutDir();
    assertTrue(agent.checkoutDirIsEmpty());
  }


  public void test_checkOutMultilineRepositoryPath() throws Exception {
    // cleanup build logs dir
    assertTrue(agent.emptyLogDir());

    // cleanup temp password dir
    assertTrue(agent.emptyPasswordDir());

    // alter
    final String multilineSourceLine = STR_VSS_ROOT + TEST_SOURCE_LINE_ONE + '\n' + STR_VSS_ROOT + TEST_SOURCE_LINE_TWO;
    final SourceControlSetting path = cm.getSourceControlSetting(vss.getBuildID(), SourceControlSetting.VSS_PROJECT_PATH);
    path.setPropertyValue(multilineSourceLine);
    cm.saveObject(path);

    // sync w/reload
    vss.reloadConfiguration();
    vss.checkoutLatest();

    // assert
    assertPathsEqual(TEST_SOURCE_LINE_ONE, vss.getRelativeBuildDir());
    TestHelper.assertDirIsNotEmpty(agent, TEST_SOURCE_LINE_ONE);
    TestHelper.assertDirIsNotEmpty(agent, TEST_SOURCE_LINE_TWO);

    // no logs /passwds left
    assertTrue(agent.logDirIsEmpty());
    assertTrue(agent.passwordDirIsEmpty());
  }


  public static void assertPathsEqual(final String path1, final String path2) {
    assertTrue(new File(path1).equals(new File(path2)));
  }


  private void alterVSSPathToSecondSourceLine() {
    TestHelper.setSourceControlProperty(TestHelper.TEST_VSS_VALID_BUILD_ID, SourceControlSetting.VSS_PROJECT_PATH, STR_VSS_ROOT + TEST_SOURCE_LINE_TWO);
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source
   * line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessUnexistingSourceLine() throws Exception {
    final List settings = new ArrayList(11);
    settings.add(makeSetting(SourceControlSetting.VSS_DATABASE_PATH, TEST_VALID_DATABASE_PATH));
    settings.add(makeSetting(SourceControlSetting.VSS_EXE_PATH, TEST_VALID_EXE_PATH));
    settings.add(makeSetting(SourceControlSetting.VSS_PROJECT_PATH, TEST_INVALID_PROJECT_PATH));
    settings.add(makeSetting(SourceControlSetting.VSS_PASSWORD, TestHelper.VSS_VALID_PASSWORD));
    settings.add(makeSetting(SourceControlSetting.VSS_USER, TEST_VALID_USER));
    final VSSSourceControl vss = makeVSSSourceControlWithAlteredSettings(settings);
    boolean thrown = false;
    try {
      vss.checkoutLatest();
    } catch (BuildException e) {
      if (log.isDebugEnabled()) log.debug("e = " + e);
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
    final List settings = new ArrayList(11);
    settings.add(makeSetting(SourceControlSetting.VSS_DATABASE_PATH, TEST_VALID_DATABASE_PATH));
    settings.add(makeSetting(SourceControlSetting.VSS_EXE_PATH, TEST_VALID_EXE_PATH));
    settings.add(makeSetting(SourceControlSetting.VSS_PROJECT_PATH, TEST_INVALID_PROJECT_PATH));
    settings.add(makeSetting(SourceControlSetting.VSS_PASSWORD, TestHelper.VSS_VALID_PASSWORD));
    settings.add(makeSetting(SourceControlSetting.VSS_USER, TEST_INVALID_USER));
    final VSSSourceControl vss = makeVSSSourceControlWithAlteredSettings(settings);
    try {
      vss.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected.
    }
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListInitsEmptyCheckoutDir() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);
//    vss.setLocalCopyInitialized(true);
    vss.syncToChangeList(TEST_CHANGELIST_ID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  /**
   * Tests label method.
   */
  public void test_label() throws BuildException, CommandStoppedException, AgentFailureException {
    final BuildLabelNameGenerator nameGenerator = new BuildLabelNameGenerator();
    nameGenerator.setBuildName(cm.getBuildConfiguration(vss.getBuildID()).getBuildName());
    nameGenerator.setBuildNumber(cm.getNewBuildNumber(vss.getBuildID()));
    nameGenerator.setChangeListNumber("9999");
    nameGenerator.setBuildTimestamp(new Date());
    nameGenerator.setLabelTemplate("${build.name}_${build.timestamp}");
    final String labelName = nameGenerator.generateLabelName();
    if (log.isDebugEnabled()) log.debug("labelName = " + labelName);
    vss.syncToChangeList(TEST_CHANGELIST_ID); // need to sync
    vss.label(labelName);

    // label again with the same label - no exception should be thrown
    vss.label(labelName);

    // label again with dif label - no exception should be thrown
    nameGenerator.setLabelTemplate("${build.name}_${build.number}_${build.timestamp}");
    vss.label(nameGenerator.generateLabelName());

    // TODO: check that the label was created?
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeList() throws Exception {
    vss.syncToChangeList(TEST_CHANGELIST_ID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertDirIsNotEmpty(agent, vss.getRelativeBuildDir());
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListPicksUpConfigChanges() throws Exception {
    // we expect that there is change list in the test/config/dataset.xml
    vss.syncToChangeList(TEST_CHANGELIST_ID);
    final String oldRelativeBuildDir = TestHelper.assertCurrentBuildPathExists(vss, agent);

    // update property
    alterVSSPathToSecondSourceLine();

    // reload
    vss.reloadConfiguration();
    vss.syncToChangeList(TEST_CHANGELIST_ID);

    // assert
    TestHelper.assertOldBuildPathGoneAndNewAppeared(vss, oldRelativeBuildDir, agent);
  }


  /**
   * Tests that getting change list from source line that doesn't
   * contain any changes but dirs
   *
   * @throws Exception
   */
  public void test_getChangesSinceDoesntFailOnBlankSourceline() throws Exception {
// REVIEWME: simeshev@parabuilci.org -> uncomment when in CVS there a path define by TEST_EMPTY_PROJECT_PATH
//    List settings = new ArrayList(11);
//    settings.add(makeSetting(SourceControlSetting.VSS_DATABASE_PATH, TEST_VALID_DATABASE_PATH));
//    settings.add(makeSetting(SourceControlSetting.VSS_EXE_PATH, TEST_VALID_EXE_PATH));
//    settings.add(makeSetting(SourceControlSetting.VSS_PROJECT_PATH, TEST_EMPTY_PROJECT_PATH));
//    settings.add(makeSetting(SourceControlSetting.VSS_PASSWORD, TEST_VALID_PASSWORD));
//    settings.add(makeSetting(SourceControlSetting.VSS_USER, TEST_VALID_USER));
//    VSSSourceControl vss = makeVSSSourceControlWithAlteredSettings(settings);
//    int changeListID = vss.getChangesSince(TEST_CHANGELIST_ID);
//    assertEquals(TEST_CHANGELIST_ID, changeListID);
  }


  /**
   * Tests can not access passworded source line with wrong
   * password
   */
  public void test_canNotAccessWithWrongPassword() throws Exception {
    final List settings = new ArrayList(11);
    settings.add(makeSetting(SourceControlSetting.VSS_DATABASE_PATH, TEST_VALID_DATABASE_PATH));
    settings.add(makeSetting(SourceControlSetting.VSS_EXE_PATH, TEST_VALID_EXE_PATH));
    settings.add(makeSetting(SourceControlSetting.VSS_PROJECT_PATH, TEST_INVALID_PROJECT_PATH));
    settings.add(makeSetting(SourceControlSetting.VSS_PASSWORD, TestHelper.VSS_INVALID_PASSWORD));
    settings.add(makeSetting(SourceControlSetting.VSS_USER, TEST_VALID_USER));
    final VSSSourceControl vss = makeVSSSourceControlWithAlteredSettings(settings);
    try {
      vss.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  public void test_getUsersMap() throws Exception {
    final Map resut = vss.getUsersMap();
    assertNotNull(resut);
    assertEquals(0, resut.size());
  }


  public int getTestBuildID() {
    return TestHelper.TEST_VSS_VALID_BUILD_ID;
  }


  public void test_getSyncCommandNote() throws AgentFailureException {
    assertNotNull(vss.getSyncCommandNote(1));
  }


  public void test_getRelativeBuildDir() throws BuildException {
    TestHelper.setSourceControlProperty(TestHelper.TEST_VSS_VALID_BUILD_ID, SourceControlSetting.VSS_PROJECT_PATH, STR_VSS_ROOT);
    vss.reloadConfiguration();
    assertEquals("Relative build dire for root project should be empty",
            "", vss.getRelativeBuildDir());
  }


  /**
   * Tests that r/o checkout works.
   *
   * @throws org.parabuild.ci.util.CommandStoppedException
   * @throws BuildException
   * @throws IOException
   */
  public void test_cr889_readOnlyCheckout() throws CommandStoppedException, BuildException, IOException, AgentFailureException {
    vss.checkoutLatest();
    final String path = agent.getCheckoutDirName() + '/' + TEST_SOURCE_LINE_ONE + "/src/readme.txt";
    final File file = new File(path);
    assertTrue(file.canWrite());
    TestHelper.setSourceControlProperty(TestHelper.TEST_VSS_VALID_BUILD_ID,
            SourceControlSetting.VSS_READONLY_CHECKOUT, SourceControlSetting.OPTION_CHECKED);
    vss.reloadConfiguration();
    vss.checkoutLatest();
    assertTrue(!file.canWrite());
  }


  /**
   * Shortcut to test helper's makeSourceControlSetting
   */
  private SourceControlSetting makeSetting(final String name, final String value) {
    return TestHelper.makeSourceControlSetting(name, value);
  }


  private VSSSourceControl makeVSSSourceControlWithAlteredSettings(final List settings) {
    final VSSSourceControl vssSourceControl = new VSSSourceControl(cm.getBuildConfiguration(TestHelper.TEST_VSS_VALID_BUILD_ID), settings);
    vssSourceControl.setAgentHost(agentHost);
    return vssSourceControl;
  }


  private static File testDirWithSpaces() {
    return new File(TestHelper.getTestTempDir(), "test dir with spaces");
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    ErrorManagerFactory.getErrorManager().clearAllActiveErrors();
    final BuildConfig buildConfig = cm.getBuildConfiguration(getTestBuildID());
    this.vss = new VSSSourceControl(buildConfig);
    this.vss.setAgentHost(agentHost);

    // set in super's setUp
    assertEquals(SystemProperty.UNLIMITED_INITIAL_NUMBER_OF_CHANGELISTS, this.vss.initialNumberOfChangeLists());

    // test dataset.xml expects it at this position
    final File source = new File(TestHelper.getTestDataDir(), "vss");
    final File testDabase = new File(TestHelper.getTestTempDir(), "test_vss_db");
    testDabase.mkdirs();
    IoUtils.emptyDir(testDabase);
    IoUtils.copyDirectory(source, testDabase);

    // clean up test dirs
    IoUtils.deleteFileHard(testDirWithSpaces());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    final LocalAgentEnvironment environment = new LocalAgentEnvironment();
    if (!environment.isWindows()) return new TestSuite();
    return new OrderedTestSuite(SSTestVSSSourceControl.class,
            new String[]{
                    "test_cr889_readOnlyCheckout",
                    "test_checkoutLatestWithRootProject",
                    "test_832_checkoutLatest",
                    "test_getChangesSinceHandlesFirstRun",
                    "test_checkoutDirIsCleanable",
                    "test_getChangesSinceDoesntFailOnBlankSourceline",
                    "test_checkOutLatestCantProcessInavalidUser",
                    "test_canNotAccessWithWrongPassword",
                    "test_label",
                    "test_getRelativeBuildDir",
                    "test_syncToChangeListPicksUpConfigChanges",
                    "test_syncToChangeList",
                    "test_getChangesSince",
                    "test_checkoutLatest",
                    "test_getUsersMap",
                    "test_getChangesSinceHandlesRootPath",
                    "test_checkOutLatestCanProcessRootPaths",
            });
  }
}
