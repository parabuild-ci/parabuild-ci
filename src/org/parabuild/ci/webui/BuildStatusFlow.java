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
import org.parabuild.ci.build.BuildStatus;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Flow;
import viewtier.ui.Image;
import viewtier.ui.Label;
import viewtier.ui.Layout;

/**
 * Holds either a label status or a throbber, depending on
 * state of the build.
 */
final class BuildStatusFlow extends Flow {

  private static final long serialVersionUID = -762751713936648354L;


  BuildStatusFlow() {
    setAlignX(Layout.CENTER);
  }


  public void setState(final BuildState state) {
    final Image throbber = WebuiUtils.makeThrobber(state, state.getStatusAsString(), 20);
    clear();
    if (state.isBusy()) {
      add(throbber);
    } else {
      final Label label = new Label(state.getStatusAsString());
      final BuildStatus status = state.getStatus();
      if (!BuildStatus.IDLE.equals(status)
              && !BuildStatus.PENDING_BUILD.equals(status)
              && !BuildStatus.GETTING_CHANGES.equals(status)
              && !BuildStatus.INACTIVE.equals(status)) {
        // mark bold if status is not idle or inactive
        label.setFont(Pages.FONT_COMMON_BOLD_LABEL);
      }
      add(label);
    }
  }
}

