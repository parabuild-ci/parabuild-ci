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
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.error.*;
import org.parabuild.ci.object.*;
import net.sf.hibernate.*;

/**
 * Tests MonthlyTestStatsUpdater
 */
public class SSTestMonthlyTestStatsUpdater extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestMonthlyTestStatsUpdater.class);

  private MonthlyTestStatsUpdater statsUpdater = null;
  private ErrorManager errorManager;


  public SSTestMonthlyTestStatsUpdater(final String s) {
    super(s);
  }


  public void test_truncateLevel() {
    assertEquals(Calendar.MONTH, statsUpdater.truncateLevel());
  }

  public void test_updateStatistics() {
    // get build run
    final BuildRun buildRun = ConfigurationManager.getInstance().getBuildRun(2);

    // update stats
    statsUpdater.updateStatistics(buildRun);

    // assert
    final Date sampleTime = TestHelper.makeDate(2003, 0, 1, 0, 0, 0);
    final MonthlyTestStats testStats = (MonthlyTestStats)ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        final Query query = session.createQuery("select mts from MonthlyTestStats mts " +
          "  where mts.activeBuildID = ? and mts.testCode = ? and mts.sampleTime = ?");
        query.setInteger(0, buildRun.getActiveBuildID());
        query.setByte(1, PersistentTestStats.TYPE_JUNIT);
        query.setTimestamp(2, sampleTime);
        return (MonthlyTestStats)query.uniqueResult();
      }
    });
    assertNotNull(testStats);
    assertEquals(PersistentTestStats.TYPE_JUNIT, testStats.getTestCode());
    assertEquals(sampleTime, testStats.getSampleTime());
    assertEquals(10, testStats.getErrorTestCount());
    assertEquals(5, testStats.getFailedTestCount());
    assertEquals(85, testStats.getSuccessfulTestCount());
  }

  protected void tearDown() throws Exception {
    super.tearDown();
    assertEquals(0, errorManager.errorCount());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestMonthlyTestStatsUpdater.class);
  }


  protected void setUp() throws Exception {
    super.setUp();
    super.enableErrorManagerStackTraces();
    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();
    statsUpdater = new MonthlyTestStatsUpdater(PersistentTestStats.TYPE_JUNIT);
  }
}
