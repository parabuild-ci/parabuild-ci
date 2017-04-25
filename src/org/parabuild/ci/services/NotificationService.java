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



/**
 */
public final class NotificationService implements Service {

  private byte status = SERVICE_STATUS_NOT_STARTED;


  public void shutdownService() {
    status = SERVICE_STATUS_NOT_STARTED;
  }


  public ServiceName serviceName() {
    return ServiceName.NOTIFICATION_SERVICE;
  }


  public byte getServiceStatus() {
    return status;
  }


  public void startupService() {
    status = SERVICE_STATUS_STARTED;
  }


  public String toString() {
    return "NotificationService{" +
      "status=" + status +
      '}';
  }
}