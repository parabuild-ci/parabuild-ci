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
package org.parabuild.ci.webui;

import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildRun;

import java.util.Collection;
import java.util.Iterator;

/**
 * Tests home page
 */
public class SSTestBuildHistoryTable extends ServersideTestCase {

  private static final Log LOG = LogFactory.getLog(SSTestBuildHistoryTable.class);
  static final int ACTIVE_BUILD_ID = TestHelper.TEST_CVS_VALID_BUILD_ID;
  private BuildHistoryTable historyTable = null;
  private ConfigurationManager cm = null;


  public SSTestBuildHistoryTable(final String s) {
    super(s);
  }


  /**
   * Tests that BuildHistoryTable doesn't break if there are
   * imcomplete build runs with null finishedAt field.
   */
  public void test_bug265() throws Exception {
    validateBuildRuns();
    cm.getCompletedBuildRuns(ACTIVE_BUILD_ID, 0, 100);
    historyTable.populate();
    final int rowCount = historyTable.getRowCount();
    if (LOG.isDebugEnabled()) LOG.debug("rowCount = " + rowCount);
    assertTrue(rowCount > 0);
  }


  private void validateBuildRuns() {
    final Collection buildRuns = cm.getBuildRuns(ACTIVE_BUILD_ID, 100);
    for (Iterator i = buildRuns.iterator(); i.hasNext();) {
      final BuildRun buildRun = (BuildRun) i.next();
      if (buildRun.getFinishedAt() == null) return;
    }
    fail("Precondition doesn't met - build run with null finishedAt not found");
  }


  protected void setUp() throws Exception {
    super.setUp();
    historyTable = new BuildHistoryTable("Build Name");
    cm = ConfigurationManager.getInstance();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestBuildHistoryTable.class);
  }
}
