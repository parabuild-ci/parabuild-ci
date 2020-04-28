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
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.services.RemoteFileDescriptor;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.util.ThreadUtils;
import org.parabuild.ci.versioncontrol.clearcase.ClearCaseStartDate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * ClearCase version control.
 */
final class ClearCaseSourceControl extends AbstractSourceControl {

  private static final Log log = LogFactory.getLog(ClearCaseSourceControl.class);
  private static final String DEFAULT_VIEW_NAME_TEMPLATE = "${cc.user}_parabuild_${build.id}";

  private final SimpleDateFormat historyDateFormat = ClearCaseConstants.getHistoryDateFormat();
  private Date lastSyncDate;


  /**
   * Constructor.
   *
   * @param buildConfig for with the CC is created.
   */
  ClearCaseSourceControl(final BuildConfig buildConfig) {
    super(buildConfig);
    validateIsClearCaseConfiguration(buildConfig);
  }


  ClearCaseSourceControl(final BuildConfig config, final List settings) {
    this(config);
    currentSettings = ConfigurationManager.settingsListToMap(settings);
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
    boolean result = true;

    // basic check
    final Agent agent = getCheckoutDirectoryAwareAgent();
    if (!agent.fileRelativeToCheckoutDirExists(getRelativeBuildDir())
            || !viewDatExists(agent)) {
      result = false;
    }

    // check if there are any dirs there
    result &= !getLoadedDirectories(agent).isEmpty();

    return result;
  }


  private static boolean viewDatExists(final Agent agent) throws IOException, AgentFailureException {
    return agent.fileRelativeToCheckoutDirExists("view.dat")
            || agent.fileRelativeToCheckoutDirExists(".view.dat");
  }


  /**
   * Helper method.
   *
   * @param agent
   * @return
   * @throws IOException
   */
  private List getLoadedDirectories(final Agent agent) throws IOException, AgentFailureException {
    // parse "load" rules to determine directories
    final List result = new ArrayList(11);
    final String checkoutDirName = agent.getCheckoutDirName();
    final String configSpec = getSettingValue(VersionControlSystem.CLEARCASE_VIEW_CONFIG_SPEC);
    final List configSpecLines = StringUtils.multilineStringToList(configSpec);
    for (int i = 0, n = configSpecLines.size(); i < n; i++) {
      final String configSpecLine = (String) configSpecLines.get(i);
      if (configSpecLine.trim().toLowerCase().startsWith("load")) { // this a "load" line
        final StringTokenizer st = new StringTokenizer(configSpecLine, " ", false);
        if (st.countTokens() > 1) {
          st.nextToken(); // "load" part
          final String specPath = st.nextToken();
          final String fullPath = checkoutDirName + specPath;
          if (log.isDebugEnabled()) {
            log.debug("fullPath: " + fullPath);
          }
          if (agent.absolutePathExists(fullPath)) {
            final RemoteFileDescriptor fileDescriptor = agent.getFileDescriptor(fullPath);
            if (fileDescriptor.isDirectory()) {
              final String canonicalPath = fileDescriptor.getCanonicalPath();
              if (log.isDebugEnabled()) {
                log.debug("canonicalPath: " + canonicalPath);
              }
              result.add(canonicalPath);
            }
          }
        }
      }
    }
    // check if we found anything
    if (result.isEmpty()) {
      throw new IOException("Snapshot view config spec doesn't appear to have any load rules:\n" + configSpec);
    }
    return result;
  }


