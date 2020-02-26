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

import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.util.ValidationException;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.NoLiveAgentsException;
import org.parabuild.ci.versioncontrol.StarTeamProjectListParser;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.EncryptingPassword;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.DropDown;
import viewtier.ui.Field;
import viewtier.ui.Layout;
import viewtier.ui.Text;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 */
public final class StarTeamSettingsPanel extends AbstractSourceControlPanel {

  private static final long serialVersionUID = 4467119862820193870L; // NOPMD

  public static final String CAPTION_ENCRYPTION = "Encryption:";
  public static final String CAPTION_HOST = "Server address:";
  public static final String CAPTION_PASSWORD = "Password:";
  public static final String CAPTION_PATH_TO_EXE = "Path to stcmd executable:";
  public static final String CAPTION_PORT = "TCP/IP endpoint:";
  public static final String CAPTION_PROJECT_PATH = "Project path:";
  public static final String CAPTION_USER = "User:";
  public static final String CAPTION_EOL_CONVERSION = "End-of-line conversion";

  private final EncryptingPassword flPassword = new EncryptingPassword(30, 20, "starteam_password");
  private final Field flHost = new Field(100, 50);
  private final Field flPathToExe = new Field(200, 50);
  private final Field flPort = new Field(5, 5);
  private final Field flUser = new Field(20, 20);
  private final DropDown flEncryption = new StarTeamEncriptionDropDown();
  private final DropDown flEOLConversion = new StarTeamEndOfLineDropDown();
  private final Text flProjectPath = new Text(100, 5);


  public StarTeamSettingsPanel() {
    super("StartTeam Settings");
    // layout
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PATH_TO_EXE), new RequiredFieldMarker(flPathToExe));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_HOST), new RequiredFieldMarker(flHost));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PORT), new RequiredFieldMarker(flPort));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_ENCRYPTION), flEncryption);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_USER), new RequiredFieldMarker(flUser));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PASSWORD), new RequiredFieldMarker(flPassword));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_EOL_CONVERSION), flEOLConversion);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_PROJECT_PATH), new RequiredFieldMarker(flProjectPath));

    // init property to input map
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.STARTEAM_ENCRIPTION, flEncryption);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.STARTEAM_HOST, flHost);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.STARTEAM_PASSWORD, flPassword);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.STARTEAM_PATH_TO_EXE, flPathToExe);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.STARTEAM_PORT, flPort);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.STARTEAM_PROJECT_PATH, flProjectPath);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.STARTEAM_USER, flUser);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.STARTEAM_EOL_CONVERSION, flEOLConversion);

    // add footer
    addCommonAttributes();

    // appearance
    flProjectPath.setAlignY(Layout.TOP);
    flPort.setValue("49201");
  }


  /**
   * Sets edit mode
   *
   * @param mode
   */
  protected final void doSetMode(final int mode) {
    if (mode == WebUIConstants.MODE_VIEW) {
      setEditable(false);
    } else if (mode == WebUIConstants.MODE_EDIT) {
      setEditable(true);
    } else if (mode == WebUIConstants.MODE_INHERITED) {
      setEditable(false);
      flPathToExe.setEditable(true);
      flEOLConversion.setEditable(true);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  private void setEditable(final boolean editable) {
    flHost.setEditable(editable);
    flPort.setEditable(editable);
    flProjectPath.setEditable(editable);
    flPassword.setEditable(editable);
    flPathToExe.setEditable(editable);
    flUser.setEditable(editable);
    flEncryption.setEditable(editable);
    flEOLConversion.setEditable(editable);
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  protected final boolean doValidate() {
    clearMessage();
    final ArrayList errors = new ArrayList(11);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_PATH_TO_EXE, flPathToExe);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_PROJECT_PATH, flProjectPath);
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
      } catch (final NoLiveAgentsException ignore) {
        IoUtils.ignoreExpectedException(ignore);
      } catch (final IOException e) {
        errors.add("Error while checking path for stcmd client: " + StringUtils.toString(e));
      }

      // further validate StartTeam depot path
      final StarTeamProjectListParser parser = new StarTeamProjectListParser();
      try {
        parser.validate(flProjectPath.getValue());
      } catch (final ValidationException e) {
        errors.add(StringUtils.toString(e));
      }
    }

    // show errors
    if (errors.isEmpty()) return true;
    showErrorMessage(errors);
    return false;
  }
}
