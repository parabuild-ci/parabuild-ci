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

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.ScheduleProperty;

abstract class BuildScheduleConverter {

  private final byte sourceType;
  private final byte destinationType;


  BuildScheduleConverter(final byte sourceType, final byte destinationType) {
    this.sourceType = sourceType;
    this.destinationType = destinationType;
  }


  /**
   * Converts a given manual build to automatic build.
   *
   * @param activeBuildID buildID.
   * @noinspection ReuseOfLocalVariable
   */
  public void convert(final int activeBuildID) {

    // Prepare
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    ActiveBuildConfig activeBuildConfig = cm.getActiveBuildConfig(activeBuildID);
    if (activeBuildConfig.getScheduleType() != sourceType) {
      throw new IllegalArgumentException("Invalid source schedule type: " + activeBuildConfig.getScheduleTypeAsString());
    }

    doConvert(activeBuildID);


    // Save configuration
    activeBuildConfig = cm.getActiveBuildConfig(activeBuildID);
    activeBuildConfig.setScheduleType(destinationType);
    cm.save(activeBuildConfig);
  }


  protected abstract void doConvert(final int activeBuildID);


  /**
   * Service method.
   *
   * @param activeBuildID
   * @param propertyName
   * @param propertyValue
   */
  protected static void createScheduleProperty(final int activeBuildID, final String propertyName, final String propertyValue) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final ScheduleProperty setting = cm.getScheduleSetting(activeBuildID, propertyName);
    if (setting == null) {
      final ScheduleProperty newSetting = new ScheduleProperty();
      newSetting.setBuildID(activeBuildID);
      newSetting.setPropertyName(propertyName);
      newSetting.setPropertyValue(propertyValue);
      cm.saveObject(newSetting);
    }
  }


  public String toString() {
    return "BuildScheduleConverter{" +
            "destinationType=" + destinationType +
            ", sourceType=" + sourceType +
            '}';
  }
}
