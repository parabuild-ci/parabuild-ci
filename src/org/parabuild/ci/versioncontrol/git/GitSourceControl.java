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
package org.parabuild.ci.versioncontrol.git;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.build.BuildScriptGenerator;
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.util.ValidationException;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.versioncontrol.AbstractSourceControl;
import org.parabuild.ci.versioncontrol.ExclusionPathFinder;
import org.parabuild.ci.versioncontrol.GitDepotPathParser;
import org.parabuild.ci.versioncontrol.RepositoryPath;
import org.parabuild.ci.versioncontrol.SourceControlSettingChangeDetector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * GitSourceControl
 * <p/>
 *
 * @author Slava Imeshev
 * @since Jan 9, 2010 10:16:51 PM
 */
public final class GitSourceControl extends AbstractSourceControl {

  /*

  Example of a stdout output from add command.

  C:\WORK\mor2\dev\test_git>git commit -m "Added first files to git repository"
[master (root-commit) 76606c2] Added first files to git repository
warning: LF will be replaced by CRLF in sourceline/alwaysvalid/src/symlinked_readme.txt
 7 files changed, 26 insertions(+), 0 deletions(-)
 create mode 100644 second_sourceline/src/readme.txt
 create mode 100644 source_path/file_to_ignore_in_branch_view.txt
 create mode 100644 source_path/mode.txt
 create mode 100644 source_path/readme.txt
 create mode 100644 sourceline with spaces/readme.txt
 create mode 100644 sourceline/alwaysvalid/src/readme.txt
 create mode 100644 sourceline/alwaysvalid/src/symlinked_readme.txt

  */

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(GitSourceControl.class); // NOPMD
  private static final int INITIAL_CHANGE_LIST_CAPACITY = 101;


  public GitSourceControl(final BuildConfig buildConfig) {
    super(buildConfig);
  }


  GitSourceControl(final BuildConfig config, final List settings) {
    this(config);
    currentSettings = ConfigurationManager.settingsListToMap(settings);
  }


  public boolean isBuildDirInitialized() throws IOException, BuildException, AgentFailureException {
    final Agent agent = getCheckoutDirectoryAwareAgent();
    return !(!agent.checkoutDirExists() || !agent.fileRelativeToCheckoutDirExists(".git")
            || !agent.fileRelativeToCheckoutDirExists(getRelativeBuildDir()));
  }