  /**
   * Checks out latest state of the source line
   */
  public void checkoutLatest() throws BuildException, CommandStoppedException, AgentFailureException {
    if (log.isDebugEnabled()) {
      log.debug("begin checkoutLatest");
    }
    ClearCaseCommand command = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      // preexecute
      cleanupLocalCopyIfNecessary();
      createOrUpdateView();
      // execute
      command = new ClearCaseUpdateCommand(agent, exePath(), ignoreLines());
      command.execute();
      if (log.isDebugEnabled()) {
        log.debug("end checkoutLatest");
      }
    } catch (final IOException e) {
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, getAgentHost());
    } finally {
      cleanup(command); // cleanup this cycle
    }
  }


  /**
   * Deletes local compy files.
   *
   * @throws IOException
   */
  public boolean cleanupLocalCopy() throws IOException {
    ClearCaseCommand command = null;
    try {
      // delete view first
      final Agent agent = getCheckoutDirectoryAwareAgent();
      final boolean viewDatExists = viewDatExists(agent);
      final String viewTag = makeCurrentViewName(agent);
      if (viewDatExists) {
        // remove view
        command = new ClearCaseRmviewCommand(agent, exePath(), ignoreLines());
        command.execute();
        command.cleanup();
      }
      // remove tag
      command = new ClearCaseRmtagCommand(agent, exePath(), viewTag, ignoreLines());
      command.execute();
      command.cleanup();
      // remove dir
      return super.cleanupLocalCopy();
    } catch (final Exception e) {
      throw IoUtils.createIOException(e);
    } finally {
      cleanup(command);
    }
  }


  /**
   * Creates or updates snapshot view configuration if
   * necessary.
   */
  private void createOrUpdateView() throws CommandStoppedException, BuildException, AgentFailureException {
    /*

    *** rmview help:
    Usage: rmview [-force] {snapshot-view-pname | snapshot-view-storage-pname}
    rmview [-force] [-vob pname-in-vob | -avobs | -all] -uuid view-uuid

    *** Removing existing view with FAR positioned inside a dir:
    cleartool rmview  D:\projectory\bt\temp\test_run_manager\etc\build\b19co\a\\u\t\o
    cleartool: Error: D:\projectory\bt\temp\test_run_manager\etc\build\b19co\a\\u\t\o: Permission denied
    cleartool: Error: Unable to remove the snapshot view directory.

    *** Repeated immediately after that:
    cleartool rmview  D:\projectory\bt\temp\test_run_manager\etc\build\b19co\a\\u\t\o
    cleartool: Error: Unable to open file "D:\projectory\bt\temp\test_run_manager\etc\build\b19co\a\\u\t\o\.view": No such file or directory.
    cleartool: Error: D:\projectory\bt\temp\test_run_manager\etc\build\b19co\a\\u\t\o isn't a view: No such file or directory
    cleartool: Error: Unable to remove view "D:\projectory\bt\temp\test_run_manager\etc\build\b19co\a\\u\t\o".
    */

    ClearCaseCommand command = null;
    try {
      if (log.isDebugEnabled()) {
        log.debug("begin createOrUpdateView");
      }
      final Agent agent = getCheckoutDirectoryAwareAgent();

      // get view name
      final String viewTag = makeCurrentViewName(agent);

      // check if view exists
      final boolean viewDatExists = viewDatExists(agent);
      if (!viewDatExists) {
        // there still can be a tag lef, blindly remove
        command = new ClearCaseRmtagCommand(agent, exePath(), viewTag, ignoreLines());
        command.execute();
        command.cleanup();
      }
      // ... handling template change
      //noinspection UnnecessaryLocalVariable
      final boolean viewExists = viewDatExists;

      // update
      if (!viewExists) { // create view
        final String textModeCode = getSettingValue(VersionControlSystem.CLEARCASE_TEXT_MODE, Integer.toString(VersionControlSystem.CLEARCASE_TEXT_MODE_NOT_SET));
        final byte storageLocationCode = getSettingValue(VersionControlSystem.CLEARCASE_VIEW_STORAGE_LOCATION_CODE, VersionControlSystem.CLEARCASE_STORAGE_CODE_AUTOMATIC);
        final String storageLocation = getSettingValue(VersionControlSystem.CLEARCASE_VIEW_STORAGE_LOCATION);
        command = new ClearCaseMkviewCommand(agent, exePath(), textModeCode, storageLocationCode, storageLocation, viewTag, ignoreLines());
        command.execute();
        command.cleanup();
      }

      // set/update spec
      final String viewSpec = getSettingValue(VersionControlSystem.CLEARCASE_VIEW_CONFIG_SPEC);
      command = new ClearCaseSetcsCommand(agent, exePath(), viewSpec, ignoreLines());
      command.execute();
      command.cleanup();

//      // update view
//      command = new ClearCaseUpdateCommand(agent, exePath());
//      command.execute();
      if (log.isDebugEnabled()) {
        log.debug("end createOrUpdateView");
      }
    } catch (final IOException e) {
      throw new BuildException("Error while creating or updating view: " + StringUtils.toString(e), e, getAgentHost());
    } finally {
      cleanup(command); // cleanup this cycle
    }
  }


  private String makeCurrentViewName(final Agent agent) throws IOException, BuildException, AgentFailureException {
    final String userName = agent.getSystemProperty("user.name");
    final String template = getSettingValue(VersionControlSystem.CLEARCASE_VIEW_NAME_TEMPLATE, DEFAULT_VIEW_NAME_TEMPLATE);
    final ClearCaseViewNameGenerator viewNameGenerator = new ClearCaseViewNameGenerator(userName, buildID, template);
    return viewNameGenerator.generate(); // NOPMD
  }


  /**
   * Syncs to a given change list number
   */
  public void syncToChangeList(final int changeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    if (log.isDebugEnabled()) {
      log.debug("begin syncToChangeList changeListID: " + changeListID);
    }
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      validateChangeListID(changeListID);
      final ChangeList changeList = configManager.getChangeList(changeListID);
//      cleanupLocalCopyIfNecessary();
      cleanupLocalCopyIfNecessary();
      createOrUpdateView();

      // 1. ClearCase doesn't support change IDs, so we sync to the date.
      // 2. ClearCase update doesn't support dates, so we have to alter spec.

      // prepare spec with "time" rule
      StringWriter sw = null;
      PrintWriter pw = null;
      BufferedReader br = null;
      try {
        sw = new StringWriter(500);
        pw = new PrintWriter(sw);
        pw.println("time " + historyDateFormat.format(changeList.getCreatedAt()));
        br = new BufferedReader(new StringReader(getSettingValue(VersionControlSystem.CLEARCASE_VIEW_CONFIG_SPEC)));
        String line = br.readLine();
        while (line != null) {
          pw.println(line);
          line = br.readLine();
        }
      } finally {
        IoUtils.closeHard(pw);
        IoUtils.closeHard(br);
      }
      final String updateSpec = sw.toString();

      // execute setcs that should in turn execute update
      ClearCaseCommand command = null;
      try {
        command = new ClearCaseSetcsCommand(agent, exePath(), updateSpec, ignoreLines());
        command.execute();
      } finally {
        cleanup(command); // cleanup this cycle
      }
      this.lastSyncDate = changeList.getCreatedAt();
      if (log.isDebugEnabled()) {
        log.debug("end syncToChangeList");
      }
    } catch (final IOException e) {
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  /**
   * Returns relative project path
   */
  public String getRelativeBuildDir() {
    return getSettingValue(VersionControlSystem.CLEARCASE_RELATIVE_BUILD_DIR);
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
    final long started = System.currentTimeMillis();
    if (log.isDebugEnabled()) {
      log.debug("begin getChangesSince startChangeListID: " + startChangeListID);
    }
    try {

      cleanupLocalCopyIfNecessary();
      initLocalCopyIfNecessary();

      final Calendar fromDate;
      final int maxChangeLists;

      // check if it is first run (startChangeListID equals UNSAVED_ID)
      if (startChangeListID == ChangeList.UNSAVED_ID) {
        // REVIEWME: add checking if there are change lists in the database,
        // add corresponding test.
        //
        // set up from date to the begining of century
        final String defaultStartDate = new ClearCaseStartDate().getValue();
        final String stringStartDate = getSettingValue(VersionControlSystem.CLEARCASE_START_DATE, defaultStartDate);
        final Date startDate = ClearCaseStartDate.parse(stringStartDate);
        fromDate = Calendar.getInstance();
        fromDate.setTime(startDate);
        maxChangeLists = initialNumberOfChangeLists();
      } else {
        // roll the last build change date one second forward
        final ChangeList startChangeList = configManager.getChangeList(startChangeListID);
        if (startChangeList == null) {
          return startChangeListID;
        }
        fromDate = Calendar.getInstance();
        fromDate.setTime(startChangeList.getCreatedAt());
        fromDate.add(Calendar.SECOND, 1);
        maxChangeLists = maxNumberOfChangeLists();
      }

      // requests changes from server
      final long timeStarted = System.currentTimeMillis();
      final List result = getChangesFromDate(fromDate.getTime(), maxChangeLists);
//      if (log.isDebugEnabled()) log.debug("result: " + result);
      final long processingTime = System.currentTimeMillis() - timeStarted;

      // return if no changes
      if (result.isEmpty()) {
        return startChangeListID;
      }

      // run checkin window check if required
      final int changeWindow = getSettingValue(VersionControlSystem.CLEARCASE_CHANGE_WINDOW, 0) * 1000;
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
        final Calendar secondRunFromDate = Calendar.getInstance();
        secondRunFromDate.setTime(((ChangeList) result.get(0)).getCreatedAt());
        secondRunFromDate.add(Calendar.SECOND, 1);
        if (log.isDebugEnabled()) {
          log.debug("second run: initial: " + fromDate.toString() + ", second: " + secondRunFromDate.toString());
        }
        final List seconRunResult = getChangesFromDate(secondRunFromDate.getTime(), maxChangeLists);
        final ChangeListWindowMerger merger = new ChangeListWindowMerger();
        merger.mergeInChangesLeft(result, seconRunResult);
        // we resort it
        result.sort(ChangeList.REVERSE_CHANGE_DATE_COMPARATOR);
      }

      // validate that change lists contain not only exclusions
      if (new ExclusionPathFinder().onlyExclusionPathsPresentInChangeLists(result, getSettingValue(VersionControlSystem.VCS_EXCLUSION_PATHS))) {
        return startChangeListID;
      }

      // store changes
      if (log.isDebugEnabled()) {
        log.debug("end getChangesSince");
      }
      result.sort(ChangeList.REVERSE_CHANGE_DATE_COMPARATOR);
      return configManager.saveBuildChangeLists(activeBuildID, result);
    } catch (final IOException e) {
      processException(e);
      throw new BuildException("Error while retrieving list of changes: " + StringUtils.toString(e), e, getAgentHost());
    } catch (final ParseException e) {
      throw new BuildException("Error while retrieving list of changes: " + StringUtils.toString(e), e, getAgentHost());
    } finally {
      if (log.isDebugEnabled()) {
        log.debug("get changes since took : " + (System.currentTimeMillis() - started) + " ms");
      }
    }
  }


  private List getChangesFromDate(final Date startDate, final int maxChangeLists) throws IOException, CommandStoppedException, AgentFailureException {
    final Agent agent = getCheckoutDirectoryAwareAgent();
    final List result = new ArrayList(101);
    ClearCaseCommand command = null;
    try {
      if (log.isDebugEnabled()) {
        log.debug("getting changes for: " + getSettingValue(VersionControlSystem.CLEARCASE_VIEW_CONFIG_SPEC));
      }

      // create a branchSetting list
      final List branchList = new ArrayList(11);
      final String branchSetting = getSettingValue(VersionControlSystem.CLEARCASE_BRANCH);
      if (StringUtils.isBlank(branchSetting)) {
        branchList.add(branchSetting);
      } else {
        final StringTokenizer st = new StringTokenizer(branchSetting, " ,;", false);
        while (st.hasMoreTokens()) {
          branchList.add(st.nextToken());
        }
      }

      // go ver the list of dir und
      final List loadedDirectoryList = getLoadedDirectories(agent);
      for (int i = 0, n = loadedDirectoryList.size(); i < n; i++) {
        // NOTE: simeshev@parabuilci.org - 09/15/2005 - under view home we are interested
        // only in dirs so that lshistory stays in the context of the view
        final String absolutePath = (String) loadedDirectoryList.get(i);
//        if (log.isDebugEnabled()) log.debug("absolutePath: " + absolutePath);

        // iterate over the branch list
        for (int j = 0, m = branchList.size(); j < m; j++) {
          final String brahch = (String) branchList.get(j);
          // create and execute ClearCase history command
          command = new ClearCaseLshistoryCommand(agent, exePath(), absolutePath, brahch, startDate, ignoreLines());
          command.execute();

//        if (log.isDebugEnabled()) log.debug("IoUtils.fileToString(command.getStdoutFile()): " + IoUtils.fileToString(command.getStdoutFile()));
          // parse change log and add to the change result
          if (command.getStdoutFile().length() == 0) {
            continue;
          }
          final ClearCaseChangeLogParser changeLogParser = new ClearCaseChangeLogParser(maxChangeLists, brahch, maxChangeListSize());
          final List changeLists = changeLogParser.parseChangeLog(command.getStdoutFile());
          if (log.isDebugEnabled()) {
            log.debug("changelist size: " + changeLists.size());
          }
          result.addAll(changeLists);
          cleanup(command);
        }
      }
    } finally {
      cleanup(command); // cleanup this cycle
    }
    return result;
  }


  /**
   * Labels the last synced checkout directory with the given
   * labelType.
   * <p/>
   * Must throw a BuildException if there was no last sync made
   * or if checkout directory is empty.
   *
   * @param labelType
   */
  public void label(final String labelType) throws BuildException, CommandStoppedException, AgentFailureException {
    if (log.isDebugEnabled()) {
      log.debug("begin labelType: " + labelType);
    }
    if (lastSyncDate == null) {
      throw new IllegalStateException("Attempted to labelType without syncing first");
    }
    if (StringUtils.isBlank(labelType)) {
      throw new IllegalArgumentException("Label is blank");
    }
    ClearCaseCommand command = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      final List directoriesUnderCheckoutDir = getLoadedDirectories(agent);

      // create labelType type first

      // labelType dirs
      for (int i = 0, n = directoriesUnderCheckoutDir.size(); i < n; i++) {
        try {
          final String absolutePath = (String) directoriesUnderCheckoutDir.get(i);
          command = new ClearCaseMklbtypeCommand(agent, exePath(), labelType, absolutePath, ignoreLines());
          command.execute();
          cleanup(command);
          command = new ClearCaseMklabelCommand(agent, exePath(), labelType, absolutePath, ignoreLines());
          command.execute();
        } finally {
          cleanup(command);
        }
      }
    } catch (final IOException e) {
      processException(e);
      throw new BuildException("Error while labeling the build: " + StringUtils.toString(e), e, getAgentHost());
    } finally {
      cleanup(command);
    }
  }


  /**
   * @return lines to ignore
   */
  private String ignoreLines() {
    return getSettingValue(VersionControlSystem.CLEARCASE_IGNORE_LINES);
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
    return Collections.emptyMap(); // no user details are provided by ClearCase
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
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.CLEARCASE_BRANCH);
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.CLEARCASE_VIEW_CONFIG_SPEC);
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.CLEARCASE_VIEW_NAME_TEMPLATE);
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.CLEARCASE_TEXT_MODE);
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.CLEARCASE_VIEW_STORAGE_LOCATION);
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.CLEARCASE_IGNORE_LINES);
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
   *         <p/>
   *         This is a default implementation that returns an
   *         empty map.
   * @see BuildScriptGenerator#addVariables(Map)
   */
  public Map getShellVariables() {
    return Collections.emptyMap();
  }


  /**
   * Returns text description of a command to be used by a
   * customer to sync to a given changelist. This is a default
   * implementation.
   *
   * @param changeListID
   */
  public String getSyncCommandNote(final int changeListID) throws AgentFailureException {
    final ChangeList changeList = configManager.getChangeList(changeListID);
    if (changeList == null) {
      return super.getSyncCommandNote(changeListID);
    }
    return "Use \"time "
            + historyDateFormat.format(changeList.getCreatedAt())
            + "\" spec config rule.";
  }


  /**
   * Validates that correct ClearCase build configuration was
   * passesed
   */
  private static void validateIsClearCaseConfiguration(final BuildConfig buildConfig) {
    if (buildConfig.getSourceControl() != VersionControlSystem.SCM_REFERENCE
            && buildConfig.getSourceControl() != VersionControlSystem.SCM_CLEARCASE) {
      throw new IllegalArgumentException("Non-ClearCase build configuration");
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
    throw new BuildException("Error while accessing ClearCase: " + StringUtils.toString(e), e, getAgentHost());
  }


  private String exePath() {
    return StringUtils.putIntoDoubleQuotes(getSettingValue(VersionControlSystem.CLEARCASE_PATH_TO_EXE));
  }


  public String toString() {
    return "ClearCaseSourceControl{" +
            "historyDateFormat=" + historyDateFormat +
            ", lastSyncDate=" + lastSyncDate +
            '}';
  }
}
