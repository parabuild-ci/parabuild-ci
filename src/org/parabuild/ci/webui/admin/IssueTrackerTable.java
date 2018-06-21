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
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.IssueTracker;
import org.parabuild.ci.object.IssueTrackerProperty;
import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.AddButton;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.DeleteSelectedMenu;
import org.parabuild.ci.webui.common.MenuDividerLabel;
import org.parabuild.ci.webui.common.Pages;
import org.parabuild.ci.webui.common.Saveable;
import org.parabuild.ci.webui.common.Validatable;
import viewtier.ui.Button;
import viewtier.ui.ButtonPressedEvent;
import viewtier.ui.ButtonPressedListener;
import viewtier.ui.Color;
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

/**
 * This table contains and hasnles a list of issue trackers associated
 * with a single given build.
 *
 * @see IssueTracker
 */
public final class IssueTrackerTable extends AbstractFlatTable implements Loadable, Validatable, Saveable {

  private static final long serialVersionUID = -536487757842715466L; // NOPMD

  private List trackers = new ArrayList(); // empty list
  private final List deleted = new ArrayList();
  private int buildID = BuildConfig.UNSAVED_ID;


  public IssueTrackerTable() {
    super(1, true);
    showContentBorder(false);
    setHeaderVisible(false);
    showHeaderDivider(true);
    setGridColor(Color.White);
    populate(0);
  }


  /**
   */
  protected Component[] makeHeader() {
    return new Component[]{new CommonLabel("", Pages.PAGE_WIDTH - 40)};
  }


  /**
   * Makes row, should be implemented by successor class
   */
  protected Component[] makeRow(final int rowIndex) {
    if (rowIndex >= trackers.size()) {
      // NOTE: vimeshev - 05/12/2004 - table populate() lifecycle
      // reuquares makeRow returning non-null component, so we create an
      // empty tracker setup panel.
      return new Component[]{
        new AbstractIssueTrackerSetupPanel("") {
          protected void doValidate(final List errors) { // do nothing
          }
        }
      };
    } else {
      final IssueTracker tracker = (IssueTracker)trackers.get(rowIndex);
      return new Component[]{IssueTrackerSetupPanelFactory.makeTrackerPanel(tracker.getType())};
    }
  }


  /**
   * This implementation of this abstract method is called
   * when the table wants to fetch a row with a given rowIndex.
   * Implementing method should fill the data corresponding
   * the given rowIndex.
   *
   * @return this method should return either TBL_ROW_FETCHED
   * or TBL_NO_MORE_ROWS if the requested row is out of range.
   *
   * @see AbstractFlatTable#TBL_ROW_FETCHED
   * @see AbstractFlatTable#TBL_NO_MORE_ROWS
   *
   * @see AbstractIssueTrackerSetupPanel
   */
  protected int fetchRow(final int rowIndex, final int rowFlags) {
    if (rowIndex < trackers.size()) {
      final IssueTracker tracker = (IssueTracker)trackers.get(rowIndex);
      final AbstractIssueTrackerSetupPanel trackerPanel = (AbstractIssueTrackerSetupPanel)getRow(rowIndex)[0];
      trackerPanel.load(tracker);
      return TBL_ROW_FETCHED;
    } else {
      return TBL_NO_MORE_ROWS;
    }
  }


  /**
   * This notification method is called when
   * a row is deleted.
   */
  public void notifyRowDeleted(final int index) {
    deleted.add(trackers.remove(index));
  }


  protected TableCommands makeEditableCommands() {
    return new IssueTrackerTableCommands();
  }


