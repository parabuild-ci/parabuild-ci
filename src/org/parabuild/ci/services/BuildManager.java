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
import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.build.BuildStatus;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.object.ActiveBuild;
import org.parabuild.ci.remote.AgentManager;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Build manager is a business interface to build service
 */
public final class BuildManager implements Serializable {

  private static final long serialVersionUID = -891255148312458166L; // NOPMD
  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(BuildManager.class); //NOPMD

  /**
   * Singleton instance
   */
  private static final BuildManager instance = new BuildManager();


  public static BuildManager getInstance() {
    return instance;
  }


  /**
   * Reference to build service.
   */
  private BuildListService buildListService = null;


  /**
   * Singleton constructor.
   */
  private BuildManager() {
    buildListService = ServiceManager.getInstance().getBuildListService();
  }


  /**
   * Returns a list of current build statuses
   *
   * @return List of BuildStatus objects
   * @see BuildState
   */
  public List getCurrentBuildsStatuses() {
    return buildListService.getCurrentBuildStatuses();
  }


  /**
   * Activates build with givenID if its status is
   * BuildStatus.INACTIVE. Otherwise does nothing.
   */
  public void activateBuild(final int buildID) {
    final ActiveBuild activeBuild = ConfigurationManager.getInstance().getActiveBuild(buildID);
    if (activeBuild.getStartupStatus() == BuildStatus.INACTIVE_VALUE) {
      buildListService.getBuild(buildID).activate();
    }
  }


  /**
   * Deactivates the build.
   */
  public void deactivateBuild(final int buildID, final int userID) {
    // NOTE: vimeshev - this covers case when the buildService is not
    // there - that should be refactored/removed when a build
    // manager is made per-buildService.
    final BuildService buildService = buildListService.getBuild(buildID);
    if (buildService == null) {
      return;
    }
    buildService.deactivateBuild(userID);
  }


  /**
   * Stops a build
   *
   * @noinspection UnusedDeclaration
   */
  public void stopBuild(final int buildID, final int userID) {
    // NOTE: vimeshev - this covers case when the buildService is not
    // there - that should be refactored/removed when a build
    // manager is made per-buildService.
    final BuildService buildService = buildListService.getBuild(buildID);
    if (buildService == null) {
      return;
    }
    buildService.stopBuild(userID);
  }


  /**
   * This method is used by build manager clients to notify that
   * there were unspecified changes made in current build set
   * configuration.
   */
  public void notifyConfigurationsChanged() {
    buildListService.notifyConfigurationsChanged();
  }


  /**
   * Clients call this method to notify BuildManager that
   * configuration for the given buildID has been changed.
   */
  public void notifyConfigurationChanged(final int buildID) {
    // NOTE: vimeshev - this covers case when the buildService is not
    // there - that should be refactored/removed when a build
    // manager is made per-buildService.
    final BuildService buildService = buildListService.getBuild(buildID);
    if (buildService == null) {
      return;
    }
    buildService.notifyConfigurationChanged();
  }


  /**
   * Resumes build.
   *
   * @param buildID
   */
  public void resumeBuild(final int buildID) {
    // NOTE: vimeshev - this covers case when the buildService is not
    // there - that should be refactored/removed when a build
    // manager is made per-buildService.
    final BuildService buildService = buildListService.getBuild(buildID);
    if (buildService == null) {
      return;
    }
    buildService.resumeBuild();
  }


  /**
   * Requests to re-run given build number.
   *
   * @param activeBuildID
   * @param startRequest
   */
  public void reRunBuild(final int activeBuildID, final BuildStartRequest startRequest) {
    // NOTE: vimeshev - this covers case when the buildService is not
    // there - that should be refactored/removed when a build
    // manager is made per-buildService.
    final BuildService buildService = buildListService.getBuild(activeBuildID);
    if (buildService == null) {
      return;
    }
    buildService.rerunBuild(startRequest);
  }


  public void startBuild(final int activeBuildID, final BuildStartRequest startRequest) {
    // NOTE: vimeshev - this covers case when the buildService is not
    // there - that should be refactored/removed when a build
    // manager is made per-buildService.
    final BuildService buildService = buildListService.getBuild(activeBuildID);
    if (buildService == null) {
      return;
    }
    buildService.startBuild(startRequest);
  }


  /**
   * Returns tail update or
   *
   * @param activeBuildID
   * @param sinceServerTime
   * @return
   * @throws IOException
   */
  public TailUpdate getTailUpdate(final int activeBuildID, final long sinceServerTime) throws IOException {
    final BuildService buildService = buildListService.getBuild(activeBuildID);
    if (buildService == null) {
      return TailUpdateImpl.EMPTY_UPDATE;
    }

    return buildService.getTailUpdate(sinceServerTime);
  }


  /**
   * Returns a list of AgentHost objects.
   *
   * @param activeBuildID a build configuration ID for that to get free agents.
   * @return returns a list of AgentHost objects.
   */
  public List getFreeAgentHosts(final int activeBuildID) {

    // NOTE: simeshev@parabuilci.org - 2009-02-20 - The idea is to correlate
    // the agents configured for this builder and all busy agents.

    final ConfigurationManager cm = ConfigurationManager.getInstance();
    final int builderID = cm.getActiveBuildConfig(activeBuildID).getBuilderID();

    // Iterate through the agentHosts of configured builder agents
    final List agentHosts = new ArrayList(AgentManager.getInstance().getLiveAgentHosts(builderID, false));
    final List currentBuildsStatuses = getCurrentBuildsStatuses();
    for (final Iterator liveAgentIter = agentHosts.iterator(); liveAgentIter.hasNext();) {

      // Check if busy
      final AgentHost agentHost = (AgentHost) liveAgentIter.next();
      for (int j = 0, n = currentBuildsStatuses.size(); j < n; j++) {

        final BuildState buildState = (BuildState) currentBuildsStatuses.get(j);
        final String theirHost = buildState.getCurrentlyRunningOnBuildHost();
        if (buildState.getActiveBuildID() != activeBuildID && buildState.isBusy() && agentHost.getHost().equalsIgnoreCase(theirHost)) {

          // Remove busy
          liveAgentIter.remove();
          break;
        }
      }
    }

    return agentHosts;
  }


  public String toString() {
    return "BuildManager{" +
            "buildListService=" + buildListService +
            '}';
  }
}
