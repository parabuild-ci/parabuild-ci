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
package org.parabuild.ci.webui.admin.system;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.log.MarkerMatcherBuilder;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationConstants;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.services.ServiceManager;
import org.parabuild.ci.webui.admin.AbstractSystemConfigPanel;
import org.parabuild.ci.webui.admin.DateFormatDropdown;
import org.parabuild.ci.webui.admin.DateTimeFormatDropdown;
import org.parabuild.ci.webui.admin.HTTPProtocolDropdown;
import org.parabuild.ci.webui.agent.status.AgentsStatusMonitor;
import org.parabuild.ci.webui.common.BoldCommonLabel;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.CheckBox;
import viewtier.ui.DropDown;
import viewtier.ui.Field;
import viewtier.ui.Flow;
import viewtier.ui.Layout;
import viewtier.ui.RadioButton;
import viewtier.ui.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * System build panel
 *
 * @noinspection FieldCanBeLocal
 */
public final class AppearanceConfigPanel extends AbstractSystemConfigPanel {

  private static final long serialVersionUID = 4337715912625073404L; // NOPMD
  private static final Log log = LogFactory.getLog(AppearanceConfigPanel.class);

  private static final String CAPTION_AGENT_STATUS_CHART_HEIGHT = "Agent status chart height: ";
  private static final String CAPTION_AGENT_STATUS_CHART_WIDTH = "Agent status chart width: ";
  private static final String CAPTION_AGENT_STATUS_PAGE = "Agent status page: ";
  private static final String CAPTION_BRANDING = "Branding: ";
  private static final String CAPTION_BUILD_INSTRUCTIONS = "Show build instructions: ";
  private static final String CAPTION_BUILD_NAME_VALIDATION = "Build name validation: "; // NOPMD
  private static final String CAPTION_BUILDMAN_HOST = "Build manager host and port: ";
  private static final String CAPTION_CHANGE_LIST_DESCRIPTION_QUOTE_LENGTH = "Change list description quote: ";
  private static final String CAPTION_DASHBOARD_ROW_SIZE = "Dashboard row size: ";
  private static final String CAPTION_DATE_FORMAT = "Date format: ";
  private static final String CAPTION_DATE_TIME_FORMAT = "Date and time format: ";
  private static final String CAPTION_ENABLE_ADVANCED_SETTINGS = "Enable advanced settings: ";
  private static final String CAPTION_ENABLE_BUILD_PROMOTION = "Enable build promotion: ";
  private static final String CAPTION_ENABLE_PUBLISHING_COMMANDS = "Enable publishing commands : ";
  private static final String CAPTION_ERROR_LINE_QUOTE_LENGTH = "Error line quote length: ";
  private static final String CAPTION_ERROR_LOG_QUOTE_SIZE = "Error log quote size: ";
  private static final String CAPTION_GENERATED_URL_PROTOCOL = "Generated URL protocol: ";
  private static final String CAPTION_LOG_TAIL_BUFFER_SIZE = "Log tail window size: ";
  private static final String CAPTION_MESSAGE_ENCODING = "Output encoding: ";
  private static final String CAPTION_REFRESH_RATE = "Default build status refresh rate: ";
  private static final String CAPTION_SECONDS = " seconds";
  private static final String CAPTION_SHOW_AGENT_ON_STATUS_LIST_PAGE = "Show agent on status list page: ";
  private static final String CAPTION_SHOW_BUILD_AND_CHANGE_NUMBER_ON_DASHBOARD = "Show build and change number on dashboard: ";
  private static final String CAPTION_SHOW_MERGE_STATUSES = "Show merge statuses";
  private static final String CAPTION_SHOW_NEXT_BUILD_TIME_ON_STATUS_LIST_PAGE = "Show next build time on status list page: ";
  private static final String CAPTION_SHOW_PARALLEL_BUILDS_IN_LIST_VIEW = "Show parallel builds in list view: ";
  private static final String CAPTION_SHOW_PROJECTS_LINK = "Show link to projects: ";
  private static final String CAPTION_SHOW_RSS_LINKS = "Show RSS links: ";
  private static final String CAPTION_TEXT_LOG_MARKERS = "Text log markers:";
  private static final String CAPTION_USER_INTERFACE_SETTINGS = "User Interface Settings";
  private static final String CAPTION_VARIABLE_NAME_VALIDATION = "Variable name validation: ";

