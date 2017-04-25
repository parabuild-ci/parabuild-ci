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
 * Tests PreviousBuildRunReleaseNotesHandler
 */
public class SSTestPreviousBuildRunReleaseNotesHandler extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestPreviousBuildRunReleaseNotesHandler.class);
  private static final int TEST_BUILD_RUN_ID = 7;

  private ErrorManager em = null;
  private ConfigurationManager cm = null;
  private PreviousBuildRunReleaseNotesHandler releaseNotesHandler = null;


  public SSTestPreviousBuildRunReleaseNotesHandler(final String s) {
    super(s);
  }


  /**
   * Tests that if prev build run was broken and had release
   * notes, they are copied over to the new build run.
   */
  public void test_process() throws Exception {


    // preExecute - get counters
    final int relnotesCounterBefore = cm.getBuildRunReleaseNotes(TEST_BUILD_RUN_ID).size();
    final int issuesCounterBefore = cm.getBuildRunIssues(TEST_BUILD_RUN_ID).size();

    // get succesful (previous in dataset.xml set to failed)
    final BuildRun buildRun = cm.getBuildRun(TEST_BUILD_RUN_ID);

    // process
    releaseNotesHandler.process(buildRun);

    // issues in build run increased
    assertEquals(issuesCounterBefore + 1, cm.getBuildRunIssues(TEST_BUILD_RUN_ID).size());
    assertEquals(relnotesCounterBefore + 1, cm.getBuildRunIssues(TEST_BUILD_RUN_ID).size());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestPreviousBuildRunReleaseNotesHandler.class);
  }


  protected void tearDown() throws Exception {
    super.tearDown();
    assertEquals("Error counter should be zero", 0, em.errorCount());
  }


  protected void setUp() throws Exception {
    super.setUp();
    this.cm = ConfigurationManager.getInstance();
    this.em = ErrorManagerFactory.getErrorManager();
    this.em.clearAllActiveErrors();
    this.releaseNotesHandler = new PreviousBuildRunReleaseNotesHandler();
  }
}
