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
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.util.ValidationException;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by simeshev on Mar 7, 2006 at 5:19:06 PM
 */
public class StarTeamSourceControl extends AbstractSourceControl {

  private static final Log log = LogFactory.getLog(StarTeamSourceControl.class);

  /**
   * Contains date when we did sync last time.
   */
  private Date lastSyncDate = null;


  public StarTeamSourceControl(final BuildConfig buildConfig) {
    super(buildConfig);
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
   *         configuration.
   * @see AbstractSourceControl#initLocalCopyIfNecessary()
   * @see SourceControl#checkoutLatest()
   */
  public boolean isBuildDirInitialized() throws IOException, AgentFailureException {
    return !getCheckoutDirectoryAwareAgent().checkoutDirIsEmpty();
  }


  /**
   * Checks out latest state of the source line
   */
  public void checkoutLatest() throws BuildException, CommandStoppedException, AgentFailureException {
    if (log.isDebugEnabled()) log.debug("begin checkoutLatest");
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      cleanupLocalCopyIfNecessary();
      for (final Iterator i = getProjects().iterator(); i.hasNext();) {
        StarTeamCheckoutCommand command = null;
        try {
          final RepositoryPath projectPath = (RepositoryPath) i.next();
          final String project = projectPath.getPath();
          if (log.isDebugEnabled()) log.debug("checking out latest for: " + project);

          // execute StarTeam checkout command
          final StarTeamCheckoutCommandParameters parameters = new StarTeamCheckoutCommandParameters();
          setCommonParameters(parameters);
          parameters.setProject(project);
          parameters.setEolConversion(getSettingValue(VersionControlSystem.STARTEAM_EOL_CONVERSION, VersionControlSystem.STARTEAM_EOL_ON));
          command = new StarTeamCheckoutCommand(agent, parameters);
          command.execute();
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
      if (log.isDebugEnabled()) log.debug("end checkoutLatest");
    } catch (final IOException e) {
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  /**
   * Syncs to a given change list number
   */
  public void syncToChangeList(final int changeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    if (log.isDebugEnabled()) log.debug("begin syncToChangeList");
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      cleanupLocalCopyIfNecessary();
      final boolean checkoutDirWasEmpty = agent.checkoutDirIsEmpty();
      final ChangeList changeList = configManager.getChangeList(changeListID);
      final Date changeListDate = changeList.getCreatedAt();
      for (final Iterator i = getProjects().iterator(); i.hasNext();) {
        StarTeamCommand command = null;
        try {
          final RepositoryPath projectPath = (RepositoryPath) i.next();
          final String project = projectPath.getPath();
          if (log.isDebugEnabled()) log.debug("checking out latest for: " + project);

          // execute StarTeam checkout command setting the change list date
          final StarTeamCheckoutCommandParameters checkOutParams = new StarTeamCheckoutCommandParameters();
          setCommonParameters(checkOutParams);
          checkOutParams.setProject(project);
          checkOutParams.setEolConversion(getSettingValue(VersionControlSystem.STARTEAM_EOL_CONVERSION, VersionControlSystem.STARTEAM_EOL_ON));
          checkOutParams.setDate(changeListDate);
          command = new StarTeamCheckoutCommand(agent, checkOutParams);
          command.execute();
          cleanup(command);

          // if this wasnt a clean checkout, delete local
          // files with unknown status.
          if (!checkoutDirWasEmpty) {
            final StarTeamDeleteLocalCommandParameters deleteLocalParams = new StarTeamDeleteLocalCommandParameters();
            setCommonParameters(deleteLocalParams);
            deleteLocalParams.setProject(project);
            deleteLocalParams.setViewConfigDate(changeListDate);
            command = new StarTeamDeleteLocalCommand(agent, deleteLocalParams);
            command.execute();
            cleanup(command);
          }
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
      this.lastSyncDate = (Date) changeListDate.clone();
      if (log.isDebugEnabled()) log.debug("end syncToChangeList");
    } catch (final IOException e) {
      throw new BuildException("Error while syncing: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  /**
   * Returns relative project path
   */
  public String getRelativeBuildDir() throws BuildException {
    return ((RepositoryPath) getProjects().get(0)).getPath();
  }


  /**
   * Returns ID of list of changes that were made to controlled
   * source line since the given change list ID
   * <p/>
   * In order to run successfuly this method needs an already
   * checked out local copy on the client.
   * <p/>
   * Handling zero ID change list. When this method is called
   * first time in build's life, the ID of the change list is
   * zero. It means that caller expects that there are no change
   * lists in the database. Version control should retrieve all
   * the past changes, and pick fixed number of the latest
   * changes. This number is adentified by SourceControl.DEFAULT_FIRST_RUN_SIZE
   * constant.
   *
   * @param startChangeListID base change list ID
   * @return new change list ID if there were changes made, or
   *         the same base change list if there were changes
   * @throws BuildException
   */
  public int getChangesSince(final int startChangeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    if (log.isDebugEnabled()) log.debug("begin getChangesSince changeListID: " + startChangeListID);
    try {

      // NOTE: simeshev@parabuilci.org - we do not do clean-up or init
      // of the local copy as we retrieve changes directly from StarTeam
      // server.

      int rowLimit = Integer.MAX_VALUE;
      Date beginDate = null;
      // check if it is first run (changeListID equals UNSAVED_ID)
      if (startChangeListID == ChangeList.UNSAVED_ID) {
        rowLimit = initialNumberOfChangeLists();
      } else {
        // get last build change date
        final ChangeList latest = configManager.getChangeList(startChangeListID);
        // where there changes?
        if (latest == null) return startChangeListID;
        rowLimit = maxNumberOfChangeLists();
        // roll forward for 1 second - otherwise StarTeam will add same last change list.
        final Calendar c = Calendar.getInstance();
        Date changeListDate = null;
        changeListDate = latest.getCreatedAt();
        c.setTime(changeListDate);
        c.add(Calendar.SECOND, 1);
        beginDate = c.getTime();
      }

      // requests changes from StarTeam server
      final long timeStarted = System.currentTimeMillis();
      final Agent agent = getCheckoutDirectoryAwareAgent();
      final List result = new ArrayList(101);
      final Locale builderLocale = agent.defaultLocale();
      for (final Iterator i = getProjects().iterator(); i.hasNext();) {
        StarTeamCommand command = null;
        try {
          final RepositoryPath projectPath = (RepositoryPath) i.next();
          final String project = projectPath.getPath();
          if (log.isDebugEnabled()) log.debug("getting changes for: " + project);

          // execute StarTeam history command
          final StarTeamCommandParameters historyParams = new StarTeamCommandParameters();
          setCommonParameters(historyParams);
          historyParams.setProject(project);
          command = new StarTeamHistoryCommand(agent, historyParams);
          command.execute();

          // analyze change log
          final StarTeamChangeLogParser parser = new StarTeamChangeLogParser(builderLocale, agent.getCheckoutDirName(), rowLimit, beginDate, maxChangeListSize());
//          if (log.isDebugEnabled()) {
//            log.debug("IoUtils.fileToString(command.getStdoutFile()): " + IoUtils.fileToString(command.getStdoutFile()));
//          }
          final List changeLists = parser.parseChangeLog(command.getStdoutFile());
          if (log.isDebugEnabled()) log.debug("changelist size: " + changeLists.size());
          cleanup(command); // cleanup history command

          // fined deleted files, if any
          //findDeleted(project, agent, changeListDate);

          if (changeLists.isEmpty()) {
            continue;
          }

          // add this project changes to result
          result.addAll(changeLists);
        } catch (final ParseException e) {
          throw new BuildException(e, getAgentHost());
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }

      // get latest rowLimit changes if necessary
      result.sort(ChangeList.REVERSE_CHANGE_DATE_COMPARATOR);

      // result
      final long processingTime = System.currentTimeMillis() - timeStarted;
      if (log.isDebugEnabled()) log.debug("time to process change lists: " + processingTime);

      // return if no changes
      if (result.isEmpty()) return startChangeListID;

      // validate that change lists contain not only exclusions
      if (new ExclusionPathFinder().onlyExclusionPathsPresentInChangeLists(result, getSettingValue(VersionControlSystem.VCS_EXCLUSION_PATHS))) {
        return startChangeListID;
      }

      // store changes
      if (log.isDebugEnabled()) log.debug("end getChangesSince");
      return configManager.saveBuildChangeLists(activeBuildID, result);
    } catch (final IOException e) {
      processException(e);
      throw new BuildException("Error while retrieving list of changes: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  private List findDeleted(final String project, final Agent agent, final Date sinceDate) throws IOException, CommandStoppedException, AgentFailureException {
    // check if there is time to check since
    if (sinceDate == null) {
      return new ArrayList(11);
    }

    StarTeamCommand oldListCommand = null;
    StarTeamCommand newListCommand = null;
    final List result = new ArrayList(11);
    try {
      // this command produces information about
      // files that were present at the change
      final StarTeamListCommandParameters listParamsSince = new StarTeamListCommandParameters();
      setCommonParameters(listParamsSince);
      listParamsSince.setProject(project);
      listParamsSince.setConfigDate(sinceDate);
      // All in repo - C=Current, M=Modified, O=Out-of-Date, I=Missing, G=Merge
      listParamsSince.setFilter("CMOIG");
      oldListCommand = new StarTeamListCommand(agent, listParamsSince);
      oldListCommand.execute();

      // this command produces information about
      // files that were present at the moment we checked
      final StarTeamListCommandParameters listParamsNow = new StarTeamListCommandParameters();
      setCommonParameters(listParamsNow);
      listParamsNow.setProject(project);
      listParamsNow.setConfigDate(sinceDate);
      // Not in repo - N
      listParamsSince.setFilter("CMOIG");
      newListCommand = new StarTeamListCommand(agent, listParamsNow);
      newListCommand.execute();

      return result;
    } finally {
      cleanup(oldListCommand);
      cleanup(newListCommand);
    }
  }


  /**
   * Labels the last synced checkout directory with the given
   * label.
   * <p/>
   * Must throw a BuildException if there was no last sync made
   * or if checkout directory is empty.
   *
   * @param label
   */
  public void label(final String label) throws BuildException, CommandStoppedException, AgentFailureException {
    if (log.isDebugEnabled()) log.debug("begin label: " + label);
    if (this.lastSyncDate == null) throw new IllegalStateException("Attempted to label without syncing first");
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      // preExecute
//      cleanupLocalCopyIfNecessary();
//      initLocalCopyIfNecessary();
      // process
      for (final Iterator i = getProjects().iterator(); i.hasNext();) {
        StarTeamLabelCommand command = null;
        try {
          final RepositoryPath projectPath = (RepositoryPath) i.next();
          final String project = projectPath.getPath();
          if (log.isDebugEnabled()) log.debug("labeling latest for: " + project);
          // execute StarTeam label command
          final StarTeamLabelCommandParameters parameters = new StarTeamLabelCommandParameters();
          setCommonParameters(parameters);
          parameters.setProject(project);
          parameters.setLabelDate(lastSyncDate);
          parameters.setLabel(label);
          command = new StarTeamLabelCommand(agent, parameters);
          command.execute();
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
      if (log.isDebugEnabled()) log.debug("end label: " + label);
    } catch (final IOException e) {
      processException(e);
      throw new BuildException("Error while labeling the build: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  /**
   * Returns a map containing version control user names as keys
   * and e-mails as values. This method doesn't throw exceptions
   * as it's failure is not critical but it reports errors by
   * calling to ErrorManager.
   *
   * @see ErrorManagerFactory
   * @see ErrorManager
   */
  public Map getUsersMap() {
    return Collections.emptyMap();
  }


  /**
   * This method requests SourceControl to reload its
   * configuration from the database.
   * <p/>
   * If configuration has changed in such a way that requires
   * cleaning up source line, next operation involving
   * manipulation on source line file should should be performed
   * on a clean checkout directory.
   * <p/>
   * For instance, if source line path has changed, the content
   * of the old checkout directory should be cleaned up
   * (deleted).
   */
  public void reloadConfiguration() {
    // Get resolved settings
    final Map newSettings = getResolvedSettings();

    // check if critical settings has changed
    final SourceControlSettingChangeDetector scd = new SourceControlSettingChangeDetector(currentSettings, newSettings);
    boolean hasToCleanUp = false;
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.STARTEAM_EOL_CONVERSION);
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.STARTEAM_HOST);
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.STARTEAM_PORT);
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.STARTEAM_PROJECT_PATH);
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.STARTEAM_USER);
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE);
    if (hasToCleanUp) {
      setHasToCleanUp();
    }
    // update current settings map
    replaceCurrentSettings(newSettings);
  }


  /**
   * @return Map with a shell variable name as a key and variable
   *         value as value. The shell variables will be made
   *         avaiable to the build commands.
   * @see BuildScriptGenerator#addVariables(Map)
   */
  public Map getShellVariables() {
    return Collections.emptyMap();
  }


  /**
   * Returs project source line repository paths. First item
   * in the list is a project build home.
   *
   * @return List of StarTeam project paths
   * @throws BuildException
   */
  private List getProjects() throws BuildException {
    try {
      return new StarTeamProjectListParser().parseProjects(getSettingValue(VersionControlSystem.STARTEAM_PROJECT_PATH));
    } catch (final ValidationException e) {
      throw new BuildException(e, getAgentHost());
    }
  }


  /**
   * Helper method to pupulate common StarTeam command
   * parameters from current settings.
   *
   * @param parameters
   */
  private void setCommonParameters(final StarTeamCommandParameters parameters) {
    parameters.setAddress(getSettingValue(VersionControlSystem.STARTEAM_HOST));
    parameters.setEncryption(getSettingValue(VersionControlSystem.STARTEAM_ENCRIPTION, VersionControlSystem.STARTEAM_ENCRYPTION_NO_ENCRYPTION));
    parameters.setExePath(getSettingValue(VersionControlSystem.STARTEAM_PATH_TO_EXE));
    parameters.setPassword(StringUtils.isBlank(getSettingValue(VersionControlSystem.STARTEAM_PASSWORD)) ? "" : SecurityManager.decryptPassword(getSettingValue(VersionControlSystem.STARTEAM_PASSWORD)));
    parameters.setPort(getSettingValue(VersionControlSystem.STARTEAM_PORT, 49201));
    parameters.setUser(getSettingValue(VersionControlSystem.STARTEAM_USER));
  }


  /**
   * Tries to figure out what happened. If can not, throws
   * BuildException saying that there what a error.
   *
   * @param e Exception to process
   * @throws BuildException with
   *                        processed information
   */
  private void processException(final Exception e) throws BuildException {
    final String exceptionString = e.toString();
    if (exceptionString.contains("java.io.IOException: CreateProcess:")) {
      throw new BuildException("Error while accessing StarTeam: executable not found.", getAgentHost());
    }
    throw new BuildException("Error while accessing StarTeam: " + StringUtils.toString(e), e, getAgentHost());
  }


  /**
   * Returns text description of a command to be used by a
   * customer to sync to a given changelist. This is a default
   * implementation.
   *
   * @param changeListID
   */
  public String getSyncCommandNote(final int changeListID) throws AgentFailureException {
    try {
      final Locale locale = getCheckoutDirectoryAwareAgent().defaultLocale();
      final Date createdAt = configManager.getChangeList(changeListID).getCreatedAt();
      final StringBuilder result = new StringBuilder(100);
      result.append(" stcmd");
      result.append(" -is");
      result.append(" -ro");
      result.append(" -u"); // Unlock the file
      result.append(" -vd ").append(new StarTeamDateFormat(locale).formatInput(createdAt));
      return result.toString();
    } catch (final IOException e) {
      log.warn("Error while creating sync command note: " + StringUtils.toString(e), e);
      return super.getSyncCommandNote(changeListID);
    }
  }
}
