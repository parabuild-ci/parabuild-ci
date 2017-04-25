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
 * Panel to configure a list of files (directory) as build result
 *
 * @see AbstractResultConfigPanel
 */
public final class DirResultConfigPanel extends AbstractResultConfigPanel {

  private static final long serialVersionUID = -1731911756101403145L; // NOPMD

  public static final String NAME_EXTENSIONS = "Result  file  extensions:";
  public static final String STRING_DEFAULT_EXTENSIONS = ".tar.gz, .zip, .jar";

  // fields
  private final CommonField flExtensions = new ExtensionListField();


  /**
   * Creates message panel without title.
   */
  public DirResultConfigPanel() {
    // init
    super(false); // no conent border
    super.setResultType(ResultConfig.RESULT_TYPE_DIR);
    super.getGridIter().add(new CommonFieldLabel(NAME_EXTENSIONS));
    super.getGridIter().add(new RequiredFieldMarker(flExtensions), 3);
    super.inputMap.bindPropertyNameToInput(ResultConfigProperty.ATTR_FILE_EXTENSIONS, flExtensions);
    // set default
    this.flExtensions.setValue(STRING_DEFAULT_EXTENSIONS);
  }


  public boolean validateProperties() {
    WebuiUtils.validateFieldNotBlank(getErrors(), NAME_EXTENSIONS, flExtensions);
    return getErrors().isEmpty();
  }
}
