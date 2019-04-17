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
 * Perforce line end option dropdown.
 */
final class P4LineEndDropDown extends CodeNameDropDown {


  private static final long serialVersionUID = 6719152948150965062L;


  P4LineEndDropDown() {
    addCodeNamePair(SourceControlSetting.P4_LINE_END_LOCAL, SourceControlSetting.P4_LINE_END_VALUE_LOCAL);
    addCodeNamePair(SourceControlSetting.P4_LINE_END_UNIX, SourceControlSetting.P4_LINE_END_VALUE_UNIX);
    addCodeNamePair(SourceControlSetting.P4_LINE_END_MAC, SourceControlSetting.P4_LINE_END_VALUE_MAC);
    addCodeNamePair(SourceControlSetting.P4_LINE_END_SHARE, SourceControlSetting.P4_LINE_END_VALUE_SHARE);
    setSelection(0);
  }
}
