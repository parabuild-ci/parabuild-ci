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

import java.util.Collections;

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
import org.parabuild.ci.security.SecurityManager;

/**
 */
public class SSTestVaultSourceControl extends AbstractSourceControlTest {

  private static final Log log = LogFactory.getLog(SSTestVaultSourceControl.class);

  private static final String VAULT_ROOT = "$/";
  private static final String STRING_SOURCE_LINE_ONE = "test/sourceline/alwaysvalid";
  private static final String STRING_SOURCE_LINE_TWO = "test/second_sourceline/src";
  private static final int TEST_CHANGE_LIST_ID = 20;

  private VaultSourceControl vault = null;


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSince() throws Exception {
    // test that we get changes
    final int lastChangeListID = vault.getChangesSince(TEST_CHANGE_LIST_ID);
    assertTrue(lastChangeListID != TEST_CHANGE_LIST_ID);

    // test fix #516 - that we dont' leave temp files.

    // temp dir is empty
    IoUtils.emptyDir(TestHelper.JAVA_TEMP_DIR);
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);

    // no new changes ?
    final int newChangeListID = vault.getChangesSince(lastChangeListID);

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
    vault.checkoutLatest();
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  public void test_checkOutLatestBranch() throws Exception {
    // alter
    final SourceControlSetting branchProperty = cm.getSourceControlSetting(getTestBuildID(), SourceControlSetting.VAULT_REPOSITORY_PATH);
    branchProperty.setPropertyValue("$/test_branch");
    cm.saveObject(branchProperty);
    // run
    TestHelper.emptyCheckoutDir(agent);
    vault.reloadConfiguration();
    vault.checkoutLatest();
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
    // alter
    TestHelper.setSourceControlProperty(vault.getBuildID(), SourceControlSetting.VAULT_REPOSITORY_PATH, "$/test_branch");

    // reload
    vault.reloadConfiguration();

    // test
    final int changeListID = vault.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID != ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);
  }


  public void test_checkOutLatestCatchesConfigUpdates() throws Exception {
    vault.checkoutLatest();
    final String oldRelativeBuildDir = TestHelper.assertCurrentBuildPathExists(vault, agent);

    // update property
    TestHelper.setSourceControlProperty(vault.getBuildID(), SourceControlSetting.VAULT_REPOSITORY_PATH, VAULT_ROOT + STRING_SOURCE_LINE_TWO);
    vault.reloadConfiguration();

    // call sync
    vault.checkoutLatest();

    TestHelper.assertOldBuildPathGoneAndNewAppeared(vault, oldRelativeBuildDir, agent);
  }


  public void test_checkOutMultilineRepositoryPath() throws Exception {
    // cleanup build logs dir
    assertTrue(agent.emptyLogDir());
    // alter
    final String multilineSourceLine = VAULT_ROOT + STRING_SOURCE_LINE_ONE + '\n' + VAULT_ROOT + STRING_SOURCE_LINE_TWO;
    TestHelper.setSourceControlProperty(vault.getBuildID(), SourceControlSetting.VAULT_REPOSITORY_PATH, multilineSourceLine);

    // sync w/reload
    vault.reloadConfiguration();
    vault.checkoutLatest();

    // assert
    TestHelper.assertPathsEqual(STRING_SOURCE_LINE_ONE, vault.getRelativeBuildDir());
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
    // alter
    TestHelper.setSourceControlProperty(vault.getBuildID(), SourceControlSetting.VAULT_REPOSITORY_PATH, VAULT_ROOT + "test/never/existed");
    vault.reloadConfiguration();

    // test
    try {
      vault.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
  }


  /**
   * Tests that checkoutLatest cannot process unxesising source
   * line
   *
   * @throws Exception
   */
  public void test_checkOutLatestCantProcessInavalidUser() throws Exception {
    // alter
    TestHelper.setSourceControlProperty(vault.getBuildID(), SourceControlSetting.VAULT_USER, "never_existed_user");
    vault.reloadConfiguration();

    // test
    try {
      vault.checkoutLatest();
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

    final int changeListID = vault.getChangesSince(ChangeList.UNSAVED_ID);
    final ChangeList newChangeList = cm.getChangeList(changeListID);
    log.debug("lastChangeList = " + newChangeList);

    // sync to latest
    vault.syncToChangeList(changeListID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeList() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);
    vault.syncToChangeList(vault.getChangesSince(ChangeList.UNSAVED_ID));
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListPicksUpConfigChanges() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);

    // alter
    TestHelper.setSourceControlProperty(vault.getBuildID(), SourceControlSetting.VAULT_REPOSITORY_PATH, VAULT_ROOT + STRING_SOURCE_LINE_ONE);
    vault.reloadConfiguration();

    // we expect that there is change list in the test/config/dataset.xml
    vault.syncToChangeList(TEST_CHANGE_LIST_ID);
    final String oldRelativeBuildDir = TestHelper.assertCurrentBuildPathExists(vault, agent);

    // update property
    TestHelper.setSourceControlProperty(vault.getBuildID(), SourceControlSetting.VAULT_REPOSITORY_PATH, VAULT_ROOT + STRING_SOURCE_LINE_TWO);
    vault.reloadConfiguration();

    // NOTE: vimeshev - 03/06/2005 - this changelist id translates
    // into Vault change list #3 that contains second source line.
    vault.syncToChangeList(20);

    // assert
    TestHelper.assertOldBuildPathGoneAndNewAppeared(vault, oldRelativeBuildDir, agent);
  }


  /**
   * @throws Exception
   */
  public void test_syncToChangeListDeletesFilesThatDontExistsAtThisVersion() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);

    // alter
    TestHelper.setSourceControlProperty(vault.getBuildID(), SourceControlSetting.VAULT_REPOSITORY_PATH, VAULT_ROOT + "test"); // home of both
    vault.reloadConfiguration();

    vault.syncToChangeList(20); // this one has first and send path
    TestHelper.assertExists(agent, STRING_SOURCE_LINE_ONE);
    TestHelper.assertExists(agent, STRING_SOURCE_LINE_TWO);


    // NOTE: simeshev@parabuildci.org -> 2005-12-11 - Currently Vault
    // doesn't delete newer files when use "getversion" to sync
    // to older versions of a directory.
