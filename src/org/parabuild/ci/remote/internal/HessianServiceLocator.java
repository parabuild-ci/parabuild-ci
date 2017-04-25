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

import java.io.IOException;

import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.remote.RemoteUtils;


/**
 * HessianServiceLocator locator is a helper class that is
 * responsible for handling details of obtaining services
 * provided by Hessian.
 */
public final class HessianServiceLocator {

  private final Class serviceClass;
  private final String host;
  private final String password;
  private final String servicePath;
  private final String user;


  /**
   * Constructor
   *
   * @param host
   * @param user
   * @param password
   */
  public HessianServiceLocator(final Class serviceClass, final String servicePath, final String host, final String user, final String password) {
    this.serviceClass = serviceClass;
    this.servicePath = ArgumentValidator.validateArgumentNotBlank(servicePath, "servicePath");
    this.host = ArgumentValidator.validateArgumentNotBlank(host, "host");
    this.user = user;
    this.password = password;
  }


  /**
   * Helper method. Returns remote service.
   */
  public Object getWebService() throws IOException {
    final ParabuildHessianProxyFactory hessianProxyFactory = getProxyFactory();
    final String url = getURL();
    return hessianProxyFactory.create(serviceClass, url);
  }


  /**
   * Helper method. Returns proxy factory.
   */
  public ParabuildHessianProxyFactory getProxyFactory() {
    final ParabuildHessianProxyFactory proxyFactory = new ParabuildHessianProxyFactory();
    proxyFactory.setUser(user);
    proxyFactory.setPassword(password);
    proxyFactory.setOverloadEnabled(true);
    return proxyFactory;
  }


  /**
   * Helper method. Returns fully qualified URL of remote
   * agent
   */
  public String getURL() {
    return "http://" + RemoteUtils.normalizeHostPort(host) + servicePath;
  }


  /**
   * @return host
   */
  public String getHost() {
    return host;
  }
}