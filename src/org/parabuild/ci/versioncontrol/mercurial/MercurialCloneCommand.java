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
package org.parabuild.ci.versioncontrol.mercurial;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.util.StringUtils;

import java.io.IOException;

/**
 * Executes hg clone command.
 */
public class MercurialCloneCommand extends MercurialCommand {

  private final String url;
  private final String branch;


  public MercurialCloneCommand(final Agent agent, final String exePath, final String url, final String branch) throws AgentFailureException, IOException {
    super(agent, exePath);
    this.url = url;
    this.branch = branch;
  }


  protected String getExeArguments() throws IOException, AgentFailureException {
    final StringBuilder sb = new StringBuilder(100);
    sb.append("clone");
    sb.append(' ');
    sb.append("--noninteractive");
    sb.append(' ');
    if (!StringUtils.isBlank(branch)) {
      sb.append(' ');
      sb.append("--branch");
      sb.append(' ');
      sb.append(StringUtils.putIntoDoubleQuotes(branch));
    }
    sb.append(' ');
    sb.append(StringUtils.putIntoDoubleQuotes(url));
    sb.append(' ');
    sb.append(StringUtils.putIntoDoubleQuotes(agent.getCheckoutDirName()));
    return sb.toString();
  }
}