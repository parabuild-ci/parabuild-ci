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
package org.parabuild.ci.merge.finder;

import java.io.IOException;

import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.util.BuildException;
import org.parabuild.ci.util.CommandStoppedException;
import org.parabuild.ci.common.ValidationException;

/**
 * Responsible for finding change lists and placing them
 * into the merge queue.
 */
public interface MergeQueueCreator {

  /**
   * Finds unmereged change lists to merge and places them into
   * the merge queue. Finds merged change lists and stores them
   * for reporting purposes.
   */
  void updateQueue() throws ValidationException, IOException, CommandStoppedException, BuildException, AgentFailureException;
}
