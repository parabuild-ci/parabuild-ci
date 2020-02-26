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
package org.parabuild.ci.versioncontrol.bazaar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.StringUtils;
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
 * BazaarSourceControl
 * <p/>
 *
 * @author Slava Imeshev
 * @since Apr 2, 2010 7:53:17 PM
 */
public final class BazaarVersionControl extends AbstractSourceControl {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(BazaarVersionControl.class); // NOPMD
  private static final int INITIAL_CHANGE_LIST_CAPACITY = 101;

  private String lastSyncRevision = null;


  public BazaarVersionControl(final BuildConfig buildConfig) {
    super(buildConfig);
  }


  protected BazaarVersionControl(final BuildConfig buildConfig, final String checkoutDirectoryName) {
    super(buildConfig, checkoutDirectoryName);
  }


  BazaarVersionControl(final BuildConfig config, final List settings) {
    this(config);
    currentSettings = ConfigurationManager.settingsListToMap(settings);
  }


  /**
   * {@inheritDoc}
   */
  public boolean isBuildDirInitialized() throws IOException, AgentFailureException {
    boolean result = true;

    // Basic check
    final Agent agent = getCheckoutDirectoryAwareAgent();
    if (!agent.fileRelativeToCheckoutDirExists(getRelativeBuildDir())) {
      result = false;
    }

    return result;
  }


  public void checkoutLatest() throws BuildException, CommandStoppedException, AgentFailureException {
    BazaarCommand command = null;
    try {
      // Hard clean up of checkout directory
      final Agent agent = getCheckoutDirectoryAwareAgent();
      agent.emptyCheckoutDir();

      command = new BazaarBranchCommand(agent, getPathToExe(), getBranchLocationSetting());
      command.execute();
    } catch (final IOException e) {
      throw new BuildException("Error while checking out latest: " + e, e);
    } finally {
      cleanup(command);
    }
  }


  public void syncToChangeList(final int changeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    BazaarCommand command = null;
    try {
      // Hard clean up of checkout directory
      validateChangeListID(changeListID);
      cleanupLocalCopyIfNecessary();
      initLocalCopyIfNecessary();

      // Update
      command = new BazaarUpdateCommand(getCheckoutDirectoryAwareAgent(), getPathToExe(), getBranchLocationSetting());
      command.execute();
      cleanup(command);

      // Revert
      final String changeListNumber = ConfigurationManager.getInstance().getChangeList(changeListID).getNumber();
      command = new BazaarRevertCommand(getCheckoutDirectoryAwareAgent(), getPathToExe(), changeListNumber, getBranchLocationSetting());
      command.execute();

      this.lastSyncRevision = changeListNumber;
    } catch (final IOException e) {
      throw new BuildException("Error while checking out latest: " + e, e);
    } finally {
      cleanup(command);
    }
  }


  public String getRelativeBuildDir() {
    return BazaarCommand.branchLocationToRelativeBuildDir(getBranchLocationSetting());
  }


