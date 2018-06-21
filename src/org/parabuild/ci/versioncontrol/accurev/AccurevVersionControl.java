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
package org.parabuild.ci.versioncontrol.accurev;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.versioncontrol.AbstractSourceControl;
import org.parabuild.ci.versioncontrol.ExclusionPathFinder;
import org.parabuild.ci.versioncontrol.SourceControlSettingChangeDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * AccurevVersionControl
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 4, 2009 8:02:27 PM
 */
public final class AccurevVersionControl extends AbstractSourceControl {

  private static final Log LOG = LogFactory.getLog(AccurevVersionControl.class); // NOPMD
  private static final int INITIAL_CANGE_LIST_CAPACITY = 11;


  public AccurevVersionControl(final BuildConfig buildConfig) {
    super(buildConfig);
  }


  AccurevVersionControl(final BuildConfig buildConfig, final String checkoutDirectoryName) {
    super(buildConfig, checkoutDirectoryName);
  }


  /**
   * {@inheritDoc}
   */
  public boolean isBuildDirInitialized() throws IOException, BuildException, AgentFailureException {
    boolean result = true;

    // Basic check
    final Agent agent = getCheckoutDirectoryAwareAgent();
    if (!agent.fileRelativeToCheckoutDirExists(getRelativeBuildDir())) {
      result = false;
    }

    return result;
  }


  // REVIEWME: simeshev@parabuilci.org - 2009-02-13 - Do we really need it given the syncToChangeList does the same thing?


  public void checkoutLatest() throws BuildException, CommandStoppedException, AgentFailureException {
    throw new BuildException("Check out latest should not be called", getAgentHost());
  }


  public void syncToChangeList(final int changeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    AccurevCommand command = null;
    try {
      // Prepare
      validateChangeListID(changeListID);
      login();

      // Get change list
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final ChangeList changeList = cm.getChangeList(changeListID);

      // Get snapshot
      String snapshotName = null;
      final Agent agent = getCheckoutDirectoryAwareAgent();
      final AccurevCommandParameters parameters = getParameters();

      // Create a snapshot if not exists
      if (StringUtils.isBlank(snapshotName)) {
        snapshotName = "parabuild_snap_" + agent.getLocalHostName() + "_" + parameters.getBackingStream()
                + "_" + activeBuildID + "_" + changeList.getNumber() + "_" + parameters.getUser();
        command = new AccurevMksnapCommand(agent, parameters, snapshotName, changeList.getNumber());
        command.execute();
        command.cleanup();
      }

      // Deleted checkout directory
      agent.deleteCheckoutDir();

      // Execute pop command to pick up missing
      command = new AccurevPopCommand(agent, parameters, snapshotName);
      command.execute();
    } catch (final IOException e) {
      throw new BuildException("Error while synching to change list:" + StringUtils.toString(e), e, getAgentHost());
    } finally {
      cleanup(command);
    }
  }


  public String getRelativeBuildDir() throws BuildException, AgentFailureException {
    // Current, i.e. the root of the checkout dir.
    return ".";
  }


  public int getChangesSince(final int startChangeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    try {
      login();

      if (LOG.isDebugEnabled()) {
        LOG.debug("Begin getChangesSince changeListID: " + startChangeListID);
      }
      // NOTE: simeshev@parabuilci.org - we do not do clean-up or init
      // of the local copy as we retrieve changes directly from Accurev
      // server.

      final int maxChangeLists;
      final String transactionNumberFrom;

      // check if it is first run (changeListID equals UNSAVED_ID)
      if (startChangeListID == ChangeList.UNSAVED_ID) {
        maxChangeLists = initialNumberOfChangeLists();
        transactionNumberFrom = "1";
      } else {
        // get last build change date
        final ChangeList latest = configManager.getChangeList(startChangeListID);
        if (LOG.isDebugEnabled()) {
          LOG.debug("Latest: " + latest);
        }
        // where there changes?
        if (latest == null) {
          return startChangeListID;
        }
        maxChangeLists = maxNumberOfChangeLists();
        transactionNumberFrom = latest.getNumber();
      }

      // requests changes from SVN server
      final int newChangeListID = getChangesSince(transactionNumberFrom, "highest", maxChangeLists);
      if (newChangeListID == ChangeList.UNSAVED_ID && startChangeListID != ChangeList.UNSAVED_ID) {
        return startChangeListID; // i.e. no changes since the exsiting one
      } else {
        return newChangeListID; // as is
      }

    } catch (final IOException e) {
      throw new BuildException(e, getAgentHost());
    }
  }


