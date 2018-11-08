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
 * Executes ClearCase mklbtype command.
 */
final class ClearCaseMklbtypeCommand extends ClearCaseCommand {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(ClearCaseMklbtypeCommand.class);

  private final String labelType;


  public ClearCaseMklbtypeCommand(final Agent agent, final String exePath, final String labelType, final String path, final String ignoreValues) throws IOException, AgentFailureException {
    super(agent, exePath);
    super.setCurrentDirectory(path);
    this.labelType = ArgumentValidator.validateArgumentNotBlank(labelType, "label type");
    super.setStderrLineProcessor(new AbstractClearCaseStderrProcessor(ignoreValues) {
      protected int doProcessLine(final int index, final String line) {
        if (line.startsWith("cleartool: Error: Name \"" + ClearCaseMklbtypeCommand.this.labelType + "\" already exists"))
          return RESULT_IGNORE;
        if (line.startsWith("cleartool: Error: Unable to create label type \"" + ClearCaseMklbtypeCommand.this.labelType + '\"'))
          return RESULT_IGNORE;
        if (line.startsWith("cleartool: Error: Label type not found: \"" + ClearCaseMklbtypeCommand.this.labelType + '\"'))
          return RESULT_IGNORE;
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
    sb.append(" mklbtype ");
    sb.append(" -ordinary ");
    sb.append(" -nc ");
    sb.append(labelType);
//    sb.append(' ');
//    sb.append(path);
    return sb.toString();
  }
}
