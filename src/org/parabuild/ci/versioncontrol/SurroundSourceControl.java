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
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.CommonConstants;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.util.ValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Surround version control implementation
 */
final class SurroundSourceControl extends AbstractSourceControl implements CommonConstants {

  private static final Log log = LogFactory.getLog(SurroundSourceControl.class);

  private Date lastSyncDate = null;


  public SurroundSourceControl(final BuildConfig buildConfig) {
    super(buildConfig);
  }


  public SurroundSourceControl(final BuildConfig config, final List settings) {
    this(config);
    currentSettings = ConfigurationManager.settingsListToMap(settings);
  }


  /**
   * Checks out latest state of the source line
   */
  public void checkoutLatest() throws BuildException, CommandStoppedException, AgentFailureException {
    if (log.isDebugEnabled()) log.debug("begin checkoutLatest");
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();

      // traverse list of paths
      SurroundCommand command = null;
      for (final Iterator i = getDepotPaths().iterator(); i.hasNext();) {
        try {
          // get single path
          final RepositoryPath repositoryPath = (RepositoryPath) i.next();

          // create Surround checkout command
          command = new SurroungGetCommand(agent,
                  getPathToExe(),
                  getSettingValue(VersionControlSystem.SURROUND_USER),
                  getPasswordSetting(),
                  getSettingValue(VersionControlSystem.SURROUND_HOST),
                  getSettingValue(VersionControlSystem.SURROUND_PORT, 5400),
                  branch(),
                  repositoryPath.getPath(),
                  null);

          // exec
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
   * @return configured branch
   */
  private String branch() {
    // we remove double quotes to address #997
    return StringUtils.removeDoubleQuotes(getSettingValue(VersionControlSystem.SURROUND_BRANCH));
  }


  private String getPathToExe() {
    return StringUtils.putIntoDoubleQuotes(getSettingValue(VersionControlSystem.SURROUND_PATH_TO_EXE));
  }


  /**
   * Syncs to a given change list number
   */
  public void syncToChangeList(final int changeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    if (log.isDebugEnabled()) log.debug("begin syncToChangeList changeListID: " + changeListID);
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();

//      validateChangeListID(changeListID);
//      cleanupLocalCopyIfNecessary();
//      initLocalCopyIfNecessary();

      // NOTE: simeshev@parabuilci.org - 08/17/2005 - we clean up every time
      // because as of now it is not clear how to get Surround removing
      // files that should not be there when doing backward updates.
      cleanupLocalCopy();

      // get latest change date for this change list. Surround doesn't support
      // change IDs, so we sync to the date.
      final ChangeList changeList = configManager.getChangeList(changeListID);

      for (final Iterator i = getDepotPaths().iterator(); i.hasNext();) {
        SurroundCommand command = null;
        try {
          // get single path
          final RepositoryPath repositoryPath = (RepositoryPath) i.next();

          // create Surround checkout command
          command = new SurroungGetCommand(agent,
                  getPathToExe(),
                  getSettingValue(VersionControlSystem.SURROUND_USER),
                  getPasswordSetting(),
                  getSettingValue(VersionControlSystem.SURROUND_HOST),
                  getSettingValue(VersionControlSystem.SURROUND_PORT, 5400),
                  branch(),
                  repositoryPath.getPath(),
                  changeList.getCreatedAt());

          // exec
          command.execute();
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
      this.lastSyncDate = changeList.getCreatedAt();
      if (log.isDebugEnabled()) log.debug("end syncToChangeList");
    } catch (final IOException e) {
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, getAgentHost());
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
   *
   */
  public int getChangesSince(final int startChangeListID) throws BuildException, CommandStoppedException, AgentFailureException {

    if (log.isDebugEnabled()) log.debug("begin getChangesSince changeListID: " + startChangeListID);
    try {

      int maxChangeLists = Integer.MAX_VALUE;
      Date startWith = null;

      // check if it is first run (changeListID equals UNSAVED_ID)
      if (startChangeListID == ChangeList.UNSAVED_ID) {
        maxChangeLists = initialNumberOfChangeLists();
        startWith = null;
      } else {
        // get last build change date
        final ChangeList latest = configManager.getChangeList(startChangeListID);
        if (log.isDebugEnabled()) log.debug("latest: " + latest);
        // where there changes?
        if (latest == null) return startChangeListID;
        maxChangeLists = maxNumberOfChangeLists();
        // add time to the latest to pass it
        final Date latestCreatedAt = latest.getCreatedAt();
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime(latestCreatedAt);
        // here we relay on the fact that "latest" is always ends with 59 seconds
        calendar.add(Calendar.SECOND, 1);
        startWith = calendar.getTime();
        if (log.isDebugEnabled()) log.debug("startWith: " + startWith);
      }

      // requests changes from Surround server, starting with
      // given date till server's "now"
      final long timeStarted = System.currentTimeMillis();
      final List result = getChangesFromDate(startWith, null, maxChangeLists);

      // process results
      if (!result.isEmpty()) {

        // get latest maxChangeLists changes if necessary
        result.sort(ChangeList.REVERSE_CHANGE_DATE_COMPARATOR);

        // map result and get max date
        Date maxDate = ((ChangeList) result.get(0)).getCreatedAt();
        final Map resultMap = new HashMap(111);
        for (final Iterator i = result.iterator(); i.hasNext();) {
          final ChangeList changeList = (ChangeList) i.next();
          resultMap.put(new SurroundChangeListKey(changeList), changeList);
          final Date date = changeList.getCreatedAt();
          if (date.compareTo(maxDate) > 0) maxDate = date;
        }

        // create second run dates
        final Date newStartDate = maxDate;
        final Calendar newEndDate = Calendar.getInstance();
        newEndDate.setTime(maxDate); // expect that seconds are null
        newEndDate.set(Calendar.SECOND, 59); // set under one second limit

        // run agian 59 seconds range
        final List lastMinuteInclusiveResult = getChangesFromDate(newStartDate, newEndDate.getTime(), maxChangeLists);
        for (final Iterator j = lastMinuteInclusiveResult.iterator(); j.hasNext();) {
          final ChangeList lastMinuteChangeList = (ChangeList) j.next();
          final SurroundChangeListKey key = new SurroundChangeListKey(lastMinuteChangeList);
          final ChangeList foundChangeList = (ChangeList) resultMap.get(key);
          final Calendar calendar = Calendar.getInstance();
          calendar.setTime(lastMinuteChangeList.getCreatedAt());
          calendar.set(Calendar.SECOND, 59);
          final Date adjustedCreatedAt = calendar.getTime();
          if (foundChangeList == null) {
            // new one
            // NOTE: vimeshev - set date seconds to 59 instead of 00
            // that is returned from parser so that when we sync we pick
            // all checked in in 00-69 seconds range.
            lastMinuteChangeList.setCreatedAt(adjustedCreatedAt);
            result.add(lastMinuteChangeList);
          } else {
            foundChangeList.setCreatedAt(adjustedCreatedAt);
          }
        }
      }

      // result
      final long processingTime = System.currentTimeMillis() - timeStarted;
      if (log.isDebugEnabled()) log.debug("time to process change lists: " + processingTime);

      // return if no changes
      if (log.isDebugEnabled()) log.debug("result.size() = " + result.size());
      if (result.isEmpty()) return startChangeListID;

      // validate that change lists contain not only exclusions
      if (new ExclusionPathFinder().onlyExclusionPathsPresentInChangeLists(result, getSettingValue(VersionControlSystem.VCS_EXCLUSION_PATHS))) {
        return startChangeListID;
      }

      // store changes
      if (log.isDebugEnabled()) log.debug("end getChangesSince");
//      if (log.isDebugEnabled()) log.debug("result: " + result);
      return configManager.saveBuildChangeLists(activeBuildID, result);
    } catch (final IOException e) {
      processException(e);
      throw new BuildException("Error while retrieving list of changes: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  private List getChangesFromDate(final Date startDate, final Date endDate, final int maxChangeLists) throws BuildException, IOException, CommandStoppedException, AgentFailureException {
    final Agent agent = getCheckoutDirectoryAwareAgent();
    final Locale builderLocale = agent.defaultLocale();
    final List result = new ArrayList(101);
    for (final Iterator i = getDepotPaths().iterator(); i.hasNext();) {
      SurroungHistoryReportCommand command = null;
      try {
        final RepositoryPath repositoryPath = (RepositoryPath) i.next();
        if (log.isDebugEnabled()) log.debug("getting changes for: " + repositoryPath.getPath());

        // create and execute Surround history command
        command = new SurroungHistoryReportCommand(agent,
                getPathToExe(),
                getSettingValue(VersionControlSystem.SURROUND_USER),
                getPasswordSetting(),
                getSettingValue(VersionControlSystem.SURROUND_HOST),
                getSettingValue(VersionControlSystem.SURROUND_PORT, 5400),
                branch(),
                repositoryPath.getPath(),
                startDate, endDate);
        command.execute();

        // parse change log and add to the change result
        final SurroundChangeLogParser changeLogParser = new SurroundChangeLogParser(builderLocale, maxChangeLists, maxChangeListSize());
        changeLogParser.setBranch(branch());
        final List changeLists = changeLogParser.parseChangeLog(command.getStdoutFile());
        if (log.isDebugEnabled()) log.debug("changelist size: " + changeLists.size());
        result.addAll(changeLists);
      } finally {
        cleanup(command); // cleanup this cycle
      }
    }
    return result;
  }


  /**
   * Tries to figure out what happened. If can not, throws
   * BuildException saying that there what a error.
   *
   * @param e Exception to process
   * @throws BuildException
   *          with
   *          processed information
   */
  private void processException(final Exception e) throws BuildException {
    final String exceptionString = e.toString();
    if (exceptionString.contains("java.io.IOException: CreateProcess:")) {
      final String exePath = getPathToExe();
      if (exePath != null) {
        throw new BuildException("Error while checking out: Surround executable \"" + exePath + "\" not found.", getAgentHost());
      }
    }
    throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, getAgentHost());
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
      for (final Iterator i = getDepotPaths().iterator(); i.hasNext();) {
        SurroundCommand command = null;
        try {
          final RepositoryPath repositoryPath = (RepositoryPath) i.next();
          // create Surround label command
          command = new SurroungLabelCommand(agent,
                  getPathToExe(),
                  getSettingValue(VersionControlSystem.SURROUND_USER),
                  getPasswordSetting(),
                  getSettingValue(VersionControlSystem.SURROUND_HOST),
                  getSettingValue(VersionControlSystem.SURROUND_PORT, 5400),
                  branch(),
                  repositoryPath.getPath(),
                  label);
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
   * @see SurroundUsersParser
   */
  public Map getUsersMap() throws CommandStoppedException, AgentFailureException {
    final HashMap result = new HashMap(11);
    SurroundCommand command = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      command = new SurroundUserListCommand(agent,
              getPathToExe(),
              getSettingValue(VersionControlSystem.SURROUND_USER),
              getPasswordSetting(),
              getSettingValue(VersionControlSystem.SURROUND_HOST),
              getSettingValue(VersionControlSystem.SURROUND_PORT, 5400));
      command.execute();
      result.putAll(new SurroundUsersParser(command.getStdoutFile()).parse());
    } catch (final IOException e) {
      final Error err = new Error(StringUtils.toString(e));
      err.setDetails(e);
      errorManager.reportSystemError(err);
    } finally {
      cleanup(command);
    }
    return result;
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
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.SURROUND_REPOSITORY);
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.SURROUND_BRANCH);
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.SURROUND_HOST);
    hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE);
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
   * @see AbstractSourceControl#initLocalCopyIfNecessary()
   * @see SourceControl#checkoutLatest()
   */
  public final boolean isBuildDirInitialized() throws IOException, BuildException, AgentFailureException {
    final Agent agent = getCheckoutDirectoryAwareAgent();
    boolean result = true;
    final String rbd = getRelativeBuildDir();
    if (!(agent.fileRelativeToCheckoutDirExists(rbd))
            || !(agent.fileRelativeToCheckoutDirExists(rbd + '/' + ".MySCMServerInfo"))) {
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
   */
  public String getSyncCommandNote(final int changeListID) {
    final ChangeList changeList = configManager.getChangeList(changeListID);
    if (changeList == null) return STRING_NO_SYNC_NOTE_AVAILABLE;
    final StringBuilder cmd = new StringBuilder(100);
    cmd.append("Use branch \"");
    cmd.append(branch());
    cmd.append("\" and timestamp \"");
    cmd.append(changeList.getCreatedAt(SurroundCommand.getOutputDateFormatter()));
    cmd.append("\" to get this build ");
    return cmd.toString();
  }


  /**
   * @return Map with a shell variable name as a key and variable
   *         value as value. The shell variables will be made
   *         avaiable to the build commands.
   *         <p/>
   *         This is a default implementation that returns an
   *         empty map.
   * @see org.parabuild.ci.build.BuildScriptGenerator#addVariables(Map)
   */
  public Map getShellVariables() {
    return new HashMap(11);
  }


  /**
   * @return decryped password or null if not defined.
   */
  private String getPasswordSetting() {
    final String encrypedPassword = getSettingValue(VersionControlSystem.SURROUND_PASSWORD);
    if (encrypedPassword == null) return null;
    return SecurityManager.decryptPassword(encrypedPassword);
  }


  /**
   * Returs project source line repository paths relative to
   * Surround root. Firs item in the list is a project build
   * home.
   *
   * @return List of Surround repository paths composing a
   *         project
   * @throws BuildException
   *
   */
  private List getDepotPaths() throws BuildException {
    try {
      final SurroundRepositoryPathParser parser = new SurroundRepositoryPathParser();
      return parser.parseDepotPath(getSettingValue(VersionControlSystem.SURROUND_REPOSITORY));
    } catch (final ValidationException e) {
      throw new BuildException(e, getAgentHost());
    }
  }


  /**
   * Class to use as a map key whe processing parsed change
   * lists.
   *
   * @see SurroundSourceControl#getChangesFromDate(Date,
   *      Date, int)
   */
  private static final class SurroundChangeListKey {

    private int hashCode = 0;


    public SurroundChangeListKey(final ChangeList changeList) {
      hashCode = changeList.getCreatedAt().hashCode();
      hashCode = 29 * hashCode + (changeList.getClient() != null ? changeList.getClient().hashCode() : 0);
      hashCode = 29 * hashCode + changeList.getDescription().hashCode();
      hashCode = 29 * hashCode + changeList.getUser().hashCode();
    }


    @SuppressWarnings("RedundantIfStatement")
    public boolean equals(final Object o) {
      if (this == o) return true;
      if (!(o instanceof SurroundChangeListKey)) return false;
      final SurroundChangeListKey chlKey = (SurroundChangeListKey) o;
      if (hashCode != chlKey.hashCode) return false;
      return true;
    }


    public int hashCode() {
      return hashCode;
    }
  }

}
