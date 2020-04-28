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

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Parameters;
import viewtier.ui.TierletContext;

/**
 * This factory class makes PreviousNextLinks in a standard way
 * when it is guaranteed that a target result page guaranteed to
 * exist. Examples of such pages are {@link BuildRunChangesPage}
 * and {@link BuildRunConfigReportPage} that use this maker
 * because there are alway changes and there is always
 * configuration.
 */
final class StandardPreviousNextLinksMaker {

  private final String page;


  public StandardPreviousNextLinksMaker(final String page) {
    this.page = page;
  }


  public PreviousNextLinks makeLinks(final Parameters parameters, final BuildRun buildRun, final TierletContext tierletContext) {
    // make next link
    final BuildRun previousBuildRun = ConfigurationManager.getInstance().getPreviousBuildRun(buildRun);
    CommonLink lnkPrevious = null;
    if (previousBuildRun != null) {
      lnkPrevious = new CommonLink("Previous", page, Pages.PARAM_BUILD_RUN_ID, Integer.toString(previousBuildRun.getBuildRunID()));
    }

    // make previous link
    final BuildRun nextBuildRun = ConfigurationManager.getInstance().getNextBuildRun(buildRun);
    CommonLink lnkNext = null;
    if (nextBuildRun != null) {
      lnkNext = new CommonLink("Next", page, Pages.PARAM_BUILD_RUN_ID, Integer.toString(nextBuildRun.getBuildRunID()));
    }

    // make surrent build number
    final BoldCommonLabel lbCurrent = new BoldCommonLabel(buildRun.getBuildRunNumberAsString());
    lbCurrent.setForeground(WebuiUtils.getBuildResultColor(tierletContext, buildRun));

    return new PreviousNextLinks(lnkPrevious, lbCurrent, lnkNext);
  }
}
