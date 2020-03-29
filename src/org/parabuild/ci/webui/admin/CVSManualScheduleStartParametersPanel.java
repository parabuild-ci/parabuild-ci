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

import org.parabuild.ci.common.WebUIConstants;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.SourceControlSettingVO;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.WebuiUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Parameters for a Subversion-based build on manual schedule.
 */
final class CVSManualScheduleStartParametersPanel extends ManualScheduleStartParametersPanel {

  private static final long serialVersionUID = 4467119862820193870L; // NOPMD

  public static final String NAME_BRANCH = "Branch name:";

  private final CommonField flBranch = new CommonField(60, 60);


  /**
   * Creates message panel with title displayed.
   */
  protected CVSManualScheduleStartParametersPanel() {
    super("CVS Parameters");

    // layout
    gridIterator.addPair(new CommonFieldLabel(NAME_BRANCH), flBranch);

    // init property to input map
    sourceControlSettingsPropertyToInputMap.bindPropertyNameToInput(SourceControlSettingVO.CVS_BRANCH_NAME, flBranch);
  }


  /**
   * Sets edit mode
   *
   * @param mode
   */
  public final void setEditMode(final int mode) {
    if (mode == WebUIConstants.MODE_VIEW) {
      flBranch.setEditable(false);
    } else if (mode == WebUIConstants.MODE_EDIT) {
      flBranch.setEditable(true);
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
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    clearMessage();
    final ArrayList errors = new ArrayList(11);

    // branch name is valid
    if (!StringUtils.isBlank(flBranch.getValue())) {
      WebuiUtils.validateFieldStrict(errors, NAME_BRANCH, flBranch);
    }

    // show errors
    if (errors.isEmpty()) return true;
    showErrorMessage(errors);
    return false;
  }
}
