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

import org.parabuild.ci.common.ArgumentValidator;
import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.realm.RealmConstants;

/**
 * Value object to hold build host name and credentials.
 */
public final class AgentHost {

  private String host = null;


  private final String password;

  /**
   * @noinspection FieldMayBeStatic
   */
  private final String user = RealmConstants.DEFAULT_BUILDER_USER;


  public AgentHost(final String host, final String password) {
    this.host = ArgumentValidator.validateArgumentNotBlank(host, "host");
    this.password = password;
  }


  public AgentHost(final String host) {
    this.host = host;
    this.password = RealmConstants.DEFAULT_BUILDER_PASSWORD;
  }


  public String getHost() {
    return host;
  }


  public String getPassword() {
    return password;
  }


  public String getUser() {
    return user;
  }


  public boolean isLocal() {
    return host.equals(AgentConfig.BUILD_MANAGER);
  }


  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    final AgentHost agentHost = (AgentHost) o;

    if (!host.equalsIgnoreCase(agentHost.host)) {
      return false;
    }

    return true;
  }


  public int hashCode() {
    return host.hashCode();
  }


  public String toString() {
    return "AgentHost{" +
            "host='" + host + '\'' +
            ", user='" + user + '\'' +
            ", password='" + password + '\'' +
            '}';
  }
}
