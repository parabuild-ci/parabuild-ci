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

import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.PaginatorFlow;
import org.parabuild.ci.webui.common.RecentBuildTimesImageStatisticsPanel;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Button;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;

import java.util.List;
import java.util.Properties;

/**
 * This class is a generic error page
 */
public final class RecentBuildHistoryPage extends AbstractBuildStatisticsPage implements ConversationalTierlet {

  private static final long serialVersionUID = 5113054037570114790L; // NOPMD

  private static final int PAGE_LENGTH = 25;

  private final BuildHistoryFilterDropDown ddBuildHistoryFilter = new BuildHistoryFilterDropDown(); // NOPMD SingularField
  private final BuildHistoryTable historyTable = new BuildHistoryTable("Build Name"); // NOPMD SingularField
  private final Button btnApplyBuildHistoryFilter = new Button("Apply"); // NOPMD SingularField
  private final RecentBuildTimesImageStatisticsPanel pnlRecentBuildTimesImage = new RecentBuildTimesImageStatisticsPanel(); // NOPMD SingularField
  private final CommonFlow flwFilterSelection = new CommonFlow(new BoldCommonLabel("Filter: "), ddBuildHistoryFilter, btnApplyBuildHistoryFilter); // NOPMD SingularField
  private final Panel pnlTableControls = new Panel();


  /**
   * Constructor
   */
  public RecentBuildHistoryPage() {
    setTitle(makeTitle(TITLE));

    // set attrs
    pnlRecentBuildTimesImage.setAlignX(Layout.CENTER);
    btnApplyBuildHistoryFilter.setName("apply_history_filter");
    flwFilterSelection.setAlignX(Layout.RIGHT);
    flwFilterSelection.setWidth(190);
    pnlTableControls.setWidth("100%");
    pnlTableControls.add(flwFilterSelection, new Layout(1, 0));

    // align
    baseContentPanel().getUserPanel().add(pnlRecentBuildTimesImage);
    baseContentPanel().getUserPanel().add(WebuiUtils.makePanelDivider());
    baseContentPanel().getUserPanel().add(pnlTableControls);
  }


  protected Result addDetails(final Panel userPanel, final ActiveBuildConfig buildConfig, final Parameters parameters) {
    historyTable.setTitle("Build History for:  " + buildConfig.getBuildName());
    pnlRecentBuildTimesImage.setBuildConfig(buildConfig);

    // get filter code
    final int filterCode;
    if (btnApplyBuildHistoryFilter.isButtonPressed()) {
      filterCode = ddBuildHistoryFilter.getCode();
      // reset page number
      parameters.removeParameter(Pages.PARAM_PAGE_NUM);
    } else {
      final String filterValue = parameters.getParameterValue(Pages.PARAM_FILTER);
      if (StringUtils.isValidInteger(filterValue)) {
        filterCode = Integer.parseInt(filterValue);
      } else {
        filterCode = BuildHistoryFilterDropDown.CODE_ALL;
      }
      ddBuildHistoryFilter.setCode(filterCode);
    }

    // add according to the filter
    final int activeBuildID = buildConfig.getActiveBuildID();
    if (filterCode == (int) BuildHistoryFilterDropDown.CODE_ALL) {
      addAllBuilds(activeBuildID, filterCode, parameters);
    } else if (filterCode == (int) BuildHistoryFilterDropDown.CODE_FAILED) {
      addUnsuccessfulBuilds(activeBuildID, filterCode, parameters);
    } else if (filterCode == (int) BuildHistoryFilterDropDown.CODE_SUCCESSFUL) {
      addSuccessfulBuilds(activeBuildID, filterCode, parameters);
    } else {
      addAllBuilds(activeBuildID, filterCode, parameters);
    }
    return Result.Done();
  }


  /**
   * Adds all builds.
   */
  private void addAllBuilds(final int activeBuildID, final int code, final Parameters parameters) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final int count = cm.getCompletedBuildRunsCount(activeBuildID);
    final PaginatorFlow paginatorFlow = makePaginatorFlow(activeBuildID, code, count, parameters);
    final List buildRuns = cm.getCompletedBuildRuns(activeBuildID, (paginatorFlow.getSelectedPage() - 1) * PAGE_LENGTH, PAGE_LENGTH);
    addPaginatorAndTable(paginatorFlow, buildRuns);
  }


  /**
   * Adds successful builds.
   */
  private void addSuccessfulBuilds(final int activeBuildID, final int code, final Parameters parameters) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final int count = cm.getCompletedSuccessfulBuildRunsCount(activeBuildID);
    final PaginatorFlow paginatorFlow = makePaginatorFlow(activeBuildID, code, count, parameters);
    final List buildRuns = cm.getCompletedSuccessfulBuildRuns(activeBuildID, (paginatorFlow.getSelectedPage() - 1) * PAGE_LENGTH, PAGE_LENGTH);
    addPaginatorAndTable(paginatorFlow, buildRuns);
  }


  /**
   * Adds unsuccessful builds.
   */
  private void addUnsuccessfulBuilds(final int activeBuildID, final int code, final Parameters parameters) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final int count = cm.getCompletedUnsuccessfulBuildRunsCount(activeBuildID);
    final PaginatorFlow paginatorFlow = makePaginatorFlow(activeBuildID, code, count, parameters);
    final List buildRuns = cm.getCompletedUnsuccessfulBuildRuns(activeBuildID, (paginatorFlow.getSelectedPage() - 1) * PAGE_LENGTH, PAGE_LENGTH);
    addPaginatorAndTable(paginatorFlow, buildRuns);
  }


  /**
   * Helper method.
   */
  private static PaginatorFlow makePaginatorFlow(final int activeBuildID, final int code, final int count, final Parameters parameters) {
    final Properties p = new Properties();
    p.setProperty(Pages.PARAM_BUILD_ID, Integer.toString(activeBuildID));
    p.setProperty(Pages.PARAM_FILTER, Integer.toString(code));
    return new PaginatorFlow(Pages.BUILD_HISTORY, p, count, parameters, PAGE_LENGTH);
  }


  /**
   * Helper method.
   */
  private void addPaginatorAndTable(final PaginatorFlow paginatorFlow, final List buildRuns) {
    if (paginatorFlow.getPageCount() > 1) {
      pnlTableControls.add(paginatorFlow, new Layout(0, 0));
    }
    historyTable.populate(buildRuns);
    baseContentPanel().getUserPanel().add(historyTable);
  }


  public String toString() {
    return "RecentBuildHistoryPage{" +
            "ddBuildHistoryFilter=" + ddBuildHistoryFilter +
            ", historyTable=" + historyTable +
            ", btnApplyBuildHistoryFilter=" + btnApplyBuildHistoryFilter +
            ", pnlRecentBuildTimesImage=" + pnlRecentBuildTimesImage +
            ", flwFilterSelection=" + flwFilterSelection +
            ", pnlTableControls=" + pnlTableControls +
            '}';
  }
}
