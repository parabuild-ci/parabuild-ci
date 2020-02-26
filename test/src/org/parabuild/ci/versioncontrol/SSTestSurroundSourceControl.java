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

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class SSTestSurroundSourceControl extends AbstractSourceControlTest {

  private static final Log log = LogFactory.getLog(SSTestSurroundSourceControl.class);
  private static final int TEST_CHANGE_LIST_ID = 1;

  private static final String STRING_SOURCE_LINE_ONE = "test/sourceline/alwaysvalid";
  private static final String STRING_SOURCE_LINE_TWO = "test/second_sourceline/src";

  private SurroundSourceControl surround;
  private static final String TEST_BRANCH = "test_branch";


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSince() throws Exception {
    // test that we get changes
    final int lastChangeListID = surround.getChangesSince(TEST_CHANGE_LIST_ID);
    assertTrue(lastChangeListID != TEST_CHANGE_LIST_ID);

    // test fix #516 - that we dont' leave temp files.

    // temp dir is empty
    IoUtils.emptyDir(TestHelper.JAVA_TEMP_DIR);
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);


    // no new changes ?
    final int newChangeListID = surround.getChangesSince(lastChangeListID);

    final ChangeList lastChangeList = cm.getChangeList(lastChangeListID);
    if (log.isDebugEnabled()) log.debug("lastChangeList = " + lastChangeList);
    final ChangeList newChangeList = cm.getChangeList(newChangeListID);
    if (log.isDebugEnabled()) log.debug("lastChangeList = " + newChangeList);

    assertEquals(lastChangeListID, newChangeListID);

    // temp dir is empty
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_bug997_getChangesSinceForRepositoryAndBranchWithSpacesAndQuotes() throws Exception {
    // alter config
    TestHelper.setSourceControlProperty(getTestBuildID(), VersionControlSystem.SURROUND_REPOSITORY, "\"test repository with spaces/Main\"");
    TestHelper.setSourceControlProperty(getTestBuildID(), VersionControlSystem.SURROUND_BRANCH, "\"test branch with spaces\"");
    surround.reloadConfiguration();

    // test that we get changes
    final int lastChangeListID = surround.getChangesSince(TEST_CHANGE_LIST_ID);
    assertTrue(lastChangeListID != TEST_CHANGE_LIST_ID);

    // test fix #516 - that we dont' leave temp files.

    // temp dir is empty
    IoUtils.emptyDir(TestHelper.JAVA_TEMP_DIR);
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);


    // no new changes ?
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final int newChangeListID = surround.getChangesSince(lastChangeListID);

    final ChangeList lastChangeList = cm.getChangeList(lastChangeListID);
    if (log.isDebugEnabled()) log.debug("lastChangeList = " + lastChangeList);
    final ChangeList newChangeList = cm.getChangeList(newChangeListID);
    if (log.isDebugEnabled()) log.debug("lastChangeList = " + newChangeList);

    assertEquals(lastChangeListID, newChangeListID);

    // temp dir is empty
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);
  }


  public void test_checkoutLatest() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    surround.checkoutLatest();
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  public void test_checkOutLatestBranch() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    // alter config to be branched
    final List settings = new ArrayList(11);
    settings.add(makeSetting(VersionControlSystem.SURROUND_REPOSITORY, "test/sourceline"));
    settings.add(makeSetting(VersionControlSystem.SURROUND_PATH_TO_EXE, "sscm"));
    settings.add(makeSetting(VersionControlSystem.SURROUND_BRANCH, TEST_BRANCH));
    settings.add(makeSetting(VersionControlSystem.SURROUND_USER, "test_user"));
    settings.add(makeSetting(VersionControlSystem.SURROUND_PORT, "4900"));
    settings.add(makeSetting(VersionControlSystem.SURROUND_HOST, "localhost"));
    settings.add(makeSetting(VersionControlSystem.SURROUND_PASSWORD, "973908CD78928E660B047F5DE5130BE0")); // no password
    final SurroundSourceControl otherBranch = makeSurroundSourceControlWithAlteredSettings(settings);
    otherBranch.checkoutLatest();
    TestHelper.assertCheckoutDirNotEmpty(agent);
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
    // REVIEWME: simeshev@parabuilci.org -> returning - cannot adde changes to branch
    if (true) return;
    // alter config to be branched
    final List settings = new ArrayList(11);
    settings.add(makeSetting(VersionControlSystem.SURROUND_REPOSITORY, "test/sourceline"));
    settings.add(makeSetting(VersionControlSystem.SURROUND_PATH_TO_EXE, "sscm"));
    settings.add(makeSetting(VersionControlSystem.SURROUND_BRANCH, TEST_BRANCH));
    settings.add(makeSetting(VersionControlSystem.SURROUND_USER, "test_user"));
    settings.add(makeSetting(VersionControlSystem.SURROUND_PORT, "4900"));
    settings.add(makeSetting(VersionControlSystem.SURROUND_HOST, "localhost"));
    settings.add(makeSetting(VersionControlSystem.SURROUND_PASSWORD, "973908CD78928E660B047F5DE5130BE0")); // no password
    final SurroundSourceControl otherBranch = makeSurroundSourceControlWithAlteredSettings(settings);
    // get changes
    final int changeListID = otherBranch.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID != ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);
    final ChangeList changeList = cm.getChangeList(changeListID);

    // make sure branch name is set
    assertEquals(TEST_BRANCH, changeList.getBranch());
  }


  public void test_checkOutLatestCatchesConfigUpdates() throws Exception {
    //REVIEWME: simeshev@parabuilci.org -> implement
  }


  public void test_checkOutMultilineRepositoryPath() throws Exception {
    // cleanup build logs dir
    assertTrue(agent.emptyLogDir());

    // cleanup temp password dir
    assertTrue(agent.emptyPasswordDir());

    // alter
    final String multilineSourceLine = STRING_SOURCE_LINE_ONE + '\n' + STRING_SOURCE_LINE_TWO;
    final SourceControlSetting path = cm.getSourceControlSetting(surround.getBuildID(), VersionControlSystem.SURROUND_REPOSITORY);
    path.setPropertyValue(multilineSourceLine);
    cm.saveObject(path);

    // sync w/reload
    surround.reloadConfiguration();
    surround.checkoutLatest();

    // assert
    TestHelper.assertPathsEqual(STRING_SOURCE_LINE_ONE, surround.getRelativeBuildDir());
    TestHelper.assertDirIsNotEmpty(agent, STRING_SOURCE_LINE_ONE);
    TestHelper.assertDirIsNotEmpty(agent, STRING_SOURCE_LINE_TWO);

    // no logs /passwds left
    assertTrue(agent.logDirIsEmpty());
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source
   * line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessUnexistingSourceLine() throws Exception {
    try {
      final List settings = new ArrayList(11);
      settings.add(makeSetting(VersionControlSystem.SURROUND_REPOSITORY, "test/sourceline/never_existed"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_PATH_TO_EXE, "sscm"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_BRANCH, "test"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_USER, "test_user"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_PORT, "4900"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_HOST, "localhost"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_PASSWORD, "973908CD78928E660B047F5DE5130BE0")); // no password
      makeSurroundSourceControlWithAlteredSettings(settings).getChangesSince(1);
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
    try {
      final List settings = new ArrayList(11);
      settings.add(makeSetting(VersionControlSystem.SURROUND_REPOSITORY, "test/sourceline/alwaysvalid"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_PATH_TO_EXE, "sscm"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_BRANCH, "test"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_USER, "test_user_never_existed"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_PORT, "4900"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_HOST, "localhost"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_PASSWORD, "973908CD78928E660B047F5DE5130BE0")); // no password
      makeSurroundSourceControlWithAlteredSettings(settings).getChangesSince(1);
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListInitsEmptyCheckoutDir() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);
//    surround.setLocalCopyInitialized(true);

    log.info("get latest");
    final int changeListID = surround.getChangesSince(ChangeList.UNSAVED_ID);
    log.info("get change list " + changeListID);
    final ChangeList newChangeList = cm.getChangeList(changeListID);
    log.debug("lastChangeList = " + newChangeList);

    // sync to latest
    surround.syncToChangeList(changeListID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeList() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);
    surround.syncToChangeList(surround.getChangesSince(ChangeList.UNSAVED_ID));
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListPicksUpConfigChanges() throws Exception {
    //REVIEWME: simeshev@parabuilci.org -> implement
  }


  /**
   * Tests that getting change list from source line that doesn't
   * contain any changes but dirs
   *
   * @throws Exception
   */
  public void test_getChangesSinceDoesntFailOnBlankSourceline() throws Exception {
    // NOTE: vimeshev - 08/18/2005 - current surround config
    // doesn't support "blank" source lines.
  }


  /**
   * Tests can not access passworded source line with wrong
   * password
   */
  public void test_canNotAccessWithWrongPassword() throws Exception {
    try {
      final List settings = new ArrayList(11);
      settings.add(makeSetting(VersionControlSystem.SURROUND_REPOSITORY, "test/sourceline/alwaysvalid"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_PATH_TO_EXE, "sscm"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_BRANCH, "test"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_USER, "test_user"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_PORT, "4900"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_HOST, "localhost"));
      settings.add(makeSetting(VersionControlSystem.SURROUND_PASSWORD, "")); // no password
      final SurroundSourceControl surroundSourceControlWithAlteredSettings = makeSurroundSourceControlWithAlteredSettings(settings);
      surroundSourceControlWithAlteredSettings.getChangesSince(1);

      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  public void test_getUsersMap() throws Exception {
    assertTrue(!surround.getUsersMap().isEmpty());
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceHandlesFirstRun() throws Exception {
    final int changeListID = surround.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);
  }


  /**
   * Tests label method.
   */
  public void test_label() throws BuildException, CommandStoppedException, AgentFailureException {
    surround.syncToChangeList(surround.getChangesSince(1));
    surround.label("test_label_" + Long.toString(System.currentTimeMillis()));
    // REVIEWME: simeshev@parabuilci.org -> add removing label
  }


  /*
   *
   * Required by JUnit.
   */
  public SSTestSurroundSourceControl(final String s) {
    super(s);
  }


  public int getTestBuildID() {
    return TestHelper.TEST_SURROUND_VALID_BUILD_ID;
  }


  /**
   * Shortcut to test helper's makeSourceControlSetting
   */
  private SourceControlSetting makeSetting(final String name, final String value) {
    return TestHelper.makeSourceControlSetting(name, value);
  }


  private SurroundSourceControl makeSurroundSourceControlWithAlteredSettings(final List settings) {
    return new SurroundSourceControl(cm.getBuildConfiguration(getTestBuildID()), settings);
  }


  protected void setUp() throws Exception {
    super.setUp();
    final BuildConfig buildConfig = cm.getBuildConfiguration(getTestBuildID());
    assertNotNull(buildConfig);
    this.surround = new SurroundSourceControl(buildConfig);
    this.surround.setAgentHost(agentHost);

    // set in super's setUp
    assertEquals(SystemProperty.UNLIMITED_INITIAL_NUMBER_OF_CHANGELISTS, this.surround.initialNumberOfChangeLists());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestSurroundSourceControl.class,
            new String[]{
                    "test_checkoutLatest",
                    "test_getChangesSince",
                    "test_checkoutLatest",
                    "test_canNotAccessWithWrongPassword",
                    "test_checkOutLatestCatchesConfigUpdates",
                    "test_syncToChangeListInitsEmptyCheckoutDir",
            });
  }
}
