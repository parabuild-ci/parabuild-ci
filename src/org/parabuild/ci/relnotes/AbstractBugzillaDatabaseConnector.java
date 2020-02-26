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

import org.parabuild.ci.util.StringUtils;
import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManager;
import org.parabuild.ci.error.ErrorManagerFactory;

import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Strategy for BugzillaDatabaseConnector
 */
abstract class AbstractBugzillaDatabaseConnector implements BugzillaDatabaseConnector {

  private final BugzillaMySQLConnectionFactory connectionFactory;


  /**
   * Constructor
   *
   */
  AbstractBugzillaDatabaseConnector(final BugzillaMySQLConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }


  public abstract Collection requestBugsFromBugzilla(String productName, String productVersion, Date fromDate, Date toDate);


  /**
   * Creates a connection to the bugzilla database.
   */
  protected final Connection connect() throws SQLException {
    return connectionFactory.connect();
  }


  /**
   * Helper method to report unexpected error to administrator
   */
  static void reportUnexpectedError(final String productName, final Exception e) {
    final Error error = new Error();
    error.setDetails(e);
    error.setDescription("Error while retrieving Bugzilla bugs for product \"" + productName + "\": " + StringUtils.toString(e));
    error.setErrorLevel(Error.ERROR_LEVEL_WARNING);
    error.setSubsystemName(Error.ERROR_SUBSYSTEM_BUILD);
    error.setPossibleCause("Make sure that the database configured in direct Bugzilla connection is accessible and the configuration is up to date.");
    error.setSendEmail(true);
    final ErrorManager em = ErrorManagerFactory.getErrorManager();
    em.reportSystemError(error);
  }


  public String toString() {
    return "AbstractBugzillaDatabaseConnector{" +
      "connectionFactory=" + connectionFactory +
      '}';
  }
}
