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
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.NoLiveAgentsException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.versioncontrol.GitDepotPathParser;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.EncryptingPassword;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.Field;
import viewtier.ui.Layout;
import viewtier.ui.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 */
public final class GitSettingsPanel extends AbstractSourceControlPanel {

  private static final long serialVersionUID = 0L; // NOPMD

  private static final String[] VALID_GIT_URL_PROTOCOLS = {"ssh://", "git://", "http://", "https://", "file://"};
  private static final String INVALID_PROTOCOL_MSG = makeInvalidProtocolMessage();
  public static final String DEFAULT_UNIX_GIT_COMMAND = "/usr/bin/git";

  private static final String NAME_GIT_DEPOT_PATH = "Git repository path:";
  private static final String NAME_GIT_PASSWORD = "Git password:";
  private static final String NAME_GIT_PATH_TO_EXE = "Path to git executable:";
  private static final String NAME_GIT_REPOSITORY = "Git repository:";


  // captions
  private final CommonFieldLabel lbDepotPath = new CommonFieldLabel(NAME_GIT_DEPOT_PATH); // NOPMD
  private final CommonFieldLabel lbPassword = new CommonFieldLabel(NAME_GIT_PASSWORD);

  // fields
  private final Field flPathToExe = new Field(200, 80);
  private final Field flRepository = new Field(80, 80);
  private final Field flBranch = new Field(75, 75);
  private final Field flUser = new Field(30, 30);
  private final EncryptingPassword flPassword = new EncryptingPassword(30, 20, "git_password");
  private final Text flDepotPath = new Text(60, 5);


  public GitSettingsPanel() {
    super("Git Settings");
    // layout
    gridIterator.addPair(new CommonFieldLabel(NAME_GIT_PATH_TO_EXE), new RequiredFieldMarker(flPathToExe));
    gridIterator.addPair(new CommonFieldLabel(NAME_GIT_REPOSITORY), new RequiredFieldMarker(flRepository));
    gridIterator.addPair(new CommonFieldLabel("Git branch:"), new RequiredFieldMarker(flBranch));
//    gridIterator.addPair(new CommonFieldLabel(NAME_GIT_USER), new RequiredFieldMarker(flUser));
//    gridIterator.addPair(lbPassword, flPassword);
    gridIterator.addPair(lbDepotPath, new RequiredFieldMarker(flDepotPath));

    // init property to input map
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.GIT_PASSWORD, flPassword);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.GIT_USER, flUser);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.GIT_DEPOT_PATH, flDepotPath);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.GIT_PATH_TO_EXE, flPathToExe);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.GIT_REPOSITORY, flRepository);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.GIT_BRANCH, flBranch);

    // add footer
    addCommonAttributes();

    flDepotPath.setAlignY(Layout.TOP);
    lbDepotPath.setAlignY(Layout.TOP);
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
      setEditable(false);
      flPathToExe.setEditable(true);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  private void setEditable(final boolean editable) {
    flRepository.setEditable(editable);
    flDepotPath.setEditable(editable);
    flPassword.setEditable(editable);
    flPathToExe.setEditable(editable);
    flUser.setEditable(editable);
    flBranch.setEditable(editable);
    if (!editable) {
      WebuiUtils.hideCaptionAndFieldIfBlank(lbPassword, flPassword);
    }
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   */
  protected boolean doValidate() {
    clearMessage();
    final List errors = new ArrayList(1);
    WebuiUtils.validateFieldNotBlank(errors, NAME_GIT_PATH_TO_EXE, flPathToExe);
    WebuiUtils.validateFieldNotBlank(errors, NAME_GIT_DEPOT_PATH, flDepotPath);
    WebuiUtils.validateFieldNotBlank(errors, NAME_GIT_REPOSITORY, flRepository);

    // counter name is valid
    if (!StringUtils.isBlank(flPassword.getValue())
            && StringUtils.isBlank(flUser.getValue())) {
      errors.add("User name cannot be blank if password is set.");
    }

    if (errors.isEmpty()) {
      // validate svn executable exists if there were no other errors
      try {
        WebuiUtils.validateCommandExists(super.getAgentEnv(), flPathToExe.getValue(), errors,
                "Path to git executable is invalid, or git executable is not accessible");
      } catch (final NoLiveAgentsException ignore) {
        IoUtils.ignoreExpectedException(ignore);
      } catch (final IOException e) {
        errors.add("Error while checking path for git executable: " + StringUtils.toString(e));
      }

      // validate GIT URL is valid if there were no other errors
      final String urlValue = flRepository.getValue().trim();
      boolean validProtocolFound = false;
      for (int i = 0; i < VALID_GIT_URL_PROTOCOLS.length; i++) {
        if (urlValue.startsWith(VALID_GIT_URL_PROTOCOLS[i])) {
          validProtocolFound = true;
          break;
        }
      }

      // show error if prefix is invalid
      if (!validProtocolFound) {
        errors.add(INVALID_PROTOCOL_MSG);
      }

      // further validate GIT depot path
      final GitDepotPathParser parser = new GitDepotPathParser();
      try {
        parser.validate(flDepotPath.getValue());
      } catch (final ValidationException e) {
        errors.add(StringUtils.toString(e));
      }
    }

    // show errors
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
        if (be.isUnix() && be.commandIsAvailable(DEFAULT_UNIX_GIT_COMMAND)) {
          flPathToExe.setValue(DEFAULT_UNIX_GIT_COMMAND);
        }
      } catch (final Exception e) {
        IoUtils.ignoreExpectedException(e);
      }
    }
  }


  /**
   * Helper method to create invalid protocol message.
   */
  private static String makeInvalidProtocolMessage() {
    final StringBuilder invalidPrefixMsg = new StringBuilder("Git URL should start with one of the following: ");
    for (int i = 0; i < VALID_GIT_URL_PROTOCOLS.length; i++) {
      invalidPrefixMsg.append('\"').append(VALID_GIT_URL_PROTOCOLS[i]).append('\"');
      if (i < VALID_GIT_URL_PROTOCOLS.length - 1) {
        invalidPrefixMsg.append(", ");
      } else {
        invalidPrefixMsg.append('.');
      }
    }
    return invalidPrefixMsg.toString();
  }


  /**
   * Returns path to Git exe.
   */
  public String getPathToGitExe() {
    return flPathToExe.getValue();
  }


  public String toString() {
    return "GITSettingsPanel{" +
            "lbDepotPath=" + lbDepotPath +
            ", lbPassword=" + lbPassword +
            ", flPathToExe=" + flPathToExe +
            ", flURL=" + flRepository +
            ", flUser=" + flUser +
            ", flPassword=" + flPassword +
            ", flDepotPath=" + flDepotPath +
            ", flBranch=" + flBranch +
            '}';
  }
}
