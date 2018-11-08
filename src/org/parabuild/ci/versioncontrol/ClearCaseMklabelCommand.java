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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.remote.Agent;

/**
 * Executes ClearCase mklabel command.
 */
final class ClearCaseMklabelCommand extends ClearCaseCommand {

  private static final Log log = LogFactory.getLog(ClearCaseMklabelCommand.class);

  private final String label;
  private final String path;


  public ClearCaseMklabelCommand(final Agent agent, final String exePath, final String label, final String path, final String ignoreLines) throws IOException, AgentFailureException {
    super(agent, exePath);
    super.setCurrentDirectory(path);

    // set
    if (log.isDebugEnabled()) log.debug("label: " + label);
    this.label = ArgumentValidator.validateArgumentNotBlank(label, "label");
    this.path = ArgumentValidator.validateArgumentNotBlank(path, "path");
    super.setStderrLineProcessor(new AbstractClearCaseStderrProcessor(ignoreLines) {
      protected int doProcessLine(final int index, final String line) {
        return RESULT_ADD_TO_ERRORS;
      }
    });
  }


  /**
   * Returns arguments to pass to SVN executable including SVN
   * command and it args.
   */
  protected String getExeArguments() {
    final StringBuilder sb = new StringBuilder(100);
    sb.append(" mklabel ");
    sb.append(" -recurse ");
    sb.append(" -replace ");
    sb.append(label);
    sb.append(' ');
    sb.append(path);
    return sb.toString();
  }
}
