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

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.common.VersionControlSystem;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.versioncontrol.VersionControlRemoteCommand;

import java.io.IOException;

/**
 * AccurevCommand
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 12, 2009 5:40:16 PM
 */
abstract class AccurevCommand extends VersionControlRemoteCommand {

  private final AccurevCommandParameters parameters;


  protected AccurevCommand(final Agent agent, final AccurevCommandParameters parameters) throws IOException, AgentFailureException {
    super(agent, true);
    this.parameters = parameters;
    super.setCurrentDirectory(agent.getCheckoutDirName());
    super.signatureRegistry.register(remoteCurrentDir);
    super.setStderrLineProcessor(new AccurevStderrLineProcessor());
  }


  protected final AccurevCommandParameters getParameters() {
    return parameters;
  }


  protected static String toStringEolType(final byte eolType) {
    switch (eolType) {
      case VersionControlSystem.ACCUREV_EOL_PLATFORM:
        return "";
      case VersionControlSystem.ACCUREV_EOL_UNIX:
        return "-eu";
      case VersionControlSystem.ACCUREV_EOL_WINDOWS:
        return "-ew";
      default:
        throw new IllegalStateException("Unknown EOL type");
    }
  }


  protected static String toStringKind(final byte kind) {
    switch (kind) {
      case VersionControlSystem.ACCUREV_WORKSPACE_LOKING_NONE:
        return "";
      case VersionControlSystem.ACCUREV_WORKSPACE_LOKING_EXCLUSIVE:
        return "-ke";
      case VersionControlSystem.ACCUREV_WORKSPACE_LOKING_ANCHOR:
        return "-ka";
      default:
        throw new IllegalStateException("Unknown kind type");
    }
  }


  public String toString() {
    return "AccurevCommand{" +
            "parameters=" + parameters +
            '}';
  }
}
