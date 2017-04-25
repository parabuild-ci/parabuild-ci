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

import org.apache.commons.logging.*;

import org.parabuild.ci.configuration.*;
import org.parabuild.ci.error.*;
import org.parabuild.ci.error.Error;

/**
 */
public final class DatabaseService implements Service {

  /** @noinspection UNUSED_SYMBOL,UnusedDeclaration*/
  private static final Log log = LogFactory.getLog(DatabaseService.class); // NOPMD

  private DatabaseRunner databaseRunner = null;
  private byte status = SERVICE_STATUS_NOT_STARTED;


  public void shutdownService() {
    if (databaseRunner != null) databaseRunner.stopDatabase();
  }


  public ServiceName serviceName() {
    return ServiceName.DATABASE_SERVICE;
  }


  public byte getServiceStatus() {
    return status;
  }


  public void startupService() {
    try {
      if (databaseRunner == null) {
        databaseRunner = DatabaseRunnerBuilder.makeDatabaseRunner();
        databaseRunner.startDatabase();
      }

      // Run upgrader
      final DatabaseSchemeUpgrader chemaUpgrader = new DatabaseSchemeUpgrader();
      chemaUpgrader.process();
      status = SERVICE_STATUS_STARTED;
    } catch (Exception e) {
      status = SERVICE_STATUS_FAILED;
      final ErrorManager errorManager = ErrorManagerFactory.getErrorManager();
      final Error error = new Error("Database serivice failed to start");
      error.setDetails(e);
      error.setErrorLevel(Error.ERROR_LEVEL_FATAL);
      error.setSendEmail(false);
      errorManager.reportSystemError(error);
    }
  }


  public String toString() {
    return "DatabaseService{" +
      "databaseRunner=" + databaseRunner +
      ", status=" + status +
      '}';
  }
}
