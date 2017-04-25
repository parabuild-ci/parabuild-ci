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
import org.parabuild.ci.versioncontrol.*;

/**
 * Shows selection of clear case text modes (-tmode) used to
 * create a view.
 */
public final class ClearCaseTextModeDropDown extends CodeNameDropDown {

  private static final long serialVersionUID = 4662209824080566402L; // NOPMD


  public ClearCaseTextModeDropDown() {
    addCodeNamePair(SourceControlSetting.CLEARCASE_TEXT_MODE_NOT_SET, ClearCaseTextModeCodeTranslator.NAME_NOT_SET);
    addCodeNamePair(SourceControlSetting.CLEARCASE_TEXT_MODE_AUTO, ClearCaseTextModeCodeTranslator.NAME_AUTOMATIC);
    addCodeNamePair(SourceControlSetting.CLEARCASE_TEXT_MODE_MSDOS, ClearCaseTextModeCodeTranslator.NAME_MSDOS);
    addCodeNamePair(SourceControlSetting.CLEARCASE_TEXT_MODE_INSERT_CR, ClearCaseTextModeCodeTranslator.NAME_INSERT_CR);
    addCodeNamePair(SourceControlSetting.CLEARCASE_TEXT_MODE_UNIX, ClearCaseTextModeCodeTranslator.NAME_UNIX);
    addCodeNamePair(SourceControlSetting.CLEARCASE_TEXT_MODE_STRIP_CR, ClearCaseTextModeCodeTranslator.NAME_STRIP_CR);
    addCodeNamePair(SourceControlSetting.CLEARCASE_TEXT_MODE_TRANSPARENT, ClearCaseTextModeCodeTranslator.NAME_TRANSPARENT);
    setCode(SourceControlSetting.CLEARCASE_TEXT_MODE_NOT_SET);
  }
}

