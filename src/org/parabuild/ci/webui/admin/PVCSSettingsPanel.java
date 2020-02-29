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

import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.remote.NoLiveAgentsException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.EncryptingPassword;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Field;
import viewtier.ui.Layout;
import viewtier.ui.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * PVCS setting panel
 */
public final class PVCSSettingsPanel extends AbstractSourceControlPanel {

  private static final long serialVersionUID = 7269513984227746767L; // NOPMD

  public static final String NAME_BRANCH_NAME = "Branch label: ";
  public static final String NAME_CHANGE_WINDOW = "Change window, secs: ";
  public static final String NAME_REPOSITORY = "Repository: ";
  public static final String NAME_PASSWORD = "Password: ";
  public static final String NAME_PROJECTS = "Projects: ";
  public static final String NAME_USER = "User: ";
  public static final String NAME_PCLI_EXE_PATH = "Path to PVCS client (pcli.exe): ";
  public static final String NAME_LABEL = "Label: ";
  public static final String NAME_PROMOTION_GROUP = "Promotion group: ";

  private final Field fldPathToPVCSClient = new CommonField(200, 60);
  private final Field fldRepository = new CommonField(200, 60);
  private final Text fldProject = new Text(60, 3);
  private final Field fldBranch = new CommonField(60, 60);
  private final EncryptingPassword fldPassword = new EncryptingPassword(30, 30, "pvcs_password");
  private final Field fldUser = new Field(30, 30);
  private final Field fldChangeWindow = new Field(2, 3);
  private final Field fldLabel = new Field(60, 60);
  private final Field fldPromotionGroup = new Field(60, 60);

//  private final Label lbLabel = new CommonFieldLabel(NAME_LABEL);


  public PVCSSettingsPanel() {
    super("PVCS Settings");
    // layout
    gridIterator.addPair(new CommonFieldLabel(NAME_PCLI_EXE_PATH), new RequiredFieldMarker(fldPathToPVCSClient));
    gridIterator.addPair(new CommonFieldLabel(NAME_REPOSITORY), new RequiredFieldMarker(fldRepository));
    gridIterator.addPair(new CommonFieldLabel(NAME_PROJECTS), new RequiredFieldMarker(fldProject));
    gridIterator.addPair(new CommonFieldLabel(NAME_USER), fldUser);
    gridIterator.addPair(new CommonFieldLabel(NAME_PASSWORD), fldPassword);
    gridIterator.addPair(new CommonFieldLabel(NAME_BRANCH_NAME), fldBranch);
    gridIterator.addPair(new CommonFieldLabel(NAME_PROMOTION_GROUP), fldPromotionGroup);
    gridIterator.addPair(new CommonFieldLabel(NAME_CHANGE_WINDOW), new RequiredFieldMarker(fldChangeWindow));
    // REVIEWME: Uncomment or delete when it's clear if we need these items for PVCS
//    gridIterator.addPair(lbLabel, fldLabel);
    new CommonFieldLabel(NAME_PROJECTS).setAlignY(Layout.TOP);

    // init property to input map
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.PVCS_PASSWORD, fldPassword);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.PVCS_USER, fldUser);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.PVCS_REPOSITORY, fldRepository);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.PVCS_PROJECT, fldProject);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.PVCS_EXE_PATH, fldPathToPVCSClient);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.PVCS_BRANCH_NAME, fldBranch);
//    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.PVCS_LABEL, fldLabel);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.PVCS_PROMOTION_GROUP, fldLabel);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.PVCS_CHANGE_WINDOW, fldChangeWindow);

    // add footer
    addCommonAttributes();

    // defaults
    fldChangeWindow.setValue(Integer.toString(30));
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
      fldPathToPVCSClient.setEditable(true);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  private void setEditable(final boolean editable) {
    fldBranch.setEditable(editable);
    fldChangeWindow.setEditable(editable);
    fldRepository.setEditable(editable);
    fldPassword.setEditable(editable);
    fldPathToPVCSClient.setEditable(editable);
    fldProject.setEditable(editable);
    fldUser.setEditable(editable);
    fldLabel.setEditable(editable);
    fldPromotionGroup.setEditable(editable);
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
    WebuiUtils.validateFieldNotBlank(errors, NAME_PCLI_EXE_PATH, fldPathToPVCSClient);
    WebuiUtils.validateFieldNotBlank(errors, NAME_REPOSITORY, fldRepository);
    WebuiUtils.validateFieldNotBlank(errors, NAME_PROJECTS, fldProject);
//    WebuiUtils.validateFieldNotBlank(errors, NAME_CHANGE_WINDOW, fldChangeWindow);
//    WebuiUtils.validateFieldNotBlank(errors, NAME_USER, fldUser);
//    WebuiUtils.validateFieldNotBlank(errors, NAME_PASSWORD, fldPassword);
//    WebuiUtils.validateFieldValidNonNegativeInteger(errors, NAME_CHANGE_WINDOW, fldChangeWindow);

    if (!StringUtils.isBlank(fldUser.getValue()) || !StringUtils.isBlank(fldPassword.getValue())) {
      if (StringUtils.isBlank(fldUser.getValue()) || StringUtils.isBlank(fldPassword.getValue())) {
        errors.add("Both user and password should be set");
      }
    }

    // validate PVCS client exists if there were no other errors
    if (errors.isEmpty()) {
      try {
        WebuiUtils.validateCommandExists(super.getAgentEnv(), fldPathToPVCSClient.getValue(), errors,
                "Path to PVCS client is invalid, or PVCS client is not accessible");
      } catch (final NoLiveAgentsException ignore) {
        IoUtils.ignoreExpectedException(ignore);
      } catch (final IOException e) {
        errors.add("Error while checking path for PVCS client: " + StringUtils.toString(e));
      }
    }

    // project path
    final List lines = StringUtils.multilineStringToList(fldProject.getValue());
    if (lines.isEmpty()) {
      errors.add(NAME_PROJECTS + " does not contain valid PVCS project path(s). A valid path begins with \"/\"");
    } else {
      for (final Iterator i = lines.iterator(); i.hasNext();) {
        final String s = (String) i.next();
        if (!StringUtils.isBlank(s) && !(s.charAt(0) == '/')) {
          errors.add(NAME_PROJECTS + " lines should begin with \"/\"");
        }
      }
    }

    // branch name is valid
    if (!StringUtils.isBlank(fldBranch.getValue())
            && !StringUtils.isValidStrictName(fldBranch.getValue())) {
      errors.add("Branch name can contain only alphanumeric characters, \"-\" and \"_\"");
    }

    // show error if there are any
    if (errors.isEmpty()) return true;
    showErrorMessage(errors);
    return false;
  }
}
