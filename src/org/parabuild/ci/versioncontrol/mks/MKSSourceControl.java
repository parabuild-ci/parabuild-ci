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
package org.parabuild.ci.versioncontrol.mks;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.build.BuildScriptGenerator;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.versioncontrol.AbstractSourceControl;
import org.parabuild.ci.versioncontrol.ExclusionPathFinder;
import org.parabuild.ci.versioncontrol.RepositoryPath;
import org.parabuild.ci.versioncontrol.SourceControlSettingChangeDetector;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * VCS manager for MKS integrity.
 */
public class MKSSourceControl extends AbstractSourceControl {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(MKSSourceControl.class); // NOPMD

  /**
   * Contains date when we did sync last time.
   */
  private Date lastSyncDate = null;


  public MKSSourceControl(final BuildConfig buildConfig) {
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
   * @see org.parabuild.ci.versioncontrol.SourceControl#checkoutLatest()
   */
  public boolean isBuildDirInitialized() throws IOException, AgentFailureException {
    final boolean result = !getCheckoutDirectoryAwareAgent().checkoutDirIsEmpty();
    if (LOG.isDebugEnabled()) LOG.debug("isBuildDirInitialized: " + result);
    return result;
  }


  /**
   * Checks out latest state of the source line
   */
  public void checkoutLatest() throws BuildException, CommandStoppedException, AgentFailureException {
    if (LOG.isDebugEnabled()) LOG.debug("begin checkoutLatest");
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      // prepare sandbox
      cleanupLocalCopyIfNecessary();
      createSandboxIfNecessary(); // this should create sandbox

      // set date
      for (final Iterator i = getProjects().iterator(); i.hasNext();) {
        MKSCommand command = null;
        try {
          final RepositoryPath projectPath = (RepositoryPath) i.next();
          final String path = projectPath.getPath();
          if (LOG.isDebugEnabled()) LOG.debug("getting changes for: " + path);

          // execute MKS co command
          final MKSResyncCommandParameters parameters = new MKSResyncCommandParameters();
          setCommonParameters(parameters);
          parameters.setProject(path);
          command = new MKSResyncCommand(agent, parameters);
          command.execute();

        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
    } catch (final IOException e) {
      processException(e);
      throw new BuildException("Error while retrieving list of changes: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  /**
   * Syncs to a given change list number
   */
  public void syncToChangeList(final int changeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    if (LOG.isDebugEnabled()) LOG.debug("begin syncToChangeList changeListID: " + changeListID);
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      // prepare sandbox
      validateChangeListID(changeListID);
      cleanupLocalCopyIfNecessary();
      createSandboxIfNecessary(); // this should create sandbox

      // set date
      final ChangeList changeList = configManager.getChangeList(changeListID);
      if (LOG.isDebugEnabled()) LOG.debug("changeDate: " + changeList.getCreatedAt());

      for (final Iterator i = getProjects().iterator(); i.hasNext();) {
        MKSCommand command = null;
        try {
          final RepositoryPath projectPath = (RepositoryPath) i.next();
          final String path = projectPath.getPath();
          if (LOG.isDebugEnabled()) LOG.debug("Running checkout for: " + path);

          // add one minute to the revsion. otherwise MKS doesn't pick it up
          final Calendar c = Calendar.getInstance();
          final Date changeListDate = changeList.getCreatedAt();
          c.setTime(changeListDate);
          c.add(Calendar.MINUTE, 1);
          final Date syncDate = c.getTime();

          // execute MKS co command
          final MKSCoCommandParameters parameters = new MKSCoCommandParameters();
          setCommonParameters(parameters);
          parameters.setProject(path);
          parameters.setDate(syncDate);
          parameters.setInputDateFormat(getSettingValue(SourceControlSetting.MKS_CO_DATE_FORMAT));
          command = new MKSCoCommand(agent, parameters);
          command.execute();

        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
      this.lastSyncDate = changeList.getCreatedAt();
    } catch (final IOException e) {
      processException(e);
      throw new BuildException("Error while retrieving list of changes: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  private void createSandboxIfNecessary() throws IOException, CommandStoppedException, AgentFailureException {
    if (!isBuildDirInitialized()) {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      for (final Iterator i = getProjects().iterator(); i.hasNext();) {
        MKSCommand command = null;
        try {
          final RepositoryPath projectPath = (RepositoryPath) i.next();
          final String path = projectPath.getPath();
          if (LOG.isDebugEnabled()) LOG.debug("creating sandbox for: " + path);

          // execute MKS dropsandbox command ignorying any exceptions
          try {
            final MKSCommandParameters dropSandboxParameters = new MKSCommandParameters();
            setCommonParameters(dropSandboxParameters);
            dropSandboxParameters.setProject(path);
            command = new MKSDropsandboxCommand(agent, dropSandboxParameters);
            command.execute();
            cleanup(command);
          } catch (final IOException e) {
            LOG.warn("Error while dropping sandbox", e);
          }

          // execute MKS createsandbox command
          final MKSCreatesandboxCommandParameters createSandboxParameters = new MKSCreatesandboxCommandParameters();
          setCommonParameters(createSandboxParameters);
          createSandboxParameters.setProject(path);
          createSandboxParameters.setLineTerminator(getSettingValue(SourceControlSetting.MKS_LINE_TERMINATOR, SourceControlSetting.MKS_LINE_TERMINATOR_NATIVE));
          createSandboxParameters.setProjectRevision(getSettingValue(SourceControlSetting.MKS_PROJECT_REVISION, null));
          command = new MKSCreatesandboxCommand(agent, createSandboxParameters);
          command.execute();
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
    }
  }


  /**
   * Returns relative project path
   */
  public String getRelativeBuildDir() {
//    final List depotPaths = getProjects();
//    final RepositoryPath rp = (RepositoryPath)depotPaths.get(0);
//    return rp.getPath();
    return ".";
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
    if (LOG.isDebugEnabled()) LOG.debug("begin getChangesSince changeListID: " + startChangeListID);
    try {

      // NOTE: simeshev@parabuilci.org - we do not do clean-up or init
      // of the local copy as we retrieve changes directly from MKS
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
        // roll forward for 1 second - otherwise MKS will add same last change list.
        final Calendar c = Calendar.getInstance();
        c.setTime(latest.getCreatedAt());
        c.add(Calendar.MINUTE, 1);
        beginDate = c.getTime();
      }

      // requests changes from MKS server
      final long timeStarted = System.currentTimeMillis();
      final Agent agent = getCheckoutDirectoryAwareAgent();
      final List result = new ArrayList(101);
      final Locale builderLocale = agent.defaultLocale();
      for (final Iterator i = getProjects().iterator(); i.hasNext();) {
        MKSCommand command = null;
        try {
          final RepositoryPath projectPath = (RepositoryPath) i.next();
          final String path = projectPath.getPath();
          if (LOG.isDebugEnabled()) LOG.debug("getting changes for: " + path);

          // execute MKS history command
          final MKSRlogCommandParameters parameters = new MKSRlogCommandParameters();
          setCommonParameters(parameters);
          parameters.setProject(path);
          command = new MKSRlogCommand(agent, parameters);
          command.execute();

          // analyze change LOG
          final String outputDateFormat = getSettingValue(SourceControlSetting.MKS_RLOG_DATE_FORMAT, MKSDateFormat.DEFAULT_OUTPUT_FORMAT);
          final MKSChangeListParser changeListParser = new MKSChangeListParser(parameters.getProject(),
                  path, rowLimit, parameters.getDevelopmentPath(), beginDate, maxChangeListSize(), outputDateFormat);
          final List changeLists = changeListParser.parseChangeLog(command.getStdoutFile());
          if (LOG.isDebugEnabled()) LOG.debug("changelist size: " + changeLists.size());
//          if (LOG.isDebugEnabled()) LOG.debug("changeLists: " + changeLists);
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
      if (LOG.isDebugEnabled()) LOG.debug("time to process change lists: " + processingTime);

      // return if no changes
      if (result.isEmpty()) return startChangeListID;

      // validate that change lists contain not only exclusions
      if (new ExclusionPathFinder().onlyExclusionPathsPresentInChangeLists(result, getSettingValue(SourceControlSetting.VCS_EXCLUSION_PATHS))) {
        return startChangeListID;
      }

      // store changes
      if (LOG.isDebugEnabled()) LOG.debug("end getChangesSince");
      return configManager.saveBuildChangeLists(activeBuildID, result);
    } catch (final IOException e) {
      processException(e);
      throw new BuildException("Error while retrieving list of changes: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  private List getProjects() {
    final List result = new ArrayList(3);
    final String settingValue = getSettingValue(SourceControlSetting.MKS_PROJECT);
    final RepositoryPath projectPath = new RepositoryPath(settingValue);
    result.add(projectPath);
    return result;
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
    if (LOG.isDebugEnabled()) LOG.debug("begin label: " + label);
    if (this.lastSyncDate == null) throw new IllegalStateException("Attempted to label without syncing first");
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      // preExecute
//      cleanupLocalCopyIfNecessary();
//      initLocalCopyIfNecessary();
      // process
      for (final Iterator i = getProjects().iterator(); i.hasNext();) {
        MKSCommand command = null;
        try {
          final RepositoryPath projectPath = (RepositoryPath) i.next();
          final String project = projectPath.getPath();
          if (LOG.isDebugEnabled()) LOG.debug("labeling for: " + project);
          // execute MKS label command
          final MKSAddlabelCommandParameters parameters = new MKSAddlabelCommandParameters();
          setCommonParameters(parameters);
          parameters.setProject(project);
          parameters.setLabel(label);
          command = new MKSAddlabelCommand(agent, parameters);
          command.execute();
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
      if (LOG.isDebugEnabled()) LOG.debug("end label: " + label);
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
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.MKS_HOST);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.MKS_PORT);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.MKS_USER);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.MKS_PASSWORD);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.MKS_DEVELOPMENT_PATH);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.MKS_PROJECT);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.MKS_LINE_TERMINATOR);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.MKS_PROJECT_REVISION);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.MKS_CO_DATE_FORMAT);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.MKS_RLOG_DATE_FORMAT);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE);
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


  public int removeLabels(final String[] labels) throws BuildException, CommandStoppedException, AgentFailureException {
    int removedLabelCounter = 0;
    for (int i = 0; i < labels.length; i++) {
      removeLabel(labels[i]);
      removedLabelCounter++;
    }
    return removedLabelCounter;
  }


  private void removeLabel(final String label) throws CommandStoppedException, BuildException, AgentFailureException {
    if (LOG.isDebugEnabled()) LOG.debug("begin deleting label: " + label);
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      // process
      for (final Iterator i = getProjects().iterator(); i.hasNext();) {
        MKSCommand command = null;
        try {
          final RepositoryPath projectPath = (RepositoryPath) i.next();
          final String project = projectPath.getPath();
          if (LOG.isDebugEnabled()) LOG.debug("deleting label for: " + project);
          // execute MKS label command
          final MKSDeletelabelCommandParameters parameters = new MKSDeletelabelCommandParameters();
          setCommonParameters(parameters);
          parameters.setProject(project);
          parameters.setLabel(label);
          command = new MKSDeletelabelCommand(agent, parameters);
          command.execute();
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
      if (LOG.isDebugEnabled()) LOG.debug("end label: " + label);
    } catch (final IOException e) {
      processException(e);
      throw new BuildException("Error while labeling the build: " + StringUtils.toString(e), e, getAgentHost());
    }
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
      throw new BuildException("Error while accessing MKS: executable not found.", getAgentHost());
    }
    throw new BuildException("Error while accessing MKS: " + StringUtils.toString(e), e, getAgentHost());
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
      // validate it's there
      final ChangeList changeList = configManager.getChangeList(changeListID);
      if (changeList == null) return "No information";

      // make sync note
      final Agent agent = getCheckoutDirectoryAwareAgent();
      final StringBuilder result = new StringBuilder(100);
      final DateFormat dateFormat = MKSCoCommand.createCoDateFormat(getSettingValue(SourceControlSetting.MKS_CO_DATE_FORMAT), agent);
      result.append("si co -r ").append(StringUtils.putIntoDoubleQuotes("time:" + changeList.getCreatedAt(dateFormat)));
      return result.toString();
    } catch (final IOException e) {
      if (LOG.isDebugEnabled()) LOG.debug("Error while generating sync command note", e);
      return "No information";
    }
  }


  /**
   * Helper method to pupulate common StarTeam command
   * parameters from current settings.
   *
   * @param parameters
   */
  private void setCommonParameters(final MKSCommandParameters parameters) {
    parameters.setHost(getSettingValue(SourceControlSetting.MKS_HOST));
    parameters.setExePath(getSettingValue(SourceControlSetting.MKS_PATH_TO_EXE));
    parameters.setPassword(StringUtils.isBlank(getSettingValue(SourceControlSetting.MKS_PASSWORD)) ? "" : org.parabuild.ci.security.SecurityManager.decryptPassword(getSettingValue(SourceControlSetting.MKS_PASSWORD)));
    parameters.setPort(getSettingValue(SourceControlSetting.MKS_PORT, 7001));
    parameters.setUser(getSettingValue(SourceControlSetting.MKS_USER));
    parameters.setDevelopmentPath(getSettingValue(SourceControlSetting.MKS_DEVELOPMENT_PATH));
  }
}
