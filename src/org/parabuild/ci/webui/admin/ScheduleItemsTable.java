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

import java.util.*;

import org.parabuild.ci.configuration.*;
import org.parabuild.ci.object.*;
import org.parabuild.ci.webui.common.*;
import viewtier.ui.*;

/**
 * This table holds a list of schedule items
 */
public final class ScheduleItemsTable extends AbstractFlatTable implements Saveable, Validatable {

  private static final long serialVersionUID = -6185282734365804275L; // NOPMD

  private static final int COL_HOUR = 0;
  private static final int COL_WEEK_DAY = 1;
  private static final int COL_MONTH_DAY = 2;
  private static final int COL_CLEAN_CHECKOUT = 3;
  private static final int COL_RUN_IF_NO_CHANGES = 4;

  private static final String NAME_HOUR = "Hour";
  private static final String NAME_MONTH_DAY = "Days of month";
  private static final String NAME_WEEK_DAY = "Days of week";
  private static final String NAME_CLEAN_CHECKOUT = "Clean checkout";
  private static final String NAME_RUN_IF_NO_CHANGES = "Run if no changes";

  private static final int COLUMN_COUNT = 5;

  private List scheduleItems = new ArrayList(1);
  private final List deleted = new ArrayList(1);
  private int buildID = BuildConfig.UNSAVED_ID;
  private final boolean requireAtLeastOneScheduleItem;


  /**
   * Constructor
   */
  public ScheduleItemsTable() {
    this(true);
  }


  public ScheduleItemsTable(final boolean requireAtLeastOneScheduleItem) {
    super(COLUMN_COUNT, true);
    this.requireAtLeastOneScheduleItem = requireAtLeastOneScheduleItem;
  }


  /**
   * Sets build ID
   *
   * @param buildID
   */
  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  /**
   * Sets schedule items
   *
   * @param scheduleItems
   */
  public void setScheduleItems(final List scheduleItems) {
    this.scheduleItems = scheduleItems;
    populate();
  }


