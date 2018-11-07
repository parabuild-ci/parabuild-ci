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

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.BuildException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.BuildRunConfig;
import org.parabuild.ci.object.LogConfig;
import org.parabuild.ci.object.StepRun;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.versioncontrol.SourceControl;
import org.parabuild.ci.versioncontrol.VersionControlFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * Makes cusom long handler.
 */
public final class LogHandlerFactory {

  /**
   * Factory constructor.
   */
  private LogHandlerFactory() {
  }


  /**
   * Creates a log handler that consists of a list of log
   * handlers for the given build.
   *
   * @param agent
   * @param stepRunID
   * @noinspection OverlyCoupledMethod,ThrowCaughtLocally
   */
  public static LogHandler makeLogHandler(final Agent agent, final int stepRunID) throws AgentFailureException {
    BuildRunConfig buildRunConfig = null;
    final CompositeLogHandler composite = new CompositeLogHandler();
    try {
      // preExecute
      final ConfigurationManager cm = ConfigurationManager.getInstance();
      final StepRun stepRun = cm.getStepRun(stepRunID);
      buildRunConfig = cm.getBuildRunConfig(stepRun);
      final SourceControl scm = VersionControlFactory.makeVersionControl(buildRunConfig);
      scm.setAgentHost(agent.getHost());
      final String projectHome = agent.getCheckoutDirName() + '/' + scm.getRelativeBuildDir();

      // add "always there" main build log handler.
      composite.addHandler(new ConsoleLogHandler(buildRunConfig.getBuildID(), stepRunID));

      // traverse log configs and create concrete log handler implementations
      final List logConfigs = cm.getLogConfigs(buildRunConfig.getBuildID());
      for (final Iterator iter = logConfigs.iterator(); iter.hasNext();) {
        final LogConfig logConfig = (LogConfig) iter.next();
        switch (logConfig.getType()) {
          case LogConfig.LOG_TYPE_TEXT_FILE:
            composite.addHandler(new TextFileLogHandler(agent, buildRunConfig, projectHome, logConfig, stepRunID));
            break;
          case LogConfig.LOG_TYPE_TEXT_DIR:
            composite.addHandler(new TextDirLogHandler(agent, buildRunConfig, projectHome, logConfig, stepRunID));
            break;
          case LogConfig.LOG_TYPE_JNUIT_XML_DIR:
            composite.addHandler(new JUnitLogHandler(agent, buildRunConfig, projectHome, logConfig, stepRunID));
            break;
          case LogConfig.LOG_TYPE_NUNIT_XML_DIR:
            composite.addHandler(new NUnitLogHandler(agent, buildRunConfig, projectHome, logConfig, stepRunID));
            break;
          case LogConfig.LOG_TYPE_PMD_XML_FILE:
            composite.addHandler(new PMDLogHandler(agent, buildRunConfig, projectHome, logConfig, stepRunID));
            break;
          case LogConfig.LOG_TYPE_HTML_FILE:
            composite.addHandler(new HTMLFileLogHandler(agent, buildRunConfig, projectHome, logConfig, stepRunID));
            break;
          case LogConfig.LOG_TYPE_HTML_DIR:
            composite.addHandler(new HTMLDirLogHandler(agent, buildRunConfig, projectHome, logConfig, stepRunID));
            break;
          case LogConfig.LOG_TYPE_CPPUNIT_XML_DIR:
            composite.addHandler(new CppUnitLogHandler(agent, buildRunConfig, projectHome, logConfig, stepRunID));
            break;
          case LogConfig.LOG_TYPE_FINDBUGS_XML_FILE:
            composite.addHandler(new FindbugsLogHandler(agent, buildRunConfig, projectHome, logConfig, stepRunID));
            break;
          case LogConfig.LOG_TYPE_CHECKSTYLE_XML_FILE:
            composite.addHandler(new CheckstyleLogHandler(agent, buildRunConfig, projectHome, logConfig, stepRunID));
            break;
          case LogConfig.LOG_TYPE_PHPUNIT_XML_DIR:
            composite.addHandler(new PHPUnitLogHandler(agent, buildRunConfig, projectHome, logConfig, stepRunID));
            break;
          case LogConfig.LOG_TYPE_BOOST_TEST_XML_DIR:
            composite.addHandler(new BoostTestLogHandler(agent, buildRunConfig, projectHome, logConfig, stepRunID));
            break;
          case LogConfig.LOG_TYPE_GOOGLETEST_XML_FILE:
            composite.addHandler(new GoogleTestLogHandler(agent, buildRunConfig, projectHome, logConfig, stepRunID));
            break;
          case LogConfig.LOG_TYPE_SQUISH_TESTER_XML_FILE:
            composite.addHandler(new SquishLogHandler(agent, buildRunConfig, projectHome, logConfig, stepRunID));
            break;
          case LogConfig.LOG_TYPE_UNITTESTPP_XML_DIR:
            composite.addHandler(new UnitTestPpLogHandler(agent, buildRunConfig, projectHome, logConfig, stepRunID));
            break;
          case LogConfig.LOG_TYPE_GENERIC_TEST_RESULT:
            composite.addHandler(new GenericTestResultHandler(agent, buildRunConfig, projectHome, logConfig, stepRunID));
            break;
          default:
            throw new BuildException("Unknown log type: " + logConfig.getType(), agent);
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
   * @param buildRunConfig for which error occured
   * @param e              - exception
   */
  private static void reportHandlerCreationError(final BuildRunConfig buildRunConfig, final Exception e) {
    final Error error = new Error("Error creating log handler: " + StringUtils.toString(e));
    error.setDetails(e);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    if (buildRunConfig != null) {
      error.setBuildName(buildRunConfig.getBuildName());
    }
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }
}
