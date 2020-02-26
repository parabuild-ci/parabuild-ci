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

import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.util.ValidationException;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.versioncontrol.SVNDepotPathParser;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.EncryptingPassword;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.CheckBox;
import viewtier.ui.Field;
import viewtier.ui.Layout;
import viewtier.ui.Text;

import java.util.ArrayList;
import java.util.List;

/**
 */
public final class SVNSettingsPanel extends AbstractSourceControlPanel {

  private static final long serialVersionUID = 4467119862820193870L; // NOPMD

  private static final String[] VALID_SVN_URL_PROTOCOLS = {"svn://", "svn+ssh://", "http://", "https://", "file://"};
  private static final String INVALID_PROTOCOL_MSG = makeInvalidProtocolMessage();
  public static final String DEFAULT_UNIX_SVN_COMMAND = "/usr/bin/svn";

  private static final String NAME_SVN_DEPOT_PATH = "SVN repository path:";
  private static final String NAME_SVN_PASSWORD = "SVN password:";
  private static final String NAME_SVN_PATH_TO_EXE = "Path to svn executable:";
  private static final String NAME_SVN_URL = "SVN URL:";
  private static final String NAME_SVN_USER = "SVN user:";
  private static final String NAME_WATCH_NON_RECURSIVE_PATHS = "Watch non-recursive paths recursively for changes: ";
  private static final String NAME_IGNORE_EXTERNALS = "Ignore externals: ";


  private static final String NAME_ADD_OPTION_TRUST_SERVER_CERT = "Add option --trust-server-cert";
  // captions
  private final CommonFieldLabel lbDepotPath = new CommonFieldLabel(NAME_SVN_DEPOT_PATH); // NOPMD

  private final CommonFieldLabel lbPassword = new CommonFieldLabel(NAME_SVN_PASSWORD);
  // fields
  private final Field flPathToExe = new Field(200, 50);
  private final Field flURL = new Field(500, 50);
  private final Field flUser = new Field(20, 20);
  private final EncryptingPassword flPassword = new EncryptingPassword(30, 20, "svn_password");
  private final Text flDepotPath = new Text(100, 5);
  private final CheckBox flWatchNonRecursivePaths = new CheckBox();
  private final CheckBox flAddTrustServerCert = new CheckBox();
  private final CheckBox flIgnoreExternals = new CheckBox();


  public SVNSettingsPanel() {

    super("SVN Settings");

    // layout
    gridIterator.addPair(new CommonFieldLabel(NAME_SVN_PATH_TO_EXE), new RequiredFieldMarker(flPathToExe));
    gridIterator.addPair(new CommonFieldLabel(NAME_SVN_URL), new RequiredFieldMarker(flURL));
    gridIterator.addPair(new CommonFieldLabel(NAME_SVN_USER), new RequiredFieldMarker(flUser));
    gridIterator.addPair(new CommonFieldLabel(NAME_ADD_OPTION_TRUST_SERVER_CERT), new RequiredFieldMarker(flAddTrustServerCert));
    gridIterator.addPair(new CommonFieldLabel(NAME_IGNORE_EXTERNALS), new RequiredFieldMarker(flIgnoreExternals));
    gridIterator.addPair(lbPassword, flPassword);
    gridIterator.addPair(lbDepotPath, new RequiredFieldMarker(flDepotPath));
    gridIterator.addPair(new CommonFieldLabel(NAME_WATCH_NON_RECURSIVE_PATHS), flWatchNonRecursivePaths);

    // init property to input map
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.SVN_PASSWORD, flPassword);
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.SVN_USER, flUser);
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.SVN_DEPOT_PATH, flDepotPath);
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.SVN_PATH_TO_EXE, flPathToExe);
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.SVN_URL, flURL);
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.SVN_WATCH_NON_RECURSIVE_PATHS, flWatchNonRecursivePaths);
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.SVN_ADD_OPTION_TRUST_SERVER_CERT, flAddTrustServerCert);
    propertyToInputMap.bindPropertyNameToInput(VCSAttribute.SVN_IGNORE_EXTERNALS, flIgnoreExternals);

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
    flURL.setEditable(editable);
    flDepotPath.setEditable(editable);
    flPassword.setEditable(editable);
    flPathToExe.setEditable(editable);
    flUser.setEditable(editable);
    flWatchNonRecursivePaths.setEditable(editable);
    flAddTrustServerCert.setEditable(editable);
    flIgnoreExternals.setEditable(editable);
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
    WebuiUtils.validateFieldNotBlank(errors, NAME_SVN_PATH_TO_EXE, flPathToExe);
    WebuiUtils.validateFieldNotBlank(errors, NAME_SVN_DEPOT_PATH, flDepotPath);
    WebuiUtils.validateFieldNotBlank(errors, NAME_SVN_URL, flURL);

    // counter name is valid
    if (!StringUtils.isBlank(flPassword.getValue())
            && StringUtils.isBlank(flUser.getValue())) {
      errors.add("User name cannot be blank if password is set.");
    }

    if (errors.isEmpty()) {

      // validate SVN URL is valid if there were no other errors
      final String urlValue = flURL.getValue().trim();
      boolean validProtocolFound = false;
      for (int i = 0; i < VALID_SVN_URL_PROTOCOLS.length; i++) {
        if (urlValue.startsWith(VALID_SVN_URL_PROTOCOLS[i])) {
          validProtocolFound = true;
          break;
        }
      }

      // show error if prefix is invalid
      if (!validProtocolFound) {
        errors.add(INVALID_PROTOCOL_MSG);
      }

      // further validate SVN depot path
      final SVNDepotPathParser parser = new SVNDepotPathParser();
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
        if (be.isUnix() && be.commandIsAvailable(DEFAULT_UNIX_SVN_COMMAND)) {
          flPathToExe.setValue(DEFAULT_UNIX_SVN_COMMAND);
        }
      } catch (final Exception e) {
        IoUtils.ignoreExpectedException(e);
      }
    }
  }


  /**
   * Helper method to create invalig protocol message.
   */
  private static String makeInvalidProtocolMessage() {
    final StringBuilder invalidPrefixMsg = new StringBuilder("SVN URL should start with one of the following: ");
    for (int i = 0; i < VALID_SVN_URL_PROTOCOLS.length; i++) {
      invalidPrefixMsg.append('\"').append(VALID_SVN_URL_PROTOCOLS[i]).append('\"');
      if (i < VALID_SVN_URL_PROTOCOLS.length - 1) {
        invalidPrefixMsg.append(", ");
      } else {
        invalidPrefixMsg.append('.');
      }
    }
    return invalidPrefixMsg.toString();
  }


  /**
   * Returns path to SVN exe.
   */
  public String getPathToSVNExe() {
    return flPathToExe.getValue();
  }


  public String toString() {
    return "SVNSettingsPanel{" +
            "lbDepotPath=" + lbDepotPath +
            ", lbPassword=" + lbPassword +
            ", flPathToExe=" + flPathToExe +
            ", flURL=" + flURL +
            ", flUser=" + flUser +
            ", flPassword=" + flPassword +
            ", flDepotPath=" + flDepotPath +
            ", flWatchNonRecursivePaths=" + flWatchNonRecursivePaths +
            ", flAddTrustServerCert=" + flAddTrustServerCert +
            "} " + super.toString();
  }
}
