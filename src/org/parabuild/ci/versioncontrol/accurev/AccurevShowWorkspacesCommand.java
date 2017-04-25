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
 * Accurev login command.
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 12, 2009 7:56:40 PM
 */
final class AccurevShowWorkspacesCommand extends AccurevCommand {

  private static final Log LOG = LogFactory.getLog(AccurevShowWorkspacesCommand.class); // NOPMD


  AccurevShowWorkspacesCommand(final Agent agent, final AccurevCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, parameters);
  }


  /**
   * Callback method - this method is called before execute.
   */
  protected void preExecute() throws IOException, AgentFailureException {
    super.preExecute();
    final StringBuffer cmd = new StringBuffer(200);
    appendCommand(cmd, StringUtils.putIntoDoubleQuotes(getParameters().getExePath()));
    appendCommand(cmd, "show");
    appendCommand(cmd, "-fx");
    appendCommand(cmd, "wspaces");
    setCommand(cmd.toString());
    if (LOG.isDebugEnabled()) {
      LOG.debug("cmd: " + cmd);
    }
  }
}
