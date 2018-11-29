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

import java.awt.*;
import java.io.*;
import java.util.*;
import javax.servlet.http.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.PersistentTestStats;
import org.parabuild.ci.security.AccessForbiddenException;
import org.parabuild.ci.security.BadRequestException;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.statistics.BuildTimeChartGenerator;
import org.parabuild.ci.statistics.FindbugsViolationsChartGenerator;
import org.parabuild.ci.statistics.PMDViolationsChartGenerator;
import org.parabuild.ci.statistics.StatisticsManager;
import org.parabuild.ci.statistics.StatisticsManagerFactory;
import org.parabuild.ci.statistics.StatisticsUtils;
import org.parabuild.ci.statistics.CheckstyleViolationsChartGenerator;
import org.parabuild.ci.webui.common.Pages;

/**
 * This servlet serves requests for build statistics presented as
 * images.
 * <p/>
 * The incoming requests are expected to come in the given
 * format:
 * <p/>
 * /parabuild/build/statistics/image/?buildid=<build_id>&statscode=<statistics_id>
 * <p/>
 * Example:
 * <p/>
 * /parabuild/build/statistics/image/?buildid=0&statscode=1
 * <p/>
 * where:
 * <p/>
 * - 0 is a build ID,
 * <p/>
 * - 1 is statistic code
 *
 * @see BuildStatisticsPage
 */
public final class StatisticsImageServlet extends HttpServlet {

  private static final long serialVersionUID = -8066817583590450367L; // NOPMD
  private static final Log log = LogFactory.getLog(StatisticsImageServlet.class);

  /**
   * Statistics code for build month-to-date image statistics.
   */
  public static final int STATISTICS_BUILD_IMAGE_MONTH_TO_DATE = 1;

  /**
   * Statistics code for build year-to-date image statistics.
   */
  public static final int STATISTICS_BUILD_IMAGE_YEAR_TO_DATE = 2;


  /**
   * Statistics code for change lists month-to-date image
   * statistics.
   */
  public static final int STATISTICS_CHANGE_LISTS_IMAGE_MONTH_TO_DATE = 3;

  /**
   * Statistics code for change lists year-to-date image
   * statistics.
   */
  public static final int STATISTICS_CHANGE_LISTS_IMAGE_YEAR_TO_DATE = 4;


  /**
   */
  public static final int STATISTICS_HOURLY_BREAKAGE_DISTRIBUTION_TO_DATE = 5;


  /**
   */
  public static final int STATISTICS_DAY_OF_WEEK_BREAKAGE_DISTRIBUTION_TO_DATE = 6;


  /**
   * Statistics code for change lists month-to-date image
   * statistics.
   */
  public static final int STATISTICS_TESTS_IMAGE_MONTH_TO_DATE = 7;


  /**
   * Statistics code for change lists year-to-date image
   * statistics.
   */
  public static final int STATISTICS_TESTS_IMAGE_YEAR_TO_DATE = 8;


  /**
   * Statistics code for tests image containing infromation for
   * test on a per-build run (last NN runs).
   */
  public static final int STATISTICS_TESTS_IMAGE_RECENT_BUILDS = 9;


  /**
   * Statistics code for build image containing information on recent build times.
   */
  public static final int STATISTICS_RECENT_BUILD_TIMES_IMAGE = 10;


  /**
   * Statistics code for recent PMD violations.
   */
  public static final int STATISTICS_PMD_IMAGE_RECENT_BUILDS = 11;


  /**
   * Statistics code for recent PMD violations.
   */
  public static final int STATISTICS_FINDBUGS_IMAGE_RECENT_BUILDS = 12;


  /**
   * Statistics code for recent time to fix
   */
  public static final int STATISTICS_RECENT_TIME_TO_FIX_IMAGE = 14;


  /**
   * Statistics code for recent time to fix
   */
  public static final int STATISTICS_RECENT_TIME_TO_FIX_TREND_IMAGE = 15;

  /**
   * Statistics code for recent Checkstyle violations.
   */
  public static final int STATISTICS_CHECKSTYLE_IMAGE_RECENT_BUILDS = 16;


