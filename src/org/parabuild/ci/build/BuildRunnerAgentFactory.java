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
import org.parabuild.ci.services.BuildStartRequest;

/**
 * Defines a factory used by {@link BuildRunner} to create a
 * agent to work on the workspace.
 */
public interface BuildRunnerAgentFactory {

  /**
   * Returns a instance of the a Agent corresponding the
   * given BuildConfig. Returns null if no agent is available.
   * <p/>
   * The agent should be checked out back when the build finishes.
   *
   * @param activeBuildID     a build configuration ID.
   * @param buildStartRequest a build start request.
   * @return instance of the a Agent corresponding the given BuildConfig or null if no live agents
   *         available or if the unique checkout was requested and all agents are busy.
   */
  Agent checkoutAgent(final int activeBuildID, final BuildStartRequest buildStartRequest);

  /**
   * Returns true if the given factory can provide more agents in case of factory.
   *
   * @return true if the given factory can provide more agents in case of factory.
   */
  boolean supportsNextAgent();

  /**
   * Returns an agent to the pool of available agents.
   *
   * @param agent the agent to return.
   */
  void checkinAgent(Agent agent);
}