  /**
   * This implementation of this abstract method is called when
   * the table wants to fetch a row with a given rowIndex.
   * Implementing method should fill the data corresponding the
   * given rowIndex.
   *
   * @return this method should return either TBL_ROW_FETCHED or
   *         TBL_NO_MORE_ROWS if the requested row is out of
   *         range.
   *
   * @see AbstractFlatTable#TBL_ROW_FETCHED
   * @see AbstractFlatTable#TBL_NO_MORE_ROWS
   */
  protected int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= scheduleItems.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final ScheduleItem scheduleItem = (ScheduleItem)scheduleItems.get(rowIndex);
    final Component[] row = getRow(rowIndex);
    ((AbstractInput) row[0]).setValue(scheduleItem.getHour());
    ((AbstractInput) row[COL_WEEK_DAY]).setValue(scheduleItem.getDayOfWeek());
    ((AbstractInput) row[COL_MONTH_DAY]).setValue(scheduleItem.getDayOfMonth());
    ((CheckBox)row[COL_CLEAN_CHECKOUT]).setChecked(scheduleItem.isCleanCheckout());
    ((CheckBox)row[COL_RUN_IF_NO_CHANGES]).setChecked(scheduleItem.isRunIfNoChanges());
    return TBL_ROW_FETCHED;
  }


  /**
   */
  protected Component[] makeHeader() {
    final Component[] header = new Component[COLUMN_COUNT];
    header[COL_HOUR] = new TableHeaderLabel(NAME_HOUR, 80);
    header[COL_WEEK_DAY] = new TableHeaderLabel(NAME_WEEK_DAY, 100);
    header[COL_MONTH_DAY] = new TableHeaderLabel(NAME_MONTH_DAY, 100);
    header[COL_CLEAN_CHECKOUT] = new TableHeaderLabel(NAME_CLEAN_CHECKOUT, 100);
    header[COL_RUN_IF_NO_CHANGES] = new TableHeaderLabel(NAME_RUN_IF_NO_CHANGES, 360);
    return header;
  }


  /**
   * Makes row, should be implemented by successor class
   *
   */
  protected Component[] makeRow(final int rowIndex) {
    final Component[] row = new Component[COLUMN_COUNT];
    row[COL_HOUR] = new CommonField(2, 5);
    row[COL_WEEK_DAY] = new CommonField(10, 11);
    row[COL_MONTH_DAY] = new CommonField(10, 11);
    row[COL_CLEAN_CHECKOUT] = new CheckBox();
    final CheckBox cbRunIfNoChanges = new CheckBox();
    cbRunIfNoChanges.setChecked(true); // default
    row[COL_RUN_IF_NO_CHANGES] = cbRunIfNoChanges;
    return row;
  }


  /**
   * This notification method is called when a row is deleted.
   */
  public void notifyRowDeleted(final int index) {
    final ScheduleItem item = (ScheduleItem)getScheduleItems().remove(index);
    if (item.getBuildID() != BuildConfig.UNSAVED_ID) {
      deleted.add(item);
    }
  }


  /**
   * This notification method is called when a new row is added.
   * Implementing class can use it to keep track of deleted rows
   */
  public void notifyRowAdded(final int addedRowIndex) {
    final ScheduleItem item = new ScheduleItem();
    item.setBuildID(buildID);
    getScheduleItems().add(addedRowIndex, item);
  }


  private List getScheduleItems() {
    if (scheduleItems == null) {
      scheduleItems = new ArrayList(1);
    }
    return scheduleItems;
  }


  /**
   * When called, component should save it's content. This method
   * should return <code>true</code> when content of a component
   * is saved successfully. If not, a component should dispaly a
   * error message in it's area and return <code>false</code>
   *
   * @return true if saved successfuly
   */
  public boolean save() {
    // validate
    if (buildID == BuildConfig.UNSAVED_ID) {
      throw new IllegalArgumentException("Build ID can not be uninitialized");
    }

    // get CM
    final ConfigurationManager configManager = ConfigurationManager.getInstance();

    // delete deleted
    configManager.deleteScheduleItems(deleted);

    // save modified
    for (int i = 0, n = getRowCount(); i < n; i++) {
      final Component[] row = getRow(i);
      final ScheduleItem scheduleItem = (ScheduleItem)scheduleItems.get(i);
      scheduleItem.setHour(((AbstractInput) row[COL_HOUR]).getValue());
      scheduleItem.setDayOfWeek(((AbstractInput) row[COL_WEEK_DAY]).getValue());
      scheduleItem.setDayOfMonth(((AbstractInput) row[COL_MONTH_DAY]).getValue());
      scheduleItem.setCleanCheckout(((CheckBox)row[COL_CLEAN_CHECKOUT]).isChecked());
      scheduleItem.setRunIfNoChanges(((CheckBox)row[COL_RUN_IF_NO_CHANGES]).isChecked());
      if (scheduleItem.getBuildID() == BuildConfig.UNSAVED_ID) {
        scheduleItem.setBuildID(buildID);
      }
    }
    configManager.saveScheduleItems(scheduleItems);

    return true;
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should dispaly a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    final List errors = new ArrayList(1);
    clearMessage();

    // validate number of rows
    if (requireAtLeastOneScheduleItem && getRowCount() <= 0) {
      errors.add("Build should have at least one schedule item. Add a schedule item.");
    }

    // validate for blank values
    for (int i = 0, n = getRowCount(); i < n; i++) {
      final Component[] row = getRow(i);
      final Field flHour = (Field)row[COL_HOUR];
      final Field flWeekDays = (Field)row[COL_WEEK_DAY];
      final Field flMonthDays = (Field)row[COL_MONTH_DAY];
      WebuiUtils.validateColumnNotBlank(errors, i, NAME_HOUR, flHour);
      if (WebuiUtils.isBlank(flWeekDays) && WebuiUtils.isBlank(flMonthDays)) {
        errors.add("Schedule at row \"" + (i + 1) + "\" is not valid. It should have at least week or month days set.");
      }
    }

    // validate for correctness of values
    if (errors.isEmpty()) {
      for (int i = 0, n = getRowCount(); i < n; i++) {
        final Component[] row = getRow(i);
        final String hours = ((AbstractInput) row[COL_HOUR]).getValue();
        final String weekDays = ((AbstractInput) row[COL_WEEK_DAY]).getValue();
        final String monthDays = ((AbstractInput) row[COL_MONTH_DAY]).getValue();
        if (!ScheduleItem.validate(hours, weekDays, monthDays)) {
          errors.add("Schedule at row " + (i + 1) + " is not valid. Hours can be in 0..23 range, week days can be in 1..7 range, month days can be in 1..31 range.");
        }
      }
    }

    if (!errors.isEmpty()) {
      showErrorMessage(errors);
      return false;
    }

    return true;
  }


  public String toString() {
    return "ScheduleItemsTable{" +
            "scheduleItems=" + scheduleItems +
            ", deleted=" + deleted +
            ", buildID=" + buildID +
            ", requireAtLeastOneScheduleItem=" + requireAtLeastOneScheduleItem +
            '}';
  }
}
