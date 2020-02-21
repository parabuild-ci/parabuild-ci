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
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.services.LoggingService;
import org.parabuild.ci.services.ServiceManager;
import org.parabuild.ci.webui.admin.AbstractSystemConfigPanel;
import org.parabuild.ci.webui.common.CommonField;
import org.parabuild.ci.webui.common.CommonFieldLabel;
import org.parabuild.ci.webui.common.CommonFlow;
import org.parabuild.ci.webui.common.CommonLabel;
import org.parabuild.ci.webui.common.GridIterator;
import org.parabuild.ci.webui.common.HourDropDown;
import org.parabuild.ci.webui.common.RequiredFieldMarker;
import org.parabuild.ci.webui.common.WebUIConstants;
import org.parabuild.ci.webui.common.WebuiUtils;
import viewtier.ui.CheckBox;
import viewtier.ui.DropDown;
import viewtier.ui.Field;
import viewtier.ui.Label;
import viewtier.ui.Panel;
import viewtier.ui.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


/**
 * System stability config panel
 */
public final class StabilityConfigPanel extends AbstractSystemConfigPanel {

  private static final long serialVersionUID = 4337715912625073404L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(StabilityConfigPanel.class); // NOPMD

  private static final int DEFAULT_NEW_BUILD_STEP_TIMEOUT = 120;
  private static final int DEFAULT_NEW_VCS_TIMEOUT = 120;
  private static final int MINIMUM_BUILD_STEP_TIMEOUT = 30;
  private static final int MINIMUM_VCS_TIMEOUT = 30;

  private static final String DEFAULT_MAX_COOLDOWN_TRIES_STR = Integer.toString(SystemProperty.DEFAULT_MAX_COOLDOWN_TRIES);
  private static final String DEFAULT_MAX_CHANGELIST_SIZE_STR = Integer.toString(SystemProperty.DEFAULT_MAX_CHANGE_LIST_SIZE);
  private static final String DEFAULT_MAX_NUMBER_OF_CHANGE_LISTS = Integer.toString(SystemProperty.DEFAULT_MAX_NUMBER_OF_CHANGE_LISTS);
  private static final String DEFAULT_NEW_BUILD_STEP_TIMEOUT_STR = Integer.toString(DEFAULT_NEW_BUILD_STEP_TIMEOUT);
  private static final String DEFAULT_NEW_VCS_TIMEOUT_STR = Integer.toString(DEFAULT_NEW_VCS_TIMEOUT);
  private static final String DEFAULT_MINIMUM_RESULTS_RETENTION_STR = Integer.toString(SystemProperty.DEFAULT_MINIMUM_RESULTS_RETENTION);
  private static final String DEFAULT_INITIAL_NUMBER_OF_CHANGELISTS_STR = Integer.toString(1);

  private static final String CAPTION_ALLOW_DELETING_PROJECTS = "Allow deleting projects: ";
  private static final String CAPTION_AUTOMATIC_AGENT_UPGRADE = "Automatic agent upgrade:";
  private static final String CAPTION_CHECK_CUSTOM_CHECKOUT_DIRS_FOR_DUPLICATES = "Check custom checkout directories for duplicates: ";
  private static final String CAPTION_DEFAULT_BUILD_STEP_TIMEOUT = "Default build step timeout: ";
  private static final String CAPTION_DEFAULT_MAX_COOLDOWN_TRIES = "Maximum cool-down attempts: ";
  private static final String CAPTION_DEFAULT_VCS_TIMEOUT = "Default version control timeout: ";
  private static final String CAPTION_ENABLE_DEBUGING = "Enable debug logging: ";
  private static final String CAPTION_ENABLE_LOGGING_ARCHIVE_CLEANUP_TIMING = "Enable logging archive cleanup timing: ";
  private static final String CAPTION_ENABLE_NO_CHECKOUT_BUILDS = "Enable no-checkout builds: ";
  private static final String CAPTION_IF_PATTERNS_ARE_PRESENT_IN_ERROR_MESSAGE = "if regex patterns are present in error message: ";
  private static final String CAPTION_INITIAL_NUMBER_OF_CHANGELISTS = "Initial number of change lists: ";
  private static final String CAPTION_MAX_CHANGELIST_SIZE = "Maximum change list size: ";
  private static final String CAPTION_MAX_NUMBER_OF_CHANGE_LISTS = "Maximum number of change lists: ";
  private static final String CAPTION_MAX_PARALLEL_UPGRADES = "Maximum parallel upgrades:";
  private static final String CAPTION_MINIMUM_RESULTS_RETENTION = "Minimum build results retention: ";
  private static final String CAPTION_QUEUE_SERIALIZED_BUILDS = "Queue serialized builds:";
  private static final String CAPTION_RESPECT_INTERMEDIATE_STEP_FAILURE = "Respect intermediate build steps failures: ";
  private static final String CAPTION_RETRY_VERSION_CONTROL_COMMANDS = "Retry version control commands: ";
  private static final String CAPTION_ROUND_ROBIN_LOAD_BALANCING = "Round-robin load balancing: ";
  private static final String CAPTION_SCHEDULE_GAP = "Schedule gap: ";
  private static final String CAPTION_SERIALIZE_BUILDS = "Serialize automatic builds:";
  private static final String CAPTION_USE_CHANGE_LIST_NUMBERS_AS_BUILD_NUMBERS = "Use change list number as build number: ";
  private static final String CAPTION_USE_XML_LOG_FORMAT_FOR_SUBVERSION = "Use XML log format for Subversion: ";

