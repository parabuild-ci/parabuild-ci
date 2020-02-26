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
import org.parabuild.ci.common.VCSAttribute;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.util.ValidationException;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.security.SecurityManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Implements PVCS source control.
 */
final class PVCSSourceControl extends AbstractSourceControl {

  private static final Log log = LogFactory.getLog(PVCSSourceControl.class);
  private static final int DEFAULT_LABEL_BLOCK_SIZE = 1000;
  private Date lastSyncDate = null;


  PVCSSourceControl(final BuildConfig buildConfig) {
    super(buildConfig);
  }


  /**
   * This GoF strategy method validates that build directory
   * is initialized according to build configuration.
   * Implementing classes may use this method to perform
   * additional validation of build directory.
   * <p/>
   * If this method returns false, initLocalCopyIfNecessary()
   * will call checkoutLatest() to populate build dir.
   *
   * @return build directory is initialized according to
   *         build configuration.
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
        PVCSGetCommand command = null;
        try {
          final RepositoryPath projectPath = (RepositoryPath) i.next();
          final String path = projectPath.getPath();
          if (log.isDebugEnabled()) log.debug("checking out latest for: " + path);

          // execute PVCS history command
          final PVCSGetCommandParameters parameters = new PVCSGetCommandParameters();
          parameters.setProject(path);
          setCommonParameters(parameters);
          command = new PVCSGetCommand(agent, parameters);
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
      final ChangeList changeList = configManager.getChangeList(changeListID);
      final Date changeListDate = changeList.getCreatedAt();
      for (final Iterator i = getProjects().iterator(); i.hasNext();) {
        PVCSGetCommand command = null;
        try {
          final RepositoryPath projectPath = (RepositoryPath) i.next();
          final String path = projectPath.getPath();
          if (log.isDebugEnabled()) log.debug("syncing to for: " + path);

          // execute PVCS history command
          final PVCSGetCommandParameters parameters = makeGetParameters(path, changeListDate);
          command = new PVCSGetCommand(agent, parameters);
          command.execute();
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
      if (log.isDebugEnabled()) log.debug("end syncToChangeList");
      this.lastSyncDate = (Date) changeListDate.clone();
    } catch (final IOException e) {
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  private PVCSGetCommandParameters makeGetParameters(final String path, final Date changeListDate) {
    final PVCSGetCommandParameters parameters = new PVCSGetCommandParameters();
    parameters.setProject(path);
    parameters.setDate(changeListDate);
    setCommonParameters(parameters);
    return parameters;
  }


  /**
   * Returns relative project path
   */
  public String getRelativeBuildDir() throws BuildException {
    final List depotPaths = getProjects();
    final RepositoryPath rp = (RepositoryPath) depotPaths.get(0);
    return rp.getPath().substring(1);
  }


