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
package org.parabuild.ci.versioncontrol.perforce;

import com.gargoylesoftware.base.testing.OrderedTestSuite;
import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.configuration.TransactionCallback;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Tests P4SourceControl
 */
public class SSTestP4SourceControl extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestP4SourceControl.class);


  private static final String[] EMPTY_STRING_ARRAY = new String[]{};

  public static final int TEST_CHANGE_LIST_ID_4 = 4;
  public static final int TEST_CHANGE_LIST_ID_5 = 5;
  public static final int TEST_CHANGE_LIST_ID_10 = 10;
  public static final int TEST_CHANGE_LIST_ID_18 = 18;

  private static final String STRING_SOURCE_LINE_ONE = "//test/sourceline/alwaysvalid/...";
  private static final String STRING_SOURCE_LINE_TWO = "//test/second_sourceline/src/...";
  private static final String STRING_SOURCE_LINE_MULTY_1 = STRING_SOURCE_LINE_ONE + '\n' + STRING_SOURCE_LINE_TWO;
  private static final String STRING_SOURCE_LINE_MULTY_2 = STRING_SOURCE_LINE_TWO + '\n' + STRING_SOURCE_LINE_ONE;

  private static final String DEPOT_PATH_SOURCE = "//test/client_view/test_client_view.txt";
  private static final String DEPOT_PATH_SOURCE_2 = "//test/client_view/test_client_view2.txt";
  private static final String DEPOT_PATH_SOURCE_3 = "//test/client_view/test_generatedviewspec_bug_1375.txt";
  private static final String DEPOT_PATH_NONEXISTING_SOURCE = "//test/client_view/never_existsed/test_client_view2.txt";

  private ConfigurationManager cm = null;
  private ErrorManager errorManager = null;
  private Agent agent;
  protected P4SourceControl perforce = null;
  protected static final int TEST_BUILD_ID = TestHelper.TEST_P4_VALID_BUILD_ID;


  public SSTestP4SourceControl(final String s) {
    super(s);
  }


  public void test_zeroLengthRelativeDir() {
    final File zeroLengthFile = new File("");
    final File clientOfZeroLength = new File(TestHelper.getTestTempDir(), zeroLengthFile.toString());
    assertTrue(clientOfZeroLength.exists());
  }


  public void test_getRelativeBuildDir() throws Exception {
    assertEquals("sourceline/alwaysvalid", perforce.getRelativeBuildDir().replace('\\', '/'));
  }


  public void test_createClient() throws Exception {
    perforce.createOrUpdateClient();
  }


  public void test_createClientNoUNC() throws Exception {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_USE_UNC_PATHS, SourceControlSetting.OPTION_UNCHECKED);
    this.perforce.reloadConfiguration();
    final String clientSpec = this.perforce.createOrUpdateClient();
    assertEquals(-1, clientSpec.indexOf("\\\\"));
  }


  public void test_syncToChangeList() throws Exception {
    perforce.syncToChangeList(TEST_CHANGE_LIST_ID_4);
    TestHelper.assertCheckoutDirNotEmpty(agent);
    assertTrue("Checkout dir should be reported as inited", perforce.isBuildDirInitialized());
  }


  /**
   * Just makes sure nothing happened.
   *
   * @throws Exception
   */
  public void test_syncToChangeListWithClobber() throws Exception {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_CLOBBER_OPTION, Byte.toString(VersionControlSystem.P4_OPTION_VALUE_CLOBBER));
    perforce.reloadConfiguration();
    perforce.syncToChangeList(TEST_CHANGE_LIST_ID_10);
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  /**
   * Just makes sure nothing happened.
   *
   * @throws Exception
   */
  public void test_syncToChangeListWithDepotSourcePath() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_CLIENT_VIEW_SOURCE, Byte.toString(VersionControlSystem.P4_CLIENT_VIEW_SOURCE_VALUE_DEPOT_PATH));
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_CLIENT_VIEW_BY_DEPOT_PATH, DEPOT_PATH_SOURCE);
    perforce.reloadConfiguration();
    perforce.syncToChangeList(TEST_CHANGE_LIST_ID_10);
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertExists(agent, "second_sourceline/src/readme.txt");
  }


  public void test_syncsToChangeListWithDepotSourcePathBug1375() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_CLIENT_VIEW_SOURCE, Byte.toString(VersionControlSystem.P4_CLIENT_VIEW_SOURCE_VALUE_DEPOT_PATH));
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_CLIENT_VIEW_BY_DEPOT_PATH, DEPOT_PATH_SOURCE_3);
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_ADVANCED_VIEW_MODE, SourceControlSetting.OPTION_CHECKED);
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_RELATIVE_BUILD_DIR, "test");
    perforce.reloadConfiguration();
    perforce.syncToChangeList(TEST_CHANGE_LIST_ID_10);
    assertEquals(0, ErrorManagerFactory.getErrorManager().errorCount());
  }


  /**
   * Confirm that chages in the configuration are picked up.
   */
  public void test_syncToChangeListWithDepotSourcePathDetectsChangesInDepotPath() throws Exception {
    if (log.isDebugEnabled())
      log.debug("==================================== step 1 ================================= ");
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_CLIENT_VIEW_SOURCE, Byte.toString(VersionControlSystem.P4_CLIENT_VIEW_SOURCE_VALUE_DEPOT_PATH));
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_CLIENT_VIEW_BY_DEPOT_PATH, DEPOT_PATH_SOURCE);
    perforce.reloadConfiguration();
    perforce.syncToChangeList(TEST_CHANGE_LIST_ID_10);
    TestHelper.assertCheckoutDirNotEmpty(agent);
    // both exist
    TestHelper.assertExists(agent, "sourceline/alwaysvalid/src/readme.txt");
    TestHelper.assertExists(agent, "second_sourceline/src/readme.txt");

    if (log.isDebugEnabled())
      log.debug("==================================== step 2 ================================= ");
    // change
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_CLIENT_VIEW_BY_DEPOT_PATH, DEPOT_PATH_SOURCE_2);
    perforce.reloadConfiguration();
    perforce.syncToChangeList(TEST_CHANGE_LIST_ID_10);
    TestHelper.assertCheckoutDirNotEmpty(agent);
    // both do not exist
    TestHelper.assertNotExists(agent, "sourceline/alwaysvalid/src/readme.txt");
    TestHelper.assertNotExists(agent, "second_sourceline/src/readme.txt");
    // required exists according to defined in test_client_view2.txt
    TestHelper.assertExists(agent, "readme.txt");

    if (log.isDebugEnabled())
      log.debug("==================================== finish ================================= ");
  }


  /**
   * Just makes sure nothing happened.
   */
  public void test_syncToChangeListWithDepotSourcePathFailsOnNonExistingPath() throws CommandStoppedException {
    try {
      TestHelper.emptyCheckoutDir(agent);
      TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_CLIENT_VIEW_SOURCE, Byte.toString(VersionControlSystem.P4_CLIENT_VIEW_SOURCE_VALUE_DEPOT_PATH));
      TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_CLIENT_VIEW_BY_DEPOT_PATH, DEPOT_PATH_NONEXISTING_SOURCE);
      perforce.reloadConfiguration();
      perforce.syncToChangeList(TEST_CHANGE_LIST_ID_10);
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
      // expected
    }
  }


  public void test_getChangesSinceWithDepotSourcePath() throws Exception {
    // just use standard
    perforce.getChangesSince(TEST_CHANGE_LIST_ID_4);

    // alter
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_CLIENT_VIEW_SOURCE, Byte.toString(VersionControlSystem.P4_CLIENT_VIEW_SOURCE_VALUE_DEPOT_PATH));
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_CLIENT_VIEW_BY_DEPOT_PATH, DEPOT_PATH_SOURCE);
    perforce.reloadConfiguration();
    final int newChangeListID = perforce.getChangesSince(TEST_CHANGE_LIST_ID_4);
    assertTrue(newChangeListID != TEST_CHANGE_LIST_ID_4);
    assertNotNull(cm.getChangeList(newChangeListID));

    final int newNewChangeListID = perforce.getChangesSince(newChangeListID);
    assertEquals(newChangeListID, newNewChangeListID);
  }


  public void test_syncToChangeListMultiline() throws Exception {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_DEPOT_PATH, STRING_SOURCE_LINE_MULTY_1);
    perforce.reloadConfiguration();
    perforce.syncToChangeList(TEST_CHANGE_LIST_ID_10);
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertCurrentBuildPathExists(perforce, agent);

    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_DEPOT_PATH, STRING_SOURCE_LINE_MULTY_2);
    perforce.reloadConfiguration();
    perforce.syncToChangeList(TEST_CHANGE_LIST_ID_10);
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertCurrentBuildPathExists(perforce, agent);
  }


  public void test_bug721resyncsAfterCleanUpdates() throws Exception {
    // load ML condig with:
    //    1st - //test/second_sourceline/src/...
    //    2nd - //test/sourceline/alwaysvalid/...
    // 2nd has the latest change list #1072.
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_DEPOT_PATH, STRING_SOURCE_LINE_MULTY_2);
    perforce.reloadConfiguration();
    perforce.syncToChangeList(TEST_CHANGE_LIST_ID_18);
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertCurrentBuildPathExists(perforce, agent);
    TestHelper.assertExists("second_sourceline/src/readme.txt", agent);

    // attempt #2
    TestHelper.emptyCheckoutDir(agent);
    perforce.syncToChangeList(TEST_CHANGE_LIST_ID_18);
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertCurrentBuildPathExists(perforce, agent);
    TestHelper.assertExists("second_sourceline/src/readme.txt", agent);

