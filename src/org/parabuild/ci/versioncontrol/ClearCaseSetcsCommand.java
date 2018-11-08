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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;

/**
 * Executes ClearCase setcs command.
 *
 * @see ClearCaseRmtagCommand
 */
final class ClearCaseSetcsCommand extends ClearCaseCommand {

  private static final Log log = LogFactory.getLog(ClearCaseSetcsCommand.class);

  private final String viewSpec;
  private final List updateLogFileNames = new ArrayList(5);
  private String tempSpecFile = null;


  public ClearCaseSetcsCommand(final Agent agent, final String exePath, final String viewSpec, final String ignoreLines) throws IOException, AgentFailureException {
    super(agent, exePath);

    // set
    this.viewSpec = ArgumentValidator.validateArgumentNotBlank(viewSpec, "view spec");
    if (log.isDebugEnabled()) log.debug("viewSpec: " + viewSpec);

    // set error lines handler
    super.setStderrLineProcessor(new ClearCaseSetcsCommandStderrProcessor(ignoreLines, updateLogFileNames));
  }


  /**
   * Returns arguments to pass to SVN executable including SVN
   * command and it args.
   */
  protected String getExeArguments() throws IOException, AgentFailureException {
    final String fixedSpec = agent.fixCRLF(viewSpec);
    tempSpecFile = agent.createTempFile(".parabuild", ".tmp", fixedSpec);
    if (log.isDebugEnabled()) log.debug("viewSpec:\n" + viewSpec);
    final StringBuilder sb = new StringBuilder(100);
    sb.append(" setcs ");
    sb.append(agent.isWindows() ? StringUtils.putIntoDoubleQuotes(tempSpecFile) : tempSpecFile);
    return sb.toString();
  }


  /**
   * Removes all output and temporary files
   */
  public void cleanup() throws AgentFailureException {
    // first delete temp files we created
    agent.deleteTempFileHard(tempSpecFile);
    for (int i = 0, n = updateLogFileNames.size(); i < n; i++) {
      final String updateLogFileName = (String) updateLogFileNames.get(i);
      try {
        agent.deleteFileUnderCheckoutDir(updateLogFileName);
      } catch (final IOException e) {
        log.warn("Failed to delete log file", e);
      }
    }
    // call parent cleanup
    super.cleanup();
  }
}
