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
package org.parabuild.ci.build.result;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.archive.ArchiveManager;
import org.parabuild.ci.archive.ArchiveManagerFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.ValidationException;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.ResultGroupManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.ResultConfig;
import org.parabuild.ci.object.StepResult;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.search.SearchManager;
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.util.IoUtils;
import org.parabuild.ci.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * AbstractResultHandler implementers GoF Strategy patters by
 * delivering common functionality and delegating concrete result
 * type processing to implementing classes. Implementing classes
 * should implement abstract processResult method.
 *
 * @noinspection ProtectedField, ParameterHidesMemberVariable@see #processResult
 */
public abstract class AbstractResultHandler implements ResultHandler {

  /**
   * @noinspection UNUSED_SYMBOL, UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(AbstractResultHandler.class); // NOPMD
  protected final ArchiveManager archiveManager;
  protected final Agent agent;
  protected final ConfigurationManager cm = ConfigurationManager.getInstance();
  protected final int stepRunID;
  protected final ResultConfig resultConfig;
  protected final SearchManager searchManager = SearchManager.getInstance();
  private final boolean resultIsFilePath;
  private final BuildRunConfig buildRunConfig;
  private final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
  private final String[] fullyQualifiedResultPaths;
  private final String[] resolvedResultPaths;
  protected boolean pinResult = false;


  /**
   * Constructor
   *
   * @param agent            for which this handler is created
   * @param stepRunID        step ID that is to be processed
   * @param resultIsFilePath
   */
  protected AbstractResultHandler(final Agent agent, final BuildRunConfig buildRunConfig,
                                  final String projectHome, final ResultConfig resultConfig, final int stepRunID,
                                  final boolean resultIsFilePath) throws IllegalArgumentException {

    try {
      // validate
      if (stepRunID == StepRun.UNSAVED_ID) {
        throw new IllegalArgumentException("Step ID is invalid");
      }
      ArgumentValidator.validateBuildIDInitialized(agent.getActiveBuildID());

      // set
      this.agent = agent;
      this.buildRunConfig = buildRunConfig;
      this.resultConfig = resultConfig;
      this.stepRunID = stepRunID;
      this.archiveManager = ArchiveManagerFactory.getArchiveManager(buildRunConfig.getActiveBuildID());
      this.resultIsFilePath = resultIsFilePath;
      this.resolvedResultPaths = resolveResultPath(agent.getHost().getHost(), resultConfig, stepRunID);
      this.fullyQualifiedResultPaths = qualifyResultPath(projectHome, resultIsFilePath, resolvedResultPaths);

      // paranoid validation - build result handler always works
      // using build run config
      if (buildRunConfig.getBuildID() == buildRunConfig.getActiveBuildID()) {
        throw new IllegalStateException("Build configuration with ID " + buildRunConfig.getBuildID()
                + " is not a build run configuration.");
      }
    } catch (final ValidationException e) {
      throw new IllegalArgumentException(StringUtils.toString(e));
    }
  }


  private static String[] qualifyResultPath(final String projectHome, final boolean resultIsFilePath, final String[] resolvedResultPath) {
    final String[] result = new String[resolvedResultPath.length];
    for (int i = 0; i < resolvedResultPath.length; i++) {
      result[i] = resultIsFilePath ? projectHome + '/' + resolvedResultPath[i] : resolvedResultPath[i];
    }
    return result;
  }


  /**
   * Finds results, moves them to archive and adjusts database.
   * <p/>
   * This method should not throw any exceptions.
   */
  public final void process() throws IllegalArgumentException {

    // validate we are called with the correct result type
    if (resultConfig.getType() != resultType()) {
      throw new IllegalArgumentException("Result type code is invalid for this result handler: " + resultConfig.getType());
    }

    try {
      for (int i = 0; i < fullyQualifiedResultPaths.length; i++) {
        final String fullyQualifiedResultPath = fullyQualifiedResultPaths[i];
        final String resolvedResultPath = resolvedResultPaths[i];

        if (resultIsFilePath) {

          // form resulting result path and check if it exists
          if (!agent.absolutePathExists(fullyQualifiedResultPath)) {
            continue;
          }

          // check if path is not outside of the allowed scope
          // REVIEWME: simeshev@parabuilci.org -> this actually does not work: IoUtils.isFileUnder
          if (!IoUtils.isFileUnder(fullyQualifiedResultPath, agent.getCheckoutDirHome())) {
            reportResultIsOutOfScope(buildRunConfig, fullyQualifiedResultPath, resultConfig);
            continue;
          }
        }

        // find if we need to check if the result was already archived by this build run ID
        final StepRun stepRun = cm.getStepRun(stepRunID); // REVIEWME: do we really need this operation? may be we should just store StepRun object in AbstractResultHandler?
        final long builderTimeStamp = cm.getBuilderTimeStamp(stepRun);
        final List archivedBuildRunResults = cm.findBuildRunResults(stepRun.getBuildRunID(), resultType(), resolvedResultPath);
        if (isResultAlreadyArchived(archivedBuildRunResults, builderTimeStamp, fullyQualifiedResultPath)) {
          continue;
        }

        // call strategy method that takes care about processing of a concrete result type
        processResult(builderTimeStamp, fullyQualifiedResultPath, resolvedResultPath);
      }

    } catch (final Exception e) {
      // We don't throw it further as gathering results is not a critical
      // issue. We just report the problem to administrator.
      reportResultProcessingException(buildRunConfig.getBuildName(), e);
    }
  }


