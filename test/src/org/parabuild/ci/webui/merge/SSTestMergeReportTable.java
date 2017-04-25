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

import java.util.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import junit.framework.TestSuite;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.merge.MergeManager;

/**
 * Tests MergeReportTable
 */
public class SSTestMergeReportTable extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestMergeReportTable.class); // NOPMD

  private static final int TEST_MERGE_ID = 0;
  private MergeReportTable mergeReportTable;


  public void test_populate() {
    // test that it doesn't blow up
    final MergeManager mm = MergeManager.getInstance();
    final List mergeReport = mm.getMergeReport(TEST_MERGE_ID, 0, Integer.MAX_VALUE);
    mergeReportTable.populate(mergeReport);
  }


  public SSTestMergeReportTable(final String s) {
    super(s);
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestMergeReportTable.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    mergeReportTable = new MergeReportTable();
  }
}
