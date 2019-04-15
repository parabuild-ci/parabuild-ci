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

import java.io.*;

/**
 * Holds essintial information about parallel builds.
 */
public final class ParallelBuildRunVO implements Serializable, ObjectConstants {

  private static final long serialVersionUID = -4591276259024162394L; // NOPMD

  private int buildRunID = UNSAVED_ID;
  private String buildName = null;
  private byte dependence = BuildRun.DEPENDENCE_STANDALONE;


  public ParallelBuildRunVO(final Integer buildRunID, final String buildName, final Byte dependence) {
    this.buildRunID = buildRunID;
    this.buildName = buildName;
    this.dependence = dependence;
  }


  public ParallelBuildRunVO(final int buildRunID, final String buildName, final byte dependence) {
    this.buildRunID = buildRunID;
    this.buildName = buildName;
    this.dependence = dependence;
  }


  public int getBuildRunID() {
    return buildRunID;
  }


  public void setBuildRunID(final int buildRunID) {
    this.buildRunID = buildRunID;
  }


  public String getBuildName() {
    return buildName;
  }


  public void setBuildName(final String buildName) {
    this.buildName = buildName;
  }


  public byte getDependence() {
    return dependence;
  }


  public void setDependence(final byte dependence) {
    this.dependence = dependence;
  }


  public void setBuildRunID(final Integer buildRunID) {
    this.buildRunID = buildRunID;
  }


  public void setDependence(final Byte dependence) {
    this.dependence = dependence;
  }


  public String toString() {
    return "ParallelBuildRunVO{" +
      "buildRunID=" + buildRunID +
      ", buildName='" + buildName + '\'' +
      ", dependence=" + dependence +
      '}';
  }
}
