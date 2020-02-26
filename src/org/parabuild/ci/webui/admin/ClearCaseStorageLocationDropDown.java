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

import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.webui.common.*;

/**
 * A dropdown to show view location options.
 */
final class ClearCaseStorageLocationDropDown extends CodeNameDropDown {


  private static final long serialVersionUID = -6918556059515000613L;


  /**
   * Constructor.
   */
  public ClearCaseStorageLocationDropDown() {
    super.addCodeNamePair(VCSAttribute.CLEARCASE_STORAGE_CODE_STGLOC, "location name (-stgloc):");
    super.addCodeNamePair(VCSAttribute.CLEARCASE_STORAGE_CODE_VWS, "location path (-vws):");
    setCode(VCSAttribute.CLEARCASE_STORAGE_CODE_STGLOC);
    setFont(Pages.FONT_COMMON_BOLD_LABEL);
  }
}