  private final CheckBox fldEnableAdvancedSettings = new CheckBox(); // NOPMD
  private final CheckBox fldEnableBuildPromotion = new CheckBox(); // NOPMD
  private final CheckBox fldEnablePublishingCommands = new CheckBox(); // NOPMD
  private final CheckBox fldShowBuildInstructions = new CheckBox(); // NOPMD
  private final CheckBox fldShowProjectLink = new CheckBox(); // NOPMD
  private final CheckBox fldShowRSSLinks = new CheckBox(); // NOPMD
  private final CheckBox flShowBuildAndChangeNumber = new CheckBox(); // NOPMD
  private final CheckBox flShowIPAddress = new CheckBox(); // NOPMD
  private final CheckBox flShowMergeStatuses = new CheckBox(); // NOPMD
  private final CheckBox flShowNextBuildTime = new CheckBox(); // NOPMD
  private final CheckBox flShowParallelInListView = new CheckBox(); // NOPMD
  private final CommonFieldLabel lbTextLogMarkers = new CommonFieldLabel(CAPTION_TEXT_LOG_MARKERS);  // NOPMD
  private final DropDown fldDateFormat = new DateFormatDropdown(); // NOPMD
  private final DropDown fldDateTimeFormat = new DateTimeFormatDropdown(); // NOPMD
  private final DropDown fldURLProtocol = new HTTPProtocolDropdown(); // NOPMD
  private final Field flAgentStatusChartHeightPix = new CommonField(3, 3); // NOPMD
  private final Field flAgentStatusChartWidthPix = new CommonField(3, 3); // NOPMD
  private final Field flAgentStatusColumns = new CommonField(3, 3); // NOPMD
  private final Field flDashboardRowSize = new CommonField(2, 2);
  private final Field fldBranding = new CommonField(50, 50); // NOPMD
  private final Field fldChangeListDescrQuoteLength = new CommonField(3, 3); // NOPMD
  private final Field fldCustomBuildNameRegexTemplate = new CommonField("custom-build-name-template", 50, 50);  // NOPMD
  private final Field fldCustomVariableNameRegexTemplate = new CommonField("custom-variable-name-template", 50, 50);  // NOPMD
  private final Field fldErrorLineQuoteLength = new CommonField(3, 3); // NOPMD
  private final Field fldErrorWindowSize = new CommonField(3, 3); // NOPMD
  private final Field fldManagerHost = new CommonField(100, 50); // NOPMD
  private final Field fldOutputEncoding = new CommonField(30, 30); // NOPMD
  private final Field fldRefreshRate = new CommonField(3, 3); // NOPMD
  private final Field flTailWindowSize = new CommonField(3, 3); // NOPMD
  private final RadioButton fldCustomBuildNameValidation = createRadioButton("build-name-validation");  // NOPMD
  private final RadioButton fldCustomVariableNameValidation = createRadioButton("variable-name-validation");  // NOPMD
  private final RadioButton fldDefaultBuildNameValidation = createRadioButton("build-name-validation"); // NOPMD
  private final RadioButton fldDefaultVariableNameValidation = createRadioButton("variable-name-validation"); // NOPMD
  private final Text flTextLogMarkers = new Text(50, 10);


