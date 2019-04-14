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
package org.parabuild.ci.versioncontrol.bazaar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;

import java.io.IOException;

/**
 * BazaarLogCommand
 * <p/>
 *
 * @author Slava Imeshev
 * @since Apr 5, 2010 9:51:21 PM
 */
public final class BazaarLogCommand extends BazaarCommand {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(BazaarLogCommand.class); // NOPMD
  private final String changeListNumberFrom;
  private final String changeListNumberTo;
  private final int maxChangeLists;
  private final String branchPath;


  protected BazaarLogCommand(final Agent agent, final String exePath, final String branchPath,
                             final String changeListNumberFrom, final String changeListNumberTo,
                             final int maxChangeLists) throws AgentFailureException, IOException {
    super(agent, exePath);
    this.branchPath = ArgumentValidator.validateArgumentNotBlank(branchPath, "Bazaar branch path").trim();
    this.changeListNumberFrom = changeListNumberFrom;
    this.changeListNumberTo = changeListNumberTo;
    this.maxChangeLists = maxChangeLists;
  }


  protected String getExeArguments() throws IOException, AgentFailureException {
    final StringBuilder sb = new StringBuilder(100);
    sb.append(" log ");
    sb.append(" -v ");
    sb.append(" -l ").append(Integer.toString(maxChangeLists));
    if (!StringUtils.isBlank(changeListNumberFrom) || !StringUtils.isBlank(changeListNumberTo)) {
      sb.append(" -r").append(changeListNumberFrom).append("..").append(changeListNumberTo);
    }
    sb.append(" ");
    sb.append(branchPath);
    return sb.toString();
  }
}
