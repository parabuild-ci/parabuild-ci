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
import org.parabuild.ci.configuration.SystemConfigurationManager;
import org.parabuild.ci.configuration.SystemConfigurationManagerFactory;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.object.SystemProperty;

/**
 *
 * @noinspection StaticFieldReferencedViaSubclass
 */
public final class LoggingService implements Service {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(LoggingService.class); // NOPMD

  private byte status = Service.SERVICE_STATUS_NOT_STARTED;


  /**
   * Returns serivce status
   *
   */
  public byte getServiceStatus() {
    return status;
  }


  public void shutdownService() {
  }


  public ServiceName serviceName() {
    return ServiceName.LOGGING_SERVICE;
  }


  public void startupService() {
    initLog4j();
    status = Service.SERVICE_STATUS_STARTED;
  }


  /**
   * Sets up dynamic log4j configuration. Log4j configuration is
   * presented in two kinds - static log4.properties file
   * packaged into autobuild.war file, and dinamic, defined in
   * the database. This method is called after
   * ConfigurationManager is prepared.
   */
  private void initLog4j() {
    try {

      // check if it is a test environment
      final SystemConfigurationManager systemCM = SystemConfigurationManagerFactory.getManager();
      if (systemCM.getSystemPropertyValue(SystemProperty.ENABLE_TEST_LOGGING, SystemProperty.OPTION_UNCHECKED)
        .equals(SystemProperty.OPTION_CHECKED)) {
        return;
      }
      // proceed
      final String value = systemCM.getSystemPropertyValue(SystemProperty.ENABLE_DEBUGGING, SystemProperty.OPTION_UNCHECKED);
      if (value.equals(SystemProperty.OPTION_CHECKED)) {
        Log4jConfigurator.getInstance().initialize(true);
      } else {
        // Still enable if this is an agent
        final ConfigurationFile cf = ConfigurationFile.getInstance();
        if (!StringUtils.isBlank(cf.getBuildManagerAddress()) && cf.isAgentDebugOutputEnabled()) {
          Log4jConfigurator.getInstance().initialize(true);
        }
      }
    } catch (final Exception e) {
      reportException(e);
    }
  }


  /** @noinspection WeakerAccess*/
  public void reInitLog4j(final boolean debug) {
    try {
      Log4jConfigurator.getInstance().initialize(debug);
    } catch (final Exception e) {
      reportException(e);
    }
  }


  /**
   * Reports logging service startup exceptions
   */
  private void reportException(final Exception e) {
    final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
    final Error error = new Error("Unexpected condifition when starting logging service");
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_BUILD);
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setDetails(e);
    error.setSendEmail(false);
    errorManager.reportSystemError(error);
  }


  public String toString() {
    return "LoggingService{" +
      "status=" + status +
      '}';
  }
}


