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
package org.parabuild.ci.versioncontrol;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.build.BuildScriptGenerator;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.SourceControlSettingResolver;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ScheduleProperty;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.process.RemoteCommand;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractSourceControl implements SourceControl {

  private final Log LOG = LogFactory.getLog(AbstractSourceControl.class);

  static final String STRING_NO_SYNC_NOTE_AVAILABLE = "No information provided";
  protected final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
  protected final ConfigurationManager configManager = ConfigurationManager.getInstance();
  protected final int activeBuildID;
  protected final int buildID;

  /**
   * Associates an agent hosts known to this version control with a hasToCleanup flag.
   */
  private final Map<AgentHost, Boolean> agentHostCleanup = new HashMap<>(11);
  private final String checkoutDirectoryName;
  protected Map<String, SourceControlSetting> currentSettings;
  private AgentHost agentHost = null;


  protected AbstractSourceControl(final BuildConfig buildConfig) {
    this(buildConfig, null);
  }


  protected AbstractSourceControl(final BuildConfig buildConfig, final String checkoutDirectoryName) {
    this.checkoutDirectoryName = checkoutDirectoryName;
    this.buildID = buildConfig.getBuildID();
    this.activeBuildID = buildConfig.getActiveBuildID();
    this.currentSettings = getResolvedSettings();
  }


  /**
   * @return maxim change list size. If a change list bigger than
   * this size it will be cut.
   */
  public static final int maxChangeListSize() {
    return SystemConfigurationManagerFactory.getManager().getMaxChangeListSize();
  }


  /**
   * @return initial number of change lists. It is number of
   * change lists to retrieve when an automatic build runs
   * first time.
   */
  public static final int initialNumberOfChangeLists() {
    return SystemConfigurationManagerFactory.getManager().getInitialNumberOfChangeLists();
  }


  /**
   * @return maximum number of change lists.
   */
  public static final int maxNumberOfChangeLists() {
    return SystemConfigurationManagerFactory.getManager().getMaxNumberOfChangeLists();
  }


  /**
   * This GoF strategy method validates that build directory is
   * initialized according to build configuration. Implementing
   * classes may use this method to perform additional validation
   * of build directory.
   * <p/>
   * If this method returns false, initLocalCopyIfNecessary()
   * will call checkoutLatest() to populate build dir.
   *
   * @return build directory is initialized according to build
   * configuration.
   * @see AbstractSourceControl#initLocalCopyIfNecessary()
   * @see SourceControl#checkoutLatest()
   */
  public abstract boolean isBuildDirInitialized() throws IOException, BuildException, AgentFailureException;


  /**
   * @return Map with a shell variable name as a key and variable
   * value as value. The shell variables will be made
   * available to the build commands.
   * @see BuildScriptGenerator#addVariables(Map)
   */
  public abstract Map getShellVariables() throws IOException, AgentFailureException;


  public int removeLabels(final String[] labels) throws BuildException, CommandStoppedException, AgentFailureException {
    // TODO: make this method abstract
    return 0;
  }


  /**
   * Returns text description of a command to be used by a
   * customer to sync to a given changelist. This is a default
   * implementation.
   *
   * @param changeListID the ID of the change list to generate text description of a command to be used by a customer
   *                     to sync to a given changelist.
   */
  public String getSyncCommandNote(final int changeListID) throws AgentFailureException {
    return STRING_NO_SYNC_NOTE_AVAILABLE;
  }


  /**
   * Returns build ID associated with this SourceControl
   */
  public final int getBuildID() {
    return buildID;
  }


  /**
   * Shortcut method to get a VCS setting from the object's
   * settings set by name
   *
   * @param settingName to retrieve
   * @return setting value, or null if not found
   */
  public final String getSettingValue(final String settingName) {
    return getSettingValue(settingName, null);
  }


  /**
   * Shortcut method to get a VCS setting from the object's
   * settings set by name
   *
   * @param settingName  to retrieve
   * @param defaultValue if setting is not provided
   * @return setting value, or defaultValue if not found or
   * blank
   */
  public final String getSettingValue(final String settingName, final String defaultValue) {
    final SourceControlSetting setting = currentSettings.get(settingName);
    if (setting == null || StringUtils.isBlank(setting.getPropertyValue())) {
      return defaultValue;
    }
    return setting.getPropertyValue();
  }


  /**
   * Shortcut method to get a VCS setting from the object's
   * settings set by name
   *
   * @param settingName  to retrieve
   * @param defaultValue if setting is not provided
   * @return setting value, or default value if not found or not
   * set.
   */
  public final int getSettingValue(final String settingName, final int defaultValue) {
    return Integer.parseInt(getSettingValue(settingName, Integer.toString(defaultValue)));
  }


  /**
   * Shortcut method to get a VCS setting from the object's
   * settings set by name
   *
   * @param settingName  to retrieve
   * @param defaultValue if setting is not provided
   * @return setting value, or default value if not found or not
   * set.
   */
  public final byte getSettingValue(final String settingName, final byte defaultValue) {
    return Byte.parseByte(getSettingValue(settingName, Byte.toString(defaultValue)));
  }


  /**
   * Deletes version control files. This method should not throw
   * any exceptions for normally it's called from finally block.
   */
  protected final void cleanup(final RemoteCommand command) {
    try {
      if (command == null) {
        return;
      }
      final String sp = SystemConfigurationManagerFactory.getManager().getSystemPropertyValue(SystemProperty.KEEP_SCM_LOGS, "false");
      if ("true".equalsIgnoreCase(sp)) {
        return;
      }
      command.cleanup();
    } catch (final Exception e) {
      final org.parabuild.ci.error.Error error = new org.parabuild.ci.error.Error("Error while cleaning up temporary version control files");
      error.setErrorLevel(org.parabuild.ci.error.Error.ERROR_LEVEL_WARNING);
      error.setSubsystemName(org.parabuild.ci.error.Error.ERROR_SUBSYSTEM_SCM);
      error.setDetails(e);
      error.setSendEmail(false);
      error.setBuildID(buildID);
      errorManager.reportSystemError(error);
    }
  }


  /**
   * Deletes content of checkout directory if flag is set to
   * true.
   */
  protected final void cleanupLocalCopyIfNecessary() throws IOException, AgentFailureException {
    if (isHasToCleanup(agentHost)) {
      cleanupLocalCopy();
    }
  }


  /**
   * Cleans up local copy. The default version just deletes files
   * from the checkout directory. Classes implementing
   * AbstractSourceControl may overwrite this method to deliver
   * specific cleanup logic.
   *
   * @throws IOException if an I/O error occurred.
   * @see Agent#emptyCheckoutDir
   */
  public boolean cleanupLocalCopy() throws IOException, AgentFailureException {
    final Agent agent = getCheckoutDirectoryAwareAgent();
    if (LOG.isDebugEnabled()) {
      LOG.debug("Cleaning up checkout directory \"" + agent.getCheckoutDirName() + '\"');
    }
    final boolean cleanedUp = agent.emptyCheckoutDir();
    if (cleanedUp) {
      // Mark host as not needing to clean up
      setHasToCleanUp(agent.getHost(), false);
    }
    return cleanedUp;
  }


  /**
   * Requests source control system to find a native change
   * list number. The found change list number is stored in
   * the list of pending change lists in the database.
   *
   * @param nativeChangeListNumber String native change list
   *                               number. Other version control systems may store
   *                               information other then change lists.
   * @return new changelist ID
   */
  public int getNativeChangeList(final String nativeChangeListNumber) throws IOException, CommandStoppedException, BuildException, AgentFailureException {
    throw new IllegalStateException("Support for the native change lists number has not been implemented for this version control system yet");
  }


  /**
   * Initializes local copy if necessary.
   * <p/>
   * Certain VCS commands, expect local copy to be present to
   * operate successfully. This method will get the latest from
   * VCS thus preparing it for further operations.
   */
  protected final void initLocalCopyIfNecessary() throws BuildException, IOException, CommandStoppedException, AgentFailureException {
    final Agent agent = getCheckoutDirectoryAwareAgent();
    if (agent.checkoutDirIsEmpty()) {
      // already empty, checkout latest
      if (LOG.isDebugEnabled()) {
        LOG.debug("Has to initialize checkout directory \"" + agent.getCheckoutDirName() + '\"');
      }
      checkoutLatest();
    } else {
      if (!isBuildDirInitialized()) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Has to cleanup and initialize checkout directory \"" + agent.getCheckoutDirName() + '\"');
        }
        cleanupLocalCopy();
        checkoutLatest();
      }
    }
  }


  /**
   * Helper method to validate change list ID
   *
   * @param changeListID to validate
   * @throws BuildException if not
   *                        valid
   */
  public final void validateChangeListID(final int changeListID) throws BuildException {
    final BuildConfig buildConfig = configManager.getBuildConfiguration(buildID);
    // REVIEWME: here we just skip validation - deliver normal validation
    if (buildConfig.getSourceControl() == BuildConfig.SCM_REFERENCE) {
      return;
    }

    // build configuration exists for this change list?
    if (!configManager.isChangeListBelongsToBuild(changeListID, activeBuildID)) {
      throw new BuildException("Change list " + changeListID
              + " does not belong to build ID " + activeBuildID, agentHost);
    }
  }


  /**
   * Creates a agent that takes in account current
   * checkout directory setting.
   *
   * @return agent that takes in account current
   * checkout directory setting.
   */
  protected final Agent getCheckoutDirectoryAwareAgent() throws IOException {
    final String checkoutDirTemplate = checkoutDirectoryName == null ? getSettingValue(SourceControlSetting.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE) : checkoutDirectoryName;
    final BuildConfig config = ConfigurationManager.getInstance().getBuildConfiguration(activeBuildID);
//    if (LOG.isDebugEnabled()) LOG.debug("agentHost: " + agentHost);
    return AgentManager.getInstance().createAgent(activeBuildID, checkoutDirTemplate, config.getBuildName(), agentHost, activeBuildID);
  }


  /**
   * Returns agent host or null if not set.
   *
   * @return agent host or null if not set.
   */
  public final AgentHost getAgentHost() {
    return agentHost;
  }


  /**
   * Sets agent host this source control should operate on.
   * <p/>
   * This method should be called first before any other method is called.
   *
   * @param agentHost this source control should operate on.
   */
  public void setAgentHost(final AgentHost agentHost) {
    if (this.agentHost != null && !agentHost.equals(this.agentHost) && isCleanCheckoutOnAgentChange()) {
      setHasToCleanUp(agentHost, true);
    }
    this.agentHost = agentHost;
  }


  /**
   * {@inheritDoc}
   */
  public Map getBuildRunAttributes() throws IOException, AgentFailureException {
    return Collections.emptyMap();
  }


  /**
   * Marks all agent hosts as required.
   */
  protected final void setHasToCleanUp() {
    // Set cleanup flags for each host to true
    for (final Object agentHost : agentHostCleanup.keySet()) {
      setHasToCleanUp((AgentHost) agentHost, true);
    }
  }


  /**
   * Sets a cleanup flag for a given host.
   *
   * @param agentHost    agent's host
   * @param hasToCleanup indicates if the build workplace has to cleanup.
   */
  private void setHasToCleanUp(final AgentHost agentHost, final boolean hasToCleanup) {
    agentHostCleanup.put(agentHost, Boolean.valueOf(hasToCleanup));
  }


  /**
   * Returns true if a given agent host has to clean up its workspace.
   *
   * @param agentHost to check
   * @return true if a given agent host has to clean up it's workspace.
   */
  private boolean isHasToCleanup(final AgentHost agentHost) {
    return agentHostCleanup.computeIfAbsent(agentHost, k -> Boolean.FALSE);
  }


  /**
   * Returns true if the VCS should do a clean checkout when agent changes.
   *
   * @return true if the VCS should do a clean checkout when agent changes.
   */
  private boolean isCleanCheckoutOnAgentChange() {
    return configManager.getScheduleSettingValue(activeBuildID, ScheduleProperty.CLEAN_CHECKOUT_ON_AGENT_CHANGE,
            ScheduleProperty.OPTION_UNCHECKED).equals(ScheduleProperty.OPTION_CHECKED);
  }


  private final String getBuildName() {
    final BuildConfig config = configManager.getBuildConfiguration(buildID);
    if (null != config) {
      return config.getBuildName();
    } else {
      return "build-not-found-" + buildID;
    }
  }


  /**
   * Returns resolved source control settings.
   *
   * @return resolved source control settings.
   */
  protected final Map<String, SourceControlSetting> getResolvedSettings() {

    // Try to determine agent host name
    final String agentHostName;
    if (agentHost == null) {

      agentHostName = "null";
    } else {

      try {
        final Agent agent = AgentManager.getInstance().createAgent(activeBuildID, null, getBuildName(), agentHost, activeBuildID);
        agentHostName = agent.getLocalHostName();
      } catch (final RuntimeException e) {

        throw e;
      } catch (final Exception e) {

        throw new IllegalStateException(e.toString(), e);
      }
    }

    // Create resolver
    final SourceControlSettingResolver resolver = new SourceControlSettingResolver(getBuildName(), buildID, agentHostName);

    // Load settings from the database
    final Map rawSettings = configManager.getEffectiveSourceControlSettingsAsMap(buildID);

    // Resolve
    final Map<String, SourceControlSetting> resolvedSettings = new HashMap<>(rawSettings.size());
    for (final Object sourceControlSettingObject : rawSettings.values()) {
      final SourceControlSetting setting = (SourceControlSetting) sourceControlSettingObject;
      final String name = setting.getPropertyName();
      final String value = setting.getPropertyValue();
      if (!StringUtils.isBlank(value)) {
        // Resolve and set back
        try {
          final String resolvedValue = resolver.resolve(value);
          setting.setPropertyValue(resolvedValue);
        } catch (final ValidationException e) {
          final Error error = new Error("Problem resolving source control setting: " + name + ", value: " + value);
          error.setBuildID(activeBuildID);
          error.setSubsystemName(Error.ERROR_SUBSYSTEM_SCM);
          errorManager.reportSystemError(error);
        }
      }
      resolvedSettings.put(name, setting);
    }
    return resolvedSettings;
  }


  /**
   * Replaces current settings with new settings.
   *
   * @param newSettings the new settings to use.
   */
  protected final void replaceCurrentSettings(final Map<String, SourceControlSetting> newSettings) {
    currentSettings.clear();
    currentSettings.putAll(newSettings);
  }


  public String toString() {
    return "AbstractSourceControl{" +
            "errorManager=" + errorManager +
            ", configManager=" + configManager +
            ", agentHostCleanup=" + agentHostCleanup +
            ", activeBuildID=" + activeBuildID +
            ", buildID=" + buildID +
            ", currentSettings=" + currentSettings +
            ", checkoutDirectoryName='" + checkoutDirectoryName + '\'' +
            ", agentHost=" + agentHost +
            '}';
  }
}
