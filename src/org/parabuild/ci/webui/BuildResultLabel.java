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

import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.webui.common.CommonSummaryLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Layout;

/**
 * This label changes it's color according to build run
 * result.
 */
final class BuildResultLabel extends CommonSummaryLabel {

  private static final long serialVersionUID = -184100169612518191L;


  /**
   * Constructor
   */
  public BuildResultLabel() {
    setAlignX(Layout.LEFT);
    setFont(Pages.FONT_BUILD_RESULT);
  }


  /**
   * Sets build run to show result for.
   *
   * @param buildRun - build run to show result for.
   */
  public void setBuildRun(final BuildRun buildRun) {
    if (buildRun.completed()) {
      // set result text and colors
      setText(buildRun.buildResultToString());
      setForeground(WebuiUtils.getBuildResultColor(getTierletContext(), buildRun));
    } else {
      // we leave result blank if build is not complete.
      setText("");
    }
  }
}

