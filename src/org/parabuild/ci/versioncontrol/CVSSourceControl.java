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
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.process.RemoteCommand;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.CommonConstants;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.util.ThreadUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * CVS SourceControl implementation
 * @noinspection BooleanMethodIsAlwaysInverted
 */
final class CVSSourceControl extends AbstractSourceControl implements CommonConstants {

  private static final Log LOG = LogFactory.getLog(CVSSourceControl.class); // NOPMD

  public static final String CVS_CVSROOT_USERS = "CVSROOT/users";

  private final SimpleDateFormat CVS_DATE_FORMATTER = makeCVSDateFormatter();
  private Date lastSyncDate = null;


  /**
   * Create an instance of the CVSSourceControl from the build
   * configuration
   *
   * @param buildConfig
   */
  public CVSSourceControl(final BuildConfig buildConfig) {
    super(buildConfig);
    validateIsCVSConfiguration(buildConfig);
  }


  /**
   * Checks out latest state of the source line
   */
  public void checkoutLatest() throws BuildException, CommandStoppedException, AgentFailureException {
    final long started = System.currentTimeMillis();
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      if (LOG.isDebugEnabled()) {
        LOG.debug("Begin checkout latest, agent: " + agent.getHost().getHost());
      }
      cleanupLocalCopyIfNecessary();

      // run formal login if necessary
      execExplicitLoginIfNecessary(agent);

      // traverse list of paths
      for (final Iterator i = getDepotPaths().iterator(); i.hasNext();) {
        CVSCommand command = null;
        try {
          final RepositoryPath repositoryPath = (RepositoryPath) i.next();
          // make and execute checkout command for a single path
          final StringBuffer checkoutCommand = makeCheckoutCommand(getCVSExePath(), getCVSRoot(), repositoryPath.getPath(), getCVSBranch());
          command = new CVSCommand(agent, checkoutCommand);
          command.setCVSPassword(getCVSPassword());
          command.setCVSRoot(getCVSRoot());
          command.setCVSRshPath(getCVSRshPath());
          command.execute();
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("end checkout latest");
      }
    } catch (final IOException e) {
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, getAgentHost());
    } finally {
      if (LOG.isDebugEnabled()) {
        LOG.debug("check out latest took : " + (System.currentTimeMillis() - started) + " ms");
      }
    }
  }


  /**
   * Synchronizes local copy to the given change list ID
   *
   * @param changeListID change list ID to sync to.
   */
  public void syncToChangeList(final int changeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    final long started = System.currentTimeMillis();
    if (LOG.isDebugEnabled()) {
      LOG.debug("begin syncToChangeList changeListID: " + changeListID);
    }
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      if (LOG.isDebugEnabled()) {
        LOG.debug("agent: " + agent);
      }
      validateChangeListID(changeListID);
      cleanupLocalCopyIfNecessary();
      initLocalCopyIfNecessary();

      // run formal login if necessary
      execExplicitLoginIfNecessary(agent);

      // get latest change date for this change list. CVS doesn't support
      // change IDs, so we sync to the date.
      final ChangeList changeList = configManager.getChangeList(changeListID);
      final Date changeDate = changeList.getCreatedAt();
      lastSyncDate = changeList.getCreatedAt();
      if (LOG.isDebugEnabled()) {
        LOG.debug("changeDate: " + changeDate);
      }

      for (final Iterator i = getDepotPaths().iterator(); i.hasNext();) {
        CVSCommand command = null;
        try {
          final RepositoryPath repositoryPath = (RepositoryPath) i.next();
          //  execute the checkout command
          final StringBuffer syncCommand = makeSyncCommand(getCVSExePath(), getCVSRoot(), repositoryPath.getPath(), changeDate, getCVSBranch());
          command = new CVSCommand(agent, syncCommand);
          command.setCVSPassword(getCVSPassword());
          command.setCVSRoot(getCVSRoot());
          command.setCVSRshPath(getCVSRshPath());
          command.execute();
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("end syncToChangeList");
      }
    } catch (final IOException e) {
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, getAgentHost());
    } finally {
      if (LOG.isDebugEnabled()) {
        LOG.debug("sync to change list took : " + (System.currentTimeMillis() - started) + " ms");
      }
    }
  }


  /**
   * Synchronizes local copy to the latest state of the source
   * line.
   */
  void syncToLatest() throws BuildException, CommandStoppedException, AgentFailureException {
    final long started = System.currentTimeMillis();
    if (LOG.isDebugEnabled()) {
      LOG.debug("begin syncToLatest");
    }
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      cleanupLocalCopyIfNecessary();
      initLocalCopyIfNecessary();

      // run formal login if necessary
      execExplicitLoginIfNecessary(agent);

      for (final Iterator i = getDepotPaths().iterator(); i.hasNext();) {
        CVSCommand command = null;
        try {
          final RepositoryPath repPath = (RepositoryPath) i.next();
          //  execute the update command with null date
          final StringBuffer cmd = makeSyncCommand(getCVSExePath(), getCVSRoot(),
                  repPath.getPath(), null, getCVSBranch());
          command = new CVSCommand(agent, cmd);
          command.setCVSPassword(getCVSPassword());
          command.setCVSRoot(getCVSRoot());
          command.setCVSRshPath(getCVSRshPath());
          command.execute();
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("end syncToLatest");
      }
    } catch (final IOException e) {
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, getAgentHost());
    } finally {
      if (LOG.isDebugEnabled()) {
        LOG.debug("sync to latest took : " + (System.currentTimeMillis() - started) + " ms");
      }
    }
  }


  /**
   * Returns true if there could be new history since given
   * date.
   */
  boolean newHistorySince(final Date fromDate) throws BuildException, CommandStoppedException, AgentFailureException {
    final long started = System.currentTimeMillis();
    if (LOG.isDebugEnabled()) {
      LOG.debug("begin pre-check for: " + fromDate + ", build ID: " + buildID);
    }
    CVSCommand command = null;
    BufferedReader br = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      // run formal login if necessary
      execExplicitLoginIfNecessary(agent);

      //  execute the checkout command
      final StringBuffer historyCommand = makeHistoryCommand(getCVSExePath(), getCVSRoot(), fromDate);
      command = new CVSCommand(agent, historyCommand);
      command.setCVSPassword(getCVSPassword());
      command.setCVSRoot(getCVSRoot());
      command.setCVSRshPath(getCVSRshPath());
      command.execute();
      // check changes
      br = new BufferedReader(new FileReader(command.getStdoutFile()));
      final String firstLine = br.readLine();
      if ("No records selected.".equalsIgnoreCase(firstLine)) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("no new history for: " + fromDate + ", build ID: " + buildID);
        }
        return false;
      } else {
        if (LOG.isDebugEnabled()) {
          LOG.debug("there is new history for: " + fromDate + ", build ID: " + buildID + ", fsz: " + command.getStdoutFile().length());
        }
        return true;
      }
    } catch (final IOException e) {
      throw new BuildException("Error while looking for changes: " + StringUtils.toString(e), e, getAgentHost());
    } finally {
      IoUtils.closeHard(br);
      cleanup(command);
      if (LOG.isDebugEnabled()) {
        LOG.debug("end pre-check");
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("pre-check took : " + (System.currentTimeMillis() - started) + " ms");
      }
    }
  }


  /**
   * Returns project build dir relative to checkout path
   */
  public String getRelativeBuildDir() throws BuildException {
    // are advanced settings enabled?
    final SystemConfigurationManager manager = SystemConfigurationManagerFactory.getManager();
    if (manager.isAdvancedConfigurationMode()) {
      // is custom build dir set?
      final String customBuildDirSetting = getSettingValue(VCSAttribute.CVS_CUSTOM_RELATIVE_BUILD_DIR, null);
      if (StringUtils.isBlank(customBuildDirSetting)) {
        return firstRepositoryPath();
      } else {
        return customBuildDirSetting;
      }
    } else {
      return firstRepositoryPath();
    }
  }


  /**
   * @return first path in the list of repository paths.
   */
  private String firstRepositoryPath() throws BuildException {
    return ((RepositoryPath) getDepotPaths().get(0)).getPath();
  }


  /**
   * Returns ID of list of changes that were made to controlled
   * source line since the given change list ID
   * <p/>
   * In order to run successfully this method needs an already
   * checked out local copy on the client.
   * <p/>
   * Handling zero ID change list: When this method is called
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
   */
  public int getChangesSince(final int startChangeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    final long started = System.currentTimeMillis();
    if (LOG.isDebugEnabled()) {
      LOG.debug("begin getChangesSince changeListID: " + startChangeListID);
    }
    try {

      cleanupLocalCopyIfNecessary();
      initLocalCopyIfNecessary();

      final Date fromDate;
      final int maxChangeLists;

      // check if it is first run (changeListID equals UNSAVED_ID)
      if (startChangeListID == ChangeList.UNSAVED_ID) {
        // REVIEWME: add checking if there are change lists in the database,
        // add corresponding test.
        //
        // set up from date to the beginning of century
        final Calendar calendar = Calendar.getInstance();
        calendar.set(1974, 1, 1);
        fromDate = calendar.getTime();
        maxChangeLists = initialNumberOfChangeLists();
      } else {
        // get last build change date
        final ChangeList startChangeList = configManager.getChangeList(startChangeListID);
        // where there changes?
        if (startChangeList == null) {
          return startChangeListID;
        }
        fromDate = startChangeList.getCreatedAt();
        maxChangeLists = maxNumberOfChangeLists();
        // do pre-check with cvs history command
        final String preCheck = getSettingValue(VCSAttribute.CVS_CHANGE_PRECHECK, SourceControlSetting.OPTION_UNCHECKED);
        if (preCheck.equalsIgnoreCase(SourceControlSetting.OPTION_CHECKED)) {
          // REVIEWME: simeshev@parabuilci.org - 11/28/2004 - cvs history
          // works on the whole repository. This can cause false
          // positives if the repository holds other actively
          // modified projects. Lots of false positives make calling
          // cvs history meaningless and in fact increasing time to
          // detect a change an the long run. In future we may
          // consider accumulating statistics on false positives and
          // use it for heuristic decision to run history precheck.
          if (!newHistorySince(fromDate)) {
            return startChangeListID; // no new changes according to cvs history command.
          }
        }
      }

      // we sync to latest to pick up new directories. without
      // added directories are not seen.
      //
      // REVIEWME: vimeshev - the only problem is if it were a clean
      // checkout, this call would be unnecessary.
      syncToLatest();

      // requests changes from CVS server
      final long timeStarted = System.currentTimeMillis();
      final List result = getChangesFromDate(fromDate, maxChangeLists);
      final long processingTime = System.currentTimeMillis() - timeStarted;

      // return if no changes
      if (result.isEmpty()) {
        return startChangeListID;
      }

      // check if we should run check-in window check
      final int changeWindow = getSettingValue(VCSAttribute.CVS_CHANGE_WINDOW, 0) * 1000;
      if (changeWindow > 0) {

        // wait for change window if necessary.
        ThreadUtils.checkIfInterrupted();
        final long timeToWait = Math.max(changeWindow - processingTime, 0);
        if (timeToWait > 0) {
          try {
            Thread.sleep(timeToWait);
          } catch (final InterruptedException e) {
            throw new CommandStoppedException();
          }
        }

        // run again - first item in the list should contain the newest change list.
        final Date secondRunFromDate = ((ChangeList) result.get(0)).getCreatedAt();
        if (LOG.isDebugEnabled()) {
          LOG.debug("second run: initial: " + fromDate.toString() + ", second: " + secondRunFromDate.toString());
        }
        final List secondRunResult = getChangesFromDate(secondRunFromDate, maxChangeLists);
        final ChangeListWindowMerger merger = new ChangeListWindowMerger();
        merger.mergeInChangesLeft(result, secondRunResult);
        // we resort it
        result.sort(ChangeList.REVERSE_CHANGE_DATE_COMPARATOR);
      }

      // validate that change lists contain not only exclusions
      if (new ExclusionPathFinder().onlyExclusionPathsPresentInChangeLists(result, getSettingValue(VCSAttribute.VCS_EXCLUSION_PATHS))) {
        return startChangeListID;
      }

      // store changes
      if (LOG.isDebugEnabled()) {
        LOG.debug("end getChangesSince");
      }
      return configManager.saveBuildChangeLists(activeBuildID, result);
    } catch (final IOException e) {
      processException(e);
      throw new BuildException("Error while retrieving list of changes: " + StringUtils.toString(e), e, getAgentHost());
    } finally {
      if (LOG.isDebugEnabled()) {
        LOG.debug("get changes since took : " + (System.currentTimeMillis() - started) + " ms");
      }
    }
  }


  /**
   * Returns change lists in reverse date order.
   */
  private List getChangesFromDate(final Date fromDate, final int maxChangeLists) throws BuildException, IOException, CommandStoppedException, AgentFailureException {
    ThreadUtils.checkIfInterrupted();

    final long started = System.currentTimeMillis();
    final Agent agent = getCheckoutDirectoryAwareAgent();

    // run formal login if necessary
    execExplicitLoginIfNecessary(agent);

    final List result = new ArrayList(101);
    for (final Iterator i = getDepotPaths().iterator(); i.hasNext();) {
      CVSCommand command = null;
      try {
        final RepositoryPath repositoryPath = (RepositoryPath) i.next();
        final String path = repositoryPath.getPath();
        if (LOG.isDebugEnabled()) {
          LOG.debug("getting changes starting " + fromDate + " for: " + path);
        }
        // exec LOG command
        final StringBuffer logCommand = makeLogCommand(getCVSExePath(), getCVSRoot(), path, fromDate, getCVSBranch());
        command = new CVSCommand(agent, logCommand);
        command.setCVSPassword(getCVSPassword());
        command.setCVSRoot(getCVSRoot());
        command.setCVSRshPath(getCVSRshPath());
        command.execute();

        // DELETEME: debug
//        if (LOG.isDebugEnabled()) LOG.debug("command.getStderrFile(): " + IoUtils.fileToString(command.getStderrFile()));
//        if (LOG.isDebugEnabled()) LOG.debug("command.getStdoutFile(): " + IoUtils.fileToString(command.getStdoutFile()));
        // NOTE: simeshev@parabuilci.org - 08/29/2003 - here we check if the output file
        // was created. For now there is only keys known when it happens - when the CVS
        // contains only directories and no files has ben ever submitted. We treat this
        // situation as like there were no changes.
        if (!command.getStdoutFile().exists()) {
          continue;
        }

        // analyze change LOG and add this path changes to result
        final CVSMissingBranchRevisionParser missingRevisionParser = new CVSMissingBranchRevisionParser(getCVSBranch());
        final Map missingRevisionsHashes = missingRevisionParser.parse(command.getStderrFile());
        final CVSChangeLogParser changeLogParser = new CVSChangeLogParser(maxChangeLists, maxChangeListSize());
        // NOTE: vimeshev - 2005-12-22 - commented out for shortened
        // paths make it impossible to use ViewCVS
        //changeLogParser.setRepositoryPath(repositoryPath.getPath());
        changeLogParser.setBranchName(getCVSBranch());
        changeLogParser.setRCSNamesHashesToExclude(missingRevisionsHashes);
        final List changeLists = changeLogParser.parseChangeLog(command.getStdoutFile());
        result.addAll(changeLists);
      } finally {
        cleanup(command); // cleanup this cycle
      }
    }

    // limit result size if necessary
    result.sort(ChangeList.REVERSE_CHANGE_DATE_COMPARATOR);

    if (LOG.isDebugEnabled()) {
      LOG.debug("get changes from date took : " + (System.currentTimeMillis() - started) + " ms");
    }

    // result
    ThreadUtils.checkIfInterrupted();
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
    if (this.lastSyncDate == null) {
      return;
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("begin label: " + label);
    }
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      // preExecute
      validateLabelName(label);
      cleanupLocalCopyIfNecessary();
      initLocalCopyIfNecessary();

      // run formal login if necessary
      execExplicitLoginIfNecessary(agent);

      // process
      for (final Iterator i = getDepotPaths().iterator(); i.hasNext();) {
        CVSCommand command = null;
        try {
          final RepositoryPath repositoryPath = (RepositoryPath) i.next();
          // execute the checkout command
          final StringBuffer labelCommand = makeLabelCommand(getCVSExePath(), getCVSRoot(), repositoryPath.getPath(), label, getCVSBranch());
          command = new CVSCommand(agent, labelCommand);
          command.setCVSPassword(getCVSPassword());
          command.setCVSRoot(getCVSRoot());
          command.setCVSRshPath(getCVSRshPath());
          command.execute();
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("end label: " + label);
      }
    } catch (final IOException e) {
      processException(e);
      throw new BuildException("Error while labeling the build: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  /**
   * Returns a map containing CVS user names as keys and e-mails
   * as values. It tries get the map from CVSROOT/users. This
   * method doesn't throw exceptions as it's failure is not
   * critical but it reports errors by calling to ErrorManager.
   *
   * @see ErrorManagerFactory
   * @see ErrorManager
   */
  public Map getUsersMap() throws CommandStoppedException {
    File fileToParse = null;
    CVSCommand command = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      // run formal login if necessary
      execExplicitLoginIfNecessary(agent);

      // make and execute checkout users command
      // REVIEWME: currently checks out users to a permanent place - consider temp-named dir?
      final StringBuffer getUsersCommand = makeCheckoutCommand(getCVSExePath(), getCVSRoot(), CVS_CVSROOT_USERS, null);
      command = new CVSCommand(agent, getUsersCommand);
      command.setCurrentDirectory(agent.getTempDirName()); // change dir from default checkout to temp
      command.setCVSPassword(getCVSPassword());
      command.setCVSRoot(getCVSRoot());
      command.setCVSRshPath(getCVSRshPath());
      command.execute();

      // get users file from backend
      fileToParse = IoUtils.createTempFile(".auto", ".scm");

      // REVIEWME: we should shorten it by using/introducing "pathExists" method on agent/agent environment
      final String remotePath = agent.getTempDirName() + '/' + CVS_CVSROOT_USERS;
      if (agent.relativeTempPathExists(CVS_CVSROOT_USERS)) {
        agent.readFile(remotePath, fileToParse);
      }

      // parse output and return
      final CVSUsersParser usersParser = new CVSUsersParser();
      usersParser.setCVSRoot(getCVSRoot());
      usersParser.setUsersFile(fileToParse);
      usersParser.setBuildID(buildID);
      return usersParser.parse();
    } catch (final CommandStoppedException e) {
      throw e;
    } catch (final Exception e) {
      final Error err;
      if (e.getMessage().contains("cvs server: cannot find module") && e.getMessage().contains("CVSROOT/users")) {
        // Missing CVS/modules
        err = new Error("Could not retrieve VCS user to e-mail map - CVSROOT/users is missing. " +
                "To correct this warning, un-check the box \"Use version control e-mails\" under the \"Notification\" " +
                "tab on the build configuration screen or add CVSROOT/users according to the CVS documentation.");
      } else {
        err = new Error("Error while getting user map: " + StringUtils.toString(e));
      }
      err.setErrorLevel(Error.ERROR_LEVEL_WARNING);
      err.setDetails(e);
      errorManager.reportSystemError(err);
    } finally {
      // clean up
      IoUtils.deleteFileHard(fileToParse);
      cleanup(command);
    }
    return Collections.emptyMap();
  }


  /**
   * Returns text description of a command to be used by a
   * customer to sync to a given changelist. This is a default
   * implementation.
   *
   * @param changeListID
   */
  public String getSyncCommandNote(final int changeListID) {
    final ChangeList changeList = configManager.getChangeList(changeListID);
    if (changeList == null) {
      return "No information";
    }
    // make common sync note
    final StringBuilder result = new StringBuilder("cvs update -P -A -d -D").append(STR_SPACE)
            .append('\"').append(CVS_DATE_FORMATTER.format(changeList.getCreatedAt())).append("\" ");
    // add branch if necessary
    final String branchName = changeList.getBranch();
    if (!StringUtils.isBlank(branchName)) {
      result.append("-r").append(STR_SPACE).append(branchName);
    }
    return result.toString();
  }


  /**
   * @return Map with a shell variable name as a key and variable
   *         value as value. The shell variables will be made
   *         available to the build commands.
   * @see BuildScriptGenerator#addVariables(Map)
   */
  public Map getShellVariables() throws IOException {
    try {
      final HashMap variableMap = new HashMap(2);
      variableMap.put("PARABUILD_CVS_ROOT", getCVSRoot());
      final String cvsBranch = getCVSBranch();
      if (!StringUtils.isBlank(cvsBranch)) {
        variableMap.put("PARABUILD_CVS_BRANCH", cvsBranch);
      }
      return variableMap;
    } catch (final BuildException e) {
      throw IoUtils.createIOException(e);
    }
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
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.CVS_REPOSITORY_PATH);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.CVS_ROOT);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.CVS_BRANCH_NAME);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE);
    if (hasToCleanUp) {
      setHasToCleanUp();
    }
    // update current settings map
    replaceCurrentSettings(newSettings);
  }


  /**
   * <<<<<<< CVSSourceControl.java /** This GoF strategy method
   * validates that build directory is initialized according to
   * build configuration. Implementing classes may use this
   * method to perform additional validation of build directory.
   * <p/>
   * If this method returns false, initLocalCopyIfNecessary()
   * will call checkoutLatest() to populate build dir. =======
   * /** This GoF strategy method validates that build directory
   * is initialized according to build configuration.
   * Implementing classes may use this method to perform
   * additional validation of build directory.
   * <p/>
   * If this method returns false, initLocalCopyIfNecessary()
   * will call checkoutLatest() to populate build dir.
   *
   * @return build directory is initialized according to build
   * @see AbstractSourceControl#initLocalCopyIfNecessary()
   * @see SourceControl#checkoutLatest()
   */
  public final boolean isBuildDirInitialized() throws IOException, BuildException, AgentFailureException {
    boolean result = true;
    final Agent agent = getCheckoutDirectoryAwareAgent();
    final String rbd = getRelativeBuildDir();
    if (!(agent.fileRelativeToCheckoutDirExists(rbd + '/' + "CVS")
            && agent.fileRelativeToCheckoutDirExists(rbd + '/' + "CVS/Root")
            && agent.fileRelativeToCheckoutDirExists(rbd + '/' + "CVS/Repository")
            && agent.fileRelativeToCheckoutDirExists(rbd + '/' + "CVS/Entries"))
            ) {
      result = false;
    }
    return result;
  }


  /**
   * Helper method to validate CVS label name
   */
  private void validateLabelName(final String label) throws BuildException {
    if (StringUtils.isBlank(label)) {
      throw new IllegalArgumentException("Label name can not be blank");
    }
    if ("HEAD".equals(label.trim().toUpperCase()) || "BASE".equals(label.trim().toUpperCase())) {
      throw new BuildException("Label is not allowed to be \"BASE\" or \"HEAD\"", getAgentHost());
    }
  }


  /**
   * Creates a StringBuffer containing CVS checkout command
   *
   * @param cvsExePath String path to CVS executable
   * @param cvsRoot    String CVS root
   * @param cvsPath    String CVS repository path
   * @return a StringBuffer containing CVS checkout command
   */
  private StringBuffer makeCheckoutCommand(final String cvsExePath, final String cvsRoot, final String cvsPath, final String cvsBranch) {
    final StringBuffer checkoutCommand = makeCommandPrefix(cvsExePath, cvsRoot);
    checkoutCommand.append("-z").append(getCompression()).append(STR_SPACE);
    checkoutCommand.append("checkout").append(STR_SPACE);
    checkoutCommand.append("-A").append(STR_SPACE);
    if (!StringUtils.isBlank(cvsBranch)) {
      checkoutCommand.append("-r").append(STR_SPACE).append(cvsBranch).append(STR_SPACE);
    }
    checkoutCommand.append(StringUtils.putIntoDoubleQuotes(cvsPath));
    return checkoutCommand;
  }


  private StringBuffer makeLogCommand(final String cvsExePath, final String cvsRoot, final String cvsPath, final Date fromDate, final String cvsBranch) throws IOException, AgentFailureException {
    final Agent agent = getCheckoutDirectoryAwareAgent();
    final char dateSep = agent.isUnix() ? '\"' : '\"';
    final StringBuffer logCommand = makeCommandPrefix(cvsExePath, cvsRoot);
    logCommand.append("log").append(" -N ")
            .append("-d ").append(dateSep).append(CVS_DATE_FORMATTER.format(fromDate)).append('<')
            .append(dateSep).append(STR_SPACE);
    if (StringUtils.isBlank(cvsBranch)) {
      logCommand.append("-b").append(STR_SPACE); // main trunk
    } else {
      logCommand.append("-r").append(cvsBranch).append(STR_SPACE);
    }
    if (getSuppressLogOutput()) {
      logCommand.append("-S").append(STR_SPACE); // suppress LOG output if no
    }
    logCommand.append(StringUtils.putIntoDoubleQuotes(cvsPath));
    return logCommand;
  }


  /**
   * Helper method to return true if suppressing log output
   * for no changes is enabled.
   *
   * @return true if suppressing log output for no changes
   *         is enabled.
   */
  private boolean getSuppressLogOutput() {
    return getSettingValue(VCSAttribute.CVS_SUPPRESS_LOG_OUTPUT_IF_NO_CHANGES, SourceControlSetting.OPTION_UNCHECKED).equals(SourceControlSetting.OPTION_CHECKED);
  }


  private StringBuffer makeHistoryCommand(final String cvsExePath, final String cvsRoot, final Date lastChangeListDate) {
    final char dateSep = '\"';
    return makeCommandPrefix(cvsExePath, cvsRoot)
            .append("history -c -a -D ")
            .append(dateSep)
            .append(CVS_DATE_FORMATTER.format(new Date(lastChangeListDate.getTime() + 1000L)))// add 1 second to simulate GT statement
            .append(dateSep);
  }


  /**
   * Makes a CVS update command.
   *
   * @param cvsExePath Path to CVS executable.
   * @param cvsRoot    CVS root
   * @param cvsPath    CVS path
   * @param changeDate Change date. If null will make a command
   *                   that updates to the latest state.
   * @param cvsBranch  CVS branch name.
   * @return StringBuffer containing CVS updated command
   */
  private StringBuffer makeSyncCommand(final String cvsExePath, final String cvsRoot,
                                       final String cvsPath, final Date changeDate, final String cvsBranch) {

    final StringBuffer cmd = makeCommandPrefix(cvsExePath, cvsRoot);
    cmd.append("-z").append(getCompression()).append(STR_SPACE)
            .append("update -P -A -d -C ");
    if (changeDate != null) {
      cmd.append("-D ").append('\"').append(CVS_DATE_FORMATTER.format(changeDate)).append("\" ");
    }
    if (!StringUtils.isBlank(cvsBranch)) {
      cmd.append("-r").append(STR_SPACE).append(cvsBranch).append(STR_SPACE);
    }
    cmd.append(StringUtils.putIntoDoubleQuotes(cvsPath));
    return cmd;
  }


  private StringBuffer makeLabelCommand(final String cvsExePath, final String cvsRoot, final String cvsPath, final String label, final String cvsBranch) {
    // REVIEWME: simeshev@parabuilci.org  11/21/2003 - think about somehow identifying
    // if there is already the a branch tag with the same name. Here is CVS docs say:
    //
    // "If any branch tags are encountered in the repository with the given name,
    // a warning is issued and the branch tag is not disturbed. If you are absolutely
    // certain you wish to move the branch tag, the -B option may be specified.
    // In that case, non-branch tags encountered with the given name are ignored
    // with a warning message."
    //
    final StringBuffer cmd = makeCommandPrefix(cvsExePath, cvsRoot);
    cmd.append("rtag -F").append(STR_SPACE);
    cmd.append("-D \"").append(CVS_DATE_FORMATTER.format(this.lastSyncDate)).append('\"').append(STR_SPACE);
    if (!StringUtils.isBlank(cvsBranch)) {
      cmd.append("-r").append(STR_SPACE).append(cvsBranch).append(STR_SPACE);
    }
    cmd.append(label).append(STR_SPACE).append(StringUtils.putIntoDoubleQuotes(cvsPath)).append(STR_SPACE);
    return cmd;
  }


  /**
   * Makes common CVS command beginning
   *
   * @param cvsExePath
   * @param cvsRoot
   * @return common CVS command beginning
   */
  private static StringBuffer makeCommandPrefix(final String cvsExePath, final String cvsRoot) {
    final StringBuffer result = new StringBuffer(200);
    result.append(cvsExePath).append(STR_SPACE);
    result.append("-q -d ").append(cvsRoot).append(STR_SPACE);
    return result;
  }


  /**
   * Returns path to CVS executable
   *
   * @return String path to CVS executable
   */
  private String getCVSExePath() {
    return StringUtils.putIntoDoubleQuotes(getSettingValue(VCSAttribute.CVS_PATH_TO_CLIENT, "cvs"));
  }


  /**
   * Helper method to return CVS root from the instance settings
   * list
   *
   * @return String CVS root
   */
  private String getCVSRoot() throws BuildException {
    final String cvsRoot = getSettingValue(VCSAttribute.CVS_ROOT);
    validateCVSRoot(cvsRoot);
    return cvsRoot;
  }


  /**
   * Helper method to return CVS root from the instance settings
   * list
   *
   * @return String CVS root
   */
  private String getCVSBranch() {
    return getSettingValue(VCSAttribute.CVS_BRANCH_NAME);
  }


  /**
   * Returns project source line repository paths relative to CVS
   * root. Firs item in the list is a project build home.
   *
   * @return List of CVS repository paths composing a project
   * @throws BuildException
   */
  private List getDepotPaths() throws BuildException {
    // get lines
    final List lines = StringUtils.multilineStringToList(getSettingValue(VCSAttribute.CVS_REPOSITORY_PATH));
    if (lines.size() <= 0) {
      throw new BuildException("Build configuration does not contain non-empty repository path", getAgentHost());
    }
    // validate and create result
    final List paths = new ArrayList(lines.size());
    for (final Iterator i = lines.iterator(); i.hasNext();) {
      final String line = validateAndNormalizeSingleLineDepotPath((String) i.next());
      paths.add(new RepositoryPath(line));
    }
    return paths;
  }


  /**
   * Removes trailing slashes from the source line depotPath
   */
  private String validateAndNormalizeSingleLineDepotPath(final String depotPath) throws BuildException {
    // validate
    if (StringUtils.isBlank(depotPath)) {
      throw new BuildException("Build configuration can not contain empty CVS repository depotPath", getAgentHost());
    }
    // normalize
    String normalizedDepotPath = depotPath.replace('\\', '/');
    while (normalizedDepotPath.charAt(0) == '/') {
      normalizedDepotPath = normalizedDepotPath.substring(1);
    }
    return normalizedDepotPath;
  }


  /**
   * @return CVS password, or null if not defined
   */
  private String getCVSPassword() {
    final String encryptedPassword = getSettingValue(VCSAttribute.CVS_PASSWORD);
    if (encryptedPassword == null) {
      return null;
    }
    return SecurityManager.decryptPassword(encryptedPassword);
  }


  /**
   * @return CVS password, or null if not defined
   */
  private String getCVSRshPath() {
    return getSettingValue(VCSAttribute.CVS_PATH_TO_RSH);
  }


  /**
   * @return CVS compression
   */
  private String getCompression() {
    final String defaultCompression = "0";
    final String s = getSettingValue(VCSAttribute.CVS_COMPRESSION, defaultCompression);
    if (StringUtils.isBlank(s) || !StringUtils.isValidInteger(s.trim())) {
      return defaultCompression;
    }
    final int v = Integer.parseInt(s.trim());
    return v >= 0 && v <= 9 ? Integer.toString(v) : defaultCompression;
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
      if (getCVSExePath() != null) {
        throw new BuildException("Error while checking out: CVS executable \"" + getCVSExePath() + "\" not found.", getAgentHost());
      }
    }
    throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, getAgentHost());
  }


  private void validateCVSRoot(final String cvsRoot) throws BuildException {
    if (cvsRoot == null) {
      throw new BuildException("Build configuration does not define CVS root.", getAgentHost());
    }
  }


  /**
   * Validates that correct CVS build configuration was passed
   */
  private static void validateIsCVSConfiguration(final BuildConfig buildConfig) {
    if (buildConfig.getSourceControl() != VersionControlSystem.SCM_REFERENCE && buildConfig.getSourceControl() != VersionControlSystem.SCM_CVS) {
      throw new IllegalArgumentException("Non-CVS build configuration");
    }
  }


  private static SimpleDateFormat makeCVSDateFormatter() {
    final SimpleDateFormat result = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z", Locale.US);
    result.setTimeZone(TimeZone.getTimeZone("GMT"));
    return result;
  }


  /**
   * This method is used to run formal login request if the client is CVSNT under Windows
   *
   * @param agent
   */
  private void execExplicitLoginIfNecessary(final Agent agent) throws IOException, CommandStoppedException, BuildException, AgentFailureException {
    if (agent.isWindows() && (getCVSRoot().startsWith(":pserver") || getCVSRoot().startsWith(":sspi"))) {
      // find if (CVSNT) string is present in the "cvs --version" output
      boolean isCVSNT = false;
      RemoteCommand versionCommand = null;
      BufferedReader br = null;
      try {
        versionCommand = new RemoteCommand(agent, true);
        versionCommand.setCommand(getCVSExePath() + " --version");
        versionCommand.execute();
        br = new BufferedReader(new FileReader(versionCommand.getStdoutFile()));
        String line = br.readLine();
        while (line != null) {
          if (line.contains("(CVSNT)")) {
            isCVSNT = true;
            break;
          }
          line = br.readLine();
        }
      } finally {
        IoUtils.closeHard(br);
        cleanup(versionCommand);
      }

      if (LOG.isDebugEnabled()) {
        LOG.debug("isCVSNT: " + isCVSNT);
      }

      // exec explicit login if present
      if (isCVSNT) {
        CVSCommand loginCommand = null;
        try {
          loginCommand = new CVSCommand(agent, makeCommandPrefix(getCVSExePath(), getCVSRoot()).append(" login"));
          // this will create remote file to hold the password and two line breaks
          loginCommand.setCVSPassword(getCVSPassword());
          loginCommand.setCVSRoot(getCVSRoot());
          loginCommand.setInputStream(new ByteArrayInputStream((getCVSPassword() + "\r\n").getBytes()));
          loginCommand.execute();
        } finally {
          cleanup(loginCommand);
        }
      }
    }
  }
}
