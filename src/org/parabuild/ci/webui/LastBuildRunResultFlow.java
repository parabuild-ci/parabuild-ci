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
import viewtier.ui.Flow;

/**
 * Shows colored last build run result in textual form. For
 * example: "Last build (#2) on 08/08/2004 (3 minutes ago) was
 * BROKEN".
 * <p/>
 * Build result itself ("was BROKEN") is displayed as a link
 * poining to changes or to log depending on this last build run
 * results.
 */
final class LastBuildRunResultFlow extends Flow {

  LastBuildRunResultFlow(final BuildRun lastBuildRun) {
    setState(lastBuildRun);
  }


  public void setState(final BuildRun lastBuildRun) {
    final StringBuilder captionPre = new StringBuilder(20);
    captionPre.append(lastBuildRun.buildResultToString());
    add(new BuildResultLink(captionPre.toString(), lastBuildRun));
  }
}
