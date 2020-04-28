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

import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.DayOfWeekBuildBreakageDistributionPanel;
import org.parabuild.ci.webui.common.HourlyBuildBreakageDistributionPanel;
import org.parabuild.ci.webui.common.MonthToDateImageBuildStatisticsPanel;
import org.parabuild.ci.webui.common.MonthToDateImageChangeListsStatisticsPanel;
import org.parabuild.ci.webui.common.MonthToDateImageTestStatisticsPanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.RecentBuildTimesImageStatisticsPanel;
import org.parabuild.ci.webui.common.RecentTimeToFixImageStatisticsPanel;
import org.parabuild.ci.webui.common.RerecentTestsStatisticsPanel;
import org.parabuild.ci.webui.common.WebuiUtils;
import org.parabuild.ci.webui.common.YearToDateImageBuildStatisticsPanel;
import org.parabuild.ci.webui.common.YearToDateImageTestStatisticsPanel;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;

/**
 * This class is a generic error page
 */
public final class BuildStatisticsPage extends AbstractBuildStatisticsPage implements ConversationalTierlet {

  private static final long serialVersionUID = 5113054037570114790L; // NOPMD

  public static final String STAT_CODE_HISTORY = "history";
  public static final String STAT_CODE_MONTHLY = "monthly";
  public static final String STAT_CODE_YEARLY = "yearly";
  public static final String STAT_CODE_BREAKAGE_DISTRIBUTION = "brkgdst";
  public static final String STAT_CODE_TESTS = "tests";
  public static final String STAT_CODE_ALL = "all";
  public static final String STAT_CODE_RECENT_BUILD_TIME = "btime";
  public static final String STAT_CODE_PMD = "rcntpmd";
  public static final String STAT_CODE_CHECKSTYLE = "rcncheckstyle";
  public static final String STAT_CODE_FINDBUGS = "rcntfndbgs";
  public static final String STAT_CODE_TIME_TO_FIX = "ttf";


  /**
   * Constructor
   */
  public BuildStatisticsPage() {
    setTitle(makeTitle(TITLE));
  }


  protected Result addDetails(final Panel userPanel, final ActiveBuildConfig buildConfig, final Parameters params) {
    final String code = getStatsCodeFromParams(params);
    if (code.equals(STAT_CODE_HISTORY) || code.equals(STAT_CODE_RECENT_BUILD_TIME)) {
      final Parameters historyParams = new Parameters();
      historyParams.addParameter(Pages.PARAM_BUILD_ID, Integer.toString(buildConfig.getActiveBuildID()));
      return Result.Done(Pages.BUILD_HISTORY, historyParams);
    } else if (code.equals(STAT_CODE_MONTHLY)) {
      addMonthToDateStatistics(userPanel, buildConfig);
    } else if (code.equals(STAT_CODE_YEARLY)) {
      addYearToDateStatistics(userPanel, buildConfig);
    } else if (code.equals(STAT_CODE_BREAKAGE_DISTRIBUTION)) {
      addBuildBreakageDistribution(userPanel, buildConfig);
    } else if (code.equals(STAT_CODE_TESTS)) {
      addTestStatistics(userPanel, buildConfig);
    } else if (code.equals(STAT_CODE_PMD)) {
      addPMDStatistics(userPanel, buildConfig);
    } else if (code.equals(STAT_CODE_FINDBUGS)) {
      addFindbugsStatistics(userPanel, buildConfig);
    } else if (code.equals(STAT_CODE_CHECKSTYLE)) {
      addCheckstyleStatistics(userPanel, buildConfig);
    } else if (code.equals(STAT_CODE_TIME_TO_FIX)) {
      addTimeToFixStatistics(userPanel, buildConfig);
    } else if (code.equals(STAT_CODE_ALL)) {
      addMonthToDateStatistics(userPanel, buildConfig);
      addYearToDateStatistics(userPanel, buildConfig);
      addTestStatistics(userPanel, buildConfig);
      addPMDStatistics(userPanel, buildConfig);
      addFindbugsStatistics(userPanel, buildConfig);
      addCheckstyleStatistics(userPanel, buildConfig);
      addTimeToFixStatistics(userPanel, buildConfig);
      addRecentBuildTimesStatistics(userPanel, buildConfig);
      addBuildBreakageDistribution(userPanel, buildConfig);
    } else {
      userPanel.add(new BoldCommonLabel("Requested code is unknown."));
    }
    return Result.Done();
  }


