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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildWatcher;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.EmailField;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import org.parabuild.ci.webui.common.Validatable;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import org.parabuild.ci.webui.common.CodeNameDropDown;
import viewtier.ui.AbstractInput;
import viewtier.ui.Component;
import viewtier.ui.Field;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 */
public final class BuildWatcherTable extends AbstractFlatTable implements Validatable {

  private static final long serialVersionUID = -554537606480131927L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(BuildWatcherTable.class); // NOPMD

  private static final int COLUMN_COUNT = 2;
  private static final int COL_WATCH_EMAIL = 0;
  private static final int COL_WATCH_LEVEL = 1;

  public static final String NAME_WATCH_EMAIL = "E-mail";
  public static final String NAME_WATCH_LEVEL = "Notification Level";

  private List watchers = new ArrayList(3);
  private final List deleted = new ArrayList(1);
  private int buildID = BuildConfig.UNSAVED_ID;
  private final byte viewMode;


  public BuildWatcherTable(final byte viewMode) {
    super(COLUMN_COUNT, true);
    this.viewMode = viewMode;
    setTitle("Build Watchers");
  }


  /**
   * Sets user to e-mail map
   *
   * @param watchers
   */
  public void populate(final List watchers) {
    this.watchers = watchers;
    populate();
    if (watchers.isEmpty()) {
      if (getRowCount() == 0) {
        for (int i = 0; i < 5; i++) {
          addRow();
        }
      }
    }
  }


  /**
   * Returns modified list of user to e-mail mappings
   *
   * @return list of user to e-mail mappings
   */
  public List getWatchers() {
    return watchers;
  }


  public int getBuildID() {
    return buildID;
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
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

    final Set duplcateFinder = new HashSet(getRowCount());

    // validate rows
    final List errors = new ArrayList(1);
    for (int index = 0, n = getRowCount(); index < n; index++) {
      final Component[] row = getRow(index);
      final BuildWatcher watcher = (BuildWatcher) watchers.get(index);
      if (isRowNewAndBlank(watcher, row)) {
        continue;
      }
      final Field email = (Field) row[COL_WATCH_EMAIL];
      email.setValue(email.getValue().trim().toLowerCase());
      WebuiUtils.validateColumnNotBlank(errors, index, NAME_WATCH_EMAIL, email.getValue());
      WebuiUtils.validateColumnNotBlank(errors, index, NAME_WATCH_LEVEL, ((AbstractInput) row[COL_WATCH_LEVEL]).getValue());
      if (!WebuiUtils.isBlank(email) && !duplcateFinder.add(email)) {
        errors.add("Duplicate e-mail: " + email.getValue());
      }
    }

    if (!errors.isEmpty()) {
      showErrorMessage(errors);
      return false;
    }

    return true;
  }


  /**
   * Saves table data.
   */
  public boolean save() {
    // validate
    ArgumentValidator.validateBuildIDInitialized(buildID);

    // delete deleted
    for (final Iterator iter = deleted.iterator(); iter.hasNext();) {
      final BuildWatcher watcher = (BuildWatcher) iter.next();
      if (watcher.getWatcherID() != BuildWatcher.UNSAVED_ID) {
        ConfigurationManager.getInstance().delete(watcher);
      }
    }

    // save modified
    for (int index = 0, n = getRowCount(); index < n; index++) {
      final Component[] row = getRow(index);
      final BuildWatcher watcher = (BuildWatcher) watchers.get(index);
      if (isRowNewAndBlank(watcher, row)) {
        continue;
      }
      if (watcher.getBuildID() == BuildConfig.UNSAVED_ID) {
        watcher.setBuildID(buildID);
      }
      watcher.setEmail(((AbstractInput) row[COL_WATCH_EMAIL]).getValue());
      watcher.setLevel((byte) ((CodeNameDropDown) row[COL_WATCH_LEVEL]).getCode());
      ConfigurationManager.getInstance().save(watcher);
    }
    return true;
  }


  /**
   * Returns true if a row is new and blank
   *
   * @param watcher
   * @param row
   */
  private static boolean isRowNewAndBlank(final BuildWatcher watcher, final Component[] row) {
    return watcher.getBuildID() == BuildConfig.UNSAVED_ID
            && StringUtils.isBlank(((AbstractInput) row[COL_WATCH_EMAIL]).getValue());
  }

  // =============================================================================================
  // == Table lifecycle methods                                                                 ==
  // =============================================================================================


  /**
   * This notification method is called when a row is deleted.
   */
  public void notifyRowDeleted(final int index) {
    deleted.add(watchers.remove(index));
  }


  /**
   * This notification method is called when a new row is added.
   * Implementing class can use it to keep track of deleted rows
   */
  public void notifyRowAdded(final int addedRowIndex) {
    watchers.add(addedRowIndex, new BuildWatcher());
  }


  /**
   */
  protected Component[] makeHeader() {
    final Component[] header = new Component[COLUMN_COUNT];
    header[0] = new TableHeaderLabel(NAME_WATCH_EMAIL, 177);
    header[1] = new TableHeaderLabel(NAME_WATCH_LEVEL, 177);
    return header;
  }


  /**
   */
  protected int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= watchers.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final Component[] row = getRow(rowIndex);
    ((AbstractInput) row[COL_WATCH_EMAIL]).setValue(get(rowIndex).getEmail());
    ((CodeNameDropDown) row[COL_WATCH_LEVEL]).setCode(get(rowIndex).getLevel());
    return TBL_ROW_FETCHED;
  }


  /**
   * Helper method to get typed object out of the list
   *
   * @param index
   */
  private BuildWatcher get(final int index) {
    return (BuildWatcher) watchers.get(index);
  }


  protected Component[] makeRow(final int rowIndex) {
    final boolean editable = viewMode == WebUIConstants.MODE_EDIT;
    final AbstractInput[] row = new AbstractInput[COLUMN_COUNT];

    row[0] = new EmailField();
    row[0].setEditable(editable);

    row[1] = new WatchLevelDropDown();
    row[1].setEditable(editable);

    return row;
  }


  public String toString() {
    return "BuildWatcherTable{" +
            "watchers=" + watchers +
            ", deleted=" + deleted +
            ", buildID=" + buildID +
            ", viewMode=" + viewMode +
            '}';
  }
}