  public final void setPinResult(final boolean pinResult) {
    this.pinResult = pinResult;
  }


  /**
   * Concrete classes should implement this method and do
   * concrete result type processing.
   *
   * @param builderTimeStamp         agent time stamp before any
   *                                 result could created for this processing call. Any
   *                                 results that are created before are old results.
   * @param fullyQualifiedResultPath
   * @param resolvedResultPath       @throws IOException
   */
  protected abstract void processResult(final long builderTimeStamp, String fullyQualifiedResultPath, String resolvedResultPath) throws IOException, AgentFailureException;


  /**
   * @return byte result type that this handler can process.
   */
  protected abstract byte resultType();


  /**
   * @param archivedBuildRunResults  List of {@link StepResult objects}
   *                                 already stored for the given build run.
   * @param builderTimeStamp         long agent's time stamp at build
   *                                 run start.
   * @param fullyQualifiedResultPath
   * @return true if result was already archived.
   */
  protected abstract boolean isResultAlreadyArchived(final List archivedBuildRunResults, final long builderTimeStamp,
                                                     final String fullyQualifiedResultPath) throws IOException, AgentFailureException;


  /**
   * Reports processing exception.
   *
   * @param buildName name of the build
   * @param e         exception to report.
   */
  private void reportResultProcessingException(final String buildName, final Exception e) {
    final Error error = new Error("Error while processing build results");
    error.setDetails(e);
    error.setSendEmail(true);
    error.setBuildName(buildName);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_LOGGING);
    errorManager.reportSystemError(error);
  }


  /**
   * Reports that result is out of range.
   */
  private void reportResultIsOutOfScope(final BuildConfig buildConfig, final String resultPath, final ResultConfig resultConfig) {
    final Error error = new Error("Custom build result path is outside of allowed scope");
    error.setDescription("Resulting custom build result path \"" + resultPath + "\" for result \"" + resultConfig.getDescription() + "\" is outside of allowed scope.");
    error.setBuildName(buildConfig.getBuildName());
    error.setPossibleCause("Custom build result configuration is invalid. Adjust result configuration.");
    error.setSendEmail(true);
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_LOGGING);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    errorManager.reportSystemError(error);
  }


  /**
   * Reports that result is not file
   *
   * @param fullyQualifiedResultPath
   */
  protected final void reportResultPathIsNotFile(final String fullyQualifiedResultPath) {
    final Error error = new Error("Custom build result path \"" + fullyQualifiedResultPath + "\" for result \"" + resultConfig.getDescription() + "\" was expected to be a file, but it was a directory");
    error.setPossibleCause("Custom build result configuration is not valid. Adjust result configuration.");
    error.setBuildName(buildRunConfig.getBuildName());
    error.setSendEmail(true);
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_LOGGING);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    errorManager.reportSystemError(error);
  }


  /**
   * Reports that result is not a dir
   *
   * @param fullyQualifiedResultPath
   */
  protected final void reportResultPathIsNotDirectory(final String fullyQualifiedResultPath) {
    final Error error = new Error("Custom build result path \"" + fullyQualifiedResultPath + "\" for result \"" + resultConfig.getDescription() + "\" was expected to be a directory, but it was a file");
    error.setPossibleCause("Custom build result configuration is not valid. Adjust result configuration.");
    error.setBuildName(buildRunConfig.getBuildName());
    error.setSendEmail(true);
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_LOGGING);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    errorManager.reportSystemError(error);
  }


  /**
   * Publishes this result if automatic publishing result group is defined.
   */
  protected final void publish(final StepResult stepResult) {
    final Integer autopublishGroupID = resultConfig.getAutopublishGroupID();
    if (autopublishGroupID != null) {
      ResultGroupManager.getInstance().publish(autopublishGroupID, stepResult);
    }
  }


  private String[] resolveResultPath(final String agentHostName, final ResultConfig resultConfig, final int stepRunID) throws ValidationException {

    final StepRun stepRun = cm.getStepRun(stepRunID);
    final int buildRunID = stepRun.getBuildRunID();
    final BuildRun buildRun = ConfigurationManager.getInstance().getBuildRun(buildRunID);
    final String resultConfigPath = resultConfig.getPath();
    final List list = StringUtils.multilineStringToList(resultConfigPath);
    final String[] result = new String[list.size()];
    for (int i = 0; i < list.size(); i++) {

      final int activeBuildID = buildRun.getActiveBuildID();
      final String stepRunName = stepRun.getName();
      final BuildRunSettingResolver buildRunSettingResolver = new BuildRunSettingResolver(activeBuildID, agentHostName, buildRun, stepRunName);
      result[i] = buildRunSettingResolver.resolve((String) list.get(i));
    }
    return result;
  }


  public final String toString() {
    return "AbstractResultHandler{" +
            "cm=" + cm +
            ", errorManager=" + errorManager +
            ", searchManager=" + searchManager +
            ", stepRunID=" + stepRunID +
            ", archiveManager=" + archiveManager +
            ", buildRunConfig=" + buildRunConfig +
            ", agent=" + agent +
            ", resultConfig=" + resultConfig +
            ", pinResult=" + pinResult +
            ", resultIsFilePath=" + resultIsFilePath +
            '}';
  }
}
