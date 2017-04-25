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

/**
 * Tests StatisticsManagerImpl
 */
public class SSTestStatisticsManagerImpl extends ServersideTestCase {

  /** @noinspection UNUSED_SYMBOL*/
  private static final Log log = LogFactory.getLog(SSTestStatisticsManagerImpl.class);

  private ConfigurationManager cm = null;
  private StatisticsManagerImpl statMan = null;
  private ErrorManager errorManager;


  public SSTestStatisticsManagerImpl(final String s) {
    super(s);
  }


  /**
   * This tests that inserts within the same session can be seen
   * through cached query.
   */
  public void test_BuildAttributeQueryCache() {
    ConfigurationManager.runInHibernate(new TransactionCallback() {
      public Object runInTransaction() throws Exception {
        ActiveBuildAttribute aba = cm.getActiveBuildAttribute(1, ActiveBuildAttribute.STAT_FAILED_BUILDS_TO_DATE);
        assertNull(aba);

        aba = new ActiveBuildAttribute(1, ActiveBuildAttribute.STAT_FAILED_BUILDS_TO_DATE, "0");
        //cm.saveObject(ba);
        session.save(aba);
        session.flush();

        final ActiveBuildAttribute baNew = cm.getActiveBuildAttribute(1, ActiveBuildAttribute.STAT_FAILED_BUILDS_TO_DATE);
        assertNotNull(baNew);
        return null;
      }
    });
  }


  public void test_updateStatistics() {
    // for now just check nothing happens
    final BuildRun buildRun = cm.getBuildRun(2);
    statMan.updateStatistics(buildRun);
    assertEquals(0, errorManager.errorCount());
  }


  public void test_clearStatistics() {
    // NOTE: currently tests only that up-to-date gets reset.
    // will have to add more tests as new caches arrive.
    statMan.clearStatistics();

    // assert was reset
    assertTrue(cm.getActiveBuildAttribute(1, ActiveBuildAttribute.STAT_FAILED_BUILDS_TO_DATE) == null);
    assertTrue(cm.getActiveBuildAttribute(1, ActiveBuildAttribute.STAT_CHANGE_LISTS_TO_DATE) == null);
    assertTrue(cm.getActiveBuildAttribute(1, ActiveBuildAttribute.STAT_SUCC_BUILDS_TO_DATE) == null);

    statMan.initStatistics();

    // assert renews
    assertExpectedValues();
  }


  public void test_getYearToDateDistribution() {
    final SortedMap yearToDateDistribution = statMan.getYearToDateBuildStatistics();
    assertEquals(AbstractPersistentBuildStatsRetriever.DEFAULT_STATS_MONTHS, yearToDateDistribution.size());
  }


  /**
   *
   */
  public void test_getUpToDateBuildStatistics() throws Exception {
    statMan.initStatistics();
    assertExpectedValues(); // first request
    assertExpectedValues(); // second request - will run against inserted values
  }


  public void test_getMonthToDateBuildStatistics() {
    final SortedMap monthToDateStatistics = statMan.getMonthToDateBuildStatistics();
    assertEquals(AbstractPersistentStatsRetriever.DEFAULT_STATS_DAYS, monthToDateStatistics.size());
  }


  public void test_getYearToDateBuildStatistics() {
    final SortedMap yearToDateStatistics = statMan.getYearToDateBuildStatistics();
    assertEquals(AbstractPersistentBuildStatsRetriever.DEFAULT_STATS_MONTHS, yearToDateStatistics.size());
  }


  public void test_getHourlyDistribution() {
    final SortedMap hourlyDistribution = statMan.getHourlyDistribution();
    assertEquals(24, hourlyDistribution.size());
  }


  public void test_getMonthToDateTestStatistics() {
    final SortedMap monthToDateStatistics = statMan.getMonthToDateTestStatistics(PersistentTestStats.TYPE_JUNIT);
    assertEquals(AbstractPersistentStatsRetriever.DEFAULT_STATS_DAYS, monthToDateStatistics.size());
  }


  public void test_getYearToDateTestStatistics() {
    final SortedMap yearToDateStatistics = statMan.getYearToDateTestStatistics(PersistentTestStats.TYPE_JUNIT);
    assertEquals(AbstractPersistentBuildStatsRetriever.DEFAULT_STATS_MONTHS, yearToDateStatistics.size());
  }


  public void test_getTestStatistics() {
    final SortedMap testStatistics = statMan.getRecentTestStatistics(PersistentTestStats.TYPE_JUNIT);
    assertNotNull(testStatistics);
    assertTrue(!testStatistics.isEmpty());
    final Set set = testStatistics.entrySet();
    for (Iterator iterator = set.iterator(); iterator.hasNext();) {
      final Map.Entry entry = (Map.Entry)iterator.next();
      if (log.isDebugEnabled()) log.debug("entry.getKey(): " + entry.getKey());
      if (log.isDebugEnabled()) log.debug("entry.getValue(): " + entry.getValue());
    }
    if (log.isDebugEnabled()) log.debug("testStatistics: " + testStatistics);
  }


  public void test_getRecentPMDViolations() {
    final SortedMap recentPMDViolations = statMan.getRecentPMDViolations();
    assertNotNull(recentPMDViolations);
  }


  private void assertExpectedValues() {
    final BuildStatistics utds = statMan.getUpToDateBuildStatistics();
    assertNotNull(utds);
    assertEquals(1, utds.getFailedBuilds());
    assertEquals(50, utds.getFailedBuildsPercent());
    assertEquals(1, utds.getSuccessfulBuilds());
    assertEquals(50, utds.getSuccessfulBuildsPercent());
    assertEquals(2, utds.getTotalBuilds());
    assertEquals(2, utds.getChangeLists());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new TestSuite(SSTestStatisticsManagerImpl.class);
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
    cm = ConfigurationManager.getInstance();
    statMan = new StatisticsManagerImpl(TestHelper.TEST_CVS_VALID_BUILD_ID);
  }
}
