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
 *
 */
public class SSTestChangeListReleaseNotesHandler extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestChangeListReleaseNotesHandler.class);

  private ChangeListReleaseNotesHandler releaseNotesHandler = null;
  private ErrorManager em = null;
  private ConfigurationManager cm = null;


  public SSTestChangeListReleaseNotesHandler(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_process() throws Exception {
    final BuildRun buildRun = cm.getLastCompleteBuildRun(TestHelper.TEST_P4_VALID_BUILD_ID);
    final int processed = releaseNotesHandler.process(buildRun);
    assertTrue(processed > 0);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestChangeListReleaseNotesHandler.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    cm = ConfigurationManager.getInstance();
    em = ErrorManagerFactory.getErrorManager();
    em.clearAllActiveErrors();

    final int TEST_ISSUE_TRACKER_ID = 4;
    releaseNotesHandler = new ChangeListReleaseNotesHandler(cm.getIssueTracker(TEST_ISSUE_TRACKER_ID));
  }
}
