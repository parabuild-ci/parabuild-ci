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
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;

import java.io.IOException;

/**
 * BazaarRevertCommand
 * <p/>
 *
 * @author Slava Imeshev
 */
public final class BazaarRevertCommand extends BazaarCommand {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(BazaarRevertCommand.class); // NOPMD
  private final String changeListNumber;
  private final String branchLocation;


  protected BazaarRevertCommand(final Agent agent, final String exePath, final String changeListNumber,
                                final String branchLocation) throws AgentFailureException, IOException {
    super(agent, exePath);
    this.changeListNumber = changeListNumber;
    this.branchLocation = branchLocation;
  }


  protected String getExeArguments() throws IOException, AgentFailureException {
    final String relativeBuildDir = branchLocationToRelativeBuildDir(branchLocation);
    final String checkoutDir = agent.getCheckoutDirName() + agent.separator() + relativeBuildDir;
    final StringBuilder sb = new StringBuilder(100);
    sb.append(" revert ");
    sb.append(" --no-backup ");
    sb.append(" -r ").append(changeListNumber);
    sb.append(' ').append(StringUtils.putIntoDoubleQuotes(checkoutDir));
    return sb.toString();
  }
}