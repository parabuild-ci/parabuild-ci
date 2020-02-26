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
package org.parabuild.ci.versioncontrol.accurev;

import org.parabuild.ci.TestHelper;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.versioncontrol.AbstractSourceControlTest;

/**
 * AccurevVersionControl Tester.
 *
 * @author simeshev@cacheonix.com
 * @version 1.0
 * @since <pre>02/13/2009</pre>
 */
public final class SSTestAccurevVersionControl extends AbstractSourceControlTest {

  private AccurevVersionControl accurev = null;
  private static final int TEST_CHANGE_LIST_ID = 39;


  public SSTestAccurevVersionControl(String s) {
    super(s);
  }


  public void test_getChangesSince() throws Exception {
    int changesSince = accurev.getChangesSince(0);
    assertTrue(changesSince != ChangeList.UNSAVED_ID);
  }


  public void test_checkoutLatest() throws Exception {
    // Not supported
  }


  public void test_checkOutLatestCatchesConfigUpdates() throws Exception {
    // Not supported
  }


  public void test_checkOutMultilineRepositoryPath() throws Exception {
    // Not supported
  }


  public void test_checkOutLatestCantProcessUnexistingSourceLine() throws Exception {
    // Not supported
  }


  public void test_checkOutLatestCantProcessInavalidUser() throws Exception {
    // Not supported
  }


  public void test_syncToChangeListInitsEmptyCheckoutDir() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);
    accurev.syncToChangeList(TEST_CHANGE_LIST_ID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
  }


  public void test_syncToChangeList() throws Exception {
    // We expect that there is change list in the test/config/dataset.xml
    accurev.syncToChangeList(TEST_CHANGE_LIST_ID);
    TestHelper.assertCheckoutDirNotEmpty(agent);
    TestHelper.assertDirIsNotEmpty(agent, accurev.getRelativeBuildDir());
  }


  public void test_syncToChangeListPicksUpConfigChanges() throws Exception {
    TestHelper.emptyCheckoutDir(agent);
    TestHelper.assertCheckoutDirExistsAndEmpty(agent);

    // we expect that there is change list in the test/config/dataset.xml
    accurev.syncToChangeList(TEST_CHANGE_LIST_ID);
    TestHelper.assertDirIsNotEmpty(agent, "directory1");

    // update property
    TestHelper.setSourceControlProperty(getTestBuildID(), VersionControlSystem.ACCUREV_DEPOT, "test_other_project");

    // reload
    accurev.reloadConfiguration();
    // NOTE: vimeshev - 03/06/2005 - this changelist id translates
    // into svn change list #3 that contains second source line.
    accurev.syncToChangeList(TEST_CHANGE_LIST_ID);

    // assert
    TestHelper.assertDirIsNotEmpty(agent, "test_folder1");
  }


  public void test_getChangesSinceDoesntFailOnBlankSourceline() throws Exception {
    // TODO: simeshev@parabuilci.org - 2009-02-13 - Implement
  }


  public void test_canNotAccessWithWrongPassword() throws Exception {
    TestHelper.setSourceControlProperty(getTestBuildID(), VersionControlSystem.ACCUREV_PASSWORD, "FED13585645B0EE34267AAAED4697000");
    accurev.reloadConfiguration();
    try {
      accurev.checkoutLatest();
      fail("Expected exception but it was not thrown");
    } catch (BuildException e) {
    }
  }


  public void test_getUsersMap() throws Exception {
    assertTrue(accurev.getUsersMap().isEmpty());
  }


  public void test_getChangesSinceHandlesFirstRun() throws Exception {
    final int changeListID = accurev.getChangesSince(ChangeList.UNSAVED_ID);
    assertTrue(changeListID > 0);
  }


  public void test_label() throws BuildException, CommandStoppedException {
    // Not supported
  }


  public void test_checkOutLatestBranch() throws Exception {
    // Does not support branches
  }


  public void test_syncToChangeListBranch() throws Exception {
    // Does not support branches
  }


  public void test_getChangesSinceInBranch() throws Exception {
    // Does not support branches
  }


  public int getTestBuildID() {
    return 33;
  }


  protected void setUp() throws Exception {
    super.setUp();
    final BuildConfig buildConfig = cm.getBuildConfiguration(getTestBuildID());
    accurev = new AccurevVersionControl(buildConfig);
    accurev.setAgentHost(agentHost);
  }
}
