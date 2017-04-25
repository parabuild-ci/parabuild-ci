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
package org.parabuild.ci.webui.merge;

import junit.framework.TestSuite;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.merge.MergeManager;

/**
 * Tests MergeStatusesTable
 */
public class SSTestMergeStatusesTable extends ServersideTestCase {


  public void test_create() {
  }


  public void test_populateAdmin() {
    final MergeStatusesTable table = new MergeStatusesTable(true);
    table.populate(MergeManager.getInstance().getMergeStatuses());
  }


  public void test_populateNoAdmin() {
    final MergeStatusesTable table = new MergeStatusesTable(false);
    table.populate(MergeManager.getInstance().getMergeStatuses());
  }


  public SSTestMergeStatusesTable(final String s) {
    super(s);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestMergeStatusesTable.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    ErrorManagerFactory.getErrorManager().clearAllActiveErrors();
  }
}
