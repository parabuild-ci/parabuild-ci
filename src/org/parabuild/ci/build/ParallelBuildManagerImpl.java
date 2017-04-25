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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.BuildRun;
import org.parabuild.ci.services.BuildListService;
import org.parabuild.ci.services.BuildManager;
import org.parabuild.ci.services.BuildService;
import org.parabuild.ci.services.BuildStartRequest;
import org.parabuild.ci.services.ServiceManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Responsible for handlind dependent builds running in
 * parallel with a leading one.
 */
final class ParallelBuildManagerImpl extends ParallelBuildManager {

  private static final Log log = LogFactory.getLog(ParallelBuildManagerImpl.class);

  private static final String NO_LABEL = "";

  private final int leadingActiveBuildID;
  private final int leadingBuildRunID;

  private final List startedBuildIDList = new ArrayList(11);


  ParallelBuildManagerImpl(final BuildRun leadingBuildRun) {
    this.leadingActiveBuildID = leadingBuildRun.getActiveBuildID();
    this.leadingBuildRunID = leadingBuildRun.getBuildRunID();
  }


  /**
   * Requests dependent (lead) parallel builds to start.
   * Only builds called that are active at the moment when
   * this method is called.
   * <p/>
   * If this build run is not
   *
   * @param leadingStartRequest a request that was used to invoke the leading build run ID
   * @param leadingChangeListID
   */
  public int startDependentBuilds(final BuildStartRequest leadingStartRequest, final int leadingChangeListID) {
    if (log.isDebugEnabled()) {
      log.debug("leadingStartRequest: " + leadingStartRequest);
    }
    // verify state
    if (!startedBuildIDList.isEmpty()) {
      throw new IllegalStateException("It is not allowed to request starting dependent builds twice.");
    }

    // REVIEWME: vimeshev - 2006-12-26 - as this is done in
    // an asyncronuous fasion, we request builds to start
    // and exit. Ideally we would not exit until all builds
    // have left "waiting" state. Whether they start
    // building is a different story (there could be
    // errors). There should be some synchgronous protocol,
    // something like XA: prepare -> ready (guaraneed to
    // start) -> start.
    //
    // In other words, we need a comfirmation that everyone
    // has started
    //

    final List buildIDListToStart = new ArrayList(11);

    // find builds to start

    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final BuildListService buildListService = ServiceManager.getInstance().getBuildListService();
    final List configuredParallelBuildList = cm.getDependentParallelBuildIDs(leadingActiveBuildID);
    for (final Iterator i = configuredParallelBuildList.iterator(); i.hasNext();) {
      final Integer dependentBuildID = (Integer) i.next();
      final BuildService dependentBuildService = buildListService.getBuild(dependentBuildID.intValue());
      if (dependentBuildService != null) {
        final BuildState buildState = dependentBuildService.getBuildState();
        final BuildStatus status = buildState.getStatus();
        if (log.isDebugEnabled()) {
          log.debug("status for build \"" + buildState.getBuildName() + "\" is " + status);
        }
        if (status.equals(BuildStatus.IDLE)) {
          if (log.isDebugEnabled()) {
            log.debug("adding build \"" + buildState.getBuildName() + "\" to the start list");
          }
          buildIDListToStart.add(dependentBuildID);
        }
      }
    }

    // start found builds
    for (int i = 0; i < buildIDListToStart.size(); i++) {
      final Integer dependentBuildID = (Integer) buildIDListToStart.get(i);

      // compose request
      final BuildStartRequest buildStartRequest = new BuildStartRequest(
              BuildStartRequest.REQUEST_PARALLEL,
              leadingStartRequest.userID(),
              leadingChangeListID,
              leadingBuildRunID,
              leadingStartRequest.parameterList(),
              NO_LABEL,
              leadingStartRequest.getNote(),
              leadingStartRequest.isPinResult(),
              leadingStartRequest.versionTemplate(),
              leadingStartRequest.versionCounter(),
              leadingStartRequest.sourceControlSettingsOverwriteList()
      );
      buildStartRequest.setCleanCheckout(leadingStartRequest.isCleanCheckout());
      buildStartRequest.setIgnoreSerialization(leadingStartRequest.isIgnoreSerialization());

      // start
      if (log.isDebugEnabled()) {
        log.debug("starting build ID: " + dependentBuildID);
      }
      if (log.isDebugEnabled()) {
        log.debug("buildStartRequest: " + buildStartRequest);
      }
      BuildManager.getInstance().startBuild(dependentBuildID.intValue(), buildStartRequest);
      startedBuildIDList.add(dependentBuildID);
    }
    return startedBuildIDList.size();
  }


  /**
   * Waits for dependent builds to stop. If this method has
   * already been called it has no effect and exits immediately.
   */
  public void waitForDependentBuildsToStop() throws InterruptedException {
    // REVIEWME: vimeshev - 2006-12-27 - This method uses
    // polling which may be not a good idea because
    // invariant (is busy) may be not good enough.
    //
    // Consider replacing with latches. Each build may have
    // a latch that is closes when it starts and opens when
    // it finilizes.
    final BuildListService buildListService = ServiceManager.getInstance().getBuildListService();
    boolean someAreBusy = true;
    while (someAreBusy) {

      // for the beginning of the poll assume none is busy
      someAreBusy = false;
      for (int i = 0; i < startedBuildIDList.size(); i++) {
        final Integer dependentBuildID = (Integer) startedBuildIDList.get(i);
        final BuildService build = buildListService.getBuild(dependentBuildID.intValue());
        if (build == null) {
          continue;
        }
        final BuildState buildState = build.getBuildState();
        if (buildState.isBusy()) {
          // rise the flag
          someAreBusy = true;
          break;
        }
      }

      // wait
      if (someAreBusy) {
        Thread.sleep(1000L);
      }
    }
  }


  public int startedCount() {
    return startedBuildIDList.size();
  }


  public String toString() {
    return "ParallelBuildManagerImpl{" +
            "leadingActiveBuildID=" + leadingActiveBuildID +
            ", leadingBuildRunID=" + leadingBuildRunID +
            ", startedBuildIDList=" + startedBuildIDList +
            '}';
  }
}
