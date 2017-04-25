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
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.error.*;
import org.parabuild.ci.object.*;

/**
 * Tests BugzillaJDBCReleaseNotesHandler on the server side.
 */
public class SSTestBugzillaJDBCReleaseNotesHandler extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestBugzillaJDBCReleaseNotesHandler.class);

  private BugzillaDatabaseReleaseNotesHandler bzReleaseNotesHandler;
  private ErrorManager em;
  private ConfigurationManager cm;


  /**
   *
   */
  public void test_process() throws Exception {
    final BuildRun buildRun = cm.getBuildRun(3);
    final int issueCountBefore = cm.getBuildRunIssues(buildRun).size();
    final int issueWithURLCountBefore = cm.getPendingIssueCountWithURLStartingWith(buildRun.getActiveBuildID(), "http://test");
    // make sure issues put to pending list
    final int pendingIssuesAdded = bzReleaseNotesHandler.process(buildRun);
    assertEquals(7, pendingIssuesAdded);

    // make sure added issues have URLs
    final int issueWithURLCountAfter = cm.getPendingIssueCountWithURLStartingWith(buildRun.getActiveBuildID(), "http://test");
    assertTrue(issueWithURLCountAfter > issueWithURLCountBefore);
    assertEquals(7, issueWithURLCountAfter);

    // make sure issues are NOT added to release notes
    final int issueCountAfter = cm.getBuildRunIssues(buildRun).size();
    assertEquals(issueCountBefore, issueCountAfter);
  }


  protected void tearDown() throws Exception {
    assertEquals("Error count should be zero", 0, em.errorCount());
    super.tearDown();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestBugzillaJDBCReleaseNotesHandler.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    cm = ConfigurationManager.getInstance();
    em = ErrorManagerFactory.getErrorManager();
    em.clearAllActiveErrors();
    final IssueTracker tracker = cm.getIssueTracker(3); // BZ
    bzReleaseNotesHandler = new BugzillaDatabaseReleaseNotesHandler(tracker);
  }


  public SSTestBugzillaJDBCReleaseNotesHandler(final String s) {
    super(s);
  }
}
