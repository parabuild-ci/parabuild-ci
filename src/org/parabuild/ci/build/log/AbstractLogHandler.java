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
package org.parabuild.ci.build.log;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.archive.ArchiveManagerFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.build.result.BuildRunSettingResolver;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.LogConfigProperty;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.search.SearchManager;

import java.io.IOException;
import java.util.List;

/**
 * AbstractCustomLogHandler implementes GoF Strategy patters by
 * delivering common functionality and delegating concrete log
 * type processing to implementing classes. Implementing classes
 * should implement abstract processLog method.
 *
 * @see #processLog
 */
public abstract class AbstractLogHandler implements LogHandler {

  private static final Log log = LogFactory.getLog(AbstractLogHandler.class);

  protected final ArchiveManager archiveManager;
  protected final Agent agent;
  protected final ConfigurationManager cm = ConfigurationManager.getInstance();
  protected final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
  protected final int buildRunID;
  protected final int stepRunID;
  protected final LogConfig logConfig;
  protected final SearchManager searchManager = SearchManager.getInstance();
  protected final String fullyQualifiedResultPath;
  protected final String resolvedResultPath;
  protected final int activeBuildID;


  /**
   * Constructor
   *
   * @param agent     for which this handler is created
   * @param stepRunID step ID that is to be processed
   * @throws IllegalArgumentException if the logConfig
   *                                  contains invalid log path template.
   */
  protected AbstractLogHandler(final Agent agent, final BuildRunConfig buildRunConfig,
                               final String projectHome, final LogConfig logConfig, final int stepRunID) {

    try {
      // validate
      if (stepRunID == StepRun.UNSAVED_ID) {
        throw new IllegalArgumentException("Step ID is invalid");
      }
      ArgumentValidator.validateBuildIDInitialized(agent.getActiveBuildID());
      ArgumentValidator.validateBuildIDInitialized(buildRunConfig.getBuildID());

      // set
      this.agent = agent;
      this.logConfig = logConfig;
      this.stepRunID = stepRunID;
      this.archiveManager = ArchiveManagerFactory.getArchiveManager(buildRunConfig.getActiveBuildID());
      final StepRun stepRun = cm.getStepRun(stepRunID);
      this.buildRunID = stepRun.getBuildRunID();
      final BuildRun buildRun = ConfigurationManager.getInstance().getBuildRun(stepRun.getBuildRunID());
      final String stepRunName = stepRun.getName();
      activeBuildID = buildRun.getActiveBuildID();
      final BuildRunSettingResolver buildRunSettingResolver = new BuildRunSettingResolver(activeBuildID, agent.getHost().getHost(), buildRun, stepRunName);
      this.resolvedResultPath = buildRunSettingResolver.resolve(logConfig.getPath());
      this.fullyQualifiedResultPath = projectHome + '/' + resolvedResultPath;
      // paranoid validation - custom log handler always works
      // using build run config
      if (buildRunConfig.getBuildID() == buildRunConfig.getActiveBuildID()) {
        throw new IllegalStateException("Build configuration with ID " + buildRunConfig.getBuildID()
                + " is not a build run configuration.");
      }
    } catch (final ValidationException e) {
      throw new IllegalArgumentException(StringUtils.toString(e));
    }
  }


  /**
   * Finds logs, moves them to archive and adjusts database. This
   * method should not throw any exceptions.
   */
  public final void process() {

    // vlaidate we are called with the correct log type
    if (logConfig.getType() != logType()) {
      throw new IllegalArgumentException("Log type code is invalid for this log handler: " + logConfig.getType());
    }

    try {
      // TODELETE: when fixed the problem that our PMD log doesn't show.
//      if (log.isDebugEnabled()) log.debug("checking path: " + logPath);

      // form resulting log path and check if it exists
      if (!agent.absolutePathExists(fullyQualifiedResultPath)) {
        return;
      }

      // TODELETE: when fixed the problem that our PMD log doesn't show.
//      if (log.isDebugEnabled()) log.debug("checking scope: " + agent.getCheckoutDirHome());

      // check if path is not outside of the allowed scope
      // REVIEWME: simeshev@parabuilci.org -> this actually does not work: IoUtils.isFileUnder
      if (!IoUtils.isFileUnder(fullyQualifiedResultPath, agent.getCheckoutDirHome())) {
        if (log.isDebugEnabled()) {
          log.debug("checking scope failed: " + fullyQualifiedResultPath);
        }
        reportLogIsOutOfScope(fullyQualifiedResultPath, logConfig);
        return;
      }

      // find if we need to check if the result was already archived
      final StepRun stepRun = cm.getStepRun(stepRunID); // REVIREWME: do we really need this operaton? may be we should just store StepRun object in AbstractResultHandler?
      final long builderTimeStamp = cm.getBuilderTimeStamp(stepRun);
      final List archivedLogs = getBuildRunLogs(stepRun.getBuildRunID());
//      if (log.isDebugEnabled()) log.debug("============== begin =========================");
//      if (log.isDebugEnabled()) log.debug("stepRun.getBuildRunID(): " + buildRunID);
//      if (log.isDebugEnabled()) log.debug("logType(): " + logType());
//      if (log.isDebugEnabled()) log.debug("logConfig.getPath(): " + logConfig.getPath());
//      if (log.isDebugEnabled()) log.debug("archivedLogs.size(): " + archivedLogs.size());
//      if (log.isDebugEnabled()) log.debug("logConfig: " + logConfig);
//      if (log.isDebugEnabled()) log.debug("archivedLogs: " + archivedLogs);
      if (isLogAlreadyArchived(archivedLogs, builderTimeStamp)) {
        return;
      }

      // call strategy method that takes care about processing of a concrete log type
      processLog();
      if (log.isDebugEnabled()) {
        log.debug("============== end =========================");
      }
    } catch (final Exception e) {
      // We don't throw it firther as gathering logs is not a crticial
      // issue. We just report the problem to administrator.
      reportLogProcessingException(e);
    }
  }


