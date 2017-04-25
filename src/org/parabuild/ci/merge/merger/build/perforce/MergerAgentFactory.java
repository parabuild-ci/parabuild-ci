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
package org.parabuild.ci.merge.merger.build.perforce;

import org.parabuild.ci.build.BuildRunner;
import org.parabuild.ci.build.BuildRunnerAgentFactory;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.services.BuildStartRequest;

/**
 * Defines a factory used by {@link
 * BuildRunner} to create a agent
 * to work on the workspace.
 * <p/>
 * This implementation just returns the agent that is
 * configured to be used by, and in a client directory of,
 * merge VCS.
 */
final class MergerAgentFactory implements BuildRunnerAgentFactory {

  private final Agent agent;


  /**
   * @see P4Merger#runValidationBuild
   */
  public MergerAgentFactory(final Agent agent) {

    this.agent = agent;
  }


  /**
   * Returns a instance of the a Agent passed by  Merger.
   *
   * @see P4Merger#runValidationBuild
   */
  public Agent checkoutAgent(final int activeBuildID, final BuildStartRequest buildConfigID) {
    return agent;
  }


  public boolean supportsNextAgent() {
    return false;
  }


  public void checkinAgent(final Agent agent) {

  }
}
