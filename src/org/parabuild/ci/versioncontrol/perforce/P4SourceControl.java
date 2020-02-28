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
package org.parabuild.ci.versioncontrol.perforce;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.build.BuildScriptGenerator;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.configuration.ChangeListsAndIssues;
import org.parabuild.ci.configuration.ChangeListsAndIssuesImpl;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.IssueTracker;
import org.parabuild.ci.object.SourceControlSetting;
import org.parabuild.ci.object.SystemProperty;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.util.CommonConstants;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.versioncontrol.AbstractSourceControl;
import org.parabuild.ci.versioncontrol.SourceControl;
import org.parabuild.ci.versioncontrol.SourceControlSettingChangeDetector;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Perforce version control implementation.
 */
public final class P4SourceControl extends AbstractSourceControl implements CommonConstants {

  private static final Log log = LogFactory.getLog(P4SourceControl.class);

  private static final String NAME_UNEXPECTED_LABEL_ERROR = "Error while creating or accessing P4 label";
  private static final String NAME_UNKNOWN_LABEL_ERROR = "Unknown error while creating or accessing P4 label. No error message was provided by P4.";
  private static final String NAME_UNEXPECTED_SYNC_ERROR = "Error while doing p4 sync";
  private static final String NAME_UNEXPECTED_COUNTER_ERROR = "Error while retrieving P4 counter.";
  private static final String UNEXPECTED_ERROR_WHILE_LOGGIN_IN = "Unexpected error while loggin in";

  private static final String PARABUILD_PREFIX = "PARABUILD_";
  public static final String PARABUILD_P4PORT = PARABUILD_PREFIX + P4Command.P4PORT;
  public static final String PARABUILD_P4USER = PARABUILD_PREFIX + P4Command.P4USER;
  public static final String PARABUILD_P4CLIENT = PARABUILD_PREFIX + P4Command.P4CLIENT;
  public static final String PARABUILD_P4PASSWD = PARABUILD_PREFIX + P4Command.P4PASSWD;


  private boolean lazyDepotInitCalled = false;

  private final P4ClientNameGenerator clientNameGenerator;


  /**
   * Constructor.
   * <p/>
   * Uses VCS settings for the checkout directory.
   *
   * @param buildConfig
   */
  public P4SourceControl(final BuildConfig buildConfig) {
    super(buildConfig);
    clientNameGenerator = new P4ClientNameGeneratorImpl();
  }


  /**
   * Constructor.
   * <p/>
   * Overrides the VCS setting for checkout directory.
   *
   * @param buildConfig
   * @param checkoutDirectoryName to use instead of VCS setting.
   * @param clientNameGenerator
   */
  public P4SourceControl(final BuildConfig buildConfig, final String checkoutDirectoryName, final P4ClientNameGenerator clientNameGenerator) {
    super(buildConfig, checkoutDirectoryName);
    this.clientNameGenerator = clientNameGenerator;
  }


