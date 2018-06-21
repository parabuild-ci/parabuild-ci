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
package org.parabuild.ci.webui.admin.email;

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.object.GlobalVCSUserMap;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.EmailField;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.MessagePanel;
import org.parabuild.ci.webui.common.WebuiUtils;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.configuration.GlobalVCSUserMapManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Edits a mapping for a single user.
 *
 * @since Dec 27, 2008 3:25:04 PM
 */
final class GlobalVCSUserMapPanel extends MessagePanel {

  // Captions
  private static final String CAPTION_NOTE = "Note: ";
  private static final String CAPTION_EMAIL = "Email: ";
  private static final String CAPTION_VERSION_CONTROL_USER = "Version Control User: ";

  // Labels
  private final CommonFieldLabel lbNote = new CommonFieldLabel(CAPTION_NOTE);
  private final CommonFieldLabel lbEmail = new CommonFieldLabel(CAPTION_EMAIL);
  private final CommonFieldLabel lbVCSUser = new CommonFieldLabel(CAPTION_VERSION_CONTROL_USER);

  // Fields
  private final EmailField flEmail = new EmailField("vcs_user_email", 60);
  private final CommonField flVCSUser = new CommonField("vcs_user_name", 50, 50);
  private final CommonField flNote = new CommonField("vcs_user_note", 70, 70);

  //
  private int mappingID = GlobalVCSUserMap.UNSAVED_ID;


  /**
   * Constructor.
   */
  public GlobalVCSUserMapPanel() {
    showHeaderDivider(true);
    final GridIterator gridIter = new GridIterator(getUserPanel(), 2);
    gridIter.addPair(lbVCSUser, new RequiredFieldMarker(flVCSUser));
    gridIter.addPair(lbEmail, new RequiredFieldMarker(flEmail));
    gridIter.addPair(lbNote, flNote);
  }


  public boolean save() {
    final GlobalVCSUserMapManager mappingManager = GlobalVCSUserMapManager.getInstance();
    if (isValid()) {
      try {
        final GlobalVCSUserMap map;
        if (isNew()) {
          map = new GlobalVCSUserMap();
        } else {
          map = mappingManager.getMapping(new Integer(mappingID));
        }
        map.setVcsUserName(flVCSUser.getValue().trim());
        map.setEmail(flEmail.getValue().trim().toLowerCase());
        map.setDescription(flNote.getValue().trim());
        mappingManager.saveMapping(map);
        return true;
      } catch (final Exception e) {
        showErrorMessage(StringUtils.toString(e));
        return false;
      }
    } else {
      return false;
    }
  }


  public void load(final GlobalVCSUserMap map) {
    mappingID = map.getID();
    flEmail.setValue(map.getEmail());
    flVCSUser.setValue(map.getVcsUserName());
    flNote.setValue(map.getDescription());
  }

  /**
   * Returns true if this entry form is valid.
   *
   * @return true if this entry form is valid.
   */
  private boolean isValid() {
    final List errors = new ArrayList(1);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_EMAIL, flEmail);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_VERSION_CONTROL_USER, flVCSUser);
    WebuiUtils.validateFieldValidEmail(errors, CAPTION_EMAIL, flEmail);
    return errors.isEmpty();
  }


  /**
   * Returns true if this form is for the new mappping.
   *
   * @return true if this form is for the new mappping.
   */
  private boolean isNew() {
    return mappingID == GlobalVCSUserMap.UNSAVED_ID;
  }


  public int getMappingID() {
    return mappingID;
  }
}
