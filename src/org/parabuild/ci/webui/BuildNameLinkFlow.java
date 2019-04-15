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

import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Flow;
import viewtier.ui.Font;
import viewtier.ui.Label;

import java.util.Properties;
import java.util.Set;

/**
 * Shows build name link possible aligned if parallel
 */
public final class BuildNameLinkFlow extends Flow {

  private static final long serialVersionUID = -2081033420095831281L;


  public void setBuildState(final BuildState buildState, final Set leaderIDs) {
    clear();

    final Properties params = new Properties();
    params.setProperty(Pages.PARAM_BUILD_ID, buildState.getBuildIDAsString());
    params.setProperty(Pages.PARAM_STATUS_VIEW, Pages.STATUS_VIEW_DETAILED);
    final BuildNameLink lnkBuildName = new BuildNameLink();
    lnkBuildName.setUrl(Pages.PUBLIC_BUILDS);
    lnkBuildName.setText(WebuiUtils.getBuildName(buildState));
    lnkBuildName.setParameters(params);
    if (buildState.isParallel() && leaderIDs != null && !leaderIDs.isEmpty()) {
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final Integer leaderBuildID = new Integer(cm.getSourceControlSettingValue(buildState.getActiveBuildID(), SourceControlSetting.REFERENCE_BUILD_ID, BuildConfig.UNSAVED_ID));
      if (leaderIDs.contains(leaderBuildID)) {
        add(new Label("&nbsp;&nbsp;&nbsp;")).add(lnkBuildName);
      } else {
        add(lnkBuildName);
      }
    } else {
      add(lnkBuildName);
    }
    final BuildRun lastCompleteBuildRun = buildState.getLastCompleteBuildRun();
    if (WebuiUtils.isBuildRunNotNullAndComplete(lastCompleteBuildRun)) {
      lnkBuildName.setForeground(WebuiUtils.getBuildResultColor(getTierletContext(), lastCompleteBuildRun));
    }
  }


  public void setBuildState(final BuildState state) {
    setBuildState(state, null);
  }


  /**
   * Specialized link to display build in the build statuses
   * table - is not underlined.
   */
  private static final class BuildNameLink extends CommonLink {

    public static final Font FONT_BUILD_NAME_LINK = new Font(Pages.COMMON_FONT_FAMILY, Font.Bold | Font.None, Pages.COMMMON_FONT_SIZE);
    private static final long serialVersionUID = -7373968932717137028L;


    /**
     * Constructor.
     */
    BuildNameLink() {
      super("", "");
      setFont(FONT_BUILD_NAME_LINK);
    }
  }
}
