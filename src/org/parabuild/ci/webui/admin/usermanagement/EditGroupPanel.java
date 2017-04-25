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
package org.parabuild.ci.webui.admin.usermanagement;

import java.util.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.common.*;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

public final class EditGroupPanel extends MessagePanel implements Validatable, Saveable {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  private static final Log log = LogFactory.getLog(EditGroupPanel.class);

  private static final String CAPTION_NAME = "Group name:";
  private static final String CAPTION_DESCRIPTION = "Description:";
  private static final String CAPTION_ALLOWED_BUILDS = "Allowed to access builds:";
  private static final String CAPTION_RIGHTS = "With the following rights:";

  public static final String FNAME_NAME = "gname";
  public static final String FNAME_DESCR = "gdescr";

  // inputs
  private final CommonField flName = new CommonField(FNAME_NAME, 50, 50);
  private final CommonField flDescr = new CommonField(FNAME_DESCR, 50, 50);
  private final GroupBuildsPanel pnlGroupBuilds = new GroupBuildsPanel();
  private final BuildRightsPanel pnlBuildRights = new BuildRightsPanel();
  private final ResultRightsPanel pnlResultRights = new ResultRightsPanel();

  private final CommonFieldLabel lbAllowedBuild = new CommonFieldLabel(CAPTION_ALLOWED_BUILDS); // NOPMD
  private final CommonFieldLabel lbRights = new CommonFieldLabel(CAPTION_RIGHTS);  // NOPMD

  private Panel pnlBuildRightsHolder = new Panel();   // NOPMD

  private int groupID = Group.UNSAVED_ID;


  /**
   * Creates message panel without title.
   */
  public EditGroupPanel() {
    super(true); // don't show conent border
    final Panel cp = getUserPanel();
    cp.setWidth(Pages.PAGE_WIDTH);
    final GridIterator gi = new GridIterator(cp, 2);
    gi.addPair(new CommonFieldLabel(CAPTION_NAME), new RequiredFieldMarker(flName));
    gi.addPair(new CommonFieldLabel(CAPTION_DESCRIPTION), new RequiredFieldMarker(flDescr));
    gi.addBlankLine();
    gi.addPair(pnlBuildRightsHolder, pnlResultRights);

    // get together build rights
    final GridIterator giBuildRightsHolder = new GridIterator(pnlBuildRightsHolder, 2);
    giBuildRightsHolder.addPair(lbAllowedBuild, pnlGroupBuilds);
    giBuildRightsHolder.addBlankLine();
    giBuildRightsHolder.addBlankLine();
    giBuildRightsHolder.addPair(lbRights, pnlBuildRights);

    // align
    lbAllowedBuild.setAlignY(Layout.TOP);
    lbRights.setAlignY(Layout.TOP);

    pnlBuildRightsHolder.setAlignX(Layout.LEFT);
    pnlResultRights.setAlignX(Layout.RIGHT);

//    // separate result groups with border
//    pnlResultRights.setBorder(1, Layout.LEFT, Pages.TABLE_COLOR_BORDER);
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should dispaly a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    // general validation
    final List errors = new ArrayList();
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_NAME, flName);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_DESCRIPTION, flDescr);

    // check if new group with this name already exists
    if (errors.isEmpty()) {
      if (groupID == Group.UNSAVED_ID && SecurityManager.getInstance().getGroupByName(flName.getValue()) != null) {
        errors.add("Group with name \"" + flName.getValue() + "\" already exists.");
      }
    }

    // validate result rights part
    if (errors.isEmpty()) {
      if (!pnlResultRights.validate()) {
        return false;
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
   * Loads given group.
   *
   * @param group
   */
  public void load(final Group group) {
    groupID = group.getID();
    flName.setValue(group.getName());
    flDescr.setValue(group.getDescription());
    pnlGroupBuilds.load(group); // because it is separate table
    pnlBuildRights.setRightsFromGroup(group); // because is is a group itself
    pnlResultRights.load(group);

    // adjust anonious and admin group settings
    if (group.getName().equals(Group.SYSTEM_ADMIN_GROUP)) {
      flName.setEditable(false);
      lbRights.setVisible(false);
      pnlBuildRights.setVisible(false);
    } else if (group.getName().equals(Group.SYSTEM_ANONYMOUS_GROUP)) {
      flName.setEditable(false);
      lbRights.setVisible(false);
      pnlBuildRights.setVisible(false);
    }
  }


  /**
   * Saves group data.
   *
   * @return if saved successfuly.
   */
  public boolean save() {
    try {
      // validate
      if (!validate()) return false;

      // get group object
      Group group = null;
      if (log.isDebugEnabled()) log.debug("groupID: " + groupID);
      if (groupID == Group.UNSAVED_ID) {
        group = new Group();
      } else {
        group = SecurityManager.getInstance().getGroup(groupID);
      }

      // cover-ass check - if the group is there
      if (group == null) {
        showErrorMessage("Group not found. Please cancel editing and try again.");
        return false;
      }

      // set props
      group.setDescription(flDescr.getValue().trim());
      group.setName(flName.getValue().trim());
      pnlBuildRights.updateRightsToGroup(group);
      pnlResultRights.updateRightsToGroup(group);

      // save group object
      SecurityManager.getInstance().save(group);

      // save group builds
      pnlGroupBuilds.setGroupID(group.getID());

      pnlResultRights.save(group.getID());

      // return
      return pnlGroupBuilds.save();
    } catch (Exception e) {
      // show error
      final String description = "Error while saving group information: " + StringUtils.toString(e);
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