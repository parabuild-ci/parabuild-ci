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
import org.parabuild.ci.build.log.LogHandler;
import org.parabuild.ci.build.log.LogHandlerFactory;
import org.parabuild.ci.build.result.ResultHandler;
import org.parabuild.ci.build.result.ResultHandlerFactory;
import org.parabuild.ci.common.CommandStoppedException;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ThreadUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.notification.NotificationManager;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildSequence;
import org.parabuild.ci.object.ChangeList;
import org.parabuild.ci.object.Project;
import org.parabuild.ci.object.StartParameter;
import org.parabuild.ci.object.StartParameterType;
import org.parabuild.ci.object.StepLog;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.object.StepRunAttribute;
import org.parabuild.ci.project.ProjectManager;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.security.SecurityManager;
import org.parabuild.ci.services.BuildStartRequest;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 */
final class BuildStepRunner {

  private static final Log LOG = LogFactory.getLog(BuildStepRunner.class); // NOPMD

  private final ArchiveManager archiveManager;
  private final boolean nextCleanCheckoutRequired;
  private final Agent agent;
  private final BuildErrorManager errorManager;
  private final BuildRun buildRun;
  private final ConfigurationManager cm = ConfigurationManager.getInstance();
  private final int activeBuildID;
  private final int errorLogQuoteSize;
  private final List buildScriptEventSubscribers = new ArrayList(3);
  private final Map shellVariables;
  private final NotificationManager notificationManager;
  private final String relativeBuildDir;


  BuildStepRunner(final Agent agent, final BuildRun buildRun, final boolean nextCleanCheckoutRequired,
                  final NotificationManager notificationManager, final ArchiveManager archiveManager,
                  final BuildErrorManager errorManager, final String relativeBuildDir,
                  final Map shellVariables, final int errorLogQuoteSize) {
    this.activeBuildID = buildRun.getActiveBuildID();
    this.archiveManager = archiveManager;
    this.agent = agent;
    this.buildRun = buildRun;
    this.errorLogQuoteSize = errorLogQuoteSize;
    this.errorManager = errorManager;
    this.nextCleanCheckoutRequired = nextCleanCheckoutRequired;
    this.notificationManager = notificationManager;
    this.relativeBuildDir = relativeBuildDir;
    this.shellVariables = new HashMap(shellVariables);
  }


