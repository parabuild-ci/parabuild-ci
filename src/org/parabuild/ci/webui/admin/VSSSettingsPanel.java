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
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.NoLiveAgentsException;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.EncryptingPassword;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.CheckBox;
import viewtier.ui.Field;
import viewtier.ui.Layout;
import viewtier.ui.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * VSS setting panel
 */
public final class VSSSettingsPanel extends AbstractSourceControlPanel {

  private static final long serialVersionUID = 7269513984227746767L; // NOPMD

  private static final String NAME_BRANCH_NAME = "Branch name: ";
  private static final String NAME_CHANGE_WINDOW = "Change window, secs: ";
  private static final String NAME_DATABASE_PATH = "VSS database path: ";
  private static final String NAME_PASSWORD = "Password: ";
  private static final String NAME_PROJECT_PATH = "Project path: ";
  private static final String NAME_READ_ONLY_CHECKOUT = "Read-only checkout:";
  private static final String NAME_USER = "User: ";
  private static final String NAME_VSS_EXE_PATH = "Path to VSS client (SS.EXE): ";

  private final Field fldPathToVSSClient = new CommonField(200, 60);
  private final Field fldDatabasePath = new CommonField(200, 60);
  private final Text fldProjectPath = new Text(52, 3);
  private final Field fldBranch = new CommonField(60, 60);
  private final EncryptingPassword fldPassword = new EncryptingPassword(30, 20, "vss_password");
  private final Field fldUser = new Field(15, 15);
  private final Field fldChangeWindow = new Field(2, 3);
  private final CheckBox fldReadOnlyCheckout = new CheckBox();

//  private final Label lbBranch = new CommonFieldLabel(NAME_BRANCH_NAME);
  //  private final Label lbChangeWindow = new CommonFieldLabel(NAME_CHANGE_WINDOW);
  final CommonFieldLabel lbProjectPath = new CommonFieldLabel(NAME_PROJECT_PATH); // NOPMD


  public VSSSettingsPanel() {
    super("Visual SourceSafe Settings");
    // layout
    gridIterator.addPair(new CommonFieldLabel(NAME_VSS_EXE_PATH), new RequiredFieldMarker(fldPathToVSSClient));
    gridIterator.addPair(new CommonFieldLabel(NAME_DATABASE_PATH), new RequiredFieldMarker(fldDatabasePath));
    gridIterator.addPair(lbProjectPath, new RequiredFieldMarker(fldProjectPath));
    gridIterator.addPair(new CommonFieldLabel(NAME_READ_ONLY_CHECKOUT), fldReadOnlyCheckout);
    gridIterator.addPair(new CommonFieldLabel(NAME_USER), new RequiredFieldMarker(fldUser));
    gridIterator.addPair(new CommonFieldLabel(NAME_PASSWORD), new RequiredFieldMarker(fldPassword));
// REVIEWME: Uncomment or delete when it's clear if we need these items for VSS
//    gridIterator.addPair(lbBranch, fldBranch);
//    gridIterator.addPair(changeWindowLabel, new RequiredFieldMarker(changeWindowField));
    lbProjectPath.setAlignY(Layout.TOP);

    // init property to input map
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VSS_PASSWORD, fldPassword);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VSS_USER, fldUser);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VSS_DATABASE_PATH, fldDatabasePath);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VSS_PROJECT_PATH, fldProjectPath);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VSS_EXE_PATH, fldPathToVSSClient);
//    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VSS_BRANCH_NAME, fldBranch);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VSS_CHANGE_WINDOW, fldChangeWindow);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.VSS_READONLY_CHECKOUT, fldReadOnlyCheckout);

    // defaults
    fldChangeWindow.setValue(Integer.toString(30));
    fldReadOnlyCheckout.setChecked(true);

    // add footer
    addCommonAttributes();
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
      fldPathToVSSClient.setEditable(true);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  private void setEditable(final boolean editable) {
    fldBranch.setEditable(editable);
    fldChangeWindow.setEditable(editable);
    fldDatabasePath.setEditable(editable);
    fldPassword.setEditable(editable);
    fldPathToVSSClient.setEditable(editable);
    fldProjectPath.setEditable(editable);
    fldUser.setEditable(editable);
    fldReadOnlyCheckout.setEditable(editable);
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

    // validate fields are not blank
    WebuiUtils.validateFieldNotBlank(errors, NAME_VSS_EXE_PATH, fldPathToVSSClient);
    WebuiUtils.validateFieldNotBlank(errors, NAME_DATABASE_PATH, fldDatabasePath);
    WebuiUtils.validateFieldNotBlank(errors, NAME_PROJECT_PATH, fldProjectPath);
    WebuiUtils.validateFieldNotBlank(errors, NAME_CHANGE_WINDOW, fldChangeWindow);
    WebuiUtils.validateFieldNotBlank(errors, NAME_USER, fldUser);
    WebuiUtils.validateFieldNotBlank(errors, NAME_PASSWORD, fldPassword);
    WebuiUtils.validateFieldValidNonNegativeInteger(errors, NAME_CHANGE_WINDOW, fldChangeWindow);

    // validate VSS client exists if there were no other errors
    if (errors.isEmpty()) {
      try {
        WebuiUtils.validateCommandExists(super.getAgentEnv(), fldPathToVSSClient.getValue(), errors,
                "Path to VSS client is invalid, or VSS client is not accessible");
      } catch (final NoLiveAgentsException ignore) {
        IoUtils.ignoreExpectedException(ignore);
      } catch (final IOException e) {
        errors.add("Error while checking path for VSS client: " + StringUtils.toString(e));
      }
    }

    // project path
    final List lines = StringUtils.multilineStringToList(fldProjectPath.getValue());
    if (lines.isEmpty()) {
      errors.add(NAME_PROJECT_PATH + " does not contain valid VSS project path(s). A valid path begins with \"$/\"");
    } else {
      for (final Iterator i = lines.iterator(); i.hasNext();) {
        final String s = (String) i.next();
        if (!StringUtils.isBlank(s) && !(s.startsWith("$/") || s.startsWith("$\\"))) {
          errors.add(NAME_PROJECT_PATH + " lines should begin with \"$/\"");
        }
      }
    }

    // branch name is valid
    if (!StringUtils.isBlank(fldBranch.getValue())) {
      WebuiUtils.validateFieldStrict(errors, NAME_BRANCH_NAME, fldBranch);
    }

    // show error if there are any
    if (errors.isEmpty()) return true;
    showErrorMessage(errors);
    return false;
  }
}
