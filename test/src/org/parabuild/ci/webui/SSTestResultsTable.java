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

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.configuration.ConfigurationManager;

/**
 * Tests ResultsTable
 */
public final class SSTestResultsTable extends ServersideTestCase {

  private static final int TEST_ACTIVE_BUILD_ID = 1;
  private static final int TEST_BUILD_RUN_ID = TEST_ACTIVE_BUILD_ID;
  private ConfigurationManager cm;


  public SSTestResultsTable(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_populateReadOnly() throws Exception {
    final ResultsTable table = new ResultsTable(TestHelper.TEST_CVS_VALID_BUILD_ID, false);
    table.populate(cm.getBuildRun(TEST_BUILD_RUN_ID));
    assertTrue(table.getRowCount() > 0);
  }


  /**
   *
   */
  public void test_populateEditable() throws Exception {
    final ResultsTable table = new ResultsTable(TestHelper.TEST_CVS_VALID_BUILD_ID, true);
    table.populate(cm.getBuildRun(TEST_BUILD_RUN_ID));
    assertTrue(table.getRowCount() > 0);
  }


  protected void setUp() throws Exception {
    cm = ConfigurationManager.getInstance();
    super.setUp();
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestResultsTable.class);
  }
}
