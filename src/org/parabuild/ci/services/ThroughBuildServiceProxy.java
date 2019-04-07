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
 * A straight-through proxy to use for valid  builds.
 *
 * @noinspection ClassHasNoToStringMethod
 */
public final class ThroughBuildServiceProxy implements BuildServiceProxy {

  private final BuildService delegate;


  public ThroughBuildServiceProxy(final BuildService delegate) {
    this.delegate = delegate;
  }


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
  public void stopBuild(final int userID) {
    delegate.stopBuild(userID);
  }


  public void deactivateBuild(final int userID) {
    delegate.deactivateBuild(-1);
  }


  /**
   * Returns composite information about build status and last
   * complete build.
   *
   * @return current {@link BuildState}
   */
  public BuildState getBuildState() {
    return delegate.getBuildState();
  }


  /**
   * @return this build's build ID
   */
  public int getActiveBuildID() {
    return delegate.getActiveBuildID();
  }


  /**
   * Returns status
   *
   * @return service status
   */
  public byte getServiceStatus() {
    return delegate.getServiceStatus();
  }


  /**
   * @return ServiceName.BUILD_SERVICE
   */
  public ServiceName serviceName() {
    return delegate.serviceName();
  }


  /**
   * Service shutdown
   */
  public void shutdownService() {
    delegate.shutdownService();
  }


  /**
   * Starts this particular build service, including starting
   * BuildRunner and HealthMonitor threads.
   */
  public void startupService() {
    delegate.startupService();
  }


  /**
   * Disabled. Normally activates build.
   */
  public void activate() {
    delegate.activate();
  }


  /**
   * Notifies the build that config has changed externally.
   */
  public void notifyConfigurationChanged() {
    delegate.notifyConfigurationChanged();
  }


  /**
   * Resumes previously stopped/paused build.
   */
  public void resumeBuild() {
    delegate.resumeBuild();
  }


  /**
   * Re-runs build
   *
   * @param startRequest
   */
  public void rerunBuild(final BuildStartRequest startRequest) {
    delegate.rerunBuild(startRequest);
  }


  public void requestCleanCheckout() {
    delegate.requestCleanCheckout();
  }


  public void startBuild(final BuildStartRequest startRequest) {
    delegate.startBuild(startRequest);
  }


  /**
   * @param sinceServerTimeMs
   * @return Log's {@link TailUpdate} since the given time in millis.
   */
  public TailUpdate getTailUpdate(final long sinceServerTimeMs) throws IOException {
    return delegate.getTailUpdate(sinceServerTimeMs);
  }


  public BuildService getDelegate() {
    return delegate;
  }
}
