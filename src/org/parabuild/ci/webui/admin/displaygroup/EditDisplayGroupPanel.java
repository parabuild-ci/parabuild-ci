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
package org.parabuild.ci.webui.admin.displaygroup;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.InputValidator;
import org.parabuild.ci.configuration.DisplayGroupManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.DisplayGroup;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;
import viewtier.ui.Layout;
import viewtier.ui.Panel;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel to edit display group.
 */
public final class EditDisplayGroupPanel extends MessagePanel implements Validatable, Saveable {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  private static final Log log = LogFactory.getLog(EditDisplayGroupPanel.class);

  public static final String CAPTION_NAME = "Display group name:";
  public static final String CAPTION_DESCRIPTION = "Description:";
  public static final String CAPTION_INCLUDES_BUILDS = "Includes builds:";

  public static final String FNAME_NAME = "dispgroupname";
  public static final String FNAME_DESCR = "dispgroupdescr";

  // inputs
  private final CommonField flName = new CommonField(FNAME_NAME, 50, 50); // NOPMD
  private final CommonField flDescr = new CommonField(FNAME_DESCR, 50, 50); // NOPMD
  private final DisplayGroupBuildsPanel pnlDisplayGroupBuilds = new DisplayGroupBuildsPanel(); // NOPMD

  private int displayGroupID = DisplayGroup.UNSAVED_ID;


  /**
   * Creates message panel without title.
   */
  public EditDisplayGroupPanel() {
    super(true); // don't show conent border
    final Panel cp = getUserPanel();
    cp.setWidth(Pages.PAGE_WIDTH);
    final GridIterator gi = new GridIterator(cp, 2);
    gi.addPair(new CommonFieldLabel(CAPTION_NAME), new RequiredFieldMarker(flName));
    gi.addPair(new CommonFieldLabel(CAPTION_DESCRIPTION), new RequiredFieldMarker(flDescr));
    gi.addBlankLine();
    gi.addPair(new CommonFieldLabel(CAPTION_INCLUDES_BUILDS), pnlDisplayGroupBuilds);

    // align
    new CommonFieldLabel(CAPTION_INCLUDES_BUILDS).setAlignY(Layout.TOP);
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
    InputValidator.validateFieldNotBlank(errors, CAPTION_NAME, flName);
    InputValidator.validateFieldNotBlank(errors, CAPTION_DESCRIPTION, flDescr);

    // check if new group with this name already exists
    if (errors.isEmpty()) {
      if (displayGroupID == DisplayGroup.UNSAVED_ID && DisplayGroupManager.getInstance().getDisplayGroupByName(flName.getValue()) != null) {
        errors.add("Display group with name \"" + flName.getValue() + "\" already exists.");
      }
    }

    // OK ?
    if (errors.isEmpty()) {
      return true;
    }

    // validation failed, show errors
    super.showErrorMessage(errors);
    return false; // return
  }


  /**
   * Loads given display group.
   *
   * @param displayGroup
   */
  public void load(final DisplayGroup displayGroup) {
    displayGroupID = displayGroup.getID();
    flName.setValue(displayGroup.getName());
    flDescr.setValue(displayGroup.getDescription());
    pnlDisplayGroupBuilds.load(displayGroup); // because it is separate table
  }


  /**
   * Saves display group data.
   *
   * @return if saved successfuly.
   */
  public boolean save() {
    try {

//      final ConfigurationManager cm = ConfigurationManager.getInstance();

      // validate
      if (!validate()) return false;

      // get display group object
      DisplayGroup displayGroup = null;
      if (log.isDebugEnabled()) log.debug("displayGroupID: " + displayGroupID);
      if (displayGroupID == DisplayGroup.UNSAVED_ID) {
        displayGroup = new DisplayGroup();
      } else {
        displayGroup = DisplayGroupManager.getInstance().getDisplayGroup(displayGroupID);
      }

      // cover-ass check - if the group is there
      if (displayGroup == null) {
        showErrorMessage("Display group being edited not found. Please cancel editing and try again.");
        return false;
      }

      // set props
      displayGroup.setDescription(flDescr.getValue().trim());
      displayGroup.setName(flName.getValue().trim());

      // save display group object
      DisplayGroupManager.getInstance().saveDisplayGroup(displayGroup);

      // save display group builds
      pnlDisplayGroupBuilds.setDisplayGroupID(displayGroup.getID());

      // return
      return pnlDisplayGroupBuilds.save();
    } catch (final Exception e) {
      // show error
      final String description = "Error while saving display group information: " + StringUtils.toString(e);
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
}
