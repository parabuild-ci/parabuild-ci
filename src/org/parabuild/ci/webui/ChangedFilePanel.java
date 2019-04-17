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

import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.webui.common.CommonLabel;
import viewtier.ui.Panel;

import java.util.List;

/**
 * Changed file panel contains a file name and a table with changes.
 */
public class ChangedFilePanel extends Panel {

  private static final long serialVersionUID = -1802105575690644856L;
  /**
   * An active build ID.
   */
  private final int activeBuildID;

  /**
   * A build number from that to show changes for a file.
   */
  private final int startBuildNumber;

  /**
   * A build number to that to show changes for a file.
   */
  private final int endBuildNumber;

  /**
   * Label to show file name.
   */
  private final CommonLabel lbFileName = new CommonLabel();

  /**
   * List of changes for this file.
   */
  private final FileChangeListsTable tblChangeLists = new FileChangeListsTable();


  /**
   * Creates a new ChangedFilePanel.
   *
   * @param activeBuildID    the active build ID.
   * @param startBuildNumber the build number from that to show changes for a file.
   * @param endBuildNumber   the build number to that to show changes for a file.
   */
  public ChangedFilePanel(final int activeBuildID, final int startBuildNumber, final int endBuildNumber) {

    setWidth("100%");

    // Save parameters
    this.activeBuildID = activeBuildID;
    this.startBuildNumber = startBuildNumber;
    this.endBuildNumber = endBuildNumber;

    // Layout
    this.add(lbFileName);
    this.add(tblChangeLists);

    lbFileName.setHeight(20);
  }


  /**
   * Populates this panel with a list of changes for the given file name.
   *
   * @param filePath the name of the file for that to show changes.
   */
  public void filePath(final String filePath) {

    // Set file name
    lbFileName.setText(filePath);

    // Populate table
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final List changeLists = cm.getChangeLists(activeBuildID, startBuildNumber, endBuildNumber, filePath);
    tblChangeLists.setChangeLists(changeLists);
    tblChangeLists.populate();
  }
}