  /**
   * Overrides HttpServlet
   */
  protected void doGet(final HttpServletRequest req, final HttpServletResponse resp) throws IOException {
    try {
      processRequest(req, resp);
    } catch (final AccessForbiddenException e) {
      resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
    } catch (final BadRequestException e) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    } catch (final IOException e) {
      if (!e.toString().contains("I/O error writing PNG file")) {
        reportException(e);
        throw e;
      }
    } catch (final RuntimeException e) {
      reportException(e);
      throw e;
    }
  }


  /**
   * Processes request for statistics chart.
   */
  private void processRequest(final HttpServletRequest req, final HttpServletResponse resp) throws IOException, AccessForbiddenException, BadRequestException {

    final SecurityManager sm = SecurityManager.getInstance();
    final BuildConfig buildConfig = sm.getBuildConfigurationFromRequest(req);

    // get stats code param
    final String stringStatsCode = req.getParameter(Pages.PARAM_STATS_CODE);

    // validate stats code param
    if (!StringUtils.isValidInteger(stringStatsCode)) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    // get stats code
    final int statsCode = Integer.parseInt(stringStatsCode);

    // validate stats code
    if (statsCode != STATISTICS_BUILD_IMAGE_MONTH_TO_DATE
      && statsCode != STATISTICS_BUILD_IMAGE_YEAR_TO_DATE
      && statsCode != STATISTICS_CHANGE_LISTS_IMAGE_MONTH_TO_DATE
      && statsCode != STATISTICS_CHANGE_LISTS_IMAGE_YEAR_TO_DATE
      && statsCode != STATISTICS_HOURLY_BREAKAGE_DISTRIBUTION_TO_DATE
      && statsCode != STATISTICS_DAY_OF_WEEK_BREAKAGE_DISTRIBUTION_TO_DATE
      && statsCode != STATISTICS_TESTS_IMAGE_MONTH_TO_DATE
      && statsCode != STATISTICS_TESTS_IMAGE_YEAR_TO_DATE
      && statsCode != STATISTICS_TESTS_IMAGE_RECENT_BUILDS
      && statsCode != STATISTICS_RECENT_BUILD_TIMES_IMAGE
      && statsCode != STATISTICS_PMD_IMAGE_RECENT_BUILDS
      && statsCode != STATISTICS_RECENT_TIME_TO_FIX_IMAGE
      && statsCode != STATISTICS_RECENT_TIME_TO_FIX_TREND_IMAGE
      && statsCode != STATISTICS_FINDBUGS_IMAGE_RECENT_BUILDS
      && statsCode != STATISTICS_CHECKSTYLE_IMAGE_RECENT_BUILDS
      ) {
      resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    // set response content type
    resp.setContentType("image/png");

    // get statistics
    final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();
    final StatisticsManager stm = StatisticsManagerFactory.getStatisticsManager(buildConfig.getBuildID());
    SortedMap stats = null;
    String categoryLabel = null;
    String dateFormat = null;
    switch (statsCode) {
      case STATISTICS_BUILD_IMAGE_MONTH_TO_DATE:
        // preExecute
        stats = stm.getMonthToDateBuildStatistics();
        categoryLabel = "Date";
        dateFormat = systemCM.getDateFormat();
        // generate image based on gathered data
        createAndOutputBuildResultBarChart(resp, stats, categoryLabel, dateFormat);
        break;
      case STATISTICS_TESTS_IMAGE_MONTH_TO_DATE:
        // preExecute
        stats = stm.getMonthToDateTestStatistics(PersistentTestStats.TYPE_JUNIT); // currently JUnit only
        categoryLabel = "Date";
        dateFormat = systemCM.getDateFormat();
        // generate image based on gathered data
        createAndOutputTestResultsChart(resp, stats, categoryLabel, dateFormat);
        break;
      case STATISTICS_TESTS_IMAGE_YEAR_TO_DATE:
        // preExecute
        stats = stm.getYearToDateTestStatistics(PersistentTestStats.TYPE_JUNIT); // currently JUnit only
        categoryLabel = "Date";
        dateFormat = "MMM yyyy";
        // generate image based on gathered data
        createAndOutputTestResultsChart(resp, stats, categoryLabel, dateFormat);
        break;
      case STATISTICS_TESTS_IMAGE_RECENT_BUILDS:
        // preExecute
        stats = stm.getRecentTestStatistics(PersistentTestStats.TYPE_JUNIT); // currently JUnit only
        categoryLabel = "Recent tests";
        // generate image based on gathered data
        createAndOutputTestResultsChart(resp, stats, categoryLabel);
        break;
      case STATISTICS_RECENT_BUILD_TIMES_IMAGE:
        // preExecute
        stats = stm.getRecentBuildTimesStatistics();
        categoryLabel = "Recent builds";
        createAndOutputRecentBuildTimesChart(resp, stats, categoryLabel);
        break;
      case STATISTICS_BUILD_IMAGE_YEAR_TO_DATE:
        // preExecute
        stats = stm.getYearToDateBuildStatistics();
        categoryLabel = "Month";
        dateFormat = "MM/yyyy";
        // generate image based on gathered data
        createAndOutputBuildResultBarChart(resp, stats, categoryLabel, dateFormat);
        break;
      case STATISTICS_CHANGE_LISTS_IMAGE_MONTH_TO_DATE:
        // preExecute
        stats = stm.getMonthToDateBuildStatistics();
        categoryLabel = "Date";
        dateFormat = systemCM.getDateFormat();
        // generate image based on gathered data
        createAndOutputChangeListBarChart(resp, stats, categoryLabel, dateFormat);
        break;
      case STATISTICS_CHANGE_LISTS_IMAGE_YEAR_TO_DATE:
        // preExecute
        stats = stm.getYearToDateBuildStatistics();
        categoryLabel = "Month";
        dateFormat = "MM/yyyy";
        // generate image based on gathered data
        createAndOutputChangeListBarChart(resp, stats, categoryLabel, dateFormat);
        break;
      case STATISTICS_HOURLY_BREAKAGE_DISTRIBUTION_TO_DATE:
        // preExecute
        stats = stm.getHourlyDistribution();
        categoryLabel = "Hour";
        // generate image based on gathered data
        createAndOutputHourlyBreakageDistributionChart(resp, stats, categoryLabel);
        break;
      case STATISTICS_DAY_OF_WEEK_BREAKAGE_DISTRIBUTION_TO_DATE:
        // preExecute
        stats = stm.getDayOfWeekDistribution();
        categoryLabel = "Day of week";
        // generate image based on gathered data
        createAndOutputDayOfWeekBreakageDistributionChart(resp, stats, categoryLabel);
        break;
      case STATISTICS_PMD_IMAGE_RECENT_BUILDS:
        createAndOutputPMDViolatiosChart(resp, stm);
        break;
      case STATISTICS_CHECKSTYLE_IMAGE_RECENT_BUILDS:
        createAndOutputCheckstyleViolatiosChart(resp, stm);
        break;
      case STATISTICS_FINDBUGS_IMAGE_RECENT_BUILDS:
        createAndOutputFindbugsViolatiosChart(resp, stm);
        break;
      case STATISTICS_RECENT_TIME_TO_FIX_IMAGE:
        createAndOutputRecentTimeToFixChart(resp, stm);
        break;
      case STATISTICS_RECENT_TIME_TO_FIX_TREND_IMAGE:
        createAndOutputRecentTimeToFixTrendChart(resp, stm);
        break;
      default:
        // we don't know how to process, return
        log.warn("Unknown stats code: " + statsCode);
        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return;
    }
  }


  private void createAndOutputRecentTimeToFixChart(final HttpServletResponse resp, final StatisticsManager stm) throws IOException {
    OutputStream out = null;
    try {
      out = new BufferedOutputStream(resp.getOutputStream());
      new BuildTimeChartGenerator().createChart(stm.getRecentTimeToFix(), "Time To Fix", Color.RED, out);
    } finally {
      IoUtils.closeHard(out);
    }
  }


  private void createAndOutputRecentTimeToFixTrendChart(final HttpServletResponse resp, final StatisticsManager stm) throws IOException {
    OutputStream out = null;
    try {
      out = new BufferedOutputStream(resp.getOutputStream());
      new BuildTimeChartGenerator().createChart(stm.getRecentTimeToFixMovingAverage(), "Time To Fix Trend", Color.RED, out);
    } finally {
      IoUtils.closeHard(out);
    }
  }


  private void createAndOutputPMDViolatiosChart(final HttpServletResponse resp, final StatisticsManager stm) throws IOException {
    OutputStream out = null;
    try {
      out = new BufferedOutputStream(resp.getOutputStream());
      new PMDViolationsChartGenerator().createChart(stm.getRecentPMDViolations(), out);
    } finally {
      IoUtils.closeHard(out);
    }
  }


  private void createAndOutputCheckstyleViolatiosChart(final HttpServletResponse resp, final StatisticsManager stm) throws IOException {
    OutputStream out = null;
    try {
      out = new BufferedOutputStream(resp.getOutputStream());
      new CheckstyleViolationsChartGenerator().createChart(stm.getRecentCheckstyleViolations(), out);
    } finally {
      IoUtils.closeHard(out);
    }
  }


  private void createAndOutputFindbugsViolatiosChart(final HttpServletResponse resp, final StatisticsManager stm) throws IOException {
    OutputStream out = null;
    try {
      out = new BufferedOutputStream(resp.getOutputStream());
      new FindbugsViolationsChartGenerator().createChart(stm.getRecentFindbugsViolations(), out);
    } finally {
      IoUtils.closeHard(out);
    }
  }


  private void createAndOutputRecentBuildTimesChart(final HttpServletResponse resp, final SortedMap stats, final String categoryLabel) throws IOException {
    OutputStream out = null;
    try {
      // cover-ass validation
      validateStats(stats);
      // generate
      out = new BufferedOutputStream(resp.getOutputStream());
      StatisticsUtils.createRecentBuildTimesChart(stats, categoryLabel, out);
    } finally {
      IoUtils.closeHard(out);
    }
  }


  private void createAndOutputBuildResultBarChart(final HttpServletResponse resp, final SortedMap stats, final String categoryLabel, final String dateFormat) throws IOException {
    OutputStream out = null;
    try {
      // cover-ass validation
      validateDateFormat(dateFormat);
      validateStats(stats);
      // generate
      out = new BufferedOutputStream(resp.getOutputStream());
      StatisticsUtils.createBuildResultsBarChart(stats, categoryLabel, dateFormat, out);
    } finally {
      IoUtils.closeHard(out);
    }
  }


  private void createAndOutputTestResultsChart(final HttpServletResponse resp, final SortedMap stats, final String categoryLabel, final String dateFormat) throws IOException {
    OutputStream out = null;
    try {
      // cover-ass validation
      validateDateFormat(dateFormat);
      validateStats(stats);
      // generate
      out = new BufferedOutputStream(resp.getOutputStream());
      StatisticsUtils.createTestResultsChart(stats, categoryLabel, dateFormat, out);
    } finally {
      IoUtils.closeHard(out);
    }
  }


  private void createAndOutputTestResultsChart(final HttpServletResponse resp, final SortedMap stats, final String categoryLabel) throws IOException {
    OutputStream out = null;
    try {
      validateStats(stats);
      // generate
      out = new BufferedOutputStream(resp.getOutputStream());
      StatisticsUtils.createTestResultsChart(stats, categoryLabel, out);
    } finally {
      IoUtils.closeHard(out);
    }
  }


  private void createAndOutputChangeListBarChart(final HttpServletResponse resp, final SortedMap stats, final String categoryLabel, final String dateFormat) throws IOException {
    OutputStream out = null;
    try {
      // cover-ass validation
      validateDateFormat(dateFormat);
      validateStats(stats);
      // generate
      out = new BufferedOutputStream(resp.getOutputStream());
      StatisticsUtils.createChangeListsBarChart(stats, categoryLabel, dateFormat, out);
    } finally {
      IoUtils.closeHard(out);
    }
  }


  private void createAndOutputHourlyBreakageDistributionChart(final HttpServletResponse resp, final SortedMap stats, final String categoryLabel) throws IOException {
    OutputStream out = null;
    try {
      // cover-ass validation
      validateStats(stats);
      // generate
      out = new BufferedOutputStream(resp.getOutputStream());
      StatisticsUtils.createHourlyBreakageDistributionChart(stats, categoryLabel, out);
    } finally {
      IoUtils.closeHard(out);
    }
  }


  private void createAndOutputDayOfWeekBreakageDistributionChart(final HttpServletResponse resp, final SortedMap stats, final String categoryLabel) throws IOException {
    OutputStream out = null;
    try {
      // cover-ass validation
      validateStats(stats);
      // generate
      out = new BufferedOutputStream(resp.getOutputStream());
      StatisticsUtils.createDayOfWeekBreakageDistributionChart(stats, categoryLabel, out);
    } finally {
      IoUtils.closeHard(out);
    }
  }


  private void validateStats(final SortedMap stats) {
    if (stats == null) throw new IllegalStateException("Statistics was not defined");
  }


  private void validateDateFormat(final String dateFormat) {
    if (StringUtils.isBlank(dateFormat)) throw new IllegalStateException("Date format was not defined");
  }


  /**
   * Helper method to report unexpected exceptions.
   *
   * @param e
   */
  private void reportException(final Exception e) {
    final Error error = new Error("Error returning an image: " + StringUtils.toString(e));
    error.setDetails(e);
    error.setSendEmail(false);
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_WEBUI);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }
}
