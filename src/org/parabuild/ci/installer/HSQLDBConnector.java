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

import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.configuration.PersistanceConstants;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * HSQLDBConnector
 * <p/>
 *
 * @author Slava Imeshev
 * @since Dec 4, 2008 9:07:32 PM
 */
final class HSQLDBConnector {

  private static final String HSQLDB_DRIVER = "org.hsqldb.jdbcDriver";

  private final String driverJarResourcePath;
  private File driverJarFile = null;


  /**
   * Constructor.
   *
   * @param driverJarResourcePath path to the driver inside the jar.
   */
  HSQLDBConnector(final String driverJarResourcePath) {
    this.driverJarResourcePath = driverJarResourcePath;
  }


  /**
   * Connects to the HSQLDB database in the given directory.
   *
   * @param databaseDirectory HSQLDB database directory.
   * @return new JDBC connection
   * @throws IOException if there are errors while connection
   */
  public Connection connect(final File databaseDirectory) throws IOException {

    // Create driver
    final Driver driver = createHSQLDBDriver();

    // Save the datbase in the format acceptable for the upgrade.
    final String connURL = "jdbc:hsqldb:" + IoUtils.getCanonicalPathHard(databaseDirectory);
    final Properties connProps = new Properties();
    connProps.setProperty("user", PersistanceConstants.DATABASE_USER_NAME);
    connProps.setProperty("password", PersistanceConstants.DATABASE_PASSWORD);
    try {
      return driver.connect(connURL, connProps);
    } catch (final SQLException e) {
      throw IoUtils.createIOException(e);
    }
  }


  private Driver createHSQLDBDriver() throws IOException {
    if (driverJarFile == null) {
      // Pull extract the jar file
      InputStream is = null;
      FileOutputStream fos = null;
      try {
        is = getClass().getResourceAsStream(driverJarResourcePath);
        if (is == null) {
          throw new IOException("Cannot find resource: " + driverJarResourcePath);
        }
        driverJarFile = File.createTempFile("drv", ".jar");
        fos = new FileOutputStream(driverJarFile);
        IoUtils.copyInputToOuputStream(is, fos);
      } catch (final IOException e) {
        if (driverJarFile != null && driverJarFile.exists()) {
          driverJarFile.delete();
          driverJarFile = null;
        }
      } finally {
        IoUtils.closeHard(is);
        IoUtils.closeHard(fos);
      }
    }
    final URL hsqldbJarURL = driverJarFile.toURI().toURL();
    final ClassLoader hsqldbClassloader = new URLClassLoader(new URL[]{hsqldbJarURL});
    try {
      final Class hsqldbDriverClass = hsqldbClassloader.loadClass(HSQLDB_DRIVER);
      return (Driver) hsqldbDriverClass.getConstructor().newInstance();
    } catch (final RuntimeException e) {
      throw e;
    } catch (final Exception e) {
      throw IoUtils.createIOException(e);
    }
  }


  public String toString() {
    return "HSQLDBConnector{" +
            "driverJarFile=" + driverJarFile +
            ", driverJarResourcePath='" + driverJarResourcePath + '\'' +
            '}';
  }
}
