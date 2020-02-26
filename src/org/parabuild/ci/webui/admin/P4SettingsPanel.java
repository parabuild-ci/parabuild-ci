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
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.NoLiveAgentsException;
import org.parabuild.ci.versioncontrol.perforce.P4ClientNameGeneratorImpl;
import org.parabuild.ci.versioncontrol.perforce.P4ClientViewParser;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @noinspection FieldCanBeLocal
 */
public final class P4SettingsPanel extends AbstractSourceControlPanel {

  /**
   * @noinspection AnalyzingVariableNaming
   */
  private static final long serialVersionUID = 4467119862820193870L; // NOPMD

  private static final String CAPTION_AUTHENTICATION_MODE = "  Authentication mode: ";
  private static final String CAPTION_CASE_SENSITIVE_USER_NAMES = "Case-sensitive user names: ";
  private static final String CAPTION_DO_NOT_SYNC = "Do not sync: ";
  private static final String CAPTION_LINE_END = "P4 line end: ";
  private static final String CAPTION_UPDATE_HAVE_LIST = "Update have list: ";
  private static final String NAME_ADVANCED_VIEW_MODE = "Advanced view mode:";
  private static final String NAME_CLIENT_NAME_TEMPLATE = "Client name template:";
  private static final String NAME_CLIENT_VIEW = "P4 client view:";
  private static final String NAME_COUNTER = "P4 counter:";
  private static final String NAME_OPTIONS = "P4 options:";
  private static final String NAME_P4WEB_URL = "P4Web URL:";
  private static final String NAME_PASSWORD = "P4 password:";
  private static final String NAME_PATH_TO_CLIENT = "Path to P4 executable:";
  private static final String NAME_PORT = "P4 port:";
  private static final String NAME_REL_BUILD_DIR = " Custom relative build dir: ";
  private static final String NAME_UNC_PATHS = "UNC paths under Windows:";
  private static final String NAME_USER = "P4 user:";
  private static final String NAME_VARS_OVERRIDE = "P4 variables override:";


  private final CommonFieldLabel lbCaseSensitiveUserNames = new CommonFieldLabel(CAPTION_CASE_SENSITIVE_USER_NAMES);  // NOPMD
  private final CommonFieldLabel lbClientNameTemplate = new CommonFieldLabel(NAME_CLIENT_NAME_TEMPLATE);  // NOPMD
  private final CommonFieldLabel lbClientView = new CommonFieldLabel(NAME_CLIENT_VIEW); // NOPMD
  private final CommonFieldLabel lbCounter = new CommonFieldLabel(NAME_COUNTER);  // NOPMD
  private final CommonFieldLabel lbDoNotSync = new CommonFieldLabel(CAPTION_DO_NOT_SYNC);
  private final CommonFieldLabel lbP4WebURL = new CommonFieldLabel(NAME_P4WEB_URL);  // NOPMD
  private final CommonFieldLabel lbPassword = new CommonFieldLabel(NAME_PASSWORD);  // NOPMD
  private final CommonFieldLabel lbRelativeBuildDir = new CommonFieldLabel(NAME_REL_BUILD_DIR);
  private final CommonFieldLabel lbUpdateHaveList = new CommonFieldLabel(CAPTION_UPDATE_HAVE_LIST);
  private final CommonFieldLabel lbLineEnd = new CommonFieldLabel(CAPTION_LINE_END);
  private final Label lbClientViewAligner = new Label();
  private final Label lbClientViewByDepotPathAligner = new Label();
  private final Label lbClientViewByClientNameAligner = new Label();