  private final CheckBox cbAllowDeletingProjects = new CheckBox(); // NOPMD
  private final CheckBox cbAutomaticAgentUpgrade = new CheckBox(); // NOPMD
  private final CheckBox cbChangeListNumbersAsBuildNumbers = new CheckBox(); // NOPMD
  private final CheckBox cbCheckCustomCheckoutDirsForDuplicates = new CheckBox(); // NOPMD
  private final CheckBox cbEnableDebugging = new CheckBox(); // NOPMD
  private final CheckBox cbEnableLoggingArchiveCleanupTiming = new CheckBox(); // NOPMD
  private final CheckBox cbEnabledNoCheckoutBuilds = new CheckBox();
  private final CheckBox cbEnableScheduleGap = new CheckBox(); // NOPMD
  private final CheckBox cbQueueSerializedBuilds = new CheckBox(); // NOPMD
  private final CheckBox cbRespectIntermediateStepFailure = new CheckBox(); // NOPMD
  private final CheckBox cbRoundRobin = new CheckBox(); // NOPMD
  private final CheckBox cbSerializedBuilds = new CheckBox(); // NOPMD
  private final CheckBox cbUseXMLLogFormatForSubversion = new CheckBox();
  private final DropDown flScheduleGapFrom = new HourDropDown(); // NOPMD
  private final DropDown flScheduleGapTo = new HourDropDown(); // NOPMD
  private final Field fldDefaultBuildStepTimeout = new CommonField("default-build-step-timeout", 6, 6); // NOPMD
  private final Field fldDefaultMaxCooldownTries = new CommonField("max-cooldown-tries", 2, 3); // NOPMD
  private final Field fldInitialNumberOfChangeLists = new CommonField("initial-number-of-changelists", 6, 6); // NOPMD
  private final Field fldMaxChangeListSize = new CommonField("max-changelist-size", 3, 3); // NOPMD
  private final Field fldMaxNumberOfChangeLists = new CommonField("max-number-of-changelists", 6, 6); // NOPMD
  private final Field fldMaxParallelAgentUpgrades = new CommonField("max-parallel-agen-upgrades", 3, 3); // NOPMD
  private final Field fldMinResultsRetention = new CommonField("min-result-retention", 4, 4); // NOPMD
  private final Field fldVCSTimeout = new CommonField("vcs-timeout", 6, 6); // NOPMD
  private final Field flRetryVCSCommandInterval = new CommonField("retry-vcs-cmd-interval", 4, 4);  // NOPMD
  private final Field flRetryVCSCommandTimes = new CommonField("retry-vcs-cmd-times", 4, 4);  // NOPMD
  private final Text flRetryVCSCommandPatterns = new Text(50, 6);  // NOPMD


