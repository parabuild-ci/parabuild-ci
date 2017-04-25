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
package org.parabuild.ci.merge;

import org.parabuild.ci.object.MergeConfiguration;

/**
 * Composit merge state report.
 */
public final class MergeState {


  private int activeMergeConfigurationID = MergeConfiguration.UNSAVED_ID;
  private String name = null;
  private String description = null;
  private String marker = null;
  private MergeStatus status = null;


  public void setActiveMergeConfigurationID(final int activeMergeConfigurationID) {
    this.activeMergeConfigurationID = activeMergeConfigurationID;
  }


  public int getActiveMergeConfigurationID() {
    return activeMergeConfigurationID;
  }


  public String getName() {
    return name;
  }


  public void setName(final String name) {
    this.name = name;
  }


  public void setStatus(final MergeStatus status) {
    this.status = status;
  }


  public MergeStatus getStatus() {
    return status;
  }


  public String getMarker() {
    return marker;
  }


  public void setMarker(final String marker) {
    this.marker = marker;
  }


  public String getDescription() {
    return description;
  }


  public void setDescription(final String description) {
    this.description = description;
  }


  public String toString() {
    return "MergeState{" +
      "activeMergeConfigurationID=" + activeMergeConfigurationID +
      ", name='" + name + '\'' +
      ", description='" + description + '\'' +
      ", marker='" + marker + '\'' +
      ", status=" + status +
      '}';
  }
}