  private final CheckBox flAdvancedViewMode = new CheckBox(); // NOPMD
  private final CheckBox flShellVariablesOverride = new CheckBox(); // NOPMD
  private final CheckBox flUseUNCPaths = new CheckBox(); // NOPMD
  private final CheckBox cbDoNotSync = new CheckBox(); // NOPMD
  private final EncryptingPassword flPassword = new EncryptingPassword(60, 20, "perforce_password"); // NOPMD
  private final Field flClientNameTemplate = new Field(70, 65); // NOPMD
  private final Field flCounter = new Field(20, 20); // NOPMD
  private final Field flPathToP4Client = new Field(200, 65); // NOPMD
  private final Field flPort = new Field(80, 65); // NOPMD
  private final Field flRelativeBuildDir = new Field(70, 65); // NOPMD
  private final Field flUser = new Field(50, 70); // NOPMD
  private final Text flClientView = new Text(70, 5); // NOPMD
  private final Field flP4WebURL = new Field(100, 65); // NOPMD
  private final Field flClientViewByDepotPath = new Field(200, 65); // NOPMD
  private final Field flClientViewByClientName = new Field(200, 65); // NOPMD
  private final DropDown flOptionModtime = new P4ModtimeDropDown(); // NOPMD
  private final DropDown flOptionClobber = new P4ClobberDropDown(); // NOPMD
  private final DropDown flLineEnd = new P4LineEndDropDown(); // NOPMD
  private final DropDown flAuthenticationMode = new P4AuthenticationModeDropDown(); // NOPMD
  private final P4ClientViewSourceDropDown flClientViewSource = new P4ClientViewSourceDropDown(); // NOPMD
  private final RequiredFieldMarker flwClientViewByDepotPathMarker = new RequiredFieldMarker(flClientViewByDepotPath);
  private final RequiredFieldMarker flwClientViewByClientNameMarker = new RequiredFieldMarker(flClientViewByClientName);
  private final CheckBox cbUpdateHaveList = new CheckBox();
  private final CheckBox cbCaseSensitiveUserNames = new CheckBox();  // NOPMD
  private final FieldWithButtonPanel pnlClientView = new FieldWithButtonPanel(NAME_CLIENT_VIEW, flClientView);