  public StabilityConfigPanel() {

    // get MessagePanel's user content panel
    final Panel content = super.getUserPanel();

    // create grid, add components
    final GridIterator gridIterator = new GridIterator(content, 2);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_ALLOW_DELETING_PROJECTS), cbAllowDeletingProjects);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_SCHEDULE_GAP), new CommonFlow(cbEnableScheduleGap, new CommonLabel(" enabled from "), flScheduleGapFrom, new CommonLabel(" to "), flScheduleGapTo));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_DEFAULT_VCS_TIMEOUT), new RequiredFieldMarker(new CommonFlow(fldVCSTimeout, new CommonLabel(" minutes"))));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_DEFAULT_BUILD_STEP_TIMEOUT), new RequiredFieldMarker(new CommonFlow(fldDefaultBuildStepTimeout, new CommonLabel(" minutes"))));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_MAX_CHANGELIST_SIZE), new RequiredFieldMarker(new CommonFlow(fldMaxChangeListSize, new CommonLabel(" files"))));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_INITIAL_NUMBER_OF_CHANGELISTS), new RequiredFieldMarker(fldInitialNumberOfChangeLists));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_MAX_NUMBER_OF_CHANGE_LISTS), fldMaxNumberOfChangeLists);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_DEFAULT_MAX_COOLDOWN_TRIES), new RequiredFieldMarker(fldDefaultMaxCooldownTries));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_USE_XML_LOG_FORMAT_FOR_SUBVERSION), new RequiredFieldMarker(cbUseXMLLogFormatForSubversion));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_MINIMUM_RESULTS_RETENTION), new RequiredFieldMarker(new CommonFlow(fldMinResultsRetention, new CommonLabel(" days"))));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_ENABLE_NO_CHECKOUT_BUILDS), new RequiredFieldMarker(cbEnabledNoCheckoutBuilds));
    gridIterator.addPair(new CommonFieldLabel(CAPTION_SERIALIZE_BUILDS), cbSerializedBuilds);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_QUEUE_SERIALIZED_BUILDS), cbQueueSerializedBuilds);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_RESPECT_INTERMEDIATE_STEP_FAILURE), cbRespectIntermediateStepFailure);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_USE_CHANGE_LIST_NUMBERS_AS_BUILD_NUMBERS), cbChangeListNumbersAsBuildNumbers);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_RETRY_VERSION_CONTROL_COMMANDS), new CommonFlow(flRetryVCSCommandTimes, new CommonLabel(" times with "), flRetryVCSCommandInterval, new CommonLabel(" seconds interval")));
    gridIterator.addPair(new Label(""), new CommonLabel(CAPTION_IF_PATTERNS_ARE_PRESENT_IN_ERROR_MESSAGE));
    gridIterator.addPair(new Label(""), flRetryVCSCommandPatterns);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_AUTOMATIC_AGENT_UPGRADE), cbAutomaticAgentUpgrade);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_MAX_PARALLEL_UPGRADES), fldMaxParallelAgentUpgrades);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_ROUND_ROBIN_LOAD_BALANCING), cbRoundRobin);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_CHECK_CUSTOM_CHECKOUT_DIRS_FOR_DUPLICATES), cbCheckCustomCheckoutDirsForDuplicates);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_ENABLE_DEBUGING), cbEnableDebugging);
    gridIterator.addPair(new CommonFieldLabel(CAPTION_ENABLE_LOGGING_ARCHIVE_CLEANUP_TIMING), cbEnableLoggingArchiveCleanupTiming);

    // init property to input map
    super.inputMap.bindPropertyNameToInput(SystemProperty.ALLOW_DELETING_PROJECTS, cbAllowDeletingProjects);
    super.inputMap.bindPropertyNameToInput(SystemProperty.AUTOMATIC_AGENT_UPGRADE, cbAutomaticAgentUpgrade);
    super.inputMap.bindPropertyNameToInput(SystemProperty.CHECK_CUSTOM_CHECKOUT_DIRS_FOR_DUPLICATES, cbCheckCustomCheckoutDirsForDuplicates);
    super.inputMap.bindPropertyNameToInput(SystemProperty.DEFAULT_BUILD_STEP_TIMEOUT, fldDefaultBuildStepTimeout);
    super.inputMap.bindPropertyNameToInput(SystemProperty.DEFAULT_VCS_TIMEOUT, fldVCSTimeout);
    super.inputMap.bindPropertyNameToInput(SystemProperty.ENABLE_DEBUGGING, cbEnableDebugging);
    super.inputMap.bindPropertyNameToInput(SystemProperty.ENABLE_LOGGING_ARCHIVE_CLEANUP_TIMING, cbEnableLoggingArchiveCleanupTiming);
    super.inputMap.bindPropertyNameToInput(SystemProperty.ENABLE_SCHEDULE_GAP, cbEnableScheduleGap);
    super.inputMap.bindPropertyNameToInput(SystemProperty.INITIAL_NUMBER_OF_CHANGELISTS, fldInitialNumberOfChangeLists);
    super.inputMap.bindPropertyNameToInput(SystemProperty.MAX_CHANGELIST_SIZE, fldMaxChangeListSize);
    super.inputMap.bindPropertyNameToInput(SystemProperty.MAX_COOLDOWN_TRIES, fldDefaultMaxCooldownTries);
    super.inputMap.bindPropertyNameToInput(SystemProperty.MAX_NUMBER_OF_CHANGE_LISTS, fldMaxNumberOfChangeLists);
    super.inputMap.bindPropertyNameToInput(SystemProperty.MAX_PARALLEL_UPGRADES, fldMaxParallelAgentUpgrades);
    super.inputMap.bindPropertyNameToInput(SystemProperty.MINIMUM_RESULTS_RETENTION, fldMinResultsRetention);
    super.inputMap.bindPropertyNameToInput(SystemProperty.QUEUE_SERIALIZED_BUILDS, cbQueueSerializedBuilds);
    super.inputMap.bindPropertyNameToInput(SystemProperty.RESPECT_INTERMEDIATE_STEP_FAILURE, cbRespectIntermediateStepFailure);
    super.inputMap.bindPropertyNameToInput(SystemProperty.RETRY_VCS_COMMAND_INTERVAL, flRetryVCSCommandInterval);
    super.inputMap.bindPropertyNameToInput(SystemProperty.RETRY_VCS_COMMAND_PATTERNS, flRetryVCSCommandPatterns);
    super.inputMap.bindPropertyNameToInput(SystemProperty.RETRY_VCS_COMMAND_TIMES, flRetryVCSCommandTimes);
    super.inputMap.bindPropertyNameToInput(SystemProperty.ROUND_ROBIN_LOAD_BALANCING, cbRoundRobin);
    super.inputMap.bindPropertyNameToInput(SystemProperty.SCHEDULE_GAP_FROM, flScheduleGapFrom);
    super.inputMap.bindPropertyNameToInput(SystemProperty.SCHEDULE_GAP_TO, flScheduleGapTo);
    super.inputMap.bindPropertyNameToInput(SystemProperty.SERIALIZE_BUILDS, cbSerializedBuilds);
    super.inputMap.bindPropertyNameToInput(SystemProperty.USE_CHANGELIST_NUMBER_AS_BUILD_NUMBER, cbChangeListNumbersAsBuildNumbers);
    super.inputMap.bindPropertyNameToInput(SystemProperty.USE_XML_LOG_FORMAT_FOR_SUBVERSION, cbUseXMLLogFormatForSubversion);
    super.inputMap.bindPropertyNameToInput(SystemProperty.ENABLE_NO_CHECKOUT_BUILDS, cbEnabledNoCheckoutBuilds);

    // defaults
    cbAutomaticAgentUpgrade.setChecked(true);
    cbCheckCustomCheckoutDirsForDuplicates.setChecked(true);
    cbRespectIntermediateStepFailure.setChecked(true);
    cbQueueSerializedBuilds.setChecked(true);
    cbUseXMLLogFormatForSubversion.setChecked(true);
    fldDefaultBuildStepTimeout.setValue(DEFAULT_NEW_BUILD_STEP_TIMEOUT_STR);
    fldDefaultMaxCooldownTries.setValue(DEFAULT_MAX_COOLDOWN_TRIES_STR);
    fldInitialNumberOfChangeLists.setValue(DEFAULT_INITIAL_NUMBER_OF_CHANGELISTS_STR);
    fldMaxChangeListSize.setValue(DEFAULT_MAX_CHANGELIST_SIZE_STR);
    fldMaxNumberOfChangeLists.setValue(DEFAULT_MAX_NUMBER_OF_CHANGE_LISTS);
    fldMinResultsRetention.setValue(DEFAULT_MINIMUM_RESULTS_RETENTION_STR);
    fldVCSTimeout.setValue(DEFAULT_NEW_VCS_TIMEOUT_STR);
    flRetryVCSCommandInterval.setValue(SystemProperty.DEFAULT_RETRY_VCS_COMMAND_INTERVAL);
    flRetryVCSCommandPatterns.setValue(SystemProperty.DEFAULT_RETRY_VCS_COMMAND_PATTERNS);
    flRetryVCSCommandTimes.setValue(SystemProperty.DEFAULT_RETRY_VCS_COMMAND_TIMES);
  }


  /**
   * When called, the panel switches to the corresponding
   * mode.
   *
   * @param viewMode to set.
   */
  public void setMode(final byte viewMode) {
    final boolean editable = viewMode == WebUIConstants.MODE_EDIT;
    cbAllowDeletingProjects.setEditable(editable);
    cbAutomaticAgentUpgrade.setEditable(editable);
    cbChangeListNumbersAsBuildNumbers.setEditable(editable);
    cbCheckCustomCheckoutDirsForDuplicates.setEditable(editable);
    cbEnableLoggingArchiveCleanupTiming.setEditable(editable);
    cbEnableDebugging.setEditable(editable);
    cbEnabledNoCheckoutBuilds.setEditable(editable);
    cbEnableScheduleGap.setEditable(editable);
    cbQueueSerializedBuilds.setEditable(editable);
    cbRespectIntermediateStepFailure.setEditable(editable);
    cbRoundRobin.setEditable(editable);
    cbSerializedBuilds.setEditable(editable);
    cbUseXMLLogFormatForSubversion.setEditable(editable);
    fldDefaultBuildStepTimeout.setEditable(editable);
    fldDefaultMaxCooldownTries.setEditable(editable);
    fldInitialNumberOfChangeLists.setEditable(editable);
    fldMaxChangeListSize.setEditable(editable);
    fldMaxNumberOfChangeLists.setEditable(editable);
    fldMinResultsRetention.setEditable(editable);
    fldVCSTimeout.setEditable(editable);
    flRetryVCSCommandInterval.setEditable(editable);
    flRetryVCSCommandPatterns.setEditable(editable);
    flRetryVCSCommandTimes.setEditable(editable);
    flScheduleGapFrom.setEditable(editable);
    flScheduleGapTo.setEditable(editable);
    fldMaxParallelAgentUpgrades.setEditable(editable);
  }


  /**
   * Requests to load panel's data
   */
  public void load() {
    final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();
    setSystemProperties(systemCM.getSystemProperties());
  }


  /**
   * Validates reuired inputs. If there are errors, shows
   * errors.
   *
   * @return true if valid
   */
  public boolean validate() {
    final List errors = new ArrayList(3);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_DEFAULT_BUILD_STEP_TIMEOUT, fldDefaultBuildStepTimeout);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_DEFAULT_VCS_TIMEOUT, fldVCSTimeout);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_MAX_CHANGELIST_SIZE, fldMaxChangeListSize);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_INITIAL_NUMBER_OF_CHANGELISTS, fldInitialNumberOfChangeLists);
    WebuiUtils.validateFieldValidNonNegativeInteger(errors, CAPTION_DEFAULT_MAX_COOLDOWN_TRIES, fldDefaultMaxCooldownTries);
    WebuiUtils.validateFieldValidNonNegativeInteger(errors, CAPTION_RETRY_VERSION_CONTROL_COMMANDS + " times", flRetryVCSCommandTimes);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_RETRY_VERSION_CONTROL_COMMANDS + " interval", flRetryVCSCommandInterval);
    WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_MAX_PARALLEL_UPGRADES, fldMaxParallelAgentUpgrades);

    // Validate command retry patterns
    final String value = flRetryVCSCommandPatterns.getValue();
    final List patterns = StringUtils.multilineStringToList(value);
    for (int i = 0; i < patterns.size(); i++) {
      final String pattern = (String) patterns.get(i);
      try {
        Pattern.compile(pattern);
      } catch (final Exception e) {
        errors.add("Pattern invalid: " + pattern);
      }
    }

    if (errors.isEmpty()) {
      validateValueGreaterThan(errors, CAPTION_DEFAULT_BUILD_STEP_TIMEOUT, MINIMUM_BUILD_STEP_TIMEOUT, fldDefaultBuildStepTimeout);
      validateValueGreaterThan(errors, CAPTION_DEFAULT_VCS_TIMEOUT, MINIMUM_VCS_TIMEOUT, fldVCSTimeout);
    }

    if (!WebuiUtils.isBlank(fldMaxNumberOfChangeLists)) {
      WebuiUtils.validateFieldValidPositiveInteger(errors, CAPTION_MAX_CHANGELIST_SIZE, fldMaxChangeListSize);
    }

    //
    if (errors.isEmpty()) {
      return true;
    }
    showErrorMessage(errors);
    return false;
  }


  public boolean save() {
    final boolean saved = super.save();
    if (saved) {
      // Special treatment for debug logging.
      final LoggingService loggingService = ServiceManager.getInstance().getLoggingService();
      loggingService.reInitLog4j(cbEnableDebugging.isChecked());

      // Update agent manager's pool size
      AgentManager.getInstance().setMaximumParallelAgentUpgrades(Integer.parseInt(fldMaxParallelAgentUpgrades.getValue()));
    }
    return saved;
  }


  private static void validateValueGreaterThan(final List errors, final String what, final int minimum, final Field fld) {
    final String value = fld.getValue();
    if (Integer.parseInt(value) < minimum) {
      errors.add('\"' + what + "\"  cannot be less than " + minimum + " minutes");
    }
  }


  public String toString() {
    return "StabilityConfigPanel{" +
            "cbAllowDeletingProjects=" + cbAllowDeletingProjects +
            ", cbAutomaticAgentUpgrade=" + cbAutomaticAgentUpgrade +
            ", cbChangeListNumbersAsBuildNumbers=" + cbChangeListNumbersAsBuildNumbers +
            ", cbCheckCustomCheckoutDirsForDuplicates=" + cbCheckCustomCheckoutDirsForDuplicates +
            ", cbEnableDebugging=" + cbEnableDebugging +
            ", cbEnableLoggingArchiveCleanupTiming=" + cbEnableLoggingArchiveCleanupTiming +
            ", cbEnabledNoCheckoutBuilds=" + cbEnabledNoCheckoutBuilds +
            ", cbEnableScheduleGap=" + cbEnableScheduleGap +
            ", cbQueueSerializedBuilds=" + cbQueueSerializedBuilds +
            ", cbRespectIntermediateStepFailure=" + cbRespectIntermediateStepFailure +
            ", cbRoundRobin=" + cbRoundRobin +
            ", cbSerializedBuilds=" + cbSerializedBuilds +
            ", cbUseXMLLogFormatForSubversion=" + cbUseXMLLogFormatForSubversion +
            ", flScheduleGapFrom=" + flScheduleGapFrom +
            ", flScheduleGapTo=" + flScheduleGapTo +
            ", fldDefaultBuildStepTimeout=" + fldDefaultBuildStepTimeout +
            ", fldDefaultMaxCooldownTries=" + fldDefaultMaxCooldownTries +
            ", fldInitialNumberOfChangeLists=" + fldInitialNumberOfChangeLists +
            ", fldMaxChangeListSize=" + fldMaxChangeListSize +
            ", fldMaxNumberOfChangeLists=" + fldMaxNumberOfChangeLists +
            ", fldMaxParallelAgentUpgrades=" + fldMaxParallelAgentUpgrades +
            ", fldMinResultsRetention=" + fldMinResultsRetention +
            ", fldVCSTimeout=" + fldVCSTimeout +
            ", flRetryVCSCommandInterval=" + flRetryVCSCommandInterval +
            ", flRetryVCSCommandTimes=" + flRetryVCSCommandTimes +
            ", flRetryVCSCommandPatterns=" + flRetryVCSCommandPatterns +
            "} " + super.toString();
  }
}
