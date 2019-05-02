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
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.NoLiveAgentsException;
import org.parabuild.ci.versioncontrol.SurroundRepositoryPathParser;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.EncryptingPassword;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Field;

import java.io.IOException;
import java.util.ArrayList;

/**
 *
 */
public final class SurroundSettingsPanel extends AbstractSourceControlPanel {

  private static final long serialVersionUID = 4467119862820193870L; // NOPMD

  private static final String DEFAULT_UNIX_SURROUND_COMMAND = "";

  public static final String NAME_SURROUND_BRANCH = "Surround branch name:";
  public static final String NAME_SURROUND_PASSWORD = "Surround password:";
  public static final String NAME_SURROUND_PATH_TO_EXE = "Path to sscm executable:";
  public static final String NAME_SURROUND_HOST = "Surround server address:";
  public static final String NAME_SURROUND_PORT = "Surround server port:";
  public static final String NAME_SURROUND_USER = "Surround username:";
  public static final String NAME_SURROUND_REPOSITORY = "Surround repository:";


  private final Field flPathToExe = new Field(200, 50);
  private final Field flHost = new Field(100, 50);
  private final Field flPort = new Field(5, 5);
  private final Field flUser = new Field(20, 20);
  private final EncryptingPassword flPassword = new EncryptingPassword(30, 20, "surround_password");
  private final Field flBranch = new Field(100, 50);
  private final Field flRepository = new Field(100, 50);


  public SurroundSettingsPanel() {
    super("Surround Settings");
    // layout
    gridIterator.addPair(new CommonFieldLabel(NAME_SURROUND_PATH_TO_EXE), new RequiredFieldMarker(flPathToExe));
    gridIterator.addPair(new CommonFieldLabel(NAME_SURROUND_HOST), new RequiredFieldMarker(flHost));
    gridIterator.addPair(new CommonFieldLabel(NAME_SURROUND_PORT), new RequiredFieldMarker(flPort));
    gridIterator.addPair(new CommonFieldLabel(NAME_SURROUND_USER), new RequiredFieldMarker(flUser));
    gridIterator.addPair(new CommonFieldLabel(NAME_SURROUND_PASSWORD), flPassword);
    gridIterator.addPair(new CommonFieldLabel(NAME_SURROUND_BRANCH), new RequiredFieldMarker(flBranch));
    gridIterator.addPair(new CommonFieldLabel(NAME_SURROUND_REPOSITORY), new RequiredFieldMarker(flRepository));

    // init property to input map
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.SURROUND_PASSWORD, flPassword);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.SURROUND_USER, flUser);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.SURROUND_BRANCH, flBranch);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.SURROUND_PATH_TO_EXE, flPathToExe);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.SURROUND_HOST, flHost);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.SURROUND_PORT, flPort);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.SURROUND_REPOSITORY, flRepository);

    // add footer
    addCommonAttributes();

    // set defaults
    flPort.setValue("4900");
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
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  private void setEditable(final boolean editable) {
    flHost.setEditable(editable);
    flPort.setEditable(editable);
    flBranch.setEditable(editable);
    flPassword.setEditable(editable);
    flPathToExe.setEditable(editable);
    flUser.setEditable(editable);
    flRepository.setEditable(editable);
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
    WebuiUtils.validateFieldNotBlank(errors, NAME_SURROUND_PATH_TO_EXE, flPathToExe);
    WebuiUtils.validateFieldNotBlank(errors, NAME_SURROUND_BRANCH, flBranch);
    WebuiUtils.validateFieldNotBlank(errors, NAME_SURROUND_REPOSITORY, flRepository);
    WebuiUtils.validateFieldNotBlank(errors, NAME_SURROUND_HOST, flHost);
    WebuiUtils.validateFieldNotBlank(errors, NAME_SURROUND_PORT, flPort);
    WebuiUtils.validateFieldNotBlank(errors, NAME_SURROUND_USER, flUser);

    if (errors.isEmpty()) {
      // validate svn executable exists if there were no other errors
      try {
        WebuiUtils.validateCommandExists(super.getAgentEnv(), flPathToExe.getValue(), errors,
                "Path to sscm executable is invalid, or sscm executable is not accessible");
      } catch (final NoLiveAgentsException ignore) {
        IoUtils.ignoreExpectedException(ignore);
      } catch (final IOException e) {
        errors.add("Error while checking path for sscm client: " + StringUtils.toString(e));
      }

      // further validate Surround repository path
      final SurroundRepositoryPathParser parser = new SurroundRepositoryPathParser();
      try {
        parser.validate(flRepository.getValue());
      } catch (final ValidationException e) {
        errors.add(StringUtils.toString(e));
      }
    }

    // show errors
    if (errors.isEmpty()) return true;
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
        if (be.isUnix() && be.commandIsAvailable(DEFAULT_UNIX_SURROUND_COMMAND)) {
          flPathToExe.setValue(DEFAULT_UNIX_SURROUND_COMMAND);
        }
      } catch (final Exception e) {
        IoUtils.ignoreExpectedException(e);
      }
    }
  }


  /**
   * Returns path to SVN exe.
   */
  public String getPathToSSCMExe() {
    return flPathToExe.getValue();
  }
}
