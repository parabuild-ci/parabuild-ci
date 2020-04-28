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

import org.parabuild.ci.configuration.GroupMemberVO;
import org.parabuild.ci.object.Group;
import org.parabuild.ci.object.GroupBuildAccess;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Saveable;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Panel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * GroupBuildsPanel displays a list of build check boxes. Builds
 * that are a memeber of this group are checked.
 */
public final class GroupBuildsPanel extends MessagePanel implements Saveable {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  private int groupID = Group.UNSAVED_ID;
  private final List buildCheckBoxes = new ArrayList(11);


  public GroupBuildsPanel() {
    super(false); // don't show conent border
    load(Group.UNSAVED_ID); // load all builds unselected
  }


  /**
   * Loads configuration for this group.
   *
   * @param group
   */
  public void load(final Group group) {
    load(group.getID());
  }


  /**
   * Loads configuration for this group.
   *
   * @param groupID
   */
  public void load(final int groupID) {
    final Panel cp = super.getUserPanel();
    cp.clear();
    buildCheckBoxes.clear();
    final List displayBuilds = SecurityManager.getInstance().getSecurityGroupBuilds(groupID);
    for (final Iterator i = displayBuilds.iterator(); i.hasNext();) {
      final GroupMemberVO groupBuildVO = (GroupMemberVO)i.next();
      final GroupMemberCheckBox cb = new GroupMemberCheckBox(groupBuildVO);
      cp.add(new Flow().add(cb).add(new Label(" ")).add(new CommonLabel(groupBuildVO.getName())));
      buildCheckBoxes.add(cb);
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
    if (groupID == Group.UNSAVED_ID) throw new IllegalStateException("Group ID is not set");
    // go through the list of group's build check boxes
    for (final Iterator i = buildCheckBoxes.iterator(); i.hasNext();) {
      final GroupMemberCheckBox cb = (GroupMemberCheckBox)i.next();
      final GroupMemberVO vo = cb.getGroupMemberVO();
      final int activeBuildID = vo.getID();
      if (cb.isChecked() && !vo.isGroupMember()) {
        // build added to group
        SecurityManager.getInstance().save(new GroupBuildAccess(this.groupID, activeBuildID));
        vo.setGroupMember(true);
      } else if (!cb.isChecked() && vo.isGroupMember()) {
        // build deleted from group
        SecurityManager.getInstance().deleteBuildFromGroup(activeBuildID, groupID);
        vo.setGroupMember(false);
      }
    }
    return true;
  }


  /**
   * Sets group ID for new group.
   *
   * @param groupID to set
   */
  public void setGroupID(final int groupID) {
    this.groupID = groupID;
  }
}
