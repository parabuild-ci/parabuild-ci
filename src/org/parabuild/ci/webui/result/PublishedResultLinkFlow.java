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
package org.parabuild.ci.webui.result;

import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;


/**
 * This helper class holds "switch-able" result link. It can
 * switch to a plain label if showLink is set to false.
 */
final class PublishedResultLinkFlow extends Flow {

  public PublishedResultLinkFlow() {
  }


  public PublishedResultLinkFlow(final String caption, final int buildRunID, final boolean showAsLink) {
    setBuildRun(caption, buildRunID, showAsLink);
  }


  /**
   * Sets build run.
   *
   * @param caption caption to set to label or link
   * @param buildRunID build run to set
   * @param showAsLink if true will be shown as link otherwise is shown as label
   */
  public void setBuildRun(final String caption, final int buildRunID, final boolean showAsLink) {
    reset();
    if (showAsLink) {
      add(new ResultsLink(caption, buildRunID));
    } else {
      add(new CommonLabel(caption));
    }
  }
}
