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
package org.parabuild.ci.relnotes;

import java.sql.*;
import org.apache.commons.logging.*;

import org.parabuild.ci.util.*;

/**
 * This class creates instances of BugzillaDatabaseConnector
 * corresponding the particular version of Bugzilla database
 * scheme.
 */
public final class BugzillaDatabaseConnectorFactory {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(BugzillaDatabaseConnectorFactory.class);


  public static BugzillaDatabaseConnector makeInstance(
    final String databaseHost, final int databasePort, final String databaseName,
    final String databaseUser, final String databasePassword) throws SQLException {

    Connection conn = null; // NOPMD
    ResultSet columns = null; // NOPMD
    try {
      // determine which version of the BugzillaDatabaseConnector we have to use.
      final BugzillaMySQLConnectionFactory connectionFactory = new BugzillaMySQLConnectionFactory(databaseHost, databasePort, databaseName, databaseUser, databasePassword);
      conn = connectionFactory.connect();
      final DatabaseMetaData metaData = conn.getMetaData();
      columns = metaData.getColumns(null, null, "bugs", "product");
      if (columns.next()) { // "product" exists, means version 2.16
        if (log.isDebugEnabled()) log.debug("columns: " + columns);
        return new Bugzilla216DatabaseConnector(connectionFactory);
      } else {
        return new Bugzilla220DatabaseConnector(connectionFactory);
      }
    } finally {
      IoUtils.closeHard(columns);
      IoUtils.closeHard(conn);
    }
  }
}
