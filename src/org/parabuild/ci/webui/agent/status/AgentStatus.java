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
package org.parabuild.ci.webui.agent.status;

import org.apache.log4j.Logger;

import java.util.Comparator;


/**
 * Valu object. Contains agent status.
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 24, 2009 3:01:07 PM
 */
public final class AgentStatus {

  public static final byte ACTIVITY_DISABLED = (byte) 1;
  public static final byte ACTIVITY_IDLE = (byte) 2;
  public static final byte ACTIVITY_BUSY = (byte) 3;
  public static final byte ACTIVITY_OFFLINE = (byte) 4;
  public static final byte ACTIVITY_VERSION_MISMATCH = (byte) 5;


  public static final Comparator BY_NAME_COMPARATOR = new Comparator() {

    public int compare(final Object o1, final Object o2) {
      try {
        if (o1 == null || o2 == null) {
          return -1;
        }
        final AgentStatus a1 = (AgentStatus) o1;
        final AgentStatus a2 = (AgentStatus) o2;
        return a1.hostName.compareToIgnoreCase(a2.hostName);
      } catch (Exception e) {
        return -1;
      }
    }
  };

  /**
   * Logger.
   *
   * @noinspection UNUSED_SYMBOL,UnusedDeclaration
   */
  private static final Logger LOG = Logger.getLogger(AgentStatus.class); // NOPMD

  private final String hostName;
  private final String remoteVersion;
  private final byte activityType;
  private final int agentID;
  private final ImmutableImage chart;


  public AgentStatus(final String hostName, final byte activityType, final String remoteVersion, final int agentID,
                     final ImmutableImage chart) {
    this.hostName = hostName;
    this.remoteVersion = remoteVersion;
    this.activityType = activityType;
    this.agentID = agentID;
    this.chart = chart;
  }


  public String getHostName() {
    return hostName;
  }


  public byte getActivityType() {
    return activityType;
  }


  public int getAgentID() {
    return agentID;
  }


  public String getRemoteVersion() {
    return remoteVersion;
  }


  /**
   * Returns activity type as a human-readable string.
   *
   * @return activity type as a human-readable string.
   */
  public String getActivityTypeAsString() {
    switch (activityType) {
      case ACTIVITY_BUSY:
        return "Busy";
      case ACTIVITY_DISABLED:
        return "Disabled";
      case ACTIVITY_IDLE:
        return "Idle";
      case ACTIVITY_OFFLINE:
        return "Offline";
      case ACTIVITY_VERSION_MISMATCH:
        return "Version mismatch: " + remoteVersion;
      default:
        return "Unknown";
    }
  }


  public ImmutableImage getChart() {
    return chart;
  }


  public String toString() {
    return "AgentStatus{" +
            "activityType=" + activityType +
            ", agentID=" + agentID +
            ", chart=" + chart +
            ", hostName='" + hostName + '\'' +
            ", remoteVersion='" + remoteVersion + '\'' +
            '}';
  }
}
