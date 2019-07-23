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

import org.parabuild.ci.Version;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;
import org.parabuild.ci.common.ThreadUtils;
import org.parabuild.ci.configuration.ConfigurationFile;
import org.parabuild.ci.configuration.ConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;

import javax.servlet.GenericServlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * ServiceManagerServlet servlet is responsible for starting
 * build manager services. It does not accept incoming calls.
 */
public final class ServiceManagerServlet extends GenericServlet {

  private static final long serialVersionUID = 6247201370637762943L; // NOPMD
  private final ServiceManager serviceManager = ServiceManager.getInstance();
  //  private static final Log log = LogFactory.getLog(ServiceManagerServlet.class);
  private volatile boolean destroyed = false; // NOPMD


  /**
   * Displays console message that the service successfully
   * started.
   */
  private static void printServiceStartedToConsole() {
    if (ConfigurationManager.isBuilderMode()) { // NOPMD
      IoUtils.printToStdout("Parabuild service started in remote agent mode");
      IoUtils.printToStdout("Remote agent accepts requests from address \"" + ConfigurationFile.getInstance().getBuildManagerAddress() + '\"');
    } else { // NOPMD
      final String hostNameAndPort = SystemConfigurationManagerFactory.getManager().getBuildManagerProtocolHostAndPort();
      IoUtils.printToStdout("Parabuild service started");
      IoUtils.printToStdout("Build manager is available at " + hostNameAndPort);
    } // NOPMD
  }


  /**
   * Reports service shutdown.
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
   * Displays console message that the service failed to start.
   */
  private static void printFailedToStartToConsole(final Exception e) {
    IoUtils.printToStdout(Version.versionToString(true));
    IoUtils.printToStdout("Error starting Parabuild: " + StringUtils.toString(e));
    IoUtils.printToStdout(e);
    IoUtils.printToStdout("Service Parabuild has NOT started");
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


  public void init(final ServletConfig servletConfig) throws ServletException {
    super.init(servletConfig);
    final String startingMessage = "Starting " + Version.versionToString(true);
    IoUtils.printToStdout(startingMessage);

    // init shutdown hook
    final Runnable shutdownHook = new ShutdownHook(this);
    final Thread shutdownHookThread = ThreadUtils.makeDaemonThread(shutdownHook, "Shutdown Runner");
    Runtime.getRuntime().addShutdownHook(shutdownHookThread);

    // startup services
    try {

      // Get listen port
      final ListenPortConfig listenPortConfig = new ListenPortConfig();
      final int listenPort = listenPortConfig.getListenPort();

      // Start services
      serviceManager.setListenPort(listenPort);
      serviceManager.startServices();
      printServiceStartedToConsole();
    } catch (final Exception e) {
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
      serviceManager.stopServices();
    } catch (final Exception e) {
      // We catch this exception in order to prevent failing this method.
      reportServiceManagerError(e, "stopping", Error.ERROR_LEVEL_WARNING);
    } finally {
      destroyed = true;
    }
    super.destroy();
  }


  public String toString() {
    return "ServiceManagerServlet{" +
            "destroyed=" + destroyed +
            '}';
  }
}
