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

import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;

/**
 * Perforce autentication modes.
 */
final class P4AuthenticationModeDropDown extends CodeNameDropDown {


  P4AuthenticationModeDropDown() {
    addCodeNamePair(SourceControlSetting.P4_AUTHENTICATION_MODE_VALUE_P4PASSWD, "P4PASSWD");
    addCodeNamePair(SourceControlSetting.P4_AUTHENTICATION_MODE_VALUE_P4LOGIN, "p4 login");
    setSelection(0);
  }
}
