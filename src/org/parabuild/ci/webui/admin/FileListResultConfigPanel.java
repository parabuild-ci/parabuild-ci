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

import org.parabuild.ci.object.ResultConfig;
import org.parabuild.ci.object.ResultConfigProperty;
import org.parabuild.ci.webui.common.CommonText;
import viewtier.ui.Text;

/**
 * Panel to configure single file customer build result
 *
 * @see AbstractResultConfigPanel
 */
public final class FileListResultConfigPanel extends AbstractResultConfigPanel {

  private static final long serialVersionUID = -1731911756101403145L; // NOPMD


  /**
   * Creates message panel without title.
   */
  public FileListResultConfigPanel() {
    super(false, "Result path(s):", new CommonText(40, 4)); // no content border
    setResultType(ResultConfig.RESULT_TYPE_FILE_LIST);
  }


  public void loadProperties(final ResultConfig resultConfig) {
    // nothing to do for this type
  }


  /**
   * Saves result configuration properties.
   * <p/>
   * This method should be implemented by extending class.
   *
   * @param resultConfig - Result config for which result properties are being saved.
   * @return true if valid
   * @see ResultConfig
   * @see ResultConfigProperty
   */
  public boolean saveProperties(final ResultConfig resultConfig) {
    // nothing to do for this type
    return true;
  }


  public boolean validateProperties() {
    // nothing to do for this type
    return true;
  }
}
