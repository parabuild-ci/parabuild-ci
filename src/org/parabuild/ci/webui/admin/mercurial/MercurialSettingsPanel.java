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
package org.parabuild.ci.webui.admin.mercurial;

import org.parabuild.ci.common.InputValidator;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.common.WebUIConstants;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.NoLiveAgentsException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.webui.admin.AbstractSourceControlPanel;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebuiUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @noinspection FieldCanBeLocal,UnusedDeclaration
 */
public final class MercurialSettingsPanel extends AbstractSourceControlPanel {

  private static final long serialVersionUID = 0L; // NOPMD

  private static final String[] VALID_MERCURIAL_URL_PROTOCOLS = {"file://", "http://", "https://", "ssh://" };
  private static final String INVALID_PROTOCOL_MSG = makeInvalidProtocolMessage();
  public static final String DEFAULT_UNIX_MERCURIAL_COMMAND = "/usr/local/bin/hg";

  private static final String CAPTION_MERCURIAL_PATH_TO_EXE = "Path to hg executable:";
  private static final String CAPTION_REPOSITORY_URL = "Repository URL: ";
  private static final String CAPTION_BRANCH = "Branch: ";

  // captions

  private final CommonField flPathToExe = new CommonField(200, 60);
  private final CommonField flURL = new CommonField(160, 70);
  private final CommonField flBranch = new CommonField(60, 60);


  public MercurialSettingsPanel() {
    super("Mercurial Settings");

    // layout
    gridIterator.addPair(new CommonFieldLabel(CAPTION_MERCURIAL_PATH_TO_EXE), new RequiredFieldMarker(flPathToExe));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_REPOSITORY_URL), new RequiredFieldMarker(flURL));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_BRANCH), flBranch);

    // init property to input map
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.MERCURIAL_EXE_PATH, flPathToExe);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.MERCURIAL_URL, flURL);
    propertyToInputMap.bindPropertyNameToInput(VersionControlSystem.MERCURIAL_BRANCH, flBranch);

    // add footer
    addCommonAttributes();

  }


  /**
   * Sets edit mode
   *
   * @param mode edit mode
   * @see AbstractSourceControlPanel#setMode(int)
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
    flPathToExe.setEditable(editable);
    flURL.setEditable(editable);
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
    InputValidator.validateFieldNotBlank(errors, CAPTION_MERCURIAL_PATH_TO_EXE, flPathToExe);

    if (errors.isEmpty()) {
      // validate svn executable exists if there were no other errors
      try {
        WebuiUtils.validateCommandExists(super.getAgentEnv(), flPathToExe.getValue(), errors,
                "Path to bazaar executable is invalid, or bazaar executable is not accessible");
      } catch (final NoLiveAgentsException ignore) {
        IoUtils.ignoreExpectedException(ignore);
      } catch (final IOException e) {
        errors.add("Error while checking path for bazaar executable: " + StringUtils.toString(e));
      }

      // validate URL is valid if there were no other errors
      final String urlValue = flURL.getValue().trim();
      boolean validProtocolFound = false;
      for (int i = 0; i < VALID_MERCURIAL_URL_PROTOCOLS.length; i++) {
        if (urlValue.startsWith(VALID_MERCURIAL_URL_PROTOCOLS[i])) {
          validProtocolFound = true;
          break;
        }
      }

      // show error if prefix is invalid
      if (!validProtocolFound) {
        errors.add(INVALID_PROTOCOL_MSG);
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
        if (be.isUnix() && be.commandIsAvailable(DEFAULT_UNIX_MERCURIAL_COMMAND)) {
          flPathToExe.setValue(DEFAULT_UNIX_MERCURIAL_COMMAND);
        }
      } catch (final Exception e) {
        IoUtils.ignoreExpectedException(e);
      }
    }
  }


  /**
   * Helper method to create invalid protocol message.
   *
   * @return error message.
   */
  private static String makeInvalidProtocolMessage() {
    final StringBuilder invalidPrefixMsg = new StringBuilder("Mercurial branch path should start with one of the following: ");
    for (int i = 0; i < VALID_MERCURIAL_URL_PROTOCOLS.length; i++) {
      invalidPrefixMsg.append('\"').append(VALID_MERCURIAL_URL_PROTOCOLS[i]).append('\"');
      if (i < VALID_MERCURIAL_URL_PROTOCOLS.length - 1) {
        invalidPrefixMsg.append(", ");
      } else {
        invalidPrefixMsg.append('.');
      }
    }
    return invalidPrefixMsg.toString();
  }


  /**
   * Returns path to Mercurial exe.
   *
   * @return path to Mercurial exe.
   */
  public String getPathToMercurialExe() {
    return flPathToExe.getValue();
  }


  public String toString() {
    return "MERCURIALSettingsPanel{" +
            ", flPathToExe=" + flPathToExe +
            '}';
  }
}