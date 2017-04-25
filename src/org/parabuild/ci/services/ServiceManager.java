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
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.configuration.ConfigurationFile;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.merge.MergeService;
import org.parabuild.ci.merge.MergeServiceImpl;
import org.parabuild.ci.webui.UIServlet;
import org.parabuild.ci.webui.agent.status.AgentsStatusMonitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service manager singletone class is responsible for BT
 * services startupService and shutdownService.
 */
public final class ServiceManager {

  private static final Log log = LogFactory.getLog(ServiceManager.class);
  private static final ServiceManager instance = new ServiceManager();

  private int listenPort = 8080;
  private final List serviceList = new ArrayList(5);
  private final Map serviceByName = new HashMap(5);


  /**
   * Default singletone constructor
   */
  private ServiceManager() {
    // we start services below only when we run as a build manager.
    if (StringUtils.isBlank(ConfigurationFile.getInstance().getBuildManagerAddress())) {
      addServiceInstance(new SupportService());
      addServiceInstance(new DatabaseService());
      addServiceInstance(new ConfigurationService());
      addServiceInstance(new LoggingService());
      addServiceInstance(new NotificationService());
      addServiceInstance(new SearchServiceImpl());
      addServiceInstance(new BuildListServiceImpl());
      addServiceInstance(new AgentsStatusMonitor());
      addServiceInstance(new MergeServiceImpl());
      addServiceInstance(new ResultCleanupService());
    }
  }


  /**
   * Adds a service to the list of services managed by
   * Servicemanager
   *
   * @param service to add
   */
  private void addServiceInstance(final Service service) {
    serviceList.add(service);
    final String serviceName = service.serviceName().toString();
    serviceByName.put(serviceName, service);
  }


  /**
   * Start BT services. This method is called once when the
   * UIServlet is initialized.
   *
   * @see UIServlet
   */
  public void startServices() {
    for (int index = 0, n = serviceList.size(); index < n; index++) {
      final Service service = (Service) serviceList.get(index);
      if (log.isDebugEnabled()) log.debug("Starting " + service.serviceName());
      service.startupService();
    }

    reportServiceStarted();
  }


  /**
   * Stops BT services. This method is called when the services
   * servlet is stopped. Services are stopped in a backward order
   * they were started.
   */
  public void stopServices() {
    for (int index = serviceList.size() - 1; index >= 0; index--) {
      final Service service = (Service) serviceList.get(index);

// REVIEWME: simeshev@parabuilci.org -> Commented until the issue # 1207 is investigated.
//      if (log.isDebugEnabled()) log.debug("Shutting down " + service.serviceName());
      System.out.println("Shutting down " + service.serviceName()); // NOPMD
      try {
        service.shutdownService();
      } catch (Throwable e) {
//        log.warn("Exception while shutting down service: " + StringUtils.toString(e), e);
        System.err.println("Exception while shutting down service: " + StringUtils.toString(e)); // NOPMD
        e.printStackTrace(System.err); // NOPMD
      }
    }
  }


  /**
   * @return ServiceManager singleton instance
   */
  public static synchronized ServiceManager getInstance() {
    return instance;
  }


  /**
   * Returns database service.
   *
   * @see DatabaseService
   */
  public DatabaseService getDatabaseService() {
    return (DatabaseService) getService(ServiceName.DATABASE_SERVICE);
  }


  /**
   * Returns configuration service.
   *
   * @see ConfigurationService
   */
  public ConfigurationService getConfigurationService() {
    return (ConfigurationService) getService(ServiceName.CONFIGURATION_SERVICE);
  }


  /**
   * Returns build service.
   *
   * @see BuildListService
   */
  public BuildListService getBuildListService() {
    return (BuildListService) getService(ServiceName.BUILDS_SERVICE);
  }


  /**
   * @return SupportService
   * @see SupportService
   */
  public SupportService getSupportService() {
    return (SupportService) getService(ServiceName.SUPPORT_SERVICE);
  }


  /**
   * Returns search service.
   *
   * @see SearchService
   */
  public SearchService getSearchService() {
    return (SearchService) getService(ServiceName.SEARCH_SERVICE);
  }


  /**
   * Returns logging service.
   *
   * @see LoggingService
   */
  public LoggingService getLoggingService() {
    return (LoggingService) getService(ServiceName.LOGGING_SERVICE);
  }


  /**
   * Returns notification service.
   *
   * @see NotificationService
   */
  public NotificationService getNotificationService() {
    return (NotificationService) getService(ServiceName.NOTIFICATION_SERVICE);
  }


  /**
   * Returns startup listen port
   */
  public int getListenPort() {
    return listenPort;
  }


  /**
   * Sets startup listen port
   */
  public void setListenPort(final int listenPort) {
    this.listenPort = listenPort;
  }


  /**
   * Returns service by name
   */
  private Service getService(final ServiceName serviceName) {
    final Service service = (Service) serviceByName.get(serviceName.toString());
    if (service == null)
      throw new IllegalArgumentException("Service \"" + serviceName.toString() + "\" is not defined.");
    return service;
  }


  public MergeService getMergeService() {
    return null;  //To change body of created methods use File | Settings | File Templates.
  }


  public AgentsStatusMonitor getAgentStatusMonitor() {
    return (AgentsStatusMonitor) getService(ServiceName.AGENT_STATUS_MONITOR);
  }


  /**
   * Reports service startup.
   *
   * @noinspection UseOfSystemOutOrSystemErr
   */
  private static void reportServiceStarted() {

    if (ConfigurationManager.isBuilderMode()) {
      return;
    }

    final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
    final org.parabuild.ci.error.Error message = new Error("Parabuild was requested to start");
    message.setErrorLevel(Error.ERROR_LEVEL_INFO);
    message.setSendEmail(false);
    errorManager.reportSystemError(message);
  }


  public String toString() {
    return "ServiceManager{" +
            "listenPort=" + listenPort +
            ", serviceList=" + serviceList +
            ", serviceByName=" + serviceByName +
            '}';
  }
}

