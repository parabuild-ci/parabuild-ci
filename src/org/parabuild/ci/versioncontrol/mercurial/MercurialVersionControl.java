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
package org.parabuild.ci.versioncontrol.mercurial;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.IoUtils;
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
 * Implementation of Mercurial version control system.
 * <p/>
 * Mercurial support three modes of access:
 * <p/>
 * 1. File access - path to a local repository is provided directly.<br/>
 * 1.1. Path to local directory<br/>
 * 2. HTTP access using a built-in server or Apache.<br/>
 * 2.1. URL<br/>
 * 3. SSH using hg serve --stdin.<br/>
 * 3.1. host name<br/>
 * 3.2. user<br/>
 * 3.2. complete path to repo<br/>
 * 3.3. keys<br/>
 */
public class MercurialVersionControl extends AbstractSourceControl {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(MercurialVersionControl.class); // NOPMD
  private static final int INITIAL_CHANGE_LIST_CAPACITY = 101;


  public MercurialVersionControl(final BuildConfig buildConfig) {
    super(buildConfig);
  }


  public MercurialVersionControl(final BuildConfig buildConfig, final String checkoutDirectoryName) {
    super(buildConfig, checkoutDirectoryName);
  }

  MercurialVersionControl(final BuildConfig config, final List settings) {
    this(config);
    currentSettings = ConfigurationManager.settingsListToMap(settings);
  }

  public boolean isBuildDirInitialized() throws IOException, AgentFailureException {

    final Agent agent = getCheckoutDirectoryAwareAgent();
    return agent.fileRelativeToCheckoutDirExists(getRelativeBuildDir());
  }


  public void checkoutLatest() throws BuildException, CommandStoppedException, AgentFailureException {
    MercurialCommand command = null;
    try {
      // Hard clean up of checkout directory
      final Agent agent = getCheckoutDirectoryAwareAgent();
      agent.emptyCheckoutDir();

      command = new MercurialCloneCommand(agent, getExePath(), getURL(), getBranch());
      command.execute();
    } catch (final IOException e) {
      throw new BuildException("Error while checking out latest: " + e, e);
    } finally {
      cleanup(command);
    }
  }


  public void syncToChangeList(final int changeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Begin syncing to change list: " + changeListID);
    }
    MercurialCommand command = null;
    try {

      final Agent agent = getCheckoutDirectoryAwareAgent();
      cleanupLocalCopyIfNecessary();
      initLocalCopyIfNecessary();

      // Execute Mercurial pull
      command = new MercurialPullCommand(agent, getExePath(), getURL(), getBranch());
      command.execute();
      cleanup(command);

      // Execute Mercurial update
      final ChangeList changeList = ConfigurationManager.getInstance().getChangeList(changeListID);
      final String changeListNumber = getHash(changeList);
      command = new MercurialUpdateCommand(agent, getExePath(), changeListNumber, getBranch());
      command.execute();
      cleanup(command);

    } catch (final IOException e) {
      throw new BuildException("Error while syncing: " + StringUtils.toString(e), e);
    } finally {
      cleanup(command);
    }
  }


  public String getRelativeBuildDir() {
    return ".";
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
        changeListNumberFrom = "null";
        changeListNumberTo = "tip";
      } else {
        // get last build change date
        final ChangeList latest = configManager.getChangeList(startChangeListID);
        if (LOG.isDebugEnabled()) LOG.debug("latest: " + latest);
        // where there changes?
        if (latest == null) {
          return startChangeListID;
        }
        maxChangeLists = maxNumberOfChangeLists();
        // NOTE: simeshev@parabuilci.org - 2010-06-22 - We store long hash in the description because it didn't fit into the number change list field.
        changeListNumberFrom = getHash(latest);
        changeListNumberTo = "tip";
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
      throw new BuildException("Error while looking for changes: " + StringUtils.toString(e), e, agent);
    }
  }


  /**
   * Requests changes from Subversion server and stores them
   * in the DB if found.
   *
   * @param changeListNumberFrom a change list to start with.
   * @param changeListNumberTo   a change list to finish at
   * @param maxChangeLists       a maximum number of change lists.
   * @return new change list ID if there were changes made, or
   *         the same base change list if there were changes
   * @throws IOException if I/O error occurs.
   * @throws AgentFailureException
   *                             if agent fails.
   * @throws BuildException
   *                             if a general build error occurs.
   * @throws CommandStoppedException
   *                             if the command was stopped.
   * @noinspection ControlFlowStatementWithoutBraces, TooBroadScope
   */
  private int getChangesSince(final String changeListNumberFrom, final String changeListNumberTo, final int maxChangeLists)
          throws IOException, CommandStoppedException, AgentFailureException {

    final long timeStarted = System.currentTimeMillis();
    final Agent agent = getCheckoutDirectoryAwareAgent();

    // Create remote style
    final String stylePath = agent.createTempFile(".auto", ".scm", IoUtils.getResourceAsString("mercurial-style.txt"));

    // Get actual changes
    MercurialCommand command = null;

    final List result = new ArrayList(INITIAL_CHANGE_LIST_CAPACITY);

    try {
      final String branch = getBranch();

      // Execute Mercurial pull
      command = new MercurialPullCommand(agent, getExePath(), getURL(), getBranch());
      command.execute();
      cleanup(command);

      // Execute Mercurial log
      command = new MercurialLogCommand(agent, getExePath(), changeListNumberFrom, changeListNumberTo, maxChangeLists, branch, stylePath);
      command.execute();

      // Analyze change LOG
      final MercurialChangeLogParser changeLogParser = new MercurialChangeLogParser();
      changeLogParser.setMaxChangeListSize(maxChangeListSize());
      changeLogParser.setMaxChangeLists(maxChangeLists);
      if (!"null".equals(changeListNumberFrom) && !"tip".equals(changeListNumberFrom)) {
        changeLogParser.ignoreChangeList(changeListNumberFrom);
      }
      final List changeLists = changeLogParser.parseChangeLog(command.getStdoutFile());
      if (LOG.isDebugEnabled()) LOG.debug("changelist size: " + changeLists.size());

      // Add to result but skip the first change list
      result.addAll(changeLists);
    } finally {
      cleanup(command); // Cleanup
      agent.deleteTempFileHard(stylePath);
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


  private String getBranch() {
    return getSettingValue(SourceControlSetting.MERCURIAL_BRANCH);
  }


  public void label(final String label) {
    // Not supported.
  }


  public Map getUsersMap() {
    return Collections.emptyMap();
  }


  /**
   * @noinspection ConstantConditions
   */
  public void reloadConfiguration() {

    // Get resolved settings
    final Map newSettings = getResolvedSettings();

    // check if critical settings has changed
    final SourceControlSettingChangeDetector scd = new SourceControlSettingChangeDetector(currentSettings, newSettings);
    boolean hasToCleanUp = false;
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.MERCURIAL_URL);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.MERCURIAL_BRANCH);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE);
    if (hasToCleanUp) {
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


  private String getURL() {
    return getSettingValue(SourceControlSetting.MERCURIAL_URL);
  }


  private String getExePath() {
    return getSettingValue(SourceControlSetting.MERCURIAL_EXE_PATH);
  }


  private static String getHash(final ChangeList latest) {
    final String description = latest.getDescription();
    final int hashIndex = description.lastIndexOf(MercurialChangeLogParser.HASH);
    return description.substring(hashIndex + MercurialChangeLogParser.HASH.length(), description.length() - 1);
  }
}
