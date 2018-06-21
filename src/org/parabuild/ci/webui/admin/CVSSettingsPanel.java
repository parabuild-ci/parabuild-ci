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
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.NoLiveAgentsException;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.EncryptingPassword;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.CheckBox;
import viewtier.ui.DropDown;
import viewtier.ui.Field;
import viewtier.ui.Label;
import viewtier.ui.Layout;
import viewtier.ui.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public final class CVSSettingsPanel extends AbstractSourceControlPanel {

  private static final long serialVersionUID = 7269513984227746767L; // NOPMD

  public static final String DEFAULT_UNIX_CVS_COMMAND = "/usr/bin/cvs";
  public static final String NAME_BRANCH = "Branch name:";
  public static final String NAME_CHANGE_PRECHECK = "Change pre-check:";
  public static final String NAME_CHANGE_WINDOW = "Change window, secs:";
  public static final String NAME_CLIENT_PATH = "Path to cvs executable:";
  public static final String NAME_EXT_RSH_PATH = "Path to extenal rsh:";
  public static final String NAME_PASSWORD = "CVS password:";
  public static final String NAME_REPOSITORY = "CVS repository path:";
  public static final String NAME_ROOT = "CVS root:";
  public static final String NAME_COMPRESSION = "Compression level:";
  public static final String NAME_REL_BUILD_DIR = "Custom relative build dir: ";
  private static final String CAPTION_FAST_CHANGE_DETECTION = "Fast change detection: ";

  private final CheckBox fldChangePreCheck = new CheckBox();
  private final CheckBox flFastChangeDetection = new CheckBox(); // NOPMD
  private final DropDown fldCompression = new CompressionDropDown();
  private final Field fldBranch = new CommonField("cvs-barnch", 60, 60);
  private final Field fldChangeWindow = new Field(2, 3, "cvs-change-window");
  private final Field fldPathToCVSClient = new CommonField("cvs-path-to-client", 200, 60);
  private final Field fldPathToExternalRsh = new CommonField("cvs-path-to-external-rsh", 200, 60);
  private final Field fldRoot = new CommonField("cvs-root", 200, 80);
  private final EncryptingPassword fldPassword = new EncryptingPassword(30, 20, "cvs-password");
  private final Text fldCVSPath = new Text(52, 3);
  private final Field flRelativeBuildDir = new Field(120, 60, "cvs-relative-build-dir");
  private final Label lbRepository = new CommonFieldLabel(NAME_REPOSITORY); // NOPMD
  private final CommonFieldLabel lbRelativeBuildDir = new CommonFieldLabel(NAME_REL_BUILD_DIR);
  private final CommonFieldLabel lbPassword = new CommonFieldLabel(NAME_PASSWORD);
  private final CommonFieldLabel lbPathToExternalRsh = new CommonFieldLabel(NAME_EXT_RSH_PATH);
  private final CommonFieldLabel lbBranch = new CommonFieldLabel(NAME_BRANCH);
  private final CommonFieldLabel lbChangePreCheck = new CommonFieldLabel(NAME_CHANGE_PRECHECK);
  private final CommonFieldLabel lbFastChangeDetection = new CommonFieldLabel(CAPTION_FAST_CHANGE_DETECTION); // NOPMD


  public CVSSettingsPanel() {
    super("CVS Settings");
    // layout
    gridIterator.addPair(new CommonFieldLabel(NAME_CLIENT_PATH), new RequiredFieldMarker(fldPathToCVSClient));
    gridIterator.addPair(new CommonFieldLabel(NAME_ROOT), new RequiredFieldMarker(fldRoot));

    gridIterator.addPair(lbRepository, new RequiredFieldMarker(fldCVSPath));
    if (SystemConfigurationManagerFactory.getManager().isAdvancedConfigurationMode()) {
      gridIterator.addPair(lbRelativeBuildDir, flRelativeBuildDir);
    }
    gridIterator.addPair(lbPassword, fldPassword);
    gridIterator.addPair(lbPathToExternalRsh, fldPathToExternalRsh);
    gridIterator.addPair(lbBranch, fldBranch);
    gridIterator.addPair(new CommonFieldLabel(NAME_CHANGE_WINDOW), new RequiredFieldMarker(fldChangeWindow));
    gridIterator.addPair(lbChangePreCheck, fldChangePreCheck);
    gridIterator.addPair(lbFastChangeDetection, flFastChangeDetection);
    gridIterator.addPair(new CommonFieldLabel(NAME_COMPRESSION), fldCompression);
    lbRepository.setAlignY(Layout.TOP);

    // init property to input map
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.CVS_PASSWORD, fldPassword);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.CVS_PATH_TO_RSH, fldPathToExternalRsh);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.CVS_ROOT, fldRoot);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.CVS_REPOSITORY_PATH, fldCVSPath);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.CVS_PATH_TO_CLIENT, fldPathToCVSClient);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.CVS_BRANCH_NAME, fldBranch);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.CVS_CHANGE_WINDOW, fldChangeWindow);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.CVS_CHANGE_PRECHECK, fldChangePreCheck);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.CVS_COMPRESSION, fldCompression);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.CVS_CUSTOM_RELATIVE_BUILD_DIR, flRelativeBuildDir);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.CVS_SUPPRESS_LOG_OUTPUT_IF_NO_CHANGES, flFastChangeDetection);

    // add footer
    addCommonAttributes();

    // defaults
    fldChangeWindow.setValue(Integer.toString(30));
    flFastChangeDetection.setChecked(true);
  }


  /**
   * Retuns path to CVS client
   */
  public String getPathToCVSClient() {
    return fldPathToCVSClient.getValue();
  }


  /**
   * Sets edit mode
   *
   * @param mode
   */
  protected void doSetMode(final int mode) {
    if (mode == (int) WebUIConstants.MODE_VIEW) {
      setEditable(false);
    } else if (mode == (int) WebUIConstants.MODE_EDIT) {
      setEditable(true);
    } else if (mode == (int) WebUIConstants.MODE_INHERITED) {
      setEditable(false); // disable all
      fldPathToExternalRsh.setEditable(true);
      fldPathToCVSClient.setEditable(true);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  private void setEditable(final boolean editable) {
    fldPathToExternalRsh.setEditable(editable);
    fldPathToCVSClient.setEditable(editable);
    fldRoot.setEditable(editable);
    fldCVSPath.setEditable(editable);
    fldPassword.setEditable(editable);
    fldChangeWindow.setEditable(editable);
    fldChangePreCheck.setEditable(editable);
    fldBranch.setEditable(editable);
    fldCompression.setEditable(editable);
    flRelativeBuildDir.setEditable(editable);
    flFastChangeDetection.setEditable(editable);
    if (!editable) {
      WebuiUtils.hideCaptionAndFieldIfBlank(lbBranch, fldBranch);
      WebuiUtils.hideCaptionAndFieldIfBlank(lbPassword, fldPassword);
      WebuiUtils.hideCaptionAndFieldIfBlank(lbPathToExternalRsh, fldPathToExternalRsh);
      WebuiUtils.hideCaptionAndFieldIfBlank(lbRelativeBuildDir, flRelativeBuildDir);
      WebuiUtils.hideCaptionAndFieldIfBlank(lbChangePreCheck, fldChangePreCheck);
    }
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should dispaly a error message in it's area.
   *
   * @return true if valid
   * @noinspection UnusedCatchParameter
   */
  protected boolean doValidate() {
    clearMessage();
    final List errors = new ArrayList(1);

    // validate fields are not blank
    WebuiUtils.validateFieldNotBlank(errors, "CVS root", fldRoot);
    WebuiUtils.validateFieldNotBlank(errors, "CVS path", fldCVSPath);
    WebuiUtils.validateFieldNotBlank(errors, "Path to CVS client", fldPathToCVSClient);
    WebuiUtils.validateFieldNotBlank(errors, NAME_CHANGE_WINDOW, fldChangeWindow);

    // validate CVS client exists if there were no other errors
    if (errors.isEmpty()) {
      try {
        WebuiUtils.validateCommandExists(super.getAgentEnv(), fldPathToCVSClient.getValue(), errors,
                "Path to CVS client is invalid, or CVS client is not accessible");
      } catch (final NoLiveAgentsException ignore) {
        IoUtils.ignoreExpectedException(ignore);
      } catch (final IOException e) {
        errors.add("Error while checking path for CVS client: " + StringUtils.toString(e));
      }
    }

    // branch name is valid
    if (!StringUtils.isBlank(fldBranch.getValue())) {
      WebuiUtils.validateFieldStrict(errors, NAME_BRANCH, fldBranch);
    }

    try {
      Integer.parseInt(fldChangeWindow.getValue());
    } catch (final NumberFormatException e) {
      errors.add("Change window should be an integer. ");
    }

    // relative build path
    //
    // REVIEWME: simeshev@parabuilci.org -> currently the env is set at the panel creation.
    // If it changes, we will faild validation. Consider providing
    // dynamic way to get env in the panel.
    //
    //if (advancedSelected && !StringUtils.isBlank(flRelativeBuildDir.getValue())) {
    //  try {
    //    if (agentEnv != null && agentEnv.isAbsoluteFile(flRelativeBuildDir.getValue())) {
    //      errors.add("Path is not relative though it should be: " + flRelativeBuildDir.getValue());
    //    }
    //  } catch (IOException e) {
    //    // log error but don't break.
    //    final Error error = new Error(buildID, "", Error.ERROR_SUSBSYSTEM_WEBUI, e);
    //    error.setDescription("Could nor validate if the path is a relative path: " + StringUtils.toString(e));
    //    ErrorManagerFactory.getErrorManager().reportSystemError(error);
    //  }
    //}

    // show error if there are any
    if (errors.isEmpty()) {
      return true;
    }
    showErrorMessage(errors);
    return false;
  }


  /**
   * Sets up defaults based on provided build config.
   *
   * @param buildConfig to use to sets up defaults.
   */
  public void setUpDefaults(final BuildConfig buildConfig) {
    if (buildConfig.getBuildID() == BuildConfig.UNSAVED_ID) {
      try {
        final AgentEnvironment be = getAgentEnv();
        if (be.isUnix() && be.commandIsAvailable(DEFAULT_UNIX_CVS_COMMAND)) {
          fldPathToCVSClient.setValue(DEFAULT_UNIX_CVS_COMMAND);
        }
      } catch (final Exception e) {
        IoUtils.ignoreExpectedException(e);
      }
    }
  }


  /**
   * A dropdown to display CVS compression levels.
   */
  private static final class CompressionDropDown extends DropDown {

    /**
     * Default constructor.
     */
    CompressionDropDown() {
      for (int i = 0; i < 10; i++) {
        addItem(Integer.toString(i) + "  ");
      }
      setSelection(0);
    }
  }


  public String toString() {
    return "CVSSettingsPanel{" +
            "fldChangePreCheck=" + fldChangePreCheck +
            ", flFastChangeDetection=" + flFastChangeDetection +
            ", fldCompression=" + fldCompression +
            ", fldBranch=" + fldBranch +
            ", fldChangeWindow=" + fldChangeWindow +
            ", fldPathToCVSClient=" + fldPathToCVSClient +
            ", fldPathToExternalRsh=" + fldPathToExternalRsh +
            ", fldRoot=" + fldRoot +
            ", fldPassword=" + fldPassword +
            ", fldCVSPath=" + fldCVSPath +
            ", flRelativeBuildDir=" + flRelativeBuildDir +
            ", lbRepository=" + lbRepository +
            ", lbRelativeBuildDir=" + lbRelativeBuildDir +
            ", lbPassword=" + lbPassword +
            ", lbPathToExternalRsh=" + lbPathToExternalRsh +
            ", lbBranch=" + lbBranch +
            ", lbChangePreCheck=" + lbChangePreCheck +
            ", lbFastChangeDetection=" + lbFastChangeDetection +
            '}';
  }
}
