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

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.admin.*;

/**
 * Tests home page
 */
public class SSTestIssueTrackerTable extends ServersideTestCase {

  private IssueTrackerTable trackerTable = null;
  private ConfigurationManager cm = null;
  private BuildConfig buildConfig;


  public SSTestIssueTrackerTable(final String s) {
    super(s);
  }


  public void test_load() {
    trackerTable.load(buildConfig);
    assertEquals(3, trackerTable.getRowCount());
  }


  public void test_validate() {
    // empty table validates
    assertTrue(trackerTable.validate());
    // filled table validates
    trackerTable.load(buildConfig);
    assertTrue(trackerTable.validate());
  }


  public void test_save() {
    trackerTable.load(buildConfig);
    assertTrue(trackerTable.save());
  }


  protected void setUp() throws Exception {
    super.setUp();
    trackerTable = new IssueTrackerTable();
    cm = ConfigurationManager.getInstance();
    buildConfig = cm.getBuildConfiguration(TestHelper.TEST_CVS_VALID_BUILD_ID);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestIssueTrackerTable.class);
  }
}
