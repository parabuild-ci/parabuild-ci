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
package org.parabuild.ci.configuration;

import org.parabuild.ci.common.IoUtils;

import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 */
public final class HSQLDBUtils {

  private HSQLDBUtils() {
  }


  /**
   * Creates HSQL DB connection with default user name and
   * password
   *
   * @see PersistanceConstants#DATABASE_PASSWORD
   * @see PersistanceConstants#DATABASE_USER_NAME
   */
  public static Connection createHSQLConnection(final File databasePath) throws SQLException {
    return createHSQLConnection(databasePath, PersistanceConstants.DATABASE_USER_NAME, PersistanceConstants.DATABASE_PASSWORD);
  }


  /**
   * Creates HSQL DB connection.
   */
  public static Connection createHSQLConnection(final File databasePath, final String userName, final String password) throws SQLException {
    InputStream is = null;
    try {
      // get hibernate properties
      is = IoUtils.stringToInputStream(IoUtils.getResourceAsString("hibernate.properties"));
      final Properties hibProps = new Properties();
      hibProps.load(is);
      final String driverName = hibProps.getProperty("hibernate.connection.driver_class");
      final String url = "jdbc:hsqldb:" + IoUtils.getCanonicalPathHard(databasePath);
      // connect
      final Properties info = new Properties();
      info.setProperty("user", userName);
      info.setProperty("password", password);
      return ((Driver)Class.forName(driverName).newInstance()).connect(url, info);
    } catch (final SQLException e) {
      throw e;
    } catch (final Exception e) {
      throw IoUtils.makeSQLException(e);
    } finally {
      IoUtils.closeHard(is);
    }
  }
}