  /**
   * Requests changes from Subversion server and stores them
   * in the DB if found.
   *
   * @param transactionNumberFrom
   * @param transactionNumberTo
   * @param maxChangeLists
   * @return new change list ID if there were changes made, or
   *         the same base change list if there were changes
   * @noinspection ControlFlowStatementWithoutBraces
   */
  private int getChangesSince(final String transactionNumberFrom, final String transactionNumberTo,
                              final int maxChangeLists)
          throws BuildException, IOException, CommandStoppedException, AgentFailureException {

    final long timeStarted = System.currentTimeMillis();
    final Agent agent = getCheckoutDirectoryAwareAgent();

    // get actual changes
    final List result = new ArrayList(INITIAL_CANGE_LIST_CAPACITY);
    AccurevCommand command = null;
    try {
      // Create Accurev history command

      command = new AccurevHistoryCommand(agent, getParameters(), transactionNumberFrom,
              transactionNumberTo, maxChangeLists);

      // exec
      command.execute();

      // analyze change log
      final AccurevChangeLogParser changeLogParser = new AccurevChangeLogParser(maxChangeListSize());
      changeLogParser.setMaxChangeLists(maxChangeLists);
      final List changeLists = changeLogParser.parseChangeLog(command.getStdoutFile());
      if (LOG.isDebugEnabled()) LOG.debug("Changelist size: " + changeLists.size());

      // add this path changes to result
      result.addAll(changeLists);
    } catch (final DocumentException e) {
      throw new BuildException(e, getAgentHost());
    } finally {
      cleanup(command); // cleanup this cycle
    }

    // NOTE: simeshev@parabuilci.org - We don't sort because the parser returns the results in the reverse order.

    // result
    final long processingTime = System.currentTimeMillis() - timeStarted;
    if (LOG.isDebugEnabled()) LOG.debug("Time to process change lists: " + processingTime);

    // return if no changes
    if (result.isEmpty()) {
      return ChangeList.UNSAVED_ID;
    }

    // Validate that change lists contain not only exclusions
    final String exclusionPaths = getSettingValue(SourceControlSetting.VCS_EXCLUSION_PATHS);
    final ExclusionPathFinder exclusionPathFinder = new ExclusionPathFinder();
    if (exclusionPathFinder.onlyExclusionPathsPresentInChangeLists(result, exclusionPaths)) {
      return ChangeList.UNSAVED_ID;
    }

    if (LOG.isDebugEnabled()) LOG.debug("End getChangesSince: " + result.size());
    // store changes
    return configManager.saveBuildChangeLists(activeBuildID, result);
  }


  public void label(final String label) throws BuildException, CommandStoppedException {
    throw new IllegalStateException("Label is not supported for AccuRev");
  }


  public Map getUsersMap() throws CommandStoppedException {
    return Collections.EMPTY_MAP;
  }


  public void reloadConfiguration() {
    // Get resolved settings
    final Map newSettings = getResolvedSettings();

    // Check if critical settings has changed
    final SourceControlSettingChangeDetector scd = new SourceControlSettingChangeDetector(currentSettings, newSettings);
    boolean hasToCleanUp = false;
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.ACCUREV_DEPOT);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.ACCUREV_EOL_TYPE);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.ACCUREV_HOST);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.ACCUREV_PASSWORD);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.ACCUREV_PATH);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.ACCUREV_PORT);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.ACCUREV_STREAM);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.ACCUREV_USER);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.ACCUREV_WORKSPACE_LOCK);
    if (hasToCleanUp) {
      setHasToCleanUp();
    }

    // Update current settings map
    replaceCurrentSettings(newSettings);
  }


  /**
   * {@inheritDoc}
   */
  public Map getShellVariables() throws IOException {
    return Collections.EMPTY_MAP;
  }


  private void login() throws BuildException, AgentFailureException, CommandStoppedException {
    AccurevLoginCommand command = null;
    try {
      command = new AccurevLoginCommand(getCheckoutDirectoryAwareAgent(), getParameters());
      command.execute();
    } catch (final IOException e) {
      throw new BuildException(e, getAgentHost());
    } finally {
      cleanup(command);
    }
  }


  private AccurevCommandParameters getParameters() {
    final String depot = getSettingValue(SourceControlSetting.ACCUREV_DEPOT);
    final String eolType = getSettingValue(SourceControlSetting.ACCUREV_EOL_TYPE);
    final String exePath = getSettingValue(SourceControlSetting.ACCUREV_EXE_PATH);
    final String host = getSettingValue(SourceControlSetting.ACCUREV_HOST);
    final String kind = getSettingValue(SourceControlSetting.ACCUREV_WORKSPACE_LOCK);
    final String password = decrypt(getSettingValue(SourceControlSetting.ACCUREV_PASSWORD));
    final String path = getSettingValue(SourceControlSetting.ACCUREV_PATH);
    final String port = getSettingValue(SourceControlSetting.ACCUREV_PORT);
    final String stream = getSettingValue(SourceControlSetting.ACCUREV_STREAM);
    final String user = getSettingValue(SourceControlSetting.ACCUREV_USER);
    return new AccurevCommandParameters(exePath, host, password, Integer.parseInt(port), stream, user, path, depot,
            Byte.parseByte(kind), Byte.parseByte(eolType));
  }


  private String decrypt(final String value) {
    return org.parabuild.ci.security.SecurityManager.decryptPassword(value);
  }
}
