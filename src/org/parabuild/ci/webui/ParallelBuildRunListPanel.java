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

import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This panel holds links with names and IDs of a leading
 * and dependant build runs paricipated in a particilar
 * [parallel] build run.
 */
final class ParallelBuildRunListPanel extends Panel {

  /**
   * Factory to generate page-specific build run links.
   */
  private final BuildRunURLFactory buildRunURLFactory;


  public ParallelBuildRunListPanel(final BuildRun buildRun, final BuildRunURLFactory buildRunURLFactory) {
    this.buildRunURLFactory = buildRunURLFactory;
    setPadding(4);
    final List dependentParallelBuildRuns = ConfigurationManager.getInstance().getAllParallelBuildRuns(buildRun);
    for (final Iterator i = dependentParallelBuildRuns.iterator(); i.hasNext();) {
      add(makeLink((BuildRun)i.next(), buildRun));
    }
  }


  private Component makeLink(final BuildRun cbr, final BuildRun current) {
    Component cmp = null;
    final String caption = cbr.getBuildName() + (cbr.getDependence() == BuildRun.DEPENDENCE_LEADER ? " [Leader]" : "");
    if (cbr.getBuildRunID() == current.getBuildRunID()) {
      cmp = new BoldCommonLabel(caption);
    } else {
      final LinkURL linkURL = buildRunURLFactory.makeLinkURL(cbr);
      cmp = new CommonLink(caption, linkURL.getUrl(), linkURL.getParameters());
    }
    cmp.setForeground(WebuiUtils.getBuildResultColor(getTierletContext(), cbr));
    cmp.setPadding(0);
    return cmp;
  }
}
