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
package org.parabuild.ci.installer;

import com.install4j.api.ServiceInfo;
import com.install4j.api.Util;
import com.install4j.api.actions.AbstractInstallAction;
import com.install4j.api.context.InstallerContext;
import com.install4j.api.context.UserCanceledException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.RuntimeUtils;
import org.parabuild.ci.common.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Properties;

/**
 * This action runs before general file level installation of
 * Parabuild is completed.
 */
public final class ParabuildPreInstallAction extends AbstractInstallAction implements Serializable {

  private static final long serialVersionUID = 1L;
  private static final Log LOG = LogFactory.getLog(ParabuildPreInstallAction.class); // NOPMD
  /**
   * @noinspection HardcodedFileSeparator
   */
  private static final String ETC_CONF_SERVER_XML = "etc/conf/server.xml";

  /**
   * A variable to use to let Parabuild know whether to install the service
   */
  private static final String VARIABLE_INSTALL_SERVICE = "installService";


  /**
   * {@inheritDoc}
   */
  public boolean install(final InstallerContext context) throws UserCanceledException {
    try {

      // Initially set to true (install)
      context.setVariable(VARIABLE_INSTALL_SERVICE, Boolean.TRUE);

      // Disable installing the service if it is already installed to preserve service's user
      if (Util.isWindows()) {
        final File serviceFile = new File(context.getInstallationDirectory(), "bin/parabuild.exe");
        final boolean serviceInstalled = ServiceInfo.isServiceInstalled(serviceFile);
        context.setVariable(VARIABLE_INSTALL_SERVICE, Boolean.valueOf(!serviceInstalled));
      }

      // Check if we have anything to do
      if (!databaseFileExists(context)) {
        return true;
      }

      // Prepare
      validateServerIsNotRunning(context);

      // Is this a remote builder?
      boolean buildManager = true;
      final File confPath = new File(context.getInstallationDirectory(), "etc/system/parabuild.conf");
      if (confPath.exists()) {
        InputStream is = null;
        try {
          is = new FileInputStream(confPath);
          final Properties props = new Properties();
          props.load(is);
          buildManager = StringUtils.isBlank(props.getProperty("parabuild.build.manager.ipaddress", ""));
        } finally {
          IoUtils.closeHard(is);
        }
      }

      // Get database directory
      final File databaseDirectory = makeDatabaseDirectoryPath(context.getInstallationDirectory());

      // Check version
      final Properties databaseProps = InstallerUtils.loadDatabaseProperties(databaseDirectory);
      final String property = databaseProps.getProperty(InstallerConstants.HSQLDB_ORIGINAL_VERSION);
      if (property.indexOf(InstallerConstants.STR_1_8) == 0) {
        // Already upgraded to HSQLDB 1.8.

        return true;
      } else if (property.indexOf(InstallerConstants.STR_1_7) == 0) {


        // Upgrade db version
        upgradeDatabaseVersion(context, databaseDirectory);

        return true;
      } else {
        throw new IOException("Unknown database version: " + property);
      }
    } catch (final IOException e) {
      context.getProgressInterface().showFailure(StringUtils.toString(e));
      throw makeUserCancelledException(e);
    } catch (final UserCanceledException e) {
      context.getProgressInterface().showFailure(StringUtils.toString(e));
      throw e;
    }
  }


  private static void upgradeDatabaseVersion(final InstallerContext ctx, final File databaseDirectory) throws IOException {
    ctx.getProgressInterface().setStatusMessage("Upgrading Database Engine...");
    final HSQLDBUpgraderFrom17To18 hsqldbUpgraderFrom17To18 = new HSQLDBUpgraderFrom17To18();
    hsqldbUpgraderFrom17To18.upgrade(databaseDirectory);
  }


    public boolean isRollbackSupported() {
    return false;
  }


  /**
   * Validates that the server is not running.
   *
   * @noinspection MethodWithMoreThanThreeNegations
   */
  private void validateServerIsNotRunning(final InstallerContext ctx) throws UserCanceledException {
    try {
      ctx.getProgressInterface().setStatusMessage("Validating Service Status...");

      // check if unix, we care about unix only
      if (!RuntimeUtils.isUnix()) {
        return;
      }

      // get install dir
      final File installationDirectory = ctx.getInstallationDirectory();
      if (!installationDirectory.exists()) {
        return;
      }

      // get tomcat config file
      final File serverXML = new File(installationDirectory, ETC_CONF_SERVER_XML);
      if (!serverXML.exists()) {
        LOG.warn("Could not find configuration file at " + ETC_CONF_SERVER_XML);
        return;
      }

      final ServerPresenceValidator validator = new ServerPresenceValidator();
      if (!validator.serverIsRunning(installationDirectory)) {
        return;
      }

      // we could connect, throw an exception;
      throw new UserCanceledException("Parabuild service is running. Please stop Parabuild service before launching the installer.");
    } catch (final IOException e) {
      throw makeUserCancelledException(e);
    }
  }


  private static UserCanceledException makeUserCancelledException(final Exception e) {
    // Get the stack trace
    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
    final PrintStream pos = new PrintStream(baos);
    pos.close();
    // Create the result
    final UserCanceledException uce = new UserCanceledException(StringUtils.toString(e) + '\n' + baos.toString());
    uce.initCause(e);
    return uce;
  }


  /**
   * Returns true if the database file exists.
   *
   * @param installerContext
   * @return true if the database file exists.
   * @noinspection BooleanMethodIsAlwaysInverted
   */
  private boolean databaseFileExists(final InstallerContext installerContext) {
    final File databaseFile = new File(makeDatabaseDirectoryPath(installerContext.getInstallationDirectory()), "parabuild.data");
    return databaseFile.exists();
  }


  private static File makeDatabaseDirectoryPath(final File installationDirectory) {
    return new File(installationDirectory, "etc/data");
  }


  public String toString() {
    return "ParabuildPreInstallAction{}";
  }
}