  /**
   * Adds a {@link BuildScriptEventSubscriber} to the build
   * script events.
   *
   * @param subscribers {@link List} of subscribers
   */
  public void addSubscribers(final List subscribers) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("buildScriptEventSubscribers: " + subscribers);
    }
    this.buildScriptEventSubscribers.addAll(subscribers);
  }


  /**
   * Runs build step
   *
   * @param respectPreviousResult if true, will not change current result set in the build run.
   * @param sequence
   * @param localStartRequest
   * @param changeListCreatedAt
   * @param versionCounter
   * @param version
   * @return
   * @throws AgentFailureException
   */
  public byte runStep(final boolean respectPreviousResult, final BuildSequence sequence,
                      final BuildStartRequest localStartRequest, final Date changeListCreatedAt,
                      final int versionCounter, final String version
  ) throws AgentFailureException {

    byte resultID = BuildRun.BUILD_RESULT_UNKNOWN;
    String resultDescription = "";
    Date stepStartedAt = new Date(); // Default start time
    Date stepFinishedAt = null;
    String mergedFileName = null;
    String windowFileName = null;
    String stepScriptFileName = null;
    File stepLogWindowFile = null;
    long stepBuilderTimeStamp = stepStartedAt.getTime(); // Default agent time stamp

    try {

      // send notification that projectBuild [sequence] started
      notificationManager.notifyBuildStepStarted(buildRun, sequence);

      // get active projectBuild
      final ProjectManager pm = ProjectManager.getInstance();
      final Project project = pm.getProject(pm.getProjectBuild(buildRun.getActiveBuildID()).getProjectID());

      // make step scripts
      if (LOG.isDebugEnabled()) {
        LOG.debug("MAKE STEP SCRIPT");
      }
      final BuildScriptGenerator scriptGenerator = BuildScriptGeneratorFactory.makeScriptGenerator(agent);
      scriptGenerator.setBuildID(buildRun.getActiveBuildID());
      scriptGenerator.setBuildName(buildRun.getBuildName());
      scriptGenerator.setBuildNumber(buildRun.getBuildRunNumber());
      scriptGenerator.setBuildRunID(buildRun.getBuildRunID());
      scriptGenerator.setChangeListDate(changeListCreatedAt);
      scriptGenerator.setChangeListNumber(buildRun.getChangeListNumber());
      scriptGenerator.setCleanCheckout(nextCleanCheckoutRequired);
      scriptGenerator.setProjectID(project.getID());
      scriptGenerator.setProjectName(project.getName());
      scriptGenerator.setRelativeBuildDir(relativeBuildDir);
      scriptGenerator.setStepName(sequence.getStepName());
      scriptGenerator.setBuildStartedAt(buildRun.getStartedAt());

      scriptGenerator.addVariables(shellVariables);
      scriptGenerator.setBuildStartedByUser(SecurityManager.getInstance().getUserName(localStartRequest.userID(), "system"));
      scriptGenerator.setSequenceNumber(cm.getSequenceNumber(buildRun.getActiveBuildID()));
      if (LOG.isDebugEnabled()) {
        LOG.debug("version: " + version);
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("versionCounter: " + versionCounter);
      }
      scriptGenerator.setVersion(version);
      scriptGenerator.setVersionCounter(versionCounter);


      //
      // Add parameters created at build run start
      //
      final List startParameters = cm.getStartParameters(localStartRequest.isPublishingRun()
              ? StartParameterType.PUBLISH : StartParameterType.BUILD, buildRun.getBuildID());
      for (final Iterator iterator = startParameters.iterator(); iterator.hasNext(); ) {
        final StartParameter startParameter = (StartParameter) iterator.next();
        final String value = startParameter.getRuntimeValue();
        if (value != null) {
          scriptGenerator.addVariable(startParameter.getName(), value);
        }
      }


      if (localStartRequest.isParallel()) {
        final BuildRun leaderBuildRun = cm.getBuildRun(localStartRequest.getBuildRunID());
        scriptGenerator.setLeadingBuildRunID(localStartRequest.getBuildRunID());
        scriptGenerator.setLeadingBuildName(leaderBuildRun.getBuildName());
        scriptGenerator.setLeadingBuildID(leaderBuildRun.getActiveBuildID());
      }

      if (localStartRequest.isChained()) {
        final BuildRun chainBuildRun = cm.getBuildRun(localStartRequest.getBuildRunID());
        scriptGenerator.setUpstreamBuildRunID(localStartRequest.getBuildRunID());
        scriptGenerator.setUpstreamBuildName(chainBuildRun.getBuildName());
        scriptGenerator.setUpstreamBuildID(chainBuildRun.getActiveBuildID());
      }

      // Set variables for previous projectBuild run
      setLastBuildVariables(scriptGenerator);

      // Set variables for last good projectBuild run
      setLastGoodBuildVariables(scriptGenerator);

      // Set previous step results
      setPreviousStepRuns(scriptGenerator);

      stepScriptFileName = scriptGenerator.generateScriptFile(sequence);

      mergedFileName = archiveManager.makeNewStepLogFileName(sequence);
      windowFileName = archiveManager.makeNewStepLogFileName(sequence);
      final File seqMergedFile = archiveManager.fileNameToLogPath(mergedFileName);
      stepLogWindowFile = archiveManager.fileNameToLogPath(windowFileName);

      // check if there was a stop request
      ThreadUtils.checkIfInterrupted();

      // Execute sequence script runner
      if (LOG.isDebugEnabled()) {
        LOG.debug("RUN STEP SCRIPT");
      }
      final BuildScriptRunner scriptRunner = BuildScriptRunnerFactory.makeScriptRunner(agent);
      final BuildTimeoutCallback timeoutCallback = new BuildTimeoutCallback(notificationManager);
      timeoutCallback.setScriptRunner(scriptRunner);
      timeoutCallback.setBuildRun(buildRun);
      timeoutCallback.setSequence(sequence);
      scriptRunner.setTimeoutCallback(timeoutCallback);
      scriptRunner.setMergedFile(seqMergedFile);
      scriptRunner.setTimeoutSecs(sequence.getTimeoutMins() * 60);
      scriptRunner.addTimeoutMatch(stepScriptFileName);
      scriptRunner.addScriptEventListeners(buildScriptEventSubscribers);

      // check if there was a stop request
      ThreadUtils.checkIfInterrupted();

      // run step script
      stepStartedAt = new Date();
      stepBuilderTimeStamp = agent.currentTimeMillis();
      final int returnCode = scriptRunner.executeBuildScript(stepScriptFileName);
      stepFinishedAt = new Date();

      // check if there was a stop request
      ThreadUtils.checkIfInterrupted();

      // analyze main log
      if (LOG.isDebugEnabled()) {
        LOG.debug("ANALYZE LOGS");
      }
      final BuildLogAnalyzer logAnalyzer = new BuildLogAnalyzer(sequence);
      logAnalyzer.setErrorWindowSize(errorLogQuoteSize);
      final BuildLogAnalyzer.Result result = logAnalyzer.analyze(seqMergedFile);
      logAnalyzer.writeLogWindow(stepLogWindowFile);

      // getErrorQuoteSize sequence result
      if (scriptRunner.isTimedOut()) {
        // process time out
        resultID = BuildRun.BUILD_RESULT_TIMEOUT;
        resultDescription = "Timed out after " + sequence.getTimeoutMins() + " minutes and was stopped.";
      } else {
        if (LOG.isDebugEnabled()) {
          LOG.debug("ANALYZE RESULT");
        }
        // process return code if necessary
        if (LOG.isDebugEnabled()) {
          LOG.debug("return code: " + returnCode);
        }
        if (sequence.getRespectErrorCode()) {
          if (returnCode == 0) {
            if (result.isPatternFound()) {
              resultID = result.getResult();
              resultDescription = result.getResultDescription();
            } else {
              // nothing to check for failure - success
              resultID = BuildRun.BUILD_RESULT_SUCCESS;
              resultDescription = "Step returned result code 0";
            }
          } else {
            // non-zero ret code - broken
            resultID = BuildRun.BUILD_RESULT_BROKEN;
            resultDescription = "Script returned non-zero result code \"" + returnCode + '\"';
          }
        } else {
          // do normal check
          resultID = result.getResult();
          resultDescription = result.getResultDescription();
        }
      }
      if (LOG.isDebugEnabled()) {
        LOG.debug("resultID: " + resultID);
      }
    } catch (final CommandStoppedException e) {
      resultID = BuildRun.BUILD_RESULT_STOPPED;
      return resultID;
    } catch (final Exception e) {

      // Get host name
      final String hostName = agent == null ? null : agent.getHost().getHost();

      // report error
      errorManager.reportUnexpectedBuildStepError(activeBuildID, hostName, sequence, e);
      // mark result as error
      resultID = BuildRun.BUILD_RESULT_SYSTEM_ERROR;
      resultDescription = StringUtils.toString(e);
    } finally {
      // finalize step
      try {

        // cleanup step script file
        agent.deleteFileHard(stepScriptFileName);

        if (stepFinishedAt == null) {
          stepFinishedAt = new Date();
        }

        // update build result with current step result
        final String adjustedResultDescription = adjustStepResultDescriptionToRunnerState(resultID, resultDescription);
        buildRun.setResult(respectPreviousResult, resultID, adjustedResultDescription);
        buildRun.setLastStepRunName(sequence.getStepName());
        cm.save(buildRun);

        // store sequence result
        if (LOG.isDebugEnabled()) {
          LOG.debug("STORE STEP RESULT");
        }
        final StepRun stepRun = new StepRun();
        stepRun.setBuildRunID(buildRun.getBuildRunID());
        stepRun.setName(sequence.getStepName());
        stepRun.setStartedAt(stepStartedAt);
        stepRun.setFinishedAt(stepFinishedAt);
        stepRun.setResultID(resultID);
        stepRun.setResultDescription(adjustedResultDescription);
        cm.save(stepRun);

        // store step attributes
        cm.saveObject(new StepRunAttribute(stepRun.getID(), StepRunAttribute.ATTR_BUILDER_TIMESTAMP, stepBuilderTimeStamp));

        // store console logs
        if (LOG.isDebugEnabled()) {
          LOG.debug("STORE STEP LOGS");
        }
        saveStepLog(mergedFileName, stepRun.getID(), StepLog.TYPE_MAIN, stepRun.getName() + " log");
        if (resultID == BuildRun.BUILD_RESULT_SUCCESS || resultID == BuildRun.BUILD_RESULT_STOPPED) {

          // We don't need log window in case of success or stop
          IoUtils.deleteFileHard(stepLogWindowFile);
        } else {

          // Record error lines 
          if (stepLogWindowFile != null && stepLogWindowFile.length() > 0) {
            saveStepLog(windowFileName, stepRun.getID(), StepLog.TYPE_WINDOW, "Error Lines");
          }
        }

        // store custom logs if any
        final LogHandler logHandler = LogHandlerFactory.makeLogHandler(agent, stepRun.getID());
        logHandler.process();

        // store build results if any
        final ResultHandler resultHandler = ResultHandlerFactory.makeResultHandler(agent, stepRun.getID());
        resultHandler.setPinResult(localStartRequest.isPinResult());
        resultHandler.process();

        // send notification
        if (LOG.isDebugEnabled()) {
          LOG.debug("SEND NOTIFICATION");
        }
        notificationManager.notifyBuildStepFinished(stepRun);
      } finally {
        if (LOG.isDebugEnabled()) {
          LOG.debug("SEQUENCE COMPLETE");
        }
      }
    }
    return resultID;
  }


  private void setPreviousStepRuns(final BuildScriptGenerator scriptGenerator) {

    final int buildRunID = buildRun.getBuildRunID();
    final List stepRuns = ConfigurationManager.getInstance().getStepRuns(buildRunID);
    scriptGenerator.setPreviousStepRuns(stepRuns);
  }


  private void setLastBuildVariables(final BuildScriptGenerator scriptGenerator) {
    final BuildRun previousBuildRun = cm.getPreviousBuildRun(buildRun);
    if (previousBuildRun != null) {
      final String previousChangeListNumber = previousBuildRun.getChangeListNumber();
      final ChangeList previousChangeList = cm.getChangeList(previousBuildRun.getBuildRunID(), previousChangeListNumber);
      if (previousChangeList == null) {
        // # 1373 - Do not report the problem if the build was stopped. It is possible that it
        //   was stopped before we could acquire the change list from VCS.
        if (previousBuildRun.getResultID() != BuildRun.BUILD_RESULT_STOPPED
                && previousBuildRun.getResultID() != BuildRun.BUILD_RESULT_SYSTEM_ERROR) {
          // NOTE: simeshev@parabuilci.org - 2007-06-17 -
          // notify about this strange condition, see bug
          // #1186 for details.
          BuildErrorManager.reportCannotFindChangeList(previousBuildRun, previousChangeListNumber);
        }
      } else {
        scriptGenerator.setPreviousChangeListNumber(previousChangeListNumber);
        scriptGenerator.setPreviousChangeListDate(previousChangeList.getCreatedAt());
      }
    }
  }


  private void setLastGoodBuildVariables(final BuildScriptGenerator scriptGenerator) {
    final BuildRun lastCleanBuildRun = cm.getLastCleanBuildRun(activeBuildID);
    if (lastCleanBuildRun != null) {
      scriptGenerator.setLastGoodBuildDate(lastCleanBuildRun.getStartedAt());
      scriptGenerator.setLastGoodBuildNumber(lastCleanBuildRun.getBuildRunNumber());
      final ChangeList lastGoodChangeList = cm.getChangeList(lastCleanBuildRun.getBuildRunID(), lastCleanBuildRun.getChangeListNumber());
      if (lastGoodChangeList != null) {
        scriptGenerator.setLastGoodChangeListNumber(lastGoodChangeList.getNumber());
        scriptGenerator.setLastGoodChangeListDate(lastGoodChangeList.getCreatedAt());
      }
    }
  }


  private static String adjustStepResultDescriptionToRunnerState(final byte resultID, final String resultDescription) {
    if (resultID == BuildRun.BUILD_RESULT_UNKNOWN || resultID == BuildRun.BUILD_RESULT_STOPPED) {
      return "Build stopped by a build administrator request";
    }
    return resultDescription;
  }


  /**
   * Stores information about log location in the database. The
   * location information is stored only if size of the log is
   * not zero.
   */
  private void saveStepLog(final String logFileName, final int stepRunID, final byte logType, final String description) {
    if (logFileName == null || logFileName.length() <= 0) {
      return;
    }
    if (LOG.isDebugEnabled()) {
      LOG.debug("logFileName: " + logFileName);
    }
    final StepLog stepLog = new StepLog();
    stepLog.setStepRunID(stepRunID);
    stepLog.setArchiveFileName(logFileName);
    stepLog.setPath("");
    stepLog.setDescription(description);
    stepLog.setFound((byte) 1);
    stepLog.setType(logType);
    stepLog.setPathType(StepLog.PATH_TYPE_TEXT_FILE);
    cm.save(stepLog);
  }


  public String toString() {
    return "BuildStepRunner{" +
            "archiveManager=" + archiveManager +
            ", nextCleanCheckoutRequired=" + nextCleanCheckoutRequired +
            ", agent=" + agent +
            ", errorManager=" + errorManager +
            ", buildRun=" + buildRun +
            ", configManager=" + cm +
            ", activeBuildID=" + activeBuildID +
            ", errorLogQuoteSize=" + errorLogQuoteSize +
            ", buildScriptEventSubscribers=" + buildScriptEventSubscribers +
            ", shellVariables=" + shellVariables +
            ", notificationManager=" + notificationManager +
            ", relativeBuildDir='" + relativeBuildDir + '\'' +
            '}';
  }
}
