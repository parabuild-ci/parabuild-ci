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
import org.parabuild.ci.object.ResultConfig;
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

public final class ResultConfigsTable extends AbstractFlatTable implements Loadable, Saveable, Validatable {

  private static final Log log = LogFactory.getLog(ResultConfigsTable.class);
  private static final long serialVersionUID = -538632644979850575L; // NOPMD


  public static final int COL_RESULTS = 0;
  public static final String NAME_RESULTS = "Results";

  private final ConfigurationManager cm = ConfigurationManager.getInstance();

  private List configs = new ArrayList(5);
  private final List deleted = new ArrayList(1);
  private int buildID = BuildConfig.UNSAVED_ID;


  public ResultConfigsTable() {
    super(1, true);
    setWidth(Pages.PAGE_WIDTH);
    setTitle("Build Results");
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
    for (int i = 0, n = getRowCount(); i < n; i++) {
      final Component[] row = getRow(i);
      final AbstractResultConfigPanel rcp = (AbstractResultConfigPanel) row[COL_RESULTS];
      if (rcp.getBuildID() == BuildConfig.UNSAVED_ID) {
        rcp.setBuildID(buildID);
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
    configs = cm.getResultConfigs(buildConfig.getBuildID());
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
      final AbstractResultConfigPanel rcp = (AbstractResultConfigPanel) row[COL_RESULTS];
      if (rcp.getBuildID() == BuildConfig.UNSAVED_ID) {
        rcp.setBuildID(buildID);
      }
      if (!rcp.validate()) {
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
      final ResultConfig rc = (ResultConfig) iter.next();
      if (log.isDebugEnabled()) {
        log.debug("result config to delete: " + rc);
      }
      if (rc.getID() != ResultConfig.UNSAVED_ID) {
        cm.deleteObject(rc);
      }
    }

    // save modified
    boolean saved = true;
    for (int i = 0, n = getRowCount(); i < n; i++) {
      final Component[] row = getRow(i);
      final AbstractResultConfigPanel rcp = (AbstractResultConfigPanel) row[COL_RESULTS];
      if (!rcp.save()) {
        saved = false;
      }
    }
    if (log.isDebugEnabled()) {
      log.debug("result configs saved: " + saved);
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
    configs.add(addedRowIndex, new ResultConfig());
  }


  /**
   */
  public Component[] makeHeader() {
    final Component[] header = new Component[1];
    header[0] = new TableHeaderLabel(NAME_RESULTS, 710);
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
    ((AbstractResultConfigPanel) row[COL_RESULTS]).load((ResultConfig) configs.get(rowIndex));
    return TBL_ROW_FETCHED;
  }


  protected Component[] makeRow(final int rowIndex) {
    if (rowIndex >= configs.size()) {
      // NOTE: vimeshev - 05/12/2004 - table populate() lifecycle
      // requires makeRow returning non-null component, so we create an
      // empty tracker setup panel.
      return new Component[]{
              new AbstractResultConfigPanel(false) {
                private static final long serialVersionUID = -7518981629039789490L;


                public boolean validateProperties() {
                  return true;
                }
              }
      };
    } else {
      final ResultConfig config = (ResultConfig) configs.get(rowIndex);
      return new Component[]{ResultConfigPanelFactory.makeResultConfigPanel(config.getType())};
    }
  }


  /**
   * Overrides parent's method to use our own edit commands,
   * particularly add row button with a result type dropdown.
   */
  protected TableCommands makeEditableCommands() {
    return new ResultConfigsTableCommands();
  }


  /**
   * Holds delete/add controls for {@link ResultConfigsTable}
   *
   * @see ResultConfigsTable
   * @see TableCommands
   */
  private final class ResultConfigsTableCommands extends Flow implements TableCommands {

    private static final long serialVersionUID = -538632644979850575L; // NOPMD

    private final Menu deleteRowCommand = new DeleteSelectedMenu(); // NOPMD SingularField
    private final ResultTypeDropDown ddResultType = new ResultTypeDropDown(); // NOPMD SingularField
    private final Button btnAddRow = new AddButton(); // NOPMD SingularField
    private final MenuDividerLabel lbAddDivider = new MenuDividerLabel(); // NOPMD SingularField
    private final BoldCommonLabel lbAddCaption = new BoldCommonLabel("Result type:  "); // NOPMD SingularField


    public ResultConfigsTableCommands() {

      // create delete selected rows command
      deleteRowCommand.addListener(new MenuSelectedListener() {
        private static final long serialVersionUID = -4459363179908636336L;


        public Tierlet.Result menuSelected(final MenuSelectedEvent event) {
          deleteSelectedRows();
          return Tierlet.Result.Continue();
        }
      });

      // create add row command
      btnAddRow.setAlignX(Layout.LEFT);
      btnAddRow.addListener(new ButtonPressedListener() {
        private static final long serialVersionUID = 2748684754682533009L;


        public Tierlet.Result buttonPressed(final ButtonPressedEvent event) {
          // first place a config into the list so that it's available to makeRow
          final int expectedRowIndex = getRowCount();
          final ResultConfig config = new ResultConfig();
          config.setType((byte) ddResultType.getCode());
          configs.add(expectedRowIndex, config);
          // now request table to create and add row. It will in turn create a
          // corresponding implementation of AbstractResultConfigPanel
          final int rowIdx = addRow();
          // cover our ass
          if (rowIdx != expectedRowIndex) {
            throw new IllegalStateException("Expected that row with index " + expectedRowIndex + " will be added, nut it was " + rowIdx);
          }
          return Tierlet.Result.Continue();
        }
      });

      // join [add command] components
      setAlignX(Layout.RIGHT);
      add(deleteRowCommand);
      add(lbAddDivider);
      add(lbAddCaption);
      add(ddResultType);
      add(btnAddRow);
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
      lbAddDivider.setVisible(visible);
      lbAddCaption.setVisible(visible);
      ddResultType.setVisible(visible);
    }


    /**
     * @param visible true if insert command should be visible.
     */
    public void setInsertRowCommandVisible(final boolean visible) {
      // do nothing, issue tracker doesn't have insert row command
    }

    public void setMoveRowCommandsVisible(final boolean visible) {
      // do nothing, result configs doesn't have insert command
    }
  }
}
