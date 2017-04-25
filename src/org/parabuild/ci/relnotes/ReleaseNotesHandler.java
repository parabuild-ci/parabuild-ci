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
package org.parabuild.ci.relnotes;

import org.parabuild.ci.object.*;

/**
 * ReleaseNotesHandler is responsible for finding issues and
 * attaching them to the given build run.
 * <p/>
 * Configuration for ReleaseNotesHandlers is stored in {@link
 * IssueTracker}  and {@link IssueTrackerProperty}
 * <p/>
 * Concrete implementations of this interface are created by
 * ReleaseNotesHandlerFactory.
 *
 * @see ReleaseNotesHandlerFactory#getHandler
 * @see Issue
 * @see IssueAttribute
 * @see IssueTracker
 * @see IssueTrackerProperty
 */
public interface ReleaseNotesHandler {

  /**
   * This method finds and attaches release notes to the list of
   * pending issues.
   *
   * @param buildRun to find and attach release notes to.
   *
   * @return number of issues accepted and added to the list of
   *         pending issues.
   */
  int process(BuildRun buildRun);
}
