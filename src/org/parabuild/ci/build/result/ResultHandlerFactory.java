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

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.ResultConfig;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.versioncontrol.SourceControl;
import org.parabuild.ci.versioncontrol.VersionControlFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Makes build results handler.
 */
public final class ResultHandlerFactory {

  /**
   * Factory constructor.
   */
  private ResultHandlerFactory() {
  }


  /**
   * Creates a result handler that consists of a list of result
   * handlers for the given build.
   *
   * @param agent
   * @param stepRunID
   */
  public static ResultHandler makeResultHandler(final Agent agent, final int stepRunID) throws AgentFailureException {
    final CompositeResultHandler composite = new CompositeResultHandler();
    BuildRunConfig buildRunConfig = null;
    try {
      // preExecute
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final StepRun stepRun = cm.getStepRun(stepRunID);
      buildRunConfig = cm.getBuildRunConfig(stepRun);
      final SourceControl scm = VersionControlFactory.makeVersionControl(buildRunConfig);
      scm.setAgentHost(agent.getHost());
      final String projectHome = agent.getCheckoutDirName() + '/' + scm.getRelativeBuildDir();

      // traverse result configs and create concrete result handler implementations
      final int buildRunConfigID = buildRunConfig.getBuildID();
      final List resultConfigs = cm.getResultConfigs(buildRunConfigID);
      for (final Iterator i = resultConfigs.iterator(); i.hasNext(); ) {
        final ResultConfig resultConfig = (ResultConfig) i.next();
        switch (resultConfig.getType()) {
          case ResultConfig.RESULT_TYPE_FILE_LIST:
            composite.addHandler(new FileListResultHandler(agent, buildRunConfig, projectHome, resultConfig, stepRunID));
            break;
          case ResultConfig.RESULT_TYPE_DIR:
            composite.addHandler(new DirResultHandler(agent, buildRunConfig, projectHome, resultConfig, stepRunID));
            break;
          case ResultConfig.RESULT_TYPE_URL:
            composite.addHandler(new URLResultHandler(agent, buildRunConfig, projectHome, resultConfig, stepRunID));
            break;
          default:
            //noinspection ThrowCaughtLocally
            throw new BuildException("Unknown result type: " + resultConfig.getType(), agent);
        }
      }
    } catch (final IOException | BuildException e) {
      reportHandlerCreationError(buildRunConfig, e);
    }
    return composite;
  }


  /**
   * Helper method.
   *
   * @param buildRunConfig for which error occurred.
   * @param e              - exception.
   */
  private static void reportHandlerCreationError(final BuildRunConfig buildRunConfig, final Exception e) {
    final Error error = new Error("Error creating result handler: " + StringUtils.toString(e));
    error.setDetails(e);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    if (buildRunConfig != null) error.setBuildName(buildRunConfig.getBuildName());
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }
}
