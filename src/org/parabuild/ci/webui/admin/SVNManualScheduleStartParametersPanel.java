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

import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.SourceControlSettingVO;
import org.parabuild.ci.versioncontrol.SVNDepotPathParser;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Field;
import viewtier.ui.Layout;
import viewtier.ui.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Parameters for a Subversion-based build on manual schedule.
 * @noinspection FieldCanBeLocal
 */
final class SVNManualScheduleStartParametersPanel extends ManualScheduleStartParametersPanel {

  private static final long serialVersionUID = 4467119862820193870L; // NOPMD

  private static final String CAPTION_DEPOT_PATH = "Subversion repository path:";
  private static final String CAPTION_CHANGE_LIST_NUMBER = "Change list number:";

  private final CommonFieldLabel lbDepotPath = new CommonFieldLabel(CAPTION_DEPOT_PATH); // NOPMD
  private final CommonFieldLabel lbChangeListNumber = new CommonFieldLabel(CAPTION_CHANGE_LIST_NUMBER); // NOPMD

  private final Text flDepotPath = new Text(100, 5);
  private final Field flChangeListNumber = new CommonField(7, 7);


  /**
   * Creates message panel with title displayed.
   */
  protected SVNManualScheduleStartParametersPanel() {
    super("Subversion parameters");

    // layout

    gridIterator.addPair(lbDepotPath, new RequiredFieldMarker(flDepotPath));
    gridIterator.addPair(lbChangeListNumber, flChangeListNumber);

    // init property to input map
    sourceControlSettingsPropertyToInputMap.bindPropertyNameToInput(SourceControlSettingVO.SVN_DEPOT_PATH, flDepotPath);
    sourceControlSettingsPropertyToInputMap.bindPropertyNameToInput(SourceControlSettingVO.SVN_CHANGE_LIST_NUMBER, flChangeListNumber);

    lbDepotPath.setAlignY(Layout.TOP);
    flDepotPath.setAlignY(Layout.TOP);
  }


  /**
   * Sets edit mode
   *
   * @param mode
   */
  public final void setEditMode(final int mode) {
    if (mode == WebUIConstants.MODE_VIEW) {
      flDepotPath.setEditable(false);
      flChangeListNumber.setEditable(false);
    } else if (mode == WebUIConstants.MODE_EDIT) {
      flDepotPath.setEditable(true);
      flChangeListNumber.setEditable(true);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  public void load(final int buildID) {
    final List parametersToLoad = new ArrayList(3);
    final List scmSetting = ConfigurationManager.getInstance().getSourceControlSettings(buildID);
    for (final Iterator i = scmSetting.iterator(); i.hasNext();) {
      final SourceControlSetting setting = (SourceControlSetting)i.next();
      if (SourceControlSettingVO.scmSettingIsSupported(setting.getPropertyName())) {
        parametersToLoad.add(new SourceControlSettingVO(setting.getPropertyName(), setting.getPropertyValue()));
      }
    }
    super.sourceControlSettingsPropertyToInputMap.setProperties(parametersToLoad);
  }

  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should dispaly a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    clearMessage();
    final ArrayList errors = new ArrayList(1);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_DEPOT_PATH, flDepotPath);

    if (errors.isEmpty()) {
      // further validate SVN depot path
      final SVNDepotPathParser parser = new SVNDepotPathParser();
      try {
        parser.validate(flDepotPath.getValue());
      } catch (final ValidationException e) {
        errors.add(StringUtils.toString(e));
      }
    }

    if (!WebuiUtils.isBlank(flChangeListNumber)) {
      WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_CHANGE_LIST_NUMBER, flChangeListNumber);
    }

    // show errors
    if (errors.isEmpty()) {
      return true;
    }
    showErrorMessage(errors);
    return false;
  }
}
