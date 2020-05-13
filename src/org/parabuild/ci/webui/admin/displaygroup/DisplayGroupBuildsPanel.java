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

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.DisplayGroupBuildVO;
import org.parabuild.ci.configuration.DisplayGroupManager;
import org.parabuild.ci.object.DisplayGroup;
import org.parabuild.ci.object.DisplayGroupBuild;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.Saveable;
import viewtier.ui.CheckBox;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Panel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * DisplayGroupBuildsPanel displays a list of build check boxes. Builds
 * that are a memeber of this display group are checked.
 */
public final class DisplayGroupBuildsPanel extends MessagePanel implements Saveable {

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  private int displayGroupID = DisplayGroup.UNSAVED_ID;
  private final List buildCheckBoxes = new ArrayList(11);


  public DisplayGroupBuildsPanel() {
    super(false); // don't show conent border
    load(DisplayGroup.UNSAVED_ID); // load all builds unselected
  }


  /**
   * Loads configuration for this display group.
   *
   * @param displayGroup
   */
  public void load(final DisplayGroup displayGroup) {
    load(displayGroup.getID());
  }


  /**
   * Loads configuration for this display group.
   *
   * @param displayGroupID
   */
  public void load(final int displayGroupID) {
    final Panel cp = super.getUserPanel();
    cp.clear();
    buildCheckBoxes.clear();
    final List displayBuilds = DisplayGroupManager.getInstance().getDisplayGroupBuildVOList(displayGroupID);
    for (final Iterator i = displayBuilds.iterator(); i.hasNext();) {
      final DisplayGroupBuildVO groupBuildVO = (DisplayGroupBuildVO)i.next();
      final GroupBuildCheckBox cb = new GroupBuildCheckBox(groupBuildVO);
      cp.add(new Flow().add(cb).add(new Label(" ")).add(new CommonLabel(groupBuildVO.getBuildName())));
      buildCheckBoxes.add(cb);
    }
    this.displayGroupID = displayGroupID;
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
    if (displayGroupID == DisplayGroup.UNSAVED_ID) throw new IllegalStateException("Display group ID is not set");
    // go through the list of group's build check boxes
    for (final Iterator i = buildCheckBoxes.iterator(); i.hasNext();) {
      final GroupBuildCheckBox cb = (GroupBuildCheckBox)i.next();
      final DisplayGroupBuildVO vo = cb.getDisplayVO();
      final int activeBuildID = vo.getBuildID();
      if (cb.isChecked() && !vo.isGroupMember()) {
        // build added to display group
        ConfigurationManager.getInstance().saveObject(new DisplayGroupBuild(this.displayGroupID, activeBuildID));
        vo.setGroupMember(true);
      } else {
        if (!cb.isChecked() && vo.isGroupMember()) {
          // build deleted from display group
          DisplayGroupManager.getInstance().deleteBuildFromDisplayGroup(activeBuildID, displayGroupID);
          vo.setGroupMember(false);
        }
      }
    }
    return true;
  }


  /**
   * Sets display group ID for new group.
   *
   * @param displayGroupID to set
   */
  public void setDisplayGroupID(final int displayGroupID) {
    this.displayGroupID = displayGroupID;
  }


  public static final class GroupBuildCheckBox extends CheckBox {

    private static final long serialVersionUID = -8266092027320538754L;
    private final DisplayGroupBuildVO displayUG;


    /**
     * Constructor.
     */
    public GroupBuildCheckBox(final DisplayGroupBuildVO displayUG) {
      this.displayUG = displayUG;
      this.setChecked(displayUG.isGroupMember());
    }


    /**
     * @return DisplayGroupBuildVO backing this GroupBuildCheckBox
     */
    public DisplayGroupBuildVO getDisplayVO() {
      return displayUG;
    }
  }
}
