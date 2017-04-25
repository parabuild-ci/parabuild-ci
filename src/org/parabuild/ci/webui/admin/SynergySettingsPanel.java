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

import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.NoLiveAgentsException;
import org.parabuild.ci.versioncontrol.DepotPathParser;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.EncryptingPassword;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.DropDown;
import viewtier.ui.Field;
import viewtier.ui.Layout;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 */
public final class SynergySettingsPanel extends AbstractSourceControlPanel {

  private static final long serialVersionUID = 4467119862820193870L; // NOPMD

  private static final String CAPTION_DEV_PATH = "Development path: ";
  private static final String CAPTION_HOST = "Host: ";
  private static final String CAPTION_LINE_TERMINATOR = "Line terminator: ";
  private static final String CAPTION_PASSWORD = "Password: ";
  private static final String CAPTION_PATH_TO_EXE = "Path to ccm executable: ";
  private static final String CAPTION_PORT = "Port: ";
  private static final String CAPTION_PROJECT_PATH = "Project: ";
  private static final String CAPTION_PROJECT_REVISION = "Project revision: ";
  private static final String CAPTION_USER = "User name: ";


  private final DropDown flLineTerminator = new MKSLineTerminatorDropDown(); // NOPMD SingularField
  private final EncryptingPassword flPassword = new EncryptingPassword(30, 20, "synergy_password"); // NOPMD SingularField
  private final Field flDevPath = new Field(200, 50); // NOPMD SingularField
  private final Field flHost = new Field(100, 50); // NOPMD SingularField
  private final Field flPathToExe = new Field(200, 80); // NOPMD SingularField
  private final Field flPort = new Field(5, 5); // NOPMD SingularField
  private final Field flProject = new Field(200, 80); // NOPMD SingularField
  private final Field flProjectRevision = new Field(20, 20); // NOPMD SingularField
  private final Field flUser = new Field(20, 20); // NOPMD SingularField


  public SynergySettingsPanel() {
    super("Synergy settings");
    // layout
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PATH_TO_EXE), new RequiredFieldMarker(flPathToExe));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_HOST), new RequiredFieldMarker(flHost));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PORT), new RequiredFieldMarker(flPort));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_USER), new RequiredFieldMarker(flUser));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PASSWORD), new RequiredFieldMarker(flPassword));
    gridIterator.addBlankLine();
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PROJECT_PATH), new RequiredFieldMarker(flProject));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PROJECT_REVISION), flProjectRevision);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_DEV_PATH), flDevPath);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_LINE_TERMINATOR), flLineTerminator);

    // init property to input map
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.SYNGERGY_HOST, flHost);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.SYNGERGY_PASSWORD, flPassword);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.SYNGERGY_PATH_TO_EXE, flPathToExe);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.MKS_PORT, flPort);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.MKS_PROJECT, flProject);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.SYNGERGY_USER, flUser);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.MKS_LINE_TERMINATOR, flLineTerminator);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.MKS_DEVELOPMENT_PATH, flDevPath);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.MKS_PROJECT_REVISION, flProjectRevision);

    // add footer
    addCommonAttributes();

    // appearance
    flProject.setAlignY(Layout.TOP);
    flPort.setValue("8888");
  }


  /**
   * Sets edit mode
   *
   * @param mode
   */
  protected final void doSetMode(final int mode) {
    if (mode == WebUIConstants.MODE_VIEW) {
      flHost.setEditable(false);
      flPort.setEditable(false);
      flProject.setEditable(false);
      flPassword.setEditable(false);
      flPathToExe.setEditable(false);
      flUser.setEditable(false);
      flLineTerminator.setEditable(false);
    } else if (mode == WebUIConstants.MODE_EDIT) {
      flHost.setEditable(true);
      flPort.setEditable(true);
      flProject.setEditable(true);
      flPassword.setEditable(true);
      flPathToExe.setEditable(true);
      flUser.setEditable(true);
      flLineTerminator.setEditable(true);
    } else if (mode == WebUIConstants.MODE_INHERITED) {
      throw new IllegalArgumentException("Not implemented yet: " + mode);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should dispaly a error message in it's area.
   *
   * @return true if valid
   */
  protected final boolean doValidate() {
    clearMessage();
    final ArrayList errors = new ArrayList();
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_PATH_TO_EXE, flPathToExe);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_PROJECT_PATH, flProject);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_USER, flUser);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_PASSWORD, flPassword);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_HOST, flHost);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_PORT, flPort);

    if (errors.isEmpty()) {
      // port
      WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_PORT, flPort);

      // validate stcmd executable exists if there were no other errors
      try {
        WebuiUtils.validateCommandExists(super.getAgentEnv(), flPathToExe.getValue(), errors,
                "Path to stcmd executable is invalid, or stcmd executable is not accessible");
      } catch (NoLiveAgentsException ignore) {
        IoUtils.ignoreExpectedException(ignore);
      } catch (IOException e) {
        errors.add("Error while checking path for stcmd client: " + StringUtils.toString(e));
      }

      // further validate StartTeam depot path
      final DepotPathParser parser = new DepotPathParser("Project path", false);
      try {
        parser.validate(flProject.getValue());
      } catch (ValidationException e) {
        errors.add(StringUtils.toString(e));
      }
    }

    // show errors
    if (errors.isEmpty()) return true;
    showErrorMessage(errors);
    return false;
  }
}