  /**
   * Checks out latest state of the source line
   */
  public void checkoutLatest() throws BuildException, CommandStoppedException, AgentFailureException {
    P4Command command = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      // preExecute
      loginIfNecessary(agent);
      lazyInitDepotViewFromDepotIfNecessary();
      createOrUpdateClient();
      cleanupLocalCopyIfNecessary();

      // Return if should not sync
      if (getP4Properties().isDoNotSync()) {
        return;
      }

      // Execute p4 sync command
      command = makeP4Command(agent, true);
      command.setExeArguments("sync " + STR_SPACE + getSyncOptions() + STR_SPACE + "//" + makeClientName(agent, getP4Properties().getP4User(), getP4Properties().getClientNameTemplate()) + STR_TRIPPLE_DOTS);
      command.setDescription("sync command");
      command.execute();
    } catch (final IOException e) {
      throw new BuildException(NAME_UNEXPECTED_SYNC_ERROR, e, getAgentHost());
    } finally {
      cleanup(command);
    }
  }


  /**
   * Syncs to a given change list number
   */
  public void syncToChangeList(final int changeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    if (log.isDebugEnabled()) {
      log.debug("syncing to changeListID: " + changeListID);
    }
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      final String clientName = makeClientName(agent, getP4Properties().getP4User(), getP4Properties().getClientNameTemplate());
      syncToChangeList(changeListID, clientName);
    } catch (final IOException e) {
      throw new BuildException(NAME_UNEXPECTED_SYNC_ERROR, e, getAgentHost());
    }
  }


  public void syncToChangeList(final int changeListID, final String clientName) throws BuildException, CommandStoppedException, AgentFailureException {
    if (log.isDebugEnabled()) {
      log.debug("syncing to changeListID: " + changeListID);
    }
    P4Command command = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();

      loginIfNecessary(agent);
      lazyInitDepotViewFromDepotIfNecessary();

      // cleate client
      final P4Properties props = getP4Properties();
      final String checkoutDirName = agent.getCheckoutDirName();
      createOrUpdateClient(agent, checkoutDirName, props, clientName, props.getP4DepotPath(), getEffectiveRelativeBuildDir(props));

      // Cleanup
      cleanupLocalCopyIfNecessary();

      // Return if should not sync
      if (getP4Properties().isDoNotSync()) {
        return;
      }

      // Init local copy
      initLocalCopyIfNecessary();

      // get P4 change list number
      final String changeListNumber = configManager.getChangeListNumberFromID(changeListID);

      // Execute p4 sync command
      command = makeP4Command(agent, true);
      command.setExeArguments("sync " + getSyncOptions() + STR_SPACE + "//" + clientName + STR_TRIPPLE_DOTS + '@' + changeListNumber);
      command.setDescription("sync command");
      command.execute();
    } catch (final IOException | ValidationException e) {
      throw new BuildException(NAME_UNEXPECTED_SYNC_ERROR, e, getAgentHost());
    } finally {
      cleanup(command);
    }
  }


  /**
   * Returns relative project path
   */
  public String getRelativeBuildDir() throws BuildException {
    try {
      final P4Properties p4Properties = getP4Properties();
      if (p4Properties.isDoNotSync()) {
        return ".";
      } else {
        final P4ClientViewParser parser = new P4ClientViewParser();
        final P4ClientView p4ClientView = parser.parse(getEffectiveRelativeBuildDir(p4Properties), p4Properties.getP4DepotPath());
        return p4ClientView.getRelativeBuildDir(); // NOPMD
      }
    } catch (final ValidationException e) {
      throw new BuildException(e, getAgentHost());
    }
  }


  private static String getEffectiveRelativeBuildDir(final P4Properties p4Properties) {
    return p4Properties.isAdvancedViewMode() ? p4Properties.getRelativeBuildDir() : "";
  }


  /**
   * Returns value of a counter associated with this instance of
   * P4 SCM.
   *
   * @see #getChangesSince(int changeListID)
   */
  public int getChangeListCounter(final boolean createOrUpdateClient) throws BuildException, CommandStoppedException, AgentFailureException {
    P4Command command = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();

      if (createOrUpdateClient) {
        createOrUpdateClient();
      }

      // load properties
      final P4Properties props = getP4Properties();

      // create p4 sync command
      command = makeP4Command(agent, true);
      command.setExeArguments("counter " + props.getP4Counter());
      command.setDescription("counter command");
      command.execute();

      // parse output and return result
      final P4CounterParser usersParser = new P4CounterParser();
      return usersParser.parse(command.getStdoutFile());
    } catch (final IOException e) {
      throw new BuildException(NAME_UNEXPECTED_COUNTER_ERROR, e, getAgentHost());
    } finally {
      cleanup(command);
    }
  }


  /**
   * Returns ID of list of changes that were made to controlled
   * source line since the given change list ID
   *
   * @param startChangeListID base change list ID
   * @return new change list ID if there were changes made, or
   *         the same base change list if there were changes
   * @throws BuildException
   */
  public int getChangesSince(final int startChangeListID) throws BuildException, CommandStoppedException, AgentFailureException {
    if (log.isDebugEnabled()) {
      log.debug("begin getChangesSince, changeListID: " + startChangeListID);
    }
    // load properties
    final int newChangeListID;
    final P4Properties props = getP4Properties();
    if (startChangeListID < 0) {
      // get the whole list
      newChangeListID = getChanges(null, null, initialNumberOfChangeLists());
    } else {
      final int fromChangeListNumber = Integer.parseInt(configManager.getChangeListNumberFromID(startChangeListID));
      final String useToChangeListNumber;
      if (props.isCounterDefined()) {
        // there is counter, use it
        final int counter = getChangeListCounter(false);
        warnAdministratorIfCounterIsZero(counter);
        if (counter <= fromChangeListNumber) {
          // no new change lists, return
          return startChangeListID;
        } else {
          useToChangeListNumber = Integer.toString(counter);
        }
      } else {
        // there is no counter, use max
        useToChangeListNumber = "99999999";
      }
      newChangeListID = getChanges(Integer.toString(fromChangeListNumber + 1), useToChangeListNumber, maxNumberOfChangeLists());
    }

    if (newChangeListID == ChangeList.UNSAVED_ID) {
      return startChangeListID; // return starting change list because nothing has changed.
    } else {
      return newChangeListID;
    }
  }


  /**
   * Requests changes from Perforce server and stores them
   * in the DB if found.
   *
   * @param fromChangeListNumber
   * @param toChangeListNumber
   * @param maxChangeLists
   * @return new change list ID if there were changes made,
   *         or {@link return ChangeList#UNSAVED_ID if there were
   *         no changes.
   */
  int getChanges(final String fromChangeListNumber,
                 final String toChangeListNumber,
                 final int maxChangeLists)
          throws BuildException, CommandStoppedException, AgentFailureException {

    if (log.isDebugEnabled()) {
      log.debug("begin getChangesSince, from: " + fromChangeListNumber + ", to: " + toChangeListNumber);
    }

    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      loginIfNecessary(agent);
      lazyInitDepotViewFromDepotIfNecessary();

      final P4Properties props = getP4Properties();
      final P4SavingChangeListDriver changeDriver = new P4SavingChangeListDriver(activeBuildID, props.getExclusionPaths());

      // execute
      getChanges(fromChangeListNumber,
              toChangeListNumber,
              maxChangeLists,
              makeClientName(agent, props.getP4User(), props.getClientNameTemplate()),
              agent.getCheckoutDirName(),
              getEffectiveRelativeBuildDir(props),
              props.getP4DepotPath(),
              isJobCollectionEnabled(),
              changeDriver);

      // return
      return changeDriver.getResultChangeListID();
    } catch (final IOException | ValidationException e) {
      throw new BuildException(NAME_UNEXPECTED_SYNC_ERROR, e, getAgentHost());
    }
  }


  /**
   * Requests changes from Perforce server and stores them
   * in the DB if found.
   *
   * @param fromChangeListNumber
   * @param toChangeListNumber
   * @param maxChangeLists
   * @param clientName
   * @param checkoutDirName
   * @param effectiveRelativeBuildDir
   * @param depotView
   * @param jobCollectionEnabled
   * @param changeListDriver
   */
  public void getChanges(final String fromChangeListNumber,
                         final String toChangeListNumber,
                         final int maxChangeLists,
                         final String clientName,
                         final String checkoutDirName,
                         final String effectiveRelativeBuildDir,
                         final String depotView,
                         final boolean jobCollectionEnabled,
                         final P4ChangeListDriver changeListDriver) throws BuildException, CommandStoppedException, ValidationException, IOException, AgentFailureException {

    final Agent agent = getCheckoutDirectoryAwareAgent();
    final ChangeListsAndIssues accumulator = new ChangeListsAndIssuesImpl();

    // prepare p4 command for P4ChangeListChunkDriver
    final P4Command command = new P4Command(agent);
    command.setP4All(getP4Properties());
    command.setP4Client(clientName);
    command.setP4Options("-s");
    command.setCurrentDirectory(checkoutDirName);

    // create driver
    final P4AccumulatingChangeListChunkDriver chunkDriver = new P4AccumulatingChangeListChunkDriver(command,
            accumulator,
            maxChangeListSize(),
            jobCollectionEnabled);

    // create from revision
    final String fromRevision = StringUtils.isBlank(fromChangeListNumber) ? fromChangeListNumber : '@' + fromChangeListNumber;

    // get changes
    getChanges(fromRevision,
            toChangeListNumber,
            maxChangeLists,
            clientName,
            checkoutDirName,
            effectiveRelativeBuildDir,
            depotView,
            "//" + clientName + STR_TRIPPLE_DOTS,
            chunkDriver);

    // call driver
    changeListDriver.process(accumulator);
  }


  /**
   * Requests changes from Perforce server and stores them
   * in the DB if found.
   *
   * @param fromRevision
   * @param toRevision
   * @param maxChangeLists
   * @param clientName
   * @param checkoutDirName
   * @param effectiveRelativeBuildDir
   * @param depotView
   */
  public void getChanges(final String fromRevision,
                         final String toRevision,
                         final int maxChangeLists,
                         final String clientName,
                         final String checkoutDirName,
                         final String effectiveRelativeBuildDir,
                         final String depotView,
                         final String depotPath,
                         final P4ChangeDriver changeDriver) throws BuildException, CommandStoppedException, ValidationException, IOException, AgentFailureException {

    final Agent agent = getCheckoutDirectoryAwareAgent();

    // prepare p4 command for P4ChangeListChunkDriver
    final P4Command command = new P4Command(agent);
    command.setP4All(getP4Properties());
    command.setP4Client(clientName);
    command.setP4Options("-s");
    command.setCurrentDirectory(checkoutDirName);

    // create driver bridge betwenn chunks and changes
    final P4ChangeListChunkDriver chunkDriver = new P4ChangeListChunkChangeDriver(command, changeDriver);

    // get changes
    getChanges(fromRevision,
            toRevision,
            maxChangeLists,
            clientName,
            checkoutDirName,
            effectiveRelativeBuildDir,
            depotView,
            depotPath,
            chunkDriver);
  }


  /**
   * Requests changes from Perforce server and stores them
   * in the DB if found.
   *
   * @param fromRevision
   * @param toRevision
   * @param maxChangeLists
   * @param clientName
   * @param checkoutDirName
   * @param effectiveRelativeBuildDir
   * @param depotView
   * @param changeListChunkDriver
   */
  void getChanges(final String fromRevision,
                  final String toRevision,
                  final int maxChangeLists,
                  final String clientName,
                  final String checkoutDirName,
                  final String effectiveRelativeBuildDir,
                  final String depotView,
                  final String depotPath,
                  final P4ChangeListChunkDriver changeListChunkDriver) throws BuildException, CommandStoppedException, ValidationException, AgentFailureException {

    if (log.isDebugEnabled()) {
      log.debug("begin getChangesSince, path: " + depotPath + ", from rev: " + fromRevision + ", to rev: " + toRevision);
    }
    P4Command command = null;
    FileInputStream fis = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();

      loginIfNecessary(agent);

      // prapare max change lists option
      final String maxChangeListsOption;
      if (maxChangeLists == SystemProperty.UNLIMITED_MAX_NUMBER_OF_CHANGE_LISTS) {
        maxChangeListsOption = "";
      } else {
        maxChangeListsOption = "-m" + maxChangeLists;
      }

      // prepare range option
      final String range;
      if (StringUtils.isBlank(toRevision)) {
        if (!StringUtils.isBlank(fromRevision)) {
          throw new IllegalArgumentException("Starting change list should be blank but it wasn't");
        }
        range = "";
      } else {
        final String useFromChangeListNumber = ArgumentValidator.validateArgumentNotBlank(fromRevision, "starting change list");
        final String useToChangeListNumber = ArgumentValidator.validateArgumentNotBlank(toRevision, "ending change list");
        range = useFromChangeListNumber + ',' + useToChangeListNumber;
      }

      // load properties
      final P4Properties props = getP4Properties();

      // update client
      createOrUpdateClient(agent, checkoutDirName, props, clientName, depotView, effectiveRelativeBuildDir);

      // execute p4 changes command
      command = new P4Command(agent);
      command.setP4All(getP4Properties());
      command.setP4Client(clientName);
      command.setP4Options("-s");
      command.setCurrentDirectory(checkoutDirName);
      command.setExeArguments("changes -s submitted " + maxChangeListsOption + ' ' + depotPath + range);
      command.setDescription("changes command");
      command.execute();

      // parse changes
      fis = new FileInputStream(command.getStdoutFile());
      final P4ChangeLogParser logParser = new P4ChangeLogParser(maxChangeListSize());
      final Collection chunks = logParser.parseChangesLog(fis);
      IoUtils.closeHard(fis);
      command.cleanup();

      // return if there are now changes
      if (!chunks.isEmpty()) {
        // go through change list
        for (final Iterator i = chunks.iterator(); i.hasNext(); ) {
          changeListChunkDriver.process((List) i.next());
        }
      }
    } catch (final IOException e) {
      throw new BuildException(NAME_UNEXPECTED_SYNC_ERROR, e, getAgentHost());
    } finally {
      IoUtils.closeHard(fis);
      cleanup(command);
    }
  }


  /**
   * @return true if P4 jobs shoould be collected when processing
   *         new changes.
   */
  private boolean isJobCollectionEnabled() {
    // NOTE: vimeshev - 06/18/2004 - Find if there are any P4
    // issue tackers configured for this build or if there other
    // builds that refere this build and have P4 issue tracker.
    //
    // The reason for this is that reference version controls
    // don't parse describe logs themselves. Rather, they just
    // copy over changes from automatic version controls.
    //
    // For P4 jobs are part of log produced by p4 describe,
    // so we grab jobs here anf then let reference source
    // controls decide if they need them and copy over.
    final byte tackerTypeToCheck = IssueTracker.TYPE_PERFORCE;
    final boolean refereingTrackersExist = configManager.referringIssueTrackersExist(activeBuildID, tackerTypeToCheck);
    final boolean owntTrackersExist = configManager.issueTrackersExist(buildID, tackerTypeToCheck);
    return owntTrackersExist || refereingTrackersExist;
  }


  /**
   *
   */
  private void warnAdministratorIfCounterIsZero(final int counter) {
    if (counter <= 0) {
      final org.parabuild.ci.error.Error err = new org.parabuild.ci.error.Error("P4 change list counter equals \"" + counter + "\".");
      err.setBuildID(buildID);
      err.setErrorLevel(org.parabuild.ci.error.Error.ERROR_LEVEL_WARNING);
      err.setSendEmail(false);
      err.setSubsystemName(org.parabuild.ci.error.Error.ERROR_SUBSYSTEM_SCM);
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
    try {
      loginIfNecessary(getCheckoutDirectoryAwareAgent());
      lazyInitDepotViewFromDepotIfNecessary();
      createOrUpdateLabel(label, null);
    } catch (final IOException e) {
      throw new BuildException(e, getAgentHost());
    }
  }


  /**
   * Removes labels with a given name.
   *
   * @param labels to remove.
   */
  public int removeLabels(final String[] labels) throws BuildException, CommandStoppedException, AgentFailureException {
    ArgumentValidator.validateArgumentNotNull(labels, "label list");
    if (labels.length == 0) {
      return 0;
    }

    P4Command command = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();

      loginIfNecessary(agent);
      lazyInitDepotViewFromDepotIfNecessary();
      createOrUpdateClient();

      // create and execute p4 command
      command = makeP4Command(agent, false);
      command.setClientRequired(false);
      int removedCount = 0;
      for (int i = 0; i < labels.length; i++) {
        final String label = labels[i];
        if (StringUtils.isBlank(label)) {
          continue;
        }
        command.setExeArguments("label -d " + label);
        command.setDescription("label command");
        command.execute();
        removedCount++;
      }
      return removedCount;
    } catch (final IOException e) {
      throw new BuildException(NAME_UNKNOWN_LABEL_ERROR, e, getAgentHost());
    } finally {
      cleanup(command);
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
    try {
      final ConfigurationManager cm = ConfigurationManager.getInstance();

      // update depot view if necessary
      final int depotViewSourceCode = cm.getSourceControlSettingValue(buildID, VersionControlSystem.P4_CLIENT_VIEW_SOURCE, VersionControlSystem.P4_CLIENT_VIEW_SOURCE_VALUE_FIELD);
      if (depotViewSourceCode == VersionControlSystem.P4_CLIENT_VIEW_SOURCE_VALUE_DEPOT_PATH) {
        loginIfNecessary(getCheckoutDirectoryAwareAgent());
        updateDepotViewFromDepot(cm.getSourceControlSettingValue(buildID, VersionControlSystem.P4_CLIENT_VIEW_BY_DEPOT_PATH, null));
      } else if (depotViewSourceCode == VersionControlSystem.P4_CLIENT_VIEW_SOURCE_VALUE_CLIENT_NAME) {
        loginIfNecessary(getCheckoutDirectoryAwareAgent());
        updateDepotViewFromNamedWorkspace(cm.getSourceControlSettingValue(buildID, VersionControlSystem.P4_CLIENT_VIEW_BY_CLIENT_NAME, null));
      }

      // Get resolved settings
      final Map newSettings = getResolvedSettings();

      // check if critical settings has changed
      final SourceControlSettingChangeDetector scd = new SourceControlSettingChangeDetector(currentSettings, newSettings);
      boolean hasToCleanUp = false;
      hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.P4_DEPOT_PATH);
      hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.P4_CLIENT_VIEW_BY_DEPOT_PATH);
      hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.P4_CLIENT_VIEW_SOURCE);
      hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.VCS_CUSTOM_CHECKOUT_DIR_TEMPLATE);
      hasToCleanUp |= scd.settingHasChanged(VersionControlSystem.DO_NOT_CHECKOUT);
      if (hasToCleanUp) {
        setHasToCleanUp();
      }
      if (log.isDebugEnabled()) {
        log.debug("hasToCleanUp: " + hasToCleanUp);
      }

      // update current settings map
      replaceCurrentSettings(newSettings);
    } catch (final RuntimeException e) {
      throw e;
    } catch (final Exception e) {
      throw  new IllegalStateException("Error while reloading Perforce configuration: " + StringUtils.toString(e), e);
    }
  }


  /**
   * Creates P4 client for the given build ID
   *
   * @return client spec used to created client for testing
   *         purposes.
   */
  public String createOrUpdateClient() throws BuildException, CommandStoppedException, AgentFailureException {
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      final P4Properties props = getP4Properties();
      final String clientName = makeClientName(agent, props.getP4User(), props.getClientNameTemplate());
      final String checkoutDirName = agent.getCheckoutDirName();
      return createOrUpdateClient(agent, checkoutDirName, props, clientName, props.getP4DepotPath(),
              getEffectiveRelativeBuildDir(props));
    } catch (final IOException | ValidationException e) {
      throw new BuildException(e, getAgentHost());
    }
  }


  /**
   * Creates P4 client according to the parameters.
   *
   * @return client spec used to created client for testing
   *         purposes.
   */
  private String createOrUpdateClient(final Agent agent, final String checkoutDirName, final P4Properties props,
                                      final String clientName, final String clientView, final String relativeBuildDir)
          throws CommandStoppedException, ValidationException, IOException, AgentFailureException {

    P4Command command = null;
    try {
      final String clientSpec = makeClientSpec(agent, activeBuildID, checkoutDirName,
              relativeBuildDir, clientName, clientView, props.getP4User(),
              props.isUseUNCPaths(), props.getModtimeOption(),
              props.getClobberOption(), props.getLineEnd());
      command = new P4ClientCommand(agent, getP4Properties(), clientSpec);
      command.setCurrentDirectory(checkoutDirName);
      command.execute();
      return clientSpec;
    } finally {
      cleanup(command);
    }
  }


  /**
   * Creates P4 label spec and adds files on the client to this
   * label. If depotview is null, this method will use P4 build
   * properties to compose it.
   */
  public void createOrUpdateLabel(final String labelName, final String labelDepotView) throws BuildException, CommandStoppedException, AgentFailureException {
    // REVIEWME: the way it's done now expects
    // that there was p4 sync issued before
    // calling this method. We may add additional
    // validations or re-sync to a given a number.
    InputStream is = null;
    P4Command command = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();

      // preExecute
      createOrUpdateClient();

      // load properties
      final P4Properties props = getP4Properties();

      // //////////////////////////////////////////////////////////////////////////
      // create label spec
      is = new ByteArrayInputStream(makeLabelSpec(agent, props, labelName, labelDepotView, "unlocked").getBytes());

      // create p4 label command
      command = makeP4Command(agent, false);
      command.setInputStream(is);

      // execute create label spec
      command.setExeArguments("label -i ");
      command.setDescription("label command");
      command.execute();
      cleanup(command);

      // //////////////////////////////////////////////////////////////////////////
      // execute labelsync
      command.setP4Client(makeClientName(agent, props.getP4User(), props.getClientNameTemplate()));
      command.setClientRequired(true);
      command.setInputStream(null);
      command.setExeArguments("labelsync -l " + labelName);
      command.setDescription("labelsync command");
      command.execute();

      cleanup(command);

      // //////////////////////////////////////////////////////////////////////////
      // lock label
      is = new ByteArrayInputStream(makeLabelSpec(agent, props, labelName, labelDepotView, "locked").getBytes());

      // create locking p4 label command
      command.setClientRequired(false);
      command.setInputStream(is);

      // execute locking create label spec
      command.setExeArguments("label -i ");
      command.setDescription("label command");
      command.execute();

      // analyze the result for errors
      cleanup(command);

    } catch (final IOException e) {
      throw new BuildException(NAME_UNKNOWN_LABEL_ERROR, e, getAgentHost());
    } catch (final ValidationException e) {
      throw new BuildException(NAME_UNEXPECTED_LABEL_ERROR + ':' + e.getMessage(), e, getAgentHost());
    } finally {
      IoUtils.closeHard(is);
      cleanup(command);
    }
  }


  private P4Command makeP4Command(final Agent agent, final boolean clientRequired) throws IOException, AgentFailureException {
    final P4Command command = new P4Command(agent);
    command.setP4All(getP4Properties());
    if (clientRequired) {
      command.setP4Client(makeClientName(agent, getP4Properties().getP4User(), getP4Properties().getClientNameTemplate()));
    } else {
      command.setClientRequired(false);
    }
    command.setP4Options("-s");
    command.setCurrentDirectory(agent.getCheckoutDirName());
    return command;
  }


  /**
   * Helper method to compose client spec.
   *
   * @param agent
   * @param buildID
   * @param checkoutDirName
   * @param effectiveRelativeBuildDir
   * @param clientName
   * @param depotPath
   * @param p4User
   * @param useUNCPaths
   * @param modtimeOption
   * @param clobberOption
   * @param lineEnd
   * @return
   * @throws IOException
   * @throws ValidationException
   */
  private static String makeClientSpec(final Agent agent, final int buildID, final String checkoutDirName,
                                       final String effectiveRelativeBuildDir, final String clientName, final String depotPath, final String p4User,
                                       final boolean useUNCPaths, final String modtimeOption, final String clobberOption, final String lineEnd) throws IOException, ValidationException, AgentFailureException {

    // make  simple template params
    final String date = P4ChangeLogParser.getP4DescribeDateFormatter().format(new Date());

    final String root;
    if (agent.isWindows()) {
      if (useUNCPaths && !checkoutDirName.startsWith("\\\\")) {
        // fix for bug #630 - p4 sync can not sync if non-UNC root
        // has reserved names in path.
        root = "\\\\.\\" + checkoutDirName;
      } else {
        root = checkoutDirName;
      }
    } else {
      root = checkoutDirName;
    }
    //if (log.isDebugEnabled()) log.debug("root: " + root);

    // make view lines
    final P4ClientViewComposer clientViewComposer = new P4ClientViewComposer();
    clientViewComposer.setClientName(clientName);
    clientViewComposer.setView(depotPath);
    clientViewComposer.setRelativeBuildDir(effectiveRelativeBuildDir);
    final String clientView = clientViewComposer.composeClientView();
    if (log.isDebugEnabled()) {
      log.debug("clientView: " + clientView);
    }

    // format template w/given params
    final Object[] arguments = {
            clientName, date, p4User, new Integer(buildID), root,
            clientView, modtimeOption, clobberOption,
            agent.getLocalHostName(), lineEnd
    };
    return new MessageFormat(IoUtils.getResourceAsString("p4_client_template.txt")).format(arguments);
  }


  /**
   * Composes p4 label spec
   */
  private String makeLabelSpec(final Agent agent, final P4Properties props, final String labelName, final String labelDepotView, final String lock) throws ValidationException, IOException, AgentFailureException {
    final P4ClientViewComposer clientViewComposer = new P4ClientViewComposer();
    clientViewComposer.setClientName(makeClientName(agent, props.getP4User(), props.getClientNameTemplate()));
    clientViewComposer.setRelativeBuildDir(getEffectiveRelativeBuildDir(props));
    if (labelDepotView == null) {
      clientViewComposer.setView(props.getP4DepotPath());
    } else {
      clientViewComposer.setView(labelDepotView);
    }

    final String labelViewToProcess = clientViewComposer.composeLabelView();

    // create template params
    final String normalizedLabelName = labelName.replace(' ', '_');
    final Object[] arguments = {normalizedLabelName, props.getP4User(), labelViewToProcess, lock};
    return new MessageFormat(IoUtils.getResourceAsString("p4_label_template.txt")).format(arguments);
  }


  /**
   * Returns a map containing P4 user names as keys and e-mails
   * as values. This method doesn't throw exceptions as it's
   * failure is not critical but it reports errors by calling to
   * ErrorManager.
   */
  public Map getUsersMap() throws CommandStoppedException, AgentFailureException {
    P4Command command = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();

      loginIfNecessary(agent);

      // load properties

      // execute P4 coomand to get a map
      command = makeP4Command(agent, false);

      // execute create label spec
      command.setExeArguments("users ");
      command.setDescription("users command");
      command.execute();

      // parse output
      final P4UsersParser usersParser = new P4UsersParser();
      usersParser.setUsersFile(command.getStdoutFile());
      usersParser.setCaseSensitiveUserNames(getP4Properties().caseSensitiveUserNames());

      // return result
      return usersParser.parse();

    } catch (final BuildException e) {
      final org.parabuild.ci.error.Error err = new org.parabuild.ci.error.Error(StringUtils.toString(e));
      err.setLogLines(e.getLogContent());
      errorManager.reportSystemError(err);
    } catch (final IOException e) {
      final org.parabuild.ci.error.Error err = new org.parabuild.ci.error.Error(StringUtils.toString(e));
      err.setDetails(e);
      errorManager.reportSystemError(err);
    } finally {
      cleanup(command);
    }
    return Collections.emptyMap(); // return empty hashmap
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
    // REVIEWME: use p4 files?
    return !getCheckoutDirectoryAwareAgent().checkoutDirIsEmpty();
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
    return "p4 sync ..." + '@' + changeList.getNumber();
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
  public int getNativeChangeList(final String nativeChangeListNumber) throws CommandStoppedException, BuildException, AgentFailureException {
    return getChanges(nativeChangeListNumber, nativeChangeListNumber, 1);
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
  public Map getShellVariables() throws IOException, AgentFailureException {
    final Agent agent = getCheckoutDirectoryAwareAgent();
    final P4Properties props = getP4Properties();
    final Map result = new HashMap(7);
    result.put(PARABUILD_P4CLIENT, makeClientName(agent, props.getP4User(), props.getClientNameTemplate()));
    result.put(PARABUILD_P4PORT, props.getP4Port());
    result.put(PARABUILD_P4USER, props.getP4User());
    addPassword(props, result, PARABUILD_P4PASSWD);
    // oprionally override factual P4 props.
    if (props.getP4VariablesOverride()) {
      result.put(P4Command.P4CLIENT, makeClientName(agent, props.getP4User(), props.getClientNameTemplate()));
      result.put(P4Command.P4PORT, props.getP4Port());
      result.put(P4Command.P4USER, props.getP4User());
      addPassword(props, result, P4Command.P4PASSWD);
    }
    return result;
  }


  /**
   * @return Map with a reference variable name as a key and variable
   *         value as value. The reference variables will be made
   *         available to the user interface.
   *         <p/>
   *         This is a default implementation that returns an
   *         empty map.
   * @see BuildScriptGenerator#addVariables(Map)
   */
  public Map getBuildRunAttributes() throws IOException, AgentFailureException {

    final Map result = new HashMap(1);
    final Agent agent = getCheckoutDirectoryAwareAgent();
    final P4Properties props = getP4Properties();
    final String p4User = props.getP4User();
    final String clientNameTemplate = props.getClientNameTemplate();
    result.put(BuildRunAttribute.REFERENCE_P4_CLIENT_NAME, makeClientName(agent, p4User, clientNameTemplate));

    return result;
  }


  /**
   * Loads P4Properties from configuration provider
   *
   * @return P4Properties
   */
  private P4Properties getP4Properties() {
    final P4Properties props = new P4Properties();
    props.load(currentSettings);
    return props;
  }


  /**
   * Returns name of the P4CLIENT. Each name is unique for a
   * given build.
   */
  private String makeClientName(final Agent agent, final String p4User, final String clientNameTemplate) throws IOException, AgentFailureException {
    try {
      return clientNameGenerator.generate(activeBuildID, agent.getLocalHostName(), p4User, clientNameTemplate);
    } catch (final BuildException e) {
      // REVIEWME: this is bad - generate throwing BuildException is bad,
      // converting to IOException is bad too. 
      throw IoUtils.createIOException(e);
    }
  }


  /**
   * Returns sync options based on existance of checkout
   * directory
   */
  private String getSyncOptions() throws IOException, AgentFailureException {
    final Agent agent = getCheckoutDirectoryAwareAgent();
    final StringBuilder syncOpts = new StringBuilder(10);
    if (agent.checkoutDirIsEmpty()) {
      if (log.isDebugEnabled()) {
        log.debug("will use forced sync");
      }
      syncOpts.append(" -f ");
    } else {
      final P4Properties p4Properties = getP4Properties();
      if (!p4Properties.updateHaveList()) {
        syncOpts.append(" -p ");
      }
    }
    return syncOpts.toString();
  }


  /**
   * Issues p4 login command if set to use p4 authentication.
   *
   * @param agent
   */
  private void loginIfNecessary(final Agent agent) throws CommandStoppedException, BuildException, AgentFailureException {
    final P4Properties props = getP4Properties();
    if (props.getAuthenticationMode() == VersionControlSystem.P4_AUTHENTICATION_MODE_VALUE_P4LOGIN) {
      InputStream is = null;
      P4Command command = null;
      try {

        // create password stream
        final StringBuilder sb = new StringBuilder(100);
        sb.append(props.getP4Password()).append("\n\n");
        is = new ByteArrayInputStream(agent.fixCRLF(sb.toString()).getBytes());

        // create p4 sync command
        command = new P4Command(agent);
        command.setP4All(props);
        command.setP4Options("-s");
        command.setInputStream(is);
        command.setClientRequired(false);
        command.setCurrentDirectory(agent.getTempDirName());

        // execute create client spec
        command.setExeArguments(" login ");
        command.setDescription("login command");
        command.execute();
      } catch (final IOException e) {
        throw new BuildException(UNEXPECTED_ERROR_WHILE_LOGGIN_IN, e, getAgentHost());
      } finally {
        IoUtils.closeHard(is);
        cleanup(command);
      }
    }
  }


  /**
   * This method addresses need to lazily update active
   * build's depot path defined by {@link
   * SourceControlSetting#P4_DEPOT_PATH} with a value stored
   * in the depot location, if it is required by the
   * configuration.
   * <p/>
   * The reason for this is that once the build with view in
   * depot is created, the {@link
   * SourceControlSetting#P4_DEPOT_PATH} is not set, so any
   * consequent calls to it will fail. We have to init it.
   * Doing this in a constructor can cause construction
   * fail.
   * <p/>
   * This needs to be done only once. The consequent
   * refreshes are handled by the reloadConfiguration()
   * method.
   */
  private void lazyInitDepotViewFromDepotIfNecessary() throws IOException, CommandStoppedException, AgentFailureException {

    // is this method already called?
    if (lazyDepotInitCalled) {
      return;
    }

    final P4Properties props = getP4Properties();

    if (props.getClientViewSource() == VersionControlSystem.P4_CLIENT_VIEW_SOURCE_VALUE_FIELD) {

      // Case # 1: Current build configuration as it is
      // known since last configuration load/reload is a
      // straight getting it from th econfiguration field.
      // We don't have to do anything.

      lazyDepotInitCalled = true;
    } else if (props.getClientViewSource() == VersionControlSystem.P4_CLIENT_VIEW_SOURCE_VALUE_DEPOT_PATH) {

      // Case # 2: Current build configuration as it is
      // known since last configuration load/reload is to
      // get deopt view from the depot.

      updateDepotViewFromDepot(props.getClientViewByDepotPath());

      lazyDepotInitCalled = true;
    } else if (props.getClientViewSource() == VersionControlSystem.P4_CLIENT_VIEW_SOURCE_VALUE_CLIENT_NAME) {

      // Case # 3: Current build configuration as it is
      // known since last configuration load/reload is to
      // get deopt view from Perforce using workspace name.

      updateDepotViewFromNamedWorkspace(props.getClientViewByWorkspaceName());

      lazyDepotInitCalled = true;
    }
  }


  private void updateDepotViewFromDepot(final String clientViewByDepotPath) throws IOException, CommandStoppedException, AgentFailureException {
    try {
      final P4Properties props = getP4Properties();
      final Agent agent = getCheckoutDirectoryAwareAgent();

      if (log.isDebugEnabled()) {
        log.debug("will get view spec from depot ");
      }

      // update Parabuild's configuration from Perforce to support build re-run

      // get client name for retrieval of the spec
      final String clientName = makeClientName(agent, props.getP4User(), props.getDepotSourceClientNameTemplate());
      final String depotPath = clientViewByDepotPath.replace('\\', '/');
      if (log.isDebugEnabled()) {
        log.debug("pre-update depotPath: " + depotPath);
      }

      // -----------------
      // - modtime?
      // - clobber?
      // -----------------
      final String checkoutDirName = agent.getTempDirName(); // checkout to temp
      final String depotSourceClientViewClientSpec = makeClientSpec(agent, activeBuildID,
              checkoutDirName, ".", clientName, depotPath, props.getP4User(),
              props.isUseUNCPaths(), props.getModtimeOption(), props.getClobberOption(),
              VersionControlSystem.P4_LINE_END_VALUE_LOCAL);
//      if (log.isDebugEnabled()) log.debug("depotSourceClientViewClientSpec: " + depotSourceClientViewClientSpec);

      // create client for retrieval of the spec
      P4Command command = null;
      try {
        command = new P4ClientCommand(agent, props, depotSourceClientViewClientSpec);
        command.setCurrentDirectory(agent.getCheckoutDirName());
        command.execute();
        cleanup(command);

        // sync
        command = new P4Command(agent);
        command.setP4Client(clientName);
        command.setP4All(props);
        command.setP4Options("-s");
        command.setCurrentDirectory(checkoutDirName);
        command.setExeArguments("sync " + STR_SPACE + "-f" + STR_SPACE + "//" + clientName + STR_TRIPPLE_DOTS);
        command.setDescription("sync command");
        command.execute();
      } finally {
        cleanup(command);
      }

      // read file
      final String rawClientView;
      final File tempFile = File.createTempFile("pre", "fix");
      try {

        final String builderFile = checkoutDirName + '/' + depotPath.substring(depotPath.indexOf('/', 3));
        if (log.isDebugEnabled()) {
          log.debug("builderFile: " + builderFile);
        }
        if (!agent.absolutePathExists(builderFile)) {
          throw new IOException("File containing client view not found: " + builderFile);
        }
        agent.readFile(builderFile, tempFile);

        // file to string
        rawClientView = IoUtils.fileToString(tempFile);

      } finally {
        tempFile.delete();
      }

      // pull depot view
      final StringBuffer depotViewSpec = new StringBuffer(rawClientView.length());
      final List l = StringUtils.multilineStringToList(rawClientView);
      for (int i = 0; i < l.size(); i++) {
        final String s = ((String) l.get(i)).trim();
        if (s.startsWith("//") || s.startsWith("+//") || s.startsWith("-//")) {
          depotViewSpec.append(s).append('\n');
        }
      }

      // update the current if necessary
      updateConfigurationDepotViewSpec(depotViewSpec);
    } catch (final ValidationException e) {
      throw IoUtils.createIOException(e);
    }
  }


  private void updateDepotViewFromNamedWorkspace(final String clientName) throws IOException, CommandStoppedException, AgentFailureException {
    final P4Properties props = getP4Properties();
    final Agent agent = getCheckoutDirectoryAwareAgent();
    final String checkoutDirName = agent.getTempDirName(); // checkout to temp

    if (log.isDebugEnabled()) {
      log.debug("Will get view spec from named workspace ");
    }

    // create client for retrieval of the spec
    P4Command command = null;
    try {
      command = new P4Command(agent);
      command.setClientRequired(false);
      command.setP4All(props);
      command.setP4Options("-s");
      command.setCurrentDirectory(checkoutDirName);
      command.setExeArguments("client -o" + STR_SPACE + clientName);
      command.setDescription("client command");
      command.execute();

      // Get raw view
      final P4ClientParser clientParser = new P4ClientParser();
      final P4Client client = clientParser.parse(command.getStdoutFile());
      final String rawClientView = client.getViewLines();

      // Normalize
      final P4ClientViewParser clientViewParser = new P4ClientViewParser(true);
      final P4ClientView clientView = clientViewParser.parse("", rawClientView);
      final List lines = clientView.getClientViewLines();
      final StringBuffer result = new StringBuffer(500);
      for (int i = 0; i < lines.size(); i++) {
        final P4ClientViewLine clientViewLine = (P4ClientViewLine) lines.get(i);
        final String depotSide = clientViewLine.getDepotSide();
        final String clientSide = clientViewLine.getClientSide();
        result.append(depotSide).append('\t').append(clientSide).append('\n');
      }

      // update the current if necessary
      updateConfigurationDepotViewSpec(result);
    } catch (final ValidationException e) {
      throw IoUtils.createIOException(e);
    } finally {
      cleanup(command);
    }
  }


  private void updateConfigurationDepotViewSpec(final StringBuffer depotViewSpec) {
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    SourceControlSetting scs = cm.getSourceControlSetting(activeBuildID, VersionControlSystem.P4_DEPOT_PATH);
    if (scs == null) {
      if (log.isDebugEnabled()) {
        log.debug("saving new depot path obtained from perforce location ");
      }
      scs = new SourceControlSetting(activeBuildID, VersionControlSystem.P4_DEPOT_PATH, depotViewSpec.toString());
      cm.save(scs);
    } else {
      if (scs.getPropertyValue().equals(depotViewSpec.toString())) {
        if (log.isDebugEnabled()) {
          log.debug("no update is needed from depot path obtained from perforce location ");
        }
      } else {
        if (log.isDebugEnabled()) {
          log.debug("updating depot path obtained from perforce location ");
        }
        scs.setPropertyValue(depotViewSpec.toString());
        cm.save(scs);
      }
    }
  }


  /**
   * Retrievs branch view by given branch view name.
   *
   * @param branchViewName to retrieve
   * @return branch view
   */
  public P4BranchView getBranchView(final String branchViewName) throws IOException, CommandStoppedException, BuildException, AgentFailureException {
    loginIfNecessary(getCheckoutDirectoryAwareAgent());
    final P4BranchViewRetriever branchViewRetriever = new P4BranchViewRetriever(getCheckoutDirectoryAwareAgent(), getP4Properties());
    return branchViewRetriever.retrieveBranchView(branchViewName);
  }


  /**
   * Finds first change list for the given client view.
   *
   * @param clientName
   * @param clientView
   * @return
   * @throws CommandStoppedException
   * @throws BuildException
   * @throws ValidationException
   */
  public Integer findFirstChangeList(final String clientName, final String clientView) throws CommandStoppedException, BuildException, ValidationException, AgentFailureException {
    if (log.isDebugEnabled()) {
      log.debug("============== finding first change list ===================");
    }
    if (log.isDebugEnabled()) {
      log.debug("clientName: " + clientName);
    }
    if (log.isDebugEnabled()) {
      log.debug("clientView: " + clientView);
    }
    P4Command command = null;
    FileInputStream fis = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();

      loginIfNecessary(agent);

      // load properties
      final P4Properties props = getP4Properties();

      // update client
      createOrUpdateClient(agent, agent.getTempDirName(), props, clientName, clientView, ".");

      // execute p4 changes command
      command = new P4Command(agent);
      command.setP4All(getP4Properties());
      command.setP4Client(clientName);
      command.setClientRequired(true);
      command.setP4Options("-s");
      command.setCurrentDirectory(agent.getTempDirName());

      command.setExeArguments("changes -s submitted  //" + clientName + STR_TRIPPLE_DOTS);
      command.setDescription("changes command");
      command.execute();

      // parse changes
      fis = new FileInputStream(command.getStdoutFile());
      final P4ChangeLogParser logParser = new P4ChangeLogParser(maxChangeListSize());
      final Collection changeListNumberChunks = logParser.parseChangesLog(fis, 1); // limits number of chunks to 1
      if (changeListNumberChunks.isEmpty()) {
        return null;
      }
      List lastChunk = null;
      for (final Iterator i = changeListNumberChunks.iterator(); i.hasNext(); ) {
        lastChunk = (List) i.next();
      }
      return !lastChunk.isEmpty() ? new Integer(Integer.parseInt((String) lastChunk.get(lastChunk.size() - 1))) : null;
    } catch (final IOException e) {
      throw new BuildException(NAME_UNEXPECTED_SYNC_ERROR, e, getAgentHost());
    } finally {
      IoUtils.closeHard(fis);
      cleanup(command);
    }
  }


  public void integrate(final ClientName clientName, final DepotView depotView, final String branchViewName, final boolean reverse, final boolean dryRun, final boolean indirectMerges, final String checkoutDirName, final String effectiveRelativeBuildDir, final P4IntegrateParserDriver driver) throws IOException, CommandStoppedException, ValidationException, BuildException, AgentFailureException {
    if (log.isDebugEnabled()) {
      log.debug("begin integrate");
    }
    if (log.isDebugEnabled()) {
      log.debug("clientName: " + clientName.getValue());
    }
    if (log.isDebugEnabled()) {
      log.debug("depotView: " + depotView.getValue());
    }
    if (log.isDebugEnabled()) {
      log.debug("branchViewName: " + branchViewName);
    }
    if (log.isDebugEnabled()) {
      log.debug("reverse: " + reverse);
    }
    if (log.isDebugEnabled()) {
      log.debug("dryRun: " + dryRun);
    }
    if (log.isDebugEnabled()) {
      log.debug("driver: " + driver);
    }

    P4Command command = null;
    try {

      final Agent agent = getCheckoutDirectoryAwareAgent();

      loginIfNecessary(agent);

      final P4Properties props = getP4Properties();

      // update client
      createOrUpdateClient(agent, checkoutDirName, props, clientName.getValue(), depotView.getValue(), effectiveRelativeBuildDir);

      // run p4 integrate
      command = new P4Command(agent);
      command.setP4All(props);
      command.setP4Client(clientName.getValue());
      command.setClientRequired(true);
      command.setP4Options("-s");
      command.setCurrentDirectory(agent.getTempDirName());
      command.setExeArguments(" integrate " + (indirectMerges ? " -i " : " ") + (dryRun ? " -n " : " ") + (reverse ? " -r " : " ") + " -b " + branchViewName);
      command.setDescription("integrate command");
      command.execute();

      // parse
      final P4IntegrateParser p4IntegrateParser = new P4IntegrateParserImpl();
      p4IntegrateParser.parse(command.getStdoutFile(), driver);
      if (log.isDebugEnabled()) {
        log.debug("end integrate");
      }
    } finally {
      if (command != null) {
        command.cleanup();
      }
    }
  }


  public void integrate(final String clientName, final String changeListNumber, final String branchViewName, final boolean reverse, final boolean dryRun, final boolean indirectMerges, final P4IntegrateParserDriver driver) throws IOException, CommandStoppedException, BuildException, ValidationException, AgentFailureException {
    if (log.isDebugEnabled()) {
      log.debug("begin integrate");
    }
    if (log.isDebugEnabled()) {
      log.debug("clientName: " + clientName);
    }
    if (log.isDebugEnabled()) {
      log.debug("branchViewName: " + branchViewName);
    }
    if (log.isDebugEnabled()) {
      log.debug("reverse: " + reverse);
    }
    if (log.isDebugEnabled()) {
      log.debug("dryRun: " + dryRun);
    }
    if (log.isDebugEnabled()) {
      log.debug("driver: " + driver);
    }

    P4Command command = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      final P4Properties properties = getP4Properties();

      // preExecute
      loginIfNecessary(agent);

      // REVIEWME: do we really need this?
      lazyInitDepotViewFromDepotIfNecessary();

      // cleate client
      final P4Properties props = getP4Properties();
      final String checkoutDirName = agent.getCheckoutDirName();
      if (log.isDebugEnabled()) {
        log.debug("checkoutDirName for integrate: " + checkoutDirName);
      }


      createOrUpdateClient(agent, checkoutDirName, props, clientName, props.getP4DepotPath(), getEffectiveRelativeBuildDir(props));

      // run p4 integrate
      command = new P4Command(agent);
      command.setP4All(properties);
      command.setP4Client(clientName);
      command.setClientRequired(true);
      command.setP4Options("-s");
      command.setCurrentDirectory(checkoutDirName);
      command.setExeArguments(" integrate " + (indirectMerges ? " -i " : " ") + (dryRun ? " -n " : " ") + (reverse ? " -r " : " ") + " -b " + branchViewName + " @" + changeListNumber + ",@" + changeListNumber);
      command.setDescription("integrate command");
      command.execute();

      // parse
      final P4IntegrateParser p4IntegrateParser = new P4IntegrateParserImpl();
      p4IntegrateParser.parse(command.getStdoutFile(), driver);
      if (log.isDebugEnabled()) {
        log.debug("end integrate");
      }
    } finally {
      if (command != null) {
        command.cleanup();
      }
    }
  }


  public void resolve(final String clientName, final ResolveMode resolveMode, final P4ResolveParser resolveParser, final P4ResolveDriver driver) throws IOException, CommandStoppedException, BuildException, ValidationException, AgentFailureException {
    if (log.isDebugEnabled()) {
      log.debug("begin resolve");
    }
    if (log.isDebugEnabled()) {
      log.debug("clientName: " + clientName);
    }

    P4Command command = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      final P4Properties properties = getP4Properties();

      // preExecute
      loginIfNecessary(agent);

      // REVIEWME: do we really need this?
      lazyInitDepotViewFromDepotIfNecessary();

      // cleate client
      final P4Properties props = getP4Properties();
      final String checkoutDirName = agent.getCheckoutDirName();
      if (log.isDebugEnabled()) {
        log.debug("checkoutDirName for resolve: " + checkoutDirName);
      }


      createOrUpdateClient(agent, checkoutDirName, props, clientName, props.getP4DepotPath(), getEffectiveRelativeBuildDir(props));


      final String resolveModeAsString;
      if (resolveMode.equals(ResolveMode.AM)) {
        resolveModeAsString = "-am";
      } else if (resolveMode.equals(ResolveMode.AT)) {
        resolveModeAsString = "-at";
      } else if (resolveMode.equals(ResolveMode.AY)) {
        resolveModeAsString = "-ay";
      } else {
        throw new IllegalArgumentException("Unknown resolve mode: " + resolveMode);
      }

      // run p4 integrate
      command = new P4Command(agent);
      command.setP4All(properties);
      command.setP4Client(clientName);
      command.setClientRequired(true);
      command.setP4Options("-s");
      command.setCurrentDirectory(checkoutDirName);
      command.setExeArguments(" resolve " + resolveModeAsString);
      command.setDescription("resolve command");
      command.execute();

      // parse
      resolveParser.parse(command.getStdoutFile(), driver);
      if (log.isDebugEnabled()) {
        log.debug("end resolve");
      }
    } finally {
      if (command != null) {
        command.cleanup();
      }
    }
  }


  public void revert(final String clientName) throws IOException, CommandStoppedException, BuildException, ValidationException, AgentFailureException {
    if (log.isDebugEnabled()) {
      log.debug("begin revert");
    }
    if (log.isDebugEnabled()) {
      log.debug("clientName: " + clientName);
    }

    P4Command command = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      final P4Properties properties = getP4Properties();

      // pre-execute
      loginIfNecessary(agent);

      // REVIEWME: do we really need this?
      lazyInitDepotViewFromDepotIfNecessary();

      // cleate client
      final P4Properties props = getP4Properties();
      final String checkoutDirName = agent.getCheckoutDirName();
      if (log.isDebugEnabled()) {
        log.debug("checkoutDirName for revert: " + checkoutDirName);
      }

      createOrUpdateClient(agent, checkoutDirName, props, clientName, props.getP4DepotPath(), getEffectiveRelativeBuildDir(props));

      // run p4 revert
      command = new P4Command(agent);
      command.setP4All(properties);
      command.setP4Client(clientName);
      command.setClientRequired(true);
      command.setP4Options("-s");
      command.setCurrentDirectory(checkoutDirName);
      command.setExeArguments(" revert " + "//" + clientName + "/...");
      command.setDescription("revert command");
      command.execute();
      if (log.isDebugEnabled()) {
        log.debug("end revert");
      }
    } finally {
      if (command != null) {
        command.cleanup();
      }
    }
  }


  /**
   * Submits default change list.
   *
   * @param clientName
   * @param description
   */
  public void submit(final String clientName, final String description) throws IOException, CommandStoppedException, BuildException, ValidationException, AgentFailureException {
    if (log.isDebugEnabled()) {
      log.debug("begin submit");
    }
    if (log.isDebugEnabled()) {
      log.debug("clientName: " + clientName);
    }

    File tempFile = null;
    P4Command command = null;
    try {
      final Agent agent = getCheckoutDirectoryAwareAgent();
      final P4Properties properties = getP4Properties();

      //
      loginIfNecessary(agent);

      // REVIEWME: do we really need this?
      lazyInitDepotViewFromDepotIfNecessary();

      // cleate client
      final P4Properties props = getP4Properties();
      final String checkoutDirName = agent.getCheckoutDirName();
      if (log.isDebugEnabled()) {
        log.debug("checkoutDirName for submit: " + checkoutDirName);
      }

      createOrUpdateClient(agent, checkoutDirName, props, clientName, props.getP4DepotPath(), getEffectiveRelativeBuildDir(props));

      // run p4 opened
      command = new P4Command(agent);
      command.setP4All(properties);
      command.setP4Client(clientName);
      command.setClientRequired(true);
      command.setP4Options("-s");
      command.setCurrentDirectory(checkoutDirName);
      command.setExeArguments(" opened ");
      command.setDescription("opened command");
      command.execute();

      // parse results of p4 opened
      final P4OpenedAccumulatingDriver driver = new P4OpenedAccumulatingDriver();
      new P4OpenedParserImpl().parse(command.getStdoutFile(), driver);
      cleanup(command);

      // generate P4 submit spec
      final Collection openedPaths = driver.getOpenedPaths();
      final StringBuilder files = new StringBuilder(openedPaths.size() * 50);
      for (final Iterator i = openedPaths.iterator(); i.hasNext(); ) {
        final Opened opened = (Opened) i.next();
        files.append('\t').append(opened.getPath()).append("\t# ").append(opened.getOperation()).append('\n');
        i.remove(); // free up memory.
      }

      // NOTE: spec can be big, so we store it in a file
      tempFile = File.createTempFile("pre", "fix");
      IoUtils.writeStringToFile(tempFile, new MessageFormat(IoUtils.getResourceAsString("p4_change_template.txt")).format(new Object[]{"new", clientName, props.getP4User(), "new", description, files.toString()}));

      // run p4 submit
      command = new P4Command(agent);
      command.setInputStream(new FileInputStream(tempFile));
      command.setP4All(properties);
      command.setP4Client(clientName);
      command.setClientRequired(true);
      command.setP4Options("-s");
      command.setCurrentDirectory(checkoutDirName);
      command.setExeArguments(" submit -i ");
      command.setDescription("submit command");
      command.execute();

    } finally {
      cleanup(command);
      IoUtils.deleteFileHard(tempFile);
      if (log.isDebugEnabled()) {
        log.debug("end submit");
      }
    }
  }


  public String toString() {
    return "P4SourceControl{" +
            "lazyDepotInitCalled=" + lazyDepotInitCalled +
            '}';
  }


  /**
   * Adds password to the environmant variable map given that the authentication mode is not p4 login.
   */
  private static void addPassword(final P4Properties props, final Map result, final String environmentName) {
    if (props.getAuthenticationMode() != VersionControlSystem.P4_AUTHENTICATION_MODE_VALUE_P4LOGIN) {
      result.put(environmentName, props.getP4Password());
    }
  }
}