//    vault.syncToChangeList(19); // this one has only second
//    TestHelper.assertNotExists(agent, STRING_SOURCE_LINE_ONE);
//    TestHelper.assertExists(agent, STRING_SOURCE_LINE_TWO);
  }


  /**
   * Tests that getting change list from source line that doesn't
   * contain any changes but dirs
   *
   * @throws Exception
   */
  public void test_getChangesSinceDoesntFailOnBlankSourceline() throws Exception {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  /**
   * Tests can not access passworded source line with wrong
   * password
   */
  public void test_canNotAccessWithWrongPassword() throws Exception {
    // alter
    TestHelper.setSourceControlProperty(vault.getBuildID(), SourceControlSetting.VAULT_PASSWORD, SecurityManager.encryptPassword("wrong_passord"));
    vault.reloadConfiguration();

    // test
    try {
      vault.getChangesSince(TEST_CHANGE_LIST_ID);
      TestHelper.failNoExceptionThrown();
    } catch (Exception e) {
    }
  }


  public void test_getUsersMap() throws Exception {
    assertEquals(Collections.emptyMap(), vault.getUsersMap());
  }


  /**
   * Tests getting change list
   *
   * @throws Exception
   */
  public void test_getChangesSinceHandlesFirstRun() throws Exception {
    final int changeListID = vault.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);
  }


  /**
   * Tests label method.
   */
  public void test_label() throws BuildException, CommandStoppedException, AgentFailureException {
    // no exception thrown
    vault.syncToChangeList(TEST_CHANGE_LIST_ID);
    vault.label("parabuild_test_" + System.currentTimeMillis());
    assertTrue(errorManager.errorCount() == 0);
  }


  public int getTestBuildID() {
    return TestHelper.TEST_VAULT_VALID_BUILD_ID;  //To change body of implemented methods use File | Settings | File Templates.
  }


  protected void setUp() throws Exception {
    super.setUp();
    final BuildConfig buildConfig = cm.getBuildConfiguration(getTestBuildID());
    assertNotNull(buildConfig);
    this.vault = new VaultSourceControl(buildConfig);
    this.vault.setAgentHost(agentHost);

    // set in super's setUp
    assertEquals(SystemProperty.UNLIMITED_INITIAL_NUMBER_OF_CHANGELISTS, this.vault.initialNumberOfChangeLists());
    super.errorManager.clearAllActiveErrors();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestVaultSourceControl.class,
            new String[]{
                    "test_syncToChangeListDeletesFilesThatDontExistsAtThisVersion",
                    "test_checkOutLatestCatchesConfigUpdates",
                    "test_label",
                    "test_syncToChangeList",
                    "test_checkOutLatestBranch",
                    "test_checkoutLatest"
            });
  }


  public SSTestVaultSourceControl(final String s) {
    super(s);
  }
}
