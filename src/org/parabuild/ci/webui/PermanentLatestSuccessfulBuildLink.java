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

import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.Link;

/**
 * Link to point to the static page that shows the last clean build.
 */
public final class PermanentLatestSuccessfulBuildLink extends Link {

  private static final long serialVersionUID = 8291089703337429379L;


  public PermanentLatestSuccessfulBuildLink(final int activeBuildID) {
    super("Latest Successful Build", Pages.PAGE_LATEST_SUCCESSFUL_BUILD, Pages.PARAM_BUILD_ID, Integer.toString(activeBuildID));
    setForeground(Pages.COLOR_BUILD_SUCCESSFUL);
  }
}