  /**
   * Load configuration from given build config.
   *
   * For this class it load content of ISSUE_TRACKER.
   *
   * @param buildConfig BuildConfig to load configuration for.
   *
   * @see #fetchRow
   */
  public void load(final BuildConfig buildConfig) {
    buildID = buildConfig.getBuildID();
    // get trackers
    trackers = ConfigurationManager.getInstance().getIssueTrackers(buildConfig.getBuildID());
    // start fetchRow sequence
    populate();
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not
   * valid, a component should dispaly a error message in it's area.
   *
   * @return true if valid
   */
  public boolean validate() {
    boolean valid = true;
    final int count = getRowCount();
    for (int index = 0; index < count; index++) {
      final Validatable trackerPanel = (Validatable)getRow(index)[0];
      valid = trackerPanel.validate() && valid;
    }
    return valid;
  }


  /**
   * When called, component should save it's content. This method should
   * return <code>true</code> when content of a component is saved successfully.
   * If not, a component should dispaly a error message in it's area and return
   * <code>false</code>
   *
   * @return true if saved successfuly
   */
  public boolean save() {
    final ConfigurationManager cm = ConfigurationManager.getInstance();

    // delete deleted
    for (final Iterator i = deleted.iterator(); i.hasNext();) {
      final IssueTracker tracker = (IssueTracker)i.next();
      if (tracker.getID() == IssueTracker.UNSAVED_ID) continue;
      cm.deleteObject(tracker);
    }

    // save updated and new
    for (int idx = 0, n = trackers.size(); idx < n; idx++) {
      // save tracker
      final IssueTracker tracker = (IssueTracker)trackers.get(idx);
      tracker.setBuildID(buildID);
      cm.saveObject(tracker);
      // save props from panel
      // REVIEWME: vimeshev - 05/09/2004 - consider moving to save
      // method in AbstractIssueTrackerSetupPanel
      final AbstractIssueTrackerSetupPanel trackerPanel = (AbstractIssueTrackerSetupPanel)getRow(idx)[0];
      final List updatedProperties = trackerPanel.getUpdatedProperties();
      for (final Iterator i = updatedProperties.iterator(); i.hasNext();) {
        final IssueTrackerProperty trackerProperty = (IssueTrackerProperty)i.next();
        trackerProperty.setTrackerID(tracker.getID());
        cm.saveObject(trackerProperty);
      }
    }
    return true;
  }


  public void setBuildID(final int buildID) {
    this.buildID = buildID;
  }


  private final class IssueTrackerTableCommands extends Flow implements TableCommands {

    private static final long serialVersionUID = -536487757842715466L; // NOPMD

    private final IssueTrackerTypeDropDown trackerTypeDropDown = new IssueTrackerTypeDropDown(); // NOPMD SingularField
    private final MenuDividerLabel lbAddDivider = new MenuDividerLabel(); // NOPMD SingularField
    private final Menu deleteRowCommand = new DeleteSelectedMenu(); // NOPMD SingularField
    private final Button addRowCommand = new AddButton(); // NOPMD SingularField


    public IssueTrackerTableCommands() {
      // create delete selected rows command
      deleteRowCommand.addListener(new MenuSelectedListener() {
        public Tierlet.Result menuSelected(final MenuSelectedEvent event) {
          deleteSelectedRows();
          return Tierlet.Result.Continue();
        }
      });

      // create add row command
      addRowCommand.setAlignX(Layout.LEFT);
      addRowCommand.addListener(new ButtonPressedListener() {
        public Tierlet.Result buttonPressed(final ButtonPressedEvent event) {
          // first place a tracker into the list so that it's available to makeRow
          final int expectedRowIndex = getRowCount();
          final IssueTracker tracker = new IssueTracker();
          tracker.setType((byte)trackerTypeDropDown.getCode());
          trackers.add(expectedRowIndex, tracker);
          // now request table to create and add row. It will in turn create a
          // corresponding implementation of AbstractIssueTrackerSetupPanel
          final int rowIdx = addRow();
          // cover our ass
          if (rowIdx != expectedRowIndex) throw new IllegalStateException("Expected that row with index " + expectedRowIndex + " will be added, nut it was " + rowIdx);
          return Tierlet.Result.Continue();
        }
      });

      // join [add command] components
      add(deleteRowCommand);
      add(lbAddDivider);
      add(new BoldCommonLabel("Issue tracker:  "));
      add(trackerTypeDropDown);
      add(addRowCommand);
    }


    /**
     * @return a component contaning controls.
     */
    public Component getComponent() {
      return this;
    }


    /**
     * @param visible true if delete command should be visible.
     */
    public void setAddRowCommandVisible(final boolean visible) {
      lbAddDivider.setVisible(visible);
      addRowCommand.setVisible(visible);
    }


    /**
     * @param visible true if insert command should be visible.
     */
    public void setInsertRowCommandVisible(final boolean visible) {
      // do nothing, issue tracker doesn't have insert row command
    }


    public void setMoveRowCommandsVisible(final boolean visible) {
      // do nothing, issue tracker doesn't have insert command
    }
  }
}
