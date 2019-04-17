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

import org.parabuild.ci.common.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * Shows last clean buld link or nothing, based given
 * buildState.
 */
public final class LastCleanBuildRunFlow extends Flow {

  private static final long serialVersionUID = -7990154614217584287L;


  public LastCleanBuildRunFlow(final BuildRun lastCleanBuildRun, final String dateTimeFormat) {
    if (!lastCleanBuildRunIsValid(lastCleanBuildRun)) return;

    // create caption
    final StringBuilder caption = new StringBuilder(30);
    caption.append("Last clean build (#");
    caption.append(lastCleanBuildRun.getBuildRunNumberAsString());
    if (lastCleanBuildRun.isPhysicalChangeListNumber() && !StringUtils.isBlank(lastCleanBuildRun.getChangeListNumber())) {
      caption.append(" @ ");
      caption.append(lastCleanBuildRun.getChangeListNumber());
    }
    caption.append(')');
    caption.append(" at ");
    caption.append(StringUtils.formatDate(lastCleanBuildRun.getFinishedAt(), dateTimeFormat));
    caption.append(' ');
    caption.append(WebuiUtils.makeAgoAsString(lastCleanBuildRun));

    // add
    add(new BuildResultLink(caption.toString(), lastCleanBuildRun));
  }


  /**
   * Return true if the given BuildRun is a valid last clean
   * build run.
   *
   * @param buildRun
   */
  private static boolean lastCleanBuildRunIsValid(final BuildRun buildRun) {
    return WebuiUtils.isBuildRunNotNullAndComplete(buildRun)
      && buildRun.getResultID() == BuildRun.BUILD_RESULT_SUCCESS;
  }
}
