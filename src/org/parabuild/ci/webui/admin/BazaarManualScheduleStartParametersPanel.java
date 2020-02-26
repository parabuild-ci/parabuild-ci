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

import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.SourceControlSettingVO;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Field;
import viewtier.ui.Layout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Parameters for a Bazaar-based build on manual schedule.
 */
final class BazaarManualScheduleStartParametersPanel extends ManualScheduleStartParametersPanel {

  private static final long serialVersionUID = 4467119862820193870L; // NOPMD

  private static final String CAPTION_BRANCH_LOCATION = "Bazaar branch path:";
  private static final String CAPTION_CHANGE_LIST_NUMBER = "Revision number or label (optional): ";

  private final CommonFieldLabel lbBranchLocation = new CommonFieldLabel(CAPTION_BRANCH_LOCATION); // NOPMD
  private final CommonFieldLabel lbChangeListNumber = new CommonFieldLabel(CAPTION_CHANGE_LIST_NUMBER); // NOPMD

  private final Field flBranchLocation = new CommonField(100, 100); // NOPMD
  private final Field flRevisionNumber = new CommonField(100, 50);
  private final boolean showBranchPathOverride;


  /**
   * Creates message panel with title displayed.
   *
   * @param showBranchPathOverride
   */
  protected BazaarManualScheduleStartParametersPanel(final boolean showBranchPathOverride, final boolean showParameters) {
    super("Bazaar Parameters");
    this.showBranchPathOverride = showBranchPathOverride;

    if (showBranchPathOverride) {
      gridIterator.addPair(lbBranchLocation, new RequiredFieldMarker(flBranchLocation));
      sourceControlSettingsPropertyToInputMap.bindPropertyNameToInput(SourceControlSettingVO.BAZAAR_BRANCH_LOCATION, flBranchLocation);
    }

    if (showParameters) {
      gridIterator.addPair(lbChangeListNumber, flRevisionNumber);
      sourceControlSettingsPropertyToInputMap.bindPropertyNameToInput(SourceControlSettingVO.BAZAAR_REVISION_NUMBER, flRevisionNumber);
    }

    lbBranchLocation.setAlignY(Layout.TOP);
    flBranchLocation.setAlignY(Layout.TOP);
  }


  /**
   * Sets edit mode
   *
   * @param mode
   */
  public final void setEditMode(final int mode) {
    if (mode == WebUIConstants.MODE_VIEW) {
      flBranchLocation.setEditable(false);
      flRevisionNumber.setEditable(false);
    } else if (mode == WebUIConstants.MODE_EDIT) {
      flBranchLocation.setEditable(true);
      flRevisionNumber.setEditable(true);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  public void load(final int buildID) {
    final List parametersToLoad = new ArrayList(3);
    final List sourceControlSettings = ConfigurationManager.getInstance().getSourceControlSettings(buildID);
    for (final Iterator i = sourceControlSettings.iterator(); i.hasNext();) {
      final SourceControlSetting setting = (SourceControlSetting) i.next();
      if (setting.getPropertyName().equals(VersionControlSystem.BAZAAR_BRANCH_LOCATION) && !showBranchPathOverride) {
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

    if (showBranchPathOverride) {
      WebuiUtils.validateFieldNotBlank(errors, CAPTION_BRANCH_LOCATION, flBranchLocation);
    }

    if (!WebuiUtils.isBlank(flRevisionNumber)) {
      final String value = flRevisionNumber.getValue();
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
    return "BazaarManualScheduleStartParametersPanel{" +
            "flBranchLocation=" + flBranchLocation +
            ", flRevisionNumber=" + flRevisionNumber +
            ", lbBranchLocation=" + lbBranchLocation +
            ", lbChangeListNumber=" + lbChangeListNumber +
            ", showBranchPathOverride=" + showBranchPathOverride +
            "} " + super.toString();
  }
}