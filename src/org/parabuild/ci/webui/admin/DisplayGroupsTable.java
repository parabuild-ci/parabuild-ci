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
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.DisplayGroupManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.DisplayGroup;
import org.parabuild.ci.object.DisplayGroupBuild;
import org.parabuild.ci.webui.DisplayGroupDropDown;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.AddButton;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.DeleteSelectedMenu;
import org.parabuild.ci.webui.common.MenuDividerLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import org.parabuild.ci.webui.common.Validatable;
import viewtier.ui.Button;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Menu;
import viewtier.ui.MenuSelectedEvent;
import viewtier.ui.MenuSelectedListener;
import viewtier.ui.Tierlet;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class DisplayGroupsTable extends AbstractFlatTable implements Loadable, Saveable, Validatable {

  private static final Log log = LogFactory.getLog(DisplayGroupsTable.class);
  private static final long serialVersionUID = -538632644979850575L; // NOPMD


  private static final int COL_display_GROUP = 0;
  private static final String DISPLAY_GROUP = "Display Group";

  private final DisplayGroupManager dgm = DisplayGroupManager.getInstance();

  private List displayGroups = new ArrayList(5);
  private final List deleted = new ArrayList(1);
  private int buildID = BuildConfig.UNSAVED_ID;


  public DisplayGroupsTable() {
    super(1, true);
    setWidth(Pages.PAGE_WIDTH);
    setTitle("Display Groups");
    setHeaderVisible(false);
  }

  /**
   * Load configuration from given build config
   *
   * @param buildConfig BuildConfig to load configuration for.
   */
  public void load(final BuildConfig buildConfig) {
    displayGroups = dgm.getDisplayGroupBuildsByBuildID(buildConfig.getBuildID());
    populate();
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
    final ArrayList errors = new ArrayList(1);

    final int rowCount = getRowCount();
    final Set groupNames = new HashSet(rowCount);
    for (int i = 0; i < rowCount; i++) {
      final Component[] row = getRow(i);
      final String groupName = ((Label) row[0]).getText();
      if (!groupNames.add(groupName)) {
        errors.add("Duplicate group: " + groupName);
      }
    }

    if (errors.isEmpty()) {
      return true;
    }

    showErrorMessage(errors);
    return false;
  }


  /**
   * Saves table data.
   */
  public boolean save() {

    // validate
    if (buildID == BuildConfig.UNSAVED_ID) {
      throw new IllegalArgumentException("Build ID can not be uninitialized");
    }

    // delete deleted
    for (final Iterator iter = deleted.iterator(); iter.hasNext(); ) {
      final DisplayGroupBuild displayGroupBuild = (DisplayGroupBuild) iter.next();
      if (log.isDebugEnabled()) {
        log.debug("Dispalay group build to delete to delete: " + displayGroupBuild);
      }
      if (displayGroupBuild.getID() != DisplayGroupBuild.UNSAVED_ID) {
        dgm.deleteBuildFromDisplayGroup(displayGroupBuild.getBuildID(), displayGroupBuild.getDisplayGroupID());
      }
    }

    // save modified
    for (int index = 0, n = getRowCount(); index < n; index++) {
      final DisplayGroupBuild displayGroupBuild = (DisplayGroupBuild) displayGroups.get(index);
      if (displayGroupBuild.getID() == DisplayGroupBuild.UNSAVED_ID) {
        ConfigurationManager.getInstance().saveObject(displayGroupBuild);
      }
    }
    return true;
  }

  // =============================================================================================
  // == Table lifecycle methods                                                                 ==
  // =============================================================================================


  /**
   * This notification method is called when a row is deleted.
   */
  public void notifyRowDeleted(final int index) {
    deleted.add(displayGroups.remove(index));
  }


  /**
   */
  public Component[] makeHeader() {
    final Component[] header = new Component[1];
    header[0] = new TableHeaderLabel(DISPLAY_GROUP, 710);
    return header;
  }


  /**
   * @return result
   */
  public int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= displayGroups.size()) {
      return TBL_NO_MORE_ROWS;
    }
    loadRow(rowIndex);
    return TBL_ROW_FETCHED;
  }

  /**
   * Loads a given row into the view.
   *
   * @param rowIndex an index of the row to load.
   */
  private void loadRow(final int rowIndex) {
    final DisplayGroupBuild displayGroupBuild = (DisplayGroupBuild) displayGroups.get(rowIndex);
    final DisplayGroup displayGroup = DisplayGroupManager.getInstance().getDisplayGroup(displayGroupBuild.getDisplayGroupID());
    final String groupName = displayGroup.getName();
    final Component[] row = getRow(rowIndex);
    ((Label) row[COL_display_GROUP]).setText(groupName);
  }


  protected Component[] makeRow(final int rowIndex) {
    if (rowIndex >= displayGroups.size()) {
      // NOTE: vimeshev - 05/12/2004 - table populate() lifecycle
      // requires makeRow returning non-null component, so we create an
      // empty tracker setup panel.
      return new Component[]{new Label()};
    } else {
      return new Component[]{new Label()};
    }
  }


  /**
   * Overrides parent's method to use our own edit commands,
   * particularly add row button with a log type dropdown.
   */
  protected TableCommands makeEditableCommands() {
    return new DisplayGroupsEditableCommands();
  }

  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  private final class DisplayGroupsEditableCommands extends Flow implements TableCommands {

    private static final long serialVersionUID = -538632644979850575L; // NOPMD

    private final Menu deleteRowCommand = new DeleteSelectedMenu(); // NOPMD SingularField
    private final DisplayGroupDropDown displayGroupDropDown = new DisplayGroupDropDown(false, false);  // NOPMD SingularField
    private final Button addRowCommand = new AddButton(); // NOPMD SingularField
    private final MenuDividerLabel lbAddDivider = new MenuDividerLabel(); // NOPMD SingularField
    private final BoldCommonLabel lbAddCaption = new BoldCommonLabel("Display group:  "); // NOPMD SingularField


    public DisplayGroupsEditableCommands() {

      // create delete selected rows command
      deleteRowCommand.addListener(new MenuSelectedListener() {
        private static final long serialVersionUID = 6168831060379313199L;


        public Tierlet.Result menuSelected(final MenuSelectedEvent event) {
          deleteSelectedRows();
          return Tierlet.Result.Continue();
        }
      });

      // create add row command
      addRowCommand.setAlignX(Layout.LEFT);
      addRowCommand.addListener(new ButtonPressedListener() {
        private static final long serialVersionUID = 9182753660814734988L;


        public Tierlet.Result buttonPressed(final ButtonPressedEvent event) {
          // first place a config into the list so that it's available to makeRow
          final int expectedRowIndex = getRowCount();
          final DisplayGroupBuild displayGroupBuild = new DisplayGroupBuild();
          displayGroupBuild.setDisplayGroupID((byte) displayGroupDropDown.getCode());
          displayGroupBuild.setBuildID(buildID);
          displayGroups.add(expectedRowIndex, displayGroupBuild);
          // now request table to create and add row. It will in turn create a
          // corresponding implementation of AbstractLogConfigPanel
          final int rowIdx = addRow();
          // cover our ass
          if (rowIdx != expectedRowIndex) {
            throw new IllegalStateException("Expected that row with index " + expectedRowIndex + " will be added, nut it was " + rowIdx);
          }
          loadRow(rowIdx);
          return Tierlet.Result.Continue();
        }
      });

      // join [add command] components
      add(deleteRowCommand);
      add(lbAddDivider);
      add(lbAddCaption);
      add(displayGroupDropDown);
      add(addRowCommand);
    }


    /**
     * @return a component containing controls.
     */
    public Component getComponent() {
      return this;
    }


    /**
     * @param visible true if delete command should be visible.
     */
    public void setAddRowCommandVisible(final boolean visible) {
      addRowCommand.setVisible(visible);
      lbAddCaption.setVisible(visible);
      lbAddDivider.setVisible(visible);
    }


    /**
     * @param visible true if insert command should be visible.
     */
    public void setInsertRowCommandVisible(final boolean visible) {
      // do nothing, log displayGroups doesn't have insert link
    }


    public void setMoveRowCommandsVisible(final boolean visible) {
      // do nothing, log displayGroups doesn't have insert command
    }
  }
}
