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
package org.parabuild.ci.relnotes;

import junit.framework.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.error.*;
import org.parabuild.ci.object.*;

/**
 * Tests PendingReleaseNotesHandler on the server side.
 */
public class SSTestPendingReleaseNotesHandler extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestPendingReleaseNotesHandler.class);

  private PendingReleaseNotesHandler releaseNotesHandler = null;
  private ErrorManager em = null;
  private ConfigurationManager cm = null;
  private static final int TEST_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;
  private static final int TEST_ISSUE_ID = 5;


  public SSTestPendingReleaseNotesHandler(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_process() throws Exception {
    // create a pending issue
    final PendingIssue pendingIssue = new PendingIssue(TEST_BUILD_ID, TEST_ISSUE_ID);
    cm.saveObject(pendingIssue);

    final int pendingCounterBefore = cm.getPendingIssues(TEST_BUILD_ID).size();
    if (log.isDebugEnabled()) log.debug("pendingCounterBefore = " + pendingCounterBefore);
    final int releaseCounterBefore = cm.getBuildRunIssues(3).size();
    if (log.isDebugEnabled()) log.debug("releaseCounterBefore = " + releaseCounterBefore);
    final BuildRun buildRun = cm.getBuildRun(3);
    releaseNotesHandler.process(buildRun);

    // pending decreased
    assertEquals(0, cm.getPendingIssues(TEST_BUILD_ID).size());

    // issues in build run increased
    assertEquals(releaseCounterBefore + 1, cm.getBuildRunIssues(3).size());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestPendingReleaseNotesHandler.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    cm = ConfigurationManager.getInstance();
    em = ErrorManagerFactory.getErrorManager();
    em.clearAllActiveErrors();
    releaseNotesHandler = new PendingReleaseNotesHandler();
  }
}