  /**
   * Returns ID of list of changes that were made to controlled
   * source line since the given change list ID
   * <p/>
   * In order to run successfully this method needs an already
   * checked out local copy on the client.
   * <p/>
   * Handling zero ID change list. When this method is called
   * first time in build's life, the ID of the change list is
   * zero. It means that caller expects that there are no change
   * lists in the database. Version control should retrieve all
   * the past changes, and pick fixed number of the latest
   * changes. This number is identified by SourceControl.DEFAULT_FIRST_RUN_SIZE
   * constant.
   *
   * @param startChangeListID base change list ID
   * @return new change list ID if there were changes made, or
   *         the same base change list if there were changes
   * @throws BuildException
   * @noinspection ControlFlowStatementWithoutBraces
   */
  public int getChangesSince(final int startChangeListID) throws BuildException, CommandStoppedException, AgentFailureException {

    if (LOG.isDebugEnabled()) LOG.debug("Begin getting changes since change list ID: " + startChangeListID);
    try {

      cleanupLocalCopyIfNecessary();
      initLocalCopyIfNecessary();

      final int maxChangeLists;
      final String changeListNumberFrom;
      final String changeListNumberTo;

      // check if it is first run (changeListID equals UNSAVED_ID)
      if (startChangeListID == ChangeList.UNSAVED_ID) {
        maxChangeLists = initialNumberOfChangeLists();
        changeListNumberFrom = "";
        changeListNumberTo = "";
      } else {
        // get last build change date
        final ChangeList latest = configManager.getChangeList(startChangeListID);
        if (LOG.isDebugEnabled()) LOG.debug("latest: " + latest);
        // where there changes?
        if (latest == null) {
          return startChangeListID;
        }
        maxChangeLists = maxNumberOfChangeLists();
        changeListNumberFrom = latest.getNumber();
        changeListNumberTo = "";
      }

      // requests changes from SVN server
      final int newChangeListID = getChangesSince(changeListNumberFrom, changeListNumberTo, maxChangeLists);
      if (newChangeListID == ChangeList.UNSAVED_ID && startChangeListID != ChangeList.UNSAVED_ID) {
        return startChangeListID; // i.e. no changes since the existing one
      } else {
        return newChangeListID; // as os
      }
    } catch (final IOException e) {
      Agent agent = null;
      //noinspection EmptyCatchBlock
      try {
        agent = getCheckoutDirectoryAwareAgent();
      } catch (final Exception ex) {
      }
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, agent);
    }
  }


  /**
   * Requests changes from Subversion server and stores them
   * in the DB if found.
   *
   * @param changeListNumberFrom
   * @param changeListNumberTo
   * @param maxChangeLists
   * @return new change list ID if there were changes made, or
   *         the same base change list if there were changes
   * @noinspection ControlFlowStatementWithoutBraces
   */
  private int getChangesSince(final String changeListNumberFrom, final String changeListNumberTo, final int maxChangeLists)
          throws IOException, CommandStoppedException, AgentFailureException {

    final long timeStarted = System.currentTimeMillis();
    final Agent agent = getCheckoutDirectoryAwareAgent();

    // Get actual changes
    BazaarCommand command = null;

    final List result = new ArrayList(INITIAL_CHANGE_LIST_CAPACITY);
    try {
      final String path = getBranchLocationSetting();
      if (LOG.isDebugEnabled()) LOG.debug("getting changes for: " + path);

      // Create Bazaar checkout command
      command = new BazaarLogCommand(agent, getPathToExe(), getBranchLocationSetting(), changeListNumberFrom,
              changeListNumberTo, maxChangeLists);

      // Exec
      command.execute();

      // Analyze change LOG
      final BazaarChangeLogParser changeLogParser = new BazaarChangeLogParser();
      changeLogParser.setMaxChangeListSize(maxChangeListSize());
      changeLogParser.setMaxChangeLists(maxChangeLists);
      final List changeLists = changeLogParser.parseChangeLog(command.getStdoutFile());
      if (LOG.isDebugEnabled()) LOG.debug("changelist size: " + changeLists.size());

      // Add to result but skip the first change list
      for (int i = 0; i < changeLists.size(); i++) {
        final ChangeList changeList = (ChangeList) changeLists.get(i);
        if (changeList.getNumber().equals(changeListNumberFrom)) {
          continue;
        }
        result.add(changeList);
      }
    } finally {
      cleanup(command); // Cleanup
    }

    try {
      Thread.sleep(1000L);
    } catch (final InterruptedException e) {
      throw new CommandStoppedException(e);
    }

    // get latest maxChangeLists changes if necessary
    result.sort(ChangeList.REVERSE_CHANGE_DATE_COMPARATOR);

    // result
    final long processingTime = System.currentTimeMillis() - timeStarted;
    if (LOG.isDebugEnabled()) LOG.debug("Time to process change lists: " + processingTime);

    // return if no changes
    if (result.isEmpty()) {
      return ChangeList.UNSAVED_ID;
    }

    // validate that change lists contain not only exclusions
    if (new ExclusionPathFinder().onlyExclusionPathsPresentInChangeLists(result, getSettingValue(SourceControlSetting.VCS_EXCLUSION_PATHS))) {
      return ChangeList.UNSAVED_ID;
    }

    if (LOG.isDebugEnabled()) LOG.debug("End getChangesSince: " + result.size());

    // store changes
    return configManager.saveBuildChangeLists(activeBuildID, result);
  }


  public void label(final String label) throws BuildException, CommandStoppedException, AgentFailureException {
    if (StringUtils.isBlank(lastSyncRevision)) {
      return;
    }
    BazaarCommand command = null;
    try {
      command = new BazaarTagCommand(getCheckoutDirectoryAwareAgent(), getPathToExe(), lastSyncRevision, label, getBranchLocationSetting());
      command.execute();
    } catch (final IOException e) {
      throw new BuildException("Error while tagging: " + e.toString(), e);
    } finally {
      cleanup(command);
    }
  }


  public Map getUsersMap() {
    return Collections.emptyMap();
  }


  public void reloadConfiguration() {
    // Get resolved settings
    final Map newSettings = getResolvedSettings();

    // check if critical settings has changed
    final SourceControlSettingChangeDetector scd = new SourceControlSettingChangeDetector(currentSettings, newSettings);
    boolean hasToCleanUp = false;
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.BAZAAR_BRANCH_LOCATION);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE);
    if (hasToCleanUp) {
      lastSyncRevision = null;
      setHasToCleanUp();
    }
    // update current settings map
    replaceCurrentSettings(newSettings);
  }


  /**
   * {@inheritDoc}
   * <p/>
   * This implementation returns an empty map.
   */
  public Map getShellVariables() {
    return Collections.emptyMap();
  }


  /**
   * Returns path to Bazaar client executable.
   */
  private String getPathToExe() {
    return StringUtils.putIntoDoubleQuotes(getSettingValue(SourceControlSetting.BAZAAR_EXE_PATH));
  }


  /**
   * Returns Bazaar branch location.
   */
  private String getBranchLocationSetting() {
    return getSettingValue(SourceControlSetting.BAZAAR_BRANCH_LOCATION);
  }
}
