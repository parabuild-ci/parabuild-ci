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

import org.parabuild.ci.configuration.UnexpectedErrorException;
import org.parabuild.ci.object.BuildConfig;

/**
 *
 */
public final class ScheduleSettingsPanelFactory {

  private ScheduleSettingsPanelFactory() {
  }


  public static ScheduleSettingsPanel getPanel(final BuildConfig buildConfig) {
    if (buildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_AUTOMATIC) {
      return new AutomaticScheduleSettingsPanel();
    } else if (buildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_RECURRENT) {
      return new RecurrentScheduleSettingsPanel();
    } else if (buildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_PARALLEL) {
      final ParallelScheduleSettingsPanel pnlParallelScheduleSettings = new ParallelScheduleSettingsPanel();
      pnlParallelScheduleSettings.setVisible(false);
      return pnlParallelScheduleSettings;
    } else if (buildConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_MANUAL) {
      return new ManualScheduleSettingsPanel();
    } else {
      throw new UnexpectedErrorException("Unknown schedule type: " + buildConfig.getScheduleType());
    }
  }
}
