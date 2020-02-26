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
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Flow;
import viewtier.ui.Layout;

/**
 * Shows build time
 */
final class BuildTimeFlow extends Flow {

  private static final long serialVersionUID = 1321249943398819863L;
  private final String dateTimeFormat;


  BuildTimeFlow(final String dateTimeFormat) {
    this.dateTimeFormat = dateTimeFormat;
    setAlignX(Layout.CENTER);
  }


  public void setState(final BuildRun currentBuildRun, final BuildRun lastCompleteBuildRun) {
    if (currentBuildRun != null) {
      addElapsedTimeLink(currentBuildRun);
    } else {
      if (WebuiUtils.isBuildRunNotNullAndComplete(lastCompleteBuildRun)) {
        addFinishedLink(lastCompleteBuildRun);
      }
    }
  }


  private void addElapsedTimeLink(final BuildRun buildRun) {
    if (buildRun.getStartedAt() == null) {
      return;
    }
    final long elapsedTime = (System.currentTimeMillis() - buildRun.getStartedAt().getTime()) / 1000L;
    final StringBuilder caption = new StringBuilder(30);
    caption.append("Elapsed time: ");
    caption.append(StringUtils.durationToString(elapsedTime, false));
    add(new BuildResultLink(caption.toString(), buildRun));
  }


  private void addFinishedLink(final BuildRun buildRun) {
    if (buildRun.getFinishedAt() == null) {
      return;
    }
    final BuildResultLink buildResultLink = new BuildResultLink(WebuiUtils.makeAgoAsString(buildRun, false).toString(), buildRun, "Finished at " + StringUtils.formatDate(buildRun.getFinishedAt(), dateTimeFormat));
    add(buildResultLink);
  }


  public String toString() {
    return "BuildTimeFlow{" +
            "dateTimeFormat='" + dateTimeFormat + '\'' +
            '}';
  }
}