  public void checkoutLatest() throws BuildException, CommandStoppedException, AgentFailureException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("begin checkoutLatest");
    }
    Agent agent = null;
    try {
      agent = getCheckoutDirectoryAwareAgent();

      // Hard clean up of checkout directory
      agent.emptyCheckoutDir();

      // traverse list of paths
      GitCommand command = null;
      try {
        // Execute Git checkout command
        command = new GitCloneCommand(agent, getPathToGitExe(), getRepositorySetting(), getBranchSetting(),
                getUserSetting(), getPasswordSetting());
        command.execute();
        command.cleanup();


      } finally {
        cleanup(command); // cleanup this cycle
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("end checkoutLatest");
      }
    } catch (final IOException e) {
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, agent);
    }
  }


  public void syncToChangeList(final int changeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("begin syncToChangeList changeListID: " + changeListID);
    }
    Agent agent = null;
    try {
      agent = getCheckoutDirectoryAwareAgent();
      validateChangeListID(changeListID);
      cleanupLocalCopyIfNecessary();
      initLocalCopyIfNecessary();

      // get latest change date for this change list. SVN doesn't support
      // change IDs, so we sync to the date.
      final ChangeList changeList = configManager.getChangeList(changeListID);
      final String hash = changeList.getNumber();

      GitCommand command = null;
      try {

        // Execute pull command to the head so that reset can observe the change
        command = new GitPullCommand(agent, getPathToGitExe(), getRepositorySetting(),
                getUserSetting(), getPasswordSetting());
        command.execute();
        command.cleanup();

        // Execute reset command with the given hash
        command = new GitResetCommand(agent, getPathToGitExe(), getRepositorySetting(),
                getUserSetting(), getPasswordSetting(), hash);
        command.execute();

      } finally {
        cleanup(command); // cleanup this cycle
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("end syncToChangeList");
      }
    } catch (final IOException e) {
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, agent);
    }
  }


  public String getRelativeBuildDir() throws BuildException {
    return ((RepositoryPath) getDepotPaths().get(0)).getPath();
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
        if (latest == null) return startChangeListID;
        maxChangeLists = maxNumberOfChangeLists();
        changeListNumberFrom = latest.getNumber();
        changeListNumberTo = "HEAD";
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
          throws BuildException, IOException, CommandStoppedException, AgentFailureException {

    final long timeStarted = System.currentTimeMillis();
    final Agent agent = getCheckoutDirectoryAwareAgent();

    // Get actual changes
    GitCommand command;

    // Execute reset command to the head so that log can observe all
    command = new GitResetCommand(agent, getPathToGitExe(), getRepositorySetting(),
            getUserSetting(), getPasswordSetting(), "HEAD");
    command.execute();
    command.cleanup();

    // Execute pull command to the head so that log can observe all
    command = new GitPullCommand(agent, getPathToGitExe(), getRepositorySetting(),
            getUserSetting(), getPasswordSetting());
    command.execute();
    command.cleanup();

    final List result = new ArrayList(INITIAL_CHANGE_LIST_CAPACITY);
    for (final Iterator iter = getDepotPaths().iterator(); iter.hasNext(); ) {
      try {
        final RepositoryPath repositoryPath = (RepositoryPath) iter.next();
        final String path = repositoryPath.getPath();
        if (LOG.isDebugEnabled()) LOG.debug("getting changes for: " + path);

        // create SVN checkout command
        command = new GitLogCommand(agent, getPathToGitExe(), getRepositorySetting(), repositoryPath, changeListNumberFrom,
                changeListNumberTo, maxChangeLists, getUserSetting(), getPasswordSetting(), true);

        // exec
        command.execute();

        // analyze change LOG
        final GitTextChangeLogParser changeLogParser = new GitTextChangeLogParser();
        changeLogParser.setUseUserEmailAsUserName(useUserEmailAsUserName());
        changeLogParser.setMaxChangeListSize(maxChangeListSize());
        changeLogParser.setMaxChangeLists(maxChangeLists);
        final List changeLists = changeLogParser.parseChangeLog(command.getStdoutFile());
        if (LOG.isDebugEnabled()) LOG.debug("changelist size: " + changeLists.size());
        if (changeLists.isEmpty()) continue;

        // add this path changes to result
        result.addAll(changeLists);
      } finally {
        cleanup(command); // cleanup this cycle
      }
      try {
        Thread.sleep(1000L);
      } catch (final InterruptedException e) {
        throw new CommandStoppedException(e);
      }
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
    if (new ExclusionPathFinder().onlyExclusionPathsPresentInChangeLists(result, getSettingValue(VCSAttribute.VCS_EXCLUSION_PATHS))) {
      return ChangeList.UNSAVED_ID;
    }

    // De-dupicate
    String currentNumber = null;
    for (final Iterator iter = result.iterator(); iter.hasNext(); ) {
      final ChangeList changeList = (ChangeList) iter.next();
      if (currentNumber != null && currentNumber.equals(changeList.getNumber())) {
        iter.remove(); // Found duplicate, remove
        continue;
      }
      currentNumber = changeList.getNumber();
    }

    if (LOG.isDebugEnabled()) LOG.debug("End getChangesSince: " + result.size());

    // store changes
    return configManager.saveBuildChangeLists(activeBuildID, result);
  }


  private static boolean useUserEmailAsUserName() {
    return SystemConfigurationManagerFactory.getManager().isUseGitUserEmail();
  }


  private String getUserSetting() {
    return getSettingValue(VCSAttribute.GIT_USER, "");
  }


  private String getRepositorySetting() {
    return getSettingValue(VCSAttribute.GIT_REPOSITORY);
  }


  private String getBranchSetting() {
    return getSettingValue(VCSAttribute.GIT_BRANCH);
  }


  public void label(final String label) {
    //To change body of implemented methods use File | Settings | File Templates.
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
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.GIT_DEPOT_PATH);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.GIT_REPOSITORY);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.GIT_BRANCH);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE);
    if (hasToCleanUp) {
      setHasToCleanUp();
    }
    // update current settings map
    replaceCurrentSettings(newSettings);
  }


  /**
   * {@inheritDoc}
   */
  public Map getShellVariables() {

    final Map result = new HashMap(1);
    result.put(BuildScriptGenerator.VAR_PARABUILD_GIT_BRANCH, getSettingValue(VCSAttribute.GIT_BRANCH));

    return result;
  }


  /**
   * Returns project source line repository paths relative to Git
   * root. Firs item in the list is a project build home.
   *
   * @return List of Git repository paths composing a project
   * @throws BuildException
   */
  private List getDepotPaths() throws BuildException {
    try {
      final GitDepotPathParser parser = new GitDepotPathParser();
      return parser.parseDepotPath(getSettingValue(VCSAttribute.GIT_DEPOT_PATH));
    } catch (final ValidationException e) {
      throw new BuildException(e, getAgentHost());
    }
  }


  private String getPathToGitExe() {
    return StringUtils.putIntoDoubleQuotes(getSettingValue(VCSAttribute.GIT_PATH_TO_EXE));
  }


  /**
   * @return decrypted password or null if not defined.
   * @noinspection ControlFlowStatementWithoutBraces
   */
  private String getPasswordSetting() {
    final String encryptedPassword = getSettingValue(VCSAttribute.GIT_PASSWORD);
    if (encryptedPassword == null) return null;
    return org.parabuild.ci.security.SecurityManager.decryptPassword(encryptedPassword);
  }
}
