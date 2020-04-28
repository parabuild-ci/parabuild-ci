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
package org.parabuild.ci.versioncontrol;

import org.parabuild.ci.object.SourceControlSetting;

import java.util.Map;

/**
 * SourceControlSettingChangeDetector is repsonsible for identifying
 * a fact of version control setting change.
 */
public final class SourceControlSettingChangeDetector {

  private Map currentSettings;
  private Map newSettings;


  /**
   * Constructor
   *
   * @param currentSettings
   * @param newSettings
   */
  public SourceControlSettingChangeDetector(final Map currentSettings, final Map newSettings) {
    this.newSettings = newSettings;
    this.currentSettings = currentSettings;
  }


  /**
   * Returns true if a setting has changed
   */
  public boolean settingHasChanged(final String settingName) {
    final SourceControlSetting newSetting = (SourceControlSetting) newSettings.get(settingName);
    final SourceControlSetting currentSetting = (SourceControlSetting) currentSettings.get(settingName);
    if (newSetting == null && currentSetting != null) return true;
    if (newSetting == null && currentSetting == null) return false;
    if (newSetting != null && currentSetting == null) return true;
    return newSetting != null && currentSetting != null
            && newSetting.getPropertyTimeStamp() != currentSetting.getPropertyTimeStamp()
            && !newSetting.getPropertyValue().equals(currentSetting.getPropertyValue());
  }
}