  private static void addTestStatistics(final Panel userPanel, final ActiveBuildConfig buildConfig) {
    userPanel.add(new RerecentTestsStatisticsPanel(buildConfig));
    userPanel.add(WebuiUtils.makePanelDivider());
    userPanel.add(new MonthToDateImageTestStatisticsPanel(buildConfig));
    userPanel.add(WebuiUtils.makePanelDivider());
    userPanel.add(new YearToDateImageTestStatisticsPanel(buildConfig));
    userPanel.add(WebuiUtils.makePanelDivider());
  }


  private static void addBuildBreakageDistribution(final Panel userPanel, final ActiveBuildConfig buildConfig) {
    userPanel.add(new HourlyBuildBreakageDistributionPanel(buildConfig));
    userPanel.add(WebuiUtils.makePanelDivider());
    userPanel.add(new DayOfWeekBuildBreakageDistributionPanel(buildConfig));
    userPanel.add(WebuiUtils.makePanelDivider());
  }


  private static void addYearToDateStatistics(final Panel userPanel, final ActiveBuildConfig buildConfig) {
    userPanel.add(new YearToDateImageBuildStatisticsPanel(buildConfig));
    userPanel.add(WebuiUtils.makePanelDivider());
  }


  private static void addRecentBuildTimesStatistics(final Panel userPanel, final ActiveBuildConfig buildConfig) {
    userPanel.add(new RecentBuildTimesImageStatisticsPanel(buildConfig));
    userPanel.add(WebuiUtils.makePanelDivider());
  }


  private static void addMonthToDateStatistics(final Panel userPanel, final ActiveBuildConfig buildConfig) {
    userPanel.add(new MonthToDateImageBuildStatisticsPanel(buildConfig));
    userPanel.add(WebuiUtils.makePanelDivider());
    userPanel.add(new MonthToDateImageChangeListsStatisticsPanel(buildConfig));
    userPanel.add(WebuiUtils.makePanelDivider());
  }


  private void addPMDStatistics(final Panel userPanel, final ActiveBuildConfig buildConfig) {
    if (pmdViolationsVisible) {
      userPanel.add(new RecentPMDStatisticsPanel(buildConfig));
      userPanel.add(WebuiUtils.makePanelDivider());
    }
  }


  private void addCheckstyleStatistics(final Panel userPanel, final ActiveBuildConfig buildConfig) {
    if (checkstyleViolationsVisible) {
      userPanel.add(new RecentCheckstyleStatisticsPanel(buildConfig));
      userPanel.add(WebuiUtils.makePanelDivider());
    }
  }


  private static void addTimeToFixStatistics(final Panel userPanel, final ActiveBuildConfig buildConfig) {
    // REVIEWME: simeshev@parabuilci.org -> add visibility check
    userPanel.add(new RecentTimeToFixImageStatisticsPanel(buildConfig));
    userPanel.add(WebuiUtils.makePanelDivider());
  }


  private void addFindbugsStatistics(final Panel userPanel, final ActiveBuildConfig buildConfig) {
    if (findbugsViolationsVisible) {
      userPanel.add(new RecentFindbugsStatisticsPanel(buildConfig));
      userPanel.add(WebuiUtils.makePanelDivider());
    }
  }


  private static String getStatsCodeFromParams(final Parameters params) {
    String code = null;
    if (params.isParameterPresent(StatisticsLink.PARAM_STAT_CODE)) {
      code = params.getParameterValue(StatisticsLink.PARAM_STAT_CODE);
    } else {
      code = STAT_CODE_ALL;
    }
    return code;
  }
}
