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
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.webui.common.MenuDividerLabel;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Flow;

/**
 * This class shows last build result link, links to build
 * results, history.
 */
final class BuildResultFlow extends Flow {

  private final String dateTimeFormat;


  BuildResultFlow(final String dateTimeFormat) {
    this.dateTimeFormat = dateTimeFormat;
  }


  public void setState(final BuildState state) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();

    // get current build run
    final int currentlyRunningBuildRunID = state.getCurrentlyRunningBuildRunID();
    final boolean currentBuildRunIsPresent = currentlyRunningBuildRunID != BuildRun.UNSAVED_ID;

    // get last build run
    final BuildRun lastBuildRun = state.getLastCompleteBuildRun();
    final boolean lastBuildRunIsPresent = WebuiUtils.isBuildRunNotNullAndComplete(lastBuildRun);

    // add curretnly runnig steps, if any
    if (currentBuildRunIsPresent) {
      add(new CurrentlyBuildingStepsFlow(state, false));
      add(new MenuDividerLabel(false));
      // show current build run links.
      add(new BuildRunResultsLinksFlow(cm.getBuildRun(currentlyRunningBuildRunID), false, true, false));
      if (lastBuildRunIsPresent) {
        add(new MenuDividerLabel(false));
      }
    }

    // add last build run results
    if (lastBuildRunIsPresent) {
      // add last build run flow - shows colored build result link
      if (currentBuildRunIsPresent) {
        add(new LastBuildRunFlow(lastBuildRun, dateTimeFormat));
      } else {
        add(new LastBuildRunResultFlow(lastBuildRun));
        add(new MenuDividerLabel(false));
        add(new BuildRunResultsLinksFlow(lastBuildRun, false, true, false));
      }
    }
  }


  public String toString() {
    return "BuildResultFlow{" +
            "dateTimeFormat='" + dateTimeFormat + '\'' +
            '}';
  }
}


