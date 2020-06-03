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
package org.parabuild.ci.versioncontrol;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.remote.Agent;

import java.io.IOException;

/**
 * Transalates ClearCase text mode code to -tmode value
 */
public final class ClearCaseTextModeCodeTranslator {

  public static final String NAME_AUTOMATIC = "Automatic";
  public static final String NAME_INSERT_CR = "insert_cr";
  public static final String NAME_MSDOS = "msdos";
  public static final String NAME_NOT_SET = "Not set";
  public static final String NAME_STRIP_CR = "strip_cr";
  public static final String NAME_UNIX = "unix";
  public static final String NAME_TRANSPARENT = "transparent";

  private final boolean isWindows;


  public ClearCaseTextModeCodeTranslator(final Agent agent) throws IOException, AgentFailureException {
    this.isWindows = agent.isWindows();
  }


  public String translateTextModeCode(final byte textModeCode) {
    String textMode = null;
    switch (textModeCode) {
      case VersionControlSystem.CLEARCASE_TEXT_MODE_NOT_SET:
        textMode = "";
        break;
      case VersionControlSystem.CLEARCASE_TEXT_MODE_MSDOS:
        textMode = "msdos";
        break;
      case VersionControlSystem.CLEARCASE_TEXT_MODE_INSERT_CR:
        textMode = "insert_cr";
        break;
      case VersionControlSystem.CLEARCASE_TEXT_MODE_UNIX:
        textMode = "unix";
        break;
      case VersionControlSystem.CLEARCASE_TEXT_MODE_STRIP_CR:
        textMode = "strip_cr";
        break;
      case VersionControlSystem.CLEARCASE_TEXT_MODE_TRANSPARENT:
        textMode = "transparent";
        break;
      case VersionControlSystem.CLEARCASE_TEXT_MODE_AUTO:
      default:
        textMode = isWindows ? "msdos" : "unix";
        break;
    }
    return textMode;
  }


  /**
   * Translate name to code.
   *
   * @param textModeName
   * @return
   */
  public byte translateTextModeName(final String textModeName) {
    if (textModeName.equals(NAME_AUTOMATIC)) return VersionControlSystem.CLEARCASE_TEXT_MODE_AUTO;
    if (textModeName.equals(NAME_INSERT_CR)) return VersionControlSystem.CLEARCASE_TEXT_MODE_INSERT_CR;
    if (textModeName.equals(NAME_MSDOS)) return VersionControlSystem.CLEARCASE_TEXT_MODE_MSDOS;
    if (textModeName.equals(NAME_NOT_SET)) return VersionControlSystem.CLEARCASE_TEXT_MODE_NOT_SET;
    if (textModeName.equals(NAME_STRIP_CR)) return VersionControlSystem.CLEARCASE_TEXT_MODE_STRIP_CR;
    if (textModeName.equals(NAME_UNIX)) return VersionControlSystem.CLEARCASE_TEXT_MODE_UNIX;
    if (textModeName.equals(NAME_TRANSPARENT)) return VersionControlSystem.CLEARCASE_TEXT_MODE_TRANSPARENT;
    return VersionControlSystem.CLEARCASE_TEXT_MODE_NOT_SET;
  }
}
