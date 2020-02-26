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
import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.remote.Agent;

import java.io.IOException;

/**
 * Executes hg log command.
 */
public class MercurialLogCommand extends MercurialCommand {

  private final String fromRev;
  private final String toRev;
  private final int maxChangeLists;
  private final String branch;
  private final String stylePath;


  public MercurialLogCommand(final Agent agent, final String exePath, final String fromRev,
                             final String toRev, final int maxChangeLists, final String branch, final String stylePath) throws AgentFailureException, IOException {
    super(agent, exePath);
    this.fromRev = fromRev;
    this.toRev = toRev;
    this.maxChangeLists = maxChangeLists;
    this.branch = branch;
    this.stylePath = stylePath;
  }


  protected String getExeArguments() {
    final StringBuilder sb = new StringBuilder(100);
    sb.append("log");
    sb.append(' ');
    sb.append("--noninteractive");
    sb.append(' ');
    sb.append("--verbose");
    sb.append(' ');
    sb.append("--style");
    sb.append(' ');
    sb.append(StringUtils.putIntoDoubleQuotes(stylePath));
    sb.append(' ');
    sb.append("--rev");
    sb.append(' ');
    sb.append(toRev);
    sb.append(':');
    sb.append(fromRev);
    sb.append(' ');
    sb.append("--limit");
    sb.append(' ');
    sb.append(maxChangeLists);
    if (!StringUtils.isBlank(branch)) {
      sb.append(' ');
      sb.append("--only-branch");
      sb.append(' ');
      sb.append(StringUtils.putIntoDoubleQuotes(branch));
    }
    return sb.toString();
  }
}