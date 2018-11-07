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
package org.parabuild.ci.build;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.archive.ArchiveManagerFactory;
import org.parabuild.ci.build.result.BuildRunSettingResolver;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.BuildVersionGenerator;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.BuildConfigCloner;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.notification.NotificationManager;
import org.parabuild.ci.object.ActiveBuildAttribute;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildConfigAttribute;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunAttribute;
import org.parabuild.ci.object.BuildRunDependence;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.BuildStepType;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.ResultConfig;
import org.parabuild.ci.object.ScheduleProperty;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.object.StartParameterType;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.relnotes.ReleaseNotesHandlerFactory;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.services.BuildFinishedSubscriber;
import org.parabuild.ci.services.BuildManager;
import org.parabuild.ci.services.BuildStartRequest;
import org.parabuild.ci.services.BuildStartRequestParameter;
import org.parabuild.ci.versioncontrol.LabelRemover;
import org.parabuild.ci.versioncontrol.SourceControl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * Build runner is a specialized thread to run a build
 *
 * @noinspection FieldNotUsedInToString, FieldAccessedSynchronizedAndUnsynchronized, StringContatenationInLoop, ThrowCaughtLocally, ProhibitedExceptionThrown, ClassExplicitlyExtendsThread, OverlyComplexBooleanExpression, InstanceVariableNamingConvention, JavaDoc
 */
public final class BuildRunner extends Thread {

  private static final Log LOG = LogFactory.getLog(BuildRunner.class);  // NOPMD

  private static final byte RUNNER_COMMAND_SLEEP = 1;
  private static final byte RUNNER_COMMAND_RUN = 2;
  private static final byte RUNNER_COMMAND_DIE = 3;
  private static final byte RUNNER_COMMAND_STOP = 4;

  // lifecycle fields
  private final Object lock = new Object();
  private byte command = RUNNER_COMMAND_SLEEP;
  private RunnerStatus runnerStatus = RunnerStatus.WAITING;

  private StopRequest stopRequest = null;

  // state objects
  private int activeBuildID = BuildConfig.UNSAVED_ID;

  private boolean labelingEnabled = true;
  private boolean forceLabeling = false;
  private boolean nextCleanCheckoutRequired = false;
  private BuildStartRequest startRequest = null;
  private Agent currentAgent = null;

  private SourceControl versionControl = null;
  private ValidatingRunner selfDispatcher = null;

  // service objects
  private final ArchiveManager archiveManager;
  private final BuildErrorManager errorManager;
  private final BuildRunnerVersionControlFactory versionControlFactory;
  private final ConfigurationManager cm;
  private final NotificationManager notificationManager;

  private final BuildRunnerStateImpl buildRunnerState;

  private final List buildFinishedSubscribers = new ArrayList(3);
  private final List buildScriptEventSubscribers = new ArrayList(3);
  private final BuildRunnerAgentFactory agentFactory;
  private static final String PARABUILD_PARENT_PARAMETER_PREFIX = "PARABUILD_PARENT_";


  /**
   * Creates build runner providing a reference to the owner.
   * Build runner will callback it's owner to report results.
   *
   * @noinspection ThisEscapedInObjectConstruction
   */
  public BuildRunner(final int activeBuildID, final NotificationManager notificationManager,
                     final BuildRunnerVersionControlFactory versionControlFactory,
                     final BuildRunnerAgentFactory agentFactory) {
    super.setName("BuildRunner:" + activeBuildID);
    super.setDaemon(true);
    ArgumentValidator.validateBuildIDInitialized(activeBuildID);
    this.activeBuildID = activeBuildID;
    this.cm = ConfigurationManager.getInstance();
    this.errorManager = new BuildErrorManager();
    this.notificationManager = notificationManager;
    this.archiveManager = ArchiveManagerFactory.getArchiveManager(activeBuildID);
    this.buildRunnerState = new BuildRunnerStateImpl(cm.getLastCompleteBuildRun(activeBuildID), cm.getLastCleanBuildRun(activeBuildID));
    this.selfDispatcher = new ValidatingRunner(this);
    this.versionControlFactory = versionControlFactory;
    this.agentFactory = agentFactory;
  }


  /**
   * Creates build runner providing a reference to the owner.
   * Build runner will callback it's owner to report results.
   *
   * @noinspection ThisEscapedInObjectConstruction
   */
  public BuildRunner(final int activeBuildID, final NotificationManager notificationManager) {
    this(activeBuildID, notificationManager, new DefaultBuildRunnerVersionControlFactory(), new DefaultBuildRunnerAgentFactory());
  }


  void runBuild() {
    runBuild(startRequest);
  }


  /**
   * Runs full build cycle. This method should not throw any
   * exception. All exceptions should be handled.
   *
   * @return an ID of the BuildRun or BuildRun.UNSAVED_ID
   */
  public int runBuild(final BuildStartRequest localStartRequest) {

    // Run in a reliable way. If an agent fails, get another one and try again.
    Agent agent = agentFactory.checkoutAgent(activeBuildID, localStartRequest);
    if (agent == null) {

      return BuildRun.UNSAVED_ID;
    }

    final int maxAttempts = agentFactory.supportsNextAgent() ? 10 : 1;
    int agentAttemptCount = 0;
    for (; agent != null && agentAttemptCount < maxAttempts; agentAttemptCount++) {

      try {

        try {

          // Check if need to re-build on error
          final boolean rebuildIfBroken = isRebuildIfBroken();

          // Build until success or out of rebuild attempts
          int buildRunID = BuildRun.UNSAVED_ID;
          final int rebuildAttempts = rebuildIfBroken ? 2 : 1;
          for (int rebuildCount = 0; rebuildCount < rebuildAttempts; rebuildCount++) {

            // Calculate if the build should run a clean checkout
            if (rebuildCount > 0) {

              localStartRequest.setCleanCheckout(true);
            }

            // Run build
            buildRunID = runBuild(localStartRequest, agent);
            final BuildRun buildRun = ConfigurationManager.getInstance().getBuildRun(buildRunID);
            if (buildRun.isSuccessful()) {

              // Success, exit rebuild cycle.
              break;
            }
          }

          return buildRunID;
        } finally {

          agentFactory.checkinAgent(agent);
        }

      } catch (final AgentFailureException e) {

        if (LOG.isDebugEnabled()) {
          LOG.debug("Build failed with " + StringUtils.toString(e) + " will retry");
        }
        agent = agentFactory.checkoutAgent(activeBuildID, localStartRequest);
      }
    }

    // Report error
    final Error error = new Error("Could not complete the build after " + agentAttemptCount + " attempts");
    error.setErrorLevel(Error.ERROR_LEVEL_FATAL);
    error.setSendEmail(true);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);

