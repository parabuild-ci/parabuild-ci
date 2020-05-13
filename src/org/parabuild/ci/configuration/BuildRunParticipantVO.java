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

import org.parabuild.ci.object.ChangeList;

/**
 * This Value Object holds a build run participant data.
 */
public final class BuildRunParticipantVO {

  private final ChangeList changeList;
  private int firstBuildRunID = -1;
  private int firstBuildRunNumber = -1;
  private final int participantID;


  public BuildRunParticipantVO(final ChangeList changeList, final int firstBuildRunID, final int firstBuildRunNumber, final int participantID) {
    this.changeList = changeList;
    this.firstBuildRunID = firstBuildRunID;
    this.firstBuildRunNumber = firstBuildRunNumber;
    this.participantID = participantID;
  }


  public BuildRunParticipantVO(final ChangeList changeList, final Integer firstBuildRunID, final Integer firstBuildRunNumber, final Integer participantID) {
    this(changeList, firstBuildRunID.intValue(), firstBuildRunNumber.intValue(), participantID.intValue());
  }


  public ChangeList getChangeList() {
    return changeList;
  }


  public int getFirstBuildRunID() {
    return firstBuildRunID;
  }


  public int getFirstBuildRunNumber() {
    return firstBuildRunNumber;
  }


  public int getParticipantID() {
    return participantID;
  }


  public String getFirstBuildRunNumberAsString() {
    return Integer.toString(firstBuildRunNumber);
  }


  public String toString() {
    return "BuildRunParticipantVO{" +
      "changeList=" + changeList +
      ", firstBuildRunID=" + firstBuildRunID +
      ", firstBuildRunNumber=" + firstBuildRunNumber +
      ", participantID=" + participantID +
      '}';
  }
}
