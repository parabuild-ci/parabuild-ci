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
package org.parabuild.ci.object;

import org.parabuild.ci.realm.RealmConstants;

import java.io.Serializable;

/**
 * AgentConfig.
 *
 * @hibernate.class table="AGENT" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class AgentConfig implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -5237501290837750448L; // NOPMD

  public static final String BUILD_MANAGER = "<Build Manager>";

  private int ID = UNSAVED_ID;
  private int capacity = 0;
  private String host = null;
  private long timeStamp = 0;
  private boolean enabled = true;
  private boolean deleted = false;
  private boolean serialize = false;
  private String description = "";
  private int maxConcurrentBuilds = 0;


  public AgentConfig() {
  }


  public AgentConfig(final String builderHost) {
    this.host = builderHost;
  }


  /**
   * The getter method for member user ID
   *
   * @return int
   * @hibernate.id generator-class="identity" column="ID"
   * unsaved-value="-1"
   */
  public int getID() {
    return ID;
  }


  public void setID(final int ID) {
    this.ID = ID;
  }


  /**
   * The getter method for member user ID
   *
   * @return int
   * @hibernate.property column = "HOST" unique="false"
   * null="false"
   */
  public String getHost() {
    return host;
  }


  public void setHost(final String host) {
    this.host = host;
  }


  /**
   * Returns timestamp
   *
   * @return long
   * @hibernate.version column="TIMESTAMP"  null="false"
   */
  public long getTimeStamp() {
    return timeStamp;
  }


  public void setTimeStamp(final long timeStamp) {
    this.timeStamp = timeStamp;
  }


  /**
   * Returns true if this cluster member is enabled
   *
   * @return String
   * @hibernate.property column="ENABLED"  type="yes_no"
   * unique="false" null="false"
   */
  public boolean isEnabled() {
    return enabled;
  }


  public void setEnabled(final boolean enabled) {
    this.enabled = enabled;
  }


  /**
   * The getter method for description.
   *
   * @return int
   * @hibernate.property column = "DESCRIPTION" unique="false"
   * null="false"
   */
  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  public void setSerialize(final boolean serialize) {
    this.serialize = serialize;
  }


  /**
   * Returns true if this build running on this cluster member should be serialized.
   *
   * @return String
   * @hibernate.property column="SERIALIZE"  type="yes_no"
   * unique="false" null="false"
   */
  public boolean isSerialize() {
    return serialize;
  }


  /**
   * Returns true if this cluster member was deleted
   *
   * @return String
   * @hibernate.property column="DELETED"  type="yes_no"
   * unique="false" null="false"
   */
  public boolean isDeleted() {
    return deleted;
  }


  public void setDeleted(final boolean deleted) {
    this.deleted = deleted;
  }


  /**
   * The getter method for agent's weight.
   *
   * @return int
   * @hibernate.property column = "CAPACITY" unique="false" null="false"
   */
  public int getCapacity() {
    return capacity;
  }


  public void setCapacity(final int capacity) {
    this.capacity = capacity;
  }


  /**
   * The getter method for agent's max concurrent builds.
   *
   * @return int
   * @hibernate.property column = "MAX_CONCURRENT_BUILDS" unique="false" null="false"
   */
  public int getMaxConcurrentBuilds() {
    return maxConcurrentBuilds;
  }


  public void setMaxConcurrentBuilds(final int maxConcurrentBuilds) {
    this.maxConcurrentBuilds = maxConcurrentBuilds;
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


  public String isEnabledAsString() {
    return enabled ? "Enabled" : "Disabled";
  }


  public String getPassword() {
    return RealmConstants.DEFAULT_BUILDER_PASSWORD;
  }


  public String toString() {
    return "AgentConfig{" +
            "deleted=" + deleted +
            ", description='" + description + '\'' +
            ", enabled=" + enabled +
            ", host='" + host + '\'' +
            ", ID=" + ID +
            ", serialize=" + serialize +
            ", timeStamp=" + timeStamp +
            '}';
  }
}
