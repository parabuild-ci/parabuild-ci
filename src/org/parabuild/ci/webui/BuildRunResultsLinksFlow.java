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

import java.util.*;

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.webui.common.BackToBuildListLink;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.MenuDividerLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.ResultsLink;
import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Link;

/**
 * BuildHistoryLinks incapsulates a change list link and log
 * links
 */
public final class BuildRunResultsLinksFlow extends Flow {

  private static final long serialVersionUID = 3956858657850044811L;


  /**
   * Default constructor.
   */
  public BuildRunResultsLinksFlow() {
  }


  /**
   * Constructor.
   */
  public BuildRunResultsLinksFlow(final BuildRun buildRun, final boolean backToBuildListLink) {
    setBuildRun(buildRun, backToBuildListLink);
  }


  /**
   * Constructor.
   *
   * @param buildRun to show result links for
   * @param buildListLink if true "back build list" link will be
   *        shown
   * @param buildHistoryLink if true "build history" link will be
   *        shown
   * @param showAllLogs if true all build log links will be shown, if
   *        false only main logs and error windows will be shown
   */
  public BuildRunResultsLinksFlow(final BuildRun buildRun, final boolean buildListLink,
    final boolean buildHistoryLink, final boolean showAllLogs) {
    setBuildRun(buildRun, buildListLink, buildHistoryLink, showAllLogs, false, false);
  }


  /**
   * Sets build run ID to show links for.
   *
   * @param buildRun to show result links for
   * @param buildListLink if true "back build list" link will be
   *        shown
   * @param buildHistoryLink if true "build history" link will be
   *        shown
   * @param showAllLogs if true all build log links will be shown, if
   *        false only main logs and error windows will be shown
   */
  public final void setBuildRun(final BuildRun buildRun, final boolean buildListLink,
    final boolean buildHistoryLink, final boolean showAllLogs, final boolean showConfigReport, final boolean buildRunDiffLink) {

    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final int buildRunID = buildRun.getBuildRunID();

    // changes link
    add(new CommonLink("Changes", Pages.BUILD_CHANGES, Pages.PARAM_BUILD_RUN_ID, buildRunID));

    // long links
    final LogLinks logLinks = new LogLinks(buildRunID, showAllLogs);
    if (logLinks.getLogCount() > 0) {
      add(new MenuDividerLabel());
      add(logLinks);
    }

    // build run config
    if (showConfigReport) {
      add(new MenuDividerLabel());
      add(new CommonLink("Configuration", Pages.BUILD_COFNIG_REPORT, Pages.PARAM_BUILD_RUN_ID, buildRunID));
    }

    // release notes link
    if (cm.buildRunIssuesExist(buildRun.getBuildRunID())) {
      add(new MenuDividerLabel());
      add(new CommonLink("Release Notes", Pages.RELEASE_NOTES, Pages.PARAM_BUILD_RUN_ID, buildRunID));
    }

    // build results link
    add(new MenuDividerLabel());
    add(new ResultsLink(buildRunID));

    // build run diff link
    if (buildRunDiffLink) {
      add(new MenuDividerLabel());
      add(new BuildRunDiffLink(buildRun));
    }

    // history link
    if (buildHistoryLink) {
      add(new MenuDividerLabel());
      add(new StatisticsLink("History", BuildStatisticsPage.STAT_CODE_HISTORY, buildRun.getActiveBuildID()));
      add(new MenuDividerLabel());
      add(new StatisticsLink("Statistics", BuildStatisticsPage.STAT_CODE_ALL, buildRun.getActiveBuildID()));
    }

    // build list
    if (buildListLink) {
      add(new MenuDividerLabel());
      add(new BackToBuildListLink());
    }
  }


  /**
   * Sets build run ID to show links for. Build history link is
   * shown
   */
  public final void setBuildRun(final BuildRun buildRun, final boolean showBuildListLink) {
    setBuildRun(buildRun, showBuildListLink, true, true, false, false);
  }


  /**
   * Sets all link's fonts to bold
   */
  private void setBoldLinkFont(final Flow flow) {
    final List components = flow.getComponents();
    for (int i = 0; i < components.size(); i++) {
      final Component comp = (Component)components.get(i);
      if (comp instanceof Link) {
        comp.setFont(Pages.FONT_COMMON_BOLD_LINK);
      } else if (comp instanceof Flow) {
        setBoldLinkFont((Flow)comp);
      }
    }
  }


  /**
   * Sets all link's fonts to bold
   */
  public void setBoldLinkFont() {
    setBoldLinkFont(this);
  }
}
