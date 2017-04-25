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
import org.parabuild.ci.common.ExceptionUtils;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.SettingResolver;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.process.RemoteCommand;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.security.SecurityManager;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Perforce version control implementation
 */
final class SVNSourceControl extends AbstractSourceControl {

  private static final Log LOG = LogFactory.getLog(SVNSourceControl.class); // NOPMD
  private static final String PARABUILD_SVN_REPOSITORY_PATH = "PARABUILD_SVN_REPOSITORY_PATH";
  private static final int INITIAL_CANGE_LIST_CAPACITY = 101;


  SVNSourceControl(final BuildConfig buildConfig) {
    super(buildConfig);
  }


  SVNSourceControl(final BuildConfig config, final List settings) {
    this(config);
    currentSettings = ConfigurationManager.settingsListToMap(settings);
  }


  /**
   * Checks out latest state of the source line
   *
   * @noinspection NestedTryStatement, ControlFlowStatementWithoutBraces
   */
  public void checkoutLatest() throws BuildException, CommandStoppedException, AgentFailureException {
    if (LOG.isDebugEnabled()) LOG.debug("begin checkoutLatest");
    Agent agent = null;
    try {
      agent = getCheckoutDirectoryAwareAgent();

      // hard clean up of checkout directory - SVN cannot stand
      // presense of already checked out SVN paths.
      agent.emptyCheckoutDir();

      // traverse list of paths
      SVNCommand command = null;
      for (final Iterator iter = getDepotPaths().iterator(); iter.hasNext(); ) {
        try {
          // get single path
          final RepositoryPath repositoryPath = (RepositoryPath) iter.next();

          // create SVN checkout command
          final boolean ignoreExternals = getIgnoreExternals();
          command = new SVNCheckoutCommand(agent, getPathToSVNExe(), getURL(), repositoryPath, ignoreExternals);
          command.setUser(getUser());
          command.setPassword(getPasswordSetting());
          command.setAddTrustServerCert(getAddTrustServerCertSetting());

          // exec
          execute(command);
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
      if (LOG.isDebugEnabled()) LOG.debug("end checkoutLatest");
    } catch (IOException e) {
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, agent);
    }
  }


  /**
   * Syncs to a given change list number
   *
   * @noinspection NestedTryStatement, ControlFlowStatementWithoutBraces, ThrowCaughtLocally
   */
  public void syncToChangeList(final int changeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    if (LOG.isDebugEnabled()) LOG.debug("begin syncToChangeList changeListID: " + changeListID);
    Agent agent = null;
    try {
      agent = getCheckoutDirectoryAwareAgent();
      validateChangeListID(changeListID);
      cleanupLocalCopyIfNecessary();
      initLocalCopyIfNecessary();

      // get latest change date for this change list. SVN doesn't support
      // change IDs, so we sync to the date.
      final ChangeList changeList = configManager.getChangeList(changeListID);
      final String changeListNumber = changeList.getNumber();

      for (final Iterator iter = getDepotPaths().iterator(); iter.hasNext(); ) {
        // get single path
        final RepositoryPath repositoryPath = (RepositoryPath) iter.next();

        // Execute SVN update command
        executeUpdateCommand(agent, repositoryPath, changeListNumber);
      }
      if (LOG.isDebugEnabled()) LOG.debug("end syncToChangeList");
    } catch (IOException e) {
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, agent);
    }
  }


  /**
   * Executes SVN update command taking into an account that it may need a cleanup.
   *
   * @param agent
   * @param repositoryPath
   * @param changeListNumber
   * @throws AgentFailureException
   * @throws CommandStoppedException
   * @throws IOException
   */
  private void executeUpdateCommand(final Agent agent, final RepositoryPath repositoryPath,
                                    final String changeListNumber)
          throws AgentFailureException, CommandStoppedException, IOException {

    // Execute until success or the number of attempts exhausted
    IOException commandException = null;
    boolean done = false;
    int attemptCount = 0;
    final int maxAttempts = 2;
    while (!done && attemptCount < maxAttempts) { // We need to try several time

      // Execute the update command once
      SVNCommand command = null;
      try {
        final boolean ignoreExternals = getIgnoreExternals();
        command = new SVNUpdateCommand(agent, getPathToSVNExe(), getURL(), repositoryPath, changeListNumber,
                ignoreExternals);
        command.setUser(getUser());
        command.setPassword(getPasswordSetting());
        command.setAddTrustServerCert(getAddTrustServerCertSetting());
        execute(command);
        done = true;

      } catch (IOException e) {

        // Store exception in case we cannot have
        // a clean execution w/in attempt count.
        commandException = e;

        // Check if we have to run a cleanup
        if (e.toString().indexOf("run 'svn cleanup' to remove locks") >= 0) {
          // Execute cleanup command
          SVNCommand updateCommand = null;
          try {
            updateCommand = new SVNCleanupCommand(agent, getPathToSVNExe(), getURL(), repositoryPath);
            updateCommand.setUser(getUser());
            updateCommand.setPassword(getPasswordSetting());
            updateCommand.setAddTrustServerCert(getAddTrustServerCertSetting());
            updateCommand.execute();
            attemptCount++;
          } finally {
            cleanup(updateCommand);
          }
        } else {
          throw e;
        }
      } finally {
        cleanup(command); // cleanup this cycle
      }
    }
    if (!done) {
      throw commandException;
    }
  }


