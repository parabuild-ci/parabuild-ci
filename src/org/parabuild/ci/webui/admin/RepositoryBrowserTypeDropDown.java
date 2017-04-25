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

import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.webui.common.CodeNameDropDown;

/**
 * This dropdown shows selection of available integrations
 * with repository browsers.
 */
final class RepositoryBrowserTypeDropDown extends CodeNameDropDown {


  /**
   * Default constructor.
   */
  public RepositoryBrowserTypeDropDown() {
    addCodeNamePair(SourceControlSetting.CODE_NOT_SELECTED, "Select:");
    addCodeNamePair(SourceControlSetting.CODE_VIEWVC, "ViewVC/ViewCVS");
    addCodeNamePair(SourceControlSetting.CODE_FISHEYE, "FishEye");
    addCodeNamePair(SourceControlSetting.CODE_WEB_SVN, "WebSVN");
    addCodeNamePair(SourceControlSetting.CODE_GITHUB, "Github");
    setCode(SourceControlSetting.CODE_NOT_SELECTED);
  }
}
