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
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.CommonConstants;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * VSS SourceControl implementation
 */
final class VSSSourceControl extends AbstractSourceControl implements CommonConstants {

  private static final Log log = LogFactory.getLog(VSSSourceControl.class);


  private Date lastSyncDate = null;


  /**
   * Create an instance of the VSSSourceControl from the build
   * configuration.
   *
   * @param buildConfig
   */
  public VSSSourceControl(final BuildConfig buildConfig) {
    super(buildConfig);
    validateIsVSSConfiguration(buildConfig);
  }


  /**
   * Create an instance of the VSSSourceControl from the list of
   * SourceControlSetting
   *
   * @param VSSSettings List of SourceControlSettings
   * @see SourceControlSetting
   */
  public VSSSourceControl(final BuildConfig config, final List VSSSettings) {
    this(config);
    currentSettings = ConfigurationManager.settingsListToMap(VSSSettings);
  }


  /**
   * Checks out latest state of the source line
   */
  public void checkoutLatest() throws BuildException, CommandStoppedException, AgentFailureException {
    try {
      if (log.isDebugEnabled()) log.debug("begin sync to latest");
      final Agent agent = getCheckoutDirectoryAwareAgent();
      cleanupLocalCopyIfNecessary();

      // traverse list of paths
      VSSCommand command = null;
      for (final Iterator i = getVSSPaths().iterator(); i.hasNext();) {
        try {
          final RepositoryPath projectPath = (RepositoryPath) i.next();
          final String checkoutDirName = makeCheckoutDirName(agent, projectPath);
          final StringBuilder sb = new StringBuilder(200);
          sb.append(" get ").append(STR_DBLQOUTE).append(projectPath.getPath()).append(STR_DBLQOUTE);
          sb.append(' ').append(STR_DBLQOUTE).append("-GL").append(checkoutDirName).append(STR_DBLQOUTE);
          sb.append(" -R"); // retrieve subrojects recuresively
          sb.append(makeReadonlyCheckoutOption()); // files are writeble?
          sb.append(" -GWR"); // overwrite writeable
          agent.mkdirs(checkoutDirName);
          command = new VSSCommand(agent, getExePath(), getDatabasePath(), getUserName(), getPassword(), sb.toString());
          command.execute();
          analyzeErrorLog(command.getStderrFile());
        } finally {
          cleanup(command);
        }
      }
      if (log.isDebugEnabled()) log.debug("end sync to latest");
    } catch (final IOException e) {
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  /**
   * Makes fully qualified project checkout dir name by appending
   * project's path to build's checkout path.
   * <p/>
   * I.e. if build's checkout path is C:\autobuild\etc\build\b6co
   * and project path is $/my/project then the result will be
   * C:\autobuild\etc\build\b6co\my\project.
   */
  private static String makeCheckoutDirName(final Agent agent, final RepositoryPath repositoryPath) throws IOException, AgentFailureException {
    final String pathToAppend = repositoryPath.getPath().substring(1).replace('/', '\\');
//    if (log.isDebugEnabled()) log.debug("pathToAppend: " + pathToAppend);
    return agent.getCheckoutDirName() + (pathToAppend.endsWith("\\") ? pathToAppend.substring(0, pathToAppend.length() - 1) : pathToAppend);
  }


  /**
   * Syncronizes local copy to the given change list ID
   *
   * @param changeListID
   */
  public void syncToChangeList(final int changeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    if (log.isDebugEnabled()) log.debug("begin sync to change list ID: " + changeListID);
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      validateChangeListID(changeListID);
      cleanupLocalCopyIfNecessary();
      initLocalCopyIfNecessary();

      // get latest change date for this change list. VSS doesn't support
      // change IDs, so we sync to the date.
      final ChangeList changeList = configManager.getChangeList(changeListID);
      final Date changeDate = changeList.getCreatedAt();
      this.lastSyncDate = changeList.getCreatedAt();
      if (log.isDebugEnabled()) log.debug("change date: " + changeDate);

      VSSCommand command = null;
      final VSSDateFormatFactory formatFactory = new VSSDateFormatFactory(agent.defaultLocale());
      for (final Iterator i = getVSSPaths().iterator(); i.hasNext();) {
        try {
          final RepositoryPath projectPath = (RepositoryPath) i.next();
          final String checkoutDirName = makeCheckoutDirName(agent, projectPath);
          final StringBuilder sb = new StringBuilder(200);
          sb.append(" get ").append(STR_DBLQOUTE).append(projectPath.getPath()).append(STR_DBLQOUTE);
          sb.append(" -GL");
          sb.append(STR_DBLQOUTE).append(checkoutDirName).append(STR_DBLQOUTE);
          sb.append(" -Vd").append(formatFactory.outputDateTimeFormat().format(changeDate));
          sb.append(" -R"); // retrieve subrojects recuresively
          sb.append(makeReadonlyCheckoutOption()); // files are writeble?
          sb.append(" -GWR"); // overwrite writeable
          agent.mkdirs(checkoutDirName);
          command = new VSSCommand(agent, getExePath(), getDatabasePath(), getUserName(), getPassword(), sb.toString());
          command.execute();
          analyzeErrorLog(command.getStderrFile());
        } finally {
          cleanup(command);
        }
      }
      if (log.isDebugEnabled()) log.debug("end sync to change list");
    } catch (final IOException e) {
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  /**
   * Retuns project build dir relative to checkout path
   */
  public String getRelativeBuildDir() throws BuildException {
    final RepositoryPath repositoryPath = (RepositoryPath) getVSSPaths().get(0);
    return repositoryPath.getPath().substring(2);
  }


  /**
   * Returns last ID of list of changes that were made to
   * controlled source line since the given change list ID.
   * <p/>
   * In order to run successfuly this method needs an already
   * checked out local copy on the client.
   * <p/>
   * Handling zero ID change list: When this method is called
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

    if (log.isDebugEnabled()) log.debug("begin get changes since change list ID: " + startChangeListID);
    try {

      cleanupLocalCopyIfNecessary();
      initLocalCopyIfNecessary();

      Date fromDate = null;
      int maxChangeLists = Integer.MAX_VALUE;

      // check if it is first run (changeListID equals UNSAVED_ID)
      if (startChangeListID == ChangeList.UNSAVED_ID) {
        // REVIEWME: add checking if there are change lists in the database,
        // add corresponding test.
        //
        // set up from date to the begining of century
        final Calendar calendar = Calendar.getInstance();
        calendar.set(1974, 1, 1);
        fromDate = calendar.getTime();
        maxChangeLists = initialNumberOfChangeLists();
      } else {
        // get last build change date
        final ChangeList latest = configManager.getChangeList(startChangeListID);
//        if (log.isDebugEnabled()) log.debug("latest: " + latest);
        // where there changes?
        if (latest == null) return startChangeListID;
        fromDate = new Date(latest.getCreatedAt().getTime() + 60000L); // add next minute
        maxChangeLists = maxNumberOfChangeLists();
      }

      // requests changes from VSS server
      final long timeStarted = System.currentTimeMillis();
      final List result = getChangesFromDate(fromDate, maxChangeLists);
      final long processingTime = System.currentTimeMillis() - timeStarted;

      // return if no changes
      if (result.isEmpty()) return startChangeListID;

      // get value of VSS change window
      final String changeWindowString = getSettingValue(VCSAttribute.VSS_CHANGE_WINDOW);
      final int changeWindow = StringUtils.isValidInteger(changeWindowString) ? Integer.parseInt(changeWindowString) * 1000 : 0;

      // check if we should run checkin window check
      if (changeWindow > 0) {

        // wait for change window if necessary.
        final long timeToWait = Math.max(changeWindow - processingTime, 0);
        if (timeToWait > 0) {
          try {
            Thread.sleep(timeToWait);
          } catch (final InterruptedException e) {
            throw new CommandStoppedException();
          }
        }

        // run again - first item in the list should contain the newest change list.
        // REVIEWME: implement if VSS does not mark check ins the same time
        // Date secondRunFromDate = ((ChangeList)result.get(0)).createdAt();
        // if (log.isDebugEnabled()) log.debug("second run: initial: " + fromDate.toString() + ", second: " + secondRunFromDate.toString());
        // List seconRunResult = getChangesFromDate(secondRunFromDate, maxChangeLists);
        // VSSChangeListWindowMerger merger = new VSSChangeListWindowMerger();
        // merger.mergeInChangesLeft(result, seconRunResult);

        // we resort it
        result.sort(ChangeList.REVERSE_CHANGE_DATE_COMPARATOR);
      }

      // validate that change lists contain not only exclusions
      if (new ExclusionPathFinder().onlyExclusionPathsPresentInChangeLists(result, getSettingValue(VCSAttribute.VCS_EXCLUSION_PATHS))) {
        return startChangeListID;
      }

      // store changes
      if (log.isDebugEnabled()) log.debug("end get changes since");
      return configManager.saveBuildChangeLists(activeBuildID, result);
    } catch (final IOException e) {
      processException(e);
      throw new BuildException("Error while retrieving list of changes: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  /**
   * Returns change lists in reverse date order.
   */
  private List getChangesFromDate(final Date fromDate, final int maxChangeLists) throws BuildException, IOException, CommandStoppedException, AgentFailureException {
    final List resultingChangeLists = new ArrayList(101);
    final Agent agent = getCheckoutDirectoryAwareAgent();
    final VSSDateFormatFactory formatFactory = new VSSDateFormatFactory(agent.defaultLocale());
    VSSCommand command = null;
    for (final Iterator i = getVSSPaths().iterator(); i.hasNext();) {
      try {
        final RepositoryPath projectPath = (RepositoryPath) i.next();

        // preExecute history command
        final Date toDate = new Date(); // VSS requires "end date" for -Vd
        final StringBuilder sb = new StringBuilder(200);
        sb.append(" history \"").append(projectPath.getPath()).append('\"');
        //sb.append(" -Vd").append(formatFactory.outputDateTimeFormatUS().format(toDate)).append("~").append(formatFactory.outputDateTimeFormatUS().format(fromDate));
        sb.append(" -Vd").append(formatFactory.outputDateTimeFormat().format(toDate)).append('~').append(formatFactory.outputDateTimeFormat().format(fromDate));
        sb.append(" -R"); // retrieve subrojects recuresively

        // exec log command
        command = new VSSCommand(agent, getExePath(), getDatabasePath(), getUserName(), getPassword(), sb.toString());
        command.execute();
        //if (log.isDebugEnabled()) log.debug("IoUtils.fileToString(command.getStdoutFile());: " + IoUtils.fileToString(command.getStdoutFile()));

        // analyze checkout log
        analyzeErrorLog(command.getStderrFile());

        // analyze change log
        final VSSChangeLogParser changeLogParser = new VSSChangeLogParser(agent.defaultLocale(), maxChangeLists, maxChangeListSize());
        changeLogParser.setProjectPath(projectPath.getPath());
        changeLogParser.setProjectBranch(getVSSBranch());
        final List changeLists = changeLogParser.parseChangeLog(command.getStdoutFile());
//        if (log.isDebugEnabled()) log.debug("IoUtils.fileToString(command.getStdoutFile()): " + IoUtils.fileToString(command.getStdoutFile()));
        if (log.isDebugEnabled()) log.debug("changelist size: " + changeLists.size());
        if (changeLists.isEmpty()) continue;

        // add this path changes to result
        resultingChangeLists.addAll(changeLists);
      } finally {
        cleanup(command);
      }
    }

    // get latest maxChangeLists changes if necessary
    resultingChangeLists.sort(ChangeList.REVERSE_CHANGE_DATE_COMPARATOR);

    // result
    return resultingChangeLists;
  }


  /**
   * Labels the last synced checkout directory with the given
   * label.
   * <p/>
   * Must throw a BuildException if there was no last sync made
   * or if checkout directory is empty.
   *
   * @param label
   * @see #analyzeErrorLog
   */
  public void label(final String label) throws BuildException, CommandStoppedException, AgentFailureException {
    // NOTE: Here are cases when error handler ignores errors and
    // label is not placed (see analyzeErrorLog method):
    //
    // Case #1: simeshev@parabuilci.org - 09/18/2004 - That's how SS
    // message looks loke when the same label is applied again:
    //      The label "vss_test_build_20040918161035" is used by a previous version of $/test/sourceline/alwaysvalid.
    //      Remove the old label?(Y/N)N
    // TODO: document that we don't remove old labels.
    //
    // Case #2: simeshev@parabuilci.org - 09/18/2004 - That's how SS
    // message looks loke when a path has the same label:
    //      This version of $/test/sourceline/alwaysvalid already has a label.
    //      Overwrite with new label?(Y/N)N
    // TODO: document that we don't overwrite with new labels.

    if (log.isDebugEnabled()) log.debug("begin label: " + label);
    if (this.lastSyncDate == null) {
      // if state is not valid - report and exit
      final org.parabuild.ci.error.Error error = new org.parabuild.ci.error.Error("Build was not labeled");
      error.setBuildID(buildID);
      error.setSendEmail(false);
      error.setDescription("Request to label was issued before source line was checked out.");
      error.setDetails("Please report this issue to technical support.");
      error.setErrorLevel(org.parabuild.ci.error.Error.ERROR_LEVEL_WARNING);
      errorManager.reportSystemError(error);
      return;
    }

    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      if (StringUtils.isBlank(label)) throw new IllegalArgumentException("Label name can not be blank");
      cleanupLocalCopyIfNecessary();
      initLocalCopyIfNecessary();

      final VSSDateFormatFactory formatFactory = new VSSDateFormatFactory(agent.defaultLocale());
      VSSCommand command = null;
      for (final Iterator i = getVSSPaths().iterator(); i.hasNext();) {
        try {
          final RepositoryPath repositoryPath = (RepositoryPath) i.next();
          // execute the checkout command
          final StringBuilder sb = new StringBuilder(200);
          sb.append(" Label").append(' ').append(StringUtils.putIntoDoubleQuotes(repositoryPath.getPath()));
          sb.append(" -C-"); // no note
          sb.append(" -L").append(label);
          sb.append(" -Vd").append(formatFactory.outputDateTimeFormat().format(lastSyncDate));
          if (log.isDebugEnabled()) log.debug("sb = " + sb);
          command = new VSSCommand(agent, getExePath(), getDatabasePath(), getUserName(), getPassword(), sb.toString());
          command.execute();
          if (log.isDebugEnabled())
            log.debug("IoUtils.fileToString(command.getStdoutFile()): " + IoUtils.fileToString(command.getStdoutFile()));
          if (log.isDebugEnabled())
            log.debug("IoUtils.fileToString(command.getStderrFile()): " + IoUtils.fileToString(command.getStderrFile()));
          analyzeErrorLog(command.getStderrFile());
        } finally {
          cleanup(command);
        }
      }

      if (log.isDebugEnabled()) log.debug("end label: " + label);
    } catch (final IOException e) {
      processException(e);
      throw new BuildException("Error while labeling the build: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  /**
   * Returns a map containing VSS user names as keys and e-mails
   * as values. It tries get the map from VSSROOT/users. This
   * method doesn't throw exceptions as it's failure is not
   * critical but it reports errors by calling to ErrorManager.
   *
   * @see ErrorManagerFactory
   * @see ErrorManager
   */
  public Map getUsersMap() {
    return new HashMap(11); // VSS does not support user e-mails, return empty map
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
  public final boolean isBuildDirInitialized() {
    return true; // no additional validation for VSS
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
      final ChangeList changeList = configManager.getChangeList(changeListID);
      if (changeList == null) return "No information";
      // make common sync note
      // REVIEWME: simeshev@parabuilci.org -> The problem here is that at the
      final Agent agent = getCheckoutDirectoryAwareAgent();
      final VSSDateFormatFactory formatFactory = new VSSDateFormatFactory(agent.defaultLocale());
      final StringBuilder result = new StringBuilder("VSS update -P -A -d -D").append(STR_SPACE)
              .append('\"').append(formatFactory.outputDateTimeFormatUS().format(changeList.getCreatedAt())).append("\" ");
      // add branch if necessary
      final String branchName = changeList.getBranch();
      if (!StringUtils.isBlank(branchName)) {
        result.append("-r").append(STR_SPACE).append(branchName);
      }
      return result.toString();
    } catch (final IOException e) {
      return "Infomation is not available: " + StringUtils.toString(e);
    }
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
  public Map getShellVariables() {
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
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.VSS_BRANCH_NAME);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.VSS_DATABASE_PATH);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.VSS_PROJECT_PATH);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.VSS_READONLY_CHECKOUT);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE);
    if (hasToCleanUp) {
      setHasToCleanUp();
    }
    // update current settings map
    replaceCurrentSettings(newSettings);
  }


  /**
   * Returns path to VSS executable
   *
   * @return String path to VSS executable
   */
  private String getExePath() {
    String exePath = getSettingValue(VCSAttribute.VSS_EXE_PATH);
    // REVIEWME: vimeshev - 08/17/2003 - this's a temporary placeholder.
    // Need to decide whether we can let use default path.
    if (StringUtils.isBlank(exePath)) exePath = "VSS";
    return StringUtils.putIntoDoubleQuotes(exePath);
  }


  /**
   * Helper method to return VSS user name from the instance
   * settings list
   *
   * @return String VSS root
   */
  private String getUserName() throws BuildException {
    final String userName = getSettingValue(VCSAttribute.VSS_USER);
    if (userName == null) {
      throw new BuildException("Build configuration does not define VSS user name.", getAgentHost());
    }
    return userName;
  }


  /**
   * Helper method to return VSS user name from the instance
   * settings list
   *
   * @return String VSS root
   */
  private String getDatabasePath() throws BuildException {
    final String databasePath = getSettingValue(VCSAttribute.VSS_DATABASE_PATH);
    if (databasePath == null)
      throw new BuildException("Build configuration does not define path to VSS database.", getAgentHost());
    return databasePath;
  }


  /**
   * Helper method to return VSS root from the instance settings
   * list
   *
   * @return String VSS root
   */
  private String getVSSBranch() {
    return getSettingValue(VCSAttribute.VSS_BRANCH_NAME);
  }


  /**
   * Returs project source line repository paths relative to VSS
   * root. Firs item in the list is a project build home.
   *
   * @return List of VSS repository paths composing a project
   * @throws BuildException
   */
  private List getVSSPaths() throws BuildException {
    // get lines
    final List lines = StringUtils.multilineStringToList(getSettingValue(VCSAttribute.VSS_PROJECT_PATH));
    if (lines.size() <= 0) {
      throw new BuildException("Build configuration does not contain non-empty repository path", getAgentHost());
    }
    // validate and create result
    final List paths = new ArrayList(lines.size());
    for (final Iterator i = lines.iterator(); i.hasNext();) {
      final String line = validateAndNormalizeSingleVSSPath((String) i.next());
      paths.add(new RepositoryPath(line));
    }
    return paths;
  }


  /**
   * Removes traling slashes from the source line path
   */
  private String validateAndNormalizeSingleVSSPath(final String VSSPath) throws BuildException {
    // validate
    if (StringUtils.isBlank(VSSPath)) {
      throw new BuildException("Build configuration can not contain empty VSS repository path", getAgentHost());
    }
    // normalize
    String normalizedVSSPath = VSSPath.replace('\\', '/');
    while (normalizedVSSPath.charAt(0) == '/') {
      normalizedVSSPath = normalizedVSSPath.substring(1);
    }
    return normalizedVSSPath;
  }


  /**
   * @return VSS password, or null if not defined
   */
  private String getPassword() {
    final String encrypedPassword = getSettingValue(VCSAttribute.VSS_PASSWORD);
    if (encrypedPassword == null) return null;
    return SecurityManager.decryptPassword(encrypedPassword);
  }


  /**
   * Analyzes checkout log for known errors
   *
   * @param stderr File defining checkout log
   * @throws BuildException tha
   *                        contains error descripting
   */
  private void analyzeErrorLog(final File stderr) throws BuildException {
//    if (log.isDebugEnabled()) log.debug("analyze error log");
    if (!stderr.exists() || stderr.length() == 0) return;
    boolean unknownErrors = false;
    // first, try to fins errors we know
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new FileReader(stderr));
      String line = reader.readLine();
      while (line != null) {
        if (bothPresent(line, "The label ", "is used by a previous version of")) { // NOPMD
          // ignore, see label() method
        } else if (bothPresent(line, "This version of ", "already has a label")) { // NOPMD
          // ignore, see label() method
        } else if (line.contains("No VSS database (srcsafe.ini) found")) { // NOPMD
          throw new BuildException("Path to VSS database \"" + getDatabasePath() + "\" is invalid", getAgentHost());
        } else if (bothPresent(line, "User \"", "\" not found")) {
          throw new BuildException("VSS user \"" + getUserName() + "\" used to configure build is invalid.", getAgentHost());
        } else if (line.contains("Invalid password")) {
          throw new BuildException("Password for VSS user \"" + getUserName() + "\" used to configure build is invalid.", getAgentHost());
        } else if (line.endsWith("(Y/N)N")) {  // NOPMD EmptyIfStmt
          // ignore
        } else { // NOPMD EmptyIfStmt
          unknownErrors = true; // rise flag
        }
        line = reader.readLine();
      }
    } catch (final IOException e) {
      processException(e);
    } finally {
      IoUtils.closeHard(reader);
    }
    // we are here, that means we could not find known errors
    if (unknownErrors)
      throw new BuildException("Error accessing Visual Source Safe: " + IoUtils.fileToString(stderr), getAgentHost());
  }


  public static boolean bothPresent(final String toTest, final String testString1, final String testString2) {
    return toTest.contains(testString1) && toTest.contains(testString2);
  }


  /**
   * Tries to figure out what happened. If can not, throws
   * BuildException saying that there what a error.
   *
   * @param e Exception to process
   * @throws BuildException with
   *                        processed information.
   */
  private void processException(final Exception e) throws BuildException {
    final String exceptionString = e.toString();
    if (exceptionString.contains("java.io.IOException: CreateProcess:")) {
      if (getExePath() != null) {
        throw new BuildException("Error while checking out: VSS executable \"" + getExePath() + "\" not found.", getAgentHost());
      }
    }
    throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, getAgentHost());
  }


  /**
   * Validates that correct VSS build configuration was passesed
   */
  private static void validateIsVSSConfiguration(final BuildConfig buildConfig) {
    if (buildConfig.getSourceControl() != VCSAttribute.SCM_REFERENCE && buildConfig.getSourceControl() != VCSAttribute.SCM_VSS) {
      throw new IllegalArgumentException("Non-VSS build configuration");
    }
  }


  /**
   * @return true if read-only checkout is requested.
   * @see SourceControlSetting#VSS_READONLY_CHECKOUT
   */
  private boolean isReadOnlyCheckout() {
    return getSettingValue(VCSAttribute.VSS_READONLY_CHECKOUT, SourceControlSetting.OPTION_UNCHECKED).equals(SourceControlSetting.OPTION_CHECKED);
  }


  /**
   * @return a command line option to signal if checkout files
   *         should be made read-only (-W-) or should be made writeable (-W).
   * @see #isReadOnlyCheckout()
   * @see SourceControlSetting#VSS_READONLY_CHECKOUT
   */
  private String makeReadonlyCheckoutOption() {
    return isReadOnlyCheckout() ? " -W-" : " -W";
  }
}