  public P4SettingsPanel() {
    super("P4 Settings");

    // finish setting up selector
    flClientViewSource.showOnSelect(P4ClientViewSourceDropDown.SOURCE_FIELD, pnlClientView);
    flClientViewSource.showOnSelect(P4ClientViewSourceDropDown.SOURCE_FIELD, lbClientViewAligner);
    flClientViewSource.showOnSelect(P4ClientViewSourceDropDown.SOURCE_DEPOT_PATH, flwClientViewByDepotPathMarker);
    flClientViewSource.showOnSelect(P4ClientViewSourceDropDown.SOURCE_DEPOT_PATH, lbClientViewByDepotPathAligner);
    flClientViewSource.showOnSelect(P4ClientViewSourceDropDown.SOURCE_CLIENT_NAME, flwClientViewByClientNameMarker);
    flClientViewSource.showOnSelect(P4ClientViewSourceDropDown.SOURCE_CLIENT_NAME, lbClientViewByClientNameAligner);

    final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
    final boolean advancedSelected = scm.isAdvancedConfigurationMode();

    //
    lbClientView.setAlignY(Layout.TOP);
    pnlClientView.setAlignY(Layout.TOP);
    flClientView.setAlignY(Layout.TOP);

    // layout

    gridIterator.addPair(new CommonFieldLabel(NAME_PATH_TO_CLIENT), new RequiredFieldMarker(flPathToP4Client));
    gridIterator.addPair(new CommonFieldLabel(NAME_PORT), new RequiredFieldMarker(flPort));
    gridIterator.addPair(new CommonFieldLabel(NAME_USER), new RequiredFieldMarker(flUser));
    gridIterator.addPair(lbPassword, new CommonFlow(flPassword, new CommonFieldLabel(CAPTION_AUTHENTICATION_MODE), flAuthenticationMode));

    // add a selector for client view
    if (advancedSelected) {
      gridIterator.addPair(lbClientView, flClientViewSource);
      gridIterator.addPair(lbClientViewByDepotPathAligner, flwClientViewByDepotPathMarker);
      gridIterator.addPair(lbClientViewAligner, pnlClientView);
      gridIterator.addPair(lbClientViewByClientNameAligner, flwClientViewByClientNameMarker);
    } else {
      gridIterator.addPair(lbClientView, pnlClientView);
    }

    gridIterator.addPair(lbLineEnd, flLineEnd);

    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final SourceControlSetting setting = cm.getSourceControlSetting(buildID, SourceControlSetting.DO_NOT_CHECKOUT);
    if (scm.isNoCheckoutBuildEnabled() || (setting != null && setting.getPropertyValue().equals(SourceControlSetting.OPTION_CHECKED))) {
      gridIterator.addPair(lbDoNotSync, cbDoNotSync);
    }

    gridIterator.addPair(new CommonFieldLabel(NAME_ADVANCED_VIEW_MODE), new CommonFlow(flAdvancedViewMode, lbRelativeBuildDir, flRelativeBuildDir));
    gridIterator.addPair(lbCounter, flCounter);

    // layout optional
    if (advancedSelected) {
      gridIterator.addPair(new CommonFieldLabel(NAME_OPTIONS), new CommonFlow(flOptionModtime, new Label(" "), flOptionClobber));
      gridIterator.addPair(lbClientNameTemplate, flClientNameTemplate);
      gridIterator.addPair(new CommonFieldLabel(NAME_VARS_OVERRIDE), flShellVariablesOverride);
      gridIterator.addPair(new CommonFieldLabel(NAME_UNC_PATHS), flUseUNCPaths);
    }
    gridIterator.addPair(lbP4WebURL, flP4WebURL);
    gridIterator.addPair(lbUpdateHaveList, cbUpdateHaveList);
    gridIterator.addPair(lbCaseSensitiveUserNames, cbCaseSensitiveUserNames);

    // init property to input map
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_PASSWORD, flPassword);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_USER, flUser);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_PORT, flPort);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_DEPOT_PATH, flClientView);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_PATH_TO_CLIENT, flPathToP4Client);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_COUNTER, flCounter);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_RELATIVE_BUILD_DIR, flRelativeBuildDir);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_ADVANCED_VIEW_MODE, flAdvancedViewMode);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_USE_UNC_PATHS, flUseUNCPaths);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_VARS_OVERRIDE, flShellVariablesOverride);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_CLIENT_NAME_TEMPLATE, flClientNameTemplate);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_MODTIME_OPTION, flOptionModtime);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_CLOBBER_OPTION, flOptionClobber);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_P4WEB_URL, flP4WebURL);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_AUTHENTICATION_MODE, flAuthenticationMode);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_CLIENT_VIEW_SOURCE, flClientViewSource);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_CLIENT_VIEW_BY_DEPOT_PATH, flClientViewByDepotPath);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_CLIENT_VIEW_BY_CLIENT_NAME, flClientViewByClientName);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_UPDATE_HAVE_LIST, cbUpdateHaveList);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_CASE_SENSITIVE_USER_NAMES, cbCaseSensitiveUserNames);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.DO_NOT_CHECKOUT, cbDoNotSync);
    propertyToInputMap.bindPropertyNameToInput(SourceControlSetting.P4_LINE_END, flLineEnd);

    // add footer
    addCommonAttributes();

    cbUpdateHaveList.setChecked(true);
    cbCaseSensitiveUserNames.setChecked(scm.isCaseSensitiveVCSNames());
  }


  /**
   * Common implementation for loading version control
   * configuration from given build config
   *
   * @param buildConfig BuildConfig to load configuration for.
   */
  public void load(final BuildConfig buildConfig) {
    super.load(buildConfig);
    // NOTE: simeshev@parabuilci.org - 2007-05-30 - initially
    // shows/hides components according to loaded selection
    flClientViewSource.refresh();
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
      // first, diable everything
      setEditable(false);
      // enable those editable for parallel mode
      flPathToP4Client.setEditable(true);
    } else {
      throw new IllegalArgumentException("Illegal edit mode: " + mode);
    }
  }


  /**
   * Helper method.
   *
   * @param editable if true controls are editable.
   */
  private void setEditable(final boolean editable) {
    cbCaseSensitiveUserNames.setEditable(editable);
    cbUpdateHaveList.setEditable(editable);
    cbDoNotSync.setEditable(editable);
    flAdvancedViewMode.setEditable(editable);
    flAuthenticationMode.setEditable(editable);
    flClientNameTemplate.setEditable(editable);
    flClientView.setEditable(editable);
    flClientViewByDepotPath.setEditable(editable);
    flClientViewByClientName.setEditable(editable);
    flClientViewSource.setEditable(editable);
    flCounter.setEditable(editable);
    flOptionClobber.setEditable(editable);
    flOptionModtime.setEditable(editable);
    flP4WebURL.setEditable(editable);
    flPassword.setEditable(editable);
    flPathToP4Client.setEditable(editable);
    flPort.setEditable(editable);
    flRelativeBuildDir.setEditable(editable);
    flShellVariablesOverride.setEditable(editable);
    flUser.setEditable(editable);
    flUseUNCPaths.setEditable(editable);
    if (!editable) {
      WebuiUtils.hideCaptionAndFieldIfBlank(lbClientNameTemplate, flClientNameTemplate);
      WebuiUtils.hideCaptionAndFieldIfBlank(lbCounter, flCounter);
      WebuiUtils.hideCaptionAndFieldIfBlank(lbP4WebURL, flP4WebURL);
      WebuiUtils.hideCaptionAndFieldIfBlank(lbPassword, flPassword);
      WebuiUtils.hideCaptionAndFieldIfBlank(lbRelativeBuildDir, flRelativeBuildDir);
    }
  }


  /**
   * When called, this method should return <code>true</code>
   * when content of a component is valid for save. If not valid,
   * a component should display a error message in it's area.
   *
   * @return true if valid
   * @noinspection ResultOfObjectAllocationIgnored
   */
  protected boolean doValidate() {
    clearMessage();
    final List errors = new ArrayList(5);
    WebuiUtils.validateFieldNotBlank(errors, NAME_PATH_TO_CLIENT, flPathToP4Client);
    WebuiUtils.validateFieldNotBlank(errors, NAME_PORT, flPort);
    WebuiUtils.validateFieldNotBlank(errors, NAME_USER, flUser);

    if (flClientViewSource.getCode() == (int) P4ClientViewSourceDropDown.SOURCE_FIELD) {
      WebuiUtils.validateFieldNotBlank(errors, NAME_CLIENT_VIEW, flClientView);
    } else if (flClientViewSource.getCode() == (int) P4ClientViewSourceDropDown.SOURCE_DEPOT_PATH) {
      WebuiUtils.validateFieldNotBlank(errors, NAME_CLIENT_VIEW, flClientViewByDepotPath);
      if (!flClientViewByDepotPath.getValue().startsWith("//") || flClientViewByDepotPath.getValue().endsWith("...")) {
        errors.add('\"' + NAME_CLIENT_VIEW + "\" is not a valid Perforce depot path");
      }
    } else if (flClientViewSource.getCode() == (int) P4ClientViewSourceDropDown.SOURCE_CLIENT_NAME) {
      WebuiUtils.validateFieldNotBlank(errors, NAME_CLIENT_VIEW, flClientViewByClientName);
    }

    // counter name is valid
    if (!StringUtils.isBlank(flCounter.getValue())) {
      WebuiUtils.validateFieldStrict(errors, NAME_COUNTER, flCounter);
    }

    // relative build path not null
    final String relativeBuildPath = flAdvancedViewMode.isChecked() ? flRelativeBuildDir.getValue() : "";
    if (flAdvancedViewMode.isChecked() && StringUtils.isBlank(relativeBuildPath)) {
      errors.add('\"' + NAME_REL_BUILD_DIR + "\" should be set if \"" + NAME_ADVANCED_VIEW_MODE + "\" is checked.");
    }

    //

    // depot path length
    final String depotPath = flClientView.getValue();
    // See #778
    //if (errors.size() == 0 && depotPath.length() > MAX_CLIENT_VIEW_LENGTH) {
    //  errors.add("\"" + NAME_CLIENT_VIEW + "\" is too long. The maximum allowed length is " + MAX_CLIENT_VIEW_LENGTH_STRING + ".");
    //}

    // depot path validity
    if (flClientViewSource.getCode() == (int) P4ClientViewSourceDropDown.SOURCE_FIELD && errors.isEmpty()) {
      try {
        final P4ClientViewParser parser = new P4ClientViewParser();
        parser.parse(relativeBuildPath, depotPath);
      } catch (final ValidationException e) {
        errors.add(e.getMessage());
      }
    }

    // validate P4 client exists if there were no other errors
    try {
      WebuiUtils.validateCommandExists(super.getAgentEnv(), flPathToP4Client.getValue(), errors,
              "Path to p4 executable is invalid, or p4 executable is not accessible");
    } catch (final NoLiveAgentsException ignore) {
      IoUtils.ignoreExpectedException(ignore);
    } catch (final IOException e) {
      errors.add("Error while checking path for p4 executable: " + StringUtils.toString(e));
    }

    // validate client name template
    if (!StringUtils.isBlank(flClientNameTemplate.getValue())) {
      final P4ClientNameGeneratorImpl nameGenerator = new P4ClientNameGeneratorImpl();
      if (!nameGenerator.isTemplateValid(flClientNameTemplate.getValue())) {
        errors.add("Client name template is not valid. Client name template should contain ${build.id} and may contain ${p4.user} and ${builder.host}.");
      }
    }

    // validate P4Web URL
    if (!StringUtils.isBlank(flP4WebURL.getValue())) {
      try {
        new URL(flP4WebURL.getValue());
      } catch (final MalformedURLException e) {
        errors.add('\"' + NAME_P4WEB_URL + "\" is not a valid URL: " + StringUtils.toString(e));
      }
    }

    // validate P4 password contains alpha-numerical characters
    if (!StringUtils.isBlank(flPassword.getValue())) {
      if (!Pattern.compile("[-a-zA-Z_0-9]*").matcher(flPassword.getValue()).matches()) {
        errors.add("Field \"" + NAME_PASSWORD + "\" can contain only alphanumeric characters, \"-\" and \"_\".");
      }
    }

    // Validate P4 port
    final P4PortValidator p4PortValidator = new P4PortValidator();
    if (!p4PortValidator.validate(flPort.getValue())) {
      errors.add("Field \"" + NAME_PORT + "\" should have format <host>:<port>");
    }

    // show errors
    if (errors.isEmpty()) {
      return true;
    }
    showErrorMessage(errors);
    return false;
  }


  public String toString() {
    return "P4SettingsPanel{" +
            "cbCaseSensitiveUserNames=" + cbCaseSensitiveUserNames +
            ", cbUpdateHaveList=" + cbUpdateHaveList +
            ", flAdvancedViewMode=" + flAdvancedViewMode +
            ", flAuthenticationMode=" + flAuthenticationMode +
            ", flClientNameTemplate=" + flClientNameTemplate +
            ", flClientView=" + flClientView +
            ", flClientViewByDepotPath=" + flClientViewByDepotPath +
            ", flClientViewByWorkspaceName=" + flClientViewByClientName +
            ", flClientViewSource=" + flClientViewSource +
            ", flCounter=" + flCounter +
            ", flOptionClobber=" + flOptionClobber +
            ", flOptionModtime=" + flOptionModtime +
            ", flP4WebURL=" + flP4WebURL +
            ", flPassword=" + flPassword +
            ", flPathToP4Client=" + flPathToP4Client +
            ", flPort=" + flPort +
            ", flRelativeBuildDir=" + flRelativeBuildDir +
            ", flShellVariablesOverride=" + flShellVariablesOverride +
            ", flUser=" + flUser +
            ", flUseUNCPaths=" + flUseUNCPaths +
            ", flwClientViewByDepotPathMarker=" + flwClientViewByDepotPathMarker +
            ", flwClientViewByWorkspaceNameMarker=" + flwClientViewByClientNameMarker +
            ", lbCaseSensitiveUserNames=" + lbCaseSensitiveUserNames +
            ", lbClientNameTemplate=" + lbClientNameTemplate +
            ", lbClientView=" + lbClientView +
            ", lbClientViewAligner=" + lbClientViewAligner +
            ", lbClientViewByDepotPathAligner=" + lbClientViewByDepotPathAligner +
            ", lbClientViewByWorkspaceNamehAligner=" + lbClientViewByClientNameAligner +
            ", lbCounter=" + lbCounter +
            ", lbP4WebURL=" + lbP4WebURL +
            ", lbPassword=" + lbPassword +
            ", lbRelativeBuildDir=" + lbRelativeBuildDir +
            ", lbUpdateHaveList=" + lbUpdateHaveList +
            "} " + super.toString();
  }
}
