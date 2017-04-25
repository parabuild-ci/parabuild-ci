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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Create our database.
 *
 * @see HSQLDBDatabaseRunner#startDatabase
 */
public final class DatabaseCreator implements PersistanceConstants {


  /**
   * Creates database for the given path.
   */
  public void createDatabase(final File database) throws SQLException {
    Connection conn = null; // NOPMD
    Statement stmt = null; // NOPMD
    FileOutputStream out = null;
    FileWriter fw = null;
    try {
      // create our props
      final Properties props = new Properties();
      final String properties = IoUtils.getResourceAsString("hsqldb-database.properties");
      final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(properties.getBytes());
      props.load(byteArrayInputStream);
      out = new FileOutputStream(new File(database + ".properties"));
      props.store(out, "Database properties");
      IoUtils.closeHard(out);

      // create our bootrsrap script
      final String script = IoUtils.getResourceAsString("hsqldb-database.script");
      fw = new FileWriter(new File(database + ".script"));
      fw.write(script);
      IoUtils.closeHard(fw);

      // run schema script
      final String schema = IoUtils.getResourceAsString("schema.sql");
      conn = HSQLDBUtils.createHSQLConnection(database, PersistanceConstants.DATABASE_USER_NAME, PersistanceConstants.DATABASE_PASSWORD);
      stmt = conn.createStatement();
      stmt.execute(schema);
      stmt.execute("SET SCRIPTFORMAT COMPRESSED");
      stmt.execute("SHUTDOWN");
    } catch (IOException e) {
      throw IoUtils.makeSQLException(e);
    } finally {
      IoUtils.closeHard(stmt);
      IoUtils.closeHard(conn);
      IoUtils.closeHard(out);
    }
  }
}
