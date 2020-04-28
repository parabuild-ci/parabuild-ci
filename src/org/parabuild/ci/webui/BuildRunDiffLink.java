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
import org.parabuild.ci.webui.common.CommonLink;
import org.parabuild.ci.webui.common.Pages;

/**
 * This class shows build run diff link
 */
public final class BuildRunDiffLink extends CommonLink {

  private static final long serialVersionUID = -5440935824288919529L;


  /**
   * Constructor.
   *
   * @param buildRunID
   */
  public BuildRunDiffLink(final int buildRunID) {
    super("Diff", Pages.BUILD_DIFF,
      Pages.PARAM_BUILD_RUN_ID, buildRunID);
  }


  /**
   * Constructor.
   *
   * @param buildRun
   */
  public BuildRunDiffLink(final BuildRun buildRun) {
    this(buildRun.getBuildRunID());
  }
}
