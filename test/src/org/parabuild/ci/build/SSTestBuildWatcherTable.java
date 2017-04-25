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
package org.parabuild.ci.build;

import java.util.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.webui.admin.*;
import org.parabuild.ci.webui.common.WebUIConstants;

/**
 * Tests home page
 */
public class SSTestBuildWatcherTable extends ServersideTestCase {

  private static final int TEST_BUILD_ID_1 = 1;
  BuildWatcherTable watcherTable = null;


  public SSTestBuildWatcherTable(final String s) {
    super(s);
  }


  /**
   * Tests the table component constructor
   */
  public void test_populateFromEmptyList() throws Exception {
    final List emptyList = new ArrayList();
    watcherTable.populate(emptyList);
    assertEquals(watcherTable.getRowCount(), watcherTable.getWatchers().size());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestBuildWatcherTable.class);
  }


  protected void setUp() throws Exception {
    // call ServerSideTest setup that initializes db data
    super.setUp();
    watcherTable = new BuildWatcherTable(WebUIConstants.MODE_EDIT);
  }
}
