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

import org.parabuild.ci.common.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import org.parabuild.ci.configuration.VerbialBuildResult;
import viewtier.ui.*;

/**
 * Shows colored last build run result in textual form. For
 * example: "Last build (#2) on 08/08/2004 (3 minutes ago) was
 * BROKEN".
 * <p/>
 * Build result itself ("was BROKEN") is displayed as a link
 * poining to changes or to log depending on this last build run
 * results.
 */
public final class LastBuildRunFlow extends Flow {

  private static final long serialVersionUID = 6471975830201426357L;


  public LastBuildRunFlow(final BuildRun lastBuildRun, final String dateTimeFormat) {
    this(lastBuildRun, false, dateTimeFormat);
  }


  public LastBuildRunFlow(final BuildRun lastBuildRun, final boolean useBuildName, final String dateTimeFormat) {
    final Date lastBuildRunFinishedAt = lastBuildRun.getFinishedAt();
    if (lastBuildRunFinishedAt == null) return;

    // add pre-link text
    final StringBuilder captionPre = new StringBuilder(30);
    captionPre.append(useBuildName ? lastBuildRun.getBuildName() : "Last build").append(" (#");
    captionPre.append(lastBuildRun.getBuildRunNumberAsString());
    if (lastBuildRun.isPhysicalChangeListNumber() && !StringUtils.isBlank(lastBuildRun.getChangeListNumber())) {
      captionPre.append(" @ ");
      captionPre.append(lastBuildRun.getChangeListNumber());
    }
    captionPre.append(')');
    captionPre.append(" at ");
    captionPre.append(StringUtils.formatDate(lastBuildRunFinishedAt, dateTimeFormat));
    captionPre.append(' ');
    captionPre.append(new VerbialBuildResult().getVerbialResultString(lastBuildRun));
    captionPre.append(' ');
    captionPre.append(WebuiUtils.makeAgoAsString(lastBuildRun));

    // add result link
    add(new BuildResultLink(captionPre.toString(), lastBuildRun));
  }
}
