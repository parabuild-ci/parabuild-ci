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
package org.parabuild.ci.versioncontrol.bazaar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.versioncontrol.AbstractSourceControlTest;

import java.util.ArrayList;
import java.util.List;


/**
 * SSTestBazaarSourceControl
 *
 * Test repository location: /var/git/test.git
 *
 * Port: 94918
 *
 * <p/>
 *
 * @author Slava Imeshev
 * @since Jan 24, 2010 2:05:44 PM
 */
public final class SSTestBazaarSourceControl extends AbstractSourceControlTest {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(SSTestBazaarSourceControl.class); // NOPMD
  private BazaarVersionControl bazaar;
  private static final int TEST_CHANGE_LIST_ID = -1;


  public SSTestBazaarSourceControl(final String s) {
    super(s);
  }


  public void test_getChangesSince() throws Exception {
    // test that we get changes
    final int lastChangeListID = bazaar.getChangesSince(TEST_CHANGE_LIST_ID);
    assertTrue(lastChangeListID != TEST_CHANGE_LIST_ID);

    // test fix #516 - that we dont' leave temp files.

    // temp dir is empty
    IoUtils.emptyDir(TestHelper.JAVA_TEMP_DIR);
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);
    final int newChangeListID = bazaar.getChangesSince(lastChangeListID);

    // no new changes
    assertEquals(lastChangeListID, newChangeListID);

    // temp dir is empty
    TestHelper.assertDirIsEmpty(TestHelper.JAVA_TEMP_DIR);
  }


  public void test_checkoutLatest() throws Exception {
    assertTrue("Build logs home dir is not empty", agent.emptyLogDir());
    bazaar.checkoutLatest();
    assertTrue("Build logs home dir is not empty", agent.emptyLogDir());
  }


  public void test_checkOutLatestCatchesConfigUpdates() throws Exception {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  public void test_checkOutLatestCantProcessUnexistingSourceLine() throws Exception {
  }


  public void test_checkOutLatestCantProcessUnexistingRepository() throws Exception {
    final List settings = new ArrayList(1);
    settings.add(makeSetting(VersionControlSystem.BAZAAR_BRANCH_LOCATION, "bzr://127.0.0.1/blah"));
    settings.add(makeSetting(VersionControlSystem.BAZAAR_EXE_PATH, "bzr"));
    final BazaarVersionControl svn = makeBazaarSourceControlWithAlteredSettings(settings);
    svn.setAgentHost(agentHost);
    try {
      svn.checkoutLatest();
      TestHelper.failNoExceptionThrown();
    } catch (BuildException e) {
    }
  }


  public void test_syncToChangeListInitsEmptyCheckoutDir() throws Exception {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  public void test_syncToChangeList() throws Exception {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  public void test_syncToChangeListPicksUpConfigChanges() throws Exception {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  public void test_getChangesSinceDoesntFailOnBlankSourceline() throws Exception {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  public void test_canNotAccessWithWrongPassword() throws Exception {
    // Not supported
  }


  public void test_getUsersMap() throws Exception {
    assertTrue(this.bazaar.getUsersMap().isEmpty());
  }


  public int getTestBuildID() {
    return 35;
  }


  public void test_getChangesSinceHandlesFirstRun() throws Exception {
    //To change body of implemented methods use File | Settings | File Templates.
  }


  public void test_checkOutLatestCantProcessInavalidUser() throws Exception {
    // Not supported
  }


  public void test_label() throws BuildException, CommandStoppedException, AgentFailureException {
    // Not supported
  }

  public void test_checkOutLatestBranch() throws Exception {
    // Not supported
  }


  public void test_syncToChangeListBranch() throws Exception {
    // Not supported
  }


  public void test_getChangesSinceInBranch() throws Exception {
    // Not supported
  }


  public void test_checkOutMultilineRepositoryPath() throws Exception {
    // Not supported
  }


  private BazaarVersionControl makeBazaarSourceControlWithAlteredSettings(final List settings) {
    return new BazaarVersionControl(cm.getBuildConfiguration(getTestBuildID()), settings);
  }



  /**
   * Shortcut to test helper's makeSourceControlSetting
   */
  private SourceControlSetting makeSetting(final String name, final String value) {
    return TestHelper.makeSourceControlSetting(name, value);
  }



  private BazaarVersionControl makeBazaarSourceControl(final int buildID)  {
    final BuildConfig buildConfig = cm.getBuildConfiguration(buildID);
    assertNotNull(buildConfig);
    return new BazaarVersionControl(buildConfig);
  }


  protected final void setUp() throws Exception {
    super.setUp();
    this.bazaar = makeBazaarSourceControl(getTestBuildID());
    this.bazaar.setAgentHost(agentHost);

    // set in super's setUp
    assertEquals(99, this.bazaar.initialNumberOfChangeLists());
  }
}