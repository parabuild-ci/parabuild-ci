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

import org.apache.commons.logging.*;

import org.parabuild.ci.object.*;
import viewtier.ui.*;

// REVIEWME: extend CodeNameDropDown

/**
 * Access dropdown contains a list of build access types
 */
public final class ScheduleTypeDropDown extends DropDown {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(ScheduleTypeDropDown.class); // NOPMD

  private static final long serialVersionUID = -745426151799565293L; // NOPMD

  public static final String STRING_AUTOMATIC = "Automatic";
  public static final String STRING_MANUAL = "Manual";
  public static final String STRING_SCHEDULED = "Scheduled";
  public static final String STRING_PARALLEL = "Parallel";


  public ScheduleTypeDropDown() {
    super.addItem(scheduleCodeToString(BuildConfig.SCHEDULE_TYPE_AUTOMATIC));
    super.addItem(scheduleCodeToString(BuildConfig.SCHEDULE_TYPE_RECURRENT));
    super.addItem(scheduleCodeToString(BuildConfig.SCHEDULE_TYPE_PARALLEL));
    super.addItem(scheduleCodeToString(BuildConfig.SCHEDULE_TYPE_MANUAL));
    this.setScheduleType(BuildConfig.SCHEDULE_TYPE_AUTOMATIC);
  }


  /**
   * Helper method
   *
   * @param scheduleCode
   */
  private static String scheduleCodeToString(final int scheduleCode) {
    if (scheduleCode == BuildConfig.SCHEDULE_TYPE_AUTOMATIC) return STRING_AUTOMATIC;
    if (scheduleCode == BuildConfig.SCHEDULE_TYPE_MANUAL) return STRING_MANUAL;
    if (scheduleCode == BuildConfig.SCHEDULE_TYPE_RECURRENT) return STRING_SCHEDULED;
    if (scheduleCode == BuildConfig.SCHEDULE_TYPE_PARALLEL) return STRING_PARALLEL;
    return STRING_AUTOMATIC;
  }


  /**
   * Returns selected build access type
   *
   * @return int access type
   */
  public int getScheduleType() {
    final String value = getValue();
    if (log.isDebugEnabled()) log.debug("value: " + value);
    if (value.equals(STRING_AUTOMATIC)) return BuildConfig.SCHEDULE_TYPE_AUTOMATIC;
    if (value.equals(STRING_MANUAL)) return BuildConfig.SCHEDULE_TYPE_MANUAL;
    if (value.equals(STRING_SCHEDULED)) return BuildConfig.SCHEDULE_TYPE_RECURRENT;
    if (value.equals(STRING_PARALLEL)) return BuildConfig.SCHEDULE_TYPE_PARALLEL;
    return BuildConfig.SCHEDULE_TYPE_AUTOMATIC;
  }


  /**
   * Sets selected access type
   *
   * @param scheduleType type to select
   */
  public final void setScheduleType(final int scheduleType) {
    super.setSelection(scheduleCodeToString(scheduleType));
  }
}
