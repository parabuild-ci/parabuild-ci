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

import org.parabuild.ci.configuration.ResultGroupManager;
import org.parabuild.ci.object.ResultGroup;
import org.parabuild.ci.webui.common.CodeNameDropDown;

import java.util.List;

/**
 */
public class AutopublishDropdown extends CodeNameDropDown {

  private static final String CAPTION_SELECT_RESULT_GROUP = "Select result group";
  public static final int CODE_NOT_SET = -1;
  private static final long serialVersionUID = -4724891970469118657L;


  public AutopublishDropdown() {
    super(ALLOW_NONEXISTING_CODES);
    addCodeNamePair(CODE_NOT_SET, CAPTION_SELECT_RESULT_GROUP);
    final List resultGroups = ResultGroupManager.getInstance().getResultGroups();
    for (int i = 0; i < resultGroups.size(); i++) {
      final ResultGroup resultGroup = (ResultGroup) resultGroups.get(i);
      addCodeNamePair(resultGroup.getID(), resultGroup.getName());
    }
  }


  /**
   * Sets code.
   *
   * @param autopublishGroupID
   */
  public void setGroupID(final Integer autopublishGroupID) {
    if (autopublishGroupID == null || !codeExists(autopublishGroupID)) {
      setCode(CODE_NOT_SET);
    } else {
      setCode(autopublishGroupID);
    }
  }


  public Integer getGroupID() {
    final int code = getCode();
    return code == CODE_NOT_SET ? null : Integer.valueOf(code);
  }
}
