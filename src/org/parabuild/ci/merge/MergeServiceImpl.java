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
package org.parabuild.ci.merge;

import org.parabuild.ci.error.Error;
import org.parabuild.ci.error.ErrorManagerFactory;
import org.parabuild.ci.services.ServiceName;

/**
 * Merge service
 */
public final class MergeServiceImpl implements MergeService {

  private byte status = SERVICE_STATUS_NOT_STARTED;


  public void startupService() {
    try {
      MergeManager.getInstance().startupMerges();
      status = SERVICE_STATUS_STARTED;
    } catch (final Exception e) {
      reportStartupError(e, serviceName(), Error.ERROR_SUSBSYSTEM_MERGE);
    }
  }


  public void shutdownService() {
    try {
      MergeManager.getInstance().shutdownMerges();
      status = SERVICE_STATUS_NOT_STARTED;
    } catch (final Exception e) {
      reportShutdownService(e, serviceName(), Error.ERROR_SUSBSYSTEM_MERGE);
    }
  }


  public ServiceName serviceName() {
    return ServiceName.MERGE_SERVICE;
  }


  public byte getServiceStatus() {
    return status;
  }


  private static void reportShutdownService(final Exception e, final ServiceName serviceName, final String subsystemName) {
    final Error error = new Error();
    error.setDetails(e);
    error.setErrorLevel(Error.ERROR_LEVEL_FATAL);
    error.setSendEmail(false);
    error.setDescription("Error while shutting down service " + serviceName);
    error.setSubsystemName(subsystemName);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  private static void reportStartupError(final Exception e, final ServiceName serviceName, final String subsystemName) {
    final Error error = new Error();
    error.setDetails(e);
    error.setErrorLevel(Error.ERROR_LEVEL_FATAL);
    error.setSendEmail(false);
    error.setDescription("Error while starting up service " + serviceName);
    error.setSubsystemName(subsystemName);
    ErrorManagerFactory.getErrorManager().reportSystemError(error);
  }


  public String toString() {
    return "MergeServiceImpl{" +
      "status=" + status +
      '}';
  }
}
