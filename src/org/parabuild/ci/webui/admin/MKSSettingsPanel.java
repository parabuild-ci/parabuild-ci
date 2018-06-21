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
import org.parabuild.ci.versioncontrol.mks.MKSDateFormat;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.EncryptingPassword;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.DropDown;
import viewtier.ui.Field;
import viewtier.ui.Layout;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 *
 */
public final class MKSSettingsPanel extends AbstractSourceControlPanel {

  private static final long serialVersionUID = 4467119862820193870L; // NOPMD

  private static final String CAPTION_CO_DATE_FORMAT = "co format override: ";
  private static final String CAPTION_DEV_PATH = "Development path: ";
  private static final String CAPTION_HOST = "Host: ";
  private static final String CAPTION_LINE_TERMINATOR = "Line terminator: ";
  private static final String CAPTION_PASSWORD = "Password: ";
  private static final String CAPTION_PATH_TO_EXE = "Path to si executable: ";
  private static final String CAPTION_PORT = "Port: ";
  private static final String CAPTION_PROJECT_PATH = "Project: ";
  private static final String CAPTION_PROJECT_REVISION = "Project revision: ";
  private static final String CAPTION_RLOG_DATE_FORMAT = "rlog date format: ";
  private static final String CAPTION_USER = "Name: ";


  private final DropDown flLineTerminator = new MKSLineTerminatorDropDown(); // NOPMD SingularField
  private final EncryptingPassword flPassword = new EncryptingPassword(30, 20, "mks_password"); // NOPMD SingularField
  private final Field flDevPath = new Field(200, 50); // NOPMD SingularField
  private final Field flHost = new Field(100, 50); // NOPMD SingularField
  private final Field flPathToExe = new Field(200, 80); // NOPMD SingularField
  private final Field flPort = new Field(5, 5); // NOPMD SingularField
  private final Field flProject = new Field(200, 80); // NOPMD SingularField
  private final Field flProjectRevision = new Field(20, 20); // NOPMD SingularField
  private final Field flUser = new Field(20, 20); // NOPMD SingularField
  private final Field flRlogDateFormat = new Field(35, 35); // NOPMD SingularField
  private final Field flCoDateFormat = new Field(40, 40); // NOPMD SingularField


  public MKSSettingsPanel() {
    super("MKS Settings");
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
    gridIterator.addBlankLine();
    gridIterator.addPair(new CommonFieldLabel(CAPTION_RLOG_DATE_FORMAT), new RequiredFieldMarker(flRlogDateFormat));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_CO_DATE_FORMAT), flCoDateFormat);
    gridIterator.addBlankLine();

    // init property to input map
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.MKS_HOST, flHost);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.MKS_PASSWORD, flPassword);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.MKS_PATH_TO_EXE, flPathToExe);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.MKS_PORT, flPort);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.MKS_PROJECT, flProject);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.MKS_USER, flUser);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.MKS_LINE_TERMINATOR, flLineTerminator);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.MKS_DEVELOPMENT_PATH, flDevPath);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.MKS_PROJECT_REVISION, flProjectRevision);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.MKS_RLOG_DATE_FORMAT, flRlogDateFormat);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.MKS_CO_DATE_FORMAT, flCoDateFormat);

    // add footer
    addCommonAttributes();

    // appearance
    flProject.setAlignY(Layout.TOP);
    flPort.setValue("8888");
    flRlogDateFormat.setValue(MKSDateFormat.DEFAULT_OUTPUT_FORMAT);
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
      setEditable(false); // disable this
      // but enable these ones
      flPathToExe.setEditable(true);
      flLineTerminator.setEditable(true);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  private void setEditable(final boolean editable) {
    flHost.setEditable(editable);
    flPort.setEditable(editable);
    flProject.setEditable(editable);
    flPassword.setEditable(editable);
    flPathToExe.setEditable(editable);
    flUser.setEditable(editable);
    flLineTerminator.setEditable(editable);
    flRlogDateFormat.setEditable(editable);
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
    final ArrayList errors = new ArrayList(1);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_PATH_TO_EXE, flPathToExe);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_PROJECT_PATH, flProject);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_USER, flUser);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_PASSWORD, flPassword);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_HOST, flHost);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_PORT, flPort);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_RLOG_DATE_FORMAT, flRlogDateFormat);

    if (errors.isEmpty()) {
      // port
      WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_PORT, flPort);

      // validate stcmd executable exists if there were no other errors
      try {
        WebuiUtils.validateCommandExists(super.getAgentEnv(), flPathToExe.getValue(), errors,
                "Path to executable is invalid, or si executable is not accessible");
      } catch (final NoLiveAgentsException ignore) {
        IoUtils.ignoreExpectedException(ignore);
      } catch (final IOException e) {
        errors.add("Error while checking path for MKS client: " + StringUtils.toString(e));
      }

      // further validate MKS project path
      final DepotPathParser parser = new DepotPathParser("Project path", false);
      try {
        parser.validate(flProject.getValue());
      } catch (final ValidationException e) {
        errors.add(StringUtils.toString(e));
      }

      // Validate rlog format
      try {
        final SimpleDateFormat dateFormat = new SimpleDateFormat(flRlogDateFormat.getValue());
      } catch (final Exception e) {
        errors.add("Rlog date format is invalid: " + StringUtils.toString(e));
      }

      // Validate co format
      if (!WebuiUtils.isBlank(flCoDateFormat)) {
        try {
          new SimpleDateFormat(flCoDateFormat.getValue());
        } catch (final Exception e) {
          errors.add("Co date format is invalid: " + StringUtils.toString(e));
        }
      }
    }

    // show errors
    if (errors.isEmpty()) return true;
    showErrorMessage(errors);
    return false;
  }
}
