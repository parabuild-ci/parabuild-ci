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

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Flow;
import viewtier.ui.Layout;

/**
 * Shows build number and change list.
 */
public final class BuildNumberFlow extends Flow {

  BuildNumberFlow() {
    setAlignX(Layout.CENTER);
  }


  public void setState(final BuildRun currentBuildRun, final BuildRun lastCompleteBuildRun) {
    if (currentBuildRun != null) {
      addLink(currentBuildRun);
    } else {
      if (WebuiUtils.isBuildRunNotNullAndComplete(lastCompleteBuildRun)) {
        addLink(lastCompleteBuildRun);
      }
    }
  }


  private void addLink(final BuildRun buildRun) {
    final StringBuilder caption = new StringBuilder(30);
    caption.append(buildRun.getBuildRunNumberAsString());
    if (buildRun.isPhysicalChangeListNumber() && !StringUtils.isBlank(buildRun.getChangeListNumber())) {
      caption.append(" @ ").append(buildRun.getChangeListNumber());
    }
    add(new BuildResultLink(caption.toString(), buildRun));
  }
}

