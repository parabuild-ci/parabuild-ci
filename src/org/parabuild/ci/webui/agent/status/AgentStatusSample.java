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

/**
 * AgentStatusSample
 * <p/>
 *
 * @author Slava Imeshev
 * @since Feb 24, 2009 4:41:38 PM
 */
final class AgentStatusSample {

  static final AgentStatusSample OFFLINE = new AgentStatusSample(0, AgentStatus.ACTIVITY_OFFLINE);
  static final AgentStatusSample IDLE = new AgentStatusSample(0, AgentStatus.ACTIVITY_IDLE);

  private final Integer busyCounter;
  private final byte activity;
  private final String remoteVersion;


  AgentStatusSample(final int busyCounter, final byte activity) {
    this(busyCounter, activity, null);
  }


  AgentStatusSample(final int buzyCounter, final byte activity, final String remoteVersion) {
    this.remoteVersion = remoteVersion;
    this.busyCounter = new Integer(buzyCounter);
    this.activity = activity;
  }


  public Integer getBusyCounter() {
    return busyCounter;
  }


  public byte getActivity() {
    return activity;
  }


  public String getRemoteVersion() {
    return remoteVersion;
  }


  public String toString() {
    return "AgentStatusSample{" +
            "busyCounter=" + busyCounter +
            ", activity=" + activity +
            ", remoteVersion='" + remoteVersion + '\'' +
            '}';
  }
}
