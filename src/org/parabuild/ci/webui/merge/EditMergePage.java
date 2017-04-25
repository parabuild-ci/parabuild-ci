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

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.merge.MergeManager;
import org.parabuild.ci.object.MergeConfiguration;
import org.parabuild.ci.object.Project;
import org.parabuild.ci.project.ProjectManager;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.ConversationalTierlet;
import viewtier.ui.Parameters;

/**
 * Edits given merge configuration. If the merge
 * configuration is not provided, edits a new one.
 */
public final class EditMergePage extends BasePage implements ConversationalTierlet {

  private static final long serialVersionUID = 4542828122356933097L;

  private static final String CAPTION_ADDING_MERGE = "Adding Merge";
  private static final String CAPTION_EDITING_MERGE = "Editing Merge: ";
  private static final String MESSAGE_REQUESTED_PROJECT_NOT_FOUND = "Requested project not found";


  public EditMergePage() {
    super(FLAG_SHOW_HEADER_SEPARATOR | FLAG_SHOW_PAGE_HEADER_LABEL);
//    baseContentPanel().setWidth("100%");
  }


  /**
   * Strategy method to be implemented by classes inheriting
   * BasePage.
   *
   * @param parameters
   *
   * @return result of page execution
   */
  protected Result executePage(final Parameters parameters) {
    if (parameters.isParameterPresent(Pages.PARAM_MERGE_ID)) {
      // process merge edit

      // validate
      final String mergeID = parameters.getParameterValue(Pages.PARAM_MERGE_ID);
      if (!StringUtils.isValidInteger(mergeID)) return WebuiUtils.showMergeNotFound(this);
      final MergeConfiguration mergeConfiguration = MergeManager.getInstance().getMergeConfiguration(Integer.parseInt(mergeID));
      if (mergeConfiguration == null) return WebuiUtils.showMergeNotFound(this);

      setPageHeaderAndTitle(CAPTION_EDITING_MERGE + mergeConfiguration.getName());

      // proceed to editing merge config
      final EditMergePanel pnlMergeEdit = new EditMergePanel();
      baseContentPanel().add(pnlMergeEdit);
      pnlMergeEdit.load(mergeConfiguration);

      // surrender control
      return Result.Continue();
    } else if (parameters.isParameterPresent(Pages.PARAM_PROJECT_ID)) {
      // process project section

      // validate
      final String projectID = parameters.getParameterValue(Pages.PARAM_PROJECT_ID);
      if (!StringUtils.isValidInteger(projectID)) return showProjectNotFound();
      final Project project = ProjectManager.getInstance().getProject(Integer.parseInt(projectID));
      if (project == null) return showProjectNotFound();


      setPageHeaderAndTitle(CAPTION_ADDING_MERGE);

      // process new merge with project selected
      final EditMergePanel pnlMergeEdit = new EditMergePanel();
      baseContentPanel().add(pnlMergeEdit);
      pnlMergeEdit.load(project);

      // surrender control
      return Result.Continue();
    } else {
      // proceed to project selection
      baseContentPanel().add(new SelectProjectPanel(Pages.PAGE_MERGE_EDIT, Pages.PAGE_MERGE_LIST));

      setPageHeaderAndTitle(CAPTION_ADDING_MERGE);

      return Result.Continue();
    }
  }


    /**
   * Helper.
   */
  private Result showProjectNotFound() {
    baseContentPanel().showErrorMessage(MESSAGE_REQUESTED_PROJECT_NOT_FOUND);
    return Result.Done();
  }
}
