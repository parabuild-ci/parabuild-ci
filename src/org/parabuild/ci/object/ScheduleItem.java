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
package org.parabuild.ci.object;

import org.parabuild.ci.util.CommonConstants;
import org.parabuild.ci.util.StringUtils;
import org.quartz.CronTrigger;

import java.io.Serializable;
import java.text.ParseException;

/**
 * Version control system user to e-mail mapping
 *
 * @hibernate.class table="SCHEDULE_ITEM" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class ScheduleItem implements Serializable, CommonConstants, ObjectConstants {

  private static final long serialVersionUID = 1749905455750567815L; // NOPMD

  public static final int CRON_MONTH_DAYS = 1;
  public static final int CRON_WEEK_DAYS = 2;

  private int buildID = BuildConfig.UNSAVED_ID;
  private int scheduleItemID = UNSAVED_ID;
  private long timeStamp = 1;
  private String hour;
  private String dayOfWeek;
  private String dayOfMonth;
  private boolean cleanCheckout;
  private boolean runIfNoChanges = true;


  /**
   * Returns build ID
   *
   * @return String
   *
   * @hibernate.property column="BUILD_ID" unique="false" null="false"
   */
  public int getBuildID() {
    return buildID;
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Sequence ID
   *
   * @return int
   *
   * @hibernate.id generator-class="identity" column="ID" unsaved-value="-1"
   */
  public int getScheduleItemID() {
    return scheduleItemID;
  }


  public void setScheduleItemID(final int scheduleItemID) {
    this.scheduleItemID = scheduleItemID;
  }


  /**
   * Returns sequence name
   *
   * @return String
   *
   * @hibernate.property column="MONTH_DAY" unique="true" null="false"
   */
  public String getDayOfMonth() {
    return dayOfMonth;
  }


  public void setDayOfMonth(final String dayOfMonth) {
    this.dayOfMonth = dayOfMonth;
  }


  /**
   * Returns text of sequence script
   *
   * @return String
   *
   * @hibernate.property column="WEEK_DAY" unique="false" null="false"
   */
  public String getDayOfWeek() {
    return dayOfWeek;
  }


  public void setDayOfWeek(final String dayOfWeek) {
    this.dayOfWeek = dayOfWeek;
  }


  /**
   * Returns sequence's failure pattern
   *
   * @return String
   *
   * @hibernate.property column="HOUR" unique="false" null="false"
   */
  public String getHour() {
    return hour;
  }


  public void setHour(final String hour) {
    this.hour = hour;
  }


  /**
   * Returns true if this build should run clean at this time.
   *
   * @return true if this build was a re-run.
   * @hibernate.property column="CLEAN_CHECKOUT"  type="yes_no" unique="false" null="false"
   */
  public boolean isCleanCheckout() {
    return cleanCheckout;
  }


  public void setCleanCheckout(final boolean cleanCheckout) {
    this.cleanCheckout = cleanCheckout;
  }


  /**
   * Returns true if this build should run if there are no new changes.
   *
   * @return true if this build should run if there are no new changes.
   * @hibernate.property column="RUN_IF_NO_CHANGES"  type="yes_no" unique="false" null="false"
   */
  public boolean isRunIfNoChanges() {
    return runIfNoChanges;
  }


  public void setRunIfNoChanges(final boolean runIfNoChanges) {
    this.runIfNoChanges = runIfNoChanges;
  }


  /**
   * Returns timestamp
   *
   * @return long
   *
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * Helper method to validate given hours, weekDays, and month days
   *
   * @return true if valid, otherwise returns false
   */
  public static boolean validate(final String hours, final String weekDays, final String monthDays) {
    final ScheduleItem item = new ScheduleItem();
    item.dayOfWeek = weekDays;
    item.dayOfMonth = monthDays;
    item.hour = hours;
    return validate(item);
  }


  /**
   * Helper method to validate schedule item
   *
   * @return true if valid, otherwise returns false
   */
  public static boolean validate(final ScheduleItem item) {
    final CronTrigger cronTrigger = new CronTrigger();
    try {
      final String[] ce = toString(item);
      for (int i = 0; i < ce.length; i++) {
        cronTrigger.setCronExpression(ce[i]);
      }
      return true;
    } catch (final ParseException e) {
      return false;
    }
  }


  /**
   * Returns a cron string for the schedule item
   */
  public static String[] toString(final ScheduleItem item) {
    String monthDays = item.dayOfMonth;
    final String hours = item.hour;
    String weekDays = item.dayOfWeek;
    if (StringUtils.isBlank(monthDays)) {
      monthDays = "?";
    }
    if (StringUtils.isBlank(weekDays)) {
      weekDays = "?";
    }

    if ("?".equals(monthDays) || "?".equals(weekDays)) {
      return new String[]{"0 0 " + hours + STR_SPACE + monthDays + STR_SPACE + '*' + STR_SPACE + weekDays};
    } else {
      return new String[]{
        "0 0 " + hours + STR_SPACE + '?' + STR_SPACE + '*' + STR_SPACE + weekDays,
        "0 0 " + hours + STR_SPACE + monthDays + STR_SPACE + '*' + STR_SPACE + '?'
      };
    }
  }
}
