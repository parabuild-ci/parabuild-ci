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
package org.parabuild.ci.statistics;

import org.parabuild.ci.object.*;

/**
 * Functor to update persistent statistics according to the
 * results of the build run.
 *
 * @see #updateStatistics(BuildRun)
 */
interface PersistentStatsUpdater {

  /**
   * Updates statistics corresponding this build run.  This
   * method should not throw any exceptions. Instead, it should
   * report any errors using ErrorManager.
   */
  void updateStatistics(BuildRun buildRun);
}
