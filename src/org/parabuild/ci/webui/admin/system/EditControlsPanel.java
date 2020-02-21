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

import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CommonButton;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.SaveButton;
import org.parabuild.ci.webui.common.WebUIConstants;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Panel;
import viewtier.ui.Parameters;
import viewtier.ui.Tierlet;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * EditControlsPanel
 * <p/>
 *
 * @author Slava Imeshev
 * @since Sep 27, 2008 4:11:17 PM
 */
final class EditControlsPanel extends Panel {

  private static final String CAPTION_EDIT = "Edit";

  private final AbstractSystemConfigPanel pnlConfiguration;
  private final String editPage;
  private static final String MODE_EDIT = SystemConfigurationPageParameter.MODE_VALUE_EDIT;
  private static final String MODE = SystemConfigurationPageParameter.MODE;
  private static final long serialVersionUID = 5564029987063065430L;


  /**
   * Constructor.
   *
   * @param editPage         used to forward preview request upon edit cancellation.
   * @param pnlConfiguration congiration panel.
   */
  EditControlsPanel(final String editPage, final AbstractSystemConfigPanel pnlConfiguration) {
    this.editPage = editPage;
    this.pnlConfiguration = pnlConfiguration;
    this.setBackground(Pages.COLOR_PANEL_HEADER_BG);
  }


  public void setMode(final byte mode) {
    clear();
    if (mode == WebUIConstants.MODE_VIEW) {
      // Display edit button
      add(createEditButton());
    } else if (mode == WebUIConstants.MODE_EDIT) {
      add(createSaveCancelFlow());
    } else {
      throw new IllegalArgumentException("Unknown mode");
    }
  }


  private CommonFlow createSaveCancelFlow() {
    final CommonFlow flow = new CommonFlow(createSaveButton(), new Label("  "), createCancelButton());
    flow.setAlignX(Layout.CENTER);
    flow.setBackground(Pages.COLOR_PANEL_HEADER_BG);
    return flow;
  }


  private CancelButton createCancelButton() {
    final CancelButton btnCancel = new CancelButton();
    btnCancel.setName("cancel_edit");
    btnCancel.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -3428204333907398307L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
        return cancelEdit();
      }
    });
    return btnCancel;
  }


  private SaveButton createSaveButton() {
    final SaveButton btnSave = new SaveButton();
    btnSave.setName("save_configuration");
    btnSave.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 4484068503552638288L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
        return saveEdit();
      }
    });
    return btnSave;
  }


  private CommonButton createEditButton() {
    final CommonButton btnEdit = new CommonButton(CAPTION_EDIT);
    btnEdit.setName(SystemConfigurationPageParameter.EDIT_CONFIGURATION);
    btnEdit.setAlignX(Layout.CENTER);
    btnEdit.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 6354534066021487245L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
        final Parameters parameters = new Parameters();
        parameters.addParameter(MODE, MODE_EDIT);
        return Tierlet.Result.Done(editPage, parameters);
      }
    });
    return btnEdit;
  }


  private Tierlet.Result cancelEdit() {
    final Properties previewParameters = SystemConfigurationPageParameter.createPreviewParameters();
    final Parameters parameters = new Parameters();
    for (final Iterator iter = previewParameters.entrySet().iterator(); iter.hasNext();) {
      final Map.Entry entry = (Map.Entry) iter.next();
      parameters.addParameter((String) entry.getKey(), (String) entry.getValue());
    }
    return Tierlet.Result.Done(editPage, parameters);
  }


  /**
   * Processes request to save the configuration.
   *
   * @return result.
   */
  private Tierlet.Result saveEdit() {
    // Validate
    if (!pnlConfiguration.validate()) {
      return Tierlet.Result.Continue();
    }

    // Save
    pnlConfiguration.save();

    // Show message
    pnlConfiguration.showOKMessage("Configuration has been saved");

    // Move to R/O mode
    pnlConfiguration.setMode(WebUIConstants.MODE_VIEW);
    setMode(WebUIConstants.MODE_VIEW);

    // Done
    return Tierlet.Result.Done();
  }


  public String toString() {
    return "EditControlsPanel{" +
            "pnlConfiguration=" + pnlConfiguration +
            ", editPage='" + editPage + '\'' +
            '}';
  }
}
