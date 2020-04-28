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

import org.parabuild.ci.common.InputValidator;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.LogConfigProperty;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.ExtensionListField;
import org.parabuild.ci.webui.common.RequiredFieldMarker;

/**
 * Panel to configure a list of files (directory) as  customer
 * build log
 *
 * @see AbstractLogConfigPanel
 */
public final class TextDirLogConfigPanel extends AbstractLogConfigPanel {

  private static final long serialVersionUID = -1731911756101403145L; // NOPMD

  public static final String NAME_EXTENSIONS = "Log  file  extensions:";
  public static final String STRING_DEFAULT_EXTENSIONS = "log, txt";

  // fields
  private final CommonField flExtensions = new ExtensionListField();


  /**
   * Creates message panel without title.
   */
  public TextDirLogConfigPanel() {
    // init
    super(false); // no conent border
    super.setLogType(LogConfig.LOG_TYPE_TEXT_DIR);
    super.getGridIter().add(new CommonFieldLabel(NAME_EXTENSIONS));
    super.getGridIter().add(new RequiredFieldMarker(flExtensions), 3);
    super.inputMap.bindPropertyNameToInput(LogConfigProperty.ATTR_FILE_EXTENSIONS, flExtensions);
    // set default
    this.flExtensions.setValue(STRING_DEFAULT_EXTENSIONS);
  }


  public boolean validateProperties() {
    InputValidator.validateFieldNotBlank(getErrors(), NAME_EXTENSIONS, flExtensions);
    return getErrors().isEmpty();
  }
}
