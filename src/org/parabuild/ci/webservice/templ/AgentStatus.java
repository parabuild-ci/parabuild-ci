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
package org.parabuild.ci.webservice.templ;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.Serializable;

/**
 * AgentStatus
 * <p/>
 *
 * @author Slava Imeshev
 * @since Mar 28, 2010 12:44:50 PM
 */
public final class AgentStatus implements Serializable {

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Log LOG = LogFactory.getLog(AgentStatus.class); // NOPMD
  private static final long serialVersionUID = -3072995874488002720L;

  private String hostName;
  private String remoteVersion;
  private byte status;
  private int agentID;


  public String getHostName() {
    return hostName;
  }


  public void setHostName(final String hostName) {
    this.hostName = hostName;
  }


  public String getRemoteVersion() {
    return remoteVersion;
  }


  public void setRemoteVersion(final String remoteVersion) {
    this.remoteVersion = remoteVersion;
  }


  public byte getStatus() {
    return status;
  }


  public void setStatus(final byte status) {
    this.status = status;
  }


  public int getAgentID() {
    return agentID;
  }


  public void setAgentID(final int agentID) {
    this.agentID = agentID;
  }


  public String toString() {
    return "AgentStatus{" +
            "agentID=" + agentID +
            ", hostName='" + hostName + '\'' +
            ", remoteVersion='" + remoteVersion + '\'' +
            ", status=" + status +
            '}';
  }
}
