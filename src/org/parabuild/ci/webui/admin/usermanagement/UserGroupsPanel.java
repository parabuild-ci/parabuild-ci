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

import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * UserGroupsPanel displays a list of group check boxes. Groups
 * that a user is a memeber of are checked.
 */
public final class UserGroupsPanel extends MessagePanel implements Saveable {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(BasePage.class); // NOPMD

  private int userID = User.UNSAVED_ID;
  private final List groupCheckBoxes = new ArrayList(11);


  public UserGroupsPanel() {
    super(false); // don't show conent border
    load(User.UNSAVED_ID); // load all groups unselected
  }


  /**
   * Loads group configuration for this user.
   *
   * @param userID
   */
  public void load(final int userID) {
//    if (log.isDebugEnabled()) log.debug("loading groups for userID: " + userID);
    final Panel cp = super.getUserPanel();
    cp.clear();
    groupCheckBoxes.clear();
    final List displayGroups = SecurityManager.getInstance().getDisplayUserGroups(userID);
    for (final Iterator i = displayGroups.iterator(); i.hasNext();) {
      final DisplayUserGroupVO displayUG = (DisplayUserGroupVO)i.next();
      if (log.isDebugEnabled()) log.debug("loading displayUG: " + displayUG);
      final UserGroupCheckBox cbUG = new UserGroupCheckBox(displayUG);
      cp.add(new Flow().add(cbUG).add(new Label(" ")).add(new CommonLabel(displayUG.getGroupName())));
      groupCheckBoxes.add(cbUG);
    }
  }


  /**
   * When called, component should save it's content. This method should
   * return <code>true</code> when content of a component is saved successfully.
   * If not, a component should display a error message in it's area and return
   * <code>false</code>
   *
   * @return true if saved successfuly
   */
  public boolean save() {
    if (userID == User.UNSAVED_ID) throw new IllegalStateException("User ID is not set");

    // go through the list of user's group check boxes
    for (final Iterator i = groupCheckBoxes.iterator(); i.hasNext();) {
      final UserGroupCheckBox cbUG = (UserGroupCheckBox)i.next();

      // NOTE: vimeshev - fix for bug # - This is a
      // temporarily workaround. Apparently, the framework
      // submit "unchecked" as a value of a check box if it
      // is not visble while it should leave it unchanged.
      if (!this.isVisible()) continue;

      final DisplayUserGroupVO displayUG = cbUG.getDisplayUG();
      if (log.isDebugEnabled()) log.debug("analizing displayUG: " + displayUG);
      final int groupID = displayUG.getGroupID();
      if (log.isDebugEnabled()) log.debug("cbUG: " + cbUG);
      if (cbUG.isChecked() && !displayUG.isGroupMember()) {
        // user added to group
        final UserGroup userGroup = new UserGroup(userID, groupID);
        SecurityManager.getInstance().save(userGroup);
        displayUG.setGroupMember(true);
      } else if (!cbUG.isChecked() && displayUG.isGroupMember()) {
        // user deleted from group
        SecurityManager.getInstance().deleteUserFromGroup(userID, groupID);
        if (log.isDebugEnabled()) log.debug("deleting userID: " + userID + " from group ID: " + groupID);
        displayUG.setGroupMember(false);
      }
    }
    return true;
  }


  /**
   * @return number of loaded groups
   */
  int getGroupCount() {
    return groupCheckBoxes.size();
  }


  /**
   * Sets user ID for new user.
   *
   * @param userID to set
   */
  public void setUserID(final int userID) {
    this.userID = userID;
  }


  public static final class UserGroupCheckBox extends CheckBox {

    private static final long serialVersionUID = -200509072546996518L;
    private DisplayUserGroupVO displayUG = null;


    /**
     * Constructor.
     */
    public UserGroupCheckBox(final DisplayUserGroupVO displayUG) {
      this.displayUG = displayUG;
      this.setChecked(displayUG.isGroupMember());
    }


    /**
     * @return DisplayUserGroupVO backing this UserGroupCheckBox
     */
    public DisplayUserGroupVO getDisplayUG() {
      return displayUG;
    }


    public String toString() {
      return "UserGroupCheckBox{" +
        "displayUG=" + displayUG +
        ", isChecked=" + isChecked() +
        '}';
    }
  }
}
