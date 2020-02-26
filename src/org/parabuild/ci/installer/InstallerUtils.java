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

import org.parabuild.ci.util.IoUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * InstallerUtils
 * <p/>
 *
 * @author Slava Imeshev
 * @since Mar 9, 2009 10:56:02 AM
 */
final class InstallerUtils {

  private InstallerUtils() {
  }


  static Properties loadDatabaseProperties(final File databaseDirectory) throws IOException {
    final File databaseProperties = new File(databaseDirectory, InstallerConstants.PARABUILD_PROPERTIES);
    final InputStream is = new FileInputStream(databaseProperties);
    final Properties databaseProps = new Properties();
    try {
      databaseProps.load(is);
    } finally {
      IoUtils.closeHard(is);
    }
    return databaseProps;
  }


  /**
   * Should be called before last call to the database.
   *
   * @param con
   * @throws SQLException
   */
  static void shutdownDatabase(final Connection con) throws SQLException {
    Statement statement = null;
    try {
      statement = con.createStatement();
      statement.executeUpdate("SHUTDOWN");
    } finally {
      IoUtils.closeHard(statement);
    }
  }
}
