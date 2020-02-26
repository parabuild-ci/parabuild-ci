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

import java.io.*;
import java.util.*;
import org.apache.commons.logging.*;

import junit.framework.*;

import com.gargoylesoftware.base.testing.*;
import org.parabuild.ci.ServersideTestCase;
import org.parabuild.ci.TestHelper;
import org.parabuild.ci.util.*;
import org.parabuild.ci.error.*;
import org.parabuild.ci.object.*;

/**
 * Tests SSTestStatisticsUtils
 */
public class SSTestStatisticsUtils extends ServersideTestCase {

  private static final Log log = LogFactory.getLog(SSTestStatisticsUtils.class);

  private StatisticsManagerImpl statMan = null;
  private ErrorManager errorManager;


  public SSTestStatisticsUtils(final String s) {
    super(s);
  }


  /**
   *
   */
  public void test_createMTDDistributionImage() throws IOException {
    OutputStream os = null;
    try {
      final SortedMap monthToDateDistribution = statMan.getMonthToDateBuildStatistics();
      final File f = IoUtils.createTempFile("ddd", ".png", TestHelper.getTestTempDir());
      if (log.isDebugEnabled()) log.debug("f = " + f);
      os = new FileOutputStream(f);
      StatisticsUtils.createBuildResultsBarChart(monthToDateDistribution, "Test", "MM/DD/yyyy", os);
      IoUtils.closeHard(os);
      assertTrue(f.length() > 0);
    } finally {
      IoUtils.closeHard(os);
    }
  }


  /**
   *
   */
  public void test_createMTDTestDistributionImage() throws IOException {
    OutputStream os = null;
    try {
      final SortedMap monthToDateDistribution = statMan.getMonthToDateTestStatistics(PersistentTestStats.TYPE_JUNIT);
      final File f = IoUtils.createTempFile("test_image", ".png", TestHelper.getTestTempDir());
      if (log.isDebugEnabled()) log.debug("f = " + f);
      os = new FileOutputStream(f);
      StatisticsUtils.createTestResultsChart(monthToDateDistribution, "Test", "MM/DD/yyyy", os);
      IoUtils.closeHard(os);
      assertTrue(f.length() > 0);
    } finally {
      IoUtils.closeHard(os);
    }
  }


  /**
   *
   */
  public void test_createLatestTestImage() throws IOException {
    OutputStream os = null;
    try {
      final SortedMap recentTestStatistics = statMan.getRecentTestStatistics(PersistentTestStats.TYPE_JUNIT);
      final File f = IoUtils.createTempFile("test_image", ".png", TestHelper.getTestTempDir());
      if (log.isDebugEnabled()) log.debug("f = " + f);
      os = new FileOutputStream(f);
      StatisticsUtils.createTestResultsChart(recentTestStatistics, "Test", os);
      IoUtils.closeHard(os);
      assertTrue(f.length() > 0);
    } finally {
      IoUtils.closeHard(os);
    }
  }


  /**
   *
   */
  public void test_createYTDDistributionImage() throws IOException {
    OutputStream os = null;
    try {
      final SortedMap yearToDateDistribution = statMan.getYearToDateBuildStatistics();
      final File f = IoUtils.createTempFile("ddd", ".png", TestHelper.getTestTempDir());
      if (log.isDebugEnabled()) log.debug("f = " + f);
      os = new FileOutputStream(f);
      StatisticsUtils.createBuildResultsBarChart(yearToDateDistribution, "Test", "MM/yyyy", os);
      IoUtils.closeHard(os);
      assertTrue(f.length() > 0);
    } finally {
      IoUtils.closeHard(os);
    }
  }


  public void test_truncateHour() {
    final Calendar c = Calendar.getInstance();
    final Calendar target = Calendar.getInstance();
    target.clear();
    target.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), c.get(Calendar.HOUR_OF_DAY), 0, 0);

    final Date date = StatisticsUtils.truncateDate(c.getTime(), Calendar.HOUR_OF_DAY);
    final Calendar instance = Calendar.getInstance();
    instance.setTime(date);
    assertEquals(target, instance);
  }


  public void test_truncateDayOfMonth() {
    final Calendar c = Calendar.getInstance();
    final Calendar target = Calendar.getInstance();
    target.clear();
    target.set(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), 0, 0, 0);

    final Date date = StatisticsUtils.truncateDate(c.getTime(), Calendar.DAY_OF_MONTH);
    final Calendar instance = Calendar.getInstance();
    instance.setTime(date);
    assertEquals(target, instance);
  }


  public void test_truncateMonth() {
    final Calendar c = Calendar.getInstance();
    final Calendar target = Calendar.getInstance();
    target.clear();
    target.set(Calendar.YEAR, c.get(Calendar.YEAR));
    target.set(Calendar.MONTH, c.get(Calendar.MONTH));

    final Date date = StatisticsUtils.truncateDate(c.getTime(), Calendar.MONTH);
    final Calendar instance = Calendar.getInstance();
    instance.setTime(date);
    assertEquals(target, instance);
  }


  public void test_truncateYear() {
    final Calendar c = Calendar.getInstance();
    final Calendar target = Calendar.getInstance();
    target.clear();
    target.set(Calendar.YEAR, c.get(Calendar.YEAR));

    final Date date = StatisticsUtils.truncateDate(c.getTime(), Calendar.YEAR);
    final Calendar instance = Calendar.getInstance();
    instance.setTime(date);
    assertEquals(target, instance);
  }


  public void test_add() {
    final BuildStatistics buildStatistics = new BuildStatistics(2, 3, 4, 5);
    final HourlyDistribution hourlyDistribution = new HourlyDistribution();
    StatisticsUtils.addStatsToDistribution(buildStatistics, hourlyDistribution);
    assertEquals(4, hourlyDistribution.getChangeListCount());
    assertEquals(3, hourlyDistribution.getFailedBuildCount());
    assertEquals(2, hourlyDistribution.getSuccessfulBuildCount());
    assertEquals(5, hourlyDistribution.getIssueCount());
  }


  /**
   * Required by JUnit
   */
  public static TestSuite suite() {
    return new OrderedTestSuite(SSTestStatisticsUtils.class, new String[]{
      "test_truncateHour",
      "test_truncateDayOfMonth",
      "test_truncateYear",
      "test_truncateMonth"
    });
  }


  protected void tearDown() throws Exception {
    super.tearDown();
    assertEquals(0, errorManager.errorCount());
  }


  protected void setUp() throws Exception {
    super.setUp();
    errorManager = ErrorManagerFactory.getErrorManager();
    errorManager.clearAllActiveErrors();
    statMan = new StatisticsManagerImpl(TestHelper.TEST_CVS_VALID_BUILD_ID);
  }
}
