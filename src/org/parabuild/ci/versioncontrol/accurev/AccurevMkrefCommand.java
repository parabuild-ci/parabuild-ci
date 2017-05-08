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
package org.parabuild.ci.versioncontrol.accurev;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.remote.Agent;

import java.io.IOException;

/**
 * Accurev Mkref command.
 *
 * @author Slava Imeshev
 * @since Feb 12, 2009 8:50:04 PM
 */
final class AccurevMkrefCommand extends AccurevCommand {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(AccurevMkrefCommand.class); // NOPMD
  private final String referenceTreeName;
  private final byte eolType;
  private String storage;


  /**
   * accurev mkws -w <workspace-name> -b <backing-stream> -l <storage> [ -k <kind> ] [ -e <eol-type> ] [ -i ]
   *
   * @param storage
   * @param agent
   * @param parameters
   * @param transactionNumber
   * @throws java.io.IOException
   */
  AccurevMkrefCommand(final Agent agent, final AccurevCommandParameters parameters,
                      final String referenceTreeName, final byte eolType,
                      final String storage) throws IOException, AgentFailureException {
    super(agent, parameters);
    this.referenceTreeName = referenceTreeName;
    this.eolType = eolType;
    this.storage = storage;
  }


  /**
   * Requests changes in the reverse order of transactions.
   */
  protected void preExecute() throws IOException, AgentFailureException {
    super.preExecute();
    final StringBuffer cmd = new StringBuffer(200);
    appendCommand(cmd, StringUtils.putIntoDoubleQuotes(getParameters().getExePath()));
    appendCommand(cmd, "mkws");
    appendCommand(cmd, "-w", referenceTreeName);
    appendCommand(cmd, "-b", getParameters().getBackingStream());
    appendCommand(cmd, "-l", storage);
    appendCommand(cmd, toStringEolType(eolType));
    setCommand(cmd.toString());
    if (AccurevMkrefCommand.LOG.isDebugEnabled()) {
      AccurevMkrefCommand.LOG.debug("cmd: " + cmd);
    }
  }


  public String toString() {
    return "AccurevMkwsCommand{" +
            "referenceTreeName='" + referenceTreeName + '\'' +
            ", eolType=" + eolType +
            ", storage='" + storage + '\'' +
            '}';
  }
}
