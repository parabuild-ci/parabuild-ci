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
package org.parabuild.ci.webui.agent.status;

import EDU.oswego.cs.dl.util.concurrent.ConcurrentHashMap;
import org.apache.log4j.Logger;
import org.parabuild.ci.build.AgentFailureException;
import org.parabuild.ci.build.BuildState;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.configuration.BuilderConfigurationManager;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.remote.AgentEnvironment;
import org.parabuild.ci.remote.AgentManager;
import org.parabuild.ci.remote.internal.LocalAgentEnvironment;
import org.parabuild.ci.services.BuildListService;
import org.parabuild.ci.services.Service;
import org.parabuild.ci.services.ServiceManager;
import org.parabuild.ci.services.ServiceName;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class monitors the build agents and collects changes in their statuses to generate agent load charts.
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 24, 2009 2:41:26 PM
 */
public final class AgentsStatusMonitor implements Runnable, Service {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Logger LOG = Logger.getLogger(AgentsStatusMonitor.class); // NOPMD

  private final Map agentHistoryMap = new ConcurrentHashMap(11);
  private Thread pollThread = null;
  private byte serviceStatus = SERVICE_STATUS_NOT_STARTED;


  /**
   * @return a list of {@link AgentStatus} objects.
   */
  public List getStatuses() {

    // Create a copy of the history list
    final List historyList;
    synchronized (this) {
      historyList = new ArrayList(agentHistoryMap.values());
    }

    // Populate result
    final List result = new ArrayList(historyList.size());
    for (int i = 0; i < historyList.size(); i++) {
      final AgentHistory agentHistory = (AgentHistory) historyList.get(i);
      result.add(agentHistory.getAgentStatus());
    }

    // Sort
    Collections.sort(result, AgentStatus.BY_NAME_COMPARATOR);
    return result;
  }


  public void run() {
    // Make sure it is not called from outside.
    //noinspection ObjectEquality
    if (Thread.currentThread() != pollThread) {
      throw new IllegalStateException("This method can be accessed only from the internal poll thread");
    }
    // Run the cycle
    while (!Thread.interrupted()) {
      try {
        updateStatus();
        Thread.sleep(60000L);
      } catch (InterruptedException e) {
        // Exit
        return;
      } catch (Exception e) {
        LOG.warn("Ignored exception while updating agent status: " + StringUtils.toString(e), e);
      }
    }
  }


  /**
   * Updates status.
   */
  private void updateStatus() {
    final BuildListService service = ServiceManager.getInstance().getBuildListService();
    final List buildsStatuses = service.getCurrentBuildStatuses();
    final List agents = BuilderConfigurationManager.getInstance().getAgentList();
    final Set agentIDs = new HashSet(agents.size());
    for (int i = 0; i < agents.size(); i++) {
      final AgentConfig agentConfig = (AgentConfig) agents.get(i);
      final String hostName = agentConfig.getHost();
      final Integer agentConfigID = new Integer(agentConfig.getID());
      agentIDs.add(agentConfigID);
      AgentStatusSample agentStatusSample;
      if (agentConfig.isEnabled()) {
        try {
          final AgentEnvironment env = AgentManager.getInstance().getAgentEnvironment(new AgentHost(hostName));
          final String remoteVersion = env.builderVersionAsString().intern();
          final LocalAgentEnvironment localEnv = new LocalAgentEnvironment();
          final String managerVersion = localEnv.builderVersionAsString();
          if (remoteVersion.equals(managerVersion)) {
            // OK - Calculate busy
            int busyCounter = 0;
            for (int j = 0; j < buildsStatuses.size(); j++) {
              final BuildState buildState = (BuildState) buildsStatuses.get(j);
              final String runningOnHost = buildState.getCurrentlyRunningOnBuildHost();
              if (hostName.equalsIgnoreCase(runningOnHost)) {
                busyCounter++;
              }
            }
            agentStatusSample = busyCounter == 0 ? AgentStatusSample.IDLE : new AgentStatusSample(busyCounter, AgentStatus.ACTIVITY_BUSY);
          } else {
            // Version mismatch
            agentStatusSample = new AgentStatusSample(0, AgentStatus.ACTIVITY_VERSION_MISMATCH, remoteVersion);
            AgentManager.getInstance().upgrade(hostName, managerVersion, remoteVersion);
          }
        } catch (IOException e) {
          agentStatusSample = AgentStatusSample.OFFLINE;
        } catch (AgentFailureException e) {
          agentStatusSample = AgentStatusSample.OFFLINE;
        }
      } else {
        // Disabled
        agentStatusSample = new AgentStatusSample(0, AgentStatus.ACTIVITY_DISABLED);
      }

      // Add sample to the tail of the history
      synchronized (this) {
        AgentHistory history = (AgentHistory) agentHistoryMap.get(agentConfigID);
        if (history == null) {
          history = new AgentHistory(hostName, agentConfig.getID());
          agentHistoryMap.put(agentConfigID, history);
        }
        history.addStatusSample(agentStatusSample);
      }
    }

    // Remove agents that have been deleted
    synchronized (this) {
      for (final Iterator iterator = agentHistoryMap.entrySet().iterator(); iterator.hasNext();) {
        if (!agentIDs.contains(((Map.Entry) iterator.next()).getKey())) {
          iterator.remove();
        }
      }
    }
  }


  public void startupService() {
    if (pollThread != null) {
      return;
    }
    pollThread = new Thread(this);
    pollThread.setDaemon(true);
    pollThread.setName("AgentMonitor");
    pollThread.start();
    serviceStatus = SERVICE_STATUS_STARTED;
  }


  public void shutdownService() {
    if (pollThread != null) {
      pollThread.interrupt();
    }
    serviceStatus = SERVICE_STATUS_NOT_STARTED;
  }


  public ServiceName serviceName() {
    return ServiceName.AGENT_STATUS_MONITOR;
  }


  public byte getServiceStatus() {
    return serviceStatus;
  }


  public AgentStatus getStatus(final int agentID) {
    final AgentHistory history;
    synchronized (this) {
      history = (AgentHistory) agentHistoryMap.get(new Integer(agentID));
      if (history == null) {
        return null;
      }
    }
    return history.getAgentStatus();
  }


  /**
   * Provides an early notification to agent status monitor that an agent has been deleted.
   *
   * @param agentID agent ID
   */
  public void notifyAgentDeleted(final int agentID) {
    agentHistoryMap.remove(new Integer(agentID));
  }


  /**
   * Provides a notification to agent status monitor that appearance of the images changed.
   */
  public synchronized void notifyPresentationChanged() {
    for (final Iterator iterator = agentHistoryMap.values().iterator(); iterator.hasNext();) {
      final AgentHistory agentHistory = (AgentHistory) iterator.next();
      agentHistory.reinitializeImageGenerator();
    }
  }


  public String toString() {
    return "AgentsStatusMonitor{" +
            "agentHistoryMap=" + agentHistoryMap +
            ", pollthread=" + pollThread +
            ", serviceStatus=" + serviceStatus +
            '}';
  }
}
