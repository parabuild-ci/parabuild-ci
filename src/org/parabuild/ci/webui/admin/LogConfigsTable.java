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
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.LogConfig;
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
import viewtier.ui.Layout;
import viewtier.ui.Menu;
import viewtier.ui.MenuSelectedEvent;
import viewtier.ui.MenuSelectedListener;
import viewtier.ui.Tierlet;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class LogConfigsTable extends AbstractFlatTable implements Loadable, Saveable, Validatable {

  private static final Log log = LogFactory.getLog(LogConfigsTable.class);
  private static final long serialVersionUID = -538632644979850575L; // NOPMD


  private static final int COL_LOGS = 0;
  private static final String NAME_LOGS = "Logs";

  private final ConfigurationManager cm = ConfigurationManager.getInstance();

  private List configs = new ArrayList(5);
  private final List deleted = new ArrayList(1);
  private int buildID = BuildConfig.UNSAVED_ID;


  public LogConfigsTable() {
    super(1, true);
    setWidth(Pages.PAGE_WIDTH);
    setTitle("Custom Logs");
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
    for (int i = 0, n = getRowCount(); i < n; i++) {
      final Component[] row = getRow(i);
      final AbstractLogConfigPanel logConfigPanel = (AbstractLogConfigPanel) row[COL_LOGS];
      if (logConfigPanel.getBuildID() == BuildConfig.UNSAVED_ID) {
        logConfigPanel.setBuildID(buildID);
      }
    }
  }


  /**
   * Load configuration from given build config
   *
   * @param buildConfig BuildConfig to load configuration for.
   */
  public void load(final BuildConfig buildConfig) {
    setBuildID(buildConfig.getBuildID());
    configs = cm.getLogConfigs(buildConfig.getBuildID());
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

    // validate rows
    for (int i = 0, n = getRowCount(); i < n; i++) {
      final Component[] row = getRow(i);
      final AbstractLogConfigPanel logConfigPanel = (AbstractLogConfigPanel) row[COL_LOGS];
      if (logConfigPanel.getBuildID() == BuildConfig.UNSAVED_ID) {
        logConfigPanel.setBuildID(buildID);
      }
      if (!logConfigPanel.validate()) {
        return false;
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
    for (final Iterator iter = deleted.iterator(); iter.hasNext();) {
      final LogConfig logConfig = (LogConfig) iter.next();
      if (log.isDebugEnabled()) {
        log.debug("log config to delete: " + logConfig);
      }
      if (logConfig.getID() != LogConfig.UNSAVED_ID) {
        cm.deleteObject(logConfig);
      }
    }

    // save modified
    boolean saved = true;
    for (int index = 0, n = getRowCount(); index < n; index++) {
      final Component[] row = getRow(index);
      final AbstractLogConfigPanel logConfigPanel = (AbstractLogConfigPanel) row[COL_LOGS];
      if (!logConfigPanel.save()) {
        saved = false;
      }
    }
    if (log.isDebugEnabled()) {
      log.debug("log configs saved: " + saved);
    }
    return saved;
  }

  // =============================================================================================
  // == Table lifecycle methods                                                                 ==
  // =============================================================================================


  /**
   * This notification method is called when a row is deleted.
   */
  public void notifyRowDeleted(final int index) {
    deleted.add(configs.remove(index));
  }


  /**
   * This notification method is called when a new row is added.
   * Implementing class can use it to keep track of deleted rows
   */
  public void notifyRowAdded(final int addedRowIndex) {
    configs.add(addedRowIndex, new LogConfig());
  }


  /**
   */
  public Component[] makeHeader() {
    final Component[] header = new Component[1];
    header[0] = new TableHeaderLabel(NAME_LOGS, 710);
    return header;
  }


  /**
   * @return result
   */
  public int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex >= configs.size()) {
      return TBL_NO_MORE_ROWS;
    }
    final Component[] row = getRow(rowIndex);
    ((AbstractLogConfigPanel) row[COL_LOGS]).load((LogConfig) configs.get(rowIndex));
    return TBL_ROW_FETCHED;
  }


  protected Component[] makeRow(final int rowIndex) {
    if (rowIndex >= configs.size()) {
      // NOTE: vimeshev - 05/12/2004 - table populate() lifecycle
      // requires makeRow returning non-null component, so we create an
      // empty tracker setup panel.
      return new Component[]{
              new AbstractLogConfigPanel(false) {
                public boolean validateProperties() {
                  return true;
                }
              }
      };
    } else {
      final LogConfig config = (LogConfig) configs.get(rowIndex);
      return new Component[]{LogConfigPanelFactory.makeLogConfigPanel(config.getType())};
    }
  }


  /**
   * Overrides parent's method to use our own edit commands,
   * particularly add row button with a log type dropdown.
   */
  protected TableCommands makeEditableCommands() {
    return new LogConfigsTableCommands();
  }


  private final class LogConfigsTableCommands extends Flow implements TableCommands {

    private static final long serialVersionUID = -538632644979850575L; // NOPMD

    private final Menu deleteRowCommand = new DeleteSelectedMenu(); // NOPMD SingularField
    private final LogTypeDropDown logTypeDropDown = new LogTypeDropDown();  // NOPMD SingularField
    private final Button addRowCommand = new AddButton(); // NOPMD SingularField
    private final MenuDividerLabel lbAddDivider = new MenuDividerLabel(); // NOPMD SingularField
    private final BoldCommonLabel lbAddCaption = new BoldCommonLabel("Log type:  "); // NOPMD SingularField


    public LogConfigsTableCommands() {

      // create delete selected rows command
      deleteRowCommand.addListener(new MenuSelectedListener() {
        public Tierlet.Result menuSelected(final MenuSelectedEvent event) {
          deleteSelectedRows();
          return Tierlet.Result.Continue();
        }
      });

      // create type dropdown

      // create add row command
      addRowCommand.setAlignX(Layout.LEFT);
      addRowCommand.addListener(new ButtonPressedListener() {
        public Tierlet.Result buttonPressed(final ButtonPressedEvent event) {
          // first place a config into the list so that it's available to makeRow
          final int expectedRowIndex = getRowCount();
          final LogConfig config = new LogConfig();
          config.setType((byte) logTypeDropDown.getCode());
          configs.add(expectedRowIndex, config);
          // now request table to create and add row. It will in turn create a
          // corresponding implementation of AbstractLogConfigPanel
          final int rowIdx = addRow();
          // cover our ass
          if (rowIdx != expectedRowIndex) {
            throw new IllegalStateException("Expected that row with index " + expectedRowIndex + " will be added, nut it was " + rowIdx);
          }
          return Tierlet.Result.Continue();
        }
      });

      // join [add command] components
      add(deleteRowCommand);
      add(lbAddDivider);
      add(lbAddCaption);
      add(logTypeDropDown);
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
      // do nothing, log configs doesn't have insert link
    }


    public void setMoveRowCommandsVisible(final boolean visible) {
      // do nothing, log configs doesn't have insert command
    }
  }
}
