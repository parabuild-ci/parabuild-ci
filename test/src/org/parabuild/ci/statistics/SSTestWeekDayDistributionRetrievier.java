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

import junit.framework.TestSuite;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;

import java.util.Iterator;
import java.util.Map;
import java.util.SortedMap;

/**
 * Tests WeekDayDistributionRetrievier
 */
public class SSTestWeekDayDistributionRetrievier extends ServersideTestCase {

  /**
   * @noinspection UNUSED_SYMBOL
   */
  private static final Log log = LogFactory.getLog(SSTestWeekDayDistributionRetrievier.class);

  private WeekDayBuildDistributionRetriever distrRetriever = null;
  private ErrorManager errorManager;


  public SSTestWeekDayDistributionRetrievier(final String s) {
    super(s);
  }


  public void test_getStatistics() {
    final SortedMap statistics = distrRetriever.getStatistics();

    // check if we got the right size
    assertEquals(WeekDayBuildDistributionRetriever.DISTRIBUTION_SIZE, statistics.size());

    // check no null values
    for (Iterator i = statistics.entrySet().iterator(); i.hasNext();) {
      final Map.Entry entry = (Map.Entry) i.next();
      final Integer day = (Integer) entry.getKey();
      assertTrue(day.intValue() <= 7);
      assertTrue(day.intValue() >= 1);
      assertNotNull(entry.getValue());
    }
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestWeekDayDistributionRetrievier.class);
  }


  protected void tearDown() throws Exception {
    super.tearDown();
    assertEquals(0, errorManager.errorCount());
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();
    distrRetriever = new WeekDayBuildDistributionRetriever(TestHelper.TEST_CVS_VALID_BUILD_ID);
  }
}
