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
 * Shows selection of clear case text modes (-tmode) used to
 * create a view.
 */
public final class StarTeamEndOfLineDropDown extends CodeNameDropDown {

  private static final long serialVersionUID = 4662209824080566402L; // NOPMD

  public StarTeamEndOfLineDropDown() {
    addCodeNamePair(VersionControlSystem.STARTEAM_EOL_OFF, "off");
    addCodeNamePair(VersionControlSystem.STARTEAM_EOL_ON, "on");
    addCodeNamePair(VersionControlSystem.STARTEAM_EOL_CR, "cr");
    addCodeNamePair(VersionControlSystem.STARTEAM_EOL_LF, "lf");
    addCodeNamePair(VersionControlSystem.STARTEAM_EOL_CRLF, "crlf");
    setCode(VersionControlSystem.STARTEAM_EOL_ON);
  }
}