  public AppearanceConfigPanel() {
    setTitle(CAPTION_USER_INTERFACE_SETTINGS);

    // unified padding
    fldDateFormat.setPadding(4);
    fldDateTimeFormat.setPadding(4);

    // http://<host name>
    fldManagerHost.setValue(getBuildManagerHost()); // set default value
    fldManagerHost.setPadding(2);

    // 
    flTextLogMarkers.setAlignY(Layout.TOP);
    lbTextLogMarkers.setAlignY(Layout.TOP);

    // create grid, add components
    final GridIterator gridIterator = new GridIterator(super.getUserPanel(), 2);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_BUILDMAN_HOST), new RequiredFieldMarker(fldManagerHost));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_GENERATED_URL_PROTOCOL), fldURLProtocol);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_DATE_FORMAT), new RequiredFieldMarker(fldDateFormat));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_DATE_TIME_FORMAT), new RequiredFieldMarker(fldDateTimeFormat));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_BUILD_NAME_VALIDATION), new CommonFlow(fldDefaultBuildNameValidation, new BoldCommonLabel(" Default")));
    gridIterator.addPair(new CommonLabel(""), new CommonFlow(fldCustomBuildNameValidation, new BoldCommonLabel(" Custom regex: "), fldCustomBuildNameRegexTemplate));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_VARIABLE_NAME_VALIDATION), new CommonFlow(fldDefaultVariableNameValidation, new BoldCommonLabel(" Default")));
    gridIterator.addPair(new CommonLabel(""), new CommonFlow(fldCustomVariableNameValidation, new BoldCommonLabel(" Custom regex: "), fldCustomVariableNameRegexTemplate));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_REFRESH_RATE), new CommonFlow(fldRefreshRate, new CommonLabel(CAPTION_SECONDS)));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_MESSAGE_ENCODING), fldOutputEncoding);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_CHANGE_LIST_DESCRIPTION_QUOTE_LENGTH), new Flow().add(fldChangeListDescrQuoteLength).add(new CommonLabel("  characters")));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_ERROR_LINE_QUOTE_LENGTH), new Flow().add(fldErrorLineQuoteLength).add(new CommonLabel("  characters")));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_ERROR_LOG_QUOTE_SIZE), new Flow().add(fldErrorWindowSize).add(new CommonLabel("  lines")));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_BRANDING), fldBranding);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_SHOW_PROJECTS_LINK), fldShowProjectLink);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_SHOW_RSS_LINKS), fldShowRSSLinks);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_BUILD_INSTRUCTIONS), fldShowBuildInstructions);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_ENABLE_ADVANCED_SETTINGS), fldEnableAdvancedSettings);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_ENABLE_BUILD_PROMOTION), fldEnableBuildPromotion);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_ENABLE_PUBLISHING_COMMANDS), fldEnablePublishingCommands);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_SHOW_PARALLEL_BUILDS_IN_LIST_VIEW), flShowParallelInListView);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_SHOW_AGENT_ON_STATUS_LIST_PAGE), flShowIPAddress);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_SHOW_NEXT_BUILD_TIME_ON_STATUS_LIST_PAGE), flShowNextBuildTime);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_DASHBOARD_ROW_SIZE), new CommonFlow(new RequiredFieldMarker(flDashboardRowSize), new CommonLabel(" builds")));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_SHOW_BUILD_AND_CHANGE_NUMBER_ON_DASHBOARD), flShowBuildAndChangeNumber);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_LOG_TAIL_BUFFER_SIZE), new CommonFlow(new RequiredFieldMarker(flTailWindowSize), new CommonLabel(" lines")));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_SHOW_MERGE_STATUSES), flShowMergeStatuses);
    gridIterator.addPair(lbTextLogMarkers, flTextLogMarkers);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_AGENT_STATUS_PAGE), new CommonFlow(flAgentStatusColumns, new CommonLabel(" columns")));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_AGENT_STATUS_CHART_WIDTH), new CommonFlow(flAgentStatusChartWidthPix, new CommonLabel(" pixels")));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_AGENT_STATUS_CHART_HEIGHT), new CommonFlow(flAgentStatusChartHeightPix, new CommonLabel(" pixels")));

    // init property to input map
    super.inputMap.bindPropertyNameToInput(SystemProperty.AGENT_STATUS_COLUMNS, flAgentStatusColumns);
    super.inputMap.bindPropertyNameToInput(SystemProperty.AGENT_STATUS_HEIGHT_PIXELS, flAgentStatusChartHeightPix);
    super.inputMap.bindPropertyNameToInput(SystemProperty.AGENT_STATUS_WIDTH_PIXELS, flAgentStatusChartWidthPix);
    super.inputMap.bindPropertyNameToInput(SystemProperty.BRANDING, fldBranding);
    super.inputMap.bindPropertyNameToInput(SystemProperty.BUILD_MANAGER_HOST_NAME, fldManagerHost);
    super.inputMap.bindPropertyNameToInput(SystemProperty.BUILD_STATUS_REFRESH_SECS, fldRefreshRate);
    super.inputMap.bindPropertyNameToInput(SystemProperty.CHANGE_LIST_DESCRIPTION_QUOTE_LENGTH, fldChangeListDescrQuoteLength);
    super.inputMap.bindPropertyNameToInput(SystemProperty.CUSTOM_BUILD_NAME_REGEX_TEMPLATE, fldCustomBuildNameRegexTemplate);
    super.inputMap.bindPropertyNameToInput(SystemProperty.CUSTOM_BUILD_NAME_VALIDATION, fldCustomBuildNameValidation);
    super.inputMap.bindPropertyNameToInput(SystemProperty.CUSTOM_VARIABLE_NAME_REGEX_TEMPLATE, fldCustomVariableNameRegexTemplate);
    super.inputMap.bindPropertyNameToInput(SystemProperty.CUSTOM_VARIABLE_NAME_VALIDATION, fldCustomVariableNameValidation);
    super.inputMap.bindPropertyNameToInput(SystemProperty.DASHBOARD_ROW_SIZE, flDashboardRowSize);
    super.inputMap.bindPropertyNameToInput(SystemProperty.DATE_FORMAT, fldDateFormat);
    super.inputMap.bindPropertyNameToInput(SystemProperty.DATE_TIME_FORMAT, fldDateTimeFormat);
    super.inputMap.bindPropertyNameToInput(SystemProperty.DEFAULT_BUILD_NAME_VALIDATION, fldDefaultBuildNameValidation);
    super.inputMap.bindPropertyNameToInput(SystemProperty.DEFAULT_VARIABLE_NAME_VALIDATION, fldDefaultVariableNameValidation);
    super.inputMap.bindPropertyNameToInput(SystemProperty.ENABLE_ADVANCED_BUILD_SETTING, fldEnableAdvancedSettings);
    super.inputMap.bindPropertyNameToInput(SystemProperty.ENABLE_BUILD_PROMOTION, fldEnableBuildPromotion);
    super.inputMap.bindPropertyNameToInput(SystemProperty.ENABLE_PUBLISHING_COMMANDS, fldEnablePublishingCommands);
    super.inputMap.bindPropertyNameToInput(SystemProperty.ERROR_LINE_QUOTE_LENGTH, fldErrorLineQuoteLength);
    super.inputMap.bindPropertyNameToInput(SystemProperty.ERROR_LOG_QUOTE_SIZE, fldErrorWindowSize);
    super.inputMap.bindPropertyNameToInput(SystemProperty.GENERATED_URL_PROTOCOL, fldURLProtocol);
    super.inputMap.bindPropertyNameToInput(SystemProperty.OUTPUT_ENCODING, fldOutputEncoding);
    super.inputMap.bindPropertyNameToInput(SystemProperty.SHOW_BUILD_AND_CHANGE_LIST_NUMBER_ON_DASHBOARD, flShowBuildAndChangeNumber);
    super.inputMap.bindPropertyNameToInput(SystemProperty.SHOW_BUILD_INSTRUCTIONS, fldShowBuildInstructions);
    super.inputMap.bindPropertyNameToInput(SystemProperty.SHOW_IP_ADDRESS_ON_BUILD_STATUS_LIST, flShowIPAddress);
    super.inputMap.bindPropertyNameToInput(SystemProperty.SHOW_MERGE_STATUSES, flShowMergeStatuses);
    super.inputMap.bindPropertyNameToInput(SystemProperty.SHOW_NEXT_BUILD_TIME_ON_BUILD_STATUS_LIST, flShowNextBuildTime);
    super.inputMap.bindPropertyNameToInput(SystemProperty.SHOW_PARALLEL_BUILDS_IN_LIST_VIEW, flShowParallelInListView);
    super.inputMap.bindPropertyNameToInput(SystemProperty.SHOW_PROJECTS_LINK, fldShowProjectLink);
    super.inputMap.bindPropertyNameToInput(SystemProperty.SHOW_RSS_LINKS, fldShowRSSLinks);
    super.inputMap.bindPropertyNameToInput(SystemProperty.TAIL_WINDOW_SIZE, flTailWindowSize);
    super.inputMap.bindPropertyNameToInput(SystemProperty.TEXT_LOG_MARKERS, flTextLogMarkers);

    // set defaults
    flAgentStatusChartHeightPix.setValue("250");
    flAgentStatusChartWidthPix.setValue("400");
    flAgentStatusColumns.setValue("3");
    flDashboardRowSize.setValue(Integer.toString(ConfigurationConstants.DEFAULT_DASHBOARD_ROW_SIZE));
    fldChangeListDescrQuoteLength.setValue(Integer.toString(ConfigurationConstants.DEFAULT_CHANGE_LIST_DESCRIPTION_QUOTE_LENGTH));
    fldCustomBuildNameRegexTemplate.setValue(StringUtils.REGEX_STRICT_NAME);
    fldCustomBuildNameValidation.setSelected(false);
    fldCustomVariableNameRegexTemplate.setValue(StringUtils.REGEX_STRICT_NAME);
    fldCustomVariableNameValidation.setSelected(false);
    fldDefaultBuildNameValidation.setSelected(true);
    fldDefaultVariableNameValidation.setSelected(true);
    fldErrorLineQuoteLength.setValue(Integer.toString(ConfigurationConstants.DEFAULT_ERROR_LINE_QUOTE_LENGTH));
    fldErrorWindowSize.setValue(Integer.toString(ConfigurationConstants.DEFAULT_ERROR_LOG_QUOTE_SIZE));
    fldOutputEncoding.setValue(System.getProperty("file.encoding", ""));
    fldRefreshRate.setValue(SystemProperty.DEFAULT_BUILD_STATUS_REFRESH_RATE_AS_STRING);
    fldShowBuildInstructions.setChecked(true);
    flShowBuildAndChangeNumber.setChecked(true);
    flShowIPAddress.setChecked(false);
    flShowMergeStatuses.setChecked(true);
    flShowNextBuildTime.setChecked(false);
    flShowParallelInListView.setChecked(true);
    fldEnableAdvancedSettings.setChecked(true);
    flTailWindowSize.setValue(Integer.toString(ConfigurationConstants.DEFAULT_TAIL_WINDOW_SIZE));
  }


  /**
   * When called, the panel should switch to the
   * corresponding mode.
   *
   * @param modeView to set.
   */
  public void setMode(final byte modeView) {
    final boolean editable = modeView == WebUIConstants.MODE_EDIT;
    flAgentStatusChartHeightPix.setEditable(editable);
    flAgentStatusChartWidthPix.setEditable(editable);
    flAgentStatusColumns.setEditable(editable);
    flDashboardRowSize.setEditable(editable);
    fldBranding.setEditable(editable);
    fldChangeListDescrQuoteLength.setEditable(editable);
    fldCustomBuildNameRegexTemplate.setEditable(editable);
    fldCustomBuildNameValidation.setEditable(editable);
    fldCustomVariableNameRegexTemplate.setEditable(editable);
    fldCustomVariableNameValidation.setEditable(editable);
    fldDateFormat.setEditable(editable);
    fldDateTimeFormat.setEditable(editable);
    fldDefaultBuildNameValidation.setEditable(editable);
    fldDefaultBuildNameValidation.setEditable(editable);
    fldDefaultVariableNameValidation.setEditable(editable);
    fldDefaultVariableNameValidation.setEditable(editable);
    fldEnableAdvancedSettings.setEditable(editable);
    fldEnableBuildPromotion.setEditable(editable);
    fldEnablePublishingCommands.setEditable(editable);
    fldErrorLineQuoteLength.setEditable(editable);
    fldErrorWindowSize.setEditable(editable);
    fldManagerHost.setEditable(editable);
    fldOutputEncoding.setEditable(editable);
    fldRefreshRate.setEditable(editable);
    fldShowBuildInstructions.setEditable(editable);
    fldShowProjectLink.setEditable(editable);
    fldShowRSSLinks.setEditable(editable);
    fldURLProtocol.setEditable(editable);
    flShowBuildAndChangeNumber.setEditable(editable);
    flShowIPAddress.setEditable(editable);
    flShowMergeStatuses.setEditable(editable);
    flShowNextBuildTime.setEditable(editable);
    flShowParallelInListView.setEditable(editable);
    flTailWindowSize.setEditable(editable);
    flTextLogMarkers.setEditable(editable);
  }


  /**
   * Requests to load panel's data
   */
  public void load() {
    final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();
    setSystemProperties(systemCM.getSystemProperties());
  }


  public boolean save() {
    final boolean saved = super.save();
    if (saved) {
      final ServiceManager serviceManager = ServiceManager.getInstance();
      final AgentsStatusMonitor statusMonitor = serviceManager.getAgentStatusMonitor();
      statusMonitor.notifyPresentationChanged();
    }
    return saved;
  }


  /**
   * Validates reuired inputs. If there are errors, shows
   * errors.
   *
   * @return true if valid
   */
  public boolean validate() {
    final List errors = new ArrayList(3);
    WebuiUtils.validateFieldNotBlank(errors, CAPTION_BUILDMAN_HOST, fldManagerHost);
    WebuiUtils.validateFieldValidNonNegativeInteger(errors, CAPTION_ERROR_LINE_QUOTE_LENGTH, fldErrorLineQuoteLength);
    WebuiUtils.validateFieldValidNonNegativeInteger(errors, CAPTION_ERROR_LOG_QUOTE_SIZE, fldErrorWindowSize);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_REFRESH_RATE, fldRefreshRate);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_DASHBOARD_ROW_SIZE, flDashboardRowSize);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_LOG_TAIL_BUFFER_SIZE, flTailWindowSize);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_AGENT_STATUS_PAGE, flAgentStatusColumns);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_AGENT_STATUS_CHART_HEIGHT, flAgentStatusChartHeightPix);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_AGENT_STATUS_CHART_WIDTH, flAgentStatusChartWidthPix);

    // validate encoding
    if (!WebuiUtils.isBlank(fldOutputEncoding)) {
      OutputStreamWriter oswTest = null;
      try {
        oswTest = new OutputStreamWriter(new ByteArrayOutputStream(), fldOutputEncoding.getValue().trim());
        oswTest.write("");
      } catch (final UnsupportedEncodingException e) {
        errors.add("Encoding \"" + fldOutputEncoding.getValue() + "\" is not supported");
      } catch (final IOException e) {
        errors.add("Unexpected error while testing encoding: " + StringUtils.toString(e));
      } finally {
        IoUtils.closeHard(oswTest);
      }
    }

    // Validate markers
    if (!WebuiUtils.isBlank(flTextLogMarkers)) {
      try {
        final MarkerMatcherBuilder matcherBuilder = new MarkerMatcherBuilder();
        matcherBuilder.createMarkerMatcher(flTextLogMarkers.getValue());
      } catch (final PatternSyntaxException e) {
        errors.add("Invalid text log marker: " + StringUtils.toString(e));
      }
    }


    // Validate build name template
    if (fldCustomBuildNameValidation.isSelected()) {
      try {
        Pattern.compile(fldCustomBuildNameRegexTemplate.getValue());
      } catch (final PatternSyntaxException e) {
        errors.add("Invalid custom build name regex: " + StringUtils.toString(e));
      }
    }

    // Variable build name template
    if (fldCustomVariableNameValidation.isSelected()) {
      try {
        Pattern.compile(fldCustomVariableNameRegexTemplate.getValue());
      } catch (final PatternSyntaxException e) {
        errors.add("Invalid custom variable name regex: " + StringUtils.toString(e));
      }
    }

    //
    if (errors.isEmpty()) return true;
    showErrorMessage(errors);
    return false;
  }


  /**
   * Returns build manager host, or blank string if there are
   * errors or host name is blank.
   *
   * @return build manager host, or blank string if there are
   *         errors or host name is blank.
   */
  private static String getBuildManagerHost() {
    String result = "";
    try {
      result = SystemConfigurationManagerFactory.getManager().getBuildManagerHostAndPort();
    } catch (final Exception e) {
      // ignore, the resulting value is empty string
      log.warn(STR_IGNORED_EXCEPTION, e);
    }
    return result;
  }


  private static RadioButton createRadioButton(final String groupName) {
    final RadioButton radioButton = new RadioButton();
    radioButton.setGroupName(groupName);
    return radioButton;
  }
}