  protected final boolean isIgnoreTimeStamp() {
    return cm.getLogConfigPropertyValue(logConfig.getID(), LogConfigProperty.ATTR_IGNORE_TIMESTAMP, LogConfigProperty.OPTION_UNCHECKED).equals(LogConfigProperty.OPTION_CHECKED);
  }


  /**
   * Returns a list of build run logs that will be used by
   * isLogAlreadyArchived to find if the log about to be
   * processed was already processed.
   * <p/>
   * This default implementation searches logs by log type and
   * log path. Implementor of this class can override this
   * method.
   *
   * @param buildRunID for wich perform getting logs
   * @return List StepLog objects.
   * @see #isLogAlreadyArchived(List, long)
   */
  protected List getBuildRunLogs(final int buildRunID) {
    return cm.findBuildRunLogs(buildRunID, logType(), resolvedResultPath);
  }


  /**
   * @param archivedLogs     - List of StepLog objects that are
   *                         already archived.
   * @param builderTimeStamp long agent's time stamp at build
   *                         run start.
   * @return true if log being processed was already archived.
   */
  protected abstract boolean isLogAlreadyArchived(List archivedLogs, final long builderTimeStamp) throws IOException, AgentFailureException;


  /**
   * Concrete classes should impelement this method and do
   * concrete log type processing.
   *
   * @throws IOException
   */
  protected abstract void processLog() throws IOException, AgentFailureException;


  /**
   * @return byte log type that this handler can process.
   */
  protected abstract byte logType();


  private void reportLogProcessingException(final Exception e) {
    final Error error = makeLogHandlerError("Error while processing build logs");
    error.setDetails(e);
    errorManager.reportSystemError(error);
  }


  /**
   * Reports that log is out of range.
   */
  private void reportLogIsOutOfScope(final String logPath, final LogConfig logConfig) {
    final Error error = makeLogHandlerError("Custom build log path is outside of allowed scope");
    error.setDescription("Resulting custom build log path \"" + logPath + "\" for log \"" + logConfig.getDescription() + "\" is outside of allowed scope.");
    error.setPossibleCause("Custom build log configuration is invalid. Adjust log configuration.");
    errorManager.reportSystemError(error);
  }


  /**
   * Reports that log is not file
   */
  protected final void reportLogPathIsNotFile() {
    final Error error = makeLogHandlerError("Custom build log path \"" + fullyQualifiedResultPath + "\" for log \"" + logConfig.getDescription() + "\" was expected to be a file, but it was a directory");
    error.setPossibleCause("Custom build log configuration is not valid. Adjust log configuration.");
    errorManager.reportSystemError(error);
  }


  /**
   * Reports that log is not a dir
   */
  protected final void reportLogPathIsNotDirectory() {
    final Error error = makeLogHandlerError("Custom build log path \"" + fullyQualifiedResultPath + "\" for log \"" + logConfig.getDescription() + "\" was expected to be a directory, but it was a file");
    error.setPossibleCause("Custom build log configuration is not valid. Adjust log configuration.");
    errorManager.reportSystemError(error);
  }


  private Error makeLogHandlerError(final String description) {
    final Error error = new Error(description);
    error.setBuildID(activeBuildID);
    error.setSendEmail(true);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_LOGGING);
    return error;
  }


  public String toString() {
    return "AbstractLogHandler{" +
            "cm=" + cm +
            ", errorManager=" + errorManager +
            ", stepRunID=" + stepRunID +
            ", archiveManager=" + archiveManager +
            ", agent=" + agent +
            ", logConfig=" + logConfig +
            ", fullyQualifiedLogPath='" + fullyQualifiedResultPath + '\'' +
            ", searchManager=" + searchManager +
            ", buildRunID=" + buildRunID +
            ", resolvedLogPath='" + resolvedResultPath + '\'' +
            '}';
  }
}
