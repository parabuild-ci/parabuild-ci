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

import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.common.IoUtils;
import org.parabuild.ci.common.StringUtils;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;

/**
 * This class provides Connection object for MySQL Bugzilla
 * database.
 */
public final class BugzillaMySQLConnectionFactory {

  private static final String STR_MYSQL_DRIVER = "com.mysql.jdbc.Driver";
  private static final String STR_USER = "user";
  private static final String STR_PASSWORD = "password";
  
  private String databaseHost = null;
  private int databasePort = 0;
  private String databaseName = null;
  private String databaseUser = null;
  private String databasePassword = null;


  /**
   * Constructor
   *
   * @param databaseHost Manadatory Bugzilla database host
   * @param databasePort Manadatory Bugzilla database port
   * @param databaseUser Manadatory Bugzilla databse user
   * @param databasePassword Optional Bugzilla databse pasword
   */
  public BugzillaMySQLConnectionFactory(final String databaseHost, final int databasePort, final String databaseName,
    final String databaseUser, final String databasePassword) {
    // validate
    ArgumentValidator.validateArgumentNotBlank(databaseHost, "host");
    ArgumentValidator.validateArgumentGTZero(databasePort, "database port");
    ArgumentValidator.validateArgumentNotBlank(databaseUser, "database user");
    ArgumentValidator.validateArgumentNotBlank(databaseName, "database name");

    this.databaseHost = databaseHost;
    this.databasePassword = databasePassword;
    this.databasePort = databasePort;
    this.databaseUser = databaseUser;
    this.databaseName = databaseName;
  }


  /**
   * Creates a connection to the bugzilla database.
   */
  final Connection connect() throws SQLException {

    try {
      // load driver
      final Driver driver = (Driver) Class.forName(STR_MYSQL_DRIVER).getConstructor().newInstance();
      // connect
      final Properties props = new Properties();
      props.setProperty(STR_USER, databaseUser);
      if (!StringUtils.isBlank(databasePassword)) {
        props.setProperty(STR_PASSWORD, databasePassword);
      }
      // return new connection
      final Connection con = driver.connect("jdbc:mysql://" + databaseHost + ':' + databasePort + '/' + databaseName, props); // NOPMD
      con.setAutoCommit(true);
      return con;
    } catch (final ClassNotFoundException e) {
      throw new SQLException("MySQL driver cannot be found. To install the driver go to http://dev.mysql.com/downloads/connector/j/3.1.html, download a binary dirstibution for Connector/J and untar/unzip file mysql-connector-java-3.1.13-bin.jar to <parabuild install dir>/lib/common/lib and restart Parabuild.");
    } catch (final SQLException e) {
      throw e;
    } catch (final Exception e) {
      throw new SQLException(StringUtils.toString(e), e);
    }
  }


  /**
   * This method is used during build set-up to test connectivity
   * parameters.
   */
  public ConnectionTestResult testConnectionToDB() {
    Connection con = null; // NOPMD
    try {
      con = connect();
    } catch (final Exception e) {
      return new ConnectionTestResult(false, e);
    } finally {
      IoUtils.closeHard(con);
    }
    return ConnectionTestResult.SUCCESS;
  }


  public String toString() {
    return "BugzillaMySQLConnectionFactory{" +
      "databaseHost='" + databaseHost + '\'' +
      ", databasePort=" + databasePort +
      ", databaseName='" + databaseName + '\'' +
      ", databaseUser='" + databaseUser + '\'' +
      ", databasePassword='" + databasePassword + '\'' +
      '}';
  }
}
