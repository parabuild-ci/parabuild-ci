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
package org.parabuild.ci.webui.admin.mercurial;

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.SourceControlSettingVO;
import org.parabuild.ci.webui.admin.ManualScheduleStartParametersPanel;
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
 * Parameters for a Mercurial-based build on manual schedule.
 */
public final class MercurialManualScheduleStartParametersPanel extends ManualScheduleStartParametersPanel {

  private static final long serialVersionUID = 4467119862820193870L; // NOPMD

  private static final String CAPTION_URL = "Mercurial URL: ";
  private static final String CAPTION_BRANCH = "Mercurial branch (optional): ";
  private static final String CAPTION_REVISION_NUMBER = "Revision number (optional): ";

  private final CommonFieldLabel lbURL = new CommonFieldLabel(CAPTION_URL); // NOPMD
  private final CommonFieldLabel lbBranch = new CommonFieldLabel(CAPTION_BRANCH); // NOPMD
  private final CommonFieldLabel lbRevisionNumber = new CommonFieldLabel(CAPTION_REVISION_NUMBER); // NOPMD

  private final Field flURL = new CommonField(200, 60); // NOPMD
  private final Field flBranch = new CommonField(60, 60); // NOPMD
  private final Field flRevisionNumber = new CommonField(100, 50);
  private final boolean showURLOverride;


  /**
   * Creates message panel with title displayed.
   *
   * @param showURLOverride flag if branch path override should be displayed.
   * @param showParameters  true if parameter should be displayed.
   */
  public MercurialManualScheduleStartParametersPanel(final boolean showURLOverride, final boolean showParameters) {
    super("Mercurial Parameters");
    this.showURLOverride = showURLOverride;

    if (showURLOverride) {
      gridIterator.addPair(lbURL, new RequiredFieldMarker(flURL));
      gridIterator.addPair(lbBranch, flBranch);
      sourceControlSettingsPropertyToInputMap.bindPropertyNameToInput(SourceControlSettingVO.MERCURIAL_URL_PATH, flURL);
      sourceControlSettingsPropertyToInputMap.bindPropertyNameToInput(SourceControlSettingVO.MERCURIAL_BRANCH, flBranch);
    }

    if (showParameters) {
      gridIterator.addPair(lbRevisionNumber, flRevisionNumber);
      sourceControlSettingsPropertyToInputMap.bindPropertyNameToInput(SourceControlSettingVO.BAZAAR_REVISION_NUMBER, flRevisionNumber);
    }

    lbBranch.setAlignY(Layout.TOP);
    flBranch.setAlignY(Layout.TOP);
  }


  /**
   * Sets edit mode.
   *
   * @param mode edit mode.
   */
  public final void setEditMode(final int mode) {
    if (mode == WebUIConstants.MODE_VIEW) {
      flBranch.setEditable(false);
      flRevisionNumber.setEditable(false);
      flURL.setEditable(false);
    } else if (mode == WebUIConstants.MODE_EDIT) {
      flBranch.setEditable(true);
      flRevisionNumber.setEditable(true);
      flURL.setEditable(true);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  public void load(final int buildID) {
    final List parametersToLoad = new ArrayList(3);
    final List sourceControlSettings = ConfigurationManager.getInstance().getSourceControlSettings(buildID);
    for (final Iterator i = sourceControlSettings.iterator(); i.hasNext();) {
      final SourceControlSetting setting = (SourceControlSetting) i.next();
      if ((setting.getPropertyName().equals(SourceControlSetting.MERCURIAL_BRANCH) || setting.getPropertyName().equals(SourceControlSetting.MERCURIAL_URL)) && !showURLOverride) {
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
   * a component should dispaly a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    clearMessage();
    final List errors = new ArrayList(1);

    if (showURLOverride) {
      WebuiUtils.validateFieldNotBlank(errors, CAPTION_URL, flURL);
    }

    // show errors
    if (errors.isEmpty()) {
      return true;
    }
    showErrorMessage(errors);
    return false;
  }


  public String toString() {
    return "MercurialManualScheduleStartParametersPanel{" +
            "lbURL=" + lbURL +
            ", lbBranch=" + lbBranch +
            ", lbRevisionNumber=" + lbRevisionNumber +
            ", flURL=" + flURL +
            ", flBranch=" + flBranch +
            ", flRevisionNumber=" + flRevisionNumber +
            ", showURLOverride=" + showURLOverride +
            "} " + super.toString();
  }
}