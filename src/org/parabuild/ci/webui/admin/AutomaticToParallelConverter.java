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
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.SourceControlSetting;

import java.util.List;

/**
 * Converts a build with a manual schedule to automatic schedule.
 */
final class AutomaticToParallelConverter extends BuildScheduleConverter {

  private final int parentBuildID;


  AutomaticToParallelConverter(final int parentBuildID) {
    super(ActiveBuildConfig.SCHEDULE_TYPE_AUTOMATIC, ActiveBuildConfig.SCHEDULE_TYPE_PARALLEL);
    this.parentBuildID = parentBuildID;
  }


  protected void doConvert(final int activeBuildID) {

    // Change source control to reference
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final ActiveBuildConfig activeBuildConfig = cm.getActiveBuildConfig(activeBuildID);
    activeBuildConfig.setSourceControl(VersionControlSystem.SCM_REFERENCE);
    cm.saveObject(activeBuildConfig);

    // Remove all source control settings
    final List scsList = cm.getSourceControlSettings(activeBuildID);
    for (int i = 0; i < scsList.size(); i++) {
      cm.deleteObject(scsList.get(i));
    }

    // Remove all schedule settings
    final List schList = cm.getScheduleSettings(activeBuildID);
    for (int i = 0; i < schList.size(); i++) {
      cm.deleteObject(schList.get(i));
    }

    // Set reference build id
    SourceControlSetting setting = cm.getSourceControlSetting(activeBuildID, VersionControlSystem.REFERENCE_BUILD_ID);
    if (setting == null) {
      setting = new SourceControlSetting();
      setting.setBuildID(activeBuildID);
      setting.setPropertyName(VersionControlSystem.REFERENCE_BUILD_ID);
    }
    setting.setPropertyValue(parentBuildID);
    cm.saveObject(setting);
  }


  public String toString() {
    return "AutomaticToParallelConverter{" +
            "parentBuildID=" + parentBuildID +
            "} " + super.toString();
  }
}