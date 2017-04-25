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
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.SystemProperty;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Tests SVNSourceControl
 * @noinspection InstanceMethodNamingConvention,JUnitTestMethodWithNoAssertions,HardcodedLineSeparator,LocalVariableHidesMemberVariable,SimplifiableJUnitAssertion
 */
public final class SSTestSVNVersionControl extends AbstractSourceControlTest {

  private static final Log LOG = LogFactory.getLog(SSTestSVNVersionControl.class); // NOPMD

  private static final String STRING_SOURCE_LINE_ONE = "test/sourceline/alwaysvalid";
  private static final String STRING_SOURCE_LINE_TWO = "test/second_sourceline/src";

  private SVNSourceControl svn = null;
  private static final int TEST_CHANGE_LIST_ID = 12;
  private static final String TEST_SPACED_URL = "svn://localhost:11111/test/spaced source line";


  public SSTestSVNVersionControl(final String s) {
    super(s);
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSince() throws Exception {
    // test that we get changes
    final int lastChangeListID = svn.getChangesSince(TEST_CHANGE_LIST_ID);
    assertTrue(lastChangeListID != TEST_CHANGE_LIST_ID);

    // test fix #516 - that we dont' leave temp files.

    // temp dir is empty
    IoUtils.emptyDir(TestHelper.JAVA_TEMP_DIR);
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);
    final int newChangeListID = svn.getChangesSince(lastChangeListID);

    // no new changes
    assertEquals(lastChangeListID, newChangeListID);

    // temp dir is empty
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);
  }


  /**
   * Tests getting change list
   * <p/>
   * See bug #1189 -  Suversion cannot handle URLs with spaces
   */
  public void test_getChangesSinceHandlesURLWithSpaces() throws Exception {
    // test that we get changes
    TestHelper.setSourceControlProperty(getTestBuildID(), SourceControlSetting.SVN_URL, TEST_SPACED_URL);
    TestHelper.setSourceControlProperty(getTestBuildID(), SourceControlSetting.SVN_DEPOT_PATH, "alwaysvalid");
    svn.reloadConfiguration();
    final int lastChangeListID = svn.getChangesSince(TEST_CHANGE_LIST_ID);
    assertTrue(lastChangeListID != TEST_CHANGE_LIST_ID);

    // no new changes
    final int newChangeListID = svn.getChangesSince(lastChangeListID);
    assertEquals(lastChangeListID, newChangeListID);
  }


  /**
   * Tests getting change list
   * <p/>
   * See bug #1189 -  Suversion cannot handle URLs with spaces
   */
  public void test_getChangesSinceHandlesURLWithSpacesAndDepotPathWithSpaces() throws Exception {
    // test that we get changes
    TestHelper.setSourceControlProperty(getTestBuildID(), SourceControlSetting.SVN_URL, TEST_SPACED_URL);
    TestHelper.setSourceControlProperty(getTestBuildID(), SourceControlSetting.SVN_DEPOT_PATH, "alwaysvalid/spaced src");
    svn.reloadConfiguration();
    final int lastChangeListID = svn.getChangesSince(TEST_CHANGE_LIST_ID);
    assertTrue(lastChangeListID != TEST_CHANGE_LIST_ID);

    // no new changes
    final int newChangeListID = svn.getChangesSince(lastChangeListID);
    assertEquals(lastChangeListID, newChangeListID);
  }


  public void test_getChangesSinceHandlesFirstRun() throws Exception {
    final int changeListID = svn.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);
  }


  /**
   * Tests label method.
   */
  public void test_label() throws BuildException {
    // TODO: implement
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_bug251_returnsTheSameIDAfterFirstTime() throws Exception {
    final int changeListID = svn.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID != ChangeList.UNSAVED_ID);
    assertEquals(changeListID, svn.getChangesSince(changeListID));
  }


  public void test_checkoutLatest() throws Exception {
    assertTrue("Build logs home dir is not empty", agent.emptyLogDir());
    svn.checkoutLatest();
    assertTrue("Build logs home dir is not empty", agent.emptyLogDir());
  }


  /**
   * Tests getting change list
   * <p/>
   * See bug #1189 -  Suversion cannot handle URLs with spaces
   */
  public void test_checkoutLatestHandlesURLAndPathWithSpaces() throws Exception {
    TestHelper.setSourceControlProperty(getTestBuildID(), SourceControlSetting.SVN_URL, TEST_SPACED_URL);
    TestHelper.setSourceControlProperty(getTestBuildID(), SourceControlSetting.SVN_DEPOT_PATH, "alwaysvalid/spaced src");
    svn.reloadConfiguration();
    assertTrue("Build logs home dir is not empty", agent.emptyLogDir());
    svn.checkoutLatest();
    assertTrue("Build logs home dir is not empty", agent.emptyLogDir());
  }


  public void test_checkOutLatestBranch() throws Exception {
    // do nothing, SVN does not support branch tags.
  }


  public void test_syncToChangeListBranch() throws Exception {
    // do nothing, SVN does not support branch tags.
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceInBranch() throws Exception {
    // do nothing, SVN does not support branch tags.
  }


  public void test_checkOutLatestCatchesConfigUpdates() throws Exception {
    svn.checkoutLatest();
    final String oldRelativeBuildDir = TestHelper.assertCurrentBuildPathExists(svn, agent);

    // update property
    alterSVNPathToSecondSourceLine();

    // call sync
    svn.reloadConfiguration();
    svn.checkoutLatest();

    TestHelper.assertOldBuildPathGoneAndNewAppeared(svn, oldRelativeBuildDir, agent);
  }


  public void test_checkOutMultilineRepositoryPath() throws Exception {
    // cleanup build logs dir
    assertTrue(agent.emptyLogDir());

    // cleanup temp password dir
    assertTrue(agent.emptyPasswordDir());

    // alter
    final String multilineSourceLine = new StringBuffer(100).append(STRING_SOURCE_LINE_ONE).append('\n').append(STRING_SOURCE_LINE_TWO).toString();
    final SourceControlSetting path = cm.getSourceControlSetting(svn.getBuildID(), SourceControlSetting.SVN_DEPOT_PATH);
    path.setPropertyValue(multilineSourceLine);
    cm.saveObject(path);

    // sync w/reload
    svn.reloadConfiguration();
    svn.checkoutLatest();

    // assert
    TestHelper.assertPathsEqual(STRING_SOURCE_LINE_ONE, svn.getRelativeBuildDir());
    TestHelper.assertDirIsNotEmpty(agent, STRING_SOURCE_LINE_ONE);
    TestHelper.assertDirIsNotEmpty(agent, STRING_SOURCE_LINE_TWO);

    // no logs /passwds left
    assertTrue(agent.logDirIsEmpty());
    assertTrue(agent.passwordDirIsEmpty());
  }


  private void alterSVNPathToSecondSourceLine() {
    final SourceControlSetting path = cm.getSourceControlSetting(svn.getBuildID(), SourceControlSetting.SVN_DEPOT_PATH);
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

    final List settings = new ArrayList(1);
    settings.add(makeSetting(SourceControlSetting.SVN_PATH_TO_EXE, "svn"));
    settings.add(makeSetting(SourceControlSetting.SVN_URL, TestHelper.SVN_VALID_URL));
    settings.add(makeSetting(SourceControlSetting.SVN_DEPOT_PATH, TestHelper.SVN_INVALID_DEPOT_PATH));
    final SVNSourceControl svn = makeSVNSourceControlWithAlteredSettings(settings);
    svn.setAgentHost(agentHost);
    try {
      svn.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
    }
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source
   * line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessInavalidUser() throws Exception {

    final List settings = new ArrayList(1);
    settings.add(makeSetting(SourceControlSetting.SVN_PATH_TO_EXE, "svn"));
    settings.add(makeSetting(SourceControlSetting.SVN_URL, TestHelper.SVN_VALID_URL));
    settings.add(makeSetting(SourceControlSetting.SVN_DEPOT_PATH, TestHelper.SVN_INVALID_DEPOT_PATH));
    settings.add(makeSetting(SourceControlSetting.SVN_USER, TestHelper.SVN_INVALID_USER));
    final SVNSourceControl svn = makeSVNSourceControlWithAlteredSettings(settings);
    svn.setAgentHost(agentHost);
    try {
      svn.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
    }
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source
   * line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessUnknownHost() throws Exception {
    final List settings = new ArrayList(1);
    settings.add(makeSetting(SourceControlSetting.SVN_PATH_TO_EXE, "svn"));
    settings.add(makeSetting(SourceControlSetting.SVN_URL, TestHelper.SVN_INVALID_HOST_URL));
    settings.add(makeSetting(SourceControlSetting.SVN_DEPOT_PATH, TestHelper.SVN_VALID_DEPOT_PATH));
    final SVNSourceControl svn = makeSVNSourceControlWithAlteredSettings(settings);
    svn.setAgentHost(agentHost);
    try {
      svn.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
    }
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source
   * line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessUnknownPort() throws Exception {
    final List settings = new ArrayList(1);
    settings.add(makeSetting(SourceControlSetting.SVN_PATH_TO_EXE, "svn"));
    settings.add(makeSetting(SourceControlSetting.SVN_URL, TestHelper.SVN_INVALID_PORT_URL));
    settings.add(makeSetting(SourceControlSetting.SVN_DEPOT_PATH, TestHelper.SVN_VALID_DEPOT_PATH));
    final SVNSourceControl svn = makeSVNSourceControlWithAlteredSettings(settings);
    svn.setAgentHost(agentHost);
    try {
      svn.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
    }
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListInitsEmptyCheckoutDir() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);
//    svn.setLocalCopyInitialized(true);
    svn.syncToChangeList(TEST_CHANGE_LIST_ID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeList() throws Exception {
    // we expect that there is change list in the test/config/dataset.xml
    svn.syncToChangeList(TEST_CHANGE_LIST_ID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertDirIsNotEmpty(agent, svn.getRelativeBuildDir());
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListWithNonRecursivePath() throws Exception {
    // we expect that there is change list in the test/config/dataset.xml
    svn.syncToChangeList(TEST_CHANGE_LIST_ID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertDirIsNotEmpty(agent, svn.getRelativeBuildDir());
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListPicksUpConfigChanges() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);

    // we expect that there is change list in the test/config/dataset.xml
    svn.syncToChangeList(TEST_CHANGE_LIST_ID);
    final String oldRelativeBuildDir = TestHelper.assertCurrentBuildPathExists(svn, agent);

    // update property
    alterSVNPathToSecondSourceLine();

    // reload
    svn.reloadConfiguration();
    // NOTE: vimeshev - 03/06/2005 - this changelist id translates
    // into svn change list #3 that contains second source line.
    svn.syncToChangeList(14);

    // assert
    TestHelper.assertOldBuildPathGoneAndNewAppeared(svn, oldRelativeBuildDir, agent);
  }


  /**
   * Tests that getting change list from empty source line
   * returns change list on creation this source line.
   *
   * @throws Exception
   */
  public void test_getChangesSinceDoesntFailOnBlankSourceline() throws Exception {
    final SVNSourceControl svnWithEmptySourceLine = makeSVNSourceControl(TestHelper.TEST_SVN_EMPTY_BUILD_ID);
    svnWithEmptySourceLine.setAgentHost(agentHost);
    final int changeListID = svnWithEmptySourceLine.getChangesSince(TEST_CHANGE_LIST_ID);
    final ChangeList changeList = cm.getChangeList(changeListID);
    assertTrue(changeListID != TEST_CHANGE_LIST_ID);
    assertEquals("4", changeList.getNumber());
    assertTrue(changeList.getDescription().startsWith("Added always empty source line"));
  }


  /**
   * Tests can not access passworded source line with wrong
   * password
   */
  public void test_canNotAccessWithWrongPassword() throws Exception {
    final List settings = new ArrayList(1);
    settings.add(makeSetting(SourceControlSetting.SVN_PATH_TO_EXE, "svn"));
    settings.add(makeSetting(SourceControlSetting.SVN_URL, TestHelper.SVN_VALID_URL));
    settings.add(makeSetting(SourceControlSetting.SVN_DEPOT_PATH, TestHelper.SVN_VALID_DEPOT_PATH));
    settings.add(makeSetting(SourceControlSetting.SVN_USER, TestHelper.SVN_VALID_USER));
    settings.add(makeSetting(SourceControlSetting.SVN_PASSWORD, TestHelper.SVN_INVALID_PASSWORD));
    final SVNSourceControl svnWithInvalidPassword = makeSVNSourceControlWithAlteredSettings(settings);
    svnWithInvalidPassword.setAgentHost(agentHost);
    try {
      svnWithInvalidPassword.checkoutLatest();
      // REVIEWME: simeshev@parabuilci.org -> uncomment when test SVN server does not allow wrong passwords.
      // TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
    }
  }


  public void test_getUsersMap() throws Exception {
    // just check it is not null for now.
    final Map resut = svn.getUsersMap();
    assertNotNull(resut);
  }


  public final int getTestBuildID() {
    return TestHelper.TEST_SVN_VALID_BUILD_ID;
  }


  /**
   * Thats that source line existance for SVN is checked
   * including existance of system ./.svn
   */
  public void test_issue315_missngSVNSystemFilesDetected() throws Exception {
    // sync
    svn.syncToChangeList(TEST_CHANGE_LIST_ID);

    // delete a file (.svn)
    final String relativeBuildDir = TestHelper.assertCurrentBuildPathExists(svn, agent);
    final String svnSystemDir = relativeBuildDir + File.separator + ".svn";
    if (LOG.isDebugEnabled()) {
      LOG.debug("svnSystemDir = " + svnSystemDir);
    }
    TestHelper.assertExists(svnSystemDir, agent);

    agent.deleteFileUnderCheckoutDir(svnSystemDir);
    TestHelper.assertNotExists(agent, svnSystemDir);

    // sync again
    svn.syncToChangeList(TEST_CHANGE_LIST_ID);
    TestHelper.assertExists(agent, svnSystemDir);
  }


  public void test_getSyncCommandNote() {
    assertNotNull(svn.getSyncCommandNote(TEST_CHANGE_LIST_ID));
    assertTrue(!svn.getSyncCommandNote(TEST_CHANGE_LIST_ID).equals(AbstractSourceControl.STRING_NO_SYNC_NOTE_AVAILABLE));
  }


  public void test_isBuildDirInitialized() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);
    assertEquals("Build dir should be uninitialized", false, svn.isBuildDirInitialized());
    svn.syncToChangeList(TEST_CHANGE_LIST_ID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
    assertEquals("Build dir should be initialized", true, svn.isBuildDirInitialized());
  }


  public void test_getNativeChangeList() throws Exception {
    assertTrue(svn.getNativeChangeList("1") != ChangeList.UNSAVED_ID);
    assertTrue(svn.getNativeChangeList("2") != ChangeList.UNSAVED_ID);
  }


  public void test_getNativeChangeListFailsForNonExistingChangeList() throws Exception {
    final String nativeChangeListNumber = "99999999";
    try {
      svn.getNativeChangeList(nativeChangeListNumber);
    } catch (Exception e) {
      assertTrue(e.toString().indexOf("No such revision " + nativeChangeListNumber) >= 0);
    }
  }


  /**
   * Shortcut to test helper's makeSourceControlSetting
   */
  private SourceControlSetting makeSetting(final String name, final String value) {
    return TestHelper.makeSourceControlSetting(name, value);
  }


  private SVNSourceControl makeSVNSourceControl(final int buildID)  {
    final BuildConfig buildConfig = cm.getBuildConfiguration(buildID);
    assertNotNull(buildConfig);
    return new SVNSourceControl(buildConfig);
  }


  private SVNSourceControl makeSVNSourceControlWithAlteredSettings(final List settings) {
    return new SVNSourceControl(cm.getBuildConfiguration(TestHelper.TEST_SVN_VALID_BUILD_ID), settings);
  }


  protected final void setUp() throws Exception {
    super.setUp();
    this.svn = makeSVNSourceControl(getTestBuildID());
    this.svn.setAgentHost(agentHost);

    // set in super's setUp
    assertEquals(SystemProperty.UNLIMITED_INITIAL_NUMBER_OF_CHANGELISTS, this.svn.initialNumberOfChangeLists());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestSVNVersionControl.class,
            new String[]{
                    "test_getNativeChangeListFailsForNonExistingChangeList",
                    "test_getNativeChangeList",
                    "test_getChangesSince",
                    "test_getUsersMap",
                    "test_checkoutLatest",
                    "test_checkOutLatestCatchesConfigUpdates",
                    "test_checkOutMultilineRepositoryPath"
            });
  }


  public final String toString() {
    return "SSTestSVNVersionControl{" +
            "svn=" + svn +
            "} " + super.toString();
  }
}
