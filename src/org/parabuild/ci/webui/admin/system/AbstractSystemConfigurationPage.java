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
package org.parabuild.ci.webui.admin.system;

import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;

/**
 * Base page for system configuration pages.
 */
public class AbstractSystemConfigurationPage extends NavigatableSystemConfigurationPage {

  private static final String PARAM_MODE = SystemConfigurationPageParameter.MODE;
  private static final String MODE_VALUE_PREVIEW = SystemConfigurationPageParameter.MODE_VALUE_PREVIEW;

  /**
   * Edit panel with configuration.
   */
  protected final AbstractSystemConfigPanel pnlConfiguration;

  /**
   * Edit controls panel.
   */
  private final EditControlsPanel pnlEditControls;
  private static final long serialVersionUID = 1177351220855333533L;


  public AbstractSystemConfigurationPage(final String editPage, final AbstractSystemConfigPanel pnlConfiguration) {

    // Set up config panel
    this.pnlConfiguration = pnlConfiguration;
    this.pnlConfiguration.setWidth("100%");

    // Create and set up controls.
    pnlEditControls = new EditControlsPanel(editPage, pnlConfiguration);
    pnlEditControls.setWidth("100%");

    // Create right panel with controls
    final Panel pnlRight = getRightPanel();
    pnlRight.add(this.pnlConfiguration);
    pnlRight.add(WebuiUtils.makeHorizontalDivider(10));
    pnlRight.add(this.pnlEditControls);
  }


  protected final Result executeSystemConfigurationPage(final Parameters params) {
    if (isNew()) {
      final boolean validateOnLoad = isValidateOnLoad(params);
      if (params.isParameterPresent("vlti-" + SystemConfigurationPageParameter.EDIT_CONFIGURATION)) {
        return beginEdit(validateOnLoad);
      } else
      if (params.isParameterPresent(PARAM_MODE) && params.getParameterValue(PARAM_MODE).equals(MODE_VALUE_PREVIEW)) {
        return displayPreview(validateOnLoad);
      } else {
        return displayPreview(validateOnLoad);
      }
    } else {
      return Result.Continue();
    }
  }


  /**
   * Initiates edit for the page.
   *
   * @return Result.Continue();
   * @param validateOnLoad
   */
  private Result beginEdit(final boolean validateOnLoad) {
    // Set edit mode
    pnlConfiguration.setMode(WebUIConstants.MODE_EDIT);
    pnlEditControls.setMode(WebUIConstants.MODE_EDIT);

    // Load data
    pnlConfiguration.load();
    if (validateOnLoad) {
      pnlConfiguration.validate();
    }

    // Return Continue
    return Result.Continue();
  }


  /**
   * Displays preview of the page.
   *
   * @param validateOnLoad
   * @return
   */
  private Result displayPreview(final boolean validateOnLoad) {
    // Set view mode
    pnlConfiguration.setMode(WebUIConstants.MODE_VIEW);
    pnlEditControls.setMode(WebUIConstants.MODE_VIEW);

    // Load data
    pnlConfiguration.load();
    if (validateOnLoad) {
      pnlConfiguration.validate();
    }

    // return Done
    return Result.Done();
  }


  private static boolean isValidateOnLoad(final Parameters params) {
    final String validateOnLoadString = params.getParameterValue(Pages.PARAM_VALIDATE_ON_LOAD);
    return "true".equalsIgnoreCase(validateOnLoadString);
  }


  public String toString() {
    return "AbstractSystemConfigurationPage{" +
            "pnlConfiguration=" + pnlConfiguration +
            ", pnlEditControls=" + pnlEditControls +
            '}';
  }
}
