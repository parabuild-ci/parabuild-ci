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

import java.io.Serializable;

/**
 * Builder Agent.
 *
 * @hibernate.class table="CLUSTER_MEMBER" dynamic-update="true"
 * @hibernate.cache usage="read-write"
 */
public final class BuilderAgent implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -5237501290837750448L; // NOPMD

  private int ID = UNSAVED_ID;
  private int builderID = BuilderConfiguration.UNSAVED_ID;
  private int agentID = AgentConfig.UNSAVED_ID;
  private long timeStamp;


  public BuilderAgent() {
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
   * @hibernate.property column="CLUSTER_ID" unique="false"
   * null="false"
   */
  public int getBuilderID() {
    return builderID;
  }


  public void setBuilderID(final int builderID) {
    this.builderID = builderID;
  }


  /**
   * @hibernate.property column="AGENT_ID" unique="false"
   * null="false"
   */
  public int getAgentID() {
    return agentID;
  }


  public void setAgentID(final int agentID) {
    this.agentID = agentID;
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
}
