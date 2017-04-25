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

import org.parabuild.ci.object.Group;
import org.parabuild.ci.security.BuildRights;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import viewtier.ui.CheckBox;

import java.util.HashMap;
import java.util.Map;

/**
 * GroupRightsPanel displays a list of group rights check boxes.
 * Those rights apply to builds belonging to this group.
 */
public final class BuildRightsPanel extends MessagePanel {

  private static final boolean CB_CHECKED = true;
  private static final boolean CB_UNCHECKED = false;

  private static final String KEY_ACTIVATE = "can.activate.build";
  private static final String KEY_START = "can.start.build";
  private static final String KEY_STOP = "can.stop.build";
  private static final String KEY_VIEW = "can.view.build";
  private static final String KEY_UPDATE = "can.update.build";
  private static final String KEY_CREATE = "can.create.build";
  private static final String KEY_DELETE = "can.delete.build";
  private static final String KEY_DELETE_RESULTS = "can.delete.results";
  private static final String KEY_PUBLISH_RESULTS = "can.publish.results";

  private static final long serialVersionUID = 124348935234219001L; // NOPMD
  private final Map rightsCheckBoxes = new HashMap(11);
  private final GridIterator gi;


  public BuildRightsPanel() {
    super(false); // don't show content border
    this.gi = new GridIterator(getUserPanel(), 2);
    this.addRightCheckBox(KEY_CREATE, CB_UNCHECKED, "Create build", false);
    this.addRightCheckBox(KEY_ACTIVATE, CB_UNCHECKED, "Activate build", true);
    this.addRightCheckBox(KEY_UPDATE, CB_UNCHECKED, "Edit build", true);
    this.addRightCheckBox(KEY_DELETE, CB_UNCHECKED, "Delete build", false);
    this.addRightCheckBox(KEY_START, CB_UNCHECKED, "Start build", true);
    this.addRightCheckBox(KEY_STOP, CB_UNCHECKED, "Stop build", true);
    this.addRightCheckBox(KEY_VIEW, CB_CHECKED, "View build", false);
    this.addRightCheckBox(KEY_PUBLISH_RESULTS, CB_UNCHECKED, "Publish results", true);
    this.addRightCheckBox(KEY_DELETE_RESULTS, CB_UNCHECKED, "Delete results", true);

    // make cbView R/O
    final CheckBox cbView = (CheckBox) rightsCheckBoxes.get(KEY_VIEW);
    if (cbView != null) {
      cbView.setEditable(false);
    }
  }


  /**
   * Sets rights according to the given group
   *
   * @param group
   */
  public void setRightsFromGroup(final Group group) {
    final BuildRights buildRights = SecurityManager.getInstance().groupToBuildRights(group);
    setCheckBox(KEY_ACTIVATE, buildRights.isAllowedToActivateBuild());
    setCheckBox(KEY_CREATE, buildRights.isAllowedToCreateBuild());
    setCheckBox(KEY_DELETE, buildRights.isAllowedToDeleteBuild());
    setCheckBox(KEY_START, buildRights.isAllowedToStartBuild());
    setCheckBox(KEY_STOP, buildRights.isAllowedToStopBuild());
    setCheckBox(KEY_VIEW, buildRights.isAllowedToViewBuild());
    setCheckBox(KEY_UPDATE, buildRights.isAllowedToUpdateBuild());
    setCheckBox(KEY_PUBLISH_RESULTS, buildRights.isAllowedToPublishResults());
    setCheckBox(KEY_DELETE_RESULTS, buildRights.isAllowedToDeleteResults());
  }


  /**
   * Updates rights into the given group
   *
   * @param group
   */
  public void updateRightsToGroup(final Group group) {
    group.setAllowedToActivateBuild(getCheckBoxSelection(KEY_ACTIVATE));
    group.setAllowedToCreateBuild(getCheckBoxSelection(KEY_CREATE));
    group.setAllowedToDeleteBuild(getCheckBoxSelection(KEY_DELETE));
    group.setAllowedToStartBuild(getCheckBoxSelection(KEY_START));
    group.setAllowedToStopBuild(getCheckBoxSelection(KEY_STOP));
    group.setAllowedToUpdateBuild(getCheckBoxSelection(KEY_UPDATE));
    group.setAllowedToPublishResults(getCheckBoxSelection(KEY_PUBLISH_RESULTS));
    group.setAllowedToDeleteResults(getCheckBoxSelection(KEY_DELETE_RESULTS));
    group.setAllowedToViewBuild(true); // REVIEWME: When checkboxes are fixed
  }


  private boolean getCheckBoxSelection(final String key) {
    final CheckBox cb = (CheckBox) rightsCheckBoxes.get(key);
    return cb != null && cb.isChecked();
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
      gi.addPair(cbRight, new CommonLabel(caption));
    }
  }


  private void setCheckBox(final String key, final boolean checked) {
    final CheckBox cb = (CheckBox) rightsCheckBoxes.get(key);
    if (cb != null) {
      cb.setChecked(checked);
    }
  }
}
