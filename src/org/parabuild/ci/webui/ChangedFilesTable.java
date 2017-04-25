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
package org.parabuild.ci.webui;

import org.parabuild.ci.webui.common.AbstractFlatTable;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.TableHeaderLabel;
import viewtier.ui.Component;
import viewtier.ui.Label;

import java.util.List;

/**
 * This panel display changed files participating in the build.
 * The panel consists of a header, and a list (table) of files.
 */
public final class ChangedFilesTable extends AbstractFlatTable {

  private static final long serialVersionUID = 5805873768802255788L; // NOPMD
  private static final String CAPTION_FILE_NAME = "Changed File";
  private static final int COL_FILE_NAME = 0;
  private final boolean showChangeLists;
  private final int activeBuildID;
  private final int fromBuildNum;
  private final int toBuildNum;
  private List files;


  public ChangedFilesTable(final int activeBuildID, final int fromBuildNum, final int toBuildNum, final boolean showChangeLists) {

    super(1, false);
    setWidth("100%");
    this.showChangeLists = showChangeLists;
    this.activeBuildID = activeBuildID;
    this.fromBuildNum = fromBuildNum;
    this.toBuildNum = toBuildNum;
  }


  /**
   * Sets a String list of files.
   *
   * @param files a String list of file paths.
   */
  public void setFiles(final List files) {

    this.files = files;
  }


  protected Component[] makeHeader() {

    final Component[] headers = new Label[columnCount()];
    headers[COL_FILE_NAME] = new TableHeaderLabel(CAPTION_FILE_NAME, "100%");
    return headers;
  }


  protected Component[] makeRow(final int rowIndex) {

    final Component[] result = new Component[columnCount()];
    if (showChangeLists) {

      // Create panel to show a name and a list of changes for the file
      result[COL_FILE_NAME] = new ChangedFilePanel(activeBuildID, fromBuildNum, toBuildNum);
    } else {

      // Create label
      result[COL_FILE_NAME] = new CommonLabel();
    }
    return result;
  }


  protected int fetchRow(final int rowIndex, final int rowFlags) {

    if (rowIndex >= files.size()) {
      return TBL_NO_MORE_ROWS;
    }

    final String fileName = (String) files.get(rowIndex);
    final Component[] row = getRow(rowIndex);

    if (showChangeLists) {

      ((ChangedFilePanel) row[COL_FILE_NAME]).filePath(fileName);

    } else {

      ((Label) row[COL_FILE_NAME]).setText(fileName);
    }
    return TBL_ROW_FETCHED;
  }
}
