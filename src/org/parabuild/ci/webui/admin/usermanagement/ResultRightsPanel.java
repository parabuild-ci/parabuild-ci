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

import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.security.*;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * GroupResultRightsPanel displays a list of group rights check boxes.
 * Those rights apply to build results belonging to this group.
 */
public final class ResultRightsPanel extends MessagePanel {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD

  private static final String CAPTION_ALLOWED_RESULTS = "Allowed to access results:";
  private static final String CAPTION_RIGHTS = "With the following rights:";

  private static final boolean CB_CHECKED = true;
  private static final boolean CB_UNCHECKED = false;

  private static final String KEY_VIEW = "can.view";
  private static final String KEY_UPDATE = "can.update";
  private static final String KEY_CREATE = "can.create";
  private static final String KEY_DELETE = "can.delete";

  private final Map rightsCheckBoxes = new HashMap(11);
  private final Panel pnlResultGroups = new Panel();
  private final Panel pnlResultRights = new Panel();
  private final List resultCheckBoxes = new ArrayList(11);

  private final CommonFieldLabel lbAllowedBuild = new CommonFieldLabel(CAPTION_ALLOWED_RESULTS); // NOPMD
  private final CommonFieldLabel lbRights = new CommonFieldLabel(CAPTION_RIGHTS);  // NOPMD


  public ResultRightsPanel() {
    super(false); // don't show conent border
    final GridIterator gi = new GridIterator(getUserPanel(), 2);

    // add placeholder for result groups
    gi.addPair(lbAllowedBuild, pnlResultGroups);
    gi.addBlankLine();
    gi.addBlankLine();

    gi.addPair(lbRights, pnlResultRights);
    addRightCheckBox(KEY_CREATE, CB_UNCHECKED, "Create result group", false);
    addRightCheckBox(KEY_UPDATE, CB_UNCHECKED, "Edit result group", true);
    addRightCheckBox(KEY_DELETE, CB_UNCHECKED, "Delete result group", true);
    addRightCheckBox(KEY_VIEW, CB_CHECKED, "View result group", false);

    // make cbView R/O
    final CheckBox cbView = getCheckBox(KEY_VIEW);
    if (cbView != null) cbView.setEditable(false);

    // align
    lbAllowedBuild.setAlignY(Layout.TOP);
    lbRights.setAlignY(Layout.TOP);
  }


  /**
   * Updates rights into the given group
   *
   * @param group
   */
  public void updateRightsToGroup(final Group group) {
    group.setAllowedToCreateResultGroup(getCheckBoxSelection(KEY_CREATE));
    group.setAllowedToDeleteResultGroup(getCheckBoxSelection(KEY_DELETE));
    group.setAllowedToUpdateResultGroup(getCheckBoxSelection(KEY_UPDATE));
    group.setAllowedToViewResultGroup(true); // REVIEWME: When checkboxes are fixed
  }


  private boolean getCheckBoxSelection(final String key) {
    final CheckBox cb = getCheckBox(key);
    if (cb == null) {
      // REVIEWME: simeshev@parabuilci.org -> add warning
      return false;
    }
    return cb.isChecked();
  }


  /**
   * Helper method to populate check box
   *
   * @param key
   * @param checked
   * @param caption
   * @param visible
   */
  private void addRightCheckBox(final String key, final boolean checked, final String caption, final boolean visible) {
    final CheckBox cbRight = new CheckBox();
    cbRight.setChecked(checked);
    rightsCheckBoxes.put(key, cbRight);
    if (visible) {
      pnlResultRights.add(new CommonFlow(cbRight, new CommonLabel(caption)));
    }
  }


  private void setCheckBox(final String key, final boolean checked) {
    final CheckBox cb = getCheckBox(key);
    if (cb != null) cb.setChecked(checked);
  }


  private CheckBox getCheckBox(final String key) {
    return (CheckBox)rightsCheckBoxes.get(key);
  }


  public boolean validate() {
    return true;
  }


  public void load(final Group group) {

    // init check boxes right list

    pnlResultGroups.clear();
    resultCheckBoxes.clear();
    final List displayResults = SecurityManager.getInstance().getSecurityGroupResults(group.getID());
    for (final Iterator i = displayResults.iterator(); i.hasNext();) {
      final GroupMemberVO groupBuildVO = (GroupMemberVO)i.next();
      final GroupMemberCheckBox cb = new GroupMemberCheckBox(groupBuildVO);
      pnlResultGroups.add(new CommonFlow(cb, new Label(" "), new CommonLabel(groupBuildVO.getName())));
      resultCheckBoxes.add(cb);
    }

    // copy rights to checkboxes

    final ResultGroupRights rights = SecurityManager.getInstance().createResultGroupRightsFromgroup(group);
    setCheckBox(KEY_CREATE, rights.isAllowedToCreateResultGroup());
    setCheckBox(KEY_DELETE, rights.isAllowedToDeleteResultGroup());
    setCheckBox(KEY_VIEW, rights.isAllowedToViewResultGroup());
    setCheckBox(KEY_UPDATE, rights.isAllowedToUpdateResultGroup());

    // disable check boxes for anonimous group

    if (group.getName() == Group.SYSTEM_ANONYMOUS_GROUP) {
      getCheckBox(KEY_CREATE).setEditable(false);
      getCheckBox(KEY_DELETE).setEditable(false);
      getCheckBox(KEY_UPDATE).setEditable(false);
    }
  }


  public boolean save(final int groupID) {
    if (groupID == Group.UNSAVED_ID) throw new IllegalStateException("Group ID is not set");
    // go through the list of group's build check boxes
    for (final Iterator i = resultCheckBoxes.iterator(); i.hasNext();) {
      final GroupMemberCheckBox cb = (GroupMemberCheckBox)i.next();
      final GroupMemberVO vo = cb.getGroupMemberVO();
      final int resultGroupID = vo.getID();
      if (cb.isChecked() && !vo.isGroupMember()) {
        // build added to group
        SecurityManager.getInstance().save(new ResultGroupAccess(groupID, resultGroupID));
        vo.setGroupMember(true);
      } else if (!cb.isChecked() && vo.isGroupMember()) {
        // build deleted from group
        SecurityManager.getInstance().deleteResultGroupFromGroup(resultGroupID, groupID);
        vo.setGroupMember(false);
      }
    }
    return true;
  }
}
