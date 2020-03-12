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

import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.object.ResultConfig;
import org.parabuild.ci.object.ResultConfigProperty;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.util.URLResultGenerator;
import org.parabuild.ci.webui.common.CommonCheckBox;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.AbstractInput;
import viewtier.ui.Label;

import java.util.List;

/**
 * Panel to configure URL build result
 *
 * @see AbstractResultConfigPanel
 */
public final class URLResultConfigPanel extends AbstractResultConfigPanel {

  private static final long serialVersionUID = -1731911756101403145L; // NOPMD

  private static final String CAPTION_TEST_URL_FOR_AVAILABILITY = "Test URL for availability: ";
  private static final String CAPTION_URL_TEMPLATE = "URL template ";

  private final Label lbTestURL = new CommonFieldLabel(CAPTION_TEST_URL_FOR_AVAILABILITY); // NOPMD
  private final CommonCheckBox cbTestURL = new CommonCheckBox(); // NOPMD


  /**
   * Creates message panel without title.
   */
  public URLResultConfigPanel() {
    super(false, CAPTION_URL_TEMPLATE, new CommonField(250, 40)); // no conent border
    setResultType(ResultConfig.RESULT_TYPE_URL);
    super.getGridIter().addPair(lbTestURL, cbTestURL);
    super.inputMap.bindPropertyNameToInput(ResultConfigProperty.ATTR_TEST_URL, cbTestURL);
  }


  /**
   * Overwites parent.
   *
   * @see URLResultConfigPanel
   *  @param errors
   * @param flPath
   */
  protected final void validatePath(final List errors, final AbstractInput flPath) {
    // not blank
    final boolean valid = WebuiUtils.validateFieldNotBlank(errors, CAPTION_URL_TEMPLATE, flPath);
    // validate template
    if (valid) {
      try {
        new URLResultGenerator().validateTemplate(flPath.getValue());
      } catch (final ValidationException e) {
        errors.add("Invalid \"" + CAPTION_URL_TEMPLATE + "\". " + StringUtils.toString(e));
      }
    }
  }


  public void loadProperties(final ResultConfig resultConfig) {
    // nothing to do for this type
  }


  /**
   * Saves result configuration properties.
   *
   * This method should be implemented by extending class.
   *
   * @param resultConfig - Result config for which result properties are being saved.
   *
   * @return true if valid
   *
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
