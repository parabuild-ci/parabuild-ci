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
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.ActiveBuildConfig;
import org.parabuild.ci.object.BuildConfig;
import org.parabuild.ci.remote.Agent;
import org.parabuild.ci.remote.AgentManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Implementation of BuildService interface.
 * <p/>
 * The lifecycle of build service is the following - method
 * startupService is called, other methods are called,
 * shutdownService is called.
 */
public final class BuildListServiceImpl implements BuildListService {

  private static final Log log = LogFactory.getLog(BuildListServiceImpl.class);


  private volatile byte status = SERVICE_STATUS_NOT_STARTED;

  private final Map builds = new HashMap(11);


  /**
   * Returns service status
   *
   * @return int service status
   * @see #SERVICE_STATUS_FAILED
   * @see #SERVICE_STATUS_NOT_STARTED
   * @see #SERVICE_STATUS_STARTED
   */
  public synchronized byte getServiceStatus() {
    return status;
  }


  public synchronized void startupService() {

    // init build map
    final List buildsConfigs = ConfigurationManager.getInstance().getExistingBuildConfigsOrderedByID();
    log.debug("Number of build configurations to start: " + buildsConfigs.size());

    for (final Iterator i = buildsConfigs.iterator(); i.hasNext();) {

      registerAndStartupBuildService((BuildConfig) i.next());
    }
    status = SERVICE_STATUS_STARTED;
  }


  /**
   * Shutdowns builds
   */
  public synchronized void shutdownService() {
    for (final Iterator iter = builds.entrySet().iterator(); iter.hasNext();) {
      BuildService build = null;
      try {
        final Map.Entry entry = (Map.Entry) iter.next();
        build = (BuildService) entry.getValue();
        build.shutdownService();
      } catch (final Exception e) {
        reportShutdownException(build, e);
      }
    }
  }


  /**
   * @return ServiceName.BUILDS_SERVICE
   */
  public ServiceName serviceName() {
    return ServiceName.BUILDS_SERVICE;
  }


  private synchronized void registerAndStartupBuildService(final BuildConfig buildConfig) {
    try {

      final BuildService result = new ThroughBuildServiceProxy(new BuildServiceImpl(buildConfig)); // valid/OK, create through
      builds.put(new Integer(buildConfig.getActiveBuildID()), result);
      result.startupService();
    } catch (final Exception e) {
      reportStartupException(buildConfig, e);
    }
  }


  /**
   * Returns a copy of collection of BuildService objects.
   *
   * @return a copy of collection of {@link BuildService} objects
   */
  public synchronized Collection getBuilds() {
    if (status != SERVICE_STATUS_STARTED) {
      throw new IllegalStateException("Build service has not started yet");
    }
    return new LinkedList(builds.values());
  }


  /**
   * Returns the build
   */
  public BuildService getBuild(final int buildID) {
    return (BuildService) builds.get(new Integer(buildID));
  }


  /**
   * Removes build
   */
  public synchronized void removeBuild(final int activeBuildID) {
    if (log.isDebugEnabled()) {
      log.debug("begin removeBuild");
    }

    // mark build deleted
    final ConfigurationManager cm = ConfigurationManager.getInstance();
    cm.markActiveBuildDeleted(activeBuildID);

    // remove from collections
    final BuildService buildServiceToRemove = (BuildService) builds.remove(new Integer(activeBuildID));
    if (buildServiceToRemove == null) {
      throw new IllegalStateException("Build configuration ID " + activeBuildID + " not found.");
    }

    // shutdown
    buildServiceToRemove.shutdownService();

    // send task to delete build files
    final SupportService supportService = ServiceManager.getInstance().getSupportService();
    supportService.executeTask(new Runnable() {
      public void run() {
        try {
          if (log.isDebugEnabled()) {
            log.debug("Will delete activeBuildConfig files");
          }
          final ActiveBuildConfig activeBuildConfig = cm.getActiveBuildConfig(activeBuildID);
          final int builderID = activeBuildConfig.getBuilderID();
          final AgentManager instance = AgentManager.getInstance();
          final List hosts = instance.getLiveAgentHosts(builderID, true);
          for (int i = 0; i < hosts.size(); i++) {
            final AgentHost agentHost = (AgentHost) hosts.get(i);
            final Agent agent = AgentManager.getInstance().createAgent(activeBuildID, agentHost);
            agent.deleteBuildFiles();
          }
          if (log.isDebugEnabled()) {
            log.debug("Deleted activeBuildConfig files");
          }
        } catch (final Exception e) {
          reportDeleteBuildFilesError(e);
        }
      }
    });

    // notify that configuration changed
    notifyConfigurationsChanged();

    if (log.isDebugEnabled()) {
      log.debug("end removeBuild");
    }
  }

  // ==================


  /**
   * Returns a list of current build statuses
   *
   * @return List of BuildStatus objects
   * @see BuildState
   */
  public List getCurrentBuildStatuses() {
    final Collection buildList = getBuilds();
    final List result = new ArrayList(buildList.size());
    for (final Iterator buildIter = buildList.iterator(); buildIter.hasNext();) {
      result.add(((BuildService) buildIter.next()).getBuildState());
    }
    result.sort(BuildState.BUILD_NAME_COMPARATOR);
    return result;
  }


  /**
   * This method is used by build manager clients to notify that
   * there were unspecified changes made in current build set
   * configuration.
   */
  public synchronized void notifyConfigurationsChanged() {

    // collect build IDs that were removed
    boolean deletionsPresent = false;
    final Map buildConfigsMap = ConfigurationManager.getInstance().getBuildConfigurationsMap();
    for (final Iterator i = builds.entrySet().iterator(); i.hasNext();) {
      final Map.Entry entry = (Map.Entry) i.next();
      final Integer buildID = (Integer) entry.getKey();
      if (buildConfigsMap.get(buildID) == null) {
        if (log.isDebugEnabled()) {
          log.debug("BuildManager will remove buildService: " + buildID);
        }
        deletionsPresent = true; // rise change flag
        if (log.isDebugEnabled()) {
          log.debug("end removeBuild");
        }
      }
    }

    // find if there were builds added
    final List existingBuildConfigs = ConfigurationManager.getInstance().getExistingBuildConfigs();
    for (final Iterator i = existingBuildConfigs.iterator(); i.hasNext();) {
      final BuildConfig config = (BuildConfig) i.next();
      if (builds.get(new Integer(config.getActiveBuildID())) == null) {
        registerAndStartupBuildService(config);
      }
    }
  }


    private static void reportDeleteBuildFilesError(final Exception e) {
    final Error error = new Error();
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setSendEmail(false);
    error.setDescription("Error deleting local build files: " + StringUtils.toString(e));
    error.setDetails(e);
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_BUILD);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  private static void reportShutdownException(final BuildService buildService, final Exception e) {
    final Error error = new Error();
    error.setBuildID(buildService.getActiveBuildID());
    error.setDetails(e);
    error.setErrorLevel(Error.ERROR_LEVEL_FATAL);
    error.setSendEmail(false);
    error.setDescription("Error while shutting down buildService");
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_BUILD);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  private static void reportStartupException(final BuildConfig buildConfig, final Exception e) {
    final Error error = new Error();
    error.setBuildName(buildConfig.getBuildName() + ", Build ID: " + buildConfig.getBuildID());
    error.setDescription("Error while starting build service");
    error.setDetails(e);
    error.setErrorLevel(Error.ERROR_LEVEL_FATAL);
    error.setSendEmail(true);
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_BUILD);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  public String toString() {
    return "BuildListServiceImpl{" +
            "status=" + status +
            ", builds=" + builds +
            '}';
  }
}