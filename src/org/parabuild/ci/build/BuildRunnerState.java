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

import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.object.BuildSequence;

/**
 * Created by IntelliJ IDEA. User: vimeshev Date: Aug 3, 2005
 * Time: 10:36:15 PM To change this template use File | Settings
 * | File Templates.
 */
public interface BuildRunnerState {

  int getCurrentlyRunningBuildRunID();


  int getCurrentlyRunningBuildConfigID();


  BuildRun getLastCleanBuildRun();


  BuildRun getLastCompleteBuildRun();


  BuildSequence getCurrentlyRunningStep();


  /**
   * Returns build host the build is currently running on or null
   * if not running or host is not set.
   */
  String getCurrentlyRunningOnHost();


  /**
   * @return currently running build number.
   */
  int getCurrentlyRunningBuildNumber();


  /**
   * @return currently running build number.
   */
  String getCurrentlyRunningChangeListNumber();
}
