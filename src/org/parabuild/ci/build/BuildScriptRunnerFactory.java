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
package org.parabuild.ci.build;

import org.parabuild.ci.remote.Agent;

import java.io.IOException;

/**
 * Create build script runner
 */
public final class BuildScriptRunnerFactory {

  /**
   * Factory constructor
   */
  private BuildScriptRunnerFactory() {
  }


  public static BuildScriptRunner makeScriptRunner(final Agent agent) throws IOException, AgentFailureException {
    if (agent.isWindows()) {
      return new WindowsBuildScriptRunner(agent);
    } else if (agent.isUnix()) {
      return new UnixBuildScriptRunner(agent);
    } else {
      throw new IllegalStateException("Agent runs an unsupported operating system");
    }
  }
}