    // Return unknown result
    return BuildRun.UNSAVED_ID;
  }


  /**
   * Runs full build cycle. This method should not throw any
   * exception. All exceptions should be handled.
   *
   * @param localStartRequest a start request.
   * @param agent             an agent this build should run on.
   * @return an ID of the BuildRun or BuildRun.UNSAVED_ID
   * @throws AgentFailureException if the agent become unavailable while running the build.
   * @noinspection OverlyCoupledMethod
   */
  int runBuild(final BuildStartRequest localStartRequest, final Agent agent) throws AgentFailureException {

    // Get agent host name
    final String agentHostName = agent.getHost().getHost();


    // Create a copy of the reference in case it gets overwritten
    // in parallel
    int versionCounter = localStartRequest.versionCounter();

    // Normally build returns to the same status
    final RunnerStatus oldStatus = runnerStatus;
    if (LOG.isDebugEnabled()) {
      LOG.debug("================================ RUN BUILD =======================");
    }
//    if (LOG.isDebugEnabled()) LOG.debug("localStartRequest: " + localStartRequest);
//    if (LOG.isDebugEnabled()) LOG.debug("buildStatus: " + runnerStatus + ", localStartRequest: " + localStartRequest);
    BuildRun buildRun = null;
    String version = null;
    try {

      currentAgent = agent;
      if (LOG.isDebugEnabled()) {
        LOG.debug("Got agent: " + agent);
      }

      //
      // create build run config
      //
      int newBuildNumber = 0;
      int leadingBuildID = BuildConfig.UNSAVED_ID;
      final int buildConfigIDToUse;
      if (localStartRequest.isReRun() || localStartRequest.isVerificationRun() || localStartRequest.isPublishingRun()) {
        // this is a build re-run
        final BuildRun oldBuildRun = cm.getBuildRun(localStartRequest.getBuildRunID());
        buildConfigIDToUse = oldBuildRun.getBuildID();
        newBuildNumber = oldBuildRun.getBuildRunNumber();
      } else if (localStartRequest.isParallel()) {
        // this is a dependent parallel build run
        buildConfigIDToUse = activeBuildID;
        final BuildRun leadingBuildRun = cm.getBuildRun(localStartRequest.getBuildRunID());
        leadingBuildID = leadingBuildRun.getBuildID(); // this is used later by the build config cloner
        newBuildNumber = leadingBuildRun.getBuildRunNumber();
      } else {
        // "normal" run
        buildConfigIDToUse = activeBuildID;
      }
      final BuildConfigCloner cloner = new BuildConfigCloner(localStartRequest.sourceControlSettingsOverwriteList());
      final BuildConfig currentRunConfig = cloner.createBuildRunConfig(buildConfigIDToUse, leadingBuildID, agent.getLocalHostName());

      //
      // create build run
      //
      buildRun = new BuildRun();
      buildRun.setActiveBuildID(activeBuildID);
      buildRun.setBuildID(currentRunConfig.getBuildID());
      buildRun.setBuildName(currentRunConfig.getBuildName());
      buildRun.setLastStepRunName(""); // no steps yet
      buildRun.setReRun(localStartRequest.isReRun() || localStartRequest.isVerificationRun() || localStartRequest.isPublishingRun());
      buildRun.setStartedAt(new Date()); // set preliminary start date
      if (localStartRequest.isVerificationRun()) {
        buildRun.setType(BuildRun.TYPE_VERIFICATION_RUN);
      } else if (localStartRequest.isPublishingRun()) {
        buildRun.setType(BuildRun.TYPE_PUBLISHING_RUN);
      } else {
        buildRun.setType(BuildRun.TYPE_BUILD_RUN);
      }

      // mark as subordinate if necessary
      if (currentRunConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_PARALLEL) {
        buildRun.setDependence(BuildRun.DEPENDENCE_SUBORDINATE);
      }

      // checkout source line
      if (LOG.isDebugEnabled()) {
        LOG.debug("CHECKOUT");
      }
      runnerStatus = RunnerStatus.CHECKING_OUT;

      // get agent using this build configuration checkout directory
      if (command == RUNNER_COMMAND_STOP) {
        return BuildRun.BUILD_RESULT_STOPPED;
      }


      //
      // Save command parameters - may be used to resolve VCS settings.
      //
      // find requested parameters in configuration parameters
      for (final Iterator i = localStartRequest.parameterList().iterator(); i.hasNext(); ) {

        // Save runtime value for parameters
        boolean found = false;
        final BuildStartRequestParameter localStartRequestParameter = (BuildStartRequestParameter) i.next();
        final List configurationStartParameters = cm.getStartParameters(localStartRequest.isPublishingRun() ? StartParameterType.PUBLISH : StartParameterType.BUILD, buildRun.getBuildID());
        for (final Iterator j = configurationStartParameters.iterator(); j.hasNext(); ) {

          final StartParameter configuredManualStartParameter = (StartParameter) j.next();
          if (configuredManualStartParameter.getName().equals(localStartRequestParameter.getName())) {

            configuredManualStartParameter.setRuntimeValue(localStartRequestParameter.getValues());
            cm.saveObject(configuredManualStartParameter);
            found = true;
            break;
          }
        }

        // Create runtime start parameters for parameters inherited from the parent build
        if (!found) {

          // Inherited from the parent build
          final StartParameter startParameter = new StartParameter();
          startParameter.setBuildID(buildRun.getBuildID());
          startParameter.setDescription(localStartRequestParameter.getDescription());
          startParameter.setEnabled(true);
          startParameter.setModifiable(true);
          startParameter.setPresentation(StartParameter.PRESENTATION_SINGLE_VALUE);
          startParameter.setValue(localStartRequestParameter.getValues());
          startParameter.setRuntimeValue(localStartRequestParameter.getValues());
          startParameter.setName(localStartRequestParameter.getName());
          startParameter.setRequired(false);
          startParameter.setType(StartParameter.TYPE_BUILD);
          startParameter.setOrder(localStartRequestParameter.getOrder());
          cm.saveObject(startParameter);
        }
      }

      // make source control for this run
      versionControl = versionControlFactory.getVersionControl(currentRunConfig);
      versionControl.setAgentHost(agent.getHost());
      versionControl.reloadConfiguration();

      // Set user map
      if (currentRunConfig.getSourceControlEmail()) {
        notificationManager.setVCSUserMap(versionControl.getUsersMap());
      } else {
        notificationManager.setVCSUserMap(Collections.EMPTY_MAP);
      }

      // Empty CO dir if needed
      boolean cleanCheckout = nextCleanCheckoutRequired || localStartRequest.isCleanCheckout() || localStartRequest.isReRun();
      if (!cleanCheckout) {
        // Now check if the previous build was broken and the flag to run clean if broken is set
        final BuildRun lastCompleteBuildRun = cm.getLastCompleteBuildRun(activeBuildID);
        if (lastCompleteBuildRun != null && !lastCompleteBuildRun.successful()) {
          if (cm.isCleanCheckoutIfBroken(activeBuildID)) {
            // Check if the agent is the same as for the broken build.
            final String failedBuildAgentHost = cm.getBuildRunAttributeValue(lastCompleteBuildRun.getBuildRunID(), BuildRunAttribute.AGENT_HOST);
            if (agentHostName.equals(failedBuildAgentHost)) {
              cleanCheckout = true;
            }
          }
        }
      }

      if (cleanCheckout && !versionControl.cleanupLocalCopy()) {
        // NOTE: simeshev@parabuilci.org - 2008-06-07 - Customers reported that they can manually
        // clean up the directory. See # 1374 for details.
        Thread.sleep(60000);
        if (!versionControl.cleanupLocalCopy()) {
          errorManager.reportUndeleteableCheckoutDir(agent);
        }
      }

      if (command == RUNNER_COMMAND_STOP) {
        return BuildRun.BUILD_RESULT_STOPPED;
      }

      // init starting VCS operation
      final long startedGettingChanges = System.currentTimeMillis();

      // get change list ID
      //noinspection UnusedAssignment
      int changeListID = ChangeList.UNSAVED_ID;
      if (localStartRequest.isReRun() || localStartRequest.isVerificationRun() || localStartRequest.isPublishingRun()) {
        // re-run. we pull will-be-copied over change lists from the old build run
        changeListID = cm.getBuildRunChangeListFromBuildRunParicipants(localStartRequest.getBuildRunID()).getChangeListID();
      } else if (localStartRequest.isParallel()) {
        // this is a dependent parallel build run
        //
        // we have to create our build run participants as a
        // copy of the build run participants from the
        // leading build run
        changeListID = localStartRequest.changeListID();
      } else {
        // normal run
        changeListID = localStartRequest.changeListID();
        if (changeListID == ChangeList.UNSAVED_ID) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("LOOKING FOR LATEST CHANGES");
          }
          // try to find changes
          final int lastBuildChangeListID = cm.getLatestChangeListID(activeBuildID);
          changeListID = versionControl.getChangesSince(lastBuildChangeListID);
          if (LOG.isDebugEnabled()) {
            LOG.debug("lastBuildChangeListID: " + lastBuildChangeListID);
          }
          if (LOG.isDebugEnabled()) {
            LOG.debug("changeListID: " + changeListID);
          }
          if (changeListID == ChangeList.UNSAVED_ID) {
            // fail the build - project source line is empty
            throw new IllegalStateException("There is nothing to build - changes cannot be detected or project source line is empty.");
          }
        }
      }

      // time to get changes
      final long timeToGetChanges = System.currentTimeMillis() - startedGettingChanges;

      // set change list number to build run
      final ChangeList buildChangeList = cm.getChangeList(changeListID);
      final String buildChangeListNumber = buildChangeList.getNumber();
      buildRun.setChangeListNumber(buildChangeListNumber);
      buildRun.setSyncNote(versionControl.getSyncCommandNote(changeListID));

      // calculate and set build number
      final SystemConfigurationManager scm = SystemConfigurationManagerFactory.getManager();
      if (newBuildNumber == 0) {
        // check if a change list number should be user to as a build number
        if (scm.useChangeListNumberAsBuildnumber() && StringUtils.isValidInteger(buildChangeListNumber)) {
          newBuildNumber = Integer.parseInt(buildChangeListNumber);
        } else {
          newBuildNumber = cm.getNewBuildNumber(activeBuildID);
        }
      }
      buildRun.setBuildRunNumber(newBuildNumber);

      // set state
      buildRunnerState.setCurrentlyRunningChangeListNumber(buildChangeListNumber);
      buildRunnerState.setCurrentlyRunningBuildConfigID(currentRunConfig.getBuildID());
      buildRunnerState.setCurrentlyRunningOnBuildHost(agentHostName);
      buildRunnerState.setCurrentlyRunningBuildNumber(newBuildNumber);

      // create and store incomplete build run result
      if (LOG.isDebugEnabled()) {
        LOG.debug("STORE RUN HEADER");
      }
      cm.save(buildRun);

      // Store attributes
      cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.AGENT_HOST, agentHostName));
      cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.CHECKOUT_DIRECTORY, agent.getCheckoutDirName()));
      if (localStartRequest.isUserSet()) {
        cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.ATTR_STARTED_USER_ID, localStartRequest.userID()));
      }
      if (localStartRequest.isNoteSet()) {
        cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.ATTR_NOTE, localStartRequest.getNote()));
      }
      if (cleanCheckout) {
        cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.ATTR_CLEAN_CHECKOUT, true));
      }

      // Remember SCM-provided reference attributes
      final Map buildRunAttributes = versionControl.getBuildRunAttributes();
      for (final Iterator iterator = buildRunAttributes.entrySet().iterator(); iterator.hasNext(); ) {

        final Map.Entry entry = (Map.Entry) iterator.next();
        final String attributeName = (String) entry.getKey();
        final String attributeValue = (String) entry.getValue();
        cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), attributeName, attributeValue));
      }

      // NOTE: simeshev@parabuilci.org - 2008-03-17 - The current logic in the
      // AutomaticBuildScheduler and SourceControl is to pass
      // ChangeList.UNSAVED_ID to the build start request if the requested
      // native change list number cannot be found. The BuildRunner, in turn
      // considers that the request is for the "get-me-the latest" and runs
      // against the found latest one. For the manual start against a native
      // change list this number does not match the requested. We delay this
      // check until now so that there is a saved build run and the error is
      // reported to the build result rather than to the system error logs that
      // is not visited immediately sometimes.
      //
      // Check if the manual build run request's native change list number and
      // the found change list number match.
      final String nativeChangeListNumber = localStartRequest.getNativeChangeListNumber();
      if (!StringUtils.isBlank(nativeChangeListNumber)) {
        if (!nativeChangeListNumber.equals(buildChangeListNumber)) {
          // Mismatch, throw an exception
          throw new BuildException("Cannot find requested change list number: " + nativeChangeListNumber + ". Found latest: " + buildChangeListNumber, agent);
        }
      }

      // and participating change lists and index them
      if (localStartRequest.isReRun() || localStartRequest.isVerificationRun() || localStartRequest.isPublishingRun()) { // copy from old
        cm.copyBuildRunParticipants(localStartRequest.getBuildRunID(), buildRun.getBuildRunID());
        cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.ATTR_RE_RUN_BUILD_RUN_ID, localStartRequest.getBuildRunID()));
        cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.NEW_CHANGE_LIST_IN_THIS_BUILD, 0)); // no new
      } else if (localStartRequest.isParallel()) {
        cm.copyBuildRunParticipants(localStartRequest.getBuildRunID(), buildRun.getBuildRunID());
        cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.NEW_CHANGE_LIST_IN_THIS_BUILD, cm.getBuildRunAttributeValue(localStartRequest.getBuildRunID(), BuildRunAttribute.NEW_CHANGE_LIST_IN_THIS_BUILD, new Integer(0)))); // same as for the leader
        cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.ATTR_LEAD_BUILD_RUN_ID, localStartRequest.getBuildRunID()));
        cm.saveObject(new BuildRunDependence(localStartRequest.getBuildRunID(), buildRun.getBuildRunID()));
      } else {
        final boolean copyPreviousIfBroken = !(currentRunConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_MANUAL);
        cm.createBuildRunParticipants(buildRun, changeListID, copyPreviousIfBroken);
      }

      //
      // generate build version if necessary
      //
      if (currentRunConfig.getScheduleType() == BuildConfig.SCHEDULE_TYPE_PARALLEL) {
        // try to get version information from the leading build run ID
        final int leadingBuildRunID = localStartRequest.getBuildRunID();
        version = cm.getBuildRunAttributeValue(leadingBuildRunID, BuildRunAttribute.GENERATED_VERSION);
        versionCounter = cm.getBuildRunAttributeValue(leadingBuildRunID, BuildRunAttribute.GENERATED_VERSION_COUNTER, new Integer(1));
      } else {
        // NOTE: vimeshev - 2006-08-18 if version template is not
        // provide as a part of the build start request this could
        // be a start request from a scheduler rather than a manual
        // start request. That's why we check the build
        // configuration for template if request template is not
        // set.
        final String versionTemplate = StringUtils.isBlank(localStartRequest.versionTemplate()) ? cm.getBuildAttributeValue(buildConfigIDToUse, BuildConfigAttribute.VERSION_TEMPLATE, (String) null) : localStartRequest.versionTemplate();
        final boolean versionGenerationEnabled = cm.getBuildAttributeValue(activeBuildID, BuildConfigAttribute.ENABLE_VERSION, BuildConfigAttribute.OPTION_UNCHECKED).equals(BuildConfigAttribute.OPTION_CHECKED);
        if (versionGenerationEnabled && !StringUtils.isBlank(versionTemplate)) {
          if (versionCounter == -1) {
            // if auto mode generate would-be version counter
            if (cm.getBuildAttributeValue(activeBuildID, BuildConfigAttribute.VERSION_COUNTER_INCREMENT_MODE, new Integer(BuildConfigAttribute.VERSION_COUNTER_INCREMENT_MODE_MANUAL)).byteValue()
                    == BuildConfigAttribute.VERSION_COUNTER_INCREMENT_MODE_AUTOMATIC) {
              versionCounter = cm.getNewVersionCounter(activeBuildID, false);
            }
          }
          if (versionCounter != -1) {
            // validate no duplicates
            final BuildVersionDuplicateValidator duplicateValidator = new BuildVersionDuplicateValidator();
            duplicateValidator.validate(activeBuildID, versionTemplate, buildRun.getBuildName(), buildRun.getBuildRunNumber(), versionCounter, localStartRequest.getBuildRunID());
            final BuildVersionGenerator buildVersionGenerator = new BuildVersionGenerator();
            version = buildVersionGenerator.makeBuildVersion(versionTemplate, buildRun.getBuildName(), buildRun.getBuildRunNumber(), versionCounter).toString();
          }
        }
        // save generated values for future use by parallel build runners
        if (!StringUtils.isBlank(version) && versionCounter != -1) {
          cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.GENERATED_VERSION, version));
          cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.GENERATED_VERSION_COUNTER, versionCounter));
        }
      }

      // set currently building build run
      buildRunnerState.setCurrentlyRunningBuildRunID(buildRun.getBuildRunID());

      // check if there was a stop request
      if (command == RUNNER_COMMAND_STOP) {
        return BuildRun.BUILD_RESULT_STOPPED;
      }

      // process release notes
      if (LOG.isDebugEnabled()) {
        LOG.debug("Process release notes");
      }
      if (!localStartRequest.isVerificationRun()) {
        if (localStartRequest.isReRun()) {
          cm.copyReleaseNotes(localStartRequest.getBuildRunID(), buildRun.getBuildRunID());
        } else {
          ReleaseNotesHandlerFactory.getHandler(currentRunConfig.getBuildID()).process(buildRun);
        }
      }

      // check if there was a stop request
      if (command == RUNNER_COMMAND_STOP) {
        return BuildRun.BUILD_RESULT_STOPPED;
      }

      // NOTE: vimeshev - 2006-03-09 - store agent's versionTemplate timestamp
      // (see #847). Builder time stamp can be different from
      // build manager's versionTemplate time stamp. As an example, it will be
      // used to know if an archive item is older than the
      // current build.
      cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.ATTR_BUILDER_TIMESTAMP, agent.currentTimeMillis()));

      //
      // run build steps
      //
      if (LOG.isDebugEnabled()) {
        LOG.debug("TRAVERSE SEQUENCES");
      }

      // start parallel dependent builds (if any)
      final ParallelBuildManager parallelBuildManager = ParallelBuildManager.newInstance(buildRun);
      try {

        //
        // run all steps except the last.
        //
        final List sequences = cm.getEnabledBuildSequences(currentRunConfig.getBuildID(), localStartRequest.isPublishingRun() ? BuildStepType.PUBLISH : BuildStepType.BUILD);

        //
        // pre-process first step
        //
        final boolean firstStepIsInitializer = ((BuildSequence) sequences.get(0)).isInitializer();
        if (!firstStepIsInitializer) {
          startDependentBuilds(parallelBuildManager, localStartRequest, changeListID, buildRun);
        }

        // sync to change list and validate if build directory exist
        final long startedSyncingToChangeList = System.currentTimeMillis();
        versionControl.syncToChangeList(changeListID);
        if (!agent.fileRelativeToCheckoutDirExists(versionControl.getRelativeBuildDir())) {
          throw new BuildException("Build directory \"" + versionControl.getRelativeBuildDir()
                  + "\" was not found after checkout. This problem may be caused by " +
                  "modifications in the version control configuration " +
                  "that made previous changes recorded for this build " +
                  "non-existing for the current configuration. This makes syncing to " +
                  "such changes empty the build directory. Create a " +
                  "new build configuration for the current version control settings if this is the case.", agent);
        }

        // save time to sync
        cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.SYNC_TIME, (timeToGetChanges + (System.currentTimeMillis() - startedSyncingToChangeList)) / 1000));

        // actual date
        runnerStatus = RunnerStatus.BUILDING;
        buildRun.setStartedAt(new Date());

        // traverse build steps
        int actualStepRunCounter = 0;
        boolean lastContinued = true;
        final boolean respectPreviousResult = scm.isRespectIntermediateStepFailure();
        for (int i = 0, n = sequences.size() - 1; i < n; i++) {
          // get step to execute
          final BuildSequence sequence = (BuildSequence) sequences.get(i);

          // run step if it is enabled
          final int stepResult = runStep(respectPreviousResult, agent, buildRun, sequence, localStartRequest,
                  buildChangeList.getCreatedAt(), versionCounter, version, cleanCheckout);

          // increment the counter for actual step runs
          actualStepRunCounter++;

          // decide if we have to continue running steps even if the step was broken
          if (stepResult != BuildRun.BUILD_RESULT_SUCCESS) {
            if (stepResult == BuildRun.BUILD_RESULT_BROKEN || stepResult == BuildRun.BUILD_RESULT_TIMEOUT) {
              if (!sequence.isContinueOnFailure()) {
                lastContinued = false;
                break;
              }
            } else {
              lastContinued = false;
              break;
            }
          }

          // this will get executed only if the
          // number of steps is bigger than zero
          // AND there is an initializer
          if (i == 0 && firstStepIsInitializer) {

            // NOTE: vimeshev - 2007-02-17 - the fact that
            // we are hear means that first step was
            // initializer and it was either successful or
            // required continuing even if broken. It also
            // means that parallel builds have not been
            // started yet.
            //
            // See pre-process block above

            // start dependent builds if any
            startDependentBuilds(parallelBuildManager, localStartRequest, changeListID, buildRun);
          }
        }

        // cover-ass action to for a the strange case when
        // there is only one step and this step is an
        // initializer. this should normally not happen
        // because we do not allow initializer steps for
        // single-sequence builds. but if it slips in we
        // will cover it.
        if (firstStepIsInitializer && parallelBuildManager.startedCount() == 0) {
          startDependentBuilds(parallelBuildManager, localStartRequest, changeListID, buildRun);
        }

        // NOTE: vimeshev - 2006-12-25 - the last step is
        // handled separately to ensure proper finalization.

        // execute last step if necessary
        final int lastStepIndex = sequences.size() - 1;
        final BuildSequence lastStep = (BuildSequence) sequences.get(lastStepIndex);
        final boolean runLastStep = lastStepIndex == 0 || lastStep.isEnabled() && (buildRun.isSuccessful() || buildRun.isBroken() && (lastStep.isFinalizer() || lastContinued));
        if (runLastStep) {
          // NOTE: vimeshev - 2006-12-26 - if this is a
          // finalizer step we have to wait for parallel
          // builds to finish, but we don't have to wait
          // if this is the only step in the build
          // sequence. In any other case parallel builds
          // are waited for in the finalization of the
          // build run.
          if (lastStep.isFinalizer() && actualStepRunCounter > 0) {
            if (LOG.isDebugEnabled()) {
              LOG.debug("Waiting for dependent builds before executing lastStep: " + lastStep);
            }
            parallelBuildManager.waitForDependentBuildsToStop();
          }

          // Run finalizer/last step. We set respectPreviousResult to true because this step either a last or a finalizer
          runStep(respectPreviousResult || lastStep.isFinalizer(), agent, buildRun, lastStep, localStartRequest, buildChangeList.getCreatedAt(), versionCounter, version, cleanCheckout);
        }
      } finally {
        // NOTE: simeshev - 2006-12-26 - it is required that
        // dependent builds are called no matter what
        parallelBuildManager.waitForDependentBuildsToStop();
      }

      // check if there was a stop request
      if (command == RUNNER_COMMAND_STOP) {
        return BuildRun.BUILD_RESULT_STOPPED;
      }

      //
      // adjust successful build run to errors in parallel builds if any
      //
      if (buildRun.successful() && buildRun.getDependence() == BuildRun.DEPENDENCE_LEADER) {
        // get all including self
        final List allParallelBuildRuns = cm.getAllParallelBuildRuns(buildRun);
        final StringBuilder failedDependents = new StringBuilder(100);
        for (int i = 0; i < allParallelBuildRuns.size(); i++) {
          final BuildRun run = (BuildRun) allParallelBuildRuns.get(i);
          if (buildRun.getBuildRunID() == run.getBuildRunID()) {
            continue; // skip self
          }
          if (!run.successful()) {
            if (failedDependents.length() > 0) {
              failedDependents.append(", ");
            }
            failedDependents.append(run.getBuildName());
          }
        }
        if (failedDependents.length() > 0) {
          buildRun.setResult(BuildRun.BUILD_RESULT_BROKEN, "The following parallel builds failed: " + failedDependents);
        }
      }

      //
      // label build
      //
      if (LOG.isDebugEnabled()) {
        LOG.debug("LABEL BUILD");
      }

