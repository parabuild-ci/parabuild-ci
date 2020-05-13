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
package org.parabuild.ci.tray;

import org.parabuild.ci.remote.internal.HessianServiceLocator;

import java.io.IOException;


/**
 * BuildStatusServiceLocator locator is a helper class that is
 * responsible for handling details of obtaining
 * BuildStatusService.
 *
 * @see org.parabuild.ci.remote.services.RemoteBuilderWebService
 */
final class BuildStatusServiceLocator {

  private static final String REMOTE_STATUS_PATH = "/parabuild/build/status/tray";

  private final HessianServiceLocator locator;


  /**
   * Constructor
   *
   * @param host
   * @param user
   * @param password
   */
  public BuildStatusServiceLocator(final String host, final String user, final String password) {

    locator = new HessianServiceLocator(BuildStatusService.class, REMOTE_STATUS_PATH, host, user, password);
  }


  /**
   * Helper method. Returns BuilderEnvironmentWebService by
   * accessing hessian-based webservice.
   */
  public BuildStatusService getWebService() throws IOException {

    return new BuildStatusServiceHessianExceptionWrapper(
            (BuildStatusService) locator.getWebService(), locator.getHost());
  }
}