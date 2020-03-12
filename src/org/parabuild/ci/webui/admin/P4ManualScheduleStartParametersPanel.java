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

import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.common.WebUIConstants;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.SourceControlSettingVO;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.versioncontrol.perforce.P4ClientViewParser;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonText;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Parameters for a Perforce-based build on manual schedule.
 */
final class P4ManualScheduleStartParametersPanel extends ManualScheduleStartParametersPanel {

  private static final long serialVersionUID = 4467119862820193870L; // NOPMD

  private static final String CAPTION_DEPOT_PATH = "P4 client view:";
  private static final String CAPTION_CHANGE_LIST_NUMBER = "Change list number or label (optional): ";

  private final CommonFieldLabel lbDepotPath = new CommonFieldLabel(CAPTION_DEPOT_PATH); // NOPMD
  private final CommonFieldLabel lbChangeListNumber = new CommonFieldLabel(CAPTION_CHANGE_LIST_NUMBER); // NOPMD

  private final CommonText flClientView = new CommonText(100, 5); // NOPMD
  private final CommonField flChangeListNumber = new CommonField(100, 50);
  private final boolean showDepotPathOverride;


  /**
   * Creates message panel with title displayed.
   *
   * @param showDepotPathOverride
   */
  protected P4ManualScheduleStartParametersPanel(final boolean showDepotPathOverride, final boolean showParameters) {
    super("Perforce Parameters");
    this.showDepotPathOverride = showDepotPathOverride;

    if (showDepotPathOverride) {
      gridIterator.addPair(lbDepotPath, new RequiredFieldMarker(flClientView));
      sourceControlSettingsPropertyToInputMap.bindPropertyNameToInput(SourceControlSettingVO.P4_DEPOT_PATH, flClientView);
    }

    if (showParameters) {
      gridIterator.addPair(lbChangeListNumber, flChangeListNumber);
      sourceControlSettingsPropertyToInputMap.bindPropertyNameToInput(SourceControlSettingVO.P4_CHANGE_LIST_NUMBER, flChangeListNumber);
    }

    lbDepotPath.setAlignY(Layout.TOP);
    flClientView.setAlignY(Layout.TOP);
  }


  /**
   * Sets edit mode
   *
   * @param mode
   */
  public final void setEditMode(final int mode) {
    if (mode == WebUIConstants.MODE_VIEW) {
      flClientView.setEditable(false);
      flChangeListNumber.setEditable(false);
    } else if (mode == WebUIConstants.MODE_EDIT) {
      flClientView.setEditable(true);
      flChangeListNumber.setEditable(true);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  public void load(final int buildID) {
    final List parametersToLoad = new ArrayList(3);
    final List sourceControlSettings = ConfigurationManager.getInstance().getSourceControlSettings(buildID);
    for (final Iterator i = sourceControlSettings.iterator(); i.hasNext();) {
      final SourceControlSetting setting = (SourceControlSetting) i.next();
      if (setting.getPropertyName().equals(VersionControlSystem.P4_DEPOT_PATH) && !showDepotPathOverride) {
        continue;
      }
      if (SourceControlSettingVO.scmSettingIsSupported(setting.getPropertyName())) {
        parametersToLoad.add(new SourceControlSettingVO(setting.getPropertyName(), setting.getPropertyValue()));
      }
    }
    super.sourceControlSettingsPropertyToInputMap.setProperties(parametersToLoad);
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    clearMessage();
    final List errors = new ArrayList(1);

    if (showDepotPathOverride) {
      WebuiUtils.validateFieldNotBlank(errors, CAPTION_DEPOT_PATH, flClientView);

      if (errors.isEmpty()) {

        // further validate P4 depot path
        final P4ClientViewParser parser = new P4ClientViewParser();
        try {
          parser.parse(null, flClientView.getValue());
        } catch (final ValidationException e) {
          errors.add(StringUtils.toString(e));
        }
      }
    }

    if (!WebuiUtils.isBlank(flChangeListNumber)) {
      final String value = flChangeListNumber.getValue();
      if (!StringUtils.isValidInteger(value) && !StringUtils.isValidStrictName(value)) {
        errors.add("Field \"" + CAPTION_CHANGE_LIST_NUMBER + "\" may contain only valid positive integers or alphanumeric characters, \"-\" and \"_\".");
      }
    }

    // show errors
    if (errors.isEmpty()) {
      return true;
    }
    showErrorMessage(errors);
    return false;
  }


  public String toString() {
    return "P4ManualScheduleStartParametersPanel{" +
            "flChangeListNumber=" + flChangeListNumber +
            ", flClientView=" + flClientView +
            ", lbChangeListNumber=" + lbChangeListNumber +
            ", lbDepotPath=" + lbDepotPath +
            ", showDepotPathOverride=" + showDepotPathOverride +
            "} " + super.toString();
  }
}