  /**
   * Returns ID of list of changes that were made to
   * controlled source line since the given change list ID
   * <p/>
   * In order to run successfuly this method needs an
   * already checked out local copy on the client.
   * <p/>
   * Handling zero ID change list. When this method is
   * called first time in build's life, the ID of the change
   * list is zero. It means that caller expects that there
   * are no change lists in the database. Version control
   * should retrieve all the past changes, and pick fixed
   * number of the latest changes. This number is adentified
   * by SourceControl.DEFAULT_FIRST_RUN_SIZE constant.
   *
   * @param startChangeListID base change list ID
   * @return new change list ID if there were changes made,
   *         or the same base change list if there were
   *         changes
   * @throws BuildException
   */
  public int getChangesSince(final int startChangeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    if (log.isDebugEnabled()) log.debug("begin getChangesSince changeListID: " + startChangeListID);
    try {

      // NOTE: simeshev@parabuilci.org - we do not do clean-up or init
      // of the local copy as we retrieve changes directly from PVCS
      // server.

      final int rowLimit;
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
        // roll forward for 1 second - otherwise PVCS will add same last change list.
        final Calendar c = Calendar.getInstance();
        c.setTime(latest.getCreatedAt());
        c.add(Calendar.MINUTE, 1);
        beginDate = c.getTime();
      }

      // requests changes from PVCS server
      final long timeStarted = System.currentTimeMillis();
      final Agent agent = getCheckoutDirectoryAwareAgent();
      final List result = new ArrayList(101);
      final Locale builderLocale = agent.defaultLocale();
      for (final Iterator i = getProjects().iterator(); i.hasNext();) {
        PVCSCommand command = null;
        try {
          final RepositoryPath projectPath = (RepositoryPath) i.next();
          final String path = projectPath.getPath();
          if (log.isDebugEnabled()) log.debug("getting changes for: " + path);

          // execute PVCS history command
          final PVCSVlogCommandParameters parameters = new PVCSVlogCommandParameters();
          setCommonParameters(parameters);
          parameters.setStartDate(beginDate);
          parameters.setProject(path);
          command = new PVCSVlogCommand(agent, parameters);
          command.execute();

          // analyze change log
          final PVCSChangeListParser changeListParser = new PVCSChangeListParser(
                  builderLocale, parameters.getRepository(), path, rowLimit,
                  parameters.getBranch(), maxChangeListSize());
          final List changeLists = changeListParser.parseChangeLog(command.getStdoutFile());
          if (log.isDebugEnabled()) log.debug("changelist size: " + changeLists.size());
          if (changeLists.isEmpty()) continue;

          // add this path changes to result
          result.addAll(changeLists);
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
      if (new ExclusionPathFinder().onlyExclusionPathsPresentInChangeLists(result, getSettingValue(VCSAttribute.VCS_EXCLUSION_PATHS))) {
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


  /**
   * Labels the last synced checkout directory with the
   * given label.
   * <p/>
   * Must throw a BuildException if there was no last sync
   * made or if checkout directory is empty.
   *
   * @param label
   */
  public void label(final String label) throws BuildException, CommandStoppedException, AgentFailureException {
    if (log.isDebugEnabled()) log.debug("begin label");
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      final Locale builderLocale = agent.defaultLocale();
      final Date labelDate = (Date) lastSyncDate.clone();
      final Calendar c = Calendar.getInstance();
      c.setTime(labelDate);
      c.add(Calendar.MINUTE, 1);
      final Date endDate = c.getTime();
      for (final Iterator i = getProjects().iterator(); i.hasNext();) {
        PVCSCommand vlogCommand = null;
        try {
          final RepositoryPath projectPath = (RepositoryPath) i.next();
          final String path = projectPath.getPath();
          if (log.isDebugEnabled()) log.debug("labeling to for: " + path);

          // execute PVCS history vlogCommand
          final PVCSVlogCommandParameters logCommandParams = new PVCSVlogCommandParameters();
          logCommandParams.setProject(path);
          logCommandParams.setEndDate(endDate);
          setCommonParameters(logCommandParams);
          vlogCommand = new PVCSVlogCommand(agent, logCommandParams);
          vlogCommand.execute();

          // The idea is this: create a PCLI script that contains
          // blocks of label commands of configurable size. This forsees
          // that PCLI scripts may have memory limitation.

          // go over the revisions, find files and revisions that are
          // closest to the label date.
          // REVIEWME: create parameter copy
          logCommandParams.setEndDate(labelDate);
          // END REVIEWME.
          final PVCSCommandParameters commandParams = new PVCSCommandParameters();
          commandParams.setProject(path);
          setCommonParameters(commandParams);
          final PVCSLabelCreator labelCreator = new PVCSLabelCreatorImpl(agent, commandParams, label);
          final PVCSVlogLabelHandler handler = new PVCSVlogLabelHandler(logCommandParams, labelCreator, DEFAULT_LABEL_BLOCK_SIZE);
          final PVCSVlogParser driver = new PVCSVlogParser(builderLocale, logCommandParams.getRepository(), path, logCommandParams.getBranch(), handler);
          driver.parseChangeLog(vlogCommand.getStdoutFile());
        } finally {
          cleanup(vlogCommand); // cleanup this cycle
        }
      }
      if (log.isDebugEnabled()) log.debug("end label");
      this.lastSyncDate = (Date) labelDate.clone();
    } catch (final IOException e) {
      throw new BuildException("Error while labeling: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  /**
   * Returns a map containing version control user names as
   * keys and e-mails as values. This method doesn't throw
   * exceptions as it's failure is not critical but it
   * reports errors by calling to ErrorManager.
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
   * If configuration has changed in such a way that
   * requires cleaning up source line, next operation
   * involving manipulation on source line file should
   * should be performed on a clean checkout directory.
   * <p/>
   * For instance, if source line path has changed, the
   * content of the old checkout directory should be cleaned
   * up (deleted).
   */
  public void reloadConfiguration() {
    // Get resolved settings
    final Map newSettings = getResolvedSettings();

    // check if critical settings has changed
    final SourceControlSettingChangeDetector scd = new SourceControlSettingChangeDetector(currentSettings, newSettings);
    boolean hasToCleanUp = false;
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.PVCS_BRANCH_NAME);
//    hasToCleanUp = hasToCleanUp || scd.settingHasChanged(SourceControlSetting.PVCS_LABEL);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.PVCS_PROJECT);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.PVCS_PROMOTION_GROUP);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.PVCS_REPOSITORY);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE);
    if (hasToCleanUp) {
      setHasToCleanUp();
    }
    // update current settings map
    replaceCurrentSettings(newSettings);
  }


  /**
   * @return Map with a shell variable name as a key and
   *         variable value as value. The shell variables
   *         will be made avaiable to the build commands.
   * @see BuildScriptGenerator#addVariables(Map)
   */
  public Map getShellVariables() {
    return Collections.emptyMap();
  }


  /**
   * Returns text description of a command to be used by a
   * customer to sync to a given changelist. This is a
   * default implementation.
   *
   * @param changeListID
   */
  public String getSyncCommandNote(final int changeListID) throws AgentFailureException {
    try {
      final List projects = getProjects();
      final RepositoryPath rp = (RepositoryPath) projects.get(0);
      final ChangeList changeList = configManager.getChangeList(changeListID);
      final Agent agent = getCheckoutDirectoryAwareAgent();
      return "pcli" + PVCSGetCommand.getRunArguments(agent, makeGetParameters(rp.getPath(), changeList.getCreatedAt()), "", true);
    } catch (final Exception e) {
      if (log.isDebugEnabled()) log.debug("Error while creating a sync note", e);
      return super.getSyncCommandNote(changeListID);
    }
  }


  /**
   * Returs project source line repository paths. First item
   * in the list is a project build home.
   *
   * @return List of PVCS project paths
   * @throws BuildException
   */
  private List getProjects() throws BuildException {
    try {
      return new PVCSProjectListParser().parseProjects(getSettingValue(VCSAttribute.PVCS_PROJECT));
    } catch (final ValidationException e) {
      throw new BuildException(e, getAgentHost());
    }
  }


  /**
   * Helper method to pupulate common PVCS command
   * parameters from current settings.
   *
   * @param parameters
   */
  private void setCommonParameters(final PVCSCommandParameters parameters) {
    parameters.setBranch(getSettingValue(VCSAttribute.PVCS_BRANCH_NAME));
    parameters.setPathToClient(getSettingValue(VCSAttribute.PVCS_EXE_PATH));
    parameters.setRepository(getSettingValue(VCSAttribute.PVCS_REPOSITORY));
    parameters.setPassword(StringUtils.isBlank(getSettingValue(VCSAttribute.PVCS_PASSWORD)) ? "" : SecurityManager.decryptPassword(getSettingValue(VCSAttribute.PVCS_PASSWORD)));
    parameters.setUser(getSettingValue(VCSAttribute.PVCS_USER));
    parameters.setLabel(getSettingValue(VCSAttribute.PVCS_LABEL));
    parameters.setPromotionGroup(getSettingValue(VCSAttribute.PVCS_PROMOTION_GROUP));
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
      throw new BuildException("Error while accessing PVCS: PCLI executable not found.", getAgentHost());
    }
    throw new BuildException("Error while accessing PVCS: " + StringUtils.toString(e), e, getAgentHost());
  }


  public String toString() {
    return "PVCSSourceControl{" +
            "lastSyncDate=" + lastSyncDate +
            '}';
  }
}
