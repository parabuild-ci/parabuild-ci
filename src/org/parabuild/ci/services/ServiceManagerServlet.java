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
import org.parabuild.ci.Version;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ThreadUtils;
import org.parabuild.ci.configuration.ConfigurationFile;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;

import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.util.Iterator;
import java.util.Set;

/**
 * ServiceManagerServlet servlet is responsible for starting
 * build manager services. It does not accept incoming calls.
 */
public final class ServiceManagerServlet extends GenericServlet {

  private static final long serialVersionUID = 6247201370637762943L; // NOPMD
  private static final Log log = LogFactory.getLog(ServiceManagerServlet.class);
  private boolean destroyed = false; // NOPMD


  public void init(final ServletConfig servletConfig) throws ServletException {
    super.init(servletConfig);
    final String startingMessage = "Starting " + Version.versionToString(true);
    printToStdout(startingMessage);
    log.info(startingMessage);

    // init shutdown hook
    final Runnable shutdownHook = new Runnable() {
      public void run() {
        final String shuttingDownMessage = "Shutting down " + Version.versionToString(true);
        printToStdout(shuttingDownMessage);
        log.info(shuttingDownMessage);
        destroy();
      }
    };
    final Thread shutdownHookThread = ThreadUtils.makeDaemonThread(shutdownHook, "Shutdown Runner");
    Runtime.getRuntime().addShutdownHook(shutdownHookThread);

    // startup services
    try {
      final ServiceManager serviceManager = ServiceManager.getInstance();
      serviceManager.setListenPort(getListenPort());
      serviceManager.startServices();
      printServiceStartedToConsole();
    } catch (Exception e) {
      // We catch this exception in order to prevent init method.
      reportServiceManagerError(e, "starting", Error.ERROR_LEVEL_FATAL);
      printFailedToStartToConsole(e);
    }
  }


  /**
   * Implementation of the GenericServlet service() method. This
   * method does nothing as ServiceManager servlet is used only
   * for service startup purposes.
   *
   * @param servletRequest
   * @param servletResponse
   */
  public void service(final ServletRequest servletRequest, final ServletResponse servletResponse) {
  }


  public synchronized void destroy() {
    if (destroyed) {
      return;
    }
    try {
      reportServiceRequestedToStop();
      final ServiceManager serviceManager = ServiceManager.getInstance();
      serviceManager.stopServices();
    } catch (Exception e) {
      // We catch this exception in order to prevent failing this method.
      reportServiceManagerError(e, "stopping", Error.ERROR_LEVEL_WARNING);
    } finally {
      destroyed = true;
    }
    super.destroy();
  }


  /**
   * Retrieves HTTP listen port used by the app. It assumes there
   * is only one HTTP connector.
   */
  private int getListenPort() {
    int port = 8080;
    try {
      // get connectors
      final MBeanServer mbeanServer = (MBeanServer) getServletContext().getAttribute("org.apache.catalina.MBeanServer");
//      if (log.isDebugEnabled()) log.debug("server: " + mbeanServer);
      final ObjectName objectName = new ObjectName("Catalina:type=Connector,*");
      final Set mbeans = mbeanServer.queryMBeans(objectName, null);
      // find http connector
      for (final Iterator i = mbeans.iterator(); i.hasNext();) {
        final ObjectInstance mbean = (ObjectInstance) i.next();
//        if (log.isDebugEnabled()) log.debug("mbean: " + mbean);
//        if (log.isDebugEnabled()) log.debug("mbean.getClassName(): " + mbean.getClassName());
//        if (log.isDebugEnabled()) log.debug("mbean.getObjectName(): " + mbean.getObjectName());
//        if (log.isDebugEnabled()) log.debug("connector className: " + mbeanServer.getAttribute(mbean.getObjectName(), "className"));
        final String handlerClassName = (String) mbeanServer.getAttribute(mbean.getObjectName(), "className");
        // is it HTTP connector?
        if (handlerClassName.endsWith("HttpConnector")) {
          port = ((Integer) mbeanServer.getAttribute(mbean.getObjectName(), "port")).intValue();
          if (log.isDebugEnabled()) {
            log.debug("port: " + port);
          }
          break;
        }
      }
    } catch (Exception e) {
      // just log error, default value will be used
      log.error("Error while getting connector list", e);
    }
    return port;
  }


  /**
   * Displays console messgate that the service successfully
   * started.
   *
   * @noinspection UseOfSystemOutOrSystemErr
   */
  private static void printServiceStartedToConsole() {
    if (ConfigurationManager.isBuilderMode()) { // NOPMD
      printToStdout("Parabuild service started in remote agent mode");
      printToStdout("Remote agent accepts requests from address \"" + ConfigurationFile.getInstance().getBuildManagerAddress() + '\"');
    } else { // NOPMD
      final String hostNameAndPort = SystemConfigurationManagerFactory.getManager().getBuildManagerProtocolHostAndPort();
      printToStdout("Parabuild service started");
      printToStdout("Build manager is available at " + hostNameAndPort);
    } // NOPMD
  }


  /**
   * Reports service shutdown.
   *
   * @noinspection UseOfSystemOutOrSystemErr
   */
  private static void reportServiceRequestedToStop() {

    if (ConfigurationManager.isBuilderMode()) {
      return;
    }

    final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
    final Error message = new Error("Parabuild was requested shutdown");
    message.setErrorLevel(Error.ERROR_LEVEL_INFO);
    message.setSendEmail(false);
    errorManager.reportSystemError(message);
  }


  /**
   * Displays console messgate that the service failed to start.
   *
   * @noinspection UseOfSystemOutOrSystemErr
   */
  private static void printFailedToStartToConsole(final Exception e) {
    printToStdout(Version.versionToString(true));
    printToStdout("Error starting Parabuild: " + StringUtils.toString(e));
    printToStdout(e);
    printToStdout("Service Parabuild has NOT started");
  }


  /**
   * Reports service manager errors to system wide error log.
   *
   * @param e
   * @param whileString
   * @param errorLevel
   */
  private static void reportServiceManagerError(final Exception e, final String whileString, final byte errorLevel) {
    final Error error = new Error("Error while " + whileString + " Parabuild services");
    error.setPossibleCause("Please report this error to technical support");
    error.setErrorLevel(errorLevel);
    error.setSendEmail(false);
    error.setDetails(e);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  private static void printToStdout(final String message) {
    System.out.println(message); // NOPMD
  }


  private static void printToStdout(final Throwable th) {
    th.printStackTrace(System.out);  // NOPMD
  }


  public String toString() {
    return "ServiceManagerServlet{" +
            "destroyed=" + destroyed +
            '}';
  }
}