  /**
   * Returns relative project path
   */
  public String getRelativeBuildDir() throws BuildException {
    return ((RepositoryPath) getDepotPaths().get(0)).getPath();
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
   * @noinspection ControlFlowStatementWithoutBraces
   */
  public int getChangesSince(final int startChangeListID) throws BuildException, CommandStoppedException, AgentFailureException {

    if (LOG.isDebugEnabled()) LOG.debug("begin getChangesSince changeListID: " + startChangeListID);
    try {

      // NOTE: simeshev@parabuilci.org - we do not do clean-up or init
      // of the local copy as we retrieve changes directly from SVN
      // server.

      final int maxChangeLists;
      final String changeListNumberFrom;
      String ignoreChangeListNumber = null;

      // check if it is first run (changeListID equals UNSAVED_ID)
      if (startChangeListID == ChangeList.UNSAVED_ID) {
        maxChangeLists = initialNumberOfChangeLists();
        changeListNumberFrom = "HEAD";
      } else {
        // get last build change date
        final ChangeList latest = configManager.getChangeList(startChangeListID);
        if (LOG.isDebugEnabled()) LOG.debug("latest: " + latest);
        // where there changes?
        if (latest == null) return startChangeListID;
        maxChangeLists = maxNumberOfChangeLists();
        changeListNumberFrom = latest.getNumber();
        // NOTE: SVN does not accept unexisting chlist numbers,
        // so we pass it to the change LOG parser so that it
        // ignores the "starting" chlist.
        ignoreChangeListNumber = changeListNumberFrom;
      }

      // requests changes from SVN server
      final int newChangeListID = getChangesSince(changeListNumberFrom, "HEAD", maxChangeLists, ignoreChangeListNumber);
      if (newChangeListID == ChangeList.UNSAVED_ID && startChangeListID != ChangeList.UNSAVED_ID) {
        return startChangeListID; // i.e. no changes since the exsiting one
      } else {
        return newChangeListID; // as os
      }
    } catch (IOException e) {
      Agent agent = null;
      //noinspection EmptyCatchBlock
      try {
        agent = getCheckoutDirectoryAwareAgent();
      } catch (Exception ex) {
      }
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, agent);
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
  public void label(final String label) {
    // SVN doesn't support labeling - issue warning.
    LOG.warn("Subversion doesn't support labeling");
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
    return Collections.EMPTY_MAP; // we currently don't support e-mails for SVN.
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
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.SVN_IGNORE_EXTERNALS);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.SVN_DEPOT_PATH);
    hasToCleanUp |= scd.settingHasChanged(SourceControlSetting.SVN_URL);
    if (hasToCleanUp) {
      setHasToCleanUp();
    }
    // update current settings map
    replaceCurrentSettings(newSettings);
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
   * @noinspection HardcodedFileSeparator
   * @see AbstractSourceControl#initLocalCopyIfNecessary()
   * @see SourceControl#checkoutLatest()
   */
  public boolean isBuildDirInitialized() throws IOException, BuildException, AgentFailureException {
    final Agent agent = getCheckoutDirectoryAwareAgent();
    boolean result = true;
    final String rbd = getRelativeBuildDir();
    if (!agent.fileRelativeToCheckoutDirExists(rbd)
            || !agent.fileRelativeToCheckoutDirExists(rbd + '/' + ".svn")) {
      result = false;
    }
    return result;
  }


  /**
   * Returns text description of a command to be used by a
   * customer to sync to a given changelist. This is a default
   * implementation.
   *
   * @param changeListID
   * @noinspection ControlFlowStatementWithoutBraces
   */
  public String getSyncCommandNote(final int changeListID) {
    final ChangeList changeList = configManager.getChangeList(changeListID);
    if (changeList == null) return STRING_NO_SYNC_NOTE_AVAILABLE;
    return "svn update -r" + changeList.getNumber();
  }


  /**
   * @return Map with a shell variable name as a key and variable
   *         value as value. The shell variables will be made
   *         avaiable to the build commands.
   *         <p/>
   *         This is a default implementation that returns an
   *         empty map.
   * @see BuildScriptGenerator#addVariables(Map)
   */
  public Map getShellVariables() throws IOException {

    try {

      final HashMap result = new HashMap(3);
      final List depotPaths = getDepotPaths();
      final StringBuffer sb = new StringBuffer(500);
      for (int i = 0; i < depotPaths.size(); i++) {

        final String path = (String) depotPaths.get(i);
        sb.append(path).append(";");
      }

      result.put(PARABUILD_SVN_REPOSITORY_PATH, sb);
      return result;
    } catch (BuildException e) {

      final IOException ioe = new IOException();
      ioe.initCause(e);
      throw ioe;
    }
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
    return getChangesSince(nativeChangeListNumber, nativeChangeListNumber, 1, null);
  }


  /**
   * Requests changes from Subversion server and stores them
   * in the DB if found.
   *
   * @param changeListNumberFrom
   * @param changeListNumberTo
   * @param maxChangeLists
   * @param ignoreChangeListNumber
   * @return new change list ID if there were changes made, or
   *         the same base change list if there were changes
   * @noinspection ControlFlowStatementWithoutBraces
   */
  private int getChangesSince(final String changeListNumberFrom, final String changeListNumberTo,
                              final int maxChangeLists, final String ignoreChangeListNumber)
          throws BuildException, IOException, CommandStoppedException, AgentFailureException {

    final long timeStarted = System.currentTimeMillis();
    final Agent agent = getCheckoutDirectoryAwareAgent();

    // get version if possible and find out of "--limit" option is supported
    final boolean useLimitOption = isAcceptsLimitOption(agent);

    // get actual changes
    final boolean xmlFormat = SystemConfigurationManagerFactory.getManager().useXMLLogFormatForSubversion();
    final List result = new ArrayList(INITIAL_CANGE_LIST_CAPACITY);
    for (final Iterator iter = getDepotPaths().iterator(); iter.hasNext(); ) {
      SVNLogCommand command = null;
      try {
        final RepositoryPath repositoryPath = (RepositoryPath) iter.next();
        final String path = repositoryPath.getPath();
        if (LOG.isDebugEnabled()) LOG.debug("getting changes for: " + path);

        // create SVN checkout command
        command = new SVNLogCommand(agent,
                getPathToSVNExe(),
                getURL(),
                repositoryPath,
                changeListNumberFrom,
                changeListNumberTo,
                useLimitOption,
                maxChangeLists);
        command.setUseXMLFormat(xmlFormat);
        command.setUser(getUser());
        command.setPassword(getPasswordSetting());
        command.setAddTrustServerCert(getAddTrustServerCertSetting());

        // exec
        execute(command);

        // NOTE: simeshev@parabuilci.org - 08/29/2003 - here we check if the output file
        // was created. For now there is only keys known when it happens - when the SVN
        // contains only directories and no files has ben ever submitted. We treat this
        // situation as like there were no changes.
        if (!command.getStdoutFile().exists()) {
          continue;
        }

        // analyze change LOG
        final boolean watchNonRecursive = getSettingValue(SourceControlSetting.SVN_WATCH_NON_RECURSIVE_PATHS,
                SourceControlSetting.OPTION_UNCHECKED).equals(SourceControlSetting.OPTION_CHECKED);
        final SVNChangeLogParser changeLogParser = xmlFormat ? (SVNChangeLogParser) new SVNXmlChangeLogParser(maxChangeListSize()) : (SVNChangeLogParser) new SVNTextChangeLogParser(maxChangeListSize());
        changeLogParser.setMaxChangeLists(maxChangeLists);
        changeLogParser.setIgnoreChangeListNumber(ignoreChangeListNumber);
        if (!repositoryPath.getOptions().isEmpty()) {
          if (!watchNonRecursive) {
            changeLogParser.ignoreSubSubdirectory(repositoryPath.getPath());
          }
        }
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
      } catch (InterruptedException e) {
        throw new CommandStoppedException(e);
      }
    }

    // get latest maxChangeLists changes if necessary
    Collections.sort(result, ChangeList.REVERSE_CHANGE_NUMBER_COMPARATOR);

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

    // De-dupicate (see PARABUILD-1248). Uses the fact that Subversion change lists never null.
    String currentNumber = null;
    for (final Iterator iter = result.iterator(); iter.hasNext(); ) {
      final ChangeList changeList = (ChangeList) iter.next();
      if (currentNumber != null && currentNumber.equals(changeList.getNumber())) {
        // Found duplicate, remove
        iter.remove();
        continue;
      }
      currentNumber = changeList.getNumber();
    }

    if (LOG.isDebugEnabled()) LOG.debug("End getChangesSince: " + result.size());
    // store changes
    return configManager.saveBuildChangeLists(activeBuildID, result);
  }


  /**
   * Helper mehtod.
   *
   * @noinspection IOResourceOpenedButNotSafelyClosed, ControlFlowStatementWithoutBraces
   */
  private boolean isAcceptsLimitOption(final Agent agent) throws IOException, CommandStoppedException, AgentFailureException {
    RemoteCommand versionCommand = null;
    BufferedReader br = null;
    try {
      versionCommand = new SVNVersionCommand(agent, getPathToSVNExe());
      versionCommand.execute();
      br = new BufferedReader(new FileReader(versionCommand.getStdoutFile()));
      final SVNVersion version = new SVNVersionParser().parse(br.readLine());
      return version != null && version.getMajor() >= 1 && version.getMinor() >= 2;
    } finally {
      IoUtils.closeHard(br);
      cleanup(versionCommand);
    }
  }


  /**
   * @return decryped password or null if not defined.
   * @noinspection ControlFlowStatementWithoutBraces
   */
  private String getPasswordSetting() {
    final String encrypedPassword = getSettingValue(SourceControlSetting.SVN_PASSWORD);
    if (encrypedPassword == null) return null;
    return SecurityManager.decryptPassword(encrypedPassword);
  }


  /**
   * Returns the setting 'addTrustServerCert'.
   *
   * @return the setting 'addTrustServerCert'.
   */
  private boolean getAddTrustServerCertSetting() {

    final String stringSettingValue = getSettingValue(SourceControlSetting.SVN_USER, SourceControlSetting.OPTION_UNCHECKED);
    return stringSettingValue.equals(SourceControlSetting.OPTION_CHECKED);
  }


  /**
   * Returns true if externals should be ignored.
   *
   * @return true if externals should be ignored.
   */
  private boolean getIgnoreExternals() {
    final String stringSettingValue = getSettingValue(SourceControlSetting.SVN_IGNORE_EXTERNALS, SourceControlSetting.OPTION_UNCHECKED);
    return stringSettingValue.equals(SourceControlSetting.OPTION_CHECKED);
  }


  /**
   * This method adds verifying that a subversion command failed and trying to fix the failure.
   *
   * @param command
   * @throws IOException
   * @throws CommandStoppedException
   * @noinspection HardcodedLineSeparator
   */
  private static void execute(final SVNCommand command) throws IOException, CommandStoppedException, AgentFailureException {
    try {
      command.execute();
    } catch (IOException e) {
      final String errorString = e.toString().toLowerCase();
      if (errorString.indexOf("Server certificate verification failed: issuer is not trusted".toLowerCase()) >= 0
              || errorString.indexOf("Server certificate verification failed: certificate issued for a different hostname, issuer is not trusted".toLowerCase()) >= 0) {
        command.cleanup();
        // Set interactive to be able to provide "p" as a repsonse
        final boolean interactive = command.isInteractive();
        command.setInteractive(true);
        command.setInputStream(new ByteArrayInputStream("p\r\n\r\n\r\n\r\n\r\n\r\n\r\n\r\n".getBytes()));
        try {
          command.execute();
        } finally {
          // Restore interactive setting
          command.setInteractive(interactive);
        }
      } else {
        throw e;
      }
    }
  }


  /**
   * Returns a resolved path to a Subversion executable.
   *
   * @return the resolved path to a Subversion executable.
   * @throws IOException if the setting was invalid.
   */
  private String getPathToSVNExe() throws IOException {

    try {

      // Get setting as defined in the build configuration
      final String exeSettingValue = getSettingValue(SourceControlSetting.SVN_PATH_TO_EXE);

      // Resolve against the build configuration
      final SettingResolver settingResolver = new SettingResolver(buildID, getAgentHost().getHost());
      settingResolver.resolve(exeSettingValue);

      // Return result
      return StringUtils.putIntoDoubleQuotes(exeSettingValue);

    } catch (ValidationException e) {

      // Wrap into an IO exception
      throw ExceptionUtils.createIOException(e);
    }
  }


  /**
   * Returns project source line repository paths relative to SVN
   * root. Firs item in the list is a project build home.
   *
   * @return List of SVN repository paths composing a project
   * @throws BuildException if a error occurred while getting paths.
   */
  private List getDepotPaths() throws BuildException {
    try {
      final SVNDepotPathParser parser = new SVNDepotPathParser();
      return parser.parseDepotPath(getSettingValue(SourceControlSetting.SVN_DEPOT_PATH));
    } catch (ValidationException e) {
      throw new BuildException(e, getAgentHost());
    }
  }


  /**
   * Returns a setting for Subversion URL.
   *
   * @return a setting for Subversion URL.
   */
  private String getURL() {
    return getSettingValue(SourceControlSetting.SVN_URL);
  }


  /**
   * Returns user setting.
   *
   * @return user setting.
   */
  private String getUser() {
    return getSettingValue(SourceControlSetting.SVN_USER);
  }


  public String toString() {
    return "SVNSourceControl{}";
  }
}
