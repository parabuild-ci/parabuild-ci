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
package org.parabuild.ci.webui.common;

import viewtier.ui.Component;
import viewtier.ui.Flow;
import viewtier.ui.Menu;
import viewtier.ui.MenuSelectedEvent;
import viewtier.ui.MenuSelectedListener;
import viewtier.ui.Tierlet;

/**
 * Default cimplementation of table commands.
 */
public final class DefaultTableCommands extends Flow implements AbstractFlatTable.TableCommands {

  private static final long serialVersionUID = 8695672343405760041L; // NOPMD

  private static final String CAPTION_ADD_ROW = "Add Row";
  private static final String CAPTION_INSERT_ROW = "Insert Row";
  private static final String CAPTION_MOVE_ROW_UP = "Move Row Up";
  private static final String CAPTION_MOVE_ROW_DOWN = "Move Row Down";

  private Menu addRowCommand = new CommonMenu(CAPTION_ADD_ROW);
  private Menu insertRowCommand = new CommonMenu(CAPTION_INSERT_ROW);
  private Menu deleteRowCommand = new DeleteSelectedMenu();
  private Menu moveRowUpCommand = new CommonMenu(CAPTION_MOVE_ROW_UP);
  private Menu moveRowDownCommand = new CommonMenu(CAPTION_MOVE_ROW_DOWN);
  private MenuDividerLabel lbAddRowSeparator = new MenuDividerLabel();
  private MenuDividerLabel lbInsertRowSeparator = new MenuDividerLabel();
  private MenuDividerLabel lbDeleteRowSeparator = new MenuDividerLabel();
  private MenuDividerLabel lbMoveRowUpSeparator = new MenuDividerLabel();


  public DefaultTableCommands(final AbstractFlatTable table) {
    // add row command
    addRowCommand.addListener(new MenuSelectedListener() {
      public Tierlet.Result menuSelected(final MenuSelectedEvent event) {
        // add row
        table.addRow();
        return Tierlet.Result.Continue();
      }
    });

    // insert row command
    insertRowCommand.addListener(new MenuSelectedListener() {
      public Tierlet.Result menuSelected(final MenuSelectedEvent event) {
        // add row
        table.insertRow();
        return Tierlet.Result.Continue();
      }
    });

    // delete selected rows command
    deleteRowCommand.addListener(new MenuSelectedListener() {
      public Tierlet.Result menuSelected(final MenuSelectedEvent event) {
        table.deleteSelectedRows();
        return Tierlet.Result.Continue();
      }
    });

    // Move selected rows up command
    moveRowUpCommand.addListener(new MenuSelectedListener() {
      public Tierlet.Result menuSelected(final MenuSelectedEvent event) {
        table.moveSelectedRow(-1);
        return Tierlet.Result.Continue();
      }
    });

    // Move selected rows up ommand
    moveRowDownCommand.addListener(new MenuSelectedListener() {
      public Tierlet.Result menuSelected(final MenuSelectedEvent event) {
        table.moveSelectedRow(1);
        return Tierlet.Result.Continue();
      }
    });

    // join menues
    add(addRowCommand).add(lbAddRowSeparator);
    add(insertRowCommand).add(lbInsertRowSeparator);
    add(deleteRowCommand).add(lbDeleteRowSeparator);
    add(moveRowUpCommand).add(lbMoveRowUpSeparator);
    add(moveRowDownCommand);

    setInsertRowCommandVisible(false);
    setMoveRowCommandsVisible(false);
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
  public final void setAddRowCommandVisible(final boolean visible) {
    addRowCommand.setVisible(visible);
    lbAddRowSeparator.setVisible(visible);
  }


  /**
   * @param visible true if insert command should be visible.
   */
  public final void setInsertRowCommandVisible(final boolean visible) {
    insertRowCommand.setVisible(visible);
    lbInsertRowSeparator.setVisible(visible);
  }


  /**
   * @param visible true if move commands should be visible.
   */
  public final void setMoveRowCommandsVisible(final boolean visible) {
    lbMoveRowUpSeparator.setVisible(visible);
    lbDeleteRowSeparator.setVisible(visible);
    moveRowUpCommand.setVisible(visible);
    moveRowDownCommand.setVisible(visible);
  }
}


