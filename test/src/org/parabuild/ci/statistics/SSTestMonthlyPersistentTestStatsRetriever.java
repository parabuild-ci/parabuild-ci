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
package org.parabuild.ci.statistics;

import java.util.*;
import org.apache.commons.logging.*;

import junit.framework.*;

import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.error.*;
import org.parabuild.ci.object.*;

/**
 * Tests MonthlyPersistentStatsRetriever
 */
public class SSTestMonthlyPersistentTestStatsRetriever extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestMonthlyPersistentTestStatsRetriever.class);

  private MonthlyPersistentTestStatsRetriever statsRetriever = null;
  private ErrorManager errorManager;


  public SSTestMonthlyPersistentTestStatsRetriever(final String s) {
    super(s);
  }


  public void test_getStatistics() {
    final SortedMap statistics = statsRetriever.getStatistics();

    // check if we got the right size
    assertEquals(MonthlyPersistentTestStatsRetriever.DEFAULT_STATS_MONTHS, statistics.size());

    // check no null values
    for (Iterator i = statistics.entrySet().iterator(); i.hasNext();) {
      final Map.Entry entry = (Map.Entry)i.next();
      final Date sampleTime = (Date)entry.getKey();
      assertEquals(StatisticsUtils.truncateDate(sampleTime, Calendar.MONTH), sampleTime);
      assertNotNull(entry.getValue());
    }
  }


  protected void tearDown() throws Exception {
    super.tearDown();
    assertEquals(0, errorManager.errorCount());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestMonthlyPersistentTestStatsRetriever.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();
    statsRetriever = new MonthlyPersistentTestStatsRetriever(TestHelper.TEST_CVS_VALID_BUILD_ID,  PersistentTestStats.TYPE_JUNIT);
  }
}
