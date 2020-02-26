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
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.util.ValidationException;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.security.SecurityManager;

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
 *
 * @noinspection OverlyCoupledMethod
 */
final class VaultSourceControl extends AbstractSourceControl {

  private static final Log LOG = LogFactory.getLog(VaultSourceControl.class); // NOPMD
  private Date lastSyncDate = null; // holds last sync date


  VaultSourceControl(final BuildConfig buildConfig) {
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
    if (LOG.isDebugEnabled()) {
      LOG.debug("begin checkoutLatest");
    }
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      cleanupLocalCopyIfNecessary();
      for (final Iterator i = getDepotPaths().iterator(); i.hasNext();) {
        VaultCommand command = null;
        try {
          final RepositoryPath repositoryPath = (RepositoryPath) i.next();
          final String path = repositoryPath.getPath();
          if (LOG.isDebugEnabled()) {
            LOG.debug("checking out latest for: " + path);
          }

          // execute Vault history command
          final VaultGetCommandParameters parameters = new VaultGetCommandParameters();
          parameters.setRepositoryPath(path);
          setCommonParameters(parameters);
          command = new VaultGetCommand(agent, getPathToVaultExe(agent), parameters);
          command.execute();
          // parse - will throw IOException if there is a error
          final VaultOutputParser vaultOutputParser = new VaultOutputParser();
          vaultOutputParser.parse(command.getStdoutFile());
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("end checkoutLatest");
      }
    } catch (final IOException e) {
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  /**
   * Syncs to a given change list number.
   */
  public void syncToChangeList(final int changeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("begin syncToChangeList changeListID: " + changeListID);
    }
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      validateChangeListID(changeListID);
      cleanupLocalCopyIfNecessary();

      // NOTE: we don't init the directory for GETVERSION command populates it.
//      initLocalCopyIfNecessary();

      // NOTE: simeshev@parabuilci.org - 2005-12-10 - we have Vaults' TX ID.
      // now, using this TX ID we have to determine each folder's versions
      // so that we can use GETVERSION command.
      final ChangeList changeList = configManager.getChangeList(changeListID);
      final Date changeListDate = changeList.getCreatedAt();
      for (final Iterator i = getDepotPaths().iterator(); i.hasNext();) {
        VaultCommand command = null;
        try {
          final RepositoryPath repositoryPath = (RepositoryPath) i.next();
          final String path = repositoryPath.getPath();
          if (LOG.isDebugEnabled()) {
            LOG.debug("syncing for: " + path);
          }

          //
          // find directory version to sync to
          //
          final int version = getRepositoryPathVersion(agent, path, changeListDate);
          if (version == -1) {
            reportCannotFindVersion(agent, path, changeListDate);
            continue; // nothing to sync to
          }
          if (LOG.isDebugEnabled()) {
            LOG.debug("version: " + version);
          }

          //
          // sync to found version
          //
          final VaultGetVersionCommandParameters getversionCommandParameters = new VaultGetVersionCommandParameters();
          setCommonParameters(getversionCommandParameters);
          getversionCommandParameters.setRepositoryPath(path);
          getversionCommandParameters.setVersion(version);
          command = new VaultGetVersionCommand(agent, getPathToVaultExe(agent), getversionCommandParameters);
          command.execute();
          new VaultOutputParser().parse(command.getStdoutFile()); // no errors

        } catch (final ParseException e) {
          throw IoUtils.createIOException(e);
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
      // record last sync date
      this.lastSyncDate = (Date) changeListDate.clone();
      if (LOG.isDebugEnabled()) {
        LOG.debug("end syncToChangeList");
      }
    } catch (final IOException e) {
      throw new BuildException("Error while checking out: " + StringUtils.toString(e), e, getAgentHost());
    }
  }


  /**
   * @param agent
   * @param path
   * @param changeListDate
   * @return
   * @throws IOException
   * @throws CommandStoppedException
   * @throws ParseException
   */
  private int getRepositoryPathVersion(final Agent agent, final String path, final Date changeListDate) throws IOException, CommandStoppedException, ParseException, AgentFailureException {
    final VaultDateFormat format = new VaultDateFormat(agent.defaultLocale());
    VaultCommand command = null;
    try {
      final VaultVersionHistoryCommandParameters versionHistoryCommandParameters = new VaultVersionHistoryCommandParameters();

      // NOTE: simeshev@parabuilci.org - 2005-12-10 - as of this writing
      // VERSIONHISTORY won't list an item at exact enddate. we have to
      // add one second to the date. this looks like a bug in the client
      // and may change in future. we should account for this.
      final Calendar endCalendar = Calendar.getInstance();
      endCalendar.setTime(changeListDate);
      endCalendar.add(Calendar.SECOND, 1);

      // NOTE: simeshev@parabuilci.org - 2007-04-19 - we have
      // to provide begin date, according to bug #1132.
      final Calendar beginCalendar = Calendar.getInstance();
      beginCalendar.setTime(changeListDate);
      beginCalendar.add(Calendar.MONTH, -1);

      setCommonParameters(versionHistoryCommandParameters);
      versionHistoryCommandParameters.setBeginDate(beginCalendar.getTime());
      versionHistoryCommandParameters.setEndDate(endCalendar.getTime());
      versionHistoryCommandParameters.setRowLimit(2); // 2 for we account for possible change in behaviour in enddate behaviour (see note  above)
      versionHistoryCommandParameters.setRepositoryPath(path);
      command = new VaultVersionHistoryCommand(agent, getPathToVaultExe(agent), versionHistoryCommandParameters);
      command.execute();
      final Vault vault = new VaultOutputParser().parse(command.getStdoutFile());
      cleanup(command);
      // find item that date is closest to change list
      int version = -1;
      Date closestDate = null;
      final Vault.History history = vault.getHistory();
      final List items = history.getItems();
      for (int j = 0, n = items.size(); j < n; j++) {
        final Vault.Item item = (Vault.Item) items.get(j);
        final Date itemDate = format.parseOutput(item.getDate());
        if (itemDate.compareTo(changeListDate) > 0) {
          continue;
        }
        if (closestDate == null || itemDate.compareTo(closestDate) > 0) {
          closestDate = itemDate;
          version = item.getVersion();
        }
      }
      return version;
    } finally {
      cleanup(command);
    }
  }


  /**
   * Returns relative project path
   */
  public String getRelativeBuildDir() throws BuildException {
    final List depotPaths = getDepotPaths();
    final RepositoryPath rp = (RepositoryPath) depotPaths.get(0);
    return rp.getPath().substring(2);
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
   */
  public int getChangesSince(final int startChangeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    if (LOG.isDebugEnabled()) {
      LOG.debug("begin getChangesSince changeListID: " + startChangeListID);
    }
    try {

      // NOTE: simeshev@parabuilci.org - we do not do clean-up or init
      // of the local copy as we retrieve changes directly from Vault
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
        if (latest == null) {
          return startChangeListID;
        }
        rowLimit = maxNumberOfChangeLists();
        // roll forward for 1 second - otherwise Vault will add same last change list.
        final Calendar c = Calendar.getInstance();
        c.setTime(latest.getCreatedAt());
        c.add(Calendar.SECOND, 1);
        beginDate = c.getTime();
      }

      // requests changes from Vault server
      final long timeStarted = System.currentTimeMillis();
      final Agent agent = getCheckoutDirectoryAwareAgent();
      final List result = new ArrayList(101);
      final Locale builderLocale = agent.defaultLocale();
      for (final Iterator i = getDepotPaths().iterator(); i.hasNext();) {
        VaultCommand command = null;
        try {
          final RepositoryPath repositoryPath = (RepositoryPath) i.next();
          final String path = repositoryPath.getPath();
          if (LOG.isDebugEnabled()) {
            LOG.debug("getting changes for: " + path);
          }

          // execute Vault history command
          final VaultHistoryCommandParameters parameters = new VaultHistoryCommandParameters();
          setCommonParameters(parameters);
          parameters.setBeginDate(beginDate);
          parameters.setRepositoryPath(path);
          parameters.setRowLimit(rowLimit);
          command = new VaultHistoryCommand(agent, getPathToVaultExe(agent), parameters);
          command.execute();

          // analyze change log
          final VaultChangeLogParser changeLogParser = new VaultChangeLogParser(builderLocale, maxChangeListSize());
          final List changeLists = changeLogParser.parseChangeLog(command.getStdoutFile());
          if (LOG.isDebugEnabled()) {
            LOG.debug("changelist size: " + changeLists.size());
          }
          if (changeLists.isEmpty()) {
            continue;
          }

          // add this path changes to result
          result.addAll(changeLists);
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }

      // get latest rowLimit changes if necessary
      result.sort(ChangeList.REVERSE_CHANGE_NUMBER_COMPARATOR);

      // result
      final long processingTime = System.currentTimeMillis() - timeStarted;
      if (LOG.isDebugEnabled()) {
        LOG.debug("time to process change lists: " + processingTime);
      }

      // return if no changes
      if (result.isEmpty()) {
        return startChangeListID;
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
    if (LOG.isDebugEnabled()) {
      LOG.debug("begin label");
    }
    ArgumentValidator.validateArgumentNotNull(this.lastSyncDate, "last sync date");
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      for (final Iterator i = getDepotPaths().iterator(); i.hasNext();) {
        VaultCommand command = null;
        try {
          final RepositoryPath repositoryPath = (RepositoryPath) i.next();
          final String path = repositoryPath.getPath();
          if (LOG.isDebugEnabled()) {
            LOG.debug("checking out latest for: " + path);
          }

          final int version = getRepositoryPathVersion(agent, path, lastSyncDate);
          if (version == -1) {
            reportCannotFindVersion(agent, path, null);
            continue; // nothing to sync to
          }
          if (LOG.isDebugEnabled()) {
            LOG.debug("version: " + version);
          }

          // execute Vault LABEL command
          final VaultLabelCommandParameters parameters = new VaultLabelCommandParameters();
          setCommonParameters(parameters);
          parameters.setRepositoryPath(path);
          parameters.setLabel(label);
          parameters.setVersion(version);
          command = new VaultLabelCommand(agent, getPathToVaultExe(agent), parameters);
          command.execute();

          // parse - will throw IOException if there is a error
          final VaultOutputParser vaultOutputParser = new VaultOutputParser();
          vaultOutputParser.parse(command.getStdoutFile());
        } finally {
          cleanup(command); // cleanup this cycle
        }
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("end label");
      }
    } catch (final ParseException | IOException e) {
      throw new BuildException("Error while labeling: " + StringUtils.toString(e), e, getAgentHost());
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
  public final void reloadConfiguration() {
    // Get resolved settings
    final Map newSettings = getResolvedSettings();

    // check if critical settings has changed
    final SourceControlSettingChangeDetector scd = new SourceControlSettingChangeDetector(currentSettings, newSettings);
    boolean hasToCleanUp = false;
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.VAULT_HOST);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.VAULT_PROXY_SERVER);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.VAULT_REPOSITORY);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.VAULT_REPOSITORY_PATH);
    hasToCleanUp |= scd.settingHasChanged(VCSAttribute.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE);
    if (hasToCleanUp) {
      setHasToCleanUp();
    }
    // update current settings map
    replaceCurrentSettings(newSettings);
  }


  /**
   * @return Map with a shell variable name as a key and variable
   *         value as value. The shell variables will be made
   *         available to the build commands.
   * @see BuildScriptGenerator#addVariables(Map)
   */
  public Map getShellVariables() {
    return Collections.emptyMap();
  }


  /**
   * Returns project source line repository paths. First item in
   * the list is a project build home.
   *
   * @return List of SVN repository paths composing a project
   * @throws BuildException
   */
  private List getDepotPaths() throws BuildException {
    try {
      final String path = getSettingValue(VCSAttribute.VAULT_REPOSITORY_PATH);
      if (LOG.isDebugEnabled()) {
        LOG.debug("path: " + path);
      }
      return new VaultDepotPathParser().parseDepotPath(path);
    } catch (final ValidationException e) {
      throw new BuildException(e, getAgentHost());
    }
  }


  private static String getPathToVaultExe(final Agent agent) throws IOException, AgentFailureException {
    // NOTE: simeshev@parabuilci.org - 2005-12-09 - we get Vault path
    // from our own directory instead of path setting for as of this
    // writing only EA version of vault client supports VERSIONHISTORY
//    final String settingValue = getSettingValue(SourceControlSetting.VAULT_EXE);
    final String settingValue = agent.getSystemProperty("catalina.home") + '/' + ".." + '/' + "bin" + '/' + "win" + '/' + "Vault.exe";
    return StringUtils.putIntoDoubleQuotes(agent.getFileDescriptor(settingValue).getCanonicalPath());
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
      throw new BuildException("Error while accessing Vault: Vault executable not found.", getAgentHost());
    }
    throw new BuildException("Error while accessing Vault: " + StringUtils.toString(e), e, getAgentHost());
  }


  /**
   * Helper method to populate common Vault command parameters
   * from current settings.
   *
   * @param parameters
   */
  private void setCommonParameters(final VaultCommandParameters parameters) {
    parameters.setHost(getSettingValue(VCSAttribute.VAULT_HOST));
    parameters.setProxyDomain(getSettingValue(VCSAttribute.VAULT_PROXY_DOMAIN));
    parameters.setProxyPort(getSettingValue(VCSAttribute.VAULT_PROXY_PORT));
    parameters.setProxyServer(getSettingValue(VCSAttribute.VAULT_PROXY_SERVER));
    parameters.setProxyUser(getSettingValue(VCSAttribute.VAULT_PROXY_USER));
    parameters.setRepository(getSettingValue(VCSAttribute.VAULT_REPOSITORY));
    parameters.setUser(getSettingValue(VCSAttribute.VAULT_USER));
    parameters.setUseSSL(SourceControlSetting.OPTION_CHECKED.equals(getSettingValue(VCSAttribute.VAULT_USE_SSL)));
    parameters.setPassword(StringUtils.isBlank(getSettingValue(VCSAttribute.VAULT_PASSWORD)) ? "" : SecurityManager.decryptPassword(getSettingValue(VCSAttribute.VAULT_PASSWORD)));
    parameters.setProxyPassword(StringUtils.isBlank(getSettingValue(VCSAttribute.VAULT_PROXY_PASSWORD)) ? "" : SecurityManager.decryptPassword(getSettingValue(VCSAttribute.VAULT_PROXY_PASSWORD)));
  }


  private void reportCannotFindVersion(final Agent agent, final String path, final Date changeListDate) throws IOException, AgentFailureException {
    final VaultDateFormat format = new VaultDateFormat(agent.defaultLocale());
    final Error error = new Error();
    error.setBuildID(activeBuildID);
    error.setDescription("Could not find a version for Vault directory");
    error.setDetails("Vault repository: " + getSettingValue(VCSAttribute.VAULT_REPOSITORY)
            + ", directory: " + path
            + (lastSyncDate != null ? ", last sync date: " + format.formatInput(lastSyncDate) : "")
            + (changeListDate != null ? ", change list date: " + format.formatInput(changeListDate) : ""));
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  public String toString() {
    return "VaultSourceControl{" +
            "lastSyncDate=" + lastSyncDate +
            "} " + super.toString();
  }
}
