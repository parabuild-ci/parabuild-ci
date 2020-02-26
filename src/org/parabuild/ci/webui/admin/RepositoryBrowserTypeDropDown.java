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
package org.parabuild.ci.webui.admin;

import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.webui.common.CodeNameDropDown;

/**
 * This dropdown shows selection of available integrations
 * with repository browsers.
 */
final class RepositoryBrowserTypeDropDown extends CodeNameDropDown {


  private static final long serialVersionUID = -6302772628325060562L;


  /**
   * Default constructor.
   */
  public RepositoryBrowserTypeDropDown() {
    addCodeNamePair(VersionControlSystem.CODE_NOT_SELECTED, "Select:");
    addCodeNamePair(VersionControlSystem.CODE_VIEWVC, "ViewVC/ViewCVS");
    addCodeNamePair(VersionControlSystem.CODE_FISHEYE, "FishEye");
    addCodeNamePair(VersionControlSystem.CODE_WEB_SVN, "WebSVN");
    addCodeNamePair(VersionControlSystem.CODE_GITHUB, "Github");
    setCode(VersionControlSystem.CODE_NOT_SELECTED);
  }
}