//      if (LOG.isDebugEnabled()) LOG.debug("labelingEnabled: " + labelingEnabled);
//      if (LOG.isDebugEnabled()) LOG.debug("forceLabeling: " + forceLabeling);
      if (!localStartRequest.isVerificationRun()) {

        // NOTE: simeshev@parabuilci.org - 03/16/2006 - when
        // re-running, automatic labeling if set for that time
        // build should be disabled. Only manual label should be
        // considered.
        if (labelingEnabled || forceLabeling) {
          final boolean manualLabelOnly = localStartRequest.isReRun();
          final LabelCreator labelCreator = new LabelCreator(versionControl, localStartRequest.label());
          labelCreator.labelBuildRun(buildRun, currentRunConfig, manualLabelOnly);
        }

        // delete old label if necessary
        final LabelRemover labelRemover = new LabelRemover(versionControl);
        labelRemover.removeOldLabels(activeBuildID);
      }

      // Forcefully fail the build because required results
      // are not found
      if (buildRun.isSuccessful()) {
        // Go over the list of result configs
        final List resultConfigs = cm.getResultConfigs(buildRun.getBuildID());
        for (int i = 0; i < resultConfigs.size(); i++) {
          final ResultConfig resultConfig = (ResultConfig) resultConfigs.get(i);
          if (resultConfig.isFailIfNotFound()) {
            // Traverse this build run steps in order to
            // resolve the paths
            boolean requiredResultFound = false;
            final List stepRuns = cm.getStepRuns(buildRun.getBuildRunID());
            for (int j = 0; j < stepRuns.size(); j++) {

              final StepRun stepRun = (StepRun) stepRuns.get(j);
              final BuildRunSettingResolver buildRunSettingResolver = new BuildRunSettingResolver(buildRun.getActiveBuildID(), agentHostName, buildRun, stepRun);
              final String resolvedPath = buildRunSettingResolver.resolve(resultConfig.getPath());

              LOG.debug("resultConfig: " + resultConfig);
              LOG.debug("resolvedPath: " + resolvedPath);
              final List buildRunResults = cm.findBuildRunResults(buildRun.getBuildRunID(), resultConfig.getDescription(), resultConfig.getType(), resolvedPath);
              LOG.debug("buildRunResults: " + buildRunResults);
              if (!buildRunResults.isEmpty()) {
                requiredResultFound = true;
                break;
              }
            }
            // Fail build if new result is not
            if (!requiredResultFound) {
              // The failure description may contain a
              // template path rather then a resolved path.
              buildRun.setResult(BuildRun.BUILD_RESULT_BROKEN, "Build was successful but the required result was missing: \"" + resultConfig.getDescription() + "\" at " + resultConfig.getPath());
            }
          }
        }
      }


      // Mark next scheduled build to skip if necessary
      if (buildRun.isSuccessful() && localStartRequest.isSkipNextScheduledBuild() && cm.getActiveBuildConfig(activeBuildID).isScheduled()) {

        final ActiveBuildAttribute skipNextScheduledBuildAttribute = cm.getActiveBuildAttribute(activeBuildID, ActiveBuildAttribute.SKIP_NEXT_SCHEDULED_BUILD);
        if (skipNextScheduledBuildAttribute == null) {

          cm.saveObject(new ActiveBuildAttribute(activeBuildID, ActiveBuildAttribute.SKIP_NEXT_SCHEDULED_BUILD, ActiveBuildAttribute.OPTION_CHECKED));
        } else {

          skipNextScheduledBuildAttribute.setPropertyValue(ActiveBuildAttribute.OPTION_CHECKED);
          cm.saveObject(skipNextScheduledBuildAttribute);
        }
      }

      // Start the dependent build if any

      if (buildRun.isSuccessful()) {
        final Integer dependentBuildID = cm.getBuildAttributeValue(buildRun.getActiveBuildID(), BuildConfigAttribute.DEPENDENT_BUILD_ID, (Integer) null);
        if (dependentBuildID != null && dependentBuildID != BuildConfig.UNSAVED_ID) {
          // Get a non-deleted build
          final ActiveBuildConfig activeBuildConfig = cm.getExistingBuildConfig(dependentBuildID);
          if (activeBuildConfig == null) {
            if (cm.getBuildAttributeValue(buildRun.getActiveBuildID(), BuildConfigAttribute.FAIL_IF_DEPENDENT_BUILD_CANNOT_BE_STARTED, BuildConfigAttribute.OPTION_UNCHECKED).equals(BuildConfigAttribute.OPTION_CHECKED)) {
              // Fail build
              buildRun.setResult(BuildRun.BUILD_RESULT_SYSTEM_ERROR, "Downstream dependence build not found");
            }
          } else {

            // Create start request for the dependent build
            final BuildStartRequestBuilder builder = new BuildStartRequestBuilder();
            final BuildStartRequest buildStartRequest = builder.makeStartRequest(dependentBuildID, ChangeList.UNSAVED_ID, null);
            buildStartRequest.setRequestType(BuildStartRequest.REQUEST_CHAINED);
            buildStartRequest.setBuildRunID(buildRun.getBuildRunID());

            // Add parent's request parameters
            final List parameterList = localStartRequest.parameterList();
            final String prefix = PARABUILD_PARENT_PARAMETER_PREFIX;
            for (int i = 0; i < parameterList.size(); i++) {
              final BuildStartRequestParameter parentParam = (BuildStartRequestParameter) parameterList.get(i);
              final BuildStartRequestParameter dependentParam = new BuildStartRequestParameter(
                      parentParam.getName().startsWith(prefix) ? parentParam.getName() : prefix + parentParam.getName(),
                      parentParam.getDescription(), parentParam.getValues(), i);
              buildStartRequest.addParameter(dependentParam);
            }

            if (LOG.isDebugEnabled()) {
              LOG.debug("starting build ID: " + dependentBuildID);
            }
            if (LOG.isDebugEnabled()) {
              LOG.debug("buildStartRequest: " + buildStartRequest);
            }

            // Send start request
            BuildManager.getInstance().startBuild(dependentBuildID, buildStartRequest);
          }
        }
      }

    } catch (final CommandStoppedException e) {

      // Report the problem
      if (buildRun.getBuildRunID() == BuildRun.UNSAVED_ID) {

        // Stopped even before build run was assigned
        final Error error = new Error(activeBuildID, "Build was stopped before build run was saved", Error.ERROR_LEVEL_ERROR);
        final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
        error.setSendEmail(false);
        error.setDetails(e);
        errorManager.reportSystemError(error);
      }

      // Mark as stopped
      markBuildRunStopped(buildRun);
    } catch (final AgentFailureException e) {

      throw e;
    } catch (final Exception e) {

      final String hostName = agent == null ? (String) null : agentHostName;

      // report error
      errorManager.reportUnexpectedBuildError(activeBuildID, hostName, e);

      // mark run result as system error if run is available
      if (buildRun != null && buildRun.getResultID() != BuildRun.BUILD_RESULT_BROKEN) {

        // set system error result
        buildRun.setResult(BuildRun.BUILD_RESULT_SYSTEM_ERROR, StringUtils.toString(e));
        notificationManager.notifyBuildStepFailed(buildRun, e);
      }
    } finally {

      finalizeBuildRun(agent, buildRun, oldStatus, localStartRequest, versionCounter, version);
    }

    // return result code
    return buildRun == null ? BuildRun.UNSAVED_ID : buildRun.getBuildRunID();
  }


  private static void startDependentBuilds(final ParallelBuildManager parallelBuildManager,
                                           final BuildStartRequest localStartRequest, final int changeListID,
                                           final BuildRun buildRun) {
    final int startedCount = parallelBuildManager.startDependentBuilds(localStartRequest, changeListID);
    if (startedCount > 0) {
      buildRun.setDependence(BuildRun.DEPENDENCE_LEADER);
    }
  }


  /**
   * This method is called from <code>finally</code> block of
   * runBuild method. This method marks build run as completed
   * and saves build run.
   * <p/>
   * Note: This method must not throw any exceptions.
   *
   * @param agent             the agent.
   * @param buildRun          the build run object.
   * @param oldStatus         previous status.
   * @param localStartRequest the build start request.
   * @param versionCounter    the version counter.
   * @param version           build version.
   */
  private void finalizeBuildRun(final Agent agent, final BuildRun buildRun, final RunnerStatus oldStatus,
                                final BuildStartRequest localStartRequest, final int versionCounter, final String version) {
    try {

      // save build results
      if (buildRun != null) {

        final boolean buildRunWasSavedBeforeFinalization = buildRun.getBuildRunID() != BuildRun.UNSAVED_ID;

        buildRun.setComplete(BuildRun.RUN_COMPLETE);
        buildRun.setFinishedAt(new Date());

        // check if changelist number was not set
        if (StringUtils.isBlank(buildRun.getChangeListNumber())) {
          buildRun.setChangeListNumber("");
        }

        // check if build number was not set
        if (buildRun.getBuildRunNumber() == 0) {
          buildRun.setBuildRunNumber(cm.getNewBuildNumber(activeBuildID));
        }

        // check if the build was stopped
        if (LOG.isDebugEnabled()) {
          LOG.debug("command: " + command);
        }
        if (LOG.isDebugEnabled()) {
          LOG.debug("buildRun.getResultID(): " + buildRun.getResultID());
        }
        if (command == RUNNER_COMMAND_STOP) {
          if (buildRun.getResultID() == BuildRun.BUILD_RESULT_UNKNOWN) {
            markBuildRunStopped(buildRun);
          }
        }

        //
        // Save before proceeding to the next step. If build run wasn't saved,
        // it will have -1 in its primary key, so the next step fill fail.
        //
        cm.save(buildRun);

        if (!buildRunWasSavedBeforeFinalization) {
          cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.AGENT_HOST, agent.getHost().getHost()));
        }

        //
        // actions specific to non-subordinate builds
        //
        if (buildRun.getDependence() != BuildRun.DEPENDENCE_SUBORDINATE) {

          // store generated version information if any
          final boolean incrementVersionCounterIfBroken = cm.getBuildAttributeValue(activeBuildID, BuildConfigAttribute.VERSION_COUNTER_INCREMENT_IF_BROKEN, BuildConfigAttribute.OPTION_UNCHECKED).equals(BuildConfigAttribute.OPTION_CHECKED);
//        if (LOG.isDebugEnabled()) LOG.debug("incrementVersionCounterIfBroken: " + incrementVersionCounterIfBroken);
          if (buildRun.successful() || incrementVersionCounterIfBroken) {
//          if (LOG.isDebugEnabled()) LOG.debug("version: " + version);
//          if (LOG.isDebugEnabled()) LOG.debug("versionCounter: " + versionCounter);
            if (!StringUtils.isBlank(version) && versionCounter != -1) {
              // save generated version, counter for record and counter sequence
              cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.VERSION, version));
              cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.VERSION_COUNTER, versionCounter));
              cm.createOrUpdateActiveBuildAttribute(activeBuildID, ActiveBuildAttribute.VERSION_COUNTER_SEQUENCE, versionCounter);
            }
          }
        }


        // we set "Last" only for normal runs.
        if (!buildRun.isReRun()) {
          if (buildRun.getComplete() == BuildRun.RUN_COMPLETE) {
            buildRunnerState.setLastCompleteBuildRun(buildRun);
            if (buildRun.successful()) {
              buildRunnerState.setLastCleanBuildRun(buildRun);
            }
          }
        }

        //
        // notify subscribers that the build has finished
        //
        for (int i = 0; i < buildFinishedSubscribers.size(); i++) {
          try {
            final BuildFinishedSubscriber buildFinishedSubscriber = (BuildFinishedSubscriber) buildFinishedSubscribers.get(i);
            buildFinishedSubscriber.buildFinished(new BuildFinishedEventImpl(buildRun));
          } catch (final Exception e) {
            errorManager.reportUnexpectedBuildFinalizationError(activeBuildID, e);
          }
        }
      }

    } catch (final Exception e) {
      errorManager.reportUnexpectedBuildFinalizationError(activeBuildID, e);

    } finally {
      // restore build status
      runnerStatus = oldStatus;

      // reset clean checkout flag
      nextCleanCheckoutRequired = localStartRequest.isReRun();

      // reset current build run ID
      buildRunnerState.setCurrentlyRunningBuildRunID(BuildRun.UNSAVED_ID);
      buildRunnerState.setCurrentlyRunningBuildConfigID(BuildConfig.UNSAVED_ID);
      buildRunnerState.setCurrentlyRunningOnBuildHost(null);

      // Reset stop request
      stopRequest = null;

      // Return the current agent
      currentAgent = null;
    }
  }


  private void markBuildRunStopped(final BuildRun buildRun) {

    // Check precondition
    if (buildRun == null) {
      return;
    }

    // Get name of the user that stopped the build
    final String defaultUserStoppedBuild = "system";
    final String userStoppedBuild;
    if (stopRequest != null && stopRequest.isUserSet() && buildRun.getBuildRunID() != BuildRun.UNSAVED_ID) {
      // Get string user name
      userStoppedBuild = SecurityManager.getInstance().getUserName(stopRequest.getUserID(), defaultUserStoppedBuild);
      // Store ID of the user that stopped the build
      cm.saveObject(new BuildRunAttribute(buildRun.getBuildRunID(), BuildRunAttribute.STOPPED_BY_USER_ID, stopRequest.getUserID()));
    } else {
      userStoppedBuild = defaultUserStoppedBuild;
    }

    // Set result
    buildRun.setResult(BuildRun.BUILD_RESULT_STOPPED, "Build was stopped by " + userStoppedBuild);
  }


  /**
   * Runs single sequence
   */
  private byte runStep(final boolean respectPreviousResult, final Agent agent, final BuildRun buildRun,
                       final BuildSequence sequence, final BuildStartRequest localStartRequest,
                       final Date changeListCreatedAt, final int versionCounter, final String version,
                       final boolean cleanCheckout) throws BuildException, IOException, AgentFailureException {

    // set step we are running - make copy
    buildRunnerState.setCurrentlyRunningStep(sequence);

    try {
      final BuildStepRunner buildStepRunner = new BuildStepRunner(agent, buildRun,
              cleanCheckout, notificationManager, archiveManager, errorManager,
              versionControl.getRelativeBuildDir(), versionControl.getShellVariables(),
              SystemConfigurationManagerFactory.getManager().getErrorLogQuoteSize());
      buildStepRunner.addSubscribers(buildScriptEventSubscribers);
      return buildStepRunner.runStep(respectPreviousResult, sequence, localStartRequest, changeListCreatedAt, versionCounter, version);
    } finally {
      // reset current step statuses
      buildRunnerState.setCurrentlyRunningStep(null);
    }
  }


  /**
   * Starts build
   *
   * @param request
   */
  public void requestBuildStop(final StopRequest request) {
    synchronized (lock) {
      stopRequest = request;
      command = RUNNER_COMMAND_STOP;
      runnerStatus = RunnerStatus.STOPPING;
      interrupt();
    }
  }


  /**
   * Requests exist
   */
  public void requestShutdown() {
    synchronized (lock) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("requesting runner shutdown");
      }
      command = RUNNER_COMMAND_DIE;
      interrupt();
    }
  }


  /**
   * Returns build runner status.
   *
   * @return build runner status
   * @see RunnerStatus
   */
  public RunnerStatus getStatus() {
    return runnerStatus;
  }


  /**
   * Enables notification
   *
   * @param enable equal <code>true</true> enables
   *               sending notification messages. Otherwise - disables.
   */
  public void enableNotification(final boolean enable) {
    notificationManager.enableNotification(enable);
  }


  /**
   * Enables labeling of a successful build if parameter is true
   */
  public void setLabelingEnabled(final boolean labelingEnabled) {
    this.labelingEnabled = labelingEnabled;
  }


  /**
   * Forces creating label even if the build was not successful
   */
  public void setForceLabeling(final boolean forceLabeling) {
    this.forceLabeling = forceLabeling;
  }


  /**
   * Main thread execution method
   */
  public void run() {

    try {

      // main cycle
      while (command != RUNNER_COMMAND_DIE) {

        // wait for command
        while (command == RUNNER_COMMAND_SLEEP) {

          synchronized (lock) {

            try {

              lock.wait(1000L); // wake up every 0.5 seconds
            } catch (final InterruptedException ignored) {

              // NOTE: simeshev@parabuilci.org - 2011-12-06 - This exception is ignored because the only way
              // it may happen is when the runner is stopped or killed. See PARABUILD-1620 for more information.
            }
          }
        }

        // process command
        if (command == RUNNER_COMMAND_RUN) {

          try {

            selfDispatcher.runBuild();
            if (LOG.isDebugEnabled()) {
              LOG.debug("runner status at end: " + runnerStatus);
            }
          } catch (final Exception e) {

            // catch just is case something manages to escape
            final Error error = new Error(activeBuildID, "Tried to escape but was caught: " + e.toString(), Error.ERROR_LEVEL_ERROR);
            error.setDetails(e);
            ErrorManagerFactory.getErrorManager().reportSystemError(error);
          }

          // after exec, check die command that may have arrived
          // while we were running the build.
          if (command == RUNNER_COMMAND_DIE) {

            return;
          }
          // sill alive, mark our status as sleeping
          command = RUNNER_COMMAND_SLEEP;
          runnerStatus = RunnerStatus.WAITING;

        } else if (command == RUNNER_COMMAND_STOP) {

          command = RUNNER_COMMAND_SLEEP;
          runnerStatus = RunnerStatus.WAITING;

        } else if (command == RUNNER_COMMAND_DIE) {

          return;
        } else {

          throw new IllegalStateException("Unknown command: " + command);
        }
      }
    } catch (final Exception e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Exception at run cycle", e);
      }
      ErrorManagerFactory.getErrorManager().reportSystemError(new org.parabuild.ci.error.Error(activeBuildID, "", org.parabuild.ci.error.Error.ERROR_SUSBSYSTEM_BUILD, e));
    } catch (final java.lang.Error e) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Error at run cycle", e);
      }
      ErrorManagerFactory.getErrorManager().reportSystemError(new org.parabuild.ci.error.Error(activeBuildID, "", org.parabuild.ci.error.Error.ERROR_SUSBSYSTEM_BUILD, e));
      throw e;
    } catch (final Throwable e) { // NOPMD
      if (LOG.isDebugEnabled()) {
        LOG.debug("Error at run cycle", e);
      }
      ErrorManagerFactory.getErrorManager().reportSystemError(new org.parabuild.ci.error.Error(activeBuildID, "", org.parabuild.ci.error.Error.ERROR_SUSBSYSTEM_BUILD, e));
    } finally {
      if (LOG.isDebugEnabled()) {
        LOG.debug("runner was shutdown");
      }
    }
  }


  /**
   * @return runner state
   */
  public BuildRunnerState getRunnerState() {
    return buildRunnerState;
  }


  /**
   * Notifies this build about request to start. If change lists
   * equals ChangeList.UNSAVED_ID, build will attempt to find a
   * latest change to build on using associated with it
   * SourceControl.
   *
   * @noinspection ParameterHidesMemberVariable
   * @see SourceControl#getChangesSince
   * @see ConfigurationManager#getLatestChangeListID
   */
  public void requestBuildStart(final BuildStartRequest startRequest) {

    synchronized (lock) {
      command = RUNNER_COMMAND_RUN;
      this.startRequest = startRequest;
      lock.notifyAll();
    }
  }


  /**
   * Adds a subscriber to the build finished event.
   *
   * @param buildFinishedSubscriber to add
   * @see BuildFinishedSubscriber
   */
  public void addSubscriber(final BuildFinishedSubscriber buildFinishedSubscriber) {
    buildFinishedSubscribers.add(buildFinishedSubscriber);
  }


  /**
   */
  public void addSubscriber(final BuildScriptEventSubscriber buildScriptEventSubscriber) {
    buildScriptEventSubscribers.add(buildScriptEventSubscriber);
  }


  /**
   * Return currently running agent or null if not running.
   *
   * @return currently running agent or null if not running.
   */
  public Agent getCurrentAgent() {
    return currentAgent;
  }


  /**
   * Returns true if the build must re-run if it was broken.
   *
   * @return true if the build must re-run if it was broken.
   */
  private boolean isRebuildIfBroken() {

    return ConfigurationManager.getInstance().getScheduleSettingValue(activeBuildID,
            ScheduleProperty.AUTO_REBUILD_IF_BROKEN, ScheduleProperty.OPTION_UNCHECKED)
            .equals(ScheduleProperty.OPTION_CHECKED);
  }


  public String toString() {
    return "BuildRunner{" +
            "lock=" + lock +
            ", command=" + command +
            ", runnerStatus=" + runnerStatus +
            ", stopRequest=" + stopRequest +
            ", activeBuildID=" + activeBuildID +
            ", labelingEnabled=" + labelingEnabled +
            ", forceLabeling=" + forceLabeling +
            ", nextCleanCheckoutRequired=" + nextCleanCheckoutRequired +
            ", startRequest=" + startRequest +
            ", currentAgent=" + currentAgent +
            ", versionControl=" + versionControl +
            ", archiveManager=" + archiveManager +
            ", errorManager=" + errorManager +
            ", versionControlFactory=" + versionControlFactory +
            ", cm=" + cm +
            ", notificationManager=" + notificationManager +
            ", buildRunnerState=" + buildRunnerState +
            ", buildFinishedSubscribers=" + buildFinishedSubscribers +
            ", buildScriptEventSubscribers=" + buildScriptEventSubscribers +
            ", agentFactory=" + agentFactory +
            "} " + super.toString();
  }
}