//
//    TestHelper.setSourceControlProperty(TEST_BUILD_ID, SourceControlSetting.P4_DEPOT_PATH, STRING_SOURCE_LINE_MULTY_2);
//    perforce.reloadConfiguration();
//    perforce.syncToChangeList(TEST_CHANGE_LIST_ID_10);
//    TestHelper.assertCheckoutDirNotEmpty(testP4ValidBuildId);
//    TestHelper.assertCurrentBuildPathExists(perforce);
  }


  public void test_syncToUnexistingChangeNumber() throws Exception {
    // REVIEWME: simeshev@parabuilci.org -> there should be an exception thrown (may be
    // need to validate changelist via p4 describe)
    perforce.syncToChangeList(TEST_CHANGE_LIST_ID_5);
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  public void test_throwsExceptionIfChangeListNumberIsEmpty() throws Exception {
    try {
      perforce.syncToChangeList(6);
      TestHelper.failNoExceptionThrown();
    } catch (IllegalStateException e) {
      // expected
    }
  }


  public void test_syncToLatest() throws Exception {
    // clean up log dir
    assertTrue("Build logs home dir is not empty", agent.emptyLogDir());
    perforce.checkoutLatest();
    TestHelper.assertCheckoutDirNotEmpty(agent);
    assertTrue("Checkout dir should be reported as inited", perforce.isBuildDirInitialized());
    assertTrue("Build logs home should be empty", agent.logDirIsEmpty());

    // main multiline
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_DEPOT_PATH, STRING_SOURCE_LINE_MULTY_1);
    perforce.reloadConfiguration();
    perforce.checkoutLatest();
    assertTrue("Checkout dir should be reported as inited", perforce.isBuildDirInitialized());
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertCurrentBuildPathExists(perforce, agent);
    // second multiline
    final String relativeSecondFir = "second_sourceline/src";
    TestHelper.assertDirIsNotEmpty(agent, relativeSecondFir);
  }


  /**
   * Demonstrates that nothing happens whe mixed case directories
   * are checked out under Windows.
   */
  public void test_bug630syncToLatestScrewedWindowsPath() throws CommandStoppedException, BuildException, IOException, AgentFailureException {
    errorManager.clearAllActiveErrors();
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_DEPOT_PATH, "//test/abbdleclient/...");
    perforce.reloadConfiguration();
    perforce.checkoutLatest();
    TestHelper.assertCheckoutDirNotEmpty(agent);
    assertEquals(0, errorManager.errorCount());
  }


  /**
   * Demonstrates that nothing happens when use space char in P4 client view
   */
  public void test_bug737syncToLatestWorksWithSpacedPath() throws CommandStoppedException, BuildException, IOException, AgentFailureException {
    errorManager.clearAllActiveErrors();
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_DEPOT_PATH, "//test/sourceline with spaces/...");
    perforce.reloadConfiguration();
    perforce.checkoutLatest();
    TestHelper.assertCheckoutDirNotEmpty(agent);
    assertEquals(0, errorManager.errorCount());
  }


  /**
   * Demonstrates that nothing happens whe mixed case directories
   * are checked out under Windows.
   */
  public void test_bug630syncToLatestReservedMSDOSPath() throws CommandStoppedException, BuildException, IOException, AgentFailureException {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_DEPOT_PATH, "//test/aux/...");
    perforce.reloadConfiguration();
    perforce.checkoutLatest();
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);
  }


  /**
   * Tests that it fails on depot path that doesn't end with ...
   */
  public void test_createLabelFailsOnInclompleteDepotPath() throws Exception {
    try {
      final String label = "test_label_" + System.currentTimeMillis();
      perforce.createOrUpdateLabel(label, "blah");
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  public void test_createLabel() throws Exception {
    final String label = "test_label_" + System.currentTimeMillis();
    perforce.createOrUpdateLabel(label, cm.getSourceControlSettingValue(TEST_BUILD_ID, VersionControlSystem.P4_DEPOT_PATH, null));
  }


  public void test_getChangesSince() throws Exception {
    final int newChangeListID = perforce.getChangesSince(TEST_CHANGE_LIST_ID_4);
    if (log.isDebugEnabled()) log.debug("newChangeListID = " + newChangeListID);
    assertTrue(newChangeListID != TEST_CHANGE_LIST_ID_4);
    assertNotNull(cm.getChangeList(newChangeListID));

    final int newNewChangeListID = perforce.getChangesSince(newChangeListID);
    assertEquals(newChangeListID, newNewChangeListID);
  }


  public void test_getNativeChangeList() throws Exception {
    // test valid first
    int newChangeListID = perforce.getNativeChangeList("5291");
    if (log.isDebugEnabled()) log.debug("newChangeListID = " + newChangeListID);
    assertTrue(newChangeListID != TEST_CHANGE_LIST_ID_4);
    assertTrue(newChangeListID != ChangeList.UNSAVED_ID);
    assertNotNull(cm.getChangeList(newChangeListID));

    // test one that doesn't exist
    assertEquals(ChangeList.UNSAVED_ID, perforce.getNativeChangeList("100"));

    // test label
    newChangeListID = perforce.getNativeChangeList("test_label_for_manual_run");
    if (log.isDebugEnabled()) log.debug("newChangeListID = " + newChangeListID);
    assertTrue(newChangeListID != TEST_CHANGE_LIST_ID_4);
    assertTrue(newChangeListID != ChangeList.UNSAVED_ID);
    assertNotNull(cm.getChangeList(newChangeListID));
  }


  /**
   * As of this writing, the following change lists are present:
   * 5291 1072 1071 899 839 838 834, so #838 should not be present.
   */
  public void test_getChangesSinceDoesInludeFirstChangeListForFirstRun() throws Exception {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        session.delete("from ChangeList");
        return null;
      }
    });
    boolean found = false;
    // set the value used by all version control system to maximum
    SystemConfigurationManagerFactory.getManager().saveSystemProperty(new SystemProperty(SystemProperty.INITIAL_NUMBER_OF_CHANGELISTS, 10));
    perforce.getChangesSince(ChangeList.UNSAVED_ID); // stores results in the DB
    final List pendingChangeLists = cm.getPendingChangeLists(TEST_BUILD_ID);
    assertTrue(!pendingChangeLists.isEmpty());
    for (final Iterator i = pendingChangeLists.iterator(); i.hasNext();) {
      final ChangeList changeList = (ChangeList) i.next();
      found = found || changeList.getNumber().equals("834");
    }
    assertTrue("Change list #834 should be present", found);
  }


  public void test_getChangesSincePicksUpIntegOnlyBranches() throws Exception {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_DEPOT_PATH, "//test/sourceline_with_branch_integ_only/...");
    perforce.reloadConfiguration();
    assertTrue(perforce.getChangesSince(ChangeList.UNSAVED_ID) != ChangeList.UNSAVED_ID);
  }

