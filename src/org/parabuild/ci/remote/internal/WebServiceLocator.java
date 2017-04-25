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
package org.parabuild.ci.remote.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.parabuild.ci.Version;
import org.parabuild.ci.configuration.AgentHost;
import org.parabuild.ci.remote.services.RemoteBuilderWebService;

import java.io.IOException;


/**
 * WebService locator is a helper class that is responsible for
 * handling details of obtaining RemoteBuilderWebService.
 *
 * @see RemoteBuilderWebService
 */
public final class WebServiceLocator {

  /**
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log log = LogFactory.getLog(WebServiceLocator.class); //NOPMD

  public static final String REMOTE_BUILDER_PATH = "/parabuild/integration/builder";

  private final HessianServiceLocator locator;
  private final AgentHost agentHost;


  public WebServiceLocator(final AgentHost agentHost) {
    this.locator = new HessianServiceLocator(RemoteBuilderWebService.class, REMOTE_BUILDER_PATH, agentHost.getHost(),
            agentHost.getUser(), agentHost.getPassword());
    this.agentHost = agentHost;
  }


  /**
   * Helper method. Returns BuilderEnvironmentWebService by
   * accessing hessian-based webservice.
   */
  public RemoteBuilderWebService getWebService() throws IOException {
    // get service
    final RemoteBuilderWebService remoteService = (RemoteBuilderWebService) locator.getWebService();

    // check version
    final String managerVersion = Version.versionToString(true);
    final String builderVersion = remoteService.builderVersionAsString();
    if (!managerVersion.equals(builderVersion)) {
      return new FailedWithVersionMismatchRemoteBuilderWebService(managerVersion, builderVersion, locator.getURL());
    }
    // return result
    return remoteService;
  }


  /**
   * Helper method. Returns proxy factory.
   */
  public ParabuildHessianProxyFactory getProxyFactory() {
    return locator.getProxyFactory();
  }


  /**
   * @return agent host name.
   */
  public String agentHostName() {
    return locator.getHost();
  }


  /**
   * @return URL
   */
  public String getURL() {
    return locator.getURL();
  }


  public AgentHost getAgentHost() {
    return agentHost;
  }


  public String toString() {
    return "WebServiceLocator{" +
            "locator=" + locator +
            ", agentHost=" + agentHost +
            '}';
  }
}