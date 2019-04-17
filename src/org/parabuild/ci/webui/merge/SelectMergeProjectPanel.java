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
package org.parabuild.ci.webui.merge;

import org.parabuild.ci.webui.admin.ProjectDropDown;
import org.parabuild.ci.webui.common.CancelButton;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.ContinueButton;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import viewtier.ui.Button;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Parameters;
import viewtier.ui.Tierlet;

/**
 */
final class SelectMergeProjectPanel extends MessagePanel {

  private static final long serialVersionUID = 2791113912747605373L;
  
  private static final String MESSAGE_PLEASE_SELECT_PROJECT = "Please select project";
  private static final String CAPTION_PROJECT_NAME = "Project name: ";

  private final CommonFieldLabel lbProject = new CommonFieldLabel(CAPTION_PROJECT_NAME); // NOPMD
  private final ProjectDropDown projectDropDown = new ProjectDropDown(); // NOPMD
  private final Button btnContinue = new ContinueButton(); // NOPMD
  private final Button btnCancel = new CancelButton(); // NOPMD


  /**
   * Creates message panel without title.
   */
  public SelectMergeProjectPanel() {
    super(true);
    setWidth("100%");
    getUserPanel().setWidth("100%");
    btnCancel.setAlignX(Layout.RIGHT);
    final GridIterator gi = new GridIterator(getUserPanel(), 2);
    gi.add(lbProject).add(projectDropDown);
    gi.addBlankLine();
    gi.add(btnCancel).add(new CommonFlow(new Label("  "), btnContinue));

    btnContinue.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = 6041790793082587405L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
        // validate
        if (projectDropDown.getCode() == ProjectDropDown.NOT_SELECTED_ID) {
          // not selected, invalidate
          showErrorMessage(MESSAGE_PLEASE_SELECT_PROJECT);
          return Tierlet.Result.Continue();
        }

        // go to merge creation
        final Parameters parameters = new Parameters();
        parameters.addParameter(Pages.PARAM_PROJECT_ID, projectDropDown.getCode());
        return Tierlet.Result.Done(Pages.PAGE_MERGE_EDIT, parameters);
      }
    });

    btnCancel.addListener(new ButtonPressedListener() {
      private static final long serialVersionUID = -6710624587102682370L;


      public Tierlet.Result buttonPressed(final ButtonPressedEvent buttonPressedEvent) {
        return Tierlet.Result.Done(Pages.PAGE_MERGE_LIST);
      }
    });
  }
}
