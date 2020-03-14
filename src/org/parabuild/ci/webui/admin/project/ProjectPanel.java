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
package org.parabuild.ci.webui.admin.project;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.PropertyToInputMap;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.Project;
import org.parabuild.ci.object.ProjectAttribute;
import org.parabuild.ci.project.ProjectManager;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Field;
import viewtier.ui.Label;
import viewtier.ui.Panel;

import java.util.ArrayList;
import java.util.List;

public final class ProjectPanel extends MessagePanel implements Validatable, Saveable {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  private static final Log log = LogFactory.getLog(ProjectPanel.class);

  public static final byte EDIT_MODE_ADMIN = 0;

  private static final String CAPTION_PROJECT_NAME = "Project name: ";
  private static final String CAPTION_DESCRIPTION = "Description: ";
  private static final String CAPTION_KEY = "Key: ";


  private final Label lbName = new CommonFieldLabel(CAPTION_PROJECT_NAME); // NOPMD
  private final Label lbDescription = new CommonFieldLabel(CAPTION_DESCRIPTION); // NOPMD
  private final Label lbKey = new CommonFieldLabel(CAPTION_KEY); // NOPMD
  private final Field flName = new Field(60, 60); // NOPMD
  private final Field flDescription = new Field(100, 80); // NOPMD
  private final Field flKey = new Field(15, 15); // NOPMD


  private int projectID = Project.UNSAVED_ID;


  /**
   * Creates message panel without title.
   */
  public ProjectPanel() {
    super(true); // don't show conent border
    final Panel cp = getUserPanel();
    cp.setWidth(Pages.PAGE_WIDTH);
    final GridIterator gi = new GridIterator(cp, 2);
    gi.addPair(lbName, new RequiredFieldMarker(flName));
    gi.addPair(lbDescription, new RequiredFieldMarker(flDescription));
    gi.addPair(lbKey, new RequiredFieldMarker(flKey));
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    if (log.isDebugEnabled()) log.debug("validating project");
    // general validation
    final List errors = new ArrayList(11);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_PROJECT_NAME, flName);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_KEY, flKey);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_DESCRIPTION, flDescription);

    // further validation
    if (errors.isEmpty()) {
      WebuiUtils.validateFieldStrict(errors, CAPTION_KEY, flKey);
      // validate that this key does not exist
      final ProjectManager pm = ProjectManager.getInstance();
      pm.getProjectByKey(normalizedKeyValue());

    }

    // OK ?
    if (errors.isEmpty()) {
      return true;
    }

    // validation failed, show errors
    super.showErrorMessage(errors);
    return false; // return
  }


  private String normalizedKeyValue() {return flKey.getValue().trim().toUpperCase();}


  /**
   * Loads given project.
   *
   * @param project
   */
  public void load(final Project project) {
    projectID = project.getID();
    flName.setValue(project.getName());
    flDescription.setValue(project.getDescription());
    flKey.setValue(project.getKey());
  }


  /**
   * Saves project data.
   *
   * @return if saved successfuly.
   */
  public boolean save() {
    if (log.isDebugEnabled()) log.debug("saving project");
    try {
      // validate
      if (!validate()) return false;

      // get project object
      Project project = null;
      if (log.isDebugEnabled()) log.debug("projectID: " + projectID);
      if (projectID == Project.UNSAVED_ID) {
        // create project object and set random password for new project.
        project = new Project();
      } else {
        project = ProjectManager.getInstance().getProject(projectID);
      }

      // cover-ass check - if the project is there
      if (project == null) {
        showErrorMessage("Project being edited not found. Please cancel editing and try again.");
        return false;
      }

      // set project data
      project.setDescription(flDescription.getValue());
      project.setName(flName.getValue());
      project.setKey(normalizedKeyValue());

      // save project object
      final ProjectManager pm = ProjectManager.getInstance();
      pm.saveProject(project);

      // return
      return true;
    } catch (final Exception e) {
      // show error
      final String description = "Error while saving project information: " + StringUtils.toString(e);
      showErrorMessage(description + ". Please cancel editing and try again.");
      // record error
      final Error error = new Error(description);
      error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
      error.setSendEmail(false);
      error.setDetails(e);
      ErrorManagerFactory.getErrorManager().reportSystemError(error);
      return false;
    }
  }


  /**
   * @return loaded project ID
   */
  public int getProjectID() {
    return projectID;
  }


  /**
   * Factory method to create ProjectAttribute handler to be used by propertyToInputMap
   *
   * @return implementation of PropertyToInputMap.PropertyHandler
   *
   * @see PropertyToInputMap.PropertyHandler
   */
  private static PropertyToInputMap.PropertyHandler<ProjectAttribute> makePropertyHandler() {
    return new PropertyToInputMap.PropertyHandler<ProjectAttribute>() {
      private static final long serialVersionUID = -8036995290299804238L;


      public ProjectAttribute makeProperty(final String propertyName) {
        final ProjectAttribute prop = new ProjectAttribute();
        prop.setName(propertyName);
        return prop;
      }


      public void setPropertyValue(final ProjectAttribute property, final String propertyValue) {
        ((ProjectAttribute)property).setValue(propertyValue);
      }


      public String getPropertyValue(final ProjectAttribute property) {
        return ((ProjectAttribute)property).getValue();
      }


      public String getPropertyName(final ProjectAttribute property) {
        return ((ProjectAttribute)property).getName();
      }
    };
  }
}
