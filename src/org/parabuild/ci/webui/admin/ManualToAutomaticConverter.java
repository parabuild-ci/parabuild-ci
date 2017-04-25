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

import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.ScheduleProperty;

/**
 * Converts a build with a manual schedule to automatic schedule.
 */
final class ManualToAutomaticConverter extends BuildScheduleConverter {

  public ManualToAutomaticConverter() {
    super(ActiveBuildConfig.SCHEDULE_TYPE_MANUAL, ActiveBuildConfig.SCHEDULE_TYPE_AUTOMATIC);
  }


  protected void doConvert(final int activeBuildID) {
    // Set a poll interval if not set.
    createScheduleProperty(activeBuildID, ScheduleProperty.AUTO_POLL_INTERVAL, Integer.toString(ScheduleProperty.DEFAULT_POLL_INTERVAL));
    createScheduleProperty(activeBuildID, ScheduleProperty.AUTO_COOLDOWN_INTERVAL, Integer.toString(ScheduleProperty.DEFAULT_COOLDOWN_INTERVAL));
  }
}
