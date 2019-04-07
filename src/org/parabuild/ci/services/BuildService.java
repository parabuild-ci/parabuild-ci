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

import org.parabuild.ci.build.BuildState;

import java.io.IOException;

/**
 * Created by simeshev on Sep 16, 2005 at 2:47:02 PM
 */
public interface BuildService extends Service {

  /**
   * Stops the build. Stopping can happen when a user selects
   * "Stop" command for the given build. As build itself consists
   * of a scheduler and a build runner, both need to be stopped.
   * <p/>
   * Semantics of the stop for a scheduler depends on a
   * scheduler:
   * <p/>
   * o  Automatic schedulers runs in a loop constantly checking
   * for changes spanning OS processes (via SourceControl).
   * Automatic scheduler should move into "paused" state after
   * stop.
   * <p/>
   * o Scheduled scheduler should just stop current processing if
   * any and start at next scheduled time as normally.
   *
   * @param userID
   */
  void stopBuild(final int userID);


  /**
   * Deactivates build.
   *
   * @param userID user that deactivated the build
   */
  void deactivateBuild(final int userID);


  /**
   * Returns composite information about build status and last
   * complete build.
   */
  BuildState getBuildState();


  /**
   * @return this build's build ID
   */
  int getActiveBuildID();


  /**
   * Returns status
   */
  byte getServiceStatus();


  /**
   * @return ServiceName.BUILD_SERVICE
   */
  ServiceName serviceName();


  /**
   * Service shutdown
   */
  void shutdownService();


  /**
   * Starts this particular build service, including starting
   * BuildRunner and HealthMonitor threads.
   */
  void startupService();


  /**
   * Activates build
   */
  void activate();


  /**
   * Notifies the build that config has changed externally.
   */
  void notifyConfigurationChanged();


  /**
   * Resumes previously stopped/paused build.
   */
  void resumeBuild();


  /**
   * Re-runs build
   *
   * @param startRequest
   */
  void rerunBuild(final BuildStartRequest startRequest);


  /**
   * Requests that next checkout runs cleanly.
   */
  void requestCleanCheckout();


  /**
   * Starts the build manually
   */
  void startBuild(final BuildStartRequest startRequest);


  /**
   * @param sinceServerTimeMs
   * @return Log's {@link TailUpdate} since the given time in millis.
   */
  TailUpdate getTailUpdate(final long sinceServerTimeMs) throws IOException;
}
