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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.TickUnits;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.category.StackedAreaRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.PersistentDistribution;
import org.parabuild.ci.webui.common.Pages;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TimeZone;

/**
 */
public final class StatisticsUtils {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(StatisticsUtils.class); // NOPMD
  /**
   * Image width for month-to date stats.
   */
  public static final int IMG_WIDTH = Pages.PAGE_WIDTH - 30;

  /**
   * Image height for month-to date stats.
   */
  public static final int IMG_HEIGHT = 250;


  /**
   * Sroke used by line charts in Parabuild.
   */
  public static final Stroke DEFAULT_LINE_STROKE = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND);

  /**
   * Reversed order Comparator for for dates.
   */
  public static final Comparator REVERSE_DATE_COMPARATOR = new Comparator() {
    public int compare(final Object o1, final Object o2) {
      return -1 * NATURAL_DATE_COMPARATOR.compare(o1, o2);
    }
  };

  /**
   * Reversed order Comparator for for dates.
   */
  public static final Comparator NATURAL_DATE_COMPARATOR = new Comparator() {
    public int compare(final Object o1, final Object o2) {
      return ((Date) o1).compareTo((Date) o2);
    }
  };

  /**
   * Natural integer comparator.
   */
  public static final Comparator INTEGER_COMPARATOR = new Comparator() {
    public int compare(final Object o1, final Object o2) {
      return ((Integer) o1).compareTo((Integer) o2);
    }
  };

  private static final String CAPTION_FAILED_TESTS = "Failed tests";
  private static final String CAPTION_ERROR_TESTS = "Error tests";
  private static final String CAPTION_SUCCESSFUL_TESTS = "Successful tests";


  private StatisticsUtils() {
  }


  /**
   * Creates a distribution image for build results.
   *
   * @param stats         SortedMap with dates as keys and
   *                      BuildStatistics as value.
   * @param categoryLabel - label to place on X axis.
   * @param out           OutputStream to write image to.
   */
  public static void createBuildResultsBarChart(final SortedMap stats, final String categoryLabel,
                                                final String dateFormat, final OutputStream out) throws IOException {

    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (final Iterator iter = stats.entrySet().iterator(); iter.hasNext();) {
      final Map.Entry entry = (Map.Entry) iter.next();
      final Date date = (Date) entry.getKey();
      final BuildStatistics bst = (BuildStatistics) entry.getValue();
      final String dateAsString = StringUtils.formatDate(date, dateFormat);
      dataset.addValue(new Integer(bst.getFailedBuilds()), "Failed builds", dateAsString);
      dataset.addValue(new Integer(bst.getSuccessfulBuilds()), "Successful builds", dateAsString);
    }

    // create the chart object

    // This generates a stacked bar - more suitable
    final JFreeChart chart = ChartFactory.createStackedBarChart(null,
            categoryLabel, "Builds", dataset,
            PlotOrientation.VERTICAL,
            true, false, false);
    chart.setBackgroundPaint(Color.white);

    // change the auto tick unit selection to integer units only
    final CategoryPlot plot = chart.getCategoryPlot();
    final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    // rotate X dates
    final CategoryAxis domainAxis = plot.getDomainAxis();
    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

    // set bar colors

    final BarRenderer bar = (BarRenderer) plot.getRenderer();
    bar.setItemMargin(0); // reduce the width between the bars.
    bar.setSeriesPaint(0, Color.RED); // first bar
    bar.setSeriesPaint(1, Color.GREEN); // second bar
    //plot.setRenderer(bar);

    // write to reposnce
    final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
    ChartUtilities.writeChartAsPNG(out, chart, IMG_WIDTH, IMG_HEIGHT, info);
  }


  /**
   * Test results chart.
   *
   * @param stats         SortedMap with dates as keys and
   *                      TestStatistics as value.
   * @param categoryLabel - label to place on X axis.
   * @param out           OutputStream to write image to.
   */
  public static void createTestResultsChart(final SortedMap stats, final String categoryLabel,
                                            final String dateFormat, final OutputStream out) throws IOException {

    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (final Iterator iter = stats.entrySet().iterator(); iter.hasNext();) {
      final Map.Entry entry = (Map.Entry) iter.next();
      final Date date = (Date) entry.getKey();
      final TestStatistics tst = (TestStatistics) entry.getValue();
      if (tst.getTotalTests() == 0) continue; // skip no-test values
      final String dateAsString = StringUtils.formatDate(date, dateFormat);
      dataset.addValue(new Integer(tst.getAverageFailedTests()), CAPTION_FAILED_TESTS, dateAsString);
      dataset.addValue(new Integer(tst.getAverageErrorTests()), CAPTION_ERROR_TESTS, dateAsString);
      dataset.addValue(new Integer(tst.getAverageSuccessfulTests()), CAPTION_SUCCESSFUL_TESTS, dateAsString);
    }

    // create the chart object
    createTestsResultsChartHelper(categoryLabel, dataset, out, CategoryLabelPositions.UP_45);
  }


  /**
   * Test results chart.
   *
   * @param stats         SortedMap with dates as keys and
   *                      TestStatistics as value.
   * @param categoryLabel - label to place on X axis.
   * @param out           OutputStream to write image to.
   */
  public static void createTestResultsChart(final SortedMap stats, final String categoryLabel,
                                            final OutputStream out) throws IOException {

    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (final Iterator iter = stats.entrySet().iterator(); iter.hasNext();) {
      final Map.Entry entry = (Map.Entry) iter.next();
      final String buildNumberAsString = entry.getKey().toString();
      final TestStatistics tst = (TestStatistics) entry.getValue();
      final Integer failed = new Integer(tst.getFailedTests());
      final Integer error = new Integer(tst.getErrorTests());
      final Integer successful = new Integer(tst.getSuccessfulTests());
      dataset.addValue(failed, CAPTION_FAILED_TESTS, buildNumberAsString);
      dataset.addValue(error, CAPTION_ERROR_TESTS, buildNumberAsString);
      dataset.addValue(successful, CAPTION_SUCCESSFUL_TESTS, buildNumberAsString);
    }

    // create the chart object
    createTestsResultsChartHelper(categoryLabel, dataset, out, CategoryLabelPositions.UP_45);
  }


  /**
   *
   */
  private static void createTestsResultsChartHelper(final String categoryLabel, final DefaultCategoryDataset dataset, final OutputStream out, final CategoryLabelPositions categoryLabelPosition) throws IOException {
    final JFreeChart chart = ChartFactory.createStackedAreaChart(null,
            categoryLabel, "Tests", dataset,
            PlotOrientation.VERTICAL,
            true, false, false);
    chart.setBackgroundPaint(Color.white);

    // change the auto tick unit selection to integer units only
    final CategoryPlot plot = chart.getCategoryPlot();
    final LogarithmicAxis logarithmicAxis = new LogarithmicAxis("Tests");
    logarithmicAxis.setStrictValuesFlag(false);
    logarithmicAxis.setAutoRangeIncludesZero(true);
    logarithmicAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    plot.setRangeAxis(logarithmicAxis);
//    final NumberAxis rangeAxis = (NumberAxis)plot.getRangeAxis();

    // rotate X dates
    final CategoryAxis domainAxis = plot.getDomainAxis();
    domainAxis.setCategoryLabelPositions(categoryLabelPosition);

    // set area colors

    final StackedAreaRenderer area = (StackedAreaRenderer) plot.getRenderer();
    area.setSeriesPaint(0, Color.RED); // first area
    area.setSeriesPaint(1, Color.PINK); // second area
    area.setSeriesPaint(2, Color.GREEN); // thirs area
    //plot.setRenderer(area);

    // write to reposnce
    final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
    ChartUtilities.writeChartAsPNG(out, chart, IMG_WIDTH, IMG_HEIGHT, info);
  }


  /**
   * Creates a distribution image for build results.
   *
   * @param stats         SortedMap with dates as keys and
   *                      BuildStatistics as value.
   * @param categoryLabel - label to place on X axis.
   * @param out           OutputStream to write image to.
   */
  public static void createChangeListsBarChart(final SortedMap stats, final String categoryLabel,
                                               final String dateFormat, final OutputStream out) throws IOException {

    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (final Iterator iter = stats.entrySet().iterator(); iter.hasNext();) {
      final Map.Entry entry = (Map.Entry) iter.next();
      final Date date = (Date) entry.getKey();
      final BuildStatistics bst = (BuildStatistics) entry.getValue();
      final String dateAsString = StringUtils.formatDate(date, dateFormat);
      dataset.addValue(new Integer(bst.getChangeLists()), "Change lists", dateAsString);
    }

    // create the chart object

    // This generates a stacked bar - more suitable
    final JFreeChart chart = ChartFactory.createStackedBarChart(null,
            categoryLabel, "Change lists", dataset,
            PlotOrientation.VERTICAL,
            true, false, false);
    chart.setBackgroundPaint(Color.white);

    // change the auto tick unit selection to integer units only
    final CategoryPlot plot = chart.getCategoryPlot();
    final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    // rotate X dates
    final CategoryAxis domainAxis = plot.getDomainAxis();
    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

    // set bar colors

    final BarRenderer bar = (BarRenderer) plot.getRenderer();
    bar.setItemMargin(0); // reduce the width between the bars.
    bar.setSeriesPaint(0, Color.BLUE); // first bar
    //bar.setSeriesPaint(1, Color.GREEN); // second bar

    // write to reposnce
    final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
    ChartUtilities.writeChartAsPNG(out, chart, IMG_WIDTH, IMG_HEIGHT, info);
  }


  public static void createHourlyBreakageDistributionChart(final SortedMap stats, final String categoryLabel, final OutputStream out) throws IOException {

    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (final Iterator iter = stats.entrySet().iterator(); iter.hasNext();) {
      final Map.Entry entry = (Map.Entry) iter.next();
      final Integer hour = (Integer) entry.getKey();
      final BuildStatistics bst = (BuildStatistics) entry.getValue();
      dataset.addValue(new Integer(bst.getFailedBuilds()), "Failed builds", hour);
    }

    // create the chart object

    // This generates a stacked bar - more suitable
    final JFreeChart chart = ChartFactory.createStackedBarChart(null,
            categoryLabel, "Builds", dataset,
            PlotOrientation.VERTICAL,
            true, false, false);
    chart.setBackgroundPaint(Color.white);

    // change the auto tick unit selection to integer units only
    final CategoryPlot plot = chart.getCategoryPlot();
    final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    // set bar colors
    final BarRenderer bar = (BarRenderer) plot.getRenderer();
    bar.setItemMargin(0); // reduce the width between the bars.
    bar.setSeriesPaint(0, Color.RED); // first bar

    // write to reposnce
    final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
    ChartUtilities.writeChartAsPNG(out, chart, IMG_WIDTH, IMG_HEIGHT, info);
  }


  public static void createRecentBuildTimesChart(final SortedMap stats, final String categoryLabel, final OutputStream out) throws IOException {

    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (final Iterator iter = stats.entrySet().iterator(); iter.hasNext();) {
      final Map.Entry entry = (Map.Entry) iter.next();
      final Integer buildNumber = (Integer) entry.getKey();
      final Integer timeInSeconds = (Integer) entry.getValue();
      dataset.addValue(timeInSeconds, "Build time", buildNumber);
    }

    // create the chart object

    // This generates a stacked bar - more suitable
    final JFreeChart chart = ChartFactory.createLineChart(null,
            categoryLabel, "Build time", dataset,
            PlotOrientation.VERTICAL,
            true, false, false);
    chart.setBackgroundPaint(Color.white);

    // change the auto tick unit selection to integer units only
    final CategoryPlot plot = chart.getCategoryPlot();
    final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    rangeAxis.setStandardTickUnits(createWordedTimeTickUnits());

    // rotate X dates
    final CategoryAxis domainAxis = plot.getDomainAxis();
    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

    // set bar colors

    final LineAndShapeRenderer line = (LineAndShapeRenderer) plot.getRenderer();
    line.setSeriesPaint(0, Color.BLUE);
    line.setStroke(DEFAULT_LINE_STROKE);

    // write to reposnce
    final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
    ChartUtilities.writeChartAsPNG(out, chart, IMG_WIDTH, IMG_HEIGHT, info);
  }


  private static final class ComparableDayOfWeek implements Comparable {

    private static final String DAY_OF_WEEK_FORMAT = "EEE";

    private final Integer dayOfWeek;
    private final String stringDayOfWeek;


    public ComparableDayOfWeek(final Integer dayOfWeek) {
      final Calendar c = Calendar.getInstance();
      c.clear();
      c.set(Calendar.DAY_OF_WEEK, dayOfWeek.intValue());
      this.stringDayOfWeek = new SimpleDateFormat(DAY_OF_WEEK_FORMAT, Locale.getDefault()).format(c.getTime());
      this.dayOfWeek = dayOfWeek;
    }


    /**
     * String form of day of week, i.e. Sat, Sun, e.t.c.
     */
    public String toString() {
      return stringDayOfWeek;
    }


    public boolean equals(final Object o) {
      if (this == o) return true;
      if (!(o instanceof ComparableDayOfWeek)) return false;

      final ComparableDayOfWeek comparableDayOfWeek = (ComparableDayOfWeek) o;

      return dayOfWeek.equals(comparableDayOfWeek.dayOfWeek);

    }


    public int hashCode() {
      return dayOfWeek.hashCode();
    }


    /**
     * Compares wrapped day of week.
     */
    public int compareTo(final Object o) {
      if (o instanceof ComparableDayOfWeek) return -1;
      if (!(o instanceof Integer)) return -1;
      return dayOfWeek.compareTo((Integer) o);
    }
  }


  public static void createDayOfWeekBreakageDistributionChart(final SortedMap stats, final String categoryLabel, final OutputStream out) throws IOException {


    final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    for (final Iterator iter = stats.entrySet().iterator(); iter.hasNext();) {
      final Map.Entry entry = (Map.Entry) iter.next();
      final Integer dayOfWeek = (Integer) entry.getKey();
      final BuildStatistics bst = (BuildStatistics) entry.getValue();
      dataset.addValue(new Integer(bst.getFailedBuilds()), "Failed builds", new ComparableDayOfWeek(dayOfWeek));
    }

    // create the chart object

    // This generates a stacked bar - more suitable
    final JFreeChart chart = ChartFactory.createStackedBarChart(null,
            categoryLabel, "Builds", dataset,
            PlotOrientation.VERTICAL,
            true, false, false);
    chart.setBackgroundPaint(Color.white);

    // change the auto tick unit selection to integer units only
    final CategoryPlot plot = chart.getCategoryPlot();
    final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    // set bar colors
    final BarRenderer bar = (BarRenderer) plot.getRenderer();
    bar.setItemMargin(0); // reduce the width between the bars.
    bar.setSeriesPaint(0, Color.RED); // first bar

    // write to reposnce
    final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
    ChartUtilities.writeChartAsPNG(out, chart, IMG_WIDTH, IMG_HEIGHT, info);
  }


  /**
   * Factory method.
   *
   * @param buildRun
   */
  public static BuildStatistics calculateBuildStatistics(final BuildRun buildRun) {
    if (!buildRun.completed()) return new BuildStatistics();

    final ConfigurationManager cm = ConfigurationManager.getInstance();
    // get build counts
    int successfulBuildCount = 0;
    int failedBuildCount = 0;
    if (buildRun.getResultID() == BuildRun.BUILD_RESULT_SUCCESS) {
      successfulBuildCount = 1;
    } else {
      failedBuildCount = 1;
    }

    // get change list count
    // REVIEWME: simeshev@parabuilci.org -> new only change lists
    final int changeListCount = cm.getNewBuildRunParticipantsCount(buildRun);

    // get issue counts
    // REVIEWME: simeshev@parabuilci.org -> new only issues
    final int issuesCount = cm.getBuildRunReleaseNotesCount(buildRun.getBuildRunID());

    return new BuildStatistics(successfulBuildCount, failedBuildCount, changeListCount, issuesCount);
  }


  /**
   * @param date
   * @param truncateAt
   */
  public static Date truncateDate(final Date date, final int truncateAt) {
    final Calendar source = Calendar.getInstance();
    source.clear();
    source.setTime(date);
    final Calendar result = Calendar.getInstance();
    result.clear();
    result.set(Calendar.YEAR, source.get(Calendar.YEAR));
    final TimeZone sourceTimeZone = source.getTimeZone();
    result.setTimeZone(sourceTimeZone);
    if (truncateAt == Calendar.HOUR_OF_DAY) {
      result.set(Calendar.HOUR_OF_DAY, source.get(Calendar.HOUR_OF_DAY));
      result.set(Calendar.MONTH, source.get(Calendar.MONTH));
      result.set(Calendar.DAY_OF_MONTH, source.get(Calendar.DAY_OF_MONTH));
      result.set(Calendar.DST_OFFSET, source.get(Calendar.DST_OFFSET));
    } else if (truncateAt == Calendar.DAY_OF_MONTH) {
      result.set(Calendar.MONTH, source.get(Calendar.MONTH));
      result.set(Calendar.DAY_OF_MONTH, source.get(Calendar.DAY_OF_MONTH));
//      if (log.isDebugEnabled()) log.debug("result.get(Calendar.HOUR_OF_DAY): " + result.get(Calendar.HOUR_OF_DAY));
    } else if (truncateAt == Calendar.MONTH) {
      result.set(Calendar.MONTH, source.get(Calendar.MONTH));
    } else if (truncateAt == Calendar.YEAR) { // NOPMD
      // do nothing
    } else { // NOPMD
      throw new IllegalArgumentException("Unknown truncateAt");
    }
    return result.getTime();
  }


  /**
   * Helper method to add build statistis to distribution.
   *
   * @param stats BuildStatistics to add to PersistentDistribution
   * @param pd    PersistentDistribution to add to.
   */
  public static void addStatsToDistribution(final BuildStatistics stats, final PersistentDistribution pd) {

    // make BuildStatistics that is up to date first
    final BuildStatistics result = new BuildStatistics(stats);
    result.addChangeLists(pd.getChangeListCount());
    result.addFailedBuilds(pd.getFailedBuildCount());
    result.addIssues(pd.getIssueCount());
    result.addSuccessfulBuilds(pd.getSuccessfulBuildCount());

    // make store
    pd.setChangeListCount(result.getChangeLists());
    pd.setFailedBuildCount(result.getFailedBuilds());
    pd.setIssueCount(result.getIssues());
    pd.setSuccessfulBuildCount(result.getSuccessfulBuilds());
    pd.setTotalBuildCount(result.getTotalBuilds());
  }


  /**
   * Returns a collection of tick units for integer values.
   */
  public static TickUnitSource createWordedTimeTickUnits() {

    final TickUnits units = new TickUnits();
    units.add(new WordedTimeTickUnit(1));
    units.add(new WordedTimeTickUnit(2));
    units.add(new WordedTimeTickUnit(5));
    units.add(new WordedTimeTickUnit(10));
    units.add(new WordedTimeTickUnit(20));
    units.add(new WordedTimeTickUnit(50));
    units.add(new WordedTimeTickUnit(100));
    units.add(new WordedTimeTickUnit(200));
    units.add(new WordedTimeTickUnit(500));
    units.add(new WordedTimeTickUnit(1000));
    units.add(new WordedTimeTickUnit(2000));
    units.add(new WordedTimeTickUnit(5000));
    units.add(new WordedTimeTickUnit(10000));
    units.add(new WordedTimeTickUnit(20000));
    units.add(new WordedTimeTickUnit(50000));
    units.add(new WordedTimeTickUnit(100000));
    units.add(new WordedTimeTickUnit(200000));
    units.add(new WordedTimeTickUnit(500000));
    units.add(new WordedTimeTickUnit(1000000));
    units.add(new WordedTimeTickUnit(2000000));
    units.add(new WordedTimeTickUnit(5000000));
    units.add(new WordedTimeTickUnit(10000000));
    units.add(new WordedTimeTickUnit(20000000));
    units.add(new WordedTimeTickUnit(50000000));
    units.add(new WordedTimeTickUnit(100000000));
    units.add(new WordedTimeTickUnit(200000000));
    units.add(new WordedTimeTickUnit(500000000));
    units.add(new WordedTimeTickUnit(1000000000));
    units.add(new WordedTimeTickUnit(2000000000));
    units.add(new WordedTimeTickUnit(5000000000.0));
    units.add(new WordedTimeTickUnit(10000000000.0));
    return units;
  }


  /**
   * Requires to extend NumberTickUnit. Otherwise a class
   * cast exception is thrown from deep guts of JFreeChart.
   */
  private static final class WordedTimeTickUnit extends NumberTickUnit {

    public WordedTimeTickUnit(final double size) {
      super(size);
    }


    /**
     * Converts the supplied value to a string.
     * <p/>
     * Subclasses may implement special formatting by overriding this method.
     *
     * @param value the data value.
     * @return Value as string.
     */
    public String valueToString(final double value) {
      return StringUtils.durationToString((long) value, false).toString();
    }
  }

}
