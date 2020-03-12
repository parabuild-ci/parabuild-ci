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
package org.parabuild.ci.webui.admin.accurev;

import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.common.WebUIConstants;
import org.parabuild.ci.webui.admin.AbstractSourceControlPanel;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.EncryptingPassword;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.DropDown;

import java.util.ArrayList;
import java.util.List;

/**
 * AccurevSettingsPanel
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 12, 2009 2:32:25 PM
 */
public final class AccurevSettingsPanel extends AbstractSourceControlPanel {

  private static final String ACCUREV_SETTINGS = "AccuRev Settings";

  private static final String DEPOT = "Depot: ";
  private static final String HOST = "Host: ";
  private static final String LINE_TERMINATOR = "Text-file line terminator: ";
  private static final String PASSWORD = "Password: ";
  private static final String EXE_PATH = "Path to accurev executable: ";
  private static final String PORT = "Port: ";
  private static final String STREAM = "Stream: ";
  private static final String USER = "User: ";
  private static final String WORKSPACE_KIND = "File Locking: ";
  private static final long serialVersionUID = -2268327593466070570L;

  private final CommonFieldLabel lbDepot = new CommonFieldLabel(DEPOT);
  private final CommonFieldLabel lbHost = new CommonFieldLabel(HOST);
  private final CommonFieldLabel lbLineTerminator = new CommonFieldLabel(LINE_TERMINATOR);
  private final CommonFieldLabel lbPassword = new CommonFieldLabel(PASSWORD);
  private final CommonFieldLabel lbPathToExe = new CommonFieldLabel(EXE_PATH);
  private final CommonFieldLabel lbPort = new CommonFieldLabel(PORT);
  private final CommonFieldLabel lbStream = new CommonFieldLabel(STREAM);
  private final CommonFieldLabel lbUser = new CommonFieldLabel(USER);
  private final CommonFieldLabel lbLock = new CommonFieldLabel(WORKSPACE_KIND);

  private final CommonField flDepot = new CommonField("accurev-depot", 75, 80);
  private final CommonField flHost = new CommonField("accurev-host", 50, 50);
  private final CommonField flPathToExe = new CommonField("path-to-accurev-exe", 100, 70);
  private final CommonField flPort = new CommonField("accurev-port", 5, 5);
  private final CommonField flStream = new CommonField("accurev-stream", 150, 90);
  private final CommonField flUser = new CommonField("accurev-user", 30, 30);
  private final DropDown flLock = new AccurevFileLockingDropDown();
  private final DropDown flLineTerminator = new AccurevEOLTypeDropDown();
  private final EncryptingPassword flPassword = new EncryptingPassword(25, 25, "accurev-password");


  /**
   * Creates message panel with title displayed
   */
  public AccurevSettingsPanel() {
    super(ACCUREV_SETTINGS);

    // Layout
    gridIterator.addPair(lbPathToExe, new RequiredFieldMarker(flPathToExe));
    gridIterator.addPair(lbHost, new RequiredFieldMarker(flHost));
    gridIterator.addPair(lbPort, new RequiredFieldMarker(flPort));
    gridIterator.addPair(lbUser, new RequiredFieldMarker(flUser));
    gridIterator.addPair(lbPassword, new RequiredFieldMarker(flPassword));
//    gridIterator.addPair(lbDepot, new RequiredFieldMarker(flDepot));
    gridIterator.addPair(lbStream, new RequiredFieldMarker(flStream));
//    gridIterator.addPair(lbLineTerminator, flLineTerminator);
//    gridIterator.addPair(lbLock, flLock);

    // Register
//    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.ACCUREV_DEPOT, flDepot);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.ACCUREV_EXE_PATH, flPathToExe);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.ACCUREV_HOST, flHost);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.ACCUREV_PASSWORD, flPassword);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.ACCUREV_PORT, flPort);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.ACCUREV_STREAM, flStream);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.ACCUREV_USER, flUser);
//    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.ACCUREV_EOL_TYPE, flLineTerminator);
//    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.ACCUREV_WORKSPACE_LOCK, flLock);

    // Add footer
    addCommonAttributes();


    // Set defaults
    flPort.setValue("5050");
  }


  protected void doSetMode(final int mode) {
    if (mode == (int) WebUIConstants.MODE_VIEW) {
      setEditable(false);
    } else if (mode == (int) WebUIConstants.MODE_EDIT) {
      setEditable(true);
    } else if (mode == (int) WebUIConstants.MODE_INHERITED) {
      // first, disable everything
      setEditable(false);
      // enable those editable for parallel mode
      flPathToExe.setEditable(true);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  private void setEditable(final boolean editable) {
    flDepot.setEditable(editable);
    flHost.setEditable(editable);
    flPassword.setEditable(editable);
    flPathToExe.setEditable(editable);
    flPort.setEditable(editable);
    flStream.setEditable(editable);
    flUser.setEditable(editable);
  }


  protected boolean doValidate() {
    final List errors = new ArrayList(1);

    // Basic validation
//    WebuiUtils.validateFieldNotBlank(errors, DEPOT, flDepot);
    WebuiUtils.validateFieldNotBlank(errors, STREAM, flStream);
    WebuiUtils.validateFieldNotBlank(errors, HOST, flHost);
    WebuiUtils.validateFieldNotBlank(errors, PASSWORD, flPassword);
    WebuiUtils.validateFieldNotBlank(errors, EXE_PATH, flPathToExe);
    WebuiUtils.validateFieldNotBlank(errors, PORT, flPort);
    WebuiUtils.validateFieldNotBlank(errors, USER, flUser);

    // Continue validating
    if (errors.isEmpty()) {
      WebuiUtils.validateFieldValidPositiveInteger(errors, PORT, flPort);
    }

    // Return
    if (!errors.isEmpty()) {
      showErrorMessage(errors);
    }
    return errors.isEmpty();
  }


  public String toString() {
    return "AccurevSettingsPanel{" +
            "lbDepot=" + lbDepot +
            ", lbHost=" + lbHost +
            ", lbLineTerminator=" + lbLineTerminator +
            ", lbPassword=" + lbPassword +
            ", lbPathToExe=" + lbPathToExe +
            ", lbPort=" + lbPort +
            ", lbStream=" + lbStream +
            ", lbUser=" + lbUser +
            ", lbLock=" + lbLock +
            ", flDepot=" + flDepot +
            ", flHost=" + flHost +
            ", flPathToExe=" + flPathToExe +
            ", flPort=" + flPort +
            ", flStream=" + flStream +
            ", flUser=" + flUser +
            ", flLock=" + flLock +
            ", flLineTerminator=" + flLineTerminator +
            ", flPassword=" + flPassword +
            '}';
  }
}
