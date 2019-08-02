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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * Created by IntelliJ IDEA.
 * User: vimeshev
 * Date: Jul 4, 2006
 * Time: 7:34:19 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractBuildStatisticsPage extends BasePage {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(AbstractBuildStatisticsPage.class); // NOPMD

  protected static final String TITLE = "Statistics And Metrics";
  private static final long serialVersionUID = -1368751070420362282L;

  private final UpToDateStatisticsPanel upToDateStatisticsPanel = new UpToDateStatisticsPanel();
  private final StatisticsLink lnkSelectAll = new StatisticsLink("All");
  private final StatisticsLink lnkSelectBuildHistory = new StatisticsLink("Build History");
  private final StatisticsLink lnkSelectMTD = new StatisticsLink("This Month");
  private final StatisticsLink lnkSelectYTD = new StatisticsLink("This Year");
  private final StatisticsLink lnkSelectBreakage = new StatisticsLink("Build Breakage Distribution");
  private final StatisticsLink lnkSelectTestsStatistics = new StatisticsLink("Tests");
  private final StatisticsLink lnkTimeToFix = new StatisticsLink("Time To Fix");  // NOPMD
  // PMD
  private final StatisticsLink lnkPMDViolations = new StatisticsLink("PMD");
  private final MenuDividerLabel lbPMDViolationsDivider = new MenuDividerLabel(true);
  // Findbugs
  private final StatisticsLink lnkFindbugsViolations = new StatisticsLink("Findbugs");
  private final MenuDividerLabel lbFindbugsViolationsDivider = new MenuDividerLabel(true);
  // Checkstyle
  private final StatisticsLink lnkCheckstyleViolations = new StatisticsLink("Checkstyle"); // NOPMD
  private final MenuDividerLabel lbCheckstyleViolationsDivider = new MenuDividerLabel(true);  // NOPMD

  private final MenuDividerLabel lbTimeToFix = new MenuDividerLabel(true);

  protected boolean pmdViolationsVisible = true;
  protected boolean checkstyleViolationsVisible = true;
  protected boolean findbugsViolationsVisible = true;


  protected AbstractBuildStatisticsPage() {
    super(FLAG_SHOW_QUICK_SEARCH | FLAG_FLOATING_WIDTH | FLAG_SHOW_PAGE_HEADER_LABEL);
    final Panel userPanel = baseContentPanel().getUserPanel();

    // Add common up to date statistics to page
    upToDateStatisticsPanel.setAlignX(Layout.CENTER);
    userPanel.add(upToDateStatisticsPanel);
    userPanel.add(WebuiUtils.makePanelDivider());

    // Add statistics selector
    final Flow flNavBar = new Flow();
    flNavBar.setWidth("100%");
    flNavBar.setBorder(Border.ALL, 1, Pages.COLOR_PANEL_BORDER);
    flNavBar.setBackground(new Color(0xfffff0));
    flNavBar.setHeight(25);
    flNavBar.add(lnkSelectAll);
    flNavBar.add(new MenuDividerLabel(true)).add(lnkSelectBuildHistory);
    flNavBar.add(lbPMDViolationsDivider).add(lnkPMDViolations);
    flNavBar.add(lbFindbugsViolationsDivider).add(lnkFindbugsViolations);
    flNavBar.add(lbCheckstyleViolationsDivider).add(lnkCheckstyleViolations);
    flNavBar.add(new MenuDividerLabel(true)).add(lnkSelectMTD);
    flNavBar.add(new MenuDividerLabel(true)).add(lnkSelectYTD);
    flNavBar.add(new MenuDividerLabel(true)).add(lnkSelectBreakage);
    flNavBar.add(new MenuDividerLabel(true)).add(lnkSelectTestsStatistics);
    flNavBar.add(lbTimeToFix).add(lnkTimeToFix);
    userPanel.add(flNavBar);
    userPanel.add(WebuiUtils.makePanelDivider());
  }


  public Result executePage(final Parameters params) {

    // validate build ID
    final ActiveBuildConfig buildConfig = ParameterUtils.getActiveBuildConfigFromParameters(params);
    if (buildConfig == null) {
      return WebuiUtils.showBuildNotFound(this);
    }
    final int activeBuildID = buildConfig.getActiveBuildID();

    // authorise
    if (!getUserRights(activeBuildID).isAllowedToViewBuild()) {
      return WebuiUtils.showNotAuthorized(this);
    }

    // set title
    setTitle(makeTitle(TITLE) + " >> " + buildConfig.getBuildName());

    // set page header
    setPageHeader("Statistics for " + buildConfig.getBuildName());

    // set build ID to header
    upToDateStatisticsPanel.setBuildID(activeBuildID);
    lnkSelectBuildHistory.setParameters(BuildStatisticsPage.STAT_CODE_RECENT_BUILD_TIME, activeBuildID);
    lnkSelectMTD.setParameters(BuildStatisticsPage.STAT_CODE_MONTHLY, buildConfig);
    lnkSelectYTD.setParameters(BuildStatisticsPage.STAT_CODE_YEARLY, buildConfig);
    lnkSelectBreakage.setParameters(BuildStatisticsPage.STAT_CODE_BREAKAGE_DISTRIBUTION, buildConfig);
    lnkSelectTestsStatistics.setParameters(BuildStatisticsPage.STAT_CODE_TESTS, buildConfig);
    lnkSelectAll.setParameters(BuildStatisticsPage.STAT_CODE_ALL, buildConfig);
    lnkSelectBuildHistory.setParameters(BuildStatisticsPage.STAT_CODE_RECENT_BUILD_TIME, buildConfig);
    lnkPMDViolations.setParameters(BuildStatisticsPage.STAT_CODE_PMD, buildConfig);
    lnkFindbugsViolations.setParameters(BuildStatisticsPage.STAT_CODE_FINDBUGS, buildConfig);
    lnkCheckstyleViolations.setParameters(BuildStatisticsPage.STAT_CODE_CHECKSTYLE, buildConfig);
    lnkTimeToFix.setParameters(BuildStatisticsPage.STAT_CODE_TIME_TO_FIX, buildConfig);

    // decide if we have to show PMD
    // To decide if we have to show the PMD link, we should check if any of this is valid:
    //  1. The current build configuration contains PMD XML log.
    //  2. The current build run attribute has PMD statistics
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final boolean pmdConfigured = !cm.getLogConfigs(activeBuildID, LogConfig.LOG_TYPE_PMD_XML_FILE).isEmpty();
    final boolean pmdPresent;
    final BuildRun lastCleanBuildRun = cm.getLastCleanBuildRun(activeBuildID);
    final BuildRun lastCompleteBuildRun = cm.getLastCompleteBuildRun(activeBuildID);
    if (lastCleanBuildRun == null) {
      pmdPresent = false;
    } else {
      pmdPresent = cm.getBuildRunAttribute(lastCleanBuildRun.getBuildRunID(), BuildRunAttribute.ATTR_PMD_PROBLEMS) != null;
    }
    if (!pmdConfigured && !pmdPresent) {
      lbPMDViolationsDivider.setVisible(false);
      lnkPMDViolations.setVisible(false);
      pmdViolationsVisible = false;
    }

    // decide if we have to show Findbugs
    // To decide if we have to show the Findbugs link, we should check if any of this is valid:
    //  1. The current build configuration contains Findbugs XML log.
    //  2. The current build run attribute has Findbugs statistics
    final boolean findbugsConfigured = !cm.getLogConfigs(activeBuildID, LogConfig.LOG_TYPE_FINDBUGS_XML_FILE).isEmpty();
    final boolean findbugsPresent;
    if (lastCleanBuildRun == null) {
      findbugsPresent = false;
    } else {
      findbugsPresent = cm.getBuildRunAttribute(lastCleanBuildRun.getBuildRunID(), BuildRunAttribute.ATTR_FINDBUGS_PROBLEMS) != null;
    }
    if (!findbugsConfigured && !findbugsPresent) {
      lbFindbugsViolationsDivider.setVisible(false);
      lnkFindbugsViolations.setVisible(false);
      findbugsViolationsVisible = false;
    }

    // decide if we have to show Checkstyle
    // To decide if we have to show the Checkstyle link, we should check if any of this is valid:
    //  1. The current build configuration contains Checkstyle XML log.
    //  2. The current build run attribute has Checkstyle statistics
    final boolean checkstyleConfigured = !cm.getLogConfigs(activeBuildID, LogConfig.LOG_TYPE_CHECKSTYLE_XML_FILE).isEmpty();
    if (log.isDebugEnabled()) {
      log.debug("checkstyleConfigured: " + checkstyleConfigured);
    }
    final boolean checkstylePresent;
    if (lastCleanBuildRun == null) {
      checkstylePresent = false;
      if (log.isDebugEnabled()) {
        log.debug("checkstylePresent based on build run: " + checkstylePresent);
      }
    } else {
      checkstylePresent = lastCompleteBuildRun != null && cm.getBuildRunAttribute(lastCompleteBuildRun.getBuildRunID(), BuildRunAttribute.ATTR_CHECKSTYLE_FILES) != null;
      if (log.isDebugEnabled()) {
        log.debug("checkstylePresent based on files: " + checkstylePresent);
      }
    }
    if (!checkstyleConfigured && !checkstylePresent) {
      lbCheckstyleViolationsDivider.setVisible(false);
      lnkCheckstyleViolations.setVisible(false);
      checkstyleViolationsVisible = false;
    }

    // add selected details
    return addDetails(baseContentPanel().getUserPanel(), buildConfig, params);
  }


  protected abstract Result addDetails(final Panel userPanel, final ActiveBuildConfig buildConfig, final Parameters params);

  public String toString() {
    return "AbstractBuildStatisticsPage{" +
            "upToDateStatisticsPanel=" + upToDateStatisticsPanel +
            ", lnkSelectAll=" + lnkSelectAll +
            ", lnkSelectBuildHistory=" + lnkSelectBuildHistory +
            ", lnkSelectMTD=" + lnkSelectMTD +
            ", lnkSelectYTD=" + lnkSelectYTD +
            ", lnkSelectBreakage=" + lnkSelectBreakage +
            ", lnkSelectTestsStatistics=" + lnkSelectTestsStatistics +
            ", lnkTimeToFix=" + lnkTimeToFix +
            ", lnkPMDViolations=" + lnkPMDViolations +
            ", lbPMDViolationsDivider=" + lbPMDViolationsDivider +
            ", lnkFindbugsViolations=" + lnkFindbugsViolations +
            ", lbFindbugsViolationsDivider=" + lbFindbugsViolationsDivider +
            ", lnkCheckstyleViolations=" + lnkCheckstyleViolations +
            ", lbCheckstyleViolationsDivider=" + lbCheckstyleViolationsDivider +
            ", lbTimeToFix=" + lbTimeToFix +
            ", pmdViolationsVisible=" + pmdViolationsVisible +
            ", checkstyleViolationsVisible=" + checkstyleViolationsVisible +
            ", findbugsViolationsVisible=" + findbugsViolationsVisible +
            '}';
  }
}
