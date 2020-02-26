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
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.ArgumentValidator;
import org.parabuild.ci.remote.Agent;

/**
 * Executes ClearCase rmtag command. If a tag doesn't exist,
 * ignores it.
 */
final class ClearCaseRmtagCommand extends ClearCaseCommand {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(ClearCaseRmtagCommand.class); // NOPMD

  private final String viewTag;
  private final Map linesToIgnore = new HashMap(5);


  public ClearCaseRmtagCommand(final Agent agent, final String exePath, final String viewTag, final String ignoreLines) throws IOException, AgentFailureException {
    super(agent, exePath);
    super.setCurrentDirectory(agent.getCheckoutDirHome()); // mkview doesn't have current dir

    // set
    this.viewTag = ArgumentValidator.validateArgumentNotBlank(viewTag, "view tag");

    // init error lines handler
    this.linesToIgnore.put("cleartool: Error: Unable to remove \"" + this.viewTag + "\": ClearCase object not found.", Boolean.TRUE);
    super.setStderrLineProcessor(new AbstractClearCaseStderrProcessor(ignoreLines) {
      protected int doProcessLine(final int index, final String line) {
        if (linesToIgnore.containsKey(line)) return RESULT_IGNORE; // ignorable
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
    sb.append(" rmtag ");
    sb.append(" -view ").append(viewTag); // Removes one or more view-tags.
    return sb.toString();
  }
}
