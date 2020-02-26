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
package org.parabuild.ci.webui.result;

import java.util.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.util.*;
import org.parabuild.ci.configuration.*;
import org.parabuild.ci.error.*;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import org.parabuild.ci.webui.admin.*;
import org.parabuild.ci.project.*;
import viewtier.ui.*;

/**
 * Panel to edit display result group.
 */
public final class ResultGroupPanel extends MessagePanel implements Validatable, Saveable {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  private static final Log log = LogFactory.getLog(ResultGroupPanel.class);

  private static final String CAPTION_NAME = "Result group name: ";
  private static final String CAPTION_DESCRIPTION = "Description: ";
  private static final String CAPTION_PROJECT = "Project: ";

  private static final String FNAME_NAME = "resultgroupname";
  private static final String FNAME_DESCR = "resultgroupdescr";

  // inputs
  private final CommonField flName = new CommonField(FNAME_NAME, 50, 50);
  private final CommonField flDescr = new CommonField(FNAME_DESCR, 50, 50);
  private final ProjectDropDown flProject = new ProjectDropDown();

  private int resultGroupID = ResultGroup.UNSAVED_ID;


  /**
   * Creates message panel without title.
   */
  public ResultGroupPanel() {
    super(true); // don't show conent border
    final Panel cp = getUserPanel();
    cp.setWidth(Pages.PAGE_WIDTH);
    final GridIterator gi = new GridIterator(cp, 2);
    gi.addPair(new CommonFieldLabel(CAPTION_NAME), new RequiredFieldMarker(flName));
    gi.addPair(new CommonFieldLabel(CAPTION_DESCRIPTION), new RequiredFieldMarker(flDescr));
    gi.addPair(new CommonFieldLabel(CAPTION_PROJECT), new RequiredFieldMarker(flProject));
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    // general validation
    final List errors = new ArrayList(11);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_NAME, flName);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_DESCRIPTION, flDescr);

    // continue validation
    if (errors.isEmpty()) {
      // check if new resultGroup with this name already exists
      if (resultGroupID == ResultGroup.UNSAVED_ID && ResultGroupManager.getInstance().getResultGroupByName(flName.getValue()) != null) {
        errors.add("ResultGroup with name \"" + flName.getValue() + "\" already exists.");
      }

      // continue validation
      if (flProject.getCode() == Project.UNSAVED_ID) {
        errors.add("Please select project");
      }
    }



    // validation failed, show errors
    if (errors.isEmpty()) return true;
    super.showErrorMessage(errors);
    return false; // return
  }


  /**
   * Loads given display resultGroup.
   *
   * @param resultGroup
   */
  public void load(final ResultGroup resultGroup) {
    resultGroupID = resultGroup.getID();
    flName.setValue(resultGroup.getName());
    flDescr.setValue(resultGroup.getDescription());

    // set project drop down
    final ProjectManager pm = ProjectManager.getInstance();
    final ProjectResultGroup projectResultGroup = pm.getProjectResultGroup(resultGroup.getID());
    flProject.setCode(projectResultGroup.getProjectID());
  }


  /**
   * Saves display resultGroup data.
   *
   * @return if saved successfuly.
   */
  public boolean save() {
    try {

      // validate
      if (!validate()) return false;

      // get involved managers
      final ResultGroupManager rgm = ResultGroupManager.getInstance();
      final ProjectManager pm = ProjectManager.getInstance();

      // get display resultGroup object
      ResultGroup resultGroup = null;
      ProjectResultGroup projectResultGroup = null;
      if (log.isDebugEnabled()) log.debug("resultGroupID: " + resultGroupID);
      if (resultGroupID == ResultGroup.UNSAVED_ID) {
        resultGroup = new ResultGroup();
        projectResultGroup = new ProjectResultGroup();
      } else {
        resultGroup = rgm.getResultGroup(resultGroupID);
        projectResultGroup = pm.getProjectResultGroup(resultGroupID);
      }

      // cover-ass check - if the resultGroup is there
      if (resultGroup == null) {
        showErrorMessage("ResultGroup being edited not found. Please cancel editing and try again.");
        return false;
      }

      // save resultGroup object
      resultGroup.setDescription(flDescr.getValue().trim());
      resultGroup.setName(flName.getValue().trim());
      rgm.saveResultGroup(resultGroup);
      if (log.isDebugEnabled()) log.debug("resultGroup: " + resultGroup);

      // save project/group link
      projectResultGroup.setProjectID(flProject.getCode());
      projectResultGroup.setResultGroupID(resultGroup.getID());
      pm.saveProjectResultGroup(projectResultGroup);

      // return
      return true;
    } catch (final Exception e) {
      showAndReportSaveError(e);
      return false;
    }
  }


  private void showAndReportSaveError(final Exception e) {
    final String description = "Error while saving resultGroup information: " + StringUtils.toString(e);
    showErrorMessage(description + ". Please cancel editing and try again.");
    // record error
    final Error error = new Error(description);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setSendEmail(false);
    error.setDetails(e);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }
}
