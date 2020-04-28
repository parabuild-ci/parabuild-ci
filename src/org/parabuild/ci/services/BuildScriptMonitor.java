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
package org.parabuild.ci.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.build.BuildScriptEventSubscriber;
import org.parabuild.ci.build.BuildScriptFinishedEvent;
import org.parabuild.ci.build.BuildScriptStartedEvent;

/**
 */
class BuildScriptMonitor implements BuildScriptEventSubscriber {

  private static final Log log = LogFactory.getLog(BuildScriptMonitor.class);

  private int currentlyRunningHandle = 0;


  /**
   * Called when a command with a given handle started.
   */
  public void scriptStarted(final BuildScriptStartedEvent startedEvent) {
//    if (log.isDebugEnabled()) log.debug("==================================================== startedEvent: " + startedEvent);
    if (currentlyRunningHandle != 0) {
      log.warn("Received script started event when the handle was non-zero");
    }
    currentlyRunningHandle = startedEvent.getHandle();
  }


  /**
   * Called when a command with a given handle finished.
   */
  public void scriptFinished(final BuildScriptFinishedEvent finishedEvent) {
//    if (log.isDebugEnabled()) log.debug("==================================================== finished: " + finishedEvent);
    if (currentlyRunningHandle == 0) {
      log.warn("Received script finished event when the handle was zero");
    }
    final int handle = finishedEvent.getHandle();
    if (currentlyRunningHandle != handle) {
      log.warn("Received script finished event with handle mismatch");
    }
    currentlyRunningHandle = 0; // mark as not running
  }


  /**
   * @return a handle for the currently running command or 0 if not running.
   */
  public int getCurrentlyRunningHandle() {
    return currentlyRunningHandle;
  }


  public String toString() {
    return "BuildScriptMonitor{" +
      "currentlyRunningHandle=" + currentlyRunningHandle +
      '}';
  }
}
