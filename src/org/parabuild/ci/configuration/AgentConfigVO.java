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

import org.parabuild.ci.object.AgentConfig;
import org.parabuild.ci.object.ObjectConstants;

/**
 * @noinspection StaticInheritance
 */
public final class AgentConfigVO implements ObjectConstants {

  private static final String BUILD_MANAGER = AgentConfig.BUILD_MANAGER;

  private int ID = UNSAVED_ID;
  private int capacity = 0;
  private int maxConcurrentBuilds = 0;
  private String host = null;
  private long timeStamp = 0L;
  private boolean enabled = true;
  private boolean deleted = false;
  private String description = "";
  private final String password = null;
  private int buildConfigCount = 0;
  private boolean serialize = false;


  /**

   */
  public int getID() {
    return ID;
  }


  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   */
  public String getHost() {
    return host;
  }


  public void setHost(final String host) {
    this.host = host;
  }


  /**
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   */
  public boolean isEnabled() {
    return enabled;
  }


  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }


  /**
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  /**
   */
  public boolean isDeleted() {
    return deleted;
  }


  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }


  /**
   * Returns true if this agent is a build manager.
   */
  public boolean isLocal() {
    return host.equals(BUILD_MANAGER);
  }


  public String getPortAsString() {
    final int i = host.indexOf(':');
    if (i <= 0) {
      return "";
    }
    return host.substring(i);
  }


  /**
   * @noinspection NonBooleanMethodNameMayNotStartWithQuestion
   */
  public String isEnabledAsString() {
    return enabled ? "Enabled" : "Disabled";
  }


  public String getPassword() {
    return password;
  }


  public int getBuildConfigCount() {
    return buildConfigCount;
  }


  public String getBuildConfigCountAsString() {
    return Integer.toString(buildConfigCount);
  }


  public void setBuildConfigCount(final int buildConfigCount) {
    this.buildConfigCount = buildConfigCount;
  }


  public void setSerialize(final boolean serialize) {
    this.serialize = serialize;
  }


  public int getCapacity() {
    return capacity;
  }


  public void setCapacity(final int capacity) {
    this.capacity = capacity;
  }


  public int getMaxConcurrentBuilds() {
    return maxConcurrentBuilds;
  }


  public void setMaxConcurrentBuilds(final int maxConcurrentBuilds) {
    this.maxConcurrentBuilds = maxConcurrentBuilds;
  }


  /**
   * @return String "Serialize" is serialize is set or an empty string if not.
   */
  public String getSerializeAsString() {
    return serialize ? "Serialize" : "";
  }


  /**
   * Returns agent's weight as a string.
   *
   * @return agent's weight as a string.
   */
  public String getCapacityAsString() {
    return Integer.toString(capacity);
  }


  /**
   * Returns agent's max concurrent builds as a string.
   *
   * @return agent's max concurrent builds as a string.
   */
  public String getMaxConcurrentBuildsAsString() {
    return Integer.toString(maxConcurrentBuilds);
  }


  public String toString() {
    return "AgentConfigVO{" +
            "ID=" + ID +
            ", capacity=" + capacity +
            ", host='" + host + '\'' +
            ", timeStamp=" + timeStamp +
            ", enabled=" + enabled +
            ", deleted=" + deleted +
            ", description='" + description + '\'' +
            ", password='" + password + '\'' +
            ", buildConfigCount=" + buildConfigCount +
            ", serialize=" + serialize +
            '}';
  }
}