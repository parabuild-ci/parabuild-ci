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
package org.parabuild.ci.webui.admin;

import java.util.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 */
public abstract class AbstractCommandVCSSettingsPanel extends AbstractSourceControlPanel {

  private static final Log log = LogFactory.getLog(AbstractCommandVCSSettingsPanel.class);

  private static final String CAPTION_LABEL_TAG_A_BUILD = "Command to label/tag a build: ";
  private static final String CAPTION_REMOVE_TABEL_TAG = "Command to remove tabel/tag: ";
  private static final String CAPTION_SYNC_TO_CHANGE_LIST = "Command to sync to change list: ";
  private static final long serialVersionUID = -1342054234743631119L;

  private final Field flLabelCommand = new CommonField(200, 80);
  private final Field flRemoveLabelCommand = new CommonField(200, 80);
  private final Field flSyncToChangeListCommand = new CommonField(200, 80);


  public AbstractCommandVCSSettingsPanel(final String title) {
    super(title);
    //noinspection OverriddenMethodCallInConstructor,AbstractMethodCallInConstructor,OverridableMethodCallInConstructor
    createHeaderFields();
    // layout
    //noinspection OverriddenMethodCallInConstructor,AbstractMethodCallInConstructor,OverridableMethodCallInConstructor
    addHeaderFields(gridIterator); // NOPMD
    gridIterator.addPair(new CommonFieldLabel(CAPTION_SYNC_TO_CHANGE_LIST), flSyncToChangeListCommand);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_LABEL_TAG_A_BUILD), flLabelCommand);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_REMOVE_TABEL_TAG), flRemoveLabelCommand);

    // init property to input map
    //noinspection OverriddenMethodCallInConstructor,AbstractMethodCallInConstructor,OverridableMethodCallInConstructor
    bindHeaderFields(); // NOPMD
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.COMMAND_VCS_LABEL_COMMAND, flLabelCommand);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.COMMAND_VCS_REMOVE_LABEL_COMMAND, flRemoveLabelCommand);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.COMMAND_VCS_SYNC_TO_CHANGE_LIST_COMMAND, flSyncToChangeListCommand);
    addCommonAttributes();
  }


  protected abstract void createHeaderFields();


  protected abstract void addHeaderFields(GridIterator gridIterator);


  protected abstract void bindHeaderFields();


  protected abstract void setHeaderMode(final int mode);


  protected abstract void validateHeader(final ArrayList errors);


  /**
   * Sets edit mode
   *
   * @param mode
   */
  protected final void doSetMode(final int mode) {
    if (log.isDebugEnabled()) log.debug("holder mode: " + mode);
    setHeaderMode(mode);
    if (mode == WebUIConstants.MODE_VIEW) {
      setEditable(false);
    } else if (mode == WebUIConstants.MODE_EDIT) {
      setEditable(true);
    } else if (mode == WebUIConstants.MODE_INHERITED) {
      setEditable(true);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  private void setEditable(final boolean editable) {
    flLabelCommand.setEditable(editable);
    flRemoveLabelCommand.setEditable(editable);
    flSyncToChangeListCommand.setEditable(editable);
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should dispaly a error message in it's area.
   *
   * @return true if valid
   */
  protected final boolean doValidate() {
    clearMessage();
    final ArrayList errors = new ArrayList();
    validateHeader(errors);

    // TODO: add validating path(s)

    // show errors
    if (errors.isEmpty()) return true;
    showErrorMessage(errors);
    return false;
  }
}
