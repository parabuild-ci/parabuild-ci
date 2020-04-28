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
package org.parabuild.ci.versioncontrol;

/**
 * Base class to accomodate common parameters for Vault command
 * line client.
 */
class VaultCommandParameters {

  private String user;
  private String host;
  private String password;
  private boolean useSSL;
  private String proxyServer;
  private String proxyPort;
  private String proxyUser;
  private String proxyPassword;
  private String proxyDomain;
  private String repository;


  /**
   * Base's class constructor is made protected to prevent direct
   * instantioation.
   */
  protected VaultCommandParameters() {
  }


  public String getUser() {
    return user;
  }


  public void setUser(final String user) {
    this.user = user;
  }


  public String getHost() {
    return host;
  }


  public void setHost(final String host) {
    this.host = host;
  }


  public String getPassword() {
    return password;
  }


  public void setPassword(final String password) {
    this.password = password;
  }


  public boolean getUseSSL() {
    return useSSL;
  }


  public void setUseSSL(final boolean useSSL) {
    this.useSSL = useSSL;
  }


  public String getProxyServer() {
    return proxyServer;
  }


  public void setProxyServer(final String proxyServer) {
    this.proxyServer = proxyServer;
  }


  public String getProxyPort() {
    return proxyPort;
  }


  public void setProxyPort(final String proxyPort) {
    this.proxyPort = proxyPort;
  }


  public String getProxyUser() {
    return proxyUser;
  }


  public void setProxyUser(final String proxyUser) {
    this.proxyUser = proxyUser;
  }


  public String getProxyPassword() {
    return proxyPassword;
  }


  public void setProxyPassword(final String proxyPassword) {
    this.proxyPassword = proxyPassword;
  }


  public String getProxyDomain() {
    return proxyDomain;
  }


  public void setProxyDomain(final String proxyDomain) {
    this.proxyDomain = proxyDomain;
  }


  public String getRepository() {
    return repository;
  }


  public void setRepository(final String repository) {
    this.repository = repository;
  }


  public String toString() {
    return "VaultCommandParameters{" +
      "user='" + user + '\'' +
      ", host='" + host + '\'' +
      ", password='" + password + '\'' +
      ", useSSL='" + useSSL + '\'' +
      ", proxyServer='" + proxyServer + '\'' +
      ", proxyPort='" + proxyPort + '\'' +
      ", proxyUser='" + proxyUser + '\'' +
      ", proxyPassword='" + proxyPassword + '\'' +
      ", proxyDomain='" + proxyDomain + '\'' +
      ", repository='" + repository + '\'' +
      '}';
  }
}