//  public void test_getChangesSinceCanHandlerZeroChangeListID() throws Exception {
//    TestHelper.setSourceControlProperty(TEST_BUILD_ID, SourceControlSetting.P4_DEPOT_PATH, "//test/sourceline/...");
//    perforce.reloadConfiguration();
//    int newChangeListID = perforce.getChangesSince(ChangeList.UNSAVED_ID);
//    if (log.isDebugEnabled()) log.debug("newChangeListID: " + newChangeListID);
//    assertTrue(newChangeListID != TEST_CHANGE_LIST_ID_4);
//    assertNotNull(cm.getChangeList(newChangeListID));
//  }


  public void test_getChangesSinceFailsOnWrongPassword() throws Exception {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_PASSWORD, TestHelper.P4_INVALID_PASSWORD);
    try {
      perforce.reloadConfiguration();
      perforce.getChangesSince(TEST_CHANGE_LIST_ID_4);
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  public void test_getChangesSinceFailsOnWrongExecutable() throws Exception {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_PATH_TO_CLIENT, "there_is_no_such_executable");
    try {
      perforce.reloadConfiguration();
      perforce.getChangesSince(TEST_CHANGE_LIST_ID_4);
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  public void test_getChangesSinceFailsOnWrongPort() throws Exception {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_PORT, "there_is_no_such_host:555");
    try {
      perforce.reloadConfiguration();
      perforce.getChangesSince(TEST_CHANGE_LIST_ID_4);
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  public void test_getChangesSinceFetchesJobs() throws Exception {
    // set to "all"
    SystemConfigurationManagerFactory.getManager().saveSystemProperty(new SystemProperty(SystemProperty.INITIAL_NUMBER_OF_CHANGELISTS, SystemProperty.UNLIMITED_INITIAL_NUMBER_OF_CHANGELISTS));
    final Integer iclCountBefore = P4TestHelper.getIssueChangeListCount();
    perforce.reloadConfiguration();
    perforce.getChangesSince(-1);
    final Integer iclCountAfter = P4TestHelper.getIssueChangeListCount();
    if (log.isDebugEnabled()) log.debug("iclCountBefore = " + iclCountBefore);
    if (log.isDebugEnabled()) log.debug("iclCountAfter = " + iclCountAfter);
    assertEquals(1, iclCountAfter.compareTo(iclCountBefore));
  }


  public void test_syncToLatestFailsOnWrongPassword() throws Exception {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_PASSWORD, TestHelper.P4_INVALID_PASSWORD);
    try {
      perforce.reloadConfiguration();
      perforce.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  public void test_syncToLatestFailsOnWrongExecutable() throws Exception {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_PATH_TO_CLIENT, "there_is_no_such_executable");
    try {
      perforce.reloadConfiguration();
      perforce.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  public void test_syncToLatestFailsOnWrongPort() throws Exception {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_PORT, "there_is_no_such_host:555");
    try {
      perforce.reloadConfiguration();
      perforce.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  public void test_syncToLatestFailsOnNonExistingDepotPath() throws Exception {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_DEPOT_PATH, "//test/there_is_no_such_depot_path");
    try {
      perforce.reloadConfiguration();
      perforce.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  public void test_syncToChangeListFailsOnWrongPassword() throws Exception {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_PASSWORD, TestHelper.P4_INVALID_PASSWORD);
    try {
      perforce.reloadConfiguration();
      perforce.syncToChangeList(TEST_CHANGE_LIST_ID_4);
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  public void test_syncToChangeListFailsOnWrongExecutable() throws Exception {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_PATH_TO_CLIENT, "there_is_no_such_executable");
    try {
      perforce.reloadConfiguration();
      perforce.syncToChangeList(TEST_CHANGE_LIST_ID_4);
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  public void test_syncToChangeListFailsOnWrongPort() throws Exception {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_PORT, "there_is_no_such_host:555");
    try {
      perforce.reloadConfiguration();
      perforce.syncToChangeList(TEST_CHANGE_LIST_ID_4);
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
      // expected
    }
  }


  public void test_getUserMap() throws Exception {
    final Map result = perforce.getUsersMap();
    assertTrue(!result.isEmpty());
  }


  public void test_getUserMapFailsOnWrongP4Port() throws Exception {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_PORT, "there_is_no_such_host:555");
    perforce.reloadConfiguration();
    final Map result = perforce.getUsersMap();
    assertTrue(result.isEmpty());
    assertTrue(errorManager.errorCount() > 0);
  }


  //  TODO: simeshev - think over multiple lines
  public void test_checkOutLatestCatchesConfigUpdates() throws Exception {
    perforce.checkoutLatest();
    final String oldRelativeBuildDir = TestHelper.assertCurrentBuildPathExists(perforce, agent);

    // update property
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_DEPOT_PATH, STRING_SOURCE_LINE_TWO);

    // call sync
    perforce.reloadConfiguration();
    perforce.checkoutLatest();

    TestHelper.assertOldBuildPathGoneAndNewAppeared(perforce, oldRelativeBuildDir, agent);
  }


  public void test_getChangeListCounter() throws Exception {
    TestHelper.setSourceControlProperty(TEST_BUILD_ID, VersionControlSystem.P4_COUNTER, "change");
    perforce.reloadConfiguration();
    final int counter = perforce.getChangeListCounter(true);
    if (log.isDebugEnabled()) log.debug("counter = " + counter);
    assertTrue(counter > 0);
  }


  public void test_getShellVariables() throws IOException, AgentFailureException {
    final Map shellVariables = perforce.getShellVariables();
    assertNotNull(shellVariables.get(P4SourceControl.PARABUILD_P4CLIENT));
    assertNotNull(shellVariables.get(P4SourceControl.PARABUILD_P4PASSWD));
    assertNotNull(shellVariables.get(P4SourceControl.PARABUILD_P4PORT));
    assertNotNull(shellVariables.get(P4SourceControl.PARABUILD_P4USER));

    // check protocol - should start with "PARABUILD_"
    final String expectedPrefix = "PARABUILD_";
    assertTrue(P4SourceControl.PARABUILD_P4CLIENT.startsWith(expectedPrefix));
    assertTrue(P4SourceControl.PARABUILD_P4PASSWD.startsWith(expectedPrefix));
    assertTrue(P4SourceControl.PARABUILD_P4PORT.startsWith(expectedPrefix));
    assertTrue(P4SourceControl.PARABUILD_P4USER.startsWith(expectedPrefix));
  }


  public void test_removeLabel() throws CommandStoppedException, BuildException, AgentFailureException {
    perforce.removeLabels(EMPTY_STRING_ARRAY); // noting should happen
    perforce.removeLabels(new String[]{"test_label_" + Long.toString(System.currentTimeMillis())}); // noting should happen
  }


  public void test_getBranchView() throws IOException, CommandStoppedException, BuildException, AgentFailureException {
    final P4BranchView branchView = perforce.getBranchView("bt31");
    assertNotNull(branchView);
    assertEquals("//depot/dev/bt/... //depot/dev/bt31/...", branchView.view());
  }


  public void test_findFirstChangeList() throws IOException, CommandStoppedException, BuildException, ValidationException, AgentFailureException {
    final Integer firstChangeList = perforce.findFirstChangeList("merge_first_change_list_retriever_1", "//depot/dev/bt/... //merge_first_change_list_retriever_1/dev/bt/...");
    assertNotNull(firstChangeList);
    assertEquals(Integer.valueOf(1118), firstChangeList);
  }


  protected void afterSuperSetUp() {
    // may be overwirtten in children
  }


  protected void setUp() throws Exception {
    super.setUp();
//    super.enableErrorManagerStackTraces();

    afterSuperSetUp();

    // get config
    final BuildConfig buildConfig = ConfigurationManager.getInstance().getBuildConfiguration(TEST_BUILD_ID);
    assertNotNull(buildConfig);

    // create P4 version control
    final AgentHost agentHost = AgentManager.getInstance().getNextLiveAgentHost(TEST_BUILD_ID);
    this.agent = AgentManager.getInstance().createAgent(TEST_BUILD_ID, agentHost);
    this.perforce = new P4SourceControl(buildConfig);
    this.perforce.setAgentHost(agentHost);
    this.cm = ConfigurationManager.getInstance();
    TestHelper.emptyCheckoutDir(agent);
    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestP4SourceControl.class, new String[]{
            "test_syncsToChangeListWithDepotSourcePathBug1375",
            "test_getChangesSinceWithDepotSourcePath",
            "test_findFirstChangeList",
            "test_getBranchView",
            "test_syncToChangeListWithDepotSourcePathDetectsChangesInDepotPath",
            "test_syncToChangeListWithDepotSourcePathFailsOnNonExistingPath",
            "test_syncToChangeListWithDepotSourcePath",
            "test_getChangesSince",
            "test_getChangesSinceDoesInludeFirstChangeListForFirstRun",
            "test_getChangesSincePicksUpIntegOnlyBranches",
            "test_bug721resyncsAfterCleanUpdates",
            "test_bug737syncToLatestWorksWithSpacedPath",
            "test_bug630syncToLatestReservedMSDOSPath",
            "test_bug630syncToLatestScrewedWindowsPath",
            "test_checkOutLatestCatchesConfigUpdates",
            "test_createClient",
            "test_syncToLatest",
            "test_createLabel"
    });
  }
}


class P4TestHelper {

  /**
   * Helper method - retuns global IssueChangeList count
   */
  public static Integer getIssueChangeListCount() {
    return (Integer) ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final List list = session.find("select count(icl) from IssueChangeList icl ");
        return list.get(0);
      }
    });
  }
}