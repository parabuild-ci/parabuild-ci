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

import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.webui.common.CodeNameDropDown;

/**
 * This drop down contains types of values of manual run
 * param3ters.
 */
public final class ManualStartParameterPresentationDropDown extends CodeNameDropDown {

  private static final long serialVersionUID = 1175596430066030886L; // NOPMD


  public ManualStartParameterPresentationDropDown() {
    super.addCodeNamePair(StartParameter.PRESENTATION_CHECK_LIST, "Check list");
    super.addCodeNamePair(StartParameter.PRESENTATION_RADIO_LIST, "Radio list");
    super.addCodeNamePair(StartParameter.PRESENTATION_DROPDOWN_LIST, "Dropdown list");
    super.addCodeNamePair(StartParameter.PRESENTATION_SINGLE_VALUE, "Single value");
    super.setCode(StartParameter.PRESENTATION_CHECK_LIST);
  }
}
